/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.graph;

import java.awt.geom.Point2D;

/**
 * @author Willian Honda
 */
public class Vertex {

    Point2D p;
    private int id; //indice no grafo
    private double[] shapeContextExpression; //cada vertices tem um vetor de 60 medidas (shape context)
    private double[][] shapeContextSymbol;

    public Vertex(Point2D p) {
        this.p = p;
    }

    public Vertex(int id, double x, double y) {
        this(new Point2D.Double(x, y));
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public double getX() {
        return this.p.getX();
    }

    public double getY() {
        return this.p.getY();
    }

    public void setX(double x) {
        this.p.setLocation(x, this.p.getY());
    }

    public void setY(double y) {
        this.p.setLocation(this.p.getX(), y);
    }

    public double[] getShapeContextExpression() {
        return this.shapeContextExpression;
    }

    public void setShapeContextExpression(double[] shapeContext) {
        this.shapeContextExpression = shapeContext;
    }

    public double[][] getShapeContextSymbol() {
        return shapeContextSymbol;
    }

    public void setShapeContextSymbol(double[][] shapeContextSymbol) {
        this.shapeContextSymbol = shapeContextSymbol;
    }

    public float compareShapeContextExpression(Vertex other) {
        double[] localHistogram = this.getShapeContextExpression();
        double[] externalHistogram = other.getShapeContextExpression();
        return this.calculateShapeContext(localHistogram, externalHistogram);
    }
    
    private float calculateShapeContext(double [] localHistogram, double [] externalHistogram){
        double sum = 0.0;
        for (int i = 0; i < localHistogram.length; i++) {
            double d1 = localHistogram[i] - externalHistogram[i];
            double d2 = localHistogram[i] + externalHistogram[i];
            if (d2 > 0) {
                sum = sum + (d1 * d1) / d2;
            }
        }
        double result = (sum / 2.0);
//        if(result == 1){
//            int cc = 0;
//        }
        return (float) result;
//        return (float) (sum / 2.0);
    }
}