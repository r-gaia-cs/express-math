/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser.crohme2014;

import br.usp.ime.faguilar.main.Main;
import br.usp.ime.faguilar.util.ArraysUtil;
import br.usp.ime.faguilar.util.FilesUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Frank
 */
public class ParserCrohme2014 {
//    public static final String PARSER_DIR = "parserCrohme2014/";
    public static final String RECOGNITION_RESULTS_FILE = "temporyRecognitionResultsTest.txt";
    public static final String[] runParserInstruction = {"java", "-jar", "pep.jar", 
        "-g", "gram/GramCROHMEpart4_revision2.xml"
                    , "-s", "S" ,"-v", "0"};
//    {"./tokenAndParse.pl", 
//        "G=GramCROHMEpart4_revision2.xml" , "F=" + RECOGNITION_RESULTS_FILE};
    
    public static final String PARSING_RESULTS_FILE = "temporaryParsingResults.txt";
    private static BufferedReader bufferedReader;
    private static ProcessBuilder pb;
    public static void testParser() {
//        try {
//            //        try {
////            Process p = Runtime.getRuntime().exec("java -jar pep.jar -g gram/GramCROHMEpart4_revision2.xml -s S -v 0 - 2+3");
////            
////            System.out.println();
////        } catch (IOException ex) {
////            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        ProcessBuilder pb = new ProcessBuilder("pep.jar", "-jar", "pep.jar");
////        pb.directory(new File("../"));
////        try {
////            Process p = pb.start();
////            LogStreamReader lsr = new LogStreamReader(p.getInputStream());
////            Thread thread = new Thread(lsr, "LogStreamReader");
////            thread.start();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//            String[] contentAsString = FilesUtil.getContentAsStringArrayList("temporyRecognitionResultsTest.txt");
//            String[] arsa = {"java", "-jar", "pep.jar", "-g", "gram/GramCROHMEpart4_revision2.xml"
//                    , "-s", "S" ,"-v", "0", "2","\\sin","3"};
//            ProcessBuilder theBuilder = new ProcessBuilder(arsa);
////                    "java", "-jar", "pep.jar", "-g", "gram/GramCROHMEpart4_revision2.xml"
////                    , "-s", "S" ,"-v", "0", "2","+","3");
//
////            ProcessBuilder theBuilder = getProcessBuilder();
//            File log = new File(PARSING_RESULTS_FILE);
//            
//            theBuilder.redirectErrorStream(true);
//            theBuilder.redirectOutput(ProcessBuilder.Redirect.to(log));
//            Process p = theBuilder.start();
//            try {
//                p.waitFor();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ParserCrohme2014.class.getName()).log(Level.SEVERE, null, ex);
//            }
////            assert pb.redirectInput() == ProcessBuilder.Redirect.PIPE;
////            assert pb.redirectOutput().file() == log;
////            assert p.getInputStream().read() == -1;
//            
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        ParserCrohme2014.parseLatexString("\\sigma ( 0 ) = 0 1");
        if(isCurrentResultGood())
            System.out.println("good");
    }
    
    public static void parseCurrentResult() {
        try {
//            ProcessBuilder pb = new ProcessBuilder(runParserInstruction);
            ProcessBuilder theBuilder = getProcessBuilder(runParserInstruction);
            File log = new File(PARSING_RESULTS_FILE);
            
            theBuilder.redirectErrorStream(true);
            theBuilder.redirectOutput(ProcessBuilder.Redirect.to(log));
            Process p = theBuilder.start();
            try {
                p.waitFor();
//                System.out.println(p.exitValue());
            } catch (InterruptedException ex) {
                Logger.getLogger(ParserCrohme2014.class.getName()).log(Level.SEVERE, null, ex);
            }
//            assert pb.redirectInput() == ProcessBuilder.Redirect.PIPE;
//            assert pb.redirectOutput().file() == log;
//            assert p.getInputStream().read() == -1;
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public static void parseLatexString(String latex) {
        try {
            ProcessBuilder theBuilder = getProcessBuilderForLatexString(latex);
//            File log = new File(PARSING_RESULTS_FILE);
//            theBuilder.redirectErrorStream(true);
//            theBuilder.redirectOutput(ProcessBuilder.Redirect.to(log));
//            theBuilder.redirectO
            Process p = theBuilder.start();
//            if (bufferedReader == null)
                String line;
            try {
                int waitFor = p.waitFor();
//                System.out.println(waitFor);
                bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                
            } catch (InterruptedException ex) {
                Logger.getLogger(ParserCrohme2014.class.getName()).log(Level.SEVERE, null, ex);
            }

            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public static void readAllOutputs(){
        try {
            while(bufferedReader.ready()){
                    System.out.println(bufferedReader.readLine());

            }
        } catch (IOException ex) {
            Logger.getLogger(ParserCrohme2014.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
        
    public static ProcessBuilder getProcessBuilderForLatexString(String latex){
        String newLatex;
        if(latex.startsWith("-"))
            newLatex = "( " + latex + " )";
        else 
            newLatex = latex;
        String preprocessedLatex = newLatex.trim();
        String[] latexArray = preprocessedLatex.split("\\s+");
        String[] completeInstruction = new String[runParserInstruction.length + 
                latexArray.length];
        System.arraycopy(runParserInstruction, 0, completeInstruction, 0, runParserInstruction.length);
        System.arraycopy(latexArray, 0, completeInstruction, runParserInstruction.length, latexArray.length);
        return getProcessBuilder(completeInstruction);
    }
    
    public static ProcessBuilder getProcessBuilder(String[] instruction){
//        if(pb == null)
            pb = new ProcessBuilder(instruction);
        return pb;
    }
    
    public static void saveInTemporaryResult(String result){
        FilesUtil.write(RECOGNITION_RESULTS_FILE, result);
    }
    
    public static boolean isCurrentResultGood(){
        String line = "";
        try {
            line = bufferedReader.readLine();
//        String contentAsString = FilesUtil.getContentAsString(PARSING_RESULTS_FILE);
//        String[] split = contentAsString.split(":");
//        return split[0].equalsIgnoreCase("ACCEPT");
        } catch (IOException ex) {
            Logger.getLogger(ParserCrohme2014.class.getName()).log(Level.SEVERE, null, ex);
        }
        return line.contains("ACCEPT");
    }
}
