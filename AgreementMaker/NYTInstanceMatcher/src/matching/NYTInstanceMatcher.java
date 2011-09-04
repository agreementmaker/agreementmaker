package matching;

import java.io.File;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;

import edu.uic.advis.im.knowledgebase.ontology.OntologyKBFactory;

import misc.NYTConstants;
import misc.Queries;
import misc.Utilities;
import am.app.mappingEngine.AbstractMatcher;
import am.app.ontology.Ontology;


public class NYTInstanceMatcher{
	Ontology sourceOntology;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3520832217713242380L;

	public NYTInstanceMatcher(String source, String target){
		System.out.println("Opening source ontology...");
		sourceOntology = Utilities.openOntology(source);
		System.out.println("Done");
		
		System.out.println("Target is " + target  + ", choosing dataset");
		if(target.equals(NYTConstants.DBPEDIA)){
			
			OntologyKBFactory.createKBFromXML(new File(System.getProperty("user.dir") + File.separator + NYTConstants.DBP_PERSONDATA));
			
		}
		
		
		
	}
	
	public void initialize(){
		
		
	}
	
	public void match() throws Exception {
		OntModel sourceModel = sourceOntology.getModel();
		List<Statement> indStatements = Queries.getIndividualsStatements(sourceModel, NYTConstants.SKOS_CONCEPT);
		
		String instanceURI;
		for(Statement stmt: indStatements){
			instanceURI = stmt.getSubject().getURI();
			
			String label = Queries.getPropertyValue(sourceModel, instanceURI, NYTConstants.SKOS_PREFLABEL);
			System.out.println(label);
			
			
		}
		
		
	}
	
}
