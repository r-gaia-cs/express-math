/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.mrf;

/**
 *
 * @author Frank
 */
public class HummelRelaxationLabeling {
    
    private double[][] labeling;
    private double[][] gradient;
    private double[][] direction;
    
    
    public void calculateLabeling(){
        initializeLabeling();
        iterateUpdatingLabeling();
    }

    private void initializeLabeling() {
        double sum;
        for (double[] labelingOfANode : labeling) {
            sum = 0.;
            for (int j = 0; j < labelingOfANode.length; j++) {
                labelingOfANode[j] = 1 + 0.001 * Math.random();
                sum += labelingOfANode[j];
            }   
            for (int i = 0; i < labelingOfANode.length; i++) 
                labelingOfANode[i] = labelingOfANode[i] / sum;
        }
    }

    private void iterateUpdatingLabeling() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
