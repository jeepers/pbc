package powerbuilder.compiler;

public class DateToken extends LiteralToken {

	final String date;
	
	public DateToken(String num, int l, int c) {
		super(l, c);
		this.date = num;
	}

	public String getDate() {
		return date;
	}

	public String toString() {
		return super.toString() + "[date]" + date;
	}
}
