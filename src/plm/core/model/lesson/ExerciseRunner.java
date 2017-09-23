package plm.core.model.lesson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import plm.core.PLMCompilerException;
import plm.core.PLMEntityNotFound;
import plm.core.lang.ProgrammingLanguage;
import plm.core.model.I18nManager;
import plm.core.model.lesson.Exercise.StudentOrCorrection;
import plm.core.model.lesson.Exercise.WorldKind;
import plm.core.model.session.SourceFile;
import plm.universe.Entity;
import plm.universe.World;

public class ExerciseRunner {

	public static int DEFAULT_NUMBER_OF_TRIES = 3;
	public static long DEFAULT_WAITING_TIME = 20;
	
	private Locale locale;
	private List<Thread> runnerVect = new ArrayList<Thread>();
	private boolean executionStopped = false;

	private int maxNumberOfTries = DEFAULT_NUMBER_OF_TRIES;
	private long waitingTime = DEFAULT_WAITING_TIME;

	public ExerciseRunner(Locale locale) {
		this.locale = locale;
	}

	public void setI18n(Locale locale) {
		this.locale = locale;
	}

	public CompletableFuture<ExecutionProgress> run(Exercise exo, ProgrammingLanguage progLang, String code) {
		// FIXME: Clean this function
		// FIXME: Don't share lastResult with mutatesEntities() and runEntities()

		final CompletableFuture<ExecutionProgress> future = new CompletableFuture<>();

		ExecutionProgress lastResult = new ExecutionProgress(progLang, locale);

		exo.reset();

		/*
		 *  Compilation time
		 */
		SourceFile sf = exo.getDefaultSourceFile(progLang);
		sf.setBody(code);
		try {
			mutateEntities(exo, sf, progLang, StudentOrCorrection.STUDENT, lastResult);
		} catch (PLMCompilerException e) {
			e.printStackTrace();
			return CompletableFuture.completedFuture(lastResult);
		} catch (PLMEntityNotFound e) {
			e.printStackTrace();
			String msg = "An error occurred while running your program. Usually, it means that you wrote some code (others than methods or functions) outside the run() method. Please check your code.";
			lastResult.setExecutionError(msg);
			return CompletableFuture.completedFuture(lastResult);
		}

		/*
		 *  Execution time
		 */
		executionStopped = false;

		// Start entities in separate threads
		for (World w : exo.getWorlds(WorldKind.CURRENT)) 
			w.runEntities(progLang, runnerVect, lastResult, locale);

		
		/*
		 *  Watchdog to take care of the timeouts
		 */
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

		Runnable monitorThreads = new Runnable() {
			int numberOfTries = maxNumberOfTries*500; // maxNumberOfTries is in second, with a 20ms inter-run delay in the scheduler 

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				boolean stillAlive = false;
				boolean executionEnded = false;
				if(!executionStopped && numberOfTries > 0 && runnerVect.size()>0) {
					do {
						if(runnerVect.get(0).isAlive()) {
							numberOfTries--;
							stillAlive = true;
						}
						else {
							runnerVect.remove(0);
							executionEnded = runnerVect.isEmpty();
						}
					} while(!stillAlive && runnerVect.size()>0);
				}

				if(executionStopped || numberOfTries == 0) {
					for(Thread t : runnerVect) {
						t.stop();
					}
					runnerVect.clear();
					if(executionStopped) {
						lastResult.setStopError();
						executionStopped = false;
					}
					else if(numberOfTries == 0) {
						lastResult.setTimeoutError();
					}
					ses.shutdown();
					future.complete(lastResult);
				}
				else if(executionEnded) {
					/*
					 *  Assessment time
					 */
					Vector<World> currentWorlds = exo.getWorlds(WorldKind.CURRENT);
					for(int i=0; i<currentWorlds.size(); i++) {
						World currentWorld = currentWorlds.get(i);
						World answerWorld = exo.getWorlds(WorldKind.ANSWER).get(i);
						checkWorld(currentWorld, answerWorld, progLang, lastResult);
					}
					ses.shutdown();
					future.complete(lastResult);
				}
			}
		};
		ses.scheduleAtFixedRate(monitorThreads, 20, waitingTime, TimeUnit.MILLISECONDS);

		return future;
	}

	public void runDemo(Exercise exo, ProgrammingLanguage progLang) {
		// FIXME: Factorize this code

		exo.reset();
		for(World w : exo.getWorlds(WorldKind.ANSWER)) 
			w.runEntities(progLang, runnerVect, null, locale);
		
		while (runnerVect.size()>0) {
			try {
				Thread t = runnerVect.get(0);
				t.join();
				runnerVect.remove(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void mutateEntities(Exercise exo, SourceFile sourceFile, ProgrammingLanguage progLang, StudentOrCorrection whatToCompile, ExecutionProgress progress) throws PLMCompilerException {
		progLang.compileExo(sourceFile, progress, whatToCompile, locale);

		WorldKind worldKind = null;
		switch(whatToCompile) {
		case STUDENT:
			worldKind = WorldKind.CURRENT;
			break;
		case CORRECTION:
			worldKind = WorldKind.ANSWER;
			break;
		default:
			break;
		}

		String[] bits = exo.getId().split("\\.");
		String entityName = bits[bits.length - 1];

		for(World w : exo.getWorlds(worldKind)) {
			List<Entity> entities = w.getEntities();
			List<Entity> newEntities = progLang.mutateEntities(entityName, sourceFile, whatToCompile, entities);
			w.setEntities(newEntities);
		}
	}

	private void checkWorld(World currentWorld, World answerWorld, ProgrammingLanguage progLang, ExecutionProgress progress) {
		progress.commonErrorText = "";
		progress.commonErrorID = -1;
		progress.totalTests++;

		if (!answerWorld.winning(currentWorld)) {
			// FIXME: Enable again commonErrors
			/*
			for(int j = 0 ; j < commonErrors.size() ; j++) {
				if(currentWorld.get(i).winning((commonErrors.get(j)).get(i))) { //winning do an equals, but it is the same
					String path = Game.JAVA.nameOfCommonError(this, j).replaceAll("\\.", "/");
					try {
						StringBuffer sb = FileUtils.readContentAsText(path, getGame().getLocale(), "html", true);
						lastResult.commonErrorText = sb.toString();
						lastResult.commonErrorID = j;
					} catch (IOException e) {
						e.printStackTrace();
					} 
					break;
				}
			}
			*/
			String diff = answerWorld.diffTo(currentWorld);
			progress.executionError += I18nManager.getI18n(locale).tr("The world ''{0}'' differs",currentWorld.getName());
			if (diff != null) 
				progress.executionError += ":\n"+diff;
			progress.executionError += "\n------------------------------------------\n";
			progress.outcome = ExecutionProgress.outcomeKind.FAIL;
		} else {
			progress.passedTests++;
		}
	}

	public void stopExecution() {
		executionStopped = true;
	}
}
