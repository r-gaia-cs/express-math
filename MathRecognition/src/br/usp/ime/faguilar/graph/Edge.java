/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.graph;

/**
 *
 * @author Willian
 */
public class Edge {
    private Vertex from, to;

    public Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
    }
    
    public Vertex getFrom() {
        return this.from;
    }
    
    public Vertex getTo() {
        return this.to;
    }
}
