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
import br.usp.ime.faguilar.segmentation.Segmentation;
import br.usp.ime.faguilar.segmentation.TreeSearchSegmentation;
import br.usp.ime.faguilar.util.FilesUtil;
import br.usp.ime.faguilar.util.SymbolUtil;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import java.io.File;
import java.util.ArrayList;

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

    public SegmentationAndClassificationEvaluator() {
        ArrayList<Classifible> classifibles = SymbolUtil.readSymbolData(
                EvaluationView.TEMPLATES_FILE);
        classifier = new ShapeContextClassifier();
        classifier.setTrainingData(classifibles);
        classifier.train();
        setSegmentationAlgorithm(SEGMENTATION_LINEAR_ALGORITHM);
        filterByDistance = true;
        filterByMST = false;
    }



    public void runEvaluation(){
        chargeFileNames();
        String resultFileNames = "";
        int count = 0;
        for (String fileName : getInkFiles()) {
            if(fileName.endsWith(".inkml")){
            count++;
            System.out.println("count: "+ count);
            System.out.println("processing: " + fileName);
            partExpressionFromINKMLFile(fileName);
            resultFileNames += (fileName + ", "+ fileName.substring(0,
                    fileName.length() - 6) + "-res.inkml"+ "\n");
            }
        }
        FilesUtil.write("fileNames.txt", resultFileNames);
    }

    private void chargeFileNames(){
        inkFiles = new ArrayList<String>();
        File file = null;
        try {
            file = new File (EvaluationView.INKML_DIR);
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                inkFiles.add(file1.getName());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public String  partExpressionFromINKMLFile(String inkFileName){
        String stringResult = "";
        InkMLInput inkMlInput = new InkMLInput();
        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile(EvaluationView.INKML_DIR + inkFileName);
        strokes = PreprocessingAlgorithms.preprocessStrokes(strokes);
        EdgeWeightedGraph StrokeSetToEdgeWeightedGraph = MathExpressionGraph.StrokeSetToEdgeWeightedGraph(strokes);
        KruskalMST mst = new KruskalMST(StrokeSetToEdgeWeightedGraph);

        double meandistance = 0;
        double filterMaxDistance;
        double alpha = 0.8;
        double beta = 0.6;
        for(Edge e: mst.edges())
            meandistance += e.weight();
        meandistance = meandistance / (StrokeSetToEdgeWeightedGraph.V() - 1);
        filterMaxDistance = alpha * meandistance;
        double mindistance = beta * meandistance;
        Segmentation segmentation = null;
        if(getSegmentationAlgorithm() == SEGMENTATION_TREE_ALGORITHM){
            segmentation =  new TreeSearchSegmentation();
            ((TreeSearchSegmentation) segmentation).setMinDist(mindistance);
        }else if(getSegmentationAlgorithm() == SEGMENTATION_LINEAR_ALGORITHM)
            segmentation =  new Segmentation();
        segmentation.setClassifier(classifier);
        segmentation.setTruncateByDistance(filterByDistance);
        segmentation.setMaxDistanceBetweenStrokes(filterMaxDistance);
        segmentation.setTruncateByMST(filterByMST);
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
            FilesUtil.write(EvaluationView.INKML_DIR + inkFileName.substring(0, inkFileName.length() - 6)
                    + "-res.inkml", inkmlTex);

        return stringResult;
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
