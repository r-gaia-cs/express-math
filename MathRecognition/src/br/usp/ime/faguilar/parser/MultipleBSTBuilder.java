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
    private List<List<SymbolNode>> horizontalResults;

    List<NodeStructuralChange> ambiguiusNodeRelations;
    
    public MultipleBSTBuilder() {
        super();
        treeChanges = new TreeStructuralChange();
        horizontalResults = new ArrayList<>();
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
        if (children.size() <= 1) {
            Baseline aBaseline;
            aBaseline = new Baseline();
            aBaseline.addNode(new SymbolNode(children.get(0)));
            ((MultipleBaselineRegionNode) regionNode).addBaseline(
                    aBaseline);
            return regionNode;
        }
        SymbolNode startSymbol = startSymbol(children);
        List<SymbolNode> startList = new ArrayList<>();
        startList.add(new SymbolNode(startSymbol.getSymbol()));
//        ambiguiusNodeRelations.clear();
        horizontalResults.clear();
//        List<SymbolNode> baseLineSymbols = horizontal(startList, children);
        horizontal(startList, children);
//        List<List<SymbolNode>> multipleBaselines = generateMultipleBaselines(baseLineSymbols);
        Baseline aBaseline;
//        for (List<SymbolNode> baseline : multipleBaselines) {
        for (List<SymbolNode> baseline : horizontalResults) {
            aBaseline = new Baseline();
            aBaseline.addAllNodes(collectRegions(baseline));
            ((MultipleBaselineRegionNode) regionNode).addBaseline(
                    aBaseline);
        }
        for (Baseline baselineChild : ((MultipleBaselineRegionNode) regionNode).getBaselines()) {
            for (SymbolNode symbolNode : baselineChild.getNodes()) {
                for (RegionNode childrenNode : symbolNode.getChildren()) {
                    childrenNode = extractBaselineRecursively(childrenNode);
                }
            }
        }
        return regionNode;
    }
    
    @Override
    protected List<SymbolNode> horizontal(List<SymbolNode> startList, List<SymbolNode> nodes) {
        SymbolNode newNode;
        SymbolNode newPrevious;
        if(nodes.isEmpty()) {
            horizontalResults.add(startList);
            return startList;
        }
        SymbolNode currentSymbol = startList.get(startList.size() - 1);
        List<SymbolNode> remainingSymbols = partition(nodes, currentSymbol);
        if(remainingSymbols.isEmpty()){
            horizontalResults.add(startList);
            return startList;
        }
        if(SymbolClass.symbolClass(currentSymbol.getSymbol()) == SymbolClass.NON_SCRIPTED){
            newNode = copySymbolNodeStructure(startSymbol(remainingSymbols));
            List<SymbolNode> newStartSymbols = ListUtil.concat(startList, 
                    newNode);
            return horizontal(newStartSymbols, remainingSymbols);
        }
        List<SymbolNode> SL = new LinkedList<>(remainingSymbols);
        SymbolNode overlappingResult;
        SymbolNode tempSymbolNode;
        while(!SL.isEmpty()){
            SymbolNode firstNode = SL.get(0);
            overlappingResult = firstNode;
            if(RelationClassifier.isHighProbabilityJunkRelation(currentSymbol.getSymbol(), firstNode.getSymbol()))
                    setHasParsing(false);
            if(StructuralRelation.isRegularHor(currentSymbol.getSymbol(), firstNode.getSymbol())
                    || symbolMustBeAtHorizontal(currentSymbol.getSymbol(), firstNode.getSymbol())
//                    || StructuralRelation.horizontalOverlappingGraterThan(
//                    currentSymbol.getSymbol(), firstNode.getSymbol(), StructuralRelation.minIntersectionRatio)
                    ){
                
                overlappingResult = StructuralRelation.checkOverlap(firstNode, remainingSymbols);
                if(!RelationClassifier.isAmbiguousRelation(currentSymbol.getSymbol(), overlappingResult.getSymbol())){
//                    addAmbiguousRelation(overlappingResult);         
                    newNode = new SymbolNode(overlappingResult);
                    return horizontal(ListUtil.concat(startList, 
                        newNode), 
                        remainingSymbols);
                } 
//                else {
//                    newPrevious = new SymbolNode(currentSymbol);
//                    newNode = new SymbolNode(overlappingResult);
//                    List<SymbolNode> newstartList = new ArrayList<>(startList.subList(0, startList.size() - 1));
//                    newstartList.add(newPrevious);
//                    horizontal(ListUtil.concat(newstartList, 
//                        newNode), nodes);
//                }                                   
            }
            if(RelationClassifier.isAmbiguousRelation(currentSymbol.getSymbol(), firstNode.getSymbol())){
                newPrevious = copySymbolNodeStructure(currentSymbol);
                newNode = copySymbolNodeStructure(overlappingResult);
                List<SymbolNode> newstartList = new ArrayList<>();//ArrayList<>(startList.subList(0, startList.size() - 1));
                
                for (SymbolNode symbolNode : startList.subList(0, startList.size() - 1)) {
                    tempSymbolNode = copySymbolNodeStructure(symbolNode);
                    newstartList.add(tempSymbolNode);
                }
                newstartList.add(newPrevious);
                horizontal(ListUtil.concat(newstartList, 
                        newNode), remainingSymbols);
//                    newNode), nodes);
            }
//                addAmbiguousRelation(firstNode);    
            SL.remove(0);
        }
        newNode = new SymbolNode(currentSymbol);
        partitionFinal(remainingSymbols, newNode);
        horizontalResults.add(ListUtil.concat(startList.subList(0, startList.size() - 1), newNode));
        return horizontalResults.get(horizontalResults.size() - 1);
//        return ListUtil.concat(startList.subList(0, startList.size() - 1), currentSymbol);
    }
    
    public SymbolNode copySymbolNodeStructure(SymbolNode aSymbolNode) { 
        SymbolNode newSymbolNode = new SymbolNode(aSymbolNode.getSymbol());
        for (RegionNode regionNode : aSymbolNode.getChildren()) {
            newSymbolNode.addNodesToRegion(regionNode.getChildren(), regionNode.getLabel());
        }
        return newSymbolNode;
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

    private List<List<SymbolNode>> generateMultipleBaselines(List<SymbolNode> baselineSymbols) {
        List<List<SymbolNode>> baselines = new ArrayList<>();
        List<SymbolNode> newBaseline;
        List<List<NodeStructuralChange>> combinations = ListUtil.generateCombinations(ambiguiusNodeRelations);
        for (List<NodeStructuralChange> aCombination : combinations) {
            newBaseline = applyCombination(baselineSymbols, aCombination);
            baselines.add(newBaseline);
        }
        return baselines;
    }

    private List<SymbolNode> applyCombination(List<SymbolNode> baselineSymbols, List<NodeStructuralChange> aCombination) {
        List<SymbolNode> newBaseline;
        newBaseline = applyHorizontalToSuperOrSubScriptChanges(baselineSymbols, aCombination);
//        newBaseline = applySuperOrSubScriptToHorizontalChanges(baselineSymbols, aCombination);
        return newBaseline;
    }

    private List<SymbolNode> applyHorizontalToSuperOrSubScriptChanges(List<SymbolNode> baselineSymbols, List<NodeStructuralChange> aCombination) {
        List<SymbolNode> newBaseline = new ArrayList<>();//copyBaseline(baselineSymbols);
        SymbolNode newSymbolNode;
        for (int i = 0; i < baselineSymbols.size(); i++) {
            for (NodeStructuralChange nodeStructuralChange : aCombination) {
                if(nodeStructuralChange.getSymbolNode() == baselineSymbols.get(i) && 
                        ((nodeStructuralChange.getType() == NodeStructuralChange.CHANGE_TYPE_TO_SUBSCRIPT) || 
                        (nodeStructuralChange.getType() == NodeStructuralChange.CHANGE_TYPE_TO_SUPERSCRIPT))){
                    newSymbolNode = new SymbolNode(baselineSymbols.get(i));                    
                    putInSuperOrSubScriptRegionOfPrevoiusSymbol(newBaseline.get(newBaseline.size() - 1), newSymbolNode, 
                            nodeStructuralChange.getType());
                } else {
                    newSymbolNode = new SymbolNode(baselineSymbols.get(i));
                    newBaseline.add(baselineSymbols.get(i));
                }
            }
        }
        return newBaseline;
    }

    private void applySuperOrSubScriptToHorizontalChanges(List<SymbolNode> baselineSymbols, List<NodeStructuralChange> aCombination) {
//        List<SymbolNode> newBaseline = new ArrayList<>();//copyBaseline(baselineSymbols);
        SymbolNode newSymbolNode;
        for (int i = 0; i < baselineSymbols.size(); i++) {
            for (NodeStructuralChange nodeStructuralChange : aCombination) {
                if(nodeStructuralChange.getSymbolNode() == baselineSymbols.get(i) && 
                        nodeStructuralChange.getType() == NodeStructuralChange.CHANGE_TYPE_TO_HORIZONTAL
                        ){
                    baselineSymbols.get(i - 1).getChildren().remove(nodeStructuralChange.getSymbolNode());
                    baselineSymbols.add(i, baselineSymbols.get(i));
                } 
            }
        }
//        return newBaseline;
    }

    private List<SymbolNode> copyBaseline(List<SymbolNode> baselineSymbols) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void putInSuperOrSubScriptRegionOfPrevoiusSymbol(SymbolNode previousSymbolNode, SymbolNode aSymbolNode, int position) {
        if(position == NodeStructuralChange.CHANGE_TYPE_TO_SUBSCRIPT){
            previousSymbolNode.addNodeToRegion(aSymbolNode, RegionLabel.SUBSCRIPT);
        }
        else if(position == NodeStructuralChange.CHANGE_TYPE_TO_SUPERSCRIPT){
            previousSymbolNode.addNodeToRegion(aSymbolNode, RegionLabel.SUPERSCRIPT);
        }
    }

}
