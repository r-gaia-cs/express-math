/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.util;

import br.usp.ime.faguilar.parser.SymbolNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frank
 */
public class ListUtil {
    public static List<SymbolNode> concat;
    public static <T> List<T> concat(List<T> l1, List<T> l2){
        List newList = new ArrayList<T>();
        newList.addAll(l1);
        newList.addAll(l2);
        return newList;
    }
    
    public static <T> List<T> concat(List<T> l1, T element){
        List newList = new ArrayList<T>();
        newList.addAll(l1);
        newList.add(element);
        return newList;
    }
    
}
