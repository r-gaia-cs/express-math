/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.export;

import DatabaseMathExpressions.ExpressionLevelGroundTruth;
import DatabaseMathExpressions.ModelExpressionGroundTruth;
import DatabaseMathExpressions.UserExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.users.User;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class InkMLExpression {
//    private User user;
//    private UserExpression userExpression;
//    private String Category;
    private MathExpressionSample sampleExpression;
//    private ArrayList<String> groundTruthExpression;
    private ModelExpressionGroundTruth groundTruthExpression;

    private String inkmlText;

    private int maxID;

    private final static String JUMP_LINE = "\n";
    private final static String TAB = "\t";
    public static final String INKML_NAMESPACE = "http://www.w3.org/2003/InkML";

    protected boolean timeStampIncluded;
    public InkMLExpression() {
//        inkmlText = INKML_NAMESPACE;
        timeStampIncluded = true;
    }

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
//        generateGroundTruthExpression();
        generateTraces();
        generateSymbolTraceGroups();
        insertLastMarkup();
    }

    private void generateTraceFormat(){
        inkmlText += "<traceFormat>" + JUMP_LINE;
        inkmlText += "<channel name=\"X\" type=\"decimal\"/>" + JUMP_LINE;
        inkmlText += "<channel name=\"Y\" type=\"decimal\"/>" + JUMP_LINE;
        if(isTimeStampIncluded()){
            inkmlText += "<timestamp xml:id=\"ts001\" time=\"0\"/>" +
                    JUMP_LINE;
            inkmlText += "<channel name=\"T\" type=\"integer\" units=\"ms\" "
                    + "respectTo=\"#ts001\"/>" + JUMP_LINE;
        }
        inkmlText += "</traceFormat>" + JUMP_LINE;
    }

    private void generateAnnotations(){
//        inkmlText += "<annotation type=\"writer\">"+user.getNickName()+
//                "</annotation>" + JUMP_LINE;
        inkmlText += "<annotation type=\"expression\">"+sampleExpression.getModel()+
                "</annotation>" + JUMP_LINE;
        inkmlText += "<annotation type=\"writer\">"+sampleExpression.getUser()+
                "</annotation>" + JUMP_LINE;
        inkmlText += "<annotation type=\"category\">"+sampleExpression.getCategory()+
                "</annotation>" + JUMP_LINE;
    }

    private void generateGroundTruthExpression(){
        String[] lines = null;
        for (ExpressionLevelGroundTruth expressionGroundTruth : groundTruthExpression) {
            if(expressionGroundTruth.isContentMathML()){
                inkmlText += "<annotationXML type=\"truth\" "
                    + "encoding=\"Content-MathML\">"+JUMP_LINE;
//                inkmlText += TAB + "<math xmlns='http://www.w3.org/1998/Math/MathML'>"
//                                + JUMP_LINE;
                String stringGroundTruth = expressionGroundTruth.getGroundTruth();
                lines = stringGroundTruth.split(JUMP_LINE);
                for (int i = 0; i < lines.length; i++) //{
                    inkmlText += TAB+ lines[i] + JUMP_LINE;
    //            }
    //            inkmlText += groundTruth;
//                inkmlText += TAB+"</math>"+JUMP_LINE;
                inkmlText += "</annotationXML>"+JUMP_LINE;
            }
             else{
                inkmlText += "<annotation type=\"truth\">" +
                        expressionGroundTruth.getGroundTruth() + "</annotation>"
                        + JUMP_LINE;
             }
        }
    }

    private void generateTraces(){
        for (DSymbol dSymbol : sampleExpression.getMathExpression()) {
            for (DStroke dStroke : dSymbol) {
                incrementMaxID();
                inkmlText += "<trace id=\""+getMaxID()+"\">"+JUMP_LINE;
                if(isTimeStampIncluded()){
                    for (TimePoint timePoint : dStroke)
                        inkmlText += timePoint.toString()+", ";
                }else{
                    for (TimePoint timePoint : dStroke)
                        inkmlText += (timePoint.getX() + " " +
                            timePoint.getY() + ", ");
                }

                if(!dStroke.isEmpty())
                    inkmlText = inkmlText.substring(0, inkmlText.length()-2);
                inkmlText += JUMP_LINE + "</trace>"+JUMP_LINE;
            }
        }
    }

    private void generateSymbolTraceGroups(){
        incrementMaxID();
        inkmlText += "<traceGroup xml:id=\""+getMaxID()+"\">" + JUMP_LINE;
        inkmlText += TAB + "<annotation type=\"truth\">From ITF</annotation>"+
                JUMP_LINE;
        int idStroke = -1;
        int contStrokes = 0;
//        for (DSymbol dSymbol : userExpression.getdMExpression()) {
        for (DSymbol dSymbol : sampleExpression.getMathExpression()) {
            incrementMaxID();
            inkmlText += TAB+"<traceGroup xml:id=\""+getMaxID()+"\">"+JUMP_LINE;
            inkmlText += TAB+TAB+"<annotation type=\"truth\">"+dSymbol.getLabel()
                    +"</annotation>"+JUMP_LINE;
            for (contStrokes = 0; contStrokes < dSymbol.size(); contStrokes++) {
                idStroke++;
                inkmlText += TAB+TAB+"<traceView traceDataRef=\""+idStroke+"\"/>" +
                        JUMP_LINE;
            }
            inkmlText += TAB+TAB+"<annotationXML href=\""+dSymbol.getLabel()+
                    "_"+dSymbol.getId()
                    +"\"/>"+JUMP_LINE;
            inkmlText += TAB+"</traceGroup>"+JUMP_LINE;
        }
        inkmlText += "</traceGroup>"+JUMP_LINE;
    }

    private void insertLastMarkup(){
        inkmlText += "</ink>" + JUMP_LINE;
    }

    private void incrementMaxID(){
        setMaxID(getMaxID()+1);
    }

//    public String getCategory() {
//        return Category;
//    }
//
//    public void setCategory(String Category) {
//        this.Category = Category;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

//    public UserExpression getUserExpression() {
//        return userExpression;
//    }
//
//    public void setUserExpression(UserExpression userExpression) {
//        this.userExpression = userExpression;
//    }

    public ModelExpressionGroundTruth getGroundTruthExpression() {
        return groundTruthExpression;
    }

    public void setGroundTruthExpression(ModelExpressionGroundTruth groundTruthExpression) {
        this.groundTruthExpression = groundTruthExpression;
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

    public MathExpressionSample getSampleExpression() {
        return sampleExpression;
    }

    public void setSampleExpression(MathExpressionSample sampleExpression) {
        this.sampleExpression = sampleExpression;
    }

    public boolean isTimeStampIncluded() {
        return timeStampIncluded;
    }

    public void setTimeStampIncluded(boolean timeStampIncluded) {
        this.timeStampIncluded = timeStampIncluded;
    }

}
