/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifier;
import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifierEvaluator;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.feature_extraction.Histohgram2D;
import br.usp.ime.faguilar.feature_extraction.RelationFeatures;
import br.usp.ime.faguilar.util.FilesUtil;
import java.util.Arrays;
import org.neuroph.core.NeuralNetwork;

/**
 *
 * @author Frank
 */
public class RelationClassifier {
    private static NeuralNetwork relationClassifier;
    private static final String classifierPath = "optimalHistogramRelationClassifierwithJunk.net";//"optimalRelationClassifier.net";
    private static String[] relationLabels;
    private static final double TREASHOLD_JUNKS = 0.95;
    private static final int SUPERSCRIPT_INDEX = 4;
    private static final int SUBERSCRIPT_INDEX = 5;
    private static final String RIGHT_LABEL = "R";
    private static final String BELOW_LABEL = "B";
    private static final String ABOVE_LABEL = "A";
    private static final String INSIDE_LABEL = "I";
    private static final String JUNK_LABEL = "junk";
    
    public static double costOfClassification(DSymbol symbol1, DSymbol symbol2, int 
            resultIndex) {
        double[] output = getClassificationOutput(symbol1, symbol2);
        return NeuralNetworkClassifier.probabilityToCost(output[resultIndex]);
    }
    
    public static boolean isInHorizontalZone(DSymbol symbol1, DSymbol symbol2){
        double[] output = getClassificationOutput(symbol1, symbol2);
        String label  = calculateLabel(output);
        return label.equals(RIGHT_LABEL);
    }
    
    public static boolean isBestStructuralRelation(double[] output, String relationLabel) {
        int indexOfResult = NeuralNetworkClassifier.extractClassIndexFromOutput(output);
        String[] labels = getRelationLabels();
        return labels[indexOfResult].equals(relationLabel);
    }
    
    public static double[] getClassificationOutput(DSymbol symbol1, DSymbol symbol2){
        Histohgram2D.configureHistogramForRelations();
        double[] features = RelationFeatures.calculate2DHistogramDifferenceFeatures(symbol1, symbol2);//RelationFeatures.calculateGeometricFeatures(symbol1, symbol2);
        Histohgram2D.configureHistogramForSymbols();
        NeuralNetwork classifer = getClassifier();
        classifer.setInput(features);
        classifer.calculate();
        return classifer.getOutput();
    }
    
    public static boolean isBelowRelation(DSymbol symbol1, DSymbol symbol2){
        double[] output = getClassificationOutput(symbol1, symbol2);
        String label  = calculateLabel(output);
        return label.equals(BELOW_LABEL);
    }
    
    public static boolean isInsideRelation(DSymbol symbol1, DSymbol symbol2){
        double[] output = getClassificationOutput(symbol1, symbol2);
        String label  = calculateLabel(output);
        return label.equals(INSIDE_LABEL);
    }
    
    public static boolean isJunkOutput(double[] output){
        int indexOfResult = NeuralNetworkClassifier.extractClassIndexFromOutput(output);
        String[] labels = getRelationLabels();
        return labels[indexOfResult].equals(JUNK_LABEL);
    }
    
    public static boolean isHighProbabilityOfJunk(double probabilityOfJunk) {
        return probabilityOfJunk > TREASHOLD_JUNKS;
    }
    
    public static boolean isAboveRelation(DSymbol symbol1, DSymbol symbol2){
        double[] output = getClassificationOutput(symbol1, symbol2);
        String label  = calculateLabel(output);
        return label.equals(ABOVE_LABEL);
    }
    
    public static boolean isJunkRelation(DSymbol symbol1, DSymbol symbol2){
        Histohgram2D.configureHistogramForRelations();
        double[] features = RelationFeatures.calculate2DHistogramDifferenceFeatures(symbol1, symbol2);//RelationFeatures.calculateGeometricFeatures(symbol1, symbol2);
        Histohgram2D.configureHistogramForSymbols();
        NeuralNetwork lassifer = getClassifier();
        lassifer.setInput(features);
        lassifer.calculate();
        return isJunkOutput(lassifer.getOutput());
    }
    
    public static boolean isHighProbabilityJunkRelation(DSymbol symbol1, DSymbol symbol2){
        double[] output = getClassificationOutput(symbol1, symbol2);
        int indexOFResult = NeuralNetworkClassifier.extractClassIndexFromOutput(output);
        return isJunkOutput(output) && isHighProbabilityOfJunk(output[indexOFResult]);
    }
    
//    public static boolean isTopLeftRelation(DSymbol symbol1, DSymbol symbol2){
//        Histohgram2D.configureHistogramForRelations();
//        double[] features = RelationFeatures.calculate2DHistogramDifferenceFeatures(symbol1, symbol2);//RelationFeatures.calculateGeometricFeatures(symbol1, symbol2);
//        Histohgram2D.configureHistogramForSymbols();
//        NeuralNetwork lassifer = getClassifier();
//        lassifer.setInput(features);
//        lassifer.calculate();
//        double[] probabilities = lassifer.getOutput();
//        return probabilities[SUBERSCRIPT_INDEX] > probabilities[SUPERSCRIPT_INDEX];
//    }
    
    public static NeuralNetwork getClassifier(){
        if(relationClassifier == null)
            relationClassifier = NeuralNetwork.load(classifierPath);
        return relationClassifier;
    }
    
    public static String[] getRelationLabels(){
        if (relationLabels == null){
            relationLabels = FilesUtil.getContentAsStringArrayList(RelationFeatures.relationLabelesFile);
        }
        return relationLabels;
    }

    private static String calculateLabel(double[] output) {
        double[] copyOutput = Arrays.copyOf(output, output.length);
        int indexOFResult = NeuralNetworkClassifier.extractClassIndexFromOutput(copyOutput);
        if(isJunkOutput(output)){
            if(isHighProbabilityOfJunk(output[indexOFResult]))
                return JUNK_LABEL;
             copyOutput[indexOFResult] = -1;
             indexOFResult = NeuralNetworkClassifier.extractClassIndexFromOutput(copyOutput);
        }
        String[] labels = getRelationLabels();
        return labels[indexOFResult];
    }
}
