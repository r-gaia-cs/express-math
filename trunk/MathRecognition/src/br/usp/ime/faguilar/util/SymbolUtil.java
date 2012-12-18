/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.matching.MatchingParameters;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author frank
 */
public class SymbolUtil {
    
    

//    public static ArrayList<Classifible> readSymbolData(String fileName){
//        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
//        String fileContent = RWFiles.getContentAsString(fileName);
////        String[] arrayContent = fileContent.split("\n");
//        Scanner scanner = new Scanner(fileContent);
////        for (int i = 0; i < arrayContent.length; i++) {
////            String string = arrayContent[i];
////
////        }
//        String label = "";
//        Point2D.Double[] points = null;
//        Point2D.Double newPoint = null;
////        int dataIndex = 0;
//        while(scanner.hasNext()){
////            for (Classifible classifible : symbolData) {
//            if(scanner.hasNextDouble()){
//                points = new Point2D.Double[MatchingParameters.numberOfPointPerSymbol];
//                for (int i = 0; i < MatchingParameters.numberOfPointPerSymbol; i++) {
//                    newPoint = new Point2D.Double(scanner.nextDouble(),
//                            scanner.nextDouble());
//                    points[i] = newPoint;
//                }
//                label = scanner.next();
//                Classifible<Point2D.Double> newClassifible = new Classifible();
//                newClassifible.setFeatures(points);
//                newClassifible.setMyClass(label);
//                symbolData.add(newClassifible);
//            }else{
//                if(scanner.next("#").equals("#"))
//                    scanner.nextLine();
//            }
////                dataIndex++;
////            }
//        }
//        return symbolData;
//        
////        public static boolean areCrossing(){
////
////        }
//    }

    public static ArrayList<Classifible> readSymbolData(String fileName){
        ArrayList<Classifible> symbolData = new ArrayList<Classifible>();
        String fileContent = RWFiles.getContentAsString(fileName);
        String[] arrayContent = fileContent.split("\n");
//        Scanner scanner = new Scanner(fileContent);
        String label = "";
        Point2D.Double[] points = null;
        Point2D.Double newPoint = null;
        String[] line;
        int j;
        for (int i = 0; i < arrayContent.length; i++) {
            line = arrayContent[i].split(" ");
            if(!line[0].equals("#")){
                points = new Point2D.Double[MatchingParameters.numberOfPointPerSymbol];
                for (j = 0; j < MatchingParameters.numberOfPointPerSymbol; j++) {
                    newPoint = new Point2D.Double(Double.valueOf(line[2*j]), 
                            Double.valueOf(line[2*j + 1]));
                    points[j] = newPoint;
                }
                label = line[2*j];
                Classifible<Point2D.Double> newClassifible = new Classifible();
                newClassifible.setFeatures(points);
                newClassifible.setMyClass(label);
                symbolData.add(newClassifible);
            }
        }
        
        return symbolData;
        
//        public static boolean areCrossing(){
//
//        }
    }
    
    public static boolean nearEndsToStrokeFromTo(DStroke s1, DStroke s2){
        boolean nearEnds = false;
        double minDistance = 5;
        if(s1.size() >1){
            for (Point2D point : s2) {
                if(s1.get(0).distance(point) < minDistance ||
                        s1.get(s1.size() -1).distance(point) < minDistance){
                    nearEnds = true;
                    break;
                }
            }
        }
         else if(s1.size() == 1){
            for (Point2D point : s2) {
                    if(s1.get(0).distance(point) < minDistance){
                        nearEnds = true;
                        break;
                    }
            }
         }
        return nearEnds;
    }
    public static boolean nearEnds(DStroke s1, DStroke s2){
        boolean nearEnds = false;
        if(nearEndsToStrokeFromTo(s1, s2) ||
                nearEndsToStrokeFromTo(s2, s1))
            nearEnds = true;
        return nearEnds;
    }
}
