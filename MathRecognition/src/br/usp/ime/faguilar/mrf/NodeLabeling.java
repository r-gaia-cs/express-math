/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.mrf;

import java.util.ArrayList;

/**
 *
 * @author Frank Aguilar
 */
public class NodeLabeling {
    private double[] labelingScores;
    private double[] gradients;
    private double[] directions;
    
    private Compatibility binaryCompatibility;
    private ArrayList neighboors;
    private Object node;

    public double getLAbelingScoreAt(int position){
        return labelingScores[position];
    }

    public double getDirectionAt(int position){
        return directions[position];
    }

    public double getGradientAt(int position){
        return gradients[position];
    }

    public int getNumberOfLabels(){
        return labelingScores.length;
    }

    public void setLabelingScoreAt(int position, double score){
        labelingScores[position] = score;
    }

    public void setGradientAt(int position, double gradient){
        gradients[position] = gradient;
    }

    public void setDirectionAt(int position, double direction){
        directions[position] = direction;
    }

    public void updateGradients() {
        double sumBinaryCosts;
        for (int i = 0; i < gradients.length; i++) {
            sumBinaryCosts = 0;
            for (int j = 0; j < binaryCompatibility.getNumberOfNeighbors(); j++) {
                for (int k = 0; k < binaryCompatibility.getNumberOfLabelsOfNeighbor(j); k++) {
                    sumBinaryCosts += binaryCompatibility.getCompatibility(j, k) *
                            binaryCompatibility.getLabelingScoreOfNeighborAt(j, k);
                }                
            }
            gradients[i] = binaryCompatibility.getUnaryCompatibilityAt(i) +
                    2 * sumBinaryCosts;
        }
    }
}
