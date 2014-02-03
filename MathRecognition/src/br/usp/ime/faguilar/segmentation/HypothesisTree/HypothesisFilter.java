/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import java.util.ArrayList;

/**
 *
 * @author Frank Aguilar
 */
public interface HypothesisFilter {
//    public boolean isGoodHypothesis(SymbolHypothesis hypothesis);
    public ArrayList<SymbolHypothesis> filter(ArrayList<SymbolHypothesis> hypothesis);
}
