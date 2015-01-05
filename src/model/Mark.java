package model;

import java.awt.Color;

import constants.Constants;

public enum Mark {
	EMPTY, RED, YELLOW;

	/**
	 * Gives the next Mark. If this is empty, the next will always be empty. 
	 * @return
	 */
	public Mark next(){
//		//TODO fix
//		Mark mark = this;
//		//System.out.println(this.toString() + "is het");
//		switch(mark){
//			case RED:
//				mark = YELLOW;
//			case YELLOW:
//				mark = RED;
//			default:
//				//mark = EMPTY;
//		}
//		return mark;
		Mark mark = this;
		if(mark == RED){
			mark = YELLOW;
		}else if(mark == YELLOW){
			mark = RED;
		}else{
			mark = EMPTY;
		}
		return mark;
	}

	public Color toColor(){
		Color color = null;
		//TODO: make it a switch
//		switch(this){
//		case RED:
//			color = Constants.RED;
//		case YELLOW:
//			color = Constants.YELLOW;
//		default:
//			color = Constants.RED;
//		}
//		return color;
		if(this == RED){
			color = Constants.RED;
		}else if(this == YELLOW){
			color = Constants.YELLOW;
		}else{
			color = Constants.WHITE;
		}
		return color;
	}

	public Mark getMarkByString(String s){
		Mark res = null;
		if(s.equals("RED") || s.equals("red") || s.equals("Red")){
			res = RED;
		}else if(s.equals("YELLOW") || s.equals("yellow") || s.equals("Yellow")){
			res = YELLOW;
		}else if(s.equals("EMPTY") || s.equals("empty") || s.equals("Empty")){
			res = EMPTY;
		}
		return res;
	}
}
