package br.usp.ime.faguilar.matching;

import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.cost.Cost;
import br.usp.ime.faguilar.graph.Vertex;
import java.awt.geom.Point2D;

public abstract class GraphMatching {

    protected Graph input;
    protected Graph model;
    protected Cost cost;

    private float[] anglesModel;
    private float[] anglesInput;   
    private Point2D[]modelPoints;
    private Point2D[] inputPoints;
    

    public static float ANGLE_WEIGHT = (float) 0.6;

    public GraphMatching() {
        this(null, null);
    }

    public GraphMatching(Graph model, Graph input) {
        this.model = model;
        this.input = input;
    }

    public abstract int[][] getMatch();

    protected float[][] getCostMatrix() {
        int tamanho = this.model.getVertexSize();
        if (tamanho < this.input.getVertexSize()) {
            tamanho = this.input.getVertexSize();
        }
        float[][] costMatrix = new float[tamanho][tamanho];
        Vertex[] modelVertex = model.getIndexedVertexes();
        Vertex[] inputVertex = input.getIndexedVertexes();
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                if (i < modelVertex.length && j < inputVertex.length) {
                    //not considering angles
//                    costMatrix[i][j] = this.cost.getCost(modelVertex[i], inputVertex[j]);
                    //to consider angles
//                    float anglesDiference = (float) (0.5 * (1 - Math.cos(getAnglesModel()[i] - getAnglesInput()[j])));
//                    float anglesDiference = (float)((Math.abs(getAnglesModel()[i] - getAnglesInput()[j]))/ (2 * Math.PI));
                    float anglesDiference = (float)((modelPoints[i].distance(inputPoints[j]))/ Math.sqrt(2));
//                    float anglesDiference = (float)((Math.abs(modelPoints[i].distance(inputPoints[j])))/ 2);
                    float shapeContextDistance = this.cost.getCost(modelVertex[i], inputVertex[j]);
                    costMatrix[i][j] = (1 - ANGLE_WEIGHT) *  shapeContextDistance +
                            ANGLE_WEIGHT * anglesDiference;
                } else {
                    costMatrix[i][j] = Float.MAX_VALUE;
                }
            }
        }
        return costMatrix;
    }

    public Point2D[] getModelPoints() {
        return modelPoints;
    }

    public void setModelPoints(Point2D[] modelPoints) {
        this.modelPoints = modelPoints;
    }

    public Point2D[] getInputPoints() {
        return inputPoints;
    }

    public void setInputPoints(Point2D[] inputPoints) {
        this.inputPoints = inputPoints;
    }

    
    public Graph getInput() {
        return input;
    }

    public void setInput(Graph input) {
        this.input = input;
    }

    public Graph getModel() {
        return model;
    }

    public void setModel(Graph model) {
        this.model = model;
    }

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }


    public float[] getAnglesInput() {
        return anglesInput;
    }

    public void setAnglesInput(float[] anglesInput) {
        this.anglesInput = anglesInput;
    }

    public float[] getAnglesModel() {
        return anglesModel;
    }

    public void setAnglesModel(float[] anglesModel) {
        this.anglesModel = anglesModel;
    }
}
