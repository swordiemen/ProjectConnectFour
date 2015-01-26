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
	//Instance variables

	private Board board;
	private boolean isCopy;
	private Map<Mark, Player> players;
	private Mark current;
	private List<Player> playerList;
	private Random rand = new Random();

	
	// Constructors

	public Game(){
		this(Constants.ROWS, Constants.COLUMNS);
	}

	public Game(ArrayList<Player> argPlayerList){
		this(Constants.ROWS, Constants.COLUMNS, argPlayerList);
	}
	
	public Game(int r, int c){
		this(r, c, null);
	}

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

	// Queries
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

	public Mark getCurrent(){
		return current;
	}

	public Board getBoard(){
		return board;		
	}

	public Mark getWinner(){
		return board.getWinner();
	}

	public Map<Mark, Player> getPlayers(){
		return players;
	}
	
	public List<Player> getPlayerList(){
		return playerList;
	}

	// Commands
	public void reset(ArrayList<Player> argPlayerList) {
		if(playerList != null){
			playerList.clear();
		}else{
			playerList = new ArrayList<Player>();
		}
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

	public void takeTurn(int c) throws FalseMoveException{
		if(isIllegalMove(c)){
			throw new FalseMoveException("Je kunt geen disc in een volle rij gooien. Of je gooit mis. ha. ");
		}
		System.out.println("Super.takeTurn " + c );
		board.addToCol(c, current);
		current = current.next();
		
		if(!isCopy && !board.isGameOver()){
			System.out.println(current.name());
			//players.get(current).requestMove(this);
		}			
		setChanged();
		notifyObservers();
	}

	public void setIsCopy(boolean b){
		isCopy = b;
	}

	public void start(){
		players.get(current).requestMove(this);
		setChanged();
		notifyObservers();
	}

	public void setBoard(Board b) {
		board = b;
	}

	public Mark getField(int i) {
		return board.getField(i);
	}

	public boolean isEmpty(int i) {
		return board.isEmpty(i);
	}

	public boolean isGameOver() {
		return board.isGameOver();
	}
}
