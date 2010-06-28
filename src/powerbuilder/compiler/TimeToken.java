package powerbuilder.compiler;

public class TimeToken extends LiteralToken {

	final String time;
	
	public TimeToken(String num, int l, int c) {
		super(l, c);
		this.time = num;
	}

	public String getDate() {
		return time;
	}

	public String toString() {
		return super.toString() + "[time]" + time;
	}
}
