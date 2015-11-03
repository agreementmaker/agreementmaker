package am.extension.userfeedback.MLutility;


import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;



public class WekaUtility {
	Instances trainingSet;
	LinearRegression lr=new LinearRegression();
	
	
	
	public void setTrainingSet(double[][] ts)
	{
		trainingSet=createTrainingInstances(ts);
		try {
			lr.buildClassifier(trainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private Instance createDataInstances(double[] dtInstance){
				
		//initialize the attribute array
		int attributeNum=dtInstance.length;
		Attribute[] attributes = new Attribute[dtInstance.length+1]; 
		for(int i = 0; i<attributeNum;i++){
			attributes[i]= new Attribute("att"+i);
		}
		attributes[attributeNum]= new Attribute("theClass");
		
	
		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(dtInstance.length+1);
		for (int i = 0; i< dtInstance.length+1; i++){
			fvWekaAttributes.addElement(attributes[i]);
		}
		
		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, dtInstance.length);        
//		
		isTrainingSet.setClassIndex(dtInstance.length-1);
		

		Instance instance = null;
		
		//for each ontology create an instance and add it to the trainingset
		// Create the instance
		instance = new Instance(dtInstance.length+1);
		for (int i = 0; i<dtInstance.length; i++){
			instance.setValue((Attribute)fvWekaAttributes.elementAt(i), dtInstance[i]);
		}
				
			
		// add the instance
		//isTrainingSet=instance;

		
		return instance;

		
	}	
	
	private Instances createTrainingInstances(double[][] ts){
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
		
		Instance instance = null;

		//for each ontology create an instance and add it to the trainingset
		for(int j = 0; j<ts.length;j++){
			
			// Create the instance
			instance = new Instance(ts[0].length);
			for (int i = 0; i<ts[0].length-1; i++){
				instance.setValue((Attribute)fvWekaAttributes.elementAt(i), (double)ts[j][i]);
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(ts[0].length-1), (double)ts[j][ts[0].length-1]);
			
			
			// add the instance
			isTrainingSet.add(instance);
		}
		
		return isTrainingSet;
	}	
	
	
	public double runRegression(double[] ds)
	{
		double lrSim=0;
		Instance dataSet=createDataInstances(ds);

		try {
			
			
			lrSim=lr.classifyInstance(dataSet);

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lrSim;
	}
	
	public double runKNN(double[] ds)
	{
		double simSVM=0;
		
		Instance dataSet=createDataInstances(ds);
		// create new instance of scheme
		LibSVM scheme = new LibSVM();
		//IBk scheme=new IBk();
		try {
			scheme.buildClassifier(trainingSet);
			//scheme.
			simSVM=scheme.classifyInstance(dataSet);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		 // set options
		 try {
			scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return simSVM;
	}
	
}
