/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import br.usp.ime.faguilar.segmentation.SegmentationNode;
import java.util.ArrayList;

/**
 *
 * @author Frank Aguilar
 */
public class Node {

    /**
     * Father of this node
     */
    private Node father;
    
    ArrayList<Integer> selected;
    
    private SymbolHypothesis hypothesis;
    
    /**
     * Childs of this node
     */
    private ArrayList<Node> childs;

    public Node() {
        selected = new ArrayList<Integer>();
        childs = new ArrayList<Node>();
        father = null;
    }

    public boolean alreadySelected(int pos){
        return selected.contains(pos);
    }
    
    public boolean intersectingStrokes(SymbolHypothesis hypothesis){
        DSymbol symbol = hypothesis.getSymbol();
        for (DStroke dStroke : symbol) {
            if(alreadySelected(((OrderedStroke) dStroke).getIndex()))
                return true;
        }
        return false;
    }

    public boolean isRoot(){
        return getFather() == null;
    }
    
    public boolean addChild(Node e) {
        return childs.add(e);
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
        selected.addAll(father.getSelected());
        
    }

    public ArrayList<Integer> getSelected() {
        return selected;
    }

    public void setSelected(ArrayList<Integer> selected) {
        this.selected = selected;
    }

    public SymbolHypothesis getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis(SymbolHypothesis hypothesis) {
        this.hypothesis = hypothesis;
        DSymbol symbol = hypothesis.getSymbol();
        for (DStroke dStroke : symbol) {
            selected.add(((OrderedStroke) dStroke).getIndex());
        }
    }

    public ArrayList<Node> getChilds() {
        return childs;
    }

    public void setChilds(ArrayList<Node> childs) {
        this.childs = childs;
    }
    
    
    
}
