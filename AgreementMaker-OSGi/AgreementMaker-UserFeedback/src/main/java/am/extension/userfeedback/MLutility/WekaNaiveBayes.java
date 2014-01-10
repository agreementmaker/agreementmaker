package am.extension.userfeedback.MLutility;

import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.functions.LinearRegression;



public class WekaNaiveBayes {
	
	
	private Instances trainingSet;
	private FastVector fvClassVal;
	
	public void WekaNaiveBayes()
	{
		
	}
	
	private Instances createDataInstances(Object[][] ts){
		
		
		
		//initialize the attribute array
		int attributeNum=ts[0].length;
		int labelIndex=ts[0].length;
		Attribute[] attributes = new Attribute[ts[0].length+1]; 
		for(int i = 0; i<attributeNum;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[labelIndex]= new Attribute("theClass");
		
	
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(ts[0].length+1);
		for (int i = 0; i< ts[0].length+1; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
		
		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, ts[0].length);        
		
		isTrainingSet.setClassIndex(ts[0].length-1);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<ts.length;j++){
			
			// Create the instance
			instance = new Instance(ts[0].length+1);
			for (int i = 0; i<ts[0].length; i++){
				instance.setValue((Attribute)fvWekaAttributes.elementAt(i), (Integer)ts[j][i]);
			}
			//instance.setValue((Attribute)fvWekaAttributes.elementAt(ts[0].length), '?');
				
			
			// add the instance
			isTrainingSet.add(instance);
		}
		
		return isTrainingSet;

		
	}	
	
	private Instances createTrainingInstances(Object[][] ts){
		
		
	
		//initialize the attribute array
		int attributeNum=ts[0].length-1;
		int labelIndex=ts[0].length-1;
		Attribute[] attributes = new Attribute[ts[0].length]; 
		for(int i = 0; i<attributeNum;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[labelIndex]= new Attribute("theClass");
		
	
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(ts[0].length);
		for (int i = 0; i< ts[0].length; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
		
		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, ts[0].length);           

		// Set class index
		isTrainingSet.setClassIndex(ts[0].length-1);
		
		//DEBUG print
		//System.out.println("the class index is "+metricsLength);

		Instance instance = null;

		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<ts.length;j++){
			
			// Create the instance
			instance = new Instance(ts[0].length);
			for (int i = 0; i<ts[0].length-1; i++){
				instance.setValue((Attribute)fvWekaAttributes.elementAt(i), (Integer)ts[j][i]);
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(ts[0].length-1), (Integer)ts[j][ts[0].length-1]);
			
			
			// add the instance
			isTrainingSet.add(instance);
		}
		
		return isTrainingSet;

		
	}	
	
	
	public void runNBayes(Object[][] ts, Object[][] ds)
	{
		double lrSim=0;
		Instances trainingSet=createTrainingInstances(ts);
		Instances dataSet=createDataInstances(ds);
		LinearRegression lr=new LinearRegression();
		try {
			lr.buildClassifier(trainingSet);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			//IBk ibk=new IBk();
			//ibk.buildClassifier(trainingSet);
			//weka.core.neighboursearch.LinearNNSearch knn = new LinearNNSearch(trainingSet);
			//do other stuff

			//cModel.buildClassifier(createTrainingInstances(ts));
			for (int i=0;i<dataSet.numInstances();i++)
			{
				Instance inst=dataSet.instance(i);
				lrSim=lr.classifyInstance(inst);
//				ibk.classifyInstance(dataSet.instance(i));
//				nearestInstances= knn.kNearestNeighbours(dataSet.instance(i), 3);
				//fDistribution = cModel.distributionForInstance(dataSet.instance(i));
//				if (fDistribution[0]>0.8)
//				{
				System.out.println(lrSim);
//					System.out.println(knn.toString());
//					System.out.println(knn.getClass().toString());
//					System.out.println(knn.getDistances().toString());
//				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
