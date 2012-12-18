/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.data.DStroke;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frank
 */
public class SegmentationNode {
    /**
     * best cost from root until this node
     */
    private double bestCost;

    /**
     * Number of strokes that in combination with the stroke
     * that represents this stroke gives
     * the best cost from root until this node/stroke
     */
    private int bestGroup;
    
    /**
     * number of symbols given by the best combination 
     * until this node/stroke
     */
    private int symbolCount;

    /**
     * Father of this node
     */
    private SegmentationNode father;
    
    /**
     * Stroke associated to this node
     */
    private ArrayList<DStroke> stroke;
    
    /**
     * symbol label of last result of symbol until this node
     */
    private String label;

    /**
     * Childs of this node/stroke
     */
    private ArrayList<SegmentationNode> childs;
    
    /**
     * Strokes to be processed from this node
     */
    private DStroke[] strokesToProcess;
    
    public SegmentationNode(){
//        bestGroups[i] = -1;
//            bestCosts[i] = Double.MAX_VALUE;
//            symbolCount[i] = 0;
        bestGroup = -1;
        bestCost = Double.MAX_VALUE;
        symbolCount = 0;
        childs = new ArrayList<SegmentationNode>();
        father = null;
        stroke = new ArrayList<DStroke>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Stroke index
     */
//    private int strokeIndex;

//    public int getStrokeIndex() {
//        return strokeIndex;
//    }
//
//    public void setStrokeIndex(int strokeIndex) {
//        this.strokeIndex = strokeIndex;
//    }

    
    
    public DStroke[] getStrokesToProcess() {
        return strokesToProcess;
    }

    public void setStrokesToProcess(DStroke[] strokesToProcess) {
        this.strokesToProcess = strokesToProcess;
    }
    
    public double getBestCost() {
        return bestCost;
    }

    public boolean addChild(SegmentationNode e) {
        return childs.add(e);
    }

    public SegmentationNode getChild(int childIndex){
        return childs.get(childIndex);
    }

    public void setBestCost(double bestCost) {
        this.bestCost = bestCost;
    }

    public int getBestGroup() {
        return bestGroup;
    }

    public void setBestGroup(int bestGroup) {
        this.bestGroup = bestGroup;
    }

    public ArrayList<SegmentationNode> getChilds() {
        return childs;
    }

    public SegmentationNode getFather() {
        return father;
    }

    public void setFather(SegmentationNode father) {
        this.father = father;
    }

    public int getSymbolCount() {
        return symbolCount;
    }

    public void setSymbolCount(int symbolCount) {
        this.symbolCount = symbolCount;
    }

    public ArrayList<DStroke> getStroke() {
        return stroke;
    }

    public void setStroke(ArrayList<DStroke> stroke) {
        this.stroke = stroke;
    }

    public boolean addStroke(DStroke e) {
        return stroke.add(e);
    }
    
}
