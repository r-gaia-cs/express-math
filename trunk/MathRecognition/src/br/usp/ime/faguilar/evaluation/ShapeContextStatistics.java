/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.SymbolClasses;
import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.guis.EvaluationView;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author frank.aguilar
 */
public class ShapeContextStatistics {

    public static String STATISTCS_FILE_NAME = EvaluationView.TEMPLATES_DIR +
            "statistical-data.txt";
    private Map<String, Object> symbolStatistics;
    
    public ShapeContextStatistics(){
        symbolStatistics = new HashMap<String, Object>();
    }

    public void readSymbolStatistics(String fileName){
        ArrayList<ShapeContextPerSymbol> readData = ShapeContextPerSymbol.readData(fileName);
        for (ShapeContextPerSymbol shapeContextPerSymbol : readData) {
            if(!symbolStatistics.containsKey(shapeContextPerSymbol.getLabel()))
                symbolStatistics.put(shapeContextPerSymbol.getLabel(), shapeContextPerSymbol);
        }
    }

    public Object getDataOf(String symbolLabel){
        return symbolStatistics.get(symbolLabel);
    }

    public boolean containsDataOf(String symbolLabel){
        return symbolStatistics.containsKey(symbolLabel);
    }

    public void calculateStatistics(){
        ClassifierTest classifierTest = new ClassifierTest();
        classifierTest.setSelectedClasses(SymbolClasses.getClassesAsArrayList());
        classifierTest.readData();
        SymbolTestData symbolData = classifierTest.getSymbolData();
        Map<Object, List<Classifible>> map = symbolData.getMap();
        Set<Object> keySet = map.keySet();
        ArrayList<Double> matchingResults;
        List<Classifible> classifiables;
        double[][] shapeContextTemplate;
        double[][] shapeContextClassifible ;
        double diference = 0;
        
        for (Object label : keySet) {
            matchingResults = new ArrayList<Double>();
            classifiables = map.get(label);
            for (int i = 0; i < classifiables.size() - 1; i++) {
                for (int j = i + 1; j < classifiables.size(); j++) {
                    Point2D.Double[] templatePoints = (Point2D.Double[]) classifiables.get(i).getFeatures();
                    Point2D.Double[]  classifiblePoints = (Point2D.Double[]) classifiables.get(j).getFeatures();
                    shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
                    shapeContextClassifible = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
                    if(diference == 1)
                        System.out.println("dif is 1");
                    diference = CostShapeContextInside.getCost(shapeContextTemplate, shapeContextClassifible);
                    matchingResults.add(diference);
                }
                
            }
            symbolStatistics.put((String) label, matchingResults);
        }
    }
    
    public String getCompleteStatisticsAsString(){
        String string = "";
        Set<String> keySet = symbolStatistics.keySet();
        ArrayList<Double> matchingResults;
        for (String key : keySet) {
            string += (key + " " ); 
            matchingResults = (ArrayList<Double>) symbolStatistics.get(key);
            for (Double result : matchingResults) {
                string += (result + " "); 
            }
            string += "\n";
        }
        return string;
    }
    
    public String getResumeStatisticsAsString(){
        String string = "";
        Set<String> keySet = symbolStatistics.keySet();
        ArrayList<Double> matchingResults;
        double max, mean, standardDeviation = 0.;
        double sum = 0;
        for (String key : keySet) {
            string += (key + " " ); 
            matchingResults = (ArrayList<Double>) symbolStatistics.get(key);
            max = Double.MIN_VALUE;//Collections.max(matchingResults);
            sum = 0;
            for (Double result : matchingResults) {
                if(result > max)
                    max = result;
                sum+= result;
            }
            mean = sum / matchingResults.size();
            string += (mean + " ");
            string += (standardDeviation + " ");
            string += (max + " ");
            string += "\n";
        }
        return string;
    }
    
//    public ArrayList getStatisticsAsString(){
//        ArrayList<Integer> counts
//        Set<String> keySet = symbolStatistics.keySet();
//        ArrayList<Double> matchingResults;
//        for (String key : keySet) {
//            string += (key + " " ); 
//            matchingResults = (ArrayList<Double>) symbolStatistics.get(key);
//            for (Double result : matchingResults) {
//                string += (result + " "); 
//            }
//            string += "\n";
//        }
//        return string;
//    }
}

