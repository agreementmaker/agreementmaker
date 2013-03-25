package am.app.ontology.profiling.classification;

import java.util.LinkedList;

import am.GlobalStaticVariables;
import am.app.mappingEngine.utility.OAEI_Track;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.profiling.ontologymetrics.CoupleOntologyMetrics;
import am.app.ontology.profiling.ontologymetrics.OntologyEvaluation;

public class testMain {

	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OntoTreeBuilder treeBuilder = new OntoTreeBuilder(ontoName,
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
		
		OntologyClassifier oc = new OntologyClassifier();
		//OntologyClassificator oc = new OntologyClassificator("/Users/alessio/Dropbox/PROGRAMMAZIONE/test.model");
		//Instance instance = oc.createTrainingInstanceFromMetrics(om, "anatomy");
		
		
	/*	OntologyMetrics[] ll = {om1,om2};
		
		LinkedList<String> ls = new LinkedList<String>();
		ls.add("anatomy");
		ls.add("anatomy");
		Instances instances = oc.createTrainingInstancesFromMetrics(ll,ls);
		*/
		
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
	
	
	public static void main(String[] args) {
		//testOnto();
		testFile();
		//testLoadModel();
		//testEvaluation();
		//testOntologyST();
		testCrossValidation();
		
	}


	private static void testFile() {
		
		String fileNameOntologiesTraining = "Classification/trainontologies.txt";
		String fileNameClasses = "Classification/classes.txt";
		//String fileNameOntologiesTest = "/Users/alessio/Dropbox/PROGRAMMAZIONE/test.txt";
		//String fileNameClassesTest = "/Users/alessio/Dropbox/PROGRAMMAZIONE/classesTest.txt";
		
		
		OntologyClassifier oc = new OntologyClassifier();
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
		
	/*	String [] result = oc.classifiedCoupleOntology(coupleTesting);
		
		
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
		
		String evaluation = oc.testModel(coupleTraining, listaClassi, coupleTesting, listaClassiTest);
		System.out.println(evaluation);
		*/
		
	}
	
	private static void testLoadModel() {
		
		//String fileNameOntologiesTraining = "Classification/trainontologies.txt";
		//String fileNameClasses = "Classification/classes.txt";
		String fileNameOntologiesTest = "Classification/test.txt";
		//String fileNameClassesTest = "Classification/classesTest.txt";
		
		
		OntologyClassifier oc = new OntologyClassifier("Classification/test.model");
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
		
		
		OntologyClassifier oc = new OntologyClassifier("Classification/test.model");
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
	
	
	OntologyClassifier oc = new OntologyClassifier("Classification/test.model");
	LinkedList<String> listaClassi =  oc.loadClassesFromFile(fileNameClasses);
	//LinkedList<String> listaClassiTest =  oc.loadClassesFromFile(fileNameClassesTest);
	CoupleOntologyMetrics[] coupleTraining = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTraining);
	//CoupleOntologyMetrics[] coupleTesting = oc.loadCoupleOntologyMetricsFromFile(fileNameOntologiesTest);
	
	
	oc.crossValidation(coupleTraining, listaClassi, 10, 3);
	//String evaluation = oc.testModel(coupleTraining, listaClassi, coupleTesting, listaClassiTest);
	//System.out.println(evaluation);
	
	
}


	private static OAEI_Track testOntologyST(Ontology sourceOntology,Ontology targetOntology) {
	
	
		OntologyClassifier oc = new OntologyClassifier("Classification/test.model");
	
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


}
