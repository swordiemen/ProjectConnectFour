package gui;

import constants.Constants;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import exceptions.FalseMoveException;
import model.Game;

public class GameGui extends JFrame implements ActionListener, Observer{
	private static final long serialVersionUID = 1L;
	private int row;
	private int col;
	
	class GameController implements ActionListener {
		private Game game;
		
		public GameController(Game g){
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
				if(fields[i].equals(source)){
					try {
						if(game.getBoard().deepCopy().addToCol(getCol(i), game.getCurrent())){
							game.takeTurn(getCol(i));
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	JPanel p = new JPanel(new GridLayout(row, col));
	Container c;
	JButton[] fields = new JButton[row * col];
	
	
	public GameGui(){
		super("Connect Four client");
		setBounds(100, 50, 800, 800);
		c = this.getContentPane();
		c.add(p);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for(int i = 0; i < row * col; i++){
			JButton cur = fields[i];
			cur = new JButton();
			cur.addActionListener(this);
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
		for(int i = 0; i < row * col; i++){
			if(source == fields[i]){
				
				fields[i].setBackground(Constants.RED);
			}
		}
	}
	
	public static void main(String[] args){
		new ClientGui();
	}

	public static synchronized void run(){
		new GameGui();
	}

}
