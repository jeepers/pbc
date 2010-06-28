package powerbuilder.compiler;

public class WordToken extends Token {

	final Word word;
	
	public WordToken(Word word, int l, int c) {
		super(l, c);
		this.word = word;
	}
	
	public Word getWord() {
		return word;
	}

	public String toString() {
		return super.toString() + ((word instanceof Keyword) ? "[keyword]" : "[identifier]") + word;
	}
	
	public boolean isKeyword() {
		return word instanceof Keyword;
	}
	
	public boolean isKeyword(Keyword kw) {
		return isKeyword() && is(kw);
	}
	
	public boolean is(Keyword kw) {
		return isKeyword() && kw.equals(word);
	}
	
	public boolean isIdentifier() {
		return !isKeyword();
	}
	
	public String getIdentifier() {
		return word.getWord();
	}
}
