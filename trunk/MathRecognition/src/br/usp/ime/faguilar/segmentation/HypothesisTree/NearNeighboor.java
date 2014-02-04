/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

/**
 *
 * @author frank
 */
public class NearNeighboor implements Comparable{
    private double distance;
    private int indexOfStroke;



    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getIndexOfStroke() {
        return indexOfStroke;
    }

    public void setIndexOfStroke(int indexOfStroke) {
        this.indexOfStroke = indexOfStroke;
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(getDistance(), ((NearNeighboor) o).getDistance());
    }


}
