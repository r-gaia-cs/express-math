/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank
 */
public class LevelStructuralChange {
    private int treeLevel;
    private List<NodeStructuralChange> changesInNodes;

    public LevelStructuralChange() {
       treeLevel = -1;
       changesInNodes = new ArrayList<>();
    }   
    
    public int getTreeLevel() {
        return treeLevel;
    }
    
    public void addNodeStructuralChange(NodeStructuralChange nodeChange){
        getChangesInNodes().add(nodeChange);
    }

    public void setTreeLevel(int treeLevel) {
        this.treeLevel = treeLevel;
    }

    public List<NodeStructuralChange> getChangesInNodes() {
        return changesInNodes;
    }

    public void setChangesInNodes(List<NodeStructuralChange> changesInNodes) {
        this.changesInNodes = changesInNodes;
    }
    
}