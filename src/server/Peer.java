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
	private boolean canChat;
	private boolean canChallenge;
	private boolean canLeaderboard;
	private String state;
	private Boolean exit = false;
	private Peer inChallenge;

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
				quit();
			}
			System.out.println("CLIENT SAYS " + output + ": In State: " + state);
			splitOutput = output.split(" ");
			if (state.equals(Constants.STATE_START)) {
				if (splitOutput[0].equals(Constants.Protocol.SEND_HELLO)) {
					this.setName(splitOutput[2]);
					//if(server.isValidName(getName())){
					byte[] options = new byte[Constants.NUMBER_OF_OPTIONS];
					System.out.println("1: " + splitOutput[0] + " 2: " + splitOutput[1] + " 3: " + splitOutput[2]);
					for(int i = 0; i < Constants.NUMBER_OF_OPTIONS; i++){
						options[i] = Byte.parseByte(splitOutput[1].substring(i, i + 1));
					}
					System.out.println(options);
					canChat = options[0] % 2 == 1;
					canChallenge = options[1] % 2 == 1;
					canLeaderboard = options[2] % 2 == 1;
					try {
						out.write(Constants.Protocol.SEND_HELLO + " " + name
								+ "\n");
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					state = Constants.STATE_LOBBY;
					server.sendUpdatedPlayerList();
					//}else{
					//						try{
					//							out.write(Constants.Protocol.SEND_ERROR + " invalidName je faalt\n"); //TODO add in constants
					//							out.flush();
					//						}catch(IOException e){
					//							e.printStackTrace();
					//							quit();
					//						}

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
			}  
			if (state.equals(Constants.STATE_LOBBY)) {

				if (splitOutput[0].equals(Constants.Protocol.SEND_PLAY)) {
					server.addPlayer(this);
				}
				if (splitOutput[0].equals(Constants.Protocol.SEND_CHALLENGE)){
					sendChallenge(splitOutput[1]);
				}
				if(splitOutput[0].equals(Constants.Protocol.ACCEPT_CHALLENGE)){
					acceptChallenge();
				}
				if(splitOutput[0].equals(Constants.Protocol.REJECT_CHALLENGE)){
					refuseChallenge();
				}
				if(splitOutput[0].equals(Constants.Protocol.CHAT)){
					server.sendLobbyChat(splitOutput, getName());
				}
			}
			if (state.equals(Constants.STATE_INGAME)) {
				System.out.println(state);
				if (splitOutput[0].equals(Constants.Protocol.SEND_MOVE)) {
					System.out.println("Send Move Received");
					game.takeTurn(Integer.parseInt(splitOutput[1]));
				}
				if (splitOutput[0].equals(Constants.Protocol.SEND_QUIT)) {
					game.endGame(this);
				}
			}
			if(splitOutput[0].equals(Constants.Protocol.SEND_QUIT)){
				quit();
			}
		}
	}


	private void setName(String argName) {
		this.name = argName;
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
			e.printStackTrace();
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
	public void acceptChallenge(){
		ArrayList<Peer> peers = new ArrayList<Peer>();
		peers.add(this);
		peers.add(inChallenge);
		server.startNewGame(peers);
	}
	public void refuseChallenge(){
		inChallenge.challengeCancelled();
		challengeCancelled();
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
			e.printStackTrace();
		}
		System.out.println(state);
	}

	public void sendUpdate(String players) {
		players = players + "\n";
		try {
			out.write(Constants.Protocol.SEND_PLAYERS + players);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendChallenge(String name){
		for (Peer p: server.getPeerList()){
			if(p.getName().equals(name)){
				if(!p.inChallenge()){
					p.challenged(this);
				}else{
					challengeCancelled();
				}
			}
		}
	}
	public void challengeCancelled(){
		try{
			out.write(Constants.Protocol.SEND_CHALLENGE_CANCELLED + "\n");
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public boolean inChallenge(){
		return inChallenge!=null;
	}
	public void challenged(Peer p){
		inChallenge = p;
		try{
			out.write(Constants.Protocol.SEND_CHALLENGED + " " + p.getName() + "\n");
			out.flush();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void endGame(Boolean draw, String winner) {
		try{
			out.write(Constants.Protocol.SEND_GAME_OVER + " " + draw + " " + 
					winner + "\n" );
			out.flush();
			state = Constants.STATE_LOBBY;
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void endGame(Boolean draw) {
		try{
			out.write(Constants.Protocol.SEND_GAME_OVER + " " + draw + "\n" );
			out.flush();
			state = Constants.STATE_LOBBY;
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	public void quit(){
		try{
			exit = true;
			server.removePlayer(this);
			out.close();
			in.close();
			sock.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public String getState(){
		return state;
	}

	public boolean isChallangable(){
		return canChallenge;
	}

	public boolean isLeaderboardable(){
		return canLeaderboard;
	}

	public boolean isChattable(){
		return canChat;
	}

	public void sendLobbyChat(String[] splitOutput, String sender) {
		StringBuilder sb = new StringBuilder();
		sb.append(" " + sender + ":");
		for(int i = 1; i < splitOutput.length; i++){
			sb.append(" " + splitOutput[i]);
		}
		try{
			out.write(Constants.Protocol.SEND_CHAT + sb.toString() + "\n");
			System.out.println("Sending lobby chat:" + sb.toString());
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
