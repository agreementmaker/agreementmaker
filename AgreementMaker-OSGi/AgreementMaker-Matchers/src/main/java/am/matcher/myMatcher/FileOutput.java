package am.matcher.myMatcher;

import java.io.FileWriter;
import java.io.IOException;

public class FileOutput 
{
	FileWriter writer;
	FileOutput(String fileName)
	{
		try
		{
		 writer= new FileWriter(fileName);
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}
	public void writeHeader()
	{
			try {
				writer.write("Reference Alignment");
				writer.write(',');
				writer.write("My Alignment");
				writer.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	}
	
	public  void write(String refAlignment,String myAlignment,String parent1,String parent2)
	   {
		    try {
		    	writer.write(refAlignment);
		    	writer.write(',');
		    	writer.write(myAlignment);
		    	writer.write(',');
		    	if (parent1!=null)
		    	{
		    		writer.write(parent1);
		    		writer.write(',');
		    	}
		    	if (parent2!=null)
		    	{
		    		writer.write(parent2);
		    	}
		    	writer.write('\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		    
		}
	public void close()
	{
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}
		
	    

}
