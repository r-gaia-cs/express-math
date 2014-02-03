/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction;

import br.usp.ime.faguilar.cost.ShapeContextVector;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 *
 * @author frank
 */
public class FeatureGroup implements Serializable{
    
    static final long serialVersionUID = -8563085518696891422L;
    private Point2D coord;
    private Point2D normalizedCoord;
    private float angle;
    private ShapeContextVector vector;

    public FeatureGroup(){
        angle = 0;
        coord = new Point2D.Double(0, 0);
        vector = new ShapeContextVector(0, 0);
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

    public ShapeContextVector getVector() {
        return vector;
    }

    public void setVector(ShapeContextVector vector) {
        this.vector = vector;
    }
    
}
