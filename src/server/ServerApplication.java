package server;

import gui.ClientGUI;

public class ServerApplication {
	private ClientGUI gui;
	public ServerApplication(ClientGUI guiArg){
		gui = guiArg;
		startUp();
	}
	public void startUp(){
		Server server = new Server();
		server.setPort(gui.askForPortNumber());
	}
	public static void main(String[] args){
		new ServerApplication(new ClientGUI());
	}
}
