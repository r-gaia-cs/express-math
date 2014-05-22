/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser;

import java.util.List;

/**
 *
 * @author Frank
 */
public class MultipleBaselineRegionNode extends RegionNode{
    List<Baseline> baselines;
       
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
    
    
}
