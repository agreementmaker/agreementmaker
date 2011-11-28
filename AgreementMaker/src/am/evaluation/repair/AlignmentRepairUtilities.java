package am.evaluation.repair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;

public class AlignmentRepairUtilities {

	private static Logger log;
	
	public AlignmentRepairUtilities(Logger log){
		this.log = log;
	}
	
//	public void getReasoner(OWLOntology ontology){
//		
//	}
	
	public Reasoner getReasoner(OWLOntology ontology){
		Configuration configuration = new Configuration();
		configuration.tableauMonitorType = org.semanticweb.HermiT.Configuration.TableauMonitorType.NONE;
		
		Reasoner reasoner = new Reasoner(configuration, ontology);
		
//		Set<OWLAxiom> s = sourceReasoner.getPendingAxiomAdditions();
//		
//		return sourceReasoner.getUnsatisfiableClasses();
		return reasoner;
	}
	
	public OWLOntology mergeOntology(OWLOntologyManager owlontologymanager){
		//Call the OWL merger to merge the 2 ontologies
		OWLOntologyMerger merger = new OWLOntologyMerger(owlontologymanager);
		OWLOntology mergedOntology = null;

		try {
			mergedOntology = merger.createMergedOntology(owlontologymanager, IRI.create("http://output.owl"));

			//Print out the axioms
			log.debug("Printing axioms in merged ontology....\n");
			for(OWLAxiom axiom : mergedOntology.getAxioms()) {
				log.debug(axiom);
			}
			//Save the merged ontology to OWL file
			owlontologymanager.saveOntology(mergedOntology, new RDFXMLOntologyFormat(), IRI.create("file:/output.owl"));

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
		return mergedOntology;
	}
	
	public Double computeMeasures(String toEvaluate, String reference){
		int count = 0;
		
		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters param = new ReferenceAlignmentParameters();
		
		param.fileName = toEvaluate;
		matcher.setParam(param);
		
		ArrayList<MatchingPair> filePairs = null;
		ArrayList<MatchingPair> refPairs = null;
		
		try {
			filePairs = matcher.parseStandardOAEI();
			param.fileName = reference.toString();
			refPairs = matcher.parseStandardOAEI();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		for (MatchingPair p1 : filePairs) {
			for (MatchingPair p2 : refPairs) {
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					count++;
					break;
				}
			}
		}
		double precision = (double)count/filePairs.size();
		double recall = (float)count/refPairs.size();
		double fmeasure = 2 * precision * recall / (precision + recall);
		System.out.print("Precision: " + precision + " Recall: " + recall + " FMeasure: " + fmeasure);
		System.out.println("");
		
		return precision;
	}
}
