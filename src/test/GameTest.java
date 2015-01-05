package test;
import org.junit.*;

import model.*;
import static org.junit.Assert.*;

public class GameTest {
	Game g;
	
	@Before
	public void SetUp(){
		g = new Game();
	}
	
	@Test
	public void testConstructors(){
		g = new Game(6, 7, null);
		assertEquals("Constructor met alles", 7, g.getBoard().getCol());
		g = new Game(6, 7);
		assertEquals("Constructor met alleen dims", 7, g.getBoard().getCol());
		g = new Game();
		assertEquals("Constructor zonder iets", 7, g.getBoard().getCol());
	}
}
