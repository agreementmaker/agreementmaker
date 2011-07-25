package am.app.userfeedbackloop.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.panayotis.gnuplot.JavaPlot;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.userfeedbackloop.CandidateSelectionEvaluation;
import am.app.userfeedbackloop.UFLExperiment;

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
		
		List<Mapping> rankedList = exp.candidateSelection.getRankedMappings();
		Alignment<Mapping> reference = exp.getReferenceAlignment();
		
		correct=0;
		float precision;
		float recall;
		int isCorrect;
	
		// open the output file
		PrintStream out = null;
		try
		{
			FileOutputStream outputfile = null;
			if( filename == null ) 
				outputfile = new FileOutputStream(File.createTempFile("AgreementMaker", "gnuplot"));
			else 
				outputfile = new FileOutputStream(filename);
			out = new PrintStream(outputfile);	
		}
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		NumberFormat formatter = new DecimalFormat("0.000000000");
		NumberFormat indexformat = new DecimalFormat("0000000000");
		
		// do the evaluation
		for(int i=0;i<rankedList.size();i++)
		{
			isCorrect=0;
			
			Mapping currentMapping=rankedList.get(i);
			
		
			if(reference.contains(currentMapping))//increase the number of correct and set isCorrect to true
			{
				correct++;
				isCorrect=1;
			}
			
			precision=(float)correct/(float)(i+1);
			recall=(float)correct/(float)reference.size();
			
			//write the data to a file
			out.println( indexformat.format(i)+ ", " + 
						 formatter.format(recall) + ", " + 
						 formatter.format(precision) + ", " + 
						 isCorrect + ", " + formatter.format(currentMapping.getSimilarity()) + ", "
						 + currentMapping.toString());
		}//end for loop
		
		done();
	}
	
	private void showPlot() {
		JavaPlot plot = new JavaPlot();
		
	}
}
