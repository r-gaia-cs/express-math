/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.cost;

/**
 *
 * @author Frank Aguilar
 */
public class FuzziBin {
    private float beginningRadioDistance;
    private float finalRadioDistance;
    private float beginningAngularDisplacement;
    private float finalAngularDisplacement;

    
    
    private float getCenterRadioDistanceWithSlopes(float beginningSlope, 
            float finalSlope){
        
        float beginningOfZoneOne = beginningRadioDistance + 1 / beginningSlope;
        float endOfZoneOne = finalRadioDistance - 1 / finalSlope;
        return beginningOfZoneOne + (endOfZoneOne - 
                beginningOfZoneOne) / 2;
    }
    
    private float getCenterAngularDisplacementWithSlopes(float beginningSlope, 
            float finalSlope){
        float beginningOfZoneOne = beginningAngularDisplacement + 1 / beginningSlope;
        float endOfZoneOne = finalAngularDisplacement - 1 / finalSlope;
        return beginningOfZoneOne + (endOfZoneOne - 
                beginningOfZoneOne) / 2;
    }
      
    public float getBeginningRadioDistance() {
        return beginningRadioDistance;
    }

    public void setBeginningRadioDistance(float beginningRadioDistance) {
        this.beginningRadioDistance = beginningRadioDistance;
    }

    public float getFinalRadioDistance() {
        return finalRadioDistance;
    }

    public void setFinalRadioDistance(float finalRadioDistance) {
        this.finalRadioDistance = finalRadioDistance;
    }

    public float getBeginningAngularDisplacement() {
        return beginningAngularDisplacement;
    }

    public void setBeginningAngularDisplacement(float beginningAngularDisplacement) {
        this.beginningAngularDisplacement = beginningAngularDisplacement;
    }

    public float getFinalAngularDisplacement() {
        return finalAngularDisplacement;
    }

    public void setFinalAngularDisplacement(float finalAngularDisplacement) {
        this.finalAngularDisplacement = finalAngularDisplacement;
    }
    
    
}
