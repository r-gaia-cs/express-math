/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction;

import br.usp.ime.faguilar.conversion.InkMLInput;
import br.usp.ime.faguilar.conversion.InkmlReader;
import br.usp.ime.faguilar.cost.FuzzyShapeContext;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.MathExpressionData;
import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.feature_extraction.labeledGraph.LabeledGraph;
import br.usp.ime.faguilar.feature_extraction.labeledGraph.SpatialRelation;
import br.usp.ime.faguilar.feature_extraction.labeledGraph.SpatialRelationNode;
import br.usp.ime.faguilar.feature_extraction.labeledGraph.SpatialRelationTree;
import br.usp.ime.faguilar.graphics.GSymbol;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.parser.SymbolClass;
import br.usp.ime.faguilar.segmentation.HypothesisTree.HypothesisForStroke;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import br.usp.ime.faguilar.util.ArraysUtil;
import br.usp.ime.faguilar.util.FilesUtil;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Frank
 */
public class RelationFeatures {
    private String mathMLFile;
    private String labeledGraphMLFile;
    private String outputFile;
    private LabeledGraph labeledGraph;
    private SpatialRelationTree spatialRelationTree;
    private static final int NUMBER_OF_GEOMETRIC_FEATURES = 9;
    public static final String relationLabelesFile = "relationLabelsTrainLabelesWithJunk.txt";//"relationLabels.txt";
    public static String[] labelsOFRelations;
    private boolean junksIncluded;
    private ArrayList<DStroke> strokes;
    private final String junkLabel = "junk";

    public RelationFeatures() {
        junksIncluded = false;
    }
   
    public boolean isJunksIncluded() {
        return junksIncluded;
    }

    public void setJunksIncluded(boolean junksIncluded) {
        this.junksIncluded = junksIncluded;
    }
    
    public void generateFeatures(){
        readLabeledGraph();
        createSpatialRelationsTree();
        calculateAndSaveFeatures();
        if(isJunksIncluded()){
            FuzzyShapeContext.USE_PREDEFINED_CENTER = true;
            extractAndSaveJunks();
        }
    }
    
    private void readLabeledGraph() {
        labeledGraph = LabeledGraph.createGraphFromFile(labeledGraphMLFile);
    }

    private void calculateAndSaveFeatures() {
        InkMLInput inkMlInput = new InkMLInput();
        InkmlReader reader = new InkmlReader();
        reader.read(mathMLFile);
        
        strokes = DMathExpression.extractStrokes(
                reader.getMathExpression().asDMathExpression());//inkMlInput.extractStrokesFromInkMLFile(mathMLFile);
        double[] features;
        for (SpatialRelation spatialRelation : spatialRelationTree.getRelations()) {
            features = calculateFeatures(spatialRelation, strokes);
            addFeaturesToOuputFile(spatialRelation.getRelationLabel(), features);
        }
    }

    private void createSpatialRelationsTree() {
        spatialRelationTree = new SpatialRelationTree();
        spatialRelationTree.extractTreeFromLabeledGraph(labeledGraph);
    }
    
    public static void batchExtractionOfFeatures(){
        RelationFeatures relationFeaturesGenerator = new RelationFeatures();
        relationFeaturesGenerator.setJunksIncluded(true);
        String labelGraphDirTrain = "/Users/Frank/Documents/doctorado/programa/MathFiles/CROHME/labeledGraphs/train/";
        String[] listOFFilesValidation = FilesUtil.getContentAsStringArrayList("validationFilesCrohmePart4.txt");//"validationFilesCrohmePart4.txt");//"trainFilesCrohmePart4Without_formulaire038-equation000.txt");
        String[] listOFFilesTrain = FilesUtil.getContentAsStringArrayList("trainFilesCrohmePart4Without_formulaire038-equation000.txt");
        String mathMLDirTrain = "/Users/Frank/Documents/doctorado/programa/MathFiles/CROHME/2013/train/";
        
        relationFeaturesGenerator.setOutputFile("validationHistogramRelationFeaturesWithJunk.txt");
        for (String string : listOFFilesValidation) {
            if(!string.isEmpty()) {
                relationFeaturesGenerator.setLabeledGraphMLFile(labelGraphDirTrain + string.substring(0, string.length() - 5) + "lg");
                relationFeaturesGenerator.setMathMLFile(mathMLDirTrain + string);
                relationFeaturesGenerator.generateFeatures();
            }
        }
        
        relationFeaturesGenerator.setOutputFile("trainHistogramRelationFeaturesWithJunk.txt");
        for (String string : listOFFilesTrain) {
            if(!string.isEmpty()) {
                relationFeaturesGenerator.setLabeledGraphMLFile(labelGraphDirTrain + string.substring(0, string.length() - 5) + "lg");
                relationFeaturesGenerator.setMathMLFile(mathMLDirTrain + string);
                relationFeaturesGenerator.generateFeatures();
            }
        }
        
        String labelGraphDirTest = "/Users/Frank/Documents/doctorado/programa/MathFiles/CROHME/labeledGraphs/test/";
//        String[] listOFFiles = FilesUtil.getContentAsStringArrayList("validationFilesCrohmePart4.txt");//"validationFilesCrohmePart4.txt");//"trainFilesCrohmePart4Without_formulaire038-equation000.txt");
        
        ArrayList<String> listOFFiles = FilesUtil.getNotHiddenFileNames("/Users/Frank/Documents/doctorado/programa/MathFiles/CROHME/2013/test/");
        String mathMLDirTest = "/Users/Frank/Documents/doctorado/programa/MathFiles/CROHME/2013/test/";
        relationFeaturesGenerator.setOutputFile("testHistogramRelationFeaturesWithJunk.txt");
        for (String string : listOFFiles) {
            if(!string.isEmpty()) {
                relationFeaturesGenerator.setLabeledGraphMLFile(labelGraphDirTest + string.substring(0, string.length() - 5) + "lg");
                relationFeaturesGenerator.setMathMLFile(mathMLDirTest + string);
                relationFeaturesGenerator.generateFeatures();
            }
        }
        
        
    }
    
    public String getMathMLFile() {
        return mathMLFile;
    }

    public void setMathMLFile(String mathMLFile) {
        this.mathMLFile = mathMLFile;
    }

    public String getLabeledGraphMLFile() {
        return labeledGraphMLFile;
    }

    public void setLabeledGraphMLFile(String labeledGraphMLFile) {
        this.labeledGraphMLFile = labeledGraphMLFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    private double[] calculateFeatures(SpatialRelation spatialRelation, ArrayList strokes) {
        DSymbol symbol1 = formSymbol(spatialRelation.getNode1(), strokes);
        DSymbol symbol2 = formSymbol(spatialRelation.getNode2(), strokes);
        return calculateFeatures(symbol1, symbol2);
    }
    
    private double[] calculateFeatures(DSymbol symbol1, DSymbol symbol2) {
//        double[] geometricFeatures = calculateGeometricFeatures(symbol1, symbol2);
//        double[] histogreamFeatures = calculate2DHistogramDifferenceFeatures(symbol1, symbol2);//calculate2DHistogramFeatures(symbol1, symbol2);
//        double[] features = ArraysUtil.concat(geometricFeatures, histogreamFeatures); //calculateGeometricFeatures(symbol1, symbol2);
//        return features;
        
        return calculate2DHistogramDifferenceFeatures(symbol1, symbol2);//calculateFuzzySCFeatures(symbol1, symbol2);//calculate2DHistogramDifferenceFeatures(symbol1, symbol2);//calculateDouble2DHistogramFeatures(symbol1, symbol2);
    }
    
    
    public double[] calculate2DHistogramFeatures(DSymbol symbol1, DSymbol symbol2) {
        double[] features;
        DSymbol preprocessedSymbol1 = PreprocessingAlgorithms.preprocessDSymbolWithOrderedStrokes(symbol1);
        Point2D[] pointsOFSymbol1 = PointsExtractor.getNPoints(preprocessedSymbol1, MatchingParameters.numberOfPointPerSymbol);
        Point2D[] pointsOfSymbol2 = new Point2D[8];
        Point2D[] alloints = new Point2D[38];
        pointsOfSymbol2[0] = symbol2.getLtPoint();
        pointsOfSymbol2[1] = symbol2.getRbPoint();
        pointsOfSymbol2[2] = new Point2D.Double(symbol2.getLtPoint().getX(), symbol2.getRbPoint().getY());
        pointsOfSymbol2[3] = new Point2D.Double(symbol2.getRbPoint().getX(), symbol2.getLtPoint().getY());
        pointsOfSymbol2[4] = new Point2D.Double(symbol2.getLtPoint().getX() + symbol2.getWidthAsDouble() / 2., symbol2.getRbPoint().getY());
        pointsOfSymbol2[5] = new Point2D.Double(symbol2.getRbPoint().getX() - symbol2.getWidthAsDouble() / 2., symbol2.getLtPoint().getY());
        pointsOfSymbol2[6] = new Point2D.Double(symbol2.getLtPoint().getX(), symbol2.getRbPoint().getY() - symbol2.getHeightAsDouble()/ 2.);
        pointsOfSymbol2[7] = new Point2D.Double(symbol2.getRbPoint().getX(), symbol2.getLtPoint().getY() + symbol2.getHeightAsDouble()/ 2.);
        Rectangle2D boundingBox = (Rectangle2D) symbol1.getBBox().clone();
        boundingBox.add(symbol2.getLtPoint());
        boundingBox.add(symbol2.getRbPoint());
        
        System.arraycopy(pointsOFSymbol1, 0, alloints, 0, pointsOFSymbol1.length);
        System.arraycopy(pointsOfSymbol2, 0, alloints, pointsOFSymbol1.length, pointsOfSymbol2.length);
        Histohgram2D histogram = new Histohgram2D();
        histogram.calculateHistogram(alloints, boundingBox);
        features = histogram.getHistogramAsArray();
        
        return features;
    }
    
    public double[] calculateDouble2DHistogramFeatures(DSymbol symbol1, DSymbol symbol2) {
        double[] features, features1, features2;
        DSymbol preprocessedSymbol1 = PreprocessingAlgorithms.preprocessDSymbolWithOrderedStrokes(symbol1);
        Point2D[] pointsOFSymbol1 = PointsExtractor.getNPoints(preprocessedSymbol1, MatchingParameters.numberOfPointPerSymbol);
        Point2D[] pointsOfSymbol2 = pointsOfBoundingBox(symbol2);
        Rectangle2D boundingBox = (Rectangle2D) symbol1.getBBox().clone();
        boundingBox.add(symbol2.getLtPoint());
        boundingBox.add(symbol2.getRbPoint());
        
//        System.arraycopy(pointsOFSymbol1, 0, alloints, 0, pointsOFSymbol1.length);
//        System.arraycopy(pointsOfSymbol2, 0, alloints, pointsOFSymbol1.length, pointsOfSymbol2.length);
        Histohgram2D histogram = new Histohgram2D();
        histogram.calculateHistogram(pointsOFSymbol1, boundingBox);
        features1 = histogram.getHistogramAsArray();
        histogram.calculateHistogram(pointsOfSymbol2, boundingBox);
        features2 = histogram.getHistogramAsArray();
        features = ArraysUtil.concat(features1, features2);
        return features;
    }
    
    public static double[] calculate2DHistogramDifferenceFeatures(DSymbol symbol1, DSymbol symbol2) {
        double[] features, features1, features2;
        DSymbol preprocessedSymbol1 = PreprocessingAlgorithms.preprocessDSymbolWithOrderedStrokes(symbol1);
        Point2D[] pointsOFSymbol1 = PointsExtractor.getNPoints(preprocessedSymbol1, MatchingParameters.numberOfPointPerSymbol);
        DSymbol preprocessedSymbol2 = PreprocessingAlgorithms.preprocessDSymbolWithOrderedStrokes(symbol2);
        Point2D[] pointsOfSymbol2 = PointsExtractor.getNPoints(preprocessedSymbol2, MatchingParameters.numberOfPointPerSymbol);//pointsOfBoundingBox(symbol2);
        Rectangle2D boundingBox = (Rectangle2D) symbol1.getBBox().clone();
        boundingBox.add(symbol2.getLtPoint());
        boundingBox.add(symbol2.getRbPoint());
        
//        System.arraycopy(pointsOFSymbol1, 0, alloints, 0, pointsOFSymbol1.length);
//        System.arraycopy(pointsOfSymbol2, 0, alloints, pointsOFSymbol1.length, pointsOfSymbol2.length);
        
        Histohgram2D histogram = new Histohgram2D();
        histogram.calculateHistogram(pointsOFSymbol1, boundingBox);
        features1 = histogram.getHistogramAsArray();
        histogram.calculateHistogram(pointsOfSymbol2, boundingBox);
        features2 = histogram.getHistogramAsArray();
        features = ArraysUtil.difference(features1, features2);
        return features;
    }
    
    public static Point2D[] pointsOfBoundingBox(DSymbol symbol){
        Point2D[] pointsOfSymbol;
        DSymbol boundingBoxSymbol = new GSymbol();
        DStroke stroke = new OrderedStroke();
        TimePoint aPoint1 = new TimePoint();
        aPoint1.setLocation(symbol.getLtPoint());
        TimePoint aPoint2 = new TimePoint();
        aPoint2.setLocation(new Point2D.Double(symbol.getRbPoint().getX(), symbol.getLtPoint().getY()));
        TimePoint aPoint3 = new TimePoint();
        aPoint3.setLocation(symbol.getRbPoint());
        TimePoint aPoint4 = new TimePoint();
        aPoint4.setLocation(new Point2D.Double(symbol.getLtPoint().getX(), symbol.getRbPoint().getY()));
        TimePoint aPoint5 = new TimePoint();
        aPoint5.setLocation(symbol.getLtPoint());
        stroke.addCheckingBoundingBox(aPoint1);
        stroke.addCheckingBoundingBox(aPoint2);
        stroke.addCheckingBoundingBox(aPoint3);
        stroke.addCheckingBoundingBox(aPoint4);
        stroke.addCheckingBoundingBox(aPoint5);
        boundingBoxSymbol.addCheckingBoundingBox(stroke);
        pointsOfSymbol = PointsExtractor.getNPoints(boundingBoxSymbol, MatchingParameters.numberOfPointPerSymbol);
        return pointsOfSymbol;
    }

    
    private void addFeaturesToOuputFile(String label, double[] features) {
        readRelationLabels();
        String ouputLine = String.valueOf(getIndexOFRelationLabel(label)) 
                + "\t";
        ouputLine += ArraysUtil.formatArrayWithSeparator(features, "\t");
        FilesUtil.append(outputFile, ouputLine + "\n");
    }

    private DSymbol formSymbol(SpatialRelationNode node, ArrayList<DStroke> strokes) {
        DSymbol newSymbol = new DSymbol();
        for (int strokeID : node.getStrokeIds()) {
            newSymbol.addCheckingBoundingBox(OrderedStroke.findStrokeByID(strokes, strokeID));
        }
        newSymbol.setLabel(node.getLabel());
        return newSymbol;
    }  
    
    private DSymbol formSymbol(List<? extends DStroke> strokes) {
        DSymbol newSymbol = new DSymbol();
        for (DStroke stroke : strokes) {
            newSymbol.addCheckingBoundingBox(stroke);
        }
        return newSymbol;
    } 

    public static double[] calculateGeometricFeatures(DSymbol symbol1, DSymbol symbol2) {
        double[] features = new double[NUMBER_OF_GEOMETRIC_FEATURES];
        double normalizingFactor = symbol1.getBoundingBoxCenter().distance(symbol2.getBoundingBoxCenter());
        features[0] = symbol2.getHeightAsDouble() / normalizingFactor;
        double centroidY1 = symbol1.getBoundingBoxCenter().getY();//SymbolClass.centroidY(symbol1);
        double centroidY2 = symbol2.getBoundingBoxCenter().getY();//SymbolClass.centroidY(symbol2);
        features[1] = (centroidY1 - centroidY2) / normalizingFactor;
        features[2] = (symbol1.getCentroid().getX() - symbol2.getCentroid().getX()) 
                / normalizingFactor;
        features[3] = (symbol1.getRbPoint().getX() - symbol2.getLtPoint().getX()) 
                / normalizingFactor;
        features[4] = (symbol1.getLtPoint().getX() - symbol2.getLtPoint().getX()) 
                / normalizingFactor;
        features[5] = (symbol1.getRbPoint().getX() - symbol2.getRbPoint().getX()) 
                / normalizingFactor;
        features[6] = (symbol1.getRbPoint().getY() - symbol2.getLtPoint().getY()) 
                / normalizingFactor;
        features[7] = (symbol1.getLtPoint().getY() - symbol2.getLtPoint().getY()) 
                / normalizingFactor;
        features[8] = (symbol1.getRbPoint().getY() - symbol2.getRbPoint().getY()) 
                / normalizingFactor;
        return features;
    }

    public static void readRelationLabels(){
        if (labelsOFRelations == null)
            labelsOFRelations = FilesUtil.getContentAsStringArrayList(relationLabelesFile);
    }
    
    public static int getIndexOFRelationLabel(String label){
        for (int i = 0; i < labelsOFRelations.length; i++) {
            if (labelsOFRelations[i].equalsIgnoreCase(label))
                return i;
        }
        return -1;
    }
    
    public static String getRelationLabelOfIndex(int index){
        return labelsOFRelations[index];
    }

    private void extractAndSaveJunks() {
        double[] features;
        List<List<DSymbol>> symbolPairs;
        for (SpatialRelationNode node : spatialRelationTree.getNodes()) {
            symbolPairs = calculateSymbolPairs(node);
            for (List<DSymbol> aPair : symbolPairs) {
                features = calculateFeatures(aPair.get(0), aPair.get(1));
                addFeaturesToOuputFile(junkLabel, features);
            }
        }
    }

    private List<List<DSymbol>> calculateSymbolPairs(SpatialRelationNode node) {
        List<List<DSymbol>> pairs = new ArrayList<>();
        
//        int firstPartSize = 1;
//        List<DStroke> strokesOfNode = new ArrayList<>();
//        for (Integer integer : node.getStrokeIds()) {
//            strokesOfNode.add(OrderedStroke.findStrokeByID(strokes, integer));
//        }
//        Collections.sort(strokesOfNode, new XComparator());
//        DSymbol symbol1, symbol2;
//        List<DSymbol> aPair;
//        while(firstPartSize < strokesOfNode.size()) {
//            symbol1 = formSymbol(strokesOfNode.subList(0, firstPartSize));
//            symbol2 = formSymbol(strokesOfNode.subList(firstPartSize, strokesOfNode.size()));
//            aPair = new ArrayList<>();
//            aPair.add(symbol1);
//            aPair.add(symbol2);
//            pairs.add(aPair);
//            firstPartSize++;
//        }
        
        HypothesisForStroke hypothesisPerStroke = new HypothesisForStroke();
        OrderedStroke[] strokesOfNode = new OrderedStroke[node.getStrokeIds().size()];
        List<DSymbol> aPair;
        int i = 0;
        for (Integer integer : node.getStrokeIds()) {
            strokesOfNode[i] = (OrderedStroke) OrderedStroke.findStrokeByID(strokes, integer);
            i++;
        }
        List<ArrayList<OrderedStroke>> combinations = new ArrayList<>();
        hypothesisPerStroke.combinations((ArrayList<ArrayList<OrderedStroke>>) combinations, strokesOfNode);
        List<DSymbol> symbols = new ArrayList<>();
        for (ArrayList<OrderedStroke> arrayList : combinations) {
            if(!arrayList.isEmpty())
                symbols.add(formSymbol(arrayList));
        }            
        
        for (int j = 0; j < symbols.size(); j++) {
            for (int k = j + 1; k < symbols.size(); k++) {
                if(!haveIntersectingStrokes(symbols.get(j), symbols.get(k))){
                    aPair = new ArrayList<>();
                    if(symbols.get(j).getLtPoint().getX() <= symbols.get(k).getLtPoint().getX()){
                        aPair.add(symbols.get(j));
                        aPair.add(symbols.get(k));
                    } else {
                        aPair.add(symbols.get(k));
                        aPair.add(symbols.get(j));
                    }
                    pairs.add(aPair);
                }
            }
        }
        return pairs;
    }

    private double[] calculateFuzzySCFeatures(DSymbol symbol1, DSymbol symbol2) {
        DSymbol preprocessedSymbol1 = PreprocessingAlgorithms.preprocessDSymbolWithOrderedStrokes(symbol1);
//        DSymbol preprocessedSymbol2 = PreprocessingAlgorithms.preprocessDSymbolWithOrderedStrokes(symbol2);
        Point2D[] pointsOFSymbol1 = PointsExtractor.getNPoints(preprocessedSymbol1, MatchingParameters.numberOfPointPerSymbol);
        Point2D[] pointsOfSymbol2 = pointsOfBoundingBox(symbol2);

        Point2D[] alloints = new Point2D[pointsOFSymbol1.length + pointsOfSymbol2.length];
        System.arraycopy(pointsOFSymbol1, 0, alloints, 0, pointsOFSymbol1.length);
        System.arraycopy(pointsOfSymbol2, 0, alloints, pointsOFSymbol1.length, pointsOfSymbol2.length);
        return NeuralNetworkFeatures.extractCenterFuzzyShapecontextsWitthCenter(alloints, symbol1.getCentroid());
    }

    private boolean haveIntersectingStrokes(DSymbol symbol1, DSymbol symbol2) {
        for (int i = 0; i < symbol1.size(); i++) {
            for (int j = 0; j < symbol2.size(); j++) {
                if (((OrderedStroke) symbol1.get(i)).getIndex() == 
                        ((OrderedStroke) symbol2.get(j)).getIndex())
                    return true;
            }
        }
        return false;
    }
    
    class XComparator implements Comparator<DStroke>{
        @Override
        public int compare(DStroke str1, DStroke str2) {
            return Double.compare(str1.getLtPoint().getX(), str2.getLtPoint().getX()); 
        }
    }
    
}
