/* automatically converted from the Nick Parlante's excellent exercising site http://javabat.com/ */

package bat.bool2;
import plm.core.model.lesson.ExerciseTemplated;
import plm.core.model.lesson.Lesson;
import plm.universe.bat.BatTest;
import plm.universe.bat.BatWorld;

public class TwoAsOne extends ExerciseTemplated {
	public TwoAsOne(Lesson lesson) {
		super("TwoAsOne");

		BatWorld myWorld = new BatWorld("twoAsOne");
		myWorld.addTest(BatTest.VISIBLE, 1, 2, 3) ;
		myWorld.addTest(BatTest.VISIBLE, 3, 1, 2) ;
		myWorld.addTest(BatTest.VISIBLE, 3, 2, 2) ;
		myWorld.addTest(BatTest.INVISIBLE, 2, 3, 1) ;
		myWorld.addTest(BatTest.INVISIBLE, 5, 3, -2) ;
		myWorld.addTest(BatTest.INVISIBLE, 5, 3, -3) ;
		myWorld.addTest(BatTest.INVISIBLE, 2, 5, 3) ;
		myWorld.addTest(BatTest.INVISIBLE, 9, 5, 5) ;
		myWorld.addTest(BatTest.INVISIBLE, 9, 4, 5) ;
		myWorld.addTest(BatTest.INVISIBLE, 5, 4, 9) ;
		myWorld.addTest(BatTest.INVISIBLE, 3, 3, 0) ;
		myWorld.addTest(BatTest.INVISIBLE, 3, 3, 2) ;

		templatePython("twoAsOne", new String[]{"Int","Int","Int"},
				"def twoAsOne(a, b, c):\n",
				"   return (a + b == c) or (a + c == b) or (b + c == a)");
		templateScala("twoAsOne", new String[]{"Int","Int","Int"}, 
				"def twoAsOne(a:Int, b:Int, c:Int):Boolean = {\n",
				"   return (a + b == c) || (a + c == b) || (b + c == a)\n"+
				"}");
		setup(myWorld);
	}

	public void run(BatTest t) {
		/* BEGIN SKEL */
		t.setResult( twoAsOne((Integer)t.getParameter(0), (Integer)t.getParameter(1), (Integer)t.getParameter(2)) );
		/* END SKEL */
	}

	/* BEGIN TEMPLATE */
	boolean twoAsOne(int a, int b, int c) {
		/* BEGIN SOLUTION */
		return (a + b == c) || (a + c == b) || (b + c == a);
		/* END SOLUTION */
	}
	/* END TEMPLATE */
}
