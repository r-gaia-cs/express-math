/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction;

import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.TimePoint;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author Frank
 */
public class PointsExtractor {
    private static int currentStroke;
    private static int currentPoisitionInStroke;
    private static int strokesOfSizeOne;
    private static double separation;
    private static double difference;
    private static DSymbol symbol;
    private static Point2D[] points;
    private static int sampledPoints;
    private static Point2D currentPointToMeasureDistance;
    
    public static Point2D[] getNPoints(DSymbol aSymbol, int N){
        points = new Point2D[N];
        currentStroke = 0;
        currentPoisitionInStroke = 0;
        symbol = aSymbol;
        strokesOfSizeOne = strokesOFSizeOne(symbol);
        double length = PreprocessingAlgorithms.getLength(symbol);
        if (length > 0){
            separation = length / (N - strokesOfSizeOne - 1);
            sampledPoints = 0;
            difference = separation;
            while (sampledPoints < N){
                addAPoint();
            }
            
        } else {
            int pointsPerStroke = Math.round(N / symbol.size());
            double x; //= symbol.get(0).get(0).getX();
            double y; //= symbol.get(0).get(0).getY();
            int stroke = 0;
            int count = 0;
            for (int i = 0; i < N; i++) {
                x = symbol.get(stroke).get(0).getX();
                y = symbol.get(stroke).get(0).getY();
                points[i] = new Point2D.Double(x, y);
                count++;
                if(count >= pointsPerStroke){
                    stroke++;
                    count = 0;
                }
                if(stroke >= symbol.size())
                    stroke--;
            }
        }
        return points; 
    }
    
    

    private static int strokesOFSizeOne(DSymbol symbol) {
        int count = 0;
        for (DStroke dStroke : symbol) {
            if(dStroke.size() == 1)
                count ++;
        }
        return count;
    }

    private static void addAPoint() {
        double distanceTemp;
        if(sampledPoints == 0){
            points[sampledPoints] = new Point2D.Double(
                    symbol.get(currentStroke).get(currentPoisitionInStroke).getX(), 
                    symbol.get(currentStroke).get(currentPoisitionInStroke).getY());
            currentPointToMeasureDistance = symbol.get(currentStroke).get(currentPoisitionInStroke);
            sampledPoints++;
            currentPoisitionInStroke++;
        } else if(currentStroke < symbol.size() && symbol.get(currentStroke).size() == 1 && 
                currentPoisitionInStroke < 1) {
            points[sampledPoints] = new Point2D.Double(
                    symbol.get(currentStroke).get(currentPoisitionInStroke).getX(), 
                    symbol.get(currentStroke).get(currentPoisitionInStroke).getY());
            advanceStroke();
            sampledPoints++;
        } else if(currentStroke < symbol.size()){
            double accumulatedDistance = 0;
            while(currentPoisitionInStroke < symbol.get(currentStroke).size()){
//                if (difference <= accumulatedDistance + currentPointToMeasureDistance.distance(
//                symbol.get(currentStroke).get(currentPoisitionInStroke)))
                distanceTemp = accumulatedDistance + currentPointToMeasureDistance.distance(
                symbol.get(currentStroke).get(currentPoisitionInStroke));
                if (differenceLessThanAccumulated(difference, distanceTemp))
                    break;
                accumulatedDistance = distanceTemp;
//                        currentPointToMeasureDistance.distance(
//                symbol.get(currentStroke).get(currentPoisitionInStroke));
                currentPointToMeasureDistance = symbol.get(currentStroke).get(currentPoisitionInStroke);
                currentPoisitionInStroke ++;
            }
            if (currentPoisitionInStroke < symbol.get(currentStroke).size() && 
//                    difference <= accumulatedDistance + currentPointToMeasureDistance.distance(
//                symbol.get(currentStroke).get(currentPoisitionInStroke))){
                    differenceLessThanAccumulated(difference, accumulatedDistance + currentPointToMeasureDistance.distance(
                symbol.get(currentStroke).get(currentPoisitionInStroke)))){
               points[sampledPoints] = linearInterpolation(currentPointToMeasureDistance, 
                       symbol.get(currentStroke).get(currentPoisitionInStroke), difference - 
                               accumulatedDistance);
               difference = separation;
               currentPointToMeasureDistance = points[sampledPoints];
               sampledPoints++;
            }
            else {
                difference -= accumulatedDistance;
                advanceStroke();
//                if(currentStroke < symbol.size())
                    addAPoint();
            }
        } else {
            int lastStrokeSize = symbol.get(currentStroke - 1).size();
            double x = symbol.get(currentStroke - 1).get(lastStrokeSize - 1).getX();
            double y = symbol.get(currentStroke - 1).get(lastStrokeSize - 1).getY();
            points[sampledPoints] = new Point2D.Double(x, y);
            sampledPoints++;
        }
    }
    
    public static boolean differenceLessThanAccumulated(double val1, double val2){
        double diference = val1 - val2;
        return (diference < 0.000001);
    }
    
    private static void advanceStroke(){
        currentStroke++;
        currentPoisitionInStroke = 0;
        if(currentStroke < symbol.size())
            currentPointToMeasureDistance = symbol.get(currentStroke).get(currentPoisitionInStroke);
        
    }

    private static Point2D linearInterpolation(Point2D point1, Point2D point2, double difference) {
        double distance = point1.distance(point2);
        double ratio = difference / distance;
        Point2D newPoint = new Point2D.Double(point1.getX() + ratio * (point2.getX() - 
                point1.getX()), point1.getY() + ratio * (point2.getY() - 
                point1.getY()));
        return newPoint;
    }
    
    
}
