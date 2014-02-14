/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.cost;

import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;
import br.usp.ime.faguilar.graphics.GMathExpression;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Frank Aguilar
 */
public class FuzzyShapeContext extends ShapeContext{
    private FuzzyShapeContextRegion regionShapecontext;
    
    public FuzzyShapeContext(float raioSC, Graph graph, int tot_r, int tot_t, 
            boolean rotation, GMathExpression mathExpression) {
        super();
        this.tot_r = tot_r;
        this.tot_t = tot_t;
        this.totSC = tot_r * tot_t;
        regionShapecontext = new FuzzyShapeContextRegion();
        
        this.init(raioSC, graph, rotation);
    }
    
    protected void init(float raioSC, Graph graph, boolean rotation) {
        
        this.raioSC = raioSC;
        Vertex [] vertexList = graph.getIndexedVertexes();

        float[] vt = regionShapecontext.calculateAngularRegions(tot_t, rotation);
        float[] vr = regionShapecontext.calculateRadialRegions(tot_r, raioSC);

        int totPoints = graph.getVertexSize();
        this.sc = new double[totPoints][totSC];
        for (int i = 0; i < totPoints; i++)
            for (int k = 0; k < totSC; k++)
                sc[i][k] = 0.0;

        centerShapeContext = new double[totSC];
        for (int k = 0; k < totSC; k++)
            centerShapeContext[k] = 0.;
        ////////////////////////////////////////////////////////////////////////

        //matriz de distancias
        float[][] dist = new float[totPoints][totPoints];
        for (int i = 0; i < totPoints; i++){
            dist[i][i] = 0;
            for (int j = i + 1; j < totPoints; j++) {
                dist[i][j] = (float)this.euclideanDistance(vertexList[i], vertexList[j]);
                dist[j][i] = dist[i][j];
            }
        }

        float[]distCenter = new float[totPoints];
        Point2D center = graph.getCentroid();
        centerVertex = new Vertex(-1, center.getX(), center.getY());
        for (int j = 0; j < totPoints; j++) {
                distCenter[j] = (float)this.euclideanDistance(centerVertex, vertexList[j]);
        }
        //cria lista de pontos dentro do disco, para cada ponto
        LinkedList[] ldisc = new LinkedList[totPoints];
        for (int i = 0; i < totPoints; i++) {
            ldisc[i] = new LinkedList();
            for (int j = 0; j < totPoints; j++) {
                if (i != j && dist[i][j] <= raioSC && dist[i][j] > 0)
                    ldisc[i].addLast(j);
            }
        }

        LinkedList ldiscCenter = new LinkedList();
        for (int j = 0; j < totPoints; j++) {
                if (distCenter[j] <= raioSC && distCenter[j] > 0)
                    ldiscCenter.addLast(j);
        }
        for (int i = 0; i < totPoints; i++) {
            int total = ldisc[i].size();
            Iterator it = ldisc[i].iterator();
            while(it.hasNext()) {
                int j = ((Integer)it.next()).intValue();
                //cos theta
                float dx = (float)(vertexList[j].getX() - vertexList[i].getX());
                float dy = (float)(vertexList[j].getY() - vertexList[i].getY());
                float modv = dist[i][j]; //(float)fe.euclideanDistance(x1,y1, x2,y2);
                float theta = (float) (Math.atan2(dy, dx) + Math.PI);
                float raio = modv;
                regionShapecontext.updateBinWithConstantSlope(sc[i], raio, theta, 
                        rotation);
//                int id_bin = id_t*tot_r+id_r;
//                sc[i][id_bin] += 1.0f;
            }
            if (total > 0)
                for (int ii=0; ii < totSC; ii++)
                    sc[i][ii] = sc[i][ii] / (float)total;
            vertexList[i].setShapeContextExpression(sc[i]);
        }

        //////////////////////
        int total = ldiscCenter.size();
        Iterator it = ldiscCenter.iterator();
        while(it.hasNext()) {
            int j = ((Integer)it.next()).intValue();
            //cos theta
            float dx = (float)(vertexList[j].getX() - center.getX());
            float dy = (float)(vertexList[j].getY() - center.getY());
            float modv = distCenter[j]; //(float)fe.euclideanDistance(x1,y1, x2,y2);
            float cos_theta = dx / modv; //adjacente/hipotenusa
            float theta = (float)Math.acos(cos_theta);
//            CAMBIAR PARA MATH.ATAN2?
            if (dy > 0){
                theta = 2.0f*(float)Math.PI - theta;
            }

            float raio = modv;
            //tenho raio e theta, agora localiza 'bin' por angulo e raio
            int id_r=0, id_t=0;
            for (int ii = 0; ii < tot_r; ii++){
                if (raio <= vr[ii]) {
                    id_r = ii;
                    break;
                }
            }
            for (int jj = 0; jj < tot_t; jj++){
                if (theta <= vt[jj]) {
                    id_t = jj;
                    break;
                }
            }
            if (rotation && theta > vt[tot_t - 1]){
                id_t = 0;
            }
            //System.out.println(i + ">> (" + x1 + ";" + y1 + ") vs. (" + x2 + ";" + y2 + "):\t"+ id_t);
            //identificador do bin
//            int id_bin = id_t*tot_r+id_r;
//            centerShapeContext[id_bin] += 1.0f;
        }
//            GUIForShapeContext.showGUI(vertexList, vt, vr,(int)this.raioSC , i,gmathExpression);
        if (total > 0)
            for (int ii=0; ii < totSC; ii++)
                centerShapeContext[ii] = centerShapeContext[ii] / (float)total;
        centerVertex.setShapeContextExpression(centerShapeContext);


        ////////////////////

    }
}
