/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.matching.symbol_matching.UserSymbol;

/**
 *
 * @author frank
 */
public class Classifible<T> {
    private T[] features;
    private Object myClass;
    private Object aditionalFeatures;
    private DSymbol symbol;
    private UserSymbol userSymbol;

    public Classifible(T[] features){
        Classifible classifible = new Classifible();
        classifible.setFeatures(features);
    }

    @Override
    public String toString(){
        String string = "Class: " + (String)myClass + " data: ";
        for (T object : features) {
            string+= ((String) object.toString());
        }
        return string;
    }


    public Classifible(){
        features = null;
        myClass = null;
    }
    public T[] getFeatures() {
        return features;
    }

    public void setFeatures(T[] features) {
        this.features = features;
    }

    public Object getMyClass() {
        return myClass;
    }

    public void setMyClass(Object myClass) {
        this.myClass = myClass;
    }

    public Object getAditionalFeatures() {
        return aditionalFeatures;
    }

    public void setAditionalFeatures(Object aditionalFeatures) {
        this.aditionalFeatures = aditionalFeatures;
    }

    public DSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(DSymbol symbol) {
        this.symbol = symbol;
    }

    public UserSymbol getUserSymbol() {
        return userSymbol;
    }

    public void setUserSymbol(UserSymbol userSymbol) {
        this.userSymbol = userSymbol;
    }
}
