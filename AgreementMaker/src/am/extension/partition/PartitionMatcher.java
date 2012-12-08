package am.extension.partition;

import java.util.ArrayList;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Ontology;

public class PartitionMatcher extends AbstractMatcher {

	private static final long serialVersionUID = 5563156352028334226L;

	public PartitionMatcher() {
		super();
		
		setName("Partition Matcher");
		setCategory(MatcherCategory.HYBRID);
	}

	@Override
	public void match() throws Exception {


		// setup the Ontologies
		if( sourceOntology == null ) {
			if( Core.getInstance().getSourceOntology() == null ) {
				// no source ontology defined or loaded
				throw new Exception("No source ontology is loaded!");
			} else {
				// the source Ontology is not defined, but a Source ontology is loaded in the Core. Use that.
				sourceOntology = Core.getInstance().getSourceOntology();
			}
		}

		if( targetOntology == null ) {
			if( Core.getInstance().getTargetOntology() == null ) {
				// no target ontology defined or loaded
				throw new Exception("No target ontology is loaded!");
			} else {
				// the target Ontology is not defined as part of this matcher, but a Target ontology is loaded in the Core.  Use that.
				targetOntology = Core.getInstance().getTargetOntology();
			}
		}

		matchStart();
		

		// ***********************************************************************************
		// ************************ PARTITION MATCHING CODE **********************************
		// ***********************************************************************************
		

		Ontology source = getSourceOntology();
		Ontology target = getTargetOntology();
		

		//DisplayOntMappings.openOntology("C:\\Academic\\Ist Semester\\Data and Web Semantics\\BigOntolgies\\oaei2012_FMA_whole_ontology.owl");
		//DisplayOntMappings.openOntology("C:\\Academic\\Ist Semester\\Data and Web Semantics\\BigOntolgies\\oaei2012_NCI_whole_ontology.owl");

		//OntoProcessing.traverseOntology(ontology1);

		//For Testing purpose
		ArrayList<CustomNode> sourceList = OntoProcessing.testOntoScalability(source.getFilename());

		OntoProcessing testObj = new OntoProcessing();
		ArrayList<ArrayList<CustomNode>> sourceBlocks = testObj.createBlocks(sourceList);


		ArrayList<CustomNode> targetList = OntoProcessing.testOntoScalability(target.getFilename());

		OntoProcessing testObj2 = new OntoProcessing();
		ArrayList<ArrayList<CustomNode>> targetBlocks = testObj.createBlocks(targetList);
		SimilarityComputer s = new SimilarityComputer();
		s.setSourceOntology(source);
		s.setTargetOntology(target);
		s.buildSimilarityMatrices();
		SimilarityMatrix m = s.getClassesMatrix();
		SimilarityMatrix finalMatrix = m;
		for(int i=0;i<m.getRows();i++)
			for(int j=0;j<m.getColumns();j++)
				finalMatrix.get(i,j).setSimilarity(0.0);
		OntoProcessing processingObj = new  OntoProcessing();
		double proximity;
		ArrayList<ArrayList<ArrayList<CustomNode>>> finalList = new  ArrayList<ArrayList<ArrayList<CustomNode>>>();
		ArrayList<ArrayList<CustomNode>> finalTargetList = new  ArrayList<ArrayList<CustomNode>>();
		for(int i=0;i<sourceBlocks.size();i++)
			for(int j=0;j<targetBlocks.size();j++)
			{	
				proximity = OntoProcessing.caluculateProiximity(sourceBlocks.get(i),targetBlocks.get(j),sourceBlocks,targetBlocks,m);
				System.out.println(processingObj.caluculateAnchors(sourceBlocks.get(i),targetBlocks.get(j),m)+" Proximity "+proximity);
				if(proximity > OntoProcessing.proximityThreshold)
				{
					SimilarityMatrix m1 = OntoProcessing.alignBlocks(sourceBlocks.get(i),targetBlocks.get(j));
					OntoProcessing.mergeSimilarity(finalMatrix,m1);
				}

			}


		Alignment<Mapping> al = s.scanMatrix(finalMatrix);
		s.matchStart();
		s.setClassesAlignmentSet(al);
		s.matchEnd();


		
		// set the classes matrix.
		classesMatrix = finalMatrix;
		
		// set the classes alignment
		classesAlignmentSet = al;

		// set the empty properties matrix.
		propertiesMatrix = new SparseMatrix(sourceOntology, targetOntology, alignType.aligningProperties);
		
		// set the empty properties alignment set
		propertiesAlignmentSet = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());

		matchEnd();

	}


}
