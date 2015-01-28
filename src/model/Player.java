package model;

public class Player {
	private String name;
	
	/**
	 * Creates a new Player with a specified name.
	 * @param argName The name of the Player.
	 */
	public Player(String argName){
		name = argName;
	}
	
	/**
	 * Creates a new Player with name "The Unnamed".
	 */
	public Player(){
		this("The Unnamed");
	}
	
	/**
	 * Returns the name of this Player.
	 * @return The name of this Player.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Sets the name of this Player.
	 * @param argName The new name of this Player.
	 */
	public void setName(String argName){
		name = argName;
	}

	public void requestMove(Game game) {
		
	}
}
