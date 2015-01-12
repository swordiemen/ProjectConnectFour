package constants;

import java.awt.Color;


public interface Constants {
	public static final int ROWS = 6;
	public static final int COLUMNS = 7;
	public static final int WIN_DISCS = 4;
	public static final String[] COLOURS = {"Yellow", "Red"};
	public static final Color WHITE = Color.WHITE;
	public static final Color RED = Color.RED;
	public static final Color DARK_RED = new Color(186, 32, 30);
	public static final Color YELLOW = Color.YELLOW;
	public static final Color DARK_YELLOW = new Color(148, 148, 15);
	interface Protocol {		
		//alles wat achter de string zit zijn eventuele argumenten
		//client en server
		public static final String SEND_HELLO = "hello"; //name -> 3 bits, chat - challenge - leaderboards
		
		//client
		public static final String SEND_QUIT = "quit";
		public static final String SEND_PLAY = "play";
		public static final String SEND_MOVE = "move"; //column
		public static final String CHAT = "chat"; //message
		public static final String SEND_CHALLENGE = "challenge"; //name
		public static final String GET_LEADERBOARDS = "getLeaderboard";
		
		
		//server
		public static final String MAKE_MOVE = "makeMove"; //column
		public static final String SEND_GAME_OVER = "gameOver";//<boolean draw> <winnaar if false>
		public static final String SEND_PLAYERS = "sendPlayers";// <naam clientOpties>, <naam2 clientOpties2> -- alleen de lobbymensen
		public static final String SEND_ERROR = "error"; //message (invalid name, move, command, client (voor chat/andere extra opties)
		public static final String MAKE_GAME = "makeGame"; //client1, client2
		public static final String SEND_CHAT = "sendChat"; //name message
		public static final String SEND_CHALLENGED = "challenged"; //name
		public static final String SEND_CHALLENGE_CANCELLED = "challengeCancelled"; //name message(reject, in game, quit)
		public static final String SEND_LEADERBOARDS = "leaderboard"; //player1 score1, player2 score2, ... van hoog aan punten > laag, anders alfabetisch
		
		
	}
	
}
