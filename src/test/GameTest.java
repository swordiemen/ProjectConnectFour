package test;

import java.util.ArrayList;

import org.junit.*;

import constants.Constants;
import exceptions.FalseMoveException;
import model.*;
import static org.junit.Assert.*;

public class GameTest {
	Game g;
	Player p1;
	Player p2;
	ArrayList<Player> players;

	@Before
	public void SetUp() {
		p1 = new Player("Player1");
		p2 = new Player("Player2");
		players = new ArrayList<Player>();
		players.add(p1);
		players.add(p2);
		g = new Game(players);
	}

	@Test
	public void testGetBoard() {
		assertEquals("Testing standard constructor", 7, g.getBoard().getCol());
	}

	@Test
	public void testTakeTurn() {
		try {
			for (int i = 0; i < Constants.ROWS; i++) {
				g.takeTurn(1);
			}
		} catch (FalseMoveException e) {
			e.printStackTrace();
		}
		assertEquals("Checking if our last disc is yellow after takeTurn().", Mark.YELLOW, g
				  .getBoard().getField(1));
	}

	@Test
	public void testReset() {
		try {
			for (int i = 0; i < Constants.ROWS; i++) {
				g.takeTurn(1);
			}
		} catch (FalseMoveException e) {
			e.printStackTrace();
		}
		g.reset(players);
		assertEquals("reset(players)", Mark.EMPTY, g.getBoard().getField(1));
		assertEquals("Checking if reset reset the current mark", Mark.RED, g.getCurrent());
	}

	@Test
	public void testSimulateMultipleGames() {
		try {
			for (int i = 0; i < 4; i++) {
				g.takeTurn(i);
				g.takeTurn(i);
			}
		} catch (FalseMoveException e) {
			e.printStackTrace();
		}
		assertEquals("Testing first isGameOver()", true, g.isGameOver());
		g.reset(players);
		assertEquals("Testing second isGameOver()", false, g.isGameOver());
		try {
			for (int i = 0; i < 4; i++) {
				g.takeTurn(i);
				g.takeTurn(i);
			}
		} catch (FalseMoveException e) {
			e.printStackTrace();
		}
		assertEquals("Testing third isGameOver()", true, g.isGameOver());
	}
}
