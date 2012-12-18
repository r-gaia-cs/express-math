/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util;

import DatabaseMathExpressions.DBFuntions;
import DatabaseMathExpressions.ModelExpression;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.graphics.EditableMathExpression;
import br.usp.ime.faguilar.graphics.GMathExpression;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class UtilLabeler {
    public static void labelSymbols(GMathExpression instance, GMathExpression model,int[][]match){
        for (int i = 0; i < match.length; i++) {
            ((EditableMathExpression)instance).setSymbolLabel(match[i][1],model.get(match[i][0]).getLabel());
        }
    }

    public static void resetModelSymbolLabels(){
        DBFuntions dbFunctions=new DBFuntions();
        dbFunctions.openConnection();
        ArrayList<ModelExpression> modelExpressions = dbFunctions.getModelExpressions();
        for (ModelExpression modelExpression : modelExpressions) {
            reinsertSymbolLabels((GMathExpression) modelExpression.getdMathExpression());
            dbFunctions.updateModelByID(modelExpression.getId(), modelExpression);
        }
    }

    public static void reinsertSymbolLabels(GMathExpression expression){
        int index = 0;
        for (DSymbol symbol : expression) {
            ((EditableMathExpression)expression).setSymbolLabel(index,symbol.getLabel());
            index++;
        }
    }
}
