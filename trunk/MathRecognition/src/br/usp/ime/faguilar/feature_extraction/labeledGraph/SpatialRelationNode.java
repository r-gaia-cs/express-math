/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction.labeledGraph;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank
 */
public class SpatialRelationNode {
    private List<Integer> strokeIds;
    private String label;
        
    public SpatialRelationNode(){
        strokeIds = new ArrayList<>();
    }
    
    public boolean relationHasStrokeOfThisNode(LabeledGraphRelation relation) {
        return containsStrokeID(relation.getStroke1ID()) || 
                containsStrokeID(relation.getStroke2ID());
    }
    
    public void addStrokesOFRelation(LabeledGraphRelation relation){
        if(!containsStrokeID(relation.getStroke1ID()))
            strokeIds.add(relation.getStroke1ID());
        if(!containsStrokeID(relation.getStroke2ID()))
            strokeIds.add(relation.getStroke2ID());
    }
    
    public boolean containsStrokeID(int id){
        return strokeIds.contains(id);
    }
        
//    private List<SpatialRelation> relations;

    public List<Integer> getStrokeIds() {
        return strokeIds;
    }

    public void setStrokeIds(List<Integer> strokeIds) {
        this.strokeIds = strokeIds;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
