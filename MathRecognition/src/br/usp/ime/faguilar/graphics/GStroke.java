/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.graphics;

import br.usp.ime.faguilar.data.DStroke;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 *
 * @author frank
 */
public class GStroke extends DStroke{
    static final long serialVersionUID = -3076946632671525919L;

     public void drawStroke(Graphics g){
         GStroke.drawStroke(this,g);
     }

    /**
     * Draws a stroke on the graphic context g
     * @param s stroke to be drawn
     * @param g graphic context
     */
     public static void drawStroke(DStroke s,Graphics g){
//      TO DRAW LINEAS BETWEEN CONSECUTIVE POINTS
        Graphics2D g2D=(Graphics2D) g;

        Stroke str= g2D.getStroke();

        //To make lines thicker
        g2D.setStroke(new BasicStroke(3.0f));


        Point2D pAux1,pAux2;
        int i;
        if(s.size()>1){
            //when the stroke has more than one point
            //it is drawn a line between every pair of points
            pAux1=s.get(0);
            for(i=1;i<s.size();i++){
                pAux2=s.get(i);
                g.drawLine((int) pAux1.getX(), (int)pAux1.getY(), (int)pAux2.getX(),(int) pAux2.getY());
                pAux1=pAux2;
            }
        }
        else if(s.size()==1){
            //if the stroke has just a point, it is drawn a line of length 1
            g2D.drawLine((int)s.get(0).getX(),(int) s.get(0).getY(),(int) s.get(0).getX()+1,(int)s.get(0).getY()+1);
        }
        g2D.setStroke(str);
         
//        TO DRAW JUST POINTS
        drawPoints(s, g);
    }

    public static void drawPoints(DStroke s,Graphics g){
        Graphics2D g2D=(Graphics2D) g;

        Stroke str= g2D.getStroke();

        //To make lines thicker
//        g2D.setStroke(new BasicStroke(3.0f));

        for (int i = 0; i < s.size(); i++) {
//            g2D.drawOval((int)(s.get(i).getX()-1),(int) (s.get(i).getY()-1),
//                    3,3);
            g2D.fillOval((int)(s.get(i).getX()-1),(int) (s.get(i).getY()-1),
                    2,2);
        }
        //draws bondoung box center
//        g2D.fillOval((int)s.getBoundingBoxCenter().getX(), (int)s.getBoundingBoxCenter().getY(), 2, 2);
        
        g2D.setStroke(str);
    }
    
    public void translate(double horixontalTraslation,double verticalTraslation){
        for (Point2D p : this) {
            p.setLocation(p.getX()+horixontalTraslation,p.getY()+verticalTraslation);
        }
        ltPoint.setLocation(ltPoint.getX()+horixontalTraslation,ltPoint.getY()+verticalTraslation);
        rbPoint.setLocation(rbPoint.getX()+horixontalTraslation,rbPoint.getY()+verticalTraslation);
    }
}
