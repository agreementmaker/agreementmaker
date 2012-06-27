package am.app.triplestore.jenatdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

public class JenaTDBTripleStore{

	private StoreDesc storeDesc;
	private SDBConnection connSource,connTarget;
	private Store storeSource;
	private Store storeTarget;
	private Connection jdbcConnectionTarget;
	
	private String hostSource,hostTarget;
	private String DBNameSource,DBNameTarget;
	private String usernameSource,usernameTarget;
	private String passwordSource,passwordTarget;
	private int portSource,portTarget;
	private String URISource,URITarget;
	private Connection jdbcConnectionSource;
	private boolean persistentSource,persistentTarget;
	
	public JenaTDBTripleStore(String hostSource, int portSource, String DBNameSource,String usernameSource,String passwordSource, String URISource, 
							  String hostTarget, int portTarget, String DBNameTarget,String usernameTarget,String passwordTarget,String URITarget,
							  boolean persistantS, boolean persistantT)
	{
		JDBC.loadDriverPGSQL();
		this.hostSource=hostSource;
		this.portSource=portSource;
		this.DBNameSource=DBNameSource;
		this.usernameSource=usernameSource;
		this.passwordSource=passwordSource;
		this.URISource=URISource;
		
		this.hostTarget=hostTarget;
		this.portTarget=portTarget;
		this.DBNameTarget=DBNameTarget;
		this.usernameTarget=usernameTarget;
		this.passwordTarget=passwordTarget;
		this.URITarget=URITarget;
		
		persistentSource=persistantS;
		persistentTarget=persistantT;
		
		storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesIndex,DatabaseType.PostgreSQL);
	}

	public boolean openSourceConnection() {
		try {
			jdbcConnectionSource= DriverManager.getConnection("jdbc:postgresql://"+hostSource+":"+portSource+"/"+DBNameSource,usernameSource,
					passwordSource);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
			}
		connSource = SDBFactory.createConnection(jdbcConnectionSource);	
		storeSource = SDBFactory.connectStore(connSource, storeDesc);
		
		try {
			System.out.println(StoreUtils.isFormatted(storeSource));
			if(!StoreUtils.isFormatted(storeSource))
				storeSource.getTableFormatter().create();
			else if(!persistentSource)
				storeSource.getTableFormatter().truncate();

		} catch (SQLException e) {e.printStackTrace();}
		
		return true;
	}
	
	public boolean openTargetConnection() {
		try {
			jdbcConnectionTarget= DriverManager.getConnection("jdbc:postgresql://"+hostTarget+":"+portTarget+"/"+DBNameTarget,usernameTarget,
					passwordTarget);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
			}
		connTarget= SDBFactory.createConnection(jdbcConnectionTarget);	
		storeTarget = SDBFactory.connectStore(connTarget, storeDesc);
		
		//formate the store and empty it if it is full
		try {
			if(!StoreUtils.isFormatted(storeTarget))
				storeTarget.getTableFormatter().create();
			else if(!persistentTarget)
				storeTarget.getTableFormatter().truncate();
		} catch (SQLException e) {e.printStackTrace();}
		
		return true;
	}

	public void loadSourceModel()
	{
		//formate the store and empty it if it is full
		Model modelSource=SDBFactory.connectDefaultModel(storeSource);
		
		if(!persistentSource)
			modelSource.read(URISource);
		
		StmtIterator sIter = modelSource.listStatements() ;
		int x=0;
		for ( ; sIter.hasNext() ; )
		{
			Statement stmt = sIter.nextStatement() ;
		    System.out.println(stmt) ;
		    x++;
		}
		sIter.close();
		System.out.println(x);
		storeSource.close();
	}

	public void loadTargetModel()
	{
		Model modelTarget=SDBFactory.connectDefaultModel(storeTarget);
		if(!persistentTarget)
			modelTarget.read(URITarget);
		
		StmtIterator sIter = modelTarget.listStatements() ;
		int x=0;
		for ( ; sIter.hasNext() ; )
		{
			Statement stmt = sIter.nextStatement() ;
		    System.out.println(stmt) ;
		    x++;
		}
		sIter.close();
		System.out.println(x);

		storeTarget.close();
	}
	
	public void closeSourceConnection() {
		if(!persistentSource)
			storeSource.getTableFormatter().truncate();
		storeSource.getConnection().close();
		storeSource.close();
	}
	public void closeTargetConnection() {
		if(!persistentTarget)
			storeTarget.getTableFormatter().truncate();
		storeTarget.getConnection().close();
		storeTarget.close();
	}


	public void removeEntry() {
		// TODO Auto-generated method stub
		
	}


	public void addEntry() {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args)
	{
		JenaTDBTripleStore t=new JenaTDBTripleStore("localhost", 5432, "source", "postgres", "tomato", 
				"file:/home/joe/ADVISWorkspace/AgreementMaker/AgreementMaker/ontologies/OAEI09_OWL_RDF:XML/conference/Conference.owl",
				"localhost", 5432, "target", "postgres", "tomato",
				"file:/home/joe/ADVISWorkspace/AgreementMaker/AgreementMaker/ontologies/OAEI09_OWL_RDF:XML/conference/edas.owl",true,true);
		t.openSourceConnection();
		t.openTargetConnection();
		t.loadSourceModel();
		t.loadTargetModel();
		t.closeSourceConnection();
		t.closeTargetConnection();
	}
}
/*import java.sql.Connection;s
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