/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.parser.conversors.MultipleBSTToLatex.StringNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank
 */
public class MultipleBaselineRegionNode extends RegionNode{
    List<Baseline> baselines;
    private int baselineToParse;
    private boolean addedChilds;
    List<StringNode> nodesToLink;
//    private StringsNode prebiousStringNode;

    public MultipleBaselineRegionNode() {
        baselines = new ArrayList<>();
        baselineToParse = 0;
        nodesToLink = new ArrayList<>();
    }   
    
    public void addNodesToLink(StringNode node){
        nodesToLink.add(node);
    }

    public List<StringNode> getNodesToLink() {
        return nodesToLink;
    }

    public void setNodesToLink(List<StringNode> nodesToLink) {
        this.nodesToLink = nodesToLink;
    }
    
    
    
    public List<Baseline> getBaselines() {
        return baselines;
    }

    public void setBaselines(List<Baseline> baselines) {
        this.baselines = baselines;
    }
   
    public boolean addBaseline(Baseline e) {
        return baselines.add(e);
    }

    public Baseline getBaseline(int index) {
        return baselines.get(index);
    }

    public int getBaselineToParse() {
        return baselineToParse;
    }

    public void setBaselineToParse(int baselineToParse) {
        this.baselineToParse = baselineToParse;
    }

    public boolean isAddedChilds() {
        return addedChilds;
    }

    public void setAddedChilds(boolean addedChilds) {
        this.addedChilds = addedChilds;
    }
    
    
}
