package powerbuilder.compiler;

public class Builtins {

	private static final Namespace instance = new Namespace();
	
	static {
		//primitive types
		instance.addType(new Type("string"));
		instance.addType(new Type("decimal"));
		instance.addType(new Type("integer"));
		instance.addType(new Type("long"));
		instance.addType(new Type("longlong"));
		instance.addType(new Type("uint"));
		instance.addType(new Type("ulong"));
		instance.addType(new Type("real"));
		instance.addType(new Type("double"));
		instance.addType(new Type("boolean"));
		
		//object types
		Type powerobject = new Type("powerobject");
		instance.addType(powerobject);
	}

	public static Namespace getBuiltins() {
		return instance;
	}
	
}
