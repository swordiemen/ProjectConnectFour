package model;

import java.awt.Color;

import constants.Constants;

public enum Mark {
	EMPTY, RED, YELLOW;

	public Mark next(){
		Mark mark = null;
			switch(this){
			case  RED:
				mark = RED;
			case YELLOW:
				mark = YELLOW;
			default:
				mark = RED;
		}
		return mark;
	}
	
	public Color toColor(){
		Color color = null;
		switch(this){
		case RED:
			color = Constants.RED;
		case YELLOW:
			color = Constants.YELLOW;
		default:
			color = Constants.WHITE;
		return color;
		}
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
