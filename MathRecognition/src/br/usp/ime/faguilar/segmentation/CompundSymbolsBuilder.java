/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.ime.faguilar.segmentation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ClassificationResult;
import br.usp.ime.faguilar.classification.Classifier;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.graphics.GMathExpression;
import br.usp.ime.faguilar.parser.StructuralRelation;
import br.usp.ime.faguilar.parser.SymbolNode;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author frank
 */
public class CompundSymbolsBuilder {
//    private List<DSymbol> inputSymbols;
    private List<DSymbol> compoundSymbols;
    private Classifier classifier;
    private static final int MAX_NUMBER_OF_SEARCH_SYMBOLS = 10;
    private static final int MAX_NUMBER_OF_SYMBOL_COMPONENTS = 3;
    private static float INTERSECTION_RATIO = (float) 0.5;
    private static final int VERTICAL_JOINNING = 1;
    private static final int HORIZONTAL_JOINNING = 2;
    private static final float HEIGHT_RATIO = (float) 0.3;
    private static final float WIDTH_RATIO = (float) 0.4;
    public static CompundSymbolsBuilder builderFromInputSymbols(List<DSymbol> symbols){
        CompundSymbolsBuilder builder = new CompundSymbolsBuilder();
        List<DSymbol> compoundSymbols = new ArrayList<DSymbol>(symbols);
        builder.setCompoundSymbols(compoundSymbols);
        return builder;
    }
    
    public static DMathExpression symbolsAsMathExpression(List<DSymbol> symbols){
        DMathExpression expression = new GMathExpression();
        for (DSymbol dSymbol : symbols) {
            expression.addCheckingBoundingBox(dSymbol);
        }
        return expression;
    }
    
    public void calculateCompoundSymbols(){
        calculateSymbolsWithPoints();
        calculateVerticalCompoundSymbols();
        calculateHorizontalCompoundSymbols();
    }
    
    private void calculateVerticalCompoundSymbols() {
        Collections.sort(compoundSymbols, minYComparator());
        for (int i = 0; i < compoundSymbols.size(); i++) 
             joinSymbolsFromPosition(i, VERTICAL_JOINNING);
    }
    
    private void calculateSymbolsWithPoints() {
        Collections.sort(compoundSymbols, minYComparator());
        Collections.reverse(compoundSymbols);
        for (int i = 0; i < compoundSymbols.size(); i++) 
            if(SymbolLabel.canBePoint(compoundSymbols.get(i)))
                joinSymbolWithPointFromPosition(i);
    }
    
     private void calculateHorizontalCompoundSymbols() {
        Collections.sort(compoundSymbols, minXComparator());
         for (int i = 0; i < compoundSymbols.size(); i++) {
             joinSymbolsFromPosition(i, HORIZONTAL_JOINNING);
         }
    }
     
     private void joinSymbolsFromPosition(int position, int type_ofJoinning) {
         List<Integer> positionsOfSymbolComponents = new ArrayList();
         positionsOfSymbolComponents.add(position);
         int positionOfSymbolToBeEvaluated;
         boolean evaluateJoinning = false;
         for (int j = 0; j < MAX_NUMBER_OF_SEARCH_SYMBOLS && 
                 positionsOfSymbolComponents.size() < MAX_NUMBER_OF_SYMBOL_COMPONENTS; j++) {
             positionOfSymbolToBeEvaluated = position - j - 1;
             if(positionOfSymbolToBeEvaluated >= 0){
                 if(type_ofJoinning == HORIZONTAL_JOINNING)
                     evaluateJoinning = StructuralRelation.horizontalOverlappingGraterThan(compoundSymbols.get(position), 
                        compoundSymbols.get(positionOfSymbolToBeEvaluated), INTERSECTION_RATIO);
                 else if (type_ofJoinning == VERTICAL_JOINNING)
                     evaluateJoinning = StructuralRelation.VerticalOverlappingGraterThan(compoundSymbols.get(position), 
                        compoundSymbols.get(positionOfSymbolToBeEvaluated), INTERSECTION_RATIO);
                if(evaluateJoinning){
                    positionsOfSymbolComponents.add(positionOfSymbolToBeEvaluated);
                     boolean evaluateSymbolLabelsAtPositions = evaluateSymbolLabelsAtPositions(compoundSymbols, positionsOfSymbolComponents);
                     if(evaluateSymbolLabelsAtPositions)
                         break;
                }
             }else
                 break;
         }
    }
     
     private void joinSymbolWithPointFromPosition(int position) {
         List<Integer> positionsOfSymbolComponents = new ArrayList();
         positionsOfSymbolComponents.add(position);
         int positionOfSymbolToBeEvaluated;
         boolean evaluateJoinning;
//         double meanWidth = 0;
//         for (DSymbol dSymbol : compoundSymbols) {
//             meanWidth += dSymbol.getWidthAsDouble();
//         }
//         meanWidth /= compoundSymbols.size();
         int maxComponents = 2;
         for (int j = 0; j < MAX_NUMBER_OF_SEARCH_SYMBOLS && 
                 positionsOfSymbolComponents.size() < maxComponents; j++) {
             positionOfSymbolToBeEvaluated = position - j - 1;
             if(positionOfSymbolToBeEvaluated >= 0){
                     evaluateJoinning = StructuralRelation.VerticalOverlappingGraterThan(compoundSymbols.get(position).getBBox(), 
                        compoundSymbols.get(positionOfSymbolToBeEvaluated).getBBox(), INTERSECTION_RATIO);
                if(evaluateJoinning){
                    positionsOfSymbolComponents.add(positionOfSymbolToBeEvaluated);
                    boolean evaluateSymbolWithPointAtPositions = evaluateSymbolWithPointAtPositions(compoundSymbols, positionsOfSymbolComponents);
                    if(evaluateSymbolWithPointAtPositions)
                        break;
                }
             }else
                 break;
         }
    }
     
     private boolean evaluateSymbolWithPointAtPositions(List<DSymbol> sortedSymbols, 
             List<Integer> positionsOfSymbolComponents) {
         if(correctSizeRatios(sortedSymbols, positionsOfSymbolComponents, "i")){
            DSymbol newSymbol = new DSymbol();
            DSymbol symbolAux;
            for(int position = positionsOfSymbolComponents.size() - 1; position >= 0; 
                    position--){
                symbolAux = sortedSymbols.get(positionsOfSymbolComponents.get(position));
                for (DStroke dStroke : symbolAux)
                    newSymbol.addCheckingBoundingBox(dStroke);
            }  
            Classifible c= new Classifible();
            c.setSymbol(newSymbol);
             ClassificationResult result = (ClassificationResult) classifier.classify(c);
             if(result != null && (result.getMyClass().equals("i") || 
                     result.getMyClass().equals("j"))){
                 joinSymbols(sortedSymbols, positionsOfSymbolComponents, 
                         (String) result.getMyClass());
                 return true;
             }
         }
         return false;
    }
//     public Rectangle2D incrementWidth(Rectangle2D r, double increment){
//         return new Rectangle2D.Double(r.getMinX() - increment / 2, r.getMinY(), 
//                 r.getWidth() + increment, r.getHeight());
//     }
     
    private boolean evaluateSymbolLabelsAtPositions(List<DSymbol> sortedSymbols, List<Integer> positionsOfSymbolComponents) {
        String compoundLabel = joinLabels(sortedSymbols, positionsOfSymbolComponents);
        if(isACompoundSymbolLabel(compoundLabel)){
            if(correctSizeRatios(sortedSymbols, positionsOfSymbolComponents, 
                    determineCompoundLabel(compoundLabel))){
                joinSymbols(sortedSymbols, positionsOfSymbolComponents, determineCompoundLabel(compoundLabel));
                return true;
            }
        }
        return false;
    }
    
    private boolean correctSizeRatios(List<DSymbol> sortedSymbols, 
            List<Integer> positionsOfSymbolComponents, String label) {
//        String compoundLabel = determineCompoundLabel(label);
        if(label.equals("\\pm") || label.equals("=")){
            if(StructuralRelation.widthRatio(sortedSymbols.get(
                    positionsOfSymbolComponents.get(0)), sortedSymbols.get(positionsOfSymbolComponents.get(1)))
                    >= WIDTH_RATIO)
                return true;
            else return false;
        }
        else if(label.equals("i") || label.equals("j")){
            if(StructuralRelation.heightRatio(sortedSymbols.get(
                    positionsOfSymbolComponents.get(0)), sortedSymbols.get(positionsOfSymbolComponents.get(1)))
                    <= HEIGHT_RATIO)
                return true;
            else return false;
        }
        return true;
    }
    
    private String joinLabels(List<DSymbol> sortedSymbols, List<Integer> positionsOfSymbolComponents) {
        String label = "";
        for (int i = positionsOfSymbolComponents.size() - 1; i >= 0; i--) {
            label += sortedSymbols.get(positionsOfSymbolComponents.get(i)).getLabel();
        }
        return label;
    }
    
    private void joinSymbols(List<DSymbol> sortedSymbols, List<Integer> positionsOfSymbolComponents, String label) {
//        String compoundLabel = joinLabels(sortedSymbols, positionsOfSymbolComponents);
//        String label = determineCompoundLabel(compoundLabel);
        DSymbol newSymbol = new DSymbol();
        DSymbol symbolAux;
        for(int position = positionsOfSymbolComponents.size() - 1; position >= 0; 
                position--){
            symbolAux = sortedSymbols.get(positionsOfSymbolComponents.get(position));
            for (DStroke dStroke : symbolAux)
                newSymbol.addCheckingBoundingBox(dStroke);
        }  
        for (int i = 0; i < positionsOfSymbolComponents.size(); i++)
            sortedSymbols.remove((int)  positionsOfSymbolComponents.get(i));
        newSymbol.setLabel(label);
        List<DSymbol> newSymbols = new ArrayList<DSymbol>(sortedSymbols.subList(0, positionsOfSymbolComponents.get(
                positionsOfSymbolComponents.size() - 1)));
        newSymbols.add(newSymbol);
        newSymbols.addAll(sortedSymbols.subList(positionsOfSymbolComponents.get(
                positionsOfSymbolComponents.size() - 1), sortedSymbols.size()));
        sortedSymbols.clear();
        sortedSymbols.addAll(newSymbols);
    }

    private boolean isACompoundSymbolLabel(String compoundLabel) {
        String labelResult = determineCompoundLabel(compoundLabel);
        if(labelResult != null)
            return true;
        return false;
    }
    
    private String determineCompoundLabel(String compoundLabel) {
        String labelResult = null;
        if(SymbolLabel.canBeSin(compoundLabel))
            labelResult = "\\sin";
        else if(SymbolLabel.canBeCos(compoundLabel))
            labelResult = "\\cos";
        else if(SymbolLabel.canBeTan(compoundLabel))
            labelResult = "\\tan";
        else if(SymbolLabel.canBeLog(compoundLabel))
            labelResult = "\\log";
        else if(SymbolLabel.canBeLim(compoundLabel))
            labelResult = "\\lim";
        else if(SymbolLabel.canBePM(compoundLabel))
            labelResult = "\\pm";
        else if(SymbolLabel.canBeEqual(compoundLabel))
            labelResult = "=";
        return labelResult;
    }
     
     public static Comparator<DSymbol> minXComparator(){
        return new Comparator<DSymbol>() {
            public int compare(DSymbol s1, DSymbol s2) {
                return Double.compare(s1.getLtPoint().getX(), 
                        s2.getLtPoint().getX());
            }
        };
    }
     
     public static Comparator<DSymbol> minYComparator(){
        return new Comparator<DSymbol>() {
            public int compare(DSymbol s1, DSymbol s2) {
                return Double.compare(s1.getLtPoint().getY(), 
                        s2.getLtPoint().getY());
            }
        };
    }

    public List<DSymbol> getCompoundSymbols() {
        return compoundSymbols;
    }    

    public void setCompoundSymbols(List<DSymbol> compoundSymbols) {
        this.compoundSymbols = compoundSymbols;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }    
}
