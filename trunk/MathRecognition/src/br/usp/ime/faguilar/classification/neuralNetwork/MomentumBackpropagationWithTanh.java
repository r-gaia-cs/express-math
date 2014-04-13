/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification.neuralNetwork;

import org.neuroph.core.Connection;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.nnet.learning.MomentumBackpropagation;

/**
 *
 * @author Frank
 */
public class MomentumBackpropagationWithTanh extends MomentumBackpropagation{
    /**
     * This method implements weights update procedure for the single neuron for
     * the back propagation with momentum factor
     *
     * @param neuron neuron to update weights
     */
    @Override
    protected void updateNeuronWeights(Neuron neuron) {
        for (Connection connection : neuron.getInputConnections()) {
            double input = connection.getInput();
            if (input == 0) {
                continue;
            }

            // get the error for specified neuron,
            double neuronError = Math.tanh(neuron.getError());

            // tanh can be used to minimise the impact of big error values, which can cause network instability
            // suggested at https://sourceforge.net/tracker/?func=detail&atid=1107579&aid=3130561&group_id=238532
            // double neuronError = Math.tanh(neuron.getError());

            Weight weight = connection.getWeight();
            MomentumWeightTrainingData weightTrainingData = (MomentumWeightTrainingData) weight.getTrainingData();

            //double currentWeightValue = weight.getValue();
            double previousWeightValue = weightTrainingData.previousValue;
            double weightChange = this.learningRate * neuronError * input
                    + momentum * (weight.value - previousWeightValue);
            // save previous weight value
            //weight.getTrainingData().set(TrainingData.PREVIOUS_WEIGHT, currentWeightValue);
            weightTrainingData.previousValue = weight.value;


            // if the learning is in batch mode apply the weight change immediately
            if (this.isInBatchMode() == false) {
                weight.weightChange = weightChange;
                weight.value += weightChange;
            } else { // otherwise, sum the weight changes and apply them after at the end of epoch
                weight.weightChange += weightChange;
            }
        }
    }
}
