/*
 * 	Francesco Loprete October 2013
 */
package am.extension.userfeedback.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.extension.collaborationClient.api.CollaborationAPI;
import am.extension.collaborationClient.api.CollaborationCandidateMapping;
import am.extension.collaborationClient.api.CollaborationTask;
import am.extension.collaborationClient.api.CollaborationUser;
import am.extension.collaborationClient.restful.RESTfulCollaborationServer;
import am.extension.multiUserFeedback.ui.TaskSelectionDialog;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.logic.IndependentSequentialLogicML;
import am.extension.userfeedback.logic.IndependentSequentialLogicMultiUser;
import am.extension.userfeedback.logic.UFLControlLogic;
import am.ui.UICore;

public class MLFExperiment extends UFLExperiment {

	private static Logger LOG = Logger.getLogger(MLFExperiment.class);
	
	public CollaborationAPI server;
	public CollaborationUser clientID;
	public CollaborationTask selectedTask;
	public CollaborationCandidateMapping candidateMapping;
	
	public int feedbackCount = 0;
	
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
public List<Mapping> alreadyEvaluated=new ArrayList<Mapping>();
public List<Mapping> conflictualClass;
public List<Mapping> conflictualProp;

private alignCardinality alignCardinalityType=alignCardinality.cn_m;

	

public alignCardinality getAlignCardinalityType() {
	return alignCardinalityType;
}


public void setAlignCardinalityType(alignCardinality alignCardinalityType) {
	this.alignCardinalityType = alignCardinalityType;
}


public SparseMatrix classesSparseMatrix;
public SparseMatrix propertiesSparseMatrix;

public File logFileFile;


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

	public MLFExperiment() {
		super();
		
		FileWriter file;
		try {
			logFileFile = new File("UFLlog.txt");
			file = new FileWriter(logFileFile, true);
			logFile=new BufferedWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			am.Utility.displayErrorPane("<html><p>Permission error:</p><p>Log file can not be created!</p><p>Please make the AgreementMaker directory writable or the UFLlog.txt will not be created.</p></html>", "Error");
		}
		
		connection();
	}
	
	
	private void connection()
	{
		// connect to the server
		// TODO: Make the server baseURL be configured by the user?
		//String baseURL = "http://127.0.0.1:9000";
		String baseURL = "http://advis.cs.uic.edu:9000";
		server = new RESTfulCollaborationServer(baseURL);
		clientID = server.register();
		
		LOG.info("Connected to " + baseURL + ", ClientID: " + clientID);
		
		List<CollaborationTask> taskList = server.getTaskList();
		
		LOG.info("Retrieved " + taskList.size() + " tasks.");
		
		TaskSelectionDialog tsd = new TaskSelectionDialog(taskList);
		selectedTask = tsd.getTask();
		
		LOG.info("User selected task: " + selectedTask);
		
		
		OntologyDefinition sourceOntDef = server.getOntologyDefinition(selectedTask.getSourceOntologyURL());
		LOG.info("Loading source ontology: " + sourceOntDef);
		Ontology sourceOnt = UICore.getUI().openFile(sourceOntDef);
		Core.getInstance().setSourceOntology(sourceOnt);
		
		
		OntologyDefinition targetOntDef = server.getOntologyDefinition(selectedTask.getTargetOntologyURL());
		LOG.info("Loading target ontology: " + targetOntDef);
		Ontology targetOnt = UICore.getUI().openFile(targetOntDef);
		Core.getInstance().setTargetOntology(targetOnt);
	}
	
	
	
	@Override
	public Ontology getSourceOntology() {
		return Core.getInstance().getSourceOntology();
	}

	@Override
	public Ontology getTargetOntology() {
		return Core.getInstance().getTargetOntology();
	}

	@Override
	public Alignment<Mapping> getReferenceAlignment() {
		List<MatchingTask> tasks = Core.getInstance().getMatchingTasks();
		for( MatchingTask m : tasks ) {
			if( m.matchingAlgorithm instanceof ReferenceAlignmentMatcher ) {
				// return the alignment of the first reference alignment matcher
				return m.selectionResult.getAlignment();
			}
		}
		
		// if we are connected to a server, get the reference alignment from the server
		if( server != null && selectedTask != null && selectedTask.getReferenceAlignmentURL() != null ) {
			return server.getReferenceAlignment(selectedTask.getReferenceAlignmentURL());
		}
		
		return null;
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
		return new IndependentSequentialLogicML();
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
	
}


