/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.export;
import DatabaseMathExpressions.DBFuntions;
import DatabaseMathExpressions.ModelExpression;
import DatabaseMathExpressions.UserExpression;
import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.graphics.GMathExpression;
import br.usp.ime.faguilar.util.FilesUtil;
import java.util.ArrayList;

/**
 *
 * @author frank
 */
public class Exporter {
    private boolean filterByCategory;
    private boolean filterByUserNickName;
    private boolean filterByExpressionSize;//size = number of symbols of expression
    private boolean includeModels;
    private int minSizeOfExpression;
    private int maxSizeOfExpression;
    private ArrayList<String> categories;
    private ArrayList<String> userNickNames;

    private String path; // path where files will be saved

    private DBFuntions database;

    public Exporter(){
        database=new DBFuntions();
        database.openConnection();
        setPath("");
        setFilterByCategory(false);
        setFilterByExpressionSize(false);
        setFilterByUserNickName(false);
        setIncludeModels(false);
    }

    public void exportDatasetToInkML(){
        ArrayList<MathExpressionSample> samples = null;
        if(isFilterByUserNickName()){
            samples = getSamplesByUser();
            if(isFilterByCategory())
                samples = filterSamplesByCategories(samples);
            if(isFilterByExpressionSize())
                samples = filterSamplesBySize(samples);
        }else{
            samples =getModelExpressionSamples();
            if(isFilterByCategory())
                samples = filterSamplesByCategories(samples);
            if(isFilterByExpressionSize())
                samples = filterSamplesBySize(samples);
            ArrayList<MathExpressionSample> userSamples = getUserExpressionsForModelSamples(samples);
            if(isIncludeModels())
                samples.addAll(userSamples);
            else
                samples = userSamples;
        }
        exportSamples(samples);
    }

    public ArrayList<MathExpressionSample> getUserExpressionsForModelSamples(ArrayList<MathExpressionSample> modelSamples){
        ArrayList<MathExpressionSample> userExpressionSamples =
                new ArrayList<MathExpressionSample>();
        for (MathExpressionSample modelDMathExpressionSample : modelSamples) {
            ArrayList<UserExpression> userExpressionsForModel =
                    database.getUserExpressionsForModel(modelDMathExpressionSample.getID());
            for (UserExpression userExpression : userExpressionsForModel) {
                DMathExpression dmathUserExpression=userExpression.getdMExpression();
                DMathExpression dMathmodelExpression = modelDMathExpressionSample.getMathExpression();
                br.usp.ime.faguilar.util.UtilLabeler.labelSymbols((GMathExpression)dmathUserExpression,
                        (GMathExpression) dMathmodelExpression,
                        userExpression.getMatch());
                MathExpressionSample instanceME=new MathExpressionSample(
                    String.valueOf(modelDMathExpressionSample.getID()),
                    userExpression.getIdUser(),dmathUserExpression);
                instanceME.setTextualRepresentation(modelDMathExpressionSample.getTextualRepresentation());
                instanceME.setCategory(modelDMathExpressionSample.getCategory());
                userExpressionSamples.add(instanceME);
            }
        }
        return userExpressionSamples;
    }

    public ArrayList<MathExpressionSample> getSamplesByUser(){
        ArrayList<MathExpressionSample> samples= getMathExpressionSamplesByUsers();
         if(isIncludeModels()){
             ArrayList<MathExpressionSample> modelSamples = getModelExpressionSamples();
             for (MathExpressionSample mathExpressionSample : modelSamples) {
                 samples.add(mathExpressionSample);
             }
         }
        return samples;
    }

    public ArrayList<MathExpressionSample> getMathExpressionSamplesByUsers(){
        ArrayList<MathExpressionSample> samples=new ArrayList<MathExpressionSample>();
        for (String nickName : getUserNickNames()) {
             ArrayList<UserExpression> userExpressions = database.getExpressionsForUser(nickName);
             for (UserExpression userExpression : userExpressions) {
                ModelExpression model = database.getModelByID(userExpression.getIdModelExpression());
                DMathExpression dmathUserExpression=userExpression.getdMExpression();
                DMathExpression dMathmodelExpression = model.getdMathExpression();
                br.usp.ime.faguilar.util.UtilLabeler.labelSymbols((GMathExpression)dmathUserExpression,
                        (GMathExpression)dMathmodelExpression,
                        userExpression.getMatch());
                MathExpressionSample instanceME=new MathExpressionSample(
                    String.valueOf(model.getId()),
                    nickName,dmathUserExpression);
                instanceME.setTextualRepresentation(model.getTextualRepresentation());
                instanceME.setCategory(model.getCategoryName());
                samples.add(instanceME);
            }
        }
        return samples;
    }

     public ArrayList<MathExpressionSample> getModelExpressionSamples(){
        ArrayList<MathExpressionSample> samples=new ArrayList<MathExpressionSample>();
        ArrayList<ModelExpression> models=database.getModelExpressions();
        for (ModelExpression modelExpression : models) {
            DMathExpression dmathModel = modelExpression.getdMathExpression();
            MathExpressionSample me=new MathExpressionSample(
                    String.valueOf(modelExpression.getId()),
                    "user0",dmathModel);
            me.setCategory(modelExpression.getCategoryName());
            me.setTextualRepresentation(modelExpression.getTextualRepresentation());
            me.setID(modelExpression.getId());
            samples.add(me);
        }
        return samples;
    }

    public void exportSamples(ArrayList<MathExpressionSample> samples){
        int count = 0;
        for (MathExpressionSample sample : samples) {
            InkMLExpression inkMlExpression = new InkMLExpression();
            inkMlExpression.setGroundTruthExpression(sample.getTextualRepresentation());
            inkMlExpression.setSampleExpression(sample);
            inkMlExpression.generateInkML();
            String inkmlTex = inkMlExpression.getInkmlText();
//            count++;
            FilesUtil.write((getPath() + sample.getModel() + "_" +
                    sample.getUser()+".inkml"), inkmlTex);
        }
    }

    public ArrayList<MathExpressionSample> filterSamplesByCategories(
        ArrayList<MathExpressionSample> samples){
        ArrayList<MathExpressionSample> filteredSamples=new ArrayList<MathExpressionSample>();
        for (MathExpressionSample sampleExpression : samples) {
            if(categories.contains(sampleExpression.getCategory()))
                filteredSamples.add(sampleExpression);
        }
        return filteredSamples;
    }

    public ArrayList<MathExpressionSample> filterSamplesBySize(
        ArrayList<MathExpressionSample> samples){
        ArrayList<MathExpressionSample> filteredSamples=new ArrayList<MathExpressionSample>();
        for (MathExpressionSample sampleExpression : samples) {
            if(sampleExpression.getMathExpression().size() >= getMinSizeOfExpression() &&
                    sampleExpression.getMathExpression().size() <= getMaxSizeOfExpression())
                filteredSamples.add(sampleExpression);
        }
        return filteredSamples;
    }

    public static void main(String[] args) {
        // Criando um objeto XStream
        Exporter teste= new Exporter();
        Exporter teste2= new SymbolExporter();
        //TEST EXPORT ALL DATABASE
//        teste.exportAllDatasetToInkML();

        //TEST EXPORT BY CATEGORIES
//        ArrayList<String> categories = new ArrayList<String>();
//        categories.add("ctest");
//        teste.exportDatasetByCategoryToInkML(categories);

//        TEST BY USER
//        ArrayList<String> nickNames = new ArrayList<String>();
//        nickNames.add("Willian");
//        teste.exportDatasetByUsersToInkML(nickNames,true);

//        TEST BY NUMBER OF SYMBOLS
        int minNumberOfSymbols = 0;
        int maxNumberOfSymbols = 4;
        teste.setMinSizeOfExpression(minNumberOfSymbols);
        teste.setMaxSizeOfExpression(maxNumberOfSymbols);
//        teste.setFilterByExpressionSize(true);
//        teste.exportDatasetToInkML();

//        TEST SYMBOLS EXPORTATION
        teste2.setMinSizeOfExpression(minNumberOfSymbols);
        teste2.setMaxSizeOfExpression(maxNumberOfSymbols);
        teste2.setFilterByExpressionSize(true);
        teste2.exportDatasetToInkML();
    }

    public boolean isFilterByCategory() {
        return filterByCategory;
    }

    public void setFilterByCategory(boolean filterByCategory) {
        this.filterByCategory = filterByCategory;
    }

    public boolean isFilterByExpressionSize() {
        return filterByExpressionSize;
    }

    public void setFilterByExpressionSize(boolean filterByExpressionSize) {
        this.filterByExpressionSize = filterByExpressionSize;
    }

    public boolean isFilterByUserNickName() {
        return filterByUserNickName;
    }

    public void setFilterByUserNickName(boolean filterByUserNickName) {
        this.filterByUserNickName = filterByUserNickName;
    }

    public boolean isIncludeModels() {
        return includeModels;
    }

    public void setIncludeModels(boolean includeModels) {
        this.includeModels = includeModels;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public int getMaxSizeOfExpression() {
        return maxSizeOfExpression;
    }

    public void setMaxSizeOfExpression(int maxSizeOfExpression) {
        this.maxSizeOfExpression = maxSizeOfExpression;
    }

    public int getMinSizeOfExpression() {
        return minSizeOfExpression;
    }

    public void setMinSizeOfExpression(int minSizeOfExpression) {
        this.minSizeOfExpression = minSizeOfExpression;
    }

    public ArrayList<String> getUserNickNames() {
        return userNickNames;
    }

    public void setUserNickNames(ArrayList<String> userNickNames) {
        this.userNickNames = userNickNames;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
