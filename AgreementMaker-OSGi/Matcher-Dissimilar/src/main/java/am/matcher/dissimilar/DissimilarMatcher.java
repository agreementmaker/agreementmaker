/**
 * 
 */
package am.matcher.dissimilar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import am.Utility;
import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.manual.ManualProfilerMatchingParameters;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.Combination.CombinationParameters;
import am.matcher.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;
import am.matcher.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralParameters;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcherParameters;
import am.matcher.bsm.BaseSimilarityMatcher;
import am.matcher.bsm.BaseSimilarityParameters;
import am.matcher.intraInterCoupling.IntraInterCouplingMatcher;
import am.matcher.intraInterCoupling.IntraInterCouplingParameters;
import am.matcher.multiWords.MultiWordsMatcher;
import am.matcher.multiWords.MultiWordsParameters;
import am.matcher.parametricStringMatcher.ParametricStringMatcher;
import am.matcher.parametricStringMatcher.ParametricStringParameters;

import com.hp.hpl.jena.rdf.model.Property;
/**
 * The matching algorithm for OAEI 2011.
 * 
 * New features included in this year's matching algorithm:
 * 
 * 
 */
public class DissimilarMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = -2258529392257305644L;
	
	private static String ONTOLOGY_BASE_PATH = "ontologies/benchmarks/"; // Change ONLY IF REQUIRED
	//private static String ONTOLOGY_BASE_PATH = "ontologies/conference/"; 
	private static String SOURCE_ONTOLOGY = "101";  // Change this for TESTING
	private static String TARGET_ONTOLOGY = ""; // Change this for TESTING
	private static Logger log = Logger.getLogger(DissimilarMatcher.class);
	
	//This should be false in batch mode & using learning matcher / true for alignment evaluation
	
	public DissimilarMatcher(){
		super();
		needsParam = false;
		setName("Dissimilar Matcher");
		setCategory(MatcherCategory.HYBRID);
	}
	
	public static void main(String[] args) throws Exception {

		DissimilarMatcher dm = new DissimilarMatcher();
		
		File folder = new File("ontologies/benchmarks/");
		File[] listOfFiles = folder.listFiles();
		//File folder = new File("ontologies/conference_alignment/");
		//File[] listOfFiles = folder.listFiles();// folder.listFiles();
		for (File file : listOfFiles) 
		{
		    if (file.isDirectory()) //(file.isFile())
		    {
		    	//String[] tmp_str=file.getName().split("-");
		    	//String tmp_src=tmp_str[0]+".owl";
		    	//String tmp_trg=tmp_str[1].substring(0, tmp_str[1].lastIndexOf('.'))+".owl";
		    	TARGET_ONTOLOGY=file.getName();
		
		    	System.out.println(TARGET_ONTOLOGY);
				Ontology source = OntoTreeBuilder.loadOWLOntology(ONTOLOGY_BASE_PATH
						+ SOURCE_ONTOLOGY + "/onto.rdf");
				Ontology target = OntoTreeBuilder.loadOWLOntology(ONTOLOGY_BASE_PATH
						+ TARGET_ONTOLOGY + "/onto.rdf");
//
//				Ontology source = OntoTreeBuilder.loadOWLOntology(ONTOLOGY_BASE_PATH
//						+ tmp_src);
//				Ontology target = OntoTreeBuilder.loadOWLOntology(ONTOLOGY_BASE_PATH
//						+ tmp_trg);
//		    	
				source.setDescription(SOURCE_ONTOLOGY);
				OntologyDefinition def1=new OntologyDefinition(true, source.getURI(), null, null);
				OntologyDefinition def2=new OntologyDefinition(true, target.getURI(), null, null);
				def1.largeOntologyMode=false;
				source.setDefinition(def1);
				def2.largeOntologyMode=false;
				target.setDefinition(def2);
				ManualOntologyProfiler mop=new ManualOntologyProfiler(source, target);
				dm.setSourceOntology(source);
				dm.setTargetOntology(target);
		
				DissimilarMatcherParameters param = new DissimilarMatcherParameters();
				param.threshold = 0.6;
				param.maxSourceAlign = 1;
				param.maxTargetAlign = 1;
				dm.setName(TARGET_ONTOLOGY);
				dm.setParam(param);
		
				try 
				{
					dm.match();
				} 
				catch (Exception e) 
				{
					log.error("Caught exception when running MyMatcher.", e);
				}

				dm.getAlignment();
				// run the reference alignment evaluation and output it to the log4j
				// logger
				dm.referenceEvaluation(ONTOLOGY_BASE_PATH + TARGET_ONTOLOGY + "/refalign.rdf");
				//dm.referenceEvaluation("ontologies/conference_alignment/"+TARGET_ONTOLOGY.substring(0,TARGET_ONTOLOGY.lastIndexOf("."))+".rdf");
				
				
		    }
		}

	}
	
	private void save_to_file(StringBuilder content, String fileName)
	{
		File file_ref = new File("ontologies/refalign/iic_iism/"+fileName+".txt");
		 
		// if file doesnt exists, then create it
		try
		{
			if (!file_ref.exists()) {
				file_ref.createNewFile();
			}
	
			FileWriter fw = new FileWriter(file_ref.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content.toString());
			bw.close();
			fw.close();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}

	}

	private void referenceEvaluation(String pathToReferenceAlignment)
			throws Exception {
		// Run the reference alignment matcher to get the list of mappings in
		// the reference alignment file
		ReferenceAlignmentMatcher refMatcher = new ReferenceAlignmentMatcher();//(ReferenceAlignmentMatcher) MatcherFactory
//				.getMatcherInstance(MatchersRegistry.ImportAlignment);

		// these parameters are equivalent to the ones in the graphical
		// interface
		ReferenceAlignmentParameters parameters = new ReferenceAlignmentParameters();
		parameters.fileName = pathToReferenceAlignment;
		parameters.format = ReferenceAlignmentMatcher.OAEI;
		parameters.onlyEquivalence = false;
		parameters.skipClasses = false;
		parameters.skipProperties = false;
		refMatcher.setSourceOntology(this.getSourceOntology());
		refMatcher.setTargetOntology(this.getTargetOntology());

		// When working with sub-superclass relations the cardinality is always
		// ANY to ANY
		if (!parameters.onlyEquivalence) {
			parameters.maxSourceAlign = AbstractMatcher.ANY_INT;
			parameters.maxTargetAlign = AbstractMatcher.ANY_INT;
		}

		refMatcher.setParam(parameters);

		// load the reference alignment
		refMatcher.match();
		
		Alignment<Mapping> referenceSet;
		if (refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned()) {
			referenceSet = refMatcher.getAlignment(); // class + properties
		} else if (refMatcher.areClassesAligned()) {
			referenceSet = refMatcher.getClassAlignmentSet();
		} else if (refMatcher.arePropertiesAligned()) {
			referenceSet = refMatcher.getPropertyAlignmentSet();
		} else {
			// empty set? -- this should not happen
			referenceSet = new Alignment<Mapping>(Ontology.ID_NONE,
					Ontology.ID_NONE);
		}

		// the alignment which we will evaluate
		Alignment<Mapping> myAlignment;

		if (refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned()) {
			myAlignment = getAlignment();
		} else if (refMatcher.areClassesAligned()) {
			myAlignment = getClassAlignmentSet();
		} else if (refMatcher.arePropertiesAligned()) {
			myAlignment = getPropertyAlignmentSet();
		} else {
			myAlignment = new Alignment<Mapping>(Ontology.ID_NONE,
					Ontology.ID_NONE); // empty
		}

		// use the ReferenceEvaluator to actually compute the metrics
		ReferenceEvaluationData rd = ReferenceEvaluator.compare(myAlignment,
				referenceSet);

		// optional
		setRefEvaluation(rd);

		// output the report
		StringBuilder report = new StringBuilder();
		report.append("Reference Evaluation Complete\n\n").append(getName())
				.append("\n\n").append(rd.getReport()).append("\n");

		//log.info(report);
		
		// use system out if you don't see the log4j output
		System.out.println(report);
		save_to_file(report, TARGET_ONTOLOGY);
	}
	@Override
	public String getDescriptionString() {
		return "The method adopted in the OAEI 2011 competition.  This algorithm chooses a matcher configuration automatically.";
	}
	
	
	public enum SubMatcherID {
		BSM, PSM, VMM, LSM,	MM,	IISM, GFM, LWC1, LWC2, LWC3
	}
	
	private Map<SubMatcherID, AbstractMatcher> matchersByID = new HashMap<SubMatcherID, AbstractMatcher>();
	
	/** *****************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	@Override
	public void match() throws Exception {
    	matchStart();
    	for( MatchingProgressListener mpd : progressDisplays ) mpd.ignoreComplete(true);
    	
    	AbstractMatcher finalResult = null;
    	finalResult = runGeneralPurpose();
		for( MatchingProgressListener mpd : progressDisplays ) mpd.ignoreComplete(false); 
    	
		if( finalResult != null ) {
			finalResult.select();
			classesMatrix = finalResult.getClassesMatrix();
			propertiesMatrix = finalResult.getPropertiesMatrix();
			classesAlignmentSet = finalResult.getClassAlignmentSet();
			propertiesAlignmentSet = finalResult.getPropertyAlignmentSet();
			finalResult = null;
		}
    	matchEnd();
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
	}
	

	private AbstractMatcher runGeneralPurpose() throws Exception {
		
		// Build the lexicons.
		LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
		lexParam.sourceOntology = sourceOntology;
		lexParam.targetOntology = targetOntology;
		
		lexParam.sourceUseLocalname = true;
		lexParam.targetUseLocalname = true;
		lexParam.sourceUseSCSLexicon = false;
		lexParam.targetUseSCSLexicon = false;
		
		lexParam.detectStandardProperties();
		
		Core.getLexiconStore().buildAll(lexParam);
		
		List<AbstractMatcher> lwcInputMatchers = new ArrayList<AbstractMatcher>();
		
		final DefaultMatcherParameters param = getParam();
		
		// The BSM needs an ontology profiler.
		Core.getInstance().setOntologyProfiler(
				ManualOntologyProfiler.createOntologyProfiler(sourceOntology, targetOntology));
				
		// BSM
		
		
		if( !isCancelled() ) {
			//AbstractMatcher psm = MatcherFactory.getMatcherInstance(BaseSimilarityMatcher.class);
			AbstractMatcher bsm =  new BaseSimilarityMatcher();
			
			BaseSimilarityParameters bsmParam = 
					new BaseSimilarityParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			bsmParam.useDictionary = false;
			
			setupSubMatcher(bsm, bsmParam);
			runSubMatcher(bsm, "BSM 1/6");
			
			lwcInputMatchers.add(bsm);			
		}
		
		
		// PSM
		if( !isCancelled() ) {
			//AbstractMatcher psm = MatcherFactory.getMatcherInstance(ParametricStringMatcher.class);
			AbstractMatcher psm = new ParametricStringMatcher();
			ParametricStringParameters psmParam = 
					new ParametricStringParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			
			psmParam.localWeight = 0.33;
			psmParam.labelWeight = 0.34d;
			psmParam.commentWeight = 0.33d;
			psmParam.seeAlsoWeight = 0.00d;
			psmParam.isDefinedByWeight = 0.00d;
			
			psmParam.useLexicons = false;
			psmParam.useBestLexSimilarity = false;
			psmParam.measure = StringMetrics.AMSUB_AND_EDIT;
			psmParam.normParameter = new NormalizerParameter();
			psmParam.normParameter.setForOAEI2009();
			psmParam.redistributeWeights = true;
			
			setupSubMatcher(psm, psmParam);
			runSubMatcher(psm, "PSM 2/6");
			
			lwcInputMatchers.add(psm);
			//psm.getClassesMatrix().get(i, j)
		}
		
		// VMM
		if( !isCancelled() ) {
			//AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MultiWordsMatcher.class);
			AbstractMatcher vmm=new MultiWordsMatcher();
			MultiWordsParameters vmmParam = 
					new MultiWordsParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			
			vmmParam.measure = MultiWordsParameters.TFIDF;
			//only on concepts right now because it should be weighted differently
			vmmParam.considerInstances = true;
			vmmParam.considerNeighbors = false;
			vmmParam.considerConcept = true;
			vmmParam.considerClasses = false;
			vmmParam.considerProperties = false;
			vmmParam.ignoreLocalNames = true; 
			
			vmmParam.useLexiconSynonyms = true; // May change later.
			
			setupSubMatcher(vmm, vmmParam);
			runSubMatcher(vmm, "VMM 3/6");
			
			lwcInputMatchers.add(vmm);
		}
		
		// LSM
		if( !isCancelled() ) {
			//AbstractMatcher lsm = MatcherFactory.getMatcherInstance(LexicalSynonymMatcher.class);
			AbstractMatcher lsm = new LexicalSynonymMatcher();
			LexicalSynonymMatcherParameters lsmParam = 
					new LexicalSynonymMatcherParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			lsmParam.useSynonymTerms = false;
			
			setupSubMatcher(lsm, lsmParam);
			runSubMatcher(lsm, "LSM 4/6");
			
			lwcInputMatchers.add(lsm);
			
		}
		
		lwcInputMatchers.get(0).getClassesMatrix();
		//printMatrix(lwcInputMatchers,0, "first");
		/*
		// IIC2
		if( !isCancelled() ) {
			//AbstractMatcher lsm = MatcherFactory.getMatcherInstance(LexicalSynonymMatcher.class);
			AbstractMatcher iic2 = new iic2();
			iic2Parameters iicParam = 
					new iic2Parameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			//iicParam.useSynonymTerms = false;
			iic2.setInputMatchers(lwcInputMatchers);
			setupSubMatcher(iic2, iicParam);
			runSubMatcher(iic2, "IIC2 4/6");
					
			//lwcInputMatchers.add(iic2);
					
		}
		lwcInputMatchers.get(0).getClassesMatrix();
		*/
		//printMatrix(lwcInputMatchers,0, "second");
		
		AbstractMatcher lastLayer=new CombinationMatcher();
		boolean lwc_combination=false;
		if (lwc_combination)
		{
		// LWC
			AbstractMatcher lwc = null;
			if( !isCancelled() ) {
				//lwc = MatcherFactory.getMatcherInstance(CombinationMatcher.class);
				lwc=new CombinationMatcher();
				lwc.setInputMatchers(lwcInputMatchers);
				
				CombinationParameters lwcParam = 
						new CombinationParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
				lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
				lwcParam.qualityEvaluation = true;
				lwcParam.manualWeighted = false;
				lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
				
				setupSubMatcher(lwc, lwcParam);
				runSubMatcher(lwc, "LWC 6/6");
				//return lwc;
				lastLayer=lwc;
			}
		}
		else
		{
		//IIC
			AbstractMatcher iic = null;
			if( !isCancelled() ) {
				//iic = MatcherFactory.getMatcherInstance(IntraInterCouplingMatcher.class);
				iic=new IntraInterCouplingMatcher();
				iic.setInputMatchers(lwcInputMatchers);
				
				IntraInterCouplingParameters iicParam = 
						new IntraInterCouplingParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
				iicParam.combinationType = IntraInterCouplingParameters.AVERAGECOMB;
				
				iicParam.qualityEvaluation = true;
				iicParam.manualWeighted = false;
				iicParam.metricType=IntraInterCouplingParameters.BOTH_COUPLING;
				iicParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
				
				setupSubMatcher(iic, iicParam);
				runSubMatcher(iic, "IIC 6/6");
				//return iic;
				lastLayer=iic;
			}
		}
		

		//IISM
		
		if( !isCancelled() ) {
			//AbstractMatcher iism = MatcherFactory.getMatcherInstance(IterativeInstanceStructuralMatcher.class);
			AbstractMatcher iism=new IterativeInstanceStructuralMatcher();
			iism.addInputMatcher(lastLayer);
			
			IterativeInstanceStructuralParameters iismParam = 
					new IterativeInstanceStructuralParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			
			iismParam.allBoost();
			iismParam.setConsiderIndividuals(true);
			iismParam.setPropertyUsageThreshold(0.6);
			iismParam.setPropertyValuesThreshold(0.5);
			iismParam.setRangeDomainThreshold(0.89);
			iismParam.setSuperclassThreshold(0.6);
			iismParam.setUsePropertyUsage(true);
			iismParam.setUsePropertyValues(true);
			iismParam.setUseRangeDomain(true);
			iismParam.setUseSuperclasses(true);
			
			setupSubMatcher(iism, iismParam);
			runSubMatcher(iism, "IISM 6/6");
			
			return iism;
		}
		
		return null;
	}
	
	
	
	
	/* ********************************* COMMON METHODS *********************************** */
	
	private void setupSubMatcher( AbstractMatcher m, DefaultMatcherParameters p ) { setupSubMatcher(m, p, true); }
	
	private void setupSubMatcher( AbstractMatcher m, DefaultMatcherParameters p, boolean progressDelay ) {
		m.setParameters(p);
		m.setSourceOntology(sourceOntology);
    	m.setTargetOntology(targetOntology);
		for( MatchingProgressListener mpd : progressDisplays ) m.addProgressDisplay(mpd);
		m.setUseProgressDelay(progressDelay);
		m.setPerformSelection(true);
	}
	
	private void runSubMatcher(AbstractMatcher m, String label) throws Exception {
		long startime = 0, endtime = 0, time = 0;
		long measure = 1000000;
		
		//DissimilarMatcherParameters p = (DissimilarMatcherParameters) param;
		
		if( Core.DEBUG ) System.out.println("Running " + m.getRegistryEntry().getMatcherShortName() );
		startime = System.nanoTime()/measure;
		
		for( MatchingProgressListener mpd : progressDisplays ) mpd.setProgressLabel(label);
		//m.setProgressDisplay(getProgressDisplay());
		m.match();
		//m.setProgressDisplay(null);
		if( m.isCancelled() ) { cancel(true); } // the user canceled the matching process  
		
		endtime = System.nanoTime()/measure;
	    time = (endtime-startime);
		if( Core.DEBUG ) System.out.println(m.getRegistryEntry().getMatcherShortName() + " completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		/*
		if(p.showIntermediateMatchers && !m.isCancelled()) {
			MatchingTask mt = new MatchingTask(m, m.getParam(), 
					new MwbmSelection(), new DefaultSelectionParameters());
			
			Core.getInstance().addMatchingTask(mt);
		}*/
	}
	

	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new DissimilarMatcherParametersPanel();
		}
		return parametersPanel;
	}
	
	public AbstractMatcher getSubMatcherByID(SubMatcherID id) {
		return matchersByID.get(id);
	}
	
}