package client;

import java.net.InetAddress;

import javax.swing.JOptionPane;

import model.Player;

public class Client {

	public Client(InetAddress address, int port, String Name) {
		try {
			logIn();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(gui,
					"Could not establish a connection.\nPlease try again.",
					"Could not establish a connection",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static Client createClient(ClientGUI gui) {
		Client client = null;
		int port = -1;
		InetAddress address;
		String name;
		Player player = null;
		int nrPlayers = 2;
		int ai = JOptionPane.showOptionDialog(gui, "Choose your player type",
				"Player type", JOptionPane.OK_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] {
						"MultiPlayer", "Human" }, 2);
		if (ai == JOptionPane.CLOSED_OPTION) {
			System.exit(0);
		}
		switch(ai){
		case 0:
			new Game()
		}
		while (client == null) {
			address = gui.askForIP();
			port = gui.askForPortNumber();
			nrPlayers = gui.askForPlayers();
			client = new Client(address, port, name);

		}
		return client;
	}
}
