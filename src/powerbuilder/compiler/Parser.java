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
	
	private WordToken expectKeyword(Keyword... kw) {
		Token tok = nextToken();
		if (tok instanceof WordToken) {
			WordToken wt = (WordToken) tok;
			if (wt.isKeyword()) {
				for (Keyword w : kw) {
					if (wt.is(w)) {
						return wt;
					}
				}
			}
		}
		throw new UnexpectedToken(tok);
	}
	
	public Namespace parse() {
		//typically start with a global type or a forward
		WordToken tok = expectKeyword(Keyword.GLOBAL, Keyword.FORWARD);
		if (tok != null) {
			if (tok.is(Keyword.GLOBAL)) {
				parseGlobalType(false);
			} else if (tok.is(Keyword.FORWARD)) {
				parseForward();
			}
		}
		return global;
	}

	private void parseForward() {
		// TODO Auto-generated method stub
		parseGlobalType(true);
	}

	private void parseGlobalType(boolean forward) {
		expectKeyword(Keyword.TYPE);
		Token id = nextToken();
		if (id.isIdentifier()) {
			String name = id.as(WordToken.class).getWord().getWord();
			Token tok = nextToken();
			String superClass = null;
			if (tok.isA(WordToken.class)) {
				if (tok.isKeyword(Keyword.FROM)) {
					superClass = nextToken().as(WordToken.class).getWord().getWord();
					tok = nextToken();
				}
			}
			Type t = new Type(name, superClass);
			if (!forward) {
				if (tok.isKeyword(Keyword.AUTOINSTANTIATE)) {
					t.setAutoInstantiate(true);
					tok = nextToken();
				}
				if (tok.isEndOfStatement()) {
					//end of declaration
					tok = nextToken();
				}
				while (!tok.isKeyword(Keyword.END)) {
					//declarations - variables, events
					if (tok.isKeyword(Keyword.EVENT)) {
						//declares an event
					} else {
						//declares a variable
						List<Variable> vars = parseVariableDeclaration();
						if (!vars.isEmpty()) {
							t.getNamespace().addVariables(vars);
						}
						tok = token();
					}
					if (tok.isEndOfStatement()) {
						tok = nextToken();
					}
				}
			} else {
				if (tok.isEndOfStatement()) {
					//end of declaration
					tok = nextToken();
				}
				//may be inner types
				while (tok.isKeyword(Keyword.TYPE)) {
					//type name from super within parent
					String n = nextToken().getIdentifier();
					expectKeyword(Keyword.FROM);
					String sc = nextToken().getIdentifier();
					expectKeyword(Keyword.WITHIN);
					String pc = nextToken().getIdentifier();
					t.getNamespace().addType(new Type(n, sc, pc));
					nextToken().isEndOfStatement();
					expectKeyword(Keyword.END);
					expectKeyword(Keyword.TYPE);
				}
			}
			tok.isKeyword(Keyword.END);
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
				size = Integer.parseInt(token().as(NumberToken.class).getNum());
			}
			nextToken().isTerminal(Terminal.RBRACE);
			nextToken();
		}
		while (!token().isEndOfStatement()) {
			String var = token().getIdentifier();
			nextToken();
			if (token().isTerminal(Terminal.LBRACKET)) {
				//array, skip the bounds for now
				while (!nextToken().isTerminal(Terminal.RBRACKET));
				nextToken();
			}
			if (token().isTerminal(Terminal.EQ)) {
				//initial expression, skip for now
				nextToken();
				while (!token().isEndOfStatement() && !token().isTerminal(Terminal.COMMA)) {
					nextToken();
				}
			}
			vars.add(new Variable(access, type, size, var));
			if (token().isTerminal(Terminal.COMMA)) {
				//another variable of same type
				nextToken();
			}
		}
		return vars;
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
