package am.evaluation.repair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.batchMode.simpleBatchMode.SimpleBatchModeRunner;
import am.output.similaritymatrix.SimilarityMatrixOutput;
import am.utility.referenceAlignment.AlignmentUtilities;
import am.utility.referenceAlignment.MappingsOutput;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;

/**
 * @author Renu Srinivasan 
 * @author Ramya Kannan
 *
 *	This class checks for inconsistency in the alignment between a pair of source and target ontologies,
 *	and repairs it (by removal), if any.
 */
public class HermitReasonerTest {

	private static Logger log = Logger.getLogger(HermitReasonerTest.class);
	
	private File referenceFile = new File("../Ontologies/OAEI/2011/anatomy/reference_2011.rdf");
	private File sourceOwl = new File("../Ontologies/OAEI/2011/anatomy/mouse.owl");
	private File targetOwl = new File("../Ontologies/OAEI/2011/anatomy/human.owl");
	//Load the Alignment file mapping the 2 ontologies.
	File alignmentFile = new File("../Ontologies/OAEI/2011/anatomy/alignments/am_oaei_2011.rdf");
	File repairedAlignmentFile = new File("../Ontologies/OAEI/2011/test/repairedalignment.rdf");
	
	AlignmentRepairUtilities util = new AlignmentRepairUtilities(log);
	HashMap<OWLClass,Set<OWLAxiom>> conflictingAxiomsMap = null;
	
	SimilarityMatrix matrix = null;
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		DOMConfigurator.configure("log4j.xml");
		log.setLevel(Level.DEBUG);

		new HermitReasonerTest().repairAlignment();
	}
	
	public List<Double> populateMap(List<MatchingPair> unsatAlignments,
			HashMap<Double, MatchingPair> axiomsMap){
		
		for(MatchingPair mp : unsatAlignments){
			axiomsMap.put(mp.similarity, mp);
		}
		
		//Get sorted list of the similarities
		List<Double> similarityList = new ArrayList<Double>(axiomsMap.keySet());
		Collections.sort(similarityList, new Comparator<Double>() {

			@Override
			public int compare(Double d, Double d1)
			{
				if(d > d1)
					return -1;
				if(d < d1)
				{
					return 1;
				} else
				{
					long l = Double.doubleToLongBits(d);
					long l1 = Double.doubleToLongBits(d1);
					return l != l1 ? l >= l1 ? -1 : 1 : 0;
				}
			}
		});
		
		return similarityList;
	}
	
	public List<MatchingPair> repairAdditions(List<MatchingPair> unsatAlignments, List<MatchingPair> alignment, 
			OWLOntology mergedOntology){

		Reasoner reasoner = null;
		System.out.println("Size of the unsat alignments (After removal): " + unsatAlignments.size());
		
		MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
		util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());

		//List storing the inconsistent mappings
		List<MatchingPair> inconsistentPair = new ArrayList<MatchingPair>();
		int iteration = 0;
		
		for(MatchingPair unsatPair : unsatAlignments){
		
			System.out.println("Iteration " + iteration);
			
			alignment.add(unsatPair);
			System.out.println("Adding back axiom: " + unsatPair.toString());

			
			//Merge the ontology with present alignment
			try {
				mergedOntology = loadOntologies(alignment, util);

			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Run reasoner and get unsatisfiable classes
			reasoner = util.loadReasoner(mergedOntology);
			Node<OWLClass> unsatClassesFromRepaired = reasoner.getUnsatisfiableClasses();

			//If this mapping produces unsatisfiable classes, remove it from alignment (undoing the addition step).
			if(unsatClassesFromRepaired.getSize() > 1){
				alignment.remove(unsatPair);
				inconsistentPair.add(unsatPair);

				System.out.println("Removing axiom: " + unsatPair.toString());
			}
			//Save the alignment to file and calculate measures. //just to keep track of each iteration
			MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
			util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());
			iteration++;
		}

		System.out.println("Finished Repair. Saving alignment..");
		MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
		util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());

		System.out.println("Number of inconsistent pairs: " + inconsistentPair.size());
		
		return inconsistentPair;
	}
	
	public List<MatchingPair> getUnsatisfiableAlignments(List<MatchingPair> alignment, OWLOntology mergedOntology){
		
		ReasonerFactory reasonerFactory = new ReasonerFactory();
		Node<OWLClass> unsatClasses;
		List<MatchingPair> unsatAlignments = new ArrayList<MatchingPair>();
		
		Reasoner reasoner = util.loadReasoner(mergedOntology);
		
		unsatClasses = reasoner.getUnsatisfiableClasses();
		log.debug(unsatClasses.getSize());
		
		//Repair the alignment (save and remove the unsatisfiable matching pairs).
		for(OWLClass unsatClass: unsatClasses){
			if(! unsatClass.isBottomEntity()){
				Iterator<MatchingPair> mpIter = alignment.iterator();
				
				while(mpIter.hasNext()){
					MatchingPair pair = mpIter.next();
					if(pair.sourceURI.equals(unsatClass.getIRI().toString()) || pair.targetURI.equals(unsatClass.getIRI().toString())){
						unsatAlignments.add(pair);
						mpIter.remove();

						//*** Get an explanation for each of the unsatisfiable classes.
						//*** Save all the conflicting EquivalentClass axioms.
						BlackBoxExplanation exp = new BlackBoxExplanation(mergedOntology, reasonerFactory, reasoner);
						Set<OWLAxiom> expSet = exp.getExplanation(unsatClass);
						Set<OWLAxiom> conflictingAxioms = new HashSet<OWLAxiom>();

						System.out.println("Unsatisfiable Class: " + unsatClass);
						log.debug("Unsat class: " + unsatClass);

						for(OWLAxiom causingAxiom : expSet) {
							log.debug(causingAxiom.getAxiomType());
							if(causingAxiom.getAxiomType() == AxiomType.EQUIVALENT_CLASSES) {
								//Get the Source and Target classes in the axiom signature.
								List<OWLClass> classList = new ArrayList<OWLClass>(causingAxiom.getClassesInSignature());
								String[] sourceURI = classList.get(0).getIRI().toString().split("#");
								String[] targetURI = classList.get(1).getIRI().toString().split("#");

								//Save the conflicting axiom (if its not a mapping between classes in same ontology).
								if(!sourceURI.equals(targetURI))
									conflictingAxioms.add(causingAxiom);
							}
							else if(causingAxiom.getAxiomType() == AxiomType.EQUIVALENT_DATA_PROPERTIES) {
								//Get the Source and Target classes in the axiom signature.
								List<OWLClass> classList = new ArrayList<OWLClass>(causingAxiom.getClassesInSignature());
								String[] sourceURI = classList.get(0).getIRI().toString().split("#");
								String[] targetURI = classList.get(1).getIRI().toString().split("#");

								//Save the conflicting axiom (if its not a mapping between classes in same ontology).
								if(!sourceURI.equals(targetURI))
									conflictingAxioms.add(causingAxiom);
							}
							else if(causingAxiom.getAxiomType() == AxiomType.EQUIVALENT_OBJECT_PROPERTIES) {
								//Get the Source and Target classes in the axiom signature.
								List<OWLClass> classList = new ArrayList<OWLClass>(causingAxiom.getClassesInSignature());
								String[] sourceURI = classList.get(0).getIRI().toString().split("#");
								String[] targetURI = classList.get(1).getIRI().toString().split("#");

								//Save the conflicting axiom (if its not a mapping between classes in same ontology).
								if(!sourceURI.equals(targetURI))
									conflictingAxioms.add(causingAxiom);
							}
						}
						for(OWLAxiom confAx : conflictingAxioms)
							System.out.println("Conflicting axiom: " + confAx);

						//Save to the class-axioms map
						conflictingAxiomsMap = new HashMap<OWLClass,Set<OWLAxiom>>();
						conflictingAxiomsMap.put(unsatClass, conflictingAxioms);
						System.out.println("--------------------------------------------------------");
					}
				}
			}
		}
		System.out.println("Size of unsat alignments (Before removal): " + unsatAlignments.size());
		
		//*** Put back each of the mapping and check if it causes any unsatisfiability
		
		for(Set<OWLAxiom> conflictingAxioms : conflictingAxiomsMap.values()) {
			System.out.println("------------- New conflicting set: ------------- ");
			
			for(OWLAxiom conflictingAxiom : conflictingAxioms) {
				Iterator<MatchingPair> mpIter = alignment.iterator();
				System.out.println("Searching for mappings of " + conflictingAxiom);
				
				while(mpIter.hasNext()){
					MatchingPair mpair = mpIter.next();
					//Get the Source and Target classes in the axiom signature.
					List<OWLClass> classList = new ArrayList<OWLClass>(conflictingAxiom.getClassesInSignature());
					OWLClass source = classList.get(0);
					OWLClass target = classList.get(1);
					
					//Get the corresponding mapping from alignment and remove it.
					if((mpair.sourceURI.equalsIgnoreCase(source.getIRI().toString()) &&
							mpair.targetURI.equalsIgnoreCase(target.getIRI().toString())) ||
							(mpair.sourceURI.equalsIgnoreCase(target.getIRI().toString()) &&
							mpair.targetURI.equalsIgnoreCase(source.getIRI().toString()))){
						
						System.out.println("Removing conflicting mappings.. " + mpair.toString());
						unsatAlignments.add(mpair);
						mpIter.remove();
					}
				}
			}
		}
		return unsatAlignments;
	}
	
	public void loadMatrix(){
		//get the similarity matrix
		SimpleBatchModeRunner bm = new SimpleBatchModeRunner((File)null);
		AbstractMatcher oaei2011 = bm.instantiateMatcher(null);
		
		log.debug("Loading source ontology...");
		Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(sourceOwl.toString());
		
		log.debug("Loading target ontology...");
		Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(targetOwl.toString());

		oaei2011.setSourceOntology(sourceOntology);
		oaei2011.setTargetOntology(targetOntology);
		
		SimilarityMatrixOutput matrixoutput = new SimilarityMatrixOutput(oaei2011);
		matrix = matrixoutput.loadClassesMatrix("../Ontologies/OAEI/2011/test/oaei2011-classmatrix.mtx");
		log.debug("Loaded similarity matrix...");
	}
	
	
	public void repairAlignment(){
		
		List<MatchingPair> alignment = AlignmentUtilities.getMatchingPairsOAEI(alignmentFile.getAbsolutePath());
		log.debug("Loaded alignment. The alignment contains " + alignment.size() + " mappings.");

		System.out.println("Initial measures are:");
		util.computeMeasures(alignmentFile.toString(), referenceFile.toString());

		List<MatchingPair> unsatAlignments = new ArrayList<MatchingPair>();
		OWLOntology mergedOntology = null;
		
		try{
			loadMatrix();
			
			//Load the 2 ontologies and get back the merged + translated ontology.
			mergedOntology = loadOntologies(alignment, util);
			unsatAlignments = getUnsatisfiableAlignments(alignment, mergedOntology);
			
			List<MatchingPair> inconsistentPairs = repairAdditions(unsatAlignments, alignment, mergedOntology);
			
			int iteration = 1;
			List<MatchingPair> newUnsatAlignments = new ArrayList<MatchingPair>();
			
			while(inconsistentPairs.size() > 0){
				
				System.out.println("********** " + iteration + " **********");
				HashMap<String, Mapping> candidateMap = getCandidateMappings(inconsistentPairs, alignment);
				
				for(Mapping candidate : candidateMap.values()){
					if(candidate != null)
						newUnsatAlignments.add(new MatchingPair(candidate.getEntity1().getUri(), candidate.getEntity2().getUri(), 
								candidate.getSimilarity(), candidate.getRelation()));
				}
				
				alignment.addAll(newUnsatAlignments);
				mergedOntology = loadOntologies(alignment, util);
				unsatAlignments = getUnsatisfiableAlignments(alignment, mergedOntology);
				inconsistentPairs = repairAdditions(unsatAlignments, alignment, mergedOntology);
				
				iteration++;
			}
		}
		catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Mapping> getCandidateMappings(List<MatchingPair> inconsistentPairs, List<MatchingPair> alignment) throws Exception{

		HashMap<String, Mapping> candidateMap = new HashMap<String, Mapping>();
		
		PriorityQueue<Mapping> rowQueue = new PriorityQueue<Mapping>(4000, new Comparator<Mapping>() {

			@Override
			public int compare(Mapping o1, Mapping o2) {
				double v = o1.getSimilarity() - o2.getSimilarity();
				if( v < 0.0d ) return -1;
				if( v > 0.0d ) return 1;
				return 0;
			}
		});
		
		//Sets of all Source and Target classes present in the (repaired) alignment.
		Set<String> alignmentSources = new HashSet<String>();
		Set<String> alignmentTargets = new HashSet<String>();
		
		for(MatchingPair pair : alignment){
			alignmentSources.add(pair.sourceURI);
			alignmentTargets.add(pair.targetURI);
		}
		
		//get class names from inconsistentPairs
		List<String> inconsistentSources = new ArrayList<String>();
		List<String> inconsistentTargets = new ArrayList<String>();
		for(MatchingPair inconsistentPair : inconsistentPairs){
			inconsistentSources.add(inconsistentPair.sourceURI);
			inconsistentTargets.add(inconsistentPair.targetURI);
		}
		
		List<am.app.ontology.Node> matrixSources = matrix.getSourceOntology().getClassesList();
		HashMap<String, Integer> matrixSourceNames = new HashMap<String, Integer>();
		int index = 0;
		//Save all class names + row index from matrix
		for(am.app.ontology.Node source : matrixSources){
			matrixSourceNames.put(source.getUri(), index);
			index++;
		}
		int rowindex = -1;
		for(String name : inconsistentSources){
			if(matrixSourceNames.containsKey(name)){
				rowindex = matrixSourceNames.get(name);
			}
			rowQueue = new PriorityQueue<Mapping>(4000, new Comparator<Mapping>() {

				@Override
				public int compare(Mapping o1, Mapping o2) {
					double v = o1.getSimilarity() - o2.getSimilarity();
					if( v < 0.0d ) return 1;
					if( v > 0.0d ) return -1;
					return 0;
				}
			});
			//put corresponding mapping into map
			
			for(int i = 0; i < matrix.getColumns(); i++)
				if(rowindex != -1 && matrix.get(rowindex, i) != null){
					rowQueue.add(matrix.get(rowindex, i));
				}
			Mapping candidate = rowQueue.poll();
			if(candidate == null)
				continue;
			while(alignmentTargets.contains(candidate.getEntity2().getUri()))
				candidate = rowQueue.poll();
			candidateMap.put(name, candidate);
		}
		
		
		
		List<am.app.ontology.Node> matrixTargets = matrix.getTargetOntology().getClassesList();
		HashMap<String, Integer> matrixTargetNames = new HashMap<String, Integer>();
		index = 0;
		//Save all class names + row index from matrix
		for(am.app.ontology.Node target : matrixTargets){
			matrixTargetNames.put(target.getUri(), index);
			index++;
		}
		int colindex = -1;
		for(String name : inconsistentTargets){
			if(matrixTargetNames.containsKey(name)){
				colindex = matrixTargetNames.get(name);
			}
			rowQueue = new PriorityQueue<Mapping>(4000, new Comparator<Mapping>() {

				@Override
				public int compare(Mapping o1, Mapping o2) {
					double v = o1.getSimilarity() - o2.getSimilarity();
					if( v < 0.0d ) return 1;
					if( v > 0.0d ) return -1;
					return 0;
				}
			});
			//put corresponding mapping into map
			for(int i = 0; i < matrix.getRows(); i++)
				if(colindex != -1 && matrix.get(i, colindex) != null){
					
					rowQueue.add(matrix.get(i, colindex));
				}
			Mapping candidate = rowQueue.poll();
			if(candidate == null)
				continue;
			while(alignmentSources.contains(candidate.getEntity1().getUri()))
				candidate = rowQueue.poll();
			candidateMap.put(name, candidate);
		}
		return candidateMap;
	}
	
	
	public OWLOntology loadOntologies(List<MatchingPair> alignment, AlignmentRepairUtilities util)
			throws OWLOntologyCreationException{
		
		OWLOntologyManager owlontologymanager = OWLManager.createOWLOntologyManager();
		
		// Load source and target ontologies.
		OWLOntology sourceOntology = owlontologymanager.loadOntologyFromOntologyDocument(sourceOwl);
		OWLOntology targetOntology = owlontologymanager.loadOntologyFromOntologyDocument(targetOwl);
		
		//Run reasoner and check if all classes are satisfiable.
		Node<OWLClass> sourceUnsatClass = util.loadReasoner(sourceOntology).getUnsatisfiableClasses();
		Node<OWLClass> targetUnsatClass = util.loadReasoner(targetOntology).getUnsatisfiableClasses();

		log.debug("What are the unsatisfiable classes in Human.owl? " + sourceUnsatClass + "\n");
		log.debug("What are the unsatisfiable classes in Mouse.owl? " + targetUnsatClass + "\n");

		//Merge the source and target ontologies
		OWLOntology mergedOntology = util.mergeOntology(owlontologymanager);
		
		return translateOntology(owlontologymanager, alignment, sourceOntology, targetOntology, 
				mergedOntology, util);
	}
	
	
	public OWLOntology translateOntology(OWLOntologyManager owlontologymanager, List<MatchingPair> alignment,
			OWLOntology sourceOntology, OWLOntology targetOntology, OWLOntology mergedOntology,
			AlignmentRepairUtilities util){
		
		//Translate all the mappings (from alignment file) into OWL Axioms (in merged ontology).
		OWLDataFactory dataFactory = owlontologymanager.getOWLDataFactory();
		
		for( MatchingPair currentMatchingPair : alignment ) {

			IRI sourceConceptIRI = IRI.create(currentMatchingPair.sourceURI);
			IRI targetConceptIRI = IRI.create(currentMatchingPair.targetURI);

			Set<OWLEntity> sourceEntities = sourceOntology.getEntitiesInSignature(sourceConceptIRI);
			Set<OWLEntity> targetEntities = targetOntology.getEntitiesInSignature(targetConceptIRI);

			//First we need to check if we are working with classes or properties
			if( currentMatchingPair.relation == MappingRelation.EQUIVALENCE ) {

				if( sourceEntities.size() == 1 && targetEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLClass ) {
					//We are working with classes, so we create EquivalentClass axioms
					OWLClass sourceClass = (OWLClass) sourceEntities.toArray()[0];
					OWLClass targetClass = (OWLClass) targetEntities.toArray()[0];

					OWLEquivalentClassesAxiom equivClasses = dataFactory.getOWLEquivalentClassesAxiom(sourceClass, targetClass);
					
					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivClasses);
					owlontologymanager.applyChanges(axiomList);
				}
				else if( sourceEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLDataProperty ) {
					//We are working with properties, so we create EquivalentDataProperty axioms
					OWLDataProperty sourceProperty = (OWLDataProperty) sourceEntities.toArray()[0];
					OWLDataProperty targetProperty = (OWLDataProperty) targetEntities.toArray()[0];

					OWLEquivalentDataPropertiesAxiom equivProperties = dataFactory.getOWLEquivalentDataPropertiesAxiom(sourceProperty, targetProperty);
					
					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivProperties);
					owlontologymanager.applyChanges(axiomList);
				}
				else if( sourceEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLObjectProperty ) {
					//We are working with properties, so we create EquivalentObjectProperty axioms
					OWLObjectProperty sourceProperty = (OWLObjectProperty) sourceEntities.toArray()[0];
					OWLObjectProperty targetProperty = (OWLObjectProperty) targetEntities.toArray()[0];

					OWLEquivalentObjectPropertiesAxiom equivProperties = dataFactory.getOWLEquivalentObjectPropertiesAxiom(sourceProperty, targetProperty);
					
					List<OWLOntologyChange> axiomList = owlontologymanager.addAxiom(mergedOntology, equivProperties);
					owlontologymanager.applyChanges(axiomList);
				}
			}
			else {
				log.debug("Write Code!!");
			}
		}
		try {
			owlontologymanager.saveOntology(mergedOntology, new RDFXMLOntologyFormat(), 
					IRI.create("file:/output.owl"));
			
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
		
		//Derive subclass equivalences
		/*for( MatchingPair currentMatchingPair : alignment ) {
			IRI sourceConceptIRI = IRI.create(currentMatchingPair.sourceURI);
			IRI targetConceptIRI = IRI.create(currentMatchingPair.targetURI);

			Set<OWLEntity> sourceEntities = sourceOntology.getEntitiesInSignature(sourceConceptIRI);
			Set<OWLEntity> targetEntities = targetOntology.getEntitiesInSignature(targetConceptIRI);
			
			if(sourceEntities.size() == 1 && targetEntities.size() == 1 && sourceEntities.toArray()[0] instanceof OWLClass ) {
				mergedOntology.getObjectSubPropertyAxiomsForSuperProperty((OWLObjectPropertyExpression) sourceEntities.toArray()[0]);
			}
			
		}*/
		
		return mergedOntology;
	}
}
