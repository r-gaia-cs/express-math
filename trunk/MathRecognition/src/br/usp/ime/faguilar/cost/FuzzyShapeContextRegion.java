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
public class FuzzyShapeContextRegion extends ShapeContextRegion{
    private float minDistFromLimit;
    private float minAngleDiferenceFromLimit;
    
    public FuzzyShapeContextRegion() {
//        beginningRadioSlope = 
    }

    
    public void updateBinAt(int position, double[][] shapeContext, double dist, 
            double angle){
    }
 
    @Override
    public float[] calculateAngularRegions(int tot_t, boolean rotation) {
        super.calculateAngularRegions(tot_t, rotation);
        minAngleDiferenceFromLimit = (anglularRegions[1] - anglularRegions[0]) / 2;
        return anglularRegions;
    }

    @Override
    public float[] calculateRadialRegions(int tot_r, float raioSC) {
        super.calculateRadialRegions(tot_r, raioSC);
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
        if(angularPosToUpdate >= 0 && radialPosToUpdate >= 0)
            updateBin(radialPosToUpdate, angularPosToUpdate, scOfAPoint, 1 - radioUpdate, 
                    1 - angularUpdate);
    }  

    private void updateBin(int id_r, int id_t, double[] scOfAPoint, float radioUpdate, float angularUpdate) {
        int pos = id_t * radialRegions.length + id_r;
        scOfAPoint[pos] += radioUpdate * angularUpdate;
    }
}
