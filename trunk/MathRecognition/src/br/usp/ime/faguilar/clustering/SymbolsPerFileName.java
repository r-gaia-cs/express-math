/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.clustering;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.util.SymbolUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author frank
 */
public class SymbolsPerFileName {
    Map<String, ArrayList<Classifible>> symbols;
    public SymbolsPerFileName(){
        symbols = new HashMap<String, ArrayList<Classifible>>();
    }

    public void addSymbolsOfFile(String fileName, ArrayList<Classifible> symbolData){
        symbols.put(fileName, symbolData);
    }

    public ArrayList<Classifible> getSymbolsOfFiles(ArrayList<String> fileNames){
        ArrayList<Classifible> requiredSymbols = new ArrayList<Classifible>();
        for (String file : fileNames) {
            requiredSymbols.addAll(symbols.get(file));
        }
        return requiredSymbols;
    }

    public static void createFileData(){
//        ArrayList<Classifible> classifibles = SymbolUtil.readSymbolData(
//                EvaluationView.TEMPLATES_FILE);
        ArrayList<Classifible> classifibles = SymbolUtil.readTemplates();
        

    }
}
