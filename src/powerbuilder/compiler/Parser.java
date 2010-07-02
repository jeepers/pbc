package powerbuilder.compiler;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import powerbuilder.compiler.Variable.Access;

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
	
	public Namespace parse() {
		//typically start with a global type or a forward
		nextToken();
		if (token().isKeyword(Keyword.GLOBAL)) {
			parseGlobalType(false);
		} else if (token().isKeyword(Keyword.FORWARD)) {
			parseForward();
		}
		return global;
	}

	private void parseForward() {
		nextToken();
		if (token().isKeyword(Keyword.PROTOTYPES)) {
			//function prototypes
		} else {
			parseGlobalType(true);
		}
	}

	private void parseGlobalType(boolean forward) {
		nextToken();
		expectKeyword(Keyword.TYPE);
		Token id = nextToken();
		if (id.isIdentifier()) {
			String name = id.as(WordToken.class).getWord().getWord();
			nextToken();
			String superClass = null;
			if (token().isKeyword(Keyword.FROM)) {
				superClass = nextToken().as(WordToken.class).getWord().getWord();
				nextToken();
			}
			Type t = new Type(name, superClass);
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
				//may be inner types
				while (token().isKeyword(Keyword.TYPE)) {
					//type name from super within parent
					String n = nextToken().getIdentifier();
					expectKeyword(Keyword.FROM);
					String sc = nextToken().getIdentifier();
					expectKeyword(Keyword.WITHIN);
					String pc = nextToken().getIdentifier();
					t.getNamespace().addType(new Type(n, sc, pc));
					nextToken().isEndOfStatement();
					expectKeyword(Keyword.END);
					nextToken();
					expectKeyword(Keyword.TYPE);
				}
			}
			token().isKeyword(Keyword.END);
			nextToken();
			expectKeyword(Keyword.TYPE);
			global.addType(t);
		} else {
			throw new UnexpectedToken(id);
		}
	}
	
	private List<Variable> parseVariableDeclaration() {
		List<Variable> vars = new ArrayList<Variable>();
		Set<Access> access = parseAccess();
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
		while (!token().isEndOfStatement()) {
			String var = token().getIdentifier();
			nextToken();
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
			if (token().isTerminal(Terminal.EQ)) {
				//initial expression, skip for now
				nextToken();
				while (!token().isEndOfStatement() && !token().isTerminal(Terminal.COMMA)) {
					nextToken();
				}
			}
			vars.add(new Variable(access, type, size, var, bounds));
			if (token().isTerminal(Terminal.COMMA)) {
				//another variable of same type
				nextToken();
			}
		}
		return vars;
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
			if (current.isKeyword(Keyword.GLOBAL)) {
				set.add(Access.GLOBAL);
			} else if (current.isKeyword(Keyword.PUBLIC)) {
				last = Access.PUBLIC;
				set.add(Access.PUBLIC);
			} else if (current.isKeyword(Keyword.PROTECTED)) {
				last = Access.PROTECTED;
				set.add(Access.PROTECTED);
			} else if (current.isKeyword(Keyword.PROTECTEDREAD)) {
				set.add(Access.PROTECTED_READ);
			} else if (current.isKeyword(Keyword.PROTECTEDWRITE)) {
				set.add(Access.PROTECTED_WRITE);
			} else if (current.isKeyword(Keyword.PRIVATE)) {
				last = Access.PRIVATE;
				set.add(Access.PRIVATE);
			} else if (current.isKeyword(Keyword.PRIVATEREAD)) {
				set.add(Access.PRIVATE_READ);
			} else if (current.isKeyword(Keyword.PRIVATEWRITE)) {
				set.add(Access.PRIVATE_WRITE);
			} else {
				break;
			}
			nextToken();
		}
		if (current.isTerminal(Terminal.COLON)) {
			//setting the default access for following declarations
			if (last != null) {
				currentAccess = last;
			} else {
				throw new UnexpectedToken(current);
			}
		}
		return set;
	}
}
