/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.segmentation.CompundSymbolsBuilder;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import br.usp.ime.faguilar.util.ListUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author frank
 * Builds a Baseline Structure Tree from 
 * a set of Nodes
 */
public class BSTBuilder {
    private RegionNode root;
    private List<DSymbol> mySymbols;
    private boolean hasParsing;
    
    public String treeString(){
        String string = root.treeString();
        return string;
    }
    
    public String labelGraph(){
        return root.LabelGraphString();
    }
    
    public RegionNode buildBST(List<DSymbol> symbols, Classifier classifier){
//        CompundSymbolsBuilder builder = CompundSymbolsBuilder.builderFromInputSymbols(symbols);
//        builder.setClassifier(classifier);
//        builder.calculateCompoundSymbols();
//        symbols = builder.getCompoundSymbols();
        hasParsing = true;
        mySymbols = symbols;
        List<SymbolNode> symbolNodes = new ArrayList<SymbolNode>(symbols.size());
        for (int i = 0; i < symbols.size(); i++) {
            SymbolNode symbolNode = new SymbolNode(symbols.get(i));
            symbolNodes.add(symbolNode);
        }
        return buildBSTFromSymbolNodes(symbolNodes);
    }
    
    public DMathExpression symbolsAsExpression(){
        return CompundSymbolsBuilder.symbolsAsMathExpression(mySymbols);
    }
    
    public RegionNode buildBSTFromSymbolNodes(List<SymbolNode> symbolNodes){
        RegionNode rootNode = new RegionNode();
        rootNode.setLabel(RegionLabel.EXPRESSION);
        setRoot(rootNode);
        if(symbolNodes.isEmpty())
            return rootNode;
        List<SymbolNode> sortedSymbolNodes = new ArrayList<>(symbolNodes);
        Collections.sort(sortedSymbolNodes, SymbolNode.minXComparator());
        copySortedSymbols(sortedSymbolNodes);
        rootNode.addChildren(sortedSymbolNodes);
        return extractBaseline(rootNode);
    }
    
    public void copySortedSymbols(List<SymbolNode> nodes){
        StructuralRelation.symbolsSortedByXCoord = new ArrayList<>();
        for (SymbolNode symbolNode : nodes) {
            StructuralRelation.symbolsSortedByXCoord.add(symbolNode.getSymbol());
        }
    }
    
    public RegionNode extractBaseline(RegionNode regionNode){
        List<SymbolNode> children = regionNode.getChildren();
        if(children.size() <= 1)
            return regionNode;
        SymbolNode startSymbol = startSymbol(children);
        List<SymbolNode> startList = new ArrayList<>();
        startList.add(startSymbol);
        List<SymbolNode> baseLineSymbols = horizontal(startList, children);
        List<SymbolNode> updatedBaseline = collectRegions(baseLineSymbols);
        regionNode.clearChildren();
        regionNode.addChildren(updatedBaseline);
        for (SymbolNode symbolNode : updatedBaseline)
            for (RegionNode childrenNode : symbolNode.getChildren())
                childrenNode = extractBaseline(childrenNode);
        return regionNode;
    }
    
    /**
     * 
     * @param nodes
     * @return 
     */
    private SymbolNode startSymbol(List<SymbolNode> nodes) {
        List<SymbolNode> newNodes = new ArrayList<>(nodes);
        return recursiveStartSymbol(newNodes);
    }
    
    /**
     * modifies the parameter nodes
     * @param nodes
     * @return 
     */
    private SymbolNode recursiveStartSymbol(List<SymbolNode> nodes) {
        if(nodes.size() >= 2){
            int candidate1 = nodes.size() - 1;
            int candidate2 = nodes.size() - 2;
            if(StructuralRelation.symbolDominatesPreviousSymbol(nodes.get(candidate1).getSymbol(), 
                    nodes.get(candidate2).getSymbol()) && 
                    !StructuralRelation.horizontalOverlappingGraterThan(nodes.get(candidate1).getSymbol(), 
                    nodes.get(candidate2).getSymbol(), StructuralRelation.minIntersectionRatio))
                nodes.remove(candidate2);
            else
                nodes.remove(candidate1);
            return recursiveStartSymbol(nodes);
        }else
            return nodes.get(0);
    }

    private List<SymbolNode>  partition(List<SymbolNode> nodes, SymbolNode symbol) {
        int regionLabel;
        List<SymbolNode> remainingNodes = new ArrayList<SymbolNode>();
        for (SymbolNode symbolNode : nodes) {
            if(symbol != symbolNode){
                regionLabel = symbol.determineRegionForPartitionFunction(symbolNode.getSymbol());
                if(regionLabel == RegionLabel.NOT_DEFINED)
                    remainingNodes.add(symbolNode);
                else{
//                    if(regionLabel == RegionLabel.ABOVE || regionLabel == RegionLabel.BELOW){
//                        if(canHaveAboveOrBelow(symbol.getSymbol()))
//                            symbol.addNodeToRegion(symbolNode, regionLabel);
//                        else
//                            remainingNodes.add(symbolNode);
//                    }
//                    else
                        symbol.addNodeToRegion(symbolNode, regionLabel);
                }
            }
        }
        return remainingNodes;
    }
    
    
    
    private List<SymbolNode> horizontal(List<SymbolNode> startList, List<SymbolNode> nodes) {
        if(nodes.isEmpty()) 
            return startList;
        SymbolNode currentSymbol = startList.get(startList.size() - 1);
        List<SymbolNode> remainingSymbols = partition(nodes, currentSymbol);
        if(remainingSymbols.isEmpty())
            return startList;
        if(SymbolClass.symbolClass(currentSymbol.getSymbol()) == SymbolClass.NON_SCRIPTED){
            List<SymbolNode> newStartSymbols = ListUtil.concat(startList, 
                    startSymbol(remainingSymbols));
            return horizontal(newStartSymbols, remainingSymbols);
        }
        List<SymbolNode> SL = new LinkedList<SymbolNode>(remainingSymbols);
        while(!SL.isEmpty()){
            SymbolNode firstNode = SL.get(0);
            if(RelationClassifier.isHighProbabilityJunkRelation(currentSymbol.getSymbol(), firstNode.getSymbol()))
                    hasParsing = false;
            if(StructuralRelation.isRegularHor(currentSymbol.getSymbol(), firstNode.getSymbol())
                    || symbolMustBeAtHorizontal(currentSymbol.getSymbol(), firstNode.getSymbol())
//                    || StructuralRelation.horizontalOverlappingGraterThan(
//                    currentSymbol.getSymbol(), firstNode.getSymbol(), StructuralRelation.minIntersectionRatio)
                    ){
                return horizontal(ListUtil.concat(startList, 
                        StructuralRelation.checkOverlap(firstNode, remainingSymbols)), 
                        remainingSymbols);
                
            }
                
            SL.remove(0);
        }
        partitionFinal(remainingSymbols, currentSymbol);
        return ListUtil.concat(startList.subList(0, startList.size() - 1), currentSymbol);
    }
    
    private boolean symbolMustBeAtHorizontal(DSymbol symbol, DSymbol mayBeAtHorizontal) {
        if(mustHaveOperandAtLeft(mayBeAtHorizontal.getLabel()) && 
                StructuralRelation.overlapsY(symbol, mayBeAtHorizontal))
            return true;
        return false;
    }
    
    private boolean mustHaveOperandAtLeft(String label) {
        if(label.equals("+") || label.equals("=") || label.equals("\\leq") ||
                label.equals("\\lt") || label.equals("\\gt") || label.equals("\\geq") ||
                label.equals("\\div") || label.equals("\\neq") || label.equals("\\pm") ||
                label.equals("\\in") || label.equals("\\rightarrow"))
            return true;
        return false;
    }

    private List<SymbolNode> collectRegions(List<SymbolNode> baselineSymbols) {
        List<SymbolNode> baselineCopy = new LinkedList<SymbolNode>(baselineSymbols);
        return recursiveCollectRegions(baselineCopy);
    }
    
    private List<SymbolNode> recursiveCollectRegions(List<SymbolNode> baselineSymbols) {
        if(baselineSymbols.isEmpty())
            return baselineSymbols;
        SymbolNode firstNode = new SymbolNode(baselineSymbols.remove(0));
        if(baselineSymbols.size() > 0){
            SymbolNode secondNode = baselineSymbols.get(0);
            PartitionSharedRegionResult resultTopLeft = partitionSharedRegion(
                    RegionLabel.TOP_LEFT, firstNode, secondNode);
            if(resultTopLeft != null){
                firstNode.addNodesToRegion(resultTopLeft.superOrSubscriptRegionSymbols, 
                    RegionLabel.SUPERSCRIPT);
                secondNode.removeRegion(RegionLabel.TOP_LEFT);
                secondNode.addNodesToRegion(resultTopLeft.aboveOrBelowRegionSymbols, 
                    RegionLabel.TOP_LEFT);
            }
            PartitionSharedRegionResult resultBottomLeft = partitionSharedRegion(
                    RegionLabel.BOTTOM_LEFT, firstNode, secondNode);
            if(resultBottomLeft != null){
                firstNode.addNodesToRegion(resultBottomLeft.superOrSubscriptRegionSymbols, 
                    RegionLabel.SUBSCRIPT);
                secondNode.removeRegion(RegionLabel.BOTTOM_LEFT);
                secondNode.addNodesToRegion(resultBottomLeft.aboveOrBelowRegionSymbols, 
                    RegionLabel.BOTTOM_LEFT);
            }
            
        }
        if(SymbolClass.symbolClass(firstNode.getSymbol()) == SymbolClass.VARIABLE_RANGE)
            mergeRegions(firstNode);
        List<SymbolNode> firstNodeList = new ArrayList<SymbolNode>();
        firstNodeList.add(firstNode);
        return ListUtil.concat(firstNodeList, recursiveCollectRegions(
                baselineSymbols));
    }
    
    private void mergeRegions(SymbolNode node){
        List<Integer> regions = new ArrayList<Integer>();
        regions.add(RegionLabel.TOP_LEFT);
        regions.add(RegionLabel.ABOVE);
        regions.add(RegionLabel.SUPERSCRIPT);
        node.mergeRegions(regions, RegionLabel.ABOVE);
        regions.clear();
        regions.add(RegionLabel.BOTTOM_LEFT);
        regions.add(RegionLabel.BELOW);
        regions.add(RegionLabel.SUBSCRIPT);
        node.mergeRegions(regions, RegionLabel.BELOW);
    }
    
    private PartitionSharedRegionResult partitionSharedRegion(int regionLabel, SymbolNode node1, SymbolNode node2){
        PartitionSharedRegionResult modifiedNodes = null;
        if(hasNonEmptyRegion(node2, regionLabel)){
            modifiedNodes = new PartitionSharedRegionResult();
            int node2Region = RegionLabel.ABOVE;
            if(regionLabel == RegionLabel.BOTTOM_LEFT)
                node2Region = RegionLabel.BELOW;
            RegionNode regionNode = node2.getRegion(regionLabel);
            List<SymbolNode> SL = regionNode.getChildren();
            if(SymbolClass.symbolClass(node1.getSymbol()) == SymbolClass.NON_SCRIPTED)
                modifiedNodes.superOrSubscriptRegionSymbols = new ArrayList<SymbolNode>();
            else if(SymbolClass.symbolClass(node2.getSymbol()) != SymbolClass.VARIABLE_RANGE || 
                    (SymbolClass.symbolClass(node2.getSymbol()) == SymbolClass.VARIABLE_RANGE
                    && !hasNonEmptyRegion(node2, node2Region)))
                modifiedNodes.superOrSubscriptRegionSymbols = SL;
            else if(SymbolClass.symbolClass(node2.getSymbol()) == SymbolClass.VARIABLE_RANGE
                    && hasNonEmptyRegion(node2, node2Region))
                modifiedNodes.superOrSubscriptRegionSymbols = selecectSubScriptOrSubScriptNodes(SL, node2);
            boolean isSelected;
            List<SymbolNode> nonSelected = new ArrayList<SymbolNode>();
            for (SymbolNode symbolNode : SL) {
                isSelected = false;
                for (SymbolNode selected : modifiedNodes.superOrSubscriptRegionSymbols) {
                    if(symbolNode == selected){
                        isSelected = true;
                        break;
                    }
                }
                if(!isSelected)
                    nonSelected.add(symbolNode);
            }
            modifiedNodes.aboveOrBelowRegionSymbols = nonSelected;
        }
//        if(modifiedNodes != null){
//            System.out.println("non selected");
//            for (SymbolNode symbolNode : modifiedNodes.superOrSubscriptRegionSymbols) {
//                for (DStroke dStroke : symbolNode.getSymbol()) {
//                    System.out.println(((OrderedStroke) dStroke).getIndex());
//                }
//            }
//        }
//        
//        if(modifiedNodes != null){
//            System.out.println("selected");
//            for (SymbolNode symbolNode : modifiedNodes.aboveOrBelowRegionSymbols) {
//                for (DStroke dStroke : symbolNode.getSymbol()) {
//                    System.out.println(((OrderedStroke) dStroke).getIndex());
//                }
//            }
//        }
        return modifiedNodes;
    }

    private List<SymbolNode> selecectSubScriptOrSubScriptNodes(List<SymbolNode> SL, SymbolNode node) {
        List<SymbolNode> selected = new ArrayList<SymbolNode>();
        for (SymbolNode symbolNode : SL)
            if(StructuralRelation.isAdjacent(symbolNode.getSymbol(), node.getSymbol()))
                selected.add(node);
        Collections.sort(selected, SymbolNode.minXComparator());
        return selected;
    }

    private boolean canHaveAboveOrBelow(DSymbol symbol) {
        if(SymbolClass.symbolClass(symbol) == SymbolClass.VARIABLE_RANGE || 
//                symbol.getLabel().equals("\\frac")  // crohme contest does not use \\frac
                symbol.getLabel().equals("-") 
                || symbol.getLabel().equals("\\lim"))
            //do i need to consider other symbols?
            return true;
        return false;
        
    }

    public String latexResult() {
        return root.latexString();
    }

        
    
    private class PartitionSharedRegionResult{
        private List<SymbolNode> aboveOrBelowRegionSymbols;
        private List<SymbolNode> superOrSubscriptRegionSymbols;
        
        public PartitionSharedRegionResult(){
            aboveOrBelowRegionSymbols = new ArrayList<SymbolNode>();
            superOrSubscriptRegionSymbols = new ArrayList<SymbolNode>();
        }
    }
    
    private boolean hasNonEmptyRegion(SymbolNode node, int regionLabel){
        RegionNode rnode = node.getRegion(regionLabel);
        if(rnode == null)
            return false;
        if(rnode.getChildren().isEmpty())
            return false;
        return true;
    }

    public RegionNode getRoot() {
        return root;
    }

    public void setRoot(RegionNode root) {
        this.root = root;
    }    

    private void partitionFinal(List<SymbolNode> symbolNodes, SymbolNode aSymbolNode) {
        for (SymbolNode symbolNode : symbolNodes) {
            if(aSymbolNode != symbolNode){
                if(StructuralRelation.isSymbolAtSubscriptRegion(aSymbolNode.getSymbol(), symbolNode.getSymbol()) || 
                        StructuralRelation.isSymbolAtBelowRegion(aSymbolNode.getSymbol(), symbolNode.getSymbol()))
                    aSymbolNode.addNodeToRegion(symbolNode, RegionLabel.SUBSCRIPT);
                else 
//                    if(StructuralRelation.isSymbolAtSuperscriptRegion(aSymbolNode.getSymbol(), symbolNode.getSymbol()) || 
//                        StructuralRelation.isSymbolAtAboveRegion(aSymbolNode.getSymbol(), symbolNode.getSymbol()))
                    aSymbolNode.addNodeToRegion(symbolNode, RegionLabel.SUPERSCRIPT);
            }
        }
    }

    public boolean isHasParsing() {
        return hasParsing;
    }

    public void setHasParsing(boolean hasParsing) {
        this.hasParsing = hasParsing;
    }
    
}