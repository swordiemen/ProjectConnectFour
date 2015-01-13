package strategies;

import model.Game;

public interface Strategy {
	public String getName();
	public int determineMove(Game g);	
}
