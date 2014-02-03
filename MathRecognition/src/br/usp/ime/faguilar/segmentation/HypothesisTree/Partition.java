/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import java.util.ArrayList;

/**
 *
 * @author Frank Aguilar
 */
public class Partition implements Comparable{
    private ArrayList<SymbolHypothesis> symbols;
    private double cost;
    
    public Partition() {
        symbols = new ArrayList<SymbolHypothesis>();
    }
   
    public boolean addSymbolHypothesis(SymbolHypothesis e) {
        return symbols.add(e);
    }
    
    public ArrayList<SymbolHypothesis> getSymbols() {
        return symbols;
    }

    public void setSymbols(ArrayList<SymbolHypothesis> symbols) {
        this.symbols = symbols;
    }
    
    public void updateCost(){
        cost = 0;
        for (SymbolHypothesis symbolHypothesis : symbols) {
            cost += symbolHypothesis.getCost();
        }
    }
    
    public double getCost(){
//        double cost = 0;
//        for (SymbolHypothesis symbolHypothesis : symbols) {
//            cost += symbolHypothesis.getCost();
//        }
        return cost;
    }

    public int compareTo(Object o) {
        double othersCost = ((Partition) o).getCost();
        double myCost = getCost();
        if(myCost > othersCost)
            return 1;
        if(myCost < othersCost)
            return -1;
        return 0;
    }
    
    
    
    
}
