package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import client.Client;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Lobby extends JFrame implements ActionListener, Runnable{

	private static final long serialVersionUID = 6766393428618386437L;
	private JPanel contentPane;
	private JTextField chatField;
	private Client client;
	JList<String> playerListList;
	JButton challengeButton;
	JTextPane chatboxPane;
	JScrollPane chatboxScrollPane;
	JScrollPane playerListListScrollPane;
	JButton sendButton;
	JComboBox<String> whisperBox;
	JButton playButton;
	JButton showLeaderBoardButton;
	JButton quitButton;
	DefaultListModel<String> listModel;
	List<String> playerList;
	private String msg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Client c = new Client(InetAddress.getByName("localhost"), 1338, "Sjon");
					Client c = Client.createClient(new ClientGUI());
					Lobby frame = new Lobby(c);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Starts a new <code>Lobby</code>.
	 */
	public void run(){
		chatboxPane.setText(chatboxPane.getText() + msg + "\n");
		listModel.clear();
		for(String name:playerList){
			listModel.addElement(name);
		}
	}

	/**
	 * Create the frame.
	 */
	public Lobby(Client argClient) {
		client = argClient;
		setUp();
		setVisible(true);
	}


	/**
	 * Sets up the lobby GUI.
	 */
	public void setUp(){
		playerList = new ArrayList<String>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 570);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		listModel = new DefaultListModel<String>();
		if(playerList.size() > 0){
			for(String name : playerList){
				listModel.addElement(name);
			}
		}else{
			listModel.addElement("The server has not sent \n a list yet");
		}

		playerListList = new JList<String>(listModel);
		playerListList.setBounds(5, 5, 222, 386);
		playerListListScrollPane = new JScrollPane(playerListList);
		playerListListScrollPane.setBounds(5, 5, 222, 386);
		contentPane.add(playerListListScrollPane);
		//contentPane.add(playerListList);


		challengeButton = new JButton("Challenge!");
		challengeButton.setBounds(5, 402, 222, 23);
		challengeButton.addActionListener(this);
		contentPane.add(challengeButton);

		chatboxPane = new JTextPane();
		chatboxPane.setEditable(false);
		chatboxPane.setBounds(238, 5, 236, 386);
		chatboxPane.setEnabled(true);
		chatboxScrollPane = new JScrollPane(chatboxPane);
		chatboxScrollPane.setBounds(238, 5, 236, 386);
		contentPane.add(chatboxScrollPane);

		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		sendButton.setBounds(386, 436, 89, 23);
		contentPane.add(sendButton);

		chatField = new JTextField();
		chatField.setBounds(237, 402, 237, 20);
		contentPane.add(chatField);
		chatField.setColumns(10);

		whisperBox = new JComboBox<String>();
		whisperBox.setBounds(238, 437, 138, 20);
		whisperBox.addItem("Everyone");
		for(String name : playerList){
			whisperBox.addItem(name);
		}
		contentPane.add(whisperBox);

		playButton = new JButton("Seek random player");
		playButton.addActionListener(this);
		playButton.setBounds(5, 436, 222, 23);
		contentPane.add(playButton);

		showLeaderBoardButton = new JButton("Leaderboards");
		showLeaderBoardButton.setBounds(5, 470, 469, 23);
		showLeaderBoardButton.addActionListener(this);
		contentPane.add(showLeaderBoardButton);

		quitButton = new JButton("Disconnect");
		quitButton.setBounds(5, 500, 469, 23);
		quitButton.addActionListener(this);
		contentPane.add(quitButton);
		chatboxPane.setText("Welkom " + client.getName() + ".\n");
	}

	/**
	 * This method is called when the client has received a challenge. This will prompt
	 * a <code>JOptionPane</code>, where the client can choose to accept or decline the challenge.
	 * If the client accepts, it will send a <code>challangeAccept</code> with its name, otherwise it will send
	 * a <code>challengeRefused</code> with its name.
	 * @param name The name of the client.
	 */
	public void challenged(String name){
		String[] options = new String[]{"Accept", "Decline"};
		int accepted = JOptionPane.showOptionDialog(this, "Accept the challenge from " + name + "?",
				"", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		if(accepted == JOptionPane.OK_OPTION){
			client.challengeAccepted(name);
		}else{
			client.challengeRefused(name);
		}
	}

	/**
	 * Displays an error.
	 * @param errorMsg The error message.
	 */
	public void displayError(String errorMsg){
		String[] options = new String[]{"OK"};
		JOptionPane.showOptionDialog(this, errorMsg, "Error occurred.", JOptionPane.DEFAULT_OPTION, 
				JOptionPane.ERROR_MESSAGE, null, options, options[0]);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(showLeaderBoardButton.equals(source)){
			displayError("Leaderboards zijn nog niet gemaakt.");
			//TODO leaderboards.
		}else if(playButton.equals(source)){
			chatboxPane.setText(chatboxPane.getText() + "[Server] Searching for someone for you to play with...\n");
			client.makeGame();
		}else if(quitButton.equals(source)){
			client.quit();
			System.exit(0);
		}else if(challengeButton.equals(source)){
			if(playerListList.getSelectedValue() != null){
				chatboxPane.setText(chatboxPane.getText() + "[Server] You challenged " + playerListList.getSelectedValue() + ".\n");
				client.sendChallenge(playerListList.getSelectedValue());
			}else{
				chatboxPane.setText(chatboxPane.getText() + "[Server] You must select a player to challenge.\n");
			}
		}else if(sendButton.equals(source)){
//			if(whisperBox.getSelectedIndex() == 0){
////				byte b = (byte) 0x101;
////				System.out.println("1e: " + b % 2 + " 2e: " + (b >> 1 % 2) + " 3e: " + (b >> 2 % 2));
//				//client.sendTell(chatField.getText());
//			}else{
//				
//				//chatboxPane.setText(chatboxPane.getText() + "[Chat] Je target " + whisperBox.getSelectedItem() + ".\n");
//				//client.sendWhisper(chatField.getText(), whisperBox.getSelectedItem());
//			}
			client.sendChat(chatField.getText());
			//client.sendMsg();
		}
	}

	/**
	 * When a <code>Client</code> receives an updated player list from the <code>Server</code>, this method is called. 
	 * Updates the <code>playerList</code> of this <code>Lobby</code>.
	 * @param argPlayerList The updated <code>playerList</code>
	 */
	public void setPlayerList(List<String> argPlayerList){
		System.out.println("setPlayerList()");
		playerList = argPlayerList;
		SwingUtilities.invokeLater(this);
	}

	/**
	 * This method is called when the <code>Client</code> receives a chat message from the <code>Server</code>.
	 * It will then be displayed in this <code>Lobby1</code>'s <code>chatboxPane</code>.
	 * @param sender The sender of the chat message.
	 * @param msg The chat message of the sender.
	 */
	public void receivedChat(String sender, String[] msgArray){
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i < msgArray.length; i++){
			sb.append(" " + msgArray[i]);
		}
		msg = sb.toString();
		SwingUtilities.invokeLater(this);
	}
}
