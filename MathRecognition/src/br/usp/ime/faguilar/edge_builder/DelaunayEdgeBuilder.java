/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.edge_builder;

import br.usp.ime.faguilar.graph.Graph;

/**
 *
 * @author Willian
 */
public class DelaunayEdgeBuilder implements EdgeBuilder {
	@Override
	public void build(Graph graph) {
		DelaunayAlgorithm d = new DelaunayAlgorithm(graph.getVertexSize());
		d.triangularizaG(graph);
	}
    
}