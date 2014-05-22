/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.util.ListUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Frank
 */
public class MultipleBSTBuilder extends BSTBuilder{
    private TreeStructuralChange treeChanges;
    private int currentRegionID;
    private RegionNode currentRegion;
    private List<List<NodeStructuralChange>> changeCombinations;
    private List<RegionNode> treeRoots;

    List<NodeStructuralChange> ambiguiusNodeRelations;
    
    public MultipleBSTBuilder() {
        super();
        treeChanges = new TreeStructuralChange();
    }    
    
    public void generateMultipleParses(){
        generateChangeCombinations();
        generateModifiedBSTress();
    }

    @Override
    public RegionNode buildBSTFromSymbolNodes(List<SymbolNode> symbolNodes){
        RegionNode rootNode = new MultipleBaselineRegionNode();
        rootNode.setLabel(RegionLabel.EXPRESSION);
        setRoot(rootNode);
        if(symbolNodes.isEmpty())
            return rootNode;
        List<SymbolNode> sortedSymbolNodes = new ArrayList<>(symbolNodes);
        Collections.sort(sortedSymbolNodes, SymbolNode.minXComparator());
        copySortedSymbols(sortedSymbolNodes);
        rootNode.addChildren(sortedSymbolNodes);
        currentRegionID = 0;
        return extractBaselineRecursively(rootNode);
    }
    
    @Override
    public RegionNode extractBaselineRecursively(RegionNode regionNode){ 
        currentRegionID++;
        currentRegion = regionNode;
        List<SymbolNode> children = regionNode.getChildren();
        if(children.size() <= 1)
            return regionNode;
        SymbolNode startSymbol = startSymbol(children);
        List<SymbolNode> startList = new ArrayList<>();
        startList.add(startSymbol);
        List<SymbolNode> baseLineSymbols = horizontal(startList, children);
        List<List<SymbolNode>> multipleBaselines = generateMultipleBaselines();
        Baseline aBaseline;
        for (List<SymbolNode> baseline : multipleBaselines) {
            aBaseline = new Baseline();
            aBaseline.addAllNodes(collectRegions(baseline));
            ((MultipleBaselineRegionNode) regionNode).addBaseline(
                    aBaseline);
            for (SymbolNode symbolNode : aBaseline.getNodes()) {
                for (RegionNode childrenNode : symbolNode.getChildren()) {
                    extractBaselineRecursively(childrenNode);
                }
            }
        }
        
        
        
//        List<SymbolNode> updatedBaseline = collectRegions(baseLineSymbols);
//        regionNode.clearChildren();
//        regionNode.addChildren(updatedBaseline);
//        for (SymbolNode symbolNode : updatedBaseline)
//            for (RegionNode childrenNode : symbolNode.getChildren())
//                childrenNode = extractBaselineRecursively(childrenNode);
        return regionNode;
    }
    
    @Override
    protected List<SymbolNode> horizontal(List<SymbolNode> startList, List<SymbolNode> nodes) {
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
        List<SymbolNode> SL = new LinkedList<>(remainingSymbols);
        SymbolNode overlappingResult;
        while(!SL.isEmpty()){
            SymbolNode firstNode = SL.get(0);
            if(RelationClassifier.isHighProbabilityJunkRelation(currentSymbol.getSymbol(), firstNode.getSymbol()))
                    setHasParsing(false);
            if(StructuralRelation.isRegularHor(currentSymbol.getSymbol(), firstNode.getSymbol())
                    || symbolMustBeAtHorizontal(currentSymbol.getSymbol(), firstNode.getSymbol())
//                    || StructuralRelation.horizontalOverlappingGraterThan(
//                    currentSymbol.getSymbol(), firstNode.getSymbol(), StructuralRelation.minIntersectionRatio)
                    ){
                overlappingResult = StructuralRelation.checkOverlap(firstNode, remainingSymbols);
                if(RelationClassifier.isAmbiguousRelation(currentSymbol.getSymbol(), overlappingResult.getSymbol()))
                    addAmbiguousRelation(overlappingResult);                
                return horizontal(ListUtil.concat(startList, 
                        overlappingResult), 
                        remainingSymbols);                
            }
            if(RelationClassifier.isAmbiguousRelation(currentSymbol.getSymbol(), firstNode.getSymbol()))
                addAmbiguousRelation(firstNode);    
            SL.remove(0);
        }
        partitionFinal(remainingSymbols, currentSymbol);
        return ListUtil.concat(startList.subList(0, startList.size() - 1), currentSymbol);
    }
  
    private void addAmbiguousRelation(SymbolNode node) {
        NodeStructuralChange nodeChange = new NodeStructuralChange();
        nodeChange.setSymbolNode(node);
        nodeChange.setRegion(currentRegion);
        int ambiguityType = RelationClassifier.getLastAmbiguityType();
        nodeChange.setType(ambiguityType);
        nodeChange.setRegionLevel(currentRegionID);
        ambiguiusNodeRelations.add(nodeChange);
//        treeChanges.addNodeStructuralChangeAtLevel(nodeChange, currentRegionID);        
    }    

    private void generateChangeCombinations() {
        List<NodeStructuralChange> allNodeChanges = new ArrayList<>();
        for (LevelStructuralChange levelChange : treeChanges.getChangesInLevels()) {
            for (NodeStructuralChange nodeStructuralChange : levelChange.getChangesInNodes()) {
                allNodeChanges.add(nodeStructuralChange);
            }
        }
        changeCombinations = ListUtil.generateCombinations(allNodeChanges);
    }
    
// THIS METHOD CONSIDERS THAT THE COMBINATIONS ARE SORTES 
//    AS IN THE COMBINATIONS OF 0, 1, 2 AS FOLLOWS:
//0 
//1 
//0 1 
//2 
//0 2 
//1 2 
//0 1 2 
    
    private void generateModifiedBSTress() {
        RegionNode newRoot;
        for (List<NodeStructuralChange> aCombination : changeCombinations) {
            newRoot = applyStructuralChanges(aCombination);
            treeRoots.add(newRoot);
        }
    }

    private RegionNode applyStructuralChanges(List<NodeStructuralChange> aCombination) {
        RegionNode newRoot = TreeStructuralChange.applyChanges(getRoot(), aCombination);
        return newRoot;
    }

    private List<List<SymbolNode>> generateMultipleBaselines() {
        List<List<SymbolNode>> baselines = new ArrayList<>();
        List<List<NodeStructuralChange>> combinations = ListUtil.generateCombinations(ambiguiusNodeRelations);
        for (List<NodeStructuralChange> aCombination : combinations) {
            
        }
        return baselines;
    }
}
