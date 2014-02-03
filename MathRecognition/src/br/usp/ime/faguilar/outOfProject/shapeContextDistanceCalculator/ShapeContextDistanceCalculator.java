/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.outOfProject.shapeContextDistanceCalculator;

import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import java.awt.geom.Point2D;

/**
 *
 * @author Frank Aguilar
 */
public class ShapeContextDistanceCalculator {
    
    public float calculateDistance(String[] stringPoints){
        Point2D[][] points = extractPointsFromStrings(stringPoints);
        return calculateDistance(points[0],points[1]);
    }
    
    public float calculateDistance(Point2D[] pointsSet1, Point2D[] pointsSet2){
        float distance = 0;
        ShapeContext scontext1 = CostShapeContextInside.calculateShapeContextFromPoints2D(pointsSet1);
        ShapeContext scontext2 = CostShapeContextInside.calculateShapeContextFromPoints2D(pointsSet2);
        distance = CostShapeContextInside.getCost(scontext1.getSC(), scontext2.getSC());
        return distance;
    }

    private Point2D[][] extractPointsFromStrings(String[] stringPoints) {
        int numberOfPointsPerSymbol = stringPoints.length / 4;
        int secondHalfStart = stringPoints.length / 2;
        int numberOfSymbols = 2;
        Point2D[][] points = new Point2D[numberOfSymbols][numberOfPointsPerSymbol];
        int coordPosition = 0;
        for (int i = 0; i < numberOfPointsPerSymbol; i++) {
            points[0][i] = new Point2D.Float(Float.valueOf(stringPoints[coordPosition]), 
                    Float.valueOf(stringPoints[coordPosition + 1]));
            
            points[1][i] = new Point2D.Float(Float.valueOf(stringPoints[secondHalfStart + coordPosition]), 
                    Float.valueOf(stringPoints[secondHalfStart + coordPosition + 1]));
            coordPosition += 2;
        }
        return points;
    }
    
}
