/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.graphics;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.KruskalMST;
import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class GraphicalStrokeKruskalMST{
    private ArrayList<DStroke> strokes;
    private DMathExpression mathExpression;
    private KruskalMST mst;
    public static final int DRAW_TYPE_STROKES = 1;
    public static final int DRAW_TYPE_EXPRESSION = 2;
    private int drawType;
    private boolean drawGraph;

    public GraphicalStrokeKruskalMST(){
        strokes = new ArrayList<DStroke>();
        mst = null;
        mathExpression = new GMathExpression();
        drawType = DRAW_TYPE_STROKES;
        drawGraph = false;
    }

    public void draw(Graphics g){
        if(getDrawType() == DRAW_TYPE_STROKES)
            drawStrokes(g);
        else if(getDrawType() == DRAW_TYPE_EXPRESSION)
            drawMathExpression(g);
        if(drawGraph)
            drawGraph(g);
    }
    
    public void drawMathExpression(Graphics g){
        ((GMathExpression)mathExpression).draw(g);
    }

    public void drawGraph(Graphics g){
        if(mst!= null){
//            mst.edges();
            Color currentColor = g.getColor();
            Color newColor = Color.GRAY;
            g.setColor(newColor);
            Graphics2D g2D = (Graphics2D) g;
            Stroke str= g2D.getStroke();
            
            //To make lines thicker
            g2D.setStroke(new BasicStroke(2.5f));
            
            // TO SET CONTROL RENDERING QUALITY
//            RenderingHints rh = new RenderingHints(
//             RenderingHints.KEY_STROKE_CONTROL,
//             RenderingHints.VALUE_STROKE_NORMALIZE);
//            g2D.setRenderingHints(rh);
            
            for (Edge e : mst.edges()) {
//                g.drawLine(
//                        (int) strokes.get(e.either()).getBBox().getCenterX(),
//                        (int) strokes.get(e.either()).getBBox().getCenterY(),
//                        (int) strokes.get(e.other(e.either())).getBBox().getCenterX(),
//                        (int) strokes.get(e.other(e.either())).getBBox().getCenterY());
//                g.fillOval((int) (strokes.get(e.either()).getBBox().getCenterX() -3),
//                        (int) (strokes.get(e.either()).getBBox().getCenterY() -3), 6, 6);
//                g.fillOval((int) (strokes.get(e.other(e.either())).getBBox().getCenterX() -3),
//                        (int) (strokes.get(e.other(e.either())).getBBox().getCenterY() -3), 6, 6);

                g.drawLine(
                        (int) strokes.get(e.either()).getNearestPointToBoundingBoxCenter().getX(),
                        (int) strokes.get(e.either()).getNearestPointToBoundingBoxCenter().getY(),
                        (int) strokes.get(e.other(e.either())).getNearestPointToBoundingBoxCenter().getX(),
                        (int) strokes.get(e.other(e.either())).getNearestPointToBoundingBoxCenter().getY());
                g.fillOval((int) (strokes.get(e.either()).getNearestPointToBoundingBoxCenter().getX() -3),
                        (int) (strokes.get(e.either()).getNearestPointToBoundingBoxCenter().getY() -3), 6, 6);
                g.fillOval((int) (strokes.get(e.other(e.either())).getNearestPointToBoundingBoxCenter().getX() -3),
                        (int) (strokes.get(e.other(e.either())).getNearestPointToBoundingBoxCenter().getY() -3), 6, 6);

            }
            g2D.setStroke(str);
            g.setColor(currentColor);
        }
    }

    public void drawStrokes(Graphics g){
        int index = 0;
        for (DStroke dStroke : strokes) {
            ((GStroke) dStroke).drawStroke(g);
            //to DRAW ORDER OF STROKES
            g.drawString(String.valueOf(index++), (int) ((GStroke) dStroke).getLtPoint().getX(), 
                    (int) (((GStroke) dStroke).getLtPoint().getY()-6));
        }
    }

    public KruskalMST getMst() {
        return mst;
    }

    public void setMst(KruskalMST mst) {
        this.mst = mst;
    }

    public ArrayList<DStroke> getStrokes() {
        return strokes;
    }

    public void setStrokes(ArrayList<DStroke> strokes) {
        this.strokes = strokes;
    }

    public DMathExpression getMathExpression() {
        return mathExpression;
    }

    public void setMathExpression(DMathExpression mathExpression) {
        this.mathExpression = mathExpression;
    }

    public int getDrawType() {
        return drawType;
    }

    public void setDrawType(int drawType) {
        this.drawType = drawType;
    }

    public boolean isDrawGraph() {
        return drawGraph;
    }

    public void setDrawGraph(boolean drawGraph) {
        this.drawGraph = drawGraph;
    }


}
