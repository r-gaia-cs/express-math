/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frank
 */
public abstract class Node<T extends Node> 
//implements Cloneable
{
    private T parent;
    private List<T> children;    
    
    public Node(){
        parent = null;
        children = new ArrayList<T>();
    }
    
    public Node(Node<T> otherNode){
        children = new ArrayList<T>();
        addChildren(otherNode.getChildren());
        setParent(otherNode.getParent());
    }

    public String treeString(){
        String string = "";
        string += (stringData() + "(");
        for (T t : children) {
            string += (" " + t.treeString());
        }
        string += ") ";
        return string;
    }
    
    public abstract String latexString();
    
    public String LabelGraphString(){
//        String string = "";
//        if(this instanceof SymbolNode)
//            string += symbolData((SymbolNode) this);
//        else if(this instanceof RegionNode){
//            if(getParent() != null){
//                string += edgeFromMyParentToMyFirstChild();
//            }
//            String crohmeRelation = RegionLabel.crohme2013Abreviation(RegionLabel.RIGHT);
//            for (int i = 0; i < getChildren().size() - 1; i++) {
//                for (int j = i + 1; j < getChildren().size(); j++) {
//                    string += edgeFromTo(((SymbolNode) getChildren().get(i)).getSymbol(), 
//                            ((SymbolNode) getChildren().get(j)).getSymbol(), crohmeRelation);
//                }             
//            }
//        }
//        for (T t : children) {
//                string += t.LabelGraphString();
//        }
//        return string;
        List<NodeWithRegion> newParents = new ArrayList<NodeWithRegion>();
        List<NodeWithRegion> leftParents = new ArrayList<NodeWithRegion>();
        return LabelGraphString(newParents, leftParents);
    }
    
    
    public String LabelGraphString(List<NodeWithRegion> parents, List<NodeWithRegion> leftParents){
        String string = "";
        if(this instanceof SymbolNode){
            for (T t : children)
                string += t.LabelGraphString(parents, leftParents);
            string += symbolData((SymbolNode) this);
            for (NodeWithRegion nodeWithRegion : leftParents) {
                string += edgeFromTo(((SymbolNode) nodeWithRegion.getNode()).getSymbol(), 
                        ((SymbolNode) this).getSymbol(), nodeWithRegion.getRelation());
            }
        }else if(this instanceof RegionNode){
            List<NodeWithRegion> newParents = new ArrayList<NodeWithRegion>(parents);
            List<NodeWithRegion> newLeftParents = new ArrayList<NodeWithRegion>(leftParents);
            String relation = RegionLabel.crohme2013Abreviation(((RegionNode) this).getLabel());
            if(!relation.isEmpty() || ((RegionNode) this).getLabel() == RegionLabel.EXPRESSION){
                if(getParent() != null){
                    NodeWithRegion newParent = new NodeWithRegion();
                    newParent.setNode(this.getParent());
                    if(!relation.isEmpty()){
                        newParent.setRelation(relation);
                        newParents.add(newParent);
                    }
                }
                for (int i = 0; i < children.size(); i++) {
                    T t = children.get(i);
                    if(i > 0){
                        NodeWithRegion newParent = new NodeWithRegion();
                        newParent.setNode(children.get(i - 1));
                        newParent.setRelation(RegionLabel.crohme2013Abreviation(RegionLabel.RIGHT));
                        newLeftParents.add(newParent);
                    }
                    string += t.LabelGraphString(newParents, newLeftParents);
                }
                string += edgeFromMyParentsToMyChildren(newParents);
            }
        }
            
        
//        String crohmeRelation = RegionLabel.crohme2013Abreviation(RegionLabel.RIGHT);
//            for (int i = 0; i < getChildren().size() - 1; i++) {
//                for (int j = i + 1; j < getChildren().size(); j++) {
//                    string += edgeFromTo(((SymbolNode) getChildren().get(i)).getSymbol(), 
//                            ((SymbolNode) getChildren().get(j)).getSymbol(), crohmeRelation);
//                }             
//            }
        
        return string;
    }
    
    public String edgeFromMyParentsToMyChildren(List<NodeWithRegion> parents){
        String edges = "";
        for (NodeWithRegion nodeWithRegion : parents) {
            String relationCrohme = nodeWithRegion.getRelation();
            SymbolNode myParent = (SymbolNode) nodeWithRegion.getNode();
            DSymbol parentSymbol = myParent.getSymbol();
            for (T aChild : getChildren()) {
                SymbolNode myChild = (SymbolNode) aChild;
                DSymbol childSymbol = myChild.getSymbol();
                edges += edgeFromTo(parentSymbol, childSymbol, relationCrohme);
            }  
        }
        return edges;
    }
    
    public String edgeFromMyParentToMyFirstChild(){
        String edge = "";
        String relationCrohme = RegionLabel.crohme2013Abreviation(((RegionNode) this).getLabel());
        SymbolNode myParent = (SymbolNode) getParent();
        SymbolNode myChilden = (SymbolNode) getChildren().get(0);
        DSymbol parentSymbol = myParent.getSymbol();
        DSymbol childSymbol = myChilden.getSymbol();
        edge = edgeFromTo(parentSymbol, childSymbol, relationCrohme);
        return edge;
    }
    
    public String edgeFromTo(DSymbol from, DSymbol to, String relation){
        String edge = "";
        for (DStroke parentStroke : from) {
            for (DStroke childStroke : to) {
//                edge += String.format("E, s%d, s%d, %s\n", ((OrderedStroke) parentStroke).getIndex(), //+ 1, 
//                        ((OrderedStroke) childStroke).getIndex()// + 1
//                        , relation);
                edge += String.format("E, %d, %d, %s, 1.0\n", ((OrderedStroke) parentStroke).getIndex(), //+ 1, 
                        ((OrderedStroke) childStroke).getIndex()// + 1
                        , relation);
            }
        }
        return edge;
    }
    
    public String symbolData(SymbolNode symbolNode){
        String data = "";
        DSymbol symbol = symbolNode.getSymbol();
        int pos;
        for (DStroke dStroke : symbol) {
            pos = ((OrderedStroke) dStroke).getIndex();// + 1;
//            data += String.format("N, s%d, %s\n", pos, symbol.getLabel());
            data += String.format("N, %d, %s, 1.0\n", pos, symbol.getLabel());
        }
        if(symbol.size() > 1){
            int pos1, pos2;
            for (DStroke firstStroke : symbol) {
                for (DStroke seconStroke : symbol) {
                    if(firstStroke != seconStroke){
                        pos1 = ((OrderedStroke) firstStroke).getIndex(); //+ 1;
                        pos2 = ((OrderedStroke) seconStroke).getIndex();// + 1;
//                        data += String.format("E, s%d, s%d, *\n", pos1, pos2);
                        data += String.format("E, %d, %d, *, 1.000\n", pos1, pos2);
                    }
                }
            }
        }
        return data;
    }
    
//    protected T clone(){
//        return 
//    }
    
    public abstract String stringData();
    
    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
//        parent.addChild(this);
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = new ArrayList<T>();
        addChildren(children);
    }
    
    public void addChild(T newChild){
        newChild.setParent(this);
        getChildren().add(newChild);
    }
    
    public void addChildren(List<T> newChildren){
        for (T node : newChildren) {
            addChild(node);
        }
    }
    
    public void clearChildren(){
        getChildren().clear();
    }
    
}
