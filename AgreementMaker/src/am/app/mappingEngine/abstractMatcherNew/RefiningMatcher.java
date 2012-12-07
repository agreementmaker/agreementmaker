package am.app.mappingEngine.abstractMatcherNew;

import java.util.ArrayList;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.SimilarityMatrix;

public abstract class RefiningMatcher extends AbstractMatcherNew {
	
	// NEW FIELDS
	
	/**
	 * Some algorithms may need other algorithms as input
	 * TODO: to move into REFINING MATCHER PARAMETERS CLASS
	 * use simMat instead of abMatch to reduce computation work
	 * either way have it depend on the user
	 * TODO: this is also to replace the performSelection boolean and make distinct ways of matching depending on the matcher type
	 */
	protected ArrayList<AbstractMatcher> inputMatchers;
	protected ArrayList<SimilarityMatrix> classesMatrices;
	protected ArrayList<SimilarityMatrix> propertiesMatrices;
	
	// CONSTRUCTOR //

	public RefiningMatcher() {
		super();
		// set what distinguishes a base matcher from a refining one
		matchingParameters.setMinInputMatchers(0); // TODO: get them from panel
		matchingParameters.setMaxInputMatchers(0);
	}
	
	// OTHER METHODS //
	
	@Override
	public abstract AbstractMatcherParametersPanel callParametersPanel();

}
