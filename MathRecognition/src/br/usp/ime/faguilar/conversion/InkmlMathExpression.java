/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.conversion;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.graphics.GStroke;
import br.usp.ime.faguilar.graphics.GSymbol;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class InkmlMathExpression {
    private TraceFormat traceFormat;
    private ArrayList<Annotation> annotations;
    private ArrayList<Trace> traces;
    private TraceGroup traceGroup;

    public DMathExpression asDMathExpression(){
        DMathExpression mathExpression = new DMathExpression();
        if(traceGroup != null){
            ArrayList<TraceGroup> traceGroups = traceGroup.getTraceGroups();
            ArrayList<Integer> traceDataRef;
            DSymbol symbol;
            OrderedStroke traceAsStroke;
            for (TraceGroup aTraceGroup : traceGroups) {
                symbol = new GSymbol();
                traceDataRef = aTraceGroup.getTraceDataRef();
                for (Integer traceRef : traceDataRef) {
                    traceAsStroke = getTraceAsStroke(traceRef);
                    traceAsStroke.setIndex(traceRef);
                    symbol.addCheckingBoundingBox(traceAsStroke);
                }
                symbol.setLabel(aTraceGroup.getAnnotation().getValue());
                mathExpression.addCheckingBoundingBox(symbol);
            }
        } else if(traces != null && !traces.isEmpty()){
            // all traces are considered as a symbol and as a junk symbol
            DSymbol symbol;
            OrderedStroke traceAsStroke;
            symbol = new GSymbol();
            for (Trace aTrace : getTraces()) {
                traceAsStroke = getTraceAsStroke(aTrace);
                traceAsStroke.setIndex(aTrace.getId());
                symbol.addCheckingBoundingBox(traceAsStroke);
            }
            symbol.setLabel(getExpressionTruth());
            mathExpression.addCheckingBoundingBox(symbol);
        }
        return mathExpression;
    }

    public String getExpressionTruth(){
        String truth = "";
        for (Annotation annotation : annotations) {
            if(annotation.getType().equals("truth")){
                truth = annotation.getValue();
                break;
            }
        }
        return truth;
    }
    
    public String getWriter(){
        String writer = "";
        for (Annotation annotation : annotations) {
            if(annotation.getType().equals("writer")){
                writer = annotation.getValue();
                break;
            }
        }
        return writer;
    }

    private OrderedStroke getTraceAsStroke(int traceIndex){
        OrderedStroke stroke = new OrderedStroke();
        Trace trace = getTraces().get(traceIndex);
        return getTraceAsStroke(trace);
    }
    
    public OrderedStroke getTraceAsStroke(Trace trace){
        OrderedStroke stroke = new OrderedStroke();
        TimePoint timePoint;
        for (Point2D point : trace.getPoints()) {
            timePoint = new TimePoint(point.getX(), point.getY(), 0);
            stroke.addCheckingBoundingBox(timePoint);
        }
        return stroke;
    }

    public void addAnnotation(Annotation annotation){
        getAnnotations().add(annotation);
    }

    public void addTrace(Trace trace){
        getTraces().add(trace);
    }

    public ArrayList<Annotation> getAnnotations() {
        if(annotations == null)
            annotations = new ArrayList<Annotation>();
        return annotations;
    }

    public void setAnnotations(ArrayList<Annotation> annotations) {
        this.annotations = annotations;
    }

    public TraceFormat getTraceFormat() {
        return traceFormat;
    }

    public void setTraceFormat(TraceFormat traceFormat) {
        this.traceFormat = traceFormat;
    }

    public TraceGroup getTraceGroup() {
        return traceGroup;
    }

    public void setTraceGroup(TraceGroup traceGroup) {
        this.traceGroup = traceGroup;
    }

    public ArrayList<Trace> getTraces() {
        if(traces == null)
            traces = new ArrayList<Trace>();
        return traces;
    }

    public void setTraces(ArrayList<Trace> traces) {
        this.traces = traces;
    }

    @Override
    public String toString(){
        String string = getTraceFormat().toString();
        for (Annotation annotation : annotations) {
            string += annotation;
        }
        return string;
    }

}
