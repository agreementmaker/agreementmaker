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
