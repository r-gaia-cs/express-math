/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 *
 * @author Frank Aguilar
 */
public class PartitionTreeGenerator {
    
    private HypothesisGenerator hypothesisGenerator;
    ArrayList<OrderedStroke> orderedStrokes;
    PartitionTree partitionTree;
    private static final int MAX_NUMBER_OF_PARTITIONS = 200000;
    private int numberOfPartitions;
    
    public void generateTree(List<OrderedStroke> strokes){
        hypothesisGenerator.generateHypothesis((ArrayList<OrderedStroke>) strokes);
        if(!strokes.isEmpty()){
            orderedStrokes = new ArrayList<>();
            this.orderedStrokes.addAll(strokes);
            Collections.sort(this.orderedStrokes);
            partitionTree = new PartitionTree();
            partitionTree.setRoot(new Node());
//            generateTree(partitionTree.getRoot(), 
//                    this.orderedStrokes.get(0), strokes.size());
            
//            generateTreeWithIndex(partitionTree.getRoot(), this.orderedStrokes.get(0), 
//                    0, strokes.size());
            numberOfPartitions = 0;
            generateTreeWithIndexWithIteration(partitionTree.getRoot(), this.orderedStrokes);
        }
    }
    
    public void generateTreeWithIndexWithIteration(Node currentNode, ArrayList<OrderedStroke> strokes){
        int currentIndex = 0;
        OrderedStroke currentStroke;
        LinkedList<Node> pileCandidates = new  LinkedList<>();
        Node removedNode;
        List<Node> newCandidates;
        currentNode.setStrokeIndex(currentIndex);
        pileCandidates.addFirst(currentNode);
        while (!pileCandidates.isEmpty() && !enoughPartitionsFound()){
//            lastElement = pileCandidates.size() - 1;
            removedNode = pileCandidates.removeFirst();//pileCandidates.remove(lastElement);
            if(removedNode.getStrokeIndex() < strokes.size()){
                currentStroke = strokes.get(removedNode.getStrokeIndex());
                newCandidates = candidates(removedNode, currentStroke);
                for (int i = newCandidates.size() - 1; i >= 0; i--) {
                    removedNode.addChild(newCandidates.get(i));
                    newCandidates.get(i).setFather(removedNode);
                    newCandidates.get(i).setStrokeIndex(removedNode.getStrokeIndex() + 1);
                    pileCandidates.addFirst(newCandidates.get(i));
                }
//                for (Node node : newCandidates) {
//                    removedNode.addChild(node);
//                    node.setFather(removedNode);
//                    node.setStrokeIndex(removedNode.getStrokeIndex() + 1);
//                    pileCandidates.addFirst(node);
//                }
                if (newCandidates.isEmpty()){
                    removedNode.setStrokeIndex(removedNode.getStrokeIndex() + 1);
                    pileCandidates.addFirst(removedNode);
                }
            } else {
                if (removedNode.getSelected().size() >= orderedStrokes.size()){
                    numberOfPartitions ++;
                    removedNode.setLeafWithCompleteStrokes(true);
                } else
                    removedNode = null;
            }
        }
    }
    
    public boolean enoughPartitionsFound(){
        boolean enough = false;
        if (numberOfPartitions >= MAX_NUMBER_OF_PARTITIONS)
            enough = true;
        return enough;
    }
    
    public void generateTreeWithIndex(Node currentNode, OrderedStroke stroke, 
            int order, int numberOFStrokes){
        if(order < numberOFStrokes) {
            List<Node> candidates = candidates(currentNode, stroke);
            for (Node node : candidates) {
                currentNode.addChild(node);
                node.setFather(currentNode);
                if(order + 1 < numberOFStrokes)
                    generateTreeWithIndex(node, orderedStrokes.get(order + 1), 
                            order + 1, numberOFStrokes);
            }
            if(candidates.isEmpty()){
                if(order + 1 < orderedStrokes.size())
                    generateTreeWithIndex(currentNode, orderedStrokes.get(order + 1),
                            order + 1, numberOFStrokes);
            }
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
        return currentNode.getSelected().size() >= numberOFStrokes;
    }
    
    private List<Node> candidates(Node currentNode, OrderedStroke currentStroke){
        List<Node> nodes = new ArrayList<>();
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
    
    public ArrayList<ArrayList<DSymbol>> getPartitionsAsListOFSymbolsInIncreasingCost(){
        return partitionTree.getPartitionsAsListOfsymbolsInIncreasingCost();
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
