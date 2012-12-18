/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.conversion;

import br.usp.ime.faguilar.data.DStroke;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class MathExpressionGraph {
    public static EdgeWeightedGraph StrokeSetToEdgeWeightedGraph
            (ArrayList<DStroke> strokes){
        EdgeWeightedGraph graph = new EdgeWeightedGraph(strokes.size());
        int i, j;
        double weight = 0;
        Rectangle2D boundingBox_i, boundingBox_j;
        for (i = 0; i < strokes.size() -1; i++)
            for(j = i+1; j < strokes.size(); j++){
//                TO CALCULATE WEIGHT AS DISTANCE BETWEEN
//                STROKES' BOUNDING BOX CENTERS
//                boundingBox_i = strokes.get(i).getBBox();
//                boundingBox_j = strokes.get(j).getBBox();
//                weight = Point2D.distance(boundingBox_i.getCenterX(), boundingBox_i.getCenterY(),
//                        boundingBox_j.getCenterX(), boundingBox_j.getCenterY());
//                graph.addEdge(new Edge(i, j, weight));
                
//                TO CALCULATE WEIGHT AS MINIMUM DISTANCE BETWEEN STROKES
//                weight = strokes.get(i).minDistance(strokes.get(j));
//                graph.addEdge(new Edge(i, j, weight));
//                END TO CALCULATE WEIGHT AS MINIMUM DISTANCE BETWEEN STROKES

//                TO CALCULATE DISTANCE BETWEEN POINT NEAREST TO BOUNDING BOX OF STROKES

                weight = strokes.get(i).getNearestPointToBoundingBoxCenter().distance(
                        strokes.get(j).getNearestPointToBoundingBoxCenter());
                graph.addEdge(new Edge(i, j, weight));
            }
        return graph;
    }
}
