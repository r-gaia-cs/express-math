/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ClassificationResult;
import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.evaluation.ShapeContextPerSymbol;
import br.usp.ime.faguilar.evaluation.ShapeContextStatistics;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.graphics.GMathExpression;
import br.usp.ime.faguilar.graphics.GSymbol;
import br.usp.ime.faguilar.util.SymbolUtil;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import edu.princeton.cs.algs4.Queue;

/**
 *
 * @author frank.aguilar
 */
public class Segmentation {
//    protected SegmentationResult result;
    
    protected static final int MAX_NUMBER_OF_STROKES_PER_SYMBOL = 4;
    
//    protected int[] bestGroups;
//    
//    protected double[] bestCosts;
    protected SegmentationTable segmentationTable;
    
    protected Classifier classifier;
    
    protected double maxDistanceBetweenStrokes;
    
    protected boolean truncateByDistance;
    
    protected boolean truncateByMST;

    protected KruskalMST mst;
    
    protected int numberOfSymbolEvaluations;
    
    protected DStroke[] myStrokes;

    protected ShapeContextStatistics symbolStatistics;
    protected EdgeWeightedGraph graph;

    protected ArrayList alreadySegmented;
    
    public void part(DStroke[] strokes){
        myStrokes = strokes;
        initializePartitionData(strokes.length);
        doPartition(strokes);
    }
    
    public void part(ArrayList<DStroke> strokes){
        DStroke[] vector =new DStroke[strokes.size()];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = strokes.get(i);
        }
        part(vector);
    }
    
    protected void initializePartitionData(int numberOfStrokes){
        segmentationTable =new SegmentationTable(numberOfStrokes);
        symbolStatistics = new ShapeContextStatistics();
        symbolStatistics.readSymbolStatistics(ShapeContextStatistics.STATISTCS_FILE_NAME);
        alreadySegmented = new ArrayList();
    }

    protected void doPartition(DStroke[] strokes) {
        numberOfSymbolEvaluations = 0;
        DSymbol dSymol = null;
        Point2D[] symbolPoints = null;
        Classifible<Point2D> classifible =new Classifible<Point2D>();//classifible = null;
        ClassificationResult classificationResult = null;
        ClassificationResult bestClassificationResult = null;
        double newCost;
        double currentBestCost;
        boolean executeClassification;
        int[] vertexIndex;
        boolean continueCombinations;
        SymbolFeatures symbolFeatures = null;
        for (int i = 0; i < strokes.length; i++) {
            continueCombinations = true;
            for (int j = 1; j <= MAX_NUMBER_OF_STROKES_PER_SYMBOL && 
                    i - j + 1>= 0 && continueCombinations; j++) {
                
                dSymol = new DSymbol();
                vertexIndex = new int[j];
                executeClassification = true;
                for (int k = 1; k <= j; k++) {
                    dSymol.addCheckingBoundingBox(strokes[i + 1 - k]);
                    vertexIndex[k-1] = i + 1 - k;
                    if(alreadySegmented.contains(i + 1 -k))
                        executeClassification = false;
                }
                
                if(truncateByDistance && !goodGroupByDistance(dSymol))
                    executeClassification = false;
                if(executeClassification && truncateByMST && !goodGroupByMST(vertexIndex))
                    executeClassification = false;
                if(executeClassification){
                    numberOfSymbolEvaluations++;
                    symbolPoints = PreprocessingAlgorithms.getNPoints(dSymol, DSymbol.NUMBER_OF_POINTS_PER_SYMBOL);
                    classifible.setFeatures(symbolPoints);
                    symbolFeatures = new SymbolFeatures();
                    symbolFeatures.setNumberOfStrokes(dSymol.size());
                    classifible.setAditionalFeatures(symbolFeatures);
                    classificationResult = (ClassificationResult) classifier.classify(classifible);
                    classificationResult.setSymbol(dSymol);
                    newCost = classificationResult.getCost(); 
                    currentBestCost = segmentationTable.getLeastCostUnitl(i);

                    if(isHorizontal(classificationResult)){
                        if(horizontalIsFraction(i)) {
                            classificationResult.setMyClass("\\frac");
                            newCost = 0;
                            classificationResult.setCost(newCost);
                            continueCombinations = false;
                            alreadySegmented.add(i);
                        }else if(horizontalIsMinus(i)) {
                            classificationResult.setMyClass("-");
                            newCost = 0;
                            classificationResult.setCost(newCost);
                            continueCombinations = false;
                            alreadySegmented.add(i);
                        }else{
                            ArrayList<ClassificationResult> orderedListOfClasses =
                                    (ArrayList<ClassificationResult>) classifier.orderedListOfClasses();
                            newCost = 100;//high cost
                            classificationResult = getNonFractionNeitherMinus(orderedListOfClasses);
                            classificationResult.setCost(newCost);
                            classificationResult.setSymbol(dSymol);
                        }
                    }
                    if(i - j >= 0)
                        newCost += segmentationTable.getLeastCostUnitl(i - j);
                    if(jointTwoStrokes(classificationResult)&&!(newCost >= 100)){
                        ///TODO: prevenir q combinaciones con dos elementos
                        // junte simbolos fraccion o menos determinados anteriormente
                        segmentationTable.setCostForGroupAt(i, newCost);// classificationResult.getCost();
                        segmentationTable.setBestGroupAt(i, j);
                        bestClassificationResult = classificationResult;
                    }
                    else if(newCost < currentBestCost) {
                        segmentationTable.setCostForGroupAt(i, newCost);// classificationResult.getCost();
                        segmentationTable.setBestGroupAt(i, j);
                        bestClassificationResult = classificationResult;
                    }
                }
            }
            segmentationTable.addSymbolHypothesis(bestClassificationResult);
        }      
    }

    private ClassificationResult getNonFractionNeitherMinus(ArrayList<ClassificationResult>
            results){
        ClassificationResult nonFractonNeighterMinus = null;
        int i = 1;
        do{
            nonFractonNeighterMinus = results.get(i++);
        }while(isHorizontal(nonFractonNeighterMinus) && i < results.size());
        return nonFractonNeighterMinus;
    }

    private boolean isHorizontal(ClassificationResult result){
        if(result.getMyClass().equals("\\frac") ||
                result.getMyClass().equals("-"))
            return true;
        return false;
    }

    public boolean jointTwoStrokes(ClassificationResult classificationResult){
        boolean joint = false;
        DSymbol symbol = classificationResult.getSymbol();
        if(symbol.size() == 2){
            if(symbolStatistics.containsDataOf((String) classificationResult.getMyClass())){
                ShapeContextPerSymbol symbolData = (ShapeContextPerSymbol) 
                        symbolStatistics.getDataOf((String) classificationResult.getMyClass());
                if(classificationResult.getCost() <= symbolData.getMaxDistance())
//                if(classificationResult.getCost() <= (symbolData.getMean() + 1 * symbolData.getStandardDeviation()))
                    joint = true;
            }
        }
        return joint;
    }

    
    public DMathExpression getPartitionAsDMathExpression(){
        DMathExpression mathExpression = new GMathExpression();
        ArrayList<Integer> groups = segmentationTable.getBestPartitionGroups();
        ArrayList<String> partitionSymbolClasses = segmentationTable.getPartitionSymbolClasses();
        GSymbol symbol;
        int count = 0;
        int numberOfelements;
        int indexOfLabel;
        Map<String, Integer> labelsMap = new HashMap();
        for (int index = 0; index < groups.size();index ++) {
            numberOfelements = groups.get(index);
            
            symbol = new GSymbol();
            for(int i = 0; i < numberOfelements; i++){
                symbol.addCheckingBoundingBox(myStrokes[count++]);
            }
            indexOfLabel = 0;
            if(labelsMap.containsKey(partitionSymbolClasses.get(index)))
                indexOfLabel = labelsMap.get(partitionSymbolClasses.get(index));
            indexOfLabel++;
            symbol.setLabel(partitionSymbolClasses.get(index));
            symbol.setId(indexOfLabel);
            labelsMap.put(partitionSymbolClasses.get(index), indexOfLabel);
            mathExpression.addCheckingBoundingBox(symbol);
        }
        return mathExpression;
    }
    
    protected boolean goodGroupByDistance(DSymbol symbol){
        boolean goodGroup = true;
        for (int i = 0; i < symbol.size() -1; i++) {
            for (int j = i + 1; j < symbol.size(); j++) {
                if(symbol.get(i).getNearestPointToBoundingBoxCenter().distance(
                        symbol.get(j).getNearestPointToBoundingBoxCenter()) > maxDistanceBetweenStrokes){
                    goodGroup = false;
                    break;
                }
            }
        }
        if(!goodGroup && symbol.size() == 2)
            if(SymbolUtil.nearEnds(symbol.get(0), symbol.get(1)))
                    goodGroup = true;
//        if(!goodGroup && symbol.size() == 2)
//            if(symbol.get(0).getBBox().intersects(symbol.get(1).getBBox()))
//                    goodGroup = true;
        return goodGroup;
    }


    protected boolean goodGroupByMST(int[] vertexIndex){
        boolean goodGroup = true;
//        boolean containsEdge;
        int edgescount = 0;
//            int selectedIndex = vertexIndex[0];
        if(vertexIndex.length > 1){
            for(int selectedIndex : vertexIndex){
    //                containsEdge = false;
                for(Edge e:mst.edges()){
                    if(e.either() == selectedIndex)
                        for (int i : vertexIndex) {
                            if(i != selectedIndex)
                                if(e.other(selectedIndex) == i){
    //                                    containsEdge = true;
                                    edgescount++;
    //                                    selectedIndex = vertexIndex[i];
                                }
                        }
                }
            }
            if(edgescount < vertexIndex.length - 1)
                goodGroup = false;
        }

        return goodGroup;
    }

    /**
     * TODO
     * @return true if stroke at pos strokeIndex is
     * contained inside a neighbor symbol (indicated by the mst).
     * Returns false otherwise.
     */
    private boolean isInsideOtherStroke(int strokeIndex){
        boolean isInside = true;
        return isInside;
    }

    private boolean horizontalIsFraction(int strokeIndex){
        if(hasStrokeAbove(strokeIndex) && hasStrokeBelow(strokeIndex))
            return true;
        return false;
    }

    private boolean horizontalIsMinus(int strokeIndex){
        if(!hasStrokeAbove(strokeIndex) && !hasStrokeBelow(strokeIndex))
            return true;
        return false;
    }

    private boolean hasStrokeAbove(int strokeIndex){
        boolean hasAbove = false;
        Iterable<Edge> adj = getGraph().adj(strokeIndex);
        int otherIndex;
        Point2D otherBoxCenter;

        for (Edge edge : adj) {
            otherIndex = edge.other(edge.either());
            if(otherIndex != strokeIndex){
                otherBoxCenter = myStrokes[otherIndex].getBoundingBoxCenter();
                if(otherBoxCenter.getY() <= myStrokes[strokeIndex].getBoundingBoxCenter().getY()
                        && otherBoxCenter.getX() < myStrokes[strokeIndex].getRbPoint().getX() &&
                        otherBoxCenter.getX() > myStrokes[strokeIndex].getLtPoint().getX()){
                    hasAbove = true;
                    break;
                }
            }
        }
        return hasAbove;
    }

    private boolean hasStrokeBelow(int strokeIndex){
        boolean hasAbove = false;
        Iterable<Edge> adj = getGraph().adj(strokeIndex);
        int otherIndex;
        Point2D otherBoxCenter;

        for (Edge edge : adj) {
            otherIndex = edge.other(edge.either());
            otherBoxCenter = myStrokes[otherIndex].getBoundingBoxCenter();
            if(otherBoxCenter.getY() > myStrokes[strokeIndex].getBoundingBoxCenter().getY()
                    && otherBoxCenter.getX() < myStrokes[strokeIndex].getRbPoint().getX() &&
                    otherBoxCenter.getX() > myStrokes[strokeIndex].getLtPoint().getX()){
                hasAbove = true;
                break;
            }
        }
        return hasAbove;
    }

    public EdgeWeightedGraph getGraph() {
        return graph;
    }

    public void setGraph(EdgeWeightedGraph graph) {
        this.graph = graph;
    }

    public DStroke[] getMyStrokes() {
        return myStrokes;
    }

    public void setMyStrokes(DStroke[] myStrokes) {
        this.myStrokes = myStrokes;
    }

    public ShapeContextStatistics getSymbolStatistics() {
        return symbolStatistics;
    }

    public void setSymbolStatistics(ShapeContextStatistics symbolStatistics) {
        this.symbolStatistics = symbolStatistics;
    }


    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public double getMaxDistanceBetweenStrokes() {
        return maxDistanceBetweenStrokes;
    }

    public void setMaxDistanceBetweenStrokes(double maxDistanceBetweenStrokes) {
        this.maxDistanceBetweenStrokes = maxDistanceBetweenStrokes;
    }

    public SegmentationTable getSegmentationTable() {
        return segmentationTable;
    }

    public void setSegmentationTable(SegmentationTable segmentationTable) {
        this.segmentationTable = segmentationTable;
    }

    public boolean isTruncateByDistance() {
        return truncateByDistance;
    }

    public void setTruncateByDistance(boolean truncateByDistance) {
        this.truncateByDistance = truncateByDistance;
    }

    public KruskalMST getMst() {
        return mst;
    }

    public void setMst(KruskalMST mst) {
        this.mst = mst;
        calculateEdgeWeightedGraphFrom(mst);
    }

    private void calculateEdgeWeightedGraphFrom(KruskalMST mst){
        int numberOfVertices = ((Queue)mst.edges()).size() + 1;
        graph = new EdgeWeightedGraph(numberOfVertices);
        for (Edge edge : mst.edges()) {
            graph.addEdge(edge);
            graph.addEdge(new Edge(edge.other(edge.either()), edge.either(), edge.weight()));
        }
    }

    public boolean isTruncateByMST() {
        return truncateByMST;
    }

    public void setTruncateByMST(boolean truncateByMST) {
        this.truncateByMST = truncateByMST;
    }

    public int getNumberOfSymbolEvaluations() {
        return numberOfSymbolEvaluations;
    }

    public void setNumberOfSymbolEvaluations(int numberOfSymbolEvaluations) {
        this.numberOfSymbolEvaluations = numberOfSymbolEvaluations;
    }
}
