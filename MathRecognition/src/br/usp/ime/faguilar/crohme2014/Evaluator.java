/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.crohme2014;

import br.usp.ime.faguilar.classification.ClassificationHypothesis;
import br.usp.ime.faguilar.classification.SymbolLabels;
import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifier;
import static br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifierEvaluator.isCorrectOutput;
import br.usp.ime.faguilar.util.FilesUtil;
import java.util.Arrays;
import java.util.Collections;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;

/**
 *
 * @author Frank
 */
public class Evaluator {
    private NeuralNetwork neuralNetwork;
    private DataSet dataset;
    public static final String OUTPU_FILE_PATH = "outputClassification.txt";
    public static final int numberOfHypothesisAsResult = 10;
    private int artificialID;
    private static boolean isOutputforCrohme = false;
    
    public void evaluate(){
        SymbolLabels.readCrohme2013LabelsWithJunk();
        artificialID = 1;
        double[] networkOutput;
        for (DataSetRow dataSetRow : dataset.getRows()) {
            neuralNetwork.setInput(dataSetRow.getInput());
            neuralNetwork.calculate();
            networkOutput = neuralNetwork.getOutput();
            addResultToOutputFile(networkOutput, dataSetRow.getDesiredOutput());
        }
//        double[] desiredOutput;
//        int correct = 0;
//        for(DataSetRow row : dataSet.getRows()) {
//            aNeuralNetwork.setInput(row.getInput());
//            aNeuralNetwork.calculate();
//            double[] networkOutput = aNeuralNetwork.getOutput();
//            networkOutput = NeuralNetworkClassifier.outputToZerosAndOne(networkOutput);
//            desiredOutput = row.getDesiredOutput();
//            //percn with 7 classes ans 20 iterations 92.07317
//            if(isCorrectOutput(desiredOutput, networkOutput))
//                correct++;
//        }
//        return String.valueOf((correct / (float) dataSet.size()) * 100);
        
        
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    private void addResultToOutputFile(double[] networkOutput, double[] desiredOutput) {
        if(isOutputforCrohme)
            generateOutputForCrohme2014(networkOutput);
        else
            generateMyOwnOutput(networkOutput, desiredOutput);
    }
    
    public void generateOutputForCrohme2014(double[] networkOutput) {
        ClassificationHypothesis[] hypothesis = new ClassificationHypothesis[networkOutput.length];
        for (int i = 0; i < networkOutput.length; i++) {
            hypothesis[i] = new ClassificationHypothesis();
            hypothesis[i].setCost(networkOutput[i]);
            hypothesis[i].setLabel(SymbolLabels.getLabelOfSymbolByIndex(i));            
        }
        Arrays.sort(hypothesis);
        String output = String.valueOf(artificialID) + ", ";
        for (int i = hypothesis.length - 1; i >= hypothesis.length - numberOfHypothesisAsResult; i--) {
            output += hypothesis[i].getLabel();
            if (i > hypothesis.length - numberOfHypothesisAsResult)
               output += ", ";
        }
        output += "\n";
        FilesUtil.append(OUTPU_FILE_PATH, output);
        artificialID ++;
    }

    public void generateMyOwnOutput(double[] networkOutput, double[] desiredOutput) {
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
        FilesUtil.append(OUTPU_FILE_PATH, output);
        artificialID ++;
    }
    
    public int getIndexOfClass(double[] output){
        for (int i = 0; i < output.length; i++) {
            if(output[i] == 1)
                return i;            
        }
        return -1;
    }
}
