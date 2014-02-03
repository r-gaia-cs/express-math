/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.evaluation;

/**
 *
 * @author frank
 */
public class CostPerIndex implements Comparable<Object>{
    private double cost;
    private int index;

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int compareTo(Object o) {
        return Double.compare(cost, ((CostPerIndex) o).getCost() );
    }
}
