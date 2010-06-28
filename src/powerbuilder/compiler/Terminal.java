package powerbuilder.compiler;

public class Terminal {

	public static final Terminal SEMI = new Terminal(";");
	public static final Terminal EOL = new Terminal("\n") {
		public String toString() {
			return "<EOL>";
		}
	};
	public static final Terminal ADD = new Terminal("+");
	public static final Terminal SUB = new Terminal("-");
	public static final Terminal MULT = new Terminal("*");
	public static final Terminal DIV = new Terminal("/");
	public static final Terminal EXP = new Terminal("^");
	public static final Terminal EQ = new Terminal("=");
	public static final Terminal GT = new Terminal(">");
	public static final Terminal LT = new Terminal("<");
	public static final Terminal NEQ = new Terminal("<>");
	public static final Terminal GEQ = new Terminal(">=");
	public static final Terminal LEQ = new Terminal("<=");
	public static final Terminal BANG = new Terminal("!");
	public static final Terminal LPAREN = new Terminal("(");
	public static final Terminal RPAREN = new Terminal(")");
	public static final Terminal LBRACKET = new Terminal("[");
	public static final Terminal RBRACKET = new Terminal("]");
	public static final Terminal LBRACE = new Terminal("{");
	public static final Terminal RBRACE = new Terminal("}");
	public static final Terminal COMMA = new Terminal(",");
	public static final Terminal DOT = new Terminal(".");
	public static final Terminal COLON = new Terminal(":");
	
	private final String term;
	
	private Terminal(String t) {
		term = t;
	}

	public String getSymbol() {
		return term;
	}
	
	public String toString() {
		return term;
	}
}
