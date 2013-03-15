/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.graphics;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DSymbol;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 *
 * @author frank
 */
public class GMathExpression extends DMathExpression
            implements Serializable, Drawable{

//    public GMathExpression(){
//        super();
//    }

    public void drawGMathExpression(Graphics g){
//        g.drawRect((int)this.ltPoint.getX(), (int)this.ltPoint.getY(), (int)this.getWidth(),(int)this.getHeight());
        for (DSymbol dSymbol : this) {
//            GSymbol.drawSymbol(dSymbol, g);
            ((GSymbol)dSymbol).drawSymbol(g);
        }
    }

    public Dimension getSize(){
        return new Dimension((int)getBBox().getWidth(),(int) getBBox().getHeight());
    }

    public int containsPoint(Point2D p){
        int posContains=-1;
        double minDist=Double.MAX_VALUE;
        int pos=0;
        Point2D tempPoint=null;
        double tempDist;
        for (DSymbol dSymbol : this) {
            if(dSymbol.contains(p)){
                tempPoint=new Point2D.Double(dSymbol.getBBox().getCenterX(),
                        dSymbol.getBBox().getCenterY());
                tempDist=tempPoint.distance(p);
                if(minDist>tempDist){
                    posContains= pos;
                    minDist=tempDist;

                }
            }
            pos++;
        }
        return posContains;
    }
    
    public void setDrawnWithBBox(boolean drawnWithBBox){
        for (DSymbol dSymbol : this) {
            ((GSymbol)dSymbol).setDrawnWithBBox(drawnWithBBox);
        }
    }

    public void setDrawnWithLabels(boolean drawWithLabels){
        for (DSymbol dSymbol : this) {
            ((GSymbol)dSymbol).setLabelDrawn(drawWithLabels);
        }
    }

    public void showJustMathExpression(){
        setDrawnWithBBox(false);
        setDrawnWithLabels(false);
    }
    
    public void setDrawnWithBBoxAt(int pos,boolean drawnWithBBox){
        ((GSymbol)this.get(pos)).setDrawnWithBBox(drawnWithBBox);
    }
    
    public void traslateSymbol(int pos,double dx,double dy){
        ((GSymbol)this.get(pos)).translate(dx,dy);
    }

    public void translate(double dx,double dy){
        this.ltPoint.setLocation(this.getLtPoint().getX()+dx,this.getLtPoint().getY()+dy);
        this.rbPoint.setLocation(rbPoint.getX()+dx,rbPoint.getY()+dy);
        for (int i = 0; i < this.size(); i++) {
            traslateSymbol(i, dx, dy);
        }
    }
    /**
     * Returns a copy of this math expression, without the element
     * at position especified by the parameter pos
     * @param Position of element that will not be copied
     * @return A copy of this math expression, without the element at
     * position pos.
     */
    public GMathExpression getACopyWithout(int pos){
        GMathExpression gMathExpression=new GMathExpression();
        for (int i = 0; i < this.size(); i++) {
            if(i!= pos){
                gMathExpression.addCheckingBoundingBox(this.get(i));
            }
        }
        return gMathExpression;
    }

    public void draw(Graphics g) {
        drawGMathExpression(g);
    }

    public void translateTo(Point2D position) {
        double distX=position.getX()-ltPoint.getX();
        double distY=position.getY()-ltPoint.getY();
        translate(distX, distY);
    }

//    public int getWidth() {
//        return (int) getBBox().getWidth();
//    }
//
//    public int getHeight() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
}
