/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching;

import java.util.Arrays;

/**
 *
 * @author frank
 */
public class Matching {
    private int[][] matching;
    private double[] matchingCosts;
    double cost;

    public double[] getSortedCosts(){
        double [] sortedCosts = Arrays.copyOf(matchingCosts, matchingCosts.length);
        Arrays.sort(sortedCosts);
        return sortedCosts;
    }
    
    public double[] getMatchingCosts() {
        return matchingCosts;
    }

    public void setMatchingCosts(double[] matchingCosts) {
        this.matchingCosts = matchingCosts;
    }
    
    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int[][] getMatching() {
        return matching;
    }

    public void setMatching(int[][] matching) {
        this.matching = matching;
    }

}
