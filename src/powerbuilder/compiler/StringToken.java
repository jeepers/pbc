package powerbuilder.compiler;

public class StringToken extends LiteralToken {

	String string;
	
	public StringToken(String str, int l, int c) {
		super(l, c);
		string = str;
	}

	public String getString() {
		return string;
	}

	public String toString() {
		return super.toString() + "[string]" + string;
	}

	@Override
	public Object getValue() {
		return string;
	}
}
