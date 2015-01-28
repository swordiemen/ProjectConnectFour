package server;
import java.util.ArrayList;

import exceptions.FalseMoveException;
import model.*;

public class ServerGame extends Game {
	private ArrayList<Peer> peers;
	//@ private invariant peers != null && peers.size() >= 0;

	/**
	 * Creates a new ServerGame with a list of Peers.
	 * @param ps The list of Peers that are going to be in this ServerGame.
	 */
	//@ requires ps != null;
	public ServerGame(ArrayList<Peer> ps) {
		super();
		System.out.println("Game wordt aangemaakt");
		this.peers=new ArrayList<Peer>();
		this.peers.addAll(ps);
		super.setPlayers(createPlayerList(ps));
		for (Peer p:ps) {
			p.setGame(this);
			p.startGame(peers);
		}
	}

	/**
	 * Creates an ArrayList of Players, given a ArrayList of Peers.
	 * @param ps The list of Peers.
	 * @return The List of Players.
	 */
	//@ requires ps != null;
	public static ArrayList<Player> createPlayerList(ArrayList<Peer> ps) {
		ArrayList<Player> res = new ArrayList<Player>();
		for(Peer p : ps){
			res.add(new Player(p.getName()));
		}
		return res;
	}

	/**
	 * Takes a turn in this board, and if it's legal, sends the move to the players in the Game.
	 * @param column The column of the move.
	 * @throws FalseMoveException Thrown if the move is illegal.
	 */
	//@ requries column > 0 && column < Constants.COLUMNS;
	public void takeTurn(int column) {
		try{
			super.takeTurn(column);
			for (Peer p:peers){
				p.sendMove(column);
			}
		}catch(FalseMoveException e){
			e.printStackTrace();
			for (Peer p: peers){
				p.quit(p);
			}
		}
		if(super.isGameOver()){
			for(Peer p: peers){
				if(super.getWinner() != null){
					Mark winner = super.getWinner();
					String winnerName = super.getPlayers().get(winner).getName();
					p.endGame(false, winnerName);
				}else{
					p.endGame(true);
				}
			}
		}
	}

	/**
	 * Checks if the board is full.
	 * @return Whether the board is full.
	 */
	/*@ pure @*/public boolean done(){
		return super.getBoard().isFull();
	}

	/**
	 * Ends the game.
	 * @param p The peer the game ended against with.
	 */
	//@ requires p != null;
	public void endGame(Peer p){
		for (Peer peer: peers){
			if(!peer.equals(p)){
				peer.quit(peer);
			}
		}

	}
}
