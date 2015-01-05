package model;

import constants.Constants;

public class Board {
	private int row, col;
	private Mark[] fields;

	/**
	 * Creates a new ROWS*COLUMNS(from Constants.java) board, whose fields are all empty.
	 */
	public Board(){
		this(Constants.ROWS, Constants.COLUMNS);
	}

	public Board(int r, int c){
		row = r;
		col = c;
		fields = new Mark[row * col];
		reset();
	}

	/**
	 * Makes all the fields of this board empty.
	 */
	public void reset(){
		for(int i = 0; i < fields.length; i++){
			clearField(i);
		}
	}

	/**
	 * Gets all marks of this board's fields.
	 * @return All fields' Marks.
	 */
	public Mark[] getFields(){
		return fields;
	}

	/**
	 * Sets a certain field to a specified mark.
	 * @param index The field that has to be set.
	 * @param m The mark the field has to be set.
	 */
	public void setField(int index, Mark m){
		fields[index] = m;
	}

	/**
	 * Sets a specified mark on a certain row & column
	 * @param r The row the mark has to be placed on.
	 * @param c The column the mark has to be placed on.
	 * @param m The mark to be set.
	 */
	public void setField(int r, int c, Mark m){
		setField(index(r, c), m);
	}

	/**
	 * Returns the index by using a row and column.
	 * @param r The row.
	 * @param c The column.
	 * @return i The index of the field.
	 */
	public int index(int r, int c){
		if(r > row || c > col){
			return -1;
		}
		return (r * col) + c;
	}

	/**
	 * Adds a certain disc to a row. Returns true if a disc was added, false otherwise.
	 * @param col The col the mark has to be added to.
	 * @param m The Mark of the inserted disc.
	 * @return result Whether the action succeeded.
	 */
	public boolean addToCol(int col, Mark m){
		boolean result = true;
		int nextEntry = getNextEntryInColumn(col);
		if(nextEntry == -1){
			result = false;
		}else{
			setField(nextEntry, m);
		}
		return result;
	}

	public int getNextEntryInColumn(int c){
		int res = -1;
		for(int r = row - 1; r >= 0; r--){
			if(isEmpty(r, c)){
				return index(r, c);
			}
		}
		return res;
	}

	/**
	 * Checks if the current board is full.
	 * @return full Whether the board is full.
	 */
	public boolean isFull() {
		boolean result = true;
		for (int i = 0; i < getFields().length; i++) {
			if (isEmpty(i)) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * Checks if the current board is game over.
	 * @return gameOver Whether the game is game over.
	 */
	public boolean isGameOver(){
		return isFull() || hasWinner();
	}

	/**
	 * Checks if the current board has a winner.
	 * @return winner Whether there is a winner.
	 */
	public boolean hasWinner(){
		return isWinner(Mark.RED) || isWinner(Mark.YELLOW);
	}

	/**
	 * Returns the Mark of the winner of the game. Requires hasWinner() to be true.
	 * @return winner The mark of the winner.
	 */
	public Mark getWinner(){
		Mark winner = null;
		if(isWinner(Mark.RED)){
			winner = Mark.RED;
		}else if(isWinner(Mark.YELLOW)){
			winner = Mark.YELLOW;
		}
		return winner;
	}

	/**
	 * Checks if a specified Mark has won.
	 * @param m The mark to be checked.
	 * @return won Whether m has won.
	 */
	public boolean isWinner(Mark m) {
		return hasRow(m) || hasCol(m) || hasDiagonal(m);		
	}

	/**
	 * Checks if a specified Mark has 4 sequential discs in a row.
	 * @param m The mark to be checked.
	 * @return b Whether m has 4 in a row.
	 */
	public boolean hasRow(Mark m) {
		boolean res = false;
		for(int r = 0; r < row; r++){
			int count = 0;
			for(int c = 0; c < col; c++){
				if(getField(r, c).equals(m)){
					count++;
				}else{
					count = 0;
				}
				if(count >= Constants.WIN_DISCS){
					return true;
				}
			}
		}
		return res;
	}

	/**
	 * Checks if a specified Mark has 4 sequential discs in a column.
	 * @param m The mark to be checked.
	 * @return b Whether m has 4 in a column.
	 */
	public boolean hasCol(Mark m) {
		boolean res = false;
		for(int c = 0; c < col; c++){
			int count = 0;
			for(int r = 0; r < row; r++){
				if(getField(r, c) == m){
					count++;
				}else{
					count = 0;
				}
				if(count >= Constants.WIN_DISCS){
					return true;
				}
			}
		}
		return res;
	}

	/**
	 * Checks if a specified Mark has 4 sequential discs in a diagonal.
	 * @param m The mark to be checked.
	 * @return b Whether m has 4 in a diagonal.
	 */
	public boolean hasDiagonal(Mark m) {
		//TODO optimalize by using WIN_DISCS to limit the amount of checking that has to be done.
		//when you need 4 discs to win (default), there's no need to check
		//the first 3 beginning rows if there's an ascending diagonal.
		for(int c = 0; c <= col - Constants.WIN_DISCS; c++){ //checks the top and bottom row for diagonals.
			if(hasDiagonalDescending(0, c, m) || hasDiagonalAscending(row - 1, c, m)){
				return true;
			}
		}
		
		for(int r = 0; r < row; r++){ // checks the first column for diagonals. Together with ^, this should cover the whole field.
			if(hasDiagonalAscending(r, 0, m) || hasDiagonalDescending(r, 0, m)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a Mark m has 4 in a row on a a diagonal which goes downwards from point r, c. 
	 * @param r The row to start checking on.
	 * @param c The column to start checking on.
	 * @param m The Mark to be checked.
	 * @return b Whether M has 4 on a row on the specified diagonal.
	 */
	public boolean hasDiagonalDescending(int r, int c, Mark m){
		/*the following cases shouldn't happen, but will simply return false if it somehow still happens.
		 *decided not to throw a IndexOutOfBoundsException, as the following will most likely only occur
		 *when dealing with a off-by-one error, so the program shouldn't have any trouble continuing after this point.
		 **/
		if(r < 0 || r >= row){
			System.err.println("The specified row " + r + " is invalid.");
			return false;
		}else if(c < 0 || r >= col){
			System.err.println("The specified column " + c + " is invalid.");
			return false;
		}
		int ci = c; //column iterator
		int count = 0; //keeps track of number of successive Marks.
		for(int ri = r; r < row; ri++){ //row iterator
			if(ci >= col || ri >= row){
				break; //we've reached the end of the diagonal.
			}
			if(getField(ri, ci).equals(m)){
				count++;
			}else{
				count = 0;
			}
			ci++;
			if(count >= Constants.WIN_DISCS){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a Mark m has 4 in a row on a a diagonal which goes upwards from point r, c. 
	 * @param r The row to start checking on.
	 * @param c The column to start checking on.
	 * @param m The Mark to be checked.
	 * @return b Whether M has 4 on a row on the specified diagonal.
	 */
	public boolean hasDiagonalAscending(int r, int c, Mark m){
		if(r < 0 || r >= row){
			System.err.println("The specified row " + r + " is invalid.");
			return false;
		}else if(c < 0 || r >= col){
			System.err.println("The specified column " + c + " is invalid.");
			return false;
		}
		int ci = c; //column iterator
		int count = 0;
		for(int ri = r; r > 0; ri--){
			if(ci >= col || ri < 0){
				break; //we've reached the end of our diagonal.
			}
			if(getField(ri, ci).equals(m)){
				count++;
			}else{
				count = 0;
			}
			ci++;
			if(count >= Constants.WIN_DISCS){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the Mark at field i.
	 * @param i The field where you want the info from.
	 * @return m The Mark the field currently is.
	 */
	public Mark getField(int i){
		return fields[i];
	}

	/**
	 * Returns the Mark at row r and column c.
	 * @param r The row
	 * @param c The column
	 * @return m The mark the field currently is.
	 */
	public Mark getField(int r, int c){
		return getField(index(r, c));
	}

	/**
	 * Checks if a field i is empty.
	 * @param i The field to be checked.
	 * @return b Whether the field is empty or not.
	 */
	public boolean isEmpty(int i){
		return getField(i) == Mark.EMPTY;
	}

	/**
	 * Checks if a field on row, column is empty.
	 * @param r The row
	 * @param c The column
	 * @return b Whether the field is empty or not.
	 */
	public boolean isEmpty(int r, int c){
		return isEmpty(index(r, c));
	}

	/**
	 * Makes a field empty.
	 * @param i The field to be emptied.
	 */
	public void clearField(int i){
		setField(i, Mark.EMPTY);
	}

	/**
	 * Makes a field empty on a specified row and column.
	 * @param r The row
	 * @param c The col
	 */
	public void clearField(int r, int c){
		clearField(index(r, c));
	}


	/**
	 * Returns a copy of this board.
	 * @return board A copy of this board.
	 */
	public Board deepCopy(){
		//TODO die goddamn constructors werkend maken als je een constructor in een constructor aanroept...
		Board res = new Board();
		for(int i = 0; i < row * col; i++){
			res.setField(i, this.getField(i));
		}
		return res;
	}

	public int getRowByIndex(int i){
		return i / col;
	}

	public int getColByIndex(int i){
		return i % col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}
