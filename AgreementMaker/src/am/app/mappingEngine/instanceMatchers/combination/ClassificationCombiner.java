package am.app.mappingEngine.instanceMatchers.combination;

import java.util.Arrays;
import java.util.List;

import am.app.ontology.Ontology;
import am.app.ontology.profiling.ontologymetrics.OntologyEvaluation;
import am.app.ontology.profiling.ontologymetrics.OntologyMetrics;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class ClassificationCombiner extends CombinationFunction{
	Classifier classifier;
	Instances trainingSet;
	FastVector attributes;
	String fileName; 
	Instances dataset;
	
	public ClassificationCombiner(String fileName) {
		super(Type.LOCAL);
		this.fileName = fileName;
		classifier = loadModel(fileName);			
	}
	
	public ClassificationCombiner(Instances trainingSet) {
		super(Type.LOCAL);
		classifier = new MultilayerPerceptron();
		this.trainingSet = trainingSet;
	}
	
	public void buildClassifier() throws Exception{
		classifier.buildClassifier(trainingSet);
	}
		
	/**
	 * Loads the model from a file
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
	/**
	 * Saves the model to a file 
	 * 
	 */
	public void storeModel(String fileName){
		// serialize model
		try {
			weka.core.SerializationHelper.write(fileName, classifier);
		} catch (Exception e) {
			System.out.println("Failed To store the classifier!");
			e.printStackTrace();
		}
		
	}
	
	public String classifiedOntology(Ontology testOntology){

		OntologyEvaluation eval = new OntologyEvaluation();
		OntologyMetrics ontoMetrics = eval.evaluateOntology(testOntology);
		Instances isTestingSet = null;
		//createTestInstanceFromMetrics(ontoMetrics);
		
		
		double classNumber = 0.0;
		try {
			//classNumber = cModel.classifyInstance(isTestingSet.firstInstance());
			isTestingSet.firstInstance().setClassValue(classNumber);
			//System.out.println("ClassNumber: "+ classNumber + "Class "+ isTestingSet.firstInstance().stringValue(isTestingSet.classIndex()));
			//System.out.println(isTestingSet.instance(0).toString());
			
		} catch (Exception e1) {
			System.out.println("Failed To classified the ontology!");
			e1.printStackTrace();
		}
		
		return isTestingSet.firstInstance().stringValue(isTestingSet.classIndex());
	}
	
	public FastVector getAttributes(int matchersNum) {
		if(attributes == null){
			attributes = new FastVector(matchersNum + 1);
			
			for (int i = 0; i < matchersNum; i++){
				attributes.addElement(new Attribute("matcher" + i));
			}
			FastVector fv = new FastVector();
			fv.addElement("match");
			fv.addElement("noMatch");
			attributes.addElement(new Attribute("match", fv));	
		}
		return attributes;
	}
	
	@Override
	public double combine(List<Double> similarities) {
		getAttributes(similarities.size());
		getDataset(similarities.size());
			
		//System.out.println(attributes);
				
		Instance instance = new Instance(attributes.size());
		for (int i = 0; i< similarities.size(); i++){
			instance.setValue(i, similarities.get(i));
		}
		//dataset.add(instance);	
		instance.setDataset(dataset);
		
//		try {
//			double predictedClass = classifier.classifyInstance(instance);
//			//System.out.println("Model predicts: " + predictedClass);
//			return 1 - predictedClass;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return 0;
//		}
		
		//classifier.
		
		try {
			double[] prediction = classifier.distributionForInstance(instance);
			//System.out.println(Arrays.toString(prediction));
			return prediction[0];
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return 0.0;
	}

	private Instances getDataset(int matchersNum) {
		getAttributes(matchersNum);
		if(dataset == null){
			dataset = new Instances("Rel", attributes, attributes.size()); 
			dataset.setClassIndex(attributes.size() - 1);
		}
		return dataset;
	}
}
