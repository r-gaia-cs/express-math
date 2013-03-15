/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.graph.Graph;
import java.awt.geom.Point2D;

/**
 *
 * @author frank
 */
public class GraphDeformationCalculator {
//    private double cost;
    private Graph template;
    private Graph input;
    private HungarianMatching gm;
    private float alpha;
    private float beta;
    private float gama;
    private boolean delaunay;

    public static GraphDeformationCalculator newInstanceFromTemplateAndIputGraphs(
            Graph template, Graph input){
        GraphDeformationCalculator calculator = new GraphDeformationCalculator();
        calculator.setTemplate(template);
        calculator.setInput(input);
        calculator.setDefaultParameters();
        return calculator;

    }

    public int[][] getMatching(){
        PreProcessing pp = new PreProcessing(template, input);
        this.gm = new HungarianMatching(pp.getModel(), pp.getInput(), alpha, beta, gama, delaunay);
        return this.gm.getMatch();
    }

    public double getCost(){
        int[][] matching = this.getMatching();
        float[][] costMatrix = gm.getMyCalculatedCostMatrix();
        double cost = 0;
        for (int i = 0; i < matching.length; i++) {
            cost += costMatrix[matching[i][0]][matching[i][1]];
        }
        return cost;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public boolean isDelaunay() {
        return delaunay;
    }

    public void setDelaunay(boolean delaunay) {
        this.delaunay = delaunay;
    }

    public float getGama() {
        return gama;
    }

    public void setGama(float gama) {
        this.gama = gama;
    }

    public HungarianMatching getGm() {
        return gm;
    }

    public void setGm(HungarianMatching gm) {
        this.gm = gm;
    }

    public Graph getInput() {
        return input;
    }

    public void setInput(Graph input) {
        this.input = input;
    }

    public Graph getTemplate() {
        return template;
    }

    public void setTemplate(Graph template) {
        this.template = template;
    }

    private void setDefaultParameters() {
        alpha = 0.5f;
        beta = 1.0f;
        gama = 0.25f;
        delaunay = false;
    }

}
