package am.extension.userfeedback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.extension.userfeedback.MLFeedback.MLFExperiment;

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
		saveRankedMappings(exp.allRanked);
		saveCorrectMapping(exp.correctMappings);
		saveIncorrectMapping(exp.incorrectMappings);
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
		
	

	
}
