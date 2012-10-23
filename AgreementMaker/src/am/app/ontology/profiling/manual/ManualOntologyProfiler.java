package am.app.ontology.profiling.manual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.MatcherStack;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.OntologyProfilerPanel;
import am.app.ontology.profiling.OntologyProfilerParameters;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.ontology.profiling.metrics.propertycoverage.CoverageTriple;
import am.app.ontology.profiling.metrics.propertycoverage.PropertyCoverage;
import am.utility.Pair;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * This ontology profiler allows the user to manually set 
 * profiling information.
 * 
 * @author Cosmin Stroe @date January 25, 2011
 *
 */
public class ManualOntologyProfiler implements OntologyProfiler {
	
	private ProfilerRegistry name;  // automatically set by the ProfilingDialog 
//	private ProfilingReport manualProfilingReport;

	private Ontology sourceOntology;
	private Ontology targetOntology;
	
	// TODO: This is a big mess.  Fix this somehow.
	private List<Property> sourceClassAnnotations;
	public List<Property> getSourceClassAnnotations() { return sourceClassAnnotations; }
	private List<Property> targetClassAnnotations;
	public List<Property> getTargetClassAnnotations() { return targetClassAnnotations; }
	private List<Property> sourcePropertyAnnotations;
	public List<Property> getSourcePropertyAnnotations() { return sourcePropertyAnnotations; }
	private List<Property> targetPropertyAnnotations;
	public List<Property> getTargetPropertyAnnotations() { return targetPropertyAnnotations; }
	
	private Map<Property, CoverageTriple> sourceClassAnnotationCoverage;
	public Map<Property, CoverageTriple> getSourceClassAnnotationCoverage() { return sourceClassAnnotationCoverage; }
	private Map<Property, CoverageTriple> sourcePropertyAnnotationCoverage;
	public Map<Property, CoverageTriple> getSourcePropertyAnnotationCoverage() { return sourcePropertyAnnotationCoverage; }
	private Map<Property, CoverageTriple> targetClassAnnotationCoverage;
	public Map<Property, CoverageTriple> getTargetClassAnnotationCoverage() { return targetClassAnnotationCoverage; }
	private Map<Property, CoverageTriple> targetPropertyAnnotationCoverage;
	public Map<Property, CoverageTriple> getTargetPropertyAnnotationCoverage() { return targetPropertyAnnotationCoverage; }
	
	private ManualProfilerMatchingParameters matchTimeParams;
	
	// main constructor
	public ManualOntologyProfiler(Ontology source, Ontology target) {  // TODO: This should be a list of ontologies.
		
		sourceOntology = source;
		targetOntology = target;
		
		sourceClassAnnotations = new ArrayList<Property>();
		for( Node classNode : source.getClassesList() ) createClassAnnotationsList(sourceClassAnnotations, classNode);
				
		targetClassAnnotations = new ArrayList<Property>();
		for( Node classNode : target.getClassesList() ) createClassAnnotationsList(targetClassAnnotations, classNode);
		
		// annotation property coverage
		PropertyCoverage sourceCoverage = new PropertyCoverage(source);
		sourceCoverage.runMetric();
		sourceClassAnnotationCoverage = sourceCoverage.getClassMap();
		sourcePropertyAnnotationCoverage = sourceCoverage.getPropertyMap();
		
		sourcePropertyAnnotations = new ArrayList<Property>();
		for( Node propertyNode : source.getPropertiesList() ) createPropertyAnnotationsList(sourcePropertyAnnotations, propertyNode);
		
		targetPropertyAnnotations = new ArrayList<Property>();
		for( Node propertyNode : target.getPropertiesList() ) createPropertyAnnotationsList(targetPropertyAnnotations, propertyNode);
		
		// annotation property coverage
		PropertyCoverage targetCoverage = new PropertyCoverage(target);
		targetCoverage.runMetric();
		targetClassAnnotationCoverage = targetCoverage.getClassMap();
		targetPropertyAnnotationCoverage = targetCoverage.getPropertyMap();
		
		
//		ProfilingReport manuProfilingReport = new ManualProfilingReport();
	}

	
	/**
	 * Given a Node representing an ontology property, append to the propertyList
	 * any annotation properties not in the list already that are defined on this property.
	 */
	public static void createPropertyAnnotationsList(
			List<Property> propertyList, Node propertyNode) {
		
		OntProperty currentProperty = (OntProperty) propertyNode.getResource().as(OntProperty.class);
		StmtIterator i = currentProperty.listProperties();

		while( i.hasNext() ) {
			Statement s = (Statement) i.next();
			Property p = s.getPredicate();

			if( p.canAs( AnnotationProperty.class) ) {
				if( !propertyList.contains(p) ) propertyList.add(p);
			}
		}
		
	}

	/**
	 * Given a Node representing an ontology class, append to the annotationList
	 * any annotation properties not in the list already that are defined on this class.
	 */
	public static void createClassAnnotationsList(
			List<Property> annotationList, Node classNode) {  // TODO: Change the LIST to a SET. -- Cosmin 9/19/2011
		
		OntClass currentClass = (OntClass) classNode.getResource().as(OntClass.class);
		StmtIterator i = currentClass.listProperties();
		while( i.hasNext() ) {
			Statement s = (Statement) i.next();
			Property p = s.getPredicate();

			if( p.canAs( AnnotationProperty.class ) ) {
				// this is an annotation property
				if( !annotationList.contains(p) ) annotationList.add(p);
			}
		}
	}

	@Override
	public OntologyProfilerPanel getProfilerPanel(boolean initial) {
		if( initial ) return null;
		return new ManualProfilerMatchingPanel(this);
	}

	@Override
	public boolean needsParams(boolean initial) {
		if( initial ) return false;	// this method does not require initial parameters (yet)
		else return true;  			// this method DOES require matching parameters
	}

	
	/* Getters and Setters */
	
	@Override public ProfilerRegistry getName() { return name; }
	@Override public void setName(ProfilerRegistry name) { this.name = name; }


	@Override public void setInitialParams(OntologyProfilerParameters param) { }

	@Override public void setMatchTimeParams(OntologyProfilerParameters param) {
		if( param instanceof ManualProfilerMatchingParameters ) {
			matchTimeParams = (ManualProfilerMatchingParameters) param;
		} else {
			// we were passed in a different kind of parameters, not much we can do.
			matchTimeParams = null;
		}
	}

	/**
	 * This method returns an iterator through a list of all possible (source,target) combinations 
	 * of the annotation properties.
	 */
	@Override
	public Iterator<Pair<String, String>> getAnnotationIterator(Node n1, Node n2) {
		List<Pair<String, String>> l = new ArrayList<Pair<String, String>>();
		
		String sourceLocalName = n1.getLocalName();
		String targetLocalName = n2.getLocalName();
		
		if( n1.isClass() ) {
			// aligning classes
			
			// source localname <-> target localname
			if( matchTimeParams.matchSourceClassLocalname && matchTimeParams.matchTargetClassLocalname ) {
				l.add(new Pair<String,String>(sourceLocalName, targetLocalName) );
			}
			
			// source localname <-> target annotations
			if( matchTimeParams.matchSourceClassLocalname && matchTimeParams.targetClassAnnotations != null ) {
				for( Property p : matchTimeParams.targetClassAnnotations ) {
					OntClass c1 = n2.getResource().as(OntClass.class);
					NodeIterator nIter = c1.listPropertyValues(p);
					while( nIter.hasNext() ) {
						RDFNode rdfNode = nIter.next();
						if( rdfNode.isLiteral() ) {
							Literal lit = rdfNode.asLiteral();
							l.add(new Pair<String,String>(sourceLocalName, lit.getString()));
						}
					}
				}
			}
			
			// source annotations <-> target localname
			if( matchTimeParams.sourceClassAnnotations != null && matchTimeParams.matchTargetClassLocalname) {
				for( Property p : matchTimeParams.sourceClassAnnotations ) {
					OntClass c1 = n1.getResource().as(OntClass.class);
					NodeIterator nIter = c1.listPropertyValues(p);
					while( nIter.hasNext() ) {
						RDFNode rdfNode = nIter.next();
						if( rdfNode.isLiteral() ) {
							Literal lit = rdfNode.asLiteral();
							l.add(new Pair<String,String>(lit.getString(), targetLocalName));
						}
					}
				}
			}
			
			// source annotations <-> target annotations
			// HOLY COW this is COMPLICATED. - cosmin
			if( matchTimeParams.sourceClassAnnotations != null && matchTimeParams.targetClassAnnotations != null ) {
				for( Property p1 : matchTimeParams.sourceClassAnnotations ) {  // iterate through the source annotation properties
					OntClass c1 = n1.getResource().as(OntClass.class);
					NodeIterator nIter1 = c1.listPropertyValues(p1);
					while( nIter1.hasNext() ) {    // iterate through the values of the source annotation properties
						RDFNode rdfNode1 = nIter1.next();
						if( rdfNode1.isLiteral() ) {  	// make sure they are literals (because we want strings)
							Literal lit1 = rdfNode1.asLiteral();
							for( Property p2 : matchTimeParams.targetClassAnnotations ) {  // for each source annotation, we iterate through all the target annotations in a similar fashion
								OntClass c2 = n2.getResource().as(OntClass.class);
								NodeIterator nIter2 = c2.listPropertyValues(p2);
								while( nIter2.hasNext() ) {  // iterate through the values of the target annotations properties
									RDFNode rdfNode2 = nIter2.next();
									if( rdfNode2.isLiteral() ) {  // again, make sure they are literals 
										Literal lit2 = rdfNode2.asLiteral();
										l.add(new Pair<String, String>( lit1.getString(), lit2.getString() )); // finally, we have two strings, add them to the list.
									}
								}
							}
						}
					}
				}
			}
			
		} else if ( n1.isProp() ) {
			// aligning properties (the same thing as above, except for properties)
			
			// source localname <-> target localname
			if( matchTimeParams.matchSourcePropertyLocalname && matchTimeParams.matchTargetPropertyLocalname ) {
				l.add(new Pair<String,String>(sourceLocalName, targetLocalName) );
			}
			
			// source localname <-> target annotations
			if( matchTimeParams.matchSourcePropertyLocalname && matchTimeParams.targetPropertyAnnotations != null ) {
				for( Property p : matchTimeParams.targetPropertyAnnotations ) {
					OntProperty c2 = n2.getResource().as(OntProperty.class);
					NodeIterator nIter = c2.listPropertyValues(p);
					while( nIter.hasNext() ) {
						RDFNode rdfNode = nIter.next();
						if( rdfNode.isLiteral() ) {
							Literal lit = rdfNode.asLiteral();
							l.add(new Pair<String,String>(sourceLocalName, lit.getString()));
						}
					}
				}
			}
			
			// source annotations <-> target localname
			if( matchTimeParams.sourcePropertyAnnotations != null && matchTimeParams.matchTargetPropertyLocalname) {
				for( Property p : matchTimeParams.sourcePropertyAnnotations ) {
					OntProperty c1 = n1.getResource().as(OntProperty.class);
					NodeIterator nIter = c1.listPropertyValues(p);
					while( nIter.hasNext() ) {
						RDFNode rdfNode = nIter.next();
						if( rdfNode.isLiteral() ) {
							Literal lit = rdfNode.asLiteral();
							l.add(new Pair<String,String>(lit.getString(), targetLocalName));
						}
					}
				}
			}
			
			// source annotations <-> target annotations
			// HOLY COW this is COMPLICATED. - cosmin
			if( matchTimeParams.sourcePropertyAnnotations != null && matchTimeParams.targetPropertyAnnotations != null ) {
				for( Property p1 : matchTimeParams.sourcePropertyAnnotations ) {  // iterate through the source annotation properties
					OntProperty c1 = n1.getResource().as(OntProperty.class);
					NodeIterator nIter1 = c1.listPropertyValues(p1);
					while( nIter1.hasNext() ) {    // iterate through the values of the source annotation properties
						RDFNode rdfNode1 = nIter1.next();
						if( rdfNode1.isLiteral() ) {  	// make sure they are literals (because we want strings)
							Literal lit1 = rdfNode1.asLiteral();
							for( Property p2 : matchTimeParams.targetPropertyAnnotations ) {  // for each source annotation, we iterate through all the target annotations in a similar fashion
								OntProperty c2 = n2.getResource().as(OntProperty.class);
								NodeIterator nIter2 = c2.listPropertyValues(p2);
								while( nIter2.hasNext() ) {  // iterate through the values of the target annotations properties
									RDFNode rdfNode2 = nIter2.next();
									if( rdfNode2.isLiteral() ) {  // again, make sure they are literals 
										Literal lit2 = rdfNode2.asLiteral();
										l.add(new Pair<String, String>( lit1.getString(), lit2.getString() )); // finally, we have two strings, add them to the list.
									}
								}
							}
						}
					}
				}
			}
		}
		
		return l.iterator();
	}

	@Override
	public List<String> getAnnotations(Node node) {
		
		List<String> annotations = new ArrayList<String>();
		
		if( node.getOntologyID() == sourceOntology.getID() ) {
			if( node.isClass() ) {
				if( matchTimeParams.matchSourceClassLocalname ) annotations.add(node.getLocalName());
				if( matchTimeParams.sourceClassAnnotations != null ) {
					for( Property p : matchTimeParams.sourceClassAnnotations ) {
						OntClass c1 = node.getResource().as(OntClass.class);
						NodeIterator nIter = c1.listPropertyValues(p);
						while( nIter.hasNext() ) {
							RDFNode rdfNode = nIter.next();
							if( rdfNode.isLiteral() ) {
								Literal lit = rdfNode.asLiteral();
								annotations.add(lit.getString());
							}
						}
					}
				}
			} else if ( node.isProp() ) {
				if( matchTimeParams.matchSourcePropertyLocalname ) annotations.add(node.getLocalName());
				if( matchTimeParams.sourcePropertyAnnotations != null ) {
					for( Property p : matchTimeParams.sourcePropertyAnnotations ) {
						OntProperty c1 = node.getResource().as(OntProperty.class);
						NodeIterator nIter = c1.listPropertyValues(p);
						while( nIter.hasNext() ) {
							RDFNode rdfNode = nIter.next();
							if( rdfNode.isLiteral() ) {
								Literal lit = rdfNode.asLiteral();
								annotations.add(lit.getString());
							}
						}
					}
				}
			}
		} else if( node.getOntologyID() == targetOntology.getID() ) {
			if( node.isClass() ) {
				if( matchTimeParams.matchTargetClassLocalname ) annotations.add(node.getLocalName());
				if( matchTimeParams.targetClassAnnotations != null ) {
					for( Property p : matchTimeParams.targetClassAnnotations ) {
						OntClass c1 = node.getResource().as(OntClass.class);
						NodeIterator nIter = c1.listPropertyValues(p);
						while( nIter.hasNext() ) {
							RDFNode rdfNode = nIter.next();
							if( rdfNode.isLiteral() ) {
								Literal lit = rdfNode.asLiteral();
								annotations.add(lit.getString());
							}
						}
					}
				}
			} else if ( node.isProp() ) {
				if( matchTimeParams.matchTargetPropertyLocalname ) annotations.add(node.getLocalName());
				if( matchTimeParams.targetPropertyAnnotations != null ) {
					for( Property p : matchTimeParams.targetPropertyAnnotations ) {
						OntProperty c1 = node.getResource().as(OntProperty.class);
						NodeIterator nIter = c1.listPropertyValues(p);
						while( nIter.hasNext() ) {
							RDFNode rdfNode = nIter.next();
							if( rdfNode.isLiteral() ) {
								Literal lit = rdfNode.asLiteral();
								annotations.add(lit.getString());
							}
						}
					}
				}
			}
		}

		return annotations;
	}

	@Override
	public MatcherStack getMatcherStack() {
		return null;
	}

	
}
