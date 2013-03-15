package br.usp.ime.faguilar.matching;

import br.usp.ime.faguilar.cost.CostDeformation;
import br.usp.ime.faguilar.edge_builder.CompleteEdgeBuilder;
import br.usp.ime.faguilar.edge_builder.DelaunayEdgeBuilder;
import br.usp.ime.faguilar.edge_builder.EdgeBuilder;
import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.hungarian_algorithm.AssignmentProblem;
import br.usp.ime.faguilar.hungarian_algorithm.HungarianAlgorithm;

public class HungarianMatching extends GraphMatching {

    EdgeBuilder edgeBuilder;
    private float[][] myCalculatedCostMatrix;
    


    public HungarianMatching(){
        super();
    }

    public HungarianMatching(Graph model, Graph input) {
        super(model, input);
    }

    public HungarianMatching(Graph model, Graph input, float alpha, float beta, float gama, boolean delaunay) {
        this(model, input);
        this.edgeBuilder = new CompleteEdgeBuilder();
        if(delaunay){
            this.edgeBuilder = new DelaunayEdgeBuilder();
        }
        this.edgeBuilder.build(model);
        this.cost = new CostDeformation(model, input, alpha, beta, gama);
    }

    public int[][] getMatch() {
        AssignmentProblem ap = new AssignmentProblem(this.getCostMatrix());
//        myCalculatedCostMatrix = ap.copyOfMatrix();
        return ap.solve(new HungarianAlgorithm());
        /*int result[] = new int[match.length];
        for (int i = 0; i < result.length; i++) {
            result[match[i][1]] = match[i][0];
        }
        return result;*/

        /*Vertex[] modelVertex = model.getIndexedVertexes();
        Vertex[] inputVertex = input.getIndexedVertexes();

        medianX = (int) this.median(inputVertex, modelVertex, match, true);
        medianY = (int) this.median(inputVertex, modelVertex, match, false);*/
    }

    public float[][] getMyCalculatedCostMatrix() {
        return myCalculatedCostMatrix;
    }

    public void setMyCalculatedCostMatrix(float[][] myCostMatrix) {
        this.myCalculatedCostMatrix = myCostMatrix;
    }
    /*
    private float median(Vertex[] inputVertex, Vertex[] modelVertex, int[][] match, boolean isXDistance) {
        float mediana[] = new float[modelVertex.length];
        if (inputVertex.length < modelVertex.length) {
            mediana = new float[inputVertex.length];
        }

        int i = 0;
        for (int j = 0; j < match.length; j++) {
            if (match[j][0] < modelVertex.length && match[j][1] < inputVertex.length) {
                if (isXDistance) {
                    mediana[i] = (float) this.distanceX(inputVertex[match[j][1]], modelVertex[match[j][0]]);
                } else {
                    mediana[i] = (float) this.distanceY(inputVertex[match[j][1]], modelVertex[match[j][0]]);
                }
                i++;
            }
        }
        for (int k = 0; k < mediana.length; k++) {
            for (int j = k + 1; j < mediana.length; j++) {
                if (mediana[k] > mediana[j]) {
                    float temp = mediana[k];
                    mediana[k] = mediana[j];
                    mediana[j] = temp;
                }
            }
        }
        if (mediana.length % 2 == 0) {
            return (mediana[(mediana.length - 1) / 2] + mediana[((mediana.length - 1) / 2) + 1]) / 2;
        }
        return mediana[mediana.length / 2];
    }

    private double distanceX(Vertex input, Vertex model) {
        return input.getX() - model.getX();
    }

    private double distanceY(Vertex input, Vertex model) {
        return input.getY() - model.getY();
    }*/


}
