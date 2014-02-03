/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank Aguilar
 */
public class SymbolHypothesisForStroke {
    List<SymbolHypothesis> hypothesis;

//    public boolean containsHypothesis(SymbolHypothesis anHypothesis){
//        boolean contains = false;
//        for (SymbolHypothesis symbolHypothesis : hypothesis) {
//            if(symbolHypothesis.getSymbol().toDStroke().co)
//        }
//        return contains;
//    }
    public SymbolHypothesisForStroke() {
        hypothesis = new ArrayList<SymbolHypothesis>();
    }

    public boolean add(SymbolHypothesis e) {
        return hypothesis.add(e);
    }

    public List<SymbolHypothesis> getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis(List<SymbolHypothesis> hypothesis) {
        this.hypothesis = hypothesis;
    }
    
}
