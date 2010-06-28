package powerbuilder.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Keyword extends Word {

	private static final Map<String, Keyword> KEYWORD_MAP = new HashMap<String, Keyword>();
	public static final Set<String> KEYWORDS = KEYWORD_MAP.keySet();

	public static final Keyword ALIAS = new Keyword("alias");
	public static final Keyword AND = new Keyword("and");
	public static final Keyword AUTOINSTANTIATE = new Keyword("autoinstantiate");
	public static final Keyword CALL = new Keyword("call");
	public static final Keyword CASE = new Keyword("case");
	public static final Keyword CATCH = new Keyword("catch");
	public static final Keyword CHOOSE = new Keyword("choose");
	public static final Keyword CLOSE = new Keyword("close", true);
	public static final Keyword COMMIT = new Keyword("commit");
	public static final Keyword CONNECT = new Keyword("connect");
	public static final Keyword CONSTANT = new Keyword("constant");
	public static final Keyword CONTINUE = new Keyword("continue");
	public static final Keyword CREATE = new Keyword("create", true);
	public static final Keyword CURSOR = new Keyword("cursor");
	public static final Keyword DECLARE = new Keyword("declare");
	public static final Keyword DELETE = new Keyword("delete");
	public static final Keyword DESCRIBE = new Keyword("describe", true);
	public static final Keyword DESCRIPTOR = new Keyword("descriptor");
	public static final Keyword DESTROY = new Keyword("destroy");
	public static final Keyword DISCONNECT = new Keyword("disconnect");
	public static final Keyword DO = new Keyword("do");
	public static final Keyword DYNAMIC = new Keyword("dynamic");
	public static final Keyword ELSE = new Keyword("else");
	public static final Keyword ELSEIF = new Keyword("elseif");
	public static final Keyword END = new Keyword("end");
	public static final Keyword ENUMERATED = new Keyword("enumerated");
	public static final Keyword EVENT = new Keyword("event");
	public static final Keyword EXECUTE = new Keyword("execute");
	public static final Keyword EXIT = new Keyword("exit");
	public static final Keyword EXTERNAL = new Keyword("external");
	public static final Keyword FALSE = new Keyword("false");
	public static final Keyword FETCH = new Keyword("fetch");
	public static final Keyword FINALLY = new Keyword("finally");
	public static final Keyword FIRST = new Keyword("first");
	public static final Keyword FOR = new Keyword("for");
	public static final Keyword FORWARD = new Keyword("forward");
	public static final Keyword FROM = new Keyword("from");
	public static final Keyword FUNCTION = new Keyword("function");
	public static final Keyword GLOBAL = new Keyword("global");
	public static final Keyword GOTO = new Keyword("goto");
	public static final Keyword HALT = new Keyword("halt");
	public static final Keyword IF = new Keyword("if");
	public static final Keyword IMMEDIATE = new Keyword("immediate");
	public static final Keyword INDIRECT = new Keyword("indirect");
	public static final Keyword INSERT = new Keyword("insert");
	public static final Keyword INTO = new Keyword("into");
	public static final Keyword INTRINSIC = new Keyword("intrinsic");
	public static final Keyword IS = new Keyword("is");
	public static final Keyword LAST = new Keyword("last");
	public static final Keyword LIBRARY = new Keyword("library");
	public static final Keyword LOOP = new Keyword("loop");
	public static final Keyword NEXT = new Keyword("next");
	public static final Keyword NOT = new Keyword("not");
	public static final Keyword OF = new Keyword("of");
	public static final Keyword ON = new Keyword("on");
	public static final Keyword OPEN = new Keyword("open", true);
	public static final Keyword OR = new Keyword("or");
	public static final Keyword PARENT = new Keyword("parent");
	public static final Keyword POST = new Keyword("post", true);
	public static final Keyword PREPARE = new Keyword("prepare");
	public static final Keyword PRIOR = new Keyword("prior");
	public static final Keyword PRIVATE = new Keyword("private");
	public static final Keyword PRIVATEREAD = new Keyword("privateread");
	public static final Keyword PRIVATEWRITE = new Keyword("privatewrite");
	public static final Keyword PROCEDURE = new Keyword("procedure");
	public static final Keyword PROTECTED = new Keyword("protected");
	public static final Keyword PROTECTEDREAD = new Keyword("protectedread");
	public static final Keyword PROTECTEDWRITE = new Keyword("protectedwrite");
	public static final Keyword PROTOTYPES = new Keyword("prototypes");
	public static final Keyword PUBLIC = new Keyword("public");
	public static final Keyword READONLY = new Keyword("readonly");
	public static final Keyword REF = new Keyword("ref");
	public static final Keyword RETURN = new Keyword("return");
	public static final Keyword ROLLBACK = new Keyword("rollback");
	public static final Keyword RPCFUNC = new Keyword("rpcfunc");
	public static final Keyword SELECT = new Keyword("select");
	public static final Keyword SELECTBLOB = new Keyword("selectblob");
	public static final Keyword SHARED = new Keyword("shared");
	public static final Keyword STATIC = new Keyword("static");
	public static final Keyword STEP = new Keyword("step");
	public static final Keyword SUBROUTINE = new Keyword("subroutine");
	public static final Keyword SUPER = new Keyword("super");
	public static final Keyword SYSTEM = new Keyword("system");
	public static final Keyword SYSTEMREAD = new Keyword("systemread");
	public static final Keyword SYSTEMWRITE = new Keyword("systemwrite");
	public static final Keyword THEN = new Keyword("then");
	public static final Keyword THIS = new Keyword("this");
	public static final Keyword THROW = new Keyword("throw");
	public static final Keyword THROWS = new Keyword("throws");
	public static final Keyword TO = new Keyword("to");
	public static final Keyword TRIGGER = new Keyword("trigger");
	public static final Keyword TRUE = new Keyword("true");
	public static final Keyword TRY = new Keyword("try");
	public static final Keyword TYPE = new Keyword("type");
	public static final Keyword UNTIL = new Keyword("until");
	public static final Keyword UPDATE = new Keyword("update", true);
	public static final Keyword UPDATEBLOB = new Keyword("updateblob");
	public static final Keyword USING = new Keyword("using");
	public static final Keyword VARIABLES = new Keyword("variables");
	public static final Keyword WHILE = new Keyword("while");
	public static final Keyword WITH = new Keyword("with");
	public static final Keyword WITHIN = new Keyword("within");
	public static final Keyword _DEBUG = new Keyword("_debug");
	
	private final boolean functionAllowed;
	
	private Keyword(String str) {
		this(str, false);
	}
	
	private Keyword(String str, boolean functionAllowed) {
		super(str);
		this.functionAllowed = functionAllowed;
		KEYWORD_MAP.put(str, this);
	}

	public boolean isFunctionAllowed() {
		return functionAllowed;
	}
	
	public static boolean isKeyword(String str) {
		return KEYWORDS.contains(str.toLowerCase());
	}
	
	public static boolean isFunctionAllowed(String str) {
		String kw = str.toLowerCase();
		return KEYWORDS.contains(kw) && KEYWORD_MAP.get(kw).isFunctionAllowed();
	}
	
	static Keyword get(String str) {
		return KEYWORD_MAP.get(str.toLowerCase());
	}
}
