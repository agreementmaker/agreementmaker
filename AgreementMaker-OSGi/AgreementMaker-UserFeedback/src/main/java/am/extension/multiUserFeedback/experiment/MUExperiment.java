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

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.extension.userfeedback.logic.PersistentSequentialControlLogic;
import am.extension.userfeedback.logic.api.UFLControlLogic;
import am.extension.userfeedback.rankingStrategies.StrategyInterface;
import am.ui.UIUtility;

import com.tomgibara.cluster.gvm.dbl.DblResult;

/**
 * Multi-User UFL Experiment.
 */
public class MUExperiment extends UFLExperiment {
	
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
	public int feedbackCount;
	public String feedback;

	public List<DblResult<List<double[]>>> clusterC;
	public List<DblResult<List<double[]>>> clusterP;
	public List<Mapping> alreadyEvaluated=new ArrayList<Mapping>();
	public List<Mapping> conflictualClass;
	public List<Mapping> conflictualProp;
	public HashMap<String, List<Mapping>> usersMappings=new HashMap<String, List<Mapping>>();
	public HashMap<String, Integer> usersGroup=new HashMap<String, Integer>();
	public HashMap<String, SimilarityMatrix> usersClass=new HashMap<String, SimilarityMatrix>();
	public HashMap<String, SimilarityMatrix> usersProp=new HashMap<String, SimilarityMatrix>();

	public static class csData{
		public int[] count={0,0,0};
		public int total=0;
		public List<Mapping> mqList;
		public List<Mapping> drList;
		public List<Mapping> rrList;
		public StrategyInterface mappingSource;
	}

	public csData data=new csData();


	private alignCardinality alignCardinalityType=alignCardinality.cn_m;

	public MUExperiment(UFLExperimentSetup setup) {
		super(setup);
		
		String log = setup.parameters.getParameter(Parameter.LOGFILE);
		String root = Core.getInstance().getRoot();
		try {
			FileWriter fw = new FileWriter(root + log, false);
			logFile = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
			UIUtility.displayErrorPane("Permission error: Log file can not be created.\n" + root + log, "Error");
		}
	}

	public alignCardinality getAlignCardinalityType() {
		return alignCardinalityType;
	}

	public void setAlignCardinalityType(alignCardinality alignCardinalityType) {
		this.alignCardinalityType = alignCardinalityType;
	}

	//forbidden position keeper
	private SparseMatrix forbiddenPositionsClasses;
	private SparseMatrix forbiddenPositionsProperties;

	public SparseMatrix getForbiddenPositions(alignType type) {
		switch(type) {
		case aligningClasses:
			return forbiddenPositionsClasses;
		case aligningProperties:
			return forbiddenPositionsProperties;
		default:
			throw new RuntimeException(type + " is not accepted");
		}
	}
	
	public void setForbiddenPositions(alignType type, SparseMatrix matrix) {
		switch(type) {
		case aligningClasses:
			this.forbiddenPositionsClasses = matrix; return;
		case aligningProperties:
			this.forbiddenPositionsProperties = matrix;	return;
		default:
			throw new RuntimeException(type + " is not accepted");
		}
	}
	
	public SparseMatrix getFeedbackMatrix(alignType type, Validation validation) {
		switch(type) {
		case aligningClasses:
			switch(validation) {
			case CORRECT:
				return uflStorageClass_pos;
			case INCORRECT:
				return uflStorageClass_neg;
			default:
				throw new RuntimeException(validation + " is not accepted");
			}
		case aligningProperties:
			switch(validation) {
			case CORRECT:
				return uflStorageProperty_pos;
			case INCORRECT:
				return uflStorageProperty_neg;
			default:
				throw new RuntimeException(validation + " is not accepted");
			}
		default:
			throw new RuntimeException(type + " is not accepted");
		}
	}
	
	public void setFeedBackMatrix(SparseMatrix mtrx, alignType type, Validation validation)
	{
		switch(type) {
		case aligningClasses:
			switch(validation) {
			case CORRECT:
				uflStorageClass_pos=mtrx;
				break;
			case INCORRECT:
				uflStorageClass_neg=mtrx;
				break;
			default:
				throw new RuntimeException(validation + " is not accepted");
			}
			break;
		case aligningProperties:
			switch(validation) {
			case CORRECT:
				uflStorageProperty_pos=mtrx;
				break;
			case INCORRECT:
				uflStorageProperty_neg=mtrx;
				break;
			default:
				throw new RuntimeException(validation + " is not accepted");
			}
			break;
		default:
			throw new RuntimeException(type + " is not accepted");
		}
	}

	public Object[][] getTrainingSet(alignType type) {
		switch(type) {
		case aligningClasses:
			return trainingSet_classes;
		case aligningProperties:
			return trainingSet_property;
		default:
			throw new RuntimeException(type + " is not accepted");
		}
	}

	public void setTrainingSet_classes(Object[][] trainingSet_classes) {
		this.trainingSet_classes = trainingSet_classes;
	}
	
	public void setTrainingSet_property(Object[][] trainingSet_property) {
		this.trainingSet_property = trainingSet_property;
	}

	public SimilarityMatrix getComputedUFLMatrix(alignType type) {
		switch(type) {
		case aligningClasses:
			return uflClassMatrix;
		case aligningProperties:
			return uflPropertyMatrix;
		default:
			throw new RuntimeException(type + " is not accepted");
		}
	}

	public void setComputedUFLMatrix(alignType type, SimilarityMatrix matrix) {
		switch(type) {
		case aligningClasses:
			this.uflClassMatrix = matrix; break;
		case aligningProperties:
			this.uflPropertyMatrix = matrix; break;
		default:
			throw new RuntimeException(type + " is not accepted");
		}
	}


	/**
	 * @deprecated Use {@link #getFinalAlignment()}.
	 */
	@Deprecated
	public Alignment<Mapping> getMLAlignment() {
		return MLAlignment;
	}


	public void setMLAlignment(Alignment<Mapping> mLAlignment) {
		MLAlignment = mLAlignment;
	}

	@Override
	public Alignment<Mapping> getFinalAlignment() {
		return MLAlignment;
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
	public UFLControlLogic<MUExperiment> getControlLogic() {
		return new PersistentSequentialControlLogic();
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