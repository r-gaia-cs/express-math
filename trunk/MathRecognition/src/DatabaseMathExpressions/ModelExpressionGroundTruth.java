/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DatabaseMathExpressions;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author frank.aguilar
 */
public class ModelExpressionGroundTruth extends ArrayList<ExpressionLevelGroundTruth>
implements Serializable{
//    private static final long serialVersionUID = 1L;
    @Override
    public String toString() {
        String string="";
        int textualIndex=1;
        for (ExpressionLevelGroundTruth representation:this) {
            string+=(textualIndex+". "+representation);
            textualIndex++;
        }
        return string;
    }
    
    
}
