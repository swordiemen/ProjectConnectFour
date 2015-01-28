package exceptions;

/**
 * An exception that is thrown whenever a false move is made.
 * 
 * @author Tim Blok
 *
 */
public class FalseMoveException extends Exception {

	private static final long serialVersionUID = 1L;

	public FalseMoveException(String msg) {
		super(msg);
	}

}
