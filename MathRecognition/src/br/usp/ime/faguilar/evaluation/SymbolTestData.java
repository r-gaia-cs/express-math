/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.util.SymbolUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author frank
 */
public class SymbolTestData {
    private ArrayList<Classifible> classifiebles;
    private Map<Object, List<Classifible>> map;

    public SymbolTestData(){
        map = new HashMap<Object, List<Classifible>>();
    }

    public void addClassyfible(Classifible classifible){
        List<Classifible> list = map.get(classifible.getMyClass());
        if(list == null){
            map.put(classifible.getMyClass(),
                    new ArrayList<Classifible>());
            list = map.get(classifible.getMyClass());
        }
//        TO LIMIT NUMBER OF SAMPLES PER CLASSIFIBLE CLASS
//        if(list.size() < 30)
        list.add(classifible);
        map.put(classifible.getMyClass(), list);
    }

    public static String getTrainingFrequencies(){
        // Initialize frequency table from command line
//        ArrayList<Classifible> allSymbols = SymbolUtil.readSymbolData(EvaluationView.TEMPLATES_FILE);
        ArrayList<Classifible> allSymbols = SymbolUtil.readTemplatesFromInkmlFiles();//SymbolUtil.readTemplates();
        return getFrequencies(allSymbols);
    }

    public static String getFrequencies(ArrayList<Classifible> allSymbols){
        // Initialize frequency table from command line
//        ArrayList<Classifible> allSymbols = SymbolUtil.readSymbolData(EvaluationView.TEMPLATES_FILE);
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Classifible a : allSymbols) {
            Integer freq = map.get((String) a.getMyClass());
            map.put((String) a.getMyClass(), (freq == null) ? 1 : freq + 1);
        }
        System.out.println(map.size() + " distinct words:");
        System.out.println(allSymbols.size() + " templates");
//        System.out.println(map);
        return map.toString().replaceAll(",\\s", "\n");
    }

    public ArrayList<Classifible> getClassifiebles() {
        return classifiebles;
    }

    public void setClassifiebles(ArrayList<Classifible> classifiebles) {
        this.classifiebles = classifiebles;
    }

    public Map<Object, List<Classifible>> getMap() {
        return map;
    }

    public void setMap(Map<Object, List<Classifible>> map) {
        this.map = map;
    }


//
//    public Map<String, Integer> getMap() {
//        return map;
//    }
//
//    public void setMap(Map<String, Integer> map) {
//        this.map = map;
//    }
}
