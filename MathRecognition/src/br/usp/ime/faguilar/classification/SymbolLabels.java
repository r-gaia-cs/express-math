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
    private static final String CROHME_2012_LABELS_PATH = "listSymbols.txt";
    private static final String CROHME_2013_LABELS_PATH = "listSymbolCrohme2013.txt";
    private static final String CROHME_2013_LABELS_WITH_JUNK_PATH = "listSymbolCrohme2013withJunk.txt";
    

    public static void readCrohme2013Labels(){
        readLabels(CROHME_2013_LABELS_PATH);
    }
    
    public static void readCrohme2013LabelsWithJunk(){
        readLabels(CROHME_2013_LABELS_WITH_JUNK_PATH);
    }
    
    public static void readCrohme2012Labels(){
        readLabels(CROHME_2012_LABELS_PATH);
    }
    
    
    private static void readLabels(String filePath){
        BufferedReader bufferedReader = FilesUtil.getBufferedReader(filePath);
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
