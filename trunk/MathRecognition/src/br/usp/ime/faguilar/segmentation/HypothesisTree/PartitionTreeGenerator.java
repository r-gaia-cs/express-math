/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 * @author Frank Aguilar
 */
public class PartitionTreeGenerator {
    
    private HypothesisGenerator hypothesisGenerator;
    ArrayList<OrderedStroke> orderedStrokes;
    PartitionTree partitionTree;
    
    public void generateTree(List<OrderedStroke> strokes){
        hypothesisGenerator.generateHypothesis((ArrayList<OrderedStroke>) strokes);
        if(!strokes.isEmpty()){
            orderedStrokes = new ArrayList<OrderedStroke>();
            this.orderedStrokes.addAll(strokes);
            Collections.sort(this.orderedStrokes);
            partitionTree = new PartitionTree();
            partitionTree.setRoot(new Node());
            generateTree(partitionTree.getRoot(), 
                    this.orderedStrokes.get(0), strokes.size());
        }
    }
    
    public void generateTree(Node currentNode, OrderedStroke stroke, int numberOFStrokes){
        if(!isASolution(currentNode, numberOFStrokes)) {
            List<Node> candidates = candidates(currentNode, stroke);
            for (Node node : candidates) {
                currentNode.addChild(node);
                node.setFather(currentNode);
                if(stroke.getIndex() + 1 < orderedStrokes.size())
                    generateTree(node, orderedStrokes.get(stroke.getIndex() + 1), 
                        numberOFStrokes);
            }
            if(candidates.isEmpty()){
                if(stroke.getIndex() + 1 < orderedStrokes.size())
                    generateTree(currentNode, orderedStrokes.get(stroke.getIndex() + 1), 
                        numberOFStrokes);
            }
        }
    }
    
    private boolean isASolution(Node currentNode, int numberOFStrokes){
        return currentNode.getSelected().size() == numberOFStrokes;
    }
    
    private List<Node> candidates(Node currentNode, OrderedStroke currentStroke){
        List<Node> nodes = new ArrayList<Node>();
        List<SymbolHypothesis> hypothesis = hypothesisGenerator.getHypothesis(currentStroke);
        Node newNode;
        for (SymbolHypothesis symbolHypothesis : hypothesis) {
            if(!currentNode.intersectingStrokes(symbolHypothesis)){
                newNode = new Node();
                newNode.setHypothesis(symbolHypothesis);
                nodes.add(newNode);
            }
        }
        return nodes;
    }
    
    public ArrayList<SymbolHypothesis> getHypothesis(){
        return hypothesisGenerator.getAllHypothesis();
    }

    public ArrayList<Partition> getPartitionsInIncreasingCost(){
        return partitionTree.getPartitionsInIncreasingCost();
    }
    
    public HypothesisGenerator getHypothesisGenerator() {
        return hypothesisGenerator;
    }

    public void setHypothesisGenerator(HypothesisGenerator hypothesisGenerator) {
        this.hypothesisGenerator = hypothesisGenerator;
    }

    public ArrayList<OrderedStroke> getOrderedStrokes() {
        return orderedStrokes;
    }

    public void setOrderedStrokes(ArrayList<OrderedStroke> orderedStrokes) {
        this.orderedStrokes = orderedStrokes;
    }

    public Node getRoot() {
        return partitionTree.getRoot();
    }

    public void setRoot(Node root) {
        partitionTree.setRoot(root);
    }
}
