/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.classification;

import java.util.ArrayList;

/**
 *
 * @author frank.aguilar
 */
public class SymbolClasses {
    public static String[] CLASSES =
            new String[]{
//                "2", "x", "=", "r" , "c",
//                "o", "s" , "\\theta", "t", "a",
                "n", "-", "1", "y", "\\frac", 
                "\\sqrt", "+"};
//            new String[]{
//                "a", "b","(", ")", "-", 
//                "1", "0", "=", "k", "t",
//                "\\theta", "i", "s", "r", "p"};
    
    public static ArrayList<String> getClassesAsArrayList(){
        ArrayList<String> arrayOfClasses = new ArrayList<String>();
        for (String string : CLASSES) {
            arrayOfClasses.add(string);
        }
        return arrayOfClasses;
    }
            
//            classes.add("a");
//        classes.add("b");
//        classes.add("(");
//        classes.add(")");
//        classes.add("-");
//
//        classes.add("a");
//        classes.add("b");
//        classes.add("(");
//        classes.add(")");
//        classes.add("-");
//
//        classes.add("1");
//        classes.add("0");
//        classes.add("=");
//        classes.add("k");
//        classes.add("t");
////        WITH 15 FIRST CLASSES RESULTS ARE
//// number of good results: 537
////number of bad results: 2
//
//        classes.add("\theta");
//        classes.add("i");
//        classes.add("s");
//        classes.add("r");
//        classes.add("p");

}
