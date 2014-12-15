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

public class ClientGui extends JFrame implements ActionListener, Observer{
	final int row = Constants.ROWS;
	final int col = Constants.ROWS;
	JPanel p = new JPanel(new GridLayout(row, col));
	Container c;
	JButton[] fields = new JButton[row * col];
	
	
	public ClientGui(){
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
		new ClientGui();
	}

}
