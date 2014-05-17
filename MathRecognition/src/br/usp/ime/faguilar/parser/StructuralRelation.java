/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.parser;

import br.usp.ime.faguilar.data.DSymbol;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 *
 * @author frank
 */
public class StructuralRelation {
    
    public static List<DSymbol> symbolsSortedByXCoord;
    
    public static float minIntersectionRatio = (float) 0.85;

    static boolean overlaps(DSymbol symbol1, DSymbol symbol2) {
        if(symbol1 != symbol2 && SymbolClass.symbolClass(symbol1) == SymbolClass.NON_SCRIPTED 
                && overlapsX(symbol1, symbol2) && !contains(symbol2, symbol1)
                && condition1Overlaps(symbol1, symbol2) && condition2Overlaps(symbol1, symbol2))
            return true;
        return false;
    }
    
    static boolean condition1Overlaps(DSymbol symbol1, DSymbol symbol2){
        int C = SymbolClass.symbolClass(symbol2);
        double minXSymbol1 = symbol1.getLtPoint().getX();
        double minXSymbol2 = symbol2.getLtPoint().getX();
        if((C == SymbolClass.OPEN_BRACKET || C == SymbolClass.CLOSE_BRACKET) && 
                overlapsY(symbol2, symbol1) && minXSymbol2 <= minXSymbol1)
            return false;
        return true;
    }
    
    static boolean condition2Overlaps(DSymbol symbol1, DSymbol symbol2){
        int C = SymbolClass.symbolClass(symbol2);
        if((C == SymbolClass.NON_SCRIPTED || C == SymbolClass.VARIABLE_RANGE) && 
                symbol2.getWidthAsDouble() > symbol1.getWidthAsDouble())
            return false;
        return true;
    }    
    
    static boolean overlapsX(DSymbol symbol1, DSymbol symbol2){
        if(symbol1.getLtPoint().getX() <= symbol2.getBoundingBoxCenter().getX() 
                && symbol2.getBoundingBoxCenter().getX() < symbol1.getRbPoint().getX())
            return true;
        return false;
    }
    
    public static boolean horizontalOverlappingGraterThan(DSymbol s1, DSymbol s2, float threashold){
        double minY_Intersection = Math.max(s1.getLtPoint().getY(), s2.getLtPoint().getY());
        double maxY_Intersection = Math.min(s1.getRbPoint().getY(), s2.getRbPoint().getY());
        if(maxY_Intersection >= minY_Intersection){
            double minHeight = Math.min(s1.getHeightAsDouble(), s2.getHeightAsDouble());
            if(minHeight > 0){
                double intersection = (maxY_Intersection - minY_Intersection) / minHeight;
                if(intersection >= threashold)
                    return true;
            }else if(minY_Intersection == maxY_Intersection)
                return true;
            
        }
        return false;
    }
    
    public static boolean VerticalOverlappingGraterThan(DSymbol s1, DSymbol s2, float threashold) {
        double minX_Intersection = Math.max(s1.getLtPoint().getX(), s2.getLtPoint().getX());
        double maxX_Intersection = Math.min(s1.getRbPoint().getX(), s2.getRbPoint().getX());
        if(maxX_Intersection >= minX_Intersection){
            double minWidth = Math.min(s1.getWidthAsDouble(), s2.getWidthAsDouble());
            if(minWidth > 0){
                double intersection = (maxX_Intersection - minX_Intersection) / minWidth;
                if(intersection >= threashold)
                    return true;
            }else if(minX_Intersection == maxX_Intersection)
                return true;
        }
        return false;
    }
    
    public static boolean VerticalOverlappingGraterThan(Rectangle2D r1, Rectangle2D r2, float threashold) {
        double minX_Intersection = Math.max(r1.getMinX(), r2.getMinX());
        double maxX_Intersection = Math.min(r1.getMaxX(), r2.getMaxX());
        if(maxX_Intersection >= minX_Intersection){
            double minWidth = Math.min(r1.getWidth(), r2.getWidth());
            if(minWidth > 0){
                double intersection = (maxX_Intersection - minX_Intersection) / minWidth;
                if(intersection >= threashold)
                    return true;
            }else if(minX_Intersection == maxX_Intersection)
                return true;
        }
        return false;
    }
    
    /**
     * determine width ratio between the shortest and the largest of the 
     * arguments
     * @param s1
     * @param s2
     * @return 
     */
    public static double widthRatio(DSymbol s1, DSymbol s2) {
        double ratio = 0;
        double minWidth = Math.min(s1.getWidthAsDouble(), s2.getWidthAsDouble());
        double maxWidth = Math.max(s1.getWidthAsDouble(), s2.getWidthAsDouble());
        if(maxWidth > 0){
            ratio = minWidth / maxWidth;
        }
        return ratio;
    }
    
    /**
     * determine height ratio between the shortest and the largest of the 
     * arguments
     * @param s1
     * @param s2
     * @return 
     */
    public static double heightRatio(DSymbol s1, DSymbol s2) {
        double ratio = 0;
        double minHeight = Math.min(s1.getHeightAsDouble(), s2.getHeightAsDouble());
        double maxHeight = Math.max(s1.getHeightAsDouble(), s2.getHeightAsDouble());
        if(maxHeight > 0){
            ratio = minHeight / maxHeight;
        }
        return ratio;
    }
    
    static boolean overlapsY(DSymbol symbol1, DSymbol symbol2){
        double centroidYOfSymbol2 = SymbolClass.centroidY(symbol2);
        if(symbol1.getLtPoint().getY() <= centroidYOfSymbol2
                && centroidYOfSymbol2 < symbol1.getRbPoint().getY())
            return true;
        return false;
    }

    /**
     * checks if a symbol1 dominates (previous symbol in X coords) symbol2
     * @param symbol
     * @param symbol2
     * @return 
     */
    static boolean symbolDominatesPreviousSymbol(DSymbol symbol1, DSymbol symbol2) {
        if(overlaps(symbol1, symbol2) || contains(symbol1, symbol2) || 
                (SymbolClass.symbolClass(symbol1) == SymbolClass.VARIABLE_RANGE && 
                !isAdjacentUsingClassifier(symbol2, symbol1)))
            return true;
        return false;
    }

    static boolean isRegularHor(DSymbol symbol1, DSymbol symbol2) {
        int symbolClass = SymbolClass.symbolClass(symbol2);
        if(isAdjacentUsingClassifier(symbol2, symbol1) || (symbol1.getLtPoint().getY() >= 
                symbol2.getLtPoint().getY() && symbol1.getRbPoint().getY() <= 
                symbol2.getRbPoint().getY()) || ((symbolClass == SymbolClass.OPEN_BRACKET 
                || symbolClass == SymbolClass.CLOSE_BRACKET) && overlapsY(symbol2, symbol1)))
            return true;
        
        return false;
    }
    
    static SymbolNode checkOverlap(SymbolNode aSymbol, List<SymbolNode> nodes) {
        SymbolNode overlappingSymbol = checkOverlap(aSymbol.getSymbol(), nodes);
        if(overlappingSymbol == null)
            return aSymbol;
        return overlappingSymbol;
    }
    
    static SymbolNode checkOverlap(DSymbol aSymbol, List<SymbolNode> nodes) {
        SymbolNode overlappingSymbol = null;
        for (SymbolNode symbolNode : nodes) {
            if(symbolNode.getSymbol() != aSymbol){
                if(overlaps(symbolNode.getSymbol(), aSymbol)){
                    if(overlappingSymbol == null)
                        overlappingSymbol = symbolNode;
                    else if(symbolNode.getSymbol().getWidthAsDouble() > 
                            overlappingSymbol.getSymbol().getWidthAsDouble())
                        overlappingSymbol = symbolNode;
                }
            }
        }
        return overlappingSymbol;
    }
    
    

    public static boolean contains(DSymbol symbol1, DSymbol symbol2) {
        if(symbol1 != symbol2 && SymbolClass.symbolClass(symbol1) == SymbolClass.ROOT 
                && RelationClassifier.isInsideRelation(symbol1, symbol2))//overlapsX(symbol1, symbol2) && overlapsY(symbol1, symbol2))
            return true;
        return false;
    }

    public static boolean isAdjacent(DSymbol symbol1, DSymbol symbol2) {
        double centroidYSymbol1 = SymbolClass.centroidY(symbol1);
        if(SymbolClass.symbolClass(symbol2) != SymbolClass.NON_SCRIPTED 
                && SymbolClass.symbolClass(symbol2) != SymbolClass.OPEN_BRACKET &&
                symbol1 != symbol2 && !isYCoordAtSubScriptRegion(symbol2, centroidYSymbol1) && 
                !isYCoordAtSuperScriptRegion(symbol2, centroidYSymbol1))
            return true;
        return false;
    }
    
    public static boolean isAdjacentUsingClassifier(DSymbol symbol1, DSymbol symbol2) {
        if(SymbolClass.symbolClass(symbol2) != SymbolClass.NON_SCRIPTED 
//                && SymbolClass.symbolClass(symbol2) != SymbolClass.OPEN_BRACKET 
                && symbol1 != symbol2 && RelationClassifier.isInHorizontalZone(symbol2, symbol1))
            return true;
        return false;
////        
//        double centroidYSymbol1 = SymbolClass.centroidY(symbol1);
//        if(SymbolClass.symbolClass(symbol2) != SymbolClass.NON_SCRIPTED 
//                && SymbolClass.symbolClass(symbol2) != SymbolClass.OPEN_BRACKET &&
//                symbol1 != symbol2 && !isYCoordAtSubScriptRegion(symbol2, centroidYSymbol1) && 
//                !isYCoordAtSuperScriptRegion(symbol2, centroidYSymbol1))
//            return true;
//        return false;
    }
    
    public static double rightEndOfRegion(DSymbol symbol){
        Double rightEnd = Double.MAX_VALUE;
        double maxX = symbol.getRbPoint().getX();
        for (int i = 0; i < symbolsSortedByXCoord.size(); i++) {
            if(symbolsSortedByXCoord.get(i).getLtPoint().getX() > maxX)
                if(isAdjacentUsingClassifier(symbolsSortedByXCoord.get(i), symbol) || 
                        horizontalOverlappingGraterThan(symbol, symbolsSortedByXCoord.get(i), minIntersectionRatio)){
                    rightEnd = symbolsSortedByXCoord.get(i).getLtPoint().getX();
                    break;
                }
        }
        return rightEnd;
    }
    
    public static double leftEndOfRegion(DSymbol symbol){
        Double rightEnd = Double.MIN_VALUE;
        double minX = symbol.getLtPoint().getX();
        for (int i = symbolsSortedByXCoord.size() -1 ; i >= 0; i--) {
            if(symbolsSortedByXCoord.get(i).getRbPoint().getX() < minX)
                if(isAdjacentUsingClassifier(symbol, symbolsSortedByXCoord.get(i)) || 
                        horizontalOverlappingGraterThan(symbol, symbolsSortedByXCoord.get(i), minIntersectionRatio)){
                    rightEnd = symbolsSortedByXCoord.get(i).getRbPoint().getX();
                    break;
                }
        }
        return rightEnd;
    }
    
    public static boolean isYCoordAtSubScriptRegion(DSymbol symbol, double Y){
        double subscriptTreahol = SymbolClass.subscript(symbol);
        if(subscriptTreahol < Y)
            return true;
        return false;
    }
    
    public static boolean isYCoordAtLeftSubScriptRegion(DSymbol symbol, double Y){
        double subscriptTreahol = SymbolClass.leftSubscript(symbol);
        if(subscriptTreahol < Y)
            return true;
        return false;
    }
    
    public static boolean isYCoordAtSuperScriptRegion(DSymbol symbol, double Y){
        double superscriptTreahol = SymbolClass.superscript(symbol);
        if(superscriptTreahol > Y)
            return true;
        return false;
    }
    
    public static boolean isYCoordAtLeftSuperScriptRegion(DSymbol symbol, double Y){
        double superscriptTreahol = SymbolClass.leftSuperscript(symbol);
        if(superscriptTreahol > Y)
            return true;
        return false;
    }
    
    public static boolean isYCoordAtAboveRegion(DSymbol symbol, double Y){
        double aboveTreashold = SymbolClass.above(symbol);
        if(aboveTreashold > Y)
            return true;
        return false;
    }
    
    public static boolean isYCoordAtBelowRegion(DSymbol symbol, double Y){
        double belowTrheashold = SymbolClass.below(symbol);
        if(belowTrheashold < Y)
            return true;
        return false;
    }
    
    public static boolean isXcoordAtAboveOrBelowRegions(DSymbol symbol, double xCoord){
        int symbolClass = SymbolClass.symbolClass(symbol);
        double minX = symbol.getLtPoint().getX();
        double maxX = symbol.getRbPoint().getX();
        if(symbolClass == SymbolClass.VARIABLE_RANGE){
            minX = leftEndOfRegion(symbol);
            maxX = rightEndOfRegion(symbol);
        }
        if(minX < xCoord && xCoord < maxX)
            return true;
        return false;
    }
    
    public static boolean isXcoordAtSuperOrSubscriptRegions(DSymbol symbol, double xCoord){
        int symbolClass = SymbolClass.symbolClass(symbol);
        if(symbolClass == SymbolClass.VARIABLE_RANGE || 
                symbolClass == SymbolClass.NON_SCRIPTED || 
                symbolClass == SymbolClass.OPEN_BRACKET){
            return false;
        }
        double maxX = symbol.getRbPoint().getX();
        double rightEnd = rightEndOfRegion(symbol);
        if(xCoord > maxX && xCoord < rightEnd)
            return true;
        return false;
    }
    
    public static boolean isXcoordAtTopLEftOrBottomLeftRegions(DSymbol symbol, double xCoord){
//        int symbolClass = SymbolClass.symbolClass(symbol);
//        if(symbolClass == SymbolClass.VARIABLE_RANGE || 
//                symbolClass == SymbolClass.NON_SCRIPTED || 
//                symbolClass == SymbolClass.OPEN_BRACKET)
//            return false;
        double minX = symbol.getLtPoint().getX();
        double leftEnd = leftEndOfRegion(symbol);
        if(xCoord < minX && xCoord > leftEnd)
            return true;
        return false;
    }
    
    public static boolean isSymbolAtAboveRegion(DSymbol symbol1, DSymbol symbol2){
        double centroidX = symbol2.getBoundingBoxCenter().getX();
        double centroidY = SymbolClass.centroidY(symbol2);
        if(isXcoordAtAboveOrBelowRegions(symbol1, centroidX) && 
                isYCoordAtAboveRegion(symbol1, centroidY))
            return true;
        return false;
//        return RelationClassifier.isAboveRelation(symbol1, symbol2);
    }
    
    
    public static boolean isSymbolAtBelowRegion(DSymbol symbol1, DSymbol symbol2){
        double centroidX = symbol2.getBoundingBoxCenter().getX();
        double centroidY = SymbolClass.centroidY(symbol2);
        if(isXcoordAtAboveOrBelowRegions(symbol1, centroidX) && 
                isYCoordAtBelowRegion(symbol1, centroidY))
            return true;
        return false;
//        return RelationClassifier.isBelowRelation(symbol1, symbol2);
    }
    
    public static boolean isSymbolAtSuperscriptRegion(DSymbol symbol1, DSymbol symbol2){
        double centroidX = symbol2.getBoundingBoxCenter().getX();
        double centroidY = SymbolClass.centroidY(symbol2);
        if(isXcoordAtSuperOrSubscriptRegions(symbol1, centroidX) && 
                isYCoordAtSuperScriptRegion(symbol1, centroidY))
            return true;
        return false;
    }
    
    public static boolean isSymbolAtTopLeftRegion(DSymbol symbol1, DSymbol symbol2){
//        double centroidX = symbol2.getBoundingBoxCenter().getX();
//        double centroidY = SymbolClass.centroidY(symbol2);
//        if(isXcoordAtTopLEftOrBottomLeftRegions(symbol1, centroidX) && 
//                isYCoordAtLeftSuperScriptRegion(symbol1, centroidY))
        
        double centroidX2 = symbol2.getBoundingBoxCenter().getX();
        double centroidY2 = SymbolClass.centroidY(symbol2);
        double centroidY1 = SymbolClass.centroidY(symbol1);
        double minX = symbol1.getLtPoint().getX();
        if(centroidY2 < centroidY1 && centroidX2 < minX)
            return true;
//        if(RelationClassifier.isTopLeftRelation(symbol1, symbol2) && centroidX2 < minX)
//            return true;
        return false;
    }
    
    public static boolean isSymbolAtSubscriptRegion(DSymbol symbol1, DSymbol symbol2){
        double centroidX = symbol2.getBoundingBoxCenter().getX();
        double centroidY = SymbolClass.centroidY(symbol2);
        if(isXcoordAtSuperOrSubscriptRegions(symbol1, centroidX) && 
                isYCoordAtSubScriptRegion(symbol1, centroidY))
            return true;
        return false;
    }
    
    public static boolean isSymbolAtBottomLeftRegion(DSymbol symbol1, DSymbol symbol2){
        double centroidX2 = symbol2.getBoundingBoxCenter().getX();
        double centroidY2 = SymbolClass.centroidY(symbol2);
        double centroidY1 = SymbolClass.centroidY(symbol1);
        double minX = symbol1.getLtPoint().getX();
        if(centroidY2 >= centroidY1 && centroidX2 < minX)
            return true;
        return false;
    }
}
