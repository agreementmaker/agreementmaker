/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PairwiseConnectivityGraph;
import am.utility.DirectedGraph;
import am.utility.DirectedGraphEdge;
import am.utility.DirectedGraphVertex;
import am.utility.Pair;

/**
 * Similarity Flooding algorithm implementation.
 * @author Michele Caci
 *
 */
public class SimilarityFloodingMatcher extends AbstractMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3749229483504509029L;
	private static final boolean DEBUG_FLAG = true;
	private boolean hasInput;
	private PairwiseConnectivityGraph pcg;

	/**
	 * 
	 */
	public SimilarityFloodingMatcher() {
		super();
		needsParam = true; // we need to display the parameters panel.
		//setHasInput(false);
	}

	/**
	 * @param params_new
	 */
	public SimilarityFloodingMatcher(SimilarityFloodingMatcherParameters params_new) {
		super(params_new);
	}
	
	@Override 
	public AbstractMatcherParametersPanel getParametersPanel() { return new SimilarityFloodingParametersPanel(); };
	
	/**
	 * Similarity Flooding Algorithm. 
	 * @see am.app.mappingEngine.AbstractMatcher#align(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * @author michele
	 * NOTE: we are using graphs instead of arrayList
	 */
	 protected void align() throws Exception {
		progressDisplay.clearReport();
		 
		// cannot align just one ontology (this is here to catch improper invocations)
		if( sourceOntology == null ) throw new NullPointerException("sourceOntology == null");   
		if( targetOntology == null ) throw new NullPointerException("targetOntology == null");
		
		progressDisplay.appendToReport("Creating Pairwise Connectivity Graph...");
		createPairwiseConnectivityGraph();
		progressDisplay.appendToReport("done.\n");
    	
		/* TODO: make use of alignClass and alignProp in our code
		if(alignClass && !this.isCancelled() ) {
			ArrayList<Node> sourceClassList = sourceOntology.getClassesList();
			ArrayList<Node> targetClassList = targetOntology.getClassesList();
			classesMatrix = alignClasses(sourceClassList,targetClassList );	
			//classesMatrix.show();
		}
		if(alignProp && !this.isCancelled() ) {
			ArrayList<Node> sourcePropList = sourceOntology.getPropertiesList();
			ArrayList<Node> targetPropList = targetOntology.getPropertiesList();
			propertiesMatrix = alignProperties(sourcePropList, targetPropList );					
		}
		*/
	 }
	 
	 
	 /**
	  * This method creates the Pairwise Connectivity Graph.
	  * 
	  * Given the graphs for the source and target ontologies, A and B respectively,
	  * and elements x, y in A and x', y' in B, we construct the PCG in this way:
	  *  
	  *   PCGVertex    PCGVertex
	  *      \/           \/
	  *  ( (x, y) , p, (x', y') ) in PCG(A, B) <==> (x, p, x') in A and (y, p, y') in B
	  *            /\
	  *  	     PCGEdge
	  */
	 protected void createPairwiseConnectivityGraph(){
		 
		 pcg = new PairwiseConnectivityGraph();
		 
		 OntModel localSource = sourceOntology.getModel();
		 OntModel localTarget = targetOntology.getModel();
		 
		 StmtIterator sStmtIterator = localSource.listStatements();
		 StmtIterator tStmtIterator = localTarget.listStatements();
		 
		 Statement sStmt = null;
		 Statement tStmt = null;
		 
		 while(sStmtIterator.hasNext()){
			 sStmt = sStmtIterator.next();
			 
			 while(tStmtIterator.hasNext()){
				 tStmt = tStmtIterator.next();
				 
				 // condition where we add a new element in the pairwise connectivity graph:
				 // comparison of predicates
				 if(sStmt.getPredicate().equals(tStmt.getPredicate())){

					 if( ((SimilarityFloodingMatcherParameters)param).omitAnonymousNodes && 
							 ( sStmt.getSubject().isAnon() || sStmt.getObject().isAnon() ||
							   tStmt.getSubject().isAnon() || tStmt.getObject().isAnon() )  ) {
						// these nodes are anonymous
						// parameter is set to not insert anonymous nodes
						// do nothing
					 } else {
						 
						 insertInPCG(new Pair<RDFNode, RDFNode>(sStmt.getSubject(), tStmt.getSubject()),  // vertex
							 		sStmt.getPredicate(),                                             // edge
							 		new Pair<RDFNode, RDFNode>(sStmt.getObject(), tStmt.getObject())  // vertex
									);
					 }
					 
					
				 }
				 
			 }
			 tStmtIterator = localTarget.listStatements();
		 }
		 
		 System.out.println(pcg.toString());
	 }
	 
	 /* *************************************************** */
	 /* 			   MATCHING STEP OPERATIONS				*/
	 /* *************************************************** */
	 
	 /**
	  * 
	  */
	 private void insertInPCG(Pair<RDFNode, RDFNode> sourcePair,
			 					Property linkingPred,
			 					Pair<RDFNode, RDFNode> targetPair){
		 Logger log = null;
		 
		 if( DEBUG_FLAG ) {
			 log = Logger.getLogger(this.getClass());
			 log.setLevel( Level.DEBUG );
		 }
		 
		 PCGVertex sourceVertex = new PCGVertex(sourcePair);
		 PCGVertex targetVertex = new PCGVertex(targetPair);
		 PCGEdge pairEdge = new PCGEdge(sourceVertex, targetVertex, linkingPred);
		 
		 //if( DEBUG_FLAG ) log.debug("insertInPCG: \n" + sourceVertex + "\n" + targetVertex + "\n");
		 
		 pcg.insertVertex(sourceVertex);
		 pcg.insertVertex(targetVertex);
		 pcg.insertEdge(pairEdge);
	 }

	/* *************************************************** */
	/* 					  SUPPORT FUNCTIONS 			   */
	/* *************************************************** */
	
	/**
	 * this function has to be called as soon as possible because this matcher needs some sort of input
	 */
	private void loadSimilarityMatrices(){
		if(this.hasInput){
			classesMatrix = inputMatchers.get(0).getClassesMatrix();
			propertiesMatrix = inputMatchers.get(0).getPropertiesMatrix();
		}
		else{
			// load classesMatrix
			if(classesMatrix != null){
				classesMatrix.fillMatrix(1.0);
			}
			else{
				classesMatrix = new SimilarityMatrix(getSourceOntology().getClassesList().size(),
						getTargetOntology().getClassesList().size(),
						alignType.aligningClasses,
						1.0);
			}
			// load propertiesMatrix
			if(propertiesMatrix != null){
				propertiesMatrix.fillMatrix(1.0);
			}
			else{
				classesMatrix = new SimilarityMatrix(getSourceOntology().getPropertiesList().size(),
						getTargetOntology().getPropertiesList().size(),
						alignType.aligningClasses,
						1.0);
			}
		}
	}

	/**
	 * @return the hasInput
	 */
	public boolean getHasInput() {
		return hasInput;
	}

	/**
	 * @param hasInput the hasInput value to set
	 */
	public void setHasInput(boolean hasInput) {
		this.hasInput = hasInput;
	}

}
