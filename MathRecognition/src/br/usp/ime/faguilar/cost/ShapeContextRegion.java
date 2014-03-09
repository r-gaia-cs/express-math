/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.cost;

/**
 *
 * @author Frank
 */
public class ShapeContextRegion {
    protected float[] anglularRegions;
    protected float[] radialRegions;
    
    public float[] calculateAngularRegions(int tot_t, boolean rotation) {
        anglularRegions = new float[tot_t]; 
        for (int i = 0; i < tot_t; i++){
            anglularRegions[i] = ((2.0f*(float)Math.PI)/(float)tot_t) * (float) (i+1);
            if(rotation){
                anglularRegions[i] = anglularRegions[i] - (float)Math.PI / 4.0f;
            }
        }
        return anglularRegions;
    }

    public float[] calculateRadialRegions(int tot_r, float raioSC) {
        float base = 2.0f;
        float max = (float)Math.pow(base,tot_r);
        float multiplica = raioSC/max;
        this.radialRegions = new float[tot_r];
        for (int i = 0; i < tot_r; i++)
            radialRegions[i] = (float)Math.pow(2.0f,i+1) * multiplica;
        return radialRegions;
    }
    
    public float[] getAnglularRegions() {
        return anglularRegions;
    }

    public void setAnglularRegions(float[] anglularRegions) {
        this.anglularRegions = anglularRegions;
    }

    public float[] getRadialRegions() {
        return radialRegions;
    }

    public void setRadialRegions(float[] radialRegions) {
        this.radialRegions = radialRegions;
    }
}
