/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.data;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Willian Honda
 */
public class DMathExpression extends MathExpressionData<DSymbol>
                            implements Serializable{

    static final long serialVersionUID = 1931329247720475181L;

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
//
//    private void calculateShapeContextExpression(Graph g) {
//        float diagonal = (float)Math.sqrt(Math.pow(g.getHeight(), 2)
//                    + Math.pow(g.getWidth(), 2));
//        ShapeContext sc = new ShapeContext(diagonal, g, MatchingParameters.LogPolarGlobalRegions,
//                MatchingParameters.angularGlobalRegions, true, (GMathExpression) this);
//    }
//
//    private double[][] calculateShapeContextInside(Point2D[] points) {
//        Graph g = new Graph();
//        ShapeContext sc;
//        for (int i = 0; i < points.length; i++) {
//            g.addVertex(i, (float)points[i].getX(), (float)points[i].getY());
//        }
//            float diagonal = (float)Math.sqrt(Math.pow(g.getHeight(), 2)
//                    + Math.pow(g.getWidth(), 2));
//            sc = new ShapeContext(diagonal, g, MatchingParameters.LogPolarLocalRegions,
//                    MatchingParameters.angularLocalRegions, false,null);
//        return sc.getSC();
//    }

    @Override
    public boolean addCheckingBoundingBox(DSymbol e) {
        Point2D p1=e.getLtPoint();
        Point2D p2=e.getRbPoint();
        if(this.isEmpty()){
            ltPoint.setLocation(p1);
            rbPoint.setLocation(p2);
        }
        else{
            if(p1.getX()<ltPoint.getX())
                ltPoint.setLocation(p1.getX(),ltPoint.getY());
            if(p1.getY()<ltPoint.getY())
                ltPoint.setLocation(ltPoint.getX(),p1.getY());

            if(p2.getX()>rbPoint.getX())
                rbPoint.setLocation(p2.getX(),rbPoint.getY());
            if(p2.getY()>rbPoint.getY())
                rbPoint.setLocation(rbPoint.getX(),p2.getY());
        }
        return this.add(e);
    }
    
    public static ArrayList extractStrokes(DMathExpression expression){
    ArrayList strokes = new ArrayList();
    for (DSymbol dSymbol : expression) {
        for (DStroke dStroke : dSymbol) {
                strokes.add(dStroke);
            }
        }
    return strokes;
    }
}
