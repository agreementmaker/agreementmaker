package am.app.mappingEngine.IterativeInstanceStructuralMatcher;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	
	public static void printMatrix(double[][] sims) {
		if(sims.length==0) return;
		for (int i = 0; i < sims.length; i++) {
			for (int j = 0; j < sims[0].length; j++) {
				System.out.printf("%.2f ",sims[i][j]);
			}
			System.out.println();
		}
	}
	
	public static double optimalAlignment(double[][] sims) {
		List<Integer> alignX = new ArrayList<Integer>();
		List<Integer> alignY = new ArrayList<Integer>();
		
		double sum = 0;
		
		for (int i = 0; i < sims.length; i++) {
			//search maximum in the allowed part
			double max = 0;
			int maxX = -1;
			int maxY = -1;
			for (int j = 0; j < sims.length; j++) {
				for (int k = 0; k < sims.length; k++) {
					if(!alignX.contains(j)&&!alignY.contains(k)){
						if(sims[j][k]>=max){
							max = sims[j][k];
							maxX = j;
							maxY = k;
						}
					}
						
				}
			}
			//System.out.println("max"+i+": "+max);
			alignX.add(maxX);
			alignY.add(maxY);
			sum += max;
		}
		//System.out.println("Opt sol: "+ sum/sims.length);		
		return sum/sims.length;
	}
	
	public static List<AlignIndexes> optimalAlignments(double[][] sims) {
		List<AlignIndexes> alignments = new ArrayList<AlignIndexes>();
		List<Integer> alignX = new ArrayList<Integer>();
		List<Integer> alignY = new ArrayList<Integer>();
		
		double sum = 0;
		
		for (int i = 0; i < sims.length; i++) {
			//search maximum in the allowed part
			double max = 0;
			int maxX = -1;
			int maxY = -1;
			for (int j = 0; j < sims.length; j++) {
				for (int k = 0; k < sims.length; k++) {
					if(!alignX.contains(j)&&!alignY.contains(k)){
						if(sims[j][k]>=max){
							max = sims[j][k];
							maxX = j;
							maxY = k;
						}
					}		
				}
			}
			//System.out.println("max"+i+": "+max);
			alignX.add(maxX);
			alignY.add(maxY);
			sum += max;
		}
		for (int i = 0; i < alignX.size(); i++) {
			alignments.add(new AlignIndexes(alignX.get(i),alignY.get(i)));
		}
		
		//System.out.println("Opt sol: "+ sum/sims.length);		
		return alignments;
	}
	
	public static boolean primitiveType(String uri) {
		if(uri.equals("http://www.w3.org/2001/XMLSchema#string")||
				uri.equals("http://www.w3.org/2001/XMLSchema#integer")||
			uri.equals("http://www.w3.org/2001/XMLSchema#nonNegativeInteger"))
		return true;
		
		return false;
	}

	public static int getOnlyMax(ArrayList<Double> list){
		double max = -1;
		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i)>max){
				max = list.get(i);
				index = i;
			}
		}
		for (int i = 0; i < list.size(); i++) {
			if(i!=index && list.get(i).equals(max))
				return -1;
		}
		return index;
	}
	
	public static String removeSomeChars(String s){
		if(s==null) return null;
		char[] toRemove = {'.','_','-'};
		for (int i = 0; i < toRemove.length; i++) {
			int index = s.indexOf(toRemove[i]);
			while(index!=-1){
				String s1 = s.substring(0,index);
				if(index<s.length()-1)
					s = s1 + s.substring(index+1);
				else s = s1;
				index = s.indexOf(toRemove[i]);
				}
		}
		return s;
	}

	public static boolean allEquals(double[][] sims) {
		double prev = 0;
		for (int i = 0; i < sims.length; i++) {
			for (int j = 0; j < sims[0].length; j++) {
				if(!(i==0 && j==0))
					if(sims[i][j]!=prev)
						return false;
				prev = sims[i][j]; 
			}
		}
		return true;
	}
	
	public static void addOrdered(List<Double> doubles, double value){
		if(doubles.size()==0){
			doubles.add(value);
			return;
		}
		for (int i = 0; i < doubles.size(); i++) {
			if(value >= doubles.get(i)){
				doubles.add(i,value);
				return;
			}
		}		
		doubles.add(value);
	}
}
