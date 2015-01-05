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
	Player p1 = new Player("Henk");
	Player p2 = new Player("Gozert");
	ArrayList<Player> playerList = new ArrayList<Player>();
	JButton[] fields;
	JButton quitButton;
	JButton restartButton;
	JLabel turnLabel;
	GameController gc;
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
						if(game.getBoard().deepCopy().addToCol(getCol(i), game.getCurrent())){
							game.takeTurn(getCol(i));
							//fields[game.getBoard().getNextEntryInColumn(getCol(i))+Constants.COLUMNS].setBackground(game.getCurrent().toColor());
						}
						
					} catch (FalseMoveException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	
		public int getCol(int i){
			return i % COLUMNS;
		}
		
		public Game getGame(){
			return game;
		}
	
	}
	//End game controller -------------------------------------------------------------------------------------------------------------------------------
	
	
	
	public GameGui(Game g){
		setUp();
//		setBounds(100, 50, 933, 800);
//		this.add(p);
//		for(int i = 0; i < row * col; i++){
//			JButton cur = fields[i];
//			cur = new JButton();
//			cur.setEnabled(true);
//			cur.setBackground(Constants.WHITE);
//			fields[i] = cur;
//			p.add(fields[i]);
//		}
//		
//		setVisible(true);
		gc = new GameController(g);
	}
	
	public GameController getGameController(){
		return gc;
	}
	
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
	public void update(Observable arg0, Object arg1) {
		Game g = (Game) arg0;
		for(int i = 0; i < ROWS * COLUMNS; i++){
			fields[i].setEnabled(g.isEmpty(i) && !g.isGameOver());
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
	
	public ArrayList<Player> getPlayerList(){
		return playerList;
	}
	
	public void addPlayer(Player p){
		playerList.add(p);
	}

	public static synchronized void run(){
		//TODO
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

}
