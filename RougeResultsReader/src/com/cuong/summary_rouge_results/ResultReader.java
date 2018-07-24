package com.cuong.summary_rouge_results;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultReader {
	
	// variables declaration
	static int numberOfTexts = 0;
	static int[] numberInRangesRouge1 = new int[11];
	static int[] numberInRangesRouge2 = new int[11];
	static int[] numberInRangesRouge3 = new int[11];
			
	static double totalRouge1 = 0.0, totalRouge2 = 0.0, totalRouge3 = 0.0;
	static double numberRouge1 = 0, numberRouge2 = 0, numberRouge3 = 0;
	static double[] avg = new double[4];
	
	public static void main(String[] argv) {
		// check app parameter : file name input
		if(argv.length != 2) {
			System.out.println("use command: java -jar read-result.jar <input> <output>");
			System.exit(0);
		}
		
		// init numberInRanges for rouge-1,2,3
		for(int i=0; i<11; i++) {
			numberInRangesRouge1[i] = 0;
			numberInRangesRouge2[i] = 0;
			numberInRangesRouge3[i] = 0;
		}
		
		// get global logger for logging
		Logger logger = Logger.getGlobal();
		
		// get file from filename
		File file = new File(argv[0]);
		// scanner for read lines
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
			// ignore first line
			scanner.nextLine();
			// read lines one by one
			while(scanner.hasNext()) {
				String line = scanner.nextLine();
				// ignore empty line
				if(line.isEmpty()) {
					continue;
				}
				
				String[] tokens = line.split(",");

				// check type of Rouge-?
				if(tokens[0].contains("1")) {
					// +1 for numberOfTexts
					numberOfTexts++;
					// rouge-1
					numberRouge1++;
					totalRouge1 += Double.valueOf(tokens[4]).doubleValue();
					// numberInRanges increment
					double p = Double.valueOf(tokens[4]).doubleValue();
					numberInRangesRouge1[(int)(p/0.1)]+=1.0;
				}
				else if(tokens[0].contains("2")) {
					// rouge-2
					numberRouge2++;
					totalRouge2 += Double.valueOf(tokens[4]).doubleValue();
					// numberInRanges increment
					double p = Double.valueOf(tokens[4]).doubleValue();
					numberInRangesRouge2[(int)(p/0.1)]+=1.0;
				}
				else if(tokens[0].contains("3")) {
					// rouge-3
					numberRouge3++;
					totalRouge3 += Double.valueOf(tokens[4]).doubleValue();
					// numberInRanges increment
					double p = Double.valueOf(tokens[4]).doubleValue();
					numberInRangesRouge3[(int)(p/0.1)]+=1.0;
				}
			}
			
			// calculate avg as rouge-1,2,3
			avg[1] = totalRouge1/numberRouge1;
			avg[2] = totalRouge2/numberRouge2;
			avg[3] = totalRouge3/numberRouge3;
			
			// export summary results to terminal
			printResult(System.out);
			
			PrintStream fileStream = new PrintStream(new FileOutputStream(argv[1]));
			printResult(fileStream);
	
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Can't new scanner: " + e.getMessage());
		} finally {
			if(scanner != null) {
				scanner.close();
			}
		}
		
	}
	
	public static void printResult(PrintStream stream) {
		stream.printf("Number of Texts: %d (texts)\n\n", numberOfTexts);
		
		stream.printf("Avg Precision for Rouge-1: %4.2f%%\n", avg[1] * 100);
		stream.printf("Avg Precision for Rouge-2: %4.2f%%\n", avg[2] * 100);
		stream.printf("Avg Precision for Rouge-3: %4.2f%%\n\n", avg[3] * 100);
	
		stream.println("Number of texts in every 10% range of accuracy");
		stream.printf("%-8s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s\n",
				"",
				"0%", 
				"10%", 
				"20%", 
				"30%", 
				"40%", 
				"50%", 
				"60%", 
				"70%", 
				"80%",
				"90%",
				"100%");
		stream.printf("%-8s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s\n",
				"ROUGE-1",
				String.valueOf(numberInRangesRouge1[0]), 
				String.valueOf(numberInRangesRouge1[1]), 
				String.valueOf(numberInRangesRouge1[2]), 
				String.valueOf(numberInRangesRouge1[3]), 
				String.valueOf(numberInRangesRouge1[4]), 
				String.valueOf(numberInRangesRouge1[5]), 
				String.valueOf(numberInRangesRouge1[6]), 
				String.valueOf(numberInRangesRouge1[7]), 
				String.valueOf(numberInRangesRouge1[8]), 
				String.valueOf(numberInRangesRouge1[9]), 
				String.valueOf(numberInRangesRouge1[10]));
		stream.printf("%-8s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s\n",
				"ROUGE-2",
				String.valueOf(numberInRangesRouge2[0]), 
				String.valueOf(numberInRangesRouge2[1]), 
				String.valueOf(numberInRangesRouge2[2]), 
				String.valueOf(numberInRangesRouge2[3]), 
				String.valueOf(numberInRangesRouge2[4]), 
				String.valueOf(numberInRangesRouge2[5]), 
				String.valueOf(numberInRangesRouge2[6]), 
				String.valueOf(numberInRangesRouge2[7]), 
				String.valueOf(numberInRangesRouge2[8]), 
				String.valueOf(numberInRangesRouge2[9]), 
				String.valueOf(numberInRangesRouge2[10]));
		stream.printf("%-8s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s|%-5s\n",
				"ROUGE-3",
				String.valueOf(numberInRangesRouge3[0]), 
				String.valueOf(numberInRangesRouge3[1]), 
				String.valueOf(numberInRangesRouge3[2]), 
				String.valueOf(numberInRangesRouge3[3]), 
				String.valueOf(numberInRangesRouge3[4]), 
				String.valueOf(numberInRangesRouge3[5]), 
				String.valueOf(numberInRangesRouge3[6]), 
				String.valueOf(numberInRangesRouge3[7]), 
				String.valueOf(numberInRangesRouge3[8]), 
				String.valueOf(numberInRangesRouge3[9]), 
				String.valueOf(numberInRangesRouge3[10]));
	}
	
}
