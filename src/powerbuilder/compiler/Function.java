package powerbuilder.compiler;

public class Function {

	String name;
	//local variables, parameters(?)
	Namespace namespace = new Namespace();
	
	public Function(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
