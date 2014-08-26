package am.matcher.lod.instanceMatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class NYTArticlesViewer {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		ObjectInput in = new ObjectInputStream(new FileInputStream(new File("imdata/jsonAnswers.ser")));
	    Object input = in.readObject();
		HashMap<String, String> jsonAnswers = (HashMap<String, String>) input;
		
		System.out.println(jsonAnswers.size());
		
		for(String key: jsonAnswers.keySet()){
			System.out.println("key:" + key);
			System.out.println(jsonAnswers.get(key));
		}
		
//		for(String key: jsonAnswers.keySet()){
//			if(key.contains("Rome")){
//				System.out.println("key:" + key);
//				System.out.println(jsonAnswers.get(key));
//			}	
//		}
		
		//System.out.println(jsonAnswers.get("http://data.nytimes.com/N15749223239255170333"));
		
	}

}
