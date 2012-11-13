package am.evaluation.repairExtended;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntology;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.evaluation.repair.AlignmentRepairUtilities;
import am.utility.referenceAlignment.AlignmentUtilities;

public class RepairAlignment {

	private static Logger log = Logger.getLogger(RepairAlignment.class);
		
	private File referenceFile = new File("/home/pavan/MS/WebSemantics/Project1/AgreementMaker/ontologies/Anatomy/reference.rdf");
	private File sourceOwl = new File("/home/pavan/MS/WebSemantics/Project1/AgreementMaker/ontologies/Anatomy/human.owl");
	private File targetOwl = new File("/home/pavan/MS/WebSemantics/Project1/AgreementMaker/ontologies/Anatomy/mouse.owl");
	private File alignmentFile = new File("/home/pavan/MS/WebSemantics/Project1/AgreementMaker/ontologies/Anatomy/Alignment.rdf");
	
	private AlignmentRepairUtilities util = new AlignmentRepairUtilities(log);
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		
		log.setLevel(Level.INFO);
		new RepairAlignment().repair();
	}
	
	public void repair(){
		
		OWLOntology mergedOntology;
		
		try {
			mergedOntology = mergeOntologies();
			
			getInconsistentAxioms(mergedOntology);
			
			
		} catch (OWLOntologyCreationException e) {
			log.info("OWL ontology merge failed");
			e.printStackTrace();
		} finally{
			log.info("Repair Completed");
		}
		
	}
	
	
	
	public OWLOntology mergeOntologies()
			throws OWLOntologyCreationException{
		
		log.info("Loading alignment...");
		
		List<MatchingPair> alignment = AlignmentUtilities.getMatchingPairsOAEI(alignmentFile.getAbsolutePath());
		log.info("Loaded alignment. The alignment contains " + alignment.size() + " mappings.");
		
		log.info("Initial measures are:");
		computeMeasures(alignmentFile.toString(), referenceFile.toString());
		System.out.println(" ");
		
		return util.loadOntologies(alignment);
	}
	
	public void getInconsistentAxioms(OWLOntology mergedOntology){
		
		
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
		log.info("Precision: " + precision + " Recall: " + recall + " FMeasure: " + fmeasure);
		System.out.println(" ");

		return precision;
	}
}
