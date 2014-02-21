/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.outOfProject.ExtractionOFFeaturesToFile;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.SymbolLabels;
import br.usp.ime.faguilar.cost.GeneralizedShapeContext;
import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.evaluation.ClassifierTest;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.feature_extraction.ShapeContextFeature;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.segmentation.SymbolLabel;
import br.usp.ime.faguilar.util.FilesUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frank
 */
public class NeuralNetworkFeaturesExtractor {
    public final static String UNP_DIRECTORY = "../MathFiles/unp/";
    public final static String SYMBOL_LABELS = UNP_DIRECTORY + "symbol-labels.txt";
    public final static String LIST_OF_SYMBOL_FILE_NAMES = UNP_DIRECTORY + "listUNP_CROHME2013.txt";//"list-unp-test-crohmme2012-files.txt";
    public final static String UNP_SYMBOL_DIRECTORY = UNP_DIRECTORY + "";//"unp-symbols/crohme2012/test/";
    public final static String UNP__LABELED_SYMBOL_DIRECTORY = UNP_DIRECTORY + "CROHME2013/train_validation/";//"unp-labeled-symbols/";
    public static final String FILE_TO_PART = "generalized-shape-contexts.txt";
//            "D:\\frank_temp\\NeededFilesForInterface_learningAnMLP"
//                + "\\train-test\\scontext\\shape-contexts30PointsAllTrainCrohme2012D2.txt";
//"shape-contexts-15points-3x8regions.txt";//UNP_DIRECTORY + "list-unp-files.txt";
    public static final String TRAIN_PART = "D:\\frank_temp\\NeededFilesForInterface_learningAnMLP"
                + "\\train-test\\scontext\\generalized-shape-train.data";//"train.txt";//UNP_DIRECTORY + "train-symbol-file.txt";
    public static final String TEST_PART = "D:\\frank_temp\\NeededFilesForInterface_learningAnMLP"
                + "\\train-test\\scontext\\generalized-shape-validation.data";//"test.txt";//UNP_DIRECTORY + "test-symbol-file.txt";
    
    private static Map<String, Integer> mapLabels;
    
    public static void readLabels(String fileName){
        mapLabels = new HashMap<String, Integer>();
        BufferedReader reader = FilesUtil.getBufferedReader(fileName);
        String label;
        int count = 0;
        try {
            while(reader.ready()){
                label = reader.readLine();
                if(!label.isEmpty()){
                    mapLabels.put(label, count);
                    count++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetworkFeaturesExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void exportFeaturesToAFile(){
        ClassifierTest classifierTest = new ClassifierTest();
        classifierTest.readData();
        readLabels("listSymbols.txt");
        exportShapeContexts(classifierTest.getSymbolData().getMap());
    }

    public static void exportShapeContexts(Map map){
        ArrayList<Classifible> classifibles;
        int classOrder = 0;
        String symbolLabels = "";
        for (Object object : map.keySet()) {
            classifibles = (ArrayList<Classifible>) map.get(object);
            classOrder = mapLabels.get(String.valueOf(classifibles.get(0).getMyClass()));
            extractFeatures(classOrder, classifibles);
            
//            if(!classifibles.isEmpty()){
//                symbolLabels = classifibles.get(0).getMyClass() + " " + classOrder + "\n";
//                FilesUtil.append("symbol-labels.txt", symbolLabels);
//            }
            
//            exportUNPFiles(classOrder, classifibles);
//            classOrder++;
        }
    }
    
    public static void exportUNPFiles(int classOrder, ArrayList<Classifible> classifibles){
        int numberOFSymbol;
        String label = "symbol" + "_" + String.valueOf(classOrder);
        String unpContent;
        String unpFileName;
//        if(!classifibles.isEmpty())
//            label = classifibles.get(0).getSymbol().getLabel();
        for (numberOFSymbol = 0; numberOFSymbol < classifibles.size(); numberOFSymbol++) {
            unpContent = ".COORD\tX Y\n" + ".SEGMENT CHARACTER ? ? \"" + 
               classifibles.get(0).getMyClass() + "\"\n";
            unpContent += extractUNPInfoFromSymbol(classifibles.get(numberOFSymbol).getSymbol());
            unpFileName = label  + "_" + numberOFSymbol + ".unp";
            FilesUtil.append(LIST_OF_SYMBOL_FILE_NAMES, unpFileName + "\n");
            FilesUtil.write(UNP_SYMBOL_DIRECTORY + unpFileName, unpContent);
        }
    }
    
    public static void exportUNPFiles(ArrayList<Classifible> classifibles){
        int classOrder;
        int numberOFSymbol;
        String label;// = "symbol" + "_" + String.valueOf(classOrder);
        String unpContent;
        String unpFileName;
//        if(!classifibles.isEmpty())
//            label = classifibles.get(0).getSymbol().getLabel();
        for (numberOFSymbol = 0; numberOFSymbol < classifibles.size(); numberOFSymbol++) {
            classOrder = SymbolLabels.getIndexOfSymbolByLabel((String) classifibles.get(numberOFSymbol).getMyClass());
            label = "symbol" + "_" + String.valueOf(classOrder);
            unpContent = ".COORD\tX Y\n" + ".SEGMENT CHARACTER ? ? \"" + 
               classifibles.get(numberOFSymbol).getMyClass() + "\"\n";
            unpContent += extractUNPInfoFromSymbol(classifibles.get(numberOFSymbol).getSymbol());
            unpFileName = label  + "_" + numberOFSymbol + ".unp";
            FilesUtil.append(LIST_OF_SYMBOL_FILE_NAMES, unpFileName + "\n");
            FilesUtil.write(UNP_SYMBOL_DIRECTORY + unpFileName, unpContent);
        }
    }
    
    public static String extractUNPInfoFromSymbol(DSymbol symbol){
        String unpInfo = "";
        for (DStroke dStroke : symbol) {
            unpInfo += ".PEN_DOWN" + "\n";
            for (TimePoint timePoint : dStroke) {
                unpInfo += timePoint.getX() + " " + timePoint.getY() + "\n";
            }
            unpInfo += ".PEN_UP" + "\n";
        }
        return unpInfo;
    }
    
    public static void partData() {
        ClassifierTest classifierTest = new ClassifierTest();
        classifierTest.readData();
        int numberOfTrinningSampels = 0;
        ArrayList<Classifible> classifibles = null;
        Map map = classifierTest.getSymbolData().getMap();
        String lines = "";
        BufferedReader bufferedReader = FilesUtil.getBufferedReader(FILE_TO_PART);
        for (Object object : map.keySet()) {
            classifibles = (ArrayList<Classifible>) map.get(object);
            numberOfTrinningSampels = (int) (classifibles.size() * 0.7);
            for (int i = 0; i < numberOfTrinningSampels; i++) {
                try {
                    lines += (bufferedReader.readLine() + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(NeuralNetworkFeaturesExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            FilesUtil.append(TRAIN_PART, lines);
            lines = "";
            for (int i = numberOfTrinningSampels; i < classifibles.size(); i++) {
                try {
                    lines += (bufferedReader.readLine() + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(NeuralNetworkFeaturesExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            FilesUtil.append(TEST_PART, lines);
            lines = "";
        }
    }

    public static String extractFeatures(int order, ArrayList<Classifible>
            classifibles){
        String scontexts = "";
        ShapeContextFeature scontext = null;
        int count = 0;
        for (Classifible classifible : classifibles) {
            if(count >= 100){
                FilesUtil.append("shape-contexts.txt", scontexts);
                scontexts = "";
                count = 0;
            }
            scontext = PreprocessingAlgorithms.getNGeneralizedShapeContetxFeatures(classifible.getSymbol(), 
                    MatchingParameters.numberOfPointPerSymbol);
//                    PreprocessingAlgorithms.getNShapeContetxFeatures(
//                    classifible.getSymbol(), MatchingParameters.numberOfPointPerSymbol);
            scontexts += (order + "\t" +
                    convertMatrixShapeContextToText(scontext.getShapeContext())
                    + "\n");
            count++;
        }
        if(count > 0)
            FilesUtil.append("shape-contexts.txt", scontexts);
        return scontexts;
    }

    public static String convertMatrixShapeContextToText(ShapeContext aShapecontext){
        double[][] scontext = aShapecontext.getSC();
//        double[] scontextOfCentroid = aShapecontext.getCenterShapeContext();
        String stringOfSContext = "";
        for (int i = 0; i < scontext.length; i++) {
            double[] ds = scontext[i];
            for (int j = 0; j < ds.length; j++) {
                stringOfSContext += ((float) ds[j] + "\t");
            }
        }
//        for (int i = 0; i < scontextOfCentroid.length; i++) 
//            stringOfSContext += (scontextOfCentroid[i] + "\t");
        return stringOfSContext;
    }
    
    
    

    public static void putLabelsOnUNPSymbols(){
        BufferedReader bufferUNP;
        ArrayList<String> notHiddenFileNames = FilesUtil.getNotHiddenFileNames(UNP_SYMBOL_DIRECTORY);
        String contentAsString = FilesUtil.getContentAsString(SYMBOL_LABELS);
        String[] labels = extractLabels(contentAsString);
        for (int i = 0; i < notHiddenFileNames.size(); i++) {
            if(!notHiddenFileNames.get(i).isEmpty())
                putLabelInFile(notHiddenFileNames.get(i), labels);            
        }
    }

    private static String[] extractLabels(String contentAsString) {
        String[] lines = contentAsString.split("\n");
        String[] labels = new String[lines.length];
        for (int i = 0; i < lines.length; i++) {
            if(!lines[i].isEmpty())
                labels[i] = lines[i].split(" ")[0];            
        }        
        return labels;
    }

    private static void putLabelInFile(String fileName, String[] labels) {
        int labelPosition = extractPositionFromFileName(fileName);
        String label = labels[labelPosition];
        String newContent = ".COORD\tX Y\n" + ".SEGMENT CHARACTER ? ? \"" + 
               label + "\"\n";
        String curentContent = FilesUtil.getContentAsString(UNP_SYMBOL_DIRECTORY + fileName);
        newContent += curentContent;
        FilesUtil.write(UNP__LABELED_SYMBOL_DIRECTORY + fileName, newContent);
    }

    private static int extractPositionFromFileName(String fileName) {
        return Integer.valueOf(fileName.split("_")[1]);
    }
    
    
    public static void mergeFeatureFiles(String fileName1, String fileName2, 
            String outputFileName){
        BufferedReader reader1 = FilesUtil.getBufferedReader(fileName1);
        BufferedReader reader2 = FilesUtil.getBufferedReader(fileName2);
        String outputLine;
        String line1, line2;
        String[] lines;
        try {
            while(reader1.ready()){
                line1 = reader1.readLine();
                line2 = reader2.readLine();
                line1 = line1.replaceAll("\\s", "\t");
                line2 = line2.replaceAll("\\s", "\t");
                lines = line2.split("\t");
                outputLine = line1 + "\t";
                for (int i = 1; i < lines.length; i++) {
                    outputLine += lines[i] + "\t";                    
                }
                outputLine = outputLine.substring(0, outputLine.length()-1) + "\n";
                FilesUtil.append(outputFileName, outputLine);
            }
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetworkFeaturesExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void extractColumns(String inputFileName, String outputFileName, 
            String caracterSeperator, int[] columns, int numberOfFeatures){
        String currentLine;
        String outPutLine;
        BufferedReader reader = FilesUtil.getBufferedReader(inputFileName);
        String[] numbersInLine;
        try {
            while(reader.ready()){
               currentLine = reader.readLine();
               numbersInLine = currentLine.split(caracterSeperator);
               outPutLine = numbersInLine[0] + "\t";
                for (int i = 0; i < columns.length; i++) {
                    int aColumn = columns[i];
                    int initPosition = 1 + (aColumn -1) * numberOfFeatures;
                    int finalPosition = aColumn * numberOfFeatures;
                    for (int j = initPosition; j <= finalPosition; j++) {
                        outPutLine+= numbersInLine[j] + "\t";           
                    }
                }
               outPutLine +="\n";
               FilesUtil.append(outputFileName, outPutLine);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetworkFeaturesExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
