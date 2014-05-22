/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Frank
 */
public class Baseline{
    List<SymbolNode> nodes;

    public Baseline() {
        nodes = new ArrayList<>();
    }
    
    public void addNode(SymbolNode node){
        nodes.add(node);
    }

    public List<SymbolNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<SymbolNode> nodes) {
        this.nodes = nodes;
    }    

    public boolean addAllNodes(Collection<? extends SymbolNode> c) {
        return nodes.addAll(c);
    }
    
    
}
