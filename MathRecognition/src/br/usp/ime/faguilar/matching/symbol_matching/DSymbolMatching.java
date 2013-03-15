/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching.symbol_matching;

import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.hungarian_algorithm.AssignmentProblem;
import br.usp.ime.faguilar.hungarian_algorithm.HungarianAlgorithm;
import br.usp.ime.faguilar.matching.MatchingParameters;
import br.usp.ime.faguilar.util.SymbolUtil;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class DSymbolMatching {
    private DSymbol symbol1;
    private DSymbol symbol2;
    private float[][] matrixCost;
    private int[][] strokesCorrespondence;
    private float beta;
    private float gama;

    public static DSymbolMatching newInstanceFromSymbolsToMatch(DSymbol symbol1,
            DSymbol symbol2){
        DSymbolMatching dSymbolMatching = new DSymbolMatching();
        dSymbolMatching.setSymbol1(symbol1);
        dSymbolMatching.setSymbol2(symbol2);
        dSymbolMatching.setBeta((float)0.7);
        return dSymbolMatching;
    }

    public float getCost(){
        float cost = 0;
        calculateMatrixCost();
        calculateCorrespondences();
        cost = readCost();
        return cost;
    }

    public float[][] getMatrixCost() {
        return matrixCost;
    }

    public void setMatrixCost(float[][] matrixCost) {
        this.matrixCost = matrixCost;
    }

    public int[][] getStrokesCorrespondence() {
        return strokesCorrespondence;
    }

    public void setStrokesCorrespondence(int[][] strokesCorrespondence) {
        this.strokesCorrespondence = strokesCorrespondence;
    }

    public DSymbol getSymbol1() {
        return symbol1;
    }

    public void setSymbol1(DSymbol symbol1) {
        this.symbol1 = symbol1;
    }

    public DSymbol getSymbol2() {
        return symbol2;
    }

    public void setSymbol2(DSymbol symbol2) {
        this.symbol2 = symbol2;
    }

    private void calculateMatrixCost() {
        StrokeFeature[] featuresSymbol1 = DSymbolMatching.extractFeatures(symbol1);
        StrokeFeature[] featuresSymbol2 = DSymbolMatching.extractFeatures(symbol2);
        matrixCost = new float[symbol1.size()][symbol1.size()];
        ///
        ArrayList<Point2D> points1, points2;
        double[][] shapeContext1, shapeContext2;
        //

        for (int i = 0; i < featuresSymbol1.length; i++) {
            for (int j = 0; j < featuresSymbol2.length; j++){
                featuresSymbol1[i].setGama(getGama());

//                matrixCost[i][j] = featuresSymbol1[i].distance(featuresSymbol2[j]);
                
                points1 = PreprocessingAlgorithms.getNPoints(getSymbol1().get(i)
                    , MatchingParameters.numberOfPointPerSymbol / getSymbol1().size());
                points2 = PreprocessingAlgorithms.getNPoints(getSymbol2().get(j)
                        , MatchingParameters.numberOfPointPerSymbol / getSymbol2().size());
                shapeContext1 = CostShapeContextInside.calculateShapeContextFromPoints2D(points1);
                shapeContext2 = CostShapeContextInside.calculateShapeContextFromPoints2D(points2);

                matrixCost[i][j] = (float) (getBeta() * featuresSymbol1[i].distance(featuresSymbol2[j]) +
                        (1. - getBeta()) * CostShapeContextInside.getCost(shapeContext1, shapeContext2));
            }
        }
    }

    public static StrokeFeature[] extractFeatures(DSymbol symbol) {
        StrokeFeature[] features = new StrokeFeature[symbol.size()];
        for (int i = 0; i < symbol.size(); i++) {
            features[i] = StrokeFeature.createStrokeFeaturesFromDStrokeAndDSymbol(
                    symbol.get(i), symbol);
        }
        return features;
    }

    private void calculateCorrespondences() {
        AssignmentProblem ap = new AssignmentProblem(getMatrixCost());
        setStrokesCorrespondence(ap.solve(new HungarianAlgorithm()));
    }

    private float readCost() {
        int[][] matching = getStrokesCorrespondence();
        float[][] costMatrix = getMatrixCost();
        double cost = 0;
        ArrayList<Point2D> points1, points2;
        double[][] shapeContext1, shapeContext2;
        for (int i = 0; i < matching.length; i++) {
            cost += costMatrix[matching[i][0]][matching[i][1]];

//            points1 = PreprocessingAlgorithms.getNPoints(getSymbol1().get(matching[i][0])
//                    , MatchingParameters.numberOfPointPerSymbol);
//            points2 = PreprocessingAlgorithms.getNPoints(getSymbol2().get(matching[i][1])
//                    , MatchingParameters.numberOfPointPerSymbol);
//            shapeContext1 = CostShapeContextInside.calculateShapeContextMatrixFromPoints2D(points1);
//            shapeContext2 = CostShapeContextInside.calculateShapeContextMatrixFromPoints2D(points2);
//            cost += CostShapeContextInside.getCost(shapeContext1, shapeContext2);
        }
        cost = cost / matching.length;
        return (float) cost;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float getGama() {
        return gama;
    }

    public void setGama(float gama) {
        this.gama = gama;
    }


}
