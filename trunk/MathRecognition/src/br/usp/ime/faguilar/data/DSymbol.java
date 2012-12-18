/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.data;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * DSymbol: Data symbol. Mantains basic information of a particular symbol ie: strokes
 * that form the symbol and a reference to the most left-top/right-botton points
 * of this symbol. The left-top and right-botton points
 * are stored to ease the calculation of bounding box of this symbol
 * @author Frank
 */
public class DSymbol extends MathExpressionData<DStroke>
                    implements Serializable{

    static final long serialVersionUID = -8563085518696891422L;
    
    public static final int NUMBER_OF_POINTS_PER_SYMBOL = 30;
    
    protected String label;
    private int id;

    public DSymbol(){
        super();
        label="";
    }

    public boolean addCheckingBoundingBox(DStroke e) {
        Point2D p1=e.getLtPoint();
        Point2D p2=e.getRbPoint();
        if(this.isEmpty()){
            ltPoint.setLocation(p1);
            rbPoint.setLocation(p2);
        }
        else{
            if(p1.getX()<ltPoint.getX())
                ltPoint.setLocation(p1.getX(),ltPoint.getY());
            if(p1.getY()<ltPoint.getY())
                ltPoint.setLocation(ltPoint.getX(),p1.getY());

            if(p2.getX()>rbPoint.getX())
                rbPoint.setLocation(p2.getX(),rbPoint.getY());
            if(p2.getY()>rbPoint.getY())
                rbPoint.setLocation(rbPoint.getX(),p2.getY());
        }
        return this.add(e);
    }

    /**
     * Calculates and returns the centroid of this Symbol
     * @return centroid of this symbol.
     */
    public Point2D getCentroid(){
        Point2D p=null;

        double cx=0,cy=0;
        int numOfPoints=0;
        for (DStroke dStroke : this) {
            for (Point2D pAux : dStroke) {
                cx+=pAux.getX();
                cy+=pAux.getY();
                numOfPoints++;
            }
        }
        if(numOfPoints>0){
            p=new Point2D.Double(cx/(double)numOfPoints, cy/(double)numOfPoints);
        }

        return p;
    }


    /**
     * Returns a copy of this symbol without the stroke specified by the argument.
     * Note that the complexity of this function is O(n) where n is the number
     * of points of this symbol
     * @param i
     * @return
     */
    public DSymbol removed(int i) {
        DSymbol newSymbol=new DSymbol();
        for (int j = 0; j < this.size(); j++) {
            if(j!=i){
                newSymbol.addCheckingBoundingBox(this.get(j));
            }
        }
        return newSymbol;
    }

    @Override
    public String toString(){
        String str="Symbol Data:\n";
        str+=("Bounding box: "+this.getBBox().toString()+"\n");
        str+=("Centroid: "+this.getCentroid().toString()+"\n");
        str+=("Number of strokes of this symbol: "+this.size()+"\n");
        for (DStroke stroke : this) {
            str+=(stroke.toString());
        }
        return str;
    }
    public ArrayList<DStroke> toDStroke(){
        ArrayList<DStroke> strokes = new ArrayList<DStroke>();
        for (DStroke dStroke : this) {
            strokes.add(dStroke);
        }
        return strokes;
    }
    public boolean contains(Point2D p){
        return this.getBBox().contains(p);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
