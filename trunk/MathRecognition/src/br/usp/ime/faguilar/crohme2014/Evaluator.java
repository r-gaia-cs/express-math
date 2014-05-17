/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.crohme2014;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ClassificationHypothesis;
import br.usp.ime.faguilar.classification.SymbolLabels;
import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifier;
import static br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifierEvaluator.isCorrectOutput;
import br.usp.ime.faguilar.feature_extraction.NeuralNetworkFeatures;
import br.usp.ime.faguilar.util.FilesUtil;
import br.usp.ime.faguilar.util.SymbolUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static final String OUTPU_FILE_PATH = "outputNBestFuzzySC_histogram5x5_crohme2014.txt";//"outputClassificationFuzzySC_Histogram2D_crohme2014_new.txt";
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


    private void addResultToOutputFile(double[] networkOutput, Object additionalInfo) {
        if(isOutputforCrohme)
            generateOutputForCrohme2014(networkOutput, (String) additionalInfo);
        else
            generateMyOwnOutput(networkOutput, (double[]) additionalInfo);
    }
    
    public void generateOutputForCrohme2014(double[] networkOutput, String UI) {
        ClassificationHypothesis[] hypothesis = new ClassificationHypothesis[networkOutput.length];
        for (int i = 0; i < networkOutput.length; i++) {
            hypothesis[i] = new ClassificationHypothesis();
            hypothesis[i].setCost(networkOutput[i]);
            hypothesis[i].setLabel(SymbolLabels.getLabelOfSymbolByIndex(i));            
        }
        Arrays.sort(hypothesis);
        String output =  UI + ", ";
        for (int i = hypothesis.length - 1; i >= hypothesis.length - numberOfHypothesisAsResult; i--) {
            output += hypothesis[i].getLabel();
            if (i > hypothesis.length - numberOfHypothesisAsResult)
               output += ", ";
            else
                output += "\n";
        }
        output += "scores,\t";
        for (int i = hypothesis.length - 1; i >= hypothesis.length - numberOfHypothesisAsResult; i--) {
            output += (float) hypothesis[i].getCost();
            if (i > hypothesis.length - numberOfHypothesisAsResult)
               output += ", ";
            else
               output += "\n";
        }
//        FilesUtil.append(OUTPU_FILE_PATH, output);
        System.out.print(output);
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
    
    public void evaluateWithListOFInkml(String listOfInkmlFiles) {
        try {
            BufferedReader bufferedReader = FilesUtil.getBufferedReader(listOfInkmlFiles);
            SymbolLabels.readCrohme2013LabelsWithJunk();
            isOutputforCrohme = true;
            String aFile;
            
            while (bufferedReader.ready()){
                aFile = bufferedReader.readLine();
                if(!aFile.isEmpty()){
                    Classifible classifibleWithUI = SymbolUtil.readInkmlClassifibleWithUI(aFile);
                    if(classifibleWithUI != null){
                        double[] intput = NeuralNetworkFeatures.extractMergedFeatures(
                            classifibleWithUI.getSymbol());
                        neuralNetwork.setInput(intput);
                        neuralNetwork.calculate();
                        double[] output = neuralNetwork.getOutput();
                        addResultToOutputFile(output, classifibleWithUI.getAditionalFeatures());
                    } else {
                        System.out.println("problem reading: " + aFile);
                    }
                    
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getIndexOfClass(double[] output){
        for (int i = 0; i < output.length; i++) {
            if(output[i] == 1)
                return i;            
        }
        return -1;
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
}
