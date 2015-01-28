package server;

import gui.ClientGUI;

public class ServerApplication {
	private ClientGUI gui;

	/**
	 * Creates a new ServerApplication.
	 * 
	 * @param guiArg
	 *            The GUI this server uses for asking for user inputs.
	 */
	public ServerApplication(ClientGUI guiArg) {
		gui = guiArg;
		startUp();
	}

	/**
	 * Creates a new server, and starts it. The port is asked by displaying a
	 * window, and asking for a port.
	 */
	public void startUp() {
		Server server = new Server();
		server.setPort(gui.askForPortNumber());
		Thread serverThread = new Thread(server);
		serverThread.start();
	}

	public static void main(String[] args) {
		new ServerApplication(new ClientGUI());
	}
}
