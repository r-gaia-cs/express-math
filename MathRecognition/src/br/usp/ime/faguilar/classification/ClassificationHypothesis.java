/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

/**
 *
 * @author Frank Aguilar
 */
public class ClassificationHypothesis implements Comparable{
    private double cost;
    private String label;

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int compareTo(Object o) {
        double diference = getCost() - ((ClassificationHypothesis) o).getCost();
        if (diference < 0) 
            return -1;
        if (diference > 0)
            return 1;
        return 0;
    }
    
    
}
