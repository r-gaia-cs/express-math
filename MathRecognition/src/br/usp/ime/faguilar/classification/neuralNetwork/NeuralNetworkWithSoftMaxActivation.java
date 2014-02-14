/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification.neuralNetwork;

import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.events.NNCalculatedEvent;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

/**
 *
 * @author Frank Aguilar
 */
public class NeuralNetworkWithSoftMaxActivation extends MultiLayerPerceptron{
    
    public NeuralNetworkWithSoftMaxActivation(TransferFunctionType transferFunctionType, int inputSize, 
            int numberOfHiddenUnits, int outputSize){
        super(transferFunctionType, 
                inputSize, numberOfHiddenUnits, outputSize);
    }
    
    /**
     * Performs calculation on whole network
     */
    @Override
    public void calculate() {
        for (Layer layer : getLayers()) {
            layer.calculate();
        }       
        double[] output1 = getOutput();
        double sum = 0;
        for (double d : output1) {
            sum += d;
        }
        for (Neuron neuron : getOutputNeurons()) {
            neuron.setOutput(neuron.getOutput() / sum);
        }
        fireNetworkEvent(new NNCalculatedEvent(this));
    }
    
}
