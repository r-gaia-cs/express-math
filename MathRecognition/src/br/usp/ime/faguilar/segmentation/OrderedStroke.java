/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.graphics.GStroke;
import java.util.List;

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
    
    public static DStroke findStrokeByID(List<DStroke> strokes, int id) {
        DStroke stroke = null;
        for (DStroke aStroke : strokes) {
            if (((OrderedStroke)aStroke).getIndex() == id){
                stroke = aStroke;
                break;
            }
        }
        return stroke;
    }

    @Override
    public int compareTo(Object o) {
        return getIndex() - ((OrderedStroke) o).getIndex();
    }
}
