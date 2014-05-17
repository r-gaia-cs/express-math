/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.classification.ShapeContextClassifier;
import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifier;
import br.usp.ime.faguilar.conversion.InkMLInput;
import br.usp.ime.faguilar.conversion.MathExpressionGraph;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.directories.MathRecognitionFiles;
import br.usp.ime.faguilar.export.InkMLExpression;
import br.usp.ime.faguilar.export.MathExpressionSample;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.graphics.GMathExpression;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.matching.GraphMatching;
import br.usp.ime.faguilar.parser.BSTBuilder;
import br.usp.ime.faguilar.parser.ExpressionCost;
import br.usp.ime.faguilar.parser.crohme2014.ParserCrohme2014;
import br.usp.ime.faguilar.segmentation.HypothesisTree.Partition;
import br.usp.ime.faguilar.segmentation.HypothesisTree.PartitionTreeEvaluator;
import br.usp.ime.faguilar.segmentation.Segmentation;
import br.usp.ime.faguilar.segmentation.SegmentationParameters;
import br.usp.ime.faguilar.segmentation.TreeSearchSegmentation;
import br.usp.ime.faguilar.util.FilesUtil;
import br.usp.ime.faguilar.util.SymbolUtil;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

/**
 *
 * @author frank
 */
public class SegmentationAndClassificationEvaluator {
    private int segmentationAlgorithm;

    private static final int SEGMENTATION_TREE_ALGORITHM = 1;

    private static final int SEGMENTATION_LINEAR_ALGORITHM= 2;

    private  Classifier classifier;

    private boolean filterByDistance;
    private boolean filterByMST;

    private ArrayList<String> inkFiles;

    private String fileNames;
    private SegmentationParameters parameters;
    private Segmentation segmentation;
    
    private String filesToTest;
    private String resultsDir;
    
    private String aFileToTest;
    
    private String outputFile;
    
    private List<ExpressionCost> goodResults;
    private List<ExpressionCost> badResults;
    private final int maxNumberOfCorrectPArsings = 10;
    
    public SegmentationAndClassificationEvaluator(){
        
    }

    public SegmentationAndClassificationEvaluator(String trainingFiles, 
            boolean filterCrohme2012) {
//        ArrayList<Classifible> classifibles = SymbolUtil.readTemplatesFromInkmlFiles(trainingFiles);//SymbolUtil.readTemplatesWithStrokesInfo();//SymbolUtil.readTemplates();
//        to read classifibles saved as an object
        ArrayList<Classifible> classifibles = SymbolUtil.readTemplatesFromObjectFile(trainingFiles);
        if(filterCrohme2012)
            classifibles = filter(classifibles);
        classifier = new ShapeContextClassifier();
        classifier.setTrainingData(classifibles);
        classifibles.clear();
        classifibles = null;
        classifier.train();
        this.segmentationAlgorithm = SEGMENTATION_LINEAR_ALGORITHM;
        filterByDistance = true;
        filterByMST = false;
        fileNames = "fileNames.txt";
        parameters = new SegmentationParameters();
        segmentation = null;

        if(segmentationAlgorithm == SEGMENTATION_TREE_ALGORITHM)
            segmentation =  new TreeSearchSegmentation();
        else if(segmentationAlgorithm == SEGMENTATION_LINEAR_ALGORITHM)
            segmentation =  new Segmentation();
        segmentation.setClassifier(classifier);
        segmentation.setTruncateByDistance(filterByDistance);
        segmentation.setTruncateByMST(filterByMST);
        filesToTest = "";
        resultsDir = "results/";
    }



    public void runEvaluation(){
//        chargeFileNames();
        String resultFileNames = "";
        int count = 0;
        for (String fileName : getInkFiles()) {
            if(fileName.endsWith(".inkml") && !fileName.contains("hirata") &&
                    !fileName.contains("Nina") && !fileName.contains("fujita")){
                count++;
                System.out.println("count: "+ count);
                System.out.println("processing: " + fileName);
                try{
                String resultFileName = partExpressionFromINKMLFile(fileName);
//                resultFileNames += (fileName + ", "+ resultFileName + "\n");
//                FilesUtil.write(getFileNames(), resultFileNames);
                }catch(Exception e){
                    System.out.println("error processing expresion");
                }
                
            }
        }
    }
    
    public void recognizeAExpression(){
        recognizeAndSaveExpressionFromINKMLFile(getaFileToTest());
    }
    
    public void recognizeAExpressionWithNeauralNetworkClassifier(){
//        OLD VERSION
//        GMathExpression expression = (GMathExpression) PartitionTreeEvaluator.getPartitionAsMathExpression(getaFileToTest());
//
//        BSTBuilder parser = new BSTBuilder();
//
//        classifier = new NeuralNetworkClassifier();
//        parser.buildBST(expression, classifier);
//        String labelGraph = parser.labelGraph();
//        FilesUtil.write(getOutputFile(), labelGraph);
//        NEW VERSION
//        ArrayList<ArrayList<DSymbol>> partitions = PartitionTreeEvaluator.getPartitionsAsListsOFsymbolsSORTBYINCREASINGCOST(getaFileToTest());
        List<Partition> partitions = PartitionTreeEvaluator.getPartitionsSORTBYINCREASINGCOST(getaFileToTest());
        BSTBuilder parserBST = new BSTBuilder();
        classifier = new NeuralNetworkClassifier();
        goodResults = new ArrayList<>();
        badResults = new ArrayList<>();
        String latexResult;
        long initialTime = Calendar.getInstance().getTimeInMillis();
        long durationMaxInMiliseconds = 20000;//600000;
        long finalTime;
        int countForTimer = 0;
        int count = 0;
        ExpressionCost newCost;
        int maxPartitions = Math.min(partitions.size(), 200);
        for (Partition  symbopartition : partitions.subList(0, maxPartitions)) {
            parserBST.buildBST(symbopartition.getSymbols(), classifier);
            newCost = new ExpressionCost();
            newCost.setMyPartition(symbopartition);
            newCost.setExpressionRoot(parserBST.getRoot());
//            System.out.println(parserBST.latexResult());
            if(parserBST.isHasParsing()) {
                latexResult = parserBST.latexResult();
    //            System.out.println(latexResult);
//                ParserCrohme2014.saveInTemporaryResult(latexResult);
                ParserCrohme2014.parseLatexString(latexResult);
    //            ParserCrohme2014.parseCurrentResult();
                if(ParserCrohme2014.isCurrentResultGood()){
                    goodResults.add(newCost);
    //                System.out.println(latexResult);
    //                if(goodResults.size() >= maxNumberOfCorrectPArsings)
    //                    break;

    //                String labelGraph = parserBST.labelGraph();
    //                FilesUtil.write(getOutputFile(), labelGraph); 
    //                break;
    //                System.out.println("correct result found at: " + count);
    //                int strokesTotal = 0;
    //                for (DSymbol dSymbol : symbols) {
    //                    strokesTotal += dSymbol.size();
    //                    System.out.println(dSymbol.size() + " ");
    //                }
    //                System.out.println("total: " + strokesTotal);
    //                break;

                }
                else{
                    badResults.add(newCost);
                }
            }
            else
                badResults.add(newCost);
        }
        
//        ParserCrohme2014.readAllOutputs();
//        if(goodResults.isEmpty()){
//            String labelGraph = badResults.get(0).getExpressionRoot().LabelGraphString();
//            FilesUtil.write(getOutputFile(), labelGraph); 
//        }
        
        generateBestResult();
       
    }
    
    public void recognizeExpressions(){
//        chargeFileNames();
        String resultFileNames = "";
        int count = 0;
        for (String fileName : getInkFiles()) {
                count++;
                System.out.println("count: "+ count);
                System.out.println("processing: " + fileName);
                try{
                    String resultFileName = recognizeExpressionFromINKMLFile(fileName);
                    resultFileNames += (fileName + ", "+ resultFileName + "\n");
                    FilesUtil.write(getFileNames(), resultFileNames);
                }catch(Exception e){
                    System.out.println("Error recognizing: " + fileName);
                }                
        }
    }  

    public void chargeFileNames(){
        SymbolUtil.FILES = getFilesToTest();
        String[] files = SymbolUtil.readFilesNamesInAFile();
//        String[] files = SymbolUtil.readFilesNamesInAFile(EvaluationView.TEST_FILES_CROHME);
        inkFiles = new ArrayList<String>();
        inkFiles.addAll(Arrays.asList(files));
//        inkFiles = FilesUtil.getNotHiddenFileNames(EvaluationView.INKML_CROHME_2012_TEST_DIR);
    }
    
    public String  recognizeExpressionFromINKMLFile(String inkFileName){
//        String stringResult = "";
        String resultFileName = "";
        long time;
        InkMLInput inkMlInput = new InkMLInput();
        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(inkFileName);
//        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(EvaluationView.INKML_CROHME_2012_TEST_DIR + inkFileName);
        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
        EdgeWeightedGraph StrokeSetToEdgeWeightedGraph = MathExpressionGraph.StrokeSetToEdgeWeightedGraph(strokes);
        KruskalMST mst = new KruskalMST(StrokeSetToEdgeWeightedGraph);

        double meandistance = 0;
        double filterMaxDistance;
        float alpha = getParameters().getDistanceFactor();
        double beta = 0.5;
        for(Edge e: mst.edges())
            meandistance += e.weight();
        meandistance = meandistance / (StrokeSetToEdgeWeightedGraph.V() - 1);
        filterMaxDistance = alpha * meandistance;
        double mindistance = beta * meandistance;
        segmentation.setMinDist(mindistance);
        segmentation.setMaxDistanceBetweenStrokes(filterMaxDistance);
        segmentation.setMst(mst);
        segmentation.part(strokes);
        GMathExpression expression = (GMathExpression) segmentation.getPartitionAsDMathExpression();
        BSTBuilder parser = new BSTBuilder();
        parser.buildBST(expression, classifier);
        String inkmlTex = parser.labelGraph();
//            count++;
        time = Calendar.getInstance().getTimeInMillis();
        String outputFile = inkFileName.substring(0, inkFileName.length() - 6);
        if(inkFileName.contains("/")){
            int index = outputFile.lastIndexOf("/");
            outputFile = outputFile.substring(index + 1, outputFile.length());
        }
        resultFileName = getResultsDir() + outputFile
                + "-res_"+ String.valueOf(time) +".lg";
        FilesUtil.write(resultFileName, inkmlTex);
//            FilesUtil.write(EvaluationView.INKML_CROHME_2012_TEST_DIR + resultFileName, inkmlTex);
        return resultFileName;
    }
    
    public String  recognizeAndSaveExpressionFromINKMLFile(String inkFileName){
//        String stringResult = "";
        String resultFileName = "";
        long time;
        InkMLInput inkMlInput = new InkMLInput();
        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(inkFileName);
        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
        EdgeWeightedGraph StrokeSetToEdgeWeightedGraph = MathExpressionGraph.StrokeSetToEdgeWeightedGraph(strokes);
        KruskalMST mst = new KruskalMST(StrokeSetToEdgeWeightedGraph);

        double meandistance = 0;
        double filterMaxDistance;
        float alpha = getParameters().getDistanceFactor();
        double beta = 0.5;
        for(Edge e: mst.edges())
            meandistance += e.weight();
        meandistance = meandistance / (StrokeSetToEdgeWeightedGraph.V() - 1);
        filterMaxDistance = alpha * meandistance;
        double mindistance = beta * meandistance;
        segmentation.setMinDist(mindistance);
        segmentation.setMaxDistanceBetweenStrokes(filterMaxDistance);
        segmentation.setMst(mst);
        segmentation.part(strokes);
  
        GMathExpression expression = (GMathExpression) segmentation.getPartitionAsDMathExpression();

        //        TO DO STRUCTURAL ANALISYSfilfileNameeName
        BSTBuilder parser = new BSTBuilder();

        parser.buildBST(expression, classifier);
        String labelGraph = parser.labelGraph();
        FilesUtil.write(getOutputFile(), labelGraph);       
        return resultFileName;
    }

    public String  partExpressionFromINKMLFile(String inkFileName){
//        String stringResult = "";
        String resultFileName = "";
        long time;
        InkMLInput inkMlInput = new InkMLInput();
//        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(EvaluationView.INKML_DIR + inkFileName);
        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(MathRecognitionFiles.INKML_CROHME_2012_TEST_DIR + inkFileName);
//        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(EvaluationView.INKML_CHROME_2013_DIR + inkFileName);
        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
        EdgeWeightedGraph StrokeSetToEdgeWeightedGraph = MathExpressionGraph.StrokeSetToEdgeWeightedGraph(strokes);
        KruskalMST mst = new KruskalMST(StrokeSetToEdgeWeightedGraph);

        double meandistance = 0;
        double filterMaxDistance;
        float alpha = getParameters().getDistanceFactor();
        double beta = 0.5;
        for(Edge e: mst.edges())
            meandistance += e.weight();
        meandistance = meandistance / (StrokeSetToEdgeWeightedGraph.V() - 1);
        filterMaxDistance = alpha * meandistance;
        double mindistance = beta * meandistance;
        segmentation.setMinDist(mindistance);
        segmentation.setMaxDistanceBetweenStrokes(filterMaxDistance);
        segmentation.setMst(mst);
        segmentation.part(strokes);
        
        time = Calendar.getInstance().getTimeInMillis();
        resultFileName = inkFileName.substring(0, inkFileName.length() - 6)
                + "-res_"+ String.valueOf(time) +".inkml";

        GMathExpression expression = (GMathExpression) segmentation.getPartitionAsDMathExpression();

        //        TO DO STRUCTURAL ANALISYSfilfileNameeName
        BSTBuilder parser = new BSTBuilder();

        parser.buildBST(expression, classifier);
        expression = (GMathExpression) parser.symbolsAsExpression();
        String labelGraph = parser.labelGraph();
//        FilesUtil.write(EvaluationView.INKML_CROHME_2012_TEST_DIR + 
//                resultFileName.substring(0, resultFileName.length() - 5) + "lg", labelGraph);
        FilesUtil.write("res/" + inkFileName.substring(0, inkFileName.length() - 5) + "lg", labelGraph);
            
//        end struct analysis
//        MathExpressionSample sample=new MathExpressionSample(
//        "id model",
//        "nickname",expression);
//        sample.setTextualRepresentation(null);
//        sample.setCategory("cat");
//
//        InkMLExpression inkMlExpression = new InkMLExpression();
//        inkMlExpression.setGroundTruthExpression(sample.getTextualRepresentation());
//
//        inkMlExpression.setSampleExpression(sample);
//        inkMlExpression.generateInkML();
//        String inkmlTex = inkMlExpression.getInkmlText();
////            count++;
//
////            FilesUtil.write(EvaluationView.INKML_DIR+ resultFileName, inkmlTex);
////        FilesUtil.write(EvaluationView.INKML_CROHME_2012_TEST_DIR + resultFileName, inkmlTex);
//        FilesUtil.write(EvaluationView.INKML_CHROME_2013_DIR + resultFileName, inkmlTex);
            
            
        return resultFileName;
    }

    public SegmentationParameters getParameters() {
        return parameters;
    }

    public void setParameters(SegmentationParameters parameters) {
        this.parameters = parameters;
        ((ShapeContextClassifier)getClassifier()).setAlpha(parameters.getAlpha());
//        getClassifier().setAlpha(1);
//        GraphMatching.ANGLE_WEIGHT = parameters.getAlpha();
        ((ShapeContextClassifier)getClassifier()).setBeta(parameters.getBeta());
        ((ShapeContextClassifier)getClassifier()).setGama(parameters.getGama());
    }

    public String getResultsDir() {
        return resultsDir;
    }

    public void setResultsDir(String resultsDir) {
        this.resultsDir = resultsDir;
    }

    
    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public int getSegmentationAlgorithm() {
        return segmentationAlgorithm;
    }

    public void setSegmentationAlgorithm(int segmentationAlgorithm) {
        this.segmentationAlgorithm = segmentationAlgorithm;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public boolean isFilterByDistance() {
        return filterByDistance;
    }

    public void setFilterByDistance(boolean filterByDistance) {
        this.filterByDistance = filterByDistance;
    }

    public boolean isFilterByMST() {
        return filterByMST;
    }

    public String getaFileToTest() {
        return aFileToTest;
    }

    public void setaFileToTest(String aFileToTest) {
        this.aFileToTest = aFileToTest;
    }

    public void setFilterByMST(boolean filterByMST) {
        this.filterByMST = filterByMST;
    }

    public ArrayList<String> getInkFiles() {
        return inkFiles;
    }

    public void setInkFiles(ArrayList<String> inkFiles) {
        this.inkFiles = inkFiles;
    }

    public String getFilesToTest() {
        return filesToTest;
    }

    public void setFilesToTest(String filesToTest) {
        this.filesToTest = filesToTest;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    private ArrayList<Classifible> filter(ArrayList<Classifible> classifibles) {
        String content = FilesUtil.getContentAsString("data/listSymbols_crohme2012.txt");
        String[] symbols = content.split("\n");
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void generateBestResult() {
        System.out.println(getOutputFile());
        if(goodResults.isEmpty()){
            System.out.println("only bad results " + getOutputFile());
            generateBestResults(badResults);
        }
        else 
            generateBestResults(goodResults);
//        System.out.println("done");
    }

    private void generateBestResults(List<ExpressionCost> results) {
        if (!results.isEmpty()){
            for (ExpressionCost expressionCost : results) {
                expressionCost.calculateCost();
            }
            Collections.sort(results);
//            System.out.println("good results " + getOutputFile());
//            for (int i = 0; i < Math.min(maxNumberOfCorrectPArsings, results.size()) ; i++) {
//                System.out.println(results.get(i).getExpressionRoot().latexString());
////                System.out.println(results.get(i).getMyPartition());
//            }
            String labelGraph = results.get(0).getExpressionRoot().LabelGraphString();
//            System.out.println(results.get(0).getExpressionRoot().latexString());
            FilesUtil.write(getOutputFile(), labelGraph); 
        }
        
    }
    
}
