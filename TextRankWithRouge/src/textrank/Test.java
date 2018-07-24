package textrank;

public class Test {

	public static void main(String[] args) {
		TextRank textRank = new TextRank();
		textRank.summarizeADocument("original_documents/1.txt", "output.txt", 250);
	}

}
