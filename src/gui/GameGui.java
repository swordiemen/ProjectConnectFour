package gui;

import constants.Constants;
import model.*;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import exceptions.FalseMoveException;
import model.Game;

public class GameGui extends Container implements Observer, ActionListener {
	private static final long serialVersionUID = 1L;
	private final int ROWS = Constants.ROWS;
	private final int COLUMNS = Constants.COLUMNS;
	ArrayList<Player> playerList = new ArrayList<Player>();
	JButton[] fields;
	JButton quitButton;
	JButton restartButton;
	JLabel turnLabel;
	GameController gc;
	Mark ownMark;
	
	//GameController-------------------------------------------------------------------------------------------------------------------------------
	class GameController implements ActionListener {
		private Game game;
		
		/**
		 * Creates a gameController with a specified game.
		 * @param argGame The game the controller has.
		 */
		public GameController(Game argGame){
			game = argGame;
			for(int i = 0; i < ROWS * COLUMNS; i++){
				fields[i].addActionListener(this);
			}
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			for(int i = 0; i < ROWS * COLUMNS; i++){
				if(fields[i].equals(source)){
					try {
						if(game.getBoard().deepCopy().addToCol(getCol(i), game.getCurrent())){ // Superfluous?
							game.takeTurn(getCol(i));
						}
						
					} catch (FalseMoveException e1) {
						// Should never happen, as the client doesn't allow invalid moves.
						e1.printStackTrace();
					}
				}
			}
		}
	
		/**
		 * Returns the column by a given index.
		 * @param i The index.
		 * @return c The column of the index.
		 */
		public int getCol(int i){
			return i % COLUMNS;
		}
		
		/**
		 * Returns the game of this controller.
		 * @return game The game.
		 */
		public Game getGame(){
			return game;
		}
	
	}
	//End game controller -------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Creates a connect-four gui for offline play.
	 * @param g The game the gui is created with.
	 */
	public GameGui(Game g){
		setUp();
		gc = new GameController(g);
	}
	
	/**
	 * Creates a connect-four gui for online play (board is only enabled if it is your turn).
	 * @param g The <code>Game</code> the gui is created with.
	 * @param m The <code>Mark</code> which belongs to the player.
	 */
	public GameGui(Game g, Mark m){
		this(g);
		ownMark = m;
	}
	
	/**
	 * Returns the mark of the player (client).
	 * @return
	 */
	public Mark getOwnMark(){
		return ownMark;
	}
	
	/**
	 * Returns the <code>GameController</code> of this <code>gameGui</code>.
	 * @return gc The GameController.
	 */
	public GameController getGameController(){
		return gc;
	}
	
	/**
	 * Sets up the gui of this <code>GameGui</code>.
	 */
	public void setUp(){
		fields = new JButton[ROWS * COLUMNS];
		turnLabel = new JLabel("It is red's turn.");
		Container board = new Container();
		GridLayout boardGrid = new GridLayout(ROWS, COLUMNS);
		board.setLayout(boardGrid);
		for(int i = 0; i < ROWS * COLUMNS; i++){
			fields[i] = new JButton();
			fields[i].setEnabled(true);
			fields[i].setBackground(Constants.WHITE);
			board.add(fields[i]);
		}
		quitButton = new JButton("Quit (disconnect)");
		restartButton = new JButton("Restart game");
		BoxLayout mainLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(mainLayout);
		this.add(board);
		this.add(turnLabel);		
		this.add(quitButton);
		this.add(restartButton);
		quitButton.addActionListener(this);
		restartButton.addActionListener(this);
		setBounds(20, 20, 933, 800);
	}
	
	@Override
	public void update(Observable o, Object arg0) {
		Game g = (Game) o;
		for(int i = 0; i < ROWS * COLUMNS; i++){
			fields[i].setEnabled(g.isEmpty(i) && !g.isGameOver() && isMyTurn());
			fields[i].setBackground(g.getField(i).toColor());
		}
		if(g.isGameOver()){
			if(g.getWinner() == null){
				turnLabel.setText("Draw!");
			}else{
				turnLabel.setText(g.getWinner().toString() + " has won the game!");
			}
		}else{
			turnLabel.setText("It is " + g.getPlayers().get(g.getCurrent()).getName() + " (" + g.getCurrent() + ") " + "'s turn.");
		}
	}
	
	public static void main(String[] args){
		Game game = new Game();
		GameGui gameGui = new GameGui(game);
		game.addObserver(gameGui);
		gameGui.addPlayer(new HumanPlayer("Henk"));
		gameGui.addPlayer(new HumanPlayer("Je moeder"));
		game.reset(gameGui.getPlayerList());
		game.start();
		JFrame frame = new JFrame();
		frame.add(gameGui);
		frame.setSize(933, 800);
		frame.setVisible(true);
		
	}
	
	/**
	 * Returns the list of players.
	 * @return <code>playerList</code> The list of players.
	 */
	public ArrayList<Player> getPlayerList(){
		return playerList;
	}
	
	public void addPlayer(Player p){
		playerList.add(p);
	}

	public static synchronized void run(){
		//TODO?
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(quitButton.equals(source)){
			//TODO fix socket stuff etc...
			System.exit(0);
		}else if(restartButton.equals(source)){
			gc.getGame().reset(playerList);
		}
	}
	
	public boolean isMyTurn(){
		return getOwnMark() == gc.getGame().getCurrent() || getOwnMark() == null;
	}

}