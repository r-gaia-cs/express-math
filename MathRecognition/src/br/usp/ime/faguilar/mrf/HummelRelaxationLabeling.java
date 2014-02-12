/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.mrf;

import java.util.ArrayList;

/**
 *
 * @author Frank
 */
public class HummelRelaxationLabeling {
    
//    private double[][] labeling;
//    private double[][] gradient;
//    private double[][] direction;
    private ArrayList<NodeLabeling> nodesLabeling;
    private double factor;
    private double maxFactor;
    private static final double decreaseOFFactor = 0.99;
    private static final double const1 = 1;
    private static final double const2 = 1;
    private CompatibilityFunction compatibilityFunction;
    
    public HummelRelaxationLabeling(){
        maxFactor = 1;
        factor = maxFactor;
    }
    
    public void calculateLabeling(){
        initializeLabeling();
        iterateUpdatingLabeling();
    }

    private void initializeLabeling() {
        double sum;
        for (NodeLabeling labelingOfANode : nodesLabeling) {
            sum = 0.;
            for (int j = 0; j < labelingOfANode.getNumberOfLabels(); j++) {
                labelingOfANode.setLabelingScoreAt(j, 1 + 0.001 * Math.random());
                sum += labelingOfANode.getLAbelingScoreAt(j);
            }   
            for (int i = 0; i < labelingOfANode.getNumberOfLabels(); i++) 
                labelingOfANode.setLabelingScoreAt(i, labelingOfANode.getLAbelingScoreAt(i) / sum);
        }
    }

    private void iterateUpdatingLabeling() {
        do{
            calculateGradient();
            calculatePojectionOperator();
            if(converged())
                break;
            updateLabeling();
        }while(true);
    }

    private void calculateGradient() {
        for (NodeLabeling nodeLabeling : nodesLabeling) {
            nodeLabeling.updateGradients();
        }
    }

    private void calculatePojectionOperator() {
        ArrayList<Integer> D;
        ArrayList<Integer> S;
        int ns;
        double t;
        for (NodeLabeling nodeLabeling : nodesLabeling) {
            D = selectLabelingScoreZeroPositions(nodeLabeling);
            S = new ArrayList<>();
            do{
               ns = S.size();
               t = sumOfGradientsNotInS(nodeLabeling, S) / (nodeLabeling.getNumberOfLabels() - ns);
               S = gradientsLessThanT(D, nodeLabeling, t);
            }while(ns != S.size());
            setUpDirections(nodeLabeling, S, t);
        }
        normalizeDirections();
    }

    private boolean converged() {
        double sum = 0;
        for (NodeLabeling nodeLabeling : nodesLabeling) {
            for (int i = 0; i < nodeLabeling.getNumberOfLabels(); i++) {
                sum += nodeLabeling.getLAbelingScoreAt(i) * 
                        nodeLabeling.getLAbelingScoreAt(i);
            }
        }
        sum = sum / nodesLabeling.size();
        return sum > 0.99;
    }

    private void updateLabeling() {
        maxFactor = maxFactor * decreaseOFFactor;
        factor = maxFactor;
        ArrayList<ArrayList<Double>> newScores;
        while(!goodFactor(factor)){
            factor = factor * decreaseOFFactor;
        }
        updateScores(factor);
    }

    private double sumOfGradientsNotInS(NodeLabeling nodeLabeling, ArrayList<Integer> S) {
        double sum = 0;
        for (int i = 0; i < nodeLabeling.getNumberOfLabels(); i++) {
            if(!S.contains(i))            
                sum += nodeLabeling.getGradientAt(i);
        }
        return sum;
    }

    private ArrayList<Integer> gradientsLessThanT(ArrayList<Integer> D,
            NodeLabeling nodeLabeling, double t) {
        ArrayList<Integer> selectedGradients = new ArrayList<>();
        for (int i = 0; i < D.size(); i++) 
            if(nodeLabeling.getGradientAt(D.get(i)) < t)
                selectedGradients.add(D.get(i));            
        return selectedGradients;
    }

    private ArrayList<Integer> selectLabelingScoreZeroPositions(NodeLabeling nodeLabeling) {
        ArrayList<Integer> zeros = new ArrayList<>();
        for (int i = 0; i < nodeLabeling.getNumberOfLabels(); i++) {
            if(nodeLabeling.getLAbelingScoreAt(i) == 0)
                zeros.add(i);
        }
        return zeros;
    }

    private void setUpDirections(NodeLabeling nodeLabeling, ArrayList<Integer> S, 
            double t) {
        for (int i = 0; i < nodeLabeling.getNumberOfLabels(); i++) {
            if(S.contains(i))            
                nodeLabeling.setDirectionAt(i, 0);
            else
                nodeLabeling.setDirectionAt(i, nodeLabeling.getGradientAt(i) - t);
        }
    }

    private void normalizeDirections() {
        double norm = 0;
        for (NodeLabeling nodeLabeling : nodesLabeling) 
            for (int i = 0; i < nodeLabeling.getNumberOfLabels(); i++) 
                norm += nodeLabeling.getDirectionAt(i) * nodeLabeling.getDirectionAt(i);  
        
        norm = Math.sqrt(norm);
        if(norm != 0)
            for (NodeLabeling nodeLabeling : nodesLabeling)
                for (int i = 0; i < nodeLabeling.getNumberOfLabels(); i++)
                    nodeLabeling.setDirectionAt(i, nodeLabeling.getDirectionAt(i)
                    / norm);
    }

    

    private boolean goodFactor(double factor) {
        double newScore;
        for (NodeLabeling nodeLabeling : nodesLabeling) {
            for (int i = 0; i < nodeLabeling.getNumberOfLabels(); i++) {
                newScore = nodeLabeling.getLAbelingScoreAt(i) + 
                        nodeLabeling.getDirectionAt(i) * factor;
                if(newScore < 0 || newScore > 1)
                    return false;
            }
        }
        return true;
    }

    private void updateScores(double factor) {
        for (NodeLabeling nodeLabeling : nodesLabeling)
            for (int i = 0; i < nodeLabeling.getNumberOfLabels(); i++)
                nodeLabeling.setLabelingScoreAt(i, nodeLabeling.getLAbelingScoreAt(i) + 
                        nodeLabeling.getDirectionAt(i) * factor);
    }
}
