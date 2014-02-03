/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.conversion.DsymbolToGraphConversor;
import br.usp.ime.faguilar.cost.CostPerTemplate;
import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.cost.GeneralizedShapeContext;
import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.cost.ShapeContextVector;
import static br.usp.ime.faguilar.cost.ShapeContextVector.bestMatchingCost;
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
import br.usp.ime.faguilar.util.Util;
import edu.princeton.cs.algs4.Edge;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import sun.util.resources.CalendarData;

/**
 *
 * @author frank
 */
public class ShapeContextClassifier extends Classifier{
    public final static int NUMBER_OF_SELECTED_POINTS = 10; 
    public final static float RATIO_BEST_CLASES = 20;
    private ArrayList<Classifible> templates;
    private float alpha;
    private float beta;
    private float gama;
    private float theta;
    private Segmentation segmentationAlgorithm;

    protected Map<String, List<ClassificationResult>> bestClasses;
    
    private static double[][] bestCosts;

    public ShapeContextClassifier(){
        alpha = (float) 1.;
        beta = (float) 1.;
        gama = (float) 0.7;
        theta = (float) 0.113;
        segmentationAlgorithm = null;
//        ShapeContextStatistics.readShapeContextstatistics();
    }

    public void setTrainingData(ArrayList<Classifible> classifiables) {
        templates = new ArrayList<Classifible>();
        templates.addAll(classifiables);
//        TO FILTER
//        HashMap<UserSymbol, Integer> map = new HashMap<UserSymbol, Integer>();
//        templates = new ArrayList<Classifible>();
//        int maxNumberOfSymbolsPerUser = 3;
//        for (Classifible classifible : classifiables) {
//            if(map.containsKey(classifible.getUserSymbol())){
//                if(map.get(classifible.getUserSymbol()) < maxNumberOfSymbolsPerUser)
//                    templates.add(classifible);
//                map.put(classifible.getUserSymbol(), map.get(classifible.getUserSymbol()) + 1);
//            }else{
//                templates.add(classifible);
//                map.put(classifible.getUserSymbol(), 1);
//            }
//        }
//        map.clear();
//        map = null;
//        System.out.println(SymbolTestData.getFrequencies(templates));
//        setTemplates(classifiables);
    }

    public void train() {
        SymbolFeatures.setUpMinimumNumberOfStrokesPerSymbol();
    }

//     TO CLASSIFY USING STROKES INFO
//    public Object classify(Classifible classifible) {
//        ClassificationResult result = new ClassificationResult();
//        ClassificationResult resultAux = new ClassificationResult();
//        Point2D[] templatePoints = null;
//        Point2D[] classifiblePoints = null;
//        double[][] matrixShapeContextTemplate, matrixShapeContextClassifible;
//        double diference = 0;
//        double minDiference  = Double.MAX_VALUE;
//        bestClasses = new HashMap<String, List<ClassificationResult>>();
//        boolean continueEvaluation;
//        DSymbol templateSymbol;
//        DSymbol inputSymbol = classifible.getSymbol();
//        ShapeContext shapeContextTemplate, shapeContextInput;
//        double shapeContextDistance = 0, structureDistance = 0;
//        float maxStructuralDiference;
////        ShapeContextFeature inputFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(inputSymbol,
////                MatchingParameters.numberOfPointPerSymbol);
//        ShapeContextFeature inputFeatures = PreprocessingAlgorithms.getNGeneralizedShapeContetxFeatures(inputSymbol,
//                MatchingParameters.numberOfPointPerSymbol);
//
////        PreprocessingAlgorithms.getNShapeContetxFeatures(inputSymbol, N)
////
////        classifiblePoints = PreprocessingAlgorithms.getNPoints(inputSymbol, MatchingParameters.numberOfPointPerSymbol);
////        shapeContextInput = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
////        matrixShapeContextClassifible = shapeContextInput.getSC();
//        Classifible bestTemplate = null;
//        Matching bestMatching = null, matching;
//        ShapeContextFeature bestTemplateFeatures = null;
//
//        ArrayList<Classifible> selectedTemplates = betsCandidates(inputSymbol.size(),
//                ((GeneralizedShapeContext) inputFeatures.getShapeContext()).getShapeContextVectors(), RATIO_BEST_CLASES);
////        ArrayList<Classifible> selectedTemplates = getTemplates();
//
//        int[] allIndices = Util.sampleNumbers(MatchingParameters.numberOfPointPerSymbol, 0, MatchingParameters.numberOfPointPerSymbol -1);
//        double[] normalizedTerms = calculateNormalizedTerms(selectedTemplates, ((GeneralizedShapeContext) inputFeatures.getShapeContext()).getShapeContextVectors(), allIndices);
//        Classifible aTemplate;
//
//        for (int i = 0; i < selectedTemplates.size(); i++) {
//            aTemplate = selectedTemplates.get(i);
//            continueEvaluation = true;
//            templateSymbol = aTemplate.getSymbol();
////            if((templateSymbol.getLabel().equals("-") || templateSymbol.getLabel().equals("\\frac")) &&
////                    (inputSymbol.getWidth() <= 3 || inputSymbol.size() > 1 ))
////                    (inputSymbol.size() > 1 ))
////                continueEvaluation = false;
////            if(templateSymbol.size() != inputSymbol.size())
////                continueEvaluation = false;
////            if(inputSymbol.size() == 1)
////                continueEvaluation = true;
//            if(continueEvaluation){
////                if(inputSymbol.size() == 1){
//                ///// TO TEST WITH THETA PARAMETER
////                if(inputSymbol.size() == 1){
////                    ShapeContextFeature templateFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(
////                            templateSymbol, MatchingParameters.numberOfPointPerSymbol);
////                    matching = CostShapeContextInside.getCost(templateFeatures, inputFeatures);
//
////                TO USE NORMAL SHAPE CONTEXT
////                ShapeContextFeature templateFeatures = (ShapeContextFeature) aTemplate.getAditionalFeatures();
////                    matching = CostShapeContextInside.getCost(templateSymbol, templateFeatures, inputSymbol, inputFeatures);
////                    shapeContextDistance = matching.getCost();
////                    structureDistance = templateFeatures.getShapeContext().getCenterVertex().compareShapeContextExpression(
////                            inputFeatures.getShapeContext().getCenterVertex());
////                    TO USE GENERALIZED SHAPE CONTEXT WITH ALL POINTS
//
//
////                    templatePoints = PreprocessingAlgorithms.getNPoints(templateSymbol, MatchingParameters.numberOfPointPerSymbol);
////                    shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
////                    matrixShapeContextTemplate = shapeContextTemplate.getSC();
////                    shapeContextDistance = CostShapeContextInside.getCost(matrixShapeContextTemplate, matrixShapeContextClassifible);
////                    structureDistance = shapeContextTemplate.getCenterVertex().compareShapeContextExpression(
////                            shapeContextInput.getCenterVertex());
//                    shapeContextDistance = classificationSelectionCost(((GeneralizedShapeContext)
//                            inputFeatures.getShapeContext()).getShapeContextVectors(), allIndices,
//                    normalizedTerms, i);
//
//                    diference = alpha * shapeContextDistance + (1 - alpha) * structureDistance;
////                    maxStructuralDiference = Float.valueOf((String)ShapeContextStatistics.getContextsCeterPointStatisticOf((String)
////                    aTemplate.getMyClass()));
////                    if(inputSymbol.size() > 1 && (structureDistance > maxStructuralDiference))
////                        diference = Double.MAX_VALUE;
//
////            }else{
////                templatePoints = PreprocessingAlgorithms.getNPoints(templateSymbol, MatchingParameters.numberOfPointPerSymbol);
////                classifiblePoints = PreprocessingAlgorithms.getNPoints(inputSymbol, MatchingParameters.numberOfPointPerSymbol);
////                shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
////                shapeContextInput = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
////                structureDistance = shapeContextTemplate.getCenterVertex().compareShapeContextExpression(
////                        shapeContextInput.getCenterVertex());
////                    double costOfCompundSymbol = getCostOfCompundSymbol(inputSymbol);
////                    diference =  (getAlpha() * costOfCompundSymbol) +
////                            (1- getAlpha()) * structureDistance;
////                }
//                    /////////
////                    StrokeFeature[] extractFeatures = DSymbolMatching.extractFeatures(templateSymbol);
////                    StrokeFeature[] extractFeatures1 = DSymbolMatching.extractFeatures(inputSymbol);
////                    extractFeatures[0].setGama(getGama());
////                }else//(inputSymbol.size() > 1)
////                    if(inputSymbol.size() > 1){
////                        diference =  (getAlpha() * diference) + ((1 - getAlpha()) *
////                                DSymbolMatchingCost(templateSymbol, inputSymbol));
////                    }
//
////                    else
////                        diference = extractFeatures[0].distance(extractFeatures1[0]) + diference;
////                        diference =  DSymbolMatchingCost(templateSymbol, inputSymbol);
//                if(diference < minDiference){
//                    minDiference = diference;
//                    result.setCost(minDiference);
//                    result.setSymbol(aTemplate.getSymbol());
//                    result.setFeatures(aTemplate.getFeatures());
//                    result.setMyClass(aTemplate.getMyClass());
//                    bestTemplate = aTemplate;
////                    bestMatching = matching;
////                    bestTemplateFeatures = templateFeatures;
////                    if(inputSymbol.size() == 1)
////                        Segmentation.bestCostAt[((OrderedStroke) inputSymbol.get(0)).getIndex()] = shapeContextDistance;
//                }
//
////                if(bestClasses.containsKey((String) aTemplate.getMyClass())){
////                    resultAux = new ClassificationResult();
////                    resultAux.setCost(diference);
////                    resultAux.setFeatures(aTemplate.getFeatures());
////                    resultAux.setMyClass(aTemplate.getMyClass());
////                    bestClasses.get((String) aTemplate.getMyClass()).add(resultAux);
////                }
////                else{
////                    resultAux = new ClassificationResult();
////                    resultAux.setCost(diference);
////                    resultAux.setFeatures(aTemplate.getFeatures());
////                    resultAux.setMyClass(aTemplate.getMyClass());
////                    List<ClassificationResult> newList = new ArrayList<ClassificationResult>();
////                    newList.add(resultAux);
////                    bestClasses.put((String) aTemplate.getMyClass(), newList);
////                }
//            }
//        }
////        if(result.getMyClass().equals("-") || result.getMyClass().equals("|") ||
////                result.getMyClass().equals("\\frac") || result.getMyClass().equals("'")
////                || result.getMyClass().equals("1") || result.getMyClass().equals(","))
////            result.setCost(result.getCost()* 5);
//
////        COMMENTED TO EVALUATE ONLY CLASSIFIER
////
//
//        double dificulty = 0;
//        //        SET WIGHTS TO COST USING SHAPE COMPLEXITY
//        if(inputSymbol.size() == 1){
//            dificulty = PreprocessingAlgorithms.shapeComplexity(inputSymbol);
//            if(dificulty > 0)
////                        dificulty = (float) ((Math.log10(1 / dificulty) / Math.log10(2)) * 1.5);
//                dificulty = (float) (Math.log(1 / dificulty));
//            else
//                dificulty = (float) 7;
//            result.setCost(result.getCost()* dificulty);
//        }
//
//        //        SET WEIGHTS TO COST USING SHAPE CONTEXT STATISTICS
//        //        dificulty = Float.valueOf((String)ShapeContextStatistics.getsContextstatisticOf((String) result.getMyClass()));
//        //        result.setCost(result.getCost()* dificulty);
//
//        //using cost of central vertex as cost for comparing segmentation
////        MatchingParameters.setUpParametersForSegmentationCost();
////        ShapeContextFeature templateFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(bestTemplate.getSymbol(),
////                MatchingParameters.numberOfPointPerSymbol);
////       inputFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(inputSymbol,
////                MatchingParameters.numberOfPointPerSymbol);
////
////        structureDistance = templateFeatures.getShapeContext().getCenterVertex().compareShapeContextExpression(
////                            inputFeatures.getShapeContext().getCenterVertex());
////        result.setCost(structureDistance);
////
////        MatchingParameters.setDefaultSymbolParameters();
//
////        threashold using normalized distance
////        if(bestMatching != null){
////            DSymbol bestSymbol = bestTemplate.getSymbol();
////            double normalizedCoordsDistance = ShapeContextFeature.normalizedCoordsDistance(bestSymbol,
////                    bestTemplateFeatures.getCoords(), inputSymbol, inputFeatures.getCoords(),
////                    bestMatching.getMatching());
////            if(inputSymbol.size() > 1 && normalizedCoordsDistance > 0.15)
////                result = null;
////        }
//
////        treashold using min values of shape context
//        boolean resultModified = false;//identityAnalisis(result);
//
//        if (!resultModified && result.getMyClass() != null && inputSymbol.size() > 1
////                && !segmentationAlgorithm.goodGroupByDistance(inputSymbol, segmentationAlgorithm.getMinDist())
//                ){
//            Object sContextstatisticOf = ShapeContextStatistics.getsContextstatisticOf((String)
//                                                 result.getMyClass());
//            if(sContextstatisticOf != null){
//            float fraction = 1;//0.6f;
//            float sConextMinValue = (fraction) * Float.valueOf((String) sContextstatisticOf);
//
//            ShapeContextFeature templateFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(result.getSymbol(),
//                MatchingParameters.numberOfPointPerSymbol);
//            inputFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(inputSymbol,
//                MatchingParameters.numberOfPointPerSymbol);
//
//
////            ShapeContextFeature templateFeatures = (ShapeContextFeature) result.getAditionalFeatures();
////            Matching sc = CostShapeContextInside.getCost(result.getSymbol(), templateFeatures,
////                    inputSymbol, inputFeatures);
//            Matching sc = CostShapeContextInside.getCost(templateFeatures,
//                    inputFeatures);
//
//            shapeContextDistance = sc.getCost();
////            shapeContextDistance = ShapeContextStatistics.getWorstCost(sc);
////            if(result.getCost() > sConextMinValue + 0. * sConextMinValue)
//            if(shapeContextDistance > sConextMinValue)
//                    result = null;
////            else{
////                shapeContextDistance = ShapeContextStatistics.getWorstCost(sc);
////                System.out.println(result.getMyClass() + ": " + shapeContextDistance);
//////                result.setCost(shapeContextDistance);
////            }
//            }
//
//        }
////        else
////            if(result != null){
////            ShapeContextFeature templateFeatures = PreprocessingAlgorithms.getNGeneralizedShapeContetxFeatures(result.getSymbol(),
////                MatchingParameters.numberOfPointPerSymbol);
////            inputFeatures = PreprocessingAlgorithms.getNGeneralizedShapeContetxFeatures(inputSymbol,
////                MatchingParameters.numberOfPointPerSymbol);
////            Matching sc = CostShapeContextInside.getCost(result.getSymbol(), templateFeatures,
////                    inputSymbol, inputFeatures);
////
////            shapeContextDistance = ShapeContextStatistics.getWorstCost(sc);
////            System.out.println(result.getMyClass() + ": " + shapeContextDistance +
////                    " total cost: "+ sc.getCost());
////
////        }
//
//        if(result != null && result.getMyClass() == null)
//            result = null;
//        if(result != null && result.getMyClass() != null && result.getMyClass().equals(",")){
//            result.setMyClass("COMMA");
//            result.getSymbol().setLabel("COMMA");
//        }
//
//        //        COMMENTED TO EVALUATE ONLY CLASSIFIER -END
//
//        return result;
////        return selectResult();
//    }
    
    
//    TO TEST CLASSIFIER
    
    public Object classify(Classifible classifible) {
        ClassificationResult result = new ClassificationResult();
        double minDiference  = Double.MAX_VALUE;
        bestClasses = new HashMap<String, List<ClassificationResult>>();
        DSymbol inputSymbol = classifible.getSymbol();
        double shapeContextDistance = 0.;
        ShapeContextFeature inputFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(inputSymbol, 
                MatchingParameters.numberOfPointPerSymbol);
        Classifible aTemplate;
        
        for (int i = 0; i < templates.size(); i++) {
            aTemplate = templates.get(i);
            ShapeContextFeature templateFeatures = PreprocessingAlgorithms.getNShapeContetxFeatures(aTemplate.getSymbol(), 
                MatchingParameters.numberOfPointPerSymbol);
            Matching sc = CostShapeContextInside.getCost(templateFeatures, 
                    inputFeatures);
            shapeContextDistance = sc.getCost();
            if(shapeContextDistance < minDiference){
                    minDiference = shapeContextDistance;
                    result.setCost(shapeContextDistance);
                    result.setSymbol(aTemplate.getSymbol());
                    result.setFeatures(aTemplate.getFeatures());
                    result.setMyClass(aTemplate.getMyClass());
                }  
        }      
        return result;
    }
    
    public ArrayList<Classifible> filterTemplatesByMinimumNumberOfStrokes(int numberOfStrokesOfInputSymbol){
        ArrayList<Classifible> selected = new ArrayList<Classifible>();
        for (Classifible classifible : getTemplates()) {
            int minimumNumberOfStrokesForSymbol = SymbolFeatures.getMinimumNumberOfStrokesForSymbol((String) classifible.getMyClass());
            if(minimumNumberOfStrokesForSymbol == SymbolFeatures.UNDEFINED_NUMBER_OF_STROKES || 
                    minimumNumberOfStrokesForSymbol <= numberOfStrokesOfInputSymbol)
                selected.add(classifible);
        }
        return selected;
    }
    
    private boolean identityAnalisis(ClassificationResult result){
        if(result.getMyClass() != null && result.getMyClass().equals("i") || 
                result.getMyClass().equals("j")){
            boolean hasGoodRelation = false;
            DSymbol symbol = result.getSymbol();
            for (int i = 0; i < symbol.size() -1; i++) {
                for (int j = i + 1; j < symbol.size(); j++) {
                    DStroke stroke_i = symbol.get(i);
                    DStroke stroke_j = symbol.get(j);
                    double area_i = Math.sqrt(stroke_i.getHeightAsDouble() * stroke_i.getHeightAsDouble() + 
                            stroke_i.getWidthAsDouble() * stroke_i.getWidthAsDouble());
                    double area_j = Math.sqrt(stroke_j.getHeightAsDouble() * stroke_j.getHeightAsDouble() + 
                            stroke_j.getWidthAsDouble() * stroke_j.getWidthAsDouble());
                    if(area_i <= area_j *8 || area_j <= area_i *8 ){
                        hasGoodRelation = true;
                        break;
                    }
                }
            }
            if(hasGoodRelation){
                result.setCost(0);
                return true;
            }
        }
        return false;
    }
    
    public ClassificationResult selectResult(){
        List<ClassificationResult> bestList = new ArrayList<ClassificationResult>();
        for (String string : bestClasses.keySet()) {
            bestList.addAll(bestClasses.get(string));
        }
       
        Collections.sort(bestList, new Comparator<Object>() {
            
            public int compare(Object o1, Object o2) {
                return Double.compare(((ClassificationResult) o1).getCost(), 
                        ((ClassificationResult) o2).getCost());
            }
        });
        bestList = bestList.subList(0 , 1);
        bestClasses.clear();
        for (ClassificationResult classificationResult : bestList) {
            List<ClassificationResult> myList = bestClasses.get((String) classificationResult.getMyClass());
            if(myList == null)
                myList = new ArrayList<ClassificationResult>();
            myList.add(classificationResult);
            bestClasses.put((String) classificationResult.getMyClass(), myList);
        }
        bestList.clear();
        for (String key : bestClasses.keySet()) {
            if(bestList.size() < bestClasses.get(key).size())
                bestList = bestClasses.get(key);
//            else if(bestList.size() == bestClasses.get(key).size() && bestList.get(0).getCost() > 
//                    bestClasses.get(key).get(0).getCost())
//                bestList = bestClasses.get(key);
        }
        double bestCost = Double.MAX_VALUE;
        int selected = -1;
        int pos = 0;
        for (ClassificationResult classificationResult : bestList) {
            if(classificationResult.getCost() < bestCost){
                bestCost = classificationResult.getCost();
                selected = pos;
            }
            pos++;
        }
        return bestList.get(selected);
    }
    
    
    
    
    
    private ArrayList<Classifible> betsCandidates(int nummberOfStrokesOfInput, ShapeContextVector[][] inputVectors,float ratio){
        ArrayList<Classifible> selectedTemplated = filterTemplatesByMinimumNumberOfStrokes(nummberOfStrokesOfInput);
        
        int numberOfCandidates = (int) (selectedTemplated.size() / ratio);
        ArrayList<Classifible> bestCandidates = new ArrayList<Classifible>(numberOfCandidates);
        CostPerTemplate[] costs = new CostPerTemplate[selectedTemplated.size()];
        double cost;
        CostPerTemplate costPerTemplate;
//        Classifible template;
        
//        int[] selectedIndices = Util.randomNumbers(NUMBER_OF_SELECTED_POINTS, 0, inputVectors.length -1);
        int[] selectedIndices = Util.sampleNumbers(NUMBER_OF_SELECTED_POINTS, 0, inputVectors.length -1);
        double[] normalizerTerms = calculateNormalizedTerms(selectedTemplated, inputVectors, 
                selectedIndices);
        
        for (int i = 0; i < selectedTemplated.size(); i++) {
//            template = getTemplates().get(i);
//            cost = classificationSelectionCost(inputVectors, selectedIndices, 
//                    normalizerTerms, (ShapeContextFeature) template.getAditionalFeatures());
            cost = classificationSelectionCost(inputVectors, selectedIndices, 
                    normalizerTerms, i);
            costPerTemplate = new CostPerTemplate();
            costPerTemplate.setCost(cost);
            costPerTemplate.setIndex(i);
            costs[i] = costPerTemplate;
        }
        Arrays.sort(costs);
        for (int i = 0; i < numberOfCandidates; i++) {
            bestCandidates.add(selectedTemplated.get(costs[i].getIndex()));    
        }
        return bestCandidates;   
    }
    
    private double[] calculateNormalizedTerms(ArrayList<Classifible> templates1, 
            ShapeContextVector[][] inputVectors, int[] selectedIndices) {
        double[] normalizedTerms = new double[selectedIndices.length];
        bestCosts = new double[templates1.size()][selectedIndices.length];
        for (int i = 0; i < selectedIndices.length; i++) {
            double meanCost = 0;
            for (int j =0; j < templates1.size(); j++) {
                ShapeContextFeature scf = ((ShapeContextFeature) templates1.get(j).getAditionalFeatures());
                bestCosts[j][i] = ShapeContextVector.bestMatchingCost(inputVectors[selectedIndices[i]], 
                                                  ((GeneralizedShapeContext) scf.getShapeContext()).getShapeContextVectors());
                meanCost += bestCosts[j][i];
            }
            normalizedTerms[i] = meanCost / templates1.size();
        }
        return normalizedTerms;
    }
    
    private double classificationSelectionCost(ShapeContextVector[][] inputVectors, 
            int[] selectedIndices, double[] normalizerTerms, int templatePos) {
        double cost = 0;
//        double[] bestCostsOfTemplate = new double[selectedIndices.length];
//        for (int i = 0; i < bestCostsOfTemplate.length; i++) {
//            bestCostsOfTemplate[i] = bestCosts[i][templatePos];
//        }
        cost = ShapeContextVector.matchingCost(inputVectors, selectedIndices, 
                normalizerTerms, bestCosts[templatePos]);
        return cost;
    }
    
    private double classificationSelectionCost(ShapeContextVector[][] inputVectors, 
            int[] selectedIndices, double[] normalizerTerms, ShapeContextFeature templateFeatures) {
        double cost = 0;
        cost = ShapeContextVector.matchingCost(inputVectors, selectedIndices, normalizerTerms,
                ((GeneralizedShapeContext) templateFeatures.getShapeContext()).getShapeContextVectors());
        return cost;
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
//        for (ClassificationResult classificationResult : bestClasses.values()) {
//            orderedResults.add(classificationResult);
//        }
//        Collections.sort(orderedResults, new Comparator<ClassificationResult>() {
//            public int compare(ClassificationResult o1, ClassificationResult o2) {
//                if(o1.getCost() > o2.getCost())
//                    return 1;
//                if(o1.getCost() < o2.getCost())
//                    return -1;
//                return 0;
//            }
//        });
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
