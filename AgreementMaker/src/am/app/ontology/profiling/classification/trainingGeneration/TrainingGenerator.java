package am.app.ontology.profiling.classification.trainingGeneration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.oaei.OAEI_Track;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011Matcher;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters.OAEI2011Configuration;
import am.app.mappingEngine.oaei2010.OAEI2010Matcher;
import am.app.mappingEngine.oaei2010.OAEI2010MatcherParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.profiling.classification.ClassificatorRegistry;
import am.app.ontology.profiling.classification.OntologyClassificator;
import am.app.ontology.profiling.classification.Test;
import am.app.ontology.profiling.classification.TestSet;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.userInterface.console.ConsoleProgressDisplay;
import am.utility.LocalnameComparator;

import com.hp.hpl.jena.rdf.model.Property;

public class TrainingGenerator {
	
	
	//in this main you can see how to use all the classification framework
	
	public static void main(String[] args) {
		//test("OAEI2011/path.xml");
		//testThreshold();
		//testClassified();
		/*
		//create the object input training that contains a list of matcher, parameters and classes
		InputTrainingGenerator i = new InputTrainingGenerator();
		
		OAEI2011Matcher matcher = new OAEI2011Matcher();
		matcher.setProgressDisplay( new ConsoleProgressDisplay() );
		//i have to set the name otherwise it will be "undefinedMatcher"
		matcher.setName("OEAI11"); //TODO: cosmin why if i don't set this name, the getName returns null?? 
		
		//set the parameters for each matcher
		OAEI2011MatcherParameters param1 = new OAEI2011MatcherParameters();
		param1.automaticConfiguration = false;
		param1.selectedConfiguration = OAEI2011Configuration.GENERAL_PURPOSE; //ex benchmark
		OAEI2011MatcherParameters param2 = new OAEI2011MatcherParameters();
		param2.automaticConfiguration = false;
		param2.selectedConfiguration = OAEI2011Configuration.GENERAL_PURPOSE_ADVANCED; // benchmark++
		OAEI2011MatcherParameters param3 = new OAEI2011MatcherParameters();
		param3.automaticConfiguration = false;
		param3.selectedConfiguration = OAEI2011Configuration.GENERAL_MULTI; // ex conference
		OAEI2011MatcherParameters param4 = new OAEI2011MatcherParameters();
		param4.automaticConfiguration = false;
		param4.selectedConfiguration = OAEI2011Configuration.LARGE_LEXICAL; // ex conference
		OAEI2011MatcherParameters param5 = new OAEI2011MatcherParameters();
		param5.automaticConfiguration = false;
		param5.selectedConfiguration = OAEI2011Configuration.LARGE_LEXICAL_WITH_LOCALNAMES;
		
		//add the test to the list of matcher i want to try
		try {
			i.addTest(matcher, param1, "general_purpose");
			i.addTest(matcher, param2, "general_purpose_advanced");
			i.addTest(matcher, param3, "general_multi");
			i.addTest(matcher, param4, "large_lexical");
			i.addTest(matcher, param5, "large_lexical_with_localnames");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create the training set object from the file of ontologies and reference allignment and the list of matcher
		OutputTrainingGenerator o = createTrainingSet("OAEI2011/finalPath.xml", i);
		
		//save the object on disk
		try {
			o.storeFile("Classification/finalTraining4.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		//load the object from file
		OutputTrainingGenerator o = new OutputTrainingGenerator("Classification/finalTraining3.xml");
		//o.storeTableVisual("Classification/tableFinalTraining4.txt");
	
			
			
		//create the model with the output training
		//OntologyClassificator oc = new OntologyClassificator(o2,ClassificatorRegistry.C_NaiveBayes);
		OntologyClassificator oc = new OntologyClassificator(o);
		

		
		//train the model with the 
		try {
			oc.trainCoupleOntology(o);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		//store the model
		oc.storeModel("Classification/cModel4.model");
		System.out.println("MODEL STORED!");
	
		*/
		
		
		
		//load a model // ATTENTION! YOU HAVE ALWAYS TO SPECIFY THE OutputTG! 
		//OutputTrainingGenerator o3 = new OutputTrainingGenerator("Classification/prova2.xml");
		/*OntologyClassificator*/// oc = new OntologyClassificator(o,"Classification/cModel4.model");
		
		
	/*		
		//create the test set of ontology from file
		TestSet testSet = new TestSet("Classification/finalTesting3.xml");
	
		
		
		
		
		
	
		
		//Evaluate the model 
		String evaluation = oc.testModel(o, testSet);
		System.out.println(evaluation);
		
		
			//classified a set of ontologies
		String [] result = oc.classifiedCoupleOntology(testSet);
		for (int j = 0; j < result.length; j++) {
			System.out.println(result[j]);
		}
		System.out.println(evaluation);*/
		/**/
	/*	
	*/
		
	/*	
		// cross Validate the model
		oc2.crossValidation(o3, 10, 3);
		
		
		
		//classified two ontology
		Ontology sourceOntology = OntologyClassificator.openOntology("OAEI2011/anatomy/human.owl");
		Ontology targetOntology = OntologyClassificator.openOntology("OAEI2011/anatomy/mouse.owl");
		String cl = OntologyClassificator.classifiedOntologiesST(sourceOntology,targetOntology,"Classification/test4.model", "Classification/prova3.xml");
		System.out.println(cl);
		
		*/
		
		
	/*	
		//test the method classifiedOntologiesOEAI2011
		LinkedList<String> list = new LinkedList<String>();
		LinkedList<Test> test = testSet.getTestList();
		System.out.println(test.size());
		for (Iterator<Test> it = test.iterator(); it.hasNext();) {
			Test test2 = (Test) it.next();
			Ontology sourceOntology = OntologyClassificator.openOntology(test2.getSourceOntology());
			Ontology targetOntology = OntologyClassificator.openOntology(test2.getTargetOntology());
			OAEI2011Configuration conf = OntologyClassificator.classifiedOntologiesOEAI2011(sourceOntology, targetOntology);
		
			
			switch( conf ) {
			case LARGE_LEXICAL: {
				list.add("large_lexical");
			}
			break;
			case GENERAL_PURPOSE: {
				list.add("general purpose");
			}
			break;
			case GENERAL_MULTI: {
				list.add("general multi");
			}
			break;
			case GENERAL_PURPOSE_ADVANCED: {
				list.add("general purpose advance");
			}
			break;
			case LARGE_LEXICAL_WITH_LOCALNAMES: {
				list.add("large_lexical_with localname");
			}
			break;
			default:{
				list.add("default");
			}
			break;
			}
		
		
		
		}
		for (Iterator it = list.iterator(); it.hasNext();) {
			String string = (String) it.next();
			System.out.println(string);
		}
		*/
		
		
		
	}
	
	
	
	public static void testThreshold() {
	/*	
		//create the object input training that contains a list of matcher, parameters and classes
		InputTrainingGenerator i = new InputTrainingGenerator();
		
		
		//i have to set the name otherwise it will be "undefinedMatcher"
		 //TODO: cosmin why if i don't set this name, the getName returns null?? 
		
		
		//benchmark
		double add = 0.05;
		for (int j = 0; j < 7; j++) {
			double d = 0.5 + ((double)j)*add;
			OAEI2010Matcher matcher = new OAEI2010Matcher();
			matcher.setName("OEAI10-benchmark"+d);
			OAEI2010MatcherParameters param = new OAEI2010MatcherParameters(OAEI_Track.Benchmarks);
			
			matcher.setThreshold(d);
			param.threshold= d;
			System.out.println("THRESHOLD"+matcher.getThreshold());
			
			try {
				i.addTest(matcher, param, "benchmark"+d);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
		
		//anatomy
		add = 0.05;
		for (int j = 0; j < 7; j++) {
			double d = 0.5 + ((double)j)*add;
			OAEI2010Matcher matcher = new OAEI2010Matcher();
			matcher.setName("OEAI10-anatomy"+d);
			OAEI2010MatcherParameters param = new OAEI2010MatcherParameters(OAEI_Track.Anatomy);
			
			matcher.setThreshold(d);
			param.threshold= d;
			System.out.println("THRESHOLD"+matcher.getThreshold());
			
			try {
				i.addTest(matcher, param, "anatomy"+d);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
		
		//conference
		add = 0.05;
		for (int j = 0; j < 7; j++) {
			double d = 0.5 + ((double)j)*add;
			OAEI2010Matcher matcher = new OAEI2010Matcher();
			matcher.setName("OEAI10-conference"+d);
			OAEI2010MatcherParameters param = new OAEI2010MatcherParameters(OAEI_Track.Conference);
			
			matcher.setThreshold(d);
			param.threshold= d;
			System.out.println("THRESHOLD"+matcher.getThreshold());
			
			try {
				i.addTest(matcher, param, "conference"+d);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
		
		
		
		//add the test to the list of matcher i want to try
		
		
		// create the training set object from the file of ontologies and reference allignment and the list of matcher
		OutputTrainingGenerator o = createTrainingSet("OAEI2011/path.xml", i);
		
		//save the object on disk
		try {
			o.storeFile("Classification/prova2.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	
		//load the object from file
		OutputTrainingGenerator o2 = new OutputTrainingGenerator("Classification/prova2.xml");
		//o2.storeTableVisual("Classification/visualTable.txt");
			
		//create the model with the output training
		//OntologyClassificator oc = new OntologyClassificator(o2,ClassificatorRegistry.C_NaiveBayes);
		OntologyClassificator oc = new OntologyClassificator(o2);
		

		
		//train the model with the 
		try {
			oc.trainCoupleOntology(o2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	/*	
		//store the model
		oc.storeModel("Classification/test3.model");
		System.out.println("MODEL STORED!");
		
		*/
		
	/*	
		
		//load a model // ATTENTION! YOU HAVE ALWAYS TO SPECIFY THE OutputTG! 
		OutputTrainingGenerator o3 = new OutputTrainingGenerator("Classification/prova2.xml");
		OntologyClassificator oc2 = new OntologyClassificator(o3,"Classification/test3.model");
		
		
		*/
		//create the test set of ontology from file
		TestSet testSet = new TestSet("Classification/testOntoThreshold.xml");
		
		
		
		
		/**/
		
		
		/*
		*/
	
		//Evaluate the model 
		String evaluation = oc.testModel(o2, testSet);
		System.out.println(evaluation);
		
		
		//classified a set of ontologies
		String [] result = oc.classifiedCoupleOntology(testSet);
		for (int j = 0; j < result.length; j++) {
			System.out.println(result[j]);
		}
		
		
	/*	
		// cross Validate the model
		oc2.crossValidation(o3, 10, 3);
		
		
		
		//classified two ontology
		Ontology sourceOntology = OntologyClassificator.openOntology("OAEI2011/conference/confOf.owl");
		Ontology targetOntology = OntologyClassificator.openOntology("OAEI2011/conference/sigkdd.owl");
		String cl = OntologyClassificator.classifiedOntologiesST(sourceOntology,targetOntology,"Classification/test3.model", "Classification/prova2.xml");
		System.out.println(cl);
		
		
		*/
		
		
		
		
	}
	
	
public static void testClassified() {
		
	
		//load the object from file
		OutputTrainingGenerator o2 = new OutputTrainingGenerator("Classification/prova2.xml");
		//o2.storeTableVisual("Classification/visualTable.txt");
			
		//create the model with the output training
		//OntologyClassificator oc = new OntologyClassificator(o2,ClassificatorRegistry.C_NaiveBayes);
		//OntologyClassificator oc = new OntologyClassificator(o2);
		try {
		for (ClassificatorRegistry cl : ClassificatorRegistry.values()) {
			System.out.println("#########"+cl.name());
			OntologyClassificator oc = new OntologyClassificator(o2,cl);
			//train the model with the 
			oc.trainCoupleOntology(o2);

		}
		
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	
		
		
		
		
		
	}
	
	

	/*
	 * for each couple of ontology and reference alignment gets Fmeasure.. 
	 * 
	 */
	
	private static void test(String fileName) {
		// open and parse the benchmark XML file
		try {
			System.out.println("Reading batch file.");
			File batchFile = new File( fileName );
			
			PrintStream output = new PrintStream(new FileOutputStream("table2.txt"));
			PrintStream trainOntologies = new PrintStream(new FileOutputStream("trainontologies2.txt"));
			PrintStream classes = new PrintStream(new FileOutputStream("classes2.txt"));

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(batchFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element " + doc.getDocumentElement().getNodeName());

			String outputPrefix = doc.getDocumentElement().getAttribute("title");

/*			// parse the parameters
			Element parameters = (Element) doc.getElementsByTagName("parameters").item(0);

			Element cardinality = (Element) parameters.getElementsByTagName("cardinality").item(0);
			String cardinalitySource = cardinality.getAttribute("source");
			if( cardinalitySource.equalsIgnoreCase("any") ) { prefSourceCardinality = AbstractMatcher.ANY_INT; }
			else { prefSourceCardinality = Integer.parseInt(cardinalitySource); }
			String cardinalityTarget = cardinality.getAttribute("target");
			if( cardinalityTarget.equalsIgnoreCase("any") ) { prefTargetCardinality = AbstractMatcher.ANY_INT; }
			else { prefTargetCardinality = Integer.parseInt(cardinalityTarget); }

			Element threshold = (Element) parameters.getElementsByTagName("threshold").item(0);
			String thStart = threshold.getAttribute("start");
			prefStartThreshold = Float.parseFloat(thStart);
			String thInc = threshold.getAttribute("increment");
			prefThresholdIncrement = Float.parseFloat(thInc);
			String thEnd = threshold.getAttribute("end");
			prefEndThreshold = Float.parseFloat(thEnd);*/

			// parse the Runs
			NodeList runList = doc.getElementsByTagName("run");
			for( int i = 0; i < runList.getLength(); i++ ) {

				Element currentRun = (Element) runList.item(i);
				String runName = currentRun.getAttribute("name");

				Element sourceOntology = (Element) currentRun.getElementsByTagName("sourceOntology").item(0);

				String sourceOntologyFile = sourceOntology.getAttribute("filename");
				String sourceOntologyName = sourceOntology.getAttribute("name");


				Element targetOntology = (Element) currentRun.getElementsByTagName("targetOntology").item(0);

				String targetOntologyFile = targetOntology.getAttribute("filename");
				String targetOntologyName = targetOntology.getAttribute("name");


				Element referenceAlignment = (Element) currentRun.getElementsByTagName("referenceAlignment").item(0);

				String referenceAlignmentFile = referenceAlignment.getAttribute("filename");

				//System.out.println("Running analysis for " + sourceOntologyName + " to " + targetOntologyName);
				System.out.println("SourceOntoPath " + sourceOntologyFile + " targetOntoPath " + targetOntologyFile + "REf File: "+ referenceAlignmentFile);
				////runAnalysis( sourceOntologyFile, sourceOntologyName, targetOntologyFile, targetOntologyName, referenceAlignmentFile );
				
				
				
				//MatchersRegistry matcher=MatchersRegistry.OAEI2010; //TODO mettere 2011!!!
				OAEI2010MatcherParameters param = null;
				
				OAEI2010Matcher matcher = new OAEI2010Matcher();
				
				output.print(sourceOntologyName + "\t" + targetOntologyName + "\t");
				
				
				//open the source ontology
				Ontology sourceOnto = loadOntology(sourceOntologyFile);
				Core.getInstance().setSourceOntology(sourceOnto);
				//open the target ontology
				Ontology targetOnto = loadOntology(targetOntologyFile);
				Core.getInstance().setTargetOntology(targetOnto);
				
				//built the lexicons.. 
				buildLex(sourceOnto, targetOnto);
				
				//benchmarks configuration
				param = new OAEI2010MatcherParameters(OAEI_Track.Benchmarks);
				matcher.setParam(param);
				double [] d1 = runAnalysis(sourceOntologyFile, sourceOntologyName, targetOntologyFile, targetOntologyName, referenceAlignmentFile, matcher, param );
				output.print(d1[0] + "\t" + d1[1] + "\t" + d1[2] + "\t");
				
				//anatomy
				param = new OAEI2010MatcherParameters(OAEI_Track.Anatomy);
				matcher.setParam(param);
				double[] d2 = runAnalysis( sourceOntologyFile, sourceOntologyName, targetOntologyFile, targetOntologyName, referenceAlignmentFile, matcher, param );
				output.print(d2[0] + "\t" + d2[1] + "\t" + d2[2] + "\t");
				
				//conference
				param = new OAEI2010MatcherParameters(OAEI_Track.Conference);
				matcher.setParam(param);
				double[] d3 = runAnalysis( sourceOntologyFile, sourceOntologyName, targetOntologyFile, targetOntologyName, referenceAlignmentFile, matcher, param );
				output.println(d3[0] + "\t" + d3[1] + "\t" + d3[2]);
				
				//print on a file the two ontology and the better class
				trainOntologies.println(sourceOntologyFile);
				trainOntologies.println(targetOntologyFile);
				classes.println(getClasses(d1[2],d2[2],d3[2]));
			}


		} catch(Exception e) {
			System.out.println("Analysis aborted."+e);
		}




	}
	
	
	public static OutputTrainingGenerator createTrainingSet(String sourceFileName, InputTrainingGenerator input) {

		LinkedList<String> classList = input.getClassList();
		LinkedList<InputTest> inputList = input.getTestList();
		int numClasses = classList.size();
		OutputTrainingGenerator out = new OutputTrainingGenerator();
		out.setClassList(classList);

		// open and parse the benchmark XML file

		System.out.println("Reading batch file.");
		File batchFile = new File( sourceFileName );

		//PrintStream output = new PrintStream(new FileOutputStream("table2.txt"));
		//PrintStream trainOntologies = new PrintStream(new FileOutputStream("trainontologies2.txt"));
		//PrintStream classes = new PrintStream(new FileOutputStream("classes2.txt"));

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc= null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			doc = db.parse(batchFile);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
		System.out.println("Root element " + doc.getDocumentElement().getNodeName());

		String outputPrefix = doc.getDocumentElement().getAttribute("title");


		// parse the Runs
		NodeList runList = doc.getElementsByTagName("run");
		for( int i = 0; i < runList.getLength(); i++ ) {

			Element currentRun = (Element) runList.item(i);
			String runName = currentRun.getAttribute("name");

			Element sourceOntology = (Element) currentRun.getElementsByTagName("sourceOntology").item(0);

			String sourceOntologyFile = sourceOntology.getAttribute("filename");
			String sourceOntologyName = sourceOntology.getAttribute("name");


			Element targetOntology = (Element) currentRun.getElementsByTagName("targetOntology").item(0);

			String targetOntologyFile = targetOntology.getAttribute("filename");
			String targetOntologyName = targetOntology.getAttribute("name");


			Element referenceAlignment = (Element) currentRun.getElementsByTagName("referenceAlignment").item(0);

			String referenceAlignmentFile = referenceAlignment.getAttribute("filename");

			//System.out.println("Running analysis for " + sourceOntologyName + " to " + targetOntologyName);
			System.out.println("SourceOntoPath " + sourceOntologyFile + " targetOntoPath " + targetOntologyFile + "REf File: "+ referenceAlignmentFile);
			////runAnalysis( sourceOntologyFile, sourceOntologyName, targetOntologyFile, targetOntologyName, referenceAlignmentFile );

			//output.print(sourceOntologyName + "\t" + targetOntologyName + "\t");

			//open the source ontology
			Ontology sourceOnto = loadOntology(sourceOntologyFile);
			Core.getInstance().setSourceOntology(sourceOnto);
			//open the target ontology
			Ontology targetOnto = loadOntology(targetOntologyFile);
			Core.getInstance().setTargetOntology(targetOnto);

			//built the lexicons.. 
			buildLex(sourceOnto, targetOnto);

			double maxFMeasure = 0.0;
			String maxClass = classList.getFirst();
			for (Iterator<InputTest> it = inputList.iterator(); it.hasNext();) {
				InputTest inputTest = (InputTest) it.next();
				AbstractMatcher matcher = inputTest.getMatcher();
				AbstractParameters param = inputTest.getParam();
				String inputClass = inputTest.getClassName();
				//System.out.println("THRESHOLD BEFORE SETPARAM"+matcher.getThreshold());
				matcher.setParam(param); //TODO : it is necessary here?
				//System.out.println("THRESHOLD AFTER SETPARAM"+matcher.getThreshold());

				double [] d = runAnalysis(sourceOntologyFile, sourceOntologyName, targetOntologyFile, targetOntologyName, referenceAlignmentFile, matcher, param );
				//output.print(d[0] + "\t" + d[1] + "\t" + d[2] + "\t");

				String matcherName = "undefinedMatcherName";
				try {
					matcherName = matcher.getName();
				} catch (Exception e) {
					matcherName = "undefinedMatcherName";
				}
				
				out.addResult(inputClass, matcherName, param.toString(), sourceOntologyFile, targetOntologyFile,sourceOntologyName, targetOntologyName, d[0], d[1], d[2]);

				if (d[2]>maxFMeasure){
					maxFMeasure= d[2];
					maxClass = inputClass;
				}

			}

			out.addWinner(maxClass, sourceOntologyFile, targetOntologyFile);

			
		// if we want to consider also the opposite 
			//open the source ontology
	/*	
			targetOnto = loadOntology(sourceOntologyFile);
			//open the target ontology
			sourceOnto = loadOntology(targetOntologyFile);
			String x = sourceOntologyFile.toString();
			sourceOntologyFile = targetOntologyFile.toString();
			targetOntologyFile = x.toString();
			
			String y = sourceOntologyName.toString();
			sourceOntologyName = targetOntologyName.toString();
			targetOntologyName = y.toString();
			Core.getInstance().setTargetOntology(targetOnto);
			Core.getInstance().setSourceOntology(sourceOnto);
				
			//built the lexicons.. 
				buildLex(sourceOnto, targetOnto);

			maxFMeasure = 0.0;
			maxClass = classList.getFirst();
			for (Iterator<InputTest> it = inputList.iterator(); it.hasNext();) {
				InputTest inputTest = (InputTest) it.next();
				AbstractMatcher matcher = inputTest.getMatcher();
				AbstractParameters param = inputTest.getParam();
				String inputClass = inputTest.getClassName();
				//System.out.println("THRESHOLD BEFORE SETPARAM"+matcher.getThreshold());
				matcher.setParam(param); //TODO : it is necessary here?
				//System.out.println("THRESHOLD AFTER SETPARAM"+matcher.getThreshold());

				double [] d = runAnalysis(sourceOntologyFile, sourceOntologyName, targetOntologyFile, targetOntologyName, referenceAlignmentFile, matcher, param );
				//output.print(d[0] + "\t" + d[1] + "\t" + d[2] + "\t");

				String matcherName = "undefinedMatcherName";
				try {
					matcherName = matcher.getName();
				} catch (Exception e) {
					matcherName = "undefinedMatcherName";
				}
				
				out.addResult(inputClass, matcherName, param.toString(), sourceOntologyFile, targetOntologyFile,sourceOntologyName, targetOntologyName, d[0], d[1], d[2]);

				if (d[2]>maxFMeasure){
					maxFMeasure= d[2];
					maxClass = inputClass;
				}

			}

			out.addWinner(maxClass, sourceOntologyFile, targetOntologyFile);
			
			*/
			
			
			
			
			
			
			
			
			
			
			

			//print on a file the two ontology and the better class
			//trainOntologies.println(sourceOntologyFile);
			//trainOntologies.println(targetOntologyFile);
			//classes.println(getClasses(d1[2],d2[2],d3[2]));
		}

		return out;


	}

	
	
	
	
	
	private static String getClasses(double d1, double d2, double d3) {
		
		double max = Math.max(d1, d2);
		max = Math.max(max, d3);
	
		if (max == d1) return "benchmark";
		if (max == d2) return "anatomy";
		if (max == d3) return "conference";
		
		return null;
	}

	public static void buildLex(Ontology sourceOnto, Ontology targetOnto){
		LexiconBuilderParameters params = new LexiconBuilderParameters();
		
		//TODO: how i have to set these parameters? 
		params.sourceOntology = sourceOnto;
		params.targetOntology = targetOnto;
		
		params.sourceUseLocalname = true;
		params.targetUseLocalname = true;
		
		params.sourceUseSCSLexicon = true;
		params.targetUseSCSLexicon = true;
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		
		// create the source property
		ArrayList<Property> sourceProperties = new ArrayList<Property>();
		for( Node classNode : sourceOntology.getClassesList() ) ManualOntologyProfiler.createClassAnnotationsList(sourceProperties, classNode);
		for( Node propertyNode : sourceOntology.getPropertiesList() ) ManualOntologyProfiler.createPropertyAnnotationsList(sourceProperties, propertyNode);
		Collections.sort(sourceProperties, new LocalnameComparator());
		
		// create the target property
		ArrayList<Property> targetProperties = new ArrayList<Property>();
		for( Node classNode : targetOntology.getClassesList() ) ManualOntologyProfiler.createClassAnnotationsList(targetProperties, classNode);
		for( Node propertyNode : targetOntology.getPropertiesList() ) ManualOntologyProfiler.createPropertyAnnotationsList(targetProperties, propertyNode);
		Collections.sort(targetProperties, new LocalnameComparator());
		
		
		
		params.sourceSynonyms = new ArrayList<Property>();
		for( int i = 0; i < sourceProperties.size(); i++ ) {
			//if( sourceSynonym.isSelectedIndex(i) ) 
				params.sourceSynonyms.add(sourceProperties.get(i));
		}
		
		params.sourceDefinitions = new ArrayList<Property>();
		for( int i = 0; i < sourceProperties.size(); i++ ) {
			//if( sourceDefinition.isSelectedIndex(i) ) 
				params.sourceDefinitions.add(sourceProperties.get(i));
		}
		
		params.targetSynonyms = new ArrayList<Property>();
		for( int i = 0; i < targetProperties.size(); i++ ) {
			//if( targetSynonym.isSelectedIndex(i) ) 
				params.targetSynonyms.add(targetProperties.get(i));
		}
		
		params.targetDefinitions = new ArrayList<Property>();
		for( int i = 0; i < targetProperties.size(); i++ ) {
			//if( targetDefinition.isSelectedIndex(i) ) 
				params.targetDefinitions.add(targetProperties.get(i));
		}
		
		
		/*OntModel sourceOntModel = sourceOntology.getModel();
		params.sourceLabelProperties = new ArrayList<Property>();
		Property sourceRDFSLabel = sourceOntModel.getProperty(Ontology.RDFS + "label"); // TODO: Make this customizable by the user.
		if( sourceRDFSLabel == null ) {
			// source ontology does not have RDFS label defined???
			params.sourceLabelProperties.addAll( params.sourceSynonyms );
		} else {
			// choose rdfs:label as the label property
			params.sourceLabelProperties.add( sourceRDFSLabel );
		}*/
		
		
		/*OntModel targetOntModel = targetOntology.getModel();
		params.targetLabelProperties = new ArrayList<Property>();
		Property targetRDFSLabel = targetOntModel.getProperty(Ontology.RDFS + "label"); // TODO: Make this customizable by the user.
		if( targetRDFSLabel == null ) {
			// target ontology does not have RDFS label defined???
			params.targetLabelProperties.addAll( params.targetSynonyms );
		} else {
			// choose rdfs:label as the label property
			params.targetLabelProperties.add( targetRDFSLabel );
		}*/
					
		Core.getLexiconStore().setParameters(params);
		
		try {
			Core.getLexiconStore().buildAll();
			//setVisible(false);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Utility.displayErrorPane("Unexpected error while building lexicons.\n" + e1.getMessage(), "Runtime Exception");
		}
	}
	
	
/*	public static Ontology loadOntology(String fileName){
		System.out.println("Loading ontology " +  fileName);
		// load source ontology
		Ontology sourceOntology = null;
		try {
			OntoTreeBuilder sourceBuilder = new OntoTreeBuilder(fileName , 
																GlobalStaticVariables.SOURCENODE, 
																GlobalStaticVariables.LANG_OWL,
																GlobalStaticVariables.SYNTAX_RDFXML, false);
			sourceBuilder.build();
			
			sourceOntology = sourceBuilder.getOntology();
			return sourceOntology;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}*/
	
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
	
	
	

	private static double[] runAnalysis(String sourceOntologyFile,
			String sourceOntologyName, String targetOntologyFile,
			String targetOntologyName, String referenceAlignmentFile,
			AbstractMatcher matcherToAnalyze, AbstractParameters prefParams) {

		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		double [] result = new double[3]; 
		

				
		//AbstractMatcher matcherToAnalyze = MatcherFactory.getMatcherInstance(matcher, 0);
		int prefSourceCardinality = 1;
		int prefTargetCardinality = 1;
		
		
		// set the settings for the matcher			
		matcherToAnalyze.setSourceOntology(sourceOntology);
		matcherToAnalyze.setTargetOntology(targetOntology);
		matcherToAnalyze.setPerformSelection(false);
		if( matcherToAnalyze.needsParam() ) matcherToAnalyze.setParam(prefParams);
		matcherToAnalyze.setMaxSourceAlign(prefSourceCardinality);
		matcherToAnalyze.setMaxTargetAlign(prefTargetCardinality);
		
		System.out.println("THRESHOLD BEFORE MATCH"+matcherToAnalyze.getThreshold());
			
		try {
			matcherToAnalyze.match();
		} catch (Exception e) {
			e.printStackTrace();
			//return;
		}
		
		// load the reference file
		ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
		refParam.onlyEquivalence = true;
		refParam.fileName = referenceAlignmentFile;
		refParam.format = ReferenceAlignmentMatcher.OAEI;
		AbstractMatcher referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
		referenceAlignmentMatcher.setParam(refParam);
		referenceAlignmentMatcher.setSourceOntology(sourceOntology);
		referenceAlignmentMatcher.setTargetOntology(targetOntology);
		
		try {
			referenceAlignmentMatcher.match();
		} catch (Exception e) {
			System.out.println("Analysis aborted.1"+e);
			return null;
		}
		
		// open the output files
	/*	File outputPrecision = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-precision.txt");
		File outputRecall = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-recall.txt");
		File outputFMeasure = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-fmeasure.txt");
		File outputMaxFM = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-max-fmeasure.txt");
		
		
		try {
			
			BufferedWriter writerPrecision = new BufferedWriter( new FileWriter(outputPrecision) );
			BufferedWriter writerRecall = new BufferedWriter( new FileWriter(outputRecall) );
			BufferedWriter writerFMeasure = new BufferedWriter( new FileWriter(outputFMeasure) );
			BufferedWriter writerMaxFM = new BufferedWriter( new FileWriter(outputMaxFM) );
			
			
			// ok, we ran the matcher, now do the threshold analysis
			
			double maxFMeasure = 0.0;
			double maxFMTh = 0.0;
			
			for( double currentThreshold = prefStartThreshold; currentThreshold < prefEndThreshold; currentThreshold += prefThresholdIncrement) {
				
				currentThreshold = Utility.roundDouble(currentThreshold, 4);
				
				log.info("Selecting with threshold = " + currentThreshold );
				matcherToAnalyze.setThreshold(currentThreshold);
				matcherToAnalyze.select();
							*/
				ReferenceEvaluationData currentEvaluation = ReferenceEvaluator.compare(matcherToAnalyze.getAlignment(), referenceAlignmentMatcher.getAlignment());
				
		//		writerPrecision.write(currentThreshold + "," + Utility.roundDouble( currentEvaluation.getPrecision(), 2) + "\n");
		//		writerRecall.write(currentThreshold + "," + Utility.roundDouble( currentEvaluation.getRecall(), 2) + "\n");
		//		writerFMeasure.write(currentThreshold + "," + Utility.roundDouble( currentEvaluation.getFmeasure(), 2) + "\n");
				
				System.out.println("O1: "+sourceOntologyName + " O2: "+ targetOntologyName);
				
				
				result[0] = Utility.roundDouble( currentEvaluation.getPrecision() * 100.0d, 2);
				result[1] = Utility.roundDouble( currentEvaluation.getRecall() * 100.0d, 2);
				result[2] = Utility.roundDouble( currentEvaluation.getFmeasure()* 100.0d, 2);
				
				System.out.println("Results: (precision, recall, f-measure) = (" + 
					Utility.roundDouble( currentEvaluation.getPrecision() * 100.0d, 2) + ", " + 
					Utility.roundDouble( currentEvaluation.getRecall() * 100.0d, 2) + ", " +
					Utility.roundDouble( currentEvaluation.getFmeasure()* 100.0d, 2) + ")");
				System.out.println("       : (found mappings, correct mappings, reference mappings) = (" + 
							currentEvaluation.getFound() + ", " + currentEvaluation.getCorrect() + ", " + currentEvaluation.getExist() + ")");
				
				
				
				
				/*
				if( maxFMeasure < currentEvaluation.getFmeasure() ) {
					maxFMeasure = currentEvaluation.getFmeasure();
					maxFMTh = Utility.roundDouble(currentThreshold*100f, 4);
				}
				
			}
			
			writerMaxFM.write( Utility.roundDouble( maxFMTh, 2) + ", " + Utility.roundDouble( maxFMeasure, 2) );
			
			writerPrecision.close();
			writerRecall.close();
			writerFMeasure.close();
			writerMaxFM.close();
			
		} catch (IOException e) {
			// cannot create files
			e.printStackTrace();
			return;
		}*/
		

		
		// analysis done
		return result; 
		
	}
	
	

}
