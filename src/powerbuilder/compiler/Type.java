package powerbuilder.compiler;

public class Type {

	String name;
	String superClass;
	String parentClass;
	//instance and shared variables, functions, nested types
	Namespace namespace = new Namespace();
	boolean autoInstantiate;
	
	public Type(String name) {
		this.name = name;
	}
	
	public Type(String name, String superClass) {
		this(name);
		this.superClass = superClass;
	}
	
	public Type(String name, String superClass, String parentClass) {
		this(name, superClass);
		this.parentClass = parentClass;
	}

	public String getName() {
		return name;
	}
	
	public String getSuperClass() {
		return superClass;
	}

	public String getParentClass() {
		return parentClass;
	}
	
	public void setAutoInstantiate(boolean b) {
		autoInstantiate = b;
	}
	
	public boolean isAutoInstantiate() {
		return autoInstantiate;
	}
	
	public Namespace getNamespace() {
		return namespace;
	}
}
