/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DatabaseMathExpressions;

import java.io.Serializable;

/**
 *
 * @author frank
 */
public class ExpressionLevelGroundTruth implements Serializable{

    private static final long serialVersionUID = 1L;

    private boolean isContentMathML;
    private String groundTruth;

    public ExpressionLevelGroundTruth() {
        setGroundTruth("");
        setContentMathML(false);
    }

    public boolean isContentMathML() {
        return isContentMathML;
    }

    public void setContentMathML(boolean isContentMathML) {
        this.isContentMathML = isContentMathML;
    }

    public String getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(String truth) {
        this.groundTruth = truth;
    }
}
