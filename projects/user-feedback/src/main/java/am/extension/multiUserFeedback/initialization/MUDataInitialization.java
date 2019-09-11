package am.extension.multiUserFeedback.initialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.common.ExperimentIteration;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.inizialization.FeedbackLoopInizialization;

public class MUDataInitialization extends FeedbackLoopInizialization<MUExperiment> {
	
	private static final Logger LOG = Logger.getLogger(MUDataInitialization.class);
	
	List<MatchingTask> inputMatchers = new ArrayList<>();
	private MUExperiment experiment;
	
	@Override
	public void initialize(MUExperiment exp) {
		// TODO Auto-generated method stub
		this.experiment = exp;
		inputMatchers = exp.initialMatcher.getComponentMatchers();
		
		SignatureVectorStats svs = new SignatureVectorStats(exp);
		try {
			svs.printSV(exp.initialMatcher.getFinalMatcher().getClassesMatrix(), alignType.aligningClasses);
			svs.printSV(exp.initialMatcher.getFinalMatcher().getPropertiesMatrix(), alignType.aligningProperties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		SimilarityMatrix smClass = exp.initialMatcher.getFinalMatcher().getClassesMatrix().clone();
		SimilarityMatrix smProperty = exp.initialMatcher.getFinalMatcher().getPropertiesMatrix().clone();
		
		//SimilarityMatrix am=exp.initialMatcher.getFinalMatcher().getClassesMatrix();
		smClass = prepare(smClass);
		
		//am=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix();
		smProperty = prepare(smProperty);
		

		computeAlignment(smClass, smProperty);
		
		List<SimilarityMatrix> lstC=new ArrayList<>();
		List<SimilarityMatrix> lstP=new ArrayList<>();
		
		for (int i=0;i<inputMatchers.size();i++)
		{
			lstC.add(inputMatchers.get(i).matcherResult.getClassesMatrix());
			lstP.add(inputMatchers.get(i).matcherResult.getPropertiesMatrix());
		}
		
		
//		GVM_Clustering gvm=new GVM_Clustering(lstC.toArray(new SimilarityMatrix[0]), count_vsv/4);
//		gvm.cluster();
//		exp.clusterC=gvm.getClusters();
//		
//		gvm=new GVM_Clustering(lstP.toArray(new SimilarityMatrix[0]), count_vsv/4);
//		gvm.cluster();
//		exp.clusterP=gvm.getClusters();
		
		exp.setComputedUFLMatrix(alignType.aligningClasses, smClass);
		exp.setComputedUFLMatrix(alignType.aligningProperties, smProperty);
		
		exp.setForbiddenPositions(alignType.aligningClasses, 
				new SparseMatrix(
						Core.getInstance().getSourceOntology(),
						Core.getInstance().getTargetOntology(), 
						alignType.aligningClasses));
		
		exp.setForbiddenPositions(alignType.aligningProperties, 
				new SparseMatrix(
						Core.getInstance().getSourceOntology(),
						Core.getInstance().getTargetOntology(), 
						alignType.aligningProperties));
		
		
		// set the UFL matrices for classes
		SparseMatrix sparseClassPos = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningClasses);
		
		SparseMatrix sparseClassNeg = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningClasses);
		

		exp.setFeedBackMatrix(sparseClassPos, alignType.aligningClasses, Validation.CORRECT);
		exp.setFeedBackMatrix(sparseClassNeg, alignType.aligningClasses, Validation.INCORRECT);
		
		
		// set the UFL matrices for properties
		SparseMatrix sparsePropPos = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(),
				alignType.aligningProperties);
		
		SparseMatrix sparsePropNeg = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningProperties);
		
		exp.setFeedBackMatrix(sparsePropPos, alignType.aligningProperties, Validation.CORRECT);
		exp.setFeedBackMatrix(sparsePropNeg, alignType.aligningProperties, Validation.INCORRECT);
		
		//FP and FN count for the initial alignment
		falseMappingCount();
		
		// output the experiment description
		StringBuilder d = new StringBuilder();
		
		d.append("\n\n============================ Running UFL Experiment: =================================\n");
		d.append("         NUM_USERS: " + exp.setup.parameters.getIntParameter(Parameter.NUM_USERS) + "\n");
		d.append("    NUM_ITERATIONS: " + exp.setup.parameters.getIntParameter(Parameter.NUM_ITERATIONS) + "\n");
		d.append("        ERROR_RATE: " + exp.setup.parameters.getDoubleParameter(Parameter.ERROR_RATE) + "\n");
		d.append(" REVALIDATION_RATE: " + exp.setup.parameters.getParameter(Parameter.REVALIDATION_RATE) + "\n");
		d.append("         STATIC_CS: " + exp.setup.parameters.getBooleanParameter(Parameter.STATIC_CANDIDATE_SELECTION) + "\n");
		d.append("PROPAGATION_METHOD: " + exp.setup.parameters.getParameter(Parameter.PROPAGATION_METHOD) + "\n");
		d.append("          LOG FILE: " + exp.setup.parameters.getParameter(Parameter.LOGFILE) + "\n");
		d.append("======================================================================================\n");
		
		String sourceFile = Core.getInstance().getSourceOntology().getFilename();
		if( sourceFile.length() >= 51 ) {
			d.append("Source Ont: ..." + sourceFile.substring(sourceFile.length()-50-1, sourceFile.length()) + "\n");
		}
		else {
			d.append("Source Ont: " + sourceFile + "\n");
		}
		
		String targetFile = Core.getInstance().getTargetOntology().getFilename();
		if( targetFile.length() >= 51 ) {
			d.append("Target Ont: ..." + targetFile.substring(targetFile.length()-50-1, targetFile.length()) + "\n");
		}
		else {
			d.append("Target Ont: " + targetFile + "\n");
		}
		
		exp.info(d.toString());
		LOG.info(d.toString());
		
		
		done();
	}

//	private SimilarityMatrix prepare(SimilarityMatrix sm, SimilarityMatrix am)
//	{
//		double sim=0;
//		Mapping mp;
//		Object[] ssv;
//		for(int i=0;i<sm.getRows();i++)
//			for(int j=0;j<sm.getColumns();j++)
//			{
//				mp = sm.get(i, j);
//				ssv=UFLutility.getSignatureVector(mp,experiment.initialMatcher.getComponentMatchers());
//				if (!UFLutility.validSsv(ssv))
//				{ 
//					sm.setSimilarity(i, j, 0.0);
//				}
//				else
//				{
//					sim=am.getSimilarity(i, j);
//					if (sim==0)
//						System.out.println("ciao");
//					sm.setSimilarity(i, j, sim );
//					count_vsv++;
//				}
//			}
//		
//		return sm;
//	}
	
	private SimilarityMatrix prepare(SimilarityMatrix sm)
	{
		Mapping mp;
		//Object[] ssv;
		for(int i=0;i<sm.getRows();i++)
			for(int j=0;j<sm.getColumns();j++)
			{
				mp = sm.get(i, j);
				//ssv=UFLutility.getSignatureVector(mp,experiment.initialMatcher.getComponentMatchers());
				if (!experiment.initialMatcher.getFinalMatcher().getAlignment().contains(mp))
				{ 
					sm.setSimilarity(i, j, 0.0);
				}
//				else
//				{
//					sim=am.getSimilarity(i, j);
//					sm.setSimilarity(i, j, sim );
//					count_vsv++;
//				}
			}
		
		return sm;
	}
	
	private void computeAlignment(SimilarityMatrix classMatrix, SimilarityMatrix propMatrix)
	{

		MatcherResult mr = new MatcherResult((MatchingTask)null);
		mr.setClassesMatrix(classMatrix);
		mr.setPropertiesMatrix(propMatrix);
		mr.setSourceOntology(experiment.getSourceOntology());
		mr.setTargetOntology(experiment.getTargetOntology());
		
		DefaultSelectionParameters selParam = new DefaultSelectionParameters();
		selParam.threshold = experiment.setup.parameters.getDoubleParameter(Parameter.IM_THRESHOLD);
		selParam.maxSourceAlign = experiment.initialMatcher.getFinalMatcher().getParam().maxSourceAlign;
		selParam.maxTargetAlign = experiment.initialMatcher.getFinalMatcher().getParam().maxTargetAlign;
		selParam.inputResult = mr;
		
		MwbmSelection selection = new MwbmSelection();
		selection.setParameters(selParam);
		
		selection.select();
		
		experiment.setMLAlignment(selection.getResult().getAlignment());
		
		
		DeltaFromReference deltaFromReference = new DeltaFromReference(experiment.getReferenceAlignment());
		
		experiment.info("Reference Alignment:");
		experiment.info(experiment.getReferenceAlignment().toString());
		
		int initialDelta = deltaFromReference.getDelta(experiment.getFinalAlignment());
		AlignmentMetrics initialMetrics= new AlignmentMetrics(experiment.getReferenceAlignment(), experiment.getFinalAlignment());
		
		int currentIteration = experiment.getIterationNumber();
		
		if( currentIteration != 0 ) {
			throw new AssertionError("Data initialization can only run at iteration 0.");
		}
		
		experiment.info("Iteration: " + currentIteration + 
				"\tDelta from reference: " + initialDelta + 
				"\tPrecision: " + initialMetrics.getPrecisionPercent() + 
				"\tRecall: " + initialMetrics.getRecallPercent() + 
				"\tFMeasure: " + initialMetrics.getFMeasurePercent());
		experiment.info("");
		
		ExperimentIteration iteration = 
				new ExperimentIteration(initialMetrics.getPrecision(), initialMetrics.getRecall(), initialDelta);
		experiment.experimentData.addIteration(iteration);
	}
	
	private void falseMappingCount()
	{
		int falsePositive=0;
		int falseNegative=0;
		Alignment<Mapping> referenceAlignment = experiment.getReferenceAlignment();
		
		Alignment<Mapping> computedAlignment = experiment.getFinalAlignment();
		
		for(Mapping m : computedAlignment)
		{
			if (!referenceAlignment.contains(m))
			{
				falsePositive++;
			}
		}
		for (Mapping m : referenceAlignment)
		{
			if (!computedAlignment.contains(m))
			{
				falseNegative++;
			}
		}
		
		experiment.info("\tNumber of FalsePositive mappings in initial alignment: "+ falsePositive);
		experiment.info("\tNumber of FalseNegative mappings in initial alignment: "+ falseNegative);
	}


}
