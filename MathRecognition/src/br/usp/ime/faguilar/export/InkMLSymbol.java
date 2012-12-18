/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.export;

import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.TimePoint;

/**
 *
 * @author frank
 */
public class InkMLSymbol {
    private DSymbol dSymbol;
    private String inkmlText;
    private String userNickName;
    private String model;

    private int maxID;

    private final static String JUMP_LINE = "\n";
    private final static String TAB = "\t";
    public static final String INKML_NAMESPACE = "http://www.w3.org/2003/InkML";

    public void initializeInkML(){
        inkmlText = "<ink xmlns=\""+ INKML_NAMESPACE+ "\">" + JUMP_LINE;
    }

    public void initializeMaxID(){
        setMaxID(-1);
    }

    public void generateInkML(){
        initializeMaxID();
        initializeInkML();
        generateTraceFormat();
        generateAnnotations();
        generateTraces();
        insertLastMarkup();
    }

    private void generateTraceFormat(){
        inkmlText += "<traceFormat>" + JUMP_LINE;
        inkmlText += "<channel name=\"X\" type=\"decimal\"/>" + JUMP_LINE;
        inkmlText += "<channel name=\"Y\" type=\"decimal\"/>" + JUMP_LINE;
        inkmlText += "<timestamp xml:id=\"ts001\" time=\"0\"/>" +
                JUMP_LINE;
        inkmlText += "<channel name=\"T\" type=\"integer\" units=\"ms\" "
                + "respectTo=\"#ts001\"/>" + JUMP_LINE;
        inkmlText += "<traceFormat/>" + JUMP_LINE;
    }

    private void generateAnnotations(){
        inkmlText += "<annotation type=\"writer\">"+getUserNickName()+
                "</annotation>" + JUMP_LINE;
        inkmlText += "<annotation type=\"label\">"+getdSymbol().getLabel()+
                "</annotation>" + JUMP_LINE;
        inkmlText += "<annotation type=\"href-expression\">"+getdSymbol().getLabel()
                +"_"+getdSymbol().getId()+
                "</annotation>" + JUMP_LINE;
        inkmlText += "<annotation type=\"expression\">"+getModel()+
                "</annotation>" + JUMP_LINE;
    }

    private void generateTraces(){
        for (DStroke dStroke : getdSymbol()) {
            incrementMaxID();
            inkmlText += "<trace id=\""+getMaxID()+"\">"+JUMP_LINE;
            for (TimePoint timePoint : dStroke)
                inkmlText += timePoint.toString()+", ";
            if(!dStroke.isEmpty())
                inkmlText = inkmlText.substring(0, inkmlText.length()-2);
            inkmlText += JUMP_LINE + "</trace>"+JUMP_LINE;
        }
    }

    private void insertLastMarkup(){
        inkmlText += "</ink>" + JUMP_LINE;
    }

    private void incrementMaxID(){
        setMaxID(getMaxID()+1);
    }
    public String getInkmlText() {
        return inkmlText;
    }

    public void setInkmlText(String inkmlText) {
        this.inkmlText = inkmlText;
    }

    public int getMaxID() {
        return maxID;
    }

    public void setMaxID(int maxID) {
        this.maxID = maxID;
    }

    public DSymbol getdSymbol() {
        return dSymbol;
    }

    public void setdSymbol(DSymbol dSymbol) {
        this.dSymbol = dSymbol;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

}
