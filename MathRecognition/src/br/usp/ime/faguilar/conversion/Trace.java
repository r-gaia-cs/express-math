/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.conversion;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class Trace {
    private int id;
    private ArrayList<Point2D> points;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Point2D> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point2D> points) {
        this.points = points;
    }

    @Override
    public String toString(){
        String string = "";
        string += getId() + " ";
        string += getPoints();
        return string;
    }
}
