package powerbuilder.compiler;

import java.math.BigDecimal;

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
	
	public boolean isIntegral() {
		return num.matches("\\d+");
	}
	
	public long getLong() {
		if (isIntegral()) {
			return Long.parseLong(num);
		} else {
			throw new NumberFormatException("Not an integral");
		}
	}
	
	public int getInt() {
		return (int) getLong();
	}
	
	public BigDecimal getDecimal() {
		if (isDecimal()) {
			return new BigDecimal(num);
		} else {
			throw new NumberFormatException("Not a decimal");
		}
	}
	
	public double getReal() {
		if (isReal()) {
			return Double.parseDouble(num);
		} else {
			throw new NumberFormatException("Not a real");
		}
	}
	
	public boolean isDecimal() {
		return num.matches("\\d*\\.\\d+");
	}
	
	public boolean isReal() {
		return num.matches("\\d+(\\.\\d+)?[eE][+-]?\\d+");
	}

	@Override
	public Object getValue() {
		if (isIntegral()) {
			return getLong();
		} else if (isDecimal()) {
			return getDecimal();
		} else {
			return getReal();
		}
	}
}
