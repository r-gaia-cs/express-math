/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.segmentation.SymbolFeatures;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author frank
 */
public class ShapeContextClassifier implements Classifier{
    private ArrayList<Classifible> templates;

    protected Map<String, ClassificationResult> bestClasses;

    public void setTrainingData(ArrayList<Classifible> classifiables) {
        setTemplates(classifiables);
    }

    public void train() {
        SymbolFeatures.setUpNumberOfStrokesPerSymbol();
    }

    public Object classify(Classifible classifible) {
        ClassificationResult result = new ClassificationResult();
        ClassificationResult resultAux = new ClassificationResult();
        Point2D[] templatePoints = null;
        Point2D[] classifiblePoints = null;
        double[][] shapeContextTemplate, shapeContextClassifible;
        double diference = 0;
        double minDiference  = Double.MAX_VALUE;
        SymbolFeatures symbolFeatures = (SymbolFeatures) classifible.getAditionalFeatures();
        int numberOfStrokesOfClassifible = symbolFeatures.getNumberOfStrokes();
        bestClasses = new TreeMap<String, ClassificationResult>();
        int numberOfStrokesOfTemplate;
        for (Classifible aTemplate : templates) {
            numberOfStrokesOfTemplate = SymbolFeatures.getNumberOfStrokesForSymbol((String)
                    aTemplate.getMyClass());
            if(numberOfStrokesOfTemplate <= numberOfStrokesOfClassifible){
                templatePoints = (Point2D[]) aTemplate.getFeatures();
                classifiblePoints = (Point2D[]) classifible.getFeatures();
                shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
                shapeContextClassifible = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
                diference = CostShapeContextInside.getCost(shapeContextTemplate, shapeContextClassifible);
                if(diference < minDiference){
                    minDiference = diference;
                    result.setCost(minDiference);
                    result.setFeatures(aTemplate.getFeatures());
                    result.setMyClass(aTemplate.getMyClass());
                }

                if(bestClasses.containsKey((String) aTemplate.getMyClass())){
                    if(diference < bestClasses.get((String) aTemplate.getMyClass()).getCost()){
                        resultAux = new ClassificationResult();
                        resultAux.setCost(diference);
                        resultAux.setFeatures(aTemplate.getFeatures());
                        resultAux.setMyClass(aTemplate.getMyClass());
                        bestClasses.put((String) aTemplate.getMyClass(), resultAux);
                    }
                }
                else{
                    resultAux = new ClassificationResult();
                    resultAux.setCost(diference);
                    resultAux.setFeatures(aTemplate.getFeatures());
                    resultAux.setMyClass(aTemplate.getMyClass());

                    bestClasses.put((String) aTemplate.getMyClass(), resultAux);
                }
            }
        }
        return result;
    }

    public ArrayList<Classifible> getTemplates() {
        return templates;
    }

    public void setTemplates(ArrayList<Classifible> templates) {
        this.templates = templates;
    }

    public Object orderedListOfClasses() {
        ArrayList<ClassificationResult> orderedResults = new ArrayList<ClassificationResult>();
        for (ClassificationResult classificationResult : bestClasses.values()) {
            orderedResults.add(classificationResult);
        }
        Collections.sort(orderedResults, new Comparator<ClassificationResult>() {
            public int compare(ClassificationResult o1, ClassificationResult o2) {
                if(o1.getCost() > o2.getCost())
                    return 1;
                if(o1.getCost() < o2.getCost())
                    return -1;
                return 0;
            }
        });
        return orderedResults;
    }
}
