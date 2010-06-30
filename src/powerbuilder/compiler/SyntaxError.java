package powerbuilder.compiler;

public class SyntaxError extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Token token;

	public SyntaxError(String message, Token token) {
		super(message);
		this.token = token;
	}
	
	public int getLine() {
		return token.getLine();
	}
	
	public int getColumn() {
		return token.getColumn();
	}
	
	public String getMessage() {
		return "[" + getLine() + ", " + getColumn() + "] " + super.getMessage();
	}
}
