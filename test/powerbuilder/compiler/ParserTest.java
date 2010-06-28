package powerbuilder.compiler;

import java.io.StringReader;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

	private Parser doParse(String src) {
		Parser parser = new Parser(new Lexer(new StringReader(src)));
		parser.parse();
		return parser;
	}
	
	public void testGlobalType() {
		String src = "global type f_date_add from function_object\r\nend type\r\n";
		Parser parser = doParse(src);
		assertTrue(parser.global.isDefined("f_date_add"));
		Type t = parser.global.getType("f_date_add");
		assertNotNull(t);
	}
	
	public void testWindow() {
		String src = "global type w_version_details from w_response\r\n" +
		"integer width = 2747\r\n" +
		"integer height = 1852\r\n" +
		"string title = \"ePASS Version Details\"\r\n" +
		"boolean ib_disableclosequery = true\r\n" +
		"dw_details dw_details\r\n" +
		"st_effective st_effective\r\n" +
		"st_created st_created\r\n" +
		"st_2 st_2\r\n" +
		"st_errors st_errors\r\n" +
		"mle_errors mle_errors\r\n" +
		"st_warning st_warning\r\n" +
		"mle_warning mle_warning\r\n" +
		"dw_list dw_list\r\n" +
		"mle_description mle_description\r\n" +
		"end type\r\n";
		Parser parser = doParse(src);
		Type t = parser.global.getType("w_version_details");
		assertNotNull(t);
		Variable v = t.getNamespace().getVariable("width");
		assertNotNull(v);
		assertEquals("integer", v.getType());
	}
}
