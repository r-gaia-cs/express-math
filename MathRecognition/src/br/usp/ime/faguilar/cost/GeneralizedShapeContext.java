/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.cost;

import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;
import br.usp.ime.faguilar.graphics.GMathExpression;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author frank
 */
public class GeneralizedShapeContext extends ShapeContext implements Serializable{
    static final long serialVersionUID = -8563085518696891422L;
    private ShapeContextVector[][] shapeContextVectors;
    private ShapeContextVector[] centerShapeContextVector;
    public GeneralizedShapeContext(float raioSC, Graph graph, int tot_r, int tot_t, 
            boolean rotation, GMathExpression mathExpression) {
        super();
        this.numberOfRadialBins = tot_r;
        this.numberOfAngularBins = tot_t;
//        gmathExpression=mathExpression;
        this.init(raioSC, graph, rotation);
    }
    
    @Override
    protected void init(float raioSC, Graph graph, boolean rotation) {

        this.radio = raioSC;
        int totSC = numberOfAngularBins * numberOfRadialBins;
        Vertex [] vertexList = graph.getIndexedVertexes();

        float[] vt = new float[numberOfAngularBins]; // pi/6, pi/3, pi/2, 2pi/3, 5pi/6, pi, ..., 2pi
        for (int i = 0; i < numberOfAngularBins; i++){
            vt[i] = ((2.0f*(float)Math.PI)/(float)numberOfAngularBins) * (float) (i+1);//(2.0f*(float)Math.PI /(float)tot_t) * (float) (i+1);
            if(rotation){
                vt[i] = vt[i] - (float)Math.PI / 4.0f;
            }
        }
        float base = 2.0f;
        float max = (float)Math.pow(base,numberOfRadialBins);
        float multiplica = raioSC/max;
        float[] vr = new float[numberOfRadialBins];
        for (int i = 0; i < numberOfRadialBins; i++)
            vr[i] = (float)(Math.pow(2.0f,i+1) * multiplica);


        //bin#1: raio1, ang1; b#2:r2,a1; b#3:r3,a1; b#4:r4,a1; b#5:r5,a1;
        //b#6:r1,a2; ... b#10:r5,a2;
        //b#11: r1, a3; ... b#15: r5, a3;
        //b#16:     a4       #20
        //b#21:     a5       #25
        //b#26:     a6       #30
        //b#31:     a7       #35
        //b#36:     a8       #40
        //b#41:     a9       #45
        //b#46:     a10      #50
        //b#51:     a11      #55
        //b#56:     a12      #60
        //Shape Context
        int totPoints = graph.getVertexSize();
//        this.sc = new double[totPoints][totSC];
        this.shapeContextVectors = new ShapeContextVector[totPoints][totSC];
        for (int i = 0; i < totPoints; i++){
//            Line2D line = new Line2D.Double(vertexList[i].getX(), vertexList[i].getY(), 
//                    vertexList[i].getX(), vertexList[i].getY());
            
            for (int k = 0; k < totSC; k++){
//                sc[i][k] = 0.0;
//                shapeContextVectors[i][k] = new ShapeContextVector(line);
                shapeContextVectors[i][k] = new ShapeContextVector(0 , 0);
            }
        }

//        centerShapeContext = new double[totSC];
//        for (int k = 0; k < totSC; k++)
//            centerShapeContext[k] = 0.;
        ////////////////////////////////////////////////////////////////////////


        //matriz de distancias
        float[][] dist = new float[totPoints][totPoints];
        for (int i = 0; i < totPoints; i++)
            for (int j = 0; j < totPoints; j++) {
                dist[i][j] = (float)this.euclideanDistance(vertexList[i], vertexList[j]);
            }

//        float[]distCenter = new float[totPoints];
//        Point2D center = graph.getCentroid();
//        centerVertex = new Vertex(-1, center.getX(), center.getY());
//        for (int j = 0; j < totPoints; j++) {
//                distCenter[j] = (float)this.euclideanDistance(centerVertex, vertexList[j]);
//        }
        //cria lista de pontos dentro do disco, para cada ponto
        LinkedList[] ldisc = new LinkedList[totPoints];
        for (int i = 0; i < totPoints; i++) {
            ldisc[i] = new LinkedList();
            for (int j = 0; j < totPoints; j++) {
                if (i != j && dist[i][j] <= raioSC && dist[i][j] > 0)
                    ldisc[i].addLast(j);
            }
        }

//        LinkedList ldiscCenter = new LinkedList();
//        for (int j = 0; j < totPoints; j++) {
//                if (distCenter[j] <= raioSC && distCenter[j] > 0)
//                    ldiscCenter.addLast(j);
//        }
        ////////////////////////////////////////////////////////////////////////

        /*
Cos theta = cateto adjacente / hipotenusa
sen 30 = 1/2
cos 30 = sqrt(3)/2
cos 60 = 1/2
sen 60 = sqrt(3)/2
cos theta = " (grdx*mdx+grdy*mdy)/(gr_module*mdl_module) "

i: x1,y1
j: x2,y2
dx = x2-x1
dy = y2-y1
v = j - i
cos theta = dx / |v| ==> theta = arc cos dx/|v|
se (dy < 0) entao theta:= 2PI - theta
         */
        for (int i = 0; i < totPoints; i++) {
            int total = ldisc[i].size();
            Iterator it = ldisc[i].iterator();
            while(it.hasNext()) {
                int j = ((Integer)it.next()).intValue();
                //cos theta
                float dx = (float)(vertexList[j].getX() - vertexList[i].getX());
                float dy = (float)(vertexList[j].getY() - vertexList[i].getY());
                float modv = dist[i][j]; //(float)fe.euclideanDistance(x1,y1, x2,y2);
//                float cos_theta = dx / modv; //adjacente/hipotenusa
//                float theta = (float)Math.acos(cos_theta);
                float theta = (float) (Math.atan2(dy, dx) + Math.PI);
//                if (dy > 0){
//                    theta = 2.0f*(float)Math.PI - theta;
//                }

                float raio = modv;
                //tenho raio e theta, agora localiza 'bin' por angulo e raio
                int id_r=0, id_t=0;
                for (int ii = 0; ii < numberOfRadialBins; ii++){
                    if (raio <= vr[ii]) {
                        id_r = ii;
                        break;
                    }
                }
                for (int jj = 0; jj < numberOfAngularBins; jj++){
                    if (theta <= vt[jj]) {
                        id_t = jj;
                        break;
                    }
                    /*else if(jj == tot_t - 1){
                      System.out.println("\n\nOHHH\t" + theta + "\tvs.\t" + vt[jj]);
                        System.out.println(dy);
                        System.out.println(dx);
                        System.out.println(modv);
                        System.out.println(dx / modv);
                        System.out.println(cos_theta);
                        System.out.println((float)Math.acos(cos_theta));
                    }*/
                   // }
                }
                if (rotation && theta > vt[numberOfAngularBins - 1]){
                    id_t = 0;
                }
                //System.out.println(i + ">> (" + x1 + ";" + y1 + ") vs. (" + x2 + ";" + y2 + "):\t"+ id_t);
                //identificador do bin
                int id_bin = id_t*numberOfRadialBins+id_r;
//                sc[i][id_bin] += 1.0f;
                if(modv > 0)
                    shapeContextVectors[i][id_bin].add(dx / modv, dy / modv);
            }
//            GUIForShapeContext.showGUI(vertexList, vt, vr,(int)this.raioSC , i,gmathExpression);
            normalizeShapeContextVectors();
//            if (total > 0)
//                for (int ii=0; ii < totSC; ii++)
//                    sc[i][ii] = sc[i][ii] / (float)total;
//            vertexList[i].setShapeContextExpression(sc[i]);
        }

        //////////////////////
//        int total = ldiscCenter.size();
//        Iterator it = ldiscCenter.iterator();
//        while(it.hasNext()) {
//            int j = ((Integer)it.next()).intValue();
//            //cos theta
//            float dx = (float)(vertexList[j].getX() - center.getX());
//            float dy = (float)(vertexList[j].getY() - center.getY());
//            float modv = distCenter[j]; //(float)fe.euclideanDistance(x1,y1, x2,y2);
//            float cos_theta = dx / modv; //adjacente/hipotenusa
//            float theta = (float)Math.acos(cos_theta);
////            CAMBIAR PARA MATH.ATAN2?
//            if (dy > 0){
//                theta = 2.0f*(float)Math.PI - theta;
//            }
//
//            float raio = modv;
//            //tenho raio e theta, agora localiza 'bin' por angulo e raio
//            int id_r=0, id_t=0;
//            for (int ii = 0; ii < tot_r; ii++){
//                if (raio <= vr[ii]) {
//                    id_r = ii;
//                    break;
//                }
//            }
//            for (int jj = 0; jj < tot_t; jj++){
//                if (theta <= vt[jj]) {
//                    id_t = jj;
//                    break;
//                }
//            }
//            if (rotation && theta > vt[tot_t - 1]){
//                id_t = 0;
//            }
//            //System.out.println(i + ">> (" + x1 + ";" + y1 + ") vs. (" + x2 + ";" + y2 + "):\t"+ id_t);
//            //identificador do bin
//            int id_bin = id_t*tot_r+id_r;
//            centerShapeContext[id_bin] += 1.0f;
//        }
////            GUIForShapeContext.showGUI(vertexList, vt, vr,(int)this.raioSC , i,gmathExpression);
//        if (total > 0)
//            for (int ii=0; ii < totSC; ii++)
//                centerShapeContext[ii] = centerShapeContext[ii] / (float)total;
//        centerVertex.setShapeContextExpression(centerShapeContext);


        ////////////////////

    }
    
    private void normalizeShapeContextVectors() {
        for (int i = 0; i < shapeContextVectors.length; i++) {
            for (int j = 0; j < shapeContextVectors[i].length; j++) {
                ShapeContextVector.normalize(shapeContextVectors[i][j]);
            }
        }
    }

    public ShapeContextVector[][] getShapeContextVectors() {
        return shapeContextVectors;
    }

    public void setShapeContextVectors(ShapeContextVector[][] shapeContextVectors) {
        this.shapeContextVectors = shapeContextVectors;
    } 
    
    @Override
    public double[][] getSC() {
        int totSC = numberOfAngularBins * numberOfRadialBins;
        sc = new double[shapeContextVectors.length][totSC * 2];
        int posSC;
        for (int i = 0; i < sc.length; i++) {
            posSC = 0;
            for (int j = 0; j < shapeContextVectors[0].length; j++) {
                sc[i][posSC] = shapeContextVectors[i][j].getX();
                posSC++;
                sc[i][posSC] = shapeContextVectors[i][j].getY();
                posSC++;
            }
        }
        return sc;
    } 
    
}
