package br.usp.ime.faguilar.cost;

import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;
import java.util.Iterator;
import java.util.LinkedList;

// Cost function
// = alpha * cv + (1-alpha) * ce
// = alpha * cv + (1-alpha) * [delta * ca + (1-delta) * cm]
public class CostDeformation extends Cost {

    private float alpha;
    private float beta;
    private float gama;
    private float mxdst; // normalizes ce
    // adjacency list of the model
    private LinkedList<Vertex>[] ladj;
    protected Graph model;

    public CostDeformation(Graph model, Graph input, float alpha, float beta, float gama) {
        this.alpha = alpha;
        this.beta = beta;
        this.gama = gama;
        this.model = model;
        this.ladj = model.getNeighboursList();
//        this.mxdst = this.calculateLargestDifference(input);
        double height = model.getHeight();
        double width = model.getWidth();
        this.mxdst = (float) Math.sqrt(height * height
                    + width * width);
    }


    // calculates the cost of the candidate label vm for current input vertex
    public float getCost(Vertex vm, Vertex vi) {
        //this.Gd.atualiza(vi, vm); // calculates deformation graph

        Vertex vd = new Vertex(vm.getId(), vi.getX(), vi.getY());

        float[] cost = new float[8];
        for (int i = 0; i < 8; i++) {
            cost[i] = 0.0f;
        }
        this.modificado(vm, vd, cost);
        // cost[0]=ne, cost[1]=aca, cost[2]=acm
        // cost[3]=ca, cost[4]=cm, cost[5]=ce
        // cost[6]=cv, cost[7]=c
        float cv = (float) this.costCV(vm, vi);
        float ce = cost[5];
        float c = this.alpha * cv + (1.0f - this.alpha) * ce;
        cost[6] = cv;
        cost[7] = c;
        //System.out.println("cv: " + cv + "\tce" + ce + "\tcost: " + c);
        return c;
    }

    // cada problema de casamento tem o seu custo cv particular,
    private double costCV(Vertex vm, Vertex vi) {
       CostShapeContextInside cost = new CostShapeContextInside();   
       return this.gama * cost.getCost(vm, vi) + (1.0f - this.gama) * vm.compareShapeContextExpression(vi);//vi.compareShapeContextExpression(vm);
    }

    private void modificado(Vertex vm, Vertex vd, float[] cost) {
        this.ace_modificado(vm, vd, cost);
        // cost[0]=ne, cost[1]=aca, cost[2]=acm
        float ce = 0.0f;
        float ca = 0.0f;
        float cm = 0.0f;
        int ne = (int) cost[0];
        float aca = cost[1];
        float acm = cost[2];
        if (ne > 0) {
            float cea = aca / ne;
            float cem = acm / ne;
            ce = this.beta * cea + (1.0f - this.beta) * cem;
            ca = (1.0f - this.alpha) * this.beta * cea;
            cm = (1.0f - this.alpha) * (1.0f - this.beta) * cem;
        }
        cost[3] = ca;
        cost[4] = cm;
        cost[5] = ce;
    }

    // vm2 = candidate label for current input vertex
    private void ace_modificado(Vertex vm, Vertex vd, float[] cost) {
        float acm = 0.0f, aca = 0.0f; // accumulated edge costs: modular and
        // angular
        int vm2_id = vm.getId();
        int ne = 0;
        // calculates ce for all neighbors of vm2
        int k = 0;
        //while (k < totvm && this.ladj[vm2_id][k] > -1) {
        Iterator<Vertex> it = this.ladj[vm2_id].iterator();

        //this.mxdst = calculateLargestDifferenceLocal(vd);

        while (it.hasNext()) {
            Vertex vm1 = it.next();
            ne++;
            float grdx = (float)(vd.getX() - vm1.getX());

            float mdx = (float)(vm.getX() - vm1.getX());
            float grdy = (float)(vd.getY() - vm1.getY());
            float mdy = (float)(vm.getY() - vm1.getY());
            float gr_module = (float) this.euclideanDistance(0.0f, 0.0f,
                    grdx, grdy);

            float mdl_module = (float) this.euclideanDistance(0.0f,
                    0.0f, mdx, mdy);

            if (gr_module > 0.0f && mdl_module > 0.0f) {
                aca = aca
                        + Math.abs((grdx * mdx + grdy * mdy)
                        / (gr_module * mdl_module) - 1.0f) / 2.0f;
            }
            acm = acm + Math.abs(gr_module - mdl_module) / this.mxdst;
        }
        cost[0] = ne;
        cost[1] = aca;
        cost[2] = acm;
    }


    private double euclideanDistance(double x1, double y1,
            double x2, double y2) {
        double d1 = x1 - x2;
        double d2 = y1 - y2;
        double res = Math.sqrt(d1 * d1 + d2 * d2);
        return res;
    }
}
// //////////////////////////////////////////////////////////////////////////

