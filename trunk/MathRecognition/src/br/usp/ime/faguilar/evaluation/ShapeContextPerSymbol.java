/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.util.RWFiles;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class ShapeContextPerSymbol {
    private String label;
    private double mean;
    private double standardDeviation;
    private double maxDistance;


    public static ArrayList<ShapeContextPerSymbol> readData(String fileName){
        ArrayList<ShapeContextPerSymbol> data = new ArrayList<ShapeContextPerSymbol>();
        String contentAsString = RWFiles.getContentAsString(fileName);
        String[] lines = contentAsString.split("\n");
        String[] symbolLine;
        ShapeContextPerSymbol shapeContextPerSymbol;
        for (String string : lines) {
            if(!string.startsWith("#")){
                symbolLine = string.split(" ");
                shapeContextPerSymbol = new ShapeContextPerSymbol();
                shapeContextPerSymbol.setLabel(symbolLine[0]);
                shapeContextPerSymbol.setMean(Double.valueOf(symbolLine[1]));
                shapeContextPerSymbol.setStandardDeviation(Double.valueOf(symbolLine[2]));
                shapeContextPerSymbol.setMaxDistance(Double.valueOf(symbolLine[3]));
                data.add(shapeContextPerSymbol);
            }
        }
        return data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }
}
