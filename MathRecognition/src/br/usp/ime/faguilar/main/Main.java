/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.main;

import br.usp.ime.faguilar.classification.ShapeContextClassifier;
import br.usp.ime.faguilar.conversion.InkMLInput;
import br.usp.ime.faguilar.conversion.MathExpressionGraph;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.graphics.GraphicalStrokeKruskalMST;
import br.usp.ime.faguilar.guis.DrawingArea;
import br.usp.ime.faguilar.guis.EvaluationView;
import br.usp.ime.faguilar.evaluation.ClassifierTest;
import br.usp.ime.faguilar.evaluation.SymbolTestData;
import br.usp.ime.faguilar.classification.Classifible;
import br.usp.ime.faguilar.classification.SymbolClasses;
import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.evaluation.SegmentationAndClassificationEvaluator;
import br.usp.ime.faguilar.evaluation.ShapeContextStatistics;
import br.usp.ime.faguilar.evaluation.TrainingAndTestDataPartitioner;
import br.usp.ime.faguilar.util.RWFiles;
import br.usp.ime.faguilar.util.SymbolUtil;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.introcs.StdOut;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JFrame;


public class Main {

   

    private Bag<Integer>[] adj;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        args=new String[]{"tinyEWG.txt"};
//        args=new String[]{"mediumEWG.txt"};

//        KruskalMST.main(args);
//        String content = RWFiles.getContentAsString("src/data/65_alfonso.inkml");
//        System.out.println(content);

//        JGraphT library
//        KruskalMinimumSpanningTree<String, DefaultEdge> mst =
//                KruskalMinimumSpanningTree<String, DefaultEdge>(createStringGraph());
//        end JGraphT library

//        InkMLInput inkMlInput = new InkMLInput();
//        ArrayList<DStroke> strokes = inkMlInput.extractStrokesFromInkMLFile("src/data/83_alfonso.inkml");
//        EdgeWeightedGraph StrokeSetToEdgeWeightedGraph =
//                MathExpressionGraph.StrokeSetToEdgeWeightedGraph(strokes);
//
//        KruskalMST mst = new KruskalMST(StrokeSetToEdgeWeightedGraph);


//        for (Edge e : mst.edges()) {
//            StdOut.println(e);
//        }
//        StdOut.printf("%.5f\n", mst.weight());

//        GraphicalStrokeKruskalMST graphicalObject =
//                new GraphicalStrokeKruskalMST();
//        graphicalObject.setMst(mst);
//        graphicalObject.setStrokes(strokes);
//        DrawingArea drawingArea = new DrawingArea();
//        drawingArea.setDrawingObject(graphicalObject);
//        JFrame dialog = new JFrame();
//        dialog.setContentPane(drawingArea);
//        dialog.pack();
//        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        dialog.setVisible(true);


//
//
//    SymbolTestData data = new SymbolTestData();
//    data.setClassifiebles(symbols);
//    data.getFrequencies();

//        inkMlInput.readInkMLFile("src/data/65_alfonso.inkml");
        
//        ArrayList<Integer> list1 = new ArrayList<Integer>();
//        list1.add(1);
//        list1.add(2);
//        list1.add(3);

        showEvaluationView();
//        testClassifier();
//        calculateSymbolStatistics();
//        testSegmentationAndClassification();
//        testPartition();
    }

    public static void testPartition(){
        ArrayList<String> inkFiles = new ArrayList<String>();
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
        TrainingAndTestDataPartitioner partirioner = new TrainingAndTestDataPartitioner();
        partirioner.partSamples(inkFiles);
        ArrayList<String> testExpressions = partirioner.getTestExpressions();
        ArrayList<String> trainingExpressions = partirioner.getTrainingExpressions();

        String trainingFileName = "training.txt";
        String trainingFileContent = "";
        String testFileName = "test.txt";
        String testFileContent = "";
        System.out.println("number of train expre: " + trainingExpressions.size());
        System.out.println("number of test expre: " + testExpressions.size());

        for (String expression : trainingExpressions) {
            trainingFileContent += (expression + "\n");
        }
        for (String expression : testExpressions) {
            testFileContent += (expression + "\n");
        }
        RWFiles.write(testFileName, testFileContent);
        RWFiles.write(trainingFileName, trainingFileContent);
    }

    
     private static void calculateSymbolStatistics() {
        ShapeContextStatistics statistics = new ShapeContextStatistics();
        statistics.calculateStatistics();
//        String statisticsAsString = statistics.getCompleteStatisticsAsString();
        String statisticsAsString = statistics.getResumeStatisticsAsString();
        System.out.println(statisticsAsString);        
    }


     public static void testSegmentationAndClassification(){
         SegmentationAndClassificationEvaluator evaluator = new SegmentationAndClassificationEvaluator();
         evaluator.runEvaluation();
     }

    public static void showEvaluationView(){
//                EVALUATION VIEW
        EvaluationView evaluationView = new EvaluationView();
        
        JFrame dialog = new JFrame();
        dialog.setContentPane(evaluationView);
        Dimension defaultDimension = new Dimension(800, 600);
//        dialog.setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
        dialog.setSize(defaultDimension);
//        dialog.pack();
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dialog.setVisible(true);
//        END VIEW EVALUTION

//        TEST SYMBOL CLASSIFICATION
//        ArrayList<Classifible> symbols = SymbolUtil.readSymbolData("model-symbols.txt");
//        for (Classifible classifible : symbols) {
//            System.out.println(classifible);
//        }
    }
    /**
     * Craete a toy graph based on String objects.
     *
     * @return a graph based on String objects.
     */
//    private static SimpleWeightedGraph<String, DefaultEdge> createStringGraph()
//    {
//        SimpleWeightedGraph<String, DefaultEdge> g =
//            new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
//
//        String v1 = "v1";
//        String v2 = "v2";
//        String v3 = "v3";
//        String v4 = "v4";
//
//        // add the vertices
//        g.addVertex(v1);
//        g.addVertex(v2);
//        g.addVertex(v3);
//        g.addVertex(v4);
//
//        // add edges to create a circuit
//        g.addEdge(v1, v2);
//        g.addEdge(v2, v3);
//        g.addEdge(v3, v4);
//        g.addEdge(v4, v1);
//
//        g.setEdgeWeight(g.getEdge(v1, v2), 100);
//
//        return g;
//    }


    public static void testClassifier(){
        //print frecuencies
//        System.out.println(SymbolTestData.getFrequencies());
        double trainningPercentage = 0.3;

//        WITH 15 FIRST CLASSES FOR DATA OF WILLIAN RESULTS ARE, with only trainningPercentage = 0.3;
//number of good results: 413
//number of bad results: 5
        
//        WITH 15 FIRST CLASSES FOR DATA OF WILLIAN RESULTS ARE, with only trainningPercentage = 0.3;
//number of good results: 6551
//number of bad results: 250
//       percentage error =  3.8162113 %
        
        ClassifierTest classifierTest = new ClassifierTest();
        classifierTest.setSelectedClasses(SymbolClasses.getClassesAsArrayList());
        classifierTest.readData();
        classifierTest.setTrainningPercent(trainningPercentage);
        ShapeContextClassifier shapeContextClassifier = new ShapeContextClassifier();
        classifierTest.setClassifier(shapeContextClassifier);
        
        classifierTest.testClassifier();
        
        // TO TEST SHAPE CONTEXT WITH INVERESE PARAMETERS
        
//        Point2D.Double[] templatePoints = new Point2D.Double[]{new Point2D.Double(2.3, 2), 
//            new Point2D.Double(2.3, 2), new Point2D.Double(2.3, 2)};
//          Point2D.Double[]  classifiblePoints = new Point2D.Double[]{new Point2D.Double(21, 21), 
//            new Point2D.Double(2.3, 1211), new Point2D.Double(2, 2)};
//            double[][] shapeContextTemplate = CostShapeContextInside.calculateShapeContextFromPoints2D(templatePoints);
//            double[][] shapeContextClassifible = CostShapeContextInside.calculateShapeContextFromPoints2D(classifiblePoints);
//            double diference = CostShapeContextInside.getCost(shapeContextTemplate, shapeContextClassifible);
//            System.out.println("dif1: " + diference);
//            
//            diference = CostShapeContextInside.getCost(shapeContextClassifible, shapeContextTemplate);
//            System.out.println("dif1: " + diference);
        
        
//        System.out.println(SymbolTestData.getFrequencies());
    }
    
    public static void runSegmentationTest(){
        
    }
    
}
