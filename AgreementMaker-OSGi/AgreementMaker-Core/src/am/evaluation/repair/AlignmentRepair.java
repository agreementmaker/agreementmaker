package am.evaluation.repair;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.Node;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.utility.MatchingPair;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
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
public class AlignmentRepair {

	private static Logger log = Logger.getLogger(AlignmentRepair.class);
	
	//Load the ontologies, reference and alignment file (using oaei 2011 matcher on agreement maker).
	private File sourceOwl = new File("../Ontologies/OAEI/2011/anatomy/mouse.owl");
	private File targetOwl = new File("../Ontologies/OAEI/2011/anatomy/human.owl");
	private File referenceFile = new File("../Ontologies/OAEI/2011/anatomy/reference_2011.rdf");
	private File alignmentFile = new File("../Ontologies/OAEI/2011/anatomy/alignments/am_oaei_2011.rdf");
	private File repairedAlignmentFile = new File("../Ontologies/OAEI/2011/test/repairedalignment.rdf");
	
	private AlignmentRepairUtilities util = new AlignmentRepairUtilities(log);
	private HashMap<OWLClass,Set<OWLAxiom>> conflictingAxiomsMap = null;
	private SimilarityMatrix matrix = null;
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		
		log.setLevel(Level.INFO);
		new AlignmentRepair().repairAlignment();
	}
	
	/**
	 * 
	 * @param unsatAlignments
	 * @param alignment
	 * @param mergedOntology
	 * @return
	 */
	public List<MatchingPair> repairAdditions(List<MatchingPair> unsatAlignments, List<MatchingPair> alignment, 
			OWLOntology mergedOntology){

		Reasoner reasoner = null;
		System.out.println(" ");
		//(After removal)
		log.info("Number of unsatisfiable mappings: " + (unsatAlignments.size()) + "\n");
		
		MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
		util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());

		//List storing the inconsistent mappings
		List<MatchingPair> inconsistentPair = new ArrayList<MatchingPair>();
		int iteration = 0;
		
		for(MatchingPair unsatPair : unsatAlignments){
			if(unsatPair == null) {
				System.out.println("************************ This should not happen! ********************************");
				continue;
			}
			
			System.out.println("\n------------------------------ Repair iteration " + (iteration+1) + " ------------------------------");
//			log.info("Iteration " + iteration);
			log.info("Adding axiom to alignment: " + unsatPair);
			
			alignment.add(unsatPair);
			
			//Merge the ontology with present alignment
			try {
				mergedOntology = util.loadOntologies(alignment);

			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
			//Run reasoner and get unsatisfiable classes
			reasoner = util.loadReasoner(mergedOntology);
			
			Node<OWLClass> unsatClassesFromRepaired = reasoner.getUnsatisfiableClasses();

			ReasonerFactory reasonerFactory = new ReasonerFactory();
			
			//If this mapping produces unsatisfiable classes, remove it from alignment (undoing the addition step).
			if(unsatClassesFromRepaired.getSize() > 1){
				alignment.remove(unsatPair);
				inconsistentPair.add(unsatPair);

				/*for( OWLClass unsatClass : unsatClassesFromRepaired) {
					getConflictingAxioms(mergedOntology, reasonerFactory, reasoner, unsatClass);
				}*/
				log.info("Removing axiom from alignment: " + unsatPair.toString());
			}
			//Save the alignment to file and calculate measures.
			MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
			util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());
			iteration++;
		}
		System.out.println("------------------------------------------------------------\n");
		
		log.info("Finished Repair. Saving alignment...");
		MappingsOutput.writeMappingsOnDisk(repairedAlignmentFile.toString(), alignment);
		log.info("Final measures: ");
		util.computeMeasures(repairedAlignmentFile.toString(), referenceFile.toString());

		System.out.println(" ");
		log.info("Number of unsatisfiable mappings: " + inconsistentPair.size());
		
		return inconsistentPair;
	}
	
	public List<MatchingPair> getUnsatisfiableAlignments(List<MatchingPair> alignment, OWLOntology mergedOntology){
		
		ReasonerFactory reasonerFactory = new ReasonerFactory();
		Node<OWLClass> unsatClasses;
		List<MatchingPair> unsatAlignments = new ArrayList<MatchingPair>();
		
		//Load the reasoner and find the unsatisfiable classes
		Reasoner reasoner = util.loadReasoner(mergedOntology);
		unsatClasses = reasoner.getUnsatisfiableClasses();
		log.info("Initial number of unsatisfiable classes: " + unsatClasses.getSize());
		
		conflictingAxiomsMap = new HashMap<OWLClass,Set<OWLAxiom>>();
		//Repair the alignment 1 - using explanations (save and remove the unsatisfiable matching pairs).
		for(OWLClass unsatClass: unsatClasses){
			if(! unsatClass.isBottomEntity()){
				Iterator<MatchingPair> mpIter = alignment.iterator();
				
				while(mpIter.hasNext()){
					MatchingPair pair = mpIter.next();
					if(pair.sourceURI.equals(unsatClass.getIRI().toString()) || pair.targetURI.equals(unsatClass.getIRI().toString())){
						unsatAlignments.add(pair);
						mpIter.remove();

						Set<OWLAxiom> conflictingAxioms = getConflictingAxioms(mergedOntology, reasonerFactory, reasoner, unsatClass);

						//Save to the class-axioms map
						conflictingAxiomsMap.put(unsatClass, conflictingAxioms);
						
					}
				}
			}
		}
		System.out.println(" ");
		// (Before removal)
		log.info("Number of unsatisfiable mappings: " + unsatAlignments.size());
		log.info("Number of conflicting axioms: " + conflictingAxiomsMap.size() +"\n");
		
		//*** Remove the axioms from alignment
		for(Set<OWLAxiom> conflictingAxioms : conflictingAxiomsMap.values()) {
			log.info("------------------------------ Conflicting Set ------------------------------");
			
			for(OWLAxiom conflictingAxiom : conflictingAxioms) {
				Iterator<MatchingPair> mpIter = alignment.iterator();
//				log.info("Searching for mappings of: " + conflictingAxiom);
				
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
						
						log.info("Removing conflicting mapping from alignment: " + mpair.toString());
						unsatAlignments.add(mpair);
						mpIter.remove();
					}
				}
			}
		}
		return unsatAlignments;
	}
	
	private Set<OWLAxiom> getConflictingAxioms(OWLOntology mergedOntology,
			ReasonerFactory reasonerFactory, Reasoner reasoner,
			OWLClass unsatClass) {
		//*** Get an explanation for each of the unsatisfiable classes.
		//*** Save all the conflicting EquivalentClass axioms.
		BlackBoxExplanation exp = new BlackBoxExplanation(mergedOntology, reasonerFactory, reasoner);
		Set<OWLAxiom> expSet = exp.getExplanation(unsatClass);
		Set<OWLAxiom> conflictingAxioms = new HashSet<OWLAxiom>();

		System.out.println("\n------------------------------ Unsatisfiable Class ------------------------------");
		log.info("The Unsatisfiable class is: " + unsatClass);

		for(OWLAxiom causingAxiom : expSet) {
//			log.info(causingAxiom.getAxiomType());
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
			log.info("The conflicting axiom: " + confAx);
				
		return conflictingAxioms;
	}

	public void loadMatrix(){
		//Load the similarity matrix from file
		
		log.info("Loading source ontology...");
		Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(sourceOwl.toString());
		log.info("Loading target ontology...");
		Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(targetOwl.toString());

		SimilarityMatrixOutput matrixoutput = new SimilarityMatrixOutput(sourceOntology, targetOntology);
		matrix = matrixoutput.loadClassesMatrix("../Ontologies/OAEI/2011/test/oaei2011-classmatrix.mtx");
		
		log.info("Loaded similarity matrix...");
	}
		
	public void repairAlignment(){
		
		List<MatchingPair> alignment = AlignmentUtilities.getMatchingPairsOAEI(alignmentFile.getAbsolutePath());
		log.info("Loaded alignment. The alignment contains " + alignment.size() + " mappings.");

		log.info("Initial measures are:");
		util.computeMeasures(alignmentFile.toString(), referenceFile.toString());
		System.out.println(" ");
		
		List<MatchingPair> unsatAlignments = new ArrayList<MatchingPair>();
		OWLOntology mergedOntology = null;
		List<Double> similarityList = null;
		
		try{
//			loadMatrix();
			
			//Load the 2 ontologies and get back the merged + translated ontology.
			mergedOntology = util.loadOntologies(alignment);
			unsatAlignments = getUnsatisfiableAlignments(alignment, mergedOntology);
			
			//Queue for sorting axioms by similarity number
			PriorityQueue<MatchingPair> similarity = new PriorityQueue<MatchingPair>(4000, new Comparator<MatchingPair>() {
				@Override
				public int compare(MatchingPair o1, MatchingPair o2) {
					double v = o1.similarity - o2.similarity;
					if( v < 0.0d ) return 1;
					if( v > 0.0d ) return -1;
					return 0;
				}
			});
			for(MatchingPair mp : unsatAlignments)
				similarity.add(mp);
			
			List<MatchingPair> mpList = new ArrayList<MatchingPair>();
			//MatchingPair pair = new MatchingPair();
			
			System.out.println(" ");
			log.info("Adding back mappings with similarity value of 1.0");
			while(!similarity.isEmpty()){
				//Add the axioms with similarity = 1 back into the alignment, assuming they won't cause inconsistency
				if( similarity.peek().similarity == 1.0 ) {
					MatchingPair p2 = similarity.poll();
					alignment.add(p2);
					log.info("Added back: " + p2);
				}
					
				//Store the other axioms separately
				else {
					mpList.add(similarity.poll());
				}
			}
			
			List<MatchingPair> inconsistentPairs = repairAdditions(mpList, alignment, mergedOntology);
//			List<MatchingPair> inconsistentPairs = repairAdditions(unsatAlignments, alignment, mergedOntology);
			
			log.info("The unsatisfiable mappings are: ");
			for(MatchingPair mp : inconsistentPairs){
				log.info(mp.toString());
			}
			System.out.println(" ");
			
			//Get next best mapping for the source & target classes in inconsistentPairs from similarity matrix (loaded earlier).
			//Add these new axioms back into the alignment and run the repair iteration.
			/*int iteration = 1;
			List<MatchingPair> newUnsatAlignments = new ArrayList<MatchingPair>();
			
			while(inconsistentPairs.size() > 0){
				
				System.out.println("\n------------------------------ Repair with candidate mapping " + iteration + " ------------------------------");
				
				HashMap<String, Mapping> candidateMap = getCandidateMappings(inconsistentPairs, alignment);
				
				for(Mapping candidate : candidateMap.values()){
					if(candidate != null){
						log.info("Adding new candidate mapping: " + candidate.toString() + "\n");
						newUnsatAlignments.add(new MatchingPair(candidate.getEntity1().getUri(), candidate.getEntity2().getUri(), 
								candidate.getSimilarity(), candidate.getRelation()));
					}
				}
				
				alignment.addAll(newUnsatAlignments);
				mergedOntology = util.loadOntologies(alignment);
				unsatAlignments = getUnsatisfiableAlignments(alignment, mergedOntology);
				inconsistentPairs = repairAdditions(unsatAlignments, alignment, mergedOntology);
				
				iteration++;
			}*/
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
}
