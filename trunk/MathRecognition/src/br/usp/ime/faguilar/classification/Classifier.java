/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

import java.util.ArrayList;

/**
 *
 * @author frank
 */
public interface Classifier {
    public void setTrainingData(ArrayList<Classifible> classifiables);
    public void train();
    public Object classify(Classifible classifible);
}
