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

	// @ protected invariant name !+ null;
	// @ protected invariant sock != null;
	// @ protected invariant in != null
	// @ protected invariant out != null;
	// @ protected invariant server != null;
	// @ protected invariant game != null;
	// @ private invariant state != null;

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
		out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
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
					if (server.isValidName(getName(), this)) {
						byte[] options = new byte[Constants.NUMBER_OF_OPTIONS];
						System.out.println("1: " + splitOutput[0] + " 2: " + splitOutput[1]
								  + " 3: " + splitOutput[2]);
						for (int i = 0; i < Constants.NUMBER_OF_OPTIONS; i++) {
							options[i] = Byte.parseByte(splitOutput[1].substring(i, i + 1));
						}
						System.out.println(options);
						canChat = options[0] % 2 == 1;
						canChallenge = options[1] % 2 == 1;
						canLeaderboard = options[2] % 2 == 1;
						try {
							out.write(Constants.Protocol.SEND_HELLO + " "
									  + Constants.CLIENT_OPTIONS + " " + name + "\n");
							out.flush();
						} catch (IOException e) {
							quit();
						}
						goToLobby();
						server.sendUpdatedPlayerList();
					} else {
						String reason = "Je naam bestaat al.";
						if (getName().contains(" ")) {
							reason = "Je naam bevat een spatie.";
						}
						try {
							out.write(Constants.Protocol.SEND_ERROR_INVALIDNAME + " " + reason
									  + "\n");
							out.flush();
						} catch (IOException e) {
							quit();
							quit();
						}
					}

				} else {
					try {
						out.write(Constants.Protocol.SEND_ERROR_INVALIDCOMMAND + "\n");
						out.flush();
					} catch (IOException e) {
						quit();
					}
				}
			}
			if (state.equals(Constants.STATE_LOBBY)) {

				if (splitOutput[0].equals(Constants.Protocol.SEND_PLAY)) {
					server.addPlayer(this);
				}
				if (splitOutput[0].equals(Constants.Protocol.SEND_CHALLENGE)) {
					sendChallenge(splitOutput[1]);
				}
				if (splitOutput[0].equals(Constants.Protocol.ACCEPT_CHALLENGE)) {
					acceptChallenge();
				}
				if (splitOutput[0].equals(Constants.Protocol.REJECT_CHALLENGE)) {
					refuseChallenge();
				}
				if (splitOutput[0].equals(Constants.Protocol.CHAT)) {
					server.sendLobbyChat(splitOutput, getName());
				}
				if (splitOutput[0].equals(Constants.Protocol.GET_LEADERBOARD)) {
					server.sendUpdatedPlayerList();
				}
			}
			if (state.equals(Constants.STATE_INGAME)) {
				System.out.println(state);
				if (splitOutput[0].equals(Constants.Protocol.SEND_MOVE)) {
					game.takeTurn(Integer.parseInt(splitOutput[1]));
				}
				if (splitOutput[0].equals(Constants.Protocol.SEND_QUIT)) {
					game.endGame(this);
				}
			}
			if (splitOutput[0].equals(Constants.Protocol.SEND_QUIT)) {
				quit();
			}
		}
	}

	/**
	 * Sets the name of this Peer.
	 * 
	 * @param argName
	 *            The new Peer's name.
	 */
	// @ requires argName != null;
	// @ ensures getName().equals(argName);
	private void setName(String argName) {
		this.name = argName;
	}

	/**
	 * Sends a move to the Client which is approved by the Server.
	 * 
	 * @param column
	 *            The column the move is made.
	 */
	// @ requires column > 0 && column < Constants.COLUMNS;
	public void sendMove(int column) {
		try {
			out.write(Constants.Protocol.MAKE_MOVE + " " + column + "\n");
			out.flush();
		} catch (IOException e) {
			quit();
		}
	}

	/**
	 * Sets this Peer's Game.
	 * 
	 * @param argGame
	 *            The new Game.
	 */
	// @ requires argGame != null;
	// @ ensures getGame() == argGame();
	public void setGame(ServerGame argGame) {
		this.game = argGame;
	}

	/**
	 * Returns the ServerGame of this Peer.
	 * 
	 * @return The ServerGame of this Peer.
	 */
	/* @ pure @ */public ServerGame getGame() {
		return this.game;
	}

	/**
	 * Quits a Game. The other Peer is then declared the winner.
	 * 
	 * @param p
	 *            The Peer that stayed.
	 */
	// @ requires p != null;
	public void quit(Peer p) {
		try {
			if (this.equals(p)) {
				out.write(Constants.Protocol.SEND_GAME_OVER + " " + false + " " + p.getName()
						  + "\n");
				out.flush();
			}

		} catch (IOException e) {
			quit();
		}
	}

	/**
	 * This method is called when a challenge is accepted. A new game will be
	 * created with the challenger and challenged.
	 */
	// @ ensures this.getState().equals("Constants.STATE_INGAME);
	// @ ensures inChallenge() == false;
	public void acceptChallenge() {
		ArrayList<Peer> peers = new ArrayList<Peer>();
		server.sendDirectedChat(inChallenge.getName(), this.getName()
				  + " has accepted your challenge.");
		peers.add(this);
		peers.add(inChallenge);
		System.out.println("Start New Game");
		server.startNewGame(peers);
		inChallenge = null;
	}

	/**
	 * Refuses a challenge.
	 */
	// @ ensures inChallenge == false;
	public void refuseChallenge() {
		server.sendDirectedChat(inChallenge.getName(), this.getName()
				  + " has declined your challenge.");
		inChallenge.challengeCancelled("Challenge refused.");
		challengeCancelled("Challenge refused.");
	}

	/**
	 * Returns name of the peer object
	 */
	/* @ pure @ */public String getName() {
		return name;
	}

	/**
	 * Writes a message to BufferedReader out
	 * 
	 * @param peers
	 *            An ArrayList of type Peer containing all Peers that will join
	 *            the new Game
	 */
	// @ requires peers != null
	// @ ensures (\forall(Peer p; !(server.getLobbyList().contains(p))));
	// @ ensures (\forall(Peer p; p.getState().equals(Constants.STATE_INGAME)));
	public void startGame(ArrayList<Peer> peers) {
		server.removeFromLobbyList(this);
		state = Constants.STATE_INGAME;
		try {
			out.write(Constants.Protocol.MAKE_GAME + " " + peers.get(0).getName() + " "
					  + peers.get(1).getName() + "\n");
			out.flush();
		} catch (IOException e) {
			quit();
		}
		System.out.println(state);
	}

	/**
	 * Sends an updated playerList to the Client.
	 * 
	 * @param players
	 */
	// @ requires players != null;
	public void sendUpdate(String players) {
		players = players + "\n";
		try {
			out.write(Constants.Protocol.SEND_PLAYERS + players);
			out.flush();
		} catch (IOException e) {
			quit();
		}
	}

	/**
	 * Sends a challenge to another client. Can only be done if that client
	 * isn't already challenging or challenged.
	 * 
	 * @param name
	 *            The name of the client to be challenged.
	 */
	// @ requires name != null;
	// @ ensures (\forall(Peer p; (p.getName().equals(name) && !p.inChallenge())
	// => p.inChallenge() == true;
	public void sendChallenge(String name) {
		for (Peer p : server.getPeerList()) {
			if (p.getName().equals(name)) {
				if (!p.inChallenge()) {
					p.challenged(this);
				} else {
					challengeCancelled("Player is already challenged");
				}
			}
		}
	}

	/**
	 * Tells the client his challenge was cancelled.
	 * 
	 * @param reason
	 *            The reason of the cancel.
	 */
	// @ requires reason != null;
	// @ ensures this.inChallenge() == null;
	public void challengeCancelled(String reason) {
		inChallenge = null;
		try {
			out.write(Constants.Protocol.SEND_CHALLENGE_CANCELLED + " " + reason + "\n");
			out.flush();
		} catch (IOException e) {
			quit();
		}
	}

	/**
	 * Checks if this Peer is in a challenge.
	 * 
	 * @return
	 */
	/* @ pure @ */public boolean inChallenge() {
		return inChallenge != null;
	}

	/**
	 * Tells a client he has been challenged by p.getName().
	 * 
	 * @param p
	 *            The Peer that challenged this Peer.
	 */
	// @ requires p != null;
	// @ ensures this.inChallenge() == true;
	public void challenged(Peer p) {
		inChallenge = p;
		try {
			out.write(Constants.Protocol.SEND_CHALLENGED + " " + p.getName() + "\n");
			out.flush();
		} catch (IOException e) {
			quit();
		}
	}

	/**
	 * Tells the Client the game has ended.
	 * 
	 * @param draw
	 *            Whether it was a draw.
	 * @param winner
	 *            Who the winner was.
	 */
	// @ requires winner != null;
	// @ ensures this.getState().equals(Constants.STATE_LOBBY);
	public void endGame(Boolean draw, String winner) {
		try {
			out.write(Constants.Protocol.SEND_GAME_OVER + " " + draw + " " + winner + "\n");
			out.flush();
			goToLobby();
		} catch (IOException e) {
			quit();
		}
	}

	/**
	 * Simulates this Peer going to a Lobby.
	 */
	// @ ensures this.getState().equals(Constants.STATE_LOBBY);
	// @ ensures server.getLobbyList().contains(this);
	private void goToLobby() {
		state = Constants.STATE_LOBBY;
		server.addToLobbyList(this);

	}

	/**
	 * Tells the Client the game has ended.
	 * 
	 * @param draw
	 *            Whether it was a draw.
	 */
	// @ ensures this.getState().equals(Constants.STATE_LOBBY);
	public void endGame(Boolean draw) {
		try {
			out.write(Constants.Protocol.SEND_GAME_OVER + " " + draw + "\n");
			out.flush();
			goToLobby();
		} catch (IOException e) {
			quit();
		}
	}

	/**
	 * This method is called when the Client says he's quitting. Removes this
	 * Peer from the server list of Peers, and closes all connections.
	 */
	// @ ensures !(server.getPlayerList(contains(this)));
	public void quit() {
		try {
			exit = true;
			server.removePlayer(this);
			out.close();
			in.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the state of this Peer (Start, lobby, or in game).
	 * 
	 * @return The state of this Peer.
	 */
	/* @ pure @ */public String getState() {
		return state;
	}

	/**
	 * Checks if this Peer supports challenging.
	 * 
	 * @return Whether this Peer supports challenging.
	 */
	/* @ pure @ */public boolean isChallangable() {
		return canChallenge;
	}

	/**
	 * Checks if this Peer supports leaderboards.
	 * 
	 * @return Whether this Peer supports leaderboards.
	 */
	/* @ pure @ */public boolean isLeaderboardable() {
		return canLeaderboard;
	}

	/**
	 * Checks if this Peer supports chat.
	 * 
	 * @return Whether this Peer supports chat.
	 */
	/* @ pure @ */public boolean isChattable() {
		return canChat;
	}

	/**
	 * Sends a chat to all clients.
	 * 
	 * @param splitOutput
	 *            The message the client sent.
	 * @param sender
	 *            The sender of the message.
	 * @param fromDirected
	 *            Whether this method was called from sendDirectedMessage().
	 */
	// @ requires splitOutput != null;
	// @ requires splitOutput.length > 1;
	// @ requires sender != null;
	public void sendChat(String[] splitOutput, String sender, boolean fromDirected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" " + sender + ":");
		int startIndex = fromDirected ? 0 : 1;
		for (int i = startIndex; i < splitOutput.length; i++) {
			sb.append(" " + splitOutput[i]);
		}
		try {
			out.write(Constants.Protocol.SEND_CHAT + sb.toString() + "\n");
			out.flush();
		} catch (IOException e) {
			quit();
		}
	}

	/**
	 * Sends an error to the client, saying his name is invalid.
	 * 
	 * @param reason
	 */
	// @ requires reason != null;
	public void sendInvalidName(String reason) {
		try {
			out.write(Constants.Protocol.SEND_ERROR_INVALIDNAME + " " + reason + "\n");
			out.flush();
		} catch (IOException e) {
			quit();
		}
	}

	/* @ pure @ */public String getOptions() {
		String res = "";
		res = res + (canChat ? "1" : "0") + (canChallenge ? "1" : "0")
				+ (canLeaderboard ? "1" : "0");
		return res;
	}
}
