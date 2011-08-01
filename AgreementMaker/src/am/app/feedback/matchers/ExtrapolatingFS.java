package am.app.feedback.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sun.security.provider.certpath.Vertex;
import am.app.feedback.FilteredAlignmentMatrix;
import am.app.feedback.InitialMatchers;
import am.app.feedback.measures.FamilialSimilarity;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;

public class ExtrapolatingFS extends AbstractMatcher {
	
	private static final long serialVersionUID = -8692918883729097171L;

	InitialMatchers im = new InitialMatchers();
	
	protected FilteredAlignmentMatrix classesMatrix;
	protected FilteredAlignmentMatrix propertiesMatrix;
	
	protected FilteredAlignmentMatrix inputClassesMatrix = null;
	protected FilteredAlignmentMatrix inputPropertiesMatrix = null;
	
	protected FilteredAlignmentMatrix matrix;
	
	
	
	protected void beforeAlignOperations()throws Exception {
		matchStart();
		super.beforeAlignOperations();
    	if( inputMatchers.size() != 1 ) {
    		throw new RuntimeException("eFS Algorithm needs to have one input matcher.");
    	}
    	
    	AbstractMatcher input = inputMatchers.get(0);
    	
    	inputClassesMatrix = (FilteredAlignmentMatrix) input.getClassesMatrix();
    	inputPropertiesMatrix = (FilteredAlignmentMatrix) input.getPropertiesMatrix();
    	
    	matchEnd();
    	
	}
	
	
	public void match( Alignment<Mapping> userMappings ) throws Exception {
	
		beforeAlignOperations();
		
		classesMatrix = new FilteredAlignmentMatrix(inputClassesMatrix);
		propertiesMatrix = new FilteredAlignmentMatrix(inputPropertiesMatrix);
		
		FamilialSimilarity fs_measure = new FamilialSimilarity( getThreshold() );
		Alignment<Mapping> newMappings = new Alignment<Mapping>( sourceOntology.getID(), targetOntology.getID());
		
		
		int numMappings = userMappings.size();
		for( int i = 0; i < numMappings; i++ ) {
			
			Mapping e1e2 = userMappings.get(i);
			
			List<Node> e1_sibblingList = new ArrayList<Node>();
			
			List<Node> parentList = e1e2.getEntity1().getParents();
			for( Node parent : parentList ) {
				for( int j = 0; j < parent.getChildCount(); j++ ) {
					if( !parent.getChildAt(j).equals(e1e2.getEntity1()) ) e1_sibblingList.add(parent.getChildAt(j));
				}
			}
			
			HashMap<Node, Double> e1_simAboveThreshold = fs_measure.simSetAboveThreshold(e1_sibblingList, e1e2.getEntity1() );
			
			
			
			
			List<Node> e2_sibblingList = new ArrayList<Node>();
			
			List<Node> parentList2 = e1e2.getEntity2().getParents();
			for( Node parent : parentList2 ) {
				for( int j = 0; j < parent.getChildCount(); j++ ) {
					if( !parent.getChildAt(j).equals(e1e2.getEntity1()) ) e2_sibblingList.add(parent.getChildAt(j));
				}
			}
			
			
			HashMap<Node, Double> e2_simAboveThreshold = fs_measure.simSetAboveThreshold( e2_sibblingList, e1e2.getEntity2() );
			
			
			newMappings.addAll( compare_sets( e1_simAboveThreshold , e2_simAboveThreshold, e1e2.getAlignmentType() ) );

			
			
		}
	
		
		// now add the new mappings to the alignment sets
		classesAlignmentSet = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
		propertiesAlignmentSet = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
		for( int i = 0; i < newMappings.size(); i++ ) {
			Mapping j = newMappings.get(i);
			
			if ( j.getAlignmentType() == alignType.aligningClasses ) {
				classesAlignmentSet.add(j);
			} else {
				propertiesAlignmentSet.add(j);
			}
		}
		
		classesMatrix.validateAlignments( classesAlignmentSet );
		propertiesMatrix.validateAlignments( propertiesAlignmentSet );
	
	}


	private Alignment<Mapping> compare_sets( HashMap<Node, Double> e1SimAboveThreshold, HashMap<Node, Double> e2SimAboveThreshold, alignType tyoc ) {
		
		Alignment<Mapping> newMappings = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
		
		Set<Node> e1s = e1SimAboveThreshold.keySet();
		Set<Node> e2s = e2SimAboveThreshold.keySet();
		
		Iterator<Node> e1i = e1s.iterator();
		while( e1i.hasNext() ) {
			Node e1n = e1i.next();
			
			Iterator<Node> e2i = e2s.iterator();
			while( e2i.hasNext() ) {
				Node e2n = e2i.next();
				
				if( approx( e1SimAboveThreshold.get(e1n) , e2SimAboveThreshold.get(e2n) , 0.01 ) ) {
					Mapping newmapping = new Mapping(e1n, e2n, 1.00d, MappingRelation.EQUIVALENCE, tyoc  );
					newMappings.add( newmapping );
				}
				
			}
		}
		
		
		return newMappings;
		
	}
	
	private boolean approx( Double i, Double j, Double delta ) {
		
		
		if( j <= i+delta && j >= i-delta ) return true;
		return false;
	}
	
	
   public void select() {
    	//this method is also invoked everytime the user change threshold or num relation in the table
    	beforeSelectionOperations();//Template method to allow next developer to add code after selection
    	selectAndSetAlignments();	
    	afterSelectionOperations();//Template method to allow next developer to add code after selection
    }

   
   protected void selectAndSetAlignments() {
   	if(alignClass) {
   		classesAlignmentSet = scanMatrix(classesMatrix);
   	}
   	if(alignProp) {
   		propertiesAlignmentSet = scanMatrix(propertiesMatrix);
   	}
	}
   
}
