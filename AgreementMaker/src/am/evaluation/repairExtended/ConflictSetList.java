package am.evaluation.repairExtended;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import com.hp.hpl.jena.rdf.model.Property;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.LexicalMatcherJAWS.LexicalMatcherJAWS;
import am.app.mappingEngine.abstractMatcherNew.AbstractMatchingParameters;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.structuralMatchers.SimilarityFlooding;
import am.app.mappingEngine.structuralMatchers.SimilarityFloodingParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WrappingGraph;
import am.app.mappingEngine.wikipedia.WikiMatcher;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.manual.ManualProfilerMatchingParameters;
import am.evaluation.repair.AlignmentRepairUtilities;
import am.userInterface.MatchersChooser;
/**
 * @author Pavan
 *
 *	ConflictSset class is used to hold the complete list of inconsistent OWLClass (s) and each class's corresponding OWLAxioms
 */
public class ConflictSetList {
	
	private static Logger log = Logger.getLogger(ConflictSetList.class);
	private AlignmentRepairUtilities util = new AlignmentRepairUtilities(log);
	private File sourceOwl;
	private File targetOwl;
	
	private OWLOntology MergedOntology;
	
	private ArrayList<ConflictSet> ConflictSets = new ArrayList<ConflictSet>();
	
	private ArrayList<OWLAxiom> sortedAxioms = new ArrayList<OWLAxiom>();	
	private Tree<OWLClass> classTree;
	//private Tree<AxiomRank> axiomTree = new Tree<AxiomRank>();
	private ArrayList<ArrayList<AxiomRank>> axiomLists;
	private ArrayList<OWLAxiom> minimalHittingSet;
	
	//private HashMap<Boolean, MatchingPair> RepairedMappings = new HashMap<Boolean, MatchingPair>();
	ArrayList<MatchingPair> RemoveMappings = new ArrayList<MatchingPair>();
	ArrayList<MatchingPair> AddMappings = new ArrayList<MatchingPair>();
		
	public Integer getClassCount(){
		return ConflictSets.size();
	}	
	
	public ConflictSet getSet(OWLClass cls){
		
		ConflictSet reqdSet = null;
		
		for(ConflictSet cset : ConflictSets){
			if(cset.getInconsistentClass() == cls)
				reqdSet = cset;
		}
		
		return reqdSet;
	}
	
	public Integer getAxiomCount(){
		
		int axiomCount = 0;
		
		for(ConflictSet set : ConflictSets){
			axiomCount = axiomCount + set.getAxiomCount();
		}	
		
		return axiomCount;
	}
	
	public Integer getDistinctAxiomCount(){
		
		ArrayList<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		
		for(ConflictSet cset : ConflictSets){
			for(AxiomRank axiom : cset.getAxiomList()){
				axioms.add(axiom.getAxiom());
			}
		}
		
		return removeDuplicates(axioms).size();
	}
	
	public ArrayList<OWLClass> getClassList(){
		
		ArrayList<OWLClass> classes = new ArrayList<OWLClass>();
		
		for(ConflictSet cset : ConflictSets){
			classes.add(cset.getInconsistentClass());
		}
		
		return classes;
	}
	
	public ArrayList<AxiomRank> getAxiomRankList(){
		
		ArrayList<AxiomRank> axiomRanks = new ArrayList<AxiomRank>();
		
		for(ConflictSet cset : ConflictSets){
			axiomRanks.addAll(cset.getAxiomList());
		}
		
		return axiomRanks;
	}
	
	public ArrayList<OWLAxiom> getAllAxioms(){
		
		ArrayList<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		
		for(ConflictSet cset : ConflictSets){
			for(AxiomRank axiom : cset.getAxiomList()){
				axioms.add(axiom.getAxiom());
			}
		}
		
		return axioms;
	}

	public void addDistinct(ConflictSet conflictSet) {
		
		ConflictSet set = getConflictSet(conflictSet.getInconsistentClass());
		
		if(set == null)					
			ConflictSets.add(conflictSet);			
		else 
			set.AddAxioms(conflictSet.getAxiomList());	
	}
	
	public ConflictSet getConflictSet(OWLClass cls){
		
		ConflictSet set = null;
		
		for(ConflictSet cset : ConflictSets){
			if(cset.getInconsistentClass() == cls){
				set = cset;
				break;
			}				
		}
		
		return set;		
	}
	
	//@SuppressWarnings("unchecked")
	public void rankAxioms(){
		
		int count = 1;
		Map<OWLAxiom,Integer> axiomCounts = new HashMap<OWLAxiom,Integer>();
		ArrayList<AxiomRank> axiomRanks = getAxiomRankList();
		ArrayList<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		
		for(AxiomRank ar : axiomRanks){
			
			OWLAxiom axm = ar.getAxiom();
			axioms.add(axm);
			
			if(axiomCounts.containsKey(axm)){
				axiomCounts.put(axm, (Integer)axiomCounts.get(axm)+1);				
			}
			else 
				axiomCounts.put(axm, count);	
		}
		
		/*Iterator it = axiomCounts.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }*/
		
		sortedAxioms = (ArrayList<OWLAxiom>) entriesSortedByValues(axiomCounts);
		
		for(ConflictSet set : ConflictSets){
			for(AxiomRank ar : set.getAxiomList()){
				ar.setRank(sortedAxioms.indexOf(ar.getAxiom()) + 1);	
			}
		}			
		
	}
	
	static <K,V extends Comparable<? super V>> 
		List<K> entriesSortedByValues(Map<K,V> map) {

		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
	
		Collections.sort(sortedEntries, 
				new Comparator<Entry<K,V>>() {
	        	@Override
	        	public int compare(Entry<K,V> e1, Entry<K,V> e2) {
	        		return e2.getValue().compareTo(e1.getValue());
	        	}
	    	}
		);

		List<K> sortedKeys = new ArrayList<K>();
		
		//return sortedEntries;
		for (Map.Entry<K,V> entry : sortedEntries) {
			sortedKeys.add(entry.getKey());
			//System.out.println(entry.getKey() + " - (count - " + entry.getValue() + ")");
		}		
	    		
		return sortedKeys;
	}
		
	public ArrayList<OWLAxiom> computeHittingSet(ConflictSetList mups){
		
		axiomLists = new ArrayList<ArrayList<AxiomRank>>();
		minimalHittingSet = new ArrayList<OWLAxiom>(); 
		
		for(ConflictSet set : mups.getConflictSets()){
			axiomLists.add(set.getAxiomList());			
		}
		
		//log.info(axiomLists.size());
		
		Integer outerIndex = 0;
		//Integer index = 0;
		
		//while((axiomLists.get(outerIndex).size() - 1) > index){
			
		//System.out.println(index + "/" + outerIndex);
			
		//AxiomRank ar = axiomLists.get(outerIndex).get(index);
			
		//axiomTree.addChild(ar,null); //adding under root
		minimalHittingSet.add(minimumAxiomRank(axiomLists.get(outerIndex)));

		if(((axiomLists.size() - 1) > outerIndex))
			getAxiomNextLayer((outerIndex + 1));
			
			//index++;
		//}
		
		//removing duplicates
		/*HashSet<OWLAxiom> hs = new HashSet<OWLAxiom>();
		hs.addAll(minimalHittingSet);
		minimalHittingSet.clear();
		minimalHittingSet.addAll(hs);*/
		
		minimalHittingSet = removeDuplicates(minimalHittingSet);
		
		//axiomTree.print();
		//System.out.println(minimalHittingSet.size());
		//System.out.println(minimalHittingSet);
		
		return minimalHittingSet;
	}
	
	private void getAxiomNextLayer(int outerIndex){
		
		//Integer index = 0;
		
		//while((axiomLists.get(outerIndex).size() - 1) > index){
					
		//System.out.println(index + "/" + outerIndex);
					
		//AxiomRank ar = axiomLists.get(outerIndex).get(index);
					
		//axiomTree.addChild(ar,parent); //adding under root
		minimalHittingSet.add(minimumAxiomRank(axiomLists.get(outerIndex)));

		if(((axiomLists.size() - 1) > outerIndex))
			getAxiomNextLayer((outerIndex + 1));
			
		//index++;
		//}
	}
	
	private OWLAxiom minimumAxiomRank(ArrayList<AxiomRank> axiomList){
		
		AxiomRank axiomRank = new AxiomRank(null,Integer.MAX_VALUE);
		Integer minrank = axiomRank.getRank();
		ArrayList<AxiomRank> uniqueAxms = new ArrayList<AxiomRank>();
		
		/*for(AxiomRank ar : axiomList){
			if(!minimalHittingSet.contains(ar.getAxiom())){
				//log.info(ar.getAxiom());
				uniqueAxms.add(ar);
			}
		}*/
		
		for(AxiomRank ar : axiomList){
		
			if(ar.getRank() < minrank){
				axiomRank = ar;
				minrank = ar.getRank();
			}				
		}
		
		return axiomRank.getAxiom();
	}
	
	public void repairMappings(ArrayList<OWLAxiom> axioms, String source, String target){
		
		OWLClass sourceClass = null;
		OWLClass targetClass = null;
		HashMap<Boolean,MatchingPair> repairedMappings = new HashMap<Boolean,MatchingPair>();
		Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(source); 
		Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(target); 
		MatchingPair tempPair = new MatchingPair();
		MatchingPair newMapping = null;		
		MatchingPair tempMapping = null;
		Double maxSimilarity = 0.0;
		
		for(OWLAxiom axm : axioms){
			
			if(axm != null){
			
				String originalSourceUri = "";
				String originalTargetUri = "";
				MatchingPair originalMapping = null;
				Node originalSourceNode = null;
				
				sourceClass = new ArrayList<OWLClass>(axm.getClassesInSignature()).get(1);
				targetClass = new ArrayList<OWLClass>(axm.getClassesInSignature()).get(0);
				
				for(Node sourceNode : sourceOntology.getClassesList()){		
					
					if(sourceClass.getIRI().toURI().toString().equals(sourceNode.getUri()) || targetClass.getIRI().toURI().toString().equals(sourceNode.getUri())){
						originalSourceUri = sourceNode.getUri();
						originalSourceNode = new Node(sourceNode);
						break;
					}
				}		
				
				for(Node targetNode : targetOntology.getClassesList()){			
					if(targetClass.getIRI().toURI().toString().equals(targetNode.getUri()) || sourceClass.getIRI().toURI().toString().equals(targetNode.getUri())){				
						originalTargetUri = targetNode.getUri();
						break;
					}
				}
	
				originalMapping = new MatchingPair(originalSourceUri,originalTargetUri,0.0,null);
				//log.info("-" + originalMapping);
				newMapping = null; 
							
				for(Node targetNode : targetOntology.getClassesList()){				
						
					if(originalSourceNode != null){
						tempMapping = rematch(originalSourceNode, targetNode);
					
						if(tempMapping != null){
						
							if(tempMapping.similarity > maxSimilarity){
								newMapping = tempMapping;
								maxSimilarity = tempMapping.similarity;
								break;
							}
						}
					}
				}
	
				RemoveMappings.add(originalMapping);
				
				if(newMapping != null)
					AddMappings.add(newMapping);
			}
		}		
		
	}
	
	private MatchingPair rematch(Node sourceNode, Node targetNode){
		
		//AbstractMatcher matcher;	
		//AbstractMatchingParameters param;
		SimilarityMatrix matrix = null;
		MatchingPair newMapping = null;
		Mapping tempMapping;
		Double maxSimilarity = 0.0;
		
		try {	
			//BSM
			/*Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(sourceOwl.getAbsolutePath());
			Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(targetOwl.getAbsolutePath());
			
			ManualOntologyProfiler profiler = new ManualOntologyProfiler(sourceOntology, targetOntology);			
			ManualProfilerMatchingParameters param = new ManualProfilerMatchingParameters();

			// do not use the localnames for matching
			param.matchSourceClassLocalname = false;
			param.matchTargetClassLocalname = false;
			param.matchSourcePropertyLocalname = false;
			param.matchTargetPropertyLocalname = false;
			
			// select the rdfs label as the only thing that will match against
			param.sourceClassAnnotations = createAnnotationList("label", profiler.getSourceClassAnnotations());
			param.targetClassAnnotations = createAnnotationList("label", profiler.getTargetClassAnnotations());
			param.sourcePropertyAnnotations = createAnnotationList("label", profiler.getSourcePropertyAnnotations());
			param.targetPropertyAnnotations = createAnnotationList("label", profiler.getTargetPropertyAnnotations());
			
			profiler.setMatchTimeParams(param);
			Core.getInstance().setOntologyProfiler(profiler);
			
			BaseSimilarityParameters bsmParam = new BaseSimilarityParameters();			
			BaseSimilarityMatcher bsm = new BaseSimilarityMatcher(bsmParam);
			//log.info("berfore match" + sourceNode + " - " + targetNode);
			tempMapping = bsm.alignTwoNodesParallel(sourceNode, targetNode, alignType.aligningClasses, matrix);					
			//log.info(("here " + tempMapping));
			if(tempMapping != null){
				if(tempMapping.getSimilarity() > maxSimilarity){
					newMapping =  new MatchingPair(sourceNode.getUri(),
								targetNode.getUri(),tempMapping.getSimilarity(),tempMapping.getRelation());	
					maxSimilarity = newMapping.similarity;
				}
			}*/
			
			//Parameteric string matcher
			ParametricStringParameters psparam = new ParametricStringParameters();	
			psparam.labelWeight = 1.0;
			psparam.useBestLexSimilarity = true;
			ParametricStringMatcher psm = new ParametricStringMatcher(psparam);
			
			tempMapping = psm.alignTwoNodesParallel(sourceNode, targetNode, alignType.aligningClasses, matrix);					
			
			if(tempMapping != null){
				if(tempMapping.getSimilarity() > maxSimilarity){
					newMapping =  new MatchingPair(sourceNode.getUri(),
								targetNode.getUri(),tempMapping.getSimilarity(),tempMapping.getRelation());	
					maxSimilarity = newMapping.similarity;
				}
			}
			
			LexicalMatcherJAWS lmm = new LexicalMatcherJAWS();
			tempMapping = lmm.alignTwoNodesParallel(sourceNode, targetNode, alignType.aligningClasses, matrix);					
			
			if(tempMapping != null){
				if(tempMapping.getSimilarity() > maxSimilarity){
					newMapping =  new MatchingPair(sourceNode.getUri(),
								targetNode.getUri(),tempMapping.getSimilarity(),tempMapping.getRelation());	
					maxSimilarity = newMapping.similarity;
				}
			}
			
			/*SimilarityFloodingParameters smparam = new SimilarityFloodingParameters();
			//smparam.
			SimilarityFlooding sfm = new SimilarityFlooding() {				
				@Override
				protected void loadSimilarityMatrices(WrappingGraph s, WrappingGraph t) {
					// TODO Auto-generated method stub					
				}
				
				@Override
				protected void align() throws Exception {
					// TODO Auto-generated method stub					
				}
			};
			tempMapping = sfm.alignTwoNodesParallel(sourceNode, targetNode, alignType.aligningClasses, matrix);					
			
			if(tempMapping != null){
				if(tempMapping.getSimilarity() > maxSimilarity){
					newMapping =  new MatchingPair(sourceNode.getUri(),
								targetNode.getUri(),tempMapping.getSimilarity(),tempMapping.getRelation());	
					maxSimilarity = newMapping.similarity;
				}
			}*/
			
			/*WikiMatcher wm = new WikiMatcher();
			tempMapping = wm.alignTwoNodesParallel(sourceNode, targetNode, alignType.aligningClasses, matrix);					
			
			if(tempMapping != null){
				if(tempMapping.getSimilarity() > maxSimilarity){
					newMapping =  new MatchingPair(sourceNode.getUri(),
								targetNode.getUri(),tempMapping.getSimilarity(),tempMapping.getRelation());	
					maxSimilarity = newMapping.similarity;
				}
			}*/
			
			
		} catch (Exception e) {			
			// TODO Auto-generated catch block
		}
		finally{
			if(newMapping != null){
				newMapping = new MatchingPair();
			}
		}
		
		return newMapping;
	}

//	public HashMap<Boolean, MatchingPair> FixMappings(ArrayList<OWLAxiom> axioms, String source, String target) {
//
//		OWLClass sourceClass = null;
//		OWLClass targetClass = null;
//		Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(source); 
//		Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(target); 
//
//		HashMap<Boolean,MatchingPair> repairedMappings = new HashMap<Boolean,MatchingPair>();
//		SimilarityMatrix matrix = null;
//		
//		SimpleBatchModeRunner bm = new SimpleBatchModeRunner((File)null);
//		AbstractMatcher oaei2011 = bm.instantiateMatcher(null);
//		SimilarityMatrixOutput matrixoutput = new SimilarityMatrixOutput(oaei2011);
//		//matrix = matrixoutput.loadClassesMatrix("/home/pavan/MS/WebSemantics/Ontologies/Anatomy/AnatomyAlignmentclassMatrix.rdf");
//
//		Mapping newMapping;
//		
//		AppPreferences prefs = Core.getAppPreferences();
//		
//		//AbstractMatcher matcher = new OAEI2011Matcher();
//		//AbstractMatcher matcher = new BaseSimilarityMatcher();
//		AbstractMatcher matcher = new BasicStructuralSelectorMatcher();
//		//AbstractMatcher matcher = new AdvancedSimilarityMatcher();
//		
//		/*BaseSimilarityParameters params = new BaseSimilarityParameters();
//		
//		sourceOntology.getModel().getProperty("label");
//		//rdfs:label
//		
//		
//		ManualOntologyProfiler x = new ManualOntologyProfiler(sourceOntology, targetOntology);
//		ManualProfilerMatchingPanel z = new ManualProfilerMatchingPanel(x);		
//		ManualProfilerMatchingParameters y = new ManualProfilerMatchingParameters();
//		
//		ArrayList<Property> prop1 = new ArrayList<Property>();
//		ArrayList<Property> prop2 = new ArrayList<Property>();
//		prop1.add(sourceOntology.getModel().getProperty("rdfs:label"));
//		prop2.add(targetOntology.getModel().getProperty("rdfs:label"));
//		
//		//Property prop = sourceOntology.getModel().getProperty("label");
//		//y.sourceClassAnnotations.
//		//y.sourceClassAnnotations.s 
//		//y.matchTargetClassLocalname = true;
//		//y.
//		
//		y.matchSourceClassLocalname = false;
//		y.matchSourcePropertyLocalname = false;
//		y.matchTargetClassLocalname = false;
//		y.matchTargetPropertyLocalname = false;
//		
//		y.sourceClassAnnotations = prop1;
//		y.targetClassAnnotations = prop2;
//		
//		x.setMatchTimeParams(y);
//		Core.getInstance().setOntologyProfiler(x);
//		//params.useDictionary = prefs.getPanelBool( MatcherSetting.BSIM_USEDICT );
//		params.threshold = 0.6;		
//		
//		matcher.setParameters(params);*/
//		
//		for(OWLAxiom axm : axioms){
//			
//			sourceClass = new ArrayList<OWLClass>(axm.getClassesInSignature()).get(1);
//			targetClass = new ArrayList<OWLClass>(axm.getClassesInSignature()).get(0);
//			
//			//log.info(sourceClass);
//			//log.info(targetClass);
//			//log.info(sourceClass.getIRI().toURI().toString());
//			
//			
//			//srepairedMappings.put(false, axm);
//						
//			for(Node sourceNode : sourceOntology.getClassesList()){
//				//log.info(sourceNode.getUri());
//				if(sourceClass.getIRI().toURI().toString().equals(sourceNode.getUri())){
//					
//					//log.info("in");
//					
//					for(Node targetNode : targetOntology.getClassesList()){
//
//						//if(targetClass.getIRI().toURI().toString().equals(targetNode.getUri())){
//						
//							//log.info(sourceNode);
//							//log.info(targetNode);
//							
//							try {
//								
//								newMapping = matcher.alignTwoNodesParallel(sourceNode, targetNode, alignType.aligningClasses, matrix);
//							
//								if(newMapping != null){
//									log.info("old " + axm);
//									log.info("new " + newMapping);
//									repairedMappings.put(true, new MatchingPair(sourceNode.getUri(),
//											targetNode.getUri(),newMapping.getSimilarity(),newMapping.getRelation()));
//									repairedMappings.put(false, new MatchingPair(sourceClass.getIRI().toURI().toString(),
//											targetClass.getIRI().toURI().toString(),0.0,null));
//								}
//								else{
//									//log.info("fail :" + newMapping);
//									repairedMappings.put(false, new MatchingPair(sourceNode.getUri(),
//											targetNode.getUri(),0.0,null));
//								}
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						//}
//					}
//				}					
//			}
//		}
//				
//		/*repairedMappings = new HashMap<Boolean, MatchingPair>();
//		repairedMappings.put(false, new MatchingPair(sourceClass.getIRI().toURI().toString(),
//				targetClass.getIRI().toURI().toString(),0.0,null));*/
//		
//		return repairedMappings;
//	}
	
	public ConflictSetList computeMUPS() {
		
		ArrayList<ArrayList<OWLClass>> branches;
		ConflictSetList mups = new ConflictSetList();
		classTree = new Tree<OWLClass>();
		
		for(ConflictSet set : ConflictSets){
			
			classTree.addChild(set.getInconsistentClass(),null); //adding under root
				
			classTree = getSubClasses(set.getInconsistentClass());
			//System.out.println(set.getInconsistentClass() + "--");
		}
		
		//classTree.print();
		
		branches = classTree.getAllBranches(getEqAxiomClasses());
		//System.out.println("branch size - " + branches.size());
		
		for(ArrayList<OWLClass> br : branches){
			for(OWLClass cls : br ){
				//System.out.println("Branch : Class - " + cls);	
				ConflictSet cset = getSet(cls);
				
				if(cset != null)
					mups.addDistinct(cset);
			}
		}
		
		return mups;
	}
	
	
	private Tree<OWLClass> getSubClasses(OWLClass cls){
		
		for (OWLAxiom subClassAxiom : cls.getReferencingAxioms(MergedOntology)){
		
			if(subClassAxiom.getAxiomType() == AxiomType.SUBCLASS_OF){
				
				OWLClass subClass = (OWLClass)subClassAxiom.getClassesInSignature().toArray()[1];

				if(subClass == cls)
					continue;
				else
				{
					//System.out.println(cls + " subclass axiom" + subClassAxiom);
					classTree.addChild(subClass, cls);				
					classTree = getSubClasses(subClass);
				}
			}
		}
		
		return classTree;
	}
	
	public ArrayList<OWLClass> getEqAxiomClasses(){
		
		ArrayList<OWLClass> eqClasses = new ArrayList<OWLClass>();
		
		for(ConflictSet cset : ConflictSets){
			for(AxiomRank ar : cset.getAxiomList()){
				eqClasses.addAll(ar.getAxiomClasses());
			}
		}
		
		//removing duplicates
		HashSet<OWLClass> hs = new HashSet<OWLClass>();
		hs.addAll(eqClasses);
		eqClasses.clear();
		eqClasses.addAll(hs);
		
		return eqClasses;
	}
	
	private <T> ArrayList<T> removeDuplicates(ArrayList<T> list){
		
		ArrayList<T> distinctList = new ArrayList<T>();
		
		for(T p : list){
			if(!distinctList.contains(p))
				distinctList.add(p);
		}
		
		return distinctList;
	}
	
	// filter the annotationProperties list by matching name
	private static List<Property> createAnnotationList(String name, List<Property> annotationProperties) {
		List<Property> list = new LinkedList<Property>();
		for( Property p : annotationProperties ) {
			if( p.getLocalName().equals(name) ) {
				list.add(p);
			}
		}
		return list;
	}
	
	//getter setter
	public void setConflictSets (ArrayList<ConflictSet> conflictSets)
    {
		ConflictSets = conflictSets;           
    }
    public ArrayList<ConflictSet> getConflictSets()
    {
        return ConflictSets;
    }
    
    public void setMergedOntology (OWLOntology mergedOntology)
    {
		MergedOntology = mergedOntology;           
    }
    public OWLOntology getMergedOntology()
    {
        return MergedOntology;
    }
      
    public ArrayList<MatchingPair> getRemoveMappings()
    {
        return RemoveMappings;
    }
    
    public ArrayList<MatchingPair> getAddMappings()
    {
        return AddMappings;
    }
    
    public void setSourceOntology (File sourceOntology)
    {
    	sourceOwl = sourceOntology;           
    }
    
    public void setTargetOntology (File targetOntology)
    {
    	targetOwl = targetOntology;           
    }
}
