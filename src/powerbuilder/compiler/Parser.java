package powerbuilder.compiler;

import java.util.ArrayList;
import java.util.List;

public class Parser {

	Namespace global;
	Lexer lexer;
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
		String type = token().as(WordToken.class).getWord().getWord();
		nextToken();
		while (!token().isEndOfStatement()) {
			String var = token().getIdentifier();
			nextToken();
			if (token().isTerminal(Terminal.EQ)) {
				//initial expression, skip for now
				nextToken();
				while (!token().isEndOfStatement() && !token().isTerminal(Terminal.COMMA)) {
					nextToken();
				}
			}
			vars.add(new Variable(type, var));
			if (token().isTerminal(Terminal.COMMA)) {
				//another variable of same type
				nextToken();
			}
		}
		return vars;
	}
}
