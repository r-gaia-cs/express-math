/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.util.FilesUtil;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class SegmentationParameters {
    public static String FILE_NAME = "parameters.txt";
    private float alpha;
    private float beta;
    private float gama;
    private float distanceFactor;

    public static ArrayList<SegmentationParameters> readParameters(String fileName){
        ArrayList<SegmentationParameters> parameters = new ArrayList<SegmentationParameters>();
        String fileContent = FilesUtil.getContentAsString(fileName);
        String[] parameterArray = fileContent.split("\n");
        for (int i = 0; i < parameterArray.length; i++) {
            if(!parameterArray[i].startsWith("#")){
                String[] parametersLine = parameterArray[i].split("\t");
                SegmentationParameters newParameters = new SegmentationParameters();
                newParameters.setAlpha(Float.valueOf(parametersLine[0]));
                newParameters.setBeta(Float.valueOf(parametersLine[1]));
                newParameters.setGama(Float.valueOf(parametersLine[2]));
                parameters.add(newParameters);
            }
        }
        return parameters;
    }

    public static ArrayList<SegmentationParameters> readParametersWithDistanceFactor(){
        return readParametersWithDistanceFactor(FILE_NAME);
    }

    public static ArrayList<SegmentationParameters> readParametersWithDistanceFactor(String parametersFile){
        ArrayList<SegmentationParameters> parameters = new ArrayList<SegmentationParameters>();
        String fileContent = FilesUtil.getContentAsString(parametersFile);
        String[] parameterArray = fileContent.split("\n");
        for (int i = 0; i < parameterArray.length; i++) {
            if(!parameterArray[i].startsWith("#")){
                String[] parametersLine = parameterArray[i].split("\t");
                SegmentationParameters newParameters = new SegmentationParameters();
                newParameters.setAlpha(Float.valueOf(parametersLine[0]));
                newParameters.setBeta(Float.valueOf(parametersLine[1]));
                newParameters.setGama(Float.valueOf(parametersLine[2]));
                newParameters.setDistanceFactor(Float.valueOf(parametersLine[3]));
                parameters.add(newParameters);
            }
        }
        return parameters;
    }

    public float getDistanceFactor() {
        return distanceFactor;
    }

    public void setDistanceFactor(float distanceFactor) {
        this.distanceFactor = distanceFactor;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float getGama() {
        return gama;
    }

    public void setGama(float gama) {
        this.gama = gama;
    }
}
