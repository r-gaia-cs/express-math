/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.feature_extraction;

import br.usp.ime.faguilar.cost.CostShapeContextInside;
import br.usp.ime.faguilar.cost.ShapeContext;
import br.usp.ime.faguilar.cost.ShapeContextVector;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.graphics.GMathExpression;
import br.usp.ime.faguilar.graphics.GStroke;
import br.usp.ime.faguilar.graphics.GSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author frank
 */
public class PreprocessingAlgorithms {
    private static final double percentageOfLength=0.9;
    private static final double turningAngleThreashold=(85.0/180)*Math.PI;
    private static final double alphaForTurningAngles = 0.12;

    public static DMathExpression preprocessDMathExpression(DMathExpression expression){
        DMathExpression newExpression=new GMathExpression();
        for (DSymbol symbol : expression) {
            DSymbol newSymbol=preprocessDSymbol(symbol);
            newExpression.addCheckingBoundingBox(newSymbol);
        }
        return newExpression;
    }

    public static DMathExpression preprocessDMathExpressionWithOrderedStrokes(DMathExpression expression){
        DMathExpression newExpression=new GMathExpression();
        for (DSymbol symbol : expression) {
            DSymbol newSymbol = preprocessDSymbolWithOrderedStrokes(symbol);
            newExpression.addCheckingBoundingBox(newSymbol);
        }
        return newExpression;
    }

    public static DSymbol preprocessDSymbol(DSymbol s){
        DSymbol newSymbol=new GSymbol();
        for (DStroke stroke : s) {
                ArrayList<Point2D> array=strokeToArrayList(stroke);
                array =getNotDuplicatedPoints(array);
                array=equalLengthResampling(array);
                if(array.size()>=3){
                    array =getNotDuplicatedPoints(array);
                    array=dehooking(array);
                    array=smooth(array);
                    array=equalLengthResampling(array);
                }
                array =getNotDuplicatedPoints(array);
                DStroke newStroke= new GStroke();
                for (Point2D point : array) {
                    newStroke.addCheckingBoundingBox(new TimePoint(point.getX(),
                            point.getY(), -1));
                }
                newSymbol.addCheckingBoundingBox(newStroke);
        }
        return newSymbol;
    }

    public static DSymbol preprocessDSymbolWithOrderedStrokes(DSymbol s){
        DSymbol newSymbol=new GSymbol();
        for (DStroke stroke : s) {
                ArrayList<Point2D> array=strokeToArrayList(stroke);
                array = getNotDuplicatedPoints(array);
                array=equalLengthResampling(array);
                
                if(array.size()>=3){
                    array = getNotDuplicatedPoints(array);
                    array=dehooking(array);
                    array=smooth(array);
                    array=equalLengthResampling(array);
                }
                array =getNotDuplicatedPoints(array);
                OrderedStroke newStroke= new OrderedStroke();
                for (Point2D point : array) {
                    newStroke.addCheckingBoundingBox(new TimePoint(point.getX(),
                            point.getY(), -1));
                }
                newStroke.setIndex(((OrderedStroke)stroke).getIndex());
                newSymbol.addCheckingBoundingBox(newStroke);
        }
        newSymbol.setLabel(s.getLabel());
        return newSymbol;
    }
    
    public static ArrayList<DStroke> preprocessStrokes(ArrayList<DStroke> strokes){
//        DSymbol newSymbol=new GSymbol();
        ArrayList<DStroke> newStrokes = new ArrayList<DStroke>();
        for (DStroke stroke : strokes) {
                ArrayList<Point2D> array=strokeToArrayList(stroke);
                array =getNotDuplicatedPoints(array);
                array=equalLengthResampling(array);
                if(array.size()>=3){
                    array=dehooking(array);
                    array=smooth(array);
                    array=equalLengthResampling(array);
                }
                OrderedStroke newStroke= new OrderedStroke();
                for (Point2D point : array) {
                    newStroke.addCheckingBoundingBox(new TimePoint(point.getX(),
                            point.getY(), -1));
                }
                newStroke.setIndex(((OrderedStroke)stroke).getIndex());
                newStrokes.add(newStroke);
        }
        return newStrokes;
    }
     

    public static Point2D[] getNPoints(DSymbol s,int N){
        Point2D[] points=new Point2D[N];
        ArrayList<Point2D> alPoints=new ArrayList<Point2D>();
//        int pointsPerStroke=(int) Math.round((double)N/s.size());
//        int numStrokes=s.size();
//        int total=0;

//        int[]pointsPerStroke = extractNNumberOfPointsPerStroke(s, N);
        int[]pointsPerStroke = extractNNumberOfPointsPerStrokeUsingLenght(s, N);

        for (int i = 0; i < pointsPerStroke.length; i++) {
//            Point2D[] pts=getNPoints(s.get(i), pointsPerStroke);
//            alPoints.addAll(Arrays.asList(pts));
//            total+=pointsPerStroke;
            ArrayList<Point2D> pts=getNPoints(s.get(i), pointsPerStroke[i]);
            alPoints.addAll(pts);
        }

//        int rest=N-total;
//        Point2D[] pts=getNPoints(s.get(numStrokes-1), rest);
//        alPoints.addAll(Arrays.asList(pts));

        for (int i = 0; i < points.length; i++) {
            points[i]=alPoints.get(i);

        }
        return points;
    }
    
    public static Point2D[] getNPointsAtSameDistance(DSymbol s,int N){
        Point2D[] points=new Point2D[N];
        ArrayList<Point2D> alPoints=new ArrayList<Point2D>();
        double distance = getLength(s) / N - 1;
        
        for (DStroke dStroke : s) {
            
        }
        
//        int pointsPerStroke=(int) Math.round((double)N/s.size());
//        int numStrokes=s.size();
//        int total=0;

//        int[]pointsPerStroke = extractNNumberOfPointsPerStroke(s, N);
        int[]pointsPerStroke = extractNNumberOfPointsPerStrokeUsingLenght(s, N);

        for (int i = 0; i < pointsPerStroke.length; i++) {
//            Point2D[] pts=getNPoints(s.get(i), pointsPerStroke);
//            alPoints.addAll(Arrays.asList(pts));
//            total+=pointsPerStroke;
            ArrayList<Point2D> pts=getNPoints(s.get(i), pointsPerStroke[i]);
            alPoints.addAll(pts);
        }

//        int rest=N-total;
//        Point2D[] pts=getNPoints(s.get(numStrokes-1), rest);
//        alPoints.addAll(Arrays.asList(pts));

        for (int i = 0; i < points.length; i++) {
            points[i]=alPoints.get(i);

        }
        return points;
    }

    public static ShapeContextFeature getNShapeContetxFeatures(DSymbol s,int N){
        ShapeContextFeature features = new ShapeContextFeature();
        int[] pointsPerStroke = extractNNumberOfPointsPerStroke(s, N);

        for (int i = 0; i < pointsPerStroke.length; i++) {
            ArrayList<FeatureGroup> newFeatures = getNShapeContetxFeatures(s, s.get(i), pointsPerStroke[i]);
            features.addAll(newFeatures);
        }
        ShapeContext shapeContext = CostShapeContextInside.calculateShapeContextFromPoints2D(features.getCoords());
        features.setShapeContext(shapeContext);
        return features;
    }
    
    public static ShapeContextFeature getNGeneralizedShapeContetxFeatures(DSymbol s,int N){
        ShapeContextFeature features = new ShapeContextFeature();
        int[] pointsPerStroke = extractNNumberOfPointsPerStroke(s, N);
//        int[] pointsPerStroke = extractNNumberOfPointsPerStrokeUsingLenght(s, N);

        for (int i = 0; i < pointsPerStroke.length; i++) {
            ArrayList<FeatureGroup> newFeatures = getNShapeContetxFeatures(s, s.get(i), pointsPerStroke[i]);
            features.addAll(newFeatures);
        }
        ShapeContext shapeContext = CostShapeContextInside.calculateGeneralizedShapeContextFromPoints2DAndVectors(features.getCoords(), 
                features.getVectors());
        
//        Point2D[] nPoints = PreprocessingAlgorithms.getNPoints(s, N);
//        ShapeContext shapeContext = CostShapeContextInside.calculateGeneralizedShapeContextFromPoints2D(nPoints);
        
        features.setShapeContext(shapeContext);
        return features;
    }
    
    public static ShapeContextFeature getFuzzyShapeContetxFeatures(DSymbol s,int N){
        ShapeContextFeature features = new ShapeContextFeature();
//        int[] pointsPerStroke = extractNNumberOfPointsPerStroke(s, N);
//        int[] pointsPerStroke = extractNNumberOfPointsPerStrokeUsingLenght(s, N);
        

//        for (int i = 0; i < pointsPerStroke.length; i++) {
//            ArrayList<FeatureGroup> newFeatures = getNShapeContetxFeatures(s, s.get(i), pointsPerStroke[i]);
//            features.addAll(newFeatures);
//        }
//        ShapeContext shapeContext = CostShapeContextInside.calculateFuzzyShapeContextFromPoints2D(features.getCoords());
        Point2D[] points = PointsExtractor.getNPoints(s, N);
        if (points[0] == null)
            System.out.println(s);
        ShapeContext shapeContext = CostShapeContextInside.calculateFuzzyShapeContextFromPoints2D(
                points);
        features.setShapeContext(shapeContext);
        return features;
    }

    public static int getNumberOfPoints(DSymbol s){
        int numberOfPooints=0;
        for (DStroke stroke : s) {
            numberOfPooints+=stroke.size();
        }
        return numberOfPooints;
    }

    public static int[] extractNNumberOfPointsPerStroke(DSymbol s,int N){
        int totalPoints=getNumberOfPoints(s);
        int[] numberOfPoints=new int[s.size()];
//        double[] exactNumbers=new double[s.size()];
        int index=0;
        double exactNumber=0.;
        for (DStroke stroke : s) {
            exactNumber=stroke.size()*N/(double)totalPoints;
            if(exactNumber<1.||stroke.size()==1)
                numberOfPoints[index]=1;
            else
                numberOfPoints[index]=(int) Math.floor(exactNumber);
            index++;
        }
       int suma = sum(numberOfPoints);
       while(suma!=N){
           int max=numberOfPoints[0];
           int posMax=0;
           for (int i = 1; i < numberOfPoints.length; i++) {
               if(max<numberOfPoints[i]){
                   max=numberOfPoints[i];
                   posMax=i;
               }
           }
           if(suma<N){
               numberOfPoints[posMax]++;
               suma++;
           }else{
               numberOfPoints[posMax]--;
               suma--;
           }
       }
       return numberOfPoints;
    }
    
    public static int[] extractNNumberOfPointsPerStrokeUsingLenght(DSymbol s,int N){
        double totalLenght= getLength(s);
        if(totalLenght <= 0)
            return new int[]{N};
        int[] numberOfPoints = new int[s.size()];
        int totalPoints = 0;
        for (int i = 0; i < s.size(); i++) {
            numberOfPoints[i] = (int) Math.round((getLength(s.get(i)) / totalLenght) * N);
            if(numberOfPoints[i] <= 0)
                numberOfPoints[i] = 1;
            totalPoints += numberOfPoints[i];
            if(i == s.size() -1){
                if(totalPoints < N)
                    numberOfPoints[i] += N - totalPoints;
                else if(totalPoints > N)
                    numberOfPoints[i] += totalPoints - N;
            }
        }
       return numberOfPoints;
    }
    
    public static double getLength(DSymbol symbol){
        double length = 0;
        for (DStroke dStroke : symbol) {
            length += getLength(dStroke);
        }
        return length;
    }
    
    public static double getLength(DStroke stroke){
        double length = 0;
        for (int i = 1; i < stroke.size(); i++)
            length += stroke.get(i).distance(stroke.get(i - 1));
        return length;
    } 

    public static int sum(int[] numbers){
        int sum=0;
        for (int i = 0; i < numbers.length; i++) {
            sum+= numbers[i];
        }
        return sum;
    }

    public static  ArrayList<Point2D> getNPoints(DStroke stroke,int N){
        ArrayList<Point2D> points=new ArrayList<Point2D>();
        if(N>stroke.size()){
            points=strokeToArrayList(stroke);
            completePoints(points, N);
        }
        else{
            double pos=0;
            double distance=(double)stroke.size()/N;
            for (int i = 0; i < N; i++) {
                if(pos>=stroke.size())
                    points.add(stroke.get(stroke.size()-1));
                else{
                    points.add(stroke.get((int)Math.round(pos)));
                    pos+=distance;
                }
            }
        }
        return points;
    }

    private static ArrayList<FeatureGroup> getNShapeContetxFeatures(DSymbol symbol, DStroke stroke, int N) {
        ArrayList<FeatureGroup> features = new ArrayList<FeatureGroup>();
        if(N > stroke.size())
            completePoints(symbol, features, stroke, N);
        else{
            double pos=0;
            double distance = (double) stroke.size()/N;
            for (int i = 0; i < N; i++) {
                if(pos>=stroke.size())
                    features.add(newFeatureFromPoint(symbol, stroke, stroke.size()-1));
                else{
                    features.add(newFeatureFromPoint(symbol, stroke, (int)Math.round(pos)));
                    pos+=distance;
                }
            }
        }
        return features;
    }

    public static void completePoints(ArrayList<Point2D> points,int N){
        int diference=N-points.size();
        for (int i = 0; i <diference; i++) {
            if(points.size() == 1)
                points.add(points.get(0));
            else
                interpolateAPoint(points);
        }
    }


    public static void interpolateAPoint(ArrayList<Point2D> points){
        int pos=getPosMaxDistance(points);
        int startPoint=pos;
        int finalPoint=pos+1;
        double distance=points.get(startPoint).distance(points.get(finalPoint));
        Point2D newPoint=getNewPoint(points.get(startPoint), points.get(finalPoint),
                distance/2.);
        points.add(finalPoint, newPoint);
    }

    public static int getPosMaxDistance(ArrayList<Point2D> points){
        int pos=-1;
        double maxDistance=-1;
        double distance=-1;
        int lastPosition=points.size()-1;
        for (int i = 0; i < lastPosition; i++) {
            distance=points.get(i).distance(points.get(i+1));
            if(maxDistance<distance){
                pos=i;
                maxDistance=distance;
            }
        }
        return pos;
    }

    public static DSymbol resampleSymbol(DSymbol s){
        GSymbol resampledSymbol=new GSymbol();
        for (DStroke stroke : s) {
            ArrayList<Point2D> array=strokeToArrayList(stroke);
//            System.out.println("angles: "+ turningAngles(array));
            array=getNotDuplicatedPoints(array);
            array=equalLengthResampling(array);
            GStroke newStroke=(GStroke) arrayToStroke(array);
            resampledSymbol.addCheckingBoundingBox(newStroke);
        }
        return resampledSymbol;
    }

    public static DSymbol dehookSymbol(DSymbol s){
        GSymbol resampledSymbol=new GSymbol();
        for (DStroke stroke : s) {
            ArrayList<Point2D> array=strokeToArrayList(stroke);
            if(array.size()>=3)
                array=dehooking(array);
            GStroke newStroke=(GStroke) arrayToStroke(array);
            resampledSymbol.addCheckingBoundingBox(newStroke);
        }
        return resampledSymbol;
    }

    public static ArrayList<Point2D> dehooking(ArrayList<Point2D> s){

        ArrayList<Double> tAngles=turningAngles(s);
        ArrayList<Point2D> dehooked=new ArrayList<Point2D>();
        dehooked.add(s.get(0));
        for (int i=0;i<tAngles.size();i++) {
            if(!highAngle(tAngles.get(i))||
                    !isAtExtreme(i+1, s))
                dehooked.add(s.get(i+1));
//            else
//                System.out.println("eliminado: " + tAngles.get(i) + "angle at pos : " + i);
        }
        dehooked.add(s.get(s.size()-1));
        return dehooked;
    }

    public static boolean highAngle(double angle){
        if(angle>turningAngleThreashold)
            return true;
        return false;
    }

    public static boolean isAtExtreme(int pos,ArrayList<Point2D> s){
        double lengthOfStroke=getDistance(s, 0, s.size()-1);
        if(getDistance(s, 0, pos)<lengthOfStroke*alphaForTurningAngles||
                getDistance(s, pos, s.size()-1)<lengthOfStroke*alphaForTurningAngles)
            return true;
        return false;
    }
    
    /**
     * Returns the Smooth version of DSymbol s. s is not modified
     * @param s
     */
    public static DSymbol smooth(DSymbol s){
        DSymbol s2=new GSymbol();
        for (DStroke stro : s) {
            if(stro.size()>=3)
                s2.addCheckingBoundingBox(smooth(stro));
            else
                s2.addCheckingBoundingBox(stro);
        }
        return s2;
    }

    /**
     * Returns the Smooth version of DStroke s. s is not modified
     * @param s
     */
    public static DStroke smooth(DStroke s){
        double [] coefficients={0.25,0.5,0.25};
        int center=1; //central possition of coefficients vector
        DStroke s2=new GStroke();

        double newX=0;
        double newY=0;
        s2.addCheckingBoundingBox( new TimePoint(s.get(0).getX(),
                s.get(0).getY(),-1));
        for(int i=1;i<=(s.size()-2);i++){
            newX=0;
            newY=0;
            for(int j=-1;j<=1;j++){
                newX+=(s.get(i+j).getX()*coefficients[center+j]);
                newY+=(s.get(i+j).getY()*coefficients[center+j]);
            }
            TimePoint timePoint=new TimePoint(newX,newY, -1);
            s2.addCheckingBoundingBox(timePoint);
        }
        s2.addCheckingBoundingBox(new TimePoint(s.get(s.size()-1).getX(),
                s.get(s.size()-1).getY(),-1));
        return s2;
    }

    /**
     * Returns the Smooth version of DStroke s. s is not modified
     * @param s
     */
    public static ArrayList<Point2D> smooth(ArrayList<Point2D> s){
        double [] coefficients={0.25,0.5,0.25};
        int center=1; //central possition of coefficients vector
        ArrayList<Point2D> s2=new ArrayList<Point2D>();

        double newX=0;
        double newY=0;
        s2.add( new Point2D.Double(s.get(0).getX(),
                s.get(0).getY()));
        for(int i=1;i<=(s.size()-2);i++){
            newX=0;
            newY=0;
            for(int j=-1;j<=1;j++){
                newX+=(s.get(i+j).getX()*coefficients[center+j]);
                newY+=(s.get(i+j).getY()*coefficients[center+j]);
            }
            TimePoint timePoint=new TimePoint(newX,newY, -1);
            s2.add(timePoint);
        }
        s2.add(new TimePoint(s.get(s.size()-1).getX(),
                s.get(s.size()-1).getY(),-1));
        return s2;
    }




    /**
     * Calculates the turning angles of s. S must have
     * size greater or equal than 3
     * @param s
     * @return
     */
    public static ArrayList<Double> turningAngles(ArrayList<Point2D> s){
        ArrayList<Double> tAngles=new ArrayList<Double>(s.size()-2);
        for (int i = 1; i < s.size()-1; i++) {
            double dx1 = s.get(i).getX() - s.get(i-1).getX();
            double dy1 = s.get(i).getY() - s.get(i-1).getY();
            double dx2 = s.get(i+1).getX() - s.get(i).getX();
            double dy2 = s.get(i+1).getY() - s.get(i).getY();
//            if((dx1 == 0 && dy1 == 0) || (dx2 == 0 && dy2 == 0))
//                System.out.println("zero");
            double turningAngle = angleBetween2Lines(new Line2D.Double(0,0,dx1,dy1),
                    new Line2D.Double(0,0,dx2,dy2));
//            if(turningAngle == Math.PI){
//                System.out.println("pi");
//                angleBetween2Lines(new Line2D.Double(0,0,dx1,dy1),
//                    new Line2D.Double(0,0,dx2,dy2));
//            }
            tAngles.add(turningAngle);
        }
        return tAngles;
    }

    public static ArrayList<Double> turningAngleDiferences(ArrayList<Point2D> s){
        ArrayList<Double> tAngles = turningAngles(s);
//        ArrayList<Double> tAngleDiferences = new ArrayList<Double>(s.size() -1);
//        for (int i = 1; i < tAngles.size(); i++) {
//            tAngleDiferences.add(Math.abs(tAngles.get(i) - tAngles.get(i - 1)));
//        }
        
        return tAngles;
    }

    public static float shapeComplexity(DSymbol symbol){
        float complexity = 0;
        float mean, sum;
        for (DStroke stroke : symbol) {
            if(stroke.size() > 3){
//                ArrayList<Point2D> points = new ArrayList<Point2D>(stroke.size());
//                for (Point2D point2D : stroke) {
//                    points.add(point2D);
//                }
                //                points = PreprocessingAlgorithms.getNotDuplicatedPoints(points);
                ArrayList<Point2D> points = PreprocessingAlgorithms.getNPoints(stroke, 20);
                ArrayList<Double> turningAngleDiferences = turningAngleDiferences(points);
//                TO USE MEAN AS COMPLEXITY
//                sum = (float) 0.;
//                for (Double angle : turningAngleDiferences)
//                    sum += angle;
//                mean  = (float) (sum / (turningAngleDiferences.size() * Math.PI));
//                complexity += mean;
//              TO USE MEDIAN AS COMPLEXITY
                Collections.sort(turningAngleDiferences);
                double median = turningAngleDiferences.get((int) Math.round(turningAngleDiferences.size() / 2.));
                complexity += median;
            }
//            complexity *= symbol.size();
        }
        complexity *= symbol.size();
        return complexity;
    }

    public static double angleBetween2Lines(Line2D line1, Line2D line2)
    {
        double angle1 = Math.atan2(line1.getY2() - line1.getY1(),
                                   line1.getX2() - line1.getX1());
        double angle2 = Math.atan2(line2.getY2() - line2.getY1(),
                                   line2.getX2() - line2.getX1());
//        double angle1 = angle(line1);
//        double angle2 = angle(line2);
        return Math.abs(angle1 - angle2);
    }

    public static double angle(Line2D l){
        double angle=0;
        double dx = (l.getX2()-l.getX1());
        double dy = (l.getY2()-l.getY1());
        double cos=dx/l.getP1().distance(l.getP2());
        angle=Math.acos(cos);
        if(dy>0)
            angle=2.0*Math.PI-angle;

//                float dy = (float)(vertexList[j].getY() - vertexList[i].getY());
//                float modv = dist[i][j]; //(float)fe.euclideanDistance(x1,y1, x2,y2);
//                float cos_theta = dx / modv; //adjacente/hipotenusa
//                float theta = (float)Math.acos(cos_theta);
//                if (dy > 0){
//                    theta = 2.0f*(float)Math.PI - theta;
//                }
//        if(Double.isNaN(angle)){
//            System.out.println("nan");
//        }
        return angle;
    }

    public static ArrayList<Point2D> strokeToArrayList(DStroke stroke){
        ArrayList<Point2D> arrayList=new ArrayList<Point2D>();
        for (Point2D point2D : stroke) {
            arrayList.add(new Point2D.Double(point2D.getX(), point2D.getY()));
        }
        return arrayList;
    }
    public static DStroke  arrayToStroke(ArrayList<Point2D> array){
        GStroke stroke=new GStroke();
        for (Point2D point2D : array) {
            stroke.addCheckingBoundingBox(new TimePoint(point2D.getX(), point2D.getY(), 0) );
        }
        return stroke;
    }

    public static ArrayList<Point2D> equalLengthResampling(ArrayList<Point2D> points){
        ArrayList<Point2D> resampledPoints=new ArrayList<Point2D>();
        ArrayList<Point2D> copyOfPoints=new ArrayList<Point2D>();

        for (int i = 0; i < points.size(); i++) {
            copyOfPoints.add(new Point2D.Double(points.get(i).getX(),points.get(i).getY()));
        }


        if(copyOfPoints.size()>1){
            resampledPoints.add(new Point2D.Double(copyOfPoints.get(0).getX(),
                    copyOfPoints.get(0).getY()));
            double length=getLength(points);
            double dist=0;
            int i=0;
            int j=i+1;
            while (true) {
//                if(j==(copyOfPoints.size()-1)){
//                    resampledPoints.add(copyOfPoints.get(i));
//                    break;
//                }
                dist=getDistance(copyOfPoints, i, j);
                if(length<dist){
                    Point2D newPoint = getNewPoint(copyOfPoints.get(i),
                            copyOfPoints.get(j),length);
                    resampledPoints.add(newPoint);
                    copyOfPoints.get(i).setLocation(newPoint.getX(),newPoint.getY());
                }
                 else{
                    while(length>=dist&&j<copyOfPoints.size()){
                        dist=getDistance(copyOfPoints, i, j);
                        j++;
                    }
                    j--;
                    if(length>=dist){
                        resampledPoints.add(new Point2D.Double(copyOfPoints.get(j).getX(),copyOfPoints.get(j).getY()));
                        break;
                    }
//                    double Ldif=dist-length;
                    double Ldif=length-getDistance(copyOfPoints, i, j-1);
                    Point2D newPoint=getNewPoint(copyOfPoints.get(j-1),
                            copyOfPoints.get(j),Ldif);
                    resampledPoints.add(newPoint);
                    i=j-1;
                    copyOfPoints.get(i).setLocation(newPoint.getX(),newPoint.getY());
                 }
            }
        }
     else{
            for (Point2D point2D : copyOfPoints) {
                resampledPoints.add(point2D);
            }
     }
        return resampledPoints;
    }

    public static double getLength(ArrayList<Point2D> points){
        double totalLength=getDistance(points, 0, points.size()-1);
        return percentageOfLength*totalLength/points.size();
    }

    public static Point2D getNewPoint(Point2D p1,Point2D p2, double d){
        Point2D newPoint=new Point2D.Double();
        double newX=getNewX(p1, p2, d);
        double newY=getNewY(newX, p1, p2, d);
        newPoint.setLocation(newX, newY);
        return newPoint;
    }

    public static ArrayList<Point2D> getNotDuplicatedPoints(ArrayList<Point2D> points){
        ArrayList<Point2D> notDuplicatedPoints=new ArrayList<Point2D>(points.size());
        for (int i = 0; i < points.size(); i++)
            if(!notDuplicatedPoints.contains(points.get(i)))
                notDuplicatedPoints.add(new Point2D.Double(points.get(i).getX(),
                        points.get(i).getY()));
        return notDuplicatedPoints;
    }

    public static double getNewX(Point2D p1,Point2D p2, double d){
        if(p1.getX()==p2.getX())
            return p1.getX();
        double k=(p2.getY()-p1.getY())/(p2.getX()-p1.getX());
        if(p1.getX()<p2.getX())
            return p1.getX()+Math.sqrt(d*d/(k*k+1));
        return p1.getX()-Math.sqrt(d*d/(k*k+1));
    }

    public static double getNewY(double X,Point2D p1,Point2D p2, double d){
        if(p1.getX()!=p2.getX()){
            double k=(p2.getY()-p1.getY())/(p2.getX()-p1.getX());
            return X*k+p1.getY()-k*p1.getX();
        }
        if(p1.getY()<p2.getY())
            return p1.getY()+d;
        return p1.getY()-d;
    }

    public static double getDistance(ArrayList<Point2D> points,int first,int last){
        double distance=0;
        for (int i = first; i < last; i++) {
            distance+=points.get(i).distance(points.get(i+1));
        }
        return distance;
    }

    private static void completePoints(DSymbol symbol, ArrayList<FeatureGroup> features, DStroke stroke, int N) {
        for (int i = 0; i < stroke.size(); i++)
            features.add(newFeatureFromPoint(symbol, stroke, i));
        int diference = N - stroke.size();
        for (int i = 0; i < diference; i++) {
            if(stroke.size() == 1){
                FeatureGroup featureGroup = new FeatureGroup();
                featureGroup.setCoord(stroke.get(0));
                featureGroup.setAngle(0);
                featureGroup.setVector(new ShapeContextVector((float) 0.5, 0));
                features.add(featureGroup);
            }
            else
                interpolateAPoint(symbol, features, stroke);
        }
    }

    private static FeatureGroup newFeatureFromPoint(DSymbol symbol, DStroke stroke, int i) {
        FeatureGroup feature = new FeatureGroup();
        feature.setCoord(stroke.get(i));
        feature.setAngle(calculateAngle(symbol, stroke, i));
        feature.setVector(vectorAtPoint( stroke, i));
        return feature;
    }

    private static float calculateAngle(DSymbol symbol, DStroke stroke, int i) {
        float angle = -1;
//        to use turning angles
//        if(i == 0 || i == (stroke.size() -1))
//            if(stroke.size() == 1)
//                angle = 0;
//            else
//                angle = terminalAngle(stroke, i);
//        else
//            angle = centralAngle(stroke, i);

//        TO USE ANGLE FROM CENTER OF BOUNDING BOX TO THE POINT i
        angle  = angleFromCentroidToPoint(symbol, stroke, i);
        
        return angle;
    }
    
    
    
    public static ShapeContextVector vectorAtPoint(DStroke stroke, int position){
        if(stroke.size() >= 3){
            if(position <= 0)
                return vectorAtPoint(stroke, position + 1);
            if(position >= stroke.size() - 1)
                return vectorAtPoint(stroke, position - 1);
            float dx = (float) (stroke.get(position + 1).getX() - stroke.get(position - 1).getX());
            float dy = (float) (stroke.get(position + 1).getY() - stroke.get(position - 1).getY());
            if(dx == 0 && dy == 0)
                return new ShapeContextVector(0, 0);
            float dist = (float) stroke.get(position + 1).distance(stroke.get(position - 1));
            return new ShapeContextVector(dx / dist, dy / dist);
        }
        return new ShapeContextVector((float) 0.5, 0);
    }

    private static float terminalAngle(DStroke stroke, int i) {
        Line2D axis = new Line2D.Float(0, 0, 1, 0);
        Point2D referencePoint = null;
        if(i == 0)
            referencePoint = stroke.get(1);
        else if(i == stroke.size() -1)
            referencePoint = stroke.get(i - 1);
        Line2D lineToReferencePoint = new Line2D.Float(stroke.get(i), referencePoint);
        float angle = (float) angleBetween2Lines(axis, lineToReferencePoint);
        return angle;
    }

    private static float centralAngle(DStroke stroke, int i) {
        float angle = -1;
//        double dx1 = stroke.get(i).getX() - stroke.get(i-1).getX();
//        double dy1 = stroke.get(i).getY() - stroke.get(i-1).getY();
//        double dx2 = stroke.get(i+1).getX() - stroke.get(i).getX();
//        double dy2 = stroke.get(i+1).getY() - stroke.get(i).getY();
//            if((dx1 == 0 && dy1 == 0) || (dx2 == 0 && dy2 == 0))
//                System.out.println("zero");
//        angle = (float) angleBetween2Lines(new Line2D.Double(0,0,dx1,dy1),
//                new Line2D.Double(0,0,dx2,dy2));
        angle = (float) angleBetween2Lines(new Line2D.Double(stroke.get(i - 1),
                stroke.get(i)),
                new Line2D.Double(stroke.get(i), stroke.get(i + 1)));
        return angle;
    }

    private static float angleFromCentroidToPoint(DSymbol symbol,DStroke stroke, int i) {
        float angle = -1;
        Point2D center = symbol.getBoundingBoxCenter();
        Line2D horizontal = new Line2D.Double(center, new Point2D.Double(center.getX() - 1, center.getY()));
        Line2D centerToPoint = new Line2D.Double(center, stroke.get(i));
        angle = (float) angleBetween2Lines(horizontal, centerToPoint);
        return angle;
    }

    //CHECKAR CLONE
    private static void interpolateAPoint(DSymbol symbol, ArrayList<FeatureGroup> features, DStroke stroke) {
        int pos = getPosMaxDistance(strokeToArrayList(stroke));
        int startPoint = pos;
        int finalPoint = pos + 1;
        double distance = stroke.get(startPoint).distance(stroke.get(finalPoint));
        Point2D newPoint = getNewPoint(stroke.get(startPoint), stroke.get(finalPoint),
                distance / 2.);
        FeatureGroup newFeature = new FeatureGroup();
        newFeature.setCoord(newPoint);
        DStroke strokeCopy = (DStroke) stroke.clone();
        strokeCopy.add(pos + 1, new TimePoint(newPoint.getX(), newPoint.getY(), -1));
        float angle;//calculateAngle(strokeCopy, pos + 1);
//        TO USE TURNING ANGLES
//        angle = 0;
//        newFeature.setAngle(angle);
//        TO USE ANGLE FROM CENTER TO THE NEW POINT
        angle = calculateAngle(symbol, strokeCopy, pos + 1);
        newFeature.setAngle(angle);
        newFeature.setVector(vectorAtPoint(strokeCopy, pos +1));
        features.add(pos + 1, newFeature);
    }
}
