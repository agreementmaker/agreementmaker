/**
 * 
 */
package am.app.geo.dbLoadProcess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import am.app.Core;
import am.app.geo.IFileToDatabaseConverter;
import am.batchMode.TrackDispatcher;
import am.userInterface.UI;

/**
 * @author Michele Caci
 *
 */
public class OneStepScannerConverter implements IFileToDatabaseConverter {

	private static OneStepScannerConverter converter = null;
	
	public static void main(String args[])
	{
		converter = new OneStepScannerConverter();
		JFileChooser fc = new JFileChooser("~");
		fc.showOpenDialog(null);
		converter.runAll(fc.getSelectedFile());
	}
	
	
	private Connection connect = null;
	private Statement statement = null;
	
	public OneStepScannerConverter() {
		// sets up the class
		setUp();
	}
	

	/**
	 * @author michele
	 * method to call to run every function
	 */
	@Override
	public void runAll(File inputFile) {
		runConversion(inputFile);
		close();
	}

	
	@Override
	public void setUp() {
		// This will load the PostgreSQL driver, each DB has its own driver
		try {
			Class.forName("org.postgresql.Driver");
			// Setup the connection with the DB
			// TODO: ask for user and password
			// connect = DriverManager.getConnection("jdbc:postgresql://hostname:port/dbname","username", "password");
			connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/advis","postgres", "Tomato1234");
			
			//schema creation: TODO
			//table creation: TODO
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void tearDown() {
		close();		
	}

	@Override
	public void runConversion(File inputFile) {
		readFile(inputFile);
	}
	
	/**
	 * Default readFile just goes through each line of the text file and parse it.
	 * Separator is set to "tab"
	 * @see am.app.geo.IFileToDatabaseWrapper#readFile(java.io.File)
	 */
	@Override
	public void readFile(File inputFile) {
		
		//Note that FileReader is used, not File, since File is not Closeable
	    Scanner scanner;
		try {
			scanner = new Scanner(new FileReader(inputFile));
			//first use a Scanner to get each line
			long currentLine = 0;
		    while ( scanner.hasNextLine() ){
		      processLine( scanner.nextLine(), "\t" );
		      currentLine++;
		      if( currentLine % 10000 == 0 ) System.out.println(currentLine + "\n"); 
		    }
		    //ensure the underlying stream is always closed this only has any effect if the item 
		    //passed to the Scanner constructor implements Closeable (which it does in this case).
		    scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Default processLine just print each element in the line
	 * @see am.app.geo.IFileToDatabaseWrapper#processLine(java.lang.String, java.lang.String)
	 */
	@Override
	public void processLine(String inputLine, String delimiter) {
		//use a second Scanner to parse the content of each line 
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
			  String sqlInsertScript = "INSERT INTO geonames.\"geonamesDB\"(" +
		            "\"gID\", \"name\", \"asciiName\", \"alterName\", latitude, longitude, " +
			  		"\"featureClass\", \"featureCode\", \"countryCode\", cc2, \"adminCode1\", " + 
		            "\"adminCode2\", \"adminCode3\", \"adminCode4\", population, elevation, " + 
		            "\"gTopo30\", timezone, \"modificationDate\") " +
				    "VALUES (" + processedLine + ");";
			  //System.out.println(sqlInsertScript);
			  
			  statement = connect.createStatement();
			  statement.executeUpdate(sqlInsertScript);
		  } catch (SQLException e) {
			  e.printStackTrace();
			  try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		  }

		  //no need to call scanner.close(), since the source is a String
	}
	
	// You need to close the resultSet
	private void close() {
		try {

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}
}
