/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class MatchingResult implements Serializable{
    private String idModel;
    private String writerOfModel;

    private String idInstance;
    private String writerOfInstance;

    private int numberOfSymbols;

    private ArrayList<IncorrectMatching> wrongMatchings;


    public int getNumberOfWrongMatchins;

    public int getNumberOfWrongMatchings(){
        return wrongMatchings.size();
    }

    public String getIdInstance() {
        return idInstance;
    }

    public int getNumberOfSymbols() {
        return numberOfSymbols;
    }

    public void setNumberOfSymbols(int numberOfSymbols) {
        this.numberOfSymbols = numberOfSymbols;
    }


    public void setIdInstance(String idInstance) {
        this.idInstance = idInstance;
    }

    public String getIdModel() {
        return idModel;
    }

    public void setIdModel(String idModel) {
        this.idModel = idModel;
    }

    public String getWriterOfInstance() {
        return writerOfInstance;
    }

    public void setWriterOfInstance(String writerOfInstance) {
        this.writerOfInstance = writerOfInstance;
    }

    public String getWriterOfModel() {
        return writerOfModel;
    }

    public void setWriterOfModel(String writerOfModel) {
        this.writerOfModel = writerOfModel;
    }

    public ArrayList<IncorrectMatching> getWrongMatchings() {
        return wrongMatchings;
    }

    public void setWrongMatchings(ArrayList<IncorrectMatching> wrongtMatchings) {
        this.wrongMatchings = wrongtMatchings;
    }
}
