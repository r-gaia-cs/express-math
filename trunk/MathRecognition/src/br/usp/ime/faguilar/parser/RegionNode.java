/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.parser;

/**
 *
 * @author frank
 */
public class RegionNode extends Node<SymbolNode>{
    /**
     * contains a label, defined in RegionLabel
     */
    private int label;
    
    

    public RegionNode() {
        super();
        label = RegionLabel.NOT_DEFINED;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    @Override
    public String stringData() {
        String data = RegionLabel.stringRepresentation(label);
        return data;
    }
    
    
}
