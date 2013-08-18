package lessons.welcome.traversal;

import jlm.universe.Direction;
import jlm.universe.bugglequest.SimpleBuggle;

public class SnakeEntity extends SimpleBuggle {
	@Override
	/* BEGIN TEMPLATE */
	public void run() {
		/* BEGIN SOLUTION */
		brushDown();
		while (!endingPosition()) {
			snakeStep();
		}
	}
	boolean endingPosition() {
		if (! isFacingWall()) 
			return false;

		boolean res = false;
		left();
		if (isFacingWall()) 
			res = true;
		right();		
		return res;
	}

	void snakeStep() {
		if (isFacingWall()) {
			if (getDirection() == Direction.EAST) {
				left();
				forward();
				left();
			} else {
				right();
				forward();
				right();
			}
		} else {
			forward();
		}

		/* END SOLUTION */
	}
	/* END TEMPLATE */	
}
