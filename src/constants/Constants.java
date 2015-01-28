package constants;

import java.awt.Color;

public interface Constants {
	public static final int ROWS = 6;
	public static final int COLUMNS = 7;
	public static final int WIN_DISCS = 4;
	public static final String[] COLOURS = { "Yellow", "Red" };
	public static final Color WHITE = Color.WHITE;
	public static final Color RED = Color.RED;
	public static final Color DARK_RED = new Color(186, 32, 30);
	public static final Color YELLOW = Color.YELLOW;
	public static final Color DARK_YELLOW = new Color(148, 148, 15);
	public static final String STATE_START = "START";
	public static final String STATE_LOBBY = "LOBBY";
	public static final String STATE_INGAME = "INGAME";
	public static final int NUMBER_OF_OPTIONS = 3;
	public static final String CLIENT_OPTIONS = "110";

	interface Protocol {
		/*
		 * --------------------------------------------------------- | SHARED
		 * COMMANDS | ---------------------------------------------------------
		 */

		// NOTE ABOUT ALL ERRORS IN DOCS: should be sent by 
		// the opposing party
		// if the command was wrongly sent.
		// ALL ERRORS WILL TERMINATE THE CONNECTION BETWEEN CLIENT/SERVER

		/**
		 * A message sent by a user when joining the server and a message sent
		 * by the server, after receiving hello by a client. <br>
		 * Usage client:<br>
		 * hello <code>code</code> <code>nickname</code>.<br>
		 * usage server:<br>
		 * hello <code>code</code>
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>code</b> the <code>clientcode</code> in 3 bits.
		 * <code>first bit</code> for <code>chat</code>, <code>second bit</code>
		 * for <code>challenge</code>, <code>third bit</code> for
		 * <code>leaderboard</code><br>
		 * <b>name</b> the (unique) <code>nickname</code> of the client.<br>
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDNAME <b>error invalidName</b>} if the
		 * <code>name</code> is already in use by another client.<br>
		 * client: {@link #SEND_ERROR_INVALIDCOMMAND <b>error
		 * invalidCommand</b>} if
		 * <code>Arguments.size != 2 </code>|| <code>code.size != 3 </code>|| !code.matches("[01]+")
		 * server: {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if 
		 * <code>Arguments.size != 1 || <code>code.size != 3</code> || !code.matches("[01]+")</code>
		 * </ul>
		 */
		public static final String SEND_HELLO = "hello";

		/*
		 * --------------------------------------------------------- | CLIENT
		 * ONLY COMMANDS |
		 * ---------------------------------------------------------
		 */

		/**
		 * A message sent by a user when leaving. <br>
		 * used to notify the server the user has left so sockets can be closed. <Br>
		 * usage: quit
		 * <ul>
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code>arguments.size != 0</code>
		 * </ul>
		 */
		public static final String SEND_QUIT = "quit";

		/**.
		 * A message sent by a client to indicate he wants to play a game
		 * <ul>
		 * </ul>
		 * <br>
		 * usage: play <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code>arguments.size != 0</code>
		 * </ul>
		 *
		 * @see #MAKE_GAME
		 */
		public static final String SEND_PLAY = "play";

		/**
		 * A message sent by a client to indicate he wants to make a move. <br>
		 * This message can only be sent if the client is in a game and has the
		 * current turn.<br>
		 * usage: move <code>Num</code>
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>Num</b> the <code>field</code> to make a move. number in the range
		 * 0 to 6.
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code> arguments.size != 1 || !Integer.isInteger(Num)</code><br>
		 * {@link #SEND_ERROR_INVALIDMOVE <b>error invalidMove</b>} if
		 * <code>client.game == null || Num < 0 || num > 6</code>, or if client
		 * does not have the turn.
		 * </ul>
		 *
		 * @see #MAKE_MOVE
		 */
		public static final String SEND_MOVE = "move";

		/**
		 * a message sent by the user to send a <code>message</code> to the
		 * current <i>channel</i>. <br>
		 * a channel is either a gameroom or the lobby. <br>
		 * usage: chat <code>message</code>
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>message</b> the <code>message</code> to broadcast. can not be
		 * empty.
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if the
		 * <code>message</code> is null or empty.<br>
		 * {@link #ERROR_INVALIDCLIENT <b>error invalidClient</b>} if the
		 * <code>sender</code> or <code>server</code> is does not support
		 * {@link #CHAT} as provided in {@link #SEND_HELLO}.
		 * </ul>
		 *
		 * @see #SEND_CHAT
		 */
		public static final String CHAT = "chat";

		/**
		 * 
		 * a message sent by a client when challenging another <code>user</code>
		 * . <br>
		 * usage: challenge <code>user</code>
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>user</b> the <code>user</code> to send the <code>message</code>
		 * (id). can not be empty<br>
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCLIENT <b>error invalidClient</b>} if
		 * <code>user</code> is not logged on or the <code>user</code>,
		 * <code>sender</code> or <code>server</code> does not support
		 * {@link #SEND_CHALLENGE} as provided in {@link #SEND_HELLO}<br>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * invalid arguments are specified.
		 * </ul>
		 *
		 * @see #SEND_CHALLENGED
		 * @see #SEND_CHALLENGE_CANCELLED
		 */
		public static final String SEND_CHALLENGE = "challenge";

		/*
		 * ---------------------------------------------------- | SERVER ONLY
		 * COMMANDS | ----------------------------------------------------
		 */

		/**
		 * A message sent by the server to indicate 2 clients a game has been
		 * created. <br>
		 * This message will be sent to both clients in the game.<br>
		 * 
		 * usage: makeGame <code>player1 player2</code>
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>player1</b> the first <code>player</code><br>
		 * <b>player2</b> the second <code>player</code>
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code> arguments.size != 2 </code><br>
		 * {@link #SEND_ERROR_INVALIDCLIENT <b>error invalidClient</b>} if
		 * <code>!player1.isOn || !player2.isOn || player1.inGame || player2.inGame</code>
		 * </ul>
		 */
		public static final String MAKE_GAME = "makeGame";

		/**
		 * A message sent by the server to indicate a valid move has been
		 * played. <br>
		 * This message will only be sent after a valid move receive from a
		 * client, to all client in that game.<br>
		 * 
		 * usage: makeMove <code>Num</code>
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>Num</b> the <code>field</code> to make a move. number in the range
		 * 0 to 6.
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code> arguments.size != 1 || !Integer.isInteger(Num)</code><br>
		 * {@link #SEND_ERROR_INVALIDMOVE <b>error invalidMove</b>} if
		 * <code>client.game == null || Num < 0 || num > 6</code>, or if client
		 * does not have the turn.
		 * </ul>
		 */
		public static final String MAKE_MOVE = "makeMove";

		/**
		 * A message sent by the server to indicate a game is over. <br>
		 * This message will be sent to all people in that game.<br>
		 * usage: gameOver <code>isDraw Winner</code>
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>isDraw</b> a boolean. true if draw, false otherwise.<br>
		 * <b>Winner</b> The name of the winner of the game.
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code> arguments.size != 1 || !Boolean.isBoolean(isDraw)</code><br>
		 * {@link #SEND_ERROR_INVALIDMOVE <b>error invalidMove</b>} if
		 * <code>client.game == null || Num < 0 || num > 6</code>, or if client
		 * does not have the turn. {@link #SEND_ERROR_INVALIDNAME <b>error
		 * invalidName</b>} if
		 * <code>!player1.equals(Winner) && !player2.equals(Winner) </code>
		 * </ul>
		 */
		public static final String SEND_GAME_OVER = "gameOver";

		/**
		 * A message sent by the server containing all current clients and their
		 * clientnumber (in 3 bits) <br>
		 * This message will be sent after a change to the clientlist, to all
		 * users in the <b>lobby</b>.<br>
		 * usage: sendPlayers <code>player1 code</code>,
		 * <code> player2 code</code>,<code> player3 code</code> <i>[...]</i><br>
		 * extra: spaces between code and player, commas between every pair.
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>Player</b> the <code>name</code> of the player. <br>
		 * <b>code</b> the 3 bit <code>code</code> what client supports as
		 * described in {@link #SEND_HELLO}
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if the
		 * given syntax does not follow the usage.<br>
		 * </ul>
		 */
		public static final String SEND_PLAYERS = "sendPlayers";

		/**
		 * A message sent by the server containing all clients with their
		 * scores. <br>
		 * This message is ordered by score. if score is the same, ordered
		 * Alphabetically.<br>
		 * A win is 2 points, a draw is 1 point, a lose is 0 points. <br>
		 * usage: sendLeaderboard <code>player1 score</code>,
		 * <code> player2 score</code>,<code> player3 score</code> <i>[...]</i><br>
		 * extra: spaces between code and player, commas between every pair.
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>Player</b> the <code>name</code> of the player. <br>
		 * <b>score</b> the <code>score</code> of a player.
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if the
		 * given syntax does not follow the usage.<br>
		 * {@link #SEND_ERROR_INVALIDCLIENT <b>error invalidClient</b>} if
		 * <code>sender</code> is ingame or the <code>sender/server</code> does
		 * not support {@link #SEND_LEADERBOARD} as provided in
		 * {@link #SEND_HELLO}<br>
		 * </ul>
		 */
		public static final String SEND_LEADERBOARD = "sendLeaderboard";

		/**
		 * A command sent by the server to give clients a chatmessage.<br>
		 * Every chatmessage is sent to all clients in the corresponding game /
		 * lobby, IF they support chat.<br>
		 * syntax: sendChat <code>nick message</code>
		 * <ul>
		 * </ul>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>nick</b> The nickname of the client.<br>
		 * <b>message</b> The message that has been said.
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code> argument.size < 2</code> {@link #SEND_ERROR_INVALIDCLIENT
		 * <b>error invalidClient</b>} if sent to a client that does not support
		 * chat.
		 * </ul>
		 */
		public static final String SEND_CHAT = "sendChat";

		/**
		 * a message sent by the server to indicate a client, another client has
		 * challenged him. <br>
		 * <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>user</b> the <code>user</code> that started the challenge.<br>
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCLIENT <b>error invalidClient</b>} if
		 * <code>user/sender</code> is not logged on/ingame or the
		 * <code>user/sender/server</code> does not support
		 * {@link #SEND_CHALLENGE} as provided in {@link #SEND_HELLO}<br>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code>arguments.size != 1</code><br>
		 * </ul>
		 *
		 * @see #SEND_CHALLENGE_CANCELLED
		 */
		public static final String SEND_CHALLENGED = "challenged";

		/**
		 * a message sent by the server to a client who has challenged, to
		 * indicate the other did not accept. <br>
		 * usage: challengeCancelled <code>reason</code> <br>
		 * <b> parameters:</b>
		 * <ul>
		 * <b>reason</b> the reason why a user did not accept. can only be one
		 * of the following: reject, ingame, quit<br>
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCLIENT <b>error invalidClient</b>} if
		 * <code>user/sender</code> is not logged on/ingame or the
		 * <code>user/sender/server</code> does not support
		 * {@link #SEND_CHALLENGE} as provided in {@link #SEND_HELLO}<br>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code>arguments.size != 1 </code>|| !(reason.equals("reject") || 
		 * reason.equals("ingame") || reason.equals("quit"))
		 * </ul>
		 */
		public static final String SEND_CHALLENGE_CANCELLED = "challengeCancelled";

		/**
		 * A message sent by a user when accepting a challenge. <br>
		 * As this does not specify which user he accepts, the rule has to be
		 * made by the server there can only be 1 challenge to a client. <Br>
		 * usage: acceptChallenge
		 * <ul>
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code>arguments.size != 0 || !client.isChallenged()</code>
		 * </ul>
		 */
		public static final String ACCEPT_CHALLENGE = "acceptChallenge";

		/**
		 * A message sent by a user when rejecting a challenge. <br>
		 * As this does not specify which user he rejects, the rule has to be
		 * made by the server there can only be 1 challenge to a client. <Br>
		 * usage: rejectChallenge
		 * <ul>
		 * </ul>
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCOMMAND <b>error invalidCommand</b>} if
		 * <code>arguments.size != 0 || !client.isChallenged()</code>
		 * </ul>
		 */
		public static final String REJECT_CHALLENGE = "rejectChallenge";

		/**.
		 * A message sent by a client to request the leaderboard<br>
		 * usage: getLeaderboard
		 * <ul>
		 * </ul>
		 * 
		 * <b>Errors:</b>
		 * <ul>
		 * {@link #SEND_ERROR_INVALIDCLIENT <b>error invalidClient</b>} if
		 * <code>sender</code> is ingame or the <code>sender/server</code> does
		 * not support {@link #SEND_LEADERBOARD} as provided in
		 * {@link #SEND_HELLO}<br>
		 * </ul>
		 */
		public static final String GET_LEADERBOARD = "getLeaderboard";

		/*
		 * --------------------------------------------------------- | ERROR
		 * MESSAGES THAT CAN BE SENT/RECEIVED |
		 * ---------------------------------------------------------
		 */

		/**
		 * Error message given if an invalid name has been sent by a command.
		 */
		public static final String SEND_ERROR_INVALIDNAME = "error invaliddName";

		/**
		 * error message given when a command is unknown or there has been wrong
		 * usage of a known command.<br>
		 */
		public static final String SEND_ERROR_INVALIDCOMMAND = "error invalidCommand";

		/**
		 * error message used when a command is used that is not supported by
		 * client/server<br>
		 */
		public static final String SEND_ERROR_INVALIDCLIENT = "error invalidClient";

		/**
		 * Error message given when a move is invalid. <br>
		 */
		public static final String SEND_ERROR_INVALIDMOVE = "error invalidMove";

	}

}
