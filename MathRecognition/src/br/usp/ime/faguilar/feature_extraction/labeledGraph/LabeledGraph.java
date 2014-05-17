/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction.labeledGraph;

import br.usp.ime.faguilar.util.FilesUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Frank
 */
public class LabeledGraph {
    private List<LabeledGraphNode> nodes;
    private List<LabeledGraphRelation> relations;
    
    
    
    public static LabeledGraph createGraphFromFile(String filePath){
        LabeledGraph labeledgraph = new LabeledGraph();
        BufferedReader reader = FilesUtil.getBufferedReader(filePath);
        String line;
        try {
            while (reader.ready()){
                line = reader.readLine();
                if(isNodeOrRelation(line)) {
                    addToGraph(labeledgraph, line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(LabeledGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return labeledgraph;
    }

    private static boolean isNodeOrRelation(String line) {
        return !line.isEmpty() && !line.startsWith("#");
    }

    private static void addToGraph(LabeledGraph labeledgraph, String line) {
        if(line.startsWith("N"))
            addNode(labeledgraph, line);
        if(line.startsWith("E"))
            addRelation(labeledgraph, line);
    }

    private static void addNode(LabeledGraph labeledgraph, String line) {
        String[] parts = line.split(",");
        LabeledGraphNode node = new LabeledGraphNode();
        node.setId(Integer.valueOf(parts[1]));
        node.setLabel(parts[2]);
        labeledgraph.addNode(node);
    }    

    private static void addRelation(LabeledGraph labeledgraph, String line) {
        String[] parts = line.split(",");
        LabeledGraphRelation relation = new LabeledGraphRelation();
        relation.setStroke1ID(Integer.valueOf(parts[1]));
        relation.setStroke2ID(Integer.valueOf(parts[2]));
        relation.setLabel(parts[3]);
        labeledgraph.addRelation(relation);
    }

    public List<LabeledGraphNode> getNodes() {
        if(nodes == null)
            nodes = new ArrayList<>();
        return nodes;
    }

    public void setNodes(List<LabeledGraphNode> nodes) {
        this.nodes = nodes;
    }

    public List<LabeledGraphRelation> getRelations() {
        if(relations == null)
            relations = new ArrayList<>();
        return relations;
    }

    public void setRelations(List<LabeledGraphRelation> relations) {
        this.relations = relations;
    }
    
    public void addNode(LabeledGraphNode node) {
        getNodes().add(node);
    }
    
    public void addRelation(LabeledGraphRelation relation) {
        getRelations().add(relation);
    }
    
}
