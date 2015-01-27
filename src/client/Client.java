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
					} else if(inputWords[0].equals(Constants.Protocol.SEND_CHALLENGE)){
						challengeAccepted("ACCEPT");
					} else if(inputWords[0].equals(Constants.Protocol.SEND_CHALLENGE_CANCELLED)){
						challengeRefused("DECLINE");
					}
				}
				if (state.equals(Constants.STATE_INGAME)) {
					if (inputWords[0].equals(Constants.Protocol.MAKE_MOVE)) {
						makeMove(inputWords[1]);
					}
				}
				if(inputWords[0].equals(Constants.Protocol.SEND_ERROR)){
					
				}
			}else{
				
			}
		}
		
	}

	public void makeGame() {
		try {
			out.write(Constants.Protocol.SEND_PLAY + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logIn() {
		try {
			out.write(Constants.Protocol.SEND_HELLO + " " + "111"  + " " + name
					+ "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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

	public void sendTurn(int collumn) {
		try {
			out.write(Constants.Protocol.SEND_MOVE + " " + collumn + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendChat(String msg) {
		try {
			out.write(Constants.Protocol.CHAT + " " + msg + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendChat(String msg, String name) {
		try {
			out.write(Constants.Protocol.CHAT + " " + name + " " + msg + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendChallenge(String name) {
		if (!challenged) {
			try {
				out.write(Constants.Protocol.SEND_CHALLENGE + " "+ name + "\n");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			challenged = true;
		}
	}

	public void goToLobby() {
		System.out.println("In goToLobby");
		lobby = new Lobby(this);
		state = Constants.STATE_LOBBY;
	}

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

	public void displayError(String error) {
		if(lobby != null){
			lobby.displayError(error);
		}else{
//			displayErrorNoLobby(error, new ClientGUI());
		}
	}

	public void challenged(String name) {
		if (!challenged) {
			lobby.challenged(name);
			challenged = true;
		}

	}

	public void challengeAccepted(String name) {
		challenged = false;
		try {
			out.write(Constants.Protocol.ACCEPT_CHALLENGE + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void challengeRefused(String name) {
		challenged = false;
		try {
			out.write(Constants.Protocol.REJECT_CHALLENGE + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playAgain() {
		sendChallenge(opponent);
		goToLobby();
	}

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

	public String getName() {
		return name;
	}

//	public void displayErrorNoLobby(String errorMsg, ClientGUI gui){
//		String[] options = new String[]{"OK"};
//		JOptionPane.showOptionDialog(gui, errorMsg, "Error occurred.", JOptionPane.DEFAULT_OPTION, 
//				JOptionPane.ERROR_MESSAGE, null, options, options[0]);
//		name = gui.askForName();
//		logIn();
//
//	}

	public void checkInputName(String[] inputWords){
		if (inputWords[0].equals(Constants.Protocol.SEND_HELLO)) {
			goToLobby();
		}else if(inputWords[0].equals(Constants.Protocol.SEND_ERROR)){
			if(inputWords[1].equals("invalidName")){
				StringBuilder reason = new StringBuilder();
				if(inputWords.length > 2){
					for(int i = 2; i < inputWords.length; i++){
						reason.append(" " + inputWords[i]);
					}
				}
				if(reason.length() > 0){
					displayError("Je naam is ongeldig met reden: " + reason);
				}else{
					displayError("Je naam is ongeldig (geen reden gegeven)");
				}
			}else{
				displayError("We krijgen een error van de server.\n Wij weten ook niet waarom.");
			}
		}
		else {
			System.out.println("Faal in checkInputName");
		}
	}

	public void quitGame(){
		frame.dispose();
		state = Constants.STATE_LOBBY;
	}

	public String getOpponent(){
		return opponent;
	}
}
