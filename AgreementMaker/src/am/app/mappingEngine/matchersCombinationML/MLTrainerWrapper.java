package am.app.mappingEngine.matchersCombinationML;

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
import java.io.ObjectOutputStream;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Attr;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.DecisionStump;
import weka.core.Instances;
import weka.core.matrix.DoubleVector;

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

public class MLTrainerWrapper {
	
	ArrayList<AbstractMatcher> listOfMatchers=new ArrayList<AbstractMatcher>();
	ArrayList<OntologyTriple> listOfTriples=new ArrayList<OntologyTriple>();
	ArrayList<String> matcherNames=new ArrayList<String>();
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
	
	
	void loadMatchers()
	{
		//TODO : look at oaei2011 and look how to get matchers and add to list below 
	//	listOfMatchers.add();
		//try with these matchers da
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
	
	void loadOntologyTriples(String filename,String elementname) throws Exception
	{
		//in linux RDF is rdf so had to put toLowerCase()
		//TODO: load the list of training ontologies with reference alignments
		
		XmlParser xp=new XmlParser();
		//String basePath="/home/vivek/projects/workspace/AgreementMakerSVN/";
		String basePath="";
		ArrayList<TrainingLayout> tlist=xp.parseDocument(filename, elementname,"training");
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
       		Alignment<Mapping> refmap=referenceAlignmentMatcher.getAlignment();
			OntologyTriple ot=new OntologyTriple(sourceOntology,targetOntology,refmap);
			ot.setListOfMatchers(listOfMatchers);
			listOfTriples.add(ot);
		}
	}
	
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
	
	void generateTrainingFile() throws Exception
	{
		//ArrayList<String> mappedSourceTarget=new ArrayList<String>();
	//	String[] trainingFiles={"psm","bsm","vmm"};
		for(int m=0;m<listOfMatchers.size();m++)
		{
			
			AbstractMatcher currentMatcher=listOfMatchers.get(m);
			BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File("bench/matchers/training/"+currentMatcher.getName())));
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
	
	void mergeIndividualFiles() throws IOException
	{
		ArrayList<String> matcherFiles=new ArrayList<String>();
		getFilesFromFolder(matcherFiles,"bench/matchers/training/");
		
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
		
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File("bench/combinedmatchers/trainingFilecombined")));
		
		while(mapKeyIterator.hasNext())
		{
			String currentKey=mapKeyIterator.next();
			
			HashMap<String,String> matcherMap=uniqueConcepts.get(currentKey);
			String outputStr="";
			String referenceSim="0.0";
			String[] matcherSim=new String[3];
			for(int i=0;i<matcherFiles.size();i++)
			{
				File currentFile=new File(matcherFiles.get(i));
				String matcherName=currentFile.getName();
				
				if(matcherMap.containsKey(matcherName))
				{
				//System.out.println(i);
					matcherSim[i]=matcherMap.get(matcherName).split("\t")[2];
					referenceSim=matcherMap.get(matcherName).split("\t")[3];
					
				}
				else
				{
					matcherSim[i]="0.0";
				}
				outputStr+=matcherSim[i]+"\t";
			}
			outputStr+=referenceSim;
			
			outputWriter.write(outputStr+"\n");
			
		}
		
		outputWriter.close();
		
		
	}
	
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
		
	
	void generateTrainingARFF() throws IOException
	{
		/*ArrayList<String> mn=new ArrayList<String>();
		mn.add("m1");
		mn.add("m2");
		mn.add("m3");*/
		ArffConvertor arff=new ArffConvertor("bench/combinedmatchers/trainingFilecombined", "training",matcherNames);
		arff.generateArffFile();
		arff=new ArffConvertor("bench/combinedmatchers/testFilecombined", "test",matcherNames);
		arff.generateArffFile();
		System.out.println("Training and test file generated");
	}
	void generateModel() throws Exception
	{
		 BufferedReader trainingset = new BufferedReader(new FileReader("bench/arff/trainingFilecombined.arff"));
		 //BufferedReader  testset = new BufferedReader(new FileReader("bench/arff/testFilecombined.arff"));
		 Instances train=new Instances(trainingset);
		 //Instances test=new Instances(testset);
		 train.setClassIndex(train.numAttributes() - 1);
		 //test.setClassIndex(test.numAttributes()-1);
		 Classifier cls1= (Classifier) new DecisionStump();
		 Classifier cls2= (Classifier) new BayesNet();
		 Classifier cls3= (Classifier) new LibSVM();
		 cls1.buildClassifier(train);
		 cls2.buildClassifier(train);
		 cls3.buildClassifier(train);
		 
		 //save the model for future use
		 // serialize model
		 ObjectOutputStream oos = new ObjectOutputStream(
		                            new FileOutputStream("bench/arff/model/decisiontree.model"));
		 oos.writeObject(cls1);
		 oos.flush();
		 oos=new ObjectOutputStream(
                 new FileOutputStream("bench/arff/model/naivebayes.model"));
		 oos.writeObject(cls2);
		 oos=new ObjectOutputStream(
                 new FileOutputStream("bench/arff/model/svm.model"));
		 oos.writeObject(cls3);
		
		 oos.close();
		 trainingset.close();
		/* Evaluation eval = new Evaluation(train);
		 cls.
		 
		 System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		 trainingset.close();
		 testset.close();*/
		 
		 //predict the class for testset
		/* for (int i = 0; i < test.numInstances(); i++) {
			   double clsLabel = cls.classifyInstance(test.instance(i));
			   test.instance(i).setClassValue(clsLabel);
			 }
			 // save labeled data
			 BufferedWriter writer = new BufferedWriter(
			                           new FileWriter("bench/arff/output/predicted.arff"));
			 writer.write(test.toString());
			 writer.newLine();
			 writer.flush();
			 writer.close();*/
           System.out.println("Three models generated");



	}
	//main module to predict results for test set
	//given two ontology
	void predictresult(String modelname,String srcOntology,String tarOntology,String refalign, String predicted,String combinedconceptfile,String finalfile) throws Exception
	{
		String outputfilename="bench/files/test.xml";
		ArrayList<Double> predictedList=new ArrayList<Double>();
		 try {
			 int i=1;
			 	
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root element
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("testsets");
				doc.appendChild(rootElement);
		        
				Element trainingset = doc.createElement("testset");
				rootElement.appendChild(trainingset);
				trainingset.setAttribute("id", Integer.toString(i));
		 
				// source ontology
				Element sourceontology = doc.createElement("sourceontology");
				sourceontology.setAttribute("path", srcOntology);
				trainingset.appendChild(sourceontology);
		 
				// target ontology
				Element targetontology = doc.createElement("targetontology");
				targetontology.setAttribute("path", tarOntology);
				trainingset.appendChild(targetontology);
		 
				// reference alignment
				Element refalignment = doc.createElement("refalignment");
				refalignment.setAttribute("path", refalign);
				trainingset.appendChild(refalignment);
				
		       
					 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				Result result = new StreamResult( new FileOutputStream(outputfilename));
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				System.out.println("XML File saved! " + outputfilename);
		       
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
			  catch (FileNotFoundException fnf) {
					fnf.printStackTrace();
				  }
			  
			  //running the matchers on testset
			  MLTestingWrapper tWrapper=new MLTestingWrapper();
			  tWrapper.callProcess(outputfilename, "testset");
			  
			  //deserialising the model we have built
			  ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(modelname));
			  Classifier cls = (Classifier) ois.readObject();
			  
			  //setting testset class
			  ArffConvertor arff=new ArffConvertor("bench/combinedmatchers/testFilecombined", "test",tWrapper.matcherNames);
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
				 // save labeled data
				 BufferedWriter writer = new BufferedWriter(
				                           new FileWriter(predicted));
				 writer.write(test.toString());
				 writer.newLine();
				 writer.flush();
				 writer.close();
			  ois.close();
			  
			  //Now we have the predicted value
			  //generate a single file which predicted value
			  //run it against the reference alignment
			  //to compute the precision,recall and f-measure
			  matchReference(predictedList,combinedconceptfile,finalfile);
			  Alignment<Mapping> refmap=getreference(srcOntology, tarOntology, refalign);
			  calculateMeasure(finalfile,refmap);
			  
			  
	}
	void matchReference(ArrayList<Double> predictedList,String combinedconceptfile,String finalfile) throws IOException
	{	
		
		BufferedReader  conceptfile = new BufferedReader(new FileReader(combinedconceptfile));
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File(finalfile)));
		int index=0;
		while(conceptfile.ready())
		{
			
			String inputLine=conceptfile.readLine();
			String[] inputLineParts=inputLine.split("\t");
			if(inputLineParts.length==3)
			{
				
				String concepts=inputLineParts[0]+"\t"+inputLineParts[1];
				outputWriter.write(concepts+"\t"+predictedList.get(index)+"\n");
				index++;
			}				
		}
		System.out.println("final file generated:" + finalfile);
		outputWriter.close();
	
	}
	void generateMapping(String finalfile)
	{
	
	}
	void calculateMeasure(String finalfile,Alignment<Mapping> refmap) throws IOException
	{
		
		BufferedReader  alignmentfile = new BufferedReader(new FileReader(finalfile));
		int mapped=0,count=0;
		System.out.println("-----------------------------------");
		System.out.println("reference size"+refmap.size());
		while(alignmentfile.ready())
		{
			
			
			String inputLine=alignmentfile.readLine();
			String[] inputLineParts=inputLine.split("\t");
			if(inputLineParts[2].equals("1.0"))
			{
				count++;
			}
			for(int i=0;i<refmap.size();i++)
			{
				Mapping currentMapping=refmap.get(i);
				if(currentMapping.getEntity1().getUri().equals(inputLineParts[0]) && currentMapping.getEntity2().getUri().equals(inputLineParts[1]) && currentMapping.getSimilarity()==Double.parseDouble(inputLineParts[2])) 
					
				{
					//System.out.println("match found in ref alignment");
					mapped++;
				}
			}
		}
		System.out.println("-----------------------------------------------");
		System.out.println(mapped);
		System.out.println(count);
		float precision = (float)mapped/count;
		float recall = (float)mapped/refmap.size();
		float fmeasure = 2 * precision * recall / (precision + recall);
		System.out.print(precision + "\t" + recall + "\t" + fmeasure);
		System.out.println("-----------------------------------------------------");

	}
	
	Alignment<Mapping> getreference(String srcOnto,String tarOnto,String reffile) throws Exception
	{
		Ontology sourceOntology=loadOntology(srcOnto);
		Ontology targetOntology=loadOntology(tarOnto);
		ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
		refParam.onlyEquivalence = true;
		refParam.fileName = reffile;
		refParam.format = ReferenceAlignmentMatcher.OAEI;
		AbstractMatcher referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
		referenceAlignmentMatcher.setParam(refParam);
		referenceAlignmentMatcher.setSourceOntology(sourceOntology);
		referenceAlignmentMatcher.setTargetOntology(targetOntology);
		referenceAlignmentMatcher.match();
   		Alignment<Mapping> refmap=referenceAlignmentMatcher.getAlignment();
		return refmap;
	}
	void callProcess() throws Exception
	{
		/*String trainingFileName="bench/training.xml";
		String elementName="trainingset";
		loadMatchers();
		loadOntologyTriples(trainingFileName,elementName);
		generateMappings();
		generateTrainingFile();
		generateTrainingARFF();
		generateModel();
		matchReference();
		calculateMeasure();*/
		
		predictresult("bench/arff/model/decisiontree.model","bench/training/101/onto.RDF","bench/test/258-2/onto.RDF","bench/test/258-2/refalign.RDF","bench/arff/output/predictedDT.arff","bench/combinedmatchers/testrefFilecombined","bench/files/finaloutputDT");
		predictresult("bench/arff/model/naivebayes.model","bench/training/101/onto.RDF","bench/test/258-2/onto.RDF","bench/test/258-2/refalign.RDF","bench/arff/output/predictedNB.arff","bench/combinedmatchers/testrefFilecombined","bench/files/finaloutputNB");
		predictresult("bench/arff/model/svm.model","bench/training/101/onto.RDF","bench/test/258-2/onto.RDF","bench/test/258-2/refalign.RDF","bench/arff/output/predictedSVM.arff","bench/combinedmatchers/testrefFilecombined","bench/files/finaloutputSVM");
		
//		String testFileName="";
//		elementName="testset";
//		loadOntologyTriples(testFileName,elementName);
	}
	
	public static void main(String args[])throws Exception
	{
		MLTrainerWrapper ml=new MLTrainerWrapper();
		ml.callProcess();
				
	}

}
