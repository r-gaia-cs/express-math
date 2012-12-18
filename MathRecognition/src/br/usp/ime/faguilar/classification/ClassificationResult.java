/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.data.DSymbol;

/**
 *
 * @author frank.aguilar
 */
public class ClassificationResult extends Classifible {
    private double cost;
    private DSymbol symbol;
    
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
}