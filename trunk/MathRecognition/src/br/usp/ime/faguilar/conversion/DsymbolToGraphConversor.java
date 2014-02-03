/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.conversion;

import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;
import br.usp.ime.faguilar.graphics.GStroke;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class DsymbolToGraphConversor {
    public static Graph convert(DSymbol symbol){
        Graph graph = new Graph();
        int idCount = 0;
        Point2D centroid;

        for(int i = 0; i < symbol.size(); i++){//for (DSymbol symbol : this) {
            DStroke stroke = symbol.get(i);
            ((OrderedStroke)stroke).setIndex(i);
            centroid = stroke.getCentroid();
            Vertex v = graph.addVertex(i, (float) centroid.getX() , (float) centroid.getY());
            ArrayList<Point2D> points = PreprocessingAlgorithms.getNPoints(stroke, 
                    MatchingParameters.numberOfPointPerSymbol / symbol.size());
            v.setShapeContextSymbol(CostShapeContextInside.calculateShapeContextFromPoints2D(points));
            idCount++;
        }
        calculateShapeContextExpression(graph);
        return graph;
    }

    public static void calculateShapeContextExpression(Graph g) {
        double height = g.getHeight();
        double width = g.getWidth();
        float diagonal = (float)Math.sqrt(height * height
                    + width * width);
        ShapeContext sc = new ShapeContext(diagonal, g, MatchingParameters.LogPolarGlobalRegions,
                MatchingParameters.angularGlobalRegions, true, null);
    }
//    public Graph toGraph() {
//        Graph graph = new Graph();
//        int idCount = 0;
//        for(int i = 0; i < this.size(); i++){//for (DSymbol symbol : this) {
//            DSymbol symbol = this.get(i);
//            symbol.setId(i);
//            Vertex v = graph.addVertex(i, (float) symbol.getCentroid().getX(), (float) symbol.getCentroid().getY());
//            Point2D[] points = PreprocessingAlgorithms.getNPoints(symbol, MatchingParameters.numberOfPointPerSymbol);
//            v.setShapeContextSymbol(this.calculateShapeContextInside(points));
//            idCount++;
//        }
//        this.calculateShapeContextExpression(graph);
//        return graph;
//    }
}
