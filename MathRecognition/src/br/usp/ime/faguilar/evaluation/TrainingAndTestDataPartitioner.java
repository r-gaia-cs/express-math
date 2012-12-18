/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.util.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author frank
 */
public class TrainingAndTestDataPartitioner {
    private Map<String, ArrayList<String>> expressionsPerModel;
    private ArrayList<String> trainingExpressions;
    private ArrayList<String> testExpressions;
    private double trainingPercentage;
    

    public TrainingAndTestDataPartitioner(){
        expressionsPerModel = new HashMap<String, ArrayList<String>>();
        trainingExpressions = new ArrayList<String>();
        testExpressions = new ArrayList<String>();
        trainingPercentage = 0.7;
    }

    public void partSamples(ArrayList<String> fileNames){
        chargeFileNames(fileNames);
        doPartition();
    }

    private void chargeFileNames(ArrayList<String> fileNames){
        String modelID;
        for (int i = 0; i < fileNames.size(); i++) {
            ArrayList<String> expressionNames = new ArrayList<String>();
            modelID = fileNames.get(i).split("_")[0];
            if(expressionsPerModel.containsKey(modelID))
                expressionNames = expressionsPerModel.get(modelID);
            expressionNames.add(fileNames.get(i));
            expressionsPerModel.put(modelID, expressionNames);
        }
    }

    private void doPartition() {
        int numberOfTrainingElements;
        for (ArrayList<String> expressionsOfAModel : expressionsPerModel.values()) {
            Util.randomizeInPlaze(expressionsOfAModel);
            numberOfTrainingElements = (int) (expressionsOfAModel.size() * trainingPercentage);
            for (int i = 0; i < expressionsOfAModel.size(); i++) {
                if(i < numberOfTrainingElements)
                    trainingExpressions.add(expressionsOfAModel.get(i));
                else
                    testExpressions.add(expressionsOfAModel.get(i));
            }
        }
    }

    public Map<String, ArrayList<String>> getExpressionsPerModel() {
        return expressionsPerModel;
    }

    public void setExpressionsPerModel(Map<String, ArrayList<String>> expressionsPerModel) {
        this.expressionsPerModel = expressionsPerModel;
    }

    public ArrayList<String> getTestExpressions() {
        return testExpressions;
    }

    public void setTestExpressions(ArrayList<String> testExpressions) {
        this.testExpressions = testExpressions;
    }

    public ArrayList<String> getTrainingExpressions() {
        return trainingExpressions;
    }

    public void setTrainingExpressions(ArrayList<String> trainingExpressions) {
        this.trainingExpressions = trainingExpressions;
    }

    public double getTrainingPercentage() {
        return trainingPercentage;
    }

    public void setTrainingPercentage(double trainingPercentage) {
        this.trainingPercentage = trainingPercentage;
    }


}
