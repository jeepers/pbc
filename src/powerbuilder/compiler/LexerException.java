package powerbuilder.compiler;

/**
 * @author gpeterson
 *
 */
public class LexerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public LexerException() {
	}

	/**
	 * @param message
	 */
	public LexerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LexerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LexerException(String message, Throwable cause) {
		super(message, cause);
	}

}
