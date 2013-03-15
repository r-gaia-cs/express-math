/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching;

import java.io.Serializable;

/**
 *
 * @author frank
 */
public class IncorrectMatching implements Serializable{
    int symbolAtModel;
    int symbolAtInstance;
    int correctSymbolAtInstance;

    public IncorrectMatching() {
        symbolAtModel=-1;
        symbolAtInstance=-1;
        correctSymbolAtInstance=-1;
    }

    public int getCorrectSymbolAtInstance() {
        return correctSymbolAtInstance;
    }

    public void setCorrectSymbolAtInstance(int correctSymbolAtInstance) {
        this.correctSymbolAtInstance = correctSymbolAtInstance;
    }

    public int getSymbolAtInstance() {
        return symbolAtInstance;
    }

    public void setSymbolAtInstance(int symbolAtInstance) {
        this.symbolAtInstance = symbolAtInstance;
    }

    public int getSymbolAtModel() {
        return symbolAtModel;
    }

    public void setSymbolAtModel(int symbolAtModel) {
        this.symbolAtModel = symbolAtModel;
    }
    
}
