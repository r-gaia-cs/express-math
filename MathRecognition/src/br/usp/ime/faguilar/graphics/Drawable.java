/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;

/**
 *
 * @author frank
 */
public interface  Drawable {
    public void draw(Graphics g);

//    public Point2D getLeftTopExtreme();

    public abstract void translateTo(Point2D position);

    public Dimension getSize();//{
//        return new Dimension(getWidth(),getHeight());
//    }
    public abstract int getWidth();
    public abstract int getHeight();

}
