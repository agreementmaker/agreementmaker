package am.extension.userfeedback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.List;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.extension.userfeedback.experiments.MLFExperiment;

public class MultiUserSaveFeedback extends SaveFeedback<MLFExperiment> {
	
	public MultiUserSaveFeedback()
	{
		super();
	}
	
	@Override
	public void save(MLFExperiment exp) 
	{
		// TODO Auto-generated method stub
		saveFinalAlignment(exp.getMLAlignment());
		saveForbiddenPos(exp.classesSparseMatrix,exp.propertiesSparseMatrix);
		saveRankedMappings(exp.alreadyEvaluated);
		saveCorrectMapping(exp.correctMappings);
		saveIncorrectMapping(exp.incorrectMappings);
		try {
			saveTrainingSet(exp.getTrainingSet_classes(),"classes");
			saveTrainingSet(exp.getTrainingSet_property(),"properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void saveFinalAlignment(Alignment<Mapping> finalAlignment)
	{
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(path+"finalAlignment.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(finalAlignment);
	         out.close();
	         fileOut.close();
	         System.out.println("Final Alignment saved in "+path);
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	private void saveForbiddenPos(SparseMatrix classes, SparseMatrix properties)
	{
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(path+"forbiddenPosClasses.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(classes);
	         out.close();
	         fileOut.close();
	         System.out.println("Classes Sparse saved in "+path);
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
		
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(path+"forbiddenPosProperties.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(properties);
	         out.close();
	         fileOut.close();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	private void saveRankedMappings(List<Mapping> rankedMappings)
	{
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(path+"rankedMappings.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(rankedMappings);
	         out.close();
	         fileOut.close();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	private void saveCorrectMapping(Alignment<Mapping> correct)
	{
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(path+"correctMappings.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(correct);
	         out.close();
	         fileOut.close();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	private void saveIncorrectMapping(Alignment<Mapping> incorrect)
	{
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(path+"incorrectMappings.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(incorrect);
	         out.close();
	         fileOut.close();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	private void saveTrainingSet(Object[][] obj, String type) throws IOException
	{
		
		File file = new File(path+"trainingSet_"+type+".txt");
		// if file doesnt exists, then create it
		if (!file.exists()) 
			file.createNewFile();
		FileWriter fw=null;

		fw = new FileWriter(file.getAbsoluteFile());

		BufferedWriter bw = new BufferedWriter(fw);

		for(int i=0;i<obj.length;i++)
		{

			//bw.write(i+"\t");
			for (int j=0;j<obj[0].length;j++)
			{
				bw.write(round((Double)obj[i][j],2)+"\t");
				
			}
			bw.write("\n");
		}

		bw.close();
	}
		
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
}
