package am.matcher.MachineLearning;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.core.Instances;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.ontology.Node;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.Combination.CombinationParameters;
import am.userInterface.MatchingProgressDisplay;

public class machineLearningMatcher extends AbstractMatcher {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 6298803128635729082L;
	ArrayList<AbstractMatcher> listOfMatchers = new ArrayList<AbstractMatcher>();
	ArrayList<OntologyTriple> listOfTriples = new ArrayList<OntologyTriple>();
	ArrayList<String> matcherNames = new ArrayList<String>();
	Alignment<Mapping> testMapping;
	Alignment<Mapping> finalMapping;
	Alignment<Mapping> combinedMapping;
	double wekaConfidence=0.6;
	String modelname="mlroot/model/4/basesimilarity";
	String testFolder = "mlroot/test/matchers";
	Modes mode=Modes.BASE_MODE;
	@Override
	protected void beforeAlignOperations() throws Exception{
		super.beforeAlignOperations();
		cleanup(testFolder);
		ArrayList<String> files = new ArrayList<String>();
		getFilesFromFolder(files, testFolder);
		for(String str: files)
		{
			System.out.println("after cleanup" + str);
		}
		int size = inputMatchers.size();
		listOfMatchers.clear();
		listOfMatchers=(ArrayList<AbstractMatcher>) inputMatchers;
		
		System.out.println("mlm:"+listOfMatchers.size());
		OntologyTriple ot = new OntologyTriple(sourceOntology,
				targetOntology);
		ot.setListOfMatchers(listOfMatchers);
		this.listOfTriples.add(ot);
		System.out.println(listOfTriples.size()+" size of triples\n");
		testMapping=new Alignment<Mapping>(sourceOntology.getID(),targetOntology.getID());
		finalMapping=new Alignment<Mapping>(sourceOntology.getID(),targetOntology.getID());
		combinedMapping=new Alignment<Mapping>(sourceOntology.getID(),targetOntology.getID());
		machineLearningParameters mp = (machineLearningParameters)param;
		wekaConfidence=mp.threshold;
		if(mp.featureType.equals(machineLearningParameters.Matsim))
		{
			mode=Modes.BASE_MODE;
			if(size==2)
			{
				modelname="mlroot/model/2/basesimilarity/C_NaiveBayes";
			}
			if(size==3)
			{
				modelname="mlroot/model/3/basesimilarity/C_NaiveBayes";
			}
			if(size==4)
			{
				modelname="mlroot/model/4/basesimilarity/C_NaiveBayes";
			}
		}
		if(mp.featureType.equals(machineLearningParameters.Matfound))
		{
			mode=Modes.BASE_MODE_MATCHER_FOUND;
			if(size==2)
			{
				modelname="mlroot/model/2/basesimilaritymatcherfound/C_NaiveBayes";
			}
			if(size==3)
			{
				modelname="mlroot/model/3/basesimilaritymatcherfound/C_NaiveBayes";
			}
			if(size==4)
			{
				modelname="mlroot/model/4/basesimilaritymatcherfound/C_NaiveBayes";
			}
		}
		if(mp.featureType.equals(machineLearningParameters.Matvote))
		{
			mode=Modes.BASE_MODE_MATCHER_VOTE_MATCHER_FOUND;
			if(size==2)
			{
				modelname="mlroot/model/2/basesimilaritymatcherfoundmatchervote/C_NaiveBayes";
			}
			if(size==3)
			{
				modelname="mlroot/model/3/basesimilaritymatcherfoundmatchervote/C_NaiveBayes";
			}
			if(size==4)
			{
				System.out.println("inside 4");
				modelname="mlroot/model/4/basesimilaritymatcherfoundmatchervote/C_NaiveBayes";
			}
		}
		System.out.println("mode used" + modelname);
	}

	
	
	public machineLearningMatcher() {
		super();
		needsParam = true; // need the parameters
		//I can't initialize the parametersPanel in here because i need to pass the inputmatchers as parameters 
		// but the input matchers will be set later so I will initialize the panel in the getParametersPanel() method
	}
	
	public machineLearningMatcher( machineLearningParameters param_new ) {
		super(param_new);
	}
	
	@Override
	protected void initializeVariables() {
		super.initializeVariables();
		minInputMatchers = 2;
		maxInputMatchers = ANY_INT;
	}

	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new machineLearningParametersPanel(inputMatchers);
		}
		return parametersPanel;
	}
    @Override
    public void match() throws Exception {
    	
    	// setup the Ontologies
    	if( sourceOntology == null ) {
    		if( Core.getInstance().getSourceOntology() == null ) {
    			// no source ontology defined or loaded
    			throw new Exception("No source ontology is loaded!");
    		} else {
    			// the source Ontology is not defined, but a Source ontology is loaded in the Core. Use that.
    			sourceOntology = Core.getInstance().getSourceOntology();
    		}
    	}
    	
    	if( targetOntology == null ) {
    		if( Core.getInstance().getTargetOntology() == null ) {
    			// no target ontology defined or loaded
    			throw new Exception("No target ontology is loaded!");
    		} else {
    			// the target Ontology is not defined as part of this matcher, but a Target ontology is loaded in the Core.  Use that.
    			targetOntology = Core.getInstance().getTargetOntology();
    		}
    	}
    	
    	matchStart();
    	buildSimilarityMatrices(); // align()
    	/*if(performSelection && !this.isCancelled() ){
        	select();	
    	}*/
    	matchEnd();
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
    }

    public void buildSimilarityMatrices()throws Exception{
    	beforeAlignOperations();
    	align();
    	
    }
	
    protected void align() throws Exception {
    	
		System.out.println("inside align");
		callTestProcess(mode);
	

	}

    //Time calculation, if you override this method remember to call super.afterSelectionOperations()
    public void matchStart() {
    	if( isProgressDisplayed() ) {
    		setupProgress();  // if we are using the progress dialog, setup the variables
    		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.matchingStarted(this);
    	}
    	start = System.nanoTime();
    	starttime = System.currentTimeMillis();
    	
	}
    //Time calculation, if you override this method remember to call super.afterSelectionOperations()
	public void matchEnd() {
		// TODO: Need to make sure this timing is correct.  - Cosmin ( Dec 17th, 2008 )
		end = System.nanoTime();
    	executionTime = (end-start)/1000000; // this time is in milliseconds.
	    setSuccesfullReport();	
		if( isProgressDisplayed() ) {
			allStepsDone();
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.clearReport();
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.matchingComplete();
		}
    	
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

	public void cleanup(String folder) {
		ArrayList<String> files = new ArrayList<String>();

		getFilesFromFolder(files, folder);
		for (String str : files) {
			File filename = new File(str);
			System.out.println("file deleted" + filename.getName());
			filename.delete();
		}

	}
	/**
	 * run the classifier on the testset
	 * @param mode
	 */
	void callTestProcess(Modes mode) {
		
		try {
			// predicting the result for the testset using naivebayes
			// classifier
		 System.out.println("using naive bayes classifier");
		 //String modelname="mlroot/model/C_NaiveBayes";
		 predictresult(modelname);
		 } catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	
	void generateTestFile(Modes mode) throws Exception {
		//cleanup(testFolder);
		for (int m = 0; m < this.listOfMatchers.size(); m++) {
			
			AbstractMatcher currentMatcher = this.listOfMatchers.get(m);
			System.out.println("inside generate test file" +currentMatcher.getName());
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

		}
		mergeIndividualTestFiles(mode);
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
			System.out.println("inside merge" + matcherName);
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
			inputReader.close();

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

	void generateMappings() {

		
		for (int t = 0; t < listOfTriples.size(); t++) {
			
			OntologyTriple currentTriple = listOfTriples.get(t);
			ArrayList<AbstractMatcher> matchers = currentTriple
					.getListOfMatchers();
			for (int m = 0; m < matchers.size(); m++) {

				try {
					
					AbstractMatcher currentMatcher = matchers.get(m);
					System.out.println("hello" +currentMatcher.getName());
					Alignment<Mapping> resultAlignment;
					resultAlignment = currentMatcher.getAlignment();
					currentTriple.setAlignmentObtained(currentMatcher.getName(), resultAlignment);
					
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}
	}


	/*
	 * main module to predict results for test setgiven two ontologies,reference
	 * alignment and a ML model
	 */
	void predictresult(String modelName)
			throws Exception {
		// generating the test.xml file needed by MLTestingWrapper
		String outputFileName = "mlroot/output/test.xml";
		String predicted="mlroot/test/predicted";
		String combinedConceptFile="mlroot/test/testrefFilecombined";
		String finalFile="mlroot/test/output";
		ArrayList<Double> predictedList = new ArrayList<Double>();
		ArrayList<Double> confidenceList = new ArrayList<Double>();
		
		// running the matchers on testset

		generateMappings();	
		generateTestFile(mode);	
		

		// deserialising the model we have built
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				modelName));
		Classifier cls = (Classifier) ois.readObject();

		// generating arff file and setting testset class
		ArffConvertor arff = new ArffConvertor("mlroot/test/testFilecombined","test",matcherNames,mode);

		arff.generateArffFile("mlroot/test/arff/testFilecombined.arff");
		BufferedReader testset = new BufferedReader(new FileReader(
				"mlroot/test/arff/testFilecombined.arff"));
		Instances test = new Instances(testset);
		test.setClassIndex(test.numAttributes() - 1);

		// predict the result using given model

		for (int i = 0; i < test.numInstances(); i++) {
			double clsLabel = cls.classifyInstance(test.instance(i));
			double[] prob=cls.distributionForInstance(test.instance(i));
			
			test.instance(i).setClassValue(clsLabel);
			{
				
				predictedList.add(clsLabel);
				if(clsLabel==0.0)
				{
					confidenceList.add(prob[0]);
				}
				else
				{
					confidenceList.add(prob[1]);
				}
			}
			

		}
			System.out.println(confidenceList.size()+"\tconfidencelist size");
			System.out.println(predictedList.size()+"\tpredictedlist size");
			// save the predicted data
			BufferedWriter writer = new BufferedWriter(new FileWriter(predicted));
			writer.write(test.toString());
			writer.newLine();
			writer.flush();
			writer.close();
			ois.close();
			
		//time to run lwc
			AbstractMatcher lwc = null;
			
			lwc = MatcherFactory
					.getMatcherInstance(CombinationMatcher.class);

			lwc.setInputMatchers(listOfMatchers);

			CombinationParameters lwcParam = new CombinationParameters(
					param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			setupSubMatcher(lwc, lwcParam);
			lwc.match();
			
			
		// Now we have the predicted value
		// generate a single file which has the correspondences and predicted
		// value
		// run it against the reference alignment
		// to compute the precision,recall and f-measure
	
			Alignment<Mapping> lwcAlignment=lwc.getAlignment();
			System.out.println("lwc"+lwcAlignment.size());
			System.out.println("final"+testMapping.size());
			matchReference(predictedList, confidenceList,combinedConceptFile, finalFile);
			generateAlignment(finalFile,mode,lwcAlignment);
			//finalMapping.addAllNoDuplicate(lwcAlignment);
			System.out.println("final"+finalMapping.size());
			
		
		
		
	
			
		
	}

	private void setupSubMatcher(AbstractMatcher m, DefaultMatcherParameters p) {
		setupSubMatcher(m, p, true);
	}

	private void setupSubMatcher(AbstractMatcher m, DefaultMatcherParameters p,
			boolean progressDelay) {
		m.setParam(p);
		m.setSourceOntology(sourceOntology);
		m.setTargetOntology(targetOntology);
		for( MatchingProgressDisplay mpd : progressDisplays ) m.addProgressDisplay(mpd);
		m.setUseProgressDelay(progressDelay);
		m.setPerformSelection(true);
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
	void matchReference(ArrayList<Double> predictedList,ArrayList<Double> confidenceList,
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
				if (inputLineParts.length == 3) 
				{

					String concepts = inputLineParts[0] + "\t" + inputLineParts[1];
					outputWriter.write(concepts + "\t" + predictedList.get(index) + "\t" + confidenceList.get(index)
						+ "\n");
				
				
				}
			}
			
			index++;
			if(index==predictedList.size())
			{
				break;
			}
			
		}
		outputWriter.close();

	}

 
	
	void generateAlignment(String finalFile,Modes mode,Alignment<Mapping> lwcAlign) throws IOException
	{

		BufferedReader mappingFile = new BufferedReader(new FileReader(
				finalFile));
		while (mappingFile.ready()) {
			String inputLine = mappingFile.readLine();
			String[] inputLineParts = inputLine.split("\t");
			double confidence;
			double predicted;
			
			for(Mapping currentMapping:testMapping)
			{
		
				if ((currentMapping.getEntity1().getUri()
						.equals(inputLineParts[0].trim())
						&& currentMapping.getEntity2().getUri()
								.equals(inputLineParts[1].trim()))
						|| (currentMapping.getEntity2().getUri()
								.equals(inputLineParts[0])
								&& currentMapping.getEntity1().getUri()
										.equals(inputLineParts[1]) ))
				{
					
					//System.out.println("confidence" + inputLineParts[3]);
					confidence=Double.parseDouble(inputLineParts[3]);
					
					predicted=Double.parseDouble(inputLineParts[2]);
					if(predicted==1.0)
					{
						if(confidence>=wekaConfidence)
						{
							currentMapping.setSimilarity(predicted);
							currentMapping.setRelation(relation.EQUIVALENCE);
							finalMapping.add(currentMapping);
						}
						else
						{
							Node sourceNode=currentMapping.getEntity1();
							Node targetNode=currentMapping.getEntity2();
							if(lwcAlign.isMapped(targetNode)&& lwcAlign.isMapped(sourceNode))
							{
								double lwcSim=lwcAlign.getSimilarity(sourceNode, targetNode);
								currentMapping.setSimilarity(lwcSim);
								finalMapping.add(currentMapping);	
							}
						}	
					}
					
					break;
					
				}
				
			}
			
		  }
		
		mappingFile.close();
		
	}
	
	
	void generateAlignment1(String finalFile,Modes mode,Alignment<Mapping> lwcAlign) throws IOException
	{

		BufferedReader mappingFile = new BufferedReader(new FileReader(
				finalFile));
		boolean isexists=false;
		while (mappingFile.ready()) {
			String inputLine = mappingFile.readLine();
			String[] inputLineParts = inputLine.split("\t");
			double confidence;
			double predicted;
			
			for(Mapping currentMapping:testMapping)
			{
		
				if ((currentMapping.getEntity1().getUri()
						.equals(inputLineParts[0].trim())
						&& currentMapping.getEntity2().getUri()
								.equals(inputLineParts[1].trim()))
						|| (currentMapping.getEntity2().getUri()
								.equals(inputLineParts[0])
								&& currentMapping.getEntity1().getUri()
										.equals(inputLineParts[1]) ))
				{
					
					//System.out.println("confidence" + inputLineParts[3]);
					confidence=Double.parseDouble(inputLineParts[3]);
					
					predicted=Double.parseDouble(inputLineParts[2]);
					if(predicted==1.0)
					{
						
						if(confidence>=wekaConfidence)
						{
							isexists=false;
							for(Mapping lwcMapping:lwcAlign)
							{
								if ((currentMapping.getEntity1().getUri()
										.equals(lwcMapping.getEntity1().getUri())
										&& currentMapping.getEntity2().getUri()
												.equals(lwcMapping.getEntity2().getUri()))
										|| (currentMapping.getEntity2().getUri()
												.equals(lwcMapping.getEntity1().getUri()))
												&& currentMapping.getEntity1().getUri()
														.equals(lwcMapping.getEntity2().getUri()))
								{
									isexists=true;
								}
							}
							if(!isexists)
							{
								System.out.println("new mapping added");
								currentMapping.setSimilarity(predicted);
								currentMapping.setRelation(relation.EQUIVALENCE);
								finalMapping.add(currentMapping);
							}
							
						}
					}
					
					break;
					
				}
				
			}
			
		  }
		
		mappingFile.close();
		
	}

/*	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new CombinationParametersPanel(inputMatchers);
		}
		return parametersPanel;
	}
	
*/	@Override
	public String getDescriptionString() {
		String result = "The Machine Learning Matcher is a combination matcher" +
				"\nwhich needs minimum of 2 input matchers and can work with upto four input matchers" +
				"\nIt makes use of machine learning approach and has the following feature vector" +
				"\n1.Matcher Similarity- uses the similarity value from input matchers" +
				"\n2.Matcher Similarity and Matcher found- uses the similarity value from input matchers and also whether " +
				"\nthe correspondences was found by the input matcher" +
				"\n3.Matcher Similarity,Matcher found , Matcher vote- uses the similarity values \nfrom input matchers,whether the matcher" +
				"\nfound the given correspondences,and no of matchers which was able to find the given correspondences";
		
		return result;
	}

   @Override
   public Alignment<Mapping> getAlignment() {
	   System.out.println("reference evaluation for mlm");
   return finalMapping;
   }
   
}
