/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Frank Aguilar
 */
public class NearestNeighborGraphHypothesisGenerator implements HypothesisGenerator{
    private final int numberOfNeighbors = 3;

    double[][] distances;
    private OrderedStroke[][] neighboors;
    private ArrayList<OrderedStroke> strokes;

    private HypothesisFilter filter;
    private SymbolHypothesisForStroke[] hypothesis;
    private ArrayList<SymbolHypothesis> allHypothesis;

    public void generateHypothesis(ArrayList<OrderedStroke> strokes) {
        init(strokes);
        fillDistancesMatrix();
        calculateNearestNeighbors();
        combineStrokesWithNeighbors();
        filterHypothesis();
        groupSymbolHypothesisPerStroke();
    }
    
    private void init(ArrayList<OrderedStroke> inputStrokes){
        this.strokes = inputStrokes;
        SymbolHypothesis.BINARY_REPRESENTATION_LENGHT = (int) Math.pow(2, Math.ceil(Math.log(strokes.size())/ Math.log(2)));
        hypothesis = new SymbolHypothesisForStroke[strokes.size()];
        for (int i = 0; i < hypothesis.length; i++) {
            hypothesis[i] = new SymbolHypothesisForStroke();
        }
        allHypothesis = new ArrayList<SymbolHypothesis>();
    }

    public void fillDistancesMatrix(){
        distances = new double[strokes.size()][strokes.size()];
        for (int i = 0; i < strokes.size(); i++) {
            for (int j = i + 1; j < strokes.size(); j++) {
                distances[i][j] = strokes.get(i).minDistance(strokes.get(j));
                distances[j][i] = distances[i][j];
            }
        }
    }

    public void calculateNearestNeighbors(){
        neighboors = new OrderedStroke[strokes.size()][numberOfNeighbors];
        ArrayList<NearNeighboor> nearNeighbors = new ArrayList<NearNeighboor>();
        NearNeighboor neighbor;
        for (int i = 0; i < strokes.size(); i++) {
            for (int j = 0; j < strokes.size(); j++) {
                if(i != j){
                    neighbor = new NearNeighboor();
                    neighbor.setDistance(distances[i][j]);
                    neighbor.setIndexOfStroke(j);
                    nearNeighbors.add(neighbor);
                }
            }
            Collections.sort(nearNeighbors);
            for (int ii = 0; ii < numberOfNeighbors; ii++) {
                neighboors[strokes.get(i).getIndex()][ii] = strokes.get(
                        nearNeighbors.get(ii).getIndexOfStroke());
            }
            nearNeighbors.clear();
        }
    }
    
    public List<SymbolHypothesis> getHypothesis(OrderedStroke stroke) {
        return hypothesis[stroke.getIndex()].getHypothesis();     
    }

    private void combineStrokesWithNeighbors() {
        OrderedStroke[] strokeNeighbors;
        SymbolHypothesisForStroke symbolHypothesisForStroke;
        
        for (int i = 0; i < strokes.size(); i++) {
            OrderedStroke orderedStroke = strokes.get(i);
            HypothesisForStroke hypothesisPerStroke = new HypothesisForStroke();
            strokeNeighbors = neighboors[orderedStroke.getIndex()];
            ArrayList<SymbolHypothesis> hypothesisForStroke = 
                    hypothesisPerStroke.combine(orderedStroke, strokeNeighbors);
            
            for (SymbolHypothesis symbolHypothesis : hypothesisForStroke) {
                addHypothesisCheckingRepetitions(symbolHypothesis);
            }
            
//            hypothesisForStroke = filter.filter(hypothesisForStroke);
//            symbolHypothesisForStroke = new SymbolHypothesisForStroke();
//            symbolHypothesisForStroke.setHypothesis(hypothesisForStroke);
//            hypothesis[orderedStroke.getIndex()] = symbolHypothesisForStroke;
        }
    }
    
    private void groupSymbolHypothesisPerStroke() {
        for (SymbolHypothesis symbolHypothesis : allHypothesis) {
            for (OrderedStroke orderedStroke : strokes) {
                if(symbolHypothesis.containsStroke(orderedStroke.getIndex()))
                    hypothesis[orderedStroke.getIndex()].add(symbolHypothesis);
            }
        }
    }
    
    public void addHypothesisCheckingRepetitions(SymbolHypothesis newHypothesis){
        if(!alreadyInList(newHypothesis))
            allHypothesis.add(newHypothesis);
    }
    
    public boolean alreadyInList(SymbolHypothesis newHypothesis){
        boolean alreadyInList = false;
        for (SymbolHypothesis symbolHypothesis : allHypothesis) {
            if(symbolHypothesis.hasSameStrokes(newHypothesis)){
                alreadyInList = true;
                break;
            }
        }
       return alreadyInList;
    }

    public HypothesisFilter getFilter() {
        return filter;
    }

    public void setFilter(HypothesisFilter filter) {
        this.filter = filter;
    }

    private void filterHypothesis() {
        allHypothesis = filter.filter(allHypothesis);
    }

    
    
}