/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util;

/**
 *
 * @author Frank Aguilar
 */
public class ArraysUtil {
    public static double[] concat(double[] array1, double[] array2){
        double[] result = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
    
    public static double[] difference(double[] array1, double[] array2){
        double[] result = new double[array1.length];
        for (int i = 0; i < array1.length; i++) {
            result[i] = array1[i] - array2[i];            
        }
        return result;
    }
    
    public static String formatArrayWithSeparator(double[] array, String separator){
        String string = "";
        for (double d : array) {
            string += d + separator;
        }
        return string;
    }
    
    
}
