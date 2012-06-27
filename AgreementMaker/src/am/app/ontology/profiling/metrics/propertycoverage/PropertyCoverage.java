package am.app.ontology.profiling.metrics.propertycoverage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.metrics.AbstractOntologyMetric;
import am.utility.numeric.AvgMinMaxNumber;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class PropertyCoverage extends AbstractOntologyMetric {

	Map<Property, CoverageTriple> classMetricMap;
	Map<Property, CoverageTriple> propertyMetricMap;
	
	int totalClassCount;
	public int getTotalClassCount() { return totalClassCount; }
	int totalPropertyCount;
	public int getTotalPropertyCount() { return totalPropertyCount; }
	
	public PropertyCoverage(Ontology o) {
		super(o);
	}
	
	@Override
	public void runMetric() {
		
		// class annotation cover
		//List<Property> classAnnotationList = new ArrayList<Property>();
		classMetricMap = new HashMap<Property,CoverageTriple>();
		
		totalClassCount = ontology.getClassesList().size();
		
		for( Node classNode : ontology.getClassesList() ) {
			OntClass currentClass = (OntClass) classNode.getResource().as(OntClass.class);
			StmtIterator i = currentClass.listProperties();
			
			Map<Property,Boolean> countedMap = new HashMap<Property,Boolean>();
			while( i.hasNext() ) {
				Statement s = (Statement) i.next();
				Property p = s.getPredicate();
	
				if( p.canAs( AnnotationProperty.class ) && countedMap.get(p) == null ) {
					// this is an annotation property
					if( classMetricMap.get(p) == null ) {
						// this property doesn't exit
						CoverageTriple t = new CoverageTriple();
						t.count = 1;
						countedMap.put(p, new Boolean(true));  // avoid counting duplicate annotations
						classMetricMap.put(p, t);
					} else {
						CoverageTriple count = classMetricMap.get(p);
						count.count++;
						countedMap.put(p, new Boolean(true));  // avoid counting duplicate annotations
					}
				}
			}
		}
		
		
		totalPropertyCount = ontology.getPropertiesList().size();
		// property annotation cover
		propertyMetricMap = new HashMap<Property,CoverageTriple>();
		
		for( Node propertyNode : ontology.getPropertiesList() ) {
			OntProperty currentProperty = (OntProperty) propertyNode.getResource().as(OntProperty.class);
			StmtIterator i = currentProperty.listProperties();
	
			Map<Property,Boolean> countedMap = new HashMap<Property,Boolean>();
			while( i.hasNext() ) {
				Statement s = (Statement) i.next();
				Property p = s.getPredicate();
	
				if( p.canAs( AnnotationProperty.class ) ) {
					// this is an annotation property
					if( propertyMetricMap.get(p) == null && countedMap.get(p) == null ) {
						// this property doesn't exit
						CoverageTriple t = new CoverageTriple();
						t.count = 1;
						propertyMetricMap.put(p, t);
						countedMap.put(p, new Boolean(true));  // avoid counting duplicate annotations
					} else {
						CoverageTriple count = propertyMetricMap.get(p);
						count.count++;
						countedMap.put(p, new Boolean(true));  // avoid counting duplicate annotations
					}
				}
			}
		}
				
		// compute the percent coverage
		for( Entry<Property,CoverageTriple> entry : classMetricMap.entrySet() ) {
			entry.getValue().coverage = (double)entry.getValue().count / (double)totalClassCount;
			if( entry.getValue().count > totalClassCount ) {
				//test?
				System.out.println("--");
			}
		}
		
		for( Entry<Property,CoverageTriple> entry : propertyMetricMap.entrySet() ) {
			entry.getValue().coverage = (double)entry.getValue().count / (double)totalPropertyCount;
			if( entry.getValue().count > totalPropertyCount ) {
				//test?
				System.out.println("--");
			}
		}
	}
	
	
	public Map<Property, CoverageTriple> getClassMap() { return classMetricMap; }
	public Map<Property, CoverageTriple> getPropertyMap() { return propertyMetricMap; }

	@Override public List<AvgMinMaxNumber> getResult() {
		// FIXME: Implement this.
		return null; 
	}
}
