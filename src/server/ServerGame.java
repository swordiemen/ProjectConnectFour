package server;

import java.util.ArrayList;

import model.*;

public class ServerGame extends Game {
	private ArrayList<Peer> peers;
	private int currentPlayer;
	public ServerGame(ArrayList<Peer> ps) {
		super();
		this.peers=new ArrayList<Peer>();
		this.peers.addAll(ps);
		System.out.println(peers.size());
		for (int i = 0; i < peers.size(); i++) {
			peers.get(i).setGame(this);
			peers.get(i).startGame(peers);

		}
		currentPlayer=0;
		sendNextPerson();
	}
	public void ClientLeave(){
		for(Peer p:peers){
			p.illegalMove();
		}
	}

	/**
	 * invokes the sendMove method in this class, and sends the move to the board.
	 * @param x the x-coordinate of the move
	 * @param y the y-coordinate of the move
	 */
	//@ requires x >= 0 && x <= 8;
	//@ requires y >= 0 && y <= 8;

	
	public void takeTurn(int x, int y) {
		if(super.legalMoves()[super.getBoard().index(x,y)]||!super.legalMovesNotEmpty()){
			System.out.println(super.legalMoves()[8*y+x]);
			sendMove(peers.get(currentPlayer), x, y); 
			super.takeTurn(super.getBoard().index(x, y));
			if(currentPlayer<peers.size()-1){
				currentPlayer++;
			}
			else{
				currentPlayer=0;
			}
			sendNextPerson();
		}else{
			for(Peer p : this.peers){
				p.illegalMove();
			}
		}



	}
	/**
	 * 
	 * @return returns the Mark of superclass Game
	 */
	//@ ensures \result == super.getCurrent();

	public Mark getNextPerson() {
		return super.getCurrent();
	}

	/**
	 * 
	 * @return returns a peer from the Peer ArrayList peers, depending on the mark of getNextPerson()
	 */


	public Peer getPerson() {
		System.out.print(peers.size()+ "");
		Peer result = null;
		if (getNextPerson().equals(Mark.YY)) {
			result = peers.get(0);
		} else if (getNextPerson().equals(Mark.GG)) {
			result = peers.get(1);
		} else if (getNextPerson().equals(Mark.BB)) {
			result = peers.get(2);
		} else if (getNextPerson().equals(Mark.RR)) {
			result = peers.get(3);
		}
		return result;
	}

	/**
	 * invokes the sendYourTurn method of the Peer that is returned from the getPerson method.
	 */

	public void sendNextPerson() {
		getPerson().sendYourTurn();
	}

	/**
	 * Sends the move to all peers except the Peer that makes the move
	 * @param peer The peer that makes the move
	 * @param x the x-coordinate of the move
	 * @param y the y-coordinate of the move
	 */

	public void sendMove(Peer peer, int x, int y) {
		for (int i = 0; i < peers.size(); i++) {
			if (!peers.get(i).equals(peer)) {
				peers.get(i).sendMove(x, y);
			}
		}
	}
	/**
	 * invokes the isFull method in class Board from the getBoard method from the superclass Game
	 * @return
	 */
	public boolean done(){
		return super.getBoard().isFull();
	}
}
