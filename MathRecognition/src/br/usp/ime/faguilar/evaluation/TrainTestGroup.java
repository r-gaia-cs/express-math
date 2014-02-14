/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifible;
import java.util.ArrayList;

/**
 *
 * @author Frank Aguilar
 */
public class TrainTestGroup {
    private ArrayList<Classifible> train;
    private ArrayList<Classifible> test;

    public ArrayList<Classifible> getTrain() {
        return train;
    }

    public void setTrain(ArrayList<Classifible> train) {
        this.train = train;
    }

    public ArrayList<Classifible> getTest() {
        return test;
    }

    public void setTest(ArrayList<Classifible> test) {
        this.test = test;
    }
    
}
