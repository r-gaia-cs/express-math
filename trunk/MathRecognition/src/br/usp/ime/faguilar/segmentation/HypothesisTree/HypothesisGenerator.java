/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank Aguilar
 */
public interface HypothesisGenerator {
    
    public void generateHypothesis(ArrayList<OrderedStroke> strokes);
    
    public List<SymbolHypothesis> getHypothesis(OrderedStroke stroke);

    public ArrayList<SymbolHypothesis> getAllHypothesis();
    
    
    
    public ArrayList<SymbolHypothesis> getAllHypothesisFilteredByStroke();
}
