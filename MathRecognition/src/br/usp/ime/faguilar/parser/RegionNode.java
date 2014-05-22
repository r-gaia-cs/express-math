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
    
    @Override
    public String latexString(){
        String string = "";
        if(label == RegionLabel.ABOVE || label == RegionLabel.SUPERSCRIPT){
            if(!getParent().getSymbolLabel().equalsIgnoreCase("\\frac")){
                 string += "^ ";              
            } 
//            else {
//                if (!getChildren().isEmpty()){
//                    string += "^{" + getChildren().get(0).latexString() + "}";
//                    if(getChildren().size() > 1)
//                        string += "{" + getChildren().get(1).latexString() + "}";
//                } 
//            }
     
        }
        else if(label == RegionLabel.BELOW || label == RegionLabel.SUBSCRIPT){
            if(!getParent().getSymbolLabel().equalsIgnoreCase("\\frac")){
                 string += "_ ";              
            } 
        }
        if(label == RegionLabel.EXPRESSION)
            for (SymbolNode symbolNode : getChildren()) {
                string += symbolNode.latexString();
            }
        else {
            if (!getChildren().isEmpty()){
                string += "{ ";
                for (SymbolNode symbolNode : getChildren()) {
                    string += symbolNode.latexString();
                }
                string += "} ";
            } 
        }

        return string;
    }
}
