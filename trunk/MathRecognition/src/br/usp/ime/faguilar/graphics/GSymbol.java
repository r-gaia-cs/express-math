/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.graphics;

//import br.usp.ime.faguilar.matching.graphics.GMatch;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.DStroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 * GSymbol: Graphic symbol. A Symbol that can be
 * drawn on a given graphic context
 * @author frank
 */
public class GSymbol extends DSymbol{
    private boolean drawnWithBBox;

//    private GMatch gMatch;

    private boolean labelDrawn;

    private static final int graphicalThicknessOfTraces=4;

    private static final Color BOUNDING_BOX_COLOR=Color.GRAY;

    static final long serialVersionUID = 943645510831771215L;
    /**
     * Creates an instance of this class
     */
    public GSymbol(){
        super();
        this.setDrawnWithBBox(true);
//        gMatch=null;
        this.setLabelDrawn(false);
    }

    public void translate(double horixontalTraslation,double verticalTraslation){
        for (DStroke dStroke : this) {
            ((GStroke)dStroke).translate(horixontalTraslation, verticalTraslation);
        }
        this.ltPoint.setLocation(ltPoint.getX()+horixontalTraslation, ltPoint.getY()+verticalTraslation);
        this.rbPoint.setLocation(rbPoint.getX()+horixontalTraslation, rbPoint.getY()+verticalTraslation);
    }

    /**
     * Draws this symbol on a given graphic contexts g
     * @param g
     */
    public void drawSymbol(Graphics g){
//        GSymbol.drawSymbol(this, g);
        Point2D pLT=this.getLtPoint();
        Point2D pRB=this.getRbPoint();
        
        if(this.isDrawnWithBBox()){
            //i increase the limits of the boounding box
            //(by 4 pixels in width and height) to avoid
            //the impression that some points are out of that
            Color c=g.getColor();
            float dash[] = { 10.0f };
            Stroke s=((Graphics2D)g).getStroke();
            ((Graphics2D)g).setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            g.setColor(BOUNDING_BOX_COLOR);
            g.drawRect((int)pLT.getX()-2, (int)pLT.getY()-2, (int)(this.getWidth()+4),(int)(this.getHeight()+4));
            g.setColor(c);
            ((Graphics2D)g).setStroke(s);
        }

        if(this.isLabelDrawn()){
//            g.drawString(label+"("+label+"_"+
//                    getId()+")", (int) pLT.getX(), (int) (pLT.getY()-6));
            g.drawString(label, (int) pLT.getX(), (int) (pLT.getY()-6));
        }
        for (DStroke stroke : this) {
            GStroke.drawStroke(stroke, g);
        }
    }

    public boolean contains(Point2D point){
        Rectangle graphicalBoundingBox=(Rectangle) this.getBBox().clone();
        graphicalBoundingBox.grow(graphicalThicknessOfTraces, graphicalThicknessOfTraces);
        return graphicalBoundingBox.contains(point);
    }

    public Point2D getCenterOfBBoc(){
        Point2D center=null;
        center=new Point2D.Double(this.getBBox().getCenterX(),this.getBBox().getCenterY());
        return center;
    }

    public boolean isDrawnWithBBox() {
        return drawnWithBBox;
    }

    public void setDrawnWithBBox(boolean drawBBox) {
        this.drawnWithBBox = drawBBox;
    }

//    public GMatch getgMatch() {
//        return gMatch;
//    }
//
//    public void setgMatch(GMatch gMatch) {
//        this.gMatch = gMatch;
//    }

    public boolean isLabelDrawn() {
        return labelDrawn;
    }

    public void setLabelDrawn(boolean labelDrawn) {
        this.labelDrawn = labelDrawn;
    }
}
