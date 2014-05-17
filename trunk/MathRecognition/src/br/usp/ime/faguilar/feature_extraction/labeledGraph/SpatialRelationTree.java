/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction.labeledGraph;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank
 */
public class SpatialRelationTree {
    private List<SpatialRelationNode> nodes;
    private List<SpatialRelation> relations;
    
    
    public SpatialRelationTree(){
        nodes = new ArrayList<>();
        relations = new ArrayList<>();
    }
    
    public void extractTreeFromLabeledGraph(LabeledGraph labeledGraph) {
        extractNodes(labeledGraph);
        extractRelations(labeledGraph);
    }

    public List<SpatialRelationNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<SpatialRelationNode> nodes) {
        this.nodes = nodes;
    }

    public List<SpatialRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<SpatialRelation> relations) {
        this.relations = relations;
    }
    
    private void extractNodes(LabeledGraph labeledGraph) {
        extractNodesFromRelations(labeledGraph);
        labelNodesFromRelations(labeledGraph);
        extractNodesFromIndividualStrokes(labeledGraph);
    }

    private void extractRelations(LabeledGraph labeledGraph) {
        List<LabeledGraphRelation> relationsOfLabeledGraph = labeledGraph.getRelations();
        for (LabeledGraphRelation labeledGraphRelation : relationsOfLabeledGraph) {
            if(!labeledGraphRelation.isSameSymbolRelation())
                if(!relationIsInTree(labeledGraphRelation))
                    addRelationBetweenNodes(labeledGraphRelation);
        }
    }

    private void extractNodesFromRelations(LabeledGraph labeledGraph) {
        List<LabeledGraphRelation> relationsOfLabeledGraph = labeledGraph.getRelations();
        for (LabeledGraphRelation labeledGraphRelation : relationsOfLabeledGraph) {
            if(labeledGraphRelation.isSameSymbolRelation())
                addSameSymbolRelation(labeledGraphRelation);
        }
    }
    
    private void labelNodesFromRelations(LabeledGraph labeledGraph) {
        List<LabeledGraphNode> nodesOfLabeledGraph = labeledGraph.getNodes();
        for (LabeledGraphNode labeledGraphNode : nodesOfLabeledGraph) {
            for (SpatialRelationNode spatialRelationNode : nodes) {
                if(spatialRelationNode.containsStrokeID(labeledGraphNode.getId()))
                    spatialRelationNode.setLabel(labeledGraphNode.getLabel());
            }
        }
    }

    private void addSameSymbolRelation(LabeledGraphRelation labeledGraphRelation) {
        boolean added = false;
        for (SpatialRelationNode spatialRelationNode : nodes) {
            if(spatialRelationNode.relationHasStrokeOfThisNode(labeledGraphRelation)){
                spatialRelationNode.addStrokesOFRelation(labeledGraphRelation);
                added = true;
                break;
            }
        }
        if(!added){
            SpatialRelationNode newNode = new SpatialRelationNode();
            newNode.addStrokesOFRelation(labeledGraphRelation);
            nodes.add(newNode);
        }
    }

    private void addRelationBetweenNodes(LabeledGraphRelation labeledGraphRelation) {
        SpatialRelationNode[] nodesOFRelation = getNodesOFRelation(labeledGraphRelation);
        SpatialRelation newRelation = new SpatialRelation();
        newRelation.setNode1(nodesOFRelation[0]);
        newRelation.setNode2(nodesOFRelation[1]);
        newRelation.setRelationLabel(labeledGraphRelation.getLabel());
        relations.add(newRelation);
    }

    private boolean relationIsInTree(LabeledGraphRelation labeledGraphRelation) {
        SpatialRelationNode[] nodesOFRelation = getNodesOFRelation(labeledGraphRelation);
        SpatialRelationNode node1 = nodesOFRelation[0];
        SpatialRelationNode node2 = nodesOFRelation[1];
        for (SpatialRelation spatialRelation : relations) {
            if((spatialRelation.getNode1() == node1 && spatialRelation.getNode2() == node2)
                    || (spatialRelation.getNode2() == node1 && spatialRelation.getNode1() == node2))
                return true;
        }
        return false;
    }
    
    public SpatialRelationNode[] getNodesOFRelation(LabeledGraphRelation labeledGraphRelation){
        SpatialRelationNode node1 = null;
        SpatialRelationNode node2 = null;
        for (SpatialRelationNode spatialRelationNode : nodes) {
            if(spatialRelationNode.containsStrokeID(labeledGraphRelation.getStroke1ID()))
                node1 = spatialRelationNode;
            if(spatialRelationNode.containsStrokeID(labeledGraphRelation.getStroke2ID()))
                node2 = spatialRelationNode;
        }
        SpatialRelationNode[] nodesOfRelation = new SpatialRelationNode[2];
        nodesOfRelation[0] = node1;
        nodesOfRelation[1] = node2;
        return nodesOfRelation;
    }

    private void extractNodesFromIndividualStrokes(LabeledGraph labeledGraph) {
        List<LabeledGraphNode> nodesOfLabeledGraph = labeledGraph.getNodes();
        SpatialRelationNode newNode;
        for (LabeledGraphNode labeledGraphNode : nodesOfLabeledGraph) {
            if(!strokeIsInAnyNode(labeledGraphNode.getId())){
                newNode = new SpatialRelationNode();
                newNode.setLabel(labeledGraphNode.getLabel());
                newNode.getStrokeIds().add(labeledGraphNode.getId());
                nodes.add(newNode);
            }
        }
    }
    
    public boolean strokeIsInAnyNode(int idStroke){
        for (SpatialRelationNode spatialRelationNode : nodes) {
            if(spatialRelationNode.containsStrokeID(idStroke))
                return true;
        }
        return false;
    }
    
}
