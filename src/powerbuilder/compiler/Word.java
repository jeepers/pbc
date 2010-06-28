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
	
	public String getWord() {
		return word;
	}
	
	public String getSrc() {
		return src;
	}
	
	public int compareTo(Word other) {
		return word.compareTo(other.word);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
	
}
