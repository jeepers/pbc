package powerbuilder.compiler;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

	private Parser newParser(String src) {
		return new Parser(new Lexer(new StringReader(src)));
	}
	
	private Parser doParse(String src) {
		Parser parser = newParser(src);
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
		assertFalse(v.isArray());
	}
	
	public void testDecimal() {
		String src = "global type w_test from userobject\r\ndecimal{2} id_x\r\nend type\r\n";
		Parser parser = doParse(src);
		Type t = parser.global.getType("w_test");
		assertNotNull(t);
		Variable v = t.getNamespace().getVariable("id_x");
		assertNotNull(v);
		assertEquals("decimal", v.getType());
		assertEquals(2, v.getSize());	
	}
	
	public void testArray() {
		String src = "global type w_test from userobject\r\ninteger iia_arr[5]\r\nend type\r\n";
		Parser parser = doParse(src);
		Type t = parser.global.getType("w_test");
		assertNotNull(t);
		Variable v = t.getNamespace().getVariable("iia_arr");
		assertNotNull(v);
		assertEquals("integer", v.getType());
		assertTrue(v.isArray());
		assertFalse(v.isUnboundedArray());
		List<Bound> bounds = v.getBounds();
		assertNotNull(bounds);
		assertEquals(1, bounds.size());
		assertEquals(0, bounds.get(0).getLower());
		assertEquals(5, bounds.get(0).getUpper());
		assertEquals(6, bounds.get(0).length());
	}

	public void testUnboundedArray() {
		String src = "global type w_test from userobject\r\ninteger iia_arr[]\r\nend type\r\n";
		Parser parser = doParse(src);
		Type t = parser.global.getType("w_test");
		assertNotNull(t);
		Variable v = t.getNamespace().getVariable("iia_arr");
		assertNotNull(v);
		assertEquals("integer", v.getType());
		assertTrue(v.isArray());
		assertTrue(v.isUnboundedArray());
	}

	public void testArrayBounds() {
		String src = "global type w_test from userobject\r\ninteger iia_arr[1 to 10]\r\nend type\r\n";
		Parser parser = doParse(src);
		Type t = parser.global.getType("w_test");
		assertNotNull(t);
		Variable v = t.getNamespace().getVariable("iia_arr");
		assertNotNull(v);
		assertEquals("integer", v.getType());
		assertTrue(v.isArray());
		assertFalse(v.isUnboundedArray());
		List<Bound> bounds = v.getBounds();
		assertNotNull(bounds);
		assertEquals(1, bounds.size());
		assertEquals(1, bounds.get(0).getLower());
		assertEquals(10, bounds.get(0).getUpper());
		assertEquals(10, bounds.get(0).length());
	}
	
	public void testMultiDimArray() {
		String src = "global type w_test from userobject\r\ninteger iia_arr[3,3]\r\nend type\r\n";
		Parser parser = doParse(src);
		Type t = parser.global.getType("w_test");
		assertNotNull(t);
		Variable v = t.getNamespace().getVariable("iia_arr");
		assertNotNull(v);
		assertEquals("integer", v.getType());
		assertTrue(v.isArray());
		assertFalse(v.isUnboundedArray());
		List<Bound> bounds = v.getBounds();
		assertNotNull(bounds);
		assertEquals(2, bounds.size());
		assertEquals(0, bounds.get(0).getLower());
		assertEquals(3, bounds.get(0).getUpper());
		assertEquals(4, bounds.get(0).length());
		assertEquals(0, bounds.get(1).getLower());
		assertEquals(3, bounds.get(1).getUpper());
		assertEquals(4, bounds.get(1).length());
	}

	public void testMultiDimArrayBounds() {
		String src = "global type w_test from userobject\r\n"
			+ "integer iia_arr[1 TO 3, 1 TO 4]\r\n"
			+ "end type\r\n";
		Parser parser = doParse(src);
		Type t = parser.global.getType("w_test");
		assertNotNull(t);
		Variable v = t.getNamespace().getVariable("iia_arr");
		assertNotNull(v);
		assertEquals("integer", v.getType());
		assertTrue(v.isArray());
		assertFalse(v.isUnboundedArray());
		List<Bound> bounds = v.getBounds();
		assertNotNull(bounds);
		assertEquals(2, bounds.size());
		assertEquals(1, bounds.get(0).getLower());
		assertEquals(3, bounds.get(0).getUpper());
		assertEquals(3, bounds.get(0).length());
		assertEquals(1, bounds.get(1).getLower());
		assertEquals(4, bounds.get(1).getUpper());
		assertEquals(4, bounds.get(1).length());
	}
	
	public void testNegativeBounds() {
		String src = "global type w_test from userobject\r\n"
			+ "integer iia_arr[-5 to 5]\r\n"
			+ "end type\r\n";
		Parser parser = doParse(src);
		Type t = parser.global.getType("w_test");
		assertNotNull(t);
		Variable v = t.getNamespace().getVariable("iia_arr");
		assertNotNull(v);
		assertEquals("integer", v.getType());
		assertTrue(v.isArray());
		assertFalse(v.isUnboundedArray());
		List<Bound> bounds = v.getBounds();
		assertNotNull(bounds);
		assertEquals(1, bounds.size());
		assertEquals(-5, bounds.get(0).getLower());
		assertEquals(5, bounds.get(0).getUpper());
		assertEquals(11, bounds.get(0).length());		
	}
	
	public void testFunctionPrototypes() {
		String src = "\r\npublic function string of_getpref (integer ai_preference_id)\r\n"
			+ "public subroutine of_setprefs ()\r\n"
			+ "end prototypes";
		Parser parser = newParser(src);
		List<Function> f = parser.parsePrototypes();
		assertEquals(2, f.size());
		assertEquals("string", f.get(0).returns);
		assertEquals("of_getpref", f.get(0).name);
		assertEquals(1, f.get(0).parameters.size());
		assertEquals("integer", f.get(0).parameters.get(0).getType());
		assertEquals("ai_preference_id", f.get(0).parameters.get(0).getName());
		assertNull(f.get(1).returns);
		assertEquals("of_setprefs", f.get(1).name);
		assertEquals(0, f.get(1).parameters.size());
	}
}
