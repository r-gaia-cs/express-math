/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.classification.ClassificationResult;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author frank.aguilar
 */
public class SegmentationTable {
    private int[] bestGroups;
    
    private double[] bestCosts;
    
    private int[] symbolCount;
    
//    private double[] maxCost;
    
    private SegmentationResult result;

    public SegmentationTable(int numberOfStrokes) {
        bestGroups = new int[numberOfStrokes];
        bestCosts = new double[numberOfStrokes];
        symbolCount = new int[numberOfStrokes];
        for (int i = 0; i < numberOfStrokes; i++) {
            bestGroups[i] = -1;
            bestCosts[i] = Double.MAX_VALUE;
            symbolCount[i] = 0;
        }
        result = new SegmentationResult();
    }

    public double getTotalcost() {
        return result.getTotalcost();
    }

    public boolean addSymbolHypothesis(ClassificationResult e) {
        return result.addSymbolHypothesis(e);
    }
    
    public String getPartitionClassesAsString(){
        String partitionclases = "symbol clases: "; 
        for (int i = bestGroups.length -1; i >=0; i = i - bestGroups[i]) {
            partitionclases += (result.getSymbolHypothesisAt(i).getMyClass() +
                    " ");
        }
        return partitionclases;
    }
    
    public ArrayList<String> getPartitionSymbolClasses(){
        ArrayList<String> symbolclases = new ArrayList<String>(); 
        for (int i = bestGroups.length -1; i >=0; i = i - bestGroups[i]) {
            symbolclases.add((String)result.getSymbolHypothesisAt(i).getMyClass());
        }
        Collections.reverse(symbolclases);
        return symbolclases;
    }
    
    public ArrayList<Integer> getBestPartitionGroups(){
        ArrayList<Integer> groups = new ArrayList<Integer>();
        for (int i = bestGroups.length -1; i >=0; i = i - bestGroups[i]) {
            groups.add(bestGroups[i]);
        }
        Collections.reverse(groups);
        return groups;
    }
    
    public int getBestNumberOfelemntsForGroupAt(int index) {
        return bestGroups[index];
    }
    
    public double getLeastCostUnitl(int index){
        return bestCosts[index];
    }
    
    public int getSymbolcountUntil(int index){
        return symbolCount[index];
    }
    
    public void setSymbolcountAt(int index, int count){
        symbolCount[index] = count;
    }
    
    
    public void setCostForGroupAt(int index, double cost){
        bestCosts[index] = cost;
    }
    
    public void setBestGroupAt(int index, int numberOfGroupElements){
        bestGroups[index] = numberOfGroupElements;
    }
    
    
}
