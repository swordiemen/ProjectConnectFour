package model;

import java.util.ArrayList;

import exceptions.FalseMoveException;

public class AIGame extends Game {
	public AIGame(ArrayList<Player> argPlayerList) {
		super(argPlayerList);
	}

	public void takeTurn(int c) throws FalseMoveException {
		super.takeTurn(c);
		super.getPlayers().get(super.getCurrent()).requestMove(this);
	}
}
