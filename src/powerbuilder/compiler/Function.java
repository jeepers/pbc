package powerbuilder.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Function {

	Set<Access> access;
	String name;
	String returns;
	
	List<Variable> parameters = new ArrayList<Variable>();
	List<Variable> locals = new ArrayList<Variable>();
	
	public Function(Set<Access> access, String returns, String name) {
		this.access = access;
		this.returns = returns;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<Access> getAccess() {
		return access;
	}
	
	public void addParameter(Variable v) {
		parameters.add(v);
	}
	
	public void addLocal(Variable v) {
		locals.add(v);
	}
}
