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
public class LabeledGraphRelation {
    private int stroke1ID;
    private int stroke2ID;
    private String label;

    
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getStroke1ID() {
        return stroke1ID;
    }

    public void setStroke1ID(int stroke1ID) {
        this.stroke1ID = stroke1ID;
    }

    public int getStroke2ID() {
        return stroke2ID;
    }

    public void setStroke2ID(int stroke2ID) {
        this.stroke2ID = stroke2ID;
    }    
    
    public boolean isSameSymbolRelation() {
        return getLabel().equals("*");
    }
}
