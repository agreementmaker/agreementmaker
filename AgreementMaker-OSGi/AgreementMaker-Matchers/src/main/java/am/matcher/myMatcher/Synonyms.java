package am.matcher.myMatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Synonyms 
{
	HashMap<String,Integer> synset=new HashMap<String,Integer>();
	public void synonymList() 
	{

		String line="";
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("synonyms.csv"));
			int count=1;
			while ((line = br.readLine()) != null) {

			        // use comma as separator
				String[] syn = line.split(",");
				for(int i=0;i<syn.length;i++)
				{
					synset.put(syn[i].toLowerCase(),count);
				}
				count++;
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	public Boolean isSynonym(String word1,String word2)
	{
		try
		{
		int num1=this.synset.get(word1);
		int num2=this.synset.get(word2);
		if (num1==num2)
			return true;
		}
		catch(NullPointerException e)
		{
			
		}
		return false;
	}
	
	
}

