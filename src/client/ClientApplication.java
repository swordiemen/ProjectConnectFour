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

	/**
	 * Creates a new ClientApplication.
	 * 
	 * @param guiArg
	 *            The ClientGUI this ClientApplication should use.
	 */
	public ClientApplication(ClientGUI guiArg) {
		gui = guiArg;
	}

	/**
	 * Initializes all the variables, by asking for them in a window.
	 */
	public void startUp() {
		int ai = JOptionPane.showOptionDialog(gui, "Choose your player type", "Player type",
				  JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {
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
						  "Player type", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						  new String[] {"Easiest", "Easier", "Easy"}, 2));
				break;
		}
	}

	/**
	 * Creates a new Client, given a name.
	 * 
	 * @param name
	 *            The name of the Client.
	 */
	public void createClient(String name) {
		int port = -1;
		Client client;
		InetAddress address = gui.askForIP();
		port = gui.askForPortNumber();
		client = new Client(address, port, name);
		Thread a = new Thread(client);
		a.start();
	}

	/**
	 * Creates and starts a local Game.
	 * 
	 * @param name1
	 *            The name of the first player.
	 * @param name2
	 *            THe name of the second player.
	 */
	public void startLocalGame(String name1, String name2) {
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

	/**
	 * Creates and starts a new AIGame.
	 * 
	 * @param name
	 *            The name of the human player.
	 * @param aILevel
	 *            The difficulty of the AI.
	 */
	public void startAIGame(String name, int aILevel) {
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(new Player(name));
		ComputerPlayer ai = null;
		switch (aILevel) {
			case 0:
				ai = new ComputerPlayer(new LosingStrategy());
				break;
			case 1:
				ai = new ComputerPlayer(new RandomStrategy());
				break;
			case 2:
				ai = new ComputerPlayer(new OneStepAheadStrategy());
		}
		players.add(ai);
		Game game = new AIGame(players);
		GameGui guiGame = new GameGui(game);
		game.addObserver(guiGame);
		game.start();
		frame = new JFrame();
		frame.add(guiGame);
		frame.setSize(933, 800);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		ClientApplication a = new ClientApplication(new ClientGUI());
		a.startUp();
	}
}
