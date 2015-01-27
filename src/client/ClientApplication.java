package client;

import gui.ClientGUI;
import gui.GameGui;

import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import strategies.LosingStrategy;
import strategies.OneStepAheadStrategy;
import strategies.PerfectStrategy;
import strategies.RandomStrategy;
import model.AIGame;
import model.ComputerPlayer;
import model.Game;
import model.Player;

public class ClientApplication {
	private ClientGUI gui;
	private JFrame frame;
	public ClientApplication(ClientGUI guiArg){
		gui = guiArg;
	}

	public void startUp() {
		int ai = JOptionPane.showOptionDialog(gui, "Choose your player type",
				"Player type", JOptionPane.OK_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] {
				"MultiPlayer", "Offline", "Computer" }, 2);
		if (ai == JOptionPane.CLOSED_OPTION) {
			System.exit(0);
		}
		String namePlayer = gui.askForName();
		switch (ai) {
		case 0:
			createClient(namePlayer);
			break;
		case 1:
			startLocalGame(namePlayer, gui.askForName());
			break;
		case 2:
			startAIGame(namePlayer, JOptionPane.showOptionDialog(gui, "Choose your player type",
				"Player type", JOptionPane.OK_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] {
				"Easy", "Medium", "Hard" }, 2));
			break;
		}
	}
	public void createClient(String name){
		int port = -1;
		Client client;
		InetAddress address = gui.askForIP();
		port = gui.askForPortNumber();
		client = new Client(address, port, name);
		Thread a = new Thread(client);
		a.start();
	}
	public void startLocalGame(String name1, String name2){
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(new Player(name1));
		players.add(new Player(name2));
		System.out.println(players.size());
		Game game = new Game(players);
		GameGui guiGame = new GameGui(game);
		game.addObserver(guiGame);
		game.start();
		frame = new JFrame();
		frame.add(guiGame);
		frame.setSize(933, 800);
		frame.setVisible(true);
	}
	public void startAIGame(String name, int AILevel){
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(new Player(name));
		ComputerPlayer AI = null;
		switch(AILevel){
		case 0:
			AI= new ComputerPlayer(new RandomStrategy());
		case 1:
			AI= new ComputerPlayer(new OneStepAheadStrategy());
		case 2:
			AI= new ComputerPlayer(new PerfectStrategy());
		}
		players.add(AI);
		Game game = new AIGame(players);
		GameGui guiGame = new GameGui(game);
		game.addObserver(guiGame);
		game.start();
		frame = new JFrame();
		frame.add(guiGame);
		frame.setSize(933, 800);
		frame.setVisible(true);
	}
	public static void main(String[] args){
		ClientApplication a =  new ClientApplication (new ClientGUI());
		a.startUp();
	}
}
