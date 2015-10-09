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
	
	public  void write(String refAlignment,String myAlignment)
	   {
		    try {
		    	writer.write(refAlignment);
		    	writer.write(',');
		    	writer.write(myAlignment);
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
