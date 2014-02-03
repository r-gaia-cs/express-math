/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.conversion.InkmlMathExpression;
import br.usp.ime.faguilar.conversion.InkmlReader;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.directories.MathRecognitionFiles;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.feature_extraction.ShapeContextFeature;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.matching.symbol_matching.UserSymbol;
import br.usp.ime.faguilar.segmentation.SymbolFeatures;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frank
 */
public class SymbolUtil {
    
    

//    public static ArrayList<Classifible> readSymbolData(String fileName){
//        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
//        String fileContent = FilesUtil.getContentAsString(fileName);
////        String[] arrayContent = fileContent.split("\n");
//        Scanner scanner = new Scanner(fileContent);
////        for (int i = 0; i < arrayContent.length; i++) {
////            String string = arrayContent[i];
////
////        }
//        String label = "";
//        Point2D.Double[] points = null;
//        Point2D.Double newPoint = null;
////        int dataIndex = 0;
//        while(scanner.hasNext()){
////            for (Classifible classifible : symbolData) {
//            if(scanner.hasNextDouble()){
//                points = new Point2D.Double[MatchingParameters.numberOfPointPerSymbol];
//                for (int i = 0; i < MatchingParameters.numberOfPointPerSymbol; i++) {
//                    newPoint = new Point2D.Double(scanner.nextDouble(),
//                            scanner.nextDouble());
//                    points[i] = newPoint;
//                }
//                label = scanner.next();
//                Classifible<Point2D.Double> newClassifible = new Classifible();
//                newClassifible.setFeatures(points);
//                newClassifible.setMyClass(label);
//                symbolData.add(newClassifible);
//            }else{
//                if(scanner.next("#").equals("#"))
//                    scanner.nextLine();
//            }
////                dataIndex++;
////            }
//        }
//        return symbolData;
//        
////        public static boolean areCrossing(){
////
////        }
//    }


    public static double MIN_WIDTH = Double.MAX_VALUE;
    public static double COUNT = 0;
    
    public static String DIR_COMPLEMENT = MathRecognitionFiles.INKML_DIR;

    public static String FILES_FOLDER = "";
    public static String FILES = "";

    public static ArrayList<Classifible> readSymbolData(String fileName){
        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        String fileContent = FilesUtil.getContentAsString(fileName);
        String[] arrayContent = fileContent.split("\n");
        String label = "";
        Point2D.Double[] points = null;
        Point2D.Double newPoint = null;
        String[] line;
        int j;
        for (int i = 0; i < arrayContent.length; i++) {
            line = arrayContent[i].split(" ");
            if(!line[0].equals("#")){
                points = new Point2D.Double[MatchingParameters.numberOfPointPerSymbol];
                for (j = 0; j < MatchingParameters.numberOfPointPerSymbol; j++) {
                    newPoint = new Point2D.Double(Double.valueOf(line[2*j]), 
                            Double.valueOf(line[2 * j + 1]));
                    points[j] = newPoint;
                }
                label = line[2 * j];
                Classifible<Point2D.Double> newClassifible = new Classifible();
                newClassifible.setFeatures(points);
                newClassifible.setMyClass(label);
                symbolData.add(newClassifible);
            }
        }
        return symbolData;
    }
    
    public static boolean intersect(DStroke s1, DStroke s2){
        for (int i = 0; i < s1.size() - 1; i++) {
            for (int j = 0; j < s2.size() -1; j++) {
                if(Line2D.linesIntersect(s1.get(i).getX(), s1.get(i).getY(), 
                        s1.get(i + 1).getX(), s1.get(i + 1).getY(), s2.get(j).getX(), s2.get(j).getY(), 
                        s2.get(j + 1).getX(), s2.get(j + 1).getY())){
                    return true;
                }
            }
        }
        return false;
    }
    
    public static double minDistanceBetweenStrokeCentroids(DSymbol symbol){
        double minDistance = Double.MAX_VALUE;
        double distance;
        for (int i = 0; i < symbol.size() - 1; i++) {
            for (int j = i + 1; j < symbol.size(); j++) {
                distance = symbol.get(i).getBoundingBoxCenter().distance(symbol.get(j).getBoundingBoxCenter());
                if(distance < minDistance)
                    minDistance = distance;
            }
        }
        return minDistance;
    }
    
    public static double minNormalizedDistanceBetweenStrokeCentroids(DSymbol symbol){
        double maxDimension = Math.max(symbol.getWidthAsDouble(), symbol.getHeightAsDouble());
        double normalizerTerm = Math.sqrt(2) * maxDimension;
        if(normalizerTerm == 0)
            return -1;
        double minDistance = minDistanceBetweenStrokeCentroids(symbol);
        if(minDistance == Double.MAX_VALUE)
            return -1;
        return minDistance / normalizerTerm;
    }

    public static ArrayList<Classifible> readSymbolDataWithStrokesInfo(String fileName){
        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        String fileContent = FilesUtil.getContentAsString(fileName);
        String[] arrayContent = fileContent.split("\n");
        String label = "";
        String[] fileNameArray = fileName.split("_");
        String userName = fileNameArray[1].substring(0, fileNameArray[1].length() - 4);
        Point2D.Double[] points = null;
        Point2D.Double newPoint = null;

        String[] line;
        int j;
        int numberOfStrokes;
        SymbolFeatures symbolFeatures;
        for (int i = 0; i < arrayContent.length; i++) {
            line = arrayContent[i].split(" ");
            if(!line[0].equals("#")){
                numberOfStrokes = Integer.valueOf(line[0]);
                symbolFeatures = new SymbolFeatures();
                symbolFeatures.setNumberOfStrokes(numberOfStrokes);
                points = new Point2D.Double[MatchingParameters.numberOfPointPerSymbol];

                for (j = 0 ; j < MatchingParameters.numberOfPointPerSymbol; j++) {
                    newPoint = new Point2D.Double(Double.valueOf(line[2 * j + ((numberOfStrokes * 4) +1)]),
                            Double.valueOf(line[2 * j + 1 + ((numberOfStrokes * 4) +1)]));
                    points[j] = newPoint;
                }
                label = line[2 * j + ((numberOfStrokes * 4) +1)];
                Classifible<Point2D.Double> newClassifible = new Classifible();
                newClassifible.setAditionalFeatures(symbolFeatures);
                newClassifible.setUserSymbol(UserSymbol.newInstanceFromUserNickNameAndSymbolLabel(
                        userName, label));
                newClassifible.setFeatures(points);
                newClassifible.setMyClass(label);
                symbolData.add(newClassifible);
            }
        }
        return symbolData;
    }

    public static ArrayList<Classifible> readSymbolData(String[] fileNames){
        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        ArrayList<Classifible> symbolsOfAFile;
        for (String aFileName: fileNames) {
            symbolsOfAFile = readSymbolData(MathRecognitionFiles.TEMPLATES_PER_EXPRESSION_DIR+aFileName);
            symbolData.addAll(symbolsOfAFile);
        }
        return symbolData;
    }

    public static ArrayList<Classifible> readSymbolDataWithStrokesInfo(String[] fileNames){
        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        ArrayList<Classifible> symbolsOfAFile;
        for (String aFileName: fileNames) {
            if(!aFileName.contains("hirata") && !aFileName.contains("Nina")
                    && !aFileName.contains("fujita")){
                symbolsOfAFile = readSymbolDataWithStrokesInfo(MathRecognitionFiles.TEMPLATES_PER_EXPRESSION_DIR+aFileName);
                symbolData.addAll(symbolsOfAFile);
            }
        }
        return symbolData;
    }

    public static String[] readAndTransforToInkmlFilesNamesInAFile(String file){
        String fileContesnt = FilesUtil.getContentAsString(file);
        String[] fileNames = fileContesnt.split("\n");
        for (int i = 0; i < fileNames.length; i++) {
                fileNames[i] = fileNames[i].substring(0, fileNames[i].length() - 6);
                fileNames[i] += ".txt";
        }
        return fileNames;
    }

    public static String[] readFilesNamesInAFile(){
        String fileContesnt = FilesUtil.getContentAsString(FILES);
        String[] fileNames = fileContesnt.split("\n");
        return fileNames;
    }

    public static ArrayList<Classifible> readTemplates(){
        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        String[] fileNames = readAndTransforToInkmlFilesNamesInAFile(MathRecognitionFiles.TRAINING_FILES);
        symbolData = readSymbolData(fileNames);
        return symbolData;
    }

    public static ArrayList<Classifible> readTemplatesFromInkmlFiles(String filesToRead, String folder){
        FILES = filesToRead;
        FILES_FOLDER = folder;

        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
//        String[] fileNames = readFilesNamesInAFile(EvaluationView.INKML_CROHME_2013_TRAIN_FILES);
        String[] fileNames = readFilesNamesInAFile();
//        String[] fileNames = readFilesNamesInAFile(MathRecognitionFiles.TEST_FILES_CROHME); //readFilesNamesInAFile(MathRecognitionFiles.TRAINING_FILES);
//        String[] fileNames = readFilesNamesInAFile(EvaluationView.TRAINING_FILES_CROHME);

//        ArrayList<String> notHiddenFileNames = FilesUtil.getNotHiddenFileNames(EvaluationView.INKML_CROHME_2012_TRAIN_DIR);
//        String[] fileNames = new String[notHiddenFileNames.size()];
//        for (int i = 0; i < notHiddenFileNames.size(); i++) {
//            fileNames[i] = notHiddenFileNames.get(i);
//        }
        symbolData = readClassifiblesFromInkmlFiles(fileNames);
        return symbolData;
    }
    
    public static ArrayList<Classifible> readTemplatesFromInkmlFiles(String files){
        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        FILES  = files;
        String[] fileNames = readFilesNamesInAFile();
//        String[] fileNames = readFilesNamesInAFile(EvaluationView.TRAINING_FILES_CROHME);

//        ArrayList<String> notHiddenFileNames = FilesUtil.getNotHiddenFileNames(EvaluationView.INKML_CROHME_2012_TRAIN_DIR);
//        String[] fileNames = new String[notHiddenFileNames.size()];
//        for (int i = 0; i < notHiddenFileNames.size(); i++) {
//            fileNames[i] = notHiddenFileNames.get(i);
//        }
        symbolData = readClassifiblesFromInkmlFiles(fileNames);
        return symbolData;
    }

    private static ArrayList<Classifible> readClassifiblesFromInkmlFiles(String[] fileNames) {
        ArrayList<Classifible> classifibles = new ArrayList<Classifible>();
        ArrayList<Classifible> symbolsOfAFile;
        for (String aFileName: fileNames) {
            if(aFileName.contains(".inkml") && !aFileName.contains("hirata") && !aFileName.contains("Nina")
                    && !aFileName.contains("fujita")){
//                symbolsOfAFile = readInkmlClassifibles(DIR_COMPLEMENT+ aFileName);
                symbolsOfAFile = readInkmlClassifibles(FILES_FOLDER + aFileName);
//readInkmlClassifibles(MathRecognitionFiles.INKML_CROHME_2012_TRAIN_DIR + aFileName);
                
//                symbolsOfAFile = readInkmlClassifibles(EvaluationView.INKML_CHROME_2013_DIR + aFileName);
                classifibles.addAll(symbolsOfAFile);
            }
        }
//        System.out.println("min width of minus: " + MIN_WIDTH);
        return classifibles;
    }

    public static ArrayList<Classifible> readTemplatesFromObjectFile(String file){
        ReadObjectInFile reader = new ReadObjectInFile();
        reader.setFileName(file);
        reader.openFile();
        ArrayList<Classifible> readRecords = (ArrayList<Classifible>) reader.readRecords();
        return readRecords;
    }
    
    private static ArrayList<Classifible> readInkmlClassifibles(String fileName){

        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        InkmlReader reader = new InkmlReader();
        reader.read(fileName);
        DMathExpression mathExpression = reader.getMathExpression().asDMathExpression();
        DSymbol newSymbol;
        Classifible newClassifible;
//        String[] fileNameArray = fileName.split("_");
//        String userName = fileNameArray[1].substring(0, fileNameArray[1].length() - 4);
        String userName = reader.getMathExpression().getWriter();
        for (DSymbol symbol : mathExpression) {
            newSymbol = PreprocessingAlgorithms.preprocessDSymbolWithOrderedStrokes(symbol);
            newClassifible = new Classifible();
            newClassifible.setUserSymbol(UserSymbol.newInstanceFromUserNickNameAndSymbolLabel(
                    userName, newSymbol.getLabel()));
            newClassifible.setSymbol(newSymbol);
            newClassifible.setMyClass(newSymbol.getLabel());
//            ShapeContextFeature inputFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(newSymbol, 
//                MatchingParameters.numberOfPointPerSymbol);

//            TO CALCULATE NGENERALIZED SHAPE CONTEXTS
            ShapeContextFeature inputFeatures = PreprocessingAlgorithms.getNGeneralizedShapeContetxFeatures(newSymbol,
                MatchingParameters.numberOfPointPerSymbol);
            newClassifible.setAditionalFeatures(inputFeatures);
//
//            END- TO CALCULATE NGENERALIZED SHAPE CONTEXTS

            symbolData.add(newClassifible);
//            if(symbol.getWidthAsDouble() < 0)
//                System.out.println("less than zero");

            // TO LOCATE MAX NUMBER OF STROKES PER SYMBOL
//            if(newSymbol.size()>=4){
//                System.out.println(newSymbol.getLabel() + " " + fileName);
//                COUNT++;
//                System.out.println("count: " + COUNT);
//            }

            // TO CALCULATE MIN WIDTH OF MINUS
//            if(newSymbol.getLabel().equals("-")){
//                System.out.println("a minus in " + fileName + " " + newSymbol.getWidthAsDouble());
//                if(newSymbol.getWidthAsDouble() < MIN_WIDTH)
//                    MIN_WIDTH = newSymbol.getWidthAsDouble();
//            }
        }
        return symbolData;
    }

    public static ArrayList<Classifible> readTemplatesWithStrokesInfo(){
        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        String[] fileNames = readAndTransforToInkmlFilesNamesInAFile(MathRecognitionFiles.TRAINING_FILES);
        symbolData = readSymbolDataWithStrokesInfo(fileNames);
        return symbolData;
    }

    public static void partSymbolsInSeveralFiles(String fileName){
        BufferedReader bufferedReader = FilesUtil.getBufferedReader(fileName);
        String[] lineArray;
        String line;
        String content = "";
        String name = "";
        String model = null;
        int count = 1;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("#")) {
                    content += (line + "\n");
                } else {
                    if (!name.isEmpty()) {
                        FilesUtil.write("data/templates/symbols/" + name, content);
                        content = "";
//                        System.out.println(count);
//                        count++;
                    }
                    lineArray = line.split(" ");
                    if(line.contains("model")){
                        model = lineArray[2];
                        line = bufferedReader.readLine();
                    }
                    lineArray = line.split(" ");
                    name = model + ("_" + lineArray[2] + ".txt");
                }
            }
            if (!name.isEmpty()) {
                        FilesUtil.write("data/templates/symbols/" + name, content);
                        content = "";
//                        System.out.println(count);
//                        count++;
            }
        } catch (IOException ex) {
            Logger.getLogger(SymbolUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean nearEndsToStrokeFromTo(DStroke s1, DStroke s2,double minDistance){
        boolean nearEnds = false;
        
        if(s1.size() >1){
            for (Point2D point : s2) {
                if(s1.get(0).distance(point) < minDistance ||
                        s1.get(s1.size() -1).distance(point) < minDistance){
                    nearEnds = true;
                    break;
                }
            }
        }
         else if(s1.size() == 1){
            for (Point2D point : s2) {
                    if(s1.get(0).distance(point) < minDistance){
                        nearEnds = true;
                        break;
                    }
            }
         }
        return nearEnds;
    }
    public static boolean nearEnds(DStroke s1, DStroke s2){
        boolean nearEnds = false;
        double minDistance = 5.;
        if(nearEndsToStrokeFromTo(s1, s2, minDistance) ||
                nearEndsToStrokeFromTo(s2, s1, minDistance))
            nearEnds = true;
        return nearEnds;
    }

    public static boolean nearEnds(DStroke s1, DStroke s2, double minDistance){
        boolean nearEnds = false;
        if(nearEndsToStrokeFromTo(s1, s2, minDistance) ||
                nearEndsToStrokeFromTo(s2, s1, minDistance))
            nearEnds = true;
        return nearEnds;
    }
}
