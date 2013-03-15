/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.conversion;

import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class TraceGroup {
    private int id;
    private Annotation annotation;
    private ArrayList<Integer> traceDataRef;
    private ArrayList<TraceGroup> traceGroups;
    private String href;

    public void addTraceGroup(TraceGroup trace){
        getTraceGroups().add(trace);
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<TraceGroup> getTraceGroups() {
        if(traceGroups == null)
            traceGroups = new ArrayList<TraceGroup>();
        return traceGroups;
    }

    public void setTraceGroups(ArrayList<TraceGroup> traceGroups) {
        this.traceGroups = traceGroups;
    }

    public ArrayList<Integer> getTraceDataRef() {
        if(traceDataRef == null)
            traceDataRef = new ArrayList<Integer>();
        return traceDataRef;
    }

    public void setTraceDataRef(ArrayList<Integer> traceDataRef) {
        this.traceDataRef = traceDataRef;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
