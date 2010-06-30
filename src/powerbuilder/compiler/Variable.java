package powerbuilder.compiler;

import java.util.EnumSet;
import java.util.List;
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
	List<Bound> bounds;
	
	public Variable(Set<Access> access, String type, int size, String name, List<Bound> bounds) {
		this.access = access;
		this.type = type;
		this.size = size;
		this.name = name;
		this.bounds = bounds;
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
	
	public boolean isArray() {
		return bounds != null;
	}
	
	public boolean isUnboundedArray() {
		return bounds != null && bounds.isEmpty();
	}
	
	public List<Bound> getBounds() {
		return bounds;
	}
	
	public String toString() {
		return type + " " + name;
	}
}
