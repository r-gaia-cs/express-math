/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DatabaseMathExpressions;

//import br.usp.ime.faguilar.matching.MatchingResult;
import br.usp.ime.faguilar.util.Util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frank
 */
public class DatabaseTestFunctions {
    public static final String DEFAULT_NAME_DB_CONTENT="dbContent.data";

    public static final String DEFAULT_NAME_DB_USER="users.data";

    private PreparedStatement statementAddMatchingResult;

    private PreparedStatement numberOfCorrectMatchingsForModel;

    private final static String url="jdbc:derby://localhost:1527/ExpressMatchTest";

    private final static String driver="org.apache.derby.jdbc.ClientDriver";

    private final static String connectionUser="frank";

    private final static String password="expressmatch";

    private Connection connection;

    private boolean openConnection;

    public DatabaseTestFunctions(){
        connection=null;
        openConnection=false;
    }

    public void openConnection(){
        try {

          Class.forName(driver).newInstance( );
          connection = DriverManager.getConnection(url, connectionUser,password);
          statementAddMatchingResult=connection.prepareStatement("INSERT INTO FRANK.matchingresult"
                  + " (idWriterOfModel,idWriterOfInstance,idModel,idInstance,numSymbols, "
                  + "numWrongLabels,wrongLabels) "
                  + "VALUES (?, ?,?,?,?,?,?)");

          numberOfCorrectMatchingsForModel=connection.prepareStatement("select  "
                  + "NUMSYMBOLS-NUMWRONGLABELS from "
                  + "FRANK.MATCHINGRESULT where IDMODEL=? and IDINSTANCE=?");

          openConnection=true;
        }
        catch( Exception e ) {
          e.printStackTrace( );
        }
    }

    public int getNumberOfCorrectMatchingsForModel(int idModel,int idInstance){
        int correctMatchings=-1;
        try {
            numberOfCorrectMatchingsForModel.setInt(1, idModel);
            numberOfCorrectMatchingsForModel.setInt(2, idInstance);
            ResultSet executeQuery = numberOfCorrectMatchingsForModel.executeQuery();
            if(executeQuery.next())
                correctMatchings=executeQuery.getInt(1);
            numberOfCorrectMatchingsForModel.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseTestFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return correctMatchings;
    }


//    public void addMatchingResult(MatchingResult matchingREsult){
//        try {
//             setUpStatementAndExecute(matchingREsult);
//        } catch (SQLException ex) {
//            Logger.getLogger(DatabaseTestFunctions.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

//    public void setUpStatementAndExecute(MatchingResult matchingResult) throws SQLException{
//        setUpStatementAddMatchingResult( matchingResult);
//        executeAddMatchingAndClearParameters();
//    }

    public void executeAddMatchingAndClearParameters() throws SQLException{
        statementAddMatchingResult.executeUpdate();
        statementAddMatchingResult.clearParameters();
    }

//    public void setUpStatementAddMatchingResult(MatchingResult matchingResult) throws SQLException{
//        byte[] wrongMatchings=Util.getBytes(matchingResult.getWrongMatchings());
//        statementAddMatchingResult.setString(1,matchingResult.getWriterOfModel());
//        statementAddMatchingResult.setString(2,matchingResult.getWriterOfInstance());
//        statementAddMatchingResult.setString(3,matchingResult.getIdModel());
//        statementAddMatchingResult.setString(4,matchingResult.getIdInstance());
//        statementAddMatchingResult.setInt(5,matchingResult.getNumberOfSymbols());
//        statementAddMatchingResult.setInt(6,matchingResult.getNumberOfWrongMatchings());
//        statementAddMatchingResult.setBytes(7,wrongMatchings);
//    }

    public boolean isOpenConnection() {
        return openConnection;
    }

    public void setOpenConnection(boolean openConnection) {
        this.openConnection = openConnection;
    }

}
