package client;

import exceptions.FalseMoveException;
import gui.ClientGUI;
import gui.GameGui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import constants.Constants;
import model.Game;
import model.HumanPlayer;
import model.Player;

public class Client implements Runnable {
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private String name;
	private String state;	
	private ClientGame game;

	public Client(InetAddress address, int port, String Name) {
		name = Name;
		try {
			socket = new Socket(address, port);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			state = "Start";
			logIn();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Client createClient(ClientGUI gui) {
		Client client = null;
		int port = -1;
		String namePlayer;
		InetAddress address;
		int ai = JOptionPane.showOptionDialog(gui, "Choose your player type",
				"Player type", JOptionPane.OK_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] {
				"MultiPlayer", "Human" }, 2);
		if (ai == JOptionPane.CLOSED_OPTION) {
			System.exit(0);
		}
		namePlayer = gui.askForName();
		switch (ai) {
		case 0:
		}
		while (client == null) {
			address = gui.askForIP();
			port = gui.askForPortNumber();
			client = new Client(address, port, namePlayer);

		}
		return client;
	}
	@Override
	public void run() {
		String input;
		String[] inputWords;
		while(state.equals("Start")){
			try{
				input = in.readLine();
				System.out.println(input);
				inputWords = input.split(" ");
				if(inputWords[0].equals(Constants.Protocol.SEND_HELLO)){
					out.write(Constants.Protocol.SEND_PLAY + "\n");
					out.flush();
					state = "Lobby";
					System.out.println(state);
				}else{
					System.out.println(input);
				}
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		while(state.equals("Lobby")){
			try{
				input = in.readLine();
				System.out.println(input);
				inputWords = input.split(" ");
				if(inputWords[0].equals(Constants.Protocol.MAKE_GAME)){
					System.out.println(input);
					game = new ClientGame(this);
					GameGui gameGui = new GameGui(game);
					game.addObserver(gameGui);
					gameGui.addPlayer(new HumanPlayer(inputWords[1]));
					gameGui.addPlayer(new HumanPlayer(inputWords[2]));
					game.reset(gameGui.getPlayerList());
					game.start();
					JFrame frame = new JFrame();
					frame.add(gameGui);
					frame.setSize(933, 800);
					frame.setVisible(true);
					state = "inGame";
				}else{
					System.out.println(input);
				}
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		while(state.equals("inGame")){
			try{
				input = in.readLine();
				inputWords = input.split(" ");
				System.out.println(input);
				if(inputWords[0].equals(Constants.Protocol.MAKE_MOVE)){
					game.doTurn(Integer.parseInt(inputWords[1]));
				}else{
					System.out.println(input);
				}
			}catch (IOException e){
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (FalseMoveException e) {
				quit();
				e.printStackTrace();
			}
		}
	}
	public void makeGame(){
		try{
			out.write(Constants.Protocol.SEND_PLAY);
			out.flush();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	public void logIn() {
		try {
			out.write(Constants.Protocol.SEND_HELLO + " " + name + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void sendTurn(int collumn) {
		try {
			out.write(Constants.Protocol.SEND_MOVE + " " + collumn + "\n" );
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void quit(){
		try{
			out.write(Constants.Protocol.SEND_QUIT + " ");
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		Client client = createClient(new ClientGUI());
		Thread a = new Thread(client);
		a.start();
	}
}
