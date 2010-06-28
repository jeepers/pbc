package powerbuilder.compiler;

public class UnexpectedToken extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Token token;

	public UnexpectedToken(Token tok) {
		this.token = tok;
	}
	
	public Token getToken() {
		return token;
	}
	
	public String getMessage() {
		return token.toString();
	}
}
