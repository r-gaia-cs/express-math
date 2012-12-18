/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author frank
 */
public class SegmentationTree {
    private SegmentationNode root;
    private SegmentationNode currentNode;

    public SegmentationTree(){
        setRoot(new SegmentationNode());
        setCurrentNode(root);
    }

    public void addChildToCurrentNode(SegmentationNode node) {
//        if(getRoot().equals(getCurrentNode()))
//            root = node;
//        else
            getCurrentNode().addChild(node);
//        setCurrentNode(node);
    }
    
    
    public int getPreviousSymbolCount(int numberOfSteps){
        SegmentationNode nodeAux = getPreviousElement(numberOfSteps);
        return nodeAux.getSymbolCount();
    }
    
    public double getPreviousLeastCost(int numberOfSteps){
        SegmentationNode nodeAux = getPreviousElement(numberOfSteps);
        return nodeAux.getBestCost();
    }
    
    public SegmentationNode getPreviousElement(int numberOfSteps){
        SegmentationNode nodeAux = currentNode;
        for (int i = 0; i < numberOfSteps; i++) {
            nodeAux = nodeAux.getFather();
        }
        return nodeAux;
    }

    private void setRoot(SegmentationNode newRoot){
        this.root = newRoot;
    }

    public SegmentationNode getRoot() {
        return root;
    }
    
    public SegmentationNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(SegmentationNode currentNode) {
        this.currentNode = currentNode;
    }
    
    
    public ArrayList<SegmentationNode> getLeaves(){
        ArrayList<SegmentationNode> leaves = new ArrayList<SegmentationNode>();
        LinkedList<SegmentationNode> pile = new LinkedList<SegmentationNode>();
        pile.addFirst(root);
        SegmentationNode nodeAux = null;
        while (!pile.isEmpty()) {            
            nodeAux = pile.removeFirst();
//            if(!nodeAux.getChilds().isEmpty()){
                
            while(!nodeAux.getChilds().isEmpty()){
                for (int i = 1; i < nodeAux.getChilds().size(); i++) 
                    pile.addFirst(nodeAux.getChilds().get(i));
                nodeAux = nodeAux.getChild(0);
            }
            leaves.add(nodeAux);
//            }
        }
        return leaves;
    }
    
    

}
