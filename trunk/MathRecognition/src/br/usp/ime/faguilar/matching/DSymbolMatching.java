/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching;

import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import java.awt.geom.Point2D;

/**
 *
 * @author frank
 */
public class DSymbolMatching {

    public static double matchingCost(DSymbol symbol1, DSymbol symbol2){
        double cost = 0;
        Point2D[] points1 = PreprocessingAlgorithms.getNPoints(symbol1, MatchingParameters.numberOfPointPerSymbol);
        Point2D[] points2 = PreprocessingAlgorithms.getNPoints(symbol2, MatchingParameters.numberOfPointPerSymbol);

        return cost;
    }

}
