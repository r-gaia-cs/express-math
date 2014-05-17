/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import br.usp.ime.faguilar.data.DSymbol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Frank Aguilar
 */
public class PartitionTree {
    private Node root;
    
    public ArrayList<Partition> getPartitionsInIncreasingCost(){
        ArrayList<Partition> partitions = extractPartitions();
        Collections.sort(partitions);
        return partitions;
    }
    
    public ArrayList<ArrayList<DSymbol>> getPartitionsAsListOfsymbolsInIncreasingCost(){
        ArrayList<Partition> partitions = getPartitionsInIncreasingCost();
        ArrayList<ArrayList<DSymbol>> symbolsAllPartitions = new ArrayList<>();
        ArrayList<DSymbol> symbolsOfAPartition;
        for (Partition partition : partitions) {
            symbolsOfAPartition = new ArrayList<>();
            symbolsOfAPartition.addAll(partition.getSymbols());
            symbolsAllPartitions.add(symbolsOfAPartition);
        }
        return symbolsAllPartitions;
    }
    
    public ArrayList<Partition> extractPartitions(){
        ArrayList<Partition> partitions = new ArrayList<>();
        ArrayList<Node> leaves = getLeaves();
        Partition newPartition;
        Node nodeAux;
        for (Node node : leaves) {
            newPartition = new Partition();
            nodeAux = node;
            while(!nodeAux.isRoot()){
                newPartition.addSymbolHypothesis(nodeAux.getHypothesis());
                nodeAux = nodeAux.getFather();
            }
            newPartition.updateCost();
            partitions.add(newPartition);
        }
        
//        int max = Math.min(MAX_NUMBER_OF_PARTITIONS, partitions.size());
//        ArrayList<Partition> subListOfPartitions = new ArrayList<>();
//        subListOfPartitions.addAll(partitions.subList(0, max));
//        return subListOfPartitions;
        return partitions;
    }
    
    public ArrayList<Node> getLeaves(){
        ArrayList<Node> leaves = new ArrayList<>();
        LinkedList<Node> pile = new LinkedList<>();
        pile.addFirst(root);
        Node nodeAux;
        while (!pile.isEmpty()) {            
            nodeAux = pile.removeFirst();
            while(!nodeAux.getChilds().isEmpty()){
                for (int i = 0; i < nodeAux.getChilds().size(); i++) {
                    if(nodeAux.getChilds().get(i) != null)
                        pile.addFirst(nodeAux.getChilds().get(i));
                }
                nodeAux = pile.removeFirst();
//                nodeAux = nodeAux.getChilds().get(0);
            }
            if(nodeAux.isLeafWithCompleteStrokes())
                leaves.add(nodeAux);
        }
        return leaves;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }
    
    
}
