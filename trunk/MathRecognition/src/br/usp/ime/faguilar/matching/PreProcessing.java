/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.matching;

import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;
import java.awt.geom.Point2D;

/**
 *
 * @author Willian
 */
public class PreProcessing {
    Graph model, input;
    
    PreProcessing(Graph model, Graph input) {
        this.model = model;
        this.input = input;
        this.equalizeScales();
        this.centralizeGraph();
        /*model.updateShapeContextExpression();
        input.updateShapeContextExpression();*/ 
    }

    public Graph getModel(){
        return this.model;
    }
    
    public Graph getInput(){
        return this.input;
    }
    
    private void equalizeScales() {
        double tamanhoFinalX = model.getWidth();
        double tamanhoFinalY = model.getHeight();

        double tamanhoX = input.getWidth();
        double tamanhoY = input.getHeight();
       
        Point2D start = input.getMinPositions();
        Vertex[] list = input.getIndexedVertexes();
        
        for (Vertex vertex : list) {
            double posicaoX = vertex.getX() - start.getX();
            double novaPosicaoX = posicaoX * tamanhoFinalX / tamanhoX;

            double posicaoY = vertex.getY() - start.getY();
            double novaPosicaoY = posicaoY * tamanhoFinalY / tamanhoY;

            vertex.setX(vertex.getX()+(novaPosicaoX - posicaoX));
            vertex.setY(vertex.getY()+(novaPosicaoY - posicaoY));
        }
    }

    private void centralizeGraph() {
        Vertex[] list = input.getIndexedVertexes();
       
        Point2D modelCentroid = model.getCentroid();//model.getMinPositions();//
        Point2D inputCentroid = input.getCentroid();//input.getMinPositions();
        
        for (Vertex vertex : list) {
            vertex.setX(vertex.getX()+(modelCentroid.getX() - inputCentroid.getX()));
            vertex.setY(vertex.getY()+(modelCentroid.getY() - inputCentroid.getY()));
        }
    
    }
}
