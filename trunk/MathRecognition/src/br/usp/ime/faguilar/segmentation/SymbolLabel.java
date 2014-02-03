/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.data.DSymbol;

/**
 *
 * @author frank
 */
public class SymbolLabel {
    public static boolean canBeSin(String compoundLabel) {
        if(compoundLabel.length() == 3)
            if(canBeS(compoundLabel.charAt(0)) && canBeI(compoundLabel.charAt(1)) && 
                    canBeN(compoundLabel.charAt(2)))
                return true;
        return false;
    }
    
    public static boolean canBeCos(String compoundLabel) {
        if(compoundLabel.length() == 3)
            if(canBeC(compoundLabel.charAt(0)) && canBeO(compoundLabel.charAt(1)) && 
                    canBeS(compoundLabel.charAt(2)))
                return true;
        return false;
    }
    
    public static boolean canBeTan(String compoundLabel) {
        if(compoundLabel.length() == 3)
            if(canBeT(compoundLabel.charAt(0)) && canBeA(compoundLabel.charAt(1)) && 
                    canBeN(compoundLabel.charAt(2)))
                return true;
        return false;
    }

    public static boolean canBeLog(String compoundLabel) {
        if(compoundLabel.length() == 3)
            if(canBeL(compoundLabel.charAt(0)) && canBeO(compoundLabel.charAt(1)) && 
                    canBeG(compoundLabel.charAt(2)))
                return true;
        return false;
    }
    
    public static boolean canBeLim(String compoundLabel) {
        if(compoundLabel.length() == 3)
            if(canBeL(compoundLabel.charAt(0)) && canBeI(compoundLabel.charAt(1)) && 
                    canBeM(compoundLabel.charAt(2)))
                return true;
        return false;
    }
    
    static boolean canBePM(String compoundLabel) {
        if(compoundLabel.length() == 2){
            if(canBeHorizontal(compoundLabel.charAt(1)) && canBePlus(compoundLabel.charAt(0)))
                return true;
        }else if(compoundLabel.equals("+\\frac"))
            return true;
        return false;
    }
    
    static boolean canBeEqual(String compoundLabel) {
        if(compoundLabel.length() == 2){
            if(canBeHorizontal(compoundLabel.charAt(1)) && canBeHorizontal(compoundLabel.charAt(0)))
                return true;
        }else if(compoundLabel.equals("-\\frac") || compoundLabel.equals("\\frac-"))
            return true;
        return false;
    }
    
    private static boolean canBePlus(char aCharacter) {
        if(aCharacter == '+' || aCharacter == 't')
            return true;
        return false;
    }

    private static boolean canBeHorizontal(char aCharacter) {
        if(aCharacter == '-')
            return true;
        return false;
    }
    
    private static boolean canBeS(char aCharacter) {
        if(aCharacter == 's' || aCharacter == 'S' || 
                aCharacter == '5')
            return true;
        return false;
    }

    private static boolean canBeI(char aCharacter) {
        if(aCharacter == 'i' || aCharacter == 'I' || 
                aCharacter == '1' || aCharacter == '|' || aCharacter == 'l')
            return true;
        return false;    
    }

    private static boolean canBeN(char aCharacter) {
        if(aCharacter == 'n' || aCharacter == 'N')
            return true;
        return false; 
    }

    private static boolean canBeO(char aCharacter) {
        if(aCharacter == '0' || aCharacter == 'O' || 
                aCharacter == 'o')
            return true;
        return false; 
    }

    private static boolean canBeC(char aCharacter) {
        if(aCharacter == 'c' || aCharacter == 'C' || 
                aCharacter == '(')
            return true;
        return false; 
    }

    private static boolean canBeA(char aCharacter) {
        if(aCharacter == 'A' || aCharacter == 'a')
            return true;
        return false; 
    }

    private static boolean canBeT(char aCharacter) {
        if(aCharacter == 't' || aCharacter == 'T' || aCharacter == '+')
            return true;
        return false; 
    }

    private static boolean canBeM(char aCharacter) {
        if(aCharacter == 'm' || aCharacter == 'M')
            return true;
        return false; 
    }

    private static boolean canBeL(char aCharacter) {
        if(aCharacter == 'l' || aCharacter == 'L' || aCharacter == '1' 
                || aCharacter == '|')
            return true;
        return false; 
    }

    private static boolean canBeG(char aCharacter) {
        if(aCharacter == 'g' || aCharacter == 'G' || aCharacter == 'Y' 
                || aCharacter == 'y')
            return true;
        return false; 
    }    

    static boolean canBePoint(DSymbol symbol) {
        String label = symbol.getLabel();
        if(label.equals(".") || label.equals("-") || 
                label.equals("\\frac") || label.equals("COMMA") || 
                label.equals(",") || label.equals("'"))
            return true;
        return false;
    }
}
