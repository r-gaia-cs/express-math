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
public class Compatibility {
    private ArrayList<Double> unaryCompatibility;
    ArrayList<ArrayList<Double>> compatibility;
    ArrayList<NodeLabeling> neighbors;
    
    public double getCompatibility(int i, int j){
        return compatibility.get(i).get(j);
    }
    
    public double getLabelingScoreOfNeighborAt(int posNeighbor, 
            int posLabelOfNeighbor){
        return neighbors.get(posNeighbor).getLAbelingScoreAt(posLabelOfNeighbor);
    }
    
    public int getNumberOfNeighbors(){
        return neighbors.size();
    }
    
    public int getNumberOfLabelsOfNeighbor(int posNeighbor){
        return neighbors.get(posNeighbor).getNumberOfLabels();
    }
    
    public double getUnaryCompatibilityAt(int position){
        return unaryCompatibility.get(position);
    }
}
