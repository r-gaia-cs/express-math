/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.classification.ClassificationResult;
import br.usp.ime.faguilar.classification.Classifier;
import java.util.ArrayList;

/**
 *
 * @author frank.aguilar
 */
public class SegmentationResult {
    ArrayList<ClassificationResult> segmentation;

    public SegmentationResult() {
        segmentation = new ArrayList<ClassificationResult>();
    }
    

    public boolean addSymbolHypothesis(ClassificationResult e) {
        return segmentation.add(e);
    }

    public ClassificationResult getSymbolHypothesisAt(int i) {
        return segmentation.get(i);
    }
    
    
    
    /**
     * Returns total cost of this segmentation
     * @return total cost of segmentation
     */
    public double getTotalcost(){
        double cost = -1;
        for (ClassificationResult symbolHypothesis : segmentation)
            cost += symbolHypothesis.getCost();
        return cost;
    }
    
    public String toString(){
        String string = "";
        for (ClassificationResult classificationResult : segmentation) {
            string += (classificationResult + "\n");
        }
        return string;
    }
            
}
