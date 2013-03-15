/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.cost;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.feature_extraction.ShapeContextFeature;
import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;
import br.usp.ime.faguilar.matching.GraphMatching;
import br.usp.ime.faguilar.matching.HungarianMatching;
import br.usp.ime.faguilar.matching.Matching;
import br.usp.ime.faguilar.matching.MatchingParameters;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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

    public static Matching getCost(ShapeContextFeature templateFeatures, ShapeContextFeature inputFeatures) {
        float[] anglesTemplate = templateFeatures.getAngles();
        float[] anglesInput = inputFeatures.getAngles();      
        return getCost(templateFeatures.getShapeContext().getSC(), anglesTemplate,
                inputFeatures.getShapeContext().getSC(), anglesInput);
    }
    
    public static Matching getCost(DSymbol templateSymbol, ShapeContextFeature templateFeatures, 
        DSymbol inputSymbol, ShapeContextFeature inputFeatures) {
        Point2D[] modelNormalizedPoints = ShapeContextFeature.normalizeCoords(templateSymbol, templateFeatures.getCoords());
        Point2D[] inputNormalizedPoints = ShapeContextFeature.normalizeCoords(inputSymbol, inputFeatures.getCoords());      
        return getCost(templateFeatures.getShapeContext().getSC(), modelNormalizedPoints,
                inputFeatures.getShapeContext().getSC(), inputNormalizedPoints);
    }

    public static Matching getCost(double[][] scModel,float[] anglesModel,
            double[][] scInput, float[] anglesInput){
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
        ((HungarianMatching)gm).setAnglesInput(anglesInput);
        ((HungarianMatching)gm).setAnglesModel(anglesModel);
        gm.setCost(new Cost());
        int[][] match = gm.getMatch();

        float totalCost = 0;
        Vertex[] vertexModel = graphModelSC.getIndexedVertexes();
        Vertex[] vertexInput = graphInputSC.getIndexedVertexes();

        float additionalDifference = 0;
        for (int i = 0; i < match.length; i++) {
            additionalDifference = (float)((Math.abs(anglesModel[match[i][0]] - 
                    anglesInput[match[i][1]])) / (2 * Math.PI));
            totalCost += (1 - GraphMatching.ANGLE_WEIGHT) * vertexModel[match[i][0]].
                    compareShapeContextExpression(vertexInput[match[i][1]]) + 
                    GraphMatching.ANGLE_WEIGHT * additionalDifference;
        }
        Matching matching = new Matching();
        matching.setCost(totalCost / match.length);
        matching.setMatching(match);
        return matching;
    }
    
    public static Matching getCost(double[][] scModel,Point2D[] modelPoints,
            double[][] scInput, Point2D[] inputPoints){
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
        ((HungarianMatching)gm).setModelPoints(modelPoints);
        ((HungarianMatching)gm).setInputPoints(inputPoints);
        gm.setCost(new Cost());
        int[][] match = gm.getMatch();

        float totalCost = 0;
        Vertex[] vertexModel = graphModelSC.getIndexedVertexes();
        Vertex[] vertexInput = graphInputSC.getIndexedVertexes();

        float additionalDifference = 0;
        double denominator = Math.sqrt(2);
        for (int i = 0; i < match.length; i++) {
            additionalDifference = (float)((modelPoints[match[i][0]].distance(inputPoints[match[i][1]]))/ denominator);
            totalCost += (1 - GraphMatching.ANGLE_WEIGHT) * vertexModel[match[i][0]].
                    compareShapeContextExpression(vertexInput[match[i][1]]) + 
                    GraphMatching.ANGLE_WEIGHT * additionalDifference;
        }
        Matching matching = new Matching();
        matching.setCost(totalCost / match.length);
        matching.setMatching(match);
        return matching;
    }

    public static double[][] calculateShapeContextMatrixFromPoints2D(Point2D[] points) {
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

    public static ShapeContext calculateShapeContextFromPoints2D(Point2D[] points) {
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
        return sc;
    }

    public static double[][] calculateShapeContextFromPoints2D(ArrayList<Point2D> points) {
        Point2D[] arrayOfPoints = new Point2D[points.size()];
        for (int i = 0; i < points.size(); i++)
            arrayOfPoints[i] = points.get(i);
        return calculateShapeContextMatrixFromPoints2D(arrayOfPoints);
    }
}
