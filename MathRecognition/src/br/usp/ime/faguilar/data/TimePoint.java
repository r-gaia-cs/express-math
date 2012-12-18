/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.data;

import java.awt.geom.Point2D;


/**
 *
 * @author frank
 */
public class TimePoint extends Point2D.Double{
    private long timeInMiliseconds;
    private static final long defaultTime=-1;
    static final long serialVersionUID = -7169337851799242629L;

    public static TimePoint TimePointFromPoint2D(Point2D p){
        return new TimePoint(p.getX(),p.getY(),defaultTime);
    }

    public static TimePoint TimePointFromPoint2DAndTimeInMiliseconds(Point2D p, long timeInMiliseconds){
        return new TimePoint(p.getX(),p.getY(),timeInMiliseconds);
    }

    public TimePoint(double x,double y,long timeInMiliseconds) {
        super(x,y);
        this.timeInMiliseconds=timeInMiliseconds;
    }

    public TimePoint() {
        super();
        timeInMiliseconds=defaultTime;
    }

    public long getTimeInMiliseconds() {
        return timeInMiliseconds;
    }

    public void setTimeInMiliseconds(long timeInMiliseconds) {
        this.timeInMiliseconds = timeInMiliseconds;
    }

    @Override
    public String toString(){
        return getX()+" "+getY()+" "+getTimeInMiliseconds();
    }
}
