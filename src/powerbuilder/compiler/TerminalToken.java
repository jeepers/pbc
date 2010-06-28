package powerbuilder.compiler;

public class TerminalToken extends Token {

	private final Terminal terminal;
	
	public TerminalToken(Terminal term, int l, int c) {
		super(l, c);
		terminal = term;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}

	public String toString() {
		return super.toString() + "[terminal]" + terminal;
	}
	
	public boolean isTerminal(Terminal t) {
		return t == terminal;
	}
}
