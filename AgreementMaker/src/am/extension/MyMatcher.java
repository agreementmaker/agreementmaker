package am.extension;

import java.util.ArrayList;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;

public class MyMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -2384502913762576364L;

	public MyMatcher() {
		super();
	}
	
	ArrayList<Node> seen = new ArrayList<Node>();
	
	/**
	 * This method will return the synonym defined in the OAEI Human Anatomy Ontology, given the AgreementMaker Node datastructure.
	 * This method is specific to that ontology because of its unique structure.  It does not work on any other ontology. 
	 * 
	 * @param n - AgreementMaker Node representing a class in the OAEI Human Anatomy Ontology
	 * @return A list of all the defined synonyms for the given class.  If there are no synonyms defined, the list will be empty.
	 * @author Cosmin Stroe (ADVIS@UIC)
	 * @date July 23, 2010
	 */
	public ArrayList<String> getOAEIHumanAnatomySynonym( Node n ) {
		
		ArrayList<String> synonymList = new ArrayList<String>();
		
		Resource nodeResource = n.getResource();
		OntClass cls = (OntClass) nodeResource;
		OntModel om = (OntModel) cls.getModel();
		
		// we start by looking at the declared properties of the current class
		for( ExtendedIterator i = cls.listDeclaredProperties(); i.hasNext(); ) {
        	OntProperty currentProperty = (OntProperty) i.next();
        		
        	if( currentProperty.hasURI("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym") ) {
        		// We have found the hasRelatedSynonym property.
        		
                for( ExtendedIterator j = om.listStatements(cls, currentProperty, (RDFNode) null); j.hasNext(); ) { 
                	// inside the hasRelatedSynonym, there will be Synonym defitions.  Unfortunately, they are parsed as Anonymous.
                	
                	Statement stmt = (Statement) j.next();
                	RDFNode synonym = stmt.getObject();  // this is the synonym
                	Resource synonymAsResource = (Resource) synonym.as(Resource.class);
                	
                	for( ExtendedIterator k = om.listStatements(synonymAsResource, (Property) null, (RDFNode) null); k.hasNext(); ) {
                		// inside the Synonym definition, there will be a label with the actual string of the synonym
                		Statement stmt2 = (Statement) k.next();
                		Property p3 = stmt2.getPredicate();
                		if( p3.hasURI("http://www.w3.org/2000/01/rdf-schema#label") ) {
                			// so the object of the statement is the actual Literal value of the synonym.
                			Literal synonymLiteral = (Literal) stmt2.getObject();
                			synonymList.add(synonymLiteral.getString());  // add the synonym to the list
                		}
                	}
                }	
        	}
        }
		return synonymList;
	}
	
	
	
	@Override
	public Mapping alignTwoNodes( Node source, Node target, alignType typeOfNodes ) {
		
	
		if( seen.contains(target) ) return null;
		// I'm assuming that the target ontology is the Human ontology.
		
		if( typeOfNodes == alignType.aligningClasses ) {
			// we are working with a class.
			
			
			System.out.println("Class :" + target.toString() + " " + target.getLabel() );
			ArrayList<String> synonyms = getOAEIHumanAnatomySynonym(target);
			for( int i = 0; i < synonyms.size(); i++ ) {
				System.out.println("Synonym "+i+": "+ synonyms.get(i) );
			}
			System.out.println("------------------------------------------\n");
		}
		
		
		seen.add(target);
		return null;
	}	
	
}
