/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction.labeledGraph;

/**
 *
 * @author Frank
 */
public class SpatialRelation {
    private SpatialRelationNode node1;
    private SpatialRelationNode node2;
    private String relationLabel;

    public SpatialRelationNode getNode1() {
        return node1;
    }

    public void setNode1(SpatialRelationNode node1) {
        this.node1 = node1;
    }

    public SpatialRelationNode getNode2() {
        return node2;
    }

    public void setNode2(SpatialRelationNode node2) {
        this.node2 = node2;
    }

    public String getRelationLabel() {
        return relationLabel;
    }

    public void setRelationLabel(String relationLabel) {
        this.relationLabel = relationLabel;
    }
    
    
    
}
