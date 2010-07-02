package powerbuilder.compiler;

public abstract class LiteralToken extends Token {

	public LiteralToken(int l, int c) {
		super(l, c);
	}

	public abstract Object getValue();
	
	public boolean isLiteral() {
		return true;
	}
}
