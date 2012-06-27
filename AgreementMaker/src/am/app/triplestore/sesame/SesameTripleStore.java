package am.app.triplestore.sesame;

import java.io.File;
import java.io.IOException;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import am.app.triplestore.TripleStore;

public class SesameTripleStore implements TripleStore{
	
	private Repository memoryRepo;//reposotiory that will be held in memory, not on disk
	private RepositoryConnection repoConnection;
	
	private String filePath;
	private String baseURI;
	
	public SesameTripleStore(String filePath, String baseURI)
	{
		memoryRepo= new SailRepository(new MemoryStore());//create the repo as memory storage
		try {memoryRepo.initialize();}
		catch (RepositoryException e) {e.printStackTrace();}
		
		
		this.filePath=filePath;
		this.baseURI=baseURI;
	}
	@Override
	public boolean openConnection() {
		try {
			repoConnection = memoryRepo.getConnection();//get connection to the repository
			
			//add the file to the repository
			try {repoConnection.add(new File(filePath), baseURI, RDFFormat.RDFXML);}
			catch (RDFParseException e) {e.printStackTrace();}
			catch (IOException e) {e.printStackTrace();}
		}
		catch (RepositoryException e){e.printStackTrace();}
		return true;
	}
	@Override
	public void closeConnection() {
		if(repoConnection != null){
			try {repoConnection.close();} catch (RepositoryException e) {e.printStackTrace();}
		}
	}
	@Override
	public void removeEntry() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addEntry() {
		// TODO Auto-generated method stub
		
	}

}
