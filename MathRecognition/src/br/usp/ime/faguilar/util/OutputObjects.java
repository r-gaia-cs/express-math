/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.ime.faguilar.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author frank
 */
public class OutputObjects {

    private String fileName;
    private ObjectOutputStream output; // outputs data to file
    
    public final static String DEFAULT_FILE_NAME="file.data";

    public OutputObjects(){
        fileName=OutputObjects.DEFAULT_FILE_NAME;
    }

      public void openFile()
      {
         try // open file
         {
            output = new ObjectOutputStream(
               new FileOutputStream( this.fileName ) );
         } // end try
         catch ( IOException ioException )
         {
            System.err.println( "Error opening file." );
         } // end catch
      } // end method openFile

      public void saveObject(Object o){
        try {
            output.writeObject(o);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex);
            System.err.println( "Error writing to file." );
            return;        
        }
      }


      public void deleteFile(){
          File fichero = new File(fileName);
          fichero.delete();
          
      }

      // close file and terminate application
      public void closeFile()
      {
         try // close file
         {
            if ( output != null )
               output.close();
         } // end try
         catch ( IOException ioException )
         {
             System.err.println( "Error closing file.");
            System.exit( 1 );
        } // end catch
     } // end method closeFile

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
