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
public class FuzzyShapeContextRegion {
//    private float beginningRadioSlope;
//    private float finalRadioSlope;
//    private float fractionEndAtNextRadialBin;
//    private float fractionEndAtPreviousRadialBin;
//    
//    private float beginningAngularSlope;
//    private float finalAngularSlope;
//    private float fractionEndAtNextAngularBin;
//    private float fractionEndAtPreviousAngularBin;
    
//    private int numberOAngularRegions;
//    private int numberOFRadialRegions;
    
    private float[] anglularRegions;
    private float[] radialRegions;
    
    private float minDistFromLimit;
    private float minAngleDiferenceFromLimit;
    
    public FuzzyShapeContextRegion() {
//        beginningRadioSlope = 
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
    
    public void updateBinAt(int position, double[][] shapeContext, double dist, 
            double angle){
        
    }

//    public float getBeginningRadioSlope() {
//        return beginningRadioSlope;
//    }
//
//    public void setBeginningRadioSlope(float beginningRadioSlope) {
//        this.beginningRadioSlope = beginningRadioSlope;
//    }
//
//    public float getFinalRadioSlope() {
//        return finalRadioSlope;
//    }
//
//    public void setFinalRadioSlope(float finalRadioSlope) {
//        this.finalRadioSlope = finalRadioSlope;
//    }
//
//    public float getFractionEndAtNextRadialBin() {
//        return fractionEndAtNextRadialBin;
//    }
//
//    public void setFractionEndAtNextRadialBin(float fractionEndAtNextRadialBin) {
//        this.fractionEndAtNextRadialBin = fractionEndAtNextRadialBin;
//    }
//
//    public float getFractionEndAtPreviousRadialBin() {
//        return fractionEndAtPreviousRadialBin;
//    }
//
//    public void setFractionEndAtPreviousRadialBin(float fractionEndAtPreviousRadialBin) {
//        this.fractionEndAtPreviousRadialBin = fractionEndAtPreviousRadialBin;
//    }
//
//    public float getBeginningAngularSlope() {
//        return beginningAngularSlope;
//    }
//
//    public void setBeginningAngularSlope(float beginningAngularSlope) {
//        this.beginningAngularSlope = beginningAngularSlope;
//    }
//
//    public float getFinalAngularSlope() {
//        return finalAngularSlope;
//    }
//
//    public void setFinalAngularSlope(float finalAngularSlope) {
//        this.finalAngularSlope = finalAngularSlope;
//    }
//
//    public float getFractionEndAtNextAngularBin() {
//        return fractionEndAtNextAngularBin;
//    }
//
//    public void setFractionEndAtNextAngularBin(float fractionEndAtNextAngularBin) {
//        this.fractionEndAtNextAngularBin = fractionEndAtNextAngularBin;
//    }
//
//    public float getFractionEndAtPreviousAngularBin() {
//        return fractionEndAtPreviousAngularBin;
//    }
//
//    public void setFractionEndAtPreviousAngularBin(float fractionEndAtPreviousAngularBin) {
//        this.fractionEndAtPreviousAngularBin = fractionEndAtPreviousAngularBin;
//    }
//
//    public int getNumberOAngularRegions() {
//        return numberOAngularRegions;
//    }
//
//    public void setNumberOAngularRegions(int numberOAngularRegions) {
//        this.numberOAngularRegions = numberOAngularRegions;
//    }

    
    public float[] calculateAngularRegions(int tot_t, boolean rotation) {
        anglularRegions = new float[tot_t]; // pi/6, pi/3, pi/2, 2pi/3, 5pi/6, pi, ..., 2pi
        for (int i = 0; i < tot_t; i++){
            anglularRegions[i] = ((2.0f*(float)Math.PI)/(float)tot_t) * (float) (i+1);//(2.0f*(float)Math.PI /(float)tot_t) * (float) (i+1);
            if(rotation){
                anglularRegions[i] = anglularRegions[i] - (float)Math.PI / 4.0f;
            }
        }
        minAngleDiferenceFromLimit = (anglularRegions[1] - anglularRegions[0]) / 2;
        return anglularRegions;
    }

    public float[] calculateRadialRegions(int tot_r, float raioSC) {
        float base = 2.0f;
        float max = (float)Math.pow(base,tot_r);
        float multiplica = raioSC/max;
        this.radialRegions = new float[tot_r];
        for (int i = 0; i < tot_r; i++)
            radialRegions[i] = (float)Math.pow(2.0f,i+1) * multiplica;
        minDistFromLimit = radialRegions[0] / 2.0f;
        return radialRegions;
    }

    public void updateBinWithConstantSlope(double[] scOfAPoint, float raio, 
            float theta, boolean rotation) {
        //tenho raio e theta, agora localiza 'bin' por angulo e raio
        int id_r=0, id_t=0;
        for (int ii = 0; ii < radialRegions.length; ii++){
            if (raio <= radialRegions[ii]) {
                id_r = ii;
                break;
            }
        }
        for (int jj = 0; jj < anglularRegions.length; jj++){
            if (theta <= anglularRegions[jj]) {
                id_t = jj;
                break;
            }
        }
        if (rotation && theta > anglularRegions[anglularRegions.length - 1])
            id_t = 0;
        float dist = radialRegions[id_r] - raio;
        float  radioUpdate = 1;
        float angularUpdate = 1;
        int radialPosToUpdate = -1;
        if(id_r < radialRegions.length - 1 && dist < minDistFromLimit){
            radialPosToUpdate = id_r + 1;
            radioUpdate = (dist + minDistFromLimit) / (2 * minDistFromLimit);
        }
        if(id_r > 0){
            dist = raio - radialRegions[id_r - 1];
            if(dist < minDistFromLimit){
                radialPosToUpdate = id_r - 1;
                radioUpdate = (dist + minDistFromLimit) / (2 * minDistFromLimit);
            }
        }
        int angularPosToUpdate = -1;
        dist = anglularRegions[id_t] - theta;
        if( dist < minAngleDiferenceFromLimit){ //id_t < anglularRegions.length - 1 &&
            angularPosToUpdate = id_t + 1;
            if(angularPosToUpdate >= anglularRegions.length)
                angularPosToUpdate = 0;
            angularUpdate = (dist + minAngleDiferenceFromLimit) / 
                    (2 * minAngleDiferenceFromLimit);
        }
//        if(id_t > 0){
        int previous = id_t - 1;
        if(previous < 0)
            dist = theta;
        else
            dist = theta - anglularRegions[previous];
        if(dist < minAngleDiferenceFromLimit){
            angularPosToUpdate = id_t - 1;
            if(angularPosToUpdate < 0)
                angularPosToUpdate = anglularRegions.length - 1;
            angularUpdate = (dist + minAngleDiferenceFromLimit) / 
                    (2 * minAngleDiferenceFromLimit);
        }
//        }
        updateBin(id_r, id_t, scOfAPoint, radioUpdate, angularUpdate);
        if(radialPosToUpdate >= 0)
            updateBin(radialPosToUpdate, id_t, scOfAPoint, 1 - radioUpdate, angularUpdate);
        if(angularPosToUpdate >= 0)
            updateBin(id_r, angularPosToUpdate, scOfAPoint, radioUpdate, 1 - angularUpdate);
        if(angularPosToUpdate >= 0&& radialPosToUpdate >= 0)
            updateBin(radialPosToUpdate, angularPosToUpdate, scOfAPoint, 1 - radioUpdate, 
                    1 - angularUpdate);
//        updateBinAt(id_r, id_t, scOfAPoint, raio, theta);
//        float midPointRadial = radialRegions[id_r];
//        if(id_r > 0){
//            midPointRadial += radialRegions[id_r - 1];
//        }
//        midPointRadial /= 2;
//        if(id_r > 0 && midPointRadial > raio){
//            updateBinAt(id_r - 1, id_t, scOfAPoint, raio, theta);
//        }
//        if(id_r < radialRegions.length - 1 && midPointRadial < raio){
//            updateBinAt(id_r + 1, id_t, scOfAPoint, raio, theta);
//        }
//        float midPointAngular = anglularRegions[id_t];
//        if(id_t > 0)
//            midPointAngular += radialRegions[id_t - 1];
//        midPointAngular /= 2;
//        if(id_t > 0 && midPointAngular > theta){
//            updateBinAt(id_r, id_t - 1, scOfAPoint, raio, theta);
//        }
//        if(id_t < anglularRegions.length - 1 && midPointAngular < theta){
//            updateBinAt(id_r, id_t + 1, scOfAPoint, raio, theta);
//        }
    }
//    private void updateBinAt(int id_r, int id_t, double[] scOfAPoint, float raio, float theta) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }    

    private void updateBin(int id_r, int id_t, double[] scOfAPoint, float radioUpdate, float angularUpdate) {
        int pos = id_t * radialRegions.length + id_r;
        scOfAPoint[pos] += radioUpdate * angularUpdate;
    }
}
