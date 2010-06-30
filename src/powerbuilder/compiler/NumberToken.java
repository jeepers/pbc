package powerbuilder.compiler;

public class NumberToken extends LiteralToken {

	final String num;
	
	public NumberToken(String num, int l, int c) {
		super(l, c);
		this.num = num;
	}

	public String getNum() {
		return num;
	}

	public String toString() {
		return super.toString() + "[number]" + num;
	}
	
	public boolean isNumber() {
		return true;
	}
}
