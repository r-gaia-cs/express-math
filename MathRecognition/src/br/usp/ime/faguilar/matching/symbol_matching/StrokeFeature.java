/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching.symbol_matching;


import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import java.awt.geom.Point2D;


/**
 *
 * @author frank
 */
public class StrokeFeature {
    private Point2D normalizedCentroid;
    private double normalizedHeight;
    private double normalizedWidth;
    private double gama;

    public StrokeFeature(){
        normalizedHeight = 0;
        normalizedWidth = 0;
        gama = 0.85;
    }

    public static StrokeFeature createStrokeFeaturesFromDStrokeAndDSymbol(
            DStroke stroke, DSymbol symbol){
        StrokeFeature strokeFeatures = new StrokeFeature();
        if(symbol.getHeightAsDouble() == 0.)
            strokeFeatures.setNormalizedHeight(1);
        else
            strokeFeatures.setNormalizedHeight(stroke.getHeightAsDouble() / symbol.getHeightAsDouble());
        if(symbol.getWidthAsDouble() == 0.)
            strokeFeatures.setNormalizedWidth(1);
        else
            strokeFeatures.setNormalizedWidth(stroke.getWidthAsDouble() / symbol.getWidthAsDouble());
        strokeFeatures.calculateNormalizedCentroid(stroke, symbol);
        return strokeFeatures;
    }

    public void calculateNormalizedCentroid(DStroke stroke, DSymbol symbol){
        Point2D p = stroke.getCentroid();
        float coordX = 1;
        float coordY = 1;
        if(symbol.getWidthAsDouble() != 0)
            coordX = (float)((p.getX() - symbol.getLtPoint().getX()) / symbol.getWidthAsDouble());
        if(symbol.getHeightAsDouble() != 0.)
            coordY = (float)((p.getY() - symbol.getLtPoint().getY()) / symbol.getHeightAsDouble());
        p.setLocation(coordX, coordY);
        setNormalizedCentroid(p);
    }

    public float distance(StrokeFeature otherStrokeFeature){
        float distance = 0;
        double coordsDistance =  getNormalizedCentroid().distance(otherStrokeFeature.getNormalizedCentroid());
        double heightDistance = getNormalizedHeight() - otherStrokeFeature.getNormalizedHeight();
        double widthDistance = getNormalizedWidth() - otherStrokeFeature.getNormalizedWidth();
        double dimensionsDistance =  Math.sqrt(heightDistance * heightDistance + 
                widthDistance * widthDistance);
        distance = (float) (gama * coordsDistance + (1 - gama) * dimensionsDistance);
//        double dimensionsDistance =  (Math.abs(getNormalizedHeight() - otherStrokeFeature.getNormalizedHeight())
//                + Math.abs(getNormalizedWidth() - otherStrokeFeature.getNormalizedWidth())) / 2;
//        distance = (float) ((coordsDistance + dimensionsDistance) / 2);
        return distance;
    }

    public Point2D getNormalizedCentroid() {
        return normalizedCentroid;
    }

    public void setNormalizedCentroid(Point2D normalizedCentroid) {
        this.normalizedCentroid = normalizedCentroid;
    }

    public double getNormalizedHeight() {
        return normalizedHeight;
    }

    public void setNormalizedHeight(double height) {
        this.normalizedHeight = height;
    }

    public double getNormalizedWidth() {
        return normalizedWidth;
    }

    public void setNormalizedWidth(double width) {
        this.normalizedWidth = width;
    }

    public double getGama() {
        return gama;
    }

    public void setGama(double gama) {
        this.gama = gama;
    }
}