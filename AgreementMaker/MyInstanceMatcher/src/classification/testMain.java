package classification;

import java.util.LinkedList;
import java.util.List;

import am.GlobalStaticVariables;
import am.app.Core;
import am.app.mappingEngine.oaei.OAEI_Track;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

import weka.classifiers.lazy.KStar;
import weka.core.Instance;
import weka.core.Instances;

public class testMain {

/*	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OntoTreeBuilder treeBuilder = new OntoTreeBuilder(ontoName, GlobalStaticVariables.SOURCENODE,
					GlobalStaticVariables.LANG_OWL, 
					GlobalStaticVariables.SYNTAX_RDFXML, false, true);
			treeBuilder.build();
			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}

	
	public static void testOnto(){
		
		Ontology o1 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/anatomy/mouse_anatomy_2008.txt");
		Ontology o2 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/anatomy/nci_anatomy_2008.txt");
		Ontology o3 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/conference/cmt.owl");
		Ontology o4 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/conference/Cocus.owl");
		Ontology b1 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/benchmarks/101/onto.rdf");
		Ontology b2 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/benchmarks/301/onto.rdf");
		Ontology b3 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/benchmarks/302/onto.rdf");
		
		CoupleOntology train1 = new CoupleOntology(o1,o2);
		CoupleOntology train2 = new CoupleOntology(o3,o4);
		CoupleOntology train3 = new CoupleOntology(b1,b2);
		CoupleOntology train4 = new CoupleOntology(b1,b3);
		
		
		System.out.println("1");
		Ontology o7 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/benchmarks/303/onto.rdf");
		Ontology o8 = openOntology("/Users/alessio/Dropbox/PROGRAMMAZIONE/Eclipse Workspace/AgreementMaker/OAEI09/benchmarks/304/onto.rdf");
		System.out.println("2");

		CoupleOntology test1 = new CoupleOntology(b1,o7);
		CoupleOntology test2 = new CoupleOntology(b1,o8);
		LinkedList<CoupleOntology> testList = new LinkedList<CoupleOntology>();
		testList.add(test1);
		testList.add(test2);
		
		OntologyClassificator oc = new OntologyClassificator();
		//OntologyClassificator oc = new OntologyClassificator("/Users/alessio/Dropbox/PROGRAMMAZIONE/test.model");
		//Instance instance = oc.createTrainingInstanceFromMetrics(om, "anatomy");
		
		
		OntologyMetrics[] ll = {om1,om2};
		
		LinkedList<String> ls = new LinkedList<String>();
		ls.add("anatomy");
		ls.add("anatomy");
		Instances instances = oc.createTrainingInstancesFromMetrics(ll,ls);
		
		
		//createTrainingInstancesFromMetrics(OntologyMetrics[] ontoMetrics, List<String> ontoClass )
		
		
		
		LinkedList<CoupleOntology> ll = new LinkedList<CoupleOntology>();
		ll.add(train1);
		ll.add(train2);
		ll.add(train3);
		ll.add(train4);
		//ll.add(train4);
		//ll.add(train5);
		//ll.add(train6);
		LinkedList<String> ls = new LinkedList<String>();
		ls.add("anatomy");
		//ls.add("anatomy");
		ls.add("conference");
		//ls.add("conference");
		ls.add("benchmark");
		//ls.add("benchmark");
		ls.add("benchmark");
		
		try {
			oc.trainCoupleOntology(ll, ls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//oc.storeModel("/Users/alessio/Dropbox/PROGRAMMAZIONE/test.model");
		
		
		
		
		String[] s = oc.classifiedCoupleOntology(testList);
		for (int i = 0; i < s.length; i++) {
			System.out.println(s[i]);
			
		}
		
	}
	*/
	
	public static void main(String[] args) {
		
		/*//CREATE THE TRAINING SET AND POPULATE IT
		TrainSet trainSet = new TrainSet();*/
		//ADD THE LIST OF CLASSES
		/*trainSet.addClasses("Match");
		trainSet.addClasses("noMatch");*/
		//ADD THE TRAINING VALUES WITH THE CLASSES
		/*trainSet.addTrain(1.2,3.4,5.3,"noMatch");
		trainSet.addTrain(65,33.44,25.13,"noMatch");
		trainSet.addTrain(13.2,13.4,5.13,"Match");
		trainSet.addTrain(14.2,23.4,5.23,"noMatch");
		trainSet.addTrain(15.2,33.4,5.33,"Match");
		trainSet.addTrain(16.2,43.4,5.43,"noMatch");*/
		
		//IF YOU WANT TO STORE THE TRAINING SET IN AN XML
//		System.out.println(trainSet);
//		try {
//			trainSet.storeFile("Classification/fede/train.xml");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//IF YOU WANT TO LOAD THE TRAINING SET FROM AN XML
		TrainSet trainSet = new TrainSet("trainingSet.xml");

		//System.out.println(trainSet);
		
		//CREATE THE TEST SET AND POPULATE IT
		TestSet testSet= new TestSet("testingSet.xml");
		//ADD THE TESTING VALUES
		//IF YOU WANT TO EVALUATE THE MODEL YOU NEED ALSO THE CLASS
		//OTHERWISE YOU CAN SIMPLY IGNORE: testSet.addTest(1.2,3.4,5.3);
		/*testSet.addTest(1.2,3.4,5.3,"noMatch");
		testSet.addTest(65,33.44,25.13,"noMatch");
		testSet.addTest(12.2,14.4,55.13,"Match");
		testSet.addTest(14.2,23.4,5.23,"noMatch");
		testSet.addTest(16.2,35.4,5.63,"Match");
		testSet.addTest(16.2,43.4,5.43,"noMatch");*/
		
		//IF YOU WANT TO STORE THE TRAINING SET IN AN XML
	/*	System.out.println(testSet);
		try {
			testSet.storeFile("Classification/fede/test.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//IF YOU WANT TO LOAD THE TESTING SET FROM AN XML
		testSet = new TestSet("Classification/fede/test.xml");

		System.out.println(testSet);*/
		
		//CREATE THE CLASSIFICATOR, YOU CAN CHOOSE THE TYPE FROM REGISTRY OR LOAD FROM FILE
		Classificator c = new Classificator(trainSet,ClassificatorRegistry.C_J48);
		//TRAIN THE CLASSIFICATOR
		System.out.println("Training model...");
		
		try {
			c.trainModel(trainSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
		
		//IF YOU WANT TO STORE THE CLASSIFICATOR
		c.storeModel("peopleClassificator.model");
		
		//IF YOU WANT TO LOAD THE CLASSIFICATOR
	//	Classificator c2 = new Classificator(trainSet,"Classification/fede/classificator.model");
		
		
		//CLASSIFIED THE TEST SET, IT RETURNS AN ARRAY OF STRING WITH THE CLASSES
//		String[] s = c.classifiedModel(testSet);
//		for (int i = 0; i < s.length; i++) {
//			System.out.println(s[i]);
//		}
//		
		//FOR EVALUATE THE MODEL
		System.out.println("Evaluating model...");
		String s1 = c.evaluateModel(trainSet, testSet);
		System.out.println("Done");
		System.out.println(s1);
		
		//FOR CROSS VALIDATE THE MODEL
//		c.crossValidation(trainSet, 10, 3);
//
		double[][] confidence = c.getConfidence(testSet);
//		for (int i = 0; i < confidence.length; i++) {
//			for (int j = 0; j < confidence[i].length; j++) {
//				System.out.print(confidence[i][j]);
//			}
//			System.out.println();
//		}
		
	}

/*
	private static void testFile() {
		
		String fileNameOntologiesTraining = "Classification/trainontologies.txt";
		String fileNameClasses = "Classification/classes.txt";
		//String fileNameOntologiesTest = "/Users/alessio/Dropbox/PROGRAMMAZIONE/test.txt";
		//String fileNameClassesTest = "/Users/alessio/Dropbox/PROGRAMMAZIONE/classesTest.txt";
		
		
		OntologyClassificator oc = new OntologyClassificator();
		LinkedList<String> listaClassi =  oc.loadClassesFromFile(fileNameClasses);
		//LinkedList<String> listaClassiTest =  oc.loadClassesFromFile(fileNameClassesTest);
		CoupleOntologyMetrics[] coupleTraining = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTraining);
		//CoupleOntologyMetrics[] coupleTesting = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTest);
		
		try {
			oc.trainCoupleOntology(coupleTraining, listaClassi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oc.storeModel("Classification/test.model");
		System.out.println("MODEL STORED!");
		
		String [] result = oc.classifiedCoupleOntology(coupleTesting);
		
		
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
		
		String evaluation = oc.testModel(coupleTraining, listaClassi, coupleTesting, listaClassiTest);
		System.out.println(evaluation);
		
		
	}
	
	private static void testLoadModel() {
		
		//String fileNameOntologiesTraining = "Classification/trainontologies.txt";
		//String fileNameClasses = "Classification/classes.txt";
		String fileNameOntologiesTest = "Classification/test.txt";
		//String fileNameClassesTest = "Classification/classesTest.txt";
		
		
		OntologyClassificator oc = new OntologyClassificator("Classification/test.model");
		//LinkedList<String> listaClassi =  oc.loadClassesFromFile(fileNameClasses);
		//LinkedList<String> listaClassiTest =  oc.loadClassesFromFile(fileNameClassesTest);
		//CoupleOntologyMetrics[] coupleTraining = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTraining);
		CoupleOntologyMetrics[] coupleTesting = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTest);
		
		
		
		
		
		String [] result = oc.classifiedCoupleOntology(coupleTesting);
		
		
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
		
		
	}
	
private static void testEvaluation() {
		
		String fileNameOntologiesTraining = "Classification/trainontologies.txt";
		String fileNameClasses = "Classification/classes.txt";
		String fileNameOntologiesTest = "Classification/test.txt";
		String fileNameClassesTest = "Classification/classesTest.txt";
		
		
		OntologyClassificator oc = new OntologyClassificator("Classification/test.model");
		LinkedList<String> listaClassi =  oc.loadClassesFromFile(fileNameClasses);
		LinkedList<String> listaClassiTest =  oc.loadClassesFromFile(fileNameClassesTest);
		CoupleOntologyMetrics[] coupleTraining = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTraining);
		CoupleOntologyMetrics[] coupleTesting = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTest);
		
		
		
		String evaluation = oc.testModel(coupleTraining, listaClassi, coupleTesting, listaClassiTest);
		System.out.println(evaluation);
		
		
}

private static void testCrossValidation() {
	
	String fileNameOntologiesTraining = "Classification/trainontologies.txt";
	String fileNameClasses = "Classification/classes.txt";
	//String fileNameOntologiesTest = "Classification/test.txt";
	//String fileNameClassesTest = "Classification/classesTest.txt";
	
	
	OntologyClassificator oc = new OntologyClassificator("Classification/test.model");
	LinkedList<String> listaClassi =  oc.loadClassesFromFile(fileNameClasses);
	//LinkedList<String> listaClassiTest =  oc.loadClassesFromFile(fileNameClassesTest);
	CoupleOntologyMetrics[] coupleTraining = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTraining);
	//CoupleOntologyMetrics[] coupleTesting = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTest);
	
	
	oc.crossValidation(coupleTraining, listaClassi, 10, 3);
	//String evaluation = oc.testModel(coupleTraining, listaClassi, coupleTesting, listaClassiTest);
	//System.out.println(evaluation);
	
	
}


	private static OAEI_Track testOntologyST(Ontology sourceOntology,Ontology targetOntology) {
	
	
		OntologyClassificator oc = new OntologyClassificator("Classification/test.model");
	
		OntologyEvaluation eval1 = new OntologyEvaluation();
		OntologyEvaluation eval2 = new OntologyEvaluation();
		CoupleOntologyMetrics com = new CoupleOntologyMetrics(eval1.evaluateOntology(sourceOntology), eval2.evaluateOntology(targetOntology));
		CoupleOntologyMetrics[] coupleTesting = new CoupleOntologyMetrics[1];
		coupleTesting[0] = com;
		
		String [] result = oc.classifiedCoupleOntology(coupleTesting);
		
		if (result[0].equals("anatomy")) return OAEI_Track.Anatomy;
		if (result[0].equals("benchmark")) return OAEI_Track.Benchmarks;
		if (result[0].equals("conference")) return OAEI_Track.Conference;
			return OAEI_Track.Benchmarks;
	
	}
*/

}
