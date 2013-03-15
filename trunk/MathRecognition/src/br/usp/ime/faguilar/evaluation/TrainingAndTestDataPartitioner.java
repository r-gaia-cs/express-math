/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.util.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        trainingPercentage = 0.5;
    }

    public void partFileNamesRandomlyByModels(ArrayList<String> fileNames){
        chargeFileNamesGroupedByModels(fileNames);
        doPartitionRandomlyByModels();
    }

    public void partFileNamesRandomly(ArrayList<String> fileNames){
        doPartitionRandmly(fileNames);
    }

    private void chargeFileNamesGroupedByModels(ArrayList<String> fileNames){
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

    private void doPartitionRandomlyByModels() {
        long numberOfTrainingElements;
        boolean applyFloor =  true;
        Random r = new Random();
        for (ArrayList<String> expressionsOfAModel : expressionsPerModel.values()) {
            Util.randomizeInPlaze(expressionsOfAModel);
            applyFloor = r.nextBoolean();
            if (applyFloor)
                numberOfTrainingElements = (long)Math.floor(expressionsOfAModel.size() * trainingPercentage);
            else
                numberOfTrainingElements = (long)Math.ceil(expressionsOfAModel.size() * trainingPercentage);
//            numberOfTrainingElements = Math.round(expressionsOfAModel.size() * trainingPercentage);
            for (int i = 0; i < expressionsOfAModel.size(); i++) {
                if(i < numberOfTrainingElements)
                    trainingExpressions.add(expressionsOfAModel.get(i));
                else
                    testExpressions.add(expressionsOfAModel.get(i));
            }
        }
    }

    private void doPartitionRandmly(ArrayList<String> fileNames) {
        Util.randomizeInPlaze(fileNames);
        int numberOfTrainingElements = (int) (trainingPercentage * fileNames.size());
        int count = 0;
        for (String name : fileNames) {
            if(count < numberOfTrainingElements)
                trainingExpressions.add(name);
            else
                testExpressions.add(name);
            count++;
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
