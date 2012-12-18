/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DatabaseMathExpressions;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class ExportableAndImportableDatabase implements Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<Category> categories;
    private ArrayList<ModelExpression> modelExpressions;
    private ArrayList<ExpressionsPerUser> expressionsPerUsers;

    public ExportableAndImportableDatabase() {
        categories = new ArrayList<Category>();
        modelExpressions = new ArrayList<ModelExpression>();
        expressionsPerUsers = new ArrayList<ExpressionsPerUser>();
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public ArrayList<ExpressionsPerUser> getExpressionsPerUsers() {
        return expressionsPerUsers;
    }

    public void setExpressionsPerUsers(ArrayList<ExpressionsPerUser> expressionsPerUsers) {
        this.expressionsPerUsers = expressionsPerUsers;
    }

    public ArrayList<ModelExpression> getModelExpressions() {
        return modelExpressions;
    }

    public void setModelExpressions(ArrayList<ModelExpression> modelExpressions) {
        this.modelExpressions = modelExpressions;
    }
}
