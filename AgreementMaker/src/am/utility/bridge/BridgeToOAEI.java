package am.utility.bridge;

import java.io.File;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.parsing.OutputController;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class BridgeToOAEI {

	public static void convertBridgeToOAEI( String bridgeFileName, String oaeiOutputFile, String matcherName, String sourceURI, String targetURI ) {
		
		Ontology bridgeOntology = OntoTreeBuilder.loadOWLOntology(bridgeFileName);
		
		OntModel m = bridgeOntology.getModel();
		
		Alignment<Mapping> alignment = new Alignment<Mapping>(bridgeOntology.getID(),bridgeOntology.getID());
		
		ExtendedIterator<OntClass> classesIter = m.listClasses();
		while( classesIter.hasNext() ) {
			OntClass currentClass = classesIter.next();
			
			ExtendedIterator<OntClass> equivIter = currentClass.listEquivalentClasses();
			while( equivIter.hasNext() ) {
				OntClass equivalentClass = equivIter.next();
				
				//System.out.println( currentClass.getLocalName() + " - " + equivalentClass.getLocalName() );
				
				StmtIterator defIter = m.listStatements(equivalentClass, (Property)null, (RDFNode)null);
				while( defIter.hasNext() ) {
					Statement defNode = defIter.next();
					if( defNode.getPredicate().getURI().equals("http://www.w3.org/2002/07/owl#intersectionOf") ) {
						RDFNode n = defNode.getObject();
						Resource nr = n.as(Resource.class);
						
						StmtIterator interIter = m.listStatements(nr, (Property)null, (RDFNode)null );
						while( interIter.hasNext() ) {
							Statement inter = interIter.next();
							//System.out.println(inter.toString());
							if( inter.getPredicate().getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#first")) {
								RDFNode ec = inter.getObject();
								System.out.println(currentClass + " - " + ec);
								try {
									Node sc = bridgeOntology.getNodefromOntResource(currentClass.as(OntResource.class), alignType.aligningClasses);
									Node tc = new Node(sc.getIndex(), ec.as(Resource.class), Node.OWLCLASS, bridgeOntology.getID());
									alignment.add( new Mapping(tc, sc, 1.0, MappingRelation.EQUIVALENCE));
									System.out.println(tc + " - " + sc);
									System.out.println("");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
					}
					//System.out.println(defNode.toString());
				}
				
				
			}
		}
		
		
		try {
			//OutputController.printDocumentOAEI(new File(oaeiOutputFile), alignment, matcherName, "http://purl.obolibrary.org/obo/uberon.owl", "http://mouse.owl");
			OutputController.printDocumentOAEI(new File(oaeiOutputFile), alignment, matcherName, sourceURI, targetURI);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		
		convertBridgeToOAEI("/home/cosmin/Desktop/Ontologies/Uberon with Bridges/uberon-bridge-to-ma.owl", 
							"/home/cosmin/Desktop/Ontologies/Uberon with Bridges/uberon-ma-bridge.rdf",
							"UBERON Bridge to MA", "http://purl.obolibrary.org/obo/uberon.owl", "http://mouse.owl");
		convertBridgeToOAEI("/home/cosmin/Desktop/Ontologies/Uberon with Bridges/uberon-bridge-to-ncithesaurus.owl", 
				"/home/cosmin/Desktop/Ontologies/Uberon with Bridges/uberon-ha-bridge.rdf",
				"UBERON Bridge to NCI Anatomy",  "http://purl.obolibrary.org/obo/uberon.owl" ,"http://human.owl");
	}
	
}
