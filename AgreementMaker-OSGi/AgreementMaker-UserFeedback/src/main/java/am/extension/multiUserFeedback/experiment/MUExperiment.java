/*
 * 	Francesco Loprete December 2013
 */
package am.extension.multiUserFeedback.experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Ontology;
import am.extension.multiUserFeedback.storage.MUFeedbackStorage;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.logic.IndependentSequentialLogicMultiUser;
import am.extension.userfeedback.logic.UFLControlLogic;

public class MUExperiment extends UFLExperiment {

public 	MUFeedbackStorage<UFLExperiment>	feedbackStorage;
	
public BufferedWriter logFile;
private Alignment<Mapping> MLAlignment;
private Object[][] trainingSet_classes;
private Object[][] trainingSet_property;
private SimilarityMatrix uflClassMatrix;
private SimilarityMatrix uflPropertyMatrix;
private SparseMatrix uflStorageClass_pos;
private SparseMatrix uflStorageClass_neg;
private SparseMatrix uflStorageProperty_pos;
private SparseMatrix uflStorageProperty_neg;
public List<Mapping> disRanked;
public List<Mapping> uncertainRanking;
public List<Mapping> almostRanking;
public Mapping selectedMapping;
public String feedback;
public List<Mapping> alreadyEvaluated=new ArrayList<Mapping>();
public List<Mapping> conflictualClass;
public List<Mapping> conflictualProp;

public HashMap<String, List<Mapping>> usersMappings=new HashMap<String, List<Mapping>>();
public HashMap<String, Integer> usersGroup=new HashMap<String, Integer>();
public HashMap<String, SimilarityMatrix> usersClass=new HashMap<String, SimilarityMatrix>();
public HashMap<String, SimilarityMatrix> usersProp=new HashMap<String, SimilarityMatrix>();

private alignCardinality alignCardinalityType=alignCardinality.cn_m;

public MUExperiment ()
{
	super();
	FileWriter file;
	try {
		file = new FileWriter("uflLog.txt");
		logFile=new BufferedWriter(file);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		am.Utility.displayErrorPane("Permission error: Log file can not be created", "Error");
	}
}

public alignCardinality getAlignCardinalityType() {
	return alignCardinalityType;
}


public void setAlignCardinalityType(alignCardinality alignCardinalityType) {
	this.alignCardinalityType = alignCardinalityType;
}

//forbidden position keeper
public SparseMatrix classesSparseMatrix;
public SparseMatrix propertiesSparseMatrix;


public SparseMatrix getClassesSparseMatrix() {
	return classesSparseMatrix;
}

public SparseMatrix getUflStorageClass_neg() {
	return uflStorageClass_neg;
}

public void setUflStorageClass_neg(SparseMatrix uflStorageClass_neg) {
	this.uflStorageClass_neg = uflStorageClass_neg;
}

public SparseMatrix getUflStorageProperty_neg() {
	return uflStorageProperty_neg;
}

public void setUflStorageProperty_neg(SparseMatrix uflStorageProperty_neg) {
	this.uflStorageProperty_neg = uflStorageProperty_neg;
}

public void setClassesSparseMatrix(SparseMatrix classesSparseMatrix) {
	this.classesSparseMatrix = classesSparseMatrix;
}


public SparseMatrix getPropertiesSparseMatrix() {
	return propertiesSparseMatrix;
}

public SparseMatrix getUflStorageClassPos() {
	return uflStorageClass_pos;
}

public void setUflStorageClassPos(SparseMatrix uflStorageClass) {
	this.uflStorageClass_pos = uflStorageClass;
}

public SparseMatrix getUflStoragePropertyPos() {
	return uflStorageProperty_pos;
}

public void setUflStoragePropertyPos(SparseMatrix uflStorageProperty) {
	this.uflStorageProperty_pos = uflStorageProperty;
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
	public void newIteration() {
		super.newIteration();
		// TODO: Save all the objects that we used in the previous iteration.
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
		return new IndependentSequentialLogicMultiUser();
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


