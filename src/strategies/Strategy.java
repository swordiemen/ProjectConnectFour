package strategies;

import model.Game;

public interface Strategy {
	/**
	 * Returns the name of this Strategy.
	 * @return The name of this Strategy.
	 */
	public String getName();
	/**
	 * Makes a move, given a certain Game.
	 * @param g The game this strategy has to make a move on.
	 * @return The move the strategy has determined to be the best.
	 */
	public int determineMove(Game g);	
}
