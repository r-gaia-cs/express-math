/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DatabaseMathExpressions;

import br.usp.ime.faguilar.data.DMathExpression;
import br.usp.ime.faguilar.users.User;
import br.usp.ime.faguilar.util.OutputObjects;
import br.usp.ime.faguilar.util.Util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author frank
 */
public class DBFuntions {
    public static final String DEFAULT_NAME_DB_CONTENT="dbContent.data";

    public static final String DEFAULT_NAME_DB_USER="users.data";

    private PreparedStatement psAddModel;

    private PreparedStatement psAddUser;

    private PreparedStatement psAddDBExpression;

    private PreparedStatement psAddUserExpression;

    private PreparedStatement psGetAllModels;

    private PreparedStatement psGetUserExpressionsForModel;

    private PreparedStatement psGetUserExpressionsByIDUser;

    private PreparedStatement psGetUserExpressionByIDUserAndIndex;

    private PreparedStatement psGetUserExpressionByID;

//    private PreparedStatement psGetUnevaluatedUserExpressionsForModel;

    private PreparedStatement getModelExpressionsByEvaluated;

    //calculates the models for wich one connectionUser must input expressions
    private PreparedStatement psGetModelsForUser;

    private PreparedStatement psSetMatchingUserExpression;

    private PreparedStatement psSetEvaluatedUserExpression;

    private PreparedStatement psGetNumExpressionsWrittenByUser;

    private PreparedStatement psGetMaxIDUserExpression;

    private PreparedStatement getModelbyID;

    private PreparedStatement deleteModelByID;

    private PreparedStatement psUpdateMathExpressionOfModel;

    private PreparedStatement psUpdateModelExpression;

//    private final static String url="jdbc:derby://localhost:1527/MathExpressions";
    private final static String url="jdbc:derby:MathExpressions";
//    private final static String url="jdbc:derby://localhost:1527/MathExpressions_14_10_2011";

//    private final static String driver="org.apache.derby.jdbc.ClientDriver";

    private final static String driver="org.apache.derby.jdbc.EmbeddedDriver";
    private final static String connectionUser="frank";

    private final static String password="expressmatch";

    private Connection connection;

    private static final int CONTINUE_OPERATION = 0;
    private static final int CANCEL_OPERATION = 1;

    public DBFuntions(){
        connection=null;
    }

    public void openConnection(){
        try {
          Class.forName(driver).newInstance();
          connection = DriverManager.getConnection(url, connectionUser,password);
          psGetAllModels=connection.prepareStatement("SELECT * FROM FRANK.MODEL_MATH_EXPRESSION" );
          psAddModel=connection.prepareStatement("INSERT INTO FRANK.MODEL_MATH_EXPRESSION"
                  + "(mathExpression,category,textualRepresentations) "
                  + "VALUES (?, ?,?)", Statement.RETURN_GENERATED_KEYS);

          psAddUserExpression=connection.prepareStatement("INSERT INTO FRANK.USER_MATH_EXPRESSION"
                  + "(idUser,idModel,timeOfInput,evaluated,mathExpression,matching,index) "
                  + "VALUES (?, ?, ?, ?, ?,?, ?)");

          psGetUserExpressionsForModel=connection.prepareStatement("SELECT * FROM FRANK.USER_MATH_EXPRESSION"
                  + " WHERE idModel = ?" );

          psSetMatchingUserExpression=connection.prepareStatement("UPDATE FRANK.USER_MATH_EXPRESSION"
                  + " SET matching = ? WHERE id= ? " );

          psSetEvaluatedUserExpression=connection.prepareStatement("UPDATE FRANK.USER_MATH_EXPRESSION"
                  + " SET evaluated = ? WHERE id= ? ");

//          getModelExpressionsByEvaluated=connection.prepareStatement("select MODEL_MATH_EXPRESSION.id, "
//                  + "MODEL_MATH_EXPRESSION.TEXTUALREPRESENTATIONS, MODEL_MATH_EXPRESSION.MATHEXPRESSION, "+
//                    "MODEL_MATH_EXPRESSION.CATEGORY from FRANK.MODEL_MATH_EXPRESSION, FRANK.USER_MATH_EXPRESSION where "+
//                    "FRANK.MODEL_MATH_EXPRESSION.ID = FRANK.USER_MATH_EXPRESSION.IDMODEL and frank.USER_MATH_EXPRESSION.EVALUATED= ? ORDER BY MODEL_MATH_EXPRESSION.id" );

          getModelExpressionsByEvaluated=connection.prepareStatement("select "
                  + "MODEL_MATH_EXPRESSION.id, "
                  + "MODEL_MATH_EXPRESSION.TEXTUALREPRESENTATIONS, MODEL_MATH_EXPRESSION.MATHEXPRESSION, "+
                    "MODEL_MATH_EXPRESSION.CATEGORY from frank.MODEL_MATH_EXPRESSION, "
                    +" (select idmodel from frank.USER_MATH_EXPRESSION  where evaluated=?  group by idmodel ) "
                    + "as selectedmodels where selectedmodels.idmodel=frank.MODEL_MATH_EXPRESSION.id" );

          psGetModelsForUser=connection.prepareStatement("select * from FRANK.MODEL_MATH_EXPRESSION where"+
            " (select count(FRANK.USER_MATH_EXPRESSION.id)"+
            "from FRANK.USER_MATH_EXPRESSION where FRANK.USER_MATH_EXPRESSION.idUser=? and"+
            " FRANK.MODEL_MATH_EXPRESSION.ID=FRANK.USER_MATH_EXPRESSION.IDMODEL) < ?" );
          psGetNumExpressionsWrittenByUser=connection.prepareStatement("select count(*) from "
                  + "FRANK.USER_MATH_EXPRESSION where idUser = ?" );
          psGetUserExpressionsByIDUser=connection.prepareStatement("select * from "
                  + "FRANK.USER_MATH_EXPRESSION where idUser = ?" );
          psGetUserExpressionByIDUserAndIndex=connection.prepareStatement("select * from "
                  + "FRANK.USER_MATH_EXPRESSION where idUser = ? and index = ?" );

          psGetUserExpressionByID=connection.prepareStatement("select * from "
                  + "FRANK.USER_MATH_EXPRESSION where id = ?" );

          psGetMaxIDUserExpression=connection.prepareStatement("select max(index) from FRANK.USER_MATH_EXPRESSION where idUser= ?");

          getModelbyID=connection.prepareStatement("SELECT * FROM FRANK.MODEL_MATH_EXPRESSION WHERE id=?");

          deleteModelByID=connection.prepareStatement("delete FROM FRANK.MODEL_MATH_EXPRESSION WHERE id=?");

          psUpdateMathExpressionOfModel=connection.prepareStatement("UPDATE FRANK.MODEL_MATH_EXPRESSION "
                  + "SET mathExpression = ? WHERE id = ?");

          psUpdateModelExpression=connection.prepareStatement("UPDATE FRANK.MODEL_MATH_EXPRESSION "
                  + "SET mathExpression = ? , category = ? , textualrepresentations = ? "
                  + "WHERE id = ?");
        }
        catch( Exception e ) {
          e.printStackTrace( );
        }
    }

    public void updateMathExpressionOfModel(int id, DMathExpression mathE){

        byte[] mathExpressionBytes=Util.getBytes(mathE);
        try {
             psUpdateMathExpressionOfModel.setBytes(1,mathExpressionBytes);
             psUpdateMathExpressionOfModel.setInt(2, id);
             psUpdateMathExpressionOfModel.executeUpdate();
             psUpdateMathExpressionOfModel.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateModelByID(int id, ModelExpression me){
        DMathExpression mathE=me.getdMathExpression();
        byte[] bytesMathE;
        byte[] bytesTextualRepresentation;
        bytesTextualRepresentation=Util.getBytes(me.getTextualRepresentation());
        bytesMathE = Util.getBytes(mathE);
        String category=me.getCategoryName();
//        ResultSet resultSet = null;
//        int generatedKey =-1;
        try {
            psUpdateModelExpression.setBytes(1, bytesMathE);
            psUpdateModelExpression.setString(2,category);
            psUpdateModelExpression.setBytes(3,bytesTextualRepresentation);
            psUpdateModelExpression.setInt(4, id);
            psUpdateModelExpression.executeUpdate();
            psUpdateModelExpression.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addCategory(String name,String description){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO FRANK.category" +
                    " (name,description) " + "VALUES ('" + name + "','" + description + "')");
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean existCategoryName(String categoryName){
        boolean existCategoryName = false;
        ArrayList<Category> categories = getCategories();
        for (Category category : categories) {
            if(category.getName().equals(categoryName)){
                existCategoryName = true;
                break;
            }
        }
        return existCategoryName;
    }

    public ArrayList<Category> getCategories(){
        ArrayList<Category> categories=new ArrayList<Category>();
        try {
            Statement statement = connection.createStatement();
            ResultSet executeQuery = statement.executeQuery("select * from frank.category");
            while(executeQuery.next()){
                String name=executeQuery.getString("name");
                String description=executeQuery.getString("description");
                categories.add(new Category(name, description));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return categories;
    }

    public User getUserBy(String nickname) throws SQLException{
        User user=null;
        Statement statement=connection.createStatement();
        ResultSet result=statement.executeQuery("select * from FRANK.EMUSER where nickname='"+nickname+"' ");
        if(result.next()){
            user=new User();
            user.setNickName(result.getString("nickname"));
            user.setAdmin(result.getBoolean("admin"));
            user.setFirstName(result.getString("firstName"));
            user.setLastName(result.getString("lastName"));
            user.setAllowedToInsertUserExpressions(result.getBoolean("inputDBMathExpressions"));
            user.setPassword(result.getString("password"));
        }
        return user;
    }

    public ArrayList<User> getAllUsers() throws SQLException{
        User user=null;
        Statement statement=connection.createStatement();
        ArrayList<User> users = new ArrayList<User>();
        ResultSet result=statement.executeQuery("select * from FRANK.EMUSER");
        while(result.next()){
            user=new User();
            user.setNickName(result.getString("nickname"));
            user.setAdmin(result.getBoolean("admin"));
            user.setFirstName(result.getString("firstName"));
            user.setLastName(result.getString("lastName"));
            user.setAllowedToInsertUserExpressions(result.getBoolean("inputDBMathExpressions"));
            user.setPassword(result.getString("password"));
            users.add(user);
        }
        return users;
    }

    public User getUserBy(String nickname,String password) throws SQLException{
        User user=getUserBy(nickname);
        if(user!=null){
            if(user.getPassword().equals(password))
                return user;
        }
        return null;
    }

    public void deleteModelByID(int IDModel){
        try {
            deleteModelByID.setInt(1, IDModel);
            deleteModelByID.executeUpdate();
            deleteModelByID.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ModelExpression getModelByID(int idModel){
        ModelExpression modelExpression=null;
        String category = null;
        ModelExpressionGroundTruth textRep;
        try {
            getModelbyID.setInt(1, idModel);
            ResultSet resultSet=getModelbyID.executeQuery();
            if(resultSet.next()){
                byte[] bytes=resultSet.getBytes("textualRepresentations");
                    if(bytes!=null){
                        textRep=(ModelExpressionGroundTruth) Util.getObject(bytes);
                    }else{
                        textRep=new ModelExpressionGroundTruth();
                    }
                DMathExpression mathE=(DMathExpression)Util.getObject(resultSet.getBytes("mathExpression"));
                modelExpression=new ModelExpression(idModel, textRep,mathE);
                category = resultSet.getString("category");
                modelExpression.setCategoryName(category);
            }
            getModelbyID.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modelExpression;
    }

    public int getMaxIdUserExpression(String idUser){
        int max=-1;
        try {
            psGetMaxIDUserExpression.setString(1, idUser);
            ResultSet rs=psGetMaxIDUserExpression.executeQuery();

            if(rs.next()){
                max=rs.getInt(1);
            }else{
                max=0;
            }
            psGetMaxIDUserExpression.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }

        return max;
    }

    public static String codeIdUserExpression(String idUser, int index){
        return idUser+"_"+index;
    }

    public static String[] decodeIDUserExpression(String idUE){
        String[] decoded=idUE.split("_");
        return decoded;
    }

    public void deleteUserExpressionByIDUserAndIndex(String idUser,int index){
        try {

            PreparedStatement stm = connection.prepareStatement("delete from FRANK.USER_MATH_EXPRESSION "
                    + "where FRANK.USER_MATH_EXPRESSION.idUser = ? and FRANK.USER_MATH_EXPRESSION.index = ?");
            stm.setString(1,idUser);
            stm.setInt(2, index);
            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UserExpression getUserExpression(String idUser,int index){
        UserExpression ue=null;
        try {
            psGetUserExpressionByIDUserAndIndex.setString(1,idUser);
            psGetUserExpressionByIDUserAndIndex.setInt(2, index);
            ResultSet rs = psGetUserExpressionByIDUserAndIndex.executeQuery();
            int idModel;
            Timestamp tm;
            boolean evaluated;
            DMathExpression dme;
            int[][] matching;
            rs.next();
            idModel=rs.getInt("idModel");
            tm=rs.getTimestamp("timeOfInput");
            evaluated=rs.getBoolean("evaluated");
            dme=(DMathExpression) Util.getObject(rs.getBytes("mathExpression"));
            matching=(int[][])Util.getObject(rs.getBytes("matching"));
             ue=new UserExpression(index,idModel, matching, dme,
                    evaluated, idUser, tm);

            psGetUserExpressionByIDUserAndIndex.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
         return ue;
    }


    public UserExpression getUserExpressionByID(int id){
        UserExpression ue=null;
        String idUser=null;
        try {
            psGetUserExpressionByID.setInt(1,id);
            ResultSet rs = psGetUserExpressionByID.executeQuery();
            int idModel;
            Timestamp tm;
            boolean evaluated;
            DMathExpression dme;
            int[][] matching;
            rs.next();
            idModel=rs.getInt("idModel");
            idUser=rs.getString("idUser");
            tm=rs.getTimestamp("timeOfInput");
            evaluated=rs.getBoolean("evaluated");
            dme=(DMathExpression) Util.getObject(rs.getBytes("mathExpression"));
            matching=(int[][])Util.getObject(rs.getBytes("matching"));
             ue=new UserExpression(id,idModel, matching, dme,
                    evaluated, idUser, tm);

            psGetUserExpressionByID.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
         return ue;
    }

    /**
     * Returns an array with the number of expressions written by a connectionUser
     * and the number of total expressions that he have to write
     * @param idUser id of connectionUser
     * @return int[] an array which at position 0 has the number of expressions
     * written by the connectionUser and in position 1 has the total number of expressions
     * that have to be written by the connectionUser
     */
    public int[] getAdvanceUfUser(String idUser){
        int[] advance=new int[2];
        Integer numExpressionsPerModel=(Integer) getValueDBConfiguration("numExprPerModel",
            "intValueOfProperty");
        int numExpressionsWrittenByUser=-1;
        int numModels=-1;
        try {
            psGetNumExpressionsWrittenByUser.setString(1, idUser);
            ResultSet rs=psGetNumExpressionsWrittenByUser.executeQuery();
            rs.next();
            numExpressionsWrittenByUser=rs.getInt(1);
            Statement stm = connection.createStatement();
            rs =stm.executeQuery("select count(*) from FRANK.model_Math_Expression"); //where nameOfProperty = "
                   // + nameOfProperty);
            rs.next();
            numModels=rs.getInt(1);
            advance[0]=numExpressionsWrittenByUser;
            advance[1]=numModels*numExpressionsPerModel;
            psGetNumExpressionsWrittenByUser.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }

        return advance;
    }

    public ModelExpression getModelForUserRandomly(String idUser){
        ModelExpression model=null;
        ArrayList<ModelExpression> models=getModelsForUser(idUser);
        if(!models.isEmpty()){
            Random random=new Random();
            int randomPosition=random.nextInt(models.size());
            model=models.get(randomPosition);
//            for (int i = 0; i < models.size(); i++) {
//                if(models.get(i).getId()==70){
//                    model=models.get(i);
//                    break;
//                }
//            }
        }
        return model;
    }
    public ArrayList<ModelExpression> getModelsForUser(String idUser){
        Integer numExpressionsPerModel=(Integer) getValueDBConfiguration("numExprPerModel",
                "intValueOfProperty");
        ArrayList<ModelExpression> almE=new  ArrayList<ModelExpression>();
         ModelExpressionGroundTruth textRep=null;
         int id=-1;
        try {
            psGetModelsForUser.setString(1,idUser);
            psGetModelsForUser.setInt(2,numExpressionsPerModel);
            ResultSet rs = psGetModelsForUser.executeQuery();
            ResultSetMetaData meta = rs.getMetaData( );
            while(rs.next()){
                try {
                    id=rs.getInt("id");
                    byte[] bytes=rs.getBytes("textualRepresentations");
                    if(bytes!=null){
                        textRep=(ModelExpressionGroundTruth) Util.getObject(bytes);
                    }else{
                        textRep=new ModelExpressionGroundTruth();
                    }
                    DMathExpression mathE=(DMathExpression)Util.getObject(rs.getBytes("mathExpression"));

                    ModelExpression modE=new ModelExpression(id, textRep,mathE);
                    almE.add(modE);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            psGetModelsForUser.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
         return almE;
    }

    public ArrayList<UserExpression> getExpressionsForUser(String idUser){
        ArrayList<UserExpression> alUE=new  ArrayList<UserExpression>();
        try {
            psGetUserExpressionsByIDUser.setString(1,idUser);
            ResultSet rs = psGetUserExpressionsByIDUser.executeQuery();
            int index;
            int idModel;
            Timestamp tm;
            boolean evaluated;
            DMathExpression dme;
            int[][] matching;
            while(rs.next()){
                index=rs.getInt("index");
                idModel=rs.getInt("idModel");
                tm=rs.getTimestamp("timeOfInput");
                evaluated=rs.getBoolean("evaluated");
                dme=(DMathExpression) Util.getObject(rs.getBytes("mathExpression"));
                matching=(int[][])Util.getObject(rs.getBytes("matching"));
                UserExpression ue=new UserExpression(index,idModel, matching, dme,
                        evaluated, idUser, tm);
                alUE.add(ue);
            }
            psGetUserExpressionsByIDUser.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
         return alUE;
    }
    public void setMatchingUserExpression(int idUE,int[][] matching){
        byte[] bytes=Util.getBytes(matching);
        try {
            psSetMatchingUserExpression.setBytes(1, bytes);
            psSetMatchingUserExpression.setInt(2, idUE);
            psSetMatchingUserExpression.executeUpdate();
            psSetMatchingUserExpression.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the models expressions that have evaluated or unevaluated connectionUser expressions
     * @param evaluated
     * @return
     */    public ArrayList<ModelExpression> getModelsByEvaluated(boolean evaluated){
        ArrayList<ModelExpression> almE=new  ArrayList<ModelExpression>();
         ModelExpressionGroundTruth textRep=null;
         int id=-1,lastID=-1;

//        Integer numExpressionsPerModel=(Integer) getValueDBConfiguration("numExprPerModel",
//                "intValueOfProperty");
        int cont=0;
         try {
            this.getModelExpressionsByEvaluated.setBoolean(1, evaluated);

            ResultSet rs = getModelExpressionsByEvaluated.executeQuery();
            while(rs.next()){
                try {
                cont++;
                id=rs.getInt("id");
//                    System.out.println(id);
                //if(id!=lastID){//to select just one model (the first. Java db didnt allowed to use distinct keyword in getModelExpressionsByEvaluated prepared statement )
                    byte[] bytes=rs.getBytes("textualRepresentations");
                    if(bytes!=null){
                        textRep=(ModelExpressionGroundTruth) Util.getObject(bytes);
                    }else{
                        textRep=new ModelExpressionGroundTruth();
                    }
                    DMathExpression mathE=(DMathExpression)Util.getObject(rs.getBytes("mathExpression"));
                    ModelExpression modE=new ModelExpression(id, textRep,mathE);
                    almE.add(modE);
                //}
                //lastID=id;

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
//            System.out.println(cont);
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return almE;
    }

    public void saveDatabaseToFile(String fileName){
        ExportableAndImportableDatabase database = getExportableAndImportableDatabase();
        OutputObjects connectionToFile=new OutputObjects();
        connectionToFile.setFileName(fileName);
        connectionToFile.openFile();
        connectionToFile.saveObject(database);
        connectionToFile.closeFile();
    }

    public ArrayList<ExpressionsPerUser> getExpressionsPerUser(){
        ArrayList<ExpressionsPerUser> alExportImport=new ArrayList<ExpressionsPerUser>();
        try {
            Statement stmUsers = connection.createStatement();
            ResultSet rsUsers = stmUsers.executeQuery("SELECT * FROM FRANK.EMUSER");
            String idUser="";
            User newUser=null;
            ArrayList<UserExpression> alUE = null;
            int cont=0;
            while(rsUsers.next()){
                idUser = rsUsers.getString("nickname");
                alUE = this.getExpressionsForUser(idUser);
                newUser = new User();
                newUser.setAdmin(rsUsers.getBoolean("admin"));
                newUser.setFirstName(rsUsers.getString("firstName"));
                newUser.setLastName(rsUsers.getString("lastName"));
                newUser.setNickName(rsUsers.getString("nickName"));
                newUser.setPassword(rsUsers.getString("password"));
                newUser.setAllowedToInsertUserExpressions(rsUsers.getBoolean("inputDBMathExpressions"));
                ExpressionsPerUser ei=new ExpressionsPerUser(alUE, newUser);
                alExportImport.add(ei);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
         return alExportImport;
    }

    public ExportableAndImportableDatabase getExportableAndImportableDatabase(){
        ExportableAndImportableDatabase exportableAndImportableDatabase = new ExportableAndImportableDatabase();
        ArrayList<Category> categories = getCategories();
        ArrayList<ModelExpression> modelExpressions = getModelExpressions();
        ArrayList<ExpressionsPerUser> expressionsPerUser = getExpressionsPerUser();

        exportableAndImportableDatabase.setCategories(categories);
        exportableAndImportableDatabase.setModelExpressions(modelExpressions);
        exportableAndImportableDatabase.setExpressionsPerUsers(expressionsPerUser);
        return exportableAndImportableDatabase;
    }

    public void importDatabase(ExportableAndImportableDatabase otherDatabase){
        if(!hasAlreadyRegisteredUsers(otherDatabase.getExpressionsPerUsers())){
            addCategories(otherDatabase.getCategories());
            addModels(otherDatabase.getModelExpressions(),
                    otherDatabase.getExpressionsPerUsers());
            addUserExpressions(otherDatabase.getExpressionsPerUsers());
        }
    }

    public void addCategories(ArrayList<Category> newCategories){
        ArrayList<Category> localCategories = getCategories();
        for (Category category : newCategories) {
            if(localCategories.contains(category))
                JOptionPane.showMessageDialog(null, "Category: "+category.getName() +
                        "\n will not be imported because it is already in local database");
            else
                addCategory(category.getName(), category.getDescription());
        }
    }

    public void addModels(ArrayList<ModelExpression> newModelExpressions,
            ArrayList<ExpressionsPerUser> expressionsPerUser){
        int newID = -1;
        int oldID;
        for (ModelExpression modelExpression : newModelExpressions) {
            newID = this.addModelExpressionAndGetGeneratedKey(modelExpression);
            if(newID >=1){
                oldID = modelExpression.getId();
                updateIDModelInUserExpressions(oldID, newID, expressionsPerUser);
            }
        }
    }

    public void updateIDModelInUserExpressions(int oldModelID,int newModelID,
            ArrayList<ExpressionsPerUser> expressionsPerUser){
        for (ExpressionsPerUser expressionsOfOneUser : expressionsPerUser) {
            for(UserExpression userExpression : expressionsOfOneUser.getUe())
                if(userExpression.getIdModelExpression() == oldModelID)
                    userExpression.setIdModelExpression(newModelID);
        }
    }

    public boolean hasAlreadyRegisteredUsers(ArrayList<ExpressionsPerUser> expressionsPerUser){
        boolean hasRegisteredUsers = false;
        String repeatedUsers = "";
        for (ExpressionsPerUser expressionsOfOneUser : expressionsPerUser) {
            try {
                User user = getUserBy(expressionsOfOneUser.getUser().getNickName());
                if(user != null){
                    repeatedUsers += ("- " + user.getNickName()+"\n");
                    hasRegisteredUsers = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(repeatedUsers.compareTo("")!=0)
            JOptionPane.showMessageDialog(null,
                "User(s):\n" + repeatedUsers +
                " is(are) already registered in local database.\n"+
                "Process will be aborted",
                "Repeated nickname error",
                JOptionPane.ERROR_MESSAGE);
        return hasRegisteredUsers;
    }

    public int addUserExpressions(ArrayList<ExpressionsPerUser> expressionsPerUser){
        int operationResult = CONTINUE_OPERATION;
        for (ExpressionsPerUser expressionsOfOneUser : expressionsPerUser) {
            try {
                User user = getUserBy(expressionsOfOneUser.getUser().getNickName());
                if(user != null){
//                    String message = "User" + user.getNickName() +
//                            " is already on local "
//                            + "database, please change the nickname of "
//                            + "the new entry";
//                    JOptionPane pane = new JOptionPane();
//                    pane.setMessage(message);
//                    pane.setWantsInput(true);
//                    pane.setVisible(true);
//
//                    String newNickName = (String) pane.getInputValue();
////                    String s = JOptionPane.showInputDialog(null, message);
//                //If a string was returned, say so.
//                    if ((newNickName != null) && (newNickName.length() > 0)) {
//                        user = getUserBy(newNickName);
//                    }
                    JOptionPane.showMessageDialog(null,
                        "User " + user.getNickName() +
                        " is already registered in local database",
                        "Repeated nickname error",
                        JOptionPane.ERROR_MESSAGE);
                    operationResult = CANCEL_OPERATION;
                    break;
                }
                addUser(expressionsOfOneUser.getUser());
            } catch (SQLException ex) {
                Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return operationResult;
    }


    public ArrayList<Category> sameCategoryNames(ExportableAndImportableDatabase
            otherDatabase){
        ArrayList<Category> otherCategories = otherDatabase.getCategories();
        ArrayList<Category> repeatedCategories = new ArrayList<Category>();
        ArrayList<Category> localCategories = getCategories();
        for (Category localCategory : localCategories) {
            for (Category othersCategory : otherCategories) {
                if (localCategory.getName().compareTo(othersCategory.getName()) == 0)
                    repeatedCategories.add(othersCategory);
            }
        }
        return repeatedCategories;
    }

    public void importUserExpressions(ArrayList<ExpressionsPerUser> ei){
        for (ExpressionsPerUser exportImportUserExpression : ei) {
            User newUser=exportImportUserExpression.getUser();
            ArrayList<UserExpression> alue=exportImportUserExpression.getUe();
            this.addUser(newUser);
            for (UserExpression userExpression : alue) {
                this.addUserExpressions(userExpression);
            }
        }
    }

    public void addUser(User u){
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO FRANK.EMUSER"
                  + "(nickname,firstName,lastName,admin,inputDBMathExpressions,"
                  + "password) "
                  + " VALUES (?, ?, ?, ?, ?,?)");
            stm.setString(1, u.getNickName());
            stm.setString(2, u.getFirstName());
            stm.setString(3, u.getLastName());
            stm.setBoolean(4,u.isAdmin());
            stm.setBoolean(5,u.isAllowedToInsertUserExpressions());
            stm.setString(6, u.getPassword());
            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Object getValueDBConfiguration(String nameOfProperty,String typeOfValue){
        Object obj=null;
        try {
            Statement stm = connection.createStatement();
            ResultSet rs=stm.executeQuery("select * from FRANK.db_configuration"); //where nameOfProperty = "
                   // + nameOfProperty);
            rs.next();
            obj=rs.getObject(typeOfValue);

        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    public void setEvaluateUserExpression(int idUE,boolean evaluated){
//        byte[] bytes=Util.Util.getBytes(matching);
        try {
            psSetEvaluatedUserExpression.setBoolean(1, evaluated);
            psSetEvaluatedUserExpression.setInt(2, idUE);
            psSetEvaluatedUserExpression.executeUpdate();
            psSetEvaluatedUserExpression.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addUserExpressions(UserExpression userExpression){

        DMathExpression mathE=userExpression.getdMExpression();
        byte[] bytesMathE;
        int[][] mathing=userExpression.getMatch();
        byte[] bytesMatching;
        bytesMathE = Util.getBytes(mathE);
        bytesMatching=Util.getBytes(mathing);
        try {
            psAddUserExpression.setString(1,userExpression.getIdUser());
            psAddUserExpression.setInt(2, userExpression.getIdModelExpression());
            psAddUserExpression.setTimestamp(3,userExpression.getTimeStampInput());
            psAddUserExpression.setBoolean(4, userExpression.isEvaluated());
            psAddUserExpression.setBytes(5, bytesMathE);
            psAddUserExpression.setBytes(6, bytesMatching);
            psAddUserExpression.setInt(7, userExpression.getId());
            psAddUserExpression.executeUpdate();
            psAddUserExpression.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<UserExpression> getUserExpressionsForModel(int idModel){
        ArrayList<UserExpression> alUE=new ArrayList<UserExpression>();
        try {
            psGetUserExpressionsForModel.setInt(1, idModel);
            psGetUserExpressionsForModel.executeQuery();
            ResultSet rs= psGetUserExpressionsForModel.getResultSet();
            int id;
            String idUser;
            Timestamp tm;
            boolean evaluated;
            DMathExpression dme;
            int[][] matching;
            while(rs.next()){
                id=rs.getInt("id");
                idUser=rs.getString("idUser");
                tm=rs.getTimestamp("timeOfInput");
                evaluated=rs.getBoolean("evaluated");
                dme=(DMathExpression) Util.getObject(rs.getBytes("mathExpression"));
                matching=(int[][])Util.getObject(rs.getBytes("matching"));
                UserExpression ue=new UserExpression(id,idModel, matching, dme,
                        evaluated, idUser, tm);
                alUE.add(ue);
            }
            psGetUserExpressionsForModel.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return alUE;
    }

    public ArrayList<UserExpression> getUserExpressionsForModel(int idModel, boolean evaluated){
        ArrayList<UserExpression> alUE=new ArrayList<UserExpression>();
        try {
            psGetUserExpressionsForModel.setInt(1, idModel);
            psGetUserExpressionsForModel.executeQuery();
            ResultSet rs= psGetUserExpressionsForModel.getResultSet();
            int id;
            String idUser;
            Timestamp tm;
            boolean databseExpressionEvaluated;
            DMathExpression dme;
            int[][] matching;
            while(rs.next()){
                databseExpressionEvaluated=rs.getBoolean("evaluated");
                if(databseExpressionEvaluated==evaluated){
                    id=rs.getInt("id");
                    idUser=rs.getString("idUser");
                    tm=rs.getTimestamp("timeOfInput");

                    dme=(DMathExpression) Util.getObject(rs.getBytes("mathExpression"));
                    matching=(int[][])Util.getObject(rs.getBytes("matching"));
                    UserExpression ue=new UserExpression(id,idModel, matching, dme,
                            evaluated, idUser, tm);
                    alUE.add(ue);
                }
            }
            psGetUserExpressionsForModel.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return alUE;
    }

    public int addModelExpressionAndGetGeneratedKey(ModelExpression me){
        DMathExpression mathE=me.getdMathExpression();
        byte[] bytesMathE;
        byte[] bytesTextualRepresentation;
        bytesTextualRepresentation = Util.getBytes(me.getTextualRepresentation());
        bytesMathE = Util.getBytes(mathE);
        String category=me.getCategoryName();
        ResultSet resultSet = null;
        int generatedKey =-1;
        try {
            psAddModel.setBytes(1, bytesMathE);
            psAddModel.setString(2,category);
            psAddModel.setBytes(3,bytesTextualRepresentation);

            int affectedRows = psAddModel.executeUpdate();
            if (affectedRows > 0){
                resultSet = psAddModel.getGeneratedKeys();
                if(resultSet.next())
                    generatedKey = resultSet.getInt(1);
            }
            psAddModel.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return generatedKey;
    }

    public ArrayList<ModelExpression> getModelExpressions(){
         ArrayList<ModelExpression> almE=new  ArrayList<ModelExpression>();
         ModelExpressionGroundTruth textRep=null;
         String category=null;
         int id=-1;
        try {
            ResultSet rs = psGetAllModels.executeQuery();
            while(rs.next()){
            try {
                    id=rs.getInt("id");
                    byte[] bytes=rs.getBytes("textualRepresentations");
                    if(bytes!=null){
                        textRep=(ModelExpressionGroundTruth) Util.getObject(bytes);
                    }else{
                        textRep=new ModelExpressionGroundTruth();
                    }
                    DMathExpression mathE=(DMathExpression)Util.getObject(rs.getBytes("mathExpression"));
                    category=rs.getString("category");
                    ModelExpression modE=new ModelExpression(id, textRep,mathE);
                    modE.setCategoryName(category);
                    almE.add(modE);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            psGetAllModels.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
        }
         return almE;
    }

//    public ArrayList<ModelExpression> getAllModelExpressions(){
//     ArrayList<ModelExpression> almE=new  ArrayList<ModelExpression>();
//     ModelExpressionGroundTruth textRep = null;
//     String category = null;
//     int id=-1;
//    try {
//        ResultSet rs = psGetAllModels.executeQuery();
//        while(rs.next()){
//            try {
//            id=rs.getInt("id");
//            byte[] bytes=rs.getBytes("textualRepresentations");
//            if(bytes!=null){
//                    textRep=(ModelExpressionGroundTruth) Util.getObject(bytes);
//                }else{
//                    textRep=new ModelExpressionGroundTruth();
//                }
//            DMathExpression mathE=(DMathExpression)Util.getObject(rs.getBytes("mathExpression"));
//            category=rs.getString("category");
//            ModelExpression modE=new ModelExpression(id, textRep,mathE);
//            modE.setCategoryName(category);
//            almE.add(modE);
//            } catch (Exception e) {
//            e.printStackTrace();
//            }
//        }
//        psGetAllModels.clearParameters();
//    } catch (SQLException ex) {
//        Logger.getLogger(DBFuntions.class.getName()).log(Level.SEVERE, null, ex);
//    }
//     return almE;
//    }
}
