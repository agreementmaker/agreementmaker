package matching;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Statement;

import misc.NYTConstants;
import misc.Queries;
import misc.Utilities;
import am.app.mappingEngine.AbstractMatcher;


public class NYTInstanceMatcher extends AbstractMatcher{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3520832217713242380L;

	public NYTInstanceMatcher(String source, String target){
		System.out.println("Opening source ontology...");
		sourceOntology = Utilities.openOntology(source);
		System.out.println("Done");	
	}
	
	public void initialize(){
		
		
	}
	
	@Override
	public void match() throws Exception {
		List<Statement> indStatements = Queries.getIndividualsStatements(sourceOntology.getModel(), NYTConstants.SKOS_CONCEPT);
		
		String individualURI;
		for(Statement stmt: indStatements){
			individualURI = stmt.getSubject().getURI();
			
			
			
		}
		
		
	}
	
}
