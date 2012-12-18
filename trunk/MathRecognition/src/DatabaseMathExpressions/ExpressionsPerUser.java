/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DatabaseMathExpressions;

import br.usp.ime.faguilar.users.User;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class ExpressionsPerUser implements Serializable{
    /**
     * Expressions written by 'user'
     */
    private ArrayList<UserExpression> userExpressions;
    /**
     * User that wrote the expressions 'userExpressions'
     */
    private User user;

    public ExpressionsPerUser(ArrayList<UserExpression> ue, User user) {
        this.userExpressions = ue;
        this.user = user;
    }

    public ArrayList<UserExpression> getUe() {
        return userExpressions;
    }

    public void setUe(ArrayList<UserExpression> ue) {
        this.userExpressions = ue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
