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
		super.setPlayers(createPlayerList(ps));
		for (Peer p:ps) {
			p.setGame(this);
			p.startGame(peers);
		}
	}

	public static ArrayList<Player> createPlayerList(ArrayList<Peer> ps) {
		ArrayList<Player> res = new ArrayList<Player>();
		for(Peer p : ps){
			res.add(new Player(p.getName()));
		}
		return res;
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
		try{
			super.takeTurn(collumn);
			for (Peer p:peers){
				p.sendMove(collumn);
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
					String hue = "{";
					for(Mark en : super.getPlayers().keySet()){
						hue = hue + "Mark " + en + ", name " + super.getPlayers().get(en).getName() + "| ";
					}
					hue = hue + "}";
					System.out.println(hue);
					p.endGame(false, winnerName);
				}else{
					p.endGame(true);
				}
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
