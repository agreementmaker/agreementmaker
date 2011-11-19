package am.app.mappingEngine.matchersCombinationML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ArffConvertor {
		
	String fileName;
	String type;
	ArrayList<String> listOfMatchers=new ArrayList<String>();
	
	ArffConvertor(String fileName,String type)
	{
		this.fileName=fileName;
		this.type=type;
	}
	
	void generateArffFile(String fileName,String type) throws IOException
	{
		File file=new File(fileName);
		BufferedReader inputReader=new BufferedReader(new FileReader(file));
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter("/bench/arff/"+file.getName()+".arff"));		
		outputWriter.write("% Title: Training set\n\n");
		outputWriter.write("@RELATION training_set\n\n");
			
		for(int i=0;i<listOfMatchers.size();i++)
		{
			String currentMatcher=listOfMatchers.get(i);
			outputWriter.write("@ATTRIBUTE\t"+currentMatcher.trim().replaceAll(" ", "_")+"\tNUMERIC\n");
		}
		outputWriter.write("@ATTRIBUTE\ttarget\t{0.0,1.0}\n");
		outputWriter.write("\n@DATA\n");		
		while(inputReader.ready())
		{
			String inputLine=inputReader.readLine().replaceAll("\t", ",");
			outputWriter.write(inputLine+"\n");
		}
		outputWriter.close();
	}

}
