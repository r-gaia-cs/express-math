/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ClassificationResult;
import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.classification.ShapeContextClassifier;
import br.usp.ime.faguilar.classification.neuralNetwork.NeuralNetworkClassifier;
import java.util.ArrayList;

/**
 *
 * @author Frank Aguilar
 */
public class ClassificationFilter implements HypothesisFilter{
    private Classifier classifier;

    public ClassificationFilter() {
//        classifier = new ShapeContextClassifier();
        classifier = new NeuralNetworkClassifier();
    }
    
    @Override
    public ArrayList<SymbolHypothesis> filter(ArrayList<SymbolHypothesis> hypothesis) {
        ArrayList<SymbolHypothesis> selected = new ArrayList();
        ClassificationResult clasResult;
//        ArrayList<String> listOfClasses;
        for (SymbolHypothesis symbolHypothesis : hypothesis) {
            Classifible classifible = new Classifible();
            classifible.setSymbol(symbolHypothesis.getSymbol());
            clasResult = (ClassificationResult) classifier.classify(classifible);
            if(clasResult != null){
//                listOfClasses = new ArrayList();
//                listOfClasses.add((String) clasResult.getMyClass());
//                symbolHypothesis.setLabels(listOfClasses);
                symbolHypothesis.setCost(clasResult.getCost());
                symbolHypothesis.setLabels((ArrayList<String>) classifier.orderedListOfClasses());
                selected.add(symbolHypothesis);
            }
        }
        return selected;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }
    
}
