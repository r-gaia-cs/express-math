/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util.results;

import br.usp.ime.faguilar.conversion.InkmlReader;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.guis.EvaluationView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author frank
 */
public class results {
    public static final String resultsFile = EvaluationView.INKML_DIR + "results-perl.txt";
    public static final String pairFileNames = EvaluationView.INKML_DIR + "fileNames.txt";
    public static void main(String[] args){
        printResultsWithExpressionSizes();

    }

    public static void printResultsWithExpressionSizes(){
        ArrayList<ExpressionResults> readResults = ExpressionResults.readResults(resultsFile);
        HashMap<String, Integer> fileNameVsSize = readSizes();
        int numberOfSymbols;
        for (ExpressionResults expressionResults : readResults) {
            numberOfSymbols = fileNameVsSize.get(expressionResults.getExpressionName());
            expressionResults.setNumberOfSymbols(numberOfSymbols);
        }
        printExpressionResults(readResults);
    }

    private static HashMap<String, Integer> readSizes() {
        HashMap<String, Integer> fileNameVsSize = new HashMap<String, Integer>();
        ArrayList<InputOutputFiles> readPairFiles = InputOutputFiles.readPairFiles(pairFileNames);
        InkmlReader reader = new InkmlReader();
        for (InputOutputFiles outputFiles : readPairFiles) {
            reader = new InkmlReader();
            reader.read(EvaluationView.INKML_DIR + outputFiles.getInputFile());
            DMathExpression asDMathExpression = reader.getMathExpression().asDMathExpression();
            fileNameVsSize.put(outputFiles.getOutputFile(), asDMathExpression.size());
        }
        return fileNameVsSize;
    }

    private static void printExpressionResults(ArrayList<ExpressionResults> readResults) {
        for (ExpressionResults expressionResults : readResults) {
            System.out.printf("%s %d %f %f %f\n", expressionResults.getExpressionName(),
                    expressionResults.getNumberOfSymbols(), expressionResults.getStrokeClassificationRate(),
                    expressionResults.getSegmentationRate(), expressionResults.getSymbolRecognitionRate());
        }
    }



}
