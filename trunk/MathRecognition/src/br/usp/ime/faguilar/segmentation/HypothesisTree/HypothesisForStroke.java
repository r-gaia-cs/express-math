/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.graphics.GSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.util.ArrayList;

/**
 *
 * @author Frank Aguilar
 */
public class HypothesisForStroke {

    ArrayList<SymbolHypothesis> hypothesis;
    
    public ArrayList<SymbolHypothesis> combine(OrderedStroke stroke, 
            OrderedStroke[] otherStrokes){
        hypothesis = new ArrayList<SymbolHypothesis>();
        ArrayList<ArrayList<OrderedStroke>> combinations = new ArrayList<ArrayList<OrderedStroke>>();
        combinations(combinations, otherStrokes);
        formHypothesis(combinations, stroke);
        return hypothesis;
    }
    
    private void formHypothesis(ArrayList<ArrayList<OrderedStroke>> combinations, OrderedStroke stroke) {
        DSymbol newsymbol;
        SymbolHypothesis newHypothesis;
        for (ArrayList<OrderedStroke> arrayList : combinations) {
            newsymbol = new GSymbol();
            newsymbol.addCheckingBoundingBox(stroke);
            for (OrderedStroke orderedStroke : arrayList) {
                newsymbol.addCheckingBoundingBox(orderedStroke);
            }
            newHypothesis = new SymbolHypothesis();
            newHypothesis.setSymbol(newsymbol);
            hypothesis.add(newHypothesis);
        }
    }
    
    private void combinations(
            ArrayList<ArrayList<OrderedStroke>> combinations, OrderedStroke[] otherStrokes) {
//        combinations = new ArrayList<ArrayList<OrderedStroke>>();
        ArrayList<OrderedStroke> currentList = new ArrayList<OrderedStroke>();
        combinations.add(currentList);
        backtrack(combinations, otherStrokes,currentList, 0);
    }
    
    private void backtrack(ArrayList<ArrayList<OrderedStroke>> 
            combinations, OrderedStroke[] otherStrokes, ArrayList<OrderedStroke> currentList,int i) {
        if( i >= otherStrokes.length)
            return;
        int nextPosition = i + 1;
        backtrack(combinations, otherStrokes, currentList, nextPosition);
        ArrayList<OrderedStroke> newList = new ArrayList<OrderedStroke>();
        newList.addAll(currentList);
        newList.add(otherStrokes[i]);
        combinations.add(newList);
        backtrack(combinations, otherStrokes, newList, nextPosition);
    }
}