package powerbuilder.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Namespace {

	Namespace parent;
	Map<String, Type> types = new HashMap<String, Type>();
	Map<String, Variable> variables = new LinkedHashMap<String, Variable>();
	//functions is a List to support overloads
	Map<String, List<Function>> functions = new LinkedHashMap<String, List<Function>>();
	
	public Namespace() {
	}
	
	public Namespace(Namespace parent) {
		this.parent = parent;
	}
	
	public Namespace getParent() {
		return parent;
	}
	
	public void addType(Type t) {
		types.put(t.getName(), t);
	}
	
	public void addVariable(Variable var) {
		variables.put(var.getName(), var);
	}
	
	public void addFunction(Function f) {
		List<Function> lf = functions.get(f.getName());
		if (lf == null) {
			lf = new ArrayList<Function>();
			functions.put(f.getName(), lf);
		}
		lf.add(f);
	}
	
	public boolean isDefined(String name) {
		return types.containsKey(name) || variables.containsKey(name) || functions.containsKey(name)
			|| (parent != null && parent.isDefined(name));
	}

	public Type getType(String name) {
		Type t = types.get(name);
		if (t == null && parent != null) {
			return parent.getType(name);
		}
		return t;
	}
	
	public Variable getVariable(String name) {
		Variable v = variables.get(name);
		if (v == null && parent != null) {
			return parent.getVariable(name);
		}
		return v;
	}
	
	public List<Function> getFunctions(String name) {
		List<Function> lf = functions.get(name);
		//should this be a union?
		if (lf == null && parent != null) {
			return parent.getFunctions(name);
		}
		return lf;
	}
}
