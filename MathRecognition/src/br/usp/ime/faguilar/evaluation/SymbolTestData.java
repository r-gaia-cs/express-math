/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.directories.MathRecognitionFiles;
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
        list.add(classifible);
        map.put(classifible.getMyClass(), list);
    }

    public static String getTrainingFrequencies(){
        // Initialize frequency table from command line
//        ArrayList<Classifible> allSymbols = SymbolUtil.readSymbolData(EvaluationView.TEMPLATES_FILE);
        ArrayList<Classifible> allSymbols = SymbolUtil.readTemplatesFromInkmlFiles(
                MathRecognitionFiles.TRAINING_FILES, 
                MathRecognitionFiles.INKML_CROHME_2012_TRAIN_DIR);//SymbolUtil.readTemplates();
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
        return map.toString().replaceAll(",\\s", "\n");
    }

    public Map<Object, List<Classifible>> getMap() {
        return map;
    }

    public void setMap(Map<Object, List<Classifible>> map) {
        this.map = map;
    }

    public ArrayList<Classifible> getAllSymbolsAsArrayList(){
        ArrayList<Classifible> symbols = new ArrayList<Classifible>();
        for (List classifibles : map.values()) {
            symbols.addAll(classifibles);
        }
        return symbols;
    }
}
