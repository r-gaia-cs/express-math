/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.cost.CostShapeContextInside;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class ShapeContextClassifier implements Classifier{
    private ArrayList<Classifible> templates;

    public void setTrainingData(ArrayList<Classifible> classifiables) {
        setTemplates(classifiables);
    }

    public void train() {
        
    }

    public Object classify(Classifible classifible) {
        ClassificationResult result = new ClassificationResult();
        Point2D[] templatePoints = null;
        Point2D[] classifiblePoints = null;
        double[][] shapeContextTemplate, shapeContextClassifible;
        double diference = 0;
        double minDiference  = Double.MAX_VALUE;
        for (Classifible aTemplate : templates) {
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
        }
        return result;
    }

    public ArrayList<Classifible> getTemplates() {
        return templates;
    }

    public void setTemplates(ArrayList<Classifible> templates) {
        this.templates = templates;
    }
}
