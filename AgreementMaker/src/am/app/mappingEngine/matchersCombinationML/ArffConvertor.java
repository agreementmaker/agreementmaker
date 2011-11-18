package am.app.mappingEngine.matchersCombinationML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ArffConvertor {
	
	public static void main(String args[])throws IOException
	{
		String fileName="";
		String type="training";
		
		File inputFile=new File(fileName);
		BufferedReader inputReader=new BufferedReader(new FileReader(inputFile));
		BufferedWriter outputWriter=new BufferedWriter(new FileWriter(new File("bench/"+inputFile.getName()+".arff")));
				
		while(inputReader.ready())
		{
			String inputLine=inputReader.readLine();
			String inputLineParts[]=inputLine.split("\t");
		}
	}

}
