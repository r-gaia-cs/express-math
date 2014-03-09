/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.cost;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Frank
 */
public class IndependentShapeContextCalculator {
    private boolean normalized;
    private ArrayList<Point2D> centers;
    private ArrayList<Point2D> histogramPoints;
    private ShapeContext shapeContexts;
    
    public void calculateShapeContexts(double radio){
        
        
    }
    
    public boolean isNormalized() {
        return normalized;
    }

    public void setNormalized(boolean normalize) {
        this.normalized = normalize;
    }

    public ArrayList<Point2D> getCenters() {
        return centers;
    }

    public void setCenters(ArrayList<Point2D> centers) {
        this.centers = centers;
    }

    public ArrayList<Point2D> getHistogramPoints() {
        return histogramPoints;
    }

    public void setHistogramPoints(ArrayList<Point2D> histogramPoints) {
        this.histogramPoints = histogramPoints;
    }
    
    
}
