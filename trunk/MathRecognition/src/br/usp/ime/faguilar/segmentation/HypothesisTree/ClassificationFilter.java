/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ClassificationResult;
import br.usp.ime.faguilar.classification.ShapeContextClassifier;
import java.util.ArrayList;

/**
 *
 * @author Frank Aguilar
 */
public class ClassificationFilter implements HypothesisFilter{
    private ShapeContextClassifier classifier;

    public ClassificationFilter() {
        classifier = new ShapeContextClassifier();
    }
    
    @Override
    public ArrayList<SymbolHypothesis> filter(ArrayList<SymbolHypothesis> hypothesis) {
        ArrayList<SymbolHypothesis> selected = new ArrayList();
        ClassificationResult clasResult;
        ArrayList<String> listOfClasses;
        for (SymbolHypothesis symbolHypothesis : hypothesis) {
            Classifible classifible = new Classifible();
            classifible.setSymbol(symbolHypothesis.getSymbol());
            clasResult = (ClassificationResult) classifier.classify(classifible);
            if(clasResult != null){
                listOfClasses = new ArrayList();
                listOfClasses.add((String) clasResult.getMyClass());
                symbolHypothesis.setLabels(listOfClasses);
                symbolHypothesis.setCost(clasResult.getCost());
                selected.add(symbolHypothesis);
            }
        }
        return selected;
    }

    public ShapeContextClassifier getClassifier() {
        return classifier;
    }

    public void setClassifier(ShapeContextClassifier classifier) {
        this.classifier = classifier;
    }
    
}
