/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching.symbol_matching;

/**
 *
 * @author frank
 */
public class UserSymbol {
    private String userNickName;
    private String symbolLabel;

    public static UserSymbol newInstanceFromUserNickNameAndSymbolLabel(String
            userNickName, String symbolLabel){
        UserSymbol userSymbol = new UserSymbol();
        userSymbol.setSymbolLabel(symbolLabel);
        userSymbol.setUserNickName(userNickName);
        return userSymbol;
    }

    @Override
    public boolean equals(Object anotherUserSymbol){
        if((anotherUserSymbol instanceof UserSymbol) &&
                ((UserSymbol)anotherUserSymbol).getSymbolLabel().equals(getSymbolLabel()) &&
                ((UserSymbol)anotherUserSymbol).getUserNickName().equals(getUserNickName()))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.userNickName != null ? this.userNickName.hashCode() : 0);
        hash = 19 * hash + (this.symbolLabel != null ? this.symbolLabel.hashCode() : 0);
        return hash;
    }

    public String getSymbolLabel() {
        return symbolLabel;
    }

    public void setSymbolLabel(String symbolLabel) {
        this.symbolLabel = symbolLabel;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }
}
