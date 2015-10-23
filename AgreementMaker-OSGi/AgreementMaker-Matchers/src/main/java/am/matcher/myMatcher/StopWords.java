package am.matcher.myMatcher;

import java.util.ArrayList;

public class StopWords {
	//check for stopwords
	String[] stopwords={"has","an","a","by","is"} ;
	Boolean isStopWord(String w)
	{
		for (String str:stopwords)
		{
			if (str.equalsIgnoreCase(w))
				return true;
			
		}
		return false;
	}
	
	int countStopWords(ArrayList<String> words)
	{
		int count=0;
		for (String s:words)
		{
			if (this.isStopWord(s))
				count++;
		}
		return count;
	}
}
