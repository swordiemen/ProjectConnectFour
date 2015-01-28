package model;

import exceptions.FalseMoveException;
import strategies.Strategy;

public class ComputerPlayer extends Player {
	private Strategy strategy;

	public ComputerPlayer(Strategy strat) {
		super(strat.getName());
		strategy = strat;
	}

	@Override
	public void requestMove(Game game) {
		try {
			game.takeTurn(strategy.determineMove(game));
		} catch (FalseMoveException e) {
			// Should never happen.
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return strategy.getName();
	}
}
