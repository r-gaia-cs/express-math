/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.export;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frank
 */
public class MathExpression{

    private ArrayList symbols;
    public MathExpression(DMathExpression expression){
        symbols=new ArrayList();
        for (DSymbol symbol : expression) {
            symbols.add(new Symbol(symbol));
        }
    }
}
