/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.util.FilesUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frank.aguilar
 */
public class SymbolLabels {
    private static ArrayList<String> symbolLabels;
    private static Map<String, Integer> labelToPosition;
    private static final String LABELS_PATH = "listSymbols.txt";

    public static void readClassesFromFile(){
        BufferedReader bufferedReader = FilesUtil.getBufferedReader(LABELS_PATH);
        symbolLabels = new ArrayList<>();
        labelToPosition = new HashMap<>();
        String line;
        try {
            while(bufferedReader.ready()){
                line = bufferedReader.readLine();
                addLabel(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(SymbolLabels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addLabel(String label){
        if(!label.isEmpty()){
            symbolLabels.add(label);
            int index = symbolLabels.size() -1;
            labelToPosition.put(label, index);
        }        
    }
    
    public static String getLabelOfSymbolByIndex(int index){
        return symbolLabels.get(index);
    }
    
    public static int getIndexOfSymbolByLabel(String label){
        return labelToPosition.get(label);
    }

    public static ArrayList<String> getSymbolLabels() {
        return symbolLabels;
    }

    public static void setSymbolLabels(ArrayList<String> symbolLabels) {
        SymbolLabels.symbolLabels = symbolLabels;
    }
    
    
}
