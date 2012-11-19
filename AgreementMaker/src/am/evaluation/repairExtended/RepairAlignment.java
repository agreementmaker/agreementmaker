package am.evaluation.repairExtended;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.HermiT.structural.OWLAxioms;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.evaluation.repair.AlignmentRepairUtilities;
import am.utility.referenceAlignment.AlignmentUtilities;

public class RepairAlignment {

	private static Logger log = Logger.getLogger(RepairAlignment.class);
		
	private File referenceFile = new File("/home/pavan/MS/WebSemantics/Ontologies/Anatomy/reference.rdf");
	private File sourceOwl = new File("/home/pavan/MS/WebSemantics/Ontologies/Anatomy/mouse.owl");
	private File targetOwl = new File("/home/pavan/MS/WebSemantics/Ontologies/Anatomy/human.owl");
	private File alignmentFile = new File("/home/pavan/MS/WebSemantics/Ontologies/Anatomy/Alignment.rdf");
	
	private AlignmentRepairUtilities util = new AlignmentRepairUtilities(log);
	
	private List<MatchingPair> alignment;
	//private int axiomCount = 0;
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		
		log.setLevel(Level.INFO);
		new RepairAlignment().repair();
	}
	
	public void repair(){
		
		OWLOntology mergedOntology;
		//List<OWLAxiom> inconsistentAxioms;
		//HashMap<OWLClass,ArrayList<OWLAxiom>> axiomMap;
		ConflictSetList inconsistentSets;
		//Integer axiomCount = 0;
		
		try {
						
			computeMeasures(alignmentFile.toString(), referenceFile.toString());
						
			log.info("Merging ontolgoies...");
			mergedOntology = mergeOntologies();
			log.info("Ontologies merged successfully");
			
			System.out.println(" ");
			
			log.info("Identifying inconsistent classes and respective equivalence axioms...");
			//inconsistentAxioms = getInconsistentAxioms(alignment, mergedOntology);
			inconsistentSets = getInconsistentSets(alignment, mergedOntology);
			log.info(inconsistentSets.getClassCount() + " inconsistent classes identified");
						
			
			log.info(inconsistentSets.getAxiomCount() + " inconsistent axioms identified");
			
			System.out.println(" ");
			
			log.info("Ranking axioms...");
			inconsistentSets.rankAxioms();
			log.info("Axiom ranking complete");
			//ArrayList<OWLAxiom> axiomRank = (ArrayList<OWLAxiom>)axiomRank.rankByAxiomFrequency((ArrayList<OWLAxiom>)inconsistentAxioms);
			
			System.out.println(" ");
			
			log.info("Compute hitting set...");
			inconsistentSets.computeHittingSet();
			log.info("Hitting set identified");
			
		} catch (OWLOntologyCreationException e) {
			log.error("OWL ontology merge failed");
			e.printStackTrace();
		} finally{
			log.info("Process complete");
		}		
	}
	
	public OWLOntology mergeOntologies()
			throws OWLOntologyCreationException{
		
		alignment = AlignmentUtilities.getMatchingPairsOAEI(alignmentFile.getAbsolutePath());
		log.info("Loaded alignment. The alignment contains " + alignment.size() + " mappings.");
		
		return util.loadOntologies(alignment);
	}
	
	/*public List<OWLAxiom> getInconsistentAxioms(List<MatchingPair> alignment,OWLOntology mergedOntology){
		
		Node<OWLClass> unsatisfiedClasses;
		List<OWLAxiom> inconsistentAxioms = new ArrayList<OWLAxiom>();
		
		ReasonerFactory reasonerFactory = new ReasonerFactory();
		Reasoner reasoner = util.loadReasoner(mergedOntology);		
				
		unsatisfiedClasses = reasoner.getUnsatisfiableClasses();
		
		for(OWLClass cls : unsatisfiedClasses){		

		if(! cls.isBottomEntity()){
			Iterator<MatchingPair> mpIter = alignment.iterator();
					
			while(mpIter.hasNext()){
				MatchingPair pair = mpIter.next();
				if(pair.sourceURI.equals(cls.getIRI().toString()) || pair.targetURI.equals(cls.getIRI().toString())){
					mpIter.remove();
					
					log.info("The conflicting class: " + cls);
					inconsistentAxioms.addAll((ArrayList<OWLAxiom>)getConflictingAxioms(mergedOntology, reasonerFactory, reasoner, cls));		
					}
				}
			}
		}
		
		log.info(unsatisfiedClasses.getSize() + " Inconsistent classes identified");
		
		return inconsistentAxioms;		
	}*/
	
	public ConflictSetList getInconsistentSets(List<MatchingPair> alignment,OWLOntology mergedOntology){
		
		Node<OWLClass> unsatisfiedClasses;
		//HashMap<OWLClass,ArrayList<OWLAxiom>> axiomHashMap = new HashMap<OWLClass,ArrayList<OWLAxiom>>();
		ConflictSetList inconsistentSets = new ConflictSetList();
	
		ReasonerFactory reasonerFactory = new ReasonerFactory();
		Reasoner reasoner = util.loadReasoner(mergedOntology);		
				
		unsatisfiedClasses = reasoner.getUnsatisfiableClasses();
		
		for(OWLClass cls : unsatisfiedClasses){		

			if(! cls.isBottomEntity()){
				Iterator<MatchingPair> mpIter = alignment.iterator();
					
				while(mpIter.hasNext()){
					MatchingPair pair = mpIter.next();
					if(pair.sourceURI.equals(cls.getIRI().toString()) || pair.targetURI.equals(cls.getIRI().toString())){
						mpIter.remove();

						log.info("The conflicting class: " + cls);
						//axiomHashMap.put(cls, (ArrayList<OWLAxiom>)getConflictingAxioms(mergedOntology, reasonerFactory, reasoner, cls));
						inconsistentSets.addDistinct(new ConflictSet(cls,getConflictingAxioms(mergedOntology, reasonerFactory, reasoner, cls)));
					
					}
				}
			}
		}		
		//log.info(unsatisfiedClasses.getSize() + " Inconsistent classes identified");
		
		return inconsistentSets;		
	}
	
	private ArrayList<AxiomRank> getConflictingAxioms(OWLOntology mergedOntology,
			ReasonerFactory reasonerFactory, Reasoner reasoner,
			OWLClass unsatClass) {
		//*** Get an explanation for each of the unsatisfiable classes.
		//*** Save all the conflicting EquivalentClass axioms.
		BlackBoxExplanation exp = new BlackBoxExplanation(mergedOntology, reasonerFactory, reasoner);
		Set<OWLAxiom> expSet = exp.getExplanation(unsatClass);
		//List<OWLAxiom> conflictingAxioms = new ArrayList<OWLAxiom>();
		ArrayList<AxiomRank> conflictingAxioms = new ArrayList<AxiomRank>();

		//System.out.println("\n------------------------------ Unsatisfiable Class ------------------------------");
		//log.info("The Unsatisfiable class is: " + unsatClass);

		for(OWLAxiom causingAxiom : expSet) {
//			log.info(causingAxiom.getAxiomType());
			if(causingAxiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
				//Get the Source and Target classes in the axiom signature.
				List<OWLClass> classList = new ArrayList<OWLClass>(causingAxiom.getClassesInSignature());
				String[] sourceURI = classList.get(0).getIRI().toString().split("#");
				String[] targetURI = classList.get(1).getIRI().toString().split("#");

				//Save the conflicting axiom (if its not a mapping between classes in same ontology).
				if(!sourceURI.equals(targetURI))
					conflictingAxioms.add(new AxiomRank(causingAxiom,0));
					//conflictingAxioms.add(causingAxiom);
			}
			else if(causingAxiom.getAxiomType() == AxiomType.EQUIVALENT_DATA_PROPERTIES) {
				//Get the Source and Target classes in the axiom signature.
				List<OWLClass> classList = new ArrayList<OWLClass>(causingAxiom.getClassesInSignature());
				String[] sourceURI = classList.get(0).getIRI().toString().split("#");
				String[] targetURI = classList.get(1).getIRI().toString().split("#");

				//Save the conflicting axiom (if its not a mapping between classes in same ontology).
				if(!sourceURI.equals(targetURI))
					conflictingAxioms.add(new AxiomRank(causingAxiom,0));
					//conflictingAxioms.add(causingAxiom);
			}
			else if(causingAxiom.getAxiomType() == AxiomType.EQUIVALENT_OBJECT_PROPERTIES) {
				//Get the Source and Target classes in the axiom signature.
				List<OWLClass> classList = new ArrayList<OWLClass>(causingAxiom.getClassesInSignature());
				String[] sourceURI = classList.get(0).getIRI().toString().split("#");
				String[] targetURI = classList.get(1).getIRI().toString().split("#");

				//Save the conflicting axiom (if its not a mapping between classes in same ontology).
				if(!sourceURI.equals(targetURI))
					conflictingAxioms.add(new AxiomRank(causingAxiom,0));
					//conflictingAxioms.add(causingAxiom);
			}
		}
				
		for(AxiomRank confAx : conflictingAxioms){
			log.info("The conflicting axiom: " + confAx.getAxiom());
			//axiomCount++;
		}
						
		return conflictingAxioms;
	}	

	public Double computeMeasures(String toEvaluate, String reference){
		
		int count = 0;
		ArrayList<MatchingPair> filePairs = null;
		ArrayList<MatchingPair> refPairs = null;
		
		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters param = new ReferenceAlignmentParameters();
		//matcher.setParam(param);
		matcher.setParameters(param);
		
		try {
			param.fileName = toEvaluate;
			filePairs = matcher.parseStandardOAEI();
			
			param.fileName = reference.toString();
			refPairs = matcher.parseStandardOAEI();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		int totalcount = 0;
		
		for (MatchingPair p1 : filePairs) {
			for (MatchingPair p2 : refPairs) {
				totalcount++;
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
		
		log.info("Initial measures...");
		log.info("Precision: " + precision + " Recall: " + recall + " FMeasure: " + fmeasure);
		System.out.println(" ");

		return precision;
	}
}
