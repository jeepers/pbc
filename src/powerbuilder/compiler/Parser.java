package powerbuilder.compiler;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;


public class Parser {

	Namespace global;
	Lexer lexer;
	Access currentAccess;
	Token current;
	
	public Parser(Lexer lexer) {
		this.lexer = lexer;
		this.global = new Namespace();
	}
	
	public Parser(Namespace global, Lexer lexer) {
		this.lexer = lexer;
		this.global = global;
	}

	private Token nextToken() {
		Token token = lexer.nextToken();
		System.err.println(token);
		current = token;
		return token;
	}
	
	private Token token() {
		return current;
	}
	
	private void expectKeyword(Keyword kw) {
		if (!token().isKeyword(kw)) {
			throw new UnexpectedToken(token());
		}
	}
	
	private void expectEndOfStatement() {
		if (!token().isEndOfStatement()) {
			throw new UnexpectedToken(token());
		}
	}
	
	public Namespace parse() {
		//typically start with a global type or a forward
		nextToken();
		if (token().isKeyword(Keyword.GLOBAL) && nextToken().isKeyword(Keyword.TYPE)) {
			global.addType(parseType(false));
		} else if (token().isKeyword(Keyword.FORWARD)) {
			parseForward();
		}
		return global;
	}

	private void parseForward() {
		nextToken();
		if (token().isKeyword(Keyword.PROTOTYPES)) {
			parsePrototypes();
		} else if (token().isKeyword(Keyword.GLOBAL)) {
			nextToken();
			if (token().isKeyword(Keyword.TYPE)) {
				global.addType(parseType(true));
			}
			nextToken();
			while (!token().isKeyword(Keyword.END)) {
				Set<Access> access = parseAccess();
				if (token().isKeyword(Keyword.TYPE)) {
					Type t = parseType(true);
					if (access.contains(Access.GLOBAL)) {
						global.addType(t);
					} else {
						if (t.getParentClass() != null) {
							Type parent = global.getType(t.getParentClass());
							parent.getNamespace().addType(t);
						}
					}
				} else {
					List<Variable> vars = parseVariableDeclaration();
					if (access.contains(Access.GLOBAL)) {
						global.addVariables(vars);
					}
				}
			}
			nextToken();
			expectKeyword(Keyword.FORWARD);
		}
	}

	protected List<Function> parsePrototypes() {
		List<Function> functions = new ArrayList<Function>();
		nextToken();
		expectEndOfStatement();
		//function prototypes
		nextToken();
		while (!token().isKeyword(Keyword.END)) {
			Function f = parseFunction(true);
			//don't really belong here
			functions.add(f);
		}
		nextToken();
		expectKeyword(Keyword.PROTOTYPES);
		nextToken();
		expectEndOfStatement();
		return functions;
	}
	
	private Function parseFunction(boolean forward) {
		Set<Access> access = parseAccess();
		String returnType = null;
		if (token().isKeyword(Keyword.FUNCTION)) {
			returnType = nextToken().getIdentifier();
		} else {
			expectKeyword(Keyword.SUBROUTINE);
		}
		String name = nextToken().getIdentifier();
		Function func = new Function(access, returnType, name);
		if (!nextToken().isTerminal(Terminal.LPAREN)) {
			throw new UnexpectedToken(token());
		}
		nextToken();
		while (!token().isTerminal(Terminal.RPAREN)) {
			//parameters
			DeclaredType dec = parseDeclaredType();
			String param = token().getIdentifier();
			nextToken();
			List<Bound> bounds = parseBounds();
			func.addParameter(new Variable(null, dec.type, dec.size, param, bounds));
			if (token().isTerminal(Terminal.COMMA)) {
				nextToken();
			}
		}
		nextToken();
		if (forward) {
			expectEndOfStatement();
			nextToken();
		}
		return func;
	}

	private Type parseType(boolean forward) {
		Token id = nextToken();
		if (id.isIdentifier()) {
			String name = id.as(WordToken.class).getWord().getWord();
			nextToken();
			String superClass = null;
			if (token().isKeyword(Keyword.FROM)) {
				superClass = nextToken().getIdentifier();
				nextToken();
			}
			String parentClass = null;
			if (token().isKeyword(Keyword.WITHIN)) {
				parentClass = nextToken().getIdentifier();
				nextToken();
			}
			Type t = new Type(name, superClass, parentClass);
			if (!forward) {
				if (token().isKeyword(Keyword.AUTOINSTANTIATE)) {
					t.setAutoInstantiate(true);
					nextToken();
				}
				if (token().isEndOfStatement()) {
					//end of declaration
					nextToken();
				}
				while (!token().isKeyword(Keyword.END)) {
					//declarations - variables, events
					if (token().isKeyword(Keyword.EVENT)) {
						//declares an event
					} else {
						//declares a variable
						List<Variable> vars = parseVariableDeclaration();
						if (!vars.isEmpty()) {
							t.getNamespace().addVariables(vars);
						}
					}
					if (token().isEndOfStatement()) {
						nextToken();
					}
				}
			} else {
				if (token().isEndOfStatement()) {
					//end of declaration
					nextToken();
				}
			}
			token().isKeyword(Keyword.END);
			nextToken();
			expectKeyword(Keyword.TYPE);
			return t;
		} else {
			throw new UnexpectedToken(id);
		}
	}
	
	static class DeclaredType {
		String type;
		int size;
		
		DeclaredType(String t, int z) {
			type = t;
			size = z;
		}
	}
	
	private DeclaredType parseDeclaredType() {
		String type = token().as(WordToken.class).getWord().getWord();
		nextToken();
		int size = 0;
		if (token().isTerminal(Terminal.LBRACE)) {
			//size or precision
			nextToken();
			if (token().isNumber()) {
				//parse size from number as integer
				size = token().as(NumberToken.class).getInt();
			}
			nextToken().isTerminal(Terminal.RBRACE);
			nextToken();
		}
		return new DeclaredType(type, size);
	}
	
	private List<Variable> parseVariableDeclaration() {
		List<Variable> vars = new ArrayList<Variable>();
		Set<Access> access = parseAccess();
		DeclaredType dec = parseDeclaredType();
		while (!token().isEndOfStatement()) {
			String var = token().getIdentifier();
			nextToken();
			List<Bound> bounds = parseBounds();
			if (token().isTerminal(Terminal.EQ)) {
				//initial expression, skip for now
				nextToken();
				while (!token().isEndOfStatement() && !token().isTerminal(Terminal.COMMA)) {
					nextToken();
				}
			}
			vars.add(new Variable(access, dec.type, dec.size, var, bounds));
			if (token().isTerminal(Terminal.COMMA)) {
				//another variable of same type
				nextToken();
			}
		}
		return vars;
	}
	
	private List<Bound> parseBounds() {
		List<Bound> bounds = null;
		if (token().isTerminal(Terminal.LBRACKET)) {
			//array
			bounds = new ArrayList<Bound>();
			nextToken();
			while (!token().isTerminal(Terminal.RBRACKET)) {
				if (maybeSignedInteger()) {
					//length or bound
					int b = signedInteger();
					if (nextToken().isKeyword(Keyword.TO)) {
						nextToken();
						int ub = signedInteger();
						if (ub <= b) {
							throw new SyntaxError("Array upper bound must be greater than the lower bound", token());
						}
						bounds.add(new Bound(b, ub));
						nextToken();
					} else {
						if (b <= 0) {
							throw new SyntaxError("Array size must be greater than zero", token());
						}
						bounds.add(new Bound(b));
					}
				} else if (token().isTerminal(Terminal.COMMA)) {
					nextToken();
				}
			}
			nextToken();
		}
		return bounds;
	}
	
	private boolean maybeSignedInteger() {
		return token().isNumber() || token().isTerminal(Terminal.SUB);
	}
	
	private int signedInteger() {
		boolean negate = false;
		if (token().isTerminal(Terminal.SUB)) {
			negate = true;
			nextToken();
		}
		return token().as(NumberToken.class).getInt() * (negate ? -1 : 1);
	}
	
	private Set<Access> parseAccess() {
		EnumSet<Access> set = EnumSet.noneOf(Access.class);
		Access last = null;
		while (true) {
			if (token().isKeyword(Keyword.GLOBAL)) {
				set.add(Access.GLOBAL);
			} else if (token().isKeyword(Keyword.PUBLIC)) {
				last = Access.PUBLIC;
				set.add(Access.PUBLIC);
			} else if (token().isKeyword(Keyword.PROTECTED)) {
				last = Access.PROTECTED;
				set.add(Access.PROTECTED);
			} else if (token().isKeyword(Keyword.PROTECTEDREAD)) {
				set.add(Access.PROTECTED_READ);
			} else if (token().isKeyword(Keyword.PROTECTEDWRITE)) {
				set.add(Access.PROTECTED_WRITE);
			} else if (token().isKeyword(Keyword.PRIVATE)) {
				last = Access.PRIVATE;
				set.add(Access.PRIVATE);
			} else if (token().isKeyword(Keyword.PRIVATEREAD)) {
				set.add(Access.PRIVATE_READ);
			} else if (token().isKeyword(Keyword.PRIVATEWRITE)) {
				set.add(Access.PRIVATE_WRITE);
			} else {
				break;
			}
			nextToken();
		}
		if (token().isTerminal(Terminal.COLON)) {
			//setting the default access for following declarations
			if (last != null) {
				currentAccess = last;
			} else {
				throw new UnexpectedToken(current);
			}
			nextToken();
		}
		return set;
	}
}
