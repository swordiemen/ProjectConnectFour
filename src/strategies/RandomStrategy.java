package strategies;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Game;

public class RandomStrategy implements Strategy {
	private String name;
	
	public RandomStrategy(){
		name = "Random";
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int determineMove(Game g) {
		Board b = g.getBoard();
		int result = -1;
		List<Integer> possibleMoves = new ArrayList<Integer>();
		for(int i = 0; i < b.getCol(); i++){
			if(b.getNextEntryInColumn(i) != -1){
				possibleMoves.add(i);
			}
		}
		Random r = new Random();
		result = possibleMoves.get(r.nextInt(possibleMoves.size()));
		return result;
	}

}
