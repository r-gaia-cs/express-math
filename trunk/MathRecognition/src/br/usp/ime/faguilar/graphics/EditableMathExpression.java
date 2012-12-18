/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.graphics;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.TimePoint;
import java.awt.Graphics;
import java.awt.geom.Point2D;

/**
 *
 * @author frank
 */
public class EditableMathExpression extends GMathExpression{
    /**
     * Stroke that user is currently writting
     */
    private GStroke currentStroke;
    /**
     * Symbol, that user is currently writing
     */
    private GSymbol currentSymbol;
    
    static final long serialVersionUID = 6584743360818320887L;


    public EditableMathExpression() {
        super();
        currentStroke=new GStroke();
        currentSymbol=new GSymbol();
    }

    public void setSymbolLabel(int symbolPosition,String label){
        int maxID = 0;
        for (DSymbol dSymbol : this) {
            if(dSymbol.getLabel().equals(label)){
                int idSymbol = dSymbol.getId();
                if(idSymbol > maxID)
                    maxID = idSymbol;
            }
        }
        maxID++;
        get(symbolPosition).setId(maxID);
        get(symbolPosition).setLabel(label);
    }

    public void resetFields(){
        resetNewStroke();
        resetNewSymgol();
    }

    public void resetNewStroke(){
        currentStroke=new GStroke();
    }

    public void resetNewSymgol(){
        currentSymbol=new GSymbol();
    }
    public void includeCurrentSymbol(){
        addCheckingBoundingBox((GSymbol)currentSymbol.clone());
        resetNewSymgol();
    }
    public void includeCurrentStroke(){
        currentSymbol.addCheckingBoundingBox((GStroke)currentStroke.clone());
        resetNewStroke();
    }

    public void addPointToCurrentStroke(TimePoint point){
        this.currentStroke.addCheckingBoundingBox(point);
    }

    public boolean isCurrentSymbolEmty(){
        return currentSymbol.isEmpty();
    }

    public void resetCurrentAttributes(){
        resetNewStroke();
        resetNewSymgol();
    }
    
    @Override
    public void draw(Graphics g){
        if (!currentStroke.isEmpty())
           currentStroke.drawStroke(g);
        if (!currentSymbol.isEmpty())
            currentSymbol.drawSymbol(g);
        if(!isEmpty())
            drawGMathExpression(g);
    }

    @Override
    public Object clone(){
        EditableMathExpression obj=null;
        try{
            obj=(EditableMathExpression)super.clone();
        }catch(Exception ex){
            System.out.println(" error in clone function of "+this.getClass());
        }
        obj.setCurrentStroke((GStroke) currentStroke.clone());
        obj.setCurrentSymbol((GSymbol) currentSymbol.clone());
        return obj;
    }

    /**
     * Returns a copy of this math expression, without the element
     * at position especified by the parameter pos
     * @param Position of element that will not be copied
     * @return A copy of this math expression, without the element at
     * position pos.
     */
    @Override
    public GMathExpression getACopyWithout(int pos){
        EditableMathExpression gMathExpression=new EditableMathExpression();
        for (int i = 0; i < this.size(); i++) {
            if(i!= pos){
                gMathExpression.addCheckingBoundingBox(this.get(i));
            }
        }
        gMathExpression.setCurrentStroke((GStroke) currentStroke.clone());
        gMathExpression.setCurrentSymbol((GSymbol) currentSymbol.clone());
        return gMathExpression;
    }

    public Point2D getTotalRightBottom(){
        Point2D point=this.getRbPoint();
        double x,y;
        x=point.getX();
        y=point.getY();
        if(!currentSymbol.isEmpty()){
            if(currentSymbol.getRbPoint().getX()>x)
                x=currentSymbol.getRbPoint().getX();
            if(currentSymbol.getRbPoint().getY()>y)
                y=currentSymbol.getRbPoint().getY();
        }
        if(!currentStroke.isEmpty()){
            if(currentStroke.getRbPoint().getX()>x)
                x=currentStroke.getRbPoint().getX();
            if(currentStroke.getRbPoint().getY()>y)
                y=currentStroke.getRbPoint().getY();
        }
        point.setLocation(x, y);
        return point;
    }
    public GStroke getCurrentStroke() {
        return currentStroke;
    }

    public void setCurrentStroke(GStroke currentStroke) {
        this.currentStroke = currentStroke;
    }

    public GSymbol getCurrentSymbol() {
        return currentSymbol;
    }

    public void setCurrentSymbol(GSymbol currentSymbol) {
        this.currentSymbol = currentSymbol;
    }
}
