/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.cost;

/**
 *
 * @author frank
 */
public class CostPerTemplate implements Comparable{
    private int index;
    private double cost;

    public int compareTo(Object otherCost){
        CostPerTemplate other = (CostPerTemplate) otherCost;
        if(other.getCost() < getCost())
            return 1;
        if(other.getCost() > getCost())
            return -1;
        return 0;
    }
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
}
