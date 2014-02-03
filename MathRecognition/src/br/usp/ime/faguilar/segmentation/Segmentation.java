/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ClassificationResult;
import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.classification.ShapeContextClassifier;
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
import java.awt.geom.Line2D;
import java.util.List;

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

    boolean continueCombinations;


    public static double[] bestCostAt;

    private double minDist;

    private DSymbol dSymol;
    
    public Map<Integer, String> classesForSegmentedStrokes;

    public void part(DStroke[] strokes){
        myStrokes = strokes;
        Segmentation.bestCostAt = new double[strokes.length];
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
        ShapeContextStatistics.readShapeContextstatistics();
    }

    protected void doPartition(DStroke[] strokes) {
        numberOfSymbolEvaluations = 0;
//        DSymbol dSymol = null;
        Point2D[] symbolPoints = null;
        classesForSegmentedStrokes = new HashMap<Integer, String>();
        Classifible<Point2D> classifible =new Classifible<Point2D>();//classifible = null;
        ClassificationResult classificationResult = null;
        ClassificationResult bestClassificationResult = null;
        double newCost;
        double currentBestCost;
        boolean executeClassification;
        int[] vertexIndex;
//        boolean continueCombinations;
        SymbolFeatures symbolFeatures = null;
        for (int i = 0; i < strokes.length; i++) {
            continueCombinations = true;
            for (int numberOFStrokes = 1; numberOFStrokes <= MAX_NUMBER_OF_STROKES_PER_SYMBOL && 
                    i - numberOFStrokes + 1>= 0 && continueCombinations; numberOFStrokes++) {
                
                dSymol = new DSymbol();
                vertexIndex = new int[numberOFStrokes];
                executeClassification = true;
                for (int k = 1; k <= numberOFStrokes; k++) {
                    dSymol.addCheckingBoundingBox(strokes[i + 1 - k]);
                    vertexIndex[k-1] = i + 1 - k;
                    if(alreadySegmented.contains(i + 1 -k))
                        executeClassification = false;
                }
                
                if(truncateByDistance && !goodGroupByDistance(dSymol, maxDistanceBetweenStrokes))
                    executeClassification = false;
                if(executeClassification && truncateByMST && !goodGroupByMST(vertexIndex))
                    executeClassification = false;
                if(executeClassification) {
                    numberOfSymbolEvaluations++;
//                    symbolPoints = PreprocessingAlgorithms.getNPoints(dSymol, DSymbol.NUMBER_OF_POINTS_PER_SYMBOL);
//                    classifible.setFeatures(symbolPoints);
//                    symbolFeatures = new SymbolFeatures();
//                    symbolFeatures.setNumberOfStrokes(dSymol.size());
//                    symbolFeatures.setWidth(dSymol.getWidth());
//                    classifible.setAditionalFeatures(symbolFeatures);
                    classificationResult = (ClassificationResult) classify(classifible, i);
                    if (classificationResult != null){
                        if(numberOFStrokes == 1)
                            classesForSegmentedStrokes.put(i, (String) classificationResult.getMyClass());
                        newCost = classificationResult.getCost();
                        currentBestCost = segmentationTable.getLeastCostUnitl(i);
                        if(i - numberOFStrokes >= 0)
                            newCost += segmentationTable.getLeastCostUnitl(i - numberOFStrokes);
    //                    if(jointTwoStrokes(classificationResult)&&!(newCost >= 100)){
    //                        ///TODO: prevenir q combinaciones con dos elementos
    //                        // junte simbolos fraccion o menos determinados anteriormente
    //                        segmentationTable.setCostForGroupAt(i, newCost);// classificationResult.getCost();
    //                        segmentationTable.setBestGroupAt(i, j);
    //                        bestClassificationResult = classificationResult;
    //                    }
    //                    else
                        if(newCost < currentBestCost) {
                            segmentationTable.setCostForGroupAt(i, newCost);// classificationResult.getCost();
                            segmentationTable.setBestGroupAt(i, numberOFStrokes);
                            bestClassificationResult = classificationResult;
    //                        Segmentation.bestCostAt[i] = classificationResult.getCost();
                        }
                    }
                }
            }
            segmentationTable.addSymbolHypothesis(bestClassificationResult);
        }      
    }


    protected ClassificationResult classify(Classifible<Point2D> classifible, int
            posSegmentation){
        classifible.setSymbol(dSymol);
        ClassificationResult classificationResult = (ClassificationResult) classifier.classify(classifible);
        if (classificationResult != null){
            classificationResult.setSymbol(dSymol);
            double newCost;
            if(isPiOrProd((String) classificationResult.getMyClass())){
                if(dSymol.size() == 3){
    //                if(hasStrokeAbove(posSegmentation) && hasStrokeBelow(strokeIndex))
                }
            }
            else if(isHorizontal(classificationResult))
            {
                if(horizontalIsFraction(posSegmentation)) {
                    classificationResult.setMyClass("-");
//                    classificationResult.setMyClass("\\frac");
    //                newCost = 0;
    //                classificationResult.setCost(newCost);
    //                continueCombinations = false;
    //                alreadySegmented.add(posSegmentation);
                }else if(horizontalIsMinus(posSegmentation)) {
                    classificationResult.setMyClass("-");
    //                newCost = 0;
    //                classificationResult.setCost(newCost);
    //                continueCombinations = false;
    //                alreadySegmented.add(posSegmentation);
                }
    //            else{
    //                ArrayList<ClassificationResult> orderedListOfClasses =
    //                        (ArrayList<ClassificationResult>) classifier.orderedListOfClasses();
    //                newCost = 100;//high cost
    //                classificationResult = getNonFractionNeitherMinus(orderedListOfClasses);
    //                classificationResult.setCost(newCost);
    //                classificationResult.setSymbol(dSymol);
    //            }
            }
        }
        return classificationResult;
    }

    private boolean isPiOrProd(String label){
        if(label.equals("\\pi") || label.equals("\\prod"))
            return true;
        return false;
    }

    private boolean isVertical(String label){
        if(label.equals("|") || label.equals("1"))
            return true;
        return false;
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
    
    public List<DSymbol> getPartitionAsSymbolList(){
        List<DSymbol> symbols = new ArrayList<DSymbol>();
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
            symbols.add(symbol);
        }
        return symbols;
    }

    public boolean goodGroupByDistance(DSymbol symbol, double maxDistance){
        boolean goodGroup = true;
//        test if there is a good distance between all strokes
//        for (int i = 0; i < symbol.size() -1; i++) {
//            for (int j = i + 1; j < symbol.size(); j++) {
//                if(symbol.get(i).getNearestPointToBoundingBoxCenter().distance(
//                        symbol.get(j).getNearestPointToBoundingBoxCenter()) > maxDistanceBetweenStrokes){
//                    goodGroup = false;
//                    break;
//                }
//            }
//        }

//        test if there is conected subraph whose edges are small
        List edges = new ArrayList<Edge>();
        Point2D point_i, point_j;
        for (int i = 0; i < symbol.size() -1; i++) {
            for (int j = i + 1; j < symbol.size(); j++) {
                if(classesForSegmentedStrokes.containsKey(((OrderedStroke)symbol.get(i)).getIndex()))
                    point_i = relativePointAcordingToSymbolClass((OrderedStroke) symbol.get(i));
                else
                    point_i = symbol.get(i).getBoundingBoxCenter();
                if(classesForSegmentedStrokes.containsKey(((OrderedStroke)symbol.get(j)).getIndex()))
                    point_j = relativePointAcordingToSymbolClass((OrderedStroke) symbol.get(j));
                else
                    point_j = symbol.get(j).getBoundingBoxCenter();
                if(point_i.distance(point_j) <= maxDistance)
                    edges.add(new Edge(i, j, 0));
            }
        }
        if(symbol.size() > 1 && !edgesFormAConectedGraph(edges, symbol.size()))
            goodGroup = false;

//        test if for each stroke there is another one that is "near"
//        if(symbol.size()>1){
//            boolean hasFarStroke = true;
//            for (int i = 0; i < symbol.size(); i++) {
//                hasFarStroke = true;
//                for (int j = 0; j < symbol.size(); j++) {
////                    if((i != j) && (symbol.get(i).getNearestPointToBoundingBoxCenter().distance(
////                            symbol.get(j).getNearestPointToBoundingBoxCenter()) <= maxDistanceBetweenStrokes)){
//                    if((i != j) && (symbol.get(i).getBoundingBoxCenter().distance(
//                            symbol.get(j).getBoundingBoxCenter()) <= maxDistanceBetweenStrokes)){
//                        hasFarStroke = false;
//                        break;
//                    }
//                }
//                if(hasFarStroke){
//                    goodGroup = false;
//                    break;
//                }
//            }
//        }
//        if(!goodGroup && symbol.size() == 2)
//            if(SymbolUtil.nearEnds(symbol.get(0), symbol.get(1), minDist))
////            if(SymbolUtil.nearEnds(symbol.get(0), symbol.get(1)))
//                    goodGroup = true;
//        if(!goodGroup && symbol.size() == 2)
//            if(symbol.get(0).getBBox().intersects(symbol.get(1).getBBox()))
//                    goodGroup = true;
        return goodGroup;
    }
    
    
    public Point2D relativePointAcordingToSymbolClass(OrderedStroke stroke){
        Point2D point;
        String label = classesForSegmentedStrokes.get(stroke.getIndex());
        if(label.equals("(") || label.equals(")") || label.equals("C") || label.equals("c"))
            point = stroke.getLtPoint();//stroke.getNearestPointToBoundingBoxCenter();//stroke.getLtPoint();
        else if(label.equals("\\sqrt"))
            point = stroke.getRbPoint();//stroke.getRbPoint();//stroke.getNearestPointToBoundingBoxCenter();
        else if((label.equals("1") || label.equals("|") || label.equals("[") || label.equals("]") ) 
                && !intersectsNeighboors(stroke))
            point = stroke.getLtPoint();
        else
            point = stroke.getBoundingBoxCenter();
        return point;
    }

    private boolean intersectsNeighboors(OrderedStroke stroke) {
        Iterable<Edge> adj = getGraph().adj(stroke.getIndex());
        int otherIndex;
        for (Edge edge : adj) {
            otherIndex = edge.other(stroke.getIndex());
            if(SymbolUtil.intersect(stroke, (OrderedStroke) myStrokes[otherIndex]))
                return true;
        }
        return false;
    }
    
    /**
     *
     * @param edges
     * @param numIndexes less or equal to 4
     * @return
     */
    private boolean edgesFormAConectedGraph(List<Edge> edges,int numIndexes){
        boolean isConnectedGraph = true;
        boolean vertexInEdge;
        if(edges.size() >= numIndexes -1)
            for (int i = 0; i < numIndexes; i++) {
                vertexInEdge = false;
                for (Edge edge : edges) {
                    if (edge.either() == i || edge.other(edge.either()) == i)
                        vertexInEdge = true;
                }
                if(!vertexInEdge){
                    isConnectedGraph = false;
                    break;
                }
            }
        else
            isConnectedGraph = false;
        return isConnectedGraph;
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
        if(!hasTwoNearVerticalStrokesBelow(strokeIndex) &&
                hasStrokeAbove(strokeIndex) && hasStrokeBelow(strokeIndex)){
            numberOfSymbolEvaluations++;
            Iterable<Edge> adj = getGraph().adj(strokeIndex);
           int strokeAboveIndex = getStrokeAbove(strokeIndex);
           int strokeBelowIndex = getAjacentStrokeBelow(strokeIndex);
           DStroke strokeAbove = myStrokes[strokeAboveIndex];
           DStroke strokeBelow = myStrokes[strokeBelowIndex];
           DStroke selectedStroke = myStrokes[strokeIndex];
           DSymbol newSymbol = new DSymbol();
           newSymbol.addCheckingBoundingBox(strokeAbove);
           newSymbol.addCheckingBoundingBox(selectedStroke);
           newSymbol.addCheckingBoundingBox(strokeBelow);

           Classifible<Point2D> classifible =new Classifible<Point2D>();
           Point2D[] symbolPoints = PreprocessingAlgorithms.getNPoints(newSymbol, DSymbol.NUMBER_OF_POINTS_PER_SYMBOL);
            classifible.setFeatures(symbolPoints);
           SymbolFeatures symbolFeatures = new SymbolFeatures();
            symbolFeatures.setNumberOfStrokes(newSymbol.size());
            classifible.setAditionalFeatures(symbolFeatures);
            classifible.setSymbol(newSymbol);
            ClassificationResult classificationResult = (ClassificationResult) classifier.classify(classifible);
            if(classificationResult != null && classificationResult.getMyClass().equals("\\equiv"))
                return false;
            return true;
        }
        return false;
    }

    private boolean hasTwoNearVerticalStrokesBelow(int strokeIndex){
        boolean hasbelow = false;
        int count = 0;
        ArrayList<Integer> ajacentStrokesBelow = getAjacentStrokesBelow(strokeIndex);
        for (Integer aStrokeBelow : ajacentStrokesBelow) {
            if(strokeIsVertical(aStrokeBelow)){
                count++;
            }
            Iterable<Edge> adj = getGraph().adj(aStrokeBelow);
            for (Edge edge : adj) {
                if(strokeIndex != edge.other(aStrokeBelow) &&
                        strokeBelow(strokeIndex, edge.other(aStrokeBelow))){
                    if(strokeIsVertical(edge.other(aStrokeBelow))){
                        count++;
                        if(count >= 2)
                            break;
                    }
                }
            }
            if(count >= 2)
                break;
        }
        if(count >= 2)
            hasbelow = true;
        return hasbelow;
    }
    
    private boolean strokeIsVertical(Integer aStrokeBelow) {
        DStroke stroke = myStrokes[aStrokeBelow];
        if(stroke.size() > 1){
//            Line2D line = new Line2D.Double(stroke.get(0).getX(), stroke.get(0).getY(),
//                    stroke.get(stroke.size() - 1).getX(), stroke.get(stroke.size() - 1).getY());
//            double angleWithHorizontal = line.
            double rate = stroke.getWidthAsDouble() / stroke.getHeightAsDouble();
            if(rate <= 0.3)
                return true;
        }
        return false;
    }

    private boolean horizontalIsMinus(int strokeIndex){
        if((!hasStrokeAbove(strokeIndex) && !hasStrokeBelow(strokeIndex)))// &&
                //!hasStrokeMuchNear(strokeIndex))
            return true;
        return false;
    }

    private boolean hasStrokeMuchNear(int strokeIndex){
        boolean hasNearStroke = false;
        Iterable<Edge> adj = getGraph().adj(strokeIndex);
        int otherIndex;
        Point2D otherBoxCenter;
        for (Edge edge : adj) {
            otherIndex = edge.other(strokeIndex);
            otherBoxCenter = myStrokes[otherIndex].getBoundingBoxCenter();
            if(otherBoxCenter.distance(myStrokes[strokeIndex].getBoundingBoxCenter()) <
                    getMinDist()){//USE OTHER LOWER TREASHOLD?
                hasNearStroke = true;
                break;
            }
        }
        return hasNearStroke;
    }

    private boolean hasStrokeAbove(int strokeIndex){
        boolean hasAbove = false;
        Iterable<Edge> adj = getGraph().adj(strokeIndex);
        int otherIndex;
        for (Edge edge : adj) {
            otherIndex = edge.other(strokeIndex);
            if(strokeAbove(strokeIndex, otherIndex)){
                hasAbove = true;
                break;
            }
        }
        return hasAbove;
    }

    private int getStrokeAbove(int strokeIndex){
        int aboveStroke = -1;
        Iterable<Edge> adj = getGraph().adj(strokeIndex);
        int otherIndex;
        for (Edge edge : adj) {
            otherIndex = edge.other(strokeIndex);
            if(strokeAbove(strokeIndex, otherIndex)){
                aboveStroke = otherIndex;
                break;
            }
        }
        return aboveStroke;
    }

    private boolean strokeBelow(int aStrokeIndex, int posibleStrokeBelowIndex){
        Point2D otherBoxCenter = myStrokes[posibleStrokeBelowIndex].getBoundingBoxCenter();
        if(otherBoxCenter.getY() >= myStrokes[aStrokeIndex].getBoundingBoxCenter().getY()
                && otherBoxCenter.getX() <= myStrokes[aStrokeIndex].getRbPoint().getX() &&
                otherBoxCenter.getX() >= myStrokes[aStrokeIndex].getLtPoint().getX()){
            return true;
        }
        return false;
    }

    private int getAjacentStrokeBelow(int strokeIndex){
        int belowStroke = -1;
        Iterable<Edge> adj = getGraph().adj(strokeIndex);
        int otherIndex;
        for (Edge edge : adj) {
            otherIndex = edge.other(strokeIndex);
            if(strokeBelow(strokeIndex, otherIndex)){
                belowStroke = otherIndex;
                break;
            }
        }
        return belowStroke;
    }

    private ArrayList<Integer> getAjacentStrokesBelow(int strokeIndex){
        ArrayList<Integer> strokesBelow = new ArrayList<Integer>();
        Iterable<Edge> adj = getGraph().adj(strokeIndex);
        int otherIndex;
        for (Edge edge : adj) {
            otherIndex = edge.other(strokeIndex);
            if(strokeBelow(strokeIndex, otherIndex)){
                strokesBelow.add(otherIndex);
            }
        }
        return strokesBelow;
    }

    private boolean hasStrokeBelow(int strokeIndex){
        boolean hasAbove = false;
        Iterable<Edge> adj = getGraph().adj(strokeIndex);
        int otherIndex;
        for (Edge edge : adj) {
            otherIndex = edge.other(strokeIndex);
            if(strokeBelow(strokeIndex, otherIndex)){
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
        ((ShapeContextClassifier)this.classifier).setSegmentationAlgorithm(this);
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
//            graph.addEdge(new Edge(edge.other(edge.either()), edge.either(), edge.weight()));
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


    public double getMinDist() {
        return minDist;
    }

    public void setMinDist(double minDist) {
        this.minDist = minDist;
    }

    private boolean strokeAbove(int strokeIndex, int otherIndex) {
        Point2D otherBoxCenter = myStrokes[otherIndex].getBoundingBoxCenter();
        if(otherBoxCenter.getY() <= myStrokes[strokeIndex].getBoundingBoxCenter().getY()
                && otherBoxCenter.getX() <= myStrokes[strokeIndex].getRbPoint().getX() &&
                otherBoxCenter.getX() >= myStrokes[strokeIndex].getLtPoint().getX()){
            return true;
        }
        return false;
    }
}
