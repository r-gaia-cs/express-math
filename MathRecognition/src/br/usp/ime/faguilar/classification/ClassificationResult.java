/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.data.DSymbol;
import java.util.ArrayList;

/**
 *
 * @author frank.aguilar
 */
public class ClassificationResult extends Classifible {
    private double cost;
    private DSymbol symbol;
    
    private ArrayList<ClassificationHypothesis> hypothesis;
    public double getCost() {
        return cost;
    }
    
    public void setCost(double cost) {
        this.cost = cost;
    }

    public DSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(DSymbol symbol) {
        this.symbol = symbol;
    }

    public ArrayList<ClassificationHypothesis> getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis(ArrayList<ClassificationHypothesis> hypothesis) {
        this.hypothesis = hypothesis;
    }
    
    
}