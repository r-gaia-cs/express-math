/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction;

import java.awt.geom.Point2D;

/**
 *
 * @author frank
 */
public class FeatureGroup {
    private Point2D coord;
    private Point2D normalizedCoord;
    private float angle;

    public FeatureGroup(){
        angle = 0;
        coord = new Point2D.Double(0, 0);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Point2D getCoord() {
        return coord;
    }

    public void setCoord(Point2D coord) {
        this.coord = coord;
    }

    public Point2D getNormalizedCoord() {
        return normalizedCoord;
    }

    public void setNormalizedCoord(Point2D normalizedCoord) {
        this.normalizedCoord = normalizedCoord;
    }
    
}
