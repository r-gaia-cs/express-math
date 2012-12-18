/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.cost;

import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;
import br.usp.ime.faguilar.matching.GraphMatching;
import br.usp.ime.faguilar.matching.HungarianMatching;
import br.usp.ime.faguilar.matching.MatchingParameters;
import java.awt.geom.Point2D;

/**
 *
 * @author Willian
 */
public class CostShapeContextInside extends Cost {
//    GraphMatching gm;

//    public float getCost(Vertex vm, Vertex vi){
//        Graph graphModelSC = new Graph();
//        Graph graphInputSC = new Graph();
//        double[][] scModel = vm.getShapeContextSymbol();
//        double[][] scInput = vi.getShapeContextSymbol();
//
//        for (int i = 0; i < scModel.length; i++) {
//            Vertex v = new Vertex(i, -1, -1);
//            v.setShapeContextExpression(scModel[i]);
//            graphModelSC.addVertex(v);
//        }
//        for (int i = 0; i < scInput.length; i++) {
//            Vertex v = new Vertex(i, -1, -1);
//            v.setShapeContextExpression(scInput[i]);
//            graphInputSC.addVertex(v);
//        }
//        this.gm = new HungarianMatching(graphModelSC, graphInputSC);
//        this.gm.setCost(new Cost());
//        int[][] match = this.gm.getMatch();
//
//        float totalCost = 0;
//        Vertex[] vertexModel = graphModelSC.getIndexedVertexes();
//        Vertex[] vertexInput = graphInputSC.getIndexedVertexes();
//
//        for (int i = 0; i < match.length; i++) {
//            totalCost += vertexModel[match[i][0]].compareShapeContextExpression(vertexInput[match[i][1]]);
//        }
//        return totalCost/match.length;
//    }

    public static float getCost(double[][] scModel,double[][] scInput){
        GraphMatching gm;
        Graph graphModelSC = new Graph();
        Graph graphInputSC = new Graph();
//        double[][] scModel = vm.getShapeContextSymbol();
//        double[][] scInput = vi.getShapeContextSymbol();
//
        for (int i = 0; i < scModel.length; i++) {
            Vertex v = new Vertex(i, -1, -1);
            v.setShapeContextExpression(scModel[i]);
            graphModelSC.addVertex(v);
        }
        for (int i = 0; i < scInput.length; i++) {
            Vertex v = new Vertex(i, -1, -1);
            v.setShapeContextExpression(scInput[i]);
            graphInputSC.addVertex(v);
        }
        gm = new HungarianMatching(graphModelSC, graphInputSC);
        gm.setCost(new Cost());
        int[][] match = gm.getMatch();

        float totalCost = 0;
        Vertex[] vertexModel = graphModelSC.getIndexedVertexes();
        Vertex[] vertexInput = graphInputSC.getIndexedVertexes();

        for (int i = 0; i < match.length; i++) {
            totalCost += vertexModel[match[i][0]].compareShapeContextExpression(vertexInput[match[i][1]]);
        }
        return totalCost/match.length;
    }

    public static double[][] calculateShapeContextFromPoints2D(Point2D[] points) {
        Graph g = new Graph();
        ShapeContext sc;
        double height, width;
        double maxX = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            g.addVertex(i, (float)points[i].getX(), (float)points[i].getY());
            if(points[i].getX() > maxX)
                maxX = points[i].getX();
            if(points[i].getX() < minX)
                minX = points[i].getX();
            if(points[i].getY() > maxY)
                maxY = points[i].getY();
            if(points[i].getY() < minY)
                minY = points[i].getY();

        }
        height = maxX - minX;
        width = maxY - minY;
//            float diagonal = (float) Math.sqrt(Math.pow(g.getHeight(), 2)
//                    + Math.pow(g.getWidth(), 2));
            float diagonal = (float) Math.sqrt(Math.pow(height, 2)
                    + Math.pow(width, 2));
            sc = new ShapeContext(diagonal, g, MatchingParameters.LogPolarLocalRegions,
                    MatchingParameters.angularLocalRegions, false,null);
        return sc.getSC();
    }
}
