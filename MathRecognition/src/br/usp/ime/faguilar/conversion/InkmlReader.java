/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.conversion;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Attribute;

/**
 *
 * @author frank
 */
public class InkmlReader {  
    private static final String TRACE_FORMAT = "traceFormat";
    private static final String CHANNEL = "channel";
    private static final String ANNOTATION = "annotation";
    private static final String TRACE = "trace";
    private static final String TRACE_GROUP = "traceGroup";
    private static final String TRACE_VIEW = "traceView";
    private static final String ANNOTATION_XML = "annotationXML";

    private InkmlMathExpression mathExpression;

    private XMLEventReader eventReader;

    public void read(String fileName){
        mathExpression = new InkmlMathExpression();
        try {
          // First create a new XMLInputFactory
          XMLInputFactory inputFactory = XMLInputFactory.newInstance();
          // Setup a new eventReader
          InputStream in = new FileInputStream(fileName);
          eventReader = inputFactory.createXMLEventReader(in);

        while(eventReader.hasNext()){
            XMLEvent event = eventReader.nextEvent();
            if(event.isStartElement()){
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                      // If we have a item element we create a new item
//                      System.out.println(startElement.getName().getLocalPart());
                      if(startElement.getName().getLocalPart().equals(TRACE_FORMAT))
                          processTraceFormat(event);
                      if(startElement.getName().getLocalPart().equals(ANNOTATION))
                          processAnnotation(event);
                      if(startElement.getName().getLocalPart().equals(TRACE))
                          processTrace(event);
                      if(startElement.getName().getLocalPart().equals(TRACE_GROUP))
                          processTraceGroup(event);
                }
            }
        }
//        System.out.println(mathExpression);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (XMLStreamException e) {
          e.printStackTrace();
        } catch (Exception e){
//            System.out.println(fileName);
            e.printStackTrace();
        }
    }

    private void processTraceFormat(XMLEvent event) throws XMLStreamException{
        event = eventReader.nextEvent();
        event = eventReader.nextEvent();
        if(event.isStartElement()){
        StartElement startElement;
        startElement = event.asStartElement();
        Iterator<Attribute> attributes;
        ArrayList<Channel> channels = new ArrayList<Channel>();
        TraceFormat traceFormat = new TraceFormat();
        boolean addChannel = true;
        Channel channel;
        while(addChannel){
            attributes = startElement.getAttributes();
            channel = new Channel();
            while (attributes.hasNext()) {
                  Attribute attribute = attributes.next();
                  if (attribute.getName().toString().equals(TraceFormat.NAME))
                    channel.setName(attribute.getValue());
                  else if(attribute.getName().toString().equals(TraceFormat.TYPE))
                      channel.setType(attribute.getValue());
            }
            channels.add(channel);
//            System.out.println(event.asCharacters().getData());
            eventReader.nextEvent();
            eventReader.nextEvent();
            event = eventReader.nextEvent();
//            System.out.println(event.asCharacters().getData());
            if(!event.isStartElement())
                addChannel = false;
            else{
//                event = eventReader.nextEvent();
                startElement= event.asStartElement();
            }
        }
        traceFormat.setChannels(channels);
        mathExpression.setTraceFormat(traceFormat);
        }
    }

    private void processAnnotation(XMLEvent event) throws XMLStreamException {
        StartElement startElement = event.asStartElement();
        Iterator<Attribute> attributes = startElement
                .getAttributes();
        Annotation anAnnotation;
        Attribute attribute = attributes.next();
        anAnnotation = new Annotation();
        anAnnotation.setType(attribute.getValue());
        event = eventReader.nextEvent();
        anAnnotation.setValue(event.asCharacters().getData());
        mathExpression.addAnnotation(anAnnotation);
    }

    private void processTrace(XMLEvent event) throws XMLStreamException {
        StartElement startElement = event.asStartElement();
        Iterator<Attribute> attributes = startElement.getAttributes();
        Attribute attribute = attributes.next();
        event = eventReader.nextEvent();
        Trace trace = getTrace(event.asCharacters().getData());
//        Trace trace = getTrace(event.);
        trace.setId(Integer.valueOf(attribute.getValue()));
//        System.out.println(trace.getId());
        getMathExpression().addTrace(trace);

    }

    public InkmlMathExpression getMathExpression() {
        return mathExpression;
    }

    public void setMathExpression(InkmlMathExpression mathExpression) {
        this.mathExpression = mathExpression;
    }

    private Trace getTrace(String stringPoints) {
        Trace trace = new Trace();
        stringPoints = stringPoints.trim();
        String[] stringArrayOfPoints = stringPoints.split("\\s+|,\\s*|\\n");
//        int numberOfChannels = getMathExpression().getTraceFormat().getChannels().size();
        int numberOfChannels = 2;//getMathExpression().getTraceFormat().getChannels().size();
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        Point2D point;
        for (int j = 0; j < stringArrayOfPoints.length; j = j + numberOfChannels) {
            point = new Point2D.Float(Float.valueOf(
                    stringArrayOfPoints[j]), Float.valueOf(stringArrayOfPoints[j+1]));
            points.add(point);
        }
//        System.out.println("points size: " +points.size());
        trace.setPoints(points);
        return trace;
    }

    private void processTraceGroup(XMLEvent event) throws XMLStreamException {
        StartElement startElement = event.asStartElement();
        Iterator<Attribute> attributes = startElement.getAttributes();
        Attribute attribute = attributes.next();
        TraceGroup traceGroup = new TraceGroup();
        TraceGroup newTraceGroup;
        traceGroup.setId(Integer.valueOf(attribute.getValue()));
        boolean addTRaceGroupt = true;

        eventReader.nextEvent();
        event = eventReader.nextEvent();
        Annotation annotation = new Annotation();
        startElement = event.asStartElement();
        attributes = startElement.getAttributes();
        attribute = attributes.next();
        annotation.setType(attribute.getValue());
        event = eventReader.nextEvent();
        annotation.setValue(event.asCharacters().getData());
        traceGroup.setAnnotation(annotation);
        eventReader.nextEvent();
        eventReader.nextEvent();
        event = eventReader.nextEvent();
        while(addTRaceGroupt){
            newTraceGroup = getSymbolTraceGroup(event);
            traceGroup.addTraceGroup(newTraceGroup);
            event = eventReader.nextEvent();
//            System.out.println(event.asCharacters().getData());
            if(!event.isStartElement() || !(event.asStartElement().getName()
                    .getLocalPart().equals(TRACE_GROUP)))
                addTRaceGroupt = false;
        }
        getMathExpression().setTraceGroup(traceGroup);
    }

    private TraceGroup getSymbolTraceGroup(XMLEvent event) throws XMLStreamException {
        TraceGroup traceGroup = new TraceGroup();
        StartElement startElement = event.asStartElement();
        Iterator<Attribute> attributes = startElement.getAttributes();
        Attribute attribute = attributes.next();
        traceGroup.setId(Integer.valueOf(attribute.getValue()));

        eventReader.nextEvent();
        event = eventReader.nextEvent();
        Annotation annotation = new Annotation();
        startElement = event.asStartElement();
        attributes = startElement.getAttributes();
        attribute = attributes.next();
        annotation.setType(attribute.getValue());
        traceGroup.setAnnotation(annotation);
        event = eventReader.nextEvent();
        annotation.setValue(event.asCharacters().getData());
        eventReader.nextEvent();
        eventReader.nextEvent();
        event = eventReader.nextEvent();
        ArrayList<Integer> traceDataRefs = new ArrayList<Integer>();
        Integer dataRef;
        while(event.isStartElement() && (event.asStartElement().getName().getLocalPart().equals(TRACE_VIEW)
                || event.asStartElement().getName().getLocalPart().equals(ANNOTATION_XML))){
            if(event.asStartElement().getName().getLocalPart().equals(TRACE_VIEW)){
                startElement = event.asStartElement();
                attributes = startElement.getAttributes();
                attribute = attributes.next();
                dataRef = Integer.valueOf(attribute.getValue());
                traceDataRefs.add(dataRef);
            }else if(event.asStartElement().getName().getLocalPart().equals(ANNOTATION_XML)){
                startElement = event.asStartElement();
                attributes = startElement.getAttributes();
                attribute = attributes.next();
                traceGroup.setHref(attribute.getValue());
            }
            eventReader.nextEvent();
            eventReader.nextEvent();
            event = eventReader.nextEvent();
        }
//        if(event.isStartElement()){
//            startElement = event.asStartElement();
//            attributes = startElement.getAttributes();
//            attribute = attributes.next();
//            traceGroup.setHref(attribute.getValue());
//        }
        traceGroup.setTraceDataRef(traceDataRefs);
//        eventReader.nextEvent();
//        eventReader.nextEvent();
//        eventReader.nextEvent();
        eventReader.nextEvent();
        return traceGroup;
    }
}
