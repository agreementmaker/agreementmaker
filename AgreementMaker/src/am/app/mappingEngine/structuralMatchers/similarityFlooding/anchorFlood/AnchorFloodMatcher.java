/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.anchorFlood;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;

import am.app.mappingEngine.structuralMatchers.similarityFlooding.SimilarityFloodingMatcher;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.SimilarityFloodingMatcherParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertexData;
import am.app.ontology.Node;
import am.utility.Pair;

/**
 * @author michele
 *
 */
public class AnchorFloodMatcher extends SimilarityFloodingMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6736429570974261886L;

	/**
	 * 
	 */
	public AnchorFloodMatcher() {
		super();
		minInputMatchers = 1;
		maxInputMatchers = ANY_INT;
	}

	/**
	 * @param params_new
	 */
	public AnchorFloodMatcher(SimilarityFloodingMatcherParameters params_new) {
		super(params_new);
		minInputMatchers = 1;
		maxInputMatchers = ANY_INT;
	}

	/**
	 *
	 */
	@Override
	protected void loadSimilarityMatrices() {
		classesMatrix = inputMatchers.get(0).getClassesMatrix();
		propertiesMatrix = inputMatchers.get(0).getPropertiesMatrix();
	}

	/**
	 *
	 */
	@Override
	protected PCGVertexData selectInput(Pair<RDFNode, RDFNode> pair) {
		OntResource sourceRes, targetRes;
		double sim = 0.0;
		// try to get the ontResource from them
		if(pair.getLeft().canAs(OntResource.class) && pair.getRight().canAs(OntResource.class)){
			sourceRes = pair.getLeft().as(OntResource.class);
			targetRes = pair.getRight().as(OntResource.class);
			 
			// try to get the Node and check they belong to the same alignType
			Node source, target;
			try{
				source = sourceOntology.getNodefromOntResource(sourceRes, alignType.aligningClasses);
				target = targetOntology.getNodefromOntResource(targetRes, alignType.aligningClasses);
				sim = classesMatrix.get(source.getIndex(), target.getIndex()).getSimilarity();
			}
			catch(Exception eClass){
				try{
					source = sourceOntology.getNodefromOntResource(sourceRes, alignType.aligningProperties);
					target = targetOntology.getNodefromOntResource(targetRes, alignType.aligningProperties);
					sim = propertiesMatrix.get(source.getIndex(), target.getIndex()).getSimilarity();
				}
				catch(Exception eProp){
				}
			}
		}
		return new PCGVertexData( pair, sim, 0.0 );
	}

}
