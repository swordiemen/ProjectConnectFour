package client;
import exceptions.FalseMoveException;
import model.Game;
public class ClientGame extends Game{
	private Client client;
	public ClientGame(Client client){
		super();
		this.client= client;
	}
	public void takeTurn(int collumn){
		client.sendTurn(collumn);
	}
	public void doTurn(int collumn) throws FalseMoveException{
		super.takeTurn(collumn);
	}
}
