package am.app.mappingEngine.matchersCombinationML;

/**
 * Wrapper class that calls the entire Machine learning process of matching
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.DecisionStump;
import weka.core.Instances;
import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.ontology.profiling.classification.ClassificatorRegistry;
import am.app.ontology.profiling.classification.OntologyClassificator;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.manual.ManualProfilerMatchingParameters;

import com.hp.hpl.jena.rdf.model.Property;

public class MLWrapper extends AbstractMatcher{

	ArrayList<AbstractMatcher> listOfMatchers = new ArrayList<AbstractMatcher>();
	ArrayList<OntologyTriple> listOfTriples = new ArrayList<OntologyTriple>();
	ArrayList<String> matcherNames = new ArrayList<String>();
	Alignment<Mapping> testMapping;
	Alignment<Mapping> finalMapping;
	Logger log;

	/**
	 * load the ontology given the location of ontology
	 * 
	 * @param ontoName
	 *            string given the path of the ontology file
	 * @return ontology object
	 */
	static Ontology loadOntology(String ontoName) {
		Ontology ontology;

		try {
			ontology = OntoTreeBuilder.loadOWLOntology(ontoName);
		} catch (Exception e) {
			System.out.println("Failed To open the ontology!");
			e.printStackTrace();
			return null;
		}
		return ontology;
	}

	/**
	 * load the matchers(PSM,BSM,VMM) into ArrayList listOfMatchers for later
	 * use set the parameters specific to the matchers
	 * 
	 * @throws Exception
	 */
//	void loadMatchers(Modes mode) throws Exception {
//		// TODO : look at oaei2011 and look how to get matchers and add to list
//		// below
//		// listOfMatchers.add();
//		// try with these matchers da
//		AbstractMatcher am = null;
//		am = MatcherFactory.getMatcherInstance(
//				MatchersRegistry.ParametricString, 0);
//		ParametricStringParameters psmParam = new ParametricStringParameters(
//				0.6, 1, 1);
//		psmParam.useLexicons = true;
//		psmParam.useBestLexSimilarity = true;
//		psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
//		psmParam.normParameter = new NormalizerParameter();
//		psmParam.normParameter.setForOAEI2009();
//		psmParam.redistributeWeights = true;
//		psmParam.threadedExecution = true;
//		psmParam.threadedOverlap = true;
//		am.setParam(psmParam);
//		listOfMatchers.add(am);
//		am = MatcherFactory.getMatcherInstance(MatchersRegistry.BaseSimilarity,
//				0);
//		BaseSimilarityParameters bsmParam = new BaseSimilarityParameters(0.6,
//				1, 1);
//		bsmParam.useDictionary = false;
//		am.setParam(bsmParam);
//		listOfMatchers.add(am);
//		am = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 0);
//		MultiWordsParameters vmmParam = new MultiWordsParameters(0.6, 1, 1);
//
//		vmmParam.measure = MultiWordsParameters.TFIDF;
//		// only on concepts right now because it should be weighted differently
//		vmmParam.considerInstances = true;
//		vmmParam.considerNeighbors = false;
//		vmmParam.considerConcept = true;
//		vmmParam.considerClasses = false;
//		vmmParam.considerProperties = false;
//		vmmParam.ignoreLocalNames = true;
//		vmmParam.useLexiconSynonyms = true; // May change later.
//		am.setParam(vmmParam);
//		listOfMatchers.add(am);
//		log.info(mode);
//		if (mode == Modes.BASE_MODE_LWC
//				|| mode == Modes.BASE_MODE_MATCHER_FOUND_LWC
//				|| mode == Modes.BASE_MODE_MATCHER_VOTE_LWC
//				|| mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND_LWC) {
//			log.info("mode" + mode);
//			am = MatcherFactory.getMatcherInstance(
//					MatchersRegistry.Combination, 0);
//			listOfMatchers.add(am);
//		}
//
//		// AbstractMatcher
//		// bsm=MatcherFactory.getMatcherInstance(MatchersRegistry.Equals, 0);
//
//	}

	/**
	 * Load the training.xml file which contains information about the source
	 * ontology path,target ontology path,reference alignment
	 * 
	 * @param fileName
	 * @param elementname
	 * @throws Exception
	 */

	void loadOntologyTriples(String fileName, String elementname,boolean refalign)
			throws Exception {
		// in linux RDF is rdf so had to put toLowerCase()
		// TODO: load the list of training ontologies with reference alignments

		XmlParser xp = new XmlParser();
		String basePath = "";
		String actualFilePath = "";
		if (!fileName.matches(".home.*")) {
			basePath = "/home/vivek/projects/workspace/AgreementMakerSVN/";
			actualFilePath = basePath;
		}

		// String basePath="";
	
		// actualFilePath+=fileName;
		ArrayList<TrainingLayout> tlist = xp.parseDocument(fileName,
				elementname, "training");
		for (TrainingLayout tl : tlist) {
			Ontology sourceOntology = loadOntology(tl.getsourceOntologyPath());
			Ontology targetOntology = loadOntology(tl.gettargetOntologyPath());
			if(refalign)
			{
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = tl.getrefAlignmentPath();
			System.out.println( tl.getrefAlignmentPath());
			refParam.format = ReferenceAlignmentMatcher.OAEI;
			AbstractMatcher referenceAlignmentMatcher = MatcherFactory
					.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
			referenceAlignmentMatcher.setParam(refParam);
			referenceAlignmentMatcher.setSourceOntology(sourceOntology);
			referenceAlignmentMatcher.setTargetOntology(targetOntology);
			referenceAlignmentMatcher.match();
			Alignment<Mapping> refmap = referenceAlignmentMatcher
					.getAlignment();
			OntologyTriple ot = new OntologyTriple(sourceOntology,
					targetOntology, refmap);
			ot.setListOfMatchers(listOfMatchers);
			listOfTriples.add(ot);
			}
			else
			{
				OntologyTriple ot = new OntologyTriple(sourceOntology,
						targetOntology);
				ot.setListOfMatchers(listOfMatchers);
				listOfTriples.add(ot);
				testMapping=new Alignment<Mapping>(sourceOntology.getID(),targetOntology.getID());
				finalMapping=new Alignment<Mapping>(sourceOntology.getID(),targetOntology.getID());
			}
			
			
		}
	}

	/**
	 * Run each of the matchers on the set of ontologies loaded from test.xml
	 * file and save the alignment for later use
	 */
	void generateMappings() {

		for (int t = 0; t < listOfTriples.size(); t++) {
			OntologyTriple currentTriple = listOfTriples.get(t);
			ArrayList<AbstractMatcher> matchers = currentTriple
					.getListOfMatchers();
			for (int m = 0; m < matchers.size(); m++) {

				try {
					// Build the lexicons.
					LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
					lexParam.sourceOntology = currentTriple.getOntology1();
					lexParam.targetOntology = currentTriple.getOntology2();

					lexParam.sourceUseLocalname = true;
					lexParam.targetUseLocalname = false;
					lexParam.sourceUseSCSLexicon = false;
					lexParam.targetUseSCSLexicon = false;

					lexParam.detectStandardProperties(currentTriple
							.getOntology1());
					lexParam.detectStandardProperties(currentTriple
							.getOntology2());

					Core.getLexiconStore().buildAll(lexParam);

					// Ontology profiling
					ProfilerRegistry entry = ProfilerRegistry.ManualProfiler;
					OntologyProfiler profiler = null;
					Constructor<? extends OntologyProfiler> constructor = null;

					constructor = entry.getProfilerClass().getConstructor(
							Ontology.class, Ontology.class);
					profiler = constructor.newInstance(
							currentTriple.getOntology1(),
							currentTriple.getOntology2());

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
						if (currentProperty.getLocalName().toLowerCase()
								.contains("label")) {
							profilingMatchingParams.sourceClassAnnotations
									.add(currentProperty);
						}
					}

					profilingMatchingParams.sourcePropertyAnnotations = new ArrayList<Property>();
					for (Property currentProperty : manualProfiler
							.getSourcePropertyAnnotations()) {
						if (currentProperty.getLocalName().toLowerCase()
								.contains("label")) {
							profilingMatchingParams.sourcePropertyAnnotations
									.add(currentProperty);
						}
					}

					profilingMatchingParams.targetClassAnnotations = new ArrayList<Property>();
					for (Property currentProperty : manualProfiler
							.getTargetClassAnnotations()) {
						if (currentProperty.getLocalName().toLowerCase()
								.contains("label")) {
							profilingMatchingParams.targetClassAnnotations
									.add(currentProperty);
						}
					}

					profilingMatchingParams.targetPropertyAnnotations = new ArrayList<Property>();
					for (Property currentProperty : manualProfiler
							.getTargetPropertyAnnotations()) {
						if (currentProperty.getLocalName().toLowerCase()
								.contains("label")) {
							profilingMatchingParams.targetPropertyAnnotations
									.add(currentProperty);
						}
					}

					manualProfiler.setMatchTimeParams(profilingMatchingParams);

		
					AbstractMatcher currentMatcher = matchers.get(m);
					// log.info(currentMatcher.getName());
					Alignment<Mapping> resultAlignment;
					if (currentMatcher.getName().toLowerCase()
							.contains("linear")) {
						//log.info("lwc included");
						LWCRunner runner = new LWCRunner();

						runner.setSourceOntology(currentTriple.getOntology1());
						runner.setTargetOntology(currentTriple.getOntology2());
						currentMatcher = runner.initializeLWC();
						currentMatcher.match();
						resultAlignment = currentMatcher.getAlignment();

					} else {
						currentMatcher.setOntologies(
								currentTriple.getOntology1(),
								currentTriple.getOntology2());
						currentMatcher.setPerformSelection(true);
						currentMatcher.setUseProgressDelay(false);
						currentMatcher.match();
						resultAlignment = currentMatcher.getAlignment();
					}

					if (resultAlignment != null && currentMatcher != null) {
						if (!currentTriple.containsMatcher(currentMatcher
								.getName())) {
							currentTriple.setAlignmentObtained(
									currentMatcher.getName(), resultAlignment);
						}

					}

				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * By now,We have the individual alignments from each matcher We generate a
	 * single test file which contains all the correspondences along with the
	 * similarity value from each individual matcher
	 * 
	 * @throws Exception
	 */
	void generateTrainingFile(Modes mode, String path) throws Exception {
		// ArrayList<String> mappedSourceTarget=new ArrayList<String>();
		// String[] trainingFiles={"psm","bsm","vmm"};
		for (int m = 0; m < listOfMatchers.size(); m++) {

			AbstractMatcher currentMatcher = listOfMatchers.get(m);
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(
					new File(path + "matchers/training/"
							+ currentMatcher.getName())));
			if (currentMatcher != null) {
				for (int t = 0; t < listOfTriples.size(); t++) {

					OntologyTriple currentTriple = listOfTriples.get(t);
					Alignment<Mapping> referenceAlignment = currentTriple
							.getReferenceAlignment();

					if (currentTriple.containsMatcher(currentMatcher.getName())) {

						Alignment<Mapping> currentMapping = currentTriple
								.getAlignmentObtained(currentMatcher.getName());
						if (currentMapping != null) {

							boolean mapped = false;
							for (int i = 0; i < currentMapping.size(); i++) {
								double similarity = currentMapping
										.getSimilarity(currentMapping.get(i)
												.getEntity1(), currentMapping
												.get(i).getEntity2());
								mapped = false;
								for (int j = 0; j < referenceAlignment.size(); j++) {

									// if(currentMapping.get(i).getString(true).equals(referenceAlignment.get(j).getString(true)))
									if ((currentMapping
											.get(i)
											.getEntity1()
											.getUri()
											.equals(referenceAlignment.get(j)
													.getEntity1().getUri()) && currentMapping
											.get(i)
											.getEntity2()
											.getUri()
											.equals(referenceAlignment.get(j)
													.getEntity2().getUri()))
											|| (currentMapping
													.get(i)
													.getEntity1()
													.getUri()
													.equals(referenceAlignment
															.get(j)
															.getEntity2()
															.getUri()) && currentMapping
													.get(i)
													.getEntity2()
													.getUri()
													.equals(referenceAlignment
															.get(j)
															.getEntity1()
															.getUri()))) {
										//log.info(similarity + "\t1.0");
										// System.out.println("mapped");

										// outputWriter.write(currentMapping.get(i).getEntity1().getUri()+"\t"+currentMapping.get(i).getEntity2().getUri()+"\t1.0\t1.0\n");
										outputWriter
												.write(currentMapping.get(i)
														.getEntity1().getUri()
														+ "\t"
														+ currentMapping.get(i)
																.getEntity2()
																.getUri()
														+ "\t"
														+ similarity
														+ "\t1.0\n");
										mapped = true;
										break;

									}
								}
								if (!mapped) {
									// System.out.println("matcher mapping wrong");
									outputWriter.write(currentMapping.get(i)
											.getEntity1().getUri()
											+ "\t"
											+ currentMapping.get(i)
													.getEntity2().getUri()
											+ "\t" + similarity + "\t0.0\n");
								}
							}

						}
					}
				}
			}
			outputWriter.close();
		}
		mergeIndividualTrainingFiles(mode, path);

	}

	/**
	 * Module invoked from generateTestFile() which actually generates the
	 * combined testfile 2 files are generated outputfiles file 1- name -
	 * trainingFilecombined- contains only the similarity value from each
	 * individual matcher.This file is used as input to generate the training
	 * arff file needed for ML
	 * 
	 * @throws IOException
	 */

	void mergeIndividualTrainingFiles(Modes mode, String path)
			throws IOException {
		ArrayList<String> matcherFiles = new ArrayList<String>();
		getFilesFromFolder(matcherFiles, path + "matchers/training/");

		HashMap<String, HashMap> uniqueConcepts = new HashMap<String, HashMap>();

		for (int i = 0; i < matcherFiles.size(); i++) {
			File currentFile = new File(matcherFiles.get(i));
			String matcherName = currentFile.getName();
			// adding matcher name we need to generate ARFF file
			matcherNames.add(matcherName);

			BufferedReader inputReader = new BufferedReader(new FileReader(
					currentFile));
			while (inputReader.ready()) {
				String inputLine = inputReader.readLine();
				String[] inputLineParts = inputLine.split("\t");
				if (inputLineParts.length == 4) {

					String mapKey = inputLineParts[0] + "\t"
							+ inputLineParts[1];
					HashMap<String, String> matcherMap;
					if (uniqueConcepts.containsKey(mapKey)) {
						matcherMap = uniqueConcepts.get(mapKey);
						matcherMap.put(matcherName, inputLine);
					} else {
						matcherMap = new HashMap<String, String>();
						matcherMap.put(matcherName, inputLine);
					}
					uniqueConcepts.put(mapKey, matcherMap);
				}
			}

		}

		// writing the results into a file in the format we want

		Set<String> mapKeys = uniqueConcepts.keySet();
		Iterator<String> mapKeyIterator = mapKeys.iterator();

		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(
				new File(path + "combinedmatchers/trainingFilecombined")));

		while (mapKeyIterator.hasNext()) {
			String currentKey = mapKeyIterator.next();

			HashMap<String, String> matcherMap = uniqueConcepts.get(currentKey);
			String outputStr = "";
			String referenceSim = "0.0";
			String[] matcherSim = new String[matcherNames.size()];
			int numFound = 0;
			int totalMatchers = matcherFiles.size();
			for (int i = 0; i < matcherFiles.size(); i++) {
				File currentFile = new File(matcherFiles.get(i));
				String matcherName = currentFile.getName();
				float matcherFound = (float) 0;

				if (matcherMap.containsKey(matcherName)) {
					numFound++;
					// System.out.println(i);
					matcherSim[i] = matcherMap.get(matcherName).split("\t")[2];
					referenceSim = matcherMap.get(matcherName).split("\t")[3];
					matcherFound = (float) 1;
				} else {
					matcherFound = 0;
					matcherSim[i] = "0.0";
				}
				if (mode == Modes.BASE_MODE) {
					outputStr += matcherSim[i] + "\t";// prints out matcher
														// similarity value
				} else if (mode == Modes.BASE_MODE_LWC) {
					outputStr += matcherSim[i] + "\t";// prints out matcher
														// similarity value
				} else if (mode == Modes.BASE_MODE_MATCHER_FOUND) {
					outputStr += matcherSim[i] + "\t" + matcherFound + "\t";// prints
																			// out
																			// matcher
																			// similarity
																			// value
																			// \t
																			// matcher
																			// found
																			// or
																			// not
																			// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_FOUND_LWC) {
					outputStr += matcherSim[i] + "\t" + matcherFound + "\t";// prints
																			// out
																			// matcher
																			// similarity
																			// value
																			// \t
																			// matcher
																			// found
																			// or
																			// not
																			// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_VOTE) {
					outputStr += matcherSim[i] + "\t";// prints out matcher
														// similarity value \t
														// matcher found or not
														// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_LWC) {
					outputStr += matcherSim[i] + "\t";// prints out matcher
														// similarity value \t
														// matcher found or not
														// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND) {
					outputStr += matcherSim[i] + "\t" + matcherFound + "\t";// prints
																			// out
																			// matcher
																			// similarity
																			// value
																			// \t
																			// matcher
																			// found
																			// or
																			// not
																			// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND_LWC) {
					outputStr += matcherSim[i] + "\t" + matcherFound + "\t";// prints
																			// out
																			// matcher
																			// similarity
																			// value
																			// \t
																			// matcher
																			// found
																			// or
																			// not
																			// (0/1)
				}

			}

			if (mode == Modes.BASE_MODE) {

				outputStr += referenceSim;// adds the matcher vote value and the
											// reference similarity
			} else if (mode == Modes.BASE_MODE_LWC) {

				outputStr += referenceSim;// adds the matcher vote value and the
											// reference similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_FOUND) {

				outputStr += referenceSim;// adds the matcher vote value and the
											// reference similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_FOUND_LWC) {
				outputStr += referenceSim;// adds the matcher vote value and the
											// reference similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_VOTE) {
				float matcherVote = (float) numFound / totalMatchers;
				outputStr += matcherVote + "\t" + referenceSim;// adds the
																// matcher vote
																// value and the
																// reference
																// similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_LWC) {
				float matcherVote = (float) numFound / totalMatchers;
				outputStr += matcherVote + "\t" + referenceSim;// adds the
																// matcher vote
																// value and the
																// reference
																// similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND) {
				float matcherVote = (float) numFound / totalMatchers;
				outputStr += matcherVote + "\t" + referenceSim;// adds the
																// matcher vote
																// value and the
																// reference
																// similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND_LWC) {
				float matcherVote = (float) numFound / totalMatchers;
				outputStr += matcherVote + "\t" + referenceSim;// adds the
																// matcher vote
																// value and the
																// reference
																// similarity
			}

			outputWriter.write(outputStr + "\n");

		}

		outputWriter.close();

	}

	/**
	 * Module invoked from generateTestFile() which actually generates the
	 * combined testfile 2 files are generated outputfiles file 1- name -
	 * testFilecombined- contains only the similarity value from each individual
	 * matcher.This file is used as input to generate the test arff file needed
	 * for ML file 2- name- testreffilecombined-contains the
	 * correspondences(source uri,target uri),and whether the alignment is
	 * present in the reference alignment.This file is used to map the
	 * correspondences with the prediction made by the classifier later on.
	 * 
	 * @throws IOException
	 */
	void mergeIndividualTestFiles(Modes mode) throws IOException {
		ArrayList<String> matcherFiles = new ArrayList<String>();
		getFilesFromFolder(matcherFiles, "mlroot/test/matchers/");

		HashMap<String, HashMap> uniqueConcepts = new HashMap<String, HashMap>();

		for (int i = 0; i < matcherFiles.size(); i++) {
			File currentFile = new File(matcherFiles.get(i));
			String matcherName = currentFile.getName();

			// adding matcher name we need to generate ARFF file
			if (!matcherNames.contains(matcherName))
				matcherNames.add(matcherName);

			BufferedReader inputReader = new BufferedReader(new FileReader(
					currentFile));
			while (inputReader.ready()) {
				String inputLine = inputReader.readLine();
				String[] inputLineParts = inputLine.split("\t");
				if (inputLineParts.length == 4) {

					String mapKey = inputLineParts[0] + "\t"
							+ inputLineParts[1];
					HashMap<String, String> matcherMap;
					if (uniqueConcepts.containsKey(mapKey)) {
						matcherMap = uniqueConcepts.get(mapKey);
						matcherMap.put(matcherName, inputLine);
					} else {
						matcherMap = new HashMap<String, String>();
						matcherMap.put(matcherName, inputLine);
					}
					uniqueConcepts.put(mapKey, matcherMap);
				}
			}

		}

		// writing the results into a file in the format we want

		Set<String> mapKeys = uniqueConcepts.keySet();
		Iterator<String> mapKeyIterator = mapKeys.iterator();

		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(
				new File("mlroot/test/testFilecombined")));
		BufferedWriter outputRef = new BufferedWriter(new FileWriter(new File(
				"mlroot/test/testrefFilecombined")));
		while (mapKeyIterator.hasNext()) {
			String currentKey = mapKeyIterator.next();

			HashMap<String, String> matcherMap = uniqueConcepts.get(currentKey);
			String outputStr = "";
			String outputStr1 = "";
			outputStr1 = currentKey + "\t";
			String referenceSim = "0.0";
			int numFound = 0;
			int totalMatchers = matcherFiles.size();
			String[] matcherSim = new String[matcherFiles.size()];
			for (int i = 0; i < matcherFiles.size(); i++) {
				File currentFile = new File(matcherFiles.get(i));
				// System.out.println(matcherFiles.get(i));
				String matcherName = currentFile.getName();
				float matcherFound = 0;
				if (matcherMap.containsKey(matcherName)) {
					numFound++;
					matcherFound = 1;
					matcherSim[i] = matcherMap.get(matcherName).split("\t")[2];
					referenceSim = matcherMap.get(matcherName).split("\t")[3];

				} else {
					matcherFound = 0;
					matcherSim[i] = "0.0";
				}

				if (mode == Modes.BASE_MODE) {
					outputStr += matcherSim[i] + "\t";// prints out matcher
														// similarity value
				} else if (mode == Modes.BASE_MODE_LWC) {
					outputStr += matcherSim[i] + "\t";// prints out matcher
														// similarity value
				} else if (mode == Modes.BASE_MODE_MATCHER_FOUND) {
					outputStr += matcherSim[i] + "\t" + matcherFound + "\t";// prints
																			// out
																			// matcher
																			// similarity
																			// value
																			// \t
																			// matcher
																			// found
																			// or
																			// not
																			// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_FOUND_LWC) {
					outputStr += matcherSim[i] + "\t" + matcherFound + "\t";// prints
																			// out
																			// matcher
																			// similarity
																			// value
																			// \t
																			// matcher
																			// found
																			// or
																			// not
																			// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_VOTE) {
					outputStr += matcherSim[i] + "\t";// prints out matcher
														// similarity value \t
														// matcher found or not
														// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_LWC) {
					outputStr += matcherSim[i] + "\t";// prints out matcher
														// similarity value \t
														// matcher found or not
														// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND) {
					outputStr += matcherSim[i] + "\t" + matcherFound + "\t";// prints
																			// out
																			// matcher
																			// similarity
																			// value
																			// \t
																			// matcher
																			// found
																			// or
																			// not
																			// (0/1)
				} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND_LWC) {
					outputStr += matcherSim[i] + "\t" + matcherFound + "\t";// prints
																			// out
																			// matcher
																			// similarity
																			// value
																			// \t
																			// matcher
																			// found
																			// or
																			// not
																			// (0/1)
				}

			}

			if (mode == Modes.BASE_MODE_MATCHER_VOTE) {
				float matcherVote = (float) numFound / totalMatchers;
				outputStr += matcherVote + "\t" + referenceSim;// adds the
																// matcher vote
																// value and the
																// reference
																// similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_LWC) {
				float matcherVote = (float) numFound / totalMatchers;
				outputStr += matcherVote + "\t" + referenceSim;// adds the
																// matcher vote
																// value and the
																// reference
																// similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND) {
				float matcherVote = (float) numFound / totalMatchers;
				outputStr += matcherVote + "\t" + referenceSim;// adds the
																// matcher vote
																// value and the
																// reference
																// similarity
			} else if (mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND_LWC) {
				float matcherVote = (float) numFound / totalMatchers;
				outputStr += matcherVote + "\t" + referenceSim;// adds the
																// matcher vote
																// value and the
																// reference
																// similarity
			}

			outputStr1 += referenceSim;
			outputWriter.write(outputStr.trim() + "\n");
			outputRef.write(outputStr1.trim() + "\n");

		}

		outputWriter.close();
		outputRef.close();

	}

	/**
	 * To get all files inside a particular folder this module is invoked when
	 * we want to combine the alignment generated by individual matcher
	 * 
	 * @param files
	 * @param folder
	 */
	void getFilesFromFolder(ArrayList<String> files, String folder) {
		File file = new File(folder);

		if (file.isDirectory()) {
			File[] filesInDir = file.listFiles();
			if (!file.getName().contains("svn")) {
				for (int i = 0; i < filesInDir.length; i++) {
					getFilesFromFolder(files, filesInDir[i].getAbsolutePath());
				}
			}
		} else {
			if (!file.getName().equals("entries")) {
				files.add(file.getAbsolutePath());
			}

		}
	}

	/*
	 * main module to predict results for test setgiven two ontologies,reference
	 * alignment and a ML model
	 */
	void predictresult(String modelName, String srcOntology,
			String tarOntology, String refAlign, String predicted,
			String combinedConceptFile, String finalFile, Modes mode,boolean isFirstTime,boolean isSpecific)
			throws Exception {
		// generating the test.xml file needed by MLTestingWrapper
		String outputFileName = "mlroot/output/test.xml";
		ArrayList<Double> predictedList = new ArrayList<Double>();
		GenerateTrainingDS.generateXML(srcOntology, tarOntology, refAlign,
				outputFileName);

		// running the matchers on testset

		callProcess(outputFileName, "dataset", mode,isSpecific);

		// deserialising the model we have built
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				modelName));
		Classifier cls = (Classifier) ois.readObject();

		// generating arff file and setting testset class
		ArffConvertor arff = new ArffConvertor("mlroot/test/testFilecombined",
				"test", matcherNames, mode);

		arff.generateArffFile("mlroot/test/arff/testFilecombined.arff");
		BufferedReader testset = new BufferedReader(new FileReader(
				"mlroot/test/arff/testFilecombined.arff"));
		Instances test = new Instances(testset);
		test.setClassIndex(test.numAttributes() - 1);

		// predict the result using given model

		for (int i = 0; i < test.numInstances(); i++) {
			double clsLabel = cls.classifyInstance(test.instance(i));
			test.instance(i).setClassValue(clsLabel);
			predictedList.add(clsLabel);

		}
		// save the predicted data
		BufferedWriter writer = new BufferedWriter(new FileWriter(predicted));
		writer.write(test.toString());
		writer.newLine();
		writer.flush();
		writer.close();
		ois.close();

		// Now we have the predicted value
		// generate a single file which has the correspondences and predicted
		// value
		// run it against the reference alignment
		// to compute the precision,recall and f-measure
		matchReference(predictedList, combinedConceptFile, finalFile);
		Alignment<Mapping> refMap = getReference(srcOntology, tarOntology,
				refAlign);
	//	calculateMeasure(finalFile, refMap);
		generateAlignment(finalFile);
		System.out.println("test Alignment size" + finalMapping.size());
		displayResults(finalMapping, refMap);
		
			
		
		
		
		if(isSpecific)
		{
			// Now we have the predicted value
			// generate a single file which has the correspondences and predicted
			// value
			// run it against the reference alignment
			// to compute the precision,recall and f-measure
			matchReference(predictedList, combinedConceptFile, finalFile);
			refMap = getReference(srcOntology, tarOntology,
					refAlign);
			calculateMeasure(finalFile, refMap);
			
		}
		else
		{
			calculateGeneralMeasure(predictedList);
		}
		
		if(isFirstTime && isSpecific)
		{
			//LWC measure
			log.info("LWC version");
			LWCRunner runner=new LWCRunner();
			runner.setSourceOntology(loadOntology(srcOntology));
			runner.setTargetOntology(loadOntology(tarOntology));
			AbstractMatcher lwcMatcher=runner.initializeLWC();
			displayResults(lwcMatcher.getAlignment(), refMap);
			
		}
		
		
		
	}
	
	void calculateGeneralMeasure(ArrayList<Double> predictedList) throws NumberFormatException, IOException
	{
		BufferedReader inputReader=new BufferedReader(new FileReader(new File("mlroot/test/testrefFilecombined")));
		int index=0;
		int count=0;
		int mapped=0;
		int sizeOfRef=0;
		log.info(predictedList.size()+"size of predicted");
		while(inputReader.ready())
		{
			double referenceValue=Double.parseDouble(inputReader.readLine().trim());
			double predictedValue=predictedList.get(index);
//			log.info("predicted"+predictedValue);
//			log.info("reference"+referenceValue+"\n");
			
			if(predictedValue==1.0)
			{
				count++;
			}
			if(referenceValue == predictedValue && referenceValue==1.0)
			{
				mapped++;
			}
			if(referenceValue==1.0)
			{
				sizeOfRef++;
			}
			index++;
		}
		
		log.info("-------------------------------------------------------------");
		// System.out.println("total correct" + mapped);
		// System.out.println("total mapping" +count);
		log.info("total correct" + mapped);
		log.info("total mapping" + count);
		float precision = (float) mapped / count;
		float recall = (float) mapped / sizeOfRef;
		float fmeasure = 2 * precision * recall / (precision + recall);
		// System.out.print(precision + "\t" + recall + "\t" + fmeasure);
		log.info(precision + "\t" + recall + "\t" + fmeasure);
		// System.out.println("-----------------------------------------------------");
		log.info("-------------------------------------------------------------");

	}

	/**
	 * computing the f-measure for the testset
	 * 
	 * @param finalFile
	 * @param refMap
	 * @throws IOException
	 */
	void calculateMeasure(String finalFile, Alignment<Mapping> refMap)
			throws IOException {

		BufferedReader alignmentFile = new BufferedReader(new FileReader(
				finalFile));
		int mapped = 0, count = 0;
		// System.out.println("-----------------------------------");
		// System.out.println("reference size"+refmap.size());
		log.info("reference size" + refMap.size());
		while (alignmentFile.ready()) {

			String inputLine = alignmentFile.readLine();
			String[] inputLineParts = inputLine.split("\t");
			if (inputLineParts[2].equals("1.0")) {
				count++;
			}
			for (int i = 0; i < refMap.size(); i++) {
				Mapping currentMapping = refMap.get(i);
				if ((currentMapping.getEntity1().getUri()
						.equals(inputLineParts[0])
						&& currentMapping.getEntity2().getUri()
								.equals(inputLineParts[1]) && currentMapping
						.getSimilarity() == Double
						.parseDouble(inputLineParts[2]))
						|| (currentMapping.getEntity2().getUri()
								.equals(inputLineParts[0])
								&& currentMapping.getEntity1().getUri()
										.equals(inputLineParts[1]) && currentMapping
								.getSimilarity() == Double
								.parseDouble(inputLineParts[2]))) {
					// System.out.println("match found in ref alignment");
					mapped++;
				}
			}
		}
		// System.out.println("-----------------------------------------------");
		log.info("-------------------------------------------------------------");
		// System.out.println("total correct" + mapped);
		// System.out.println("total mapping" +count);
		log.info("total correct" + mapped);
		log.info("total mapping" + count);
		float precision = (float) mapped / count;
		float recall = (float) mapped / refMap.size();
		float fmeasure = 2 * precision * recall / (precision + recall);
		// System.out.print(precision + "\t" + recall + "\t" + fmeasure);
		log.info(precision + "\t" + recall + "\t" + fmeasure);
		// System.out.println("-----------------------------------------------------");
		log.info("-------------------------------------------------------------");

	}

	
	void generateAlignment(String finalFile) throws IOException
	{

		BufferedReader mappingFile = new BufferedReader(new FileReader(
				finalFile));
		while (mappingFile.ready()) {
			String inputLine = mappingFile.readLine();
			String[] inputLineParts = inputLine.split("\t");
			
		
			
			for(Mapping currentMapping:testMapping)
			{
		
				if ((currentMapping.getEntity1().getUri()
						.equals(inputLineParts[0])
						&& currentMapping.getEntity2().getUri()
								.equals(inputLineParts[1]))
						|| (currentMapping.getEntity2().getUri()
								.equals(inputLineParts[0])
								&& currentMapping.getEntity1().getUri()
										.equals(inputLineParts[1]) ))
				{
					currentMapping.setSimilarity(1.0);
					currentMapping.setRelation(relation.EQUIVALENCE);
					finalMapping.add(currentMapping);
					break;
					
				}
				
			}
			
		  }
		
		mappingFile.close();
		
	}
	
	/**
	 * TODO : description
	 * 
	 * @param srcOnto
	 * @param tarOnto
	 * @param refFile
	 * @return
	 * @throws Exception
	 */

	Alignment<Mapping> getReference(String srcOnto, String tarOnto,
			String refFile) throws Exception {
		Ontology sourceOntology = loadOntology(srcOnto);
		Ontology targetOntology = loadOntology(tarOnto);
		ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
		refParam.onlyEquivalence = true;
		refParam.fileName = refFile;
		refParam.format = ReferenceAlignmentMatcher.OAEI;
		AbstractMatcher referenceAlignmentMatcher = MatcherFactory
				.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
		referenceAlignmentMatcher.setParam(refParam);
		referenceAlignmentMatcher.setSourceOntology(sourceOntology);
		referenceAlignmentMatcher.setTargetOntology(targetOntology);
		referenceAlignmentMatcher.match();
		Alignment<Mapping> refMap = referenceAlignmentMatcher.getAlignment();
		return refMap;
	}

	/**
	 * generate a single file which has the correspondences and predicted value
	 * run it against the reference alignment
	 * 
	 * @param predictedList
	 * @param combinedConceptFile
	 * @param finalFile
	 * @throws IOException
	 */
	void matchReference(ArrayList<Double> predictedList,
			String combinedConceptFile, String finalFile) throws IOException {

		BufferedReader conceptFile = new BufferedReader(new FileReader(
				combinedConceptFile));
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(
				new File(finalFile)));
		int index = 0;
		while (conceptFile.ready()) {
			String inputLine = conceptFile.readLine();
			if(predictedList.get(index)==1.0)
			{
			
			String[] inputLineParts = inputLine.split("\t");
			if (inputLineParts.length == 3) {

				String concepts = inputLineParts[0] + "\t" + inputLineParts[1];
				outputWriter.write(concepts + "\t" + predictedList.get(index)
						+ "\n");
				
				
			}
			}
			
			index++;
			if(index==predictedList.size())
			{
				break;
			}
			
		}
		// System.out.println("final file generated:" + finalfile);
		log.info("final file generated:" + finalFile);
		outputWriter.close();

	}

	void callProcess(String trainingFileName, String elementName, Modes mode,boolean isSpecific)
			throws Exception {
	
		//loadMatchers(mode);
	
		loadOntologyTriples(trainingFileName, elementName,false);
		generateMappings();
		
		if(isSpecific)
		{
			
			generateTestFile(mode);	
		}
		else
		{
			generateGeneralTestFile();
		}
		

	
	}
	
	
	
	void generateGeneralTestFile() throws IOException
	{
		BufferedReader inputReader=new BufferedReader(new FileReader(new File("mlroot/percent/percentfiletest")));
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File("mlroot/test/testFilecombined")));
		BufferedWriter outputReferenceWriter=new BufferedWriter(new FileWriter(new File("mlroot/test/testrefFilecombined")));
		
		while(inputReader.ready())
		{
			String inputLine=inputReader.readLine().trim();
			if(!inputLine.equals(""))
			{
				String inputLineParts[]=inputLine.split("\t");
				String referenceValue=inputLineParts[inputLineParts.length-1];
				String outputTestString="";
				for(int i=0;i<inputLineParts.length-1;i++)
				{
					outputTestString+=inputLineParts[i]+"\t";
				}
				outputWriter.write(outputTestString.trim()+"\n");
				outputReferenceWriter.write(referenceValue.trim()+"\n");
			}
		}
		outputWriter.close();
		outputReferenceWriter.close();
	}

	void callTestProcess(Modes mode) {
	
		try {
			// predicting the result for the testset using decisiontree
			// classifier
		 ArrayList<String> modelFiles=new ArrayList<String>();
		 getFilesFromFolder(modelFiles, "mlroot/model");
		 boolean isFirstTime=false;
		 boolean isSpecific=true;
		 for(String modelname:modelFiles)
		 {
			 log.info("mode used "+mode);
			 log.info("using model"+modelname);
			 System.out.println(modelname);
			 File currentModel = new File(modelname);
				String model = currentModel.getName();
			log.info("101-301");
			predictresult(modelname,"mlroot/mltraining/bench/103/onto1.rdf",
					"mlroot/mltesting/bench/301/onto.rdf",
					"mlroot/mltesting/bench/301/refalign.rdf",
					"mlroot/test/predicted"+ model + ".arff",
					"mlroot/test/testrefFilecombined",
					"mlroot/test/finaloutput" + model, mode,isFirstTime,isSpecific);
			 			 
			// listOfMatchers.clear();
				listOfTriples.clear();
			//	matcherNames.clear();
				testMapping.clear();
				finalMapping.clear();
//			 log.info("101-302");
//			 predictresult(modelname,"mlroot/mltraining/bench/103/onto1.rdf",
//						"mlroot/mltesting/bench/302/onto.rdf",
//						"mlroot/mltesting/bench/302/refalign.rdf",
//						"mlroot/test/predicted"+ model + ".arff",
//						"mlroot/test/testrefFilecombined",
//						"mlroot/test/finaloutput" + model, mode,isFirstTime,isSpecific);
//			// listOfMatchers.clear();
//				listOfTriples.clear();
//				//matcherNames.clear();
//				testMapping.clear();
//				finalMapping.clear();
//			 log.info("101-303");
//			 predictresult(modelname,"mlroot/mltraining/bench/103/onto1.rdf",
//						"mlroot/mltesting/bench/303/onto.rdf",
//						"mlroot/mltesting/bench/303/refalign.rdf",
//						"mlroot/test/predicted"+ model + ".arff",
//						"mlroot/test/testrefFilecombined",
//						"mlroot/test/finaloutput" + model, mode,isFirstTime,isSpecific);
//			// listOfMatchers.clear();
//				listOfTriples.clear();
//				//matcherNames.clear();
//				testMapping.clear();
//				finalMapping.clear();
//			 log.info("edas-iasted");
//			  predictresult(modelname,"mlroot/mltesting/conference/edas-iasted/edas.owl",
//						"mlroot/mltesting/conference/edas-iasted/iasted.owl",
//						"mlroot/mltesting/conference/edas-iasted/refalign.rdf",
//						"mlroot/test/predicted"+ model + ".arff",
//						"mlroot/test/testrefFilecombined",
//						"mlroot/test/finaloutput" + model, mode,isFirstTime,isSpecific);
//			// listOfMatchers.clear();
//				listOfTriples.clear();
//			//	matcherNames.clear();
//				testMapping.clear();
//				finalMapping.clear();
//			 log.info("iasted-sigkdd");
//			 predictresult(modelname,"mlroot/mltesting/conference/iasted-sigkdd/iasted.owl",
//						"mlroot/mltesting/conference/iasted-sigkdd/sigkdd.owl",
//						"mlroot/mltesting/conference/iasted-sigkdd/refalign.rdf",
//						"mlroot/test/predicted"+ model + ".arff",
//						"mlroot/test/testrefFilecombined",
//						"mlroot/test/finaloutput" + model, mode,isFirstTime,isSpecific);
//			// listOfMatchers.clear();
//				listOfTriples.clear();
//			//	matcherNames.clear();
//				testMapping.clear();
//				finalMapping.clear();
//			 log.info("confOf-sigkdd");
//			 predictresult(modelname,"mlroot/mltesting/conference/confOf-sigkdd/confOf.owl",
//						"mlroot/mltesting/conference/confOf-sigkdd/sigkdd.owl",
//						"mlroot/mltesting/conference/confOf-sigkdd/refalign.rdf",
//						"mlroot/test/predicted"+ model + ".arff",
//						"mlroot/test/testrefFilecombined",
//						"mlroot/test/finaloutput" + model, mode,isFirstTime,isSpecific);
//			//	listOfMatchers.clear();
//				listOfTriples.clear();
//			//	matcherNames.clear();
//				isFirstTime=false;
//				testMapping.clear();
//				finalMapping.clear();
		 }
				} catch (Exception e) {

			e.printStackTrace();
		}
	}

	void displayResults(Alignment<Mapping> predictedMapping,
			Alignment<Mapping> referenceAlignment) {
		
		
		int count = 0, mapped = 0;
		for (int i = 0; i < predictedMapping.size(); i++) {
			Mapping predictedMap = predictedMapping.get(i);
			count++;
			for (int j = 0; j < referenceAlignment.size(); j++) {
				Mapping currentMapping = referenceAlignment.get(j);
				
						
				
				if (((currentMapping.getEntity1().getUri()
						.equals(predictedMap.getEntity1().getUri())
						&& currentMapping.getEntity2().getUri()
								.equals(predictedMap.getEntity2().getUri()))
					||
						(currentMapping.getEntity1().getUri()
								.equals(predictedMap.getEntity2().getUri())
							&& currentMapping.getEntity2().getUri()
									.equals(predictedMap.getEntity1().getUri())))
					&& currentMapping.getRelation().equals(
								predictedMap.getRelation())) {
					// System.out.println("match found in ref alignment");
					mapped++;
				}
			}
		}
		
		log.info("-------------------------------------------------------------");
		log.info("total correct" + mapped);
		log.info("total mapping" + count);
		float precision = (float) mapped / count;
		float recall = (float) mapped / referenceAlignment.size();
		float fmeasure = 2 * precision * recall / (precision + recall);
		log.info(precision + "\t" + recall + "\t" + fmeasure);
		log.info("-------------------------------------------------------------");

	}

	/**
	 * uses the file trainingfilecombined generated by generateTrainingfile() as
	 * input to ARFFConvertor which generates the training file in the format
	 * needed by ML.
	 * 
	 * @throws IOException
	 */
	void generateTrainingARFF(Modes mode) throws IOException {

		ArffConvertor arff = new ArffConvertor("mlroot/percent/percentfile",
				"training", matcherNames, mode);
		arff.generateArffFile("mlroot/percent/percentfile.arff");
		log.info("Training ARFF file generated");

	}

	/*
	 * Use the arff generated by generateTrainingARFF to build the model three
	 * classifiers - decision tree,naive bayes,svm model is saved for future use
	 */
	void generateModel() throws Exception {
		BufferedReader trainingset = new BufferedReader(new FileReader(
				"mlroot/percent/percentfile.arff"));
			
		Instances train = new Instances(trainingset);
		
		train.setClassIndex(train.numAttributes() - 1);
		// test.setClassIndex(test.numAttributes()-1);
		for (ClassificatorRegistry classifier : ClassificatorRegistry.values()) {
			Classifier cls=OntologyClassificator.getClassifierInstance(classifier);
			cls.buildClassifier(train);
			weka.core.SerializationHelper.write("mlroot/model/"+ classifier,cls);
		}
		trainingset.close();

		log.info("All models generated");

	}

	/**
	 * 
	 * @param mode
	 * @param trainingPercent
	 * @param inputPath
	 * @param outputPath
	 * @throws Exception
	 */
	void callTrainingProcess(Modes mode, double trainingPercent)
			throws Exception {
		// uncomment the below lines, if you want to generate a new model
		// ArrayList<TrainingLayout> trainingfs=new ArrayList<TrainingLayout>();
		log.info("training" + trainingPercent);
		String path[] = { "mlroot/mltraining/bench",
				"mlroot/mltraining/conference" };
				
		String outputBase[] = { "mlroot/output/bench/",
				"mlroot/output/conference/" };
		String trainingFileName = "mlroot/output/training.xml";
		for (int i = 0; i < outputBase.length; i++) {
			ArrayList<String> files = new ArrayList<String>();
			GenerateTrainingDS.getFilesFromFolder(files, path[i]);
			GenerateTrainingDS.generateXML(files, trainingFileName);
			// String trainingFileName="bench/training.xml";
			String elementName = "dataset";
			//listOfMatchers.clear();
			listOfTriples.clear();
			matcherNames.clear();
		//	loadMatchers(mode);
			loadOntologyTriples(trainingFileName, elementName,true);
			generateMappings();
			generateTrainingFile(mode, outputBase[i]);
		}
		generatePercentTraining(trainingPercent, outputBase);
		generateTrainingARFF(mode);
		generateModel();

	}

	// invoke this module if you generated the overall trainingset for
	// bench,conference,anatomy
	void percentTraining(Modes mode, Double trainingpercent) throws Exception {
		String outputbase[] = { "mlroot/output/bench/",
				"mlroot/output/conference/" };
		generatePercentTraining(trainingpercent, outputbase);
		generateTrainingARFF(mode);
		generateModel();
	}

	// use of percent of training to generate model
	void generatePercentTraining(double percent, String[] outputBase)
			throws IOException {
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(
				new File("mlroot/percent/percentfile")));
		BufferedWriter outputWriterForTest=new BufferedWriter(new FileWriter(new File("mlroot/percent/percentfiletest")));

		int count[] = new int[outputBase.length];
		
		//counts the number of lines in the file based on the %
		for (int i = 0; i < outputBase.length; i++) {
			LineNumberReader lnr = new LineNumberReader(new FileReader(
					new File(outputBase[i]
							+ "combinedmatchers/trainingFilecombined")));
			lnr.skip(Long.MAX_VALUE);
			System.out.println(lnr.getLineNumber());
			count[i] = (int) (lnr.getLineNumber() * percent);

		}
		
		//writes the % of lines into the percent file
		for (int i = 0; i < outputBase.length; i++) {
			BufferedReader inputReader = new BufferedReader(new FileReader(
					outputBase[i] + "combinedmatchers/trainingFilecombined"));
			
			log.info("count of rows" + count[i]);
			int lineNum = 0;
			while (inputReader.ready()) {
				String inputLine = inputReader.readLine();
				if(lineNum<=count[i])
				{
					outputWriter.write(inputLine + "\n");	
				}
				else
				{
					outputWriterForTest.write(inputLine+"\n");
				}
				
//				if (linenum == count[i])
//					break;
				lineNum++;
			}
			inputReader.close();
		}
		outputWriterForTest.close();
		outputWriter.close();

	}

	/**
	 * By now,We have the individual alignments from each matcher We generate a
	 * single test file which contains all the correspondences along with the
	 * similarity value from each individual matcher
	 * 
	 * @throws Exception
	 */
	void generateTestFile(Modes mode) throws Exception {
		for (int m = 0; m < listOfMatchers.size(); m++) {
			
			AbstractMatcher currentMatcher = listOfMatchers.get(m);
			BufferedWriter outputWriter = new BufferedWriter(
					new FileWriter(new File("mlroot/test/matchers/"
							+ currentMatcher.getName())));
			if (currentMatcher != null) {
				for (int t = 0; t < listOfTriples.size(); t++) {

					OntologyTriple currentTriple = listOfTriples.get(t);
				//	Alignment<Mapping> referenceAlignment = currentTriple
				//			.getReferenceAlignment();

					if (currentTriple.containsMatcher(currentMatcher.getName())) {
						Alignment<Mapping> currentMapping = currentTriple
								.getAlignmentObtained(currentMatcher.getName());
											for (int i = 0; i < currentMapping.size(); i++) {
								double similarity = currentMapping
										.getSimilarity(currentMapping.get(i)
												.getEntity1(), currentMapping
												.get(i).getEntity2());
										outputWriter
												.write(currentMapping.get(i)
														.getEntity1().getUri()
														+ "\t"
														+ currentMapping.get(i)
																.getEntity2()
																.getUri()
														+ "\t"
														+ similarity
														+ "\t1.0\n");
		
												
										
				}
			     testMapping.addAllNoDuplicate(currentMapping);		
			}
			outputWriter.close();
		}
		}
		mergeIndividualTestFiles(mode);
		}

	}

	public MLWrapper() {
		log = Logger.getLogger(MLWrapper.class);

		log.setLevel(Level.DEBUG);

	}

	public void cleanup(String folder) {
		ArrayList<String> files = new ArrayList<String>();

		getFilesFromFolder(files, folder);
		for (String str : files) {
			File filename = new File(str);
			filename.delete();
		}

	}
	
	void initializeMatchers(Modes mode) throws Exception
	{
//		AbstractMatcher am = null;
//		am = MatcherFactory.getMatcherInstance(
//				MatchersRegistry.ParametricString, 0);
//		ParametricStringParameters psmParam = new ParametricStringParameters(
//				0.6, 1, 1);
//		psmParam.useLexicons = true;
//		psmParam.useBestLexSimilarity = true;
//		psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
//		psmParam.normParameter = new NormalizerParameter();
//		psmParam.normParameter.setForOAEI2009();
//		psmParam.redistributeWeights = true;
//		psmParam.threadedExecution = true;
//		psmParam.threadedOverlap = true;
//		am.setParam(psmParam);
//		
//		listOfMatchers.add(am);
//		am = MatcherFactory.getMatcherInstance(MatchersRegistry.BaseSimilarity,
//				0);
//		BaseSimilarityParameters bsmParam = new BaseSimilarityParameters(0.6,
//				1, 1);
//		bsmParam.useDictionary = false;
//		am.setParam(bsmParam);
//		
//		listOfMatchers.add(am);
//		am = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 0);
//		MultiWordsParameters vmmParam = new MultiWordsParameters(0.6, 1, 1);
//
//		vmmParam.measure = MultiWordsParameters.TFIDF;
//		// only on concepts right now because it should be weighted differently
//		vmmParam.considerInstances = true;
//		vmmParam.considerNeighbors = false;
//		vmmParam.considerConcept = true;
//		vmmParam.considerClasses = false;
//		vmmParam.considerProperties = false;
//		vmmParam.ignoreLocalNames = true;
//		vmmParam.useLexiconSynonyms = true; // May change later.
//		am.setParam(vmmParam);
//		
//		listOfMatchers.add(am);
//		log.info(mode);
		
		//Initialize the LWC used when combining with MLM
		
//		if (mode == Modes.BASE_MODE_LWC
//				|| mode == Modes.BASE_MODE_MATCHER_FOUND_LWC
//				|| mode == Modes.BASE_MODE_MATCHER_VOTE_LWC
//				|| mode == Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND_LWC) 
//		{
//			log.info("mode" + mode);
//			am = MatcherFactory.getMatcherInstance(
//					MatchersRegistry.Combination, 0);
//			am.match();
//			listOfMatchers.add(am);
//		}
		
		AbstractMatcher am = MatcherFactory.getMatcherInstance(
		MatchersRegistry.Combination, 0);
		listOfMatchers.add(am);
		initializeMatcherNames();
	}
	
	void initializeMatcherNames()
	{
		matcherNames=new ArrayList<String>();
		for(int i=0;i<listOfMatchers.size();i++)
		{
			AbstractMatcher am=listOfMatchers.get(i);
			if(!matcherNames.contains(am.getName()))
			{
				matcherNames.add(am.getName());
			}
		}
	}

	public static void main(String args[]) throws IOException {

		MLWrapper trainingWrapper = new MLWrapper();
		MLWrapper testWrapper = new MLWrapper();
		Modes mode = Modes.BASE_MODE_LWC;
		String trainFolder = "mlroot/output";
		String testFolder = "mlroot/test/matchers";
		try {
			/*
			 * set the required matchers parameters
			 * invoke matcher.match()
			 * then add to listOfMatchers
			 */
			
			trainingWrapper.initializeMatchers(mode);
			testWrapper.initializeMatchers(mode);
			

			trainingWrapper.cleanup(trainFolder);
			trainingWrapper.callTrainingProcess(mode, 0.5);
			testWrapper.cleanup(testFolder);
			testWrapper.callTestProcess(mode);

			Date now = new Date();

			File sourceDir = new File(
					"mlroot");
			File targetDir = new File("log/" + now.getDate() + "."
					+ (now.getMonth() + 1) + "." + now.getHours() + "."
					+ now.getMinutes() + "." + mode.name());

			FileUtils.copyDirectoryToDirectory(sourceDir, targetDir);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}
