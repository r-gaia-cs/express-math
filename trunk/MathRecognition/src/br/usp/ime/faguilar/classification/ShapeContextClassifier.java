/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.conversion.DsymbolToGraphConversor;
import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.evaluation.ShapeContextStatistics;
import br.usp.ime.faguilar.evaluation.SymbolTestData;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.feature_extraction.ShapeContextFeature;
import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.matching.GraphDeformationCalculator;
import br.usp.ime.faguilar.matching.Matching;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.matching.symbol_matching.DSymbolMatching;
import br.usp.ime.faguilar.matching.symbol_matching.StrokeFeature;
import br.usp.ime.faguilar.matching.symbol_matching.UserSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import br.usp.ime.faguilar.segmentation.Segmentation;
import br.usp.ime.faguilar.segmentation.SymbolFeatures;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
//import sun.util.resources.CalendarData;

/**
 *
 * @author frank
 */
public class ShapeContextClassifier implements Classifier{
    private ArrayList<Classifible> templates;
    private float alpha;
    private float beta;
    private float gama;
    private float theta;
    private Segmentation segmentationAlgorithm;

    protected Map<String, ClassificationResult> bestClasses;

    public ShapeContextClassifier(){
        alpha = (float) 1.;
        beta = (float) 1.;
        gama = (float) 0.7;
        theta = (float) 0.113;
        segmentationAlgorithm = null;
    }

    public void setTrainingData(ArrayList<Classifible> classifiables) {
        HashMap<UserSymbol, Integer> map = new HashMap<UserSymbol, Integer>();
        templates = new ArrayList<Classifible>();
        int maxNumberOfSymbolsPerUser = 3;
        for (Classifible classifible : classifiables) {
            if(map.containsKey(classifible.getUserSymbol())){
                if(map.get(classifible.getUserSymbol()) < maxNumberOfSymbolsPerUser)
                    templates.add(classifible);
                map.put(classifible.getUserSymbol(), map.get(classifible.getUserSymbol()) + 1);
            }else{
                templates.add(classifible);
                map.put(classifible.getUserSymbol(), 1);
            }
        }
//        System.out.println(SymbolTestData.getFrequencies(templates));
//        setTemplates(classifiables);
    }

    public void train() {
        SymbolFeatures.setUpNumberOfStrokesPerSymbol();
    }

    // TO CLASSIFY USING STROKES INFO
    public Object classify(Classifible classifible) {
        ClassificationResult result = new ClassificationResult();
        ClassificationResult resultAux = new ClassificationResult();
        Point2D[] templatePoints = null;
        Point2D[] classifiblePoints = null;
        double[][] matrixShapeContextTemplate, matrixShapeContextClassifible;
        double diference = 0;
        double minDiference  = Double.MAX_VALUE;
        bestClasses = new TreeMap<String, ClassificationResult>();
        boolean continueEvaluation;
        DSymbol templateSymbol;
        DSymbol inputSymbol = classifible.getSymbol();
        ShapeContext shapeContextTemplate, shapeContextInput;
        double shapeContextDistance = 0, structureDistance = 0;
        float maxStructuralDiference;
        ShapeContextFeature inputFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(inputSymbol, 
                MatchingParameters.numberOfPointPerSymbol);

//        PreprocessingAlgorithms.getNShapeContetxFeatures(inputSymbol, N)
//
//        classifiblePoints = PreprocessingAlgorithms.getNPoints(inputSymbol, MatchingParameters.numberOfPointPerSymbol);
//        shapeContextInput = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
//        matrixShapeContextClassifible = shapeContextInput.getSC();
        Classifible bestTemplate = null;
        Matching bestMatching = null, matching;
        ShapeContextFeature bestTemplateFeatures = null;
        for (Classifible aTemplate : templates) {
            continueEvaluation = true;
            templateSymbol = aTemplate.getSymbol();
//            if((templateSymbol.getLabel().equals("-") || templateSymbol.getLabel().equals("\\frac")) &&
//                    (inputSymbol.getWidth() <= 3 || inputSymbol.size() > 1 ))
//                    (inputSymbol.size() > 1 ))
//                continueEvaluation = false;
//            if(templateSymbol.size() != inputSymbol.size())
//                continueEvaluation = false;
//            if(inputSymbol.size() == 1)
//                continueEvaluation = true;
            if(continueEvaluation){
//                if(inputSymbol.size() == 1){
                ///// TO TEST WITH THETA PARAMETER
//                if(inputSymbol.size() == 1){
                    ShapeContextFeature templateFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(
                            templateSymbol, MatchingParameters.numberOfPointPerSymbol);
//                    matching = CostShapeContextInside.getCost(templateFeatures, inputFeatures);
                    matching = CostShapeContextInside.getCost(templateSymbol, templateFeatures, inputSymbol, inputFeatures);
                    shapeContextDistance = matching.getCost();
                    structureDistance = templateFeatures.getShapeContext().getCenterVertex().compareShapeContextExpression(
                            inputFeatures.getShapeContext().getCenterVertex());

//                    templatePoints = PreprocessingAlgorithms.getNPoints(templateSymbol, MatchingParameters.numberOfPointPerSymbol);
//                    shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
//                    matrixShapeContextTemplate = shapeContextTemplate.getSC();
//                    shapeContextDistance = CostShapeContextInside.getCost(matrixShapeContextTemplate, matrixShapeContextClassifible);
//                    structureDistance = shapeContextTemplate.getCenterVertex().compareShapeContextExpression(
//                            shapeContextInput.getCenterVertex());
                    diference = alpha * shapeContextDistance + (1 - alpha) * structureDistance;
//                    maxStructuralDiference = Float.valueOf((String)ShapeContextStatistics.getContextsCeterPointStatisticOf((String)
//                    aTemplate.getMyClass()));
//                    if(inputSymbol.size() > 1 && (structureDistance > maxStructuralDiference))
//                        diference = Double.MAX_VALUE;

//            }else{
//                templatePoints = PreprocessingAlgorithms.getNPoints(templateSymbol, MatchingParameters.numberOfPointPerSymbol);
//                classifiblePoints = PreprocessingAlgorithms.getNPoints(inputSymbol, MatchingParameters.numberOfPointPerSymbol);
//                shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
//                shapeContextInput = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
//                structureDistance = shapeContextTemplate.getCenterVertex().compareShapeContextExpression(
//                        shapeContextInput.getCenterVertex());
//                    double costOfCompundSymbol = getCostOfCompundSymbol(inputSymbol);
//                    diference =  (getAlpha() * costOfCompundSymbol) +
//                            (1- getAlpha()) * structureDistance;
//                }
                    /////////
//                    StrokeFeature[] extractFeatures = DSymbolMatching.extractFeatures(templateSymbol);
//                    StrokeFeature[] extractFeatures1 = DSymbolMatching.extractFeatures(inputSymbol);
//                    extractFeatures[0].setGama(getGama());
//                }else//(inputSymbol.size() > 1)
//                    if(inputSymbol.size() > 1){
//                        diference =  (getAlpha() * diference) + ((1 - getAlpha()) *
//                                DSymbolMatchingCost(templateSymbol, inputSymbol));
//                    }
                
//                    else
//                        diference = extractFeatures[0].distance(extractFeatures1[0]) + diference;
//                        diference =  DSymbolMatchingCost(templateSymbol, inputSymbol);
                if(diference < minDiference){
                    minDiference = diference;
                    result.setCost(minDiference);
                    result.setFeatures(aTemplate.getFeatures());
                    result.setMyClass(aTemplate.getMyClass());
                    bestTemplate = aTemplate;
                    bestMatching = matching;
                    bestTemplateFeatures = templateFeatures;
//                    if(inputSymbol.size() == 1)
//                        Segmentation.bestCostAt[((OrderedStroke) inputSymbol.get(0)).getIndex()] = shapeContextDistance;
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
//        if(result.getMyClass().equals("-") || result.getMyClass().equals("|") ||
//                result.getMyClass().equals("\\frac") || result.getMyClass().equals("'")
//                || result.getMyClass().equals("1") || result.getMyClass().equals(","))
//            result.setCost(result.getCost()* 5);

        double dificulty = 0;
        //        SET WIGHTS TO COST USING SHAPE COMPLEXITY
//        if(inputSymbol.size() == 1){
//            dificulty = PreprocessingAlgorithms.shapeComplexity(inputSymbol);
//            if(dificulty > 0)
////                        dificulty = (float) ((Math.log10(1 / dificulty) / Math.log10(2)) * 1.5);
//                dificulty = (float) (Math.log(1 / dificulty));
//            else
//                dificulty = (float) 7;
//            result.setCost(result.getCost()* dificulty);
//        }
        
        //        SET WEIGHTS TO COST USING SHAPE CONTEXT STATISTICS
        //        dificulty = Float.valueOf((String)ShapeContextStatistics.getsContextstatisticOf((String) result.getMyClass()));
        //        result.setCost(result.getCost()* dificulty);

        //using cost of central vertex as cost for comparing segmentation
//        MatchingParameters.setUpParametersForSegmentationCost();
//        ShapeContextFeature templateFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(bestTemplate.getSymbol(),
//                MatchingParameters.numberOfPointPerSymbol);
//       inputFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(inputSymbol,
//                MatchingParameters.numberOfPointPerSymbol);
//
//        structureDistance = templateFeatures.getShapeContext().getCenterVertex().compareShapeContextExpression(
//                            inputFeatures.getShapeContext().getCenterVertex());
//        result.setCost(structureDistance);
//
//        MatchingParameters.setDefaultSymbolParameters();

//        threashold using normalized distance
//        if(bestMatching != null){
//            DSymbol bestSymbol = bestTemplate.getSymbol();
//            double normalizedCoordsDistance = ShapeContextFeature.normalizedCoordsDistance(bestSymbol,
//                    bestTemplateFeatures.getCoords(), inputSymbol, inputFeatures.getCoords(),
//                    bestMatching.getMatching());
//            if(inputSymbol.size() > 1 && normalizedCoordsDistance > 0.15)
//                result = null;
//        }

//        treashold using min values of shape context
//        if (result.getMyClass() != null && inputSymbol.size() > 1 && !segmentationAlgorithm.goodGroupByDistance(
//                inputSymbol, segmentationAlgorithm.getMinDist())){
//            float sConextMinValue = Float.valueOf((String)ShapeContextStatistics.getsContextstatisticOf((String)
//                    result.getMyClass()));
//            if(result.getCost() > sConextMinValue + 0. * sConextMinValue)
//                    result = null;
//        }
//        if(result != null && result.getMyClass() == null)
//            result = null;
        return result;
    }

    private double getCostOfCompundSymbol(DSymbol symbol){
        double cost = 0;
        for (DStroke stroke : symbol) {
            cost += Segmentation.bestCostAt[((OrderedStroke) stroke).getIndex()];
        }
        return cost;
    }

    private double widthOfATemplate(Classifible aTemplate){
        Point2D[] templatePoints = (Point2D[]) aTemplate.getFeatures();
        double width = 0;
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        for (Point2D point2D : templatePoints) {
            if(point2D.getX() < minX)
                minX = point2D.getX();
            if(point2D.getX() > maxX)
                maxX = point2D.getX();
        }
        width  = maxX - minX;
        return width;
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
//   NOT USED
    private double graphDeformationDistance(DSymbol templateSymbol, DSymbol inputSymbol) {
        double distance = 0;
        Graph inputGraph = DsymbolToGraphConversor.convert(inputSymbol);
        Graph templateGraph = DsymbolToGraphConversor.convert(templateSymbol);
        GraphDeformationCalculator calculator = GraphDeformationCalculator.
                newInstanceFromTemplateAndIputGraphs(templateGraph, inputGraph);
        distance = calculator.getCost();
        return distance;
    }

    private double DSymbolMatchingCost(DSymbol templateSymbol, DSymbol inputSymbol) {
        DSymbolMatching symbolMatching = DSymbolMatching.newInstanceFromSymbolsToMatch(
                templateSymbol, inputSymbol);
        symbolMatching.setBeta(getBeta());
        symbolMatching.setGama(getGama());
        double distance = symbolMatching.getCost();
        return distance;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float getGama() {
        return gama;
    }

    public void setGama(float gama) {
        this.gama = gama;
    }

    public Segmentation getSegmentationAlgorithm() {
        return segmentationAlgorithm;
    }

    public void setSegmentationAlgorithm(Segmentation segmentationAlgorithm) {
        this.segmentationAlgorithm = segmentationAlgorithm;
    }

}
