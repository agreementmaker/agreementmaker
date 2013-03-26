package am.app.mappingEngine.threaded;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;

/**
 * Runs an AbstractMatcher in a threaded mode.
 */

public class AbstractMatcherRunner implements Runnable {

	private final AbstractMatcher matcher;
	private final alignType typeOfNodes;
	private final List<Node> sourceList;
	private final List<Node> targetList;

	private final int sourceStartIndex, sourceEndIndex, targetStartIndex, targetEndIndex;
	private final SimilarityMatrix matrix;

	public AbstractMatcherRunner(List<Node> sourceList, List<Node> targetList, 
			int sourceStartIndex, int sourceEndIndex, int targetStartIndex, int targetEndIndex, 
			SimilarityMatrix matrix, AbstractMatcher matcher, alignType typeOfNodes ) {

		//System.out.println("New Matcher Runner, from (" + sourceStartIndex + "-" + sourceEndIndex + ") to (" + targetStartIndex + "-" + targetEndIndex + ")");

		this.matcher = matcher;

		this.typeOfNodes = typeOfNodes;
		this.sourceList = sourceList;
		this.targetList = targetList;

		this.sourceStartIndex = sourceStartIndex;
		this.sourceEndIndex = sourceEndIndex;
		this.targetStartIndex = targetStartIndex;
		this.targetEndIndex = targetEndIndex;

		this.matrix = matrix;
	}

	@Override
	public void run() {
		for( int i = sourceStartIndex; i <= sourceEndIndex; i++ ){
			Node source = sourceList.get(i);
			for( int j = targetStartIndex; j <= targetEndIndex; j++ ) {
				Node target = targetList.get(j);

				try {
					Mapping mapping = matcher.alignTwoNodesParallel(source, target, typeOfNodes, matrix);
					if( mapping != null ) { 
						matcher.saveThreadResult(i, j, mapping, matrix);
					}

					/*if( matcher.isProgressDisplayed() ) {
						 matcher.stepDone();
						 matcher.updateProgress();
						 if( mapping != null && mapping.getSimilarity() >= param.threshold ) { 
							 tentativealignments++; // keep track of possible alignments for progress display
							 //System.out.println(mapping);
						 }
					 }*/

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}  
		}
	}
}