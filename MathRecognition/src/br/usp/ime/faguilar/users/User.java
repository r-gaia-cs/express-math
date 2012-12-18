/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.users;

import DatabaseMathExpressions.ModelExpression;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class User //extends ArrayList<ModelExpression>
implements Serializable{

    private String nickName;

    private String password;

    private String firstName;

    private String lastName;

    private boolean admin;

    private boolean allowedToInsertUserExpressions;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAllowedToInsertUserExpressions() {
        return allowedToInsertUserExpressions;
    }

    public void setAllowedToInsertUserExpressions(boolean allowedToInsertUserExpressions) {
        this.allowedToInsertUserExpressions = allowedToInsertUserExpressions;
    }

}
