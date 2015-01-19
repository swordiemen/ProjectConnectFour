package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import model.Player;
import constants.Constants;

/**
 * Peer for ConnectFour game
 * 
 * @author Yannick Mijsters & Tim Blok
 */

public class Peer implements Runnable {
	public static final String EXIT = "exit";
	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	protected Server server;
	protected ServerGame game;
	private String state;
	private Boolean exit = false;

	/*
	 * @ requires (nameArg != null) && (sockArg != null);
	 */
	/**
	 * Constructor. creates a peer object based in the given parameters.
	 * 
	 * @param nameArg
	 *            name of the Peer-proces
	 * @param sockArg
	 *            Socket of the Peer-proces
	 */
	public Peer(Socket sockArg, Server server) throws IOException {
		sock = sockArg;
		this.server = server;
		in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				sockArg.getOutputStream()));
		state = Constants.STATE_START;

	}

	/**
	 * Reads strings of the stream of the socket-connection and writes the
	 * characters to the default output
	 */
	public void run() {
		String output = null;
		String[] splitOutput;
		while (!exit) {
			try {
				output = in.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("CLIENT SAYS" + output);
			splitOutput = output.split(" ");
			if (state.equals(Constants.STATE_START)) {
				if (splitOutput[0].equals(Constants.Protocol.SEND_HELLO)) {
					this.name = splitOutput[1];
					try {
						out.write(Constants.Protocol.SEND_HELLO + " " + name
								+ "\n");
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					sendUpdate();
					state = Constants.STATE_LOBBY;
				}else{
					try {
						out.write(Constants.Protocol.SEND_ERROR+ " " + "Invalid Command"
								+ "\n");
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else if (state.equals(Constants.STATE_LOBBY)) {
				if (splitOutput[0].equals(Constants.Protocol.SEND_PLAY)) {
					server.addPlayer(this);
				}
				if (splitOutput[0].equals(Constants.Protocol.SEND_CHALLENGE)){
					for (Peer p: server.getPeerList()){
						if(p.getName().equals(splitOutput[1])){
							try{
								out.write(Constants.Protocol.SEND_CHALLENGED + "\n");
								out.flush();
							}catch(IOException e){
								e.printStackTrace();
							}
						}
					}
				}
				if(splitOutput[0].equals(Constants.Protocol.SEND_CHAT)){
				} else if (state.equals(Constants.STATE_INGAME)) {
					if (splitOutput[0].equals(Constants.Protocol.SEND_MOVE)) {
						game.takeTurn(Integer.parseInt(splitOutput[1]));
					}
					if (splitOutput[0].equals(Constants.Protocol.SEND_QUIT)) {
						game.endGame(this);
					}
				}
			}
		}
	}

	/**
	 * Reads a string from the console and sends this string over the
	 * socket-connection to the Peer proces. On Peer.EXIT the method ends
	 */
	// @ requires x >= 0 && x < 8;
	// @ requires y >= 0 && y < 9;
	public void sendMove(int collumn) {
		System.out.println("Make Move");
		try {
			out.write(Constants.Protocol.MAKE_MOVE + " " + collumn + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
	}

	/**
	 * Sets the instance variable game to gameIn;
	 * 
	 * @param gameIn
	 *            the game that this.game will be changed to
	 */
	// @ requires gameIn != null;

	public void setGame(ServerGame gameIn) {
		this.game = gameIn;
	}

	/**
	 * 
	 * @return returns the ServerGame of this class
	 */

	public ServerGame getGame() {
		return this.game;
	}

	public void quit(Peer p) {
		try {
			if (this.equals(p)) {
				out.write(Constants.Protocol.SEND_GAME_OVER + " " + false + " "
						+ true + "\n");
				out.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** returns name of the peer object */
	public String getName() {
		return name;
	}

	/**
	 * writes a message to BufferedReader out
	 * 
	 * @param peers
	 *            An ArrayList of type Peer containing all Peers that will join
	 *            the new Game
	 */

	public void startGame(ArrayList<Peer> peers) {
		this.state = Constants.STATE_INGAME;
		try {
			out.write(Constants.Protocol.MAKE_GAME + " "
					+ peers.get(0).getName() + " " + peers.get(1).getName()
					+ "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(state);
	}

	public void sendUpdate() {
		StringBuilder players = new StringBuilder();
		for (Peer p : server.getPeerList()) {
			players.append(p.getName() + " ");
		}
		players.append("\n");
		try {
			out.write(Constants.Protocol.SEND_PLAYERS + " " + players);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void endGame(Boolean draw, Player player) {
		try{
			out.write(Constants.Protocol.SEND_GAME_OVER + " " + draw +  
					player.getName() + "\n" );
			out.flush();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void endGame(Boolean draw) {
		try{
			out.write(Constants.Protocol.SEND_GAME_OVER + " " + draw + "\n" );
			out.flush();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
