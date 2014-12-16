package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

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
		new Game(6, 7);
	}
	
	public Game(int r, int c){
		new Game(r, c, null);
	}
	
	public Game(int r, int c, ArrayList<Player> argPlayerList){
		board = new Board(r, c);
		playerList = argPlayerList;
		current = Mark.RED;
		isCopy = false;
		rand = new Random();
		reset(argPlayerList);
	}

	// Queries
	
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
	
	
	
	
	
	
}
