package am.app.triplestore.jenatdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import am.app.triplestore.TripleStore;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.JDBC;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;
import com.hp.hpl.jena.sdb.util.StoreUtils;

public class JenaTDBTripleStore implements TripleStore{

	private StoreDesc storeDesc;
	private SDBConnection conn;
	private Store store;
	private Connection jdbcConnection;
	
	private String host;
	private String DBName;
	private String username;
	private String password;
	private int port;
	private String URI;
	
	public JenaTDBTripleStore(String host, int port, String DBName,String username,String password, String URI)
	{
		JDBC.loadDriverPGSQL();
		this.host=host;
		this.port=port;
		this.DBName=DBName;
		this.username=username;
		this.password=password;
		this.URI=URI;
	}
	@Override
	public void openConnection() {
		try {
			jdbcConnection= DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+DBName,username,password);
		} catch (SQLException e) {e.printStackTrace();}
		conn = SDBFactory.createConnection(jdbcConnection);	
		storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash,DatabaseType.PostgreSQL);
	}

	public void loadModel()
	{
		store = SDBFactory.connectStore(conn, storeDesc);
		//formate the store and empty it if it is full
		try {
			if(!StoreUtils.isFormatted(store))
				store.getTableFormatter().create();
			else
				store.getTableFormatter().truncate();
		} catch (SQLException e) {e.printStackTrace();}
		
		Model model=SDBFactory.connectDefaultModel(store);
		model.read(URI);
		
		StmtIterator sIter = model.listStatements() ;
		for ( ; sIter.hasNext() ; )
		{
			Statement stmt = sIter.nextStatement() ;
		    System.out.println(stmt) ;
		}
		sIter.close();
		
		store.close();
	}
	@Override
	public void closeConnection() {
		store.getConnection().close();
		store.close();		
	}

	@Override
	public void removeEntry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEntry() {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args)
	{
		JenaTDBTripleStore t=new JenaTDBTripleStore("localhost", 5432, "joedb", "postgres", "tomato", 
				"file:/home/joe/ADVISWorkspace/AgreementMaker/AgreementMaker/ontologies/OAEI09_OWL_RDF:XML/conference/Conference.owl");
		t.openConnection();
		t.loadModel();
		t.closeConnection();
	}
}
/*import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DataBaseMatrix 
{
	private Connection connect;
	private Statement statement;
	private String table, idType;
	
	public DataBaseMatrix(String host, String port, String dbName, String username, String pass, String t, int rowSize, int colSize)
	{
		//set all the connection information here and connect to the database
		try
		{
			Class.forName("org.postgresql.Driver");
			connect = DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+dbName,username, pass); 
		}
		catch (ClassNotFoundException e) {e.printStackTrace();} 	
		catch (SQLException e){e.printStackTrace();}
		
		//now create the table with the name passed in
		table=t;
		
		try
		{
			//first check the size of the matrix so that we use the proper postgresql data type for the id col
			if( (rowSize * colSize) > (Math.pow(2, 31) - 1) ){idType="bigserial";}
			else{idType="serial";}
			
			//create the sql query to create the table
			String queryString ="CREATE TABLE "+table+" ( " +
					"id "+idType+" UNIQUE," +
					" row integer," +
					" col integer," +
					" e1 text," +
					" e2 text," +
					" rel character(1)," +
					" sim double precision,"+
					" PRIMARY KEY (row, col));";
			
			statement = connect.createStatement();
			statement.executeUpdate(queryString);
			statement.close();
		}
		catch (SQLException e) {e.printStackTrace();}
	}
	public void set( int row, int col, String e1, String e2, char rel, double sim)
	{
		try {
			//when creating the string \'e1\' (and for other strings) needs to be used
			String queryString = "INSERT INTO "+table+" ( row, col, e1, e2, rel, sim) VALUES ("+
									row+","+col+",\'"+e1+"\',\'"+e2+"\',\'"+rel+"\',"+sim+");";
			
			//System.out.println(queryString);
			statement = connect.createStatement();
			statement.executeUpdate(queryString); // there are several kind of execute, each addresses a different kind of query (look for "javadoc Statement")
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Mapping get(int row, int col)
	{
		try {
			String queryString = "SELECT * FROM "+table+" WHERE row="+row+" AND col="+col+";";
			//System.out.println(queryString);
			
			ResultSet rs=null;//make a resultSet object because that is what is going to be returned
			
			statement = connect.createStatement();
			rs=statement.executeQuery(queryString);//returns only one resultSet 
			rs.next();//move the cursor to the first (and only) found row because it starts before the first row
			
			Mapping map=new Mapping();
			
			//set the results to the mapping classs
			map.e1=rs.getString("e1");
			map.e2=rs.getString("e2");
			map.rel=rs.getString("rel").toCharArray()[0];
			map.sim=rs.getDouble("sim");
			
			return map;//return the map
		} catch (SQLException e) {e.printStackTrace();}
		return null;//return null if no result is found, ie: after a SQLException
	}
	public void dropTable()
	{
		//deleates the table
		try {
			String queryString = "DROP TABLE "+table+";";
			System.out.println(queryString);
			
			
			statement = connect.createStatement();
			statement.execute(queryString);
		} 
		catch (SQLException e) {e.printStackTrace();}
	}
	public void closeConnection()
	{
		try {
			connect.close();
		} catch (SQLException e) {e.printStackTrace();}
	}
}
*/