package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import constants.Constants;

/**
 * Peer for a simple client-server application
 * 
 * @author Theo Ruys
 * @version 2005.02.21
 */

public class Peer implements Runnable {
	public static final String EXIT = "exit";
	private boolean yourTurn;
	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	protected Server server;
	protected ServerGame game;
	private String state;

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
		state = "START";

	}


	/**
	 * Reads strings of the stream of the socket-connection and writes the
	 * characters to the default output
	 */
	public void run() {
		String output = null;
		String[] splitOutput;
		while (state.equals("START")) {
			try {
				output = in.readLine();
				System.out.println(output);
				splitOutput = output.split(" ");
				if (splitOutput[0].equals(Constants.Protocol.SEND_HELLO)) {
					this.name = splitOutput[1];
					out.write(Constants.Protocol.SEND_HELLO + " " + name +  "\n");
					out.flush();
					state = "LOBBY";
				} else {
					out.write(Constants.Protocol.SEND_ERROR + " InvalidCommand" +  "\n");
					out.flush();
				}
			} catch (IOException e) {	
				e.printStackTrace();
			}

		}
		while (state.equals("LOBBY")) {
			try {
				output = in.readLine();
				System.out.println(output);
				splitOutput = output.split(" ");
				if (splitOutput[0].equals(Constants.Protocol.SEND_PLAY)) {
					server.addPlayer(this);
				} 
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		while (state.equals("ingame")) {
			try {
				output = in.readLine();
				System.out.println(output + " inGame");
				splitOutput = output.split(" ");
				if (splitOutput[0].equals(Constants.Protocol.SEND_MOVE)) {
					game.takeTurn(Integer.parseInt(splitOutput[1]));
					System.out.println("Send_Move Gelezen");
				}
				if (splitOutput[0].equals(Constants.Protocol.SEND_QUIT)) {
					game.endGame(this);
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Reads a string from the console and sends this string over the
	 * socket-connection to the Peer proces. On Peer.EXIT the method ends
	 */
	//@ requires x >= 0 && x < 8;
	//@ requires y >= 0 && y < 9;
	public void sendMove(int collumn) {
		try {
			out.write(Constants.Protocol.MAKE_MOVE + " " + collumn + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
	}

	/**
	 * writes yourTurn to BufferedWriter out
	 */

	public void sendYourTurn() {
		this.yourTurn=true;
		try {
			out.write("yourTurn\n");
			out.flush();
			System.out.println("yourTurn");
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	/**
	 * Sets the instance variable game to gameIn;
	 * @param gameIn the game that this.game will be changed to
	 */
	//@ requires gameIn != null;

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
	public void quit(Peer p){
		try{
			if(this.equals(p)){
				out.write(Constants.Protocol.SEND_GAME_OVER + " " + false + " " + true + "\n" );
				out.flush();
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/** returns name of the peer object */
	public String getName() {
		return name;
	}

	/**
	 * writes a message to BufferedReader out
	 * @param peers An ArrayList of type Peer containing all Peers that will join the new Game
	 */

	public void startGame(ArrayList<Peer> peers) {
		this.state = "ingame";
		try {
			out.write(Constants.Protocol.MAKE_GAME + " " + peers.get(0).getName() + " "
					+ peers.get(1).getName() + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(state);
	}
}

