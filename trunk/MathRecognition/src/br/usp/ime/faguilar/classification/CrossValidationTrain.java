/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.classification.neuralNetwork.ClassifiactionDataSet;
import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifier;
import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkWithSoftMaxActivation;
import br.usp.ime.faguilar.util.FilesUtil;
import java.util.Arrays;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

/**
 *
 * @author Frank
 */
public class CrossValidationTrain implements LearningEventListener{
    private DataSet trainingSet;
    private DataSet validationSet;
    private NeuralNetwork classifier;
    private double bestRecognitionRate;
    public static final String OPTIMAL_NEURAL_NET_PATH = "optimalHistogramRelationClassifierwithJunk.net";//"optimalFuzzySC_Histogram2DNeuralNet_new.net";
    
    private int countIterations;
    private double[] learningRates = new double[]{0.05, 0.01, 0.005};
    private int[] iterations = new int[]{20, 20, 60};
    private int iterationIndex;
    private LearningRule learningRule;
    private int inputSize;
    private int outputSize;
    private int numberOFHidden;
    public static boolean generateOutputFile = false;
    public static String outputFileName;
    private static int numberOfHypothesisAsResult = 7;

    public CrossValidationTrain() {
        inputSize = 50;
        numberOFHidden = 50;
        outputSize = 6;
    }

    public int getInputSize() {
        return inputSize;
    }

    public void setInputSize(int inputSize) {
        this.inputSize = inputSize;
    }

    public int getNumberOFHidden() {
        return numberOFHidden;
    }

    public void setNumberOFHidden(int numberOFHidden) {
        this.numberOFHidden = numberOFHidden;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }
    
    
    
    public void runTrainingFromTrainAndValidationPathFiles(String trainFilePath,
    String validationFilePAth){
        ClassifiactionDataSet classificationDataset = ClassifiactionDataSet.createDatasetWithInputAndOutputSize(inputSize, outputSize);
        classificationDataset.readDatasetInIVCFormat(trainFilePath, "\t");
        trainingSet = classificationDataset.getDataset();
        classificationDataset.readDatasetInIVCFormat(validationFilePAth, "\t");
        validationSet = classificationDataset.getDataset();
        execute();
    }
    
    public void execute(){
        prepareClassifier();
        runTraining();
    }

    public DataSet getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(DataSet trainingSet) {
        this.trainingSet = trainingSet;
    }

    public DataSet getValidationSet() {
        return validationSet;
    }

    public void setValidationSet(DataSet validationSet) {
        this.validationSet = validationSet;
    }

    private void prepareClassifier() {
        bestRecognitionRate = Double.MIN_VALUE;
        classifier = new NeuralNetworkWithSoftMaxActivation(TransferFunctionType.TANH, 
                inputSize, numberOFHidden, outputSize);  
        countIterations = 0;
        iterationIndex = 0;
        learningRule = new MomentumBackpropagation();
        learningRule.setNeuralNetwork(classifier);
        ((SupervisedLearning) learningRule).addListener(this);
        ((MomentumBackpropagation) learningRule).setMomentum(0.5);
        ((SupervisedLearning) learningRule).setMaxIterations(calculateMaxIterations());
        ((SupervisedLearning) learningRule).setLearningRate(learningRates[0]);
        ((SupervisedLearning) learningRule).setMaxError(0.0001);
        classifier.setLearningRule(learningRule);
    }
    
        @Override
    public void handleLearningEvent(LearningEvent event) {        
        SupervisedLearning learning = (SupervisedLearning) event.getSource();
        System.out.printf("iteration: %d\ttotal network error: %f\n", learning.getCurrentIteration(),
        learning.getTotalNetworkError());
        learning.getTrainingSet().shuffle();
        
        countIterations++;
        if(countIterations >= iterations[iterationIndex]){
            iterationIndex++;
            if(iterationIndex < learningRates.length){
                learning.setLearningRate(learningRates[iterationIndex]);
                countIterations = 0;
            }
        }
            String result = testNeuralNetwork(validationSet);
            Double resulDouble = Double.valueOf(result);
            if(resulDouble > bestRecognitionRate){
                System.out.println("best net updated ");
                bestRecognitionRate = resulDouble;
                classifier.save(OPTIMAL_NEURAL_NET_PATH);
            }
    }
    
    public  String testNeuralNetwork(DataSet dataSet) {
        return testNeuralNetwork(classifier, dataSet);
    }
    
    public static String testNeuralNetwork(NeuralNetwork aNeuralNetwork, DataSet dataSet) {
        double[] desiredOutput;
        int correct = 0;
        double[] networkOutput, zeroNetworkOutput;
        for(DataSetRow row : dataSet.getRows()) {
            aNeuralNetwork.setInput(row.getInput());
            aNeuralNetwork.calculate();
            networkOutput = aNeuralNetwork.getOutput();
            zeroNetworkOutput = NeuralNetworkClassifier.outputToZerosAndOne(networkOutput);
            desiredOutput = row.getDesiredOutput();
            //percn with 7 classes ans 20 iterations 92.07317
            if(isCorrectOutput(desiredOutput, zeroNetworkOutput))
                correct++;
            if(generateOutputFile)
                generateMyOwnOutput(networkOutput, desiredOutput);
            
        }
        return String.valueOf((correct / (float) dataSet.size()) * 100);
    }
    
    public static boolean isCorrectOutput(double[] desiredOutput, double[] output){
        boolean correctOutput = true;
        for (int i = 0; i < output.length; i++) {
            if(desiredOutput[i] != output[i]){
                correctOutput = false;
                break;
            }            
        }
        return correctOutput;
    }

    public int calculateMaxIterations(){
        int max = 0;
        for (int i : iterations) {
            max +=i;
        }
        return max;
    }
    
    private void runTraining() {
        learningRule.learn(trainingSet); 
    }
    
    public static void generateMyOwnOutput(double[] networkOutput, double[] desiredOutput) {
        ClassificationHypothesis[] hypothesis = new ClassificationHypothesis[networkOutput.length];
        for (int i = 0; i < networkOutput.length; i++) {
            hypothesis[i] = new ClassificationHypothesis();
            hypothesis[i].setCost(networkOutput[i]);
            hypothesis[i].setLabel(String.valueOf(i));            
        }
        Arrays.sort(hypothesis);
        int indexOfClass = getIndexOfClass(desiredOutput);
        String output = indexOfClass + "\n";
        for (int i = hypothesis.length - 1; i >= hypothesis.length - numberOfHypothesisAsResult; i--) {
            output += hypothesis[i].getLabel();
            if (i > hypothesis.length - numberOfHypothesisAsResult)
               output += "\t";
            else
                output += "\n";
        }
        for (int i = hypothesis.length - 1; i >= hypothesis.length - numberOfHypothesisAsResult; i--) {
            output += hypothesis[i].getCost();
            if (i > hypothesis.length - numberOfHypothesisAsResult)
               output += "\t";
            else
                output += "\n";
        }
        FilesUtil.append(outputFileName, output);
    }
    
    public static int getIndexOfClass(double[] output){
        for (int i = 0; i < output.length; i++) {
            if(output[i] == 1)
                return i;            
        }
        return -1;
    }

    public boolean isGenerateOutputFile() {
        return generateOutputFile;
    }

    
    
}
