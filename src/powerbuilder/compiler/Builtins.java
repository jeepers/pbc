package powerbuilder.compiler;

import java.util.EnumSet;

import powerbuilder.compiler.Variable.Access;

public class Builtins {

	private static final Namespace instance = new Namespace();
	
	static {
		//primitive types
		instance.addType(new Type("blob"));
		instance.addType(new Type("boolean"));
		instance.addType(new Type("character"));
		instance.addType(new Type("date"));
		instance.addType(new Type("datetime"));
		instance.addType(new Type("decimal"));
		instance.addType(new Type("double"));
		instance.addType(new Type("integer"));
		instance.addType(new Type("longlong"));
		instance.addType(new Type("long"));
		instance.addType(new Type("real"));
		instance.addType(new Type("string"));
		instance.addType(new Type("time"));
		instance.addType(new Type("unsignedinteger"));
		instance.addType(new Type("unsignedlong"));

		//the polymorphic type
		instance.addType(new Type("any"));

		//object types
		Type powerobject = new Type("powerobject");
		powerobject.getNamespace().addVariable(new Variable(EnumSet.of(Access.PUBLIC, Access.PRIVATE_WRITE), "powerobject", 0, "classdefinition", null));
		powerobject.getNamespace().addFunction(new Function("string", "classname"));
		instance.addType(powerobject);
	}

	public static Namespace getBuiltins() {
		return instance;
	}
	
}
