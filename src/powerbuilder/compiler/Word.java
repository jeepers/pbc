package powerbuilder.compiler;

public class Word implements Comparable<Word> {
	
	final String src;
	final String word;

	public Word(String str) {
		src = str;
		word = str.toLowerCase();
	}
	
	public String toString() {
		return src;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Word) {
			return word.equals(((Word) other).word);
		}
		return false;
	}

	public int compareTo(Word other) {
		return word.compareTo(other.word);
	}
	
	public int hashCode() {
		return word.hashCode();
	}
}
