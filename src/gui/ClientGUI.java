package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.Client;
import model.Game;

public class ClientGUI extends JFrame{
	private static final long serialVersionUID = -4411033752001988794L;

	public ClientGUI() {
		initializeGUI();
		setTitle("Connect Four Client");
		setResizable(true);
		setSize(new Dimension(500, 450));
	}

	private void initializeGUI() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				int close = JOptionPane.showConfirmDialog(ClientGUI.this,
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

	public InetAddress askForIP() {
		InetAddress address = null;
		while (address == null) {
			String inputString = JOptionPane.showInputDialog(this,
					"Enter the server's IP address", "Enter IP address",
					JOptionPane.QUESTION_MESSAGE);
			if(inputString == null) {
				System.exit(0);
			}
			try {
				address = InetAddress.getByName(inputString);
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(this,
						"Please enter a valid IP address",
						"IP address invalid", JOptionPane.ERROR_MESSAGE);
			}
		}
		return address;
	}

	public int askForPortNumber() {
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

	public String askForName() {
		String name = "";
		while (name.equals("")) {
			name = JOptionPane.showInputDialog(this, "Enter a name: ", "Name",
					JOptionPane.QUESTION_MESSAGE);
			if(name == null) {
				System.exit(0);
			}
		}
		return name;
	}
}