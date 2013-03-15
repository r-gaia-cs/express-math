package br.usp.ime.faguilar.hungarian_algorithm;


/**
 * an implementation of this: http://en.wikipedia.org/wiki/Hungarian_algorithm
 *
 * based loosely on Guy Robinson's description of the algorithm here: http://www.netlib.org/utk/lsi/pcwLSI/text/node222.html#SECTION001182000000000000000
 *
 * In short, it finds the cheapest assignment pairs given a cost matrix.
 *
 * Copyright 2007 Gary Baker (GPL v3)
 * @author gbaker
 */
public class AssignmentProblem {

    private final float[][] costMatrix;

    public AssignmentProblem(float[][] aCostMatrix) {
        costMatrix = aCostMatrix;
    }

    public float[][] copyOfMatrix() {
        // make a copy of the passed array
        float[][] retval = new float[costMatrix.length][];
        for (int i = 0; i < costMatrix.length; i++) {
            retval[i] = new float[costMatrix[i].length];
            System.arraycopy(costMatrix[i], 0, retval[i], 0, costMatrix[i].length);
        }
        return retval;
    }


    public int[][] solve(AssignmentAlgorithm anAlgorithm) {

        float[][] copyMatrix = copyOfMatrix();
        int [][] matching = anAlgorithm.computeAssignments(copyMatrix);
//        float[][] costs = copyOfMatrix();
//        double cost = 0;
//        for (int i = 0; i < matching.length; i++) {
//            cost += costs[matching[i][0]][matching[i][1]];
//        }
//        System.out.println((float)cost);
        return matching;
//        return anAlgorithm.computeAssignments(copyMatrix);

    }

}
