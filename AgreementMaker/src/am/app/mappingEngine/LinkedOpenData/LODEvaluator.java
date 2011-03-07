package am.app.mappingEngine.LinkedOpenData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;

public class LODEvaluator {
	//Contains methods for parsing
	ReferenceAlignmentMatcher matcher;
	
	public LODEvaluator(){
		matcher = new ReferenceAlignmentMatcher();
	}
	
	
	/**
	 * This method reads the BLOOMS subclassof alignment file and returns a list of MatchingPair
	 * 
	 * @param file
	 * @return
	 */
	public static ArrayList<MatchingPair> parseBLOOMSReference(String file, String sourceURI, String targetURI){
		Scanner scanner = null;
		try { 
			scanner = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		ArrayList<MatchingPair> ret = new ArrayList<MatchingPair>();
		
		String source;
		String target;
		String line;
		MatchingPair mp;
		while(scanner.hasNext()){
			line = scanner.nextLine();
			//System.out.println(line);
			
			String[] splitted = line.split(" ");
			
			if(splitted.length!=3){
				System.out.println("File format error");
				return null;
			}
			
			
			if(splitted[0].startsWith(sourceURI) || splitted[2].startsWith(targetURI)){
				source = splitted[0];
				target = splitted[2];
				if(source.contains("#"))
					source = afterSharp(source);
				else source = source.substring(sourceURI.length());
				if(target.contains("#"))
					target = afterSharp(target);
				else target = target.substring(targetURI.length());
				
				mp = new MatchingPair(source,target, 1.0, MappingRelation.SUBCLASS);
				ret.add(mp);
				//System.out.println(mp.getTabString());
			}
			else if(splitted[0].startsWith(targetURI) || splitted[2].startsWith(sourceURI)){
				source = splitted[2];
				target = splitted[0];
				if(source.contains("#"))
					source = afterSharp(source);
				else source = source.substring(sourceURI.length());
				if(target.contains("#"))
					target = afterSharp(target);
				else target = target.substring(targetURI.length());
				
				mp = new MatchingPair(source,target, 1.0, MappingRelation.SUPERCLASS);
				ret.add(mp);
				//System.out.println(mp.getTabString());
			}
			else System.out.println("WEIRD");
				
		}
		
		return ret;
	}
	
	private static String afterSharp(String string) {
		String[] splitted = string.split("#");
		return splitted[splitted.length-1];
	}


	/**
	 *  
	 * @param file has to be in AM exported format
	 * @param reference has to be source(tab)relationship(tab)target
	 * @throws Exception 
	 */
	public void evaluate(String file, String reference) throws Exception{
		BufferedReader fileBR = new BufferedReader(new FileReader(file));
		ArrayList<MatchingPair> filePairs = matcher.parseRefFormat4(fileBR);
		BufferedReader refBR = new BufferedReader(new FileReader(reference));
		ArrayList<MatchingPair> refPairs = matcher.parseRefFormat2(refBR);
		
		
		System.out.println("FP:" + filePairs.size());
		System.out.println("RP:" + refPairs.size());
		
		compare(filePairs, refPairs);
		
		removeDuplicates(filePairs);
		removeDuplicates(refPairs);
		
		System.out.println("FP:" + filePairs.size());
		System.out.println("RP:" + refPairs.size());
		
		compare(filePairs, refPairs);		
	}
	
	public void evaluate(ArrayList<MatchingPair> filePairs, String reference) throws Exception{
		BufferedReader refBR = new BufferedReader(new FileReader(reference));
		ArrayList<MatchingPair> refPairs = matcher.parseRefFormat2(refBR);
		
		
		System.out.println("FP:" + filePairs.size());
		System.out.println("RP:" + refPairs.size());
		
		compare(filePairs, refPairs);
		
		removeDuplicates(filePairs);
		removeDuplicates(refPairs);
		
		System.out.println("FP:" + filePairs.size());
		System.out.println("RP:" + refPairs.size());
		
		compare(filePairs, refPairs);		
	}
	
	public void compare(ArrayList<MatchingPair> toEvaluate, ArrayList<MatchingPair> reference){
		int count = 0;
		MatchingPair p1;
		MatchingPair p2;
		
		for (int i = 0; i < toEvaluate.size(); i++) {
			p1 = toEvaluate.get(i);
			for (int j = 0; j < reference.size(); j++) {
				p2 = reference.get(j);
				//System.out.println(p2.getTabString());
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					count++;
					break;
				}
			}
		}	
		System.out.println("right mappings: "+count);
		System.out.println("prec:"+ (float)count/toEvaluate.size() + " rec: " +  (float)count/reference.size());
	}
	
	public void removeDuplicates(ArrayList<MatchingPair> pairs){
		MatchingPair p1;
		MatchingPair p2;
		for (int i = 0; i < pairs.size(); i++) {
			for (int j = i+1; j < pairs.size(); j++) {
				p1 = pairs.get(i);
				p2 = pairs.get(j);
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					pairs.remove(j);
					//System.out.println(p2.getTabString());
					j--;
				}
					
			}
		}
	}
	
	public boolean equals(MatchingPair mp1, MatchingPair mp2){
		if(mp1.getTabString().equals(mp2.getTabString()))
			return true;
		return false;
	}
	
	public static void main(String[] args) throws Exception {
//		fromSubClassof(new File("LOD/BLOOMS/Music-DBpedia/SubClass.txt"), 
//				LODOntologies.DBPEDIA_URI, LODOntologies.MUSIC_ONTOLOGY_URI);
		LODEvaluator eval = new LODEvaluator();
		System.out.println("FOAF_DBPEDIA");
		eval.evaluate("LOD/AMOldAlignments/FOAFANDDBPEDIA.txt", LODReferences.FOAF_DBPEDIA);
		System.out.println("MUSIC_BBC");
		eval.evaluate("LOD/AMOldAlignments/BBCProgramsandMusicOntology.txt", LODReferences.MUSIC_BBC);
		System.out.println("GEONAMES_DBPEDIA");
		eval.evaluate("LOD/AMOldAlignments/GeoNames and DBPedia.txt", LODReferences.GEONAMES_DBPEDIA);
		System.out.println("MUSIC_DBPEDIA");
		eval.evaluate("LOD/AMOldAlignments/MUSICANDDBPedia.txt", LODReferences.MUSIC_DBPEDIA);
		System.out.println("SWC_DBPEDIA");
		eval.evaluate("LOD/AMOldAlignments/SematicWebConferenceANDDBpedia.txt", LODReferences.SWC_DBPEDIA);
		System.out.println("SIOC_FOAF");
		eval.evaluate("LOD/AMOldAlignments/SIOCandFOAF.txt", LODReferences.SIOC_FOAF);
		System.out.println("SWC_AKT");
		eval.evaluate("LOD/AMOldAlignments/SWCANDAKT.txt", LODReferences.SWC_AKT);
		System.out.println("SIOC_FOAF");
		eval.evaluate("LOD/results/sioc-foaf.txt", LODReferences.SIOC_FOAF);
		
		//ArrayList<MatchingPair> pairs = parseBLOOMSReference("LOD/BLOOMS/AKT-DBPEDIA/SubClass.txt", "http://www.aktors.org/ontology/portal#", "http://dbpedia.org/ontology/");
		ArrayList<MatchingPair> pairs = parseBLOOMSReference("LOD/BLOOMS/AKT-SWC/SubClass.txt", "http://swrc.ontoware.org/ontology#", "http://www.aktors.org/ontology/");
		eval.evaluate(pairs, LODReferences.SWC_AKT);
		//ArrayList<MatchingPair> pairs = parseBLOOMSReference("LOD/BLOOMS/AKT-SWC/SubClass.txt", "http://swrc.ontoware.org/ontology#", "http://www.aktors.org/ontology/");
		//eval.evaluate(pairs, LODReferences.SWC_AKT);
		
		System.out.println("SWC_AKT");
		eval.evaluate("LOD/results/swc-akt.txt", LODReferences.SWC_AKT);
		
		
	}
}

