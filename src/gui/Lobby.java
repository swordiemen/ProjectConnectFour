package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.crypto.spec.GCMParameterSpec;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;
import javax.swing.JList;

import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.ListModel;

import client.Client;
import model.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Lobby extends JFrame implements ActionListener, Runnable{

	private JPanel contentPane;
	private JTextField chatField;
	private Client client;
	JList<String> playerListList;
	JButton challengeButton;
	JTextPane chatboxPane;
	JScrollPane chatboxScrollPane;
	JScrollPane playerListListScrollPane;
	JButton btnSend;
	JComboBox<String> whisperBox;
	JButton playButton;
	JButton showLeaderBoardButton;
	JButton quitButton;
	DefaultListModel<String> listModel;
	List<String> playerList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client c = new Client(InetAddress.getByName("localhost"), 1338, "Sjon");
					//Client c = Client.createClient(new ClientGUI());
					Lobby frame = new Lobby(c);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void run(){
		Lobby frame = new Lobby(client);
		frame.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public Lobby(Client argClient) {
		client = argClient;
		setUp();
	}

	public void setUp(){
		playerList = new ArrayList<String>();
		for(int i = 0; i < 2100; i++){
			playerList.add(String.valueOf(i));
		}

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

		btnSend = new JButton("Send");
		btnSend.addActionListener(this);
		btnSend.setBounds(386, 436, 89, 23);
		contentPane.add(btnSend);

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
	
	public void challenged(String name){
		int accepted = JOptionPane.showOptionDialog(this, "Accept the challenge from " + name + "?",
				"", JOptionPane.OK_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] {
				"Yes", "No" }, 2);
		if(accepted == JOptionPane.OK_OPTION){
			client.challengeAccepted(name);
		}else{
			client.chalengeRefused(name);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(showLeaderBoardButton.equals(source)){
			challenged("je moeder");
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
				//client.sendChallenge(playerListList.getSelectedValue();
			}else{
				chatboxPane.setText(chatboxPane.getText() + "[Server] You must select a player to challenge.\n");
			}
		}else if(btnSend.equals(source)){
			if(whisperBox.getSelectedIndex() == 0){
				chatboxPane.setText(chatboxPane.getText() + "[Chat] Je hebt niks.\n");
				byte b = (byte) 0x101;
				System.out.println("1e: " + b % 2 + " 2e: " + (b >> 1 % 2) + " 3e: " + (b >> 2 % 2));
				//client.sendTell(chatField.getText());
			}else{
				chatboxPane.setText(chatboxPane.getText() + "[Chat] Je target " + whisperBox.getSelectedItem() + ".\n");
				//client.sendWhisper(chatField.getText(), whisperBox.getSelectedItem());
			}
			//client.sendMsg();
		}
	}
	
	public void setPlayerList(List<String> argPlayerList){
		playerList = argPlayerList;
	}
}
