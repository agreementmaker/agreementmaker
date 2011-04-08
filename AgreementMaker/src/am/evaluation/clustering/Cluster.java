package am.evaluation.clustering;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

public class Cluster<E extends Mapping> implements Iterable<E> {

	private String name;
	private ArrayList<E> clusterSet;
	
	public Cluster() {
		clusterSet = new ArrayList<E>();
	}
	
	@SuppressWarnings("unchecked")
	public Cluster( TreeSet<Point> set, Ontology sourceOntology, Ontology targetOntology, VisualizationType t ) {
		clusterSet = new ArrayList<E>();
		
		ArrayList<Node> sourceList = null;
		ArrayList<Node> targetList = null;
		alignType aType = null;
		
		if( t == VisualizationType.CLASS_MATRIX ) {
			sourceList = sourceOntology.getClassesList();
			targetList = targetOntology.getClassesList();
			aType = alignType.aligningClasses;
		} else if ( t == VisualizationType.PROPERTIES_MATRIX ) {
			sourceList = sourceOntology.getPropertiesList();
			targetList = targetOntology.getPropertiesList();
			aType = alignType.aligningProperties;
		}
		
		for( Point p : set ) {
			clusterSet.add( (E) new Mapping(sourceList.get(p.x), targetList.get(p.y), 1.0, MappingRelation.EQUIVALENCE, aType  ) ); // unchecked conversion
		}
		
	}
	
	
	public void addMapping( E m ) { clusterSet.add(m); }
	public ArrayList<E> getMappingList() { return clusterSet; }
	public Alignment<E> getAlignment() {
		Alignment<E> a = new Alignment<E>(Ontology.ID_NONE, Ontology.ID_NONE);
		for( E m : clusterSet ) { a.add(m); }
		return a;
	}
	
	/** GETTERS and SETTERS **/
	public String getName() { return name; }
	public void setName( String n ) { name = n; }
	
	/** SIZE **/
	public int size() { 
		if( clusterSet == null ) return 0; 
		return clusterSet.size(); 
	}

	@Override
	public Iterator<E> iterator() {	return clusterSet.iterator(); }
	
}
