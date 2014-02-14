/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifible;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Frank Aguilar
 */

public class KFoldPartitioner {
    private Map<Object, List<Classifible>> map;
    public static final int numberOfFolds = 10;

    public TrainTestGroup partWithTestFoldAt(int fold){
        ArrayList<Classifible> selectedSymbols;
        TrainTestGroup group = new TrainTestGroup();
        ArrayList<Classifible> train = new ArrayList<>();
        ArrayList<Classifible> test =  new ArrayList<>();
        int testfrom, testTo;
        for (Object selectedClass : map.keySet()) {
            selectedSymbols =  (ArrayList<Classifible>) map.get(selectedClass);
            testfrom = Math.round((fold - 1) * (selectedSymbols.size() / numberOfFolds));
            testTo = Math.round(fold * selectedSymbols.size() / numberOfFolds);
            for (int i = 0; i < selectedSymbols.size(); i++) {
                if(i >= testfrom && i < testTo)
                    test.add(selectedSymbols.get(i));
                else
                    train.add(selectedSymbols.get(i));
            }
        }
        group.setTest(test);
        group.setTrain(train);
        return group;
    }
    
    
//    public void partData(){
//        ArrayList<Classifible> selectedSymbols = null;
//        Map<Object, List<Classifible>> map = symbolData.getMap();
//        int numberOfTrainingElements = 0;
////        int numberOfTestElements = 0;
//        for (Object selectedClass : map.keySet()) {
////            if(map.containsKey(selectedClass)){
//                selectedSymbols =  (ArrayList<Classifible>) map.get(selectedClass);
//                if(selectedSymbols != null && !selectedSymbols.isEmpty()){
//                    numberOfTrainingElements = (int) (selectedSymbols.size() * trainningPercent);
////                    numberOfTestElements = selectedSymbols.size() - numberOfTrainingElements;
//                    for (int i = 0; i < numberOfTrainingElements; i++)
//                        trainData.add(selectedSymbols.get(i));
//                    for (int j = numberOfTrainingElements; j < selectedSymbols.size(); j++)
//                        testData.add(selectedSymbols.get(j));
////                }
//            }
//        }
////        System.out.println("training data: " + SymbolTestData.getFrequencies(trainData));
////        System.out.println("test data : " + SymbolTestData.getFrequencies(testData));
//    }

    public Map<Object, List<Classifible>> getMap() {
        return map;
    }

    public void setMap(Map<Object, List<Classifible>> map) {
        this.map = map;
    }
}
