/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Frank Aguilar
 */
public class SymbolHypothesis {
    private boolean[] binaryRepresentation;
    private DSymbol symbol;
    private double cost;
    private ArrayList<String> labels;    
    public static int BINARY_REPRESENTATION_LENGHT = 20;   
    
    public SymbolHypothesis(){
        binaryRepresentation = new boolean[BINARY_REPRESENTATION_LENGHT];
    }
    
    public SymbolHypothesis(int bynaryRepresentationSize){
        binaryRepresentation = new boolean[bynaryRepresentationSize];
    }
    
    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }
    
    public DSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(DSymbol symbol) {
        this.symbol = symbol;
        for (DStroke dStroke : symbol) {
            binaryRepresentation[((OrderedStroke) dStroke).getIndex()] = true;
        }
    }
    
    public boolean containsStroke(int indexOfStroke){
        return binaryRepresentation[indexOfStroke];
    }
    
    public boolean hasSameStrokes(SymbolHypothesis otherHypothesis){
        boolean equal = true;
        for (int i = 0; i < binaryRepresentation.length; i++) {
            if(binaryRepresentation[i] != otherHypothesis.getBinaryRepresentation()[i]){
                equal = false;
                break;
            }
        }
        return equal;
    }
    
    public boolean hasLabel(String label){
        for (String string : labels) {
            if(string.equals(label))
                return true;
        }
        return false;
    }
//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 97 * hash + Arrays.hashCode(this.binaryRepresentation);
//        return hash;
//    }
//    
//    @Override
//    public boolean equals(Object otherHypothesis){
//        if(otherHypothesis == null)
//            return false;
//        if(!(otherHypothesis instanceof SymbolHypothesis))
//            return false;            
//        boolean equal = true;
//        for (int i = 0; i < binaryRepresentation.length; i++) {
//            if(binaryRepresentation[i] != ((SymbolHypothesis) otherHypothesis).getBinaryRepresentation()[i]){
//                equal = false;
//                break;
//            }
//        }
//        return equal;
//    }

    public boolean[] getBinaryRepresentation() {
        return binaryRepresentation;
    }

    public void setBinaryRepresentation(boolean[] binaryRepresentation) {
        this.binaryRepresentation = binaryRepresentation;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
//    public boolean hasStroke(int indexOfStroke){
//        return 
//    }
}
