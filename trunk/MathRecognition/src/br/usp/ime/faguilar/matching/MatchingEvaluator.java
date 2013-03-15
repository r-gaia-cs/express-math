/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.matching;

import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class MatchingEvaluator {

    public static ArrayList<IncorrectMatching> evaluate(int[][] matchingResult,
            int[][] correctMatching){
        ArrayList<IncorrectMatching> incorrectMatchings=new ArrayList<IncorrectMatching>();
        int modelColumn=0;
        int instanceColumn=1;
        for(int i=0;i<matchingResult.length;i++){
            if(correctMatching[i][instanceColumn]!=matchingResult[i][instanceColumn]){
                IncorrectMatching incorrectMatching=new IncorrectMatching();
                incorrectMatching.setSymbolAtModel(correctMatching[i][modelColumn]);
                incorrectMatching.setSymbolAtInstance(matchingResult[i][instanceColumn]);
                incorrectMatching.setCorrectSymbolAtInstance(correctMatching[i][instanceColumn]);
                incorrectMatchings.add(incorrectMatching);
            }
        }
        return incorrectMatchings;
    }

//    public static IncorrectMatching setUpIncorrectMatching()


}
