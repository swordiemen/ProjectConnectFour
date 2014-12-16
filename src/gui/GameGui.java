package gui;

import constants.Constants;
import model.*;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import exceptions.FalseMoveException;
import model.Game;

public class GameGui extends JFrame implements ActionListener, Observer{
	private static final long serialVersionUID = 1L;
	private int row = Constants.ROWS;
	private int col = Constants.COLUMNS;
	JPanel p = new JPanel(new GridLayout(row, col));
	Player p1 = new Player("Henk");
	Player p2 = new Player("Gozert");
	Container c;
	ArrayList<Player> playerList = new ArrayList<Player>();
	JButton[] fields = new JButton[row * col];
	GameController gc = new GameController(new Game(row, col, playerList));
	//GameController-------------------------------------------------------------------------------------------------------------------------------
	class GameController implements ActionListener {
		private Game game;
		
		public GameController(Game g){
			playerList.add(new Player("Henk"));
			playerList.add(new Player("Gozert"));
			row = game.getBoard().getRow();
			col = game.getBoard().getCol();
			
			game = g;
			for(int i = 0; i < row * col; i++){
				fields[i].addActionListener(this);
			}
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			for(int i = 0; i < row * col; i++){
				System.out.println("Roflpantoffel");
				if(fields[i].equals(source)){
					try {
						if(game.getBoard().deepCopy().addToCol(getCol(i), game.getCurrent())){
							game.takeTurn(getCol(i));
							fields[game.getBoard().getNextEntryInColumn(getCol(i))].setBackground(Constants.RED);
						}
						
					} catch (FalseMoveException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	
		public int getCol(int i){
			return i % 7;
		}
	
	}
	//End game controller -------------------------------------------------------------------------------------------------------------------------------
	
	
	
	public GameGui(){
		super("Connect Four client");
		setBounds(100, 50, 800, 800);
		c = this.getContentPane();
		c.add(p);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for(int i = 0; i < row * col; i++){
			JButton cur = fields[i];
			cur = new JButton();
			cur.setEnabled(true);
			cur.setBackground(Constants.WHITE);
			fields[i] = cur;
			p.add(fields[i]);
		}
		setVisible(true);
		
		
		
	}
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
	}
	
	public static void main(String[] args){
		new GameGui();
	}

	public static synchronized void run(){
		new GameGui();
	}

}
