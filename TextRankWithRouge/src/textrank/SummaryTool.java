package textrank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MC48
 */
public class SummaryTool{
        private String fileNameIn;
        private String fileNameOut;
    
        static double d = 0.85;
        
        int summarySize = 250;
        
	FileInputStream in;
	FileOutputStream out;
	ArrayList<Sentence> sentences, contentSummary;
	ArrayList<Paragraph> paragraphs;
	int noOfSentences, noOfParagraphs;

	double[][] intersectionMatrix;
	LinkedHashMap<Sentence,Double> dictionary;


	SummaryTool(String fileNameIn, String fileNameOut, int summarySize){
		in = null;
		out = null;
		noOfSentences = 0;
		noOfParagraphs = 0;
                this.fileNameIn = fileNameIn;
                this.fileNameOut = fileNameOut;
        this.summarySize = summarySize;
	}

	void init(){
		sentences = new ArrayList<Sentence>();
		paragraphs = new ArrayList<Paragraph>();
		contentSummary = new ArrayList<Sentence>();
		dictionary = new LinkedHashMap<Sentence,Double>();
		noOfSentences = 0;
		noOfParagraphs = 0;
		try {
	        in = new FileInputStream(fileNameIn);
	        out = new FileOutputStream(fileNameOut);
    	}catch(FileNotFoundException e){
    	}catch(Exception e){
    	}
	}

	/*Gets the sentences from the entire passage*/
//	void extractSentenceFromContext(){
//		System.out.printf("Chay extract");
//		int nextChar,j=0;
//		int prevChar = -1;
//        try{
//	        while((nextChar = in.read()) != -1) {
//				j=0;
//	        	char[] temp = new char[100000];
//	        	while((char)nextChar != '.'){
//	        		//System.out.println(nextChar + " ");
//	        		temp[j] = (char)nextChar;
//	        		if((nextChar = in.read()) == -1){
//	        			break;
//	        		}
//	        		if((char)nextChar == '\n' && (char)prevChar == '\n'){
//	        			noOfParagraphs++;
//	        		}
//	        		j++;
//	        		prevChar = nextChar;
//	        	}
//                        Sentence sentence = new Sentence(noOfSentences,(new String(temp)).trim(),(new String(temp)).trim().length(),noOfParagraphs);
//	        	sentence.score = 0.5;
//                        dictionary.put(sentence, sentence.score);
//                        sentences.add(sentence);
//                        System.out.printf("sentence.score = %f\n", sentence.score);
//	        	noOfSentences++;
//	        	prevChar = nextChar;
//	        }
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }
//
//	}
        
        void extractSentenceFromContext() {
            FileReader fileReader = null;
            StringBuffer stringBuffer = new StringBuffer();
            try {
                File file = new File(fileNameIn);
                fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }   
                fileReader.close();
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                ex.getMessage();
            } finally {
                try {
                    fileReader.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            String entireText = stringBuffer.toString().trim();
            String[] arrayParagraphs = entireText.split("\n");
            for(int i = 0; i < arrayParagraphs.length; i++) {
                String paragraph = arrayParagraphs[i].trim();
                if(paragraph.isEmpty()) {
                    continue;
                }
                String[] arraySentences = paragraph.split("\\.");
                for(String strSentence : arraySentences) {
                    if(strSentence.isEmpty()) {
                        continue;
                    }
                    Sentence sentence = new Sentence(noOfSentences, strSentence.trim(), strSentence.length(), i);
                    sentence.score = 0.5;
                    dictionary.put(sentence, new Double(sentence.score));
                    // System.out.printf("SENTENCE.SCORE=%f\n", dictionary.get(sentence));
                    sentences.add(sentence);
                    noOfSentences++;
                }
                noOfParagraphs++;
            }
        }

	void groupSentencesIntoParagraphs(){
		int paraNum = 0;
		Paragraph paragraph = new Paragraph(0);

		for(int i=0;i<noOfSentences;i++){
			if(sentences.get(i).paragraphNumber == paraNum){
				//continue
			}else{
				paragraphs.add(paragraph);
				paraNum++;
				paragraph = new Paragraph(paraNum);
				
			}
			paragraph.sentences.add(sentences.get(i));
		}

		paragraphs.add(paragraph);
	}

	double noOfCommonWords(Sentence str1, Sentence str2){
		double commonCount = 0;

		for(String str1Word : str1.value.split("\\s+")){
			for(String str2Word : str2.value.split("\\s+")){
				if(str1Word.compareToIgnoreCase(str2Word) == 0){
					commonCount++;
				}
			}
		}

		return commonCount;
	}
        
        double weight(Sentence s1, Sentence s2) {
            if(noOfCommonWords(s1,s2) == 0) {
                return 0.0;
            }
            return noOfCommonWords(s1,s2) / (double)(Math.log10(s1.noOfWords) + Math.log10(s2.noOfWords));
        }

	void createIntersectionMatrix(){
		intersectionMatrix = new double[noOfSentences][noOfSentences];
		for(int i=0;i<noOfSentences;i++){
			for(int j=0;j<noOfSentences;j++){

				if(i<=j){
					Sentence str1 = sentences.get(i);
					Sentence str2 = sentences.get(j);
                                        // calculate similarity degree as weight
//					intersectionMatrix[i][j] = noOfCommonWords(str1,str2) / ((double)(str1.noOfWords + str2.noOfWords) /2);
                                        intersectionMatrix[i][j] = weight(str1, str2);
				}else{
					intersectionMatrix[i][j] = intersectionMatrix[j][i];
				}
				
			}
		}
	}
        
        double totalEdgeWeightFromVertex(int i) {
            double total = 0.0;
            for(int j=0;j<noOfSentences;j++){
                // ignore weight from a vertex to itself
                if(i == j) {
                    continue;
                }
                // add weight from vertex i to an other vertex of out(i)
                total+=intersectionMatrix[i][j];
            }
            return total;
        }
        
        double totalVertexIn(int i) {
            double total = 0.0;
            for(int j=0; j<noOfSentences; j++) {
                if(i == j) {
                    continue;
                }
                
                Sentence sentenceJ = sentences.get(j);
                double scoreJ = dictionary.get(sentenceJ);
                //System.out.printf("in totalVertexIn | scoreJ = %f\n", scoreJ);
                if(totalEdgeWeightFromVertex(j) == 0.0f) {
                	total += 0;
                } else {
                	total += intersectionMatrix[i][j] * scoreJ / totalEdgeWeightFromVertex(j);
                }
      
//                System.out.printf("intersectionMatrix[%d][%d]=%f|scoreJ=%f|totalEdge=%f|totalVertexIn=%f\n", 
//                		i,
//                		j,
//                		intersectionMatrix[i][j],
//                		scoreJ,
//                		totalEdgeWeightFromVertex(j),
//                		total);
            }
            
            return total;
        }

	void createDictionary(){
		
                for(int k =0; k < 200; k++) {
                    for(int i=0;i<noOfSentences;i++){
			double score = (1-d) + d * totalVertexIn(i);
//			if(!(new Double(score).equals(Double.NaN))) {
//				System.out.printf("calculated score = %f | totalVertextIn = %f\n", score, totalVertexIn(i));
//			} else {
//				System.out.printf("d=%f|totalVertextIn=%f|score=%f\n", d, totalVertexIn(i), score);
//				//System.exit(1);
//			}
			
			dictionary.put(sentences.get(i), score);
			((Sentence)sentences.get(i)).score = score;
                    }
                }
	}

	// ham nay tao noi dung tom tat chia deu giua cac doan
//	void createSummary(){
//
//	      for(int j=0;j<noOfParagraphs;j++){
//	      		int primary_set = paragraphs.get(j).sentences.size()/5; 
//	      		System.out.printf("paragraphis.get(%d).sentences.size()=%d | primary_set = %d\n", j, paragraphs.get(j).sentences.size(), primary_set);
//	      		//Sort based on score (importance)
//	      		Collections.sort(paragraphs.get(j).sentences,new SentenceComparator());
//		      	for(int i=0;i<primary_set;i++){
//		      		contentSummary.add(paragraphs.get(j).sentences.get(i));
//		      	}
//	      }
//
//	      //To ensure proper ordering
//	      Collections.sort(contentSummary,new SentenceComparatorForSummary());
//	}
	
	// ham nay tao tom tat dua tren ranking cau ma ko chia deu giua cac doan
	void createSummary(){
	       
	      Collections.sort(sentences, new SentenceComparator());
	      int target = 0;
	      int sum = 0;
	      for(target=0; target<sentences.size(); target++) {
	    	  int currentSentenceLeng = sentences.get(target).value.split(" ").length;
	    	  sum += currentSentenceLeng;
	    	  if(target >= sentences.size()-1) {
	    		  break;
	    	  }
	    	  if(sum >= summarySize) {
	    		  if(target <= 0) {
	    			  break;
	    		  } else {
	    			  if((sum-summarySize) <= (summarySize-sum+currentSentenceLeng)) {
	    				  break;
	    			  }
	    			  else {
	    				  target-=1;
	    				  break;
	    			  }
	    		  }
	    	  }
	      }
	      
	      for(int i=0;i<=target;i++){
	      		contentSummary.add(sentences.get(i));
	      }
	      
	      //To ensure proper ordering
	      Collections.sort(contentSummary,new SentenceComparatorForSummary());
	}

	void printSentences(){
		for(Sentence sentence : sentences){
			System.out.println(sentence.number + " => " + sentence.value + " => " + sentence.stringLength  + " => " + sentence.noOfWords + " => " + sentence.paragraphNumber);
		}
	}

	void printIntersectionMatrix(){
		for(int i=0;i<noOfSentences;i++){
			for(int j=0;j<noOfSentences;j++){
				System.out.print(intersectionMatrix[i][j] + "    ");
			}
			System.out.print("\n");
		}
	}

	void printDicationary(){
            System.out.println("DICTONARYYYYYYYYYYYYYY");
//		   Get a set of the entries
	      Set<Entry<Sentence, Double>> set = dictionary.entrySet();
	      // Get an iterator
	      Iterator<Entry<Sentence, Double>> i = set.iterator();
	      // Display elements
	      while(i.hasNext()) {
	         Map.Entry me = (Map.Entry)i.next();
	         System.out.print(((Sentence)me.getKey()).value + ": " + " => score: " + me);
	         System.out.println(me.getValue());
	      }
	}

	void printSummary(){
		// System.out.println("no of paragraphs = "+ noOfParagraphs);
		// System.out.printf("no of sentences = %d\n", contentSummary.size());
		for(Sentence sentence : contentSummary){
			System.out.println("\t\t" + sentence.value);
                    try {
                        out.write(sentence.value.getBytes());
                        String dot = ". ";
                        out.write(dot.getBytes());
                    } catch (IOException ex) {
                        System.out.println("Error to write below sentence: " + ex.getMessage());
                        System.out.println(sentence);
                    }
		}
            try {
                out.close();
            } catch (IOException ex) {
                System.out.println("Closing output stream is failed");
            }
           System.out.printf("\tWrote output : %s\n", fileNameOut);
	}

	double getWordCount(ArrayList<Sentence> sentenceList){
		double wordCount = 0.0;
		for(Sentence sentence:sentenceList){
			wordCount +=(sentence.value.split(" ")).length;
		}
		return wordCount;
	}

	void printStats(){
		System.out.println("number of words in Context : " + getWordCount(sentences));
		System.out.println("number of words in Summary : " + getWordCount(contentSummary));
		System.out.println("Commpression : " +getWordCount(contentSummary)/ getWordCount(sentences) );
	}
        
        void printSentenceOrderByScore() {
            System.out.println("RANKINGGGGGGGGG");
            Collections.sort(sentences, new SentenceComparator());
            for(Sentence sentence : sentences) {
                System.out.println(sentence.score + ": " + sentence.value);
            }
        }

}
