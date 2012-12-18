/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.data;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 *
 * @author Frank D. J. Aguilar
 * DStroke: Data stroke. Stroke made of points. It stores the list of points that form the stroke
 * and also maintans a reference to the most left-top and right-top points.
 *
 */
public class DStroke extends MathExpressionData<TimePoint>
                    implements Serializable{

    public boolean addCheckingBoundingBox(TimePoint e) {
        if(this.isEmpty()){
            //The first Point2D added will be
            //the left-top and the right-bottom Point2D
            ltPoint.setLocation(e);
            rbPoint.setLocation(e);
        }
        else{
            if(e.getX()<ltPoint.getX())
                ltPoint.setLocation(e.getX(),ltPoint.getY());
            if(e.getY()<ltPoint.getY())
                ltPoint.setLocation(ltPoint.getX(),e.getY());

            if(e.getX()>rbPoint.getX())
                rbPoint.setLocation(e.getX(),rbPoint.getY());
            if(e.getY()>rbPoint.getY())
                rbPoint.setLocation(rbPoint.getX(),e.getY());
         }
        return this.add(e);
    }
    
    @Override
    public String toString(){
        String str="Instance of DStroke:\n";
        str+=("Number of points of this stroke: "+this.size()+"\n");

        str+=("The most left-top point is: "+ltPoint.toString()+"\n");
        str+=("The most right-bottom point is: "+rbPoint.toString()+"\n");
        for (Point2D point2D : this) {
            str+=(point2D.toString()+"\n");
        }

        return str;
    }

    public double minDistance(DStroke otherDStroke){
        double minDistance =Double.MAX_VALUE;
        double distance = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < otherDStroke.size(); j++) {
                distance = this.get(i).distance(otherDStroke.get(j));
                if(distance < minDistance)
                    minDistance = distance;
            }
        }
        return minDistance;
    }

    public Point2D getNearestPointToBoundingBoxCenter(){
        Point2D nearestPoint = null;
//        Rectangle boundingBox = getBBox();
        Point2D boundingBoxCenter = getBoundingBoxCenter();
        double minDist = Double.MAX_VALUE;
        double distance;
        for (Point2D point : this) {
            distance = point.distance(boundingBoxCenter);
            if(distance < minDist){
                minDist = distance;
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }



}
