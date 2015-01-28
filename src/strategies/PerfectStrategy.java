package strategies;

import constants.Constants;
import model.Board;
import model.Game;
import model.Mark;

public class PerfectStrategy implements Strategy {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int determineMove(Game g) {
		return determineBoth(g.getBoard(), g.getCurrent())[1];
	}

	public int[] determineBoth(Board b, Mark m) {
		int[] result = new int[2];
		int qual;
		for (int i = 0; i < Constants.COLUMNS; i++) {
			if (b.isEmpty(i) && !b.isFull()) {
				qual = determineQual(b, m, i);
				if (qual > result[1]) {
					result[1] = qual;
					result[0] = i;
				}
			}
		}
		return result;
	}

	public int determineQual(Board b, Mark m, int move) {
		int oppQual[];
		int qual;
		Board testBoard = b.deepCopy();
		testBoard.setField(move, m);
		if (testBoard.isWinner(m)) {
			qual = 2;
		} else if (testBoard.isFull()) {
			qual = 1;
		} else {
			oppQual = determineBoth(testBoard, m.next());
			if (oppQual[1] == 2) {
				qual = 0;
			} else if (oppQual[1] == 1) {
				qual = 1;
			} else {
				qual = 2;
			}
		}
		return qual;
	}

}
