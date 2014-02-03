/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation.HypothesisTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

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
    
    public ArrayList<Partition> extractPartitions(){
        ArrayList<Partition> partitions = new ArrayList<Partition>();
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
        return partitions;
    }
    
    public ArrayList<Node> getLeaves(){
        ArrayList<Node> leaves = new ArrayList<Node>();
        LinkedList<Node> pile = new LinkedList<Node>();
        pile.addFirst(root);
        Node nodeAux;
        while (!pile.isEmpty()) {            
            nodeAux = pile.removeFirst();
            while(!nodeAux.getChilds().isEmpty()){
                for (int i = 1; i < nodeAux.getChilds().size(); i++) 
                    pile.addFirst(nodeAux.getChilds().get(i));
                nodeAux = nodeAux.getChilds().get(0);
            }
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
