package server;
import java.util.ArrayList;

import exceptions.FalseMoveException;
import model.*;

public class ServerGame extends Game {
	private ArrayList<Peer> peers;
	
	public ServerGame(ArrayList<Peer> ps) {
		super();
		System.out.println("Game wordt aangemaakt");
		this.peers=new ArrayList<Peer>();
		this.peers.addAll(ps);
		for (Peer p:ps) {
			p.setGame(this);
			p.startGame(peers);
		}
	}

	/**
	 * invokes the sendMove method in this class, and sends the move to the board.
	 * @param x the x-coordinate of the move
	 * @param y the y-coordinate of the move
	 * @throws FalseMoveException 
	 */
	//@ requires x >= 0 && x <= 8;
	//@ requires y >= 0 && y <= 8;


	public void takeTurn(int collumn) {
		System.out.println("take Turn Server " + collumn);
		try{
			super.takeTurn(collumn);
			System.out.println("After Super.takeTurn " + collumn);
			for (Peer p:peers){
				p.sendMove(collumn);
			}
		}catch(FalseMoveException e){
			e.printStackTrace();
			for (Peer p: peers){
				p.quit(p);
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
		public void endGame(Peer p){

			for (Peer peer: peers){
				if(!peer.equals(p)){
					peer.quit(peer);
				}
			}

		}
	}
