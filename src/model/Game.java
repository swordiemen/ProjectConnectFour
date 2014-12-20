package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import constants.Constants;
import exceptions.FalseMoveException;

public class Game extends Observable{
	//Instance variables
	
	private Board board;
	private boolean isCopy;
	private Map<Mark, Player> players;
	private Mark current;
	private List<Player> playerList;
	private Random rand;
	
	
	// Constructors
	
	public Game(){
		new Game(Constants.ROWS, Constants.COLUMNS);
	}
	
	public Game(int r, int c){
		new Game(r, c, null);
	}
	
	public Game(int r, int c, ArrayList<Player> argPlayerList){
		board = new Board(r, c);
		playerList = argPlayerList;
		players = new HashMap<Mark, Player>();
		current = Mark.RED;
		isCopy = false;
		rand = new Random();
		reset(argPlayerList);
	}

	// Queries
	
	public Game(int row, int col, Player p1, Player p2) {
		ArrayList<Player> pl = new ArrayList<Player>();
		pl.add(p1);
		pl.add(p2);
		board = new Board(row, col);
		players = new HashMap<Mark, Player>();
		current = Mark.RED;
		isCopy = false;
		rand = new Random();
		reset(pl);
	}

	public boolean isValidMove(int c) {
		return board.deepCopy().addToCol(c, Mark.RED);
	}
	
	public void setIsCopy(boolean b){
		isCopy = b;
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
	
	// Commands
	private void reset(ArrayList<Player> argPlayerList) {
		if(playerList != null){
			playerList.clear();
		}else{
			playerList = new ArrayList<Player>();
		}
		playerList.add(argPlayerList.get(0));
		playerList.add(argPlayerList.get(1));
		current = Mark.RED;
		board.reset();
		players.clear();
		int randomNumber = (int) rand.nextDouble() * 2;
		players.put(Mark.RED, argPlayerList.get(randomNumber));
		players.put(Mark.RED, argPlayerList.get((randomNumber + 1) % 2));
		setChanged();
		notifyObservers();
	}
	
	public void takeTurn(int c) throws FalseMoveException{
		if(!isValidMove(c)){
			throw new FalseMoveException("Je kunt geen disc in een volle rij gooien.");
		}
		board.addToCol(c, current);
		current = current.next();
		if(!isCopy && !board.isGameOver()){
			players.get(current).requestMove(this);
		}		
		setChanged();
		notifyObservers();
	}
	
	public void start(){
		players.get(current).requestMove(this);
		setChanged();
		notifyObservers();
	}

	public void setBoard(Board b) {
		board = b;
	}
	
	
	
	
	
	
}
