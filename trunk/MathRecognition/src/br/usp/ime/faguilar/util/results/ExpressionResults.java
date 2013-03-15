/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util.results;

import br.usp.ime.faguilar.util.FilesUtil;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class ExpressionResults {
    private String expressionName;
    private float strokeClassificationRate;
    private float segmentationRate;
    private float SymbolRecognitionRate;
    private int numberOfSymbols;

    private static final int posExpressionName = 0;
    private static final int posStrokeClassificationRate = 2;
    private static final int posSegmentationRate = 3;
    private static final int posSymbolReconitionRate = 4;


    public static ArrayList<ExpressionResults> readResults(String fileName){
        String content = FilesUtil.getContentAsString(fileName);
        ArrayList<ExpressionResults> results = new ArrayList<ExpressionResults>();
        String[] contentArray = content.split("\n");
        String[] resultLineArray;
        ExpressionResults newResult;
        for (int i = 0; i < contentArray.length; i++) {
           resultLineArray = contentArray[i].split("\\s+");
           newResult = new ExpressionResults();
           newResult.setExpressionName(resultLineArray[posExpressionName]);
           newResult.setStrokeClassificationRate(Float.valueOf(resultLineArray[posStrokeClassificationRate]));
           newResult.setSegmentationRate(Float.valueOf(resultLineArray[posSegmentationRate]));
           newResult.setSymbolRecognitionRate(Float.valueOf(resultLineArray[posSymbolReconitionRate]));
           results.add(newResult);
        }
        return results;
    }

    public float getSymbolRecognitionRate() {
        return SymbolRecognitionRate;
    }

    public void setSymbolRecognitionRate(float SymbolRecognitionRate) {
        this.SymbolRecognitionRate = SymbolRecognitionRate;
    }

    public String getExpressionName() {
        return expressionName;
    }

    public void setExpressionName(String expressionName) {
        this.expressionName = expressionName;
    }

    public float getSegmentationRate() {
        return segmentationRate;
    }

    public void setSegmentationRate(float segmentationRate) {
        this.segmentationRate = segmentationRate;
    }

    public float getStrokeClassificationRate() {
        return strokeClassificationRate;
    }

    public void setStrokeClassificationRate(float strokeClassificationRate) {
        this.strokeClassificationRate = strokeClassificationRate;
    }

    public int getNumberOfSymbols() {
        return numberOfSymbols;
    }

    public void setNumberOfSymbols(int numberOfSymbols) {
        this.numberOfSymbols = numberOfSymbols;
    }


}
