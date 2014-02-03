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
public abstract class Classifier {
    public abstract void setTrainingData(ArrayList<Classifible> classifiables);
    public abstract void train();
    public abstract Object classify(Classifible classifible);
    public abstract Object orderedListOfClasses();
}
