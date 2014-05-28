/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.parser.conversors;

import br.usp.ime.faguilar.parser.Baseline;
import br.usp.ime.faguilar.parser.MultipleBaselineRegionNode;
import br.usp.ime.faguilar.parser.Node;
import br.usp.ime.faguilar.parser.RegionLabel;
import br.usp.ime.faguilar.parser.RegionNode;
import br.usp.ime.faguilar.parser.SymbolNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Frank
 */
public class MultipleBSTToLatex {
    private List<String> latexStrings;
    private MultipleBaselineRegionNode treeRoot;
    private StringNode rootStringNode;
    
    public List<String> convert(RegionNode root){
        treeRoot = (MultipleBaselineRegionNode) root;
        BSTtoStringsTree();
        stringsTreeToLatexStrings();
        return latexStrings;
    }

    private void BSTtoStringsTree() {
//        String currentString = "";
//        List<Baseline> baselines = treeRoot.getBaselines();        
//        baselinePile.addAll(baselines);
        rootStringNode =  new StringNode();
        StringNode currentStringNode = new StringNode();
        StringNode auxStringNode;
//        currentStringNode.setCurrentString("");
        rootStringNode.addToNext(currentStringNode);
        LinkedList<Node> nodesPile = new LinkedList<>();
        LinkedList<StringNode> previousStringPile = new LinkedList<>();
        LinkedList<StringNode> lastStringNodes = new LinkedList<>();
        nodesPile.add(treeRoot);
        Node bstNode;
        MultipleBaselineRegionNode multipleBSRegionNode;
//        List<Node> childs = new ArrayList<>();
        int indexNode;
        String latex;
        
        while (!nodesPile.isEmpty()) {
            bstNode = nodesPile.removeFirst();
//            childs.clear();
            if(bstNode instanceof MultipleBaselineRegionNode){
                multipleBSRegionNode = (MultipleBaselineRegionNode) bstNode;
                if(!multipleBSRegionNode.isAddedChilds()){
//                    if(!multipleBSRegionNode.getBaselines().isEmpty())
                    currentStringNode.addToString(bstNode.latexStringWithoutChilds());
                    if(multipleBSRegionNode.getBaselines().size() > 1){
                        previousStringPile.addFirst(currentStringNode);
                    }
                    nodesPile.addFirst(bstNode);
                    for (Baseline aBaseline : multipleBSRegionNode.getBaselines()) {
                        indexNode = aBaseline.getNodes().size() - 1;
                        while (indexNode >= 0) {
                            nodesPile.addFirst(aBaseline.getNodes().get(indexNode));
                            indexNode--;
                        }                        
                        nodesPile.addFirst(bstNode);
                    }
                    multipleBSRegionNode.setAddedChilds(true);
                } else{               
//                    if(multipleBSRegionNode.getBaselineToParse() < multipleBSRegionNode.getBaselines().size()){
                        if(multipleBSRegionNode.getBaselineToParse() >= 1 && 
                                multipleBSRegionNode.getLabel() != RegionLabel.EXPRESSION){
                            currentStringNode.addToString("} ");
                        }
                        if(multipleBSRegionNode.getBaselines().size() > 1) {
                            if(multipleBSRegionNode.getBaselineToParse() > 0) {
                                multipleBSRegionNode.addNodesToLink(currentStringNode);
                            }
                            if(multipleBSRegionNode.getBaselineToParse() < multipleBSRegionNode.getBaselines().size()){
//                                auxStringNode = new StringNode();
                                currentStringNode = new StringNode();
                                previousStringPile.getFirst().addToNext(currentStringNode);
//                                currentStringNode = auxStringNode;
                            } else {
                                if(multipleBSRegionNode.getLabel() != RegionLabel.EXPRESSION){
                                    currentStringNode = new StringNode();
                                    for (StringNode stringNodeToLink : multipleBSRegionNode.getNodesToLink()) {
                                        stringNodeToLink.addToNext(currentStringNode);
                                    }
                                    //CHECKAR
                                } 
//                                else {
//                                    currentStringNode = new StringNode();
//                                    previousStringPile.getFirst().addToNext(currentStringNode);
//                                }
                                previousStringPile.removeFirst();//CHECKAR
                            }                            
                        }                        
                        if(multipleBSRegionNode.getBaselineToParse() >= multipleBSRegionNode.getBaselines().size()){
                            multipleBSRegionNode.setBaselineToParse(0); // TO RESET INDEX
                            multipleBSRegionNode.setAddedChilds(false);
                        } else {
                            multipleBSRegionNode.setBaselineToParse(multipleBSRegionNode.getBaselineToParse() + 1);
                        }
                }
                
            } else if (bstNode instanceof SymbolNode){
                latex = bstNode.latexStringWithoutChilds();
                currentStringNode.addToString(latex);
                if(latex.contains("\\frac")) {
                    RegionNode numerator, denominator;
                    if(((SymbolNode) bstNode).getChildren().get(0).getLabel() 
                            == RegionLabel.ABOVE){
                        numerator = (RegionNode) bstNode.getChildren().get(0);
                        denominator = (RegionNode) bstNode.getChildren().get(1);
                    } else {
                        numerator = (RegionNode) bstNode.getChildren().get(1);
                        denominator = (RegionNode) bstNode.getChildren().get(0);
                    }
                    nodesPile.addFirst(denominator);
                    nodesPile.addFirst(numerator);
                }
                else if (!bstNode.getChildren().isEmpty())
                    for (Object object : bstNode.getChildren()) 
                        nodesPile.addFirst((Node) object);
            }            
        }
    }

    private void stringsTreeToLatexStrings() {
        latexStrings = new ArrayList<>();
        LinkedList<StringNode> stringNodesPile = new LinkedList<>();
        stringNodesPile.addFirst(rootStringNode);
        StringNode currentNode;
        String currentString = "";
        while(!stringNodesPile.isEmpty()){
            currentNode = stringNodesPile.removeFirst();
            if (currentNode.getNext().isEmpty()){
                latexStrings.add(currentNode.getAccumulatedString() + 
                        currentNode.getCurrentString());
            } else {
                for (StringNode stringNode : currentNode.getNext()) {
                    stringNode.setAccumulatedString(currentNode.getAccumulatedString() + 
                            currentNode.getCurrentString());
                    stringNodesPile.addFirst(stringNode);
                }
            }
//            if(!currentNode.isVisited()){
//                currentString += currentNode.getCurrentString();
//                if(currentNode.getNext().size() > 1){
//    //                CHECK HERE
//                    currentNode.setAccumulatedString(new String(currentString));
//                }
//                for (StringNode stringNode : currentNode.getNext()) {
//                    if(currentNode.getNext().size() > 1){
//                        stringNodesPile.addFirst(currentNode);
//                    }
//                    stringNodesPile.addFirst(stringNode);
//                }
//                currentNode.setVisited(true);
//            } 
//            else {
//                if (currentNode.getNext().size() > 1) {
//                    currentString = new String(currentNode.getAccumulatedString()); //currentNode.getAccumulatedString() + currentString;
//                }
//            }
//            if(currentNode.getNext().isEmpty()){
//                latexStrings.add(currentString);
//                currentString = new String();
//            }            
        }
//        resetAllVisitedNodes();
    }

    private void resetAllVisitedNodes() {
        LinkedList<StringNode> stringNodesPile = new LinkedList<>();
        stringNodesPile.addFirst(rootStringNode);
        StringNode currentNode;
        String currentString = "";
        while(!stringNodesPile.isEmpty()){
            currentNode = stringNodesPile.removeFirst();

            for (StringNode stringNode : currentNode.getNext()) {
//                if(currentNode.getNext().size() > 1){
//                    stringNodesPile.addFirst(currentNode);
//                }
                stringNodesPile.addFirst(stringNode);
            }
            currentNode.setVisited(false);
            currentNode.setAccumulatedString("");
            currentNode.setCurrentString("");
        }
    }
    
    public class StringNode{
        List<StringNode> next;
        String currentString;
        String accumulatedString;
        boolean visited;

        public StringNode(){
            visited = false;
            currentString = "";
            next = new ArrayList<>();
            accumulatedString = "";
        }
        
        public String getAccumulatedString() {
            return accumulatedString;
        }

        public void setAccumulatedString(String accumulatedString) {
            this.accumulatedString = accumulatedString;
        }

//        static StringNode createFromString(String aString){
//            StringNode node = new StringNode();
//            node.setCurrentString(aString);
//            return node;
//        }

        public List<StringNode> getNext() {
            return next;
        }

        public void setNext(List<StringNode> next) {
            this.next = next;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }
        
        
        
        public void addToString(String aString){
            currentString += aString;
        }

        public boolean addToNext(StringNode e) {
            return next.add(e);
        }

        public String getCurrentString() {
            return currentString;
        }

        public void setCurrentString(String currentString) {
            this.currentString = currentString;
        }       
    }
}
