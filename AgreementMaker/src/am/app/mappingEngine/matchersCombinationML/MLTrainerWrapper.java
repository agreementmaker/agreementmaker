package am.app.mappingEngine.matchersCombinationML;

import java.util.ArrayList;

import org.dom4j.DocumentException;

import weka.core.ListOptions;

import am.GlobalStaticVariables;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

public class MLTrainerWrapper {
	
	ArrayList<AbstractMatcher> listOfMatchers=new ArrayList<AbstractMatcher>();
	ArrayList<OntologyTriple> listOfTriples=new ArrayList<OntologyTriple>();
	
	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OntoTreeBuilder treeBuilder = new OntoTreeBuilder(ontoName, GlobalStaticVariables.SOURCENODE,
					GlobalStaticVariables.LANG_OWL, 
					GlobalStaticVariables.SYNTAX_RDFXML, false, true);
			treeBuilder.build();
			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	
	void loadMatchers()
	{
		//TODO : look at oaei2011 and look how to get matchers and add to list below 
	//	listOfMatchers.add();
	}
	
	void loadOntologyTriples(String filename,String elementname)
	{
		//TODO: load the list of training ontologies with reference alignments
		XmlParser xp=new XmlParser();
		ArrayList<TrainingLayout> tlist=xp.parseDocument(filename, elementname);
		for(TrainingLayout tl: tlist)
		{
			Ontology sourceOntology=openOntology(tl.getsourceOntologyPath());
			Ontology targetOntology=openOntology(tl.gettargetOntologyPath());
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
			listOfTriples.add(ot);
		}
	}
	
	void callProcess()
	{
		String filename="bench/training.xml";
		String elementname="trainingset";
		loadMatchers();
		loadOntologyTriples(filename,elementname);
		
		for(int t=0;t<listOfTriples.size();t++)
		{
			OntologyTriple currentTriple=listOfTriples.get(t);
			System.out.println(currentTriple.getOntology1().getFilename());
			System.out.println(currentTriple.getOntology2().getInstances());
			
			//for(int m=0;m<listOfMatchers.size();m++)
			//{
				//AbstractMatcher currentMatcher=listOfMatchers.get(m);
				
			//}
		}
	}
	
	public static void main(String args[])
	{
		MLTrainerWrapper ml=new MLTrainerWrapper();
		ml.callProcess();
	}

}
