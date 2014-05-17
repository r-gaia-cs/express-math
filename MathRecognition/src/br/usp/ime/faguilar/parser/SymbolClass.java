/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.util.FilesUtil;
import java.util.HashMap;

/**
 *
 * @author frank
 */
public class SymbolClass {
   
    public static final int NON_SCRIPTED = 1;
    public static final int OPEN_BRACKET = 2;
    public static final int ROOT = 3;
    public static final int VARIABLE_RANGE = 4;
    public static final int ASCENDER = 5;
    public static final int DESCENDER = 6;
    public static final int CENTERED = 7;
    public static final int CLOSE_BRACKET = 8;
    
    private static final float CENTROID_RATIO = (float) 0.25;
    private static final float TREASHOLD_RATIO = (float) 0.2;//0.2;//0.17;
    
    public static final HashMap<String, Integer> symbolClasses = SymbolClass.readSymbolClasses();
    
    private static final String fileWithSymbolClasses = "symbol-classes-chrome2013.txt";//"files/symbol-classes.txt";
    
    public static double centroidY(DSymbol symbol){
        int symbolClass = symbolClass(symbol);
        return centroidY(symbolClass, symbol);
    }
    
    public static double below(DSymbol symbol){
        int symbolClass = symbolClass(symbol);
        return below(symbolClass, symbol);
    }
    
    public static double above(DSymbol symbol){
        int symbolClass = symbolClass(symbol);
        return above(symbolClass, symbol);
    }
    
    public static double subscript(DSymbol symbol){
        int symbolClass = symbolClass(symbol);
        return subscript(symbolClass, symbol);
    }
    
    public static double leftSubscript(DSymbol symbol){
        int symbolClass = symbolClass(symbol);
        return leftSubscript(symbolClass, symbol);
    }
    
    public static double superscript(DSymbol symbol){
        int symbolClass = symbolClass(symbol);
        return superscript(symbolClass, symbol);
    }
    
    public static double leftSuperscript(DSymbol symbol){
        int symbolClass = symbolClass(symbol);
        return leftSuperscript(symbolClass, symbol);
    }
    
    public static int symbolClass(DSymbol symbol){
        String label = symbol.getLabel();
//        System.out.println("label: " + label);
        return symbolClasses.get(label);
    }

    private static HashMap<String, Integer> readSymbolClasses() {
        String fileContent = FilesUtil.getContentAsString(fileWithSymbolClasses);
        String[] lines = fileContent.split("\n");
        HashMap<String, Integer> classes = new HashMap<String, Integer>();
        for (int i = 0; i < lines.length; i++) {
            String[] lineArray = lines[i].split("\t");
            classes.put(lineArray[0], Integer.valueOf(lineArray[1]));
        }
        return classes;
    }

    public static double centroidY(int symbolClass, DSymbol symbol) {
        double centroidY = -1;
        double bottomY = symbol.getRbPoint().getY();
        switch(symbolClass){
            case NON_SCRIPTED: centroidY = bottomY - 0.5 * symbol.getHeightAsDouble(); break;
            case OPEN_BRACKET: centroidY = bottomY - 0.5 * symbol.getHeightAsDouble(); break; //CENTROID_RATIO * symbol.getHeightAsDouble(); break;
            case ROOT: centroidY = bottomY - CENTROID_RATIO * symbol.getHeightAsDouble(); break;
            case VARIABLE_RANGE: centroidY = bottomY - 0.5 * symbol.getHeightAsDouble(); break;
            case ASCENDER: centroidY = bottomY - CENTROID_RATIO * symbol.getHeightAsDouble(); break;
            case DESCENDER: centroidY = bottomY - (1- CENTROID_RATIO) * symbol.getHeightAsDouble(); break;
            case CENTERED : centroidY = bottomY - 0.5 * symbol.getHeightAsDouble(); break;
            case CLOSE_BRACKET : centroidY = bottomY - 0.5 * symbol.getHeightAsDouble(); break;
        }
        return centroidY;
    }
    
    public static double below(int symbolClass, DSymbol symbol) {
        double treashold = -1;
        double bottomY = symbol.getRbPoint().getY();
        switch(symbolClass){
            case NON_SCRIPTED: treashold = bottomY - 0.5 * symbol.getHeightAsDouble(); break;
            case OPEN_BRACKET: treashold = bottomY; break;
            case ROOT: treashold = bottomY; break;
            case VARIABLE_RANGE: treashold = bottomY ; break;//- TREASHOLD_RATIO* symbol.getHeightAsDouble(); break;
            case ASCENDER: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case DESCENDER: treashold = bottomY - (0.5 * (1 + TREASHOLD_RATIO)) * symbol.getHeightAsDouble(); break;
            case CENTERED: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case CLOSE_BRACKET: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
        }
        return treashold;
    }
    
    public static double above(int symbolClass, DSymbol symbol) {
        double treashold = -1;
        double bottomY = symbol.getRbPoint().getY();
        switch(symbolClass){
            case NON_SCRIPTED: treashold = bottomY - 0.5 * symbol.getHeightAsDouble(); break;
            case OPEN_BRACKET: treashold = symbol.getLtPoint().getY(); break;
            case ROOT: treashold = symbol.getLtPoint().getY(); break;
            case VARIABLE_RANGE: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case ASCENDER: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case DESCENDER: treashold = bottomY - (1 - 0.5 * TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case CENTERED: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case CLOSE_BRACKET: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
        }
        return treashold;
    }
    
    public static double subscript(int symbolClass, DSymbol symbol) {
        double treashold = Double.MAX_VALUE;
        double bottomY = symbol.getRbPoint().getY();
        switch(symbolClass){
            case ROOT: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case VARIABLE_RANGE: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case ASCENDER: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case DESCENDER: treashold = bottomY - (0.5 * (1 + TREASHOLD_RATIO)) * symbol.getHeightAsDouble(); break;
            case CENTERED: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case CLOSE_BRACKET: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
        }
        return treashold;
    }
    
    public static double leftSubscript(int symbolClass, DSymbol symbol) {
        double treashold = Double.MAX_VALUE;
        double bottomY = symbol.getRbPoint().getY();
        switch(symbolClass){
            case NON_SCRIPTED: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case OPEN_BRACKET: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;   
            case ROOT: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case VARIABLE_RANGE: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case ASCENDER: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case DESCENDER: treashold = (0.5 * (1 + TREASHOLD_RATIO)) * symbol.getHeightAsDouble(); break;
            case CENTERED: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
            case CLOSE_BRACKET: treashold = bottomY - TREASHOLD_RATIO * symbol.getHeightAsDouble(); break;
        }
        return treashold;
    }
    
    public static double superscript(int symbolClass, DSymbol symbol) {
        double treashold = Double.MIN_VALUE;
        double bottomY = symbol.getRbPoint().getY();
        switch(symbolClass){
            case ROOT: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case VARIABLE_RANGE: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case ASCENDER: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case DESCENDER: treashold = bottomY - (1 - 0.5 * TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case CENTERED: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case CLOSE_BRACKET: treashold = bottomY - (1 - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
        }
        return treashold;
    }
    
    public static double leftSuperscript(int symbolClass, DSymbol symbol) {
        double treashold = Double.MIN_VALUE;
        double bottomY = symbol.getRbPoint().getY();
        switch(symbolClass){
            case NON_SCRIPTED: treashold = bottomY - (1. - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case OPEN_BRACKET: treashold = bottomY - (1. - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case ROOT: treashold = bottomY - (1. - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case VARIABLE_RANGE: treashold = bottomY - (1. - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case ASCENDER: treashold = bottomY - (1. - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case DESCENDER: treashold = bottomY - (1. - 0.5 * TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case CENTERED: treashold = bottomY - (1. - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            case CLOSE_BRACKET: treashold = bottomY - (1. - TREASHOLD_RATIO) * symbol.getHeightAsDouble(); break;
            
        }
        return treashold;
    }
}