package am.app.ontology.profiling.manual;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private List<Property> sourceAnnotations;
	private List<Property> targetAnnotations;
	public List<Property> getSourceAnnotations() { return sourceAnnotations; }
	public List<Property> getTargetAnnotations() { return targetAnnotations; }
	
	/** @deprecated Merged into {@link #sourceAnnotations} */
	@Deprecated public List<Property> getSourceClassAnnotations() { return sourceAnnotations; }
	/** @deprecated Merged into {@link #targetAnnotations} */
	@Deprecated public List<Property> getTargetClassAnnotations() { return targetAnnotations; }
	/** @deprecated Merged into {@link #sourceAnnotations} */
	@Deprecated public List<Property> getSourcePropertyAnnotations() { return sourceAnnotations; }
	/** @deprecated Merged into {@link #targetAnnotations} */
	@Deprecated public List<Property> getTargetPropertyAnnotations() { return targetAnnotations; }
	
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
		
		Set<AnnotationProperty> uniqueProperties;
		
		sourceAnnotations = new ArrayList<Property>();
		uniqueProperties = new HashSet<>();
		uniqueProperties.addAll(sourceOntology.getModel().listAnnotationProperties().toList());
		sourceAnnotations.addAll(uniqueProperties);
		
		targetAnnotations = new ArrayList<Property>();
		uniqueProperties = new HashSet<>();
		uniqueProperties.addAll(targetOntology.getModel().listAnnotationProperties().toList());
		targetAnnotations.addAll(uniqueProperties);

		// annotation property coverage
		PropertyCoverage sourceCoverage = new PropertyCoverage(source);
		sourceCoverage.runMetric();
		sourceClassAnnotationCoverage = sourceCoverage.getClassMap();
		sourcePropertyAnnotationCoverage = sourceCoverage.getPropertyMap();
		
		// annotation property coverage
		PropertyCoverage targetCoverage = new PropertyCoverage(target);
		targetCoverage.runMetric();
		targetClassAnnotationCoverage = targetCoverage.getClassMap();
		targetPropertyAnnotationCoverage = targetCoverage.getPropertyMap();
		
		
//		ProfilingReport manuProfilingReport = newFFFFFF ManualProfilingReport();
	}
	
	@Override
	public OntologyProfilerPanel getProfilerPanel(ParamType type) {
		switch(type) {
		case MATCHING_PARAMETERS:
			return new ManualProfilerMatchingPanel(this);
			
		default:
			throw new RuntimeException("This matching algorithm does not provide a panel for " + type.name());
		}
	}

	@Override
	public boolean needsParams(ParamType type) {		
		switch(type) {
		case INITIAL_PARAMETERS:
			return false; // this profiler does not require initial parameters (yet)
			
		case MATCHING_PARAMETERS:
			return true; // this profiler DOES require matching parameters
			
		default:
			return false;
		}
	}

	
	/* Getters and Setters */
	
	@Override public ProfilerRegistry getName() { return name; }
	@Override public void setName(ProfilerRegistry name) { this.name = name; }

	@Override
	public void setParams(ParamType type, OntologyProfilerParameters param) {
		assert type == ParamType.MATCHING_PARAMETERS;
		
		if( param instanceof ManualProfilerMatchingParameters ) {
			matchTimeParams = (ManualProfilerMatchingParameters) param;
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
	
	/**
	 * A helper method to instantiate a ManualOntologyProfiler using Java
	 * Reflection. The ontology profiler will <i>only</i> take into account the
	 * <b>localname</b> and <b>label</b> of the ontology concepts.
	 */
	public static OntologyProfiler createOntologyProfiler(Ontology sourceOntology, Ontology targetOntology) 
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

		ProfilerRegistry entry = ProfilerRegistry.ManualProfiler;
		Constructor<? extends OntologyProfiler> constructor = null;
		constructor = entry.getProfilerClass().getConstructor(Ontology.class, Ontology.class);
		ManualOntologyProfiler manualProfiler = 
				(ManualOntologyProfiler) constructor.newInstance(sourceOntology, targetOntology);
				
		manualProfiler.setName(entry);
		
		ManualProfilerMatchingParameters profilingMatchingParams = new ManualProfilerMatchingParameters();
		
		profilingMatchingParams.matchSourceClassLocalname = true;
		profilingMatchingParams.matchSourcePropertyLocalname = true;
		
		profilingMatchingParams.matchTargetClassLocalname = true;
		profilingMatchingParams.matchTargetPropertyLocalname = true;
		
		profilingMatchingParams.sourceClassAnnotations = new ArrayList<Property>();
		for( Property currentProperty : manualProfiler.getSourceClassAnnotations() ) {
			if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
				profilingMatchingParams.sourceClassAnnotations.add(currentProperty);
			}
		}
		
		profilingMatchingParams.sourcePropertyAnnotations = new ArrayList<Property>();
		for( Property currentProperty : manualProfiler.getSourcePropertyAnnotations() ) {
			if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
				profilingMatchingParams.sourcePropertyAnnotations.add(currentProperty);
			}
		}
		
		profilingMatchingParams.targetClassAnnotations = new ArrayList<Property>();
		for( Property currentProperty : manualProfiler.getTargetClassAnnotations() ) {
			if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
				profilingMatchingParams.targetClassAnnotations.add(currentProperty);
			}
		}
		
		profilingMatchingParams.targetPropertyAnnotations = new ArrayList<Property>();
		for( Property currentProperty : manualProfiler.getTargetPropertyAnnotations() ) {
			if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
				profilingMatchingParams.targetPropertyAnnotations.add(currentProperty);
			}
		}
		
		manualProfiler.setParams(ParamType.MATCHING_PARAMETERS, profilingMatchingParams);
		
		return manualProfiler;
	}
	
	private class LocalnameHashedProperty {
		public final Property p;
		
		public LocalnameHashedProperty(Property p) {
			this.p = p;
		}
		
		@Override public int hashCode() {
			return p.getLocalName().hashCode();
		}
	}
}
