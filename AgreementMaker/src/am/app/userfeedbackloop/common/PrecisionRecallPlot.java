package am.app.userfeedbackloop.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.userfeedbackloop.CandidateSelectionEvaluation;

public class PrecisionRecallPlot extends CandidateSelectionEvaluation {

	private int correct;// number of correct mappings found
	//private int found;//number of mappings in rankedList  TODO: is this needed???
	String filename;//name of the file to output the data
	
	public PrecisionRecallPlot(Alignment<Mapping> rL, Alignment<Mapping> ref, String filename) {
		super(rL, ref);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void evaluate() {
		// This method is called to create the 'table' and calculate the points
		correct=0;
		float persision;
		float recall;
		int isCorrect;
		
		for(int i=0;i<rankedList.size();i++)
		{
			isCorrect=0;
			
			Mapping currentMapping=rankedList.getMapping(i);
			
			if(reference.contains(currentMapping))//increase the number of correct and set isCorrect to true
			{
				correct++;
				isCorrect=1;
			}
			
			persision=(float)correct/(float)(i+1);
			recall=(float)correct/(float)reference.size();
			
			//write the data to a file
			write(persision, recall, currentMapping.toString(),isCorrect);
		}//end for loop
	}
	
	public void write(float per, float rec, String mapping, int cor)
	{
		try {
			PrintStream out = new PrintStream(new FileOutputStream(filename));
			out.println(per+", "+rec+", "+mapping+", "+cor);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
