/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.evaluation;

import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.ShapeContextClassifier;
import br.usp.ime.faguilar.conversion.InkMLInput;
import br.usp.ime.faguilar.conversion.MathExpressionGraph;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.export.InkMLExpression;
import br.usp.ime.faguilar.export.MathExpressionSample;
import br.usp.ime.faguilar.feature_extraction.PreprocessingAlgorithms;
import br.usp.ime.faguilar.graphics.GMathExpression;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.matching.GraphMatching;
import br.usp.ime.faguilar.segmentation.Segmentation;
import br.usp.ime.faguilar.segmentation.SegmentationParameters;
import br.usp.ime.faguilar.segmentation.TreeSearchSegmentation;
import br.usp.ime.faguilar.util.FilesUtil;
import br.usp.ime.faguilar.util.SymbolUtil;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 *
 * @author frank
 */
public class SegmentationAndClassificationEvaluator {
    private int segmentationAlgorithm;

    private static final int SEGMENTATION_TREE_ALGORITHM = 1;

    private static final int SEGMENTATION_LINEAR_ALGORITHM= 2;

    private  ShapeContextClassifier classifier;

    private boolean filterByDistance;
    private boolean filterByMST;

    private ArrayList<String> inkFiles;

    private String fileNames;
    private SegmentationParameters parameters;
    private Segmentation segmentation;

    public SegmentationAndClassificationEvaluator() {
        ArrayList<Classifible> classifibles = SymbolUtil.readTemplatesFromInkmlFiles();//SymbolUtil.readTemplatesWithStrokesInfo();//SymbolUtil.readTemplates();
        classifier = new ShapeContextClassifier();
        classifier.setTrainingData(classifibles);
        classifier.train();
        this.segmentationAlgorithm = SEGMENTATION_LINEAR_ALGORITHM;
        filterByDistance = true;
        filterByMST = false;
        fileNames = "fileNames.txt";
        parameters = new SegmentationParameters();
        segmentation = null;

        if(segmentationAlgorithm == SEGMENTATION_TREE_ALGORITHM)
            segmentation =  new TreeSearchSegmentation();
        else if(segmentationAlgorithm == SEGMENTATION_LINEAR_ALGORITHM)
            segmentation =  new Segmentation();
        segmentation.setClassifier(classifier);
        segmentation.setTruncateByDistance(filterByDistance);
        segmentation.setTruncateByMST(filterByMST);
    }



    public void runEvaluation(){
//        chargeFileNames();
        String resultFileNames = "";
        int count = 0;
        for (String fileName : getInkFiles()) {
            if(fileName.endsWith(".inkml") && !fileName.contains("hirata") &&
                    !fileName.contains("Nina") && !fileName.contains("fujita")){
                count++;
                System.out.println("count: "+ count);
                System.out.println("processing: " + fileName);
                String resultFileName = partExpressionFromINKMLFile(fileName);
                resultFileNames += (fileName + ", "+ resultFileName + "\n");
                FilesUtil.write(getFileNames(), resultFileNames);
            }
        }
    }

    public void chargeFileNames(){
        String[] files = SymbolUtil.readFilesNamesInAFile(EvaluationView.TEST_FILES);
//        String[] files = SymbolUtil.readFilesNamesInAFile(EvaluationView.TEST_FILES_CROHME);
        inkFiles = new ArrayList<String>();
        inkFiles.addAll(Arrays.asList(files));
//        inkFiles = FilesUtil.getNotHiddenFileNames(EvaluationView.INKML_CROHME_2012_TEST_DIR);
    }

    public String  partExpressionFromINKMLFile(String inkFileName){
//        String stringResult = "";
        String resultFileName = "";
        long time;
        InkMLInput inkMlInput = new InkMLInput();
        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(EvaluationView.INKML_DIR + inkFileName);
//        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(EvaluationView.INKML_CROHME_2012_TEST_DIR + inkFileName);
        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
        EdgeWeightedGraph StrokeSetToEdgeWeightedGraph = MathExpressionGraph.StrokeSetToEdgeWeightedGraph(strokes);
        KruskalMST mst = new KruskalMST(StrokeSetToEdgeWeightedGraph);

        double meandistance = 0;
        double filterMaxDistance;
        float alpha = getParameters().getDistanceFactor();
        double beta = 0.5;
        for(Edge e: mst.edges())
            meandistance += e.weight();
        meandistance = meandistance / (StrokeSetToEdgeWeightedGraph.V() - 1);
        filterMaxDistance = alpha * meandistance;
        double mindistance = beta * meandistance;
        segmentation.setMinDist(mindistance);
        segmentation.setMaxDistanceBetweenStrokes(filterMaxDistance);
        segmentation.setMst(mst);
        segmentation.part(strokes);

        GMathExpression expression = (GMathExpression) segmentation.getPartitionAsDMathExpression();

        MathExpressionSample sample=new MathExpressionSample(
        "id model",
        "nickname",expression);
        sample.setTextualRepresentation(null);
        sample.setCategory("cat");

        InkMLExpression inkMlExpression = new InkMLExpression();
            inkMlExpression.setGroundTruthExpression(sample.getTextualRepresentation());
            inkMlExpression.setSampleExpression(sample);
            inkMlExpression.generateInkML();
            String inkmlTex = inkMlExpression.getInkmlText();
//            count++;
            time = Calendar.getInstance().getTimeInMillis();
            resultFileName = inkFileName.substring(0, inkFileName.length() - 6)
                    + "-res_"+ String.valueOf(time) +".inkml";
            FilesUtil.write(EvaluationView.INKML_DIR + resultFileName, inkmlTex);
//            FilesUtil.write(EvaluationView.INKML_CROHME_2012_TEST_DIR + resultFileName, inkmlTex);
        return resultFileName;
    }

    public SegmentationParameters getParameters() {
        return parameters;
    }

    public void setParameters(SegmentationParameters parameters) {
        this.parameters = parameters;
//        getClassifier().setAlpha(parameters.getAlpha());
        getClassifier().setAlpha(1);
        GraphMatching.ANGLE_WEIGHT = parameters.getAlpha();
        getClassifier().setBeta(parameters.getBeta());
        getClassifier().setGama(parameters.getGama());
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public int getSegmentationAlgorithm() {
        return segmentationAlgorithm;
    }

    public void setSegmentationAlgorithm(int segmentationAlgorithm) {
        this.segmentationAlgorithm = segmentationAlgorithm;
    }

    public ShapeContextClassifier getClassifier() {
        return classifier;
    }

    public void setClassifier(ShapeContextClassifier classifier) {
        this.classifier = classifier;
    }

    public boolean isFilterByDistance() {
        return filterByDistance;
    }

    public void setFilterByDistance(boolean filterByDistance) {
        this.filterByDistance = filterByDistance;
    }

    public boolean isFilterByMST() {
        return filterByMST;
    }

    public void setFilterByMST(boolean filterByMST) {
        this.filterByMST = filterByMST;
    }

    public ArrayList<String> getInkFiles() {
        return inkFiles;
    }

    public void setInkFiles(ArrayList<String> inkFiles) {
        this.inkFiles = inkFiles;
    }
}
