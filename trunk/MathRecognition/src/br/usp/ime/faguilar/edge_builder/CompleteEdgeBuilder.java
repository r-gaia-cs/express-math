/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.edge_builder;

import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;

/**
 *
 * @author Willian
 */
public class CompleteEdgeBuilder implements EdgeBuilder {

    @Override
    public void build(Graph graph) {
        int totv = graph.getVertexSize();
        Vertex[] refv = graph.getIndexedVertexes();
        for (int i = 0; i < totv - 1; i++) {
            for (int j = i + 1; j < totv; j++) {
                Vertex v1 = refv[i];
                Vertex v2 = refv[j];
                graph.addEdge(v1, v2);
                graph.addEdge(v2, v1);
            }
        }
    }
}
