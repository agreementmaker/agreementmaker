package am.app.mappingEngine.matchersCombinationML;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import am.Utility;
import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.LexicalSynonymMatcher.LexicalSynonymMatcherParameters;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.manual.ManualProfilerMatchingParameters;

import com.hp.hpl.jena.rdf.model.Property;

public class LWCRunner {

	Ontology sourceOntology;
	Ontology targetOntology;
	double threshold; // this is the threshold value that is set in the ui
	
	int maxSourceAlign;// have to figure out what this is
	int maxTargetAlign;// have to figure out what this is

	public int getMaxSourceAlign() {
		return maxSourceAlign;
	}

	public void setMaxSourceAlign(int maxSourceAlign) {
		this.maxSourceAlign = maxSourceAlign;
	}

	public int getMaxTargetAlign() {
		return maxTargetAlign;
	}

	public void setMaxTargetAlign(int maxTargetAlign) {
		this.maxTargetAlign = maxTargetAlign;
	}

	public Ontology getSourceOntology() {
		return sourceOntology;
	}

	public void setSourceOntology(Ontology sourceOntology) {
		this.sourceOntology = sourceOntology;
	}

	public Ontology getTargetOntology() {
		return targetOntology;
	}

	public void setTargetOntology(Ontology targetOntology) {
		this.targetOntology = targetOntology;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public void initializeOntology(Ontology sourceOntology,
			Ontology targetOntology) {
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
	}

	public AbstractMatcher initializeLWC() throws Exception {
		// Build the lexicons.
		LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
		lexParam.sourceOntology = sourceOntology;
		lexParam.targetOntology = targetOntology;

		lexParam.sourceUseLocalname = true;
		lexParam.targetUseLocalname = false;
		lexParam.sourceUseSCSLexicon = false;
		lexParam.targetUseSCSLexicon = false;

		lexParam.detectStandardProperties(sourceOntology);
		lexParam.detectStandardProperties(targetOntology);

		Core.getLexiconStore().buildAll(lexParam);

		// Ontology profiling
		ProfilerRegistry entry = ProfilerRegistry.ManualProfiler;
		OntologyProfiler profiler = null;
		Constructor<? extends OntologyProfiler> constructor = null;

		constructor = entry.getProfilerClass().getConstructor(Ontology.class,
				Ontology.class);
		profiler = constructor.newInstance(Core.getInstance()
				.getSourceOntology(), Core.getInstance().getTargetOntology());

		if (profiler != null) {
			profiler.setName(entry);
			Core.getInstance().setOntologyProfiler(profiler);
		}

		ManualOntologyProfiler manualProfiler = (ManualOntologyProfiler) profiler;

		ManualProfilerMatchingParameters profilingMatchingParams = new ManualProfilerMatchingParameters();

		profilingMatchingParams.matchSourceClassLocalname = true;
		profilingMatchingParams.matchSourcePropertyLocalname = true;

		profilingMatchingParams.matchTargetClassLocalname = true;
		profilingMatchingParams.matchTargetPropertyLocalname = true;

		profilingMatchingParams.sourceClassAnnotations = new ArrayList<Property>();
		for (Property currentProperty : manualProfiler
				.getSourceClassAnnotations()) {
			if (currentProperty.getLocalName().toLowerCase().contains("label")) {
				profilingMatchingParams.sourceClassAnnotations
						.add(currentProperty);
			}
		}

		profilingMatchingParams.sourcePropertyAnnotations = new ArrayList<Property>();
		for (Property currentProperty : manualProfiler
				.getSourcePropertyAnnotations()) {
			if (currentProperty.getLocalName().toLowerCase().contains("label")) {
				profilingMatchingParams.sourcePropertyAnnotations
						.add(currentProperty);
			}
		}

		profilingMatchingParams.targetClassAnnotations = new ArrayList<Property>();
		for (Property currentProperty : manualProfiler
				.getTargetClassAnnotations()) {
			if (currentProperty.getLocalName().toLowerCase().contains("label")) {
				profilingMatchingParams.targetClassAnnotations
						.add(currentProperty);
			}
		}

		profilingMatchingParams.targetPropertyAnnotations = new ArrayList<Property>();
		for (Property currentProperty : manualProfiler
				.getTargetPropertyAnnotations()) {
			if (currentProperty.getLocalName().toLowerCase().contains("label")) {
				profilingMatchingParams.targetPropertyAnnotations
						.add(currentProperty);
			}
		}

		manualProfiler.setMatchTimeParams(profilingMatchingParams);

		// BSM
		List<AbstractMatcher> lwcInputMatchers = new ArrayList<AbstractMatcher>();

		AbstractMatcher bsm = MatcherFactory.getMatcherInstance(
				MatchersRegistry.BaseSimilarity, 0);

		BaseSimilarityParameters bsmParam = new BaseSimilarityParameters(
				getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
		bsmParam.useDictionary = false;

		setupSubMatcher(bsm, bsmParam);
		runSubMatcher(bsm, "BSM 1/6");

		lwcInputMatchers.add(bsm);

		// PSM

		AbstractMatcher psm = MatcherFactory.getMatcherInstance(
				MatchersRegistry.ParametricString, 0);

		ParametricStringParameters psmParam = new ParametricStringParameters(
				getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());

		psmParam.localWeight = 0.33;
		psmParam.labelWeight = 0.34d;
		psmParam.commentWeight = 0.33d;
		psmParam.seeAlsoWeight = 0.00d;
		psmParam.isDefinedByWeight = 0.00d;

		psmParam.useLexicons = false;
		psmParam.useBestLexSimilarity = false;
		psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
		psmParam.normParameter = new NormalizerParameter();
		psmParam.normParameter.setForOAEI2009();
		psmParam.redistributeWeights = true;

		setupSubMatcher(psm, psmParam);
		runSubMatcher(psm, "PSM 2/6");

		lwcInputMatchers.add(psm);

		// VMM

		AbstractMatcher vmm = MatcherFactory.getMatcherInstance(
				MatchersRegistry.MultiWords, 0);

		MultiWordsParameters vmmParam = new MultiWordsParameters(
				getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());

		vmmParam.measure = MultiWordsParameters.TFIDF;
		// only on concepts right now because it should be weighted differently
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

		// LSM

		AbstractMatcher lsm = MatcherFactory.getMatcherInstance(
				MatchersRegistry.LSM, 0);

		LexicalSynonymMatcherParameters lsmParam = new LexicalSynonymMatcherParameters(
				getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
		lsmParam.useSynonymTerms = false;

		setupSubMatcher(lsm, lsmParam);
		runSubMatcher(lsm, "LSM 4/6");

		lwcInputMatchers.add(lsm);

		// LWC
		AbstractMatcher lwc = null;

		lwc = MatcherFactory
				.getMatcherInstance(MatchersRegistry.Combination, 0);

		lwc.setInputMatchers(lwcInputMatchers);

		CombinationParameters lwcParam = new CombinationParameters(
				getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
		lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
		lwcParam.qualityEvaluation = true;
		lwcParam.manualWeighted = false;
		lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;

		setupSubMatcher(lwc, lwcParam);
		runSubMatcher(lwc, "LWC 5/6");
		return lwc;

	}

	private void setupSubMatcher(AbstractMatcher m, AbstractParameters p) {
		setupSubMatcher(m, p, true);
	}

	private void setupSubMatcher(AbstractMatcher m, AbstractParameters p,
			boolean progressDelay) {
		m.setParam(p);
		m.setSourceOntology(sourceOntology);
		m.setTargetOntology(targetOntology);
		m.setProgressDisplay(m.getProgressDisplay());
		m.setUseProgressDelay(progressDelay);
		m.setPerformSelection(true);
	}

	private void runSubMatcher(AbstractMatcher m, String label)
			throws Exception {
		long startime = 0, endtime = 0, time = 0;
		long measure = 1000000;

		// OAEI2011MatcherParameters p = (OAEI2011MatcherParameters) param;

		if (Core.DEBUG) {
			System.out.println("Running "
					+ m.getRegistryEntry().getMatcherShortName());
		}
		startime = System.nanoTime() / measure;

		if (m.isProgressDisplayed()) {
			m.getProgressDisplay().setProgressLabel(label);
		}
		// m.setProgressDisplay(getProgressDisplay());
		m.match();
		// m.setProgressDisplay(null);
		// if( m.isCancelled() )
		// {
		// cancel(true);
		// } // the user canceled the matching process

		endtime = System.nanoTime() / measure;
		time = (endtime - startime);
		if (Core.DEBUG) {
			System.out.println(m.getRegistryEntry().getMatcherShortName()
					+ " completed in (h.m.s.ms) "
					+ Utility.getFormattedTime(time));
		}

		// if(p.showIntermediateMatchers && !m.isCancelled())
		// {
		// Core.getUI().getControlPanel().getTablePanel().addMatcher(m);
		// }
	}
}
