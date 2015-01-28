package test;

import static org.junit.Assert.*;

import org.junit.*;

import model.*;

public class BoardTest {

	Board b;

	@Before
	public void setUp() {
		b = new Board(6, 7);
		b.reset();
	}

	@Test
	public void testGetSetField() {
		b.setField(10, Mark.RED);
		assertEquals("Test getField(10)", Mark.RED, b.getField(10));
	}

	@Test
	public void testReset() {
		b.reset();
		//16 is een random waarde
		assertEquals("Test reset", Mark.EMPTY, b.getField(16));
	}

	@Test
	public void testRowColFieldGettersSetters() {
		b.setField(3, 3, Mark.RED);
		assertEquals("Test setField met row/col", Mark.RED, b.getField(24));
		b.setField(24, Mark.YELLOW);
		assertEquals("Test getField met row/col", Mark.YELLOW, b.getField(3, 3));
	}

	@Test
	public void testIsEmpty() {
		b.setField(3, 4, Mark.RED);
		assertEquals("Testing isEmpty()", false, b.isEmpty(3, 4));
	}

	@Test
	public void testGetNextEntryInColumn() {
		b.setField(3, 3, Mark.RED);
		b.setField(4, 3, Mark.RED);
		b.setField(5, 3, Mark.RED);
		assertEquals("Testing next entry in column", 17, b.getNextEntryInColumn(3));
	}

	@Test
	public void testAddToCol() {
		for (int i = 0; i < 9; i++) {
			b.addToCol(3, Mark.RED);
		}
		assertEquals("Testing adding a disc in row 3", false, b.isEmpty(24));
		assertEquals("Testing adding a disc in row 3", false, b.isEmpty(31));
		assertEquals("Testing adding a disc in row 3", false, b.isEmpty(3));
	}

	@Test
	public void testIsFull() {
		for (int i = 0; i < b.getCol() * b.getRow(); i++) {
			b.setField(i, Mark.YELLOW);
		}
		assertEquals("Testing isFull()", true, b.isFull());
	}

	@Test
	public void testHasRow() {
		b.setField(3, 2, Mark.RED);
		b.setField(3, 3, Mark.RED);
		b.setField(3, 4, Mark.RED);
		b.setField(3, 5, Mark.RED);
		assertEquals("Testing hasRow()", true, b.hasRow(Mark.RED));
	}

	@Test
	public void testHasCol() {
		for (int i = 0; i < 4; i++) {
			b.addToCol(4, Mark.YELLOW);
		}
		assertEquals("Testing hasCol()", true, b.hasCol(Mark.YELLOW));
	}

	@Test
	public void testGetRowAndColByIndex() {
		assertEquals("Testing getting a row by index.", 1, b.getRowByIndex(10));
		assertEquals("Testing getting a col by index", 4, b.getColByIndex(25));
	}

	@Test
	public void testHasDiagonalDescending() {
		b.setField(0, 0, Mark.RED);
		b.setField(1, 1, Mark.RED);
		b.setField(2, 2, Mark.RED);
		b.setField(3, 3, Mark.RED);
		b.setField(4, 4, Mark.RED);
		assertEquals("Testing hasDiagonalDesc()", true, b.hasDiagonalDescending(0, 0, Mark.RED));
		b.clearField(b.index(1, 1));
		assertEquals("Testing hasDiagonalDesc()", false, b.hasDiagonalDescending(0, 0, Mark.RED));
	}

	@Test
	public void testHasDiagonalAscending() {
		b.setField(0, 4, Mark.YELLOW);
		b.setField(1, 3, Mark.YELLOW);
		b.setField(2, 2, Mark.YELLOW);
		b.setField(3, 1, Mark.YELLOW);
		assertEquals("Testing hasDiagonalAsc()", true, b.hasDiagonalAscending(3, 1, Mark.YELLOW));
		b.clearField(b.index(2, 2));
		assertEquals("Testing hasDiagonalAsc()", false, b.hasDiagonalAscending(0, 4, Mark.YELLOW));
	}

	@Test
	public void testHasDiagonal() {
		b.setField(1, 3, Mark.RED);
		b.setField(2, 4, Mark.RED);
		b.setField(3, 5, Mark.RED);
		assertEquals("Testing hasDiagonal()", false, b.hasDiagonal(Mark.RED));
		b.setField(4, 6, Mark.RED);
		assertEquals("Testing hasDiagonal()", true, b.hasDiagonal(Mark.RED));
		b.reset();
		b.addToCol(3, Mark.RED);
		b.addToCol(4, Mark.YELLOW);
		b.addToCol(4, Mark.RED);
		b.addToCol(5, Mark.YELLOW);
		b.addToCol(5, Mark.YELLOW);
		b.addToCol(5, Mark.RED);
		b.addToCol(6, Mark.YELLOW);
		b.addToCol(6, Mark.YELLOW);
		b.addToCol(6, Mark.YELLOW);
		b.addToCol(6, Mark.RED);
		assertEquals("Testing hasDiagonalAsc() with addToCol()", true,
				  b.hasDiagonalAscending(5, 3, Mark.RED));
		assertEquals("Testing hasDiagonal() with addToCol()", true, b.hasDiagonal(Mark.RED));
	}

	@Test
	public void testGetWinner() {
		assertEquals("Testing getWinner() on an empty board", null, b.getWinner());
		for (int i = 0; i < 4; i++) {
			b.addToCol(i, Mark.YELLOW);
		}
		assertEquals("Testing getWinner() on a board where yellow has 4 in a row", Mark.YELLOW,
				  b.getWinner());
		b.reset();
		for (int i = 0; i < 4; i++) {
			b.addToCol(4, Mark.RED);
		}

		assertEquals("Testing getWinner() on a board where red has 4 in a column", Mark.RED,
				  b.getWinner());
		b.reset();
		// simulating a game, where yellow manages to get a diagonal.
		for (int i = 0; i < 4; i++) {
			b.addToCol(i, i % 2 == 0 ? Mark.YELLOW : Mark.RED);
		}
		b.addToCol(1, Mark.YELLOW);
		b.addToCol(2, Mark.RED);
		b.addToCol(2, Mark.YELLOW);
		b.addToCol(3, Mark.RED);
		b.addToCol(3, Mark.YELLOW);
		b.addToCol(3, Mark.YELLOW);
		System.out.println(b.getField(35).toString() + b.getField(29) + b.getField(23)
				  + b.getField(17) + b.getField(18));
		assertEquals("Testing getWinner() on a board where yellow has 4 in a diagonal",
				  Mark.YELLOW, b.getWinner());
	}

}
