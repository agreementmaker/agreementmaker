package am.extension.userfeedback.inizialization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.extension.collaborationClient.api.CollaborationTask;
import am.extension.collaborationClient.restful.RESTfulCollaborationServer;
import am.extension.multiUserFeedback.MUExperiment;
import am.extension.multiUserFeedback.ui.TaskSelectionDialog;
import am.extension.userfeedback.FeedbackLoopInizialization;
import am.ui.UICore;

public class RestfulDataInizialization extends FeedbackLoopInizialization<MUExperiment>{
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	MUExperiment experiment;
	private static Logger LOG = Logger.getLogger(RestfulDataInizialization.class);
	@Override
	public void inizialize(MUExperiment exp) {
		// TODO Auto-generated method stub
		this.experiment=exp;
		connection();
		
		// TODO Auto-generated method stub
		SimilarityMatrix smClass=exp.initialMatcher.getFinalMatcher().getClassesMatrix().clone();
		SimilarityMatrix smProperty=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix().clone();
		for(int i=0;i<smClass.getRows();i++)
			for(int j=0;j<smClass.getColumns();j++)
				smClass.setSimilarity(i, j, 0.5);
		for(int i=0;i<smProperty.getRows();i++)
			for(int j=0;j<smProperty.getColumns();j++)
				smProperty.setSimilarity(i, j, 0.5);
		smClass=prepareSMforNB(smClass);
		smProperty=prepareSMforNB(smProperty);
		
		exp.setUflClassMatrix(smClass);
		exp.setUflPropertyMatrix(smProperty);
		
		done();
	}
	
	private void connection()
	{
		// connect to the server
				// TODO: Make the server baseURL be configured by the user?
				String baseURL = "http://127.0.0.1:9000";
				experiment.server = new RESTfulCollaborationServer(baseURL);
				experiment.clientID = experiment.server.register();
				
				LOG.info("Connected to " + baseURL + ", ClientID: " + experiment.clientID);
				
				List<CollaborationTask> taskList = experiment.server.getTaskList();
				
				LOG.info("Retrieved " + taskList.size() + " tasks.");
				
				TaskSelectionDialog tsd = new TaskSelectionDialog(taskList);
				experiment.selectedTask = tsd.getTask();
				
				LOG.info("User selected task: " + experiment.selectedTask);
				
				
				OntologyDefinition sourceOntDef = experiment.server.getOntologyDefinition(experiment.selectedTask.getSourceOntologyURL());
				LOG.info("Loading source ontology: " + sourceOntDef);
				Ontology sourceOnt = UICore.getUI().openFile(sourceOntDef);
				Core.getInstance().setSourceOntology(sourceOnt);
				
				
				OntologyDefinition targetOntDef = experiment.server.getOntologyDefinition(experiment.selectedTask.getTargetOntologyURL());
				LOG.info("Loading target ontology: " + targetOntDef);
				Ontology targetOnt = UICore.getUI().openFile(targetOntDef);
				Core.getInstance().setTargetOntology(targetOnt);
				
				LOG.info("Loading the reference alignment from: " + experiment.selectedTask.getReferenceAlignmentURL());
				experiment.referenceAlignment = experiment.server.getReferenceAlignment(experiment.selectedTask.getReferenceAlignmentURL());
				
				if( experiment.referenceAlignment == null ) {
					LOG.info("Reference alignment was not loaded.");
				}
				else {
					LOG.info("Reference alignment loaded: " + experiment.referenceAlignment.size() + " mappings");
				}
				
				experiment.classesSparseMatrix = 
						new SparseMatrix(
								Core.getInstance().getSourceOntology(),
								Core.getInstance().getTargetOntology(), 
								alignType.aligningClasses);
				
				experiment.propertiesSparseMatrix = 
						new SparseMatrix(
								Core.getInstance().getSourceOntology(),
								Core.getInstance().getTargetOntology(), 
								alignType.aligningProperties);
				
				// setup the log file
				try {
					FileWriter fr = new FileWriter("/home/frank/Desktop/ufllog.txt");
					experiment.logFile = new BufferedWriter(fr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	private SimilarityMatrix prepareSMforNB(SimilarityMatrix sm)
	{
		Mapping mp;
		Object[] ssv;
		for(int i=0;i<sm.getRows();i++)
			for(int j=0;j<sm.getColumns();j++)
			{
				mp = sm.get(i, j);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
				{ 
					sm.setSimilarity(i, j, 0.0);
				}
			}
		
		return sm;
	}
	
	private Object[] getSignatureVector(Mapping mp)
	{
		int size=inputMatchers.size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		AbstractMatcher a;
		Object[] ssv=new Object[size];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			ssv[i]=a.getAlignment().getSimilarity(sourceNode, targetNode);
			
		}
		return ssv;
	}
	
	
	//check if the signature vector is valid. A valid signature vector must have at least one non zero element.
	private boolean validSsv(Object[] ssv)
	{
		Object obj=0.0;
		for(int i=0;i<ssv.length;i++)
		{
			if (!ssv[i].equals(obj))
				return true;
		}
		return false;
	}

}
