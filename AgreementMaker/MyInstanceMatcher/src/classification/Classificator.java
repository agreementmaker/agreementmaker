package classification;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import am.GlobalStaticVariables;
import am.app.mappingEngine.oaei.OAEI_Track;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.VotedPerceptron;
import weka.classifiers.lazy.KStar;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.ontologymetrics.OntologyEvaluation;

public class Classificator {
	
	private FastVector fvClassVal;
	private Classifier cModel;
	
	
	
	/*
	 * constructor of the object ontologyclassificator
	 * 
	 */
	public Classificator(TrainSet o){
		LinkedList<String> classList = o.getClassList();
		fvClassVal = new FastVector(classList.size());
		
		for (Iterator it = classList.iterator(); it.hasNext();) {
			String string = (String) it.next();
			fvClassVal.addElement(string);
		}
		
		cModel =  (Classifier) new KStar();
	}
	
	public FastVector getFvClassVal() {
		return fvClassVal;
	}

	public void setFvClassVal(FastVector fvClassVal) {
		this.fvClassVal = fvClassVal;
	}

	public Classifier getcModel() {
		return cModel;
	}

	public void setcModel(Classifier cModel) {
		this.cModel = cModel;
	}

	public Classificator(TrainSet o, String fileName){
		LinkedList<String> classList = o.getClassList();
		fvClassVal = new FastVector(classList.size());
		
		for (Iterator it = classList.iterator(); it.hasNext();) {
			String string = (String) it.next();
			fvClassVal.addElement(string);
		}
		
		cModel =  loadModel(fileName);
	}
	
	public Classificator(TrainSet o, ClassificatorRegistry classificator){
		LinkedList<String> classList = o.getClassList();
		fvClassVal = new FastVector(classList.size());
		
		for (Iterator it = classList.iterator(); it.hasNext();) {
			String string = (String) it.next();
			fvClassVal.addElement(string);
		}
		
		try {
			cModel = (Classifier) classificator.getClassifier().newInstance();
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	
	
	/*
	 * this method create a set of instances from an array of metrics and a list of class
	 * 
	 */
	
	
	
	//PER FEDE! 
	public Instances createTrainingInstances(List<Train> l, List<String> classList ){
		
		int k = 0;
		int metricsLength = ((Train) l.get(0)).toArray().length;
		double [][] metrics = new double[l.size()][metricsLength];
		for (Iterator<Train> it = l.iterator(); it.hasNext();) {
			Train train = (Train) it.next();
			metrics[k]=train.toArray();
			k++;
		}
		
		int attributesLength = metricsLength + 1; // it include the class attribute
		
		//DEBUG print
		//System.out.println("metricsLength="+metricsLength);
		//System.out.println("attributesLength="+attributesLength);
		//for(int i = 0; i<metricsLength;i++){
			//System.out.println("Metric "+i+": "+ metrics[i]);
		//}
		
    
	
		//initialize the attribute array
		Attribute[] attributes = new Attribute[attributesLength]; 
		for(int i = 0; i<metricsLength;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[metricsLength]= new Attribute("theClass",fvClassVal );
		
		//DEBUG print
		//for(int i = 0; i<attributesLength;i++){
		//	System.out.println("Attribute "+i+": "+ attributes[i].name());
		//}
		
		
		
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(attributesLength);
		for (int i = 0; i< attributesLength; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
		
		//DEBUG print
		//for(int i = 0; i<attributesLength;i++){
		//	System.out.println("fvAttribute "+i+": "+ ((Attribute) fvWekaAttributes.elementAt(i)).name());
		//}

		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, l.size());          

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<l.size();j++){
			
			// Create the instance
			instance = new Instance(attributesLength);
			for (int i = 0; i<metricsLength; i++){
				instance.setValue((Attribute)fvWekaAttributes.elementAt(i), metrics[j][i]);
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), classList.get(j));
			
			
			//DEBUG print
			//for(int i = 0; i<attributesLength;i++){
			//	System.out.println("instance "+i+": "+ instance.value(i));
			//}
			
	
			// add the instance
			isTrainingSet.add(instance);
		}
		
		return isTrainingSet;

		
	}
	
	//PER FEDE
public Instances createTrainingInstancesT(List<Test> l, List<String> classList ){
		
		int k = 0;
		int metricsLength = ((Test) l.get(0)).toArray().length;
		double [][] metrics = new double[l.size()][metricsLength];
		for (Iterator<Test> it = l.iterator(); it.hasNext();) {
			Test test = (Test) it.next();
			metrics[k]=test.toArray();
			k++;
		}
		
		int attributesLength = metricsLength + 1; // it include the class attribute
		
		//DEBUG print
		//System.out.println("metricsLength="+metricsLength);
		//System.out.println("attributesLength="+attributesLength);
		//for(int i = 0; i<metricsLength;i++){
			//System.out.println("Metric "+i+": "+ metrics[i]);
		//}
		
    
	
		//initialize the attribute array
		Attribute[] attributes = new Attribute[attributesLength]; 
		for(int i = 0; i<metricsLength;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[metricsLength]= new Attribute("theClass",fvClassVal );
		
		//DEBUG print
		//for(int i = 0; i<attributesLength;i++){
		//	System.out.println("Attribute "+i+": "+ attributes[i].name());
		//}
		
		
		
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(attributesLength);
		for (int i = 0; i< attributesLength; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
		
		//DEBUG print
		//for(int i = 0; i<attributesLength;i++){
		//	System.out.println("fvAttribute "+i+": "+ ((Attribute) fvWekaAttributes.elementAt(i)).name());
		//}

		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, l.size());          

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<l.size();j++){
			
			// Create the instance
			instance = new Instance(attributesLength);
			for (int i = 0; i<metricsLength; i++){
				instance.setValue((Attribute)fvWekaAttributes.elementAt(i), metrics[j][i]);
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), classList.get(j));
			
			
			//DEBUG print
			//for(int i = 0; i<attributesLength;i++){
			//	System.out.println("instance "+i+": "+ instance.value(i));
			//}
			
	
			// add the instance
			isTrainingSet.add(instance);
		}
		
		return isTrainingSet;

		
	}
	
	

//per fede
	public Instances createTestInstances(List<Test> l ){
		
		int k = 0;
		int metricsLength = ((Test) l.get(0)).toArray().length;
		double [][] metrics = new double[l.size()][metricsLength];
		for (Iterator<Test> it = l.iterator(); it.hasNext();) {
			Test test = (Test) it.next();
			metrics[k]=test.toArray();
			k++;
		}
		
		
		
		int attributesLength = metricsLength + 1; // it include the class attribute
		
    
	
		//initialize the attribute array
		Attribute[] attributes = new Attribute[attributesLength]; 
		for(int i = 0; i<metricsLength;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[metricsLength]= new Attribute("theClass",fvClassVal );
		
		//DEBUG print
		//for(int i = 0; i<attributesLength;i++){
		//	System.out.println("Attribute "+i+": "+ attributes[i].name());
		//}
		
		
		
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(attributesLength);
		for (int i = 0; i< attributesLength; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
		
		//DEBUG print
		//for(int i = 0; i<attributesLength;i++){
		//	System.out.println("fvAttribute "+i+": "+ ((Attribute) fvWekaAttributes.elementAt(i)).name());
		//}

		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, l.size());           

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<l.size();j++){
			
			// Create the instance
			instance = new Instance(attributesLength);
			for (int i = 0; i<metricsLength; i++){
				instance.setValue((Attribute)fvWekaAttributes.elementAt(i), metrics[j][i]);
			}
			//instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), ontoClass.get(j));
			
			
			//DEBUG print
			//for(int i = 0; i<attributesLength;i++){
			//	System.out.println("instance "+i+": "+ instance.value(i));
			//}
			
	
			// add the instance
			isTrainingSet.add(instance);
		}
		
		return isTrainingSet;

		
	}
	
	
	
	
	
	
	
	
	
	
	/*
	 * this method train the model on the basis of a list of ontology and a list of class
	 * 
	 */	

	public void trainModel(TrainSet o) throws Exception{
		
		//CoupleOntologyMetrics[] om = loadCoupleOntologyMetricsFromObject(o);
		List<String> listClass = loadClassesFromObject(o);
		List<Train> trainList = o.getWinnerList();
		int size = trainList.size();
		if (size !=listClass.size()) throw new Exception("Wrong parameter size class!=size listontology");
		//create the array of ontology metrics
		
		Instances isTrainingSet = this.createTrainingInstances(trainList,listClass);
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e1) {
			
			System.out.println("Failed To build the classifier!");
			e1.printStackTrace();
		}
		
		
	}	
	
	
	

	
	/*
	 * this method store a model in a file
	 * 
	 */

	public void storeModel(String fileName){
		// serialize model
		try {
			weka.core.SerializationHelper.write(fileName, cModel);
		} catch (Exception e) {
			System.out.println("Failed To store the classifier!");
			e.printStackTrace();
		}
		
	}
	
	
	/*
	 * this method load a model from a file
	 * 
	 */
	
	
	public Classifier loadModel(String fileName){
		try {
			Classifier cls = (Classifier) weka.core.SerializationHelper.read(fileName);
			return cls;
		} catch (Exception e) {
			System.out.println("Failed To load the classifier!");
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/*
	 * this method classified an ontology
	 * 
	 */

	
	public String[] classifiedModel(TestSet o){

		List<Test> om = o.getTestList();
		//CoupleOntologyMetrics[] om = loadCoupleOntologyMetricsFromObject(o);
		Instances isTestingSet = createTestInstances(om);
		String[] s = new String[isTestingSet.numInstances()];
		
		double classNumber = 0.0;
		try {
			for (int i = 0; i < s.length; i++) {
				
			
			classNumber = cModel.classifyInstance(isTestingSet.instance(i));
			isTestingSet.instance(i).setClassValue(classNumber);
			s[i] = isTestingSet.instance(i).stringValue(isTestingSet.classIndex());
			//System.out.println("ClassNumber: "+ classNumber + "Class "+ isTestingSet.firstInstance().stringValue(isTestingSet.classIndex()));
			//System.out.println(isTestingSet.instance(0).toString());
			}
		} catch (Exception e1) {
			System.out.println("Failed To classified the ontology!");
			e1.printStackTrace();
		}
		
		
		
		//for (int i = 0; i < s.length; i++) {
			
		//}
		
		
		return s;

		

	}
	
	public double[][] getConfidence(TestSet o){

		List<Test> om = o.getTestList();
		//CoupleOntologyMetrics[] om = loadCoupleOntologyMetricsFromObject(o);
		Instances isTestingSet = createTestInstances(om);
		//String[] s = new String[isTestingSet.numInstances()];
		
		double[][] confidence = new double[isTestingSet.numInstances()][fvClassVal.size()];
		try {
			for (int i = 0; i <isTestingSet.numInstances(); i++) {
				
			
			confidence[i] = cModel.distributionForInstance(isTestingSet.instance(i));
			//isTestingSet.instance(i).setClassValue(classNumber);
			//s[i] = isTestingSet.instance(i).stringValue(isTestingSet.classIndex());
			//System.out.println("ClassNumber: "+ classNumber + "Class "+ isTestingSet.firstInstance().stringValue(isTestingSet.classIndex()));
			//System.out.println(isTestingSet.instance(0).toString());
			
			}
		} catch (Exception e1) {
			System.out.println("Failed To classified the ontology!");
			e1.printStackTrace();
		}
		
		
		
		//for (int i = 0; i < s.length; i++) {
			
		//}
		
		
		return confidence;

		

	}
	
	
	
	
	
	/*
	 * this method take a testset with the class attribute and evaluate the model we did Test the model
	 * 
	 */
	
	public String evaluateModel(TrainSet o, TestSet t){
		List<String> classList = loadClassesFromObject(o);
		List<Train> train = o.getWinnerList();
		List<Test> test = t.getTestList();
		
		
		//CoupleOntologyMetrics[] train =loadCoupleOntologyMetricsFromObject(o);
		//LinkedList<String> classList = loadClassesFromObject(o);
		//CoupleOntologyMetrics[] test = loadCoupleOntologyMetricsFromObject(t);
		LinkedList<String> classTestList = loadClassesFromObject(t);
		
		Instances isTrainingSet = createTrainingInstances(train,classList);
		Instances isTestingSet = createTrainingInstancesT(test,classTestList);
		
		Evaluation eTest=null; 
		try {
			eTest = new Evaluation(isTrainingSet);
			eTest.evaluateModel(cModel, isTestingSet);
		} catch (Exception e) {
			System.out.println("Failed To evaluate the model!");
			e.printStackTrace();
		}
	
		return eTest.toSummaryString();
	
	}
	
	
	/*
	public void getConfidence(){
		
	}
	*/
	
	

	public LinkedList<String> loadClassesFromFile(String fileName){
		try{

			LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(fileName)));
			lnr.skip(Long.MAX_VALUE);
			int numLines = lnr.getLineNumber()+1;
			System.out.println(lnr.getLineNumber()+1);		
			
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = null;
			
			LinkedList<String> classList = new LinkedList<String>();
			
			boolean b = false;
			
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				
				int sizeFv = fvClassVal.size();
				for (int j = 0; j < sizeFv; j++) {
					String classValue = ((String) fvClassVal.elementAt(j));
					//System.out.println(classValue);
					if (classValue.equals(strLine)) b=true;
				}
				if (!b) throw new Exception("File not correct1");
				
				classList.add(strLine);  
				
				
				// Print the content on the console
				//System.out.println (strLine);
			}
			//Close the input stream
			in.close();
			
			return classList;
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public LinkedList<String> loadClassesFromObject(TrainSet o){
		LinkedList<Train> winners = o.getWinnerList();
		LinkedList<String> result = new LinkedList<String>();
	
		for (Iterator<Train> it = winners.iterator(); it.hasNext();) {
			Train winner = (Train) it.next();
			result.add(winner.getClassName());
		}
	
		return result;
	}
	
	public LinkedList<String> loadClassesFromObject(TestSet o){
		LinkedList<Test> tests = o.getTestList();
		LinkedList<String> result = new LinkedList<String>();
	
		for (Iterator<Test> it = tests.iterator(); it.hasNext();) {
			Test test = (Test) it.next();
			result.add(test.getClassName());
		}
	
		return result;
	}
	
	


	public double crossValidation(TrainSet o, int runs,int folds){
		
		List<String> allClassList = loadClassesFromObject(o);
		List<Train> allTrain = o.getWinnerList();
		
		
		//CoupleOntologyMetrics[] allCoupleMetrics = loadCoupleOntologyMetricsFromObject(o);
		//LinkedList<String> allClassList = loadClassesFromObject(o);
		
		Instances data = createTrainingInstances(allTrain,allClassList);
		double avgCorrectClassified = 0.0;


    // perform cross-validation
		for (int i = 0; i < runs; i++) {
			// randomize data
			int seed = i + 1;
			Random rand = new Random(seed);
			Instances randData = new Instances(data);
			randData.randomize(rand);
			if (randData.classAttribute().isNominal())
				randData.stratify(folds);
			
			Evaluation eval = null;
			try{

				eval = new Evaluation(randData);
				for (int n = 0; n < folds; n++) {
					Instances train = randData.trainCV(folds, n);
					Instances test = randData.testCV(folds, n);
					// the above code is used by the StratifiedRemoveFolds filter, the
					// code below by the Explorer/Experimenter:
					// Instances train = randData.trainCV(folds, n, rand);

					// build and evaluate classifier
					Classifier clsCopy = Classifier.makeCopy(cModel);
					clsCopy.buildClassifier(train);
					eval.evaluateModel(clsCopy, test);
				}
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			// output evaluation
			System.out.println();
			System.out.println("=== Setup run " + (i+1) + " ===");
			System.out.println("Classifier: " + cModel.getClass().getName() + " " + Utils.joinOptions(cModel.getOptions()));
			System.out.println("Dataset: " + data.relationName());
			System.out.println("Folds: " + folds);
			System.out.println("Seed: " + seed);
			System.out.println();
			System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation run " + (i+1) + "===", false));
			avgCorrectClassified += eval.weightedTruePositiveRate();
		}

	
	avgCorrectClassified /= runs;
	System.out.println(avgCorrectClassified);
	return avgCorrectClassified;
	
	
	
	
}

	
	public static Ontology openOntology(String ontoName){
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
	


/*	public static String classifiedOntologiesST(Ontology sourceOntology,Ontology targetOntology, String modelFileName, String TrainSetFileName) {
		
		TrainSet o = new TrainSet(TrainSetFileName);
		
		OntologyClassificator oc = new OntologyClassificator(o,modelFileName);
	
		OntologyEvaluation eval1 = new OntologyEvaluation();
		OntologyEvaluation eval2 = new OntologyEvaluation();
		CoupleOntologyMetrics com = new CoupleOntologyMetrics(eval1.evaluateOntology(sourceOntology), eval2.evaluateOntology(targetOntology));
		CoupleOntologyMetrics[] coupleTesting = new CoupleOntologyMetrics[1];
		coupleTesting[0] = com;
		
		String [] result = oc.classifiedCoupleOntology(coupleTesting);
		
		return result[0];
	
	}*/
	
	
	
	
	
	

}
