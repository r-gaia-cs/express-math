/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.data;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public abstract class MathExpressionData<T extends
        Cloneable> extends ArrayList<T>
        implements Cloneable{
    /**
     * The most left-top point of the stroke
     */
    protected Point2D ltPoint;

    /**
     * The most right-bottom point of the stroke
     */
    protected Point2D rbPoint;


    /**
     * Creates an instance of empty stroke
     */
    public MathExpressionData(){
        super();
        setLtPoint(new Point2D.Double(-1,-1));
        setRbPoint(new Point2D.Double(-1,-1));
    }

    /**
     * Appends the specified element to the end of this list.
     * Before appending the point, it evaluates if that point
     * is the most left-top/rigth-botton point, if it is, the ltPoint/rbPoint is  updated
     * @param e element to be appended to this list
     * @return true if this stroke changed as a result of the call
     */
    public abstract boolean addCheckingBoundingBox(T e);

    /**
     * Calculates and returns the bounding box (as an instance of Rectangle) of this symbol
     * @return a Rectangle which is the bounding box of this symbol
     */
    public Rectangle getBBox(){
        Rectangle r=null;
        if(rbPoint!=null && ltPoint!=null)
            r=new Rectangle((int)ltPoint.getX(),(int)ltPoint.getY() ,(int)this.getWidth(),(int)this.getHeight());
        return r;
    }

    public Point2D getBoundingBoxCenter(){
        Rectangle boundingBox = getBBox();
        Point2D.Double boundingBoxCenter = null;
        if(boundingBox != null)
            boundingBoxCenter = new Point2D.Double(boundingBox.getCenterX(),
                    boundingBox.getCenterY());

        return boundingBoxCenter;
    }

    /**
     * determines the height of the bounding box of this symbol
     * @return height of symbol
     */
    public int getHeight(){
        return (int)(rbPoint.getY()-ltPoint.getY());
    }

    /**
     * determines the width of the bounding box of this symbol
     * @return height of symbol
     */
    public int getWidth(){
        return (int)(rbPoint.getX()-ltPoint.getX());
    }

    /**
     * Returns the most left-top point of this stroke
     * @return the most left-top point of this stroke
     */
    public Point2D getLtPoint() {
        return ltPoint;
    }

    /**
     * Sets the most left-top point of this stroke
     * @param ltPoint
     */
    private void setLtPoint(Point2D ltPoint) {
        this.ltPoint = ltPoint;
    }

    /**
     * Returns the most right-bottom point of this stroke
     * @param ltPoint
     */
    public Point2D getRbPoint() {
        return rbPoint;
    }

    /**
     * Sets the most right-bottom point of this stroke
     * @param ltPoint
     */
    private void setRbPoint(Point2D rbPoint) {
        this.rbPoint = rbPoint;
    }

    @Override
    public Object clone(){
        MathExpressionData<T> obj=null;
        try{
            obj=(MathExpressionData<T>)super.clone();
        }catch(Exception ex){
            System.out.println(" error in clone function of "+this.getClass());
        }
//        obj.ltPoint=(Point2D)obj.getLtPoint().clone();
//        obj.rbPoint=(Point2D)obj.getRbPoint().clone();
        obj.ltPoint=(Point2D)getLtPoint().clone();
        obj.rbPoint=(Point2D)getRbPoint().clone();

//        for (T object : this) {
//            obj.add(object);
//        }
        return obj;
    }
}
