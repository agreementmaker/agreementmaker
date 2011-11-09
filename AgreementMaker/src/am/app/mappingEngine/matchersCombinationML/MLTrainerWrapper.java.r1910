package am.app.mappingEngine.matchersCombinationML;

import java.io.IOException;
import java.util.ArrayList;

import org.dom4j.DocumentException;

import weka.core.ListOptions;

import am.GlobalStaticVariables;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

public class MLTrainerWrapper {
	
	ArrayList<AbstractMatcher> listOfMatchers=new ArrayList<AbstractMatcher>();
	ArrayList<OntologyTriple> listOfTriples=new ArrayList<OntologyTriple>();
	
	
	void loadMatchers()
	{
		//TODO : look at oaei2011 and look how to get matchers and add to list below 
	//	listOfMatchers.add();
		
		
		
	}
	
	void loadOntologyTriples()
	{
		//TODO: load the list of training ontologies with reference alignments
		OntologyTriple triple=new OntologyTriple();
		String sourceFileName="";//sourcefilename
		String targetFileName="";//targetfilename
		Ontology sourceOntology=openOntology(sourceFileName);
		Ontology targetOntology=openOntology(targetFileName);
		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters param = new ReferenceAlignmentParameters();
		param.fileName="";//set the reference alignment file name
		matcher.setParam(param);
		try {
			Alignment<Mapping> referenceAlignment=matcher.parseStandardOAEI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
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
	
	void callProcess()
	{
		loadMatchers();
		loadOntologyTriples();
		
		for(int t=0;t<listOfTriples.size();t++)
		{
			OntologyTriple currentTriple=listOfTriples.get(t);
			for(int m=0;m<listOfMatchers.size();m++)
			{
				AbstractMatcher currentMatcher=listOfMatchers.get(m);
				
			}
		}
	}

}
