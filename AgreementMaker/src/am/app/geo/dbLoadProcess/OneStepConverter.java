/**
 * 
 */
package am.app.geo.dbLoadProcess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.Vector;

import am.app.geo.IFileToDatabaseConverter;

/**
 * @author Michele Caci
 *
 */
public class OneStepConverter implements IFileToDatabaseConverter {

	private Connection connect = null;
	private Statement statement = null;
	
	public OneStepConverter() {
		setUp();
	}
	
	@Override
	public void setUp() {
		// This will load the PostgreSQL driver, each DB has its own driver
		try {
			Class.forName("org.postgresql.Driver");
			// Setup the connection with the DB
			// TODO: ask for user and password
			// connect = DriverManager.getConnection("jdbc:postgresql://hostname:port/dbname","username", "password");
			connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/geonamesADVIS","postgres", "legione");
			
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
	public void runConversion() {
		
		try {
			
			//adding information to the database
			statement = connect.createStatement();
			statement.executeUpdate("INSERT INTO geonamesSchema.geonames (gID)" +
					" VALUES (" + 60 + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
		}
		
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
		    while ( scanner.hasNextLine() ){
		      processLine( scanner.nextLine(), "\t" );
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
		  Vector<String> outcomes = new Vector<String>();
		  String choice;
		  while(scanner.hasNext()){
			  choice = scanner.next();
			  System.out.println(choice);
			  outcomes.add(choice);
		  }
		  //no need to call scanner.close(), since the source is a String
		  //return outcomes;
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
