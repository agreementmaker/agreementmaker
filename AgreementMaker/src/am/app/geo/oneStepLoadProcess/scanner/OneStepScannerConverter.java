/**
 * 
 */
package am.app.geo.oneStepLoadProcess.scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.JFileChooser;

import am.app.geo.oneStepLoadProcess.OneStepConverter;

/**
 * @author Michele Caci
 * Use Scanner class to parse the file and convert it into database
 */
public class OneStepScannerConverter extends OneStepConverter {

	
	public static void main(String args[])
	{
		converter = new OneStepScannerConverter();
		JFileChooser fc = new JFileChooser("~");
		fc.showOpenDialog(null);
		converter.runAll(fc.getSelectedFile());
	}
	
	/**
	 * @see am.app.geo.oneStepLoadProcess.OneStepConverter#readFile(java.io.File)
	 */
	@Override
	public void readFile(File inputFile) {
		
		//Note that FileReader is used, not File, since File is not Closeable
	    Scanner scanner;
		try {
			scanner = new Scanner(new FileReader(inputFile));
			//first use a Scanner to get each line 

//			Calendar cal = Calendar.getInstance();
//		    SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss:SSS");
//		    System.out.println(sdf.format(cal.getTime()));
//		    long currLine = 0;
		    while ( scanner.hasNextLine() ){
		    	processLine(scanner.nextLine(), "\t");
//		    	currLine++;
//		    	if( currLine >= 50000 ) break;  
		    }
//		    Calendar cal2 = Calendar.getInstance();
//		    System.out.println(sdf.format(cal2.getTime()));
			    
		    //ensure the underlying stream is always closed this only has any effect if the item 
		    //passed to the Scanner constructor implements Closeable (which it does in this case).
		    scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Default processLine just print each element in the line
	 * @see am.app.geo.oneStepLoadProcess.OneStepConverter#processLine(java.lang.String, java.lang.String)
	 */
	@Override
	public void processLine(String inputLine, String delimiter) {
		//use a Scanner to parse the content of each line 
		  Scanner scanner = new Scanner(inputLine);
		  scanner.useDelimiter(delimiter);
		  String processedLine = new String();
		  String currentReadValue;
		  
		  while(scanner.hasNext()){
			  currentReadValue = scanner.next();
			  
			  if(currentReadValue.equals("")){
				  processedLine += "null";
			  }
			  else{
				  // the character "'" can break the query. TODO: check for other special characters?
				  //processedLine += "'" + currentReadValue.replace('\'', '_') + "'";
				  processedLine += "'" + currentReadValue.replace("'", "\\\'") + "'";
			  }
			  
			  // this is to prevent the writing of the last comma
			  if(scanner.hasNext()){
				  processedLine += ", ";
			  }
		  }
		  //System.out.println(processedLine);
		  
		  try {
			  //adding information to the database
			  String sqlInsertScript = insertStatement + "VALUES (" + processedLine + ");";
			  //System.out.println(sqlInsertScript);
			  
			  statement = connect.createStatement();
			  statement.executeUpdate(sqlInsertScript);
		  } catch (SQLException e) {
			  e.printStackTrace();
		  }

		  //no need to call scanner.close(), since the source is a String
	}
	
}
