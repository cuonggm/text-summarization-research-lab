package textrank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cuonggm48
 */
public class ExtractSummaries {
    public static final String REFERENCE_DIR = "reference";
    public static final String ORIGINAL_DIR = "original_documents";
    public static final String SYSTEM_DIR = "system";
    public static final String DATA_DIR = "data";
    
    public static int minSumRefSize = 100;
    public static int maxSumRefSize = 200;
    
    private int startIndex;
    private final String fileName;
    private final String fileNameFixed;
    private final File file;
    private final Scanner scanner;
    

    public static void main(String[] argv) {
        if(argv.length != 2) {
        	System.out.println("Use following command:\n\tjava -jar summarize <minRefSumLeng> <maxRefSumLeng>");
        	System.exit(0);
        }
        
        int minSumRefSize = Integer.valueOf(argv[0]).intValue();
        int maxSumRefSize = Integer.valueOf(argv[1]).intValue();
    	
        extractAllFileInFolder(minSumRefSize, maxSumRefSize);
    }
    
    public static void extractAllFileInFolder(int _minSumRefSize, int _maxSumRefSize) {
    	minSumRefSize = _minSumRefSize;
    	maxSumRefSize = _maxSumRefSize;
    	
    	// check if data folder exist
    	File dataFolder = new File(DATA_DIR);
    	if(!dataFolder.exists()) {
    		System.out.println("There is no data folder");
    		dataFolder.mkdir();
    		System.exit(0);
    	}
    	if(!dataFolder.isDirectory()) {
    		System.out.println("data is not directory");
    		dataFolder.delete();
    		dataFolder.mkdir();
    		System.exit(0);
    	}
    	
    	// reset folders
    	File refFolder = new File(REFERENCE_DIR);
    	File originalFolder = new File(ORIGINAL_DIR);
    	File systemFolder = new File(SYSTEM_DIR);
    	
    	if(refFolder.exists()) {
    		if(refFolder.isDirectory()) {
    			String[] entries = refFolder.list();
    			for(String entry : entries) {
    				File f = new File(refFolder.getPath(), entry);
    				f.delete();
    			}
    		}
    		refFolder.delete();
    	}
    	refFolder.mkdir();
    	
    	if(originalFolder.exists()) {
    		if(originalFolder.isDirectory()) {
    			String[] entries = originalFolder.list();
    			for(String entry : entries) {
    				File f = new File(originalFolder.getPath(), entry);
    				f.delete();
    			}
    		}
    		originalFolder.delete();
    	}
    	originalFolder.mkdir();
    	
    	if(systemFolder.exists()) {
    		if(systemFolder.isDirectory()) {
    			String[] entries = systemFolder.list();
    			for(String entry : entries) {
    				File f = new File(systemFolder.getPath(), entry);
    				f.delete();
    			}
    		}
    		systemFolder.delete();
    	}
    	systemFolder.mkdir();
    	
    	// start extract
    	
    	File folder = new File(DATA_DIR);
    	for(final File file : folder.listFiles()) {
    		System.out.printf("Handling file: %s\n", file.getName());
    		try {
                ExtractSummaries tool = new ExtractSummaries(0, file.getName());
                tool.extract();
            } catch (FileNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
    	}
    	
    	System.out.printf("FINISHED SUMMARIZATION\nminSumRefSize=%d | maxSumRefSize=%d\n", minSumRefSize, maxSumRefSize);
    }
    
    public ExtractSummaries(int startIndex, String fileName) throws FileNotFoundException {
        this.startIndex = startIndex;
        this.fileName = fileName;
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<fileName.length(); i++) {
        	if(fileName.charAt(i) == '.') {
        		break;
        	}
        	if(fileName.charAt(i) == '-') {
        		continue;
        	}
        	builder.append(fileName.charAt(i));
        }
        fileNameFixed = builder.toString();
        file = new File(DATA_DIR + "/" + this.fileName);
        scanner = new Scanner(file);
    }
    
    // extract tat ca
//    public void extract() {
//        String content = "";
//        while(scanner.hasNextLine()) {
//            String line = scanner.nextLine();
//            line = line.trim();
//            if(line.isEmpty()) {
//                continue;
//            }
//            if(line.matches("#")) {
//                if(!content.isEmpty()) {
//                    content = content.trim();
//                    createOriginalFile(content);
//                    createSystemSummaries(ORIGINAL_DIR + "/" + String.valueOf(startIndex) + ".txt", SYSTEM_DIR + "/" + String.valueOf(startIndex) + "_textrank.txt");
//                    content = "";
//                }
//                line = scanner.nextLine();
//                line = scanner.nextLine();
//                startIndex++;
//                createReferenceSummaryFile(line);
//            } else {
//                content += "\n";
//                content += line;
//            }
//        }
//        if(!content.isEmpty()) {
//            content = content.trim();
//            createOriginalFile(content);
//            content = "";
//        }
//    }
    
    boolean ignore = false;
    // extract nhung tom tat dai hon 200 tu
    public void extract() {
    	int summarySize = 250;
        String content = "";
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();
            if(line.isEmpty()) {
                continue;
            }
            if(line.matches("#")) {
                if(!content.isEmpty()) {
                    content = content.trim();
                    createOriginalFile(content);
                    createSystemSummaries(ORIGINAL_DIR + "/" + fileNameFixed + String.valueOf(startIndex) + ".txt", SYSTEM_DIR + "/" + fileNameFixed + String.valueOf(startIndex) + "_textrank.txt", summarySize);
                    content = "";
                }
                line = scanner.nextLine();
                line = scanner.nextLine();
                startIndex++;
                String[] sumRefWords = line.split(" ");
                if(sumRefWords.length >= minSumRefSize &&
                		sumRefWords.length < maxSumRefSize) {
                	ignore = false;
                	createReferenceSummaryFile(line);
                	summarySize = line.split(" ").length;
                } else {
                	content = "";
                	ignore = true;
                }
                
            } else {
            	if(ignore==false) {
            		content += "\n";
                    content += line;
            	}
            }
        }
        if(!content.isEmpty()) {
            content = content.trim();
            createOriginalFile(content);
            content = "";
        }
    }
    
    private void createReferenceSummaryFile(String content) {
    	String refFileName = fileNameFixed + String.valueOf(startIndex) + "_ref.txt";
        File newFile = new File(REFERENCE_DIR + "/" + refFileName);
        Writer writer;
        try {
        	System.out.println("\n-----\tCreating Ref Sum: " + refFileName);
            writer = new FileWriter(newFile);
            writer.write(content);
            writer.flush();
            writer.close();
            // System.out.println("Write Ref " + startIndex);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    private void createOriginalFile(String content) {
    	String originalFileName = fileNameFixed + String.valueOf(startIndex) + ".txt";
        File newFile = new File(ORIGINAL_DIR + "/" + originalFileName);
        Writer writer;
        try {
        	System.out.println("\tCreating Original File: " + originalFileName);
            writer = new FileWriter(newFile);
            writer.write(content);
            writer.flush();
            writer.close();
            // System.out.println("Write Original " + startIndex);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    private void createSystemSummaries(String fileNameIn, String fileNameOut, int summarySize) {
        TextRank textRank = new TextRank();
        textRank.summarizeADocument(fileNameIn, fileNameOut, summarySize);
    }
    
}
