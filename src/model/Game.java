package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import constants.Constants;
import exceptions.FalseMoveException;

public class Game extends Observable {

	private Board board;
	private boolean isCopy;
	private Map<Mark, Player> players;
	private Mark current;
	private List<Player> playerList;
	private Random rand = new Random();

	/**
	 * Creates a new game with dimensions Constants.ROWS, Constants.COLUMN.
	 */
	public Game(){
		this(Constants.ROWS, Constants.COLUMNS);
	}

	/**
	 * Creates a new game with dimensions Constants.ROWS, Constants.COLUMN, and a given playerList.
	 * @param argPlayerList The players of this Game.
	 */
	public Game(ArrayList<Player> argPlayerList){
		this(Constants.ROWS, Constants.COLUMNS, argPlayerList);
	}

	/**
	 * Creates a new game with dimensions r, c.
	 * @param r The amount of rows.
	 * @param c The amount of columns.
	 */
	public Game(int r, int c){
		this(r, c, null);
	}

	/**
	 * Creates a new game with dimensions r, c and a given playerList.
	 * @param r The amount of rows.
	 * @param c The amount of columns.
	 * @param argPlayerList The players of this Game.
	 */
	public Game(int r, int c, ArrayList<Player> argPlayerList){
		board = new Board(r, c);
		playerList = argPlayerList;
		players = new HashMap<Mark, Player>();
		current = Mark.RED;
		isCopy = false;
		rand = new Random();
		if(argPlayerList != null){
			reset(argPlayerList);
		}
	}

	/**
	 * Checks if adding a disk to a certain column is valid. Returns false when the disk is:<br>
	 * 1: put into a full column<br>
	 * 2: put into a negative column<br>
	 * 3: put into a column which exceeds the board's total columns
	 * @param c The column the disk is going to be put in
	 * @return b Whether the move is valid.
	 */
	public boolean isIllegalMove(int c) {
		return  c < 0 || c >= Constants.COLUMNS || board.isFullColumn(c);

	}

	/**
	 * Gets the Mark of this Game's current player.
	 * @return
	 */
	public Mark getCurrent(){
		return current;
	}

	/**
	 * Returns the board of this Game.
	 * @return The Board of this Game.
	 */
	public Board getBoard(){
		return board;		
	}

	/**
	 * Returns the winner's Mark of this Game's Board.
	 * @return The winner's Mark.
	 */
	public Mark getWinner(){
		return board.getWinner();
	}

	/**
	 * Returns a Map of <Mark, Player>, which is a list of Marks associated with a player.
	 * @return The <Mark, Player> map.
	 */
	public Map<Mark, Player> getPlayers(){
		return players;
	}

	/**
	 * Returns the list of Players.
	 * @return The list of Players.
	 */
	public List<Player> getPlayerList(){
		return playerList;
	}

	// Commands
	/**
	 * Resets this Game with a PlayerList.
	 * @param argPlayerList
	 */
	public void reset(ArrayList<Player> argPlayerList) {
		System.out.println(argPlayerList.size());
		playerList = new ArrayList<Player>();
		System.out.println(argPlayerList.size());
		playerList.add(argPlayerList.get(0));
		playerList.add(argPlayerList.get(1));
		current = Mark.RED;
		board = new Board(Constants.ROWS, Constants.COLUMNS);

		board.reset();
		players = new HashMap<Mark, Player>();
		players.clear();
		int randomNumber = (int) rand.nextDouble() * 2;
		players.put(Mark.RED, argPlayerList.get(randomNumber));
		players.put(Mark.YELLOW, argPlayerList.get((randomNumber + 1) % 2));
		setChanged();
		notifyObservers();
	}

	/**
	 * Tries to take a turn in a specified column.
	 * @param c The column to take a turn in.
	 * @throws FalseMoveException If a turn is made in a full column, or a column outside of this Board.
	 */
	public void takeTurn(int c) throws FalseMoveException{
		if(isIllegalMove(c)){
			throw new FalseMoveException("Je kunt geen disc in een volle rij gooien. Of je gooit mis. ");
		}
		board.addToCol(c, current);
		current = current.next();

		if(!isCopy && !board.isGameOver()){
			System.out.println(current.name());
			//players.get(current).requestMove(this);
		}			
		setChanged();
		notifyObservers();
	}

	/**
	 * Sets the isCopy of this Game.
	 * @param b
	 */
	public void setIsCopy(boolean b){
		isCopy = b;
	}

	/**
	 * Starts this game.
	 */
	public void start(){
		players.get(current).requestMove(this);
		setChanged();
		notifyObservers();
	}

	/**
	 * Sets the Board of this Game.
	 * @param b The board to be set.
	 */
	public void setBoard(Board b) {
		board = b;
	}

	/**
	 * Returns the Mark of a specified index.
	 * @param i The index where a Mark is pulled from.
	 * @return The Mark of the index.
	 */
	public Mark getField(int i) {
		return board.getField(i);
	}

	/**
	 * Checks if an index is Mark.EMPTY.
	 * @param i The index to be checked.
	 * @return Whether the index is empty.
	 */
	public boolean isEmpty(int i) {
		return board.isEmpty(i);
	}

	/**
	 * Checks if this Game's Board is game over.
	 * @return Whether the Board is game over.
	 */
	public boolean isGameOver() {
		return board.isGameOver();
	}

	/**
	 * Sets the players of this Game, and resets the Game with those players.
	 * @param argPlayers The players to be set.
	 */
	public void setPlayers(ArrayList<Player> argPlayers) {
		reset(argPlayers);
	}

	/**
	 * Returns a Player, given their name.
	 * @param name The name of the player.
	 * @return The Player corresponding to the name (or null if it doesn't exist).
	 */
	public Player getPlayerByName(String name){
		Player res = null;
		for(Player p : getPlayerList()){
			if(p.getName().equals(name)){
				res = p;
			}
		}
		return res;
	}
}
