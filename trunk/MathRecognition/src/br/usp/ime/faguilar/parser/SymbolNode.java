/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author frank
 */
public class SymbolNode extends Node<RegionNode>{
    
    private DSymbol symbol;
    
    public SymbolNode(DSymbol symbol){
        super();
        this.symbol = symbol;
    }
    
    public SymbolNode(SymbolNode otherNode){
        super(otherNode);
        symbol = otherNode.getSymbol();
    }
    
    public static Comparator<SymbolNode> minXComparator(){
        return new Comparator<SymbolNode>() {
            public int compare(SymbolNode sn1, SymbolNode sn2) {
                return Double.compare(sn1.getSymbol().getLtPoint().getX(), 
                        sn2.getSymbol().getLtPoint().getX());
            }
        };
    }
    
    public int determineRegionForPartitionFunction(DSymbol symbolToEvaluate){
        int region = RegionLabel.NOT_DEFINED;
        if(StructuralRelation.isSymbolAtAboveRegion(getSymbol(), symbolToEvaluate))
            region = RegionLabel.ABOVE;
        else if(StructuralRelation.isSymbolAtBelowRegion(getSymbol(), symbolToEvaluate))
            region = RegionLabel.BELOW;
        else if(StructuralRelation.isSymbolAtTopLeftRegion(getSymbol(), symbolToEvaluate))
            region = RegionLabel.TOP_LEFT;
        else if(StructuralRelation.isSymbolAtBottomLeftRegion(getSymbol(), symbolToEvaluate))
            region = RegionLabel.BOTTOM_LEFT;
        else if(StructuralRelation.contains(getSymbol(), symbolToEvaluate))
            region = RegionLabel.INSIDE;
        return region;
    }
    
    public String stringData(){
        String data = getSymbol().getLabel();
        return data;
    }

    public void addNodeToRegion(SymbolNode node, int regionLabel){
        RegionNode region = getRegion(regionLabel);
        if(region == null)
            region = createNewRegion(regionLabel);
        region.addChild(node);
    }
    
    public void addNodesToRegion(List<SymbolNode> nodes, int regionLabel){
        for (SymbolNode symbolNode : nodes) {
            addNodeToRegion(symbolNode, regionLabel);
        }
    }
    
    public RegionNode createNewRegion(int regionLabel){
        RegionNode newRegion = new RegionNode();
        newRegion.setLabel(regionLabel);
//        getChildren().add(newRegion);
        addChild(newRegion);
        return newRegion;
    }
    
    public RegionNode getRegion(int regionLabel){
        for (RegionNode regionNode : getChildren())
            if(regionNode.getLabel() == regionLabel)
                return regionNode;
        return null;
    }
    
    public void removeRegion(int regionLabel){
        for (int i = 0; i < getChildren().size(); i++) {
            if(getChildren().get(i).getLabel() == regionLabel){
                getChildren().remove(i);
                break;
            }
        }
    }
    
    public void mergeRegions(List<Integer> regionLabelsToMerge, int mergeRegion){
        List<SymbolNode> grandchildren = new ArrayList<SymbolNode>();
        RegionNode region;
        for (Integer regionLabel : regionLabelsToMerge) {
            region = getRegion(regionLabel);
            if(region != null){
                grandchildren.addAll(region.getChildren());
                removeRegion(regionLabel);
            }
        }
        for (SymbolNode symbolNode : grandchildren)
            addNodeToRegion(symbolNode, mergeRegion);
    }
    
    public DSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(DSymbol symbol) {
        this.symbol = symbol;
    }
}
