/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class MatchingParameters {
    private float alpha;

    private float beta;

    private float gama;

    private boolean delaunay;

    public static  int LogPolarGlobalRegions=2;
    
    public static int angularGlobalRegions=4;

    public static  int LogPolarLocalRegions=4;

    public static int angularLocalRegions=10;

    public static int numberOfPointPerSymbol=30;

    public static ArrayList<MatchingParameters> readParameters(String filePath){
        ArrayList<MatchingParameters> matchingParameterses=new ArrayList<MatchingParameters>();

        try{
          // Open the file that is the first
          // command line parameter
          FileInputStream fstream = new FileInputStream(filePath);
          // Get the object of DataInputStream
          DataInputStream in = new DataInputStream(fstream);
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String strLine;
          //Read File Line By Line
          while ((strLine = br.readLine()) != null)   {
                if(isReadableLine(strLine)){
                    MatchingParameters parameters=decodeParameters(strLine);
                    matchingParameterses.add(parameters);
                }

          }
          //Close the input stream
          in.close();
            }catch (Exception e){//Catch exception if any
          System.err.println("Error: " + e.getMessage());
          }

        return matchingParameterses;

    }

    public static MatchingParameters decodeParameters(String line){
        MatchingParameters parameters=new MatchingParameters();
        String[] stringParameters=line.split("\t");

        parameters.setAlpha(Float.valueOf(stringParameters[0]));
        parameters.setBeta(Float.valueOf(stringParameters[1]));
        parameters.setGamma(Float.valueOf(stringParameters[2]));
        if(stringParameters[3].equals("1"))
            parameters.setDelaunay(true);
        else
            parameters.setDelaunay(false);

        return parameters;
    }

    public static boolean isReadableLine(String line){
        if(!line.isEmpty()&&Character.isDigit(line.charAt(0)))
            return true;
        return false;
    }


    @Override
    public String toString(){
        String string= "alpha ="+ alpha+" ";
        string+= "beta ="+ beta +" ";
        string+= "gama ="+ gama+" ";
        string+= "delaunay ="+ delaunay+" ";

        return string;
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

    public boolean isDelaunay() {
        return delaunay;
    }

    public void setDelaunay(boolean delaunay) {
        this.delaunay = delaunay;
    }

    public float getGamma() {
        return gama;
    }

    public void setGamma(float gamma) {
        this.gama = gamma;
    }

}
