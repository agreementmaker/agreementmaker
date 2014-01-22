/*
 * 	Francesco Loprete December 2014
 */
package am.extension.multiUserFeedback.experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.extension.multiUserFeedback.logic.BMlogic;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.extension.userfeedback.logic.UFLControlLogic;
import am.ui.UIUtility;

public class BMexperiment extends UFLExperiment {

	
public BufferedWriter logFile;
private Alignment<Mapping> MLAlignment;
private Object[][] trainingSet_classes;
private Object[][] trainingSet_property;
private Object[][] dataSet_classes;
private Object[][] dataSet_property;
private SimilarityMatrix uflClassMatrix;
private SimilarityMatrix uflPropertyMatrix;
public List<Mapping> disRanked;
public List<Mapping> uncertainRanking;
public List<Mapping> almostRanking;
public Mapping selectedMapping;
public String feedback;
public List<Mapping> alreadyEvaluated=new ArrayList<Mapping>();
public List<Mapping> conflictualClass;
public List<Mapping> conflictualProp;
public int currentUser=0;

/**
 * @deprecated Use {@link am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter#NUM_USERS}.
 */
@Deprecated
public int usersNumber=10;
public int realIteration=0;

public SparseMatrix agreegatedClassFeedback;
public SparseMatrix agreegatedPropertiesFeedback;

public HashMap<String, List<Mapping>> usersMappings=new HashMap<String, List<Mapping>>();
public HashMap<String, Integer> usersGroup=new HashMap<String, Integer>();
public HashMap<String, SimilarityMatrix> usersClass=new HashMap<String, SimilarityMatrix>();
public HashMap<String, SimilarityMatrix> usersProp=new HashMap<String, SimilarityMatrix>();

private alignCardinality alignCardinalityType=alignCardinality.cn_m;

	public BMexperiment(UFLExperimentSetup setup)
	{
		super(setup);
		
		try {
			String log = setup.parameters.getParameter(Parameter.LOGFILE);
			String root = Core.getInstance().getRoot();
			FileWriter file = new FileWriter(root + log, false);
			logFile=new BufferedWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			UIUtility.displayErrorPane("Permission error: Log file can not be created", "Error");
		}
	}

public alignCardinality getAlignCardinalityType() {
	return alignCardinalityType;
}


public void setAlignCardinalityType(alignCardinality alignCardinalityType) {
	this.alignCardinalityType = alignCardinalityType;
}


public SparseMatrix classesSparseMatrix;
public SparseMatrix propertiesSparseMatrix;


public SparseMatrix getClassesSparseMatrix() {
	return classesSparseMatrix;
}


public void setClassesSparseMatrix(SparseMatrix classesSparseMatrix) {
	this.classesSparseMatrix = classesSparseMatrix;
}


public SparseMatrix getPropertiesSparseMatrix() {
	return propertiesSparseMatrix;
}


public void setPropertiesSparseMatrix(SparseMatrix propertiesSparseMatrix) {
	this.propertiesSparseMatrix = propertiesSparseMatrix;
}

public Object[][] getTrainingSet_classes() {
	return trainingSet_classes;
}


public void setTrainingSet_classes(Object[][] trainingSet_classes) {
	this.trainingSet_classes = trainingSet_classes;
}


public Object[][] getTrainingSet_property() {
	return trainingSet_property;
}


public void setTrainingSet_property(Object[][] trainingSet_property) {
	this.trainingSet_property = trainingSet_property;
}


public Object[][] getDataSet_classes() {
	return dataSet_classes;
}


public void setDataSet_classes(Object[][] dataSet_classes) {
	this.dataSet_classes = dataSet_classes;
}


public Object[][] getDataSet_property() {
	return dataSet_property;
}


public void setDataSet_property(Object[][] dataSet_property) {
	this.dataSet_property = dataSet_property;
}



public SimilarityMatrix getUflClassMatrix() {
	return uflClassMatrix;
}


public void setUflClassMatrix(SimilarityMatrix uflClassMatrix) {
	this.uflClassMatrix = uflClassMatrix;
}


public SimilarityMatrix getUflPropertyMatrix() {
	return uflPropertyMatrix;
}


public void setUflPropertyMatrix(SimilarityMatrix uflPropertyMatrix) {
	this.uflPropertyMatrix = uflPropertyMatrix;
}

public Alignment<Mapping> getMLAlignment() {
	return MLAlignment;
}


public void setMLAlignment(Alignment<Mapping> mLAlignment) {
	MLAlignment = mLAlignment;
}

	@Override
	public boolean experimentHasCompleted() {
		if( userFeedback != null && userFeedback.getUserFeedback() == Validation.END_EXPERIMENT ) return true;  // we're done when the user says so
		return false;
	}

	@Override
	public void	newIteration() {
		if( userFeedback.getUserFeedback() == Validation.CORRECT ) {
			if( correctMappings == null ) {
				correctMappings = new Alignment<Mapping>(getSourceOntology().getID(), getTargetOntology().getID());
			}
			correctMappings.add( userFeedback.getCandidateMapping() );
		} else if( userFeedback.getUserFeedback() == Validation.INCORRECT ) {
			if( incorrectMappings == null ) {
				incorrectMappings = new Alignment<Mapping>(getSourceOntology().getID(), getTargetOntology().getID());
			}
			incorrectMappings.add( userFeedback.getCandidateMapping() );
		}
		realIteration++;
		int tmpIter=realIteration/usersNumber;
		currentUser=realIteration%usersNumber;
		setIterationNumber(tmpIter); 
	}

	@Override
	public Alignment<Mapping> getFinalAlignment() {
		return initialMatcher.getAlignment();
	}

	@Override
	public void info(String line) {
		if( logFile != null )
			try {
				logFile.write(line + "\n");
				logFile.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public UFLControlLogic getControlLogic() {
		return new BMlogic();
		//return new IndependentSequentialLogic();
	}
	
	@Override
	public String getDescription() {
		return "Work in progress";
	}
	
	
	public enum alignCardinality implements Serializable {
		c1_1("oneOne"),
		cn_1("nOne"),
		c1_m("OneM"),
		cn_m("nM"),
		unknown("UNKNOWN");

		private final String value;  

		alignCardinality(String value) {  
			this.value = value;  
		}  

		public static alignCardinality fromValue(String value) {  
			if (value != null) {  
				for (alignCardinality en : values()) {  
					if (en.value.equals(value)) {  
						return en;  
					}  
				}  
			}  

			// you may return a default value  
			return getDefault();  
			// or throw an exception  
			// throw new IllegalArgumentException("Invalid color: " + value);  
		}  

		public String toValue() {  
			return value;  
		}  

		public static alignCardinality getDefault() {  
			return unknown;  
		} 

		private Object readResolve () throws java.io.ObjectStreamException
		{
			if( value == c1_1.toValue() ) return c1_1;
			if( value == cn_1.toValue() ) return cn_1;
			if( value == c1_m.toValue() ) return c1_m;
			if( value == cn_m.toValue() ) return cn_m;
			return unknown;
		}


	}
	
	public void login(String id) {
		usersMappings.put(id, new ArrayList<Mapping>());
		usersGroup.put(id, getGroup());
	}

	
	private int getGroup()
	{
		int size=usersGroup.size();
		return size%3;
	}
}


