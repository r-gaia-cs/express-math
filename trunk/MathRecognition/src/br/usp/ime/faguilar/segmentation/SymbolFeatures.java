/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author frank
 */
public class SymbolFeatures {
    public static final int UNDEFINED_NUMBER_OF_STROKES = -1;
    private static Map<String, Integer> numberOfStrokesPerSymbol;



    private int numberOfStrokes;

    public SymbolFeatures() {
        setDefaultFeatureValues();
    }

    public final void setDefaultFeatureValues(){
        setNumberOfStrokes(UNDEFINED_NUMBER_OF_STROKES);
    }

    public static void setUpNumberOfStrokesPerSymbol(){
        numberOfStrokesPerSymbol = new HashMap<String, Integer>();
        numberOfStrokesPerSymbol.put("=", 2);
        numberOfStrokesPerSymbol.put("+", 2);
        numberOfStrokesPerSymbol.put("\\leq", 2);
        numberOfStrokesPerSymbol.put("||", 2);
        numberOfStrokesPerSymbol.put("i", 2);
        numberOfStrokesPerSymbol.put("j", 2);

        numberOfStrokesPerSymbol.put("!", 2);
        numberOfStrokesPerSymbol.put("t", 2);

        numberOfStrokesPerSymbol.put("\\equiv", 3);
        numberOfStrokesPerSymbol.put("\\neq", 3);
        numberOfStrokesPerSymbol.put("\\pi", 3);
        numberOfStrokesPerSymbol.put("\\pm", 3);
    }

    public static int getNumberOfStrokesForSymbol(String symbol){
        if(numberOfStrokesPerSymbol.containsKey(symbol))
            return numberOfStrokesPerSymbol.get(symbol);
        return UNDEFINED_NUMBER_OF_STROKES;
    }

    public int getNumberOfStrokes() {
        return numberOfStrokes;
    }

    public void setNumberOfStrokes(int numberOfStrokes) {
        this.numberOfStrokes = numberOfStrokes;
    }
}
