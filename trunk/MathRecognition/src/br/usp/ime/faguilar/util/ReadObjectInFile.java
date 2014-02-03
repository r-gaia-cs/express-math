/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * @author frank
 */
public class ReadObjectInFile {
    private ObjectInputStream input;
    private String fileName;

    public ReadObjectInFile(){
        fileName=OutputObjects.DEFAULT_FILE_NAME;
    }
     public void openFile()
     {
        try // open file
        {
           input = new ObjectInputStream(
              new FileInputStream( fileName ) );
        } // end try
        catch ( IOException ioException )
        {
           System.err.println( "Error opening file." );
        } // end catch
     } // end method openFile

     // read record from file
     public Object readRecords()
     {

        try // input the values from the file
        {
          
              return input.readObject();
        } // end try
//        catch ( EOFException endOfFileException )
//        {
//           return null; // end of file was reached
//        } // end catch
//        catch ( ClassNotFoundException classNotFoundException )
//        {
//           System.err.println( "Unable to create object." );
//        } // end catch
        catch ( Exception ioException )
        {
           return null;
        } // end catch
     } // end method readRecords

     // close file and terminate application
     public void closeFile()
     {
        try // close file and exit
        {
           if ( input != null )
              input.close();
        } // end try
        catch ( IOException ioException )
        {
           System.err.println( "Error closing file." );
           System.exit( 1 );
        } // end catch
     } // end method

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
