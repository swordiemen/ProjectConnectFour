package model;

public class Player {
	private String name;
	
	public Player(String s){
		name = s;
	}
	
	public Player(){
		this("The Unnamed");
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String s){
		name = s;
	}

	public void requestMove(Game game) {
		//TODO github pls
	}
}
