/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util.results;

import br.usp.ime.faguilar.util.FilesUtil;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class InputOutputFiles {
    private String inputFile;
    private String outputFile;

    public static ArrayList<InputOutputFiles> readPairFiles(String fileName){
        ArrayList<InputOutputFiles> pairs = new ArrayList<InputOutputFiles>();
        String content = FilesUtil.getContentAsString(fileName);
        String[] contentArray = content.split("\n");
        String[] resultLineArray;
        InputOutputFiles newPair;
        for (int i = 0; i < contentArray.length; i++) {
           resultLineArray = contentArray[i].split("\\s+|,\\s*");
           newPair = new InputOutputFiles();
           newPair.setInputFile(resultLineArray[0]);
           newPair.setOutputFile(resultLineArray[1]);
           pairs.add(newPair);
        }
        return pairs;
    }


    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
}
