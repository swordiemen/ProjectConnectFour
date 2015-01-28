package model;

import java.awt.Color;

import constants.Constants;

public enum Mark {
	EMPTY, RED, YELLOW;

	/**
	 * Gives the next Mark. If this is empty, the next will always be empty.
	 * 
	 * @return
	 */
	public Mark next() {
		Mark res;
		if (this == RED) {
			res = YELLOW;
		} else if (this == YELLOW) {
			res = RED;
		} else {
			res = EMPTY;
		}
		return res;

	}

	/**
	 * Returns a Color based on the current Mark.
	 * 
	 * @return The Color based on the current Mark.
	 */
	public Color toColor() {
		Color color = null;
		// TODO: make it a switch
		// switch(this){
		// case RED:
		// color = Constants.RED;
		// case YELLOW:
		// color = Constants.YELLOW;
		// default:
		// color = Constants.EMPTY;
		// }
		// return color;
		if (this == RED) {
			color = Constants.RED;
		} else if (this == YELLOW) {
			color = Constants.YELLOW;
		} else {
			color = Constants.WHITE;
		}
		return color;
	}

	/**
	 * Returns a Mark, given a String.
	 * 
	 * @param s
	 *            The String of a Mark.
	 * @return The Mark corresponding to the String.
	 */
	public Mark getMarkByString(String s) {
		Mark res = null;
		if (s.equals("RED") || s.equals("red") || s.equals("Red")) {
			res = RED;
		} else if (s.equals("YELLOW") || s.equals("yellow") || s.equals("Yellow")) {
			res = YELLOW;
		} else if (s.equals("EMPTY") || s.equals("empty") || s.equals("Empty")) {
			res = EMPTY;
		}
		return res;
	}

	public String toString() {
		return super.toString().toLowerCase();
	}
}
