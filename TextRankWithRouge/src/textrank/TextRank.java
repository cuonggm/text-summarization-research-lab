package textrank;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MC48
 */
public class TextRank {
    public static void main(String[] args){
	SummaryTool summary = new SummaryTool("", "", 250);
		summary.init();
		summary.extractSentenceFromContext();
		summary.groupSentencesIntoParagraphs();
//		summary.printSentences();
		summary.createIntersectionMatrix();

		System.out.println("INTERSECTION MATRIX");
		// summary.printIntersectionMatrix();

		summary.createDictionary();
//		summary.printDicationary();

		System.out.println("SUMMMARY");
		summary.createSummary();
		summary.printSummary();
//
		//summary.printStats();
                
//                summary.createIntersectionMatrix();
//                summary.printIntersectionMatrix();
                //summary.printSentenceOrderByScore();

//            String s1 = summary.sentences.get(1).value;
//            String s2 = summary.sentences.get(6).value;
//            Sentence sen1 = new Sentence(0, s1, 0, 0);
//            Sentence sen2 = new Sentence(0, s2, 0, 0);
//            System.out.println("noOfCommonWords = " + summary.noOfCommonWords(sen1, sen2));
//            System.out.println("log10 of noOfWords of Sen 1 = " + Math.log10(sen1.noOfWords));
//            System.out.println("noOfWords of Sen 2 = " + sen2.noOfWords);	
    }
    
    public void summarizeADocument(String fileNameIn, String fileNameOut, int summarySize) {
        SummaryTool summary = new SummaryTool(fileNameIn, fileNameOut, summarySize);
		summary.init();
		System.out.printf("\tExtracting sentences\n");
		summary.extractSentenceFromContext();
		summary.groupSentencesIntoParagraphs();
//		summary.printSentences();
		System.out.printf("\tCreating intersection matrix\n");
		summary.createIntersectionMatrix();

		//System.out.println("INTERSECTION MATRIX");
		//summary.printIntersectionMatrix();

		
		summary.createDictionary();
		//summary.printDicationary();

		// System.out.println("SUMMMARY");
		System.out.println("\tCreating summary");
		summary.createSummary();
		summary.printSummary();
//
		//summary.printStats();
                
//                summary.createIntersectionMatrix();
//                summary.printIntersectionMatrix();
                //summary.printSentenceOrderByScore();

//            String s1 = summary.sentences.get(1).value;
//            String s2 = summary.sentences.get(6).value;
//            Sentence sen1 = new Sentence(0, s1, 0, 0);
//            Sentence sen2 = new Sentence(0, s2, 0, 0);
//            System.out.println("noOfCommonWords = " + summary.noOfCommonWords(sen1, sen2));
//            System.out.println("log10 of noOfWords of Sen 1 = " + Math.log10(sen1.noOfWords));
//            System.out.println("noOfWords of Sen 2 = " + sen2.noOfWords);
    }
}
