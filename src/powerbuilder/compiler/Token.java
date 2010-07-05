package powerbuilder.compiler;

public abstract class Token {

	int line;
	int column;
	
	public Token(int l, int c) {
		line = l;
		column = c;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}
	
	public String toString() {
		return "{" + line + "," + column + "}";
	}
	
	public boolean isA(Class<? extends Token> clazz) {
		return clazz.isInstance(this);
	}
	
	public boolean isEndOfStatement() {
		if (isA(TerminalToken.class)) {
			Terminal t = as(TerminalToken.class).getTerminal();
			return t == Terminal.EOL || t == Terminal.SEMI || t == Terminal.EOF;
		}
		return false;
	}
	
	public <T extends Token> T as(Class<T> clazz) {
		if (clazz.isInstance(this)) {
			return clazz.cast(this);
		} else {
			throw new UnexpectedToken(this);
		}
	}
	
	public boolean isKeyword(Keyword kw) {
		return false;
	}
	
	public boolean isIdentifier() {
		return false;
	}
	
	public String getIdentifier() {
		throw new UnexpectedToken(this);
	}
	
	public boolean isTerminal(Terminal t) {
		return false;
	}
	
	public boolean isNumber() {
		return false;
	}
	
	public boolean isLiteral() {
		return false;
	}
}
