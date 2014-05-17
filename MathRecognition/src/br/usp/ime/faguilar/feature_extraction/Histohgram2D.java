/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author Frank
 */
public class Histohgram2D {
    private Rectangle2D boundingBox;
    private Point2D[] points;
    
    public static int NUMBER_HORIZONTAL_BINS = 5;
    public static int NUMBER_VERTICAL_BINS = 5;
    
    
    private Bin[][] bins = new Bin[NUMBER_VERTICAL_BINS][NUMBER_HORIZONTAL_BINS];
    private ArrayList<Bin> nearBins;

    public Histohgram2D() {
        nearBins = new ArrayList<>();
    }
    
    public static void configureHistogramForSymbols(){
        NUMBER_HORIZONTAL_BINS = 5;
        NUMBER_VERTICAL_BINS = 5;
    }
    
    public static void configureHistogramForRelations(){
        NUMBER_HORIZONTAL_BINS = 10;
        NUMBER_VERTICAL_BINS = 10;
    }
    
    public void calculateHistogram(Point2D[] points, Rectangle2D boundingBox){
        setBoundingBox(boundingBox);
        setPoints(points);
        calculateHistogram();
    }

    public Rectangle2D getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Rectangle2D boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Point2D[] getPoints() {
        return points;
    }

    public void setPoints(Point2D[] points) {
        this.points = points;
    }

    public void calculateHistogram() {
        calculateBinDimensions();
        if(enoughSize()){
            for (Point2D point2D : points) {
            selectNearBins(point2D);
            updateNearBins(point2D);
            clearSelectedBins();
            }
            normalizeHistogram();
        } else {
//            putAllPointsInFirstBin();
            putAllPointsEquallyDistributedInAllBins();
        }
    }

    public double[] getHistogramAsArray(){
        double[] histogram = new double [NUMBER_VERTICAL_BINS * NUMBER_HORIZONTAL_BINS];
        for (int i = 0; i < bins.length; i++) {
            for (int j = 0; j < bins[i].length; j++) {
                histogram[ i * NUMBER_HORIZONTAL_BINS + j] = bins[i][j].getValue();
            }            
        }
        return histogram;
    }
    
    private void selectNearBins(Point2D point) {
        double binWidth = boundingBox.getWidth() / (double) NUMBER_HORIZONTAL_BINS;
        double binHeight = boundingBox.getHeight() / (double) NUMBER_VERTICAL_BINS;
        double diffX, diffY;
        for (int i = 0; i < bins.length; i++) {
            for (int j = 0; j < bins[i].length; j++) {
                diffX = Math.abs(point.getX() - bins[i][j].getCenter().getX());
                diffY = Math.abs(point.getY() - bins[i][j].getCenter().getY());
                if(binWidth  == 0 && diffY < binHeight)
                    nearBins.add(bins[i][j]);
                else if (binHeight == 0 && diffX < binWidth)
                    nearBins.add(bins[i][j]);
                else if(diffX < binWidth && diffY < binHeight)
                    nearBins.add(bins[i][j]);
            }
        }
    }

    private void updateNearBins(Point2D point) {
        double diffX, diffY;
        double[] weigths = new double[nearBins.size()];
        double sum = 0;
        int indexWeight = 0;
        for (Bin bin : nearBins) {
            diffX = Math.abs(point.getX() - bin.getCenter().getX());
            diffY = Math.abs(point.getY() - bin.getCenter().getY());
            if (diffX == 0 && diffY == 0){
                bin.setValue(bin.getValue() + 1);
                return;
            } else {
                weigths[indexWeight] = 1. / Math.sqrt(diffY * diffY + diffX * diffX);
                sum += weigths[indexWeight];
                indexWeight++;
            }
        }
        for (int i = 0; i < nearBins.size(); i++) {
            nearBins.get(i).setValue(nearBins.get(i).getValue() + 
                    (weigths[i] / sum));
        }
    }

    private void clearSelectedBins() {
        nearBins.clear();
    }

    private void calculateBinDimensions() {
        double minX = boundingBox.getMinX();
        double minY = boundingBox.getMinY();
        double binWidth = boundingBox.getWidth() / (double) NUMBER_HORIZONTAL_BINS;
        double halfOfWidth = binWidth / 2.;
        double binHeight = boundingBox.getHeight() / (double) NUMBER_VERTICAL_BINS;
        double halfOfHeight = binHeight / 2.;
        for (int i = 0; i < bins.length; i++) {
            for (int j = 0; j < bins[i].length; j++) {
                bins[i][j] = new Bin();
                bins[i][j].setCenter(new Point2D.Double(minX + (j * binWidth) + halfOfWidth, 
                minY + (i * binHeight) + halfOfHeight));
            }
        }
    }

    private void normalizeHistogram() {
        for (Bin[] bins1 : bins) {
            for (Bin bin : bins1) {
                bin.setValue(bin.getValue() / points.length);
            }
        }
    }

    private boolean enoughSize() {
        return (boundingBox.getWidth() > 0. || boundingBox.getHeight() > 0.);
    }

    private void putAllPointsInFirstBin() {
        for (Bin[] bin : bins) {
            for (int j = 0; j < bin.length; j++) {
                bin[j] = new Bin();
            }
        }
        bins[0][0].setValue(1);
    }
    
   private void putAllPointsEquallyDistributedInAllBins() {
       int numberOFBins = NUMBER_HORIZONTAL_BINS * NUMBER_VERTICAL_BINS;
        for (Bin[] bin : bins) {
            for (int j = 0; j < bin.length; j++) {
                bin[j] = new Bin();
                bin[j].setValue(1. / numberOFBins);
            }
        }
    }
    
    
    
    private class Bin {
        private double value;
        private Point2D center;

        public Bin() {
            value = 0;
        }        

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public Point2D getCenter() {
            return center;
        }

        public void setCenter(Point2D center) {
            this.center = center;
        }
        
        
    }
    
}
