/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.crohme2014;

import br.usp.ime.faguilar.classification.SymbolLabels;
import br.usp.ime.faguilar.classification.neuralNetwork.ClassifiactionDataSet;
import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifierEvaluator;
import java.util.Collections;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;

/**
 *
 * @author Frank
 */
public class Task1 {
    public static final String trainData = 
            "../MathFiles/CROHME/crohme2014/fuzzySC/fold2/dataset/trainFuzzySC_2.data";
    public static final String validationData = 
            "../MathFiles/CROHME/crohme2014/fuzzySC/fold2/dataset/validationFuzzySC_2.data";
    public static final String testData = 
            "../MathFiles/CROHME/crohme2014/fuzzySC/fold2/dataset/crohme2013SFuzzyContextTest.data";
    
    public static final int numberOFClasses = 102;
    public static final int numberOFHiddenNeurons = 102;
    public static final int numberOfFeatures = 192;
    
    
    public static void trainWithValidationData(){
        ClassifiactionDataSet trainDataset = 
                ClassifiactionDataSet.createDatasetWithInputAndOutputSize(numberOfFeatures, numberOFClasses);
        trainDataset.readDatasetInIVCFormat(trainData, "\t");
        ClassifiactionDataSet validationDataset = 
                ClassifiactionDataSet.createDatasetWithInputAndOutputSize(numberOfFeatures, numberOFClasses);
        validationDataset.readDatasetInIVCFormat(validationData, "\t");
        
        NeuralNetworkClassifierEvaluator evaluator = new NeuralNetworkClassifierEvaluator();
        
        SymbolLabels.readCrohme2013LabelsWithJunk();
        trainDataset.getDataset().shuffle();
        evaluator.setTrainDataset(trainDataset.getDataset());
        evaluator.setValidationDataset(validationDataset.getDataset());
        evaluator.setUseValidation(true);
        System.out.println("start training...");
        evaluator.train();
        NeuralNetwork optimalNetwork = NeuralNetworkClassifierEvaluator.getOptimalNetwork();
        
        System.out.println("Result on trainSet: " + NeuralNetworkClassifierEvaluator.testNeuralNetwork(optimalNetwork, 
                        trainDataset.getDataset()));
        System.out.println("Result on validationSet: " + 
                NeuralNetworkClassifierEvaluator.testNeuralNetwork(optimalNetwork, 
                        validationDataset.getDataset()));
        trainDataset = null;
        validationDataset = null;
        evaluator.eraseDatasets();
        ClassifiactionDataSet testDataset = 
                ClassifiactionDataSet.createDatasetWithInputAndOutputSize(numberOfFeatures, numberOFClasses);
        testDataset.readDatasetInIVCFormat(testData, "\t");
        System.out.println("Result on TestSet: " + 
                NeuralNetworkClassifierEvaluator.testNeuralNetwork(optimalNetwork, 
                        testDataset.getDataset()));
    }
    
    public static void evaluateClassificationForTask1(){
        Evaluator evaluator  = new Evaluator();
        evaluator.setNeuralNetwork(NeuralNetworkClassifierEvaluator.getOptimalNetwork());
        ClassifiactionDataSet testDataset = 
                ClassifiactionDataSet.createDatasetWithInputAndOutputSize(numberOfFeatures, numberOFClasses);
        testDataset.readDatasetInIVCFormat(testData, "\t");
        evaluator.setDataset(testDataset.getDataset());
        evaluator.evaluate();
        
    }
    
    
}
