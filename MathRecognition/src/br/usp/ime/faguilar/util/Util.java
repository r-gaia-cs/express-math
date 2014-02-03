/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frank
 */
public class Util {
     public static int[][] stringToIntMatrix(String[][] m){
        int [][] mInt=new int[m.length][m[0].length];
        for (int i = 0; i < m.length; i++) {
            String[] is = m[i];
            for (int j = 0; j < is.length; j++) {
                mInt[i][j] = Integer.valueOf(is[j]);
            }
        }
        return mInt;
    }

     public static void randomizeInPlaze(List listToBeRandomized){
         Random random = new Random();
         for (int i = listToBeRandomized.size() - 1; i >=0 ; i--) {
             Collections.swap(listToBeRandomized, i, random.nextInt(i + 1));
         }
     }
     
     public static int[] randomNumbers(int numberOfElements, int from, int to){
         List numbers = new ArrayList<Integer>(to - from + 1); 
         for (int i = from; i <= to; i++) 
             numbers.add(i);
         randomizeInPlaze(numbers);
         int[] arrayOfNumbers = new int[numberOfElements];
         for (int i = 0; i < numberOfElements; i++)
             arrayOfNumbers[i] = (Integer) numbers.get(i);
         return arrayOfNumbers;
     }
     
     public static int[] sampleNumbers(int numberOfElements, int from, int to){
         float ratio = (to - from + 1) / numberOfElements;
         float pos = 0;
         List numbers = new ArrayList<Integer>(to - from + 1); 
         for (int i = from; i <= to; i++) 
             numbers.add(i);
         int[] arrayOfNumbers = new int[numberOfElements];
         for (int i = 0; i < numberOfElements; i++) {
             arrayOfNumbers[i] = (Integer)numbers.get(Math.round(pos));
             pos += ratio;
         }
         return arrayOfNumbers;
     }

     public static Object[] toObjectArray(List<?> l){
         Object[] ob=new Object[l.size()];
         for (int i = 0; i < ob.length; i++) {
             ob[i]=l.get(i);
         }
         return ob;
     }

     public static int[][] jointToRight(int[][] m1,int[][] m2){
         int[][] m3=new int[m1.length][m1[0].length+m2[0].length];
         for (int i = 0; i < m3.length; i++) {
             int j=0;
             while(j<m1[0].length) {
                 m3[i][j]=m1[i][j];
                 j++;
             }
             while(j<m3[0].length){
                 m3[i][j]=m2[i][j-m1[0].length];
                 j++;
             }

         }
         return m3;
     }

     public static byte[] getBytes(Object obj){
        ObjectOutputStream objOut = null;
        byte[] bytes = null;
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            bytes = out.toByteArray();
            objOut.close();
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                objOut.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return bytes;
     }

     public static Object getObject(byte[] bytes){
        Object obj=null;
         try{
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

         ObjectInputStream ins = new ObjectInputStream(bais);

         obj=ins.readObject();
         }catch (Exception e) {
            e.printStackTrace();
         }
        return obj;
     }

    

     public static void printMatrix(int[][] m){
         for (int i = 0; i < m.length; i++) {
             int[] ts = m[i];
             for (int j = 0; j < ts.length; j++) {
                 System.out.print(ts[j]+" ");

             }
             System.out.println("");
         }
     }

     public static <T> void printMatrix(T[][] m){
         for (int i = 0; i < m.length; i++) {
             T[] ts = m[i];
             for (int j = 0; j < ts.length; j++) {
                 System.out.print(ts[j] + " ");

             }
             System.out.println("");
         }
     }
}
