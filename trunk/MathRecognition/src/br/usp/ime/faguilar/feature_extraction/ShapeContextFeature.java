/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction;

import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.data.DSymbol;
import java.awt.geom.Point2D;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author frank
 */
public class ShapeContextFeature {

    private List<FeatureGroup> features;
    private ShapeContext shapeContext;

    public ShapeContextFeature() {
        features = new ArrayList<FeatureGroup>();
    }

    public Point2D[] getCoords(){
        Point2D[] points = new Point2D[features.size()];
        for (int i = 0; i < features.size(); i++) {
            points[i] = features.get(i).getCoord();
        }
        return points;
    }

//    CHEKAR COORDENADAS DE matching[][]
    public static double normalizedCoordsDistance(DSymbol symbol1, Point2D[] points1, DSymbol
            symbol2, Point2D[] points2, int[][] matching){
        double normalizedDistance = 0;
        Point2D[] normalizeCoords1 = normalizeCoords(symbol1, points1);
        Point2D[] normalizeCoords2 = normalizeCoords(symbol2, points2);
        float normalizTerm = (float) Math.sqrt(2);
        double dist;
        double sum = 0, mean;
        for (int i = 0; i < normalizeCoords1.length; i++) {
            dist = normalizeCoords1[matching[i][0]].distance(normalizeCoords2[matching[i][1]]);
            sum += dist;
        }
        mean = sum / normalizeCoords1.length;
        normalizedDistance = mean;// / normalizTerm;
        return normalizedDistance;
    }

//    public Point[] getNormalizedCor
    public static Point2D[] normalizeCoords(DSymbol symbol, Point2D[] coords){
        Point2D[] normalizedCoords = new Point2D[coords.length];
        for (int i = 0; i < coords.length; i++)
            normalizedCoords[i] = normalizeCoord(symbol, coords[i]);
        return normalizedCoords;
    }
    
//    CONTINUAR DESDE AQUI: 12-03-2013
    private static Point2D normalizeCoord(DSymbol symbol, Point2D coord) {
        float coordX = 1;
        float coordY = 1;
        double maxDimension = Math.max(symbol.getWidthAsDouble(), symbol.getHeightAsDouble());
//        if(symbol.getWidthAsDouble() != 0)
//            coordX = (float)((p.getX() - symbol.getLtPoint().getX()) / symbol.getWidthAsDouble());
//        if(symbol.getHeightAsDouble() != 0.)
//            coordY = (float)((p.getY() - symbol.getLtPoint().getY()) / symbol.getHeightAsDouble());
        if(maxDimension != 0){
            coordX = (float)((coord.getX() - symbol.getLtPoint().getX()) / maxDimension);
            coordY = (float)((coord.getY() - symbol.getLtPoint().getY()) / maxDimension);
        }   
        Point2D p = new Point2D.Float(coordX, coordY);
        return p;
    }

    public float[] getAngles(){
        float[] angles = new float[features.size()];
        for (int i = 0; i < features.size(); i++) {
            angles[i] = features.get(i).getAngle();
        }
        return angles;
    }
    
    public boolean add(FeatureGroup e) {
        return features.add(e);
    }

    public List<FeatureGroup> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureGroup> features) {
        this.features = features;
    }

    public boolean addAll(Collection<? extends FeatureGroup> c) {
        return features.addAll(c);
    }

    public ShapeContext getShapeContext() {
        return shapeContext;
    }

    public void setShapeContext(ShapeContext shapeContext) {
        this.shapeContext = shapeContext;
    }


}
