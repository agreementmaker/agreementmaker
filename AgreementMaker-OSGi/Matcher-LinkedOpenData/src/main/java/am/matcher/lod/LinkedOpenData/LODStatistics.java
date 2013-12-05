package am.matcher.lod.LinkedOpenData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import am.matcher.lod.hierarchy.Utilities;
import am.utility.WordNetUtils;
import edu.smu.tspell.wordnet.Synset;

public class LODStatistics {
	
	public static void computeClassStatistics(List<String> classNames){
		WordNetUtils utils = new WordNetUtils();		
		
		int covered = 0;
		for (String string : classNames) {
			
			string = Utilities.separateWords(string);
						
			Synset[] synsets = utils.getWordNet().getSynsets(string);
			
			if(synsets.length > 0)
				covered ++;
			
			
			//Stringu
			//utils.getWordNet().getSynsets(wordForm);		
			
		}
		
		System.out.println("total:" + classNames.size() + " covered:" + covered +
				" " + (double)covered/(double)classNames.size());
		
	}
	
	/**
	 * Requires a file containing a list of concept names, separated by \n
	 *
	 */
	public static List<String> parseClassFile(String filename){
		File file = new File(filename);
		Scanner scanner;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		ArrayList<String> names = new ArrayList<String>();
		
		String line;
		while(scanner.hasNextLine()){
			line = scanner.nextLine();
			System.out.println(line);
			names.add(line);
		}
		return names;
	}
	
	
	
	public static void main(String[] args) {
		List<String> classNames = LODStatistics.parseClassFile("C:/Users/federico/workspaceAM/conceptsUnique.txt");
		computeClassStatistics(classNames);
	}

}
