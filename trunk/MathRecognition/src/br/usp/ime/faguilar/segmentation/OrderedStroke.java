/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.graphics.GStroke;

/**
 *
 * @author frank.aguilar
 */
public class OrderedStroke extends GStroke implements Comparable{
    private int index;

    
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int compareTo(Object o) {
        return getIndex() - ((OrderedStroke) o).getIndex();
    }

}
