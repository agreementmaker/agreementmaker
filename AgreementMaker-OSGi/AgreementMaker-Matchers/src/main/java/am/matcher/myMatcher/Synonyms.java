package am.matcher.myMatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.*;

public class Synonyms 
{
	HashMap<String,List<String>> synset=new HashMap<String,List<String>>();
	public void synonymList() throws IOException
	{

		String line="";
		
		BufferedReader br = new BufferedReader(new FileReader("synonyms.csv"));
		
		while ((line = br.readLine()) != null) {

		        // use comma as separator
			String[] syn = line.split(",");
			List<String> slist = new ArrayList<String>(Arrays.asList(syn));
			
			
			for(int i=0;i<syn.length;i++)
			{
				synset.put(syn[i], slist);
			}
		}
		
		
	}
	public static void main(String args[])
	{
		Synonyms s=new Synonyms();
		try {
			s.synonymList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

