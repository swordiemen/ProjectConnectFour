package strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Board;
import model.Game;

public class OneStepAheadStrategy implements Strategy {
	public String name;
	
	public OneStepAheadStrategy(){
		name = "OSA!";
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int determineMove(Game g) {
		int res = -1;
		Board b = g.getBoard();
		Board copy;
		Random r = new Random();
		List<Integer> winningCols = new ArrayList<Integer>(); // a list of columns where we win
		List<Integer> otherWinningCols = new ArrayList<Integer>(); // a list of columns where the other player can win.
		for(int c = 0; c < b.getCol(); c++){
			copy = b.deepCopy();
			if(!copy.isFullColumn(c)){
				copy.addToCol(c, g.getCurrent().next());
				if(copy.getWinner() == g.getCurrent().next()){
					otherWinningCols.add(c);
				}
			}
		}
		for(int c = 0; c < b.getCol(); c++){
			copy = b.deepCopy();
			if(!copy.isFullColumn(c)){
				copy.addToCol(c, g.getCurrent());
				if(copy.getWinner() == g.getCurrent()){
					winningCols.add(c);
				}
			}
		}
//		System.out.println(cols.toString());
//		System.out.println(cols.size() == 0);
//		winningsCols empty? => check if otherWinningCols is 0. If otherWinningCols is empty, return a random column which isn't empty.
//		winningsCols nonEmpty => we can win, get a random one from the list of winning columns.
//		otherWinningCols nonEmpty => the other player can win, get a random one from the list of the other's winning columns.
		res = winningCols.size() == 0 ? 
				otherWinningCols.size() == 0 ? 
						new RandomStrategy().determineMove(g) 
						: otherWinningCols.get(r.nextInt(otherWinningCols.size()))
				: winningCols.get(r.nextInt(winningCols.size()));
		return res;
	}

}
