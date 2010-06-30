package powerbuilder.compiler;

import java.util.EnumSet;
import java.util.Set;

public class Variable {

	public enum Scope {
		GLOBAL, SHARED, INSTANCE, LOCAL
	}
	
	public enum Access {
		GLOBAL, PUBLIC, PROTECTED, PROTECTED_READ, PROTECTED_WRITE, PRIVATE, PRIVATE_READ, PRIVATE_WRITE 
	}
	
	String type;
	int size;
	String name;
	Set<Access> access = EnumSet.noneOf(Access.class);
	Scope scope;
	
	public Variable(Set<Access> access, String type, int size, String name) {
		this.access = access;
		this.type = type;
		this.size = size;
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
	
	public Set<Access> getAccess() {
		return access;
	}
	
	public int getSize() {
		return size;
	}
	
	public String toString() {
		return type + " " + name;
	}
}
