/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.cost;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 *
 * @author frank
 */
public class ShapeContextVector implements Serializable{
    static final long serialVersionUID = -8563085518696891422L;
     private float x;
     private float y;

    public ShapeContextVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
    
    public static double matchingCost(ShapeContextVector[][] vector1, int[] selectedPoints, 
            double[] normalizerTerms, ShapeContextVector[][] vector2){
        double cost = 0;
        for (int i = 0; i < selectedPoints.length; i++)
            cost += (bestMatchingCost(vector1[selectedPoints[i]], vector2)/ normalizerTerms[i]);
        return cost / selectedPoints.length;
    }
    
//    IS  THERE A BUG HERE? I AM NOT USING SELECTEDPOINTS ARGUMENT
    public static double matchingCost(ShapeContextVector[][] vector1, int[] selectedPoints, 
            double[] normalizerTerms, double[] bestCosts){
        double cost = 0;
        for (int i = 0; i < selectedPoints.length; i++)
            cost += (bestCosts[i]/ normalizerTerms[i]);
//            cost += (bestCosts[selectedPoints[i]]/ normalizerTerms[selectedPoints[i]]);
        return cost / selectedPoints.length;
    }
    
    public static double bestMatchingCost(ShapeContextVector[] vector1, 
            ShapeContextVector[][] candidates){
        double best = Double.MAX_VALUE;
        double cost;
        for (int i = 0; i < candidates.length; i++) {
            cost = generalizedShapeContextDistance(vector1, candidates[i]);
            if(cost < best)
                best = cost;
        }
        return best;
    }
    
    public float vectorLength(){
        return (float) Point2D.distance(0, 0, x, y);
    }
    
    public static void normalize(ShapeContextVector vector){
        float lenght = vector.vectorLength();
        if(lenght > 0.){
            vector.setX(vector.getX() / lenght);
            vector.setY(vector.getY() / lenght);
        }
    }
    
    public static double generalizedShapeContextDistance(ShapeContextVector[] vector1, 
            ShapeContextVector[] vector2){
        double distance= 0.;
        double xDif, yDif;
        for (int i = 0; i < vector1.length; i++) {
            xDif = vector1[i].getX() - vector2[i].getX();
            yDif = vector1[i].getY() - vector2[i].getY();
            distance += (xDif * xDif + yDif * yDif);
        }
        distance = Math.sqrt(distance);
        return distance;
    }

    public void add(float xDiference, float yDiference){
        setX(x + xDiference);
        setY(y + yDiference);
    }    
//    private Line2D vector;
//
//    public ShapeContextVector(Line2D vector) {
//        this.vector = vector;
//    }
//    
//    public double getXDiference(){
//        return getVector().getX2() - getVector().getX1();
//    }
//    
//    public double getYDiference(){
//        return getVector().getY2() - getVector().getY1();
//    }
//    
//    public static double matchingCost(ShapeContextVector[][] vector1, int[] selectedPoints, 
//            double[] normalizerTerms, ShapeContextVector[][] vector2){
//        double cost = 0;
//        for (int i = 0; i < selectedPoints.length; i++)
//            cost += (bestMatchingCost(vector1[selectedPoints[i]], vector2)/ normalizerTerms[i]);
//        return cost / selectedPoints.length;
//    }
//    
//    public static double matchingCost(ShapeContextVector[][] vector1, int[] selectedPoints, 
//            double[] normalizerTerms, double[] bestCosts){
//        double cost = 0;
//        for (int i = 0; i < selectedPoints.length; i++)
//            cost += (bestCosts[i]/ normalizerTerms[i]);
//        return cost / selectedPoints.length;
//    }
//    
//    public static double bestMatchingCost(ShapeContextVector[] vector1, 
//            ShapeContextVector[][] candidates){
//        double best = Double.MAX_VALUE;
//        double cost;
//        for (int i = 0; i < candidates.length; i++) {
//            cost = generalizedShapeContextDistance(vector1, candidates[i]);
//            if(cost < best)
//                best = cost;
//        }
//        return best;
//    }
//    
//    public double vectorLength(){
//        return getVector().getP1().distance(getVector().getP2());
//    }
//    
//    public static void normalize(ShapeContextVector vector){
//        double lenght = vector.vectorLength();
//        if(lenght > 0.){
//            vector.getVector().setLine(vector.getVector().getP1(), 
//                    new Point2D.Double(vector.getVector().getX1() + (vector.getXDiference() / lenght), 
//                    vector.getVector().getY1() + (vector.getYDiference() / lenght)));
//        }
//    }
//    
//    public static double generalizedShapeContextDistance(ShapeContextVector[] vector1, 
//            ShapeContextVector[] vector2){
//        double distance= 0.;
//        double xDif, yDif;
//        for (int i = 0; i < vector1.length; i++) {
//            xDif = vector1[i].getXDiference() - vector2[i].getXDiference();
//            yDif = vector1[i].getYDiference() - vector2[i].getYDiference();
//            distance += (xDif * xDif + yDif * yDif);
//        }
//        distance = Math.sqrt(distance);
//        return distance;
//    }
//
//    public void add(double xDiference, double yDiference){
//        vector.setLine(getVector().getP1(), new Point2D.Double(getVector().getX2() + 
//                xDiference, getVector().getY2() + yDiference));
//    }
//
//    public Line2D getVector() {
//        return vector;
//    }
//
//    public void setVector(Line2D vector) {
//        this.vector = vector;
//    }
//    
//    
//    
}
