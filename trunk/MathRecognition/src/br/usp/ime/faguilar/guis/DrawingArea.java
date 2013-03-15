/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DrawingArea.java
 *
 * Created on Oct 23, 2012, 10:08:06 PM
 */

package br.usp.ime.faguilar.guis;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.graphics.GraphicalStrokeKruskalMST;
import java.awt.Graphics;

/**
 *
 * @author frank
 */
public class DrawingArea extends javax.swing.JPanel {
    private GraphicalStrokeKruskalMST drawingObject;
    
    /** Creates new form DrawingArea */
    public DrawingArea() {
        initComponents();
        drawingObject = new GraphicalStrokeKruskalMST();
//        mathExpression = null;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        drawingObject.draw(g);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1363, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 620, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    public GraphicalStrokeKruskalMST getDrawingObject() {
        return drawingObject;
    }

    public void setDrawingObject(GraphicalStrokeKruskalMST drawingObject) {
        this.drawingObject = drawingObject;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
