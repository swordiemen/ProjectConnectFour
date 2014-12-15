package model;

import constants.Constants;

public class Board {
	private int row, col;
	private Mark[] fields;
	
	/**
	 * Creates a new board, whose fields are all empty.
	 */
	public Board(){
		row = Constants.ROWS;
		col = Constants.COLUMNS;
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
	
	public void setField(int r, int c, Mark m){
		setField(r * row + c, m);
	}
	
	/**
	 * Adds a certain disc to a row.
	 * @param row The row the mark has to be added to.
	 * @param m The Mark of the inserted disc.
	 */
	public boolean addToRow(int row, Mark m){
		//op een schaal van 1 tot 10, hoe lelijk is dit?
		//TODO shit fixen yo
		boolean result = true;
		if(isEmpty(row + 35)){
			setField(row + 35, m);
		}else if(isEmpty(row + 28)){
			setField(row + 28, m);
		}else if(isEmpty(row + 21)){
			setField(row + 21, m);
		}else if(isEmpty(row + 14)){
			setField(row + 14, m);
		}else if(isEmpty(row + 7)){
			setField(row + 7, m);
		}else if(isEmpty(row)){
			setField(row, m);
		}else{
			result = false;
		}
		return result;
	}
	
	public boolean isFull() {
		boolean result = true;
		for (int i = 0; i < getFields().length; i++) {
			if (isEmpty(i)) {
				result = false;
			}
		}
		return result;
	}
	
	public boolean isGameOver(){
		return isFull() || hasWinner();
	}
	
	public boolean hasWinner(){
		return isWinner(Mark.RED) || isWinner(Mark.YELLOW);
	}
	
	private boolean isWinner(Mark m) {
		return hasRow(m) || hasCol(m) || hasDiagonal(m);		
	}

	private boolean hasRow(Mark m) {
		boolean res = false;
		for(int r = 0; r < row; r++){
			int count = 0;
			for(int c = 0; c < col; c++){
				if(getField(r, c) == m){
					count++;
				}else{
					count = 0;
				}
				if(count > Constants.WIN_DISCS){
					return true;
				}
			}
		}
		return res;
	}

	private boolean hasCol(Mark m) {
		boolean res = false;
		for(int c = 0; c < col; c++){
			int count = 0;
			for(int r = 0; r < row; r = r + row){
				if(getField(r, c) == m){
					count++;
				}else{
					count = 0;
				}
				if(count > Constants.WIN_DISCS){
					return true;
				}
			}
		}
		return res;
	}

	private boolean hasDiagonal(Mark m) {
		// TODO Auto-generated method stub
		return false;
	}

	public Mark getField(int i){
		return fields[i];
	}
	
	public Mark getField(int r, int c){
		return getField(r * row + c);
	}
	
	public boolean isEmpty(int i){
		return getField(i) == Mark.EMPTY;
	}
	
	public void clearField(int i){
		setField(i, Mark.EMPTY);
	}
	
	public Board deepCopy(){
		Board res = new Board();
		for(int i = 0; i < row * col; i++){
			res.setField(i, this.getField(i));
		}
		return res;
	}
	
	
}
