/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DatabaseMathExpressions;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.users.User;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author frank
 */
public class UserExpression
    implements Serializable{

    /**
     * User's identifier
     */
    private User user;

    /**
     * The correspondig model of this user's expression
     */
    private int idModelExpression;

    private String timeofInput;

    private int[][] match;

    private DMathExpression dMExpression;

    private boolean evaluated;

    private String idUser;

    private Timestamp timeStampInput;

    private int id;

    public UserExpression(int id,int idModelExpression, int[][] match,
            DMathExpression dMExpression, boolean evaluated, String idUser,
            Timestamp timeStampInput) {
        this.id=id;
        this.idModelExpression = idModelExpression;
        this.match = match;
        this.dMExpression = dMExpression;
        this.evaluated = evaluated;
        this.idUser = idUser;
        this.timeStampInput = timeStampInput;
    }

    public UserExpression() {
        this.id=-1;
        this.idModelExpression = -1;
        this.match = null;
        this.dMExpression = null;
        this.evaluated = false;
        this.idUser = "unknown";
        this.timeStampInput = null;
    }


    public Timestamp getTimeStampInput() {
        return timeStampInput;
    }

    public void setTimeStampInput(Timestamp date) {
        this.timeStampInput = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public UserExpression(User user, int idModelExpression,
            String timeofInput, int[][] match) {
        this.user = user;
        this.idModelExpression = idModelExpression;
        this.timeofInput = timeofInput;
        this.match = match;
        dMExpression=null;
        this.evaluated=false;
    }

    public int getIdModelExpression() {
        return idModelExpression;
    }

    public void setIdModelExpression(int idModelExpression) {
        this.idModelExpression = idModelExpression;
    }
    public String getTimeofInput() {
        return timeofInput;
    }

    public void setTimeofInput(String timeofInput) {
        this.timeofInput = timeofInput;
    }

    public int[][] getMatch() {
        return match;
    }

    public void setMatch(int[][] match) {
        this.match = match;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DMathExpression getdMExpression() {
        return dMExpression;
    }

    public void setdMExpression(DMathExpression dMExpression) {
        this.dMExpression = dMExpression;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    @Override
    public String toString(){
        return this.idUser+"_"+this.getId();
    }
}
