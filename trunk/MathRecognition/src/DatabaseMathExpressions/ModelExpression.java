/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DatabaseMathExpressions;

import br.usp.ime.faguilar.data.DMathExpression;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 *
 * @author frank
 */
public class ModelExpression 
    implements Serializable{

    private static final long serialVersionUID = 1L;
    //private int idDatabase;

    /**
     * identifier of this model expression
     */
    private int id;

    /**
     * Textual representation of this model
     */
    private ModelExpressionGroundTruth textualRepresentation;

    private ArrayList<UserExpression> alUserExpression;

    private DMathExpression dMathExpression;
    
    private String categoryName;

    public ModelExpression(int id, ModelExpressionGroundTruth textualRepresentation,
            DMathExpression dMathExpression) {
        this.id = id;
        this.textualRepresentation = textualRepresentation;
        this.dMathExpression = dMathExpression;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String category) {
        this.categoryName = category;
    }
    
    public ModelExpression(int id) {
        this.id = id;
        alUserExpression=new ArrayList<UserExpression>();
    }

    public ArrayList<UserExpression> getAlUserExpression() {
        return alUserExpression;
    }

    public void setAlUserExpression(ArrayList<UserExpression> alUserExpression) {
        this.alUserExpression = alUserExpression;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ModelExpressionGroundTruth getTextualRepresentation() {
        return textualRepresentation;
    }

    public void setTextualRepresentation(ModelExpressionGroundTruth textualRepresentation) {
        this.textualRepresentation = textualRepresentation;
    }

    public DMathExpression getdMathExpression() {
        return dMathExpression;
    }

    public void setdMathExpression(DMathExpression dMathExpression) {
        this.dMathExpression = dMathExpression;
    }

    public boolean addUserExpression(UserExpression ue){
        return this.getAlUserExpression().add(ue);
    }

    public int getNumberOfCheckedExpressions(){
        int n=0;
        for (UserExpression userExpression : alUserExpression) {
            if(userExpression.isEvaluated())
                n++;
        }
        return n;
    }
}
