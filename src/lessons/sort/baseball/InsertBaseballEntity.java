package lessons.sort.baseball;

import lessons.sort.baseball.universe.BaseballEntity;

public class InsertBaseballEntity extends BaseballEntity {
	
	/* BEGIN TEMPLATE */
	public void run() {
		/* BEGIN SOLUTION */
		/* Bring the hole in 0,1 */
		while (getHole() > 1)
			move(getHole()-1);
		if (getHole() == 0) // It was probably already on base 0, but on another position
			move(0);
		
		for (int player = 2; player < getBasesAmount()*getPositionsAmount(); player ++) {
			//out("Sort player "+player);
			
			//out("Compare "+(getHole()+1)+":"+getPlayerColor(getHole()+1)+" < "+(getHole()-1)+":"+getPlayerColor(getHole()-1));
			while (getHole()>0 && getPlayerColor(getHole()+1) < getPlayerColor(getHole()-1)) {  
				int center = getHole();// ...2x1... with ascending positions from left to right
				move(center+1);        // ...21x...
				move(center-1);        // ...x12...
			}
			while (getHole() != player) 
				move(getHole()+1);
		}
		if (!isSorted())
			System.out.println("IT'S STILL NOT SORTED!! PLEASE REPORT THIS BUG"+world.toString());
		/* END SOLUTION */
	}
	/* END TEMPLATE */
	
	/* BEGIN HIDDEN */	
	int getPlayerColor(int pos) {
		return getPlayerColor(pos / getPositionsAmount(), pos % getPositionsAmount());
	}
	void move(int pos) {
		move(pos / getPositionsAmount(), pos % getPositionsAmount());
	}
	int getHole() {
		return getPositionsAmount()*getHoleBase()+getHolePosition();
	}
	
	void out(String msg) {
		if (isSelected())
			System.out.println(msg);
	}
	/* END HIDDEN */
}