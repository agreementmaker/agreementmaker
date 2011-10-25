/**
 * 
 */
package am.app.geo.oneStepLoadProcess.buffered;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFileChooser;

import am.app.geo.oneStepLoadProcess.OneStepConverter;

/**
 * @author Michele Caci
 * Use Buffered input to parse the file and convert it into database
 */
public class OneStepBufferedConverter extends OneStepConverter {

	public static void main(String args[])
	{
		converter = new OneStepBufferedConverter();
		JFileChooser fc = new JFileChooser("~");
		fc.showOpenDialog(null);
		converter.runAll(fc.getSelectedFile());
	}
	
	/* (non-Javadoc)
	 * @see am.app.geo.oneStepLoadProcess.OneStepConverter#readFile(java.io.File)
	 */
	@Override
	public void readFile(File inputFile) {
		//Note that FileReader is used, not File, since File is not Closeable
	    BufferedReader bufReader;
		try {

			bufReader = new BufferedReader(new FileReader(inputFile));

			String currentLine = null;
			
			//Calendar cal = Calendar.getInstance();
		    //SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss:SSS");
		    //System.out.println(sdf.format(cal.getTime()));
			//long currLine = 0;
		    while ( (currentLine = bufReader.readLine()) != null ){
		    	processLine( currentLine, "\t" );
		    	//currLine++;
			    // if( currLine >= 50000) break;
		    }
		    //Calendar cal2 = Calendar.getInstance();
		    //System.out.println(sdf.format(cal2.getTime()));
		    
		    //closing the buffered reader (same reason for scanner)
		    bufReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see am.app.geo.oneStepLoadProcess.OneStepConverter#processLine(java.lang.String, java.lang.String)
	 */
	@Override
	public void processLine(String inputLine, String delimiter) {
		String[] processedLine = inputLine.split(delimiter);
		String valuesString = "";
		for(int i = 0; i < processedLine.length; i++){
			if(processedLine[i].equals("")){
				valuesString += "null";
			}
			  else{
				  // the character "'" can break the query. TODO: check for other special characters?
				  valuesString += "'" + processedLine[i].replace("'", "\\\'") + "'";
			  }
			  
			  // this is to prevent the writing of the last comma
			  if(i < processedLine.length - 1){
				  valuesString += ", ";
			  }
		  }
		
		//System.out.println(valuesString);
		  
		try {
			//adding information to the database
			String sqlInsertScript = insertStatement + "VALUES (" + valuesString + ");";
			//System.out.println(sqlInsertScript);
			
			statement = connect.createStatement();
			statement.executeUpdate(sqlInsertScript);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
