package gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ServerGUI extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9134002518347860558L;
	private JTextField textField;
	private JButton Quit;
	private JButton[] playerList;	
	private int port;
	
	/**
	 * Creates a new ServerGUI.
	 */
	public ServerGUI(){
		initializeGUI();
		setTitle("Server Port");
		setResizable(true);
		setSize(new Dimension(500, 450));
	}

	/**
	 * Initializes the GUI.
	 */
	private void initializeGUI() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				int close = JOptionPane.showConfirmDialog(ServerGUI.this,
						"Close the application?", "Close?",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (close == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
				return;
			}
		});
		
	}
	
	/**
	 * Displays a window where the Server should listen to.
	 * @return
	 */
	public int askForServerPort(){
		int port = -1;
		while (port == -1) {
			String inputString = JOptionPane.showInputDialog(this,
					"Enter the server's port number: ",
					"Port number", JOptionPane.QUESTION_MESSAGE);
			if(inputString == null) {
				System.exit(0);
			}
			try {
				port = Integer.parseInt(inputString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this,
						"Please enter a valid number", "Number Invalid",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return port;
	}
	public void lobbyServer(){
		
	}

}
