/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.export;

import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.graphics.GStroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class Trace{
//    private Rectangle2D boundingBox;
//    private ArrayList timePoints;
    private String timePoints;
    public Trace(GStroke stroke){
        timePoints="";
//        boundingBox=stroke.getBBox();
        for (TimePoint timePoint : stroke) {
//            timePoints.add(object);
            timePoints+=((int)timePoint.getX()+" "+(int)timePoint.getY()+" "+
                    timePoint.getTimeInMiliseconds()+" , ");
        }
        if(stroke.size()>0){
            timePoints=timePoints.substring(0,timePoints.length()-3);
        }
    }

    public String getTimePoints() {
        return timePoints;
    }

    public void setTimePoints(String timePoints) {
        this.timePoints = timePoints;
    }


}
