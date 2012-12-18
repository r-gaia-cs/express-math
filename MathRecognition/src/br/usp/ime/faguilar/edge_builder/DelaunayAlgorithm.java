/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.edge_builder;

import br.usp.ime.faguilar.graph.Graph;
import br.usp.ime.faguilar.graph.Vertex;

/**
 *
 * @author Willian
 */
public class DelaunayAlgorithm {

    QuadEdge[] quadEdge;  // a handle (array) for all the quadedge.
    int quadEdgeNum;
    static int runs;      // decide whether a DC or BF is going to perform.
    static int numOfSites;

    public DelaunayAlgorithm(int siteNum) {
        quadEdge = new QuadEdge[(siteNum + 1) * 3 - 1];
        quadEdgeNum = 0;
    }

    public void triangularizaG(Graph graph) {
        // cria pontos para triangularizacao
        int totv = graph.getVertexSize();
        numOfSites = totv;
        GeomSites[] s = new GeomSites[numOfSites];
        Vertex[] refv = graph.getIndexedVertexes();
        for (int i = 0; i < totv; i++) {
            Vertex v = refv[i];
            int id = v.getId();
            if (id != i) {
                System.out.println("Error: triangularizaGM()  id's diferentes!");
                System.exit(1);
            }
            double x = v.getX();
            double y = v.getY();
            s[i] = new GeomSites(id, x, y);
        }

        // Delaunay Triangulation: O(n log n), Guibas & Stolfi, quadedge
        GeomSites.mergeSort(s, 0, s.length);
        this.DivideAndConquer(s, 0, s.length);

        // constroi arestas pela triangularizacao: O(n^2)
        boolean[][] adj;
        adj = new boolean[numOfSites][numOfSites];
        for (int i = 0; i < numOfSites; i++) {
            for (int j = 0; j < numOfSites; j++) {
                adj[i][j] = (i == j);
            }
        }

        // Result
        QuadEdge[] qe = this.quadEdge;
        GeomSites circumcenter = new GeomSites(0, 0, 0);
        //System.out.println("\nSites:\n");
        //for (k = 0; k < numOfSites; k++) System.out.println(s[k].toString());
        //System.out.println("\n\nEdges:\n");
        while (quadEdgeNum > 0) {
            QuadEdge q = qe[--quadEdgeNum];
            for (int k = 0; k < 2; k++) {
                if (q.rotSym().orig() == null && q.lnext().dest() == q.lprev().orig()) {
                    //System.out.println(q.orig().id + " " + q.dest().id + " " +q.lnext().dest().id);
                    q.lnext().rotSym().setData(circumcenter);
                    q.lprev().rotSym().setData(circumcenter);
                    q.rotSym().setData(circumcenter);

                    //adiciona arestas do triangulo, caso jah nao tenha adicionado...
                    int id1 = q.orig().id;
                    int id2 = q.dest().id;
                    int id3 = q.lnext().dest().id;
                    Vertex v1 = refv[id1];
                    Vertex v2 = refv[id2];
                    Vertex v3 = refv[id3];

                    if (!adj[id1][id2]) {
                        graph.addEdge(v1, v2);
                    }
                    if (!adj[id2][id1]) {
                        graph.addEdge(v2, v1);
                    }

                    if (!adj[id2][id3]) {
                        graph.addEdge(v2, v3);
                    }
                    if (!adj[id3][id2]) {
                        graph.addEdge(v3, v2);
                    }

                    if (!adj[id1][id3]) {
                        graph.addEdge(v1, v3);
                    }
                    if (!adj[id3][id1]) {
                        graph.addEdge(v3, v1);
                    }

                    adj[id1][id2] = adj[id1][id2] = true;
                    adj[id2][id3] = adj[id3][id2] = true;
                    adj[id1][id3] = adj[id3][id1] = true;
                }
                q = q.sym();
            }
        }
    }

    private QuadEdge[] DivideAndConquer(GeomSites s[], int i, int j) {
        QuadEdge[] lere = new QuadEdge[2]; // return lere[0] for le, lere[1] for re
        int size = j - i;

        if (size == 2) {
            //s[i] and s[i+1] is in sorted order, create an edge from s[i] to s[i+1].
            lere[0] = QuadEdge.makeEdge(s[i], s[i + 1]);
            lere[1] = lere[0].sym();
            quadEdge[quadEdgeNum++] = lere[0];
            return lere;
        } else if (size == 3) {
            //s[i], s[i+1] and s[i+2] are in sorted order. creat edge in that order.
            QuadEdge a = QuadEdge.makeEdge(s[i], s[i + 1]);
            QuadEdge b = QuadEdge.makeEdge(s[i + 1], s[i + 2]);
            QuadEdge.splice(a.sym(), b);

            quadEdge[quadEdgeNum++] = a;
            quadEdge[quadEdgeNum++] = b;

            // closing the triangle from b.dest to a.orig
            QuadEdge c = QuadEdge.connect(b, a);
            quadEdge[quadEdgeNum++] = c;
            // lere depend on ccw of s[i],s[i+1],s[i+2].
            if (s[i].ccw(s[i + 1], s[i + 2])) {
                lere[0] = a;
                lere[1] = b.sym();
            } else if (s[i].ccw(s[i + 2], s[i + 1])) {
                lere[0] = c.sym();
                lere[1] = c;
            } else // s[i], s[i+1],s[i+2] are collinear
            {
                lere[0] = a;
                lere[1] = b.sym();
            }
            return lere;
        } else // size >= 4
        {
            // let L and R be the left and right halves of s
            int m = size / 2;
            QuadEdge[] dodi = new QuadEdge[2];
            dodi = DivideAndConquer(s, i, i + m);
            QuadEdge ldo = dodi[0];
            QuadEdge ldi = dodi[1];

            dodi = DivideAndConquer(s, i + m, j);
            QuadEdge rdi = dodi[0];
            QuadEdge rdo = dodi[1];

            // get the lower common tangent of L and R
            while (true) {
                if (QuadEdge.leftOf(rdi.orig(), ldi)) {
                    ldi = ldi.lnext();
                } else if (QuadEdge.rightOf(rdi, ldi.orig())) {
                    rdi = rdi.rprev();
                } else {
                    // until ldi rdi near lower horizontal in left and right direction 
                    // respectively and rdi.org to the right(highter) of ldi 
                    // and ldi.org to the left(also highter) of rdi 
                    break;
                }
            }

            // the first cross edge basel is lower common tangent from rdi.orig to ldi.orig
            QuadEdge basel = QuadEdge.connect(rdi.sym(), ldi);
            quadEdge[quadEdgeNum++] = basel;

            // set rdo or ldo to basel if ldi or rdi goes too far
            if (ldi.orig() == ldo.orig()) {
                ldo = basel.sym();
            }
            if (rdi.orig() == rdo.orig()) {
                rdo = basel;
            }

            QuadEdge lcand = null;
            QuadEdge rcand = null;
            // merging 
            while (true) {
                // until the first lcand.dest satisfying inCircle test of 
                // the rising bubble. delete those basel.dest failed the test.
                lcand = basel.sym().onext();
                if (QuadEdge.rightOf(basel, lcand.dest())) {
                    while (basel.dest().inCircle(basel.orig(),
                            lcand.dest(), lcand.onext().dest())) {
                        QuadEdge tmp = lcand.onext();
                        QuadEdge.deleteEdge(lcand);   //delete this lcand
                        this.removeEdge(lcand);
                        lcand = tmp;
                    }
                }

                // same approach for R sites 
                rcand = basel.oprev();
                if (QuadEdge.rightOf(basel, rcand.dest())) {
                    while (basel.dest().inCircle(basel.orig(),
                            rcand.dest(), rcand.oprev().dest())) {
                        QuadEdge tmp = rcand.oprev();
                        QuadEdge.deleteEdge(rcand);
                        this.removeEdge(rcand);
                        rcand = tmp;
                    }
                }

                if ((!basel.rightOf(lcand.dest())) && (!basel.rightOf(rcand.dest()))) {
                    break;  // basel is the common upper tangent
                }

                // the next basel is to be connected 
                // either to lcand.dest or to rcand.dest.
                // if both is valid, then choose the 
                // appropriate one using the inCircle test
                if ((!basel.rightOf(lcand.dest())) || (basel.rightOf(basel, rcand.dest())
                        && lcand.dest().inCircle(lcand.orig(), rcand.orig(), rcand.dest()))) {
                    // connect cross edge basel from rcand.dest to basel.dest
                    basel = QuadEdge.connect(rcand, basel.sym());
                    quadEdge[quadEdgeNum++] = basel;
                } else {
                    // connet cross edge basel from basel.orig() to lcand.dest
                    basel = QuadEdge.connect(basel.sym(), lcand.sym());
                    quadEdge[quadEdgeNum++] = basel;
                }
            }
            lere[0] = ldo;
            lere[1] = rdo;
        }
        return lere;
    }

    /** Delete the given QuadEdge from the TriangDelaunay edges.
     * @param q QuadEdge to be deleted.
     */
    public void removeEdge(QuadEdge q) {
        int i;
        QuadEdge sym = q.sym();
        for (i = 0; i < quadEdgeNum; i++) {
            if (quadEdge[i] == q || quadEdge[i] == sym) {
                break;
            }
        }

        quadEdgeNum--;
        for (; i < quadEdgeNum; i++) {
            quadEdge[i] = quadEdge[i + 1];
        }
        quadEdge[i] = null;
    }
}

class GeomSites {

    int id;
    double x;
    double y;

    /** Constructor
     * @param k id
     * @param sx initial value for x
     * @param sy initial value for y
     */
    public GeomSites(int k, double sx, double sy) {
        id = k;
        x = sx;
        y = sy;
    }

    /** Generates random sites and return it as GeomSites array.
     * @param n number of sites
     * @param xul x coordinate of the upperleft corner
     * @param yul y coordinate of the upperleft corner
     * @param wid the width of the rectangular area
     * @param hei the height of the rectangular area
     * @return gs array of GeomSites
     */
    public static GeomSites[] RandSites(int n, double xul, double yul,
            double wid, double hei) {
        GeomSites[] gs;
        gs = new GeomSites[n];
        int counter = 1;

        for (int k = 0; k < n; k++) {
            gs[k] = new GeomSites(counter++,
                    (Math.random() * wid + xul),
                    (yul - Math.random() * hei));
        }
        return gs;
    }

    /** ccw is to see if the given three GeomSites sites, 
     * a, b and c, are  counter clockwise or not.
     * @param a the first GeomSites site
     * @param b the second GeomSites site
     * @param c the third GeomSites site
     * @return true if a, b and c are ccw; false if cw.
     */
    public static boolean ccw(GeomSites a, GeomSites b, GeomSites c) {
        return a.x * (b.y - c.y) - a.y * (b.x - c.x) + (b.x * c.y - b.y * c.x) > 0;
    }

    /** ccw is to see if this sites and the other two given sites, 
     * b and c, are  counter clockwise  or not.
     * @param b the second GeomSites site
     * @param c the third GeomSites site
     * @return true this, b and c are ccw; false if cw.
     */
    public boolean ccw(GeomSites b, GeomSites c) {
        return this.x * (b.y - c.y) - this.y * (b.x - c.x) + (b.x * c.y - b.y * c.x) > 0;
    }

    /** inCircle is to test if the last argument 
     * site, d, is interior to the region of the plane that 
     * is bounded by the oriented circle (a,b,c) and lies to 
     * the left of it.
     * @param a the first GeomSites site for the circle
     * @param b the second GeomSites site for the circle
     * @param c the third GeomSites site for the circle
     * @param d the site  to test
     * @return true if d lies interior to the circle; false otherwise
     */
    public boolean inCircle(GeomSites a, GeomSites b, GeomSites c, GeomSites d) {
        if (a == d || b == d || c == d) {
            return false; // d is one of a,b,c.
        }
        double d02 = a.x * a.x + a.y * a.y;
        double d12 = b.x * b.x + b.y * b.y;
        double d22 = c.x * c.x + c.y * c.y;
        double d32 = d.x * d.x + d.y * d.y;

        double det = a.x * determinant(b.y, d12, 1, c.y, d22, 1, d.y, d32, 1);
        det -= a.y * determinant(b.x, d12, 1, c.x, d22, 1, d.x, d32, 1);
        det += d02 * determinant(b.x, b.y, 1, c.x, c.y, 1, d.x, d.y, 1);
        det -= determinant(b.x, b.y, d12, c.x, c.y, d22, d.x, d.y, d32);
        return det > 0;
    }

    /** inCircle is to test if the last argument 
     * site, d, is interior to the region of the plane that 
     * is bounded by the oriented circle (this,b,c) and lies to 
     * the left of it.
     * @param a the first GeomSites site for the circle
     * @param b the second GeomSites site for the circle
     * @param c the third GeomSites site for the circle
     * @param d the site  to test
     * @return true if d lies interior to the circle; false otherwise
     */
    public boolean inCircle(GeomSites b, GeomSites c, GeomSites d) {
        if (this == d || b == d || c == d) {
            return false; // d is one of this,b,c.
        }
        double d02 = x * x + y * y;
        double d12 = b.x * b.x + b.y * b.y;
        double d22 = c.x * c.x + c.y * c.y;
        double d32 = d.x * d.x + d.y * d.y;

        double det = x * determinant(b.y, d12, 1, c.y, d22, 1, d.y, d32, 1);
        det -= y * determinant(b.x, d12, 1, c.x, d22, 1, d.x, d32, 1);
        det += d02 * determinant(b.x, b.y, 1, c.x, c.y, 1, d.x, d.y, 1);
        det -= determinant(b.x, b.y, d12, c.x, c.y, d22, d.x, d.y, d32);
        return det > 0;
    }

    /** Computing the determinant of a 3x3 matrix.
     * @param d00 d01 d02 d10 d11 d12 d20 d21 d22 defines the matrix.
     * @return the determinant the matrix.
     */
    public static double determinant(double d00, double d01, double d02,
            double d10, double d11, double d12,
            double d20, double d21, double d22) {
        return (d00 * (d11 * d22 - d12 * d21)
                - d01 * (d10 * d22 - d12 * d20)
                + d02 * (d10 * d21 - d11 * d20));
    }

    /**
     * Sorting the given sites s[a:b-1] in X ascending order, if tie, in Y.
     * @param s GeomSites array
     * @param a starting index (include)
     * @param b ending index (exclude)
     * @return void , s is replaced with the ascending one
     */
    static void mergeSort(GeomSites s[], int a, int b) {
        int size = b - a;

        if (size <= 1) {
        } //doing nothing.
        else if (size == 2) {
            if (outOfOrder(s[a], s[a + 1])) {
                GeomSites tmp = s[a];
                s[a] = s[a + 1];
                s[a + 1] = tmp;
            }
        } else // more than three sites
        {
            int mid = size / 2; // for size odd, mid is the smaller half

            mergeSort(s, a, a + mid);
            mergeSort(s, a + mid, b);

            // merge the two sorted half
            GeomSites ascend[] = new GeomSites[size];
            int i = a;
            int j = a + mid;

            for (int k = 0; k < size; k++) {
                if (i >= a + mid) // the first half is arranged, append the rest
                {
                    ascend[k] = s[j++];
                } else if (j >= b) // the second half is arranged, append the rest
                {
                    ascend[k] = s[i++];
                } else // merge, the smallest of the two is sorted out.
                {
                    if (outOfOrder(s[i], s[j])) {
                        ascend[k] = s[j++];
                    } else {
                        ascend[k] = s[i++];
                    }
                }
            }

            // replace s with the ascending one 
            for (int k = 0; k < size; k++) {
                s[a + k] = ascend[k];
            }
        }
        return;
    }

    /**
     * This checks if site s1 and site s2 is out of order.
     * in the sense that s1,s2 ascend in x, in case of tie,
    # ascend in y.
     * @param s1 first GeomSites
     * @param s2 second GeomSites
     * @return boolean
     */
    public static boolean outOfOrder(GeomSites s1, GeomSites s2) {
        if (s1.x != s2.x) {
            return s1.x > s2.x;
        } else {
            return s1.y > s2.y;
        }
    }

    public String toString() {
        return "  " + id + "  " + x + "  " + y;
    }
}

class QuadEdge {

    private QuadEdge Onext;
    private QuadEdge rot;
    private GeomSites data;
    private boolean toBeAdd;

    /** Constructor.
     * @param Onext next QuadEdge of this QuadEdge on Orig ring
     * @param data Geometry date of Orig vertex of this QuadEdge
     * @param rot cross edge in ccw of this QuadEdge
     */
    public QuadEdge(QuadEdge Onext, QuadEdge rot, GeomSites data) {
        this.Onext = Onext;
        this.data = data;
        this.rot = rot;
        toBeAdd = true;
    }

    /** Construct a QuadEdge data structure is to make four QuadEdge and
     *  setting up their 'Onext' and 'rot' references 
     *  such that with the handle q0, other can easily be accessed.
     * @return the created QuadEdge data structure
     */
    public static QuadEdge makeEdge() {
        QuadEdge q0 = new QuadEdge(null, null, null);
        QuadEdge q1 = new QuadEdge(null, null, null);
        QuadEdge q2 = new QuadEdge(null, null, null);
        QuadEdge q3 = new QuadEdge(null, null, null);

        q0.rot = q1;
        q1.rot = q2;
        q2.rot = q3;
        q3.rot = q0;

        q0.Onext = q0;
        q1.Onext = q3; //on the sphere, left=right
        q2.Onext = q2;
        q3.Onext = q1;

        return q0;
    }

    /** Construct a QuadEdge data structure with the geometric data
     * @param orig geometric data of the origin of QuadEdge to be created
     * @param dest geometric data of the end of QuadEdge to be created
     * @return the newly created QuadEdge data structure with the
     *  four QuadEdge wiring up by 'Onext' and 'rot' reference.
     */
    public static QuadEdge makeEdge(GeomSites orig, GeomSites dest) {
        QuadEdge q0 = makeEdge();

        q0.setData(orig);
        q0.sym().setData(dest);
        return q0;
    }

    /** Splices the two argument QuadEdges.
     * @param q1 QuadEdge
     * @param q2 QuadEdge
     */
    public static void splice(QuadEdge q1, QuadEdge q2) {

        /*Exchange reference of q1.rotSym() and q2.rotSym().
        This has the effect of spliting the ring to two ring
        if q1.left and q2.left are in the same ring, 
        or combining the two ring together
        if q1.left and q2.left are in the different ring.
        alpha = q1.next.rot, beta = q2.next.rot,
        in old graph, alpha.next = q1.rotSym();
        after splice, alpha.next = q2.rotSym();  
        here alpha=q1.next.rot is still old relation
         */

        q1.onext().rot().setOnext(q2.rotSym());    //refer fig.10 on page 97 
        q2.onext().rot().setOnext(q1.rotSym());

        /* Exchange q1.next and q2.next
        This has the effect of splitting or combining 
        the vertex ring of the e1.orig  and e2.orig 
        depending on whether they are the same ring
        or distinct ring.
         */

        QuadEdge q1next = q1.onext();

        q1.setOnext(q2.onext());
        q2.setOnext(q1next);

        // One orientation is ok for the this application

    }

    /** Splicing a given QuadEdge with this QuadEdge.
     * @param q QuadEdge
     */
    public void splice(QuadEdge q) {
        splice(this, q);
    }

    /** Connecting the given two QuadEdges by generating
     * a new QuadEdge from a.dest to b.orig, and returns the
     * newly created QuadEdge q, such that a.left=q.left=b.left.
     * @param a QuadEdge
     * @param b QuadEdge
     * @return q QuadEdge
     */
    public static QuadEdge connect(QuadEdge a, QuadEdge b) {
        QuadEdge q = makeEdge(a.dest(), b.orig());

        splice(q, a.lnext());
        splice(q.sym(), b);
        return q;
    }

    /** Deletes a QuadEdge.
     * @param q QuadEdge
     */
    public static void deleteEdge(QuadEdge q) {
        splice(q, q.oprev());
        splice(q.sym(), q.sym().oprev());
    }

    public QuadEdge onext() {
        return Onext;
    }

    public QuadEdge rot() {
        return rot;
    }

    public GeomSites orig() {
        return data;
    }

    public void setOnext(QuadEdge next) {
        Onext = next;
    }

    public void setRot(QuadEdge rot) {
        this.rot = rot;
    }

    public void setData(GeomSites data) {
        this.data = data;
    }

    public QuadEdge sym() {
        return rot.rot();
    }

    public GeomSites dest() {
        return sym().orig();
    }

    public GeomSites right() {
        return rot.orig();
    }

    public GeomSites left() {
        return rot.sym().orig();
    }

    public QuadEdge rotSym() {
        return rot.sym();
    }

    public QuadEdge oprev() {
        return rot.onext().rot();
    }

    public QuadEdge lnext() {
        return rotSym().onext().rot();
    }

    public QuadEdge lprev() {
        return onext().sym();
    }

    public QuadEdge rnext() {
        return rot.onext().rotSym();
    }

    public QuadEdge rprev() {
        return sym().onext();
    }

    /** Testing site s if to the right of this QuadEdge. 
     * @param s GeomSites
     * @return boolean
     */
    public boolean rightOf(GeomSites s) {
        return s.ccw(sym().data, data);
    }

    /** Testing site s if to the right of QuadEdge q. 
     * @param s GeomSites
     * @return boolean
     */
    public static boolean rightOf(QuadEdge q, GeomSites s) {
        return s.ccw(q.dest(), q.orig());
    }

    /** Testing site s if to the left of this QuadEdge. 
     * @param s GeomSites
     * @return true  site s is left to this QuadEdge;
     */
    public boolean leftOf(GeomSites s) {
        return s.ccw(data, dest());
    }

    /** Testing site s if to the left of  QuadEdge q. 
     * @param s GeomSites
     * @return true  site s is left to this QuadEdge;
     */
    public static boolean leftOf(GeomSites s, QuadEdge q) {
        return s.ccw(q.orig(), q.dest());
    }

    /** addToEPG recursively traverse all QuadEdges by Onext and lnext pointer.
     */
    public void addToEPG() {
        if (toBeAdd) {
            Onext.addToEPG();
            lnext().addToEPG();
            toBeAdd = false;
        }
    }
}

interface DelaunayConstant {

    int DIVIDE_AND_CONQUER = 1;
    int BRUTAL_FORCE = 2;
}
