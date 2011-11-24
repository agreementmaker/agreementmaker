package am.app.mappingEngine.matchersCombinationML;
/**
 * 
 * This class is used to convert the training and test files into weka supported
 * Arff format
 */


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
	
	ArffConvertor(String fileName,String type,ArrayList<String> matcherList)
	{
		this.fileName=fileName;
		this.type=type;
		this.listOfMatchers=matcherList;
	}

	/**
	 * generates Arff file based on the type
	 * type can be training and test
	 * @throws IOException
	 */
	void generateArffFile() throws IOException
	{
		File file=new File(fileName);
		BufferedReader inputReader=new BufferedReader(new FileReader(file));
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File("bench/arff/"+file.getName()+".arff")));
		if(type.equals("training"))
		{
		outputWriter.write("% Title: Training set\n\n");
		outputWriter.write("@RELATION training_set\n\n");
		}
		if(type.equals("test"))
		{
			outputWriter.write("% Title: Test set\n\n");
			outputWriter.write("@RELATION test_set\n\n");
		}
		for(int i=0;i<listOfMatchers.size();i++)
		{
			String currentMatcher=listOfMatchers.get(i);
			outputWriter.write("@ATTRIBUTE\t"+currentMatcher.trim().replaceAll(" ", "_")+"\tNUMERIC\n");
			outputWriter.write("@ATTRIBUTE\t"+currentMatcher.trim().replaceAll(" ", "_")+"found"+"\tNUMERIC\n");
		}
		outputWriter.write("@ATTRIBUTE\t"+"Matcher_Vote"+"\tNUMERIC\n");
		outputWriter.write("@ATTRIBUTE\ttarget\t{0.0,1.0}\n");
		
	
		outputWriter.write("\n@DATA\n");
		while(inputReader.ready())
		{
			String inputLine=inputReader.readLine().replaceAll("\t", ",");
			if(type.equals("test"))
			{
				inputLine+=",?";
			}
			outputWriter.write(inputLine+"\n");
		}
		outputWriter.close();
	}

}
