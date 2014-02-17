/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification.neuralNetwork;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.classification.SymbolLabels;
import br.usp.ime.faguilar.directories.MathRecognitionFiles;
import br.usp.ime.faguilar.evaluation.ClassifierTest;
import br.usp.ime.faguilar.evaluation.KFoldPartitioner;
import br.usp.ime.faguilar.evaluation.SymbolTestData;
import br.usp.ime.faguilar.evaluation.TrainTestGroup;
import br.usp.ime.faguilar.feature_extraction.NeuralNetworkFeatures;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.feature_extraction.ShapeContextFeature;
import br.usp.ime.faguilar.matching.MatchingParameters;
import static br.usp.ime.faguilar.outOfProject.ExtractionOFFeaturesToFile.NeuralNetworkFeaturesExtractor.convertMatrixShapeContextToText;
import br.usp.ime.faguilar.util.FilesUtil;
import br.usp.ime.faguilar.util.SymbolUtil;
import java.util.ArrayList;
import java.util.Arrays;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

/**
 *
 * @author Frank Aguilar
 */
public class NeuralNetworkClassifierEvaluator extends Classifier
    implements LearningEventListener{
    
    private NeuralNetwork neuralNetwork;
    private DataSet trainDataset;
    private KFoldPartitioner partitioner;
//    private DataSet validationDataset;
//    private DataSet testDataset;
    
    private int countIterations;
    private double[] learningRates = new double[]{0.005};//{0.005};//{0.009, 0.007, 0.005, 0.003, 0.001, 0.0009, 0.0007, 0.0005};//{0.01, 0.005, 0.001, 0.0005};//{0.05};//{0.005};//{0.005, 0.005, 0.001, 0.0005, 0.0001};
    private int[] iterations = new int[]{1000};//{1000};//{20, 20, 20, 20, 20};
    private String logFile = "log_neural_net.txt";
    private int iterationIndex;
    private int countToSave;
    private int outputSize;
    private int inputSize;
    private int numberOfHiddenUnits;
    
    public NeuralNetworkClassifierEvaluator(){
        inputSize = MatchingParameters.LogPolarLocalRegions * MatchingParameters.angularLocalRegions
                * NeuralNetworkFeatures.numberOShapecontext;
        //for generalized shape contxt
//        inputSize *= 2;
//        outputSize = 101;
        outputSize = 75;
        numberOfHiddenUnits = 100;
    }
    
    @Override
    public void train(){
        neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 
                inputSize, numberOfHiddenUnits, outputSize); 
//        neuralNetwork = new NeuralNetworkWithSoftMaxActivation(TransferFunctionType.SIGMOID, 
//                inputSize, numberOfHiddenUnits, outputSize);  
        
//        neuralNetwork = NeuralNetwork.load("neural_network_1500");
        LearningRule learningRule = new BackPropagation();//new BackPropagation();
        learningRule.setNeuralNetwork(neuralNetwork);
        ((SupervisedLearning) learningRule).addListener(this);
        countIterations = 0;
        iterationIndex = 0;
        countToSave = 0;
        ((SupervisedLearning) learningRule).setMaxIterations(calculateMaxIterations());
        ((SupervisedLearning) learningRule).setLearningRate(learningRates[iterationIndex]);
        ((SupervisedLearning) learningRule).setMaxError(0.0008);
        neuralNetwork.setLearningRule(learningRule);
//        for (Neuron neuron : neuralNetwork.getOutputNeurons()) {
//            neuron.setTransferFunction(new ExpTransferFunction());
//        }
//        ((SupervisedLearning) learningRule).setBatchMode(true);
        learningRule.learn(trainDataset); 
    }
    
    public int calculateMaxIterations(){
        int max = 0;
        for (int i : iterations) {
            max +=i;
        }
        return max;
    }
    
    public String testNeuralNetwork(DataSet dataSet) {
        double[] desiredOutput;
        int correct = 0;
        for(DataSetRow row : dataSet.getRows()) {
            neuralNetwork.setInput(row.getInput());
            neuralNetwork.calculate();
            double[] networkOutput = neuralNetwork.getOutput();
            networkOutput = NeuralNetworkClassifier.outputToZerosAndOne(networkOutput);
            desiredOutput = row.getDesiredOutput();
            //percn with 7 classes ans 20 iterations 92.07317
            if(isCorrectOutput(desiredOutput, networkOutput))
                correct++;
//            System.out.println(" Output: " + Arrays.toString( networkOutput) );
        }
        //addToLogFile("Recognition percentage: " + (correct / (float) dataSet.size()) * 100 + "\n");
//        System.out.println("Recognition percentage: " + (correct / (float) dataSet.size()) * 100 + "\n");
//        last test result: 8.29916
        return String.valueOf((correct / (float) dataSet.size()) * 100);
    }
    
    
    
    public boolean isCorrectOutput(double[] desiredOutput, double[] output){
        boolean correctOutput = true;
        for (int i = 0; i < output.length; i++) {
//            if(Math.round(desiredOutput[i]) != Math.round(output[i])){
            if(desiredOutput[i] != output[i]){
                correctOutput = false;
                break;
            }            
        }
        return correctOutput;
    }
    
    public static void testNeuralNetworkWithIVCFiles(){
        ClassifiactionDataSet trainDataset = 
                ClassifiactionDataSet.createDatasetWithInputAndOutputSize(210, 75);
        String path = "../MathFiles/"//"/Volumes/FRANKMYPASS/doctorado/proyecto/neural-networks/"
                + "NeededFilesForInterface_learningAnMLP/train-test/trainOnLineFeatures.data";
//                "D:\\frank_temp\\NeededFilesForInterface_learningAnMLP\\"
//                + "train-test\\trainOnLineFeatures.data";
        trainDataset.readDatasetInIVCFormat(path, "\t");
//        
        ClassifiactionDataSet testDataset = 
                ClassifiactionDataSet.createDatasetWithInputAndOutputSize(210, 75);
        path = "../MathFiles/"//"/Volumes/FRANKMYPASS/doctorado/proyecto/neural-networks/"
                + "NeededFilesForInterface_learningAnMLP/train-test/"
                + "validationOnLineFeatures.data"; // "crohme2012_testOnLineFeatures.data";//"validationOnLineFeatures.data";
//                "D:\\frank_temp\\NeededFilesForInterface_learningAnMLP\\"
//                + "train-test\\validationOnLineFeatures.data";
        testDataset.readDatasetInIVCFormat(path, "\t");
//        
////        trainDataset.getDataset().saveAsTxt("trainOnineNeuroph", "\t");
////        testDataset.getDataset().saveAsTxt("validationOnineNeuroph", "\t");
        
//        NeuralNetworkClassifier nnetwork = new NeuralNetworkClassifier();
//        nnetwork.setTrainDataset(trainDataset.getDataset());
//        nnetwork.train();
////        NeuralNetwork nnFromFile = NeuralNetwork.load("/Volumes/FRANKMYPASS/"
////                + "doctorado/proyecto/neural-networks/nnetwork-java/traineNetTous les fichiers.nnet");
////        nnetwork.setNeuralNetwork(nnFromFile);
//        nnetwork.testNeuralNetwork(testDataset.getDataset());
                
        NeuralNetwork nn;
        NeuralNetworkClassifierEvaluator nnetworkToTest = new NeuralNetworkClassifierEvaluator();
        int numberID;
       String result1, result2;
        for (numberID = 20; numberID <= 1000; numberID += 20) {
            nn = NeuralNetwork.load("neural_network_" + numberID);
            nnetworkToTest.setNeuralNetwork(nn);
            result1 = nnetworkToTest.testNeuralNetwork(trainDataset.getDataset());
            result2 = nnetworkToTest.testNeuralNetwork(testDataset.getDataset());
            System.out.printf("%s\t%s\n", result1, result2);
        }        
    }
    
    public static void testNeuralNetworkWithInkml(){
        ClassifierTest classifierTest =  new ClassifierTest();
        classifierTest.readData();
        classifierTest.partData();
        ArrayList<Classifible> trainClassifibles = classifierTest.getTrainData();
        
//        ArrayList<Classifible> testClassifibles = SymbolUtil.readTemplatesFromInkmlFiles(MathRecognitionFiles.TEST_FILES_CROHME,
//                MathRecognitionFiles.INKML_CROHME_2012_TEST_DIR);
                
        NeuralNetwork nn;
        NeuralNetworkClassifierEvaluator nnetworkToTest = new NeuralNetworkClassifierEvaluator();
        int numberID;
       String result1, result2;
       SymbolLabels.readCrohme2012Labels();
       nnetworkToTest.setTrainingData(trainClassifibles);
//       System.out.println("start training...");
//       nnetworkToTest.train();
//      
        DataSet testDataset;
//        System.out.println("Testing ...");
        for (numberID = 180; numberID <= 1000; numberID += 20) {
            nn = null;
            nn = NeuralNetwork.load("neural_network_fuzzy_soft50" + numberID);
            nnetworkToTest.setNeuralNetwork(nn);
//            
            result1 = nnetworkToTest.testNeuralNetwork(nnetworkToTest.getTrainDataset());
//            nnetworkToTest.getTrainDataset().saveAsTxt("dataFuzzyTrain", "\t");
            testDataset = new DataSet(nnetworkToTest.getInputSize(), nnetworkToTest.getOutputSize());
            nnetworkToTest.fillDatasetWithClassifibles(testDataset, classifierTest.getTestData());
            result2 = nnetworkToTest.testNeuralNetwork(testDataset);
            System.out.printf("%s\t%s\n", result1, result2);
////////            
////            TO TEST DATASET
//            testDataset = new DataSet(nnetworkToTest.getInputSize(), nnetworkToTest.getOutputSize());
//            nnetworkToTest.fillDatasetWithClassifibles(testDataset, testClassifibles);
//            result2 = nnetworkToTest.testNeuralNetwork(testDataset);
//            System.out.printf("%s\n", result2);
        }        
    }
    
    public static void runKFoldEvaluation(){
        NeuralNetworkClassifierEvaluator evaluator = new NeuralNetworkClassifierEvaluator();
        ArrayList<Classifible> classifibles = SymbolUtil.readTemplatesFromInkmlFiles(MathRecognitionFiles.INKML_CROHME_2013_TRAIN_FILES,
                MathRecognitionFiles.INKML_CROHME_2013_TRAIN_DIR);
        SymbolTestData symbolData = new SymbolTestData();
        symbolData.addClassifibles(classifibles);
//        symbolData.printLabels();
        
        KFoldPartitioner partitioner = new KFoldPartitioner();
        partitioner.setMap(symbolData.getMap());
        TrainTestGroup groupTrainAndTest;
        int numberOfFolds = KFoldPartitioner.numberOfFolds;
        SymbolLabels.readCrohme2013Labels();
        String networkName = "fuzzySContext_";
        String networkNamePerIteration;
        DataSet testDataset;
        String result;
        for (int i = 1; i <= numberOfFolds; i++) {
            groupTrainAndTest = null;
            groupTrainAndTest = partitioner.partWithTestFoldAt(i);
            evaluator.setTrainingData(groupTrainAndTest.getTrain());
            System.out.println("start training...");
            evaluator.train();
            networkNamePerIteration = networkName + i;
            evaluator.getNeuralNetwork().save(networkNamePerIteration);
            testDataset = new DataSet(evaluator.getInputSize(), evaluator.getOutputSize());
            evaluator.fillDatasetWithClassifibles(testDataset, groupTrainAndTest.getTest());
            result = evaluator.testNeuralNetwork(testDataset);
            FilesUtil.append(networkName, result + "\n");
        }
    }
    
    public static void exportKFoldFiles(){
        NeuralNetworkClassifierEvaluator evaluator = new NeuralNetworkClassifierEvaluator();
        ArrayList<Classifible> classifibles = SymbolUtil.readTemplatesFromInkmlFiles(MathRecognitionFiles.INKML_CROHME_2013_TEST_FILES,//MathRecognitionFiles.INKML_CROHME_2013_TRAIN_FILES,
                MathRecognitionFiles.INKML_CROHME_2013_TEST_DIR);
        
        
//        SymbolTestData symbolData = new SymbolTestData();
//        symbolData.addClassifibles(classifibles);
////        symbolData.printLabels();
//        
//        KFoldPartitioner partitioner = new KFoldPartitioner();
//        partitioner.setMap(symbolData.getMap());
//        TrainTestGroup groupTrainAndTest;
//        int numberOfFolds = KFoldPartitioner.numberOfFolds;
        SymbolLabels.readCrohme2013Labels();
        exportFeaturesToIVCFiles(classifibles, "crohme2013_test50pts.data");
//        
//        String trainName = "train30pts";
//        String testName = "test30pts";
//        for (int i = 1; i <= numberOfFolds; i++) {
//            groupTrainAndTest = null;
//            groupTrainAndTest = partitioner.partWithTestFoldAt(i);
//            
//            exportFeaturesToIVCFiles(groupTrainAndTest.getTrain(), trainName + "_" + i);
//            exportFeaturesToIVCFiles(groupTrainAndTest.getTest(), testName + "_" + i);
//
//        }
    }
    
    public static void exportFeaturesToIVCFiles(ArrayList<Classifible> classifibles, String fileToExport){
        int labelAsInt;
        String listsOfScontextAsString = "";
        double[] scontext;
        int count = 0;
        for (Classifible classifible : classifibles) {
            if(classifible.getSymbol().size() > 0) {
                labelAsInt = SymbolLabels.getIndexOfSymbolByLabel((String) classifible.getMyClass());
            if(count >= 100){
                FilesUtil.append(fileToExport, listsOfScontextAsString);
                listsOfScontextAsString = "";
                count = 0;
            }
            scontext = NeuralNetworkFeatures.extractMergedFeatures(classifible.getSymbol());
//                    PreprocessingAlgorithms.getNShapeContetxFeatures(
//                    classifible.getSymbol(), MatchingParameters.numberOfPointPerSymbol);
            listsOfScontextAsString += (labelAsInt + "\t" +
                    formatedIVCFloatAndStringFromarray(scontext)
                    + "\n");
            count++;
            } else
                System.out.println(classifible.getMyClass());
            
            
        }
        if(count > 0)
                FilesUtil.append(fileToExport, listsOfScontextAsString);
        
    }
    
    public static String formatedIVCFloatAndStringFromarray(double[] array){
        String string = "";
        for (int i = 0; i < array.length; i++) {
            string += (float) array[i];
            if(i < array.length - 1)
                string += "\t";
        }
        return string;
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        SupervisedLearning learning = (SupervisedLearning) event.getSource();
        String output = "Iteration: " + countIterations + "\t";
        output += "error: " + learning.getTotalNetworkError() + "\n";
//        addToLogFile(output);
        countIterations++;
        if(countIterations >= iterations[iterationIndex]){
            iterationIndex++;
            if(iterationIndex < learningRates.length){
                learning.setLearningRate(learningRates[iterationIndex]);
                countIterations = 0;
            }
        }
        countToSave++;
        if(countToSave >= 20){
            neuralNetwork.save("neural_network_fuzzy_soft50" + learning.getCurrentIteration());
            countToSave = 0;
        }
        System.out.printf("iteration: %d\ttotal network error: %f\n", learning.getCurrentIteration(),
                learning.getTotalNetworkError());
    }
    
    public void addToLogFile(String string){
        FilesUtil.append(logFile, string);
    }    

    public DataSet getTrainDataset() {
        return trainDataset;
    }

    public void setTrainDataset(DataSet trainDataset) {
        this.trainDataset = trainDataset;
    }

    @Override
    public void setTrainingData(ArrayList<Classifible> classifiables) {
        trainDataset = new DataSet(inputSize, outputSize);
        fillDatasetWithClassifibles(trainDataset, classifiables);
    }
    
    public void fillDatasetWithClassifibles(DataSet dataset,
            ArrayList<Classifible> classifiables){
        for (Classifible classifible : classifiables) {
            addClassifibleToDataSet(dataset, classifible);
        }
    }
    
    @Override
    public Object classify(Classifible classifible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object orderedListOfClasses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void addClassifibleToDataSet(DataSet dataset, Classifible classifible) {
        double[] intput = NeuralNetworkFeatures.extractMergedFeatures(classifible.getSymbol());
        
        double[] output = new double[dataset.getOutputSize()];
        output[SymbolLabels.getIndexOfSymbolByLabel((String) classifible.getMyClass())] 
                = 1;
        DataSetRow row = new DataSetRow(intput, output);
        row.setLabel((String) classifible.getMyClass());
        dataset.addRow(row);
        
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }

    public int getInputSize() {
        return inputSize;
    }

    public void setInputSize(int inputSize) {
        this.inputSize = inputSize;
    } 
}
