package am.matcher.lod.hierarchy;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrixOld;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.mappingEngine.threaded.AbstractMatcherRunner;
import am.app.ontology.Node;

/**
 * A purely structural matcher based on tree similarity.
 * @author cosmin
 *
 */
public class HierarchyStructureMatcher extends AbstractMatcher {

	private static final long serialVersionUID = 7144844302412965889L;
	
	Logger log = Logger.getLogger(HierarchyStructureMatcher.class);
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		// TODO Auto-generated method stub
		super.beforeAlignOperations();
		log.setLevel(Level.INFO);
	}
	
	@Override
	protected SimilarityMatrix alignNodesOneByOne(List<Node> sourceList,
			List<Node> targetList, alignType typeOfNodes) throws Exception {
		
		if(param.completionMode && inputMatchers != null && inputMatchers.size() > 0){ 
    		//run in optimized mode by mapping only concepts that have not been mapped in the input matcher
    		if(typeOfNodes.equals(alignType.aligningClasses)){
    			return alignUnmappedNodes(sourceList, targetList, inputMatchers.get(0).getClassesMatrix(), inputMatchers.get(0).getClassAlignmentSet(), alignType.aligningClasses);
    		}
    		else{
    			return alignUnmappedNodes(sourceList, targetList, inputMatchers.get(0).getPropertiesMatrix(), inputMatchers.get(0).getPropertyAlignmentSet(), alignType.aligningProperties);
    		}
		}
    	
    	else if(param.largeOntologyMode ==true){
    		//run as a generic matcher who maps all concepts by doing a quadratic number of comparisons
	    	SimilarityMatrix matrix = new SparseMatrix(sourceOntology, targetOntology, typeOfNodes);
			Node source;
			Node target;
			Mapping alignment = null; //Temp structure to keep sim and relation between two nodes, shouldn't be used for this purpose but is ok
			for(int i = 0; i < sourceList.size(); i++) {
				source = sourceList.get(i);
				for(int j = 0; j < targetList.size(); j++) {
					target = targetList.get(j);
					
					if( !this.isCancelled() ) { alignment = alignTwoNodes(source, target, typeOfNodes, matrix); }
					else { return matrix; }
					if(alignment != null && alignment.getSimilarity() >= param.threshold)
						matrix.set(i,j,alignment);
					if( isProgressDisplayed() ) {
						stepDone(); // we have completed one step
						if( alignment != null && alignment.getSimilarity() >= param.threshold ) tentativealignments++; // keep track of possible alignments for progress display
					}
				}
				if( isProgressDisplayed() ) updateProgress(); // update the progress dialog, to keep the user informed.
			}
			return matrix;
    	}
    	else{
    		SimilarityMatrix matrix = new ArraySimilarityMatrixOld(sourceOntology, targetOntology, typeOfNodes);; 

    		Node source;
			Node target;
//			Mapping alignment = null; //Temp structure to keep sim and relation between two nodes, shouldn't be used for this purpose but is ok
			

			int availableProcessors = Runtime.getRuntime().availableProcessors() - param.threadedReservedProcessors;
			if( availableProcessors < 1 ) // this should not happen 
				availableProcessors = 1;  // but in case it does, we fix it.
			
			if( param.threadedExecution && targetList.size() > availableProcessors ) {
				threadGroup = new ThreadGroup(getName());
				
				// partition the search space into smaller pieces, then assign each partition to a thread
				int sourceStartIndices[] = new int[availableProcessors];
				int sourceEndIndices[]   = new int[availableProcessors];
				int targetStartIndices[] = new int[availableProcessors];
				int targetEndIndices[]   = new int[availableProcessors];
				
				int sourceRemainder = sourceList.size() % availableProcessors;
				int sourceChunkSize = ( sourceList.size() - sourceRemainder ) / availableProcessors;
				
				int targetRemainder = targetList.size() % availableProcessors;
				int targetChunkSize = ( targetList.size() - targetRemainder ) / availableProcessors;
				
				for( int i = 0; i < availableProcessors; i++ ) {
					sourceStartIndices[i] = i*sourceChunkSize;
					sourceEndIndices[i] = sourceStartIndices[i] + sourceChunkSize - 1;
					
					targetStartIndices[i] = i*targetChunkSize;
					targetEndIndices[i] = targetStartIndices[i] + targetChunkSize - 1;
					
					if( i == (availableProcessors - 1) ) { 
						sourceEndIndices[i] += sourceRemainder;
						targetEndIndices[i] += targetRemainder;
					}
				}
				
				//updateProgress();
				
				// run the stages, spawn threads
				for( int stage = 0; stage < availableProcessors; stage++ ) {
					
					for( int thread = 0; thread < availableProcessors; thread++ ) {
						int targetIndex = (thread + stage) % availableProcessors;
						
						AbstractMatcherRunner runner = 
								new AbstractMatcherRunner(sourceList, targetList, 
										sourceStartIndices[thread], sourceEndIndices[thread], targetStartIndices[targetIndex], targetEndIndices[targetIndex], 
										matrix, this, typeOfNodes);
						
						Thread newThread = new Thread(threadGroup, runner);
						newThread.start();
						
						if( param.threadedOverlap ) {
							// before spawning another thread, make sure we have room to do it
							while( threadGroup.activeCount() >= availableProcessors ) { 
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
									this.cancel(true);
								} 
							}
						}
					}
			
					// wait for the threads at this stage to end, in order to avoid overlaps
					if( !param.threadedOverlap ) {
						while( threadGroup.activeCount() > 0 ) { 
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
								this.cancel(true);
							} 
						}
					}
					
				}
				
				// If running in overlap mode, we have to wait for the threads to end.
				if( param.threadedOverlap ) {
					while( threadGroup.activeCount() > 0 ) { 
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
							this.cancel(true);
						} 
					}
				}
				
			} else {
				// non threaded, normal execution
				for(int i = 0; i < sourceList.size(); i++) {
					source = sourceList.get(i);
					for(int j = 0; j < targetList.size(); j++) {
						target = targetList.get(j);
						
						if( !this.isCancelled() ) {
							Mapping alignment = alignTwoNodes(source, target, typeOfNodes, matrix);
							
							matrix.set(i,j,alignment);
							
							if( isProgressDisplayed() ) {
								stepDone(); // we have completed one step
								if( alignment != null && alignment.getSimilarity() >= param.threshold ) {
									tentativealignments++; // keep track of possible alignments for progress display
									//System.out.println(alignment);
								}
								updateProgress();
							}
						}
						else { return matrix; }
					}
					if( isProgressDisplayed() ) updateProgress(); // update the progress dialog, to keep the user informed.
				}
			} 
			
			return matrix;
    	}
		
	}
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes, SimilarityMatrix matrix) throws Exception {
	
		// if we've already matched these nodes return the mapping.
		if( matrix.get(source.getIndex(), target.getIndex()) != null ) 
			return matrix.get(source.getIndex(), target.getIndex());
		
		if( source.getChildCount() == 0 && target.getChildCount() == 0 ) {
			// this is the base case.
			return new Mapping(source, target, 1.0d);
		}
		
		if( source.getChildCount() == 0 || target.getChildCount() == 0 )
			// a node with no children is not similar to a node with children.
			return null;
		
		
		List<Node> sourceChildren = source.getChildren();
		List<Node> targetChildren = target.getChildren();
		
		// as we iterate through the children, keep track of their indices
		List<Integer> sourceIndices = new ArrayList<Integer>();
		List<Integer> targetIndices = new ArrayList<Integer>();
		
		// align the children.
		for( Node sourceChild : sourceChildren ) {
			sourceIndices.add( new Integer(sourceChild.getIndex()) );
			for( Node targetChild : targetChildren ) {
				targetIndices.add( new Integer(targetChild.getIndex()) );
				Mapping m = alignTwoNodes( sourceChild, targetChild, typeOfNodes, matrix);
				matrix.set(sourceChild.getIndex(), targetChild.getIndex(), m);  // save the mapping
			}
		}
		
		
		int n = Math.min(sourceChildren.size(), targetChildren.size());
		int m = Math.max(sourceChildren.size(), targetChildren.size());

		double multiplicationFactor = (double) n / (double) m; // guaranteed to be between 0 and 1.0
		
		// now select the top k mappings for our children
		List<Mapping> bestMappings = matrix.chooseBestN(sourceIndices, targetIndices);
		
		// calculate the average of the best similarities
		double similaritySum = 0.0d;
		for( Mapping mapping : bestMappings ) {
			similaritySum += mapping.getSimilarity();
		}
		
		double similarityAverage = 0.0d;
		if( bestMappings.size() > 0 ) {
			similarityAverage = similaritySum / bestMappings.size(); // guaranteed to be between 0 and 1.0
		}
		
		double similarity = similarityAverage * multiplicationFactor;
		
		Mapping returnMapping = new Mapping(source, target, similarity);
		
		log.info("Created mapping: " + returnMapping);
		
		return returnMapping;
	}

	@Override
	public void beforeSelectionOperations() {
		super.beforeSelectionOperations();
		
		Node sourceRoot = sourceOntology.getClassesRoot();
		Node targetRoot = targetOntology.getClassesRoot();
		
		List<Node> sourceRootChildren = sourceRoot.getChildren();
		List<Node> targetRootChildren = targetRoot.getChildren();
		
		//for( )
		
		for( Node sourceRootChild : sourceRootChildren ) {
			for( Node targetRootChild : targetRootChildren ) {
				// TODO: Finish this!
			}
		}
		
	}
	
	@Override
	public void select() {

		Node sourceRoot = sourceOntology.getClassesRoot();
		Node targetRoot = targetOntology.getClassesRoot();
		
		List<Node> sourceRootChildren = sourceRoot.getChildren();
		List<Node> targetRootChildren = targetRoot.getChildren();
		
		for( Node sourceRootChild : sourceRootChildren ) {
			for( Node targetRootChild : targetRootChildren ) {
				// TODO: Finish this!
			}
		}
		
		
	}
	
}
