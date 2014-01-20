package am.extension.multiUserFeedback.initialization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.utility.UFLutility;

public class SignatureVectorStats {
	
	MUExperiment experiment;
	HashMap<Integer, List<Mapping>> cluster=new HashMap<>();
	public SignatureVectorStats (MUExperiment exp)
	{
		this.experiment=exp;
	}
	
	public void printSV(SimilarityMatrix sm, alignType type) throws IOException
	{
		int row=sm.getRows();
		int col=sm.getColumns();
		Mapping mp;
		Object[] sv;
		BufferedWriter bw=null;
		File file = new File("/home/frank/Documents/SignatureVector/similarityMatrix"+type.toString()+".txt");
		// if file doesnt exists, then create it
		if (!file.exists())
			file.createNewFile();
		FileWriter fw=null;

		fw = new FileWriter(file.getAbsoluteFile());

		bw = new BufferedWriter(fw);

		
		for (int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				mp=sm.get(i, j);
				sv=UFLutility.getSignatureVector(mp, experiment.initialMatcher.getComponentMatchers());
				if (UFLutility.validSsv(sv))
				{


					for (int k=0;k<sv.length;k++)
					{
						if(k<sv.length-1)
							bw.write(sv[k]+",");
						else
							bw.write(sv[k]+"");
					}
					bw.write("\n");

				}

				
			}
		}

		bw.close();

	}
	
	private void clusterize()
	{
		SimilarityMatrix sm=experiment.initialMatcher.getFinalMatcher().getClassesMatrix();
		int row=sm.getRows();
		int col=sm.getColumns();
		Mapping mp;
		Object[] sv;
		for (int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				mp=sm.get(i, j);
				sv=UFLutility.getSignatureVector(mp, experiment.initialMatcher.getComponentMatchers());
				if (cluster.size()>0)
					for (int k=0;k<cluster.size();k++)
					{
						
					}
			}
		}
	}

}
