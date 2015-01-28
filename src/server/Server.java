package server;

import gui.ClientGUI;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import model.Leaderboard;
import constants.Constants;

/**
 * Server for the ConnectFour game. serverGameList is a list of all the games
 * running on the server
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
	private Leaderboard leaderboard; // not used

	// @ private invariant twoPlayerList != null && twoPlayerList.size() >= 0 &&
	// twoPlayerList.size() < 2;
	// @ private invariant peerList != null && peerList.size() >= 0;
	// @ private invariant serverGameList != null && serverGameList.size() >= 0;
	// @ private invariant lobbyList != null && lobbyList.size() >= 0;
	// @ private invariant listenSocket != null;
	// @ private invariant port > 0 && port < 65535;

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
	public void run() {
		try {
			listenSocket = new ServerSocket(port);
		} catch (BindException e1) {
			new ServerApplication(new ClientGUI());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (!shutdown) {

			try {
				connection = listenSocket.accept();
				peer = new Peer(connection, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// if(isValidName(peer.getName())){
			this.peerList.add(peer);
			// }
			connection = null;
			(new Thread(peer)).start();

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
	 * Adds a player to this twoPlayerList. If the size is 2, creates (a)
	 * Game(s) until < 2 players are left.
	 */
	// @ requires peerin != null;
	// @ ensures twoPlayerList.size() == 2 => !twoPlayerList.contains(peerin);
	// @ ensures twoPlayerList.siez() < 2 => twoPlayerList.contains(peerin);
	public synchronized void addPlayer(Peer peerin) {
		twoPlayerList.add(peerin);
		if (twoPlayerList.size() == 2) {
			startNewGame(twoPlayerList);
			twoPlayerList.clear();
		}
	}

	/**
	 * Removes a Peer from the lobbyList and peerList.
	 * 
	 * @param p
	 *            The peer to be removed.
	 */
	// @ requires p != null;
	// @ ensures !peerList.contains(p) && !lobbyList.contains(p);
	public void removePlayer(Peer p) {
		peerList.remove(p);
		if (lobbyList.contains(p)) {
			removeFromLobbyList(p);
		}
	}

	/**
	 * Sends a list of players to all the Peers in the LobbyList. The format is
	 * "nameOfPlayer, optionsOfPlayer ".
	 */
	/* @ pure @ */public void sendUpdatedPlayerList() {
		if (lobbyList.size() > 0) {
			StringBuilder players = new StringBuilder();
			players.append(" ");
			for (Peer p : lobbyList) {
				players.append(p.getName() + " " + p.getOptions() + ", ");
			}
			players.delete(players.length() - 2, players.length());
			System.out.println(players.toString());
			for (Peer p : lobbyList) {
				System.out.println("Sending players to " + p.getName());
				p.sendUpdate(players.toString());

			}
		}
	}

	public static void main(String[] args) {
		Server a = new Server();
		a.setPort(1338);
		Thread b = new Thread(a);
		b.start();
	}

	/**
	 * Sends a chat to everyone in the lobby.
	 * 
	 * @param splitOutput
	 *            The message that the Peer gave us.
	 * @param sender
	 *            The sender of the message.
	 */
	// @ requires splitOutput != null;
	// @ requires sender != null;
	public void sendLobbyChat(String[] splitOutput, String sender) {
		for (Peer p : peerList) {
			if (p.getState().equals(Constants.STATE_LOBBY)) {
				p.sendChat(splitOutput, sender, false);
			}
		}
	}

	/**
	 * Checks if a given name is valid.
	 * 
	 * @param name
	 *            The name to be checked.
	 * @param peer
	 *            The peer the name belongs to.
	 * @return Whether the name is valid.
	 */
	// @ requires name != null;
	// @ requires peer != null;
	// @ ensures name.contains(" ") => \result == false;
	public boolean isValidName(String name, Peer peer) {
		if (name.contains(" ")) {
			return false;
		} else {
			if (peerList.size() > 0) {
				for (Peer p : peerList) {
					if (p.getName().equals(name) && p != peer) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Sends a chat to a specified Peer.
	 * 
	 * @param name
	 *            The name of the peer.
	 * @param message
	 *            The message to be sent.
	 */
	// @ requires name != null;
	// @ requires message != null;
	public void sendDirectedChat(String name, String message) {
		String[] msg = message.split(" ");
		for (Peer p : peerList) {
			if (p.getName().equals(name)) {
				p.sendChat(msg, "[Server]", true);
			}
		}
	}

	/**
	 * Returns the lobbyList of this Server.
	 * 
	 * @return The lobbyList of this Server.
	 */
	/* @ pure @ */public ArrayList<Peer> getLobbyList() {
		return lobbyList;
	}

	/**
	 * Adds a Peer to this Server's lobbyList.
	 * 
	 * @param p
	 *            The Peer to be added.
	 */
	// @ requires p != null;
	// @ ensures lobbyList.contains(p);
	public void addToLobbyList(Peer p) {
		if (!lobbyList.contains(p)) {
			lobbyList.add(p);
			sendUpdatedPlayerList();
		}
	}

	/**
	 * Removes a Peer from this Server's lobbyList.
	 * 
	 * @param p
	 *            The Peer to be removed.
	 */
	// @ requires p != null;
	// @ ensures !lobbyList.contains(p);
	public void removeFromLobbyList(Peer p) {
		if (lobbyList.contains(p)) {
			lobbyList.remove(p);
			sendUpdatedPlayerList();
		}
	}

} // end of class Server
