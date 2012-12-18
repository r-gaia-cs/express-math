/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.export;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.data.DSymbol;
import br.usp.ime.faguilar.util.RWFiles;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author frank
 */
public class SymbolExporter extends Exporter{
    private static int count; 
    
    @Override
    public void exportSamples(ArrayList<MathExpressionSample> samples){
//        int count = 0;
        count = 0;
        Collections.sort(samples);
        int i = 0;
        int next = 0;
        while (i < samples.size()) {
            boolean success = (new File(getPath()+"/"+
                    samples.get(i).getUser())).mkdir();
//        if (success) {
            printSample(samples.get(i));
            while(i+1 < samples.size() && 
                    samples.get(i).compareTo(samples.get(i+1)) == 0){
                i++;
                printSample(samples.get(i));
                }                      
//            }
            i++;
        }
    }
    
    private void printSample(MathExpressionSample sample){
        DMathExpression dMathExpression = sample.getMathExpression();
        for (DSymbol dSymbol : dMathExpression) {
            InkMLSymbol inkMlExpression = new InkMLSymbol();
            inkMlExpression.setUserNickName(sample.getUser());
            inkMlExpression.setModel(sample.getModel());
            inkMlExpression.setdSymbol(dSymbol);
            inkMlExpression.generateInkML();
            String inkmlTex = inkMlExpression.getInkmlText();
            count++;
//                RWFiles.write((getPath() + dSymbol.getLabel() + "_" +
//                    sample.getUser()+".inkml"), inkmlTex);
            RWFiles.write((getPath() + "/"+ sample.getUser()+"/"+sample.getUser() + 
                    "_" + count + ".inkml"), inkmlTex);
        }
    }
}
