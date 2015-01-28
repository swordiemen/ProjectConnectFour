package client;

import exceptions.FalseMoveException;
import model.Game;

public class ClientGame extends Game {
	private Client client;

	/**
	 * Creates a new clientGame.
	 * 
	 * @param client
	 *            The client of this clientGame.
	 */
	public ClientGame(Client argClient) {
		super();
		this.client = argClient;
	}

	public void takeTurn(int collumn) {
		client.sendTurn(collumn);
	}

	public void doTurn(int collumn) throws FalseMoveException {
		super.takeTurn(collumn);
	}
}
