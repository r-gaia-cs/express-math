/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.util.SymbolUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author frank
 */
public class ClassifierTest {
    private SymbolTestData symbolData;
    private ArrayList<Classifible> trainData;
    private ArrayList<Classifible> testData;
    private ArrayList selectedClasses;
    private double trainningPercent;
    private ArrayList<Classifible> goodResults;
    private ArrayList<Classifible> badResults;
    private Classifier classifier;

    public ClassifierTest(){
        symbolData = new SymbolTestData();
        selectedClasses = null;
        goodResults = new ArrayList<Classifible>();
        badResults = new ArrayList<Classifible>();
        trainData = new ArrayList<Classifible>();
        testData = new ArrayList<Classifible>();
    }

    public void readData(){
        ArrayList<Classifible> classifibles = SymbolUtil.readSymbolData(EvaluationView.TEMPLATES_FILE);
        for (Classifible classifible : classifibles){
//            if(!selectedClasses.isEmpty()){
                if(selectedClasses.contains(classifible.getMyClass()))
                    symbolData.addClassyfible(classifible);
//            }
//            else
//               symbolData.addClassyfible(classifible);     
        }
    }

    public String getFrecuencies(){
        return SymbolTestData.getFrequencies();
    }

    public void testClassifier(){
        partData();
        classifier.setTrainingData( trainData);
        classifier.train();
        Classifible clasResult =null;
        for (Classifible classifible : testData) {
            clasResult = (Classifible) classifier.classify(classifible);
            if(classifible.getMyClass().equals(clasResult.getMyClass()))
                goodResults.add(classifible);
            else
                badResults.add(classifible);
        }
        System.out.println("number of good results: "+ goodResults.size());
        System.out.println("number of bad results: "+ badResults.size());
    }

    public void partData(){
        ArrayList<Classifible> selectedSymbols = null;
        Map<Object, List<Classifible>> map = symbolData.getMap();
        int numberOfTrainingElements = 0;
//        int numberOfTestElements = 0;
        for (Object selectedClass : selectedClasses) {
            if(map.containsKey(selectedClass)){
                selectedSymbols =  (ArrayList<Classifible>) map.get(selectedClass);
                if(selectedSymbols != null && !selectedSymbols.isEmpty()){
                    numberOfTrainingElements = (int) (selectedSymbols.size() * trainningPercent);
//                    numberOfTestElements = selectedSymbols.size() - numberOfTrainingElements;
                    for (int i = 0; i < numberOfTrainingElements; i++)
                        trainData.add(selectedSymbols.get(i));
                    for (int j = numberOfTrainingElements; j < selectedSymbols.size(); j++)
                        testData.add(selectedSymbols.get(j));
                }
            }
        }
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    

    public void setSelectedClasses(ArrayList selectedClasses) {
        this.selectedClasses = selectedClasses;
    }

    public SymbolTestData getSymbolData() {
        return symbolData;
    }

    public void setSymbolData(SymbolTestData symbolData) {
        this.symbolData = symbolData;
    }

    public ArrayList getSelectedClasses() {
        return selectedClasses;
    }


    public double getTrainningPercent() {
        return trainningPercent;
    }

    public void setTrainningPercent(double trainningPercent) {
        this.trainningPercent = trainningPercent;
    }
    
}
