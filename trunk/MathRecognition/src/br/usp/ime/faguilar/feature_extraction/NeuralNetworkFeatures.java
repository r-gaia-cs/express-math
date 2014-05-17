/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction;

import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.util.ArraysUtil;
import java.awt.geom.Point2D;

/**
 *
 * @author Frank Aguilar
 */
public class NeuralNetworkFeatures {
    public static int numberOShapecontext = 8;
    public static double[] extractMergedFeatures(DSymbol symbol){
//        double[] shapeContexts = extractGeneralizedShapecontexts(symbol);
        double[] shapeContexts = extractFuzzyShapecontexts(symbol);
        Point2D[] points = PointsExtractor.getNPoints(symbol, MatchingParameters.numberOfPointPerSymbol);
//        double[] shapeContexts = extractshapecontexts(points);
//        double[] normalizedPoints = extractNormalizedPoints(points, symbol);
        Histohgram2D histogram = new Histohgram2D();
        histogram.calculateHistogram(points, symbol.getBBox());
        double[] histogram2D = histogram.getHistogramAsArray();
//        return histogram2D;
        return ArraysUtil.concat(shapeContexts, histogram2D);
//        double[] ivcFeatures = extractIVCFeatures(points);
//        return ArraysUtil.concat(shapeContexts, normalizedPoints);
//        return normalizedPoints;
//        return shapeContexts;
    }
    
    public static double[] extractNormalizedPoints(Point2D[] points, DSymbol symbol){
        Point2D[] newPoints = PreprocessingAlgorithms.normalizePoints(points, symbol.getBBox());
        double[] normalizedCoords = new double[newPoints.length * 2];
        for (int i = 0; i < newPoints.length; i++) {
            normalizedCoords[2 * i] = newPoints[i].getX();
            normalizedCoords[2 * i + 1] = newPoints[i].getY();
        }
        return normalizedCoords;
    }
    
    private static double[] extractFuzzyShapecontexts(DSymbol symbol) {
        ShapeContextFeature shapeContetxFeatures = PreprocessingAlgorithms.getFuzzyShapeContetxFeatures(symbol, 
                MatchingParameters.numberOfPointPerSymbol);
        double[][] sc = shapeContetxFeatures.getShapeContext().getSC();
        return extractGeneralizedShapeContexts(sc);
    }
    
    public static double[] extractCenterFuzzyShapecontextsWitthCenter(Point2D[] points, Point2D center) {
        ShapeContextFeature shapeContetxFeatures = PreprocessingAlgorithms.getFuzzyShapeContetxFeaturesWithCenter(points, center);
        return shapeContetxFeatures.getShapeContext().getCenterShapeContext();
    }
    
    private static double[] extractGeneralizedShapecontexts(DSymbol symbol) {
        ShapeContextFeature nGeneralizedShapeContetxFeatures = PreprocessingAlgorithms.getNGeneralizedShapeContetxFeatures(symbol, MatchingParameters.numberOfPointPerSymbol);
        double[][] sc = nGeneralizedShapeContetxFeatures.getShapeContext().getSC();
        return extractGeneralizedShapeContexts(sc);
    }

    private static double[] extractshapecontexts(Point2D[] points) {
        ShapeContext shapeContext = CostShapeContextInside.calculateShapeContextFromPoints2D(points);
        double[][] sc = shapeContext.getSC();
        return extractShapeContexts(sc);
    }

    private static double[] extractIVCFeatures(Point2D[] points) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static double[] extractGeneralizedShapeContexts(double[][] sc) {
        int[] positions = calculatePositions(MatchingParameters.numberOfPointPerSymbol);
        double[] scontextAsArray = new double[sc[0].length * numberOShapecontext];
        int count = 0;
        for (int i : positions) {
            System.arraycopy(sc[i], 0, scontextAsArray, count * sc[0].length, sc[0].length);
            count++;
        }
        return scontextAsArray;
    }
    
    private static double[] extractShapeContexts(double[][] sc) {
        int[] positions = calculatePositions(MatchingParameters.numberOfPointPerSymbol);
        double[] scontextAsArray = new double[sc[0].length * numberOShapecontext];
        int count = 0;
        for (int i : positions) {
            System.arraycopy(sc[i], 0, scontextAsArray, count * sc[0].length, sc[0].length);
            count++;
        }
        return scontextAsArray;
    }

    private static int[] calculatePositions(int numberOfTotalShapeContext) {
        double distance = (numberOfTotalShapeContext - 1) / (double) (numberOShapecontext -1);
        int[] positions = new int[numberOShapecontext];
        double sum = 0.;
        int calculatedShapeContext = 0;
        while(calculatedShapeContext < numberOShapecontext){
            positions[calculatedShapeContext] = (int) Math.round(sum);
            sum += distance;
            calculatedShapeContext++;
        }
        return positions;        
    }    
}
