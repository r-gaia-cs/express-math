/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import br.usp.ime.faguilar.util.IndexedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Frank Aguilar
 */
public class NearestNeighborGraphHypothesisGenerator implements HypothesisGenerator{
    private static final int DEFAULT_NUMBER_OF_NEIGHBORS = 3;
    private int numberOfNeighbors;

    double[][] distances;
    private OrderedStroke[][] neighboors;
    private ArrayList<OrderedStroke> strokes;

    private HypothesisFilter filter;
//    private SymbolHypothesisForStroke[] hypothesis;
    private Map<Integer, SymbolHypothesisForStroke> hypothesis;
    private ArrayList<SymbolHypothesis> allHypothesis;
    
//        SOME INDEXES OF STROKES ARE BIGGER THAN THE NUMBER OF STROKES
//        THEN LETS ADD ADDITIONAL_BINS MORE BINARY POSITIONS
    public static final int ADDITIONAL_BINS = 0;
    private static final  int maxNumberOFHypothesesPerStroke = 5;//8;
    private double MAX_COST =  100000;
    private final double junkFactor = 1.5;

    @Override
    public void generateHypothesis(ArrayList<OrderedStroke> strokes) {
        init(strokes);
        fillDistancesMatrix();
        calculateNearestNeighbors();
        combineStrokesWithNeighbors();
        filterHypothesis();
        putSymbolLabelsInJunks();
        putLabelsInsDsymbols();
        orderAllHypothesis();
//        filterByCost();
        groupSymbolHypothesisPerStroke();
        
//        int numStrokes = strokes.size();
//        int count = 0;
//        for (Integer integer : hypothesis.keySet()) {
//            count += hypothesis.get(integer).getHypothesis().size();
//        }
//        System.out.println(count /(double)numStrokes);
        
//        TEMP CODE
//        filterNumHypothesisPerStroke();
    }
    
    private void init(ArrayList<OrderedStroke> inputStrokes){
        this.strokes = inputStrokes;
        
        numberOfNeighbors = Math.min(DEFAULT_NUMBER_OF_NEIGHBORS, strokes.size() - 1);
        SymbolHypothesis.BINARY_REPRESENTATION_LENGHT = (int) 
                Math.pow(2, Math.ceil(Math.log(strokes.size())/ Math.log(2))) + ADDITIONAL_BINS;
        hypothesis = new HashMap<>();
//        for (int i = 0; i < hypothesis.length; i++) {
//            hypothesis[i] = new SymbolHypothesisForStroke();
//        }
        allHypothesis = new ArrayList();
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
        ArrayList<NearNeighboor> nearNeighbors = new ArrayList();
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
//                neighboors[strokes.get(i).getIndex()][ii] = strokes.get(
//                        nearNeighbors.get(ii).getIndexOfStroke());
                neighboors[i][ii] = strokes.get(
                        nearNeighbors.get(ii).getIndexOfStroke());
            }
            nearNeighbors.clear();
        }
    }
    
    @Override
    public List<SymbolHypothesis> getHypothesis(OrderedStroke stroke) {
        return hypothesis.get(stroke.getIndex()).getHypothesis();
    }

    private void combineStrokesWithNeighbors() {
        OrderedStroke[] strokeNeighbors;
        SymbolHypothesisForStroke symbolHypothesisForStroke;
        
        for (int i = 0; i < strokes.size(); i++) {
            OrderedStroke orderedStroke = strokes.get(i);
            HypothesisForStroke hypothesisPerStroke = new HypothesisForStroke();
//            strokeNeighbors = neighboors[orderedStroke.getIndex()];
            strokeNeighbors = neighboors[i];
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
        List<SymbolHypothesis> newListOfHypothesis;
        for (SymbolHypothesis symbolHypothesis : allHypothesis) {
            for (OrderedStroke orderedStroke : strokes) {
                if(symbolHypothesis.containsStroke(orderedStroke.getIndex())){
//                    hypothesis[orderedStroke.getIndex()].add(symbolHypothesis);
                    if (hypothesis.containsKey(orderedStroke.getIndex()))
                        hypothesis.get(orderedStroke.getIndex()).add(symbolHypothesis);
                    else{
                        SymbolHypothesisForStroke newSymbolHypothesisForStroke = new SymbolHypothesisForStroke();
                        newSymbolHypothesisForStroke.setIndexOfStroke(orderedStroke.getIndex());
                        newListOfHypothesis = new ArrayList();
                        newListOfHypothesis.add(symbolHypothesis);
                        newSymbolHypothesisForStroke.setHypothesis(newListOfHypothesis);
                        hypothesis.put(orderedStroke.getIndex(), newSymbolHypothesisForStroke);
                    }
                }
            }
        }
//        for (Integer integer : hypothesis.keySet()) {
//            Collections.sort(hypothesis.get(integer).getHypothesis());
//        }
        RearangeToOneLabelPerHypothesis();
    }
    
    public void RearangeToOneLabelPerHypothesis(){
        List<SymbolHypothesis> hypothesisOfStroke;
        List<SymbolHypothesis> newHypothesesPerStroke;
        List<CostPerSymbolClass> costsPerClass;
        ArrayList<CostPerSymbolClass> individualCostPErClass;
        DSymbol newSymbol;
        List<SymbolHypothesis> allSelected = new ArrayList<>();
        SymbolHypothesis newHypothesis;
        for (Integer integer : hypothesis.keySet()) {
             hypothesisOfStroke = hypothesis.get(integer).getHypothesis();
             newHypothesesPerStroke = new ArrayList<>();
             for (SymbolHypothesis symbolHypothesis : hypothesisOfStroke) {
                costsPerClass = symbolHypothesis.getCostsPerClass();
                 for (CostPerSymbolClass costPerSymbolClass : costsPerClass) {
                     if (!costPerSymbolClass.getLabel().equalsIgnoreCase("junk")){
                        newHypothesis = new SymbolHypothesis(symbolHypothesis.getBinaryRepresentation().length);
                        newHypothesis.setBinaryRepresentation(symbolHypothesis.getBinaryRepresentation());
                        newHypothesis.setCost(costPerSymbolClass.getCost());
                        newSymbol = (DSymbol) symbolHypothesis.getSymbol().clone();
                        newSymbol.setLabel(costPerSymbolClass.getLabel());
                        newHypothesis.setSymbol(newSymbol);
                        individualCostPErClass = new ArrayList<>();
                        individualCostPErClass.add(costPerSymbolClass);
                        newHypothesis.setCostsPerClass(individualCostPErClass);
                        newHypothesesPerStroke.add(newHypothesis);
                     }
                 }
            }
//            Collections.sort(newHypothesesPerStroke);
//            hypothesis.get(integer).setHypothesis(newHypothesesPerStroke);
             hypothesis.get(integer).setHypothesis(selectHypothesesForStroke(allSelected, newHypothesesPerStroke));
        }
    }
    
    public List<SymbolHypothesis> selectHypothesesForStroke(List<SymbolHypothesis> allSelected, List<SymbolHypothesis> hypothesesFOrStroke){
        List<SymbolHypothesis> selected = new ArrayList<>();
        double totalCost = 0;
        Collections.sort(hypothesesFOrStroke);
        SymbolHypothesis hypothesisOfOneStroke = null;
        for (SymbolHypothesis symbolHypothesis : hypothesesFOrStroke) {
            totalCost += symbolHypothesis.getCost();
            if(symbolHypothesis.getSymbol().size() == 1)
                hypothesisOfOneStroke = symbolHypothesis;
        }
        double defaultMaxSum = 0.005;//0.5;
        
        
        double maxSum = Math.max(hypothesesFOrStroke.get(0).getCost() / (double) totalCost
                , defaultMaxSum);
//        selected.add(hypothesisOfOneStroke);
//        maxSum = Math.min(maxSum, maxNumberOFHypothesesPerStroke);
        double normalizedCost;
        double sum = 0;
        for (SymbolHypothesis symbolHypothesis : hypothesesFOrStroke) {
//            if(symbolHypothesis.getSymbol().size() > 1){
                normalizedCost = symbolHypothesis.getCost() / (double) totalCost;
//                sum += normalizedCost;
//                if(sum <= maxSum){
                if(normalizedCost <= maxSum){
//                    if(!alreadyInList(allSelected, symbolHypothesis)){
                        selected.add(symbolHypothesis);
                        if(selected.size() >= maxNumberOFHypothesesPerStroke)
                            break;
//                    }
                } 
                else
                    break;
//            }
            
        }        
        boolean hasOnStrokeSH = false;
        for (SymbolHypothesis symbolHypothesis : selected) {
            if(symbolHypothesis.getSymbol().size() == 1){
                hasOnStrokeSH = true;
                break;
            }
        }
        if(!hasOnStrokeSH)
            selected.add(hypothesisOfOneStroke);
//        Collections.sort(selected);
        allSelected.addAll(selected);
        return selected;
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
    
    public boolean alreadyInList(List<SymbolHypothesis> listHypotheses,SymbolHypothesis newHypothesis){
        boolean alreadyInList = false;
        for (SymbolHypothesis symbolHypothesis : listHypotheses) {
            if(symbolHypothesis.hasSameStrokes(newHypothesis) && 
                    symbolHypothesis.getLabel(0).equals(newHypothesis.getLabel(0))){
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
//        filterSymbols();
        filterJunks();
    }
    
    public void filterJunks(){

        ArrayList<SymbolHypothesis> selected = new ArrayList<>();
        List<SymbolHypothesis> junk = new ArrayList<>();
        float percentageOfJunkToSelect = (float) 0.2;
        int numberOfJunksToSelect;
        for (SymbolHypothesis symbolHypothesis : allHypothesis) {
            if(symbolHypothesis.getSymbol().size() > 1 &&
                    symbolHypothesis.getLabel(0).equalsIgnoreCase("junk"))
                junk.add(symbolHypothesis);
            else
                selected.add(symbolHypothesis);
        }
        numberOfJunksToSelect = Math.round(percentageOfJunkToSelect * junk.size());
        Collections.sort(junk);
        double minCostPossibleSymbol = 0.;//0.01;
        for (int i = 0; i < numberOfJunksToSelect; i++) {
            if(junk.get(junk.size() - i - 1).getCost() > minCostPossibleSymbol)
                selected.add(junk.get(junk.size() - i - 1));
            else 
                break;
        }
        allHypothesis = selected;
        if (allHypothesis.size() < strokes.size()) {
            int dif = strokes.size() - allHypothesis.size();
            for (int i = 0; i < dif; i++) {
                allHypothesis.add(junk.get(junk.size() - (numberOfJunksToSelect + i) - 1));
            }
        }
    }

    @Override
    public ArrayList<SymbolHypothesis> getAllHypothesis() {
        return allHypothesis;
    }

    private void filterNumHypothesisPerStroke() {
        List<SymbolHypothesis> selected;
        List<SymbolHypothesis> currentHypotheses = new ArrayList<>();
        List<SymbolHypothesis> junk = new ArrayList<>();
//        int numSelected;
////        for (Integer integer : hypothesis.keySet()) {
////            selected = hypothesis.get(integer).getHypothesis();
////            Collections.sort(selected);
////            numSelected = Math.min(2, selected.size());
////            hypothesis.get(integer).setHypothesis(selected.subList(0, numSelected));
////        }
//        int numJunkSelected = percentageJun
        float percentageOfJunkToSelect = (float) 0.5;
        int numberOfJunksToSelect;
        for (Integer integer : hypothesis.keySet()) {
            selected = new ArrayList<>();
            currentHypotheses = hypothesis.get(integer).getHypothesis();
            for (SymbolHypothesis symbolHypothesis : currentHypotheses) {
                if(symbolHypothesis.getLabel(0).equalsIgnoreCase("junk"))
                    junk.add(symbolHypothesis);
                else
                    selected.add(symbolHypothesis);
            }
            numberOfJunksToSelect = (int) (percentageOfJunkToSelect * junk.size());
            Collections.sort(junk);
            for (int i = 0; i < numberOfJunksToSelect; i++) 
                selected.add(junk.get(junk.size() - i - 1));
            hypothesis.get(integer).setHypothesis(selected);
            junk.clear();
        }
        
    }
    
    @Override
    public ArrayList<SymbolHypothesis> getAllHypothesisFilteredByStroke() {
        ArrayList<SymbolHypothesis> selected = new ArrayList<>();
        for (Integer integer : hypothesis.keySet()) {
            selected.addAll(hypothesis.get(integer).getHypothesis());
        }
        return selected;
    }

    private void putSymbolLabelsInJunks() {
//        ArrayList<IndexedValue> indexedValues = new ArrayList();
        CostPerSymbolClass costPerClass;
        for (SymbolHypothesis symbolHypothesis : allHypothesis) {
            ArrayList<CostPerSymbolClass> costsPerClass = symbolHypothesis.getCostsPerClass();
            if(costsPerClass.get(0).getLabel().equalsIgnoreCase("junk")){
//                if (costsPerClass.size() > 1)
                  costsPerClass.remove(0);
//                else
                    
                for (CostPerSymbolClass costPerSymbolClass : costsPerClass) {
                    costPerSymbolClass.setCost(costPerSymbolClass.getCost() * junkFactor);
                }
                symbolHypothesis.setCost(costsPerClass.get(0).getCost());
//                symbolHypothesis.setCost(MAX_COST);
            }
        }
        
        
//        for (int i = 0; i < networkOutput.length; i++) {
//            indexedValues.add(IndexedValue.createIndexedValueFromIndexAndValue(
//                    i, networkOutput[i]));            
//        }
//        Collections.sort(indexedValues);
//        ArrayList<String> labels = new ArrayList<>();
//        String label;
//        int indexOFNetworkOutput;
//        for (int i = 0; i < numberofPossibleClasses; i++) {
//            indexOFNetworkOutput = indexedValues.get(
//                    indexedValues.size() - i - 1).getIndex();
//            label = SymbolLabels.getLabelOfSymbolByIndex(indexOFNetworkOutput);
//            if (!label.equalsIgnoreCase("junk")){
//                symbolHypothesis.setCost(- Math.log(networkOutput[indexOFNetworkOutput]));
//                labels.add(label);
//                symbolHypothesis.setLabels(labels);
//                break;
//            } 
//            
////            else {
////                symbolHypothesis.setCost(- Math.log(networkOutput[indexedValues.get(
////                    indexedValues.size() - numberofPossibleClasses - 1).getIndex()]));
////                
////                indexOFNetworkOutput = indexedValues.get(
////                    indexedValues.size() - i - 2).getIndex();
////                label = SymbolLabels.getLabelOfSymbolByIndex(indexOFNetworkOutput);
////                
////                labels.add(label);
////                symbolHypothesis.setLabels(labels);
////                break;
////            }
//        }
    }
    
    //    public void putCostAndBestSymbolLabel(SymbolHypothesis symbolHypothesis) {
//        ArrayList<IndexedValue> indexedValues = new ArrayList();
//        for (int i = 0; i < networkOutput.length; i++) {
//            indexedValues.add(IndexedValue.createIndexedValueFromIndexAndValue(
//                    i, networkOutput[i]));            
//        }
//        Collections.sort(indexedValues);
//        ArrayList<String> labels = new ArrayList<>();
//        String label;
//        int indexOFNetworkOutput;
//        for (int i = 0; i < numberofPossibleClasses; i++) {
//            indexOFNetworkOutput = indexedValues.get(
//                    indexedValues.size() - i - 1).getIndex();
//            label = SymbolLabels.getLabelOfSymbolByIndex(indexOFNetworkOutput);
//            if (!label.equalsIgnoreCase("junk")){
//                symbolHypothesis.setCost(- Math.log(networkOutput[indexOFNetworkOutput]));
//                labels.add(label);
//                symbolHypothesis.setLabels(labels);
//                break;
//            } 
//            
////            else {
////                symbolHypothesis.setCost(- Math.log(networkOutput[indexedValues.get(
////                    indexedValues.size() - numberofPossibleClasses - 1).getIndex()]));
////                
////                indexOFNetworkOutput = indexedValues.get(
////                    indexedValues.size() - i - 2).getIndex();
////                label = SymbolLabels.getLabelOfSymbolByIndex(indexOFNetworkOutput);
////                
////                labels.add(label);
////                symbolHypothesis.setLabels(labels);
////                break;
////            }
//        }
//    }

    private void orderAllHypothesis() {
        Collections.sort(allHypothesis);
    }

//    private void filterByCost() {
//        double percentage = 0.5;
//        int maxNumber = (int) Math.round(allHypothesis.size() * percentage);
//        ArrayList<SymbolHypothesis> newList = new ArrayList<>();
//        newList.addAll(allHypothesis.subList(0, maxNumber));
//        allHypothesis = newList;
////        allHypothesis = (ArrayList<SymbolHypothesis>) allHypothesis.subList(0, maxNumber);
//    }

    private void putLabelsInsDsymbols() {
        for (SymbolHypothesis symbolHypothesis : allHypothesis) {
            symbolHypothesis.getSymbol().setLabel(symbolHypothesis.getLabel(0));
        }
    }

    private void filterSymbols() {
        //        VALIDATE THAT THE TOTAL NUMBER OF HYPOTHESES BE BIGGER OR EQUAL 
//        THAN THE NUMBER OF STROKES
        ArrayList<SymbolHypothesis> selected = new ArrayList<>();
        List<SymbolHypothesis> symbols = new ArrayList<>();
        float percentageOfSymbolsToSelect = (float) 0.98;
        int numberOfSymbolsToSelect;
        for (SymbolHypothesis symbolHypothesis : allHypothesis) {
            if(!symbolHypothesis.getLabel(0).equalsIgnoreCase("junk"))
                symbols.add(symbolHypothesis);
            else
                selected.add(symbolHypothesis);
        }
        numberOfSymbolsToSelect = (int) (percentageOfSymbolsToSelect * symbols.size());
        Collections.sort(symbols);
        for (int i = 0; i < numberOfSymbolsToSelect; i++) 
            selected.add(symbols.get(i));
        allHypothesis = selected;
    }

    
}