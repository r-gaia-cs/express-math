/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.segmentation.HypothesisTree.Partition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Frank
 */
public class ExpressionCost implements Comparable{
    private Partition myPartition;
    private RegionNode expressionRoot;
    private static double partitionWeight = 0.8;
    private double cost;
    public static final int RIGHT_INDEX = 0;
    public static final int ABOVE_INDEX = 1;
    public static final int BELOW_INDEX = 2;
    public static final int INSIDE_INDEX = 3;
    public static final int SUPERSCRIPT_INDEX = 4;
    public static final int SUBSCRIPT_INDEX = 5;
    
    public void calculateCost(){
        double partitionCost = myPartition.getCost();
        cost = partitionCost;// / myPartition.getSymbols().size();
        double structuralCost = calculateStructuralCost();
        cost = (partitionWeight * partitionCost) + 
                ((1 - partitionWeight) * structuralCost);
    }

    public static double getPartitionWeight() {
        return partitionWeight;
    }

    public static void setPartitionWeight(double partitionWeight) {
        ExpressionCost.partitionWeight = partitionWeight;
    }
    
    public double getCost() {
        return cost;
    }

    public Partition getMyPartition() {
        return myPartition;
    }

    public void setMyPartition(Partition myPartition) {
        this.myPartition = myPartition;
    }

    public RegionNode getExpressionRoot() {
        return expressionRoot;
    }

    public void setExpressionRoot(RegionNode expressionRoot) {
        this.expressionRoot = expressionRoot;
    }
    
   

    @Override
    public int compareTo(Object o) {
        return Double.compare(getCost(), ((ExpressionCost) o).getCost());
    }

    private double calculateStructuralCost() {
        double structuralCost = 0;
        LinkedList<RegionNode> regionPile = new LinkedList<>();
        RegionNode currentRegion;
        regionPile.addLast(expressionRoot);
        List<SymbolNode> children;
        List<List<SymbolNode>> listOFBaselines = new ArrayList<>();
        while(!regionPile.isEmpty()) {
            currentRegion = regionPile.removeFirst();
            children = currentRegion.getChildren();
            listOFBaselines.add(children);
            for (SymbolNode symbolNode : children) {
                if(!symbolNode.getChildren().isEmpty())
                    for (RegionNode regionNode : symbolNode.getChildren()) {
                        regionPile.addLast(regionNode);
                    }
            }
        }
        int total = 0;
        for (int i = 0; i < listOFBaselines.size(); i++) {
            for (int j = 0; j < listOFBaselines.get(i).size() - 1; j++) {
                structuralCost += RelationClassifier.costOfClassification(
                        listOFBaselines.get(i).get(j).getSymbol(), 
                        listOFBaselines.get(i).get(j + 1).getSymbol(), RIGHT_INDEX);
                total++;
            }
        }
        structuralCost = structuralCost / total;
        return structuralCost;
    }
    
    
    
    
}
