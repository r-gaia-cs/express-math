/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.conversion.InkMLInput;
import br.usp.ime.faguilar.conversion.InkmlReader;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.directories.MathRecognitionFiles;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import static br.usp.ime.faguilar.segmentation.HypothesisTree.SymbolHypothesis.BINARY_REPRESENTATION_LENGHT;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import br.usp.ime.faguilar.util.FilesUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank Aguilar
 */
public class PartitionTreeEvaluator {
    private static int correctSegmentations;
    private static int correctLabels;
    
    public static void testSymbolHypothesis(){
        InkmlReader reader = new InkmlReader();
        String fileContent = FilesUtil.getContentAsString(
                MathRecognitionFiles.INKML_CROHME_2013_TEST_FILES);
        String[] filesToTest = fileContent.split("\n");
        DMathExpression mathExpression;
//        InkMLInput inkMlInput = new InkMLInput();
        
        PartitionTreeGenerator partitionGenerator = new PartitionTreeGenerator();
        HypothesisGenerator hypothesisGenerator = new NearestNeighborGraphHypothesisGenerator(); 
        ClassificationFilter filter = new ClassificationFilter();
        ((NearestNeighborGraphHypothesisGenerator) hypothesisGenerator).setFilter(filter);
        partitionGenerator.setHypothesisGenerator(hypothesisGenerator);
        
        
        int totalCorrectSegmentations = 0;
        int totalSymbols = 0;
        int totalCorrectLbels = 0;
        for (String aFile : filesToTest) {
            correctSegmentations = 0;
            correctLabels = 0;
            reader.read(MathRecognitionFiles.INKML_CROHME_2013_TEST_DIR + aFile);
            mathExpression = reader.getMathExpression().asDMathExpression();
//            ArrayList Tempstrokes = DMathExpression.extractStrokes(mathExpression);
            mathExpression = PreprocessingAlgorithms.preprocessDMathExpressionWithOrderedStrokes(mathExpression);
            ArrayList strokes = DMathExpression.extractStrokes(mathExpression);
//            PARTITION TREE DOES NOT FINISHES FOR SECOND EXPRESSION OF CROME2012_TEST 
//            partitionGenerator.generateTree(strokes);
//            ArrayList<SymbolHypothesis> hypothesis = partitionGenerator.getHypothesis();
            hypothesisGenerator.generateHypothesis(strokes);
            ArrayList<SymbolHypothesis> hypothesis = hypothesisGenerator.getAllHypothesis();//hypothesisGenerator.getAllHypothesisFilteredByStroke();//hypothesisGenerator.getAllHypothesis();
            totalSymbols += mathExpression.size();
            countCorrectSegmentations(hypothesis, mathExpression);
//            countCorrectSegmentationsForUniqueLabelPErSegmentation(hypothesis, mathExpression);
            
//            System.out.println(aFile + "\t" + mathExpression.size() + "\t" + 
//                    correctLabels + "\t" +
//                    + strokes.size() + "\t" + correctSegmentations + "\t" + hypothesis.size());
            System.out.println(correctSegmentations / (float) mathExpression.size() + 
                    "\t" + correctLabels / (float) correctSegmentations);
            
            totalCorrectSegmentations += correctSegmentations;
            totalCorrectLbels += correctLabels;
            
//            System.out.println(getHypothesisPErStrokeAsString(hypothesisGenerator, Tempstrokes));
            
        }
        System.out.println("total correct segmentations: " + (float) totalCorrectSegmentations * 100 / totalSymbols +
                "\t" + (float) totalCorrectLbels * 100 / totalCorrectSegmentations );
    }
    
    public static String getHypothesisPErStrokeAsString(HypothesisGenerator generator, List<OrderedStroke> 
            strokes){
        String hypPerStroke = "";
        List<SymbolHypothesis> hypothesis;
        for (OrderedStroke orderedStroke : strokes) {
            hypPerStroke += "Stroke " + orderedStroke.getIndex() +  
                    "("+ orderedStroke.size() + ") : ";
            hypothesis = generator.getHypothesis(orderedStroke);
            for (SymbolHypothesis symbolHypothesis : hypothesis) {
                hypPerStroke += symbolHypothesis.getLabel(0) + "\t";
//                for (CostPerSymbolClass costPerSymbolClass : symbolHypothesis.getCostsPerClass()) {
//                    hypPerStroke += costPerSymbolClass.getLabel()  + 
////                            "(" + symbolHypothesis.getStrokesIDsAsString()+ ")"  + 
//                            "\t";
//                }
            }
            hypPerStroke += "\n";
        }
        return hypPerStroke;
    }
    

    public static void countCorrectSegmentations(ArrayList<SymbolHypothesis> hypothesis, 
            DMathExpression mathExpression){
        correctSegmentations = 0;
        correctLabels = 0;
        for (DSymbol dSymbol : mathExpression) {
            for (SymbolHypothesis symbolHypothesis : hypothesis) {
                if(isHypothesisOfSymbol(symbolHypothesis, dSymbol)){
                    correctSegmentations++;
                    if(symbolHypothesis.hasLabel(dSymbol.getLabel()))
                        correctLabels++;
                    break;
                }
            }
        }
    }
    
    public static void countCorrectSegmentationsForUniqueLabelPErSegmentation(ArrayList<SymbolHypothesis> hypothesis, 
            DMathExpression mathExpression){
        correctSegmentations = 0;
        correctLabels = 0;
        boolean foundSegmentation;
        for (DSymbol dSymbol : mathExpression) {
            foundSegmentation = false;
            for (SymbolHypothesis symbolHypothesis : hypothesis) {
                if(isHypothesisOfSymbol(symbolHypothesis, dSymbol)){
                    if(!foundSegmentation){
                        correctSegmentations++;
                        foundSegmentation = true;
                    }
                    if(symbolHypothesis.hasLabel(dSymbol.getLabel())){
                        correctLabels++;
                        break;
                    }
                    
                }
            }
        }
    }
    
    public static boolean isHypothesisOfSymbol(SymbolHypothesis hypothesis, DSymbol symbol){
        SymbolHypothesis newHypothesis = new SymbolHypothesis(hypothesis.getBinaryRepresentation().length);
        newHypothesis.setSymbol(symbol);
        return hypothesis.hasSameStrokes(newHypothesis);
//        for (DStroke dStroke : symbol) {
//             if(!hypothesis.getBinaryRepresentation()[((OrderedStroke) dStroke).getIndex()])
//                 return false;
//        }
//        return true;
    }
    
    public static void testSegmentationTree(){
//        InkMLInput inkMlInput = new InkMLInput();
//        ArrayList strokes = inkMlInput.extractStrokesFromInkMLFile("103_em_2.inkml"); 
//        InkmlReader reader = new InkmlReader();
//        reader.read("103_em_2.inkml");
//        DMathExpression mathExpression = reader.getMathExpression().asDMathExpression();
////"002-equation004.inkml" "formulaire054-equation056.inkml"  "KME2G3_1_sub_63.inkml"); //"KME2G3_11_sub_95.inkml");
//        
//        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
//        PartitionTreeGenerator partitionGenerator = new PartitionTreeGenerator();
//        HypothesisGenerator hypothesisGenerator = new NearestNeighborGraphHypothesisGenerator();
//        ClassificationFilter filter = new ClassificationFilter();
//        
////        TO USE SHAPE CONTEXT CLASSIFIER
////        ClassifierTest test = new ClassifierTest();
////        test.setClassifier(new ShapeContextClassifier());
////        test.readData();
////        test.prepareClassifier();
////        filter.setClassifier( test.getClassifier());
////        END - TO USE SHAPE CONTEXT CLASSIFIER        
//        
//        ((NearestNeighborGraphHypothesisGenerator) hypothesisGenerator).setFilter(filter);
//        partitionGenerator.setHypothesisGenerator(hypothesisGenerator);
//        partitionGenerator.generateTree(strokes);
//        ArrayList<Partition> partitionsInIncreasingCost = partitionGenerator.getPartitionsInIncreasingCost();
        
        String[] contentAsStringArrayList = FilesUtil.getContentAsStringArrayList(MathRecognitionFiles.INKML_CROHME_2013_TEST_FILES);
        InkmlReader reader;
        ArrayList<Partition> partitionsInIncreasingCost;
        int defaultMaxSize = 5000;
        int maxSize;
        double maxPercentage;
        DMathExpression mathExpression;
            double pecentage;
        for (String string : contentAsStringArrayList) {
            partitionsInIncreasingCost = PartitionTreeEvaluator.getPartitionsSORTBYINCREASINGCOST(
                    MathRecognitionFiles.INKML_CROHME_2013_TEST_DIR + string);
            reader = new InkmlReader();
            reader.read(MathRecognitionFiles.INKML_CROHME_2013_TEST_DIR + string);
            mathExpression = reader.getMathExpression().asDMathExpression();
            maxPercentage = 0;
            maxSize = Math.min(defaultMaxSize, partitionsInIncreasingCost.size());
            for (Partition partition : partitionsInIncreasingCost.subList(0, maxSize)) {
                pecentage = partition.percentageOfSymbolsContained(mathExpression);
                if (pecentage >  maxPercentage)
                    maxPercentage = pecentage;
            }
            System.out.println(maxPercentage);
        }
        
        
        
//        System.out.println("num part: " + partitionsInIncreasingCost.size());
//        ArrayList<SymbolHypothesis> hypothesis = partitionGenerator.getHypothesis();
//        System.out.println("number of strokes: " + strokes.size());
//        System.out.println("number of symbol hypothesis: " + hypothesis.size());
////        for (SymbolHypothesis symbolHypothesis : hypothesis) {
////            System.out.println(symbolHypothesis.getCostsPerClass().get(0).getLabel() + " " + 
////                    symbolHypothesis.getCostsPerClass().get(0).getCost() + " " + 
////                    symbolHypothesis.getStrokesIDsAsString());
////        }
//        
//        Partition aPartition;
//        for (int i = 0; i < partitionsInIncreasingCost.size(); i++) {
//            System.out.println("Partition: " );
//            aPartition = partitionsInIncreasingCost.get(i);
//            System.out.println("Cost: " + aPartition.getCost());
//            for (SymbolHypothesis symbolHypothesis : aPartition.getSymbolHypotheses()) {
//                System.out.println(symbolHypothesis.getLabel(0) + " size: " 
//                        + symbolHypothesis.getSymbol().size() + 
//                        " strokes: " + symbolHypothesis.getStrokesIDsAsString() + 
//                                " cost: " + symbolHypothesis.getCost(0));
//            }
//            System.out.println("\n");
//        }
    }
    
    public static DMathExpression getPartitionAsMathExpression(String pathFile){
        InkMLInput inkMlInput = new InkMLInput();
        ArrayList strokes = inkMlInput.extractStrokesFromInkMLFile(pathFile); //"002-equation004.inkml" "formulaire054-equation056.inkml"  "KME2G3_1_sub_63.inkml"); //"KME2G3_11_sub_95.inkml");
        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
        PartitionTreeGenerator partitionGenerator = new PartitionTreeGenerator();
        HypothesisGenerator hypothesisGenerator = new NearestNeighborGraphHypothesisGenerator();
        ClassificationFilter filter = new ClassificationFilter();
        

        ((NearestNeighborGraphHypothesisGenerator) hypothesisGenerator).setFilter(filter);
        partitionGenerator.setHypothesisGenerator(hypothesisGenerator);
        partitionGenerator.generateTree(strokes);
        ArrayList<Partition> partitionsInIncreasingCost = partitionGenerator.getPartitionsInIncreasingCost();
//        System.out.println("num part: " + partitionsInIncreasingCost.size());
//        ArrayList<SymbolHypothesis> hypothesis = partitionGenerator.getHypothesis();
//        System.out.println("number of strokes: " + strokes.size());
//        System.out.println("number of symbol hypothesis: " + hypothesis.size());
//        for (SymbolHypothesis symbolHypothesis : hypothesis) {
//            System.out.println(symbolHypothesis.getLabels().get(0) + " ");
//        }
//        
//        Partition aPartition;
//        for (int i = 0; i < partitionsInIncreasingCost.size(); i++) {
//            System.out.println("Partition: " );
//            aPartition = partitionsInIncreasingCost.get(i);
//            System.out.println("Cost: " + aPartition.getCost());
//            for (SymbolHypothesis symbolHypothesis : aPartition.getSymbols()) {
//                System.out.println(symbolHypothesis.getLabels().get(0) + " size: " 
//                        + symbolHypothesis.getSymbol().size() + " cost: " + symbolHypothesis.getCost());
//            }
//            System.out.println("\n");
//        }
        
        return partitionsInIncreasingCost.get(0).partionAsExpression();
    }
    
    public static ArrayList<ArrayList<DSymbol>> getPartitionsAsListsOFsymbolsSORTBYINCREASINGCOST(String pathFile){
        InkMLInput inkMlInput = new InkMLInput();
        ArrayList strokes = inkMlInput.extractStrokesFromInkMLFile(pathFile); //"002-equation004.inkml" "formulaire054-equation056.inkml"  "KME2G3_1_sub_63.inkml"); //"KME2G3_11_sub_95.inkml");
        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
        PartitionTreeGenerator partitionGenerator = new PartitionTreeGenerator();
        HypothesisGenerator hypothesisGenerator = new NearestNeighborGraphHypothesisGenerator();
        ClassificationFilter filter = new ClassificationFilter();
        

        ((NearestNeighborGraphHypothesisGenerator) hypothesisGenerator).setFilter(filter);
        partitionGenerator.setHypothesisGenerator(hypothesisGenerator);
        partitionGenerator.generateTree(strokes);
        return partitionGenerator.getPartitionsAsListOFSymbolsInIncreasingCost();
    }
    
    public static ArrayList<Partition> getPartitionsSORTBYINCREASINGCOST(String pathFile){
        InkMLInput inkMlInput = new InkMLInput();
        ArrayList strokes = inkMlInput.extractStrokesFromInkMLFile(pathFile); //"002-equation004.inkml" "formulaire054-equation056.inkml"  "KME2G3_1_sub_63.inkml"); //"KME2G3_11_sub_95.inkml");
        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
        PartitionTreeGenerator partitionGenerator = new PartitionTreeGenerator();
        HypothesisGenerator hypothesisGenerator = new NearestNeighborGraphHypothesisGenerator();
        ClassificationFilter filter = new ClassificationFilter();
        

        ((NearestNeighborGraphHypothesisGenerator) hypothesisGenerator).setFilter(filter);
        partitionGenerator.setHypothesisGenerator(hypothesisGenerator);
        partitionGenerator.generateTree(strokes);
        return partitionGenerator.getPartitionsInIncreasingCost();
    }
}
