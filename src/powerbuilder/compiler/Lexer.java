package powerbuilder.compiler;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Pattern;

public class Lexer {

	static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
	static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}:\\d{2}(.\\d{1,6})?");
	final LineNumberReader reader;	
	int line;
	int col;
	char next;
	boolean eof;
	
	public Lexer(Reader reader) {
		if (reader instanceof LineNumberReader) {
			this.reader = (LineNumberReader) reader;
		} else {
			this.reader = new LineNumberReader(reader);
			this.reader.setLineNumber(1);
		}
		header();
	}
	
	private void header() {
		char n = nextChar();
		if (n == '$' || n == 'H') {
			//header line
			readToEOL();
			n = nextChar();
			while (n == '$') {
				readToEOL();
			}
		}
		next = n;		
	}
	
	private char nextNonWSChar() {
		char c;
		theloop:
		while (Character.isWhitespace((c = nextChar()))) {
			switch (c) {
			case '\r':
			case '\n':
				break theloop;
			default:
			}
		}
		return c;
	}

	private char nextChar() {
		if (next != 0) {
			char tmp = next;
			next = 0;
			return tmp;
		}
		int i;
		try {
			i = reader.read();
		} catch (IOException e) {
			throw new LexerException(e);
		}
		if (i == -1) {
			eof = true;
		}
		if (reader.getLineNumber() != line) {
			line = reader.getLineNumber();
			col = 0;
		}
		col++;
		return (char) i;
	}
	
	public boolean isEOF() {
		return eof;
	}
	
	private void readToEOL() {
		boolean cr = false;
		boolean nl = false;
		do {
			char n = nextChar();
			if (n == '\r') {
				cr = true;
			} else if (n == '\n') {
				nl = true;
			} else {
				if (nl | cr) {
					next = n;
					return;
				}
			}
		} while (true);		
	}
	
	public Token nextToken() {
		char c = nextNonWSChar();
		if (c == 0) {
			return null;
		}
		int sl = line;
		int sc = col;
		if (c == '\r' || c == '\n') {
			//new line
			c = nextChar();
			if (c != '\r' && c != '\n') {
				next = c;
			}
			return new TerminalToken(Terminal.EOL, sl, sc);
		} else if (c == '\'') {
			//single quoted string
			return new StringToken(readString('\''), sl, sc);
		} else if (c ==  '"') {
			//double quoted string
			return new StringToken(readString('"'), sl, sc);
		} else if (c == '/') {
			//possible start of comment, otherwise division operator
			char n = nextChar();
			if (n == '/') {
				//single line comment
				readToEOL();
				return nextToken();
			} else if (n == '*') {
				//multi line comment
				boolean star = true;
				do {
					n = nextChar();
					if (star) {
						if (n == '/') {
							return nextToken();
						} else {
							star = false;
						}
					} else if (n == '*') {
						star = true;
					}
				} while (true);
			} else {
				//not a comment
				next = n;
				return new TerminalToken(Terminal.DIV, sl, sc);
			}
		} else if ("0123456789.".indexOf(c) >= 0) {
			StringBuilder sb = new StringBuilder();
			if (c == '.') {
				c = nextChar();
				if (!Character.isDigit(c)) {
					next = c;
					return new TerminalToken(Terminal.DOT, sl, sc);
				} else {
					sb.append('.');
				}
			}
			//numeric literal, possibly date or time
			sb.append(c);
			do {
				c = nextChar();
				if ("0123456789+-eE.:".indexOf(c) >= 0) {
					sb.append(c);
				} else {
					next = c;
					if (DATE_PATTERN.matcher(sb).matches()) {
						return new DateToken(sb.toString(), sl, sc);
					} else if (TIME_PATTERN.matcher(sb).matches()) {
						return new TimeToken(sb.toString(), sl, sc);
					} else {
						return new NumberToken(sb.toString(), sl, sc);
					}
				}
			} while (true);
		} else if (c == '_' || Character.isLetter(c)) {
			//identifier or keyword
			String id = readIdentifier(c);
			if (Keyword.isKeyword(id)) {
				return new WordToken(Keyword.get(id), sl, sc);
			} else {
				return new WordToken(new Identifier(id), sl, sc);
			}
		} else {
			if (c == ';') {
				return new TerminalToken(Terminal.SEMI, sl, sc);
			} else if (c == '-') {
				return new TerminalToken(Terminal.SUB, sl, sc);
			} else if (c == '+') {
				return new TerminalToken(Terminal.ADD, sl, sc);
			} else if (c == '*') {
				return new TerminalToken(Terminal.MULT, sl, sc);
			} else if (c == '/') {
				return new TerminalToken(Terminal.DIV, sl, sc);
			} else if (c == '^') {
				return new TerminalToken(Terminal.EXP, sl, sc);
			} else if (c == '=') {
				return new TerminalToken(Terminal.EQ, sl, sc);
			} else if (c == '(') {
				return new TerminalToken(Terminal.LPAREN, sl, sc);
			} else if (c == ')') {
				return new TerminalToken(Terminal.RPAREN, sl, sc);
			} else if (c == '[') {
				return new TerminalToken(Terminal.LBRACKET, sl, sc);
			} else if (c == ']') {
				return new TerminalToken(Terminal.RBRACKET, sl, sc);
			} else if (c == '{') {
				return new TerminalToken(Terminal.LBRACE, sl, sc);
			} else if (c == '}') {
				return new TerminalToken(Terminal.RBRACE, sl, sc);
			} else if (c == ',') {
				return new TerminalToken(Terminal.COMMA, sl, sc);
			} else if (c == '.') {
				return new TerminalToken(Terminal.DOT, sl, sc);
			} else if (c == '>') {
				char n = nextChar();
				if (n == '=') {
					return new TerminalToken(Terminal.GEQ, sl, sc);
				} else {
					next = n;
					return new TerminalToken(Terminal.GT, sl, sc);
				}
			} else if (c == '<') {
				char n = nextChar();
				if (n == '=') {
					return new TerminalToken(Terminal.LEQ, sl, sc);
				} else if (n == '>') {
					return new TerminalToken(Terminal.NEQ, sl, sc);
				} else {
					next = n;
					return new TerminalToken(Terminal.LT, sl, sc);
				}
			} else if (c == '!') {
				return new TerminalToken(Terminal.BANG, sl, sc);
			} else if (c == ':') {
				return new TerminalToken(Terminal.COLON, sl, sc);
			} else if (c == '&') {
				//suppress next EOL and return next token
				readToEOL();
				return nextToken();
			}
		}
		return null;
	}
	
	private String readString(char quote) {
		StringBuilder sb = new StringBuilder();
		boolean esc = false;
		do {
			char c = nextChar();
			if (esc) {
				switch (c) {
				case 'n':
					sb.append('\n');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'v':
					sb.append('\u000b');
					break;
				case 'h':
					//2 hex digits
					char h1 = nextChar();
					char h2 = nextChar();
					char hx = (char) (Character.digit(h1, 16) << 4 | Character.digit(h2, 16));
					sb.append(hx);
					break;
				case 'o':
					//3 octal digits
					char o1 = nextChar();
					char o2 = nextChar();
					char o3 = nextChar();
					char ox = (char) (Character.digit(o1, 8) << 6 | Character.digit(o2, 8) << 3 | Character.digit(o3, 8));
					sb.append(ox);
					break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					//3 decimal digits
					char d1 = c;
					char d2 = nextChar();
					char d3 = nextChar();
					char dx = (char) (Character.digit(d1, 10) * 100  + Character.digit(d2, 10) * 10 + Character.digit(d3, 10));
					sb.append(dx);
					break;
				default:
					sb.append(c);
					break;
				}
				esc = false;
			} else {
				if (c == quote) {
					return sb.toString();
				} else if (c == '~') {
					esc = true;
				} else {
					sb.append(c);
				}
			}
			
		} while (true);
	}
	
	private String readIdentifier(char c) {
		StringBuilder sb = new StringBuilder();
		sb.append(c);
		do {
			c = nextChar();
			if (Character.isLetterOrDigit(c) || "-_$#%".indexOf(c) >= 0) {
				sb.append(c);
			} else {
				next = c;
				return sb.toString();
			}
		} while (true);		
	}
}
