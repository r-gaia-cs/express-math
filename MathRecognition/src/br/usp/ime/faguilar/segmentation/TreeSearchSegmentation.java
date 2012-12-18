/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ClassificationResult;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.graphics.GMathExpression;
import br.usp.ime.faguilar.graphics.GSymbol;
import java.awt.geom.Point2D;
import java.util.*;

/**
 *
 * @author frank
 */
public class TreeSearchSegmentation extends Segmentation{
    private SegmentationTree segmentationTree;
    private Map<Integer, List<SegmentationNode>> strokeAmbiguities;
    private double minDist;

    @Override
    protected void initializePartitionData(int numberOfStrokes){
        super.initializePartitionData(numberOfStrokes);
        segmentationTree = new SegmentationTree();
        strokeAmbiguities = new HashMap<Integer, List<SegmentationNode>>();
    }
    
    @Override
    protected void doPartition(DStroke[] strokes){
        numberOfSymbolEvaluations = 0;
        calculateAmbiguities();
        int strokeIndex = 0;
        SegmentationNode rootNode = new SegmentationNode();
        rootNode.setStrokesToProcess(strokes);
//        rootNode.setStrokeIndex(strokeIndex);
//        segmentationTree.addChildToCurrentNode(rootNode);
        LinkedList<SegmentationNode> ambiguityNodes = new LinkedList();
        ambiguityNodes.addFirst(rootNode);
        DStroke[] strokesToProcess = null;
        SegmentationNode currentNode = null;
        DSymbol dSymol = null;
        boolean executeClassification;
        SegmentationNode nodeAux = null;
        
        
        Point2D[] symbolPoints = null;
        Classifible<Point2D> classifible =new Classifible<Point2D>();//classifible = null;
        ClassificationResult classificationResult = null;
//        ClassificationResult bestClassificationResult = null;
        double newCost;
        double currentMeanCost;
        double newMeanCost;
        int numberOfSymbols;
        boolean firstRoot = true;
        while(!ambiguityNodes.isEmpty()){
            currentNode = ambiguityNodes.removeFirst();
            strokesToProcess = currentNode.getStrokesToProcess();
            if(firstRoot)
                firstRoot = false;
            else{
//                currentNode.setFather(segmentationTree.getCurrentNode());
//                segmentationTree.addChildToCurrentNode(currentNode);
                segmentationTree.setCurrentNode(currentNode);
                currentNode = new SegmentationNode();
            }
            
            
            for (strokeIndex = 0; strokeIndex < strokesToProcess.length; strokeIndex++) {
//                if(currentNode.getStroke().isEmpty())
                currentNode.addStroke(strokesToProcess[strokeIndex]);
                if(hasAmbiguities(strokesToProcess[strokeIndex])){
                    List<SegmentationNode> childs = getChildsTo(((OrderedStroke)strokesToProcess[strokeIndex]).getIndex()); 
                    for (SegmentationNode childNode : childs) {
                        //falta settear strokes to process a los childs
                        ambiguityNodes.addFirst(childNode);
                        childNode.setFather(segmentationTree.getCurrentNode());
                        segmentationTree.addChildToCurrentNode(childNode);
                        if(!segmentationTree.getRoot().equals(segmentationTree.getCurrentNode())) {
                            childNode.setSymbolCount(segmentationTree.getCurrentNode().getSymbolCount() + 1);
                            childNode.setBestCost(childNode.getBestCost() + segmentationTree.getCurrentNode().getBestCost());
                        }else
                            childNode.setSymbolCount(1);
                    }
                }
                for (int j = 1; j <= MAX_NUMBER_OF_STROKES_PER_SYMBOL && 
                    strokeIndex - j + 1 >= 0; j++) {
                    dSymol = lastJStrokesAsDSymbol(strokeIndex, j, strokesToProcess);
                    executeClassification = true;
                    if(truncateByDistance && !goodGroupByDistance(dSymol))
                        executeClassification = false;
                    if(executeClassification) {
                        numberOfSymbolEvaluations++;
                        symbolPoints = PreprocessingAlgorithms.getNPoints(dSymol, DSymbol.NUMBER_OF_POINTS_PER_SYMBOL);
                        classifible.setFeatures(symbolPoints);
                        classificationResult = (ClassificationResult) classifier.classify(classifible);
                        newCost = classificationResult.getCost(); 
                        numberOfSymbols = 1;
                        newMeanCost = newCost;
                        currentMeanCost = currentNode.getBestCost();
                        if(currentMeanCost != Double.MAX_VALUE)
                            currentMeanCost = currentMeanCost / currentNode.getSymbolCount();
                        if(!segmentationTree.getRoot().equals(
                                segmentationTree.getPreviousElement(j-1))){
                            // TO USE SUM OF COSTS AS MEASURE OF GOOD PARTITIONS
    //                        newCost += segmentationTable.getLeastCostUnitl(i - j);
                            //TO USE MAX COST AS MEASURE OF GOOD PARTITIONS
    //                        maxCost = segmentationTable.getLeastCostUnitl(i - j);
    //                        if(newCost > maxCost)
    //                            newCost = maxCost;
    //                        TO USE MEAN OF COSTS AS MEASURE OF GOOD PARTITIONS
                            numberOfSymbols += segmentationTree.getPreviousSymbolCount(j - 1);
                            newCost = newCost + segmentationTree.getPreviousLeastCost(j - 1);
                            newMeanCost = newCost  /
                                    numberOfSymbols;
                        }
                        if(newMeanCost < currentMeanCost) {
                            currentNode.setBestCost(newCost);
                            currentNode.setBestGroup(j);
                            currentNode.setLabel((String)classificationResult.getMyClass());
                            currentNode.setSymbolCount(numberOfSymbols);
//                            segmentationTable.setCostForGroupAt(i, newCost);// classificationResult.getCost();
//                            segmentationTable.setBestGroupAt(i, j);
//                            bestClassificationResult = classificationResult;
//                            segmentationTable.setSymbolcountAt(i, numberOfSymbols);
                        }
                    }
                }
                currentNode.setFather(segmentationTree.getCurrentNode());
                segmentationTree.addChildToCurrentNode(currentNode);
                segmentationTree.setCurrentNode(currentNode);
                if(strokeIndex + 1 < strokesToProcess.length){
                    currentNode = new SegmentationNode();
//                    nodeAux.setFather(currentNode);
//                    currentNode.addChild(nodeAux);
//                    currentNode = currentNode.getChild(currentNode.getChilds().size()-1);
//                    segmentationTree.addChildToCurrentNode(nodeAux);
                }
            }
        }
    }
    
    protected DSymbol lastJStrokesAsDSymbol(int finalIndex, int j, 
            DStroke[] strokes){
        DSymbol dSymol = new DSymbol();
        for (int k = 1; k <= j; k++) 
            dSymol.addCheckingBoundingBox(strokes[finalIndex + 1 - k]);
        return dSymol;
    }

    private boolean hasAmbiguities(DStroke dStroke) {
        OrderedStroke orderedStroke = (OrderedStroke) dStroke;
        boolean hasAbiguities = false;
        if(strokeAmbiguities.containsKey(orderedStroke.getIndex()))
            hasAbiguities = true;
        return hasAbiguities;
    }

    private List<SegmentationNode> getChildsTo(int strokeIndex) {
        List<SegmentationNode> childs = new ArrayList<SegmentationNode>();
        if(strokeAmbiguities.containsKey(strokeIndex))
            childs = strokeAmbiguities.get(strokeIndex);
        return childs;
    }
    
    @Override
    public DMathExpression getPartitionAsDMathExpression(){
        //buscar los nodos que no tienen hijos (jojas)
        //y seleccionar el que tiene menor costo
        //luego desde ese nodo constrir la expression matematica
        //buscando iendo la raiz
        ArrayList<SegmentationNode> leaves = segmentationTree.getLeaves();
        SegmentationNode bestLeave = null;
        double bestCost = Double.MAX_VALUE;
        
        for (SegmentationNode aLeave : leaves) {
            if (aLeave.getBestCost() / aLeave.getSymbolCount() < 
                    bestCost) {
                bestCost = aLeave.getBestCost() / aLeave.getSymbolCount();
                bestLeave = aLeave;
            }
        }
        DMathExpression mathExpression = new GMathExpression();
        SegmentationNode nodeAux = bestLeave;
        int numberOfelements;
        GSymbol symbol;
        while(!nodeAux.equals(segmentationTree.getRoot())){
            numberOfelements = nodeAux.getBestGroup();
            symbol = new GSymbol();
            symbol.setLabel(nodeAux.getLabel());
            for(int i = 0; i < numberOfelements; i++){
                for (DStroke dStroke : nodeAux.getStroke()) {
                    symbol.addCheckingBoundingBox(dStroke);
                }
                nodeAux = nodeAux.getFather();
            }
            mathExpression.addCheckingBoundingBox(symbol);
        }
//        ArrayList<Integer> groups = segmentationTable.getBestPartitionGroups();
//        ArrayList<String> partitionSymbolClasses = segmentationTable.getPartitionSymbolClasses();
//        GSymbol symbol;
//        int count = 0;
//        int numberOfelements;
//        for (int index = 0; index < groups.size();index ++) {
//            numberOfelements = groups.get(index);
//            
//            symbol = new GSymbol();
//            for(int i = 0; i < numberOfelements; i++){
//                symbol.addCheckingBoundingBox(myStrokes[count++]);
//            }
//            symbol.setLabel(partitionSymbolClasses.get(index));
//            mathExpression.addCheckingBoundingBox(symbol);
//        }
        return mathExpression;
    }

    /**
     * FALTA MEJORAR: EN VEZ D ETENER UN VECTOR CON SELECTED STROKES, 
     * DEBERIAN SER CONSIDERADAS TODAS LAS COMBINACIONES (AUN CUANDO 
     * UNA STROKE SEA COMBINADA VARIAS VECES CON DIFERENTES STROKES), Y LA MEJOR 
     * COMBINACION DEBERIA SER CONSIDERADA DE ACUERDO AL COSTO DADO
     * POR EL CLASIFIFCADOR.
     * 
     */
    private void calculateAmbiguities() {
        DStroke dStroke1, dStroke2;
        double dist;
        ClassificationResult classificationResult = null;
        SegmentationNode nodeAux = null;
        List<DStroke> currentList;
        DStroke[] strokesCopy;
        List<SegmentationNode> segNodes;
        Map<Integer, List<DStroke>> nearStrokes = new HashMap<Integer, List<DStroke>>();
        ArrayList<Integer> selectedStrokes = new ArrayList<Integer>();
        for (int i = 0; i < myStrokes.length - 1; i++) {
            dStroke1 = myStrokes[i];
            for (int j = i+1; j < myStrokes.length; j++) {
                if(!selectedStrokes.contains(j)){
                    dStroke2 = myStrokes[j];
                    dist = dStroke1.getNearestPointToBoundingBoxCenter().distance(
                            dStroke2.getNearestPointToBoundingBoxCenter());
                    if(dist < minDist){
                        if(nearStrokes.containsKey(i))
                            currentList = nearStrokes.get(i);
                        else
                            currentList = new ArrayList<DStroke>();
                        currentList.add(dStroke2);
                        nearStrokes.put(i, currentList);
                        selectedStrokes.add(j);
                    }
                }
            }
        }
        
        for (int i = 0; i < myStrokes.length; i++) {
            if(nearStrokes.containsKey(i)){
                classificationResult = getBestSymbolToCombination(i, nearStrokes.get(i));
                nodeAux = new SegmentationNode();
                nodeAux.setBestCost(classificationResult.getCost());
                nodeAux.setBestGroup(1);
                nodeAux.setLabel((String) classificationResult.getMyClass());
                
                currentList = classificationResult.getSymbol().toDStroke();
                strokesCopy = new DStroke[myStrokes.length - i - currentList.size()];
                int cont = 0;
                for (int j = i +1; j < myStrokes.length; j++) {
                    if (!currentList.contains(myStrokes[j])){
                        strokesCopy[cont++] = myStrokes[j];
                    }
                }
                
//                Collections.sort(currentList, new Comparator<DStroke>() {
//
//                    public int compare(DStroke t, DStroke t1) {
//                        if(t.get(0).getTimeInMiliseconds() > t1.get(0).getTimeInMiliseconds())
//                            return 1;
//                        if(t.get(0).getTimeInMiliseconds() == t1.get(0).getTimeInMiliseconds())
//                            return 0;
//                        return -1;
//                    }
//                });
//                strokesCopy = new DStroke[myStrokes.length - i - 1];
//                System.arraycopy(myStrokes, i + 1, strokesCopy, 0, strokesCopy.length);
//                
//                for (int j = currentList.size() -1; j >= 0; j--) {
//                    
//                }
                
                nodeAux.setStrokesToProcess(strokesCopy);
                nodeAux.setStroke((ArrayList) currentList);
                if(strokeAmbiguities.containsKey(i))
                    segNodes = strokeAmbiguities.get(i);
                else
                    segNodes = new ArrayList<SegmentationNode>();
                segNodes.add(nodeAux);
                strokeAmbiguities.put(i, segNodes);
            }
        }
    }
    
    private ClassificationResult getBestSymbolToCombination(int fixedStroke, List<DStroke> otherStrokes){
        Point2D[] symbolPoints = null;
        Classifible<Point2D> classifible = new Classifible<Point2D>();
        ClassificationResult classificationResult, bestClasificationResult = null;
        DSymbol bestSymbol = null;
        double newCost;
        double minCost = Double.MAX_VALUE;
       List<DSymbol> SymbolCombinations = getSymbolCombintions(fixedStroke, otherStrokes);
        for (DSymbol dSymbol : SymbolCombinations) {
            symbolPoints = PreprocessingAlgorithms.getNPoints(dSymbol, DSymbol.NUMBER_OF_POINTS_PER_SYMBOL);
            classifible.setFeatures(symbolPoints);
            classificationResult = (ClassificationResult) classifier.classify(classifible);
            newCost = classificationResult.getCost();
            if(newCost < minCost){
                minCost = newCost;
                dSymbol.setLabel((String) classificationResult.getMyClass());
                classificationResult.setSymbol(dSymbol);
                bestClasificationResult = classificationResult;
            }
        }
        return bestClasificationResult;
    }
    
    public double getMinDist() {
        return minDist;
    }

    public void setMinDist(double minDist) {
        this.minDist = minDist;
    }

    /**
     * @param fixedStroke
     * @param otherStrokes
     * @return 
     */
    private List<DSymbol> getSymbolCombintions(int fixedStroke, List<DStroke> otherStrokes) {
//        System.out.println("elements to combine: " + otherStrokes.size());
        List<DSymbol> combinations = new ArrayList<DSymbol> ();
        if(otherStrokes.size() == 1){
            DSymbol symbol = new DSymbol();
            symbol.addCheckingBoundingBox(myStrokes[fixedStroke]);
            symbol.addCheckingBoundingBox(otherStrokes.get(0));
            combinations.add(symbol);
        }else if(otherStrokes.size() == 2){
            combinations = getCombinationsFor2Elements(fixedStroke, otherStrokes);
        }
        //FALTA ASEGURAR QUE otherStrokes TENGA COMO MAXIMO 3 STROKES
        else
             combinations = getCombinationsFor3Elements(fixedStroke, otherStrokes);
        return combinations;
    }
    
    private List<DSymbol> getCombinationsFor3Elements(int fixedStroke, List<DStroke> otherStrokes){
        List<DSymbol> combinations = new ArrayList<DSymbol> ();
        DSymbol symbol;
        for (DStroke dStroke : otherStrokes) {
            symbol = new DSymbol();
            symbol.addCheckingBoundingBox(myStrokes[fixedStroke]);
            symbol.addCheckingBoundingBox(dStroke);
            combinations.add(symbol);
        }
        
        symbol = new DSymbol();
        symbol.addCheckingBoundingBox(myStrokes[fixedStroke]);
        symbol.addCheckingBoundingBox(otherStrokes.get(0));
        symbol.addCheckingBoundingBox(otherStrokes.get(1));
        combinations.add(symbol);
        
        symbol = new DSymbol();
        symbol.addCheckingBoundingBox(myStrokes[fixedStroke]);
        symbol.addCheckingBoundingBox(otherStrokes.get(0));
        symbol.addCheckingBoundingBox(otherStrokes.get(2));
        combinations.add(symbol);
        
        symbol = new DSymbol();
        symbol.addCheckingBoundingBox(myStrokes[fixedStroke]);
        symbol.addCheckingBoundingBox(otherStrokes.get(1));
        symbol.addCheckingBoundingBox(otherStrokes.get(2));
        combinations.add(symbol);
        
        symbol = new DSymbol();
        symbol.addCheckingBoundingBox(myStrokes[fixedStroke]);
        symbol.addCheckingBoundingBox(otherStrokes.get(0));
        symbol.addCheckingBoundingBox(otherStrokes.get(1));
        symbol.addCheckingBoundingBox(otherStrokes.get(2));
        combinations.add(symbol);
        return combinations;
    }
    
    private List<DSymbol> getCombinationsFor2Elements(int fixedStroke, List<DStroke> otherStrokes){
        List<DSymbol> combinations = new ArrayList<DSymbol> ();
        DSymbol symbol;
        for (DStroke dStroke : otherStrokes) {
            symbol = new DSymbol();
            symbol.addCheckingBoundingBox(myStrokes[fixedStroke]);
            symbol.addCheckingBoundingBox(dStroke);
            combinations.add(symbol);
        }
        symbol = new DSymbol();
        symbol.addCheckingBoundingBox(myStrokes[fixedStroke]);
        symbol.addCheckingBoundingBox(otherStrokes.get(0));
        symbol.addCheckingBoundingBox(otherStrokes.get(1));
        combinations.add(symbol);
        return combinations;
    }
    
    
}
