/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.parser;

/**
 *
 * @author frank
 */
public class RegionLabel {
    public static final int NOT_DEFINED = 0;
    public static final int ABOVE = 1;
    public static final int BELOW = 2;
    public static final int SUPERSCRIPT = 3;
    public static final int SUBSCRIPT = 4;
    public static final int UPPER = 5;
    public static final int LOWER = 6;
    public static final int TOP_LEFT = 7;
    public static final int BOTTOM_LEFT = 8;
    public static final int INSIDE = 9;
    public static final int EXPRESSION = 10;
    public static final int RIGHT = 11;
    
    public static String stringRepresentation(int regionLabel){
        switch(regionLabel){
            case NOT_DEFINED: return "NOT_DEFINED";
            case ABOVE: return "ABOVE";
            case BELOW: return "BELOW";
            case SUPERSCRIPT: return "SUPERSCRIPT";
            case SUBSCRIPT: return "SUBSCRIPT";
            case UPPER: return "UPPER";
            case LOWER: return "LOWER";
            case TOP_LEFT: return "TOP_LEFT";
            case BOTTOM_LEFT: return "BOTTOM_LEFT";
            case INSIDE: return "INSIDE";
            case EXPRESSION: return "EXPRESSION";
        }
        return "";
    }
    
    public static String crohme2013Abreviation(int regionLabel){
        switch(regionLabel){
            case ABOVE: return "A";
            case BELOW: return "B";
            case SUPERSCRIPT: return "Sup";
            case SUBSCRIPT: return "Sub";
            case INSIDE: return "I";
            case RIGHT: return "R";
        }
        return "";
    }
}
