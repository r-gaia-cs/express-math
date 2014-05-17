/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.graphics.GMathExpression;
import br.usp.ime.faguilar.graphics.GSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Frank Aguilar
 */
public class Partition implements Comparable{
    private ArrayList<SymbolHypothesis> symbols;
    private double cost;
    
    public DMathExpression partionAsExpression(){
        DMathExpression mathExpression = new GMathExpression();
        for (SymbolHypothesis symbolHypothesis : symbols) {
            symbolHypothesis.getSymbol().setLabel(symbolHypothesis.getLabel(0));
            mathExpression.addCheckingBoundingBox(symbolHypothesis.getSymbol());
        }
//        ArrayList<Integer> groups = segmentationTable.getBestPartitionGroups();
//        ArrayList<String> partitionSymbolClasses = segmentationTable.getPartitionSymbolClasses();
//        GSymbol symbol;
//        int count = 0;
//        int numberOfelements;
//        int indexOfLabel;
//        Map<String, Integer> labelsMap = new HashMap();
//        for (int index = 0; index < groups.size();index ++) {
//            numberOfelements = groups.get(index);
//            
//            symbol = new GSymbol();
//            for(int i = 0; i < numberOfelements; i++){
//                symbol.addCheckingBoundingBox(myStrokes[count++]);
//            }
//            indexOfLabel = 0;
//            if(labelsMap.containsKey(partitionSymbolClasses.get(index)))
//                indexOfLabel = labelsMap.get(partitionSymbolClasses.get(index));
//            indexOfLabel++;
//            symbol.setLabel(partitionSymbolClasses.get(index));
//            symbol.setId(indexOfLabel);
//            labelsMap.put(partitionSymbolClasses.get(index), indexOfLabel);
//            mathExpression.addCheckingBoundingBox(symbol);
//        }
        return mathExpression;
    }
    
    public double percentageOfSymbolsContained(DMathExpression expression){
        double percentage;
        int numContained = 0;
        for (DSymbol dSymbol : expression) {
            for (SymbolHypothesis symbolHypothesis : symbols) {
                if(PartitionTreeEvaluator.isHypothesisOfSymbol(symbolHypothesis, dSymbol)){
                    numContained++;
                    break;
                }
            }
        }
        percentage = numContained / (double) expression.size();
        return percentage;
    }
    
    public Partition() {
        symbols = new ArrayList<SymbolHypothesis>();
    }
   
    public boolean addSymbolHypothesis(SymbolHypothesis e) {
        return symbols.add(e);
    }
    
    public ArrayList<SymbolHypothesis> getSymbolHypotheses() {
        return symbols;
    }

    public void setSymbolHypotheses(ArrayList<SymbolHypothesis> symbols) {
        this.symbols = symbols;
    }
    
    @Override
     public String toString(){
        String string = "";
         for (SymbolHypothesis symbolHypothesis : symbols) {
             string += symbolHypothesis.getLabel(0) + " (" + 
                     symbolHypothesis.getSymbol().size() + ") ";
         }
        return string;
    }
    
    public ArrayList<DSymbol> getSymbols(){
        ArrayList<DSymbol> allSymbols = new ArrayList<>();
        for (SymbolHypothesis symbolHypothesis : symbols) {
            allSymbols.add(symbolHypothesis.getSymbol());
        }
        return allSymbols;
    }
    
    public void updateCost(){
        cost = 0;
        for (SymbolHypothesis symbolHypothesis : symbols) {
            cost += symbolHypothesis.getCost();
        }
//        cost = cost / symbols.size();
    }
    
    public double getCost(){
//        double cost = 0;
//        for (SymbolHypothesis symbolHypothesis : symbols) {
//            cost += symbolHypothesis.getCost();
//        }
        return cost;
    }

    @Override
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
