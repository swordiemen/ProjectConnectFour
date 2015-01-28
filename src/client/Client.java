package client;

import exceptions.FalseMoveException;
import gui.ClientGUI;
import gui.GameGui;
import gui.Lobby;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import constants.Constants;
import model.HumanPlayer;
import model.Mark;
import model.Player;

/**
 * A Client, who can connect to a server to play games of connect four.
 * @author Yannick Mijsters & Tim Blok
 *
 */
public class Client implements Runnable {
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private String name;
	private String state;
	private ClientGame game;
	boolean exit = false;
	private String opponent;
	private Lobby lobby;
	private boolean challenged = false;
	private JFrame frame;

	/**
	 * Creates a new client. 
	 * @param address The address of the server this Client wants to connect to.
	 * @param port The port of the server this Client wants to connect to.
	 * @param Name The name of this Client.
	 */
	public Client(InetAddress address, int port, String Name) {
		name = Name;
		try {
			socket = new Socket(address, port);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			state = Constants.STATE_START;
			logIn();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		String input = null;
		String[] inputWords;
		while (!exit) {
			try {
				input = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("SERVER SAYS " + input);
			if(input !=null){
				inputWords = input.split(" ");
				if (state.equals(Constants.STATE_START)) {
					checkInputName(inputWords); //TODO dit fixen, server geeft geen errors.
				}
				if (state.equals(Constants.STATE_LOBBY)) {
					if (inputWords[0].equals(Constants.Protocol.MAKE_GAME)) {
						createGame(inputWords[1], (inputWords[2]));
					} else if (inputWords[0]
							.equals(Constants.Protocol.SEND_CHALLENGED)) {
						challenged(inputWords[1]);
					} else if (inputWords[0]
							.equals(Constants.Protocol.SEND_PLAYERS)) {
						sendUpdatePlayers(inputWords);
					} else if(inputWords[0].equals(Constants.Protocol.SEND_CHAT)){
						lobby.receivedChat("Server", inputWords);
					} else if(inputWords[0].equals(Constants.Protocol.ACCEPT_CHALLENGE)){
						challengeAccepted("ACCEPT");
					} else if(inputWords[0].equals(Constants.Protocol.REJECT_CHALLENGE)){
						challengeRefused("DECLINE");
					}
				}
				if (state.equals(Constants.STATE_INGAME)) {
					if (inputWords[0].equals(Constants.Protocol.MAKE_MOVE)) {
						makeMove(inputWords[1]);
					}else if(inputWords[0].equals(Constants.Protocol.SEND_ERROR_INVALIDMOVE)){
						displayError("Invalid move.");
					}
				}
				if(inputWords[0].equals(Constants.Protocol.SEND_ERROR_INVALIDCOMMAND)){

				}
			}else{

			}
		}

	}

	/**
	 * Sends a command to the server that tells it we want to play.
	 */
	public void makeGame() {
		try {
			out.write(Constants.Protocol.SEND_PLAY + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a command to the server telling our name and options.
	 * This client's options are (as defined in Constants): chat and challenge.
	 */
	public void logIn() {
		try {
			out.write(Constants.Protocol.SEND_HELLO + " " + Constants.CLIENT_OPTIONS  + " " + name
					+ "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This method is called when the Server gives us a playerList. The lobby will be updated with the new players.
	 * @param inputNames
	 */
	public void sendUpdatePlayers(String[] inputNames) {
		ArrayList<String> playerList = new ArrayList<String>();
		for (int i = 1; i < inputNames.length; i++) {
			if (inputNames[i] != name) {
				playerList.add(inputNames[i]);
				System.out.println("Added " + inputNames[i]);
			}
		}
		lobby.setPlayerList(playerList);
	}

	/**
	 * This method is called when the Server gives us a move for our game.
	 * @param turn The column the player put a disc in.
	 */
	public void makeMove(String turn) {
		try {
			game.doTurn(Integer.parseInt(turn));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FalseMoveException e) {
			quit();
			e.printStackTrace();
		}
	}

	/**
	 * Sends a command to the server, which specifies where we want our disc to be put in.
	 * We can't do moves in our own Game, as it has to be approved by the Server first.
	 * @param column The column where we want to put our disc in.
	 */
	public void sendTurn(int column) {
		try {
			out.write(Constants.Protocol.SEND_MOVE + " " + column + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Sends a chat to the Server, which will be sent to all the other Peers in the Lobby.
	 * @param msg The message to be sent.
	 */
	public void sendChat(String msg) {
		try {
			out.write(Constants.Protocol.CHAT + " " + msg + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The same as sendChat(String msg), but with a name. <b>Not used.</b>
	 * @param msg The message to be sent.
	 * @param name The name of the sender (this Client's name).
	 */
	public void sendChat(String msg, String name) {
		try {
			out.write(Constants.Protocol.CHAT + " " + name + " " + msg + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a challenge request to another Client. 
	 * A client can only challenge/be challenged by one Client at a time.
	 * @param name The name of the Client to be challenged.
	 */
	public void sendChallenge(String challengedName) {
		if(challengedName.equals(this.name)){
			displayError("Je kunt jezelf niet challengen!");
		}else{
			if (!challenged) {
				try {
					out.write(Constants.Protocol.SEND_CHALLENGE + " "+ challengedName + "\n");
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				challenged = true;
			}
		}
	}

	/**
	 * Sends this Client to the lobby.
	 */
	public void goToLobby() {
		System.out.println("In goToLobby");
		lobby = new Lobby(this);
		state = Constants.STATE_LOBBY;
	}

	/**
	 * Creates a new Game. Only called when the Server sends us makeGame, with 2 players.
	 * @param player1 The first player's name of the Game.
	 * @param player2 The second player's name of the Game.
	 */
	public void createGame(String player1, String player2) {
		game = new ClientGame(this);
		Mark ownMark;
		if (player1.equals(name)) {
			ownMark = Mark.RED;
			opponent = player2;
		} else {
			ownMark = Mark.YELLOW;
			opponent = player1;
		}
		GameGui gameGui = new GameGui(game, ownMark, this);
		game.addObserver(gameGui);
		gameGui.addPlayer(new HumanPlayer(player1));
		gameGui.addPlayer(new HumanPlayer(player2));
		game.reset(gameGui.getPlayerList());
		game.start();
		frame = new JFrame();
		frame.add(gameGui);
		frame.setSize(933, 800);
		frame.setVisible(true);
		state = Constants.STATE_INGAME;
	}

	/**
	 * Displays an error window.
	 * @param error The error to be displayed.
	 */
	public void displayError(String error) {
		if(lobby != null){
			lobby.displayError(error);
		}else{
			displayErrorNoLobby(error, new ClientGUI());
		}
	}

	/**
	 * This method is called when the Server tells us we have been challenged.
	 * @param name The challenger.
	 */
	public void challenged(String name) {
		if (!challenged) {
			lobby.challenged(name);
			challenged = true;
		}

	}

	/**
	 * Tells the server we accept our challenge.
	 * @param name The challenger.
	 */
	public void challengeAccepted(String name) {
		challenged = false;
		try {
			out.write(Constants.Protocol.ACCEPT_CHALLENGE + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tells the server we refuse our challenge.
	 */
	public void challengeRefused(String name) {
		challenged = false;
		try {
			out.write(Constants.Protocol.REJECT_CHALLENGE + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Asks the opponent if he wants to play again. 
	 * Basically functions as a challenge.
	 */
	public void playAgain() {
		goToLobby();
		sendChallenge(opponent);
	}

	/**
	 * Tells the Server we want to quit.
	 */
	public void quit() {
		exit = true;
		try {
			out.write(Constants.Protocol.SEND_QUIT + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//	public static void main(String[] args) {
	//		Client client = createClient(new ClientGUI());
	//		Thread a = new Thread(client);
	//		a.start();
	//	}

	/**
	 * Returns the name of this Client.
	 * @return The name of this Client.
	 */
	public String getName() {
		return name;
	}

	public void displayErrorNoLobby(String errorMsg, ClientGUI gui){
		String[] options = new String[]{"OK"};
		JOptionPane.showOptionDialog(gui, errorMsg, "Error occurred.", JOptionPane.DEFAULT_OPTION, 
				JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		name = gui.askForName();
		logIn();

	}

	/**
	 * This method is called whenever we expect a SEND_HELLO from the Server.
	 * If our name is invalid, asks for a different name.
	 * @param inputWords The message the Server gives us.
	 */
	public void checkInputName(String[] inputWords){
		if (inputWords[0].equals(Constants.Protocol.SEND_HELLO)) {
			goToLobby();
		}else if(inputWords[0].equals(Constants.Protocol.SEND_ERROR_INVALIDNAME) || 
				inputWords[0].equals("invalidName")){
			StringBuilder reason = new StringBuilder();
			if(inputWords.length > 2){
				for(int i = 2; i < inputWords.length; i++){
					reason.append(" " + inputWords[i]);
				}
			}
			if(reason.length() > 0){
				displayError("Je naam is ongeldig met reden: " + reason);
			}else{
				displayError("Je naam is ongeldig (geen reden gegeven).");
			}
			name = new ClientGUI().askForName();
			logIn();
		}
		else{
			displayError("We krijgen een error van de server.\n Wij weten ook niet waarom.");
		}
	}


	/**
	 * Returns the Client from a Game back to the Lobby.
	 */
	public void quitGame(){
		frame.dispose();
		state = Constants.STATE_LOBBY;
	}

	/**
	 * Returns the name of the opponent, if this Client is in-game.
	 * @return The name of the opponent.
	 */
	public String getOpponent(){
		return opponent;
	}

	public boolean isChallenged() {
		return challenged;
	}
}
