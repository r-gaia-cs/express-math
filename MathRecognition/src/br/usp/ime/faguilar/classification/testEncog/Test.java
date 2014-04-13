/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification.testEncog;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.SymbolLabels;
import br.usp.ime.faguilar.evaluation.ClassifierTest;
import br.usp.ime.faguilar.feature_extraction.NeuralNetworkFeatures;
import java.util.ArrayList;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import quicktime.std.StdQTConstants;

/**
 *
 * @author Frank
 */
public class Test {
    /**
	 * The input necessary for XOR.
	 */
	public static double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 },
			{ 0.0, 1.0 }, { 1.0, 1.0 } };

	/**
	 * The ideal data necessary for XOR.
	 */
	public static double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };
	
	/**
	 * The main method.
	 * @param args No arguments are used.
	 */
	public static void main(final String args[]) {
//                for (int i = -100; i < 100; i++) {
//                    System.out.println(Math.exp(Math.tanh(i)));
//                }
		
		// create a neural network, without using a factory
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationTANH(), false, 192));
		network.addLayer(new BasicLayer(new ActivationTANH(), true, 100));
		network.addLayer(new BasicLayer(new ActivationSoftMax(), false, 75));
		network.getStructure().finalizeStructure();
		network.reset();

		// create training data
                fillTrainData();
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
		
		// train the neural network
		final Backpropagation train = new Backpropagation(network, trainingSet, 0.005, 0);
                train.setBatchSize(1);
		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while(train.getError() > 0.0051);
		train.finishTraining();

		// test the neural network
		System.out.println("Neural Network Results:");
		for(MLDataPair pair: trainingSet ) {
			final MLData output = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
		}
		
		Encog.getInstance().shutdown();
	}
        
        public static void fillTrainData(){
            ClassifierTest classifierTest =  new ClassifierTest();
            classifierTest.readData();
            classifierTest.partData();
            ArrayList<Classifible> trainClassifibles = classifierTest.getTrainData();
            SymbolLabels.readCrohme2012Labels();
            XOR_INPUT = new double[trainClassifibles.size()][192];
            XOR_IDEAL = new double[trainClassifibles.size()][75];
            int outputSize = 75;
            int index = 0;
            for (Classifible classifible : trainClassifibles) {
                double[] intput = NeuralNetworkFeatures.extractMergedFeatures(classifible.getSymbol());
        
                double[] output = new double[outputSize];
                output[SymbolLabels.getIndexOfSymbolByLabel((String) classifible.getMyClass())] 
                        = 1;
                XOR_INPUT[index] = intput;
                XOR_IDEAL[index] = output;
                index++;
            }
        }
}
