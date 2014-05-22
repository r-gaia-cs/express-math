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
public class TreeStructuralChange {
    private static RegionNode treeResultRoot;

    private static RegionNode applyChange(RegionNode treeResultRoot, NodeStructuralChange get) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    List<LevelStructuralChange> changesInLevels;
    static RegionNode applyChanges(RegionNode root, List<NodeStructuralChange> aCombination) {
        int index = aCombination.size() - 1;
        treeResultRoot = new RegionNode();
        treeResultRoot.setLabel(RegionLabel.EXPRESSION);
        treeResultRoot.setChildren(root.getChildren());
        while(index >= 0){
            treeResultRoot = applyChange(treeResultRoot, aCombination.get(index));
            index--;
        }
        return treeResultRoot;
    }

    public TreeStructuralChange() {
        changesInLevels = new ArrayList<>();
    }
  
    public void addNodeStructuralChangeAtLevel(NodeStructuralChange nodeChange, 
            int level){
        LevelStructuralChange levelToChange;
        if(containsLevel(level))
            levelToChange = getLevel(level);
        else{
            levelToChange = new LevelStructuralChange();
            changesInLevels.add(levelToChange);
        }
        levelToChange.addNodeStructuralChange(nodeChange);
    }
    
    public boolean containsLevel(int level){
        for (LevelStructuralChange levelStructuralChange : changesInLevels) {
            if(levelStructuralChange.getTreeLevel() == level)
                return true;
        }
        return false;
    }
    
    
    public LevelStructuralChange getLevel(int level){
        for (LevelStructuralChange levelStructuralChange : changesInLevels) {
            if(levelStructuralChange.getTreeLevel() == level)
                return levelStructuralChange;
        }
        return null;
    }

    public List<LevelStructuralChange> getChangesInLevels() {
        return changesInLevels;
    }
    
    
}
