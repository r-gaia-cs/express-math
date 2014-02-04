/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification.neuralNetwork;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ClassificationResult;
import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.classification.SymbolLabels;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.feature_extraction.NeuralNetworkFeatures;
import br.usp.ime.faguilar.util.IndexedValue;
import java.util.ArrayList;
import java.util.Collections;
import org.neuroph.core.NeuralNetwork;

/**
 *
 * @author Frank Aguilar
 */
public class NeuralNetworkClassifier extends Classifier{
    private static final String NEURAL_NETWORK_PATH = "neural_network_fuzzy4000";
    private NeuralNetwork neuralNetwork;
    private int numberofPossibleClasses;

    public NeuralNetworkClassifier() {
        neuralNetwork = NeuralNetwork.load(NEURAL_NETWORK_PATH);
        numberofPossibleClasses = 1;
        if(SymbolLabels.getSymbolLabels() == null)
            SymbolLabels.readClassesFromFile();
    }
       
    @Override
    public void setTrainingData(ArrayList<Classifible> classifiables) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void train() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object classify(Classifible classifible) {
        ClassificationResult result = new ClassificationResult();
        double[] input = NeuralNetworkFeatures.extractMergedFeatures(classifible.getSymbol());
        neuralNetwork.setInput(input);
        neuralNetwork.calculate();
        double[] networkOutput = neuralNetwork.getOutput();
        int classIndex = extractClassIndexFromOutput(networkOutput);
        String label = SymbolLabels.getLabelOfSymbolByIndex(classIndex);
        result.setMyClass(label);
        result.setCost(- Math.log(networkOutput[classIndex]));
        return result;
    }
        
    private int extractClassIndexFromOutput(double[] output){
        int maxPosition = 0;
        double max = output[maxPosition];
        for (int i = 1; i < output.length; i++) {
            if(output[i] > max){
                max = output[i];
                maxPosition = i;
            }
        }
        return maxPosition;
    }
    
    public static double[] outputToZerosAndOne(double[] output){
        double[] newOutput = new double[output.length];
        int maxPosition = 0;
        double max = output[maxPosition];
        for (int i = 1; i < newOutput.length; i++) {
            if(output[i] > max){
                max = output[i];
                maxPosition = i;
            }
        }
        newOutput[maxPosition] = 1.;
        return newOutput;
    }

    @Override
    public Object orderedListOfClasses() {
        double[] networkOutput = neuralNetwork.getOutput();
        ArrayList<IndexedValue> indexedValues = new ArrayList();
        for (int i = 0; i < networkOutput.length; i++) {
            indexedValues.add(IndexedValue.createIndexedValueFromIndexAndValue(i, networkOutput[i]));            
        }
        Collections.sort(indexedValues);
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < numberofPossibleClasses; i++) {
            labels.add(SymbolLabels.getLabelOfSymbolByIndex(indexedValues.get(
                    indexedValues.size() - i - 1).getIndex()));          
        }
        return labels;
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    public int getNumberofPossibleClasses() {
        return numberofPossibleClasses;
    }

    public void setNumberofPossibleClasses(int numberofPossibleClasses) {
        this.numberofPossibleClasses = numberofPossibleClasses;
    }
    
    
    
}
