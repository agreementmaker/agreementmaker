package am.app.mappingEngine.matchersCombinationML;

/**
 * The wrapper that runs the the steps for test run
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
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import weka.classifiers.Classifier;
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
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.manual.ManualProfilerMatchingParameters;

import com.hp.hpl.jena.rdf.model.Property;

public class MLTestingWrapper {
	
	ArrayList<AbstractMatcher> listOfMatchers=new ArrayList<AbstractMatcher>();
	ArrayList<OntologyTriple> listOfTriples=new ArrayList<OntologyTriple>();
	ArrayList<String> matcherNames=new ArrayList<String>();
	
	Logger log;
	
	public MLTestingWrapper()
	{
		log = Logger.getLogger(MLTrainerWrapper.class);
		
		log.setLevel(Level.DEBUG);
	}
	//load the ontology given the location of ontology
	public static Ontology loadOntology(String ontoName){
		Ontology ontology;
		try {
			ontology = OntoTreeBuilder.loadOWLOntology(ontoName);
//			OntoTreeBuilder treeBuilder = new OntoTreeBuilder(ontoName, GlobalStaticVariables.SOURCENODE,
//					GlobalStaticVariables.LANG_OWL, 
//					GlobalStaticVariables.SYNTAX_RDFXML, false, true);
//			treeBuilder.build();
//			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			System.out.println("Failed To open the ontology!");
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	
	
	//load the matchers(PSM,BSM,VMM) into ArrayList listOfMatchers for later use
	//set the parameters specific to the matchers
	
	void loadMatchers()
	{
		//TODO : look at oaei2011 and look how to get matchers and add to list below 
	//	listOfMatchers.add();
		//try with these matchers 
		AbstractMatcher am=null;
		am=MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 0);
		ParametricStringParameters psmParam = new ParametricStringParameters(0.6, 1, 1);
		psmParam.useLexicons = true;
		psmParam.useBestLexSimilarity = true;
		psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
		psmParam.normParameter = new NormalizerParameter();
		psmParam.normParameter.setForOAEI2009();
		psmParam.redistributeWeights = true;
		psmParam.threadedExecution = true;
		psmParam.threadedOverlap = true;
		am.setParam(psmParam);
		listOfMatchers.add(am);
		am=MatcherFactory.getMatcherInstance(MatchersRegistry.BaseSimilarity, 0);
		BaseSimilarityParameters bsmParam = new BaseSimilarityParameters(0.6, 1, 1);
		bsmParam.useDictionary = false;
		am.setParam(bsmParam);
		listOfMatchers.add(am);
		am=MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 0);
		MultiWordsParameters vmmParam = new MultiWordsParameters(0.6,1,1);
		
		vmmParam.measure = MultiWordsParameters.TFIDF;
		//only on concepts right now because it should be weighted differently
		vmmParam.considerInstances = true;
		vmmParam.considerNeighbors = false;
		vmmParam.considerConcept = true;
		vmmParam.considerClasses = false;
		vmmParam.considerProperties = false;
		vmmParam.ignoreLocalNames = true; 
		vmmParam.useLexiconSynonyms = true; // May change later.
		am.setParam(vmmParam);
		listOfMatchers.add(am);
		
		//AbstractMatcher bsm=MatcherFactory.getMatcherInstance(MatchersRegistry.Equals, 0);
		
	}
	
	
	/**
	 * Load the test.xml file
	 * which contains information about the 
	 * source ontology path,target ontology path,reference alignment
	 * @param filename
	 * @param elementname
	 * @throws Exception
	 */
	void loadOntologyTriples(String filename,String elementname) throws Exception
	{
		//in linux RDF is rdf so had to put toLowerCase()
		//TODO: load the list of testing ontologies with reference alignments
		
		XmlParser xp=new XmlParser();
		//String basePath="/home/vivek/projects/workspace/AgreementMakerSVN/";
		String basePath="";
		ArrayList<TrainingLayout> tlist=xp.parseDocument(filename, elementname,"testing");
		for(TrainingLayout tl: tlist)
		{
			Ontology sourceOntology=loadOntology(basePath+tl.getsourceOntologyPath().toLowerCase());
			Ontology targetOntology=loadOntology(basePath+tl.gettargetOntologyPath().toLowerCase());
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = basePath+tl.getrefAlignmentPath().toLowerCase();
			refParam.format = ReferenceAlignmentMatcher.OAEI;
			AbstractMatcher referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
			referenceAlignmentMatcher.setParam(refParam);
			referenceAlignmentMatcher.setSourceOntology(sourceOntology);
			referenceAlignmentMatcher.setTargetOntology(targetOntology);
			referenceAlignmentMatcher.match();
       		Alignment<Mapping> refMap=referenceAlignmentMatcher.getAlignment();
			OntologyTriple ot=new OntologyTriple(sourceOntology,targetOntology,refMap);
			ot.setListOfMatchers(listOfMatchers);
			listOfTriples.add(ot);
		}
	}
	
	/**
	 * Run each of the matchers on the set of ontologies loaded
	 * from test.xml file and save the alignment for later use 
	 */
	void generateMappings()
	{

		for(int t=0;t<listOfTriples.size();t++)
		{
			OntologyTriple currentTriple=listOfTriples.get(t);
			ArrayList<AbstractMatcher> matchers=currentTriple.getListOfMatchers();
			for(int m=0;m<matchers.size();m++)
			{
				
				try {
					// Build the lexicons.
					LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
					lexParam.sourceOntology = currentTriple.getOntology1();
					lexParam.targetOntology = currentTriple.getOntology2();
					
					lexParam.sourceUseLocalname = true;
					lexParam.targetUseLocalname = false;
					lexParam.sourceUseSCSLexicon = false;
					lexParam.targetUseSCSLexicon = false;
					
					lexParam.detectStandardProperties(currentTriple.getOntology1());
					lexParam.detectStandardProperties(currentTriple.getOntology2());
					
					Core.getLexiconStore().buildAll(lexParam);
					
					// Ontology profiling
					ProfilerRegistry entry = ProfilerRegistry.ManualProfiler;
					OntologyProfiler profiler = null;
					Constructor<? extends OntologyProfiler> constructor = null;
						
					constructor = entry.getProfilerClass().getConstructor(Ontology.class, Ontology.class);
					profiler = constructor.newInstance(currentTriple.getOntology1(),currentTriple.getOntology2());
					
					if(profiler!=null) {
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
					for( Property currentProperty : manualProfiler.getSourceClassAnnotations() ) {
						if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
							profilingMatchingParams.sourceClassAnnotations.add(currentProperty);
						}
					}
					
					profilingMatchingParams.sourcePropertyAnnotations = new ArrayList<Property>();
					for( Property currentProperty : manualProfiler.getSourcePropertyAnnotations() ) {
						if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
							profilingMatchingParams.sourcePropertyAnnotations.add(currentProperty);
						}
					}
					
					profilingMatchingParams.targetClassAnnotations = new ArrayList<Property>();
					for( Property currentProperty : manualProfiler.getTargetClassAnnotations() ) {
						if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
							profilingMatchingParams.targetClassAnnotations.add(currentProperty);
						}
					}
					
					profilingMatchingParams.targetPropertyAnnotations = new ArrayList<Property>();
					for( Property currentProperty : manualProfiler.getTargetPropertyAnnotations() ) {
						if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
							profilingMatchingParams.targetPropertyAnnotations.add(currentProperty);
						}
					}
					
					manualProfiler.setMatchTimeParams(profilingMatchingParams);
					
					/*ParametricStringParameters psmParam = new ParametricStringParameters(0.6, 1, 1);
					psmParam.useLexicons = true;
					psmParam.useBestLexSimilarity = true;
					psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
					psmParam.normParameter = new NormalizerParameter();
					psmParam.normParameter.setForOAEI2009();
					psmParam.redistributeWeights = true;
					psmParam.threadedExecution = true;
					psmParam.threadedOverlap = true;
					currentMatcher.setParam(psmParam);*/
				//	BaseSimilarityParameters bsmParam = new BaseSimilarityParameters(0.6, 1, 1);
					//bsmParam.useDictionary = false;
					//currentMatcher.setParam(bsmParam);
	//				currentMatcher.setParam(vmmParam);
		
						AbstractMatcher currentMatcher=matchers.get(m);
						currentMatcher.setOntologies(currentTriple.getOntology1(), currentTriple.getOntology2());
						currentMatcher.setPerformSelection(true);
						currentMatcher.setUseProgressDelay(false);
						currentMatcher.match();
						Alignment<Mapping> resultAlignment=currentMatcher.getAlignment();
						if(resultAlignment!=null && currentMatcher!=null)
						{
							if(!currentTriple.containsMatcher(currentMatcher.getName()))
							{
								currentTriple.setAlignmentObtained(currentMatcher.getName(), resultAlignment);	
							}
								
						}
//						else
//						{
//							//currentTriple.setAlignmentObtained(currentMatcher, null);
//						}
					
					
					} catch (Exception e) {
			
						e.printStackTrace();
					}
			}
		}
	}
	
	/**
	 * By now,We have the individual alignments from each matcher
	 * We generate a single test file which contains
	 * all the correspondences along with the similarity value from
	 * each individual matcher 
	 * @throws Exception
	 */
	void generateTestFile() throws Exception
	{
		//ArrayList<String> mappedSourceTarget=new ArrayList<String>();
	//	String[] trainingFiles={"psm","bsm","vmm"};
		for(int m=0;m<listOfMatchers.size();m++)
		{
			
			AbstractMatcher currentMatcher=listOfMatchers.get(m);
			BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File("bench/matchers/test/"+currentMatcher.getName())));
			if(currentMatcher!=null)
			{
				for(int t=0;t<listOfTriples.size();t++)
				{
					
					OntologyTriple currentTriple=listOfTriples.get(t);
					Alignment<Mapping> referenceAlignment=currentTriple.getReferenceAlignment();
					
					
					if(currentTriple.containsMatcher(currentMatcher.getName()))
					{
						Alignment<Mapping> currentMapping=currentTriple.getAlignmentObtained(currentMatcher.getName());
						if(currentMapping!=null)
						{
							/*Ontology sourceOntology=currentTriple.getOntology1();
							Ontology targetOntology=currentTriple.getOntology2();
							List<Node> sourceClasses=sourceOntology.getClassesList();
							List<Node> targetClasses=targetOntology.getClassesList();
							for(int source=0;source<sourceClasses.size();source++)
							{
								Node sourceNode=sourceClasses.get(source);
								for(int target=0;target<targetClasses.size();target++)
								{
									Node targetNode=targetClasses.get(target);
									if(currentMapping.isMapped(sourceNode) && currentMapping.isMapped(targetNode))
									{
										//if(!mappedSourceTarget.contains(sourceNode.getUri()+"\t"+targetNode.getUri()))
										//{
											
											double similarityValue=currentMapping.getSimilarity(sourceNode, targetNode);
											double referenceValue=referenceAlignment.getSimilarity(sourceNode, targetNode);
											//System.out.println(sourceNode.getUri()+"\t"+targetNode.getUri()+"\t"+similarityValue+"\t"+referenceValue+"\n");
											outputWriter.write(sourceNode.getUri()+"\t"+targetNode.getUri()+"\t"+similarityValue+"\t"+referenceValue+"\n");
											mappedSourceTarget.add(sourceNode.getUri()+"\t"+targetNode.getUri());
										//}																
									}								
								}							
							}*/
							/*System.out.println("-------------------------------------");
							System.out.println(currentMapping.size());
							for(int i=0;i<currentMapping.size();i++)
							{
								System.out.println(currentMapping.get(i).getString(true));
								
							}
							System.out.println("-----------------------------");
							System.out.println(referenceAlignment.size());*/
							boolean mapped=false;
							for(int i=0;i<currentMapping.size();i++)
							{
								double similarity=currentMapping.getSimilarity(currentMapping.get(i).getEntity1(), currentMapping.get(i).getEntity2());
								mapped=false;
								for(int j=0;j<referenceAlignment.size();j++)
								{
									
									if(currentMapping.get(i).getString(true).equals(referenceAlignment.get(j).getString(true)))
									{
										//System.out.println("mapped");
	
										//outputWriter.write(currentMapping.get(i).getEntity1().getUri()+"\t"+currentMapping.get(i).getEntity2().getUri()+"\t1.0\t1.0\n");
										outputWriter.write(currentMapping.get(i).getEntity1().getUri()+"\t"+currentMapping.get(i).getEntity2().getUri()+"\t"+similarity+"\t1.0\n");
										mapped=true;
									}
								 }
								if(!mapped)
								{
									//System.out.println("matcher mapping wrong");
									outputWriter.write(currentMapping.get(i).getEntity1().getUri()+"\t"+currentMapping.get(i).getEntity2().getUri()+"\t"+similarity+"\t0.0\n");
								}
							  }
							
						}	
					}					
				}	
			}			
			outputWriter.close();
		}		
		mergeIndividualFiles();	
		
	}
	
	/**
	 * Module invoked from generateTestFile()
	 * which actually generates the combined testfile
	 * 2 files are generated
	 * outputfiles
	 * file 1- 
	 * name - testFilecombined- contains only the similarity value from each individual matcher.This file is
	 * used as input to generate the test arff file needed for ML
	 * file 2-
	 * name-  testreffilecombined-contains the correspondences(source uri,target uri),and whether the alignment is  
	 * present in the reference alignment.This file is used to map the correspondences with the prediction made
	 * by the classifier later on.
	 * @throws IOException
	 */
	void mergeIndividualFiles() throws IOException
	{
		ArrayList<String> matcherFiles=new ArrayList<String>();
		getFilesFromFolder(matcherFiles,"bench/matchers/test/");
		
		HashMap<String,HashMap> uniqueConcepts=new HashMap<String,HashMap>();
		
		for(int i=0;i<matcherFiles.size();i++)
		{
			File currentFile=new File(matcherFiles.get(i));
			String matcherName=currentFile.getName();
			
			//adding matcher name we need to generate ARFF file 
			matcherNames.add(matcherName);
			
			BufferedReader inputReader=new BufferedReader(new FileReader(currentFile));
			while(inputReader.ready())
			{
				String inputLine=inputReader.readLine();
				String[] inputLineParts=inputLine.split("\t");
				if(inputLineParts.length==4)
				{
					
					String mapKey=inputLineParts[0]+"\t"+inputLineParts[1];
					HashMap<String,String> matcherMap;
					if(uniqueConcepts.containsKey(mapKey))
					{
						matcherMap=uniqueConcepts.get(mapKey);
						matcherMap.put(matcherName, inputLine);
					}
					else
					{
						matcherMap=new HashMap<String,String>();
						matcherMap.put(matcherName, inputLine);
					}
					uniqueConcepts.put(mapKey, matcherMap);
				}				
			}
			
		}
		
		//writing the results into a file in the format we want
		
		Set<String> mapKeys=uniqueConcepts.keySet();
		Iterator<String> mapKeyIterator=mapKeys.iterator();
		
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File("bench/combinedmatchers/testFilecombined")));
		BufferedWriter outputRef=new BufferedWriter(new FileWriter(new File("bench/combinedmatchers/testrefFilecombined")));
		while(mapKeyIterator.hasNext())
		{
			String currentKey=mapKeyIterator.next();
			
			HashMap<String,String> matcherMap=uniqueConcepts.get(currentKey);
			String outputStr="";
			String outputStr1="";
			outputStr1=currentKey;
			String referenceSim="0.0";
			String[] matcherSim=new String[3];
			for(int i=0;i<matcherFiles.size();i++)
			{
				File currentFile=new File(matcherFiles.get(i));
				//System.out.println(matcherFiles.get(i));
				String matcherName=currentFile.getName();
				
				if(matcherMap.containsKey(matcherName))
				{
				
					matcherSim[i]=matcherMap.get(matcherName).split("\t")[2];
					referenceSim=matcherMap.get(matcherName).split("\t")[3];
					
				}
				else
				{
					matcherSim[i]="0.0";
				}
				outputStr+=matcherSim[i]+"\t";
			}
			//outputStr+=referenceSim;
			outputStr1+="\t"+referenceSim;
			outputWriter.write(outputStr+"\n");
			outputRef.write(outputStr1+"\n");
			
		}
		
		outputWriter.close();
		outputRef.close();
		
	}
	
	/**
	 * To get all files inside a particular folder
	 * this module is invoked when we want to combine the
	 * alignment generated by individual matcher
	 * @param files
	 * @param folder
	 */
	void getFilesFromFolder(ArrayList<String> files, String folder)
	{
		File file=new File(folder);
		
		if(file.isDirectory())
		{
			File[] filesInDir=file.listFiles();
			if(!file.getName().contains("svn"))
			{
				for(int i=0;i<filesInDir.length;i++)
				{
					getFilesFromFolder(files, filesInDir[i].getAbsolutePath());
				}
			}
		}
		else
		{
			if(!file.getName().equals("entries"))
			{
				files.add(file.getAbsolutePath());	
			}
			
		}
	}
		
	/*
	 *main module to predict results for test set
	 *given two ontologies,reference alignment and a ML model 
	 */
	void predictresult(String modelName,String srcOntology,String tarOntology,String refAlign, String predicted,String combinedConceptFile,String finalFile) throws Exception
	{
		//generating the test.xml file needed by MLTestingWrapper
		String outputFilename="bench/files/test.xml";
		ArrayList<Double> predictedList=new ArrayList<Double>();
		 try {
			 int i=1;
			 	
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root element
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("testsets");
				doc.appendChild(rootElement);
		        
				Element trainingSet = doc.createElement("testset");
				rootElement.appendChild(trainingSet);
				trainingSet.setAttribute("id", Integer.toString(i));
		 
				// source ontology
				Element sourceOntology = doc.createElement("sourceontology");
				sourceOntology.setAttribute("path", srcOntology);
				trainingSet.appendChild(sourceOntology);
		 
				// target ontology
				Element targetOntology = doc.createElement("targetontology");
				targetOntology.setAttribute("path", tarOntology);
				trainingSet.appendChild(targetOntology);
		 
				// reference alignment
				Element refAlignment = doc.createElement("refalignment");
				refAlignment.setAttribute("path", refAlign);
				trainingSet.appendChild(refAlignment);
				
		       
					 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				Result result = new StreamResult( new FileOutputStream(outputFilename));
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				//System.out.println("XML File saved! " + outputfilename);
				log.info("XML File saved! " + outputFilename);
		       
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
			  catch (FileNotFoundException fnf) {
					fnf.printStackTrace();
				  }
			  
			  //running the matchers on testset
			  
			  callProcess(outputFilename, "testset");
			  
			  //deserialising the model we have built
			  ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(modelName));
			  Classifier cls = (Classifier) ois.readObject();
			  
			  //generating arff file and setting testset class
			  ArffConvertor arff=new ArffConvertor("bench/combinedmatchers/testFilecombined", "test",matcherNames);
			  arff.generateArffFile();
			  BufferedReader  testset = new BufferedReader(new FileReader("bench/arff/testFilecombined.arff"));
			  Instances test=new Instances(testset);
			  test.setClassIndex(test.numAttributes()-1);
				
			  //predict the result using given model
			  
			  for (int i = 0; i < test.numInstances(); i++) {
				   double clsLabel = cls.classifyInstance(test.instance(i));
				   test.instance(i).setClassValue(clsLabel);
				   predictedList.add(clsLabel);
				   
				 }
				 // save the predicted data
				 BufferedWriter writer = new BufferedWriter(
				                           new FileWriter(predicted));
				 writer.write(test.toString());
				 writer.newLine();
				 writer.flush();
				 writer.close();
			  ois.close();
			  
			  //Now we have the predicted value
			  //generate a single file which has the correspondences and predicted value
			  //run it against the reference alignment
			  //to compute the precision,recall and f-measure
			  matchReference(predictedList,combinedConceptFile,finalFile);
			  Alignment<Mapping> refMap=getReference(srcOntology, tarOntology, refAlign);
			  calculateMeasure(finalFile,refMap);			  
	}
	
	/**
	 * computing the f-measure for the testset
	 * @param finalFile
	 * @param refMap
	 * @throws IOException
	 */
	void calculateMeasure(String finalFile,Alignment<Mapping> refMap) throws IOException
	{
		
		BufferedReader  alignmentFile = new BufferedReader(new FileReader(finalFile));
		int mapped=0,count=0;
		//System.out.println("-----------------------------------");
		//System.out.println("reference size"+refmap.size());
		log.info("reference size"+refMap.size());
		while(alignmentFile.ready())
		{
			
			
			String inputLine=alignmentFile.readLine();
			String[] inputLineParts=inputLine.split("\t");
			if(inputLineParts[2].equals("1.0"))
			{
				count++;
			}
			for(int i=0;i<refMap.size();i++)
			{
				Mapping currentMapping=refMap.get(i);
				if(currentMapping.getEntity1().getUri().equals(inputLineParts[0]) && currentMapping.getEntity2().getUri().equals(inputLineParts[1]) && currentMapping.getSimilarity()==Double.parseDouble(inputLineParts[2])) 
					
				{
					//System.out.println("match found in ref alignment");
					mapped++;
				}
			}
		}
		//System.out.println("-----------------------------------------------");
		log.info("-------------------------------------------------------------");
		//System.out.println("total correct" + mapped);
		//System.out.println("total mapping" +count);
		log.info("total correct" + mapped);
		log.info("total mapping" +count);
		float precision = (float)mapped/count;
		float recall = (float)mapped/refMap.size();
		float fmeasure = 2 * precision * recall / (precision + recall);
		//System.out.print(precision + "\t" + recall + "\t" + fmeasure);
		log.info(precision + "\t" + recall + "\t" + fmeasure);
		//System.out.println("-----------------------------------------------------");
		log.info("-------------------------------------------------------------");

	}
	
	Alignment<Mapping> getReference(String srcOnto,String tarOnto,String refFile) throws Exception
	{
		Ontology sourceOntology=loadOntology(srcOnto);
		Ontology targetOntology=loadOntology(tarOnto);
		ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
		refParam.onlyEquivalence = true;
		refParam.fileName = refFile;
		refParam.format = ReferenceAlignmentMatcher.OAEI;
		AbstractMatcher referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
		referenceAlignmentMatcher.setParam(refParam);
		referenceAlignmentMatcher.setSourceOntology(sourceOntology);
		referenceAlignmentMatcher.setTargetOntology(targetOntology);
		referenceAlignmentMatcher.match();
   		Alignment<Mapping> refMap=referenceAlignmentMatcher.getAlignment();
		return refMap;
	}
	
	/**
	 * 	 generate a single file which has the correspondences and predicted value
	 *	run it against the reference alignment		
	 * @param predictedList
	 * @param combinedConceptFile
	 * @param finalFile
	 * @throws IOException
	 */
	void matchReference(ArrayList<Double> predictedList,String combinedConceptFile,String finalFile) throws IOException
	{	
		
		BufferedReader  conceptFile = new BufferedReader(new FileReader(combinedConceptFile));
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File(finalFile)));
		int index=0;
		while(conceptFile.ready())
		{
			
			String inputLine=conceptFile.readLine();
			String[] inputLineParts=inputLine.split("\t");
			if(inputLineParts.length==3)
			{
				
				String concepts=inputLineParts[0]+"\t"+inputLineParts[1];
				outputWriter.write(concepts+"\t"+predictedList.get(index)+"\n");
				index++;
			}				
		}
		//System.out.println("final file generated:" + finalfile);
		log.info("final file generated:" + finalFile);
		outputWriter.close();
	
	}
	
	
	
	
	void callProcess(String trainingFileName,String elementName) throws Exception
	{
		//String trainingFileName="bench/test.xml";
		//String elementName="testset";
		loadMatchers();
		loadOntologyTriples(trainingFileName,elementName);
		generateMappings();
		generateTestFile();
		//generateModel();
//		String testFileName="";
//		elementName="testset";
//		loadOntologyTriples(testFileName,elementName);
		
		
		
	}
	
	void callProcess()
	{
		try {
			//predicting the result for the testset using decisiontree classifier
			predictresult("bench/arff/model/decisiontree.model","bench/training/101/onto.RDF","bench/test/258-2/onto.RDF","bench/test/258-2/refalign.RDF","bench/arff/output/predictedDT.arff","bench/combinedmatchers/testrefFilecombined","bench/files/finaloutputDT");
			
			//predicting the result for the testset using Naive Bayes classifier
			predictresult("bench/arff/model/naivebayes.model","bench/training/101/onto.RDF","bench/test/258-2/onto.RDF","bench/test/258-2/refalign.RDF","bench/arff/output/predictedNB.arff","bench/combinedmatchers/testrefFilecombined","bench/files/finaloutputNB");
			
			//predicting the result for the testset using SVM classifier
			predictresult("bench/arff/model/svm.model","bench/training/101/onto.RDF","bench/test/258-2/onto.RDF","bench/test/258-2/refalign.RDF","bench/arff/output/predictedSVM.arff","bench/combinedmatchers/testrefFilecombined","bench/files/finaloutputSVM");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
		
}
