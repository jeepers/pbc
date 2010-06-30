package powerbuilder.compiler;

public class Bound {

	private int lower;
	private int upper;
	
	public Bound(int upper) {
		this.upper = upper;
	}
	
	public Bound(int lower, int upper) {
		this.lower = lower;
		this.upper = upper;
	}

	public int getLower() {
		return lower;
	}

	public int getUpper() {
		return upper;
	}
	
	public int length() {
		return (upper - lower) + 1;
	}
}
