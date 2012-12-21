/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification;

/**
 *
 * @author frank
 */
public class Classifible<T> {
    private T[] features;
    private Object myClass;
    private Object aditionalFeatures;

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

}
