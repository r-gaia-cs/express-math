/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.util;

import br.usp.ime.faguilar.parser.SymbolNode;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frank
 */
public class ListUtil {
    public static List<SymbolNode> concat;
    public static <T> List<T> concat(List<T> l1, List<T> l2){
        List newList = new ArrayList<>();
        newList.addAll(l1);
        newList.addAll(l2);
        return newList;
    }
    
    public static <T> List<T> concat(List<T> l1, T element){
        List newList = new ArrayList<>();
        newList.addAll(l1);
        newList.add(element);
        return newList;
    }
    
    
    public static <T> List<List<T>> generateCombinations(List<T> elements){
        List<List<T>> combinations = new ArrayList<>();
        long numberOfElements = (long) Math.pow(2, elements.size());
        String binaryString;
        List<T> newCombination;
        for (long i = 0; i < numberOfElements; i++) {
            binaryString = Long.toBinaryString(i);
            newCombination = generateCombinationWithBinaryString(binaryString, elements);
            combinations.add(newCombination);
        }        
        return combinations;
    }

    private static <T> List<T> generateCombinationWithBinaryString(String binaryString, List<T> elements) {
        List<T> newCombination = new ArrayList<>();
        for (int i = binaryString.length() - 1; i >= 0; i--) {
            if(binaryString.charAt(i) == '1')
                newCombination.add(elements.get(binaryString.length() - i - 1));
        }
        return newCombination;
    }
    
    public void combinations(
            ArrayList<ArrayList<OrderedStroke>> combinations, OrderedStroke[] otherStrokes) {
//        combinations = new ArrayList<ArrayList<OrderedStroke>>();
        ArrayList<OrderedStroke> currentList = new ArrayList();
        combinations.add(currentList);
        backtrack(combinations, otherStrokes,currentList, 0);
    }
    
    public void backtrack(ArrayList<ArrayList<OrderedStroke>> 
            combinations, OrderedStroke[] otherStrokes, ArrayList<OrderedStroke> currentList,int i) {
        if( i >= otherStrokes.length)
            return;
        int nextPosition = i + 1;
        backtrack(combinations, otherStrokes, currentList, nextPosition);
        ArrayList<OrderedStroke> newList = new ArrayList();
        newList.addAll(currentList);
        newList.add(otherStrokes[i]);
        combinations.add(newList);
        backtrack(combinations, otherStrokes, newList, nextPosition);
    }
    
}
