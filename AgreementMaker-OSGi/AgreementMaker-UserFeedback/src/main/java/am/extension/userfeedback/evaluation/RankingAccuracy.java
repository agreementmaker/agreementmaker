package am.extension.userfeedback.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

public class RankingAccuracy extends CandidateSelectionEvaluation{
	private int falsePositive=0;
	private int falseNegative=0;
	private int count=0;
	private UFLExperiment experiment;
	
	
	@Override
	public void evaluate(UFLExperiment exp) {
		// TODO Auto-generated method stub
		
UFLExperiment log = exp;
		
		Alignment<Mapping> referenceAlignment = exp.getReferenceAlignment();
		
		Mapping candidateMapping = exp.candidateSelection.getSelectedMapping();
		if(candidateMapping == null) {
			log.info( "Candidate Selection presented null mapping. Mapping source: " + ((MUExperiment)exp).data.mappingSource.getClass().getName() );
			return;
		}
		boolean mappingIsInReference = false;
		if( referenceAlignment.contains(candidateMapping) ) mappingIsInReference = true;
		
		Alignment<Mapping> finalAlignment = exp.getFinalAlignment();
		boolean mappingIsInAlignment = false;
		if( finalAlignment.contains(candidateMapping) ) mappingIsInAlignment = true;
		
		
		String annotationString = "(in reference: no)";
		if( mappingIsInReference ) annotationString = "(in reference: yes)";
		
		String annotationString2 = "(in alignment: no)";
		if( mappingIsInAlignment ) annotationString2 = "(in alignment: yes)";
		
		log.info("Candidate selection mapping: " + annotationString + " " + annotationString2 + " " + candidateMapping );
		log.info("");
		
		this.experiment=exp;

		Alignment<Mapping> computedAlignment=exp.getFinalAlignment();
		if ((computedAlignment.contains(candidateMapping))&&(!referenceAlignment.contains(candidateMapping)))
		{
			falsePositive++;
		}
		if ((!computedAlignment.contains(candidateMapping))&&(referenceAlignment.contains(candidateMapping)))
		{
			falseNegative++;
		}
		
		count++;
		
		double accuracy=((double)(falseNegative+falsePositive))/(double)count;
		
		try {
			writeAccuracy(accuracy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		done();
		
	}
	
	
	private void writeAccuracy(double accuracy) throws Exception
	{
		String currentLog = experiment.setup.parameters.getParameter(Parameter.LOGFILE);
		File file = new File(Core.getInstance().getRoot() + currentLog + "-accuracy.txt");

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(accuracy+"\n");
		bw.close();
	}
	

}
