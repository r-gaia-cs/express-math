/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching;

/**
 *
 * @author frank
 */
public class Matching {
    private int[][] matching;
    double cost;

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
