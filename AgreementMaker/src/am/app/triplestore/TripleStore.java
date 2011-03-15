package am.app.triplestore;


public interface TripleStore {
	
	void openConnection();//opens the connection to the repository
	void closeConnection();//closes the connection to the repository
	void removeEntry();//remove a selected entry TODO:figure out what the params are
	void addEntry();//adds an entry to the repo TODO:figure out what the params are
	//QueryResult select();//retrive an entry from the repo TODO:figure out what the params are, maybe TupleQueryResult is needed instead

}
