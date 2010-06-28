package powerbuilder.compiler;

public class Variable {

	public enum Scope {
		GLOBAL, SHARED, INSTANCE, LOCAL
	}
	
	String type;
	String name;
	Scope scope;
	
	public Variable(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public String toString() {
		return type + " " + name;
	}
}
