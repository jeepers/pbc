package powerbuilder.compiler;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class LexerTest extends TestCase {

	public void testLexer() throws IOException {
		String src = "if then ELSE ls_name \"A ~\"string~\"\"";
		System.out.println(src);
		StringReader in = new StringReader(src);
		Lexer lexer = new Lexer(in);
		Token t = lexer.nextToken();
		System.out.println(t);
		assertEquals("if", ((WordToken) t).getWord().getWord());
		assertTrue(((WordToken) t).getWord() instanceof Keyword);
		t = lexer.nextToken();
		System.out.println(t);
		assertTrue(((WordToken) t).getWord() instanceof Keyword);
		t = lexer.nextToken();
		System.out.println(t);
		assertTrue(((WordToken) t).getWord() instanceof Keyword);
		t = lexer.nextToken();
		System.out.println(t);
		assertTrue(((WordToken) t).getWord() instanceof Identifier);
		t = lexer.nextToken();
		System.out.println(t);
		assertTrue(t instanceof StringToken);
		assertEquals("A \"string\"", ((StringToken) t).getString());
	}
	
	public void testMore() throws IOException {
		String src = "c = a + b";
		System.out.println(src);
		StringReader in = new StringReader(src);
		Lexer lexer = new Lexer(in);
		printTokenStream(lexer);
	}
	
	public void testSlashSlash() throws IOException {
		String src = "string ls_str  //a string\r\ninteger li_int\r\n";
		System.out.println(src);
		StringReader in = new StringReader(src);
		Lexer lexer = new Lexer(in);
		printTokenStream(lexer);
	}
	
	public void testSlashStar() throws IOException {
		String src = "/*\r\n * A multi line comment\r\n */\r\nstring ls_str";
		System.out.println(src);
		StringReader in = new StringReader(src);
		Lexer lexer = new Lexer(in);
		printTokenStream(lexer);
	}
	
	public void testNums() throws IOException {
		String src = "1 1.0 1.453 .5 1.3e-5 2010-06-10 20:45:33.333";
		System.out.println(src);
		StringReader in = new StringReader(src);
		Lexer lexer = new Lexer(in);
		printTokenStream(lexer);
	}

	public void testEscapes() throws IOException {
		String src = "'~104~h65~o154lo'";
		System.out.println(src);
		StringReader in = new StringReader(src);
		Lexer lexer = new Lexer(in);
		printTokenStream(lexer);
	}
	
	public void testFile() throws IOException {
		FileReader file = new FileReader("C:\\work\\git-home\\epass_root\\epass\\BackOffice\\epass\\f_date_add.srf");
		Lexer lexer = new Lexer(file);
		printTokenStream(lexer);
	}
	
	public void testContinuation() {
		String src = "String 	is_month[12] = { &\r\n" +
			"\"January\", \"February\", \"March\", \"April\", & \r\n" +
			"\"May\",  \"June\", \"July\", \"August\",  \"September\", &\r\n" +
			"\"October\",  \"November\", \"December\" }\r\n";
		System.out.println(src);
		StringReader in = new StringReader(src);
		Lexer lexer = new Lexer(in);
		printTokenStream(lexer);
	}
	
	private void printTokenStream(Lexer lexer) {
		while (!lexer.isEOF()) {
			System.out.println(lexer.nextToken());
		}
	}
}
