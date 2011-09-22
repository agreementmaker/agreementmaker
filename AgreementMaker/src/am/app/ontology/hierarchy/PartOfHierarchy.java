package am.app.ontology.hierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.NodeHierarchy;
import am.app.ontology.Ontology;
import am.userInterface.canvas2.graphical.GraphicalData;
import am.userInterface.canvas2.nodes.LegacyEdge;
import am.userInterface.canvas2.nodes.LegacyNode;
import am.userInterface.canvas2.utility.CanvasGraph;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class PartOfHierarchy implements NodeHierarchy {

	/** This is the parents map, used to be able to tell which are the parents of a node. */
	protected HashMap<OntClass,List<OntClass>> parentsToChildrenMap = new HashMap<OntClass,List<OntClass>>();
	
	/** This is the children map, used to be able to tell which are the children of a node. */
	protected HashMap<OntClass,List<OntClass>> childrenToParentsMap = new HashMap<OntClass,List<OntClass>>();
	
	public PartOfHierarchy(Ontology ont, Property partOfProperty) {
		
	}
	
	@Override
	public Set<Node> getChildren(Node n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Node> getParents(Node n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Node> getSiblings(Node n) {
		// TODO Auto-generated method stub
		return null;
	}

	/*protected void recursiveBuildClassGraph(LegacyNode parentNode, 
			OntClass parentClass, int depth, CanvasGraph graph, Ontology ont, 
			HashMap<OntResource,LegacyNode> hashMap) {
		
		Logger log = null;
		if( Core.DEBUG ) { 
			log = Logger.getLogger(this.getClass());
			log.setLevel(Level.DEBUG);
			log.debug(parentClass);
		}
		
		try {
			List<OntClass> childrenList = childrenToParentsMap.get(parentClass);
			if( childrenList == null ) 
				return;
			Iterator<OntClass> clsIter = childrenList.iterator();
			while( clsIter.hasNext() ) {
				OntClass cls = clsIter.next();
				if( cls.isAnon() ) {
					hashMap.put(cls, anonymousNode);  // avoid cycles between anonymous nodes
					if( Core.DEBUG ) log.debug(">> Inserted anonymous node " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
					recursiveBuildClassGraph( parentNode, cls, depth, graph, ont, hashMap );
					continue;
				} else if( cls.equals( OWL.Nothing ) ) 
					continue;
	
				// this is the cycle check
				if( hashMap.containsKey(cls) ) { // we have seen this node before, do NOT recurse again
					if( Core.DEBUG ) log.debug("Cycle detected.  OntClass:" + cls );
					continue;
				}
				
				
				try {
					// Try to connect this graphical represenation of an Ontology Class to the Node object that represents that class.
					Node amnode = ont.getNodefromOntResource(cls, alignType.aligningClasses);
					amnode.addGraphicalRepresentation(node);
				} catch (Exception e) {
					// An exception has been thrown by getNodefromOntResource().
					// This means that the OntClass was not found, therefore we cannot connect this LegacyNode to a Node object.
					if( Core.DEBUG ) {
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
				}
				
				graph.insertVertex(node);
					
				LegacyEdge edge = new LegacyEdge( parentNode, node, null, this );
				graph.insertEdge( edge );
				
				hashMap.put(cls, node);
				if( Core.DEBUG ) {
					log.debug(">> Inserted " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
					log.debug(">>   Label: " + cls.getLabel(null));
				}
				recursiveBuildClassGraph( node, cls, depth+1, graph, ont, hashMap );
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	};*/
	
	
	/**
	 * Get the hierarchy roots of the hierarchy implied by the hierarchyProperty. 
	 */
	protected List<OntClass> getAlternateClassHierarchyRoots(OntModel m, OntProperty hierarchyProperty) {
		
		Logger log = null;
		if( Core.DEBUG ) log = Logger.getLogger(this.getClass());
		
		ArrayList<OntClass> hierarchyRoots = new ArrayList<OntClass>();
				
		// query the model for all the property relations
		String queryString  = "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
	       queryString += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
	       queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
	       queryString += "SELECT ?object ?subject \n { \n";
	       queryString += "?object rdf:type owl:Class .";
	       queryString += "?object rdfs:subClassOf ?restriction .";
	       queryString += "?restriction rdf:type owl:Restriction .";
	       queryString += "?restriction owl:onProperty <"+hierarchyProperty.getURI().toString()+"> .";
	       queryString += "?restriction owl:someValuesFrom ?subject .";
	       queryString += "}";
		
        Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet results = qexec.execSelect() ;
			
			while( results.hasNext() ) {
				QuerySolution currentSolution = results.next();
				Resource subject = currentSolution.getResource("subject");
				Resource object = currentSolution.getResource("object");
				
				OntClass subjectClass = null;
				if( subject.canAs(OntClass.class) ) subjectClass = subject.as(OntClass.class);
				
				OntClass objectClass = null;
				if( object.canAs(OntClass.class) ) objectClass = object.as(OntClass.class);
				
				if( subjectClass == null || objectClass == null ) 
					continue; 
				
				if( Core.DEBUG ) log.debug(subjectClass + " " + hierarchyProperty + " " + objectClass);
				
				if( subjectClass.isURIResource() && objectClass.isURIResource() ) {
					
					if( parentsToChildrenMap.get(objectClass) == null ) {
						// no list exists for this concept.
						List<OntClass> childrenList = new ArrayList<OntClass>();
						childrenList.add(subjectClass);
						parentsToChildrenMap.put(objectClass, childrenList);
					} else {
						// a list of children already exists.
						List<OntClass> childrenList = parentsToChildrenMap.get(objectClass);
						childrenList.add(subjectClass);
					}
					
					if( childrenToParentsMap.get(subjectClass) == null ) {
						// no list of parents exists for this concept.
						List<OntClass> parentsList = new ArrayList<OntClass>();
						parentsList.add(objectClass);
						childrenToParentsMap.put(subjectClass, parentsList);
					} else {
						// a list of parents already exists
						List<OntClass> parentsList = childrenToParentsMap.get(subjectClass);
						parentsList.add(objectClass);
					}
				}
					
			}
			
//				for ( ; results.hasNext() ; )
//				{
//					//QuerySolution soln = results.nextSolution() ;
//					
//				}
		} catch(Exception exc) {
			exc.printStackTrace();
			Utility.displayErrorPane(exc.toString() + "\n\n" + exc.getMessage(), "ERROR");
		} finally {
			qexec.close();
		}
	       
		
		ExtendedIterator<OntClass> classesIter = m.listClasses();
		while( classesIter.hasNext() ) {
			OntClass currentClass = classesIter.next();
			if( parentsToChildrenMap.get( currentClass ) == null ) {
				// this is a hierarchy root.
				hierarchyRoots.add(currentClass);
			}
		}
		
		return hierarchyRoots;
	}
	
}
