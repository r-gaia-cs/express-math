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
public class IndexedValue implements Comparable{
    private int index;
    private double value;

    public static IndexedValue createIndexedValueFromIndexAndValue(int 
            index, double value){
        IndexedValue indexedValue = new IndexedValue();
        indexedValue.setIndex(index);
        indexedValue.setValue(value);
        return indexedValue;
    }
    
    @Override
    public int compareTo(Object other) {
        IndexedValue otherValueIndex = (IndexedValue) other;
        return Double.compare(getValue(), otherValueIndex.getValue());
        
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    
}
