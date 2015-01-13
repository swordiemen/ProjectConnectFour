package strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Board;
import model.Game;

public class LosingStrategy implements Strategy {
	private String name;

	public LosingStrategy() {
		name = "Losing";
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
		List<Integer> winningCols = new ArrayList<Integer>();
		for(int c = 0; c < b.getCol(); c++){
			copy = b.deepCopy();
			if(!copy.isFullColumn(c)){
				if(copy.getWinner() == g.getCurrent().next()){
					winningCols.add(c);
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
		List<Integer> cols = new ArrayList<Integer>();
		for(int i = 0; i < b.getCol(); i++){
			if(!(winningCols.contains(i)) && !(b.isFullColumn(i))){
				cols.add(i);
			}
		}
//		System.out.println(cols.toString());
//		System.out.println(cols.size() == 0);
		res = cols.size() == 0 ? new RandomStrategy().determineMove(g) : cols.get(r.nextInt(cols.size()));
		return res;
	}

}
