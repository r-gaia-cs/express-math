/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.SymbolClasses;
import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.feature_extraction.ShapeContextFeature;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.matching.Matching;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.util.FilesUtil;
import br.usp.ime.faguilar.util.SymbolUtil;
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
    public static String SCONTEXT_STATISTCS_FILE_NAME = EvaluationView.TEMPLATES_DIR +
            "scontext-mean-plus-sdeviation-values.txt";//"scontext-mean-plus-sdeviation-values.txt";//"scontext-max-values.txt";

    public static String SCONTEXT_STATISTCS_CENTER_FILE_NAME = EvaluationView.TEMPLATES_DIR +
            "scontext-center-point.txt";
    
    private Map<String, Object> symbolStatistics;

    private static Map<String, Object> sContextStatistics = new HashMap<String, Object>();
    
    private static Map<String, Object> sContextStatisticsCenterPoint = new HashMap<String, Object>();
    
    public ShapeContextStatistics(){
        symbolStatistics = new HashMap<String, Object>();
    }

    public static void readShapeContextstatistics(){
        if(sContextStatistics.isEmpty()){
            String fileContent = FilesUtil.getContentAsString(SCONTEXT_STATISTCS_FILE_NAME);
            String[] arrayData = fileContent.split("\n");

            for (int i = 0; i < arrayData.length; i++) {
                String line = arrayData[i];
                String[] lineArray = line.split("\t");
                sContextStatistics.put(lineArray[0], lineArray[1]);
            }
        }
        if(sContextStatisticsCenterPoint.isEmpty()){
            String fileContent = FilesUtil.getContentAsString(SCONTEXT_STATISTCS_CENTER_FILE_NAME);
            String[] arrayData = fileContent.split("\n");

            for (int i = 0; i < arrayData.length; i++) {
                String line = arrayData[i];
                String[] lineArray = line.split("\t");
                sContextStatisticsCenterPoint.put(lineArray[0], lineArray[1]);
            }
        }
    }

    public static Object getsContextstatisticOf(String symbolLabel){
        return sContextStatistics.get(symbolLabel);
    }
    
    public static Object getContextsCeterPointStatisticOf(String symbolLabel){
        return sContextStatisticsCenterPoint.get(symbolLabel);
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
        double[][] matrixShapeContextTemplate;
        double[][] matrixShapeContextClassifible ;
        ShapeContext shapeContextTemplate;
        ShapeContext shapeContextClassifible;
        double diference = 0;
        int limit;
        int count = 1;;
        for (Object label : keySet) {
            System.out.print("class num " + count++);
            System.out.print(", label: " + label);
            matchingResults = new ArrayList<Double>();
            classifiables = map.get(label);
            limit = classifiables.size();
            System.out.println(", num. elements: " + limit);
            for (int i = 0; i < limit -1 ; i++) {
                for (int j = i + 1; j < limit; j++) {
//                    TO TEST GET DATA FROM SHAPE CONTEXT
//                    Point2D.Double[] templatePoints = (Point2D.Double[]) classifiables.get(i).getFeatures();
//                    Point2D.Double[]  classifiblePoints = (Point2D.Double[]) classifiables.get(j).getFeatures();
//                    matrixShapeContextTemplate = CostShapeContextInside.calculateShapeContextMatrixFromPoints2D(templatePoints);
//                    matrixShapeContextClassifible = CostShapeContextInside.calculateShapeContextMatrixFromPoints2D(classifiblePoints);
//                    diference = CostShapeContextInside.getCost(matrixShapeContextTemplate, matrixShapeContextClassifible);

//                    TO GET DATA FROM SHAPE CONTEXT OF CENTER POINT
                    Point2D[] templatePoints = PreprocessingAlgorithms.getNPoints(classifiables.get(i).getSymbol(), MatchingParameters.numberOfPointPerSymbol);
                    Point2D[]  classifiblePoints = PreprocessingAlgorithms.getNPoints(classifiables.get(j).getSymbol(), MatchingParameters.numberOfPointPerSymbol);
                    shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
                    shapeContextClassifible = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
                    diference = shapeContextTemplate.getCenterVertex().compareShapeContextExpression(
                                        shapeContextClassifible.getCenterVertex());

                    matchingResults.add(diference);
                }
                
            }
            symbolStatistics.put((String) label, matchingResults);
        }
    }

    public void calculateShapeContextStatistics(){
        ClassifierTest classifierTest = new ClassifierTest();
        classifierTest.setSelectedClasses(SymbolClasses.getClassesAsArrayList());
        classifierTest.readData();
        SymbolTestData symbolData = classifierTest.getSymbolData();
        Map<Object, List<Classifible>> map = symbolData.getMap();
        Set<Object> keySet = map.keySet();
        ArrayList<Double> matchingResults;
        List<Classifible> classifiables;
        double[][] matrixShapeContextTemplate;
        double[][] matrixShapeContextClassifible ;
        ShapeContext shapeContextTemplate;
        ShapeContext shapeContextClassifible;
        double diference = 0;
        double minValue;
        int limit;
        int counter = 0;
        Matching matching;
        for (Object label : keySet) {
            matchingResults = new ArrayList<Double>();
            classifiables = map.get(label);
            limit = classifiables.size();//classifiables.size();//Math.min(classifiables.size(), 30);
            counter++;
            String data = "counter: " + counter + " , class: " + label + "\n";
            FilesUtil.write("counterClasses.txt", data);
            for (int i = 0; i < limit ; i++) {
                minValue = Double.MAX_VALUE;
                
                for (int j = 0; j < limit; j++) {
//                    TO GET MIN COST
                    if (j != i){
//                        TO USE S.C. WITHOUT ANGLES
//                        Point2D[] templatePoints = PreprocessingAlgorithms.getNPoints(classifiables.get(i).getSymbol(), MatchingParameters.numberOfPointPerSymbol);
//                        Point2D[]  classifiblePoints = PreprocessingAlgorithms.getNPoints(classifiables.get(j).getSymbol(), MatchingParameters.numberOfPointPerSymbol);
//                        shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
//                        shapeContextClassifible = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
//                        diference = CostShapeContextInside.getCost(shapeContextTemplate.getSC(), shapeContextClassifible.getSC());
//                        TO USE S.C. WITH ANGLES
//                        ShapeContextFeature templateFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(
//                            classifiables.get(i).getSymbol(), MatchingParameters.numberOfPointPerSymbol);
//                        ShapeContextFeature inputFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(
//                            classifiables.get(j).getSymbol(), MatchingParameters.numberOfPointPerSymbol);
//                        matching = CostShapeContextInside.getCost(templateFeatures, inputFeatures);
//                        diference = matching.getCost();
                        
//                        to use s.context of cnter point
                        Point2D[] templatePoints = PreprocessingAlgorithms.getNPoints(classifiables.get(i).getSymbol(), 
                                MatchingParameters.numberOfPointPerSymbol);
                        Point2D[]  classifiblePoints = PreprocessingAlgorithms.getNPoints(classifiables.get(j).getSymbol(), 
                                MatchingParameters.numberOfPointPerSymbol);
                        shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
                        shapeContextClassifible = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
                        diference = shapeContextTemplate.getCenterVertex().compareShapeContextExpression(
                                        shapeContextClassifible.getCenterVertex());
                        if (diference < minValue)
                            minValue = diference;
                    }
                }
                matchingResults.add(minValue);
            }
            symbolStatistics.put((String) label, matchingResults);
        }
    }

    public void calculateShapeComplexityStatistics(){
        ClassifierTest classifierTest = new ClassifierTest();
        classifierTest.setSelectedClasses(SymbolClasses.getClassesAsArrayList());
        classifierTest.readData();
        SymbolTestData symbolData = classifierTest.getSymbolData();
        Map<Object, List<Classifible>> map = symbolData.getMap();
        Set<Object> keySet = map.keySet();
        ArrayList<Double> matchingResults;
        List<Classifible> classifiables;
        double[][] matrixShapeContextTemplate;
        double[][] matrixShapeContextClassifible ;
        ShapeContext shapeContextTemplate;
        ShapeContext shapeContextClassifible;
        double diference = 0;
        int limit;
        for (Object label : keySet) {
            matchingResults = new ArrayList<Double>();
            classifiables = map.get(label);
            limit = classifiables.size();
            for (int i = 0; i < limit ; i++) {
                double shapeComplexity = PreprocessingAlgorithms.shapeComplexity(classifiables.get(i).getSymbol());
                matchingResults.add(shapeComplexity);
            }
            symbolStatistics.put((String) label, matchingResults);
        }
    }
    
    public void calculateSrokeDistanceStatistics(){
        ClassifierTest classifierTest = new ClassifierTest();
        classifierTest.setSelectedClasses(SymbolClasses.getClassesAsArrayList());
        classifierTest.readData();
        SymbolTestData symbolData = classifierTest.getSymbolData();
        Map<Object, List<Classifible>> map = symbolData.getMap();
        Set<Object> keySet = map.keySet();
        ArrayList<Double> matchingResults;
        List<Classifible> classifiables;
        double diference = 0;
        int limit;
        for (Object label : keySet) {
            matchingResults = new ArrayList<Double>();
            classifiables = map.get(label);
            limit = classifiables.size();
            for (int i = 0; i < limit ; i++) {
                double distance = SymbolUtil.minNormalizedDistanceBetweenStrokeCentroids(classifiables.get(i).getSymbol());
                matchingResults.add(distance);
            }
            symbolStatistics.put((String) label, matchingResults);
        }
    }
    
    public String getCompleteStatisticsAsString(){
        String string = "";
        Set<String> keySet = symbolStatistics.keySet();
        ArrayList<Double> matchingResults;
        String stringAux = "";
        for (String key : keySet) {
            stringAux = (key + " " ); 
            matchingResults = (ArrayList<Double>) symbolStatistics.get(key);
            for (Double result : matchingResults) {
                stringAux += (result + " "); 
            }
            string += (stringAux + "\n");
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

