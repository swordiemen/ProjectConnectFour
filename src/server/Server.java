package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import model.Leaderboard;
import constants.Constants;

/**
 * Server for the ConnectFour game.
 * serverGameList is a list of all the games running on the server
 * 
 * @author Yannick Mijsters & Tim Blok
 */
public class Server implements Runnable {
	private ArrayList<Peer> twoPlayerList = new ArrayList<Peer>();
	private ArrayList<Peer> peerList = new ArrayList<Peer>();
	private ArrayList<ServerGame> serverGameList = new ArrayList<ServerGame>();
	private ArrayList<Peer> lobbyList = new ArrayList<Peer>();
	private ServerSocket listenSocket;
	private Socket connection = null;
	private Peer peer;
	private boolean shutdown;
	private int port;
	private Leaderboard leaderboard;

	/**
	 * Changes the instance variable port
	 * 
	 * @param port
	 *            The new port of the server
	 */
	// @ requires portIn != 0;
	// @ ensures getPort() == portIn;
	public void setPort(int portIn) {
		this.port = portIn;
	}

	/* @pure */public int getPort() {
		return this.port;
	}

	/* @pure */public ArrayList<Peer> getPeerList() {
		return this.peerList;
	}

	/**
	 * Thread that tries to make a new ServerSocket, if the ServerSocket is
	 * created it will keep waiting for the ServerSocket to receive a Socket.
	 * Once the Socket is received, it will create a new Peer, and starts its
	 * Thread.
	 */
	//
	public void run() {
		try {
			listenSocket = new ServerSocket(port);
		} catch (BindException e1) {
			// ServerView view = new ServerView();
			// view.illegalPort();
		} catch (IOException e) {
		}
		while (!shutdown) {
			try {
				connection = listenSocket.accept();
				peer = new Peer(connection, this);
				//if(isValidName(peer.getName())){
				this.peerList.add(peer);
				//}
				connection = null;
				(new Thread(peer)).start();

			} catch (Exception e) {
			}
		}
	}

	/**
	 * Creates a new ServerGame and adds it to the serverGameList.
	 * 
	 * @param peers
	 *            The Peer ArrayList with all the Peers that want to start a new
	 *            game.
	 */
	// @ requires peers != null;
	// @ ensures getServerGameList().contains(new ServerGame(peers));

	public void startNewGame(ArrayList<Peer> peers) {		
		ServerGame game = new ServerGame(peers);
		serverGameList.add(game);
	}

	/* @pure */public ArrayList<ServerGame> getServerGameList() {
		return this.serverGameList;
	}

	/**
	 * Adds a peer into a Peer ArrayList, depending on i which ArrayList that
	 * will be. Also if there are enough peers in the ArrayList it will start a
	 * new game, and clear the ArrayList afterwards.
	 * 
	 * @param i
	 *            The amount of players in the game
	 */
	// @ requires i >= 2 && i <= 4;

	public synchronized void addPlayer(Peer peerin) {
		twoPlayerList.add(peerin);
		if (twoPlayerList.size() == 2) {
			startNewGame(twoPlayerList);
			twoPlayerList.clear();
		}
	}

	public void removePlayer(Peer p){
		peerList.remove(p);
		if(lobbyList.contains(p)){
			removeFromLobbyList(p);
		}
	}

	public void sendUpdatedPlayerList(){
		if(lobbyList.size() > 0){
			StringBuilder players = new StringBuilder();
			players.append(" ");
			for(Peer p : lobbyList){
				players.append(p.getName() + " " + p.getOptions() + ", ");
			}
			players.delete(players.length() - 2, players.length());
			System.out.println(players.toString());
			for(Peer p : lobbyList){
				System.out.println("Sending players to " + p.getName());
				p.sendUpdate(players.toString());

			}
		}
	}

	/**
	 * Closes the ServerSocket and sets legalPort to 'false'
	 */

	public static void main(String[] args) {
		Server a = new Server();
		a.setPort(1338);
		Thread b = new Thread(a);
		b.start();
	}

	public void sendLobbyChat(String[] splitOutput, String sender) {
		for(Peer p : peerList){
			if(p.getState().equals(Constants.STATE_LOBBY)){
				p.sendChat(splitOutput, sender, false);
			}
		}
	}

	public boolean isValidName(String name, Peer peer){
		if(name.contains(" ")){
			System.out.println("Er staat een spatie in de naam.");
			return false;
		}else{
			if(peerList.size() > 0){
				for(Peer p : peerList){
					if(p.getName().equals(name) && p != peer){
						System.out.println("Bestaat al");
						return false;
					}
				}
			}
		}
		return true;
	}

	public void sendDirectedChat(String name, String message) {
		String[] msg = message.split(" ");
		for(Peer p : peerList){
			if(p.getName().equals(name)){
				p.sendChat(msg, "[Server]", true);
			}
		}
	}
	public ArrayList<Peer> getLobbyList(){
		return lobbyList;
	}
	public void addToLobbyList(Peer p){
		if(!lobbyList.contains(p)){
			lobbyList.add(p);
			sendUpdatedPlayerList();
		}
	}
	public void removeFromLobbyList(Peer p){
		if(lobbyList.contains(p)){
			lobbyList.remove(p);
			sendUpdatedPlayerList();
		}
	}

} // end of class Server
