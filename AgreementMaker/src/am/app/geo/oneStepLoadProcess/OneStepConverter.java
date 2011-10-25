/**
 * 
 */
package am.app.geo.oneStepLoadProcess;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import am.app.geo.IFileToDatabaseConverter;

/**
 * @author Michele Caci
 *
 */
public abstract class OneStepConverter implements IFileToDatabaseConverter {

	// information needed by the database
	protected Connection connect = null;
	protected Statement statement = null;
	
	protected String insertStatement;
	
	/**
	 * test variable for readers: look at the "main" method in subclasses 
	 */
	protected static OneStepConverter converter = null;
	
	public OneStepConverter() {
		// sets up the class
		this.setUp();
		this.insertStatement = "INSERT INTO geonamesschema.\"geonames\"(" +
	        "\"gID\", \"name\", \"asciiName\", \"alterName\", latitude, longitude, " +
	  		"\"featureClass\", \"featureCode\", \"countryCode\", cc2, \"adminCode1\", " + 
	        "\"adminCode2\", \"adminCode3\", \"adminCode4\", population, elevation, " + 
	        "\"gTopo30\", timezone, \"modificationDate\") ";
	}
	
	/**
	 * @author michele
	 * @see am.app.geo.IFileToDatabaseConverter#runAll(java.io.File)
	 * method to call to run every function
	 */
	
	@Override
	public void runAll(File inputFile) {
		runConversion(inputFile);
		tearDown();
	}

	/* (non-Javadoc)
	 * @see am.app.geo.IFileToDatabaseConverter#setUp()
	 */
	@Override
	public void setUp() {
		// This will load the PostgreSQL driver, each DB has its own driver
		try {
			Class.forName("org.postgresql.Driver");
			// Setup the connection with the DB
			// TODO: ask for user and password
			// connect = DriverManager.getConnection("jdbc:postgresql://hostname:port/dbname","username", "password");
			connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dbname","postgres", "password");
			
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

	/* (non-Javadoc)
	 * @see am.app.geo.IFileToDatabaseConverter#tearDown()
	 */
	@Override
	public void tearDown() {
		close();
	}

	/* (non-Javadoc)
	 * @see am.app.geo.IFileToDatabaseConverter#runConversion(java.io.File)
	 */
	@Override
	public void runConversion(File inputFile) {
		readFile(inputFile);
	}

	/**
	 * Default readFile just goes through each line of the text file and parse it.
	 * Separator is set to "tab"
	 * @see am.app.geo.IFileToDatabaseConverter#readFile(java.io.File)
	 */
	@Override
	public abstract void readFile(File inputFile);

	/* (non-Javadoc)
	 * @see am.app.geo.IFileToDatabaseConverter#processLine(java.lang.String, java.lang.String)
	 */
	@Override
	public abstract void processLine(String inputLine, String delimiter);
	
	// Closing properly the data linked to the database
	protected void close() {
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
