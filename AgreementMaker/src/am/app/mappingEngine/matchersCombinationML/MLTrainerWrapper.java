package am.app.mappingEngine.matchersCombinationML;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.GlobalStaticVariables;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.conceptMatcher.ConceptMatcherParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

public class MLTrainerWrapper {
	
	ArrayList<AbstractMatcher> listOfMatchers=new ArrayList<AbstractMatcher>();
	ArrayList<OntologyTriple> listOfTriples=new ArrayList<OntologyTriple>();
	
	public static Ontology loadOntology(String ontoName){
		Ontology ontology;
		try {
			ontology = OntoTreeBuilder.loadOWLOntology(ontoName);
//			OntoTreeBuilder treeBuilder = new OntoTreeBuilder(ontoName, GlobalStaticVariables.SOURCENODE,
//					GlobalStaticVariables.LANG_OWL, 
//					GlobalStaticVariables.SYNTAX_RDFXML, false, true);
//			treeBuilder.build();
//			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			System.out.println("Failed To open the ontology!");
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	
	
	void loadMatchers()
	{
		//TODO : look at oaei2011 and look how to get matchers and add to list below 
	//	listOfMatchers.add();
		
		AbstractMatcher bsm=MatcherFactory.getMatcherInstance(MatchersRegistry.ConceptSimilarity, 0);
		listOfMatchers.add(bsm);
	}
	
	void loadOntologyTriples(String filename,String elementname)
	{
		//TODO: load the list of training ontologies with reference alignments
		XmlParser xp=new XmlParser();
		ArrayList<TrainingLayout> tlist=xp.parseDocument(filename, elementname);
		for(TrainingLayout tl: tlist)
		{
			Ontology sourceOntology=loadOntology(tl.getsourceOntologyPath());
			Ontology targetOntology=loadOntology(tl.gettargetOntologyPath());
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = tl.getrefAlignmentPath();
			refParam.format = ReferenceAlignmentMatcher.OAEI;
			AbstractMatcher referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
			referenceAlignmentMatcher.setParam(refParam);
			referenceAlignmentMatcher.setSourceOntology(sourceOntology);
			referenceAlignmentMatcher.setTargetOntology(targetOntology);
       		Alignment<Mapping> refmap=referenceAlignmentMatcher.getAlignment();
			OntologyTriple ot=new OntologyTriple(sourceOntology,targetOntology,refmap);
			ot.setListOfMatchers(listOfMatchers);
			listOfTriples.add(ot);
		}
	}
	
	void generateMappings()
	{

		for(int t=0;t<listOfTriples.size();t++)
		{
			OntologyTriple currentTriple=listOfTriples.get(t);
			ArrayList<AbstractMatcher> matchers=currentTriple.getListOfMatchers();
			for(int m=0;m<matchers.size();m++)
			{
				
				try {
						AbstractMatcher currentMatcher=matchers.get(m);
						currentMatcher.setOntologies(currentTriple.getOntology1(), currentTriple.getOntology2());
						currentMatcher.setParam(new ConceptMatcherParameters());
						currentMatcher.match();
						Alignment<Mapping> resultAlignment=currentMatcher.getAlignment();
						if(resultAlignment!=null)
						{
							currentTriple.setAlignmentObtained(currentMatcher, resultAlignment);	
						}
						else
						{
							//currentTriple.setAlignmentObtained(currentMatcher, null);
						}
					
					
					} catch (Exception e) {
			
						e.printStackTrace();
					}
			}
		}
	}
	
	void generateTrainingFile() throws Exception
	{
		ArrayList<String> mappedSourceTarget=new ArrayList<String>();
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File("bench/training/trainingFile")));
		for(int t=0;t<listOfTriples.size();t++)
		{
			OntologyTriple currentTriple=listOfTriples.get(t);
			Alignment<Mapping> referenceAlignment=currentTriple.getReferenceAlignment();
			ArrayList<AbstractMatcher> matchers=currentTriple.getListOfMatchers();
			if(matchers!=null)
			{
				for(int m=0;m<matchers.size();m++)
				{
					AbstractMatcher currentMatcher=matchers.get(m);
					Alignment<Mapping> currentMapping=currentTriple.getAlignmentObtained(currentMatcher);
					if(currentMapping!=null)
					{
						Ontology sourceOntology=currentTriple.getOntology1();
						Ontology targetOntology=currentTriple.getOntology2();
						List<Node> sourceClasses=sourceOntology.getClassesList();
						List<Node> targetClasses=targetOntology.getClassesList();
						for(int source=0;source<sourceClasses.size();source++)
						{
							Node sourceNode=sourceClasses.get(source);
							for(int target=0;target<targetClasses.size();target++)
							{
								Node targetNode=targetClasses.get(target);
								if(currentMapping.isMapped(sourceNode) && currentMapping.isMapped(targetNode))
								{
									if(!mappedSourceTarget.contains(sourceNode.getUri()+"\t"+targetNode.getUri()))
									{
										double similarityValue=currentMapping.getSimilarity(sourceNode, targetNode);
										double referenceValue=referenceAlignment.getSimilarity(sourceNode, targetNode);
										outputWriter.write(sourceNode.getUri()+"\t"+targetNode.getUri()+"\t"+similarityValue+"\t"+referenceValue+"\n");
										mappedSourceTarget.add(sourceNode.getUri()+"\t"+targetNode.getUri());
									}																
								}								
							}							
						}
					}
				}	
			}			
		}		
		outputWriter.close();
		
	}
	
	void generateModel()
	{
		
	}
	
	void callProcess() throws Exception
	{
		String trainingFileName="bench/training.xml";
		String elementName="trainingset";
		loadMatchers();
		loadOntologyTriples(trainingFileName,elementName);
		generateMappings();
		generateTrainingFile();
		generateModel();
//		String testFileName="";
//		elementName="testset";
//		loadOntologyTriples(testFileName,elementName);
	}
	
	public static void main(String args[])throws Exception
	{
		MLTrainerWrapper ml=new MLTrainerWrapper();
		ml.callProcess();
				
	}

}
