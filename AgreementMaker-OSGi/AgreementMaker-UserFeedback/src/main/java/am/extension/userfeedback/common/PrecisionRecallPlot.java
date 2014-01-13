package am.extension.userfeedback.common;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.experiments.UFLExperiment;

import com.panayotis.gnuplot.JavaPlot;

public class PrecisionRecallPlot extends CandidateSelectionEvaluation {

	private int correct;// number of correct mappings found
	//private int found;//number of mappings in rankedList  TODO: is this needed???
	//String filename = "/home/cosmin/evaluation.data";//name of the file to output the data
	String filename;
	
	public PrecisionRecallPlot() {
		super();
	}

	@Override
	public void evaluate(UFLExperiment exp ) {
		// This method is called to create the 'table' and calculate the points
		
		UFLExperiment log = exp;
		
		Alignment<Mapping> referenceAlignment = exp.getReferenceAlignment();
		
		Mapping candidateMapping = exp.candidateSelection.getCandidateMapping();
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
		
//		List<Mapping> rankedList = exp.candidateSelection.getRankedMappings();
//		Alignment<Mapping> reference = exp.getReferenceAlignment();
//		
//		correct=0;
//		float precision;
//		float recall;
//		int isCorrect;
//	
//		// open the output file
//		PrintStream out = null;
//		try
//		{
//			FileOutputStream outputfile = null;
//			if( filename == null ) 
//				outputfile = new FileOutputStream(File.createTempFile("AgreementMaker", "gnuplot"));
//			else 
//				outputfile = new FileOutputStream(filename);
//			out = new PrintStream(outputfile);	
//		}
//		catch (FileNotFoundException e) 
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		} 
//		catch (IOException e) 
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		
//		NumberFormat formatter = new DecimalFormat("0.000000000");
//		NumberFormat indexformat = new DecimalFormat("0000000000");
//		
//		// do the evaluation
//		for(int i=0;i<rankedList.size();i++)
//		{
//			isCorrect=0;
//			
//			Mapping currentMapping=rankedList.get(i);
//			
//		
//			if(reference.contains(currentMapping))//increase the number of correct and set isCorrect to true
//			{
//				correct++;
//				isCorrect=1;
//			}
//			
//			precision=(float)correct/(float)(i+1);
//			recall=(float)correct/(float)reference.size();
//			
//			//write the data to a file
//			out.println( indexformat.format(i)+ ", " + 
//						 formatter.format(recall) + ", " + 
//						 formatter.format(precision) + ", " + 
//						 isCorrect + ", " + formatter.format(currentMapping.getSimilarity()) + ", "
//						 + currentMapping.toString());
//		}//end for loop
//		
//		
		
		
		done();
	}
	
	private void showPlot() {
		JavaPlot plot = new JavaPlot();
		
	}
}
