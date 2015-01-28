package test;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import constants.Constants;
import server.Peer;
import server.Server;
import client.Client;

public class ServerClientCommunicationTest implements Constants, Constants.Protocol {
	static Server testServer;
	static Client c;
	static Thread testServerThread;
	static Thread clientThread;
	static ClientTestUtil ctu = new ClientTestUtil();
	static ClientTestUtil ctu2 = new ClientTestUtil();
	static boolean exceptionThrown = false;

	@BeforeClass
	public static void openConnection() {
		testServer = new Server();
		testServer.setPort(1338);
		testServerThread = new Thread(testServer);
		testServerThread.start();
		exceptionThrown = false;
		try {
			c = new Client(InetAddress.getByName("localhost"), 1338, "client1", ctu);
		} catch (UnknownHostException e) {
			exceptionThrown = true;
			e.printStackTrace();
		}
		clientThread = new Thread(c);
		clientThread.start();
	}

	@Test
	public void testOpenConnection() {
		assertEquals("Checking if an exception was thrown while initializing", false,
				  exceptionThrown);
	}

	@Test
	public synchronized void testGetHello() {
		c.logIn();
		try {
			wait(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("logIn(), server should return a hello as response", "hello 110 client1",
				  ctu.getLastInput());
		ArrayList<Peer> peers = testServer.getPeerList();
		boolean containsMe = false;
		Peer me = null;
		for (Peer p : peers) {
			if (p.getName().equals("client1")) {
				containsMe = true;
				me = p;
				break;
			}
		}
		assertTrue("server.getPeers().contains(this client)", containsMe);
		assertEquals("State of client should be Lobby", STATE_LOBBY, me.getState());
	}

	@Test
	public synchronized void testInvalidName() {
		Client c2 = null;
		try {
			c2 = new Client(InetAddress.getLocalHost(), 1338, "client1", ctu2);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread c2t = new Thread(c2);
		c2t.start();
		try {
			this.wait(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("Test if the server gives us an error.",
				  ctu2.getLastInput().equals("error invaliddName Je naam bestaat al."));
		c2.quit();
	}

	@Test
	public synchronized void testChallenge() {
		Client c2 = null;
		try {
			c2 = new Client(InetAddress.getLocalHost(), 1338, "client2", ctu2);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread c2t = new Thread(c2);
		c2t.start();
		try {
			this.wait(600);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c2.sendChallenge("client1");
		try {
			this.wait(600);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.challengeAccepted("client2");
		try {
			this.wait(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Peer> peers = testServer.getPeerList();
		Peer cPeer = null;
		for (Peer p : peers) {
			if (p.getName().equals("client1")) {
				cPeer = p;
				break;
			}
		}
		assertEquals("client1 should be inGame after accepting.", STATE_INGAME, cPeer.getState());
	}

	// @Test
	public synchronized void testQuit() {
		c.quit();
		try {
			this.wait(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ArrayList<Peer> peers = testServer.getPeerList();
		boolean containsMe = false;
		String peersYo = "";
		for (Peer p : peers) {
			peersYo = " " + p.getName();
		}
		System.out.println(peersYo);
		for (Peer p : peers) {
			if (p.getName().equals("client1")) {
				containsMe = true;
				break;
			}
		}
		assertFalse("Test if the server still has us after we've quit.", containsMe);
	}
}
