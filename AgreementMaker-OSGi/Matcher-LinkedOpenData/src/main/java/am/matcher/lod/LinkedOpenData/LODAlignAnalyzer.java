package am.app.mappingEngine.LinkedOpenData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LODAlignAnalyzer {
	public static void main(String[] args) throws IOException {
		LODEvaluator eval = new LODEvaluator();
		String report = eval.testDiff("LOD/batchNoLimit/music-bbc.txt", LODReferences.MUSIC_BBC, null, null, true);
		
		//System.out.println(report);
		
		Scanner s = new Scanner(report);
			
		List<int[]> tuples = new ArrayList<int[]>();
		int[] tuple;
		
		String line;
		while(s.hasNextLine()){
			line = s.nextLine();
			//System.out.println(line);
			
			if(line.contains("Wordnet") && line.contains("Right")){
				System.out.println(line);
				String provenance = line.split("\t")[5];
				tuple = createTuple(provenance, true);
				//printArray(tuple);
				tuples.add(tuple);				
			}
		}
		
		s = new Scanner(report);
		
		while(s.hasNextLine()){
			line = s.nextLine();
			if(line.contains("Wordnet") && line.contains("Wrong\t")){
				System.out.println(line);
				String provenance = line.split("\t")[5];
				tuple = createTuple(provenance, false);
				//printArray(tuple);
				tuples.add(tuple);
			}	
		}
		
		double threshold = 0.3;
		int right = 0;
		for (int i = 0; i < tuples.size(); i++) {
			tuple = tuples.get(i);
			
			//double sim = tuple[0] / Math.sqrt(tuple[2]);
			double sim = tuple[0] / (Math.sqrt(tuple[1]) * Math.log(tuple[2]));
			
			
			printArray(tuple);
			
			System.out.println(sim);
						
			int match = 0;			
			if(sim >= threshold)
				match = 1;
			
			if(match == tuple[3]){
				right++;
				System.out.println("Right!");
			}	
		}
		
		System.out.println("Right:" + right + " size:" + tuples.size());
		
	}
	
	private static void printArray(int[] tuple) {
		System.out.print("[");
		for (int i = 0; i < tuple.length; i++) {
			System.out.print(tuple[i]);
			if(i != tuple.length - 1)
				System.out.print(",");
		}
		System.out.println("]");
	}

	public static int[] createTuple(String provenance, boolean right){
		int[] tuple = new int[4];
		
		String[] split = provenance.split(" ");
		
		String direction = split[2];
		int matches = Integer.parseInt(split[3]);
		int synsets = Integer.parseInt(split[4]);
		int hypernyms = Integer.parseInt(split[5]);
		
		tuple[0] = matches;
		tuple[1] = synsets;
		tuple[2] = hypernyms;
		
		if(right)
			tuple[3] = 1;
		else tuple[3] = 0;
		return tuple; 
	}

}
