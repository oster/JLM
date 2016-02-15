package plm.test.simple.test;

import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import plm.core.PLMCompilerException;
import plm.core.lang.ProgrammingLanguage;
import plm.core.model.Game;
import plm.core.model.lesson.ExecutionProgress;
import plm.core.model.lesson.Exercise.StudentOrCorrection;
import plm.core.model.lesson.Exercise.WorldKind;
import plm.universe.Entity;
import plm.universe.World;

public abstract class JVMCompiledSimpleExerciseTest extends SimpleExerciseTest {
	
	public JVMCompiledSimpleExerciseTest(Game game, ProgrammingLanguage pl) {
		super(game, pl);
	}

	@Test
	public void testSolutionShouldPass() throws PLMCompilerException {
		// FIXME: Re-implement me
	}
	
	@Test
	public void testSolutionShouldExecuteProperly() throws PLMCompilerException {
		// FIXME: Re-implement me
	}
	
	@Test (expected = PLMCompilerException.class)
	public void testSyntaxErrorRisingCodeShouldNotCompil() throws PLMCompilerException {
		// FIXME: Re-implement me
	}
	
	@Test (expected = PLMCompilerException.class)
	public void testVariableErrorRisingCodeShouldNotCompil() throws PLMCompilerException {
		// FIXME: Re-implement me
	}
}
