package am.app.ontology.profiling.classification;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.KStar;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters.OAEI2011Configuration;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.profiling.classification.trainingGeneration.OutputTrainingGenerator;
import am.app.ontology.profiling.classification.trainingGeneration.Winner;
import am.app.ontology.profiling.ontologymetrics.CoupleOntologyMetrics;
import am.app.ontology.profiling.ontologymetrics.OntologyEvaluation;
import am.app.ontology.profiling.ontologymetrics.OntologyMetrics;

public class OntologyClassificator {
	
	private FastVector fvClassVal;
	private Classifier cModel;
	
	
	
	/*
	 * constructor of the object ontologyclassificator
	 * 
	 */
	public OntologyClassificator(OutputTrainingGenerator o){
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

	public OntologyClassificator(OutputTrainingGenerator o, String fileName){
		LinkedList<String> classList = o.getClassList();
		fvClassVal = new FastVector(classList.size());
		
		for (Iterator it = classList.iterator(); it.hasNext();) {
			String string = (String) it.next();
			fvClassVal.addElement(string);
		}
		
		cModel =  loadModel(fileName);
	}
	
	public OntologyClassificator(OutputTrainingGenerator o, ClassificatorRegistry classificator){
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
	
	
	
	
	
	public OntologyClassificator(){
		fvClassVal = new FastVector(3);
		fvClassVal.addElement("benchmark");
		fvClassVal.addElement("conference");
		fvClassVal.addElement("anatomy");
		
		cModel =  (Classifier) new KStar();
			
		
		
	}
	
	
	public OntologyClassificator(ClassificatorRegistry classificator){
		fvClassVal = new FastVector(3);
		fvClassVal.addElement("benchmark");
		fvClassVal.addElement("conference");
		fvClassVal.addElement("anatomy");
		
		
		//try if it works!!
		try {
			cModel = (Classifier) classificator.getClassifier().newInstance();
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    // NaiveBayes WE CAN CHANGE HERE THE CLASSIFIER WE WANNA USE!!!
		
	}
	
	
	/*
	 * constructor of the object ontologyclassificator from a fileName
	 * 
	 */
	
	public OntologyClassificator(String fileName){
		fvClassVal = new FastVector(3);
		fvClassVal.addElement("benchmark");
		fvClassVal.addElement("conference");
		fvClassVal.addElement("anatomy");
		
		cModel = loadModel(fileName);
		
	}
	
	
/*	public Instances createTrainingInstancesFromMetrics(OntologyMetrics ontoMetrics, String ontoClass ){
		
		//take the metrics
		float[] metrics = ontoMetrics.getAllMetrics();
		int metricsLength = metrics.length;
		int attributesLength = metricsLength + 1; // it include the class attribute
		
		//DEBUG print
		System.out.println("metricsLength="+metricsLength);
		System.out.println("attributesLength="+attributesLength);
		for(int i = 0; i<metricsLength;i++){
			System.out.println("Metric "+i+": "+ metrics[i]);
		}
		

	
		//initialize the attribute array
		Attribute[] attributes = new Attribute[attributesLength]; 
		for(int i = 0; i<metricsLength;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[metricsLength]= new Attribute("theClass",fvClassVal );
		
		//DEBUG print
		for(int i = 0; i<attributesLength;i++){
			System.out.println("Attribute "+i+": "+ attributes[i].name());
		}
		
		
		
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(attributesLength);
		for (int i = 0; i< attributesLength; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
		
		//DEBUG print
		for(int i = 0; i<attributesLength;i++){
			System.out.println("fvAttribute "+i+": "+ ((Attribute) fvWekaAttributes.elementAt(i)).name());
		}

		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);           

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		System.out.println("the class index is "+metricsLength);

		
		// Create the instance
		Instance instance = new Instance(attributesLength);
		for (int i = 0; i<metrics.length; i++){
			instance.setValue((Attribute)fvWekaAttributes.elementAt(i), metrics[i]);
		}
		instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), ontoClass);
		
		
		//DEBUG print
		for(int i = 0; i<attributesLength;i++){
			System.out.println("instance "+i+": "+ instance.value(i));
		}
		

		// add the instance
		isTrainingSet.add(instance);
		
		
		return isTrainingSet;

		
	} */
	
	
	/*
	 * this method create a set of instances from an array of metrics and a list of class
	 * 
	 */
	
	public Instances createTrainingInstancesFromMetrics(OntologyMetrics[] ontoMetrics, List<String> ontoClass ){
		
		//take the metrics
		int metricsLength = ontoMetrics[0].getAllMetrics().length;
		float[][] metrics= new float[ontoMetrics.length][metricsLength];
		for(int i = 0; i<ontoMetrics.length;i++){
			metrics[i]= ontoMetrics[i].getAllMetrics();
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
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, ontoMetrics.length);           

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<ontoMetrics.length;j++){
			
			// Create the instance
			instance = new Instance(attributesLength);
			for (int i = 0; i<metricsLength; i++){
				instance.setValue((Attribute)fvWekaAttributes.elementAt(i), metrics[j][i]);
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), ontoClass.get(j));
			
			
			//DEBUG print
			//for(int i = 0; i<attributesLength;i++){
			//	System.out.println("instance "+i+": "+ instance.value(i));
			//}
			
	
			// add the instance
			isTrainingSet.add(instance);
		}
		
		return isTrainingSet;

		
	}	
	
	public Instances createTrainingInstancesFromCoupleMetrics(CoupleOntologyMetrics[] ontoMetrics, List<String> ontoClass ){
		
		//take the metrics
		int metricsLength = ontoMetrics[0].getAllMetrics().length;
		float[][] metrics= new float[ontoMetrics.length][metricsLength];
		for(int i = 0; i<ontoMetrics.length;i++){
			metrics[i]= ontoMetrics[i].getAllMetrics();
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
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, ontoMetrics.length);          

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<ontoMetrics.length;j++){
			
			// Create the instance
			instance = new Instance(attributesLength);
			for (int i = 0; i<metricsLength; i++){
				instance.setValue((Attribute)fvWekaAttributes.elementAt(i), metrics[j][i]);
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), ontoClass.get(j));
			
			
			//DEBUG print
			//for(int i = 0; i<attributesLength;i++){
			//	System.out.println("instance "+i+": "+ instance.value(i));
			//}
			
	
			// add the instance
			isTrainingSet.add(instance);
		}
		
		return isTrainingSet;

		
	}
	
	
	
public Instances createTestInstancesFromMetrics(OntologyMetrics[] ontoMetrics ){
		
		//take the metrics
		int metricsLength = ontoMetrics[0].getAllMetrics().length;
		float[][] metrics= new float[ontoMetrics.length][metricsLength];
		for(int i = 0; i<ontoMetrics.length;i++){
			metrics[i]= ontoMetrics[i].getAllMetrics();
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
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, ontoMetrics.length);           

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<ontoMetrics.length;j++){
			
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
	
	public Instances createTestInstancesFromCoupleMetrics(CoupleOntologyMetrics[] ontoMetrics ){
		
		//take the metrics
		int metricsLength = ontoMetrics[0].getAllMetrics().length;
		float[][] metrics= new float[ontoMetrics.length][metricsLength];
		for(int i = 0; i<ontoMetrics.length;i++){
			metrics[i]= ontoMetrics[i].getAllMetrics();
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
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, ontoMetrics.length);           

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<ontoMetrics.length;j++){
			
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
	
	
	
	
	
	
	
	
	
	
	
public Instances createTestInstanceFromMetrics(OntologyMetrics ontoMetrics){
		
		//take the metrics
		float[] metrics = ontoMetrics.getAllMetrics();
		int metricsLength = metrics.length;
		int attributesLength = metricsLength + 1; // it include the class attribute
		
		//DEBUG print
		//System.out.println("metricsLength="+metricsLength);
		//System.out.println("attributesLength="+attributesLength);
		//for(int i = 0; i<metricsLength;i++){
		//	System.out.println("Metric "+i+": "+ metrics[i]);
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
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, metrics.length);           

		// Set class index
		isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		
		// Create the instance
		Instance instance = new Instance(attributesLength);
		for (int i = 0; i<metrics.length; i++){
			instance.setValue((Attribute)fvWekaAttributes.elementAt(i), metrics[i]);
		}
		//instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), ontoClass);
		
		
		//DEBUG print
		//for(int i = 0; i<attributesLength;i++){
		//	System.out.println("instance "+i+": "+ instance.value(i));
		//}
		

		// add the instance
		isTrainingSet.add(instance);
		
		
		return isTrainingSet;
	
	}

/*	public void trainOntology2(List<Ontology> listOntology, List<String> listClass) throws Exception{
		int size = listOntology.size();
		if (size !=listClass.size()) throw new Exception();
		OntologyEvaluation eval1 = new OntologyEvaluation();
		System.out.println("ontology 1"+ listOntology.get(0));
		OntologyMetrics om1 = eval1.evaluateOntology(listOntology.get(0));
		int metricsLength = om1.getAllMetrics().length;
		int attributesLength = metricsLength + 1; // it include the class attribute
		System.out.println("metrics evaluate");
		
		//initialize the attribute array
		Attribute[] attributes = new Attribute[attributesLength]; 
		for(int i = 0; i<metricsLength;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[metricsLength]= new Attribute("theClass",fvClassVal );
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(attributesLength);
		for (int i = 0; i< attributesLength; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
				
				//DEBUG print
				for(int i = 0; i<attributesLength;i++){
					System.out.println("fvAttribute "+i+": "+ ((Attribute) fvWekaAttributes.elementAt(i)).name());
				}

				// Create an empty training set
				Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, size);           

				// Set class index
				isTrainingSet.setClassIndex(metricsLength);
				
				//DEBUG print
				System.out.println("the class index is "+metricsLength);

				
				// Create the instance
	/*			Instance instance = new Instance(attributesLength);
				for (int i = 0; i<metrics.length; i++){
					instance.setValue((Attribute)fvWekaAttributes.elementAt(i), metrics[i]);
				}
				instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), ontoClass);
				
				
				//DEBUG print
				for(int i = 0; i<attributesLength;i++){
					System.out.println("instance "+i+": "+ instance.value(i));
				}*/
				

		
	/*	
		for(int i = 0; i<size; i++){
			Ontology o = listOntology.get(i);
			String c = listClass.get(i);
			System.out.println("ontology"+i+" "+o + "Class"+c);
			OntologyEvaluation eval = new OntologyEvaluation();
			OntologyMetrics om = eval.evaluateOntology(o);
			
			
			Instance instance = createTrainingInstanceFromMetrics(om, c);
			// add the instance
			isTrainingSet.add(instance);
		}
		

		
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		
	}*/
	
	
	/*
	 * this method train the model on the basis of a list of ontology and a list of class
	 * 
	 */	
	
	public void trainOntology(List<Ontology> listOntology, List<String> listClass) throws Exception{
		int size = listOntology.size();
		if (size !=listClass.size()) throw new Exception("Wrong parameter size class!=size listontology");
		//create the array of ontology metrics
		OntologyMetrics[] om = new OntologyMetrics[size];
		for(int i = 0; i<size; i++){
			Ontology o = listOntology.get(i);
			String c = listClass.get(i);
			System.out.println("Classified Ontology "+o.getFilename() + "\nClass: "+c);
			OntologyEvaluation eval = new OntologyEvaluation();
			om[i] = eval.evaluateOntology(o);
		}
		
		Instances isTrainingSet = this.createTrainingInstancesFromMetrics(om,listClass);
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e1) {
			System.out.println("Failed To build the classifier!");
			e1.printStackTrace();
		}
		
		
	}
	
	public void trainCoupleOntology(List<CoupleOntology> listOntology, List<String> listClass) throws Exception{
		int size = listOntology.size();
		if (size !=listClass.size()) throw new Exception("Wrong parameter size class!=size listontology");
		//create the array of ontology metrics
		CoupleOntologyMetrics[] om = new CoupleOntologyMetrics[size];
		for(int i = 0; i<size; i++){
			Ontology o1 = listOntology.get(i).getOnto1();
			Ontology o2 = listOntology.get(i).getOnto2();
			String c = listClass.get(i);
			System.out.println("Classified Ontology "+o1.getFilename() + "\n+ Ontology"+o2.getFilename()+"\nClass: "+c);
			OntologyEvaluation eval1 = new OntologyEvaluation();
			OntologyEvaluation eval2 = new OntologyEvaluation();
			om[i] = new CoupleOntologyMetrics(eval1.evaluateOntology(o1), eval2.evaluateOntology(o2));
		}
		
		Instances isTrainingSet = this.createTrainingInstancesFromCoupleMetrics(om,listClass);
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e1) {
			System.out.println("Failed To build the classifier!");
			e1.printStackTrace();
		}
		
		
	}
	
	public void trainOntology(OntologyMetrics[] om, List<String> listClass) throws Exception{
		int size = om.length;
		if (size !=listClass.size()) throw new Exception("Wrong parameter size class!=size listontology");
		//create the array of ontology metrics
		
		Instances isTrainingSet = this.createTrainingInstancesFromMetrics(om,listClass);
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e1) {
			System.out.println("Failed To build the classifier!");
			e1.printStackTrace();
		}
		
		
	}
	
	public void trainCoupleOntology(CoupleOntologyMetrics[] om, List<String> listClass) throws Exception{
		int size = om.length;
		if (size !=listClass.size()) throw new Exception("Wrong parameter size class!=size listontology");
		//create the array of ontology metrics
		
		
		Instances isTrainingSet = this.createTrainingInstancesFromCoupleMetrics(om,listClass);
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e1) {
			
			System.out.println("Failed To build the classifier!");
			e1.printStackTrace();
		}
		
		
	}
	public void trainCoupleOntology(OutputTrainingGenerator o) throws Exception{
		
		CoupleOntologyMetrics[] om = loadCoupleOntologyMetricsFromObject(o);
		List<String> listClass = loadClassesFromObject(o);
		int size = om.length;
		if (size !=listClass.size()) throw new Exception("Wrong parameter size class!=size listontology");
		//create the array of ontology metrics
		
		Instances isTrainingSet = this.createTrainingInstancesFromCoupleMetrics(om,listClass);
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e1) {
			
			System.out.println("Failed To build the classifier!");
			e1.printStackTrace();
		}
		
		
	}	
	

	
/*	public Instance createTrainingInstanceFromMetrics(OntologyMetrics ontoMetrics, String ontoClass ){
		
		//take the metrics
		float[] metrics = ontoMetrics.getAllMetrics();
		int metricsLength = metrics.length;
		int attributesLength = metricsLength + 1; // it include the class attribute
		
		//DEBUG print
		System.out.println("metricsLength="+metricsLength);
		System.out.println("attributesLength="+attributesLength);
		for(int i = 0; i<metricsLength;i++){
			System.out.println("Metric "+i+": "+ metrics[i]);
		}
	
		//initialize the attribute array
		Attribute[] attributes = new Attribute[attributesLength]; 
		for(int i = 0; i<metricsLength;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[metricsLength]= new Attribute("theClass",fvClassVal );
		
		//DEBUG print
		for(int i = 0; i<attributesLength;i++){
			System.out.println("Attribute "+i+": "+ attributes[i].name());
		}
		
		
		
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(attributesLength);
		for (int i = 0; i< attributesLength; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
		
		//DEBUG print
		for(int i = 0; i<attributesLength;i++){
			System.out.println("fvAttribute "+i+": "+ ((Attribute) fvWekaAttributes.elementAt(i)).name());
		}

		// Create an empty training set
		//Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);           

		// Set class index
		//isTrainingSet.setClassIndex(metricsLength);
		
		//DEBUG print
		System.out.println("the class index is "+metricsLength);

		System.out.println(attributesLength);
		// Create the instance
		Instance instance = new Instance(attributesLength); 
		for (int i = 0; i<metricsLength; i++){
			Attribute a = (Attribute) fvWekaAttributes.elementAt(i);
			float f = metrics[i];
			System.out.println(a.name());
			System.out.println(f);
			instance.setValue(a,f);
			System.out.println("set att num: "+i);
		}
		
		instance.setValue((Attribute)fvWekaAttributes.elementAt(metricsLength), ontoClass);
		
		
		//DEBUG print
		for(int i = 0; i<attributesLength;i++){
			System.out.println("instance "+i+": "+ instance.value(i));
		}
		

		// add the instance
		//isTrainingSet.add(instance);
		
		
		return instance;
	
	}*/

	
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
	
	
	public String classifiedOntology(Ontology testOntology){

		OntologyEvaluation eval = new OntologyEvaluation();
		OntologyMetrics ontoMetrics = eval.evaluateOntology(testOntology);
		Instances isTestingSet = createTestInstanceFromMetrics(ontoMetrics);
		
		
		double classNumber = 0.0;
		try {
			classNumber = cModel.classifyInstance(isTestingSet.firstInstance());
			isTestingSet.firstInstance().setClassValue(classNumber);
			//System.out.println("ClassNumber: "+ classNumber + "Class "+ isTestingSet.firstInstance().stringValue(isTestingSet.classIndex()));
			//System.out.println(isTestingSet.instance(0).toString());
			
		} catch (Exception e1) {
			System.out.println("Failed To classified the ontology!");
			e1.printStackTrace();
		}
		
		return isTestingSet.firstInstance().stringValue(isTestingSet.classIndex());

		

	}
	
	
	public String[] classifiedOntology(List<Ontology> listOntology){


		int size = listOntology.size();
		//create the array of ontology metrics
		OntologyMetrics[] om = new OntologyMetrics[size];
		for(int i = 0; i<size; i++){
			Ontology o = listOntology.get(i);

			OntologyEvaluation eval = new OntologyEvaluation();

			om[i] = eval.evaluateOntology(o);
		}

		Instances isTestingSet = createTestInstancesFromMetrics(om);
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

		return s;

	}
	
	
	
	
	public String[] classifiedCoupleOntology(List<CoupleOntology> testCoupleList){

	/*	OntologyEvaluation eval1 = new OntologyEvaluation();
		OntologyEvaluation eval2 = new OntologyEvaluation();
		Ontology o1 = testCouple.getOnto1();
		Ontology o2 = testCouple.getOnto2();
		CoupleOntologyMetrics ontoMetrics = new CoupleOntologyMetrics(eval1.evaluateOntology(o1), eval2.evaluateOntology(o2));
		*/
		
		int size = testCoupleList.size();
		//create the array of ontology metrics
		CoupleOntologyMetrics[] om = new CoupleOntologyMetrics[size];
		for(int i = 0; i<size; i++){
			Ontology o1 = testCoupleList.get(i).getOnto1();
			Ontology o2 = testCoupleList.get(i).getOnto2();
			
			//System.out.println("Classified Ontology "+o1.getFilename() + "\n+ Ontology"+o2.getFilename()+"\nClass: "+c);
			OntologyEvaluation eval1 = new OntologyEvaluation();
			OntologyEvaluation eval2 = new OntologyEvaluation();
			om[i] = new CoupleOntologyMetrics(eval1.evaluateOntology(o1), eval2.evaluateOntology(o2));
		}
		
		

		
		
		Instances isTestingSet = createTestInstancesFromCoupleMetrics(om);
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
	
	
	public String classifiedOntology(OntologyMetrics ontoMetrics){

		Instances isTestingSet = createTestInstanceFromMetrics(ontoMetrics);
		
		
		double classNumber = 0.0;
		try {
			classNumber = cModel.classifyInstance(isTestingSet.firstInstance());
			isTestingSet.firstInstance().setClassValue(classNumber);
			//System.out.println("ClassNumber: "+ classNumber + "Class "+ isTestingSet.firstInstance().stringValue(isTestingSet.classIndex()));
			//System.out.println(isTestingSet.instance(0).toString());
			
		} catch (Exception e1) {
			System.out.println("Failed To classified the ontology!");
			e1.printStackTrace();
		}
		
		return isTestingSet.firstInstance().stringValue(isTestingSet.classIndex());

		

	}
	
	
	public String[] classifiedOntology(OntologyMetrics[] om){


		Instances isTestingSet = createTestInstancesFromMetrics(om);
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

		return s;

	}
	
	
	
	
	public String[] classifiedCoupleOntology(CoupleOntologyMetrics[] om){

		
		Instances isTestingSet = createTestInstancesFromCoupleMetrics(om);
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
			System.out.println("Failed to classify the ontologies!");
			e1.printStackTrace();
		}
		
		
		
		//for (int i = 0; i < s.length; i++) {
			
		//}
		
		
		return s;

		

	}
	
	
public String[] classifiedCoupleOntology(TestSet o){

		CoupleOntologyMetrics[] om = loadCoupleOntologyMetricsFromObject(o);
		Instances isTestingSet = createTestInstancesFromCoupleMetrics(om);
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
	
	
	
	
	
	/*
	 * this method take a testset with the class attribute and evaluate the model we did Test the model
	 * 
	 */
	
	
	public String testModel(OntologyMetrics[] train,LinkedList<String> classList, OntologyMetrics[] test){
		
		Instances isTrainingSet = createTrainingInstancesFromMetrics(train,classList);
		Instances isTestingSet = createTestInstancesFromMetrics(test);
		
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
	
public String testModel(CoupleOntologyMetrics[] train,LinkedList<String> classList, CoupleOntologyMetrics[] test,LinkedList<String> classTestList){
		
		Instances isTrainingSet = createTrainingInstancesFromCoupleMetrics(train,classList);
		Instances isTestingSet = createTrainingInstancesFromCoupleMetrics(test,classTestList);
		
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
public String testModel(OutputTrainingGenerator o, TestSet t){
	CoupleOntologyMetrics[] train =loadCoupleOntologyMetricsFromObject(o);
	LinkedList<String> classList = loadClassesFromObject(o);
	CoupleOntologyMetrics[] test = loadCoupleOntologyMetricsFromObject(t);
	LinkedList<String> classTestList = loadClassesFromObject(t);
	Instances isTrainingSet = createTrainingInstancesFromCoupleMetrics(train,classList);
	Instances isTestingSet = createTrainingInstancesFromCoupleMetrics(test,classTestList);
	
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
	
	
	public CoupleOntologyMetrics[] loadCoupleOntologyMetricsFromObject(OutputTrainingGenerator o){
	
		LinkedList<Winner> winners = o.getWinnerList();
		int numCouple =winners.size();
		Ontology o1 = null;
		Ontology o2 = null;
		CoupleOntologyMetrics coupleMetrics = null;
		CoupleOntologyMetrics[] coupleArray = new CoupleOntologyMetrics[numCouple];
		int i = 0;
	
		for (Iterator<Winner> it = winners.iterator(); it.hasNext();) {
			Winner winner = (Winner) it.next();
			o1 = openOntology(winner.getSourceOntologyFileName());
			o2 = openOntology(winner.getTargetOntologyFileName());
			CoupleOntology c = new CoupleOntology(o1,o2);
			coupleMetrics = c.getAllMetrics();
			coupleArray[i] = coupleMetrics;
			i++;
		}
	
		return coupleArray;
	
	}
	public CoupleOntologyMetrics[] loadCoupleOntologyMetricsFromObject(TestSet o){
		
		LinkedList<Test> tests = o.getTestList();
		int numCouple =tests.size();
		Ontology o1 = null;
		Ontology o2 = null;
		CoupleOntologyMetrics coupleMetrics = null;
		CoupleOntologyMetrics[] coupleArray = new CoupleOntologyMetrics[numCouple];
		int i = 0;
	
		for (Iterator<Test> it = tests.iterator(); it.hasNext();) {
			Test test = (Test) it.next();
			o1 = openOntology(test.getSourceOntology());
			o2 = openOntology(test.getTargetOntology());
			CoupleOntology c = new CoupleOntology(o1,o2);
			coupleMetrics = c.getAllMetrics();
			coupleArray[i] = coupleMetrics;
			i++;
		}
	
		return coupleArray;
	
	}







	public CoupleOntologyMetrics[] loadCoupleOntologyMetricsFromFile(String fileName){
		try{

			LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(fileName)));
			lnr.skip(Long.MAX_VALUE);
			int numLines = lnr.getLineNumber()+1;
			System.out.println(lnr.getLineNumber()+1);
			if (numLines%2!=0){
				throw new Exception("the number of lines of the file is not even");
			}
			int numCouple =numLines/2;
			
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = null;
			Ontology o1 = null;
			Ontology o2 = null;
			CoupleOntologyMetrics coupleMetrics = null;
			CoupleOntologyMetrics[] coupleArray = new CoupleOntologyMetrics[numCouple];
			int i = 0;
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				//first line = onto1
				 o1 = openOntology(strLine);
				 System.out.println ("Open Ontology1:" +strLine);
				//second line = onto2
				if ((strLine = br.readLine()) != null){
					o2 = openOntology(strLine);
					System.out.println ("Open Ontology2:" +strLine);
				}
				else{
					throw new Exception("file not correct");
				}
				CoupleOntology c = new CoupleOntology(o1,o2);
				coupleMetrics = c.getAllMetrics();
				coupleArray[i] = coupleMetrics;
				
				i++;
				// Print the content on the console
				//System.out.println (strLine);
			}
			//Close the input stream
			in.close();
			
			return coupleArray;
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	
	
	
	public OntologyMetrics[] loadOntologyMetricsFromFile(String fileName){
		try{

			LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(fileName)));
			lnr.skip(Long.MAX_VALUE);
			int numLines = lnr.getLineNumber()+1;
			System.out.println(lnr.getLineNumber()+1);
			
			
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = null;
			Ontology o = null;
			OntologyMetrics metricsOnto = null;
			OntologyMetrics[] metricsArray = new OntologyMetrics[numLines];
			int i = 0;
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {

				o = openOntology(strLine);
				System.out.println ("Open Ontology:" +strLine);

				OntologyEvaluation e = new OntologyEvaluation();
				metricsOnto = e.evaluateOntology(o);
				metricsArray[i] = metricsOnto;

				i++;
				// Print the content on the console
				//System.out.println (strLine);
			}
			//Close the input stream
			in.close();
			
			return metricsArray;
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	

	

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
	
	public LinkedList<String> loadClassesFromObject(OutputTrainingGenerator o){
		LinkedList<Winner> winners = o.getWinnerList();
		LinkedList<String> result = new LinkedList<String>();
	
		for (Iterator<Winner> it = winners.iterator(); it.hasNext();) {
			Winner winner = (Winner) it.next();
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
	
	

	public double crossValidation(CoupleOntologyMetrics[] allCoupleMetrics,LinkedList<String> allClassList,int runs,int folds){
		
		
			
			Instances data = createTrainingInstancesFromCoupleMetrics(allCoupleMetrics,allClassList);
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

	public double crossValidation(OutputTrainingGenerator o, int runs,int folds){
		
		CoupleOntologyMetrics[] allCoupleMetrics = loadCoupleOntologyMetricsFromObject(o);
		LinkedList<String> allClassList = loadClassesFromObject(o);
		
		Instances data = createTrainingInstancesFromCoupleMetrics(allCoupleMetrics,allClassList);
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
	


	public static String classifiedOntologiesST(Ontology sourceOntology,Ontology targetOntology, String modelFileName, String outputTrainingGeneratorFileName) {
		
		OutputTrainingGenerator o = new OutputTrainingGenerator(outputTrainingGeneratorFileName);
		
		OntologyClassificator oc = new OntologyClassificator(o,modelFileName);
	
		OntologyEvaluation eval1 = new OntologyEvaluation();
		OntologyEvaluation eval2 = new OntologyEvaluation();
		CoupleOntologyMetrics com = new CoupleOntologyMetrics(eval1.evaluateOntology(sourceOntology), eval2.evaluateOntology(targetOntology));
		CoupleOntologyMetrics[] coupleTesting = new CoupleOntologyMetrics[1];
		coupleTesting[0] = com;
		
		String [] result = oc.classifiedCoupleOntology(coupleTesting);
		
		return result[0];
	
	}
	
public static OAEI2011Configuration classifiedOntologiesOAEI2011(Ontology sourceOntology,Ontology targetOntology) {
		String modelFileName = "Classification/cModel4.model";
		String outputTrainingGeneratorFileName = "Classification/finalTraining3.xml"; 
		
		String result = classifiedOntologiesST(sourceOntology, targetOntology, modelFileName, outputTrainingGeneratorFileName);
		/*
		
		OutputTrainingGenerator o = new OutputTrainingGenerator(outputTrainingGeneratorFileName);
		
		OntologyClassificator oc = new OntologyClassificator(o,modelFileName);
	
		OntologyEvaluation eval1 = new OntologyEvaluation();
		OntologyEvaluation eval2 = new OntologyEvaluation();
		CoupleOntologyMetrics com = new CoupleOntologyMetrics(eval1.evaluateOntology(sourceOntology), eval2.evaluateOntology(targetOntology));
		CoupleOntologyMetrics[] coupleTesting = new CoupleOntologyMetrics[1];
		coupleTesting[0] = com;
		
		String [] result = oc.classifiedCoupleOntology(coupleTesting);*/
		
		if (result.equals("general_purpose")) return OAEI2011Configuration.GENERAL_PURPOSE; //ex benchmark
		if (result.equals("general_purpose_advanced")) return OAEI2011Configuration.GENERAL_PURPOSE_ADVANCED;
		if (result.equals("general_multi")) return OAEI2011Configuration.GENERAL_MULTI;
		if (result.equals("large_lexical")) return OAEI2011Configuration.LARGE_LEXICAL;
		if (result.equals("large_lexical_with_localnames")) return OAEI2011Configuration.LARGE_LEXICAL_WITH_LOCALNAMES;
		
		return OAEI2011Configuration.GENERAL_PURPOSE; 
	
	}
	
	
	
	
	
	
	
	

}
