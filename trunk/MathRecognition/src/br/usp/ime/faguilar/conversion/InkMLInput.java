/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.conversion;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DStroke;
import br.usp.ime.faguilar.data.TimePoint;
import br.usp.ime.faguilar.graphics.GStroke;
import br.usp.ime.faguilar.segmentation.OrderedStroke;
import br.usp.ime.faguilar.util.FilesUtil;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class InkMLInput {
//    private InkMLExpression inkMLExpression;

    public InkMLInput(){
//        inkMLExpression = new InkMLExpression();
    }

//    public void readInkMLFile(String fileName){
//        String content = FilesUtil.getContentAsString(fileName);
//        ArrayList<DStroke> strokes = getStrokes(content);
//    }

    /**
     * gets strokes from an inkml file, without considering
     * timestamp of strokes
     * @param fileName
     * @return
     */
    public ArrayList<DStroke> extractStrokesFromInkMLFile(String fileName){
        String content = FilesUtil.getContentAsString(fileName);
        return getStrokes(content);
    }

    public ArrayList<DStroke> getStrokes(String inkMLString){
        ArrayList<DStroke> strokes = new ArrayList();
        String[] lines = inkMLString.split("\n");
        String[] stringNumbers;
        int numberOfChannels = 2;//getNumberOfChannels(lines);
//        int numJumps = numberOfChannels -1;
        
        for (int i = 0; i < lines.length; i++) {
            while(lines[i].contains("<trace id")){
                if(!lines[i].contains("</trace>")){
                    i++;
                    stringNumbers = lines[i].split("\\s+|,\\s*|\\n");//("\\s|,\\s");
                }
                else{
                    int first = lines[i].indexOf(">") + 1;
                    int last = lines[i].lastIndexOf("<");
                    stringNumbers = lines[i].substring(first, last).split("\\s+|,\\s*|\\n");
                }

                
                OrderedStroke newStroke =new OrderedStroke();
                for (int j = 0; j < stringNumbers.length; j = j + numberOfChannels) {
                    TimePoint timePoint = new TimePoint(Double.valueOf(
                            stringNumbers[j]), Double.valueOf(stringNumbers[j+1]), 0);
                    newStroke.addCheckingBoundingBox(timePoint);
                }
                newStroke.setIndex(strokes.size());
                strokes.add(newStroke);
                if(!lines[i].contains("</trace>"))
                    i = i + 2;
                else
                    i++;

//                if(lines[i].contains("/trace"))
//                    break;
            }
//            break;
        }
        return strokes;
    }


    public int getNumberOfChannels(String[] lines){
        int numberOfChannels = 0;
        String string;
        for (int i = 0; i < lines.length; i++) {
            string = lines[i];
            if(string.contains("channel")){
                numberOfChannels++;
                i++;
                string = lines[i];
                while(string.contains("channel")){
                    numberOfChannels++;
                    i++;
                    string = lines[i];
                }
            }
        }
        return numberOfChannels;
    }
}
