/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.export;

import DatabaseMathExpressions.ModelExpressionGroundTruth;
import br.usp.ime.faguilar.data.DMathExpression;

/**
 *
 * @author frank
 */
public class MathExpressionSample implements Comparable{
    private String expressionClass;
    private String writer;
    private String category;
    private DMathExpression mathExpression;
    private ModelExpressionGroundTruth textualRepresentation;
    private int ID;
    
    public MathExpressionSample(String model, String user, DMathExpression expression) {
        this.expressionClass = model;
        this.writer = user;
        this.mathExpression = expression;
    }

    public DMathExpression getMathExpression() {
        return mathExpression;
    }

    public void setMathExpression(DMathExpression mathExpression) {
        this.mathExpression = mathExpression;
    }

    public String getModel() {
        return expressionClass;
    }

    public void setModel(String model) {
        this.expressionClass = model;
    }

    public String getUser() {
        return writer;
    }

    public void setUser(String user) {
        this.writer = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpressionClass() {
        return expressionClass;
    }

    public void setExpressionClass(String expressionClass) {
        this.expressionClass = expressionClass;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public ModelExpressionGroundTruth getTextualRepresentation() {
        return textualRepresentation;
    }

    public void setTextualRepresentation(ModelExpressionGroundTruth textualRepresentation) {
        this.textualRepresentation = textualRepresentation;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int compareTo(Object otherMathExpressionSample) {
        MathExpressionSample mExpressionSample = (MathExpressionSample) 
                otherMathExpressionSample;
        return (this.getUser().compareTo(mExpressionSample.getUser()));
    }


}
