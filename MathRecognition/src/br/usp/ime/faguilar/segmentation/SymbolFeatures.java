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
    private double height;
    private double width;

    public SymbolFeatures() {
        setDefaultFeatureValues();
    }

    public final void setDefaultFeatureValues(){
        setNumberOfStrokes(UNDEFINED_NUMBER_OF_STROKES);
        setHeight(0);
        setWidth(0);
    }

    public static void setUpMinimumNumberOfStrokesPerSymbol(){
        numberOfStrokesPerSymbol = new HashMap<String, Integer>();
        numberOfStrokesPerSymbol.put("=", 2);
        numberOfStrokesPerSymbol.put("+", 2);
        numberOfStrokesPerSymbol.put("\\leq", 2);
        numberOfStrokesPerSymbol.put("||", 2);
        numberOfStrokesPerSymbol.put("i", 2);
        numberOfStrokesPerSymbol.put("j", 2);
        numberOfStrokesPerSymbol.put("!", 2);
        numberOfStrokesPerSymbol.put("t", 2);
        numberOfStrokesPerSymbol.put("F", 2);
        numberOfStrokesPerSymbol.put("\\forall", 2);
        numberOfStrokesPerSymbol.put("\\geq", 2);
        numberOfStrokesPerSymbol.put("\\in", 2);
        numberOfStrokesPerSymbol.put("\\exists", 2);
        numberOfStrokesPerSymbol.put("\\sin", 2);
        numberOfStrokesPerSymbol.put("\\tan", 2);
        numberOfStrokesPerSymbol.put("\\times", 2);

        numberOfStrokesPerSymbol.put("\\equiv", 3);
        numberOfStrokesPerSymbol.put("\\neq", 3);
        numberOfStrokesPerSymbol.put("\\pi", 3);
        numberOfStrokesPerSymbol.put("\\pm", 3);
        numberOfStrokesPerSymbol.put("\\prod", 3);
        numberOfStrokesPerSymbol.put("\\cdots", 3);
        numberOfStrokesPerSymbol.put("\\div", 3);
        numberOfStrokesPerSymbol.put("\\ldots", 3);
        numberOfStrokesPerSymbol.put("\\neq", 3);
        numberOfStrokesPerSymbol.put("\\rightarrow", 3);
    }

    public static int getMinimumNumberOfStrokesForSymbol(String symbol){
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

}
