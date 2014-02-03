/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.classification.neuralNetwork;
import br.usp.ime.faguilar.util.FilesUtil;
import java.util.Arrays;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
/**
 *
 * @author Frank Aguilar
 */
public class ClassifiactionDataSet {
    private DataSet dataset;
    private int inputSize;
    private int outputSize;
    
    public static ClassifiactionDataSet createDatasetWithInputAndOutputSize(int 
            inputSize, int outputSize){
        ClassifiactionDataSet classificaClassifiactionDataSet = new ClassifiactionDataSet();
        classificaClassifiactionDataSet.setInputsize(inputSize);
        classificaClassifiactionDataSet.setOutputSize(outputSize);
        return classificaClassifiactionDataSet;
    }
    
    public void readDatasetInIVCFormat(String path, String separator){
        double[][] dataMatrix = FilesUtil.readNumbersInMatrix(path, separator);
        fillDataSetWithIVCMatrix(dataMatrix);
    }

    public int getInputsize() {
        return inputSize;
    }

    public void setInputsize(int inputsize) {
        this.inputSize = inputsize;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }

    private void fillDataSetWithIVCMatrix(double[][] dataMatrix) {
        dataset = new DataSet(inputSize, outputSize);
        double[] input;
        double[] output;
        int labelPosition = 0;
        DataSetRow row;
        for (double[] dataLine : dataMatrix) {
            input = Arrays.copyOfRange(dataLine, labelPosition + 1, dataMatrix[0].length);
            output = new double[outputSize];
            output[(int) dataLine[labelPosition]] = 1.;
            row = new DataSetRow(input, output);
            row.setLabel(String.valueOf(dataLine[labelPosition]));
            dataset.addRow(row);
        }
    }
        
    public static void testDataSet(){
        ClassifiactionDataSet dataset = 
                ClassifiactionDataSet.createDatasetWithInputAndOutputSize(210, 75);
        String path = "D:\\frank_temp\\NeededFilesForInterface_learningAnMLP\\"
                + "train-test\\trainOnLineFeatures.data";
        dataset.readDatasetInIVCFormat(path, "\t");
        System.out.println("dataset size:" + dataset.getDataset().size());
    }

    public DataSet getDataset() {
        return dataset;
    }
    
}
