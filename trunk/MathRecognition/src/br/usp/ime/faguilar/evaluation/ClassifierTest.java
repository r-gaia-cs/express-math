/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ShapeContextClassifier;
import br.usp.ime.faguilar.directories.MathRecognitionFiles;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.matching.GraphMatching;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.matching.symbol_matching.UserSymbol;
import br.usp.ime.faguilar.util.FilesUtil;
import br.usp.ime.faguilar.util.SymbolUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frank
 */
public class ClassifierTest {
    private SymbolTestData symbolData;
    private ArrayList<Classifible> trainData;
    private ArrayList<Classifible> testData;
    private double trainningPercent;
    private ArrayList<Classifible> goodResults;
    private ArrayList<Classifible> badResults;
    private Classifier classifier;

    private float alpha;
    private float beta;
    private float gama;

    public ClassifierTest(){
        symbolData = new SymbolTestData();
        goodResults = new ArrayList<Classifible>();
        badResults = new ArrayList<Classifible>();
        trainData = new ArrayList<Classifible>();
        testData = new ArrayList<Classifible>();
        trainningPercent = 0.7;
    }

    public void readData(){
//        ArrayList<Classifible> classifibles = SymbolUtil.readSymbolData(EvaluationView.TEMPLATES_FILE);
        ArrayList<Classifible> classifibles = SymbolUtil.readTemplatesFromInkmlFiles(MathRecognitionFiles.TRAINING_FILES_CROHME,
                MathRecognitionFiles.INKML_CROHME_2012_TRAIN_DIR);//SymbolUtil.readTemplatesFromInkmlFiles();//SymbolUtil.readTemplatesWithStrokesInfo();//SymbolUtil.readTemplates();
        HashMap<UserSymbol, Integer> map = new HashMap<UserSymbol, Integer>();
        for (Classifible classifible : classifibles){
//            if(!selectedClasses.isEmpty()){
//                if(selectedClasses.contains(classifible.getMyClass()))
//            if(map.containsKey(classifible.getUserSymbol())){
//                if(map.get(classifible.getUserSymbol()) < 5)
                    symbolData.addClassyfible(classifible);
//                map.put(classifible.getUserSymbol(), map.get(classifible.getUserSymbol()) + 1);
//            }else{
//                symbolData.addClassyfible(classifible);
//                map.put(classifible.getUserSymbol(), 1);
//            }
//            }
//            else
//               symbolData.addClassyfible(classifible);     
        }
    }

    public String getFrecuencies(){
        return SymbolTestData.getTrainingFrequencies();
    }

    public void prepareClassifier(){
        partData();
        classifier.setTrainingData( trainData);
        classifier.train();
    }

    public void setTestData(ArrayList<Classifible> newTest){
        testData = newTest;
    }

    public ArrayList<Classifible> getTrainData() {
        return trainData;
    }

    public ArrayList<Classifible> getTestData() {
        return testData;
    }

    public void testClassifier(){
        FileWriter fileWritter = null;
        try {
            Classifible clasResult = null;
            int count = 0;
            int tens = 0;
            FilesUtil.append("classifiedSymbols.txt", "0\n");
            for (Classifible classifible : testData) {
                clasResult = (Classifible) classifier.classify(classifible);
                //            System.out.println("classified : " + count++);
//                FilesUtil.write("counter.txt", "classified : " + count++);
                count++;
                if (clasResult != null && goodClass((String) classifible.getMyClass(), (String) clasResult.getMyClass())) {
                    goodResults.add(classifible);
                } else {
                    badResults.add(classifible);
                }
                if(count >= 10){
                    tens++;
                    double percentageGood = goodResults.size() / (double) (goodResults.size() + badResults.size());
                    FilesUtil.append("classifiedSymbols.txt", "number classified: " +
                            tens * count + " percentage good: " + percentageGood + "\n");
                    count = 0;
                }
            }
            //        System.out.println("number of good results: "+ goodResults.size());
            //        System.out.println("number of bad results: "+ badResults.size());
//            String results = "parameters: alpha = " + getAlpha() + "  beta = " + getBeta() +
//                    "  gama = " + getGama() + "\n";
//            String results = "parameters: numOFPoints = " + MatchingParameters.numberOfPointPerSymbol + "  beta = " + getBeta() +
//                    "  gama = " + getGama() + "\n";
            String results = "parameters: numOFPoints = " + MatchingParameters.numberOfPointPerSymbol + "  num reg angulares = " + 
                    MatchingParameters.angularLocalRegions+
                    "  num reg. radial = " + MatchingParameters.LogPolarLocalRegions + "\n";
            results += "number of good results: " + goodResults.size() + "  number of bad results: " +
                    badResults.size() + " percentage: " + ((badResults.size() * 100.) / testData.size() ) + "\n";
//            FilesUtil.write("results.txt", results);
            File file = new File("results-classificationChrome2012Test.txt");
            //if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWritter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(results);
            bufferWritter.close();
            } catch (IOException ex) {
                Logger.getLogger(ClassifierTest.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fileWritter.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClassifierTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }

    public boolean goodClass(String truth, String result){
        if(!truth.equals(result))
            if((truth.equals("\\frac") && result.equals("-")) ||
                (truth.equals("-") && result.equals("\\frac")))
                return true;
            else return false;
        return true;
    }

    public void resetResults(){
        goodResults.clear();
        badResults.clear();
    }

    public void setParametersToClassifier(){
        ((ShapeContextClassifier) classifier).setAlpha(getAlpha());
        ((ShapeContextClassifier) classifier).setBeta(getBeta());
        ((ShapeContextClassifier) classifier).setGama(getGama());
    }

    public void partData(){
        ArrayList<Classifible> selectedSymbols = null;
        Map<Object, List<Classifible>> map = symbolData.getMap();
        int numberOfTrainingElements = 0;
//        int numberOfTestElements = 0;
        for (Object selectedClass : map.keySet()) {
//            if(map.containsKey(selectedClass)){
                selectedSymbols =  (ArrayList<Classifible>) map.get(selectedClass);
                if(selectedSymbols != null && !selectedSymbols.isEmpty()){
                    numberOfTrainingElements = (int) (selectedSymbols.size() * trainningPercent);
//                    numberOfTestElements = selectedSymbols.size() - numberOfTrainingElements;
                    for (int i = 0; i < numberOfTrainingElements; i++)
                        trainData.add(selectedSymbols.get(i));
                    for (int j = numberOfTrainingElements; j < selectedSymbols.size(); j++)
                        testData.add(selectedSymbols.get(j));
//                }
            }
        }
//        System.out.println("training data: " + SymbolTestData.getFrequencies(trainData));
//        System.out.println("test data : " + SymbolTestData.getFrequencies(testData));
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public SymbolTestData getSymbolData() {
        return symbolData;
    }

    public void setSymbolData(SymbolTestData symbolData) {
        this.symbolData = symbolData;
    }



    public double getTrainningPercent() {
        return trainningPercent;
    }

    public void setTrainningPercent(double trainningPercent) {
        this.trainningPercent = trainningPercent;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float getGama() {
        return gama;
    }

    public void setGama(float gama) {
        this.gama = gama;
    }
    
    public Map getMap(){
        return symbolData.getMap();
    }
}