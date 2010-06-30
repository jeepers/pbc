package powerbuilder.compiler;

import java.util.ArrayList;
import java.util.List;

public class Function {

	String name;
	String returns;
	
	List<Variable> parameters = new ArrayList<Variable>();
	List<Variable> locals = new ArrayList<Variable>();
	
	public Function(String returns, String name) {
		this.returns = returns;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addParameter(Variable v) {
		parameters.add(v);
	}
	
	public void addLocal(Variable v) {
		locals.add(v);
	}
}
