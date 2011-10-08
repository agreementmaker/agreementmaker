package am.app.mappingEngine.LinkedOpenData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

public class LODEvaluator {
	//Contains methods for parsing
	ReferenceAlignmentMatcher matcher;
	
	boolean printWrongMappings = false;
	
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
		
		
		//System.out.println("FP:" + filePairs.size());
		//System.out.println("RP:" + refPairs.size());
		
		//compare(filePairs, refPairs);
		
		removeDuplicates(filePairs);
		removeDuplicates(refPairs);
		
		//System.out.println("FP:" + filePairs.size());
		//System.out.println("RP:" + refPairs.size());
		
		compare(filePairs, refPairs);	
		
		System.out.println();
	}
	
	public void evaluate(ArrayList<MatchingPair> filePairs, String reference) throws Exception{
		BufferedReader refBR = new BufferedReader(new FileReader(reference));
		ArrayList<MatchingPair> refPairs = matcher.parseRefFormat2(refBR);
		
		
		//System.out.println("FP:" + filePairs.size());
		//System.out.println("RP:" + refPairs.size());
		
		compare(filePairs, refPairs);
		
		removeDuplicates(filePairs);
		removeDuplicates(refPairs);
		
		//System.out.println("FP:" + filePairs.size());
		//System.out.println("RP:" + refPairs.size());
		
		compare(filePairs, refPairs);	
		
		System.out.println();
	}
	
	public void compare(ArrayList<MatchingPair> toEvaluate, ArrayList<MatchingPair> reference){
		int count = 0;
		MatchingPair p1;
		MatchingPair p2;
		MatchingPair right = null;
		boolean found;
		for (int i = 0; i < toEvaluate.size(); i++) {
			found = false;
			p1 = toEvaluate.get(i);
			//System.out.println("Presented: "+ p1.sourceURI + " " + p1.targetURI + " " + p1.similarity);
			for (int j = 0; j < reference.size(); j++) {
				p2 = reference.get(j);
				//System.out.println(p2.getTabString());
				if(p1.sourceURI.equals(p2.sourceURI)){
					right = p2;
				}
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					count++;
					found = true;
					break;
				}
			}
			if(found == false && printWrongMappings){
				if(right != null){
					//System.out.println("Right:" + right.getTabString());
				}
				System.out.println("Wrong: " + p1.getTabString());
			}
		}	
		
		//System.out.println("right mappings: "+count);
		//System.out.println("prec:"+ (float)count/toEvaluate.size() + " rec: " +  (float)count/reference.size());
		System.out.print((float)count/toEvaluate.size() + "\t" +  (float)count/reference.size() + "\t");
	}
	
	public void removeDuplicates(List<MatchingPair> pairs){
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
	
	public void evaluateAllTests() throws Exception{
		System.out.println("OLD RESULTS\n");
//		
//		System.out.println("FOAF_DBPEDIA");
//		evaluate("LOD/AMOldAlignments/FOAFANDDBPEDIA.txt", LODReferences.FOAF_DBPEDIA);
//		System.out.println("MUSIC_BBC");
//		evaluate("LOD/AMOldAlignments/BBCProgramsandMusicOntology.txt", LODReferences.MUSIC_BBC);
//		System.out.println("GEONAMES_DBPEDIA");
//		evaluate("LOD/AMOldAlignments/GeoNames and DBPedia.txt", LODReferences.GEONAMES_DBPEDIA);
//		System.out.println("MUSIC_DBPEDIA");
//		evaluate("LOD/AMOldAlignments/MUSICANDDBPedia.txt", LODReferences.MUSIC_DBPEDIA);
//		System.out.println("SWC_DBPEDIA");
//		evaluate("LOD/AMOldAlignments/SematicWebConferenceANDDBpedia.txt", LODReferences.SWC_DBPEDIA);
//		System.out.println("SIOC_FOAF");
//		evaluate("LOD/AMOldAlignments/SIOCandFOAF.txt", LODReferences.SIOC_FOAF);
//		System.out.println("SWC_AKT");
//		evaluate("LOD/AMOldAlignments/SWCANDAKT.txt", LODReferences.SWC_AKT);
//		System.out.println("SIOC_FOAF");
//		evaluate("LOD/results/sioc-foaf.txt", LODReferences.SIOC_FOAF);
//		
		//ArrayList<MatchingPair> pairs = parseBLOOMSReference("LOD/BLOOMS/AKT-DBPEDIA/SubClass.txt", "http://www.aktors.org/ontology/portal#", "http://dbpedia.org/ontology/");
//		ArrayList<MatchingPair> pairs = parseBLOOMSReference("LOD/BLOOMS/AKT-SWC/SubClass.txt", "http://swrc.ontoware.org/ontology#", "http://www.aktors.org/ontology/");
//		evaluate(pairs, LODReferences.SWC_AKT);
		//ArrayList<MatchingPair> pairs = parseBLOOMSReference("LOD/BLOOMS/AKT-SWC/SubClass.txt", "http://swrc.ontoware.org/ontology#", "http://www.aktors.org/ontology/");
		//evaluate(pairs, LODReferences.SWC_AKT);
		
		System.out.println("\nNEW RESULTS\n");

		String folder = "LOD/batch/";
		
		evaluate(folder + "foaf-dbpedia.txt", LODReferences.FOAF_DBPEDIA);
		evaluate(folder + "geonames-dbpedia.txt", LODReferences.GEONAMES_DBPEDIA);
		evaluate(folder + "music-bbc.txt", LODReferences.MUSIC_BBC);
		evaluate(folder + "music-dbpedia.txt", LODReferences.MUSIC_DBPEDIA);
		evaluate(folder + "swc-akt.txt", LODReferences.SWC_AKT);
		evaluate(folder + "swc-dbpedia.txt", LODReferences.SWC_DBPEDIA);
		evaluate(folder + "sioc-foaf.txt", LODReferences.SIOC_FOAF);
		
	}
	
	public String diff(List<MatchingPair> sourceList, List<MatchingPair> targetList, LODOntology sourceOntology, LODOntology targetOntology){
		String report = "";
		MatchingPair source;
		MatchingPair target;
		
		Ontology sOnt = null;
		Ontology tOnt = null;
		
		if(sourceOntology != null && targetOntology != null){
			sOnt = OntoTreeBuilder.loadOntology(new File(sourceOntology.getFilename()).getAbsolutePath(), sourceOntology.getLang(), sourceOntology.getSyntax());			
			tOnt = OntoTreeBuilder.loadOntology(new File(targetOntology.getFilename()).getAbsolutePath(), targetOntology.getLang(), targetOntology.getSyntax());			
		}
				
		removeDuplicates(sourceList);
		removeDuplicates(targetList);
		
		boolean found = false;
		
		HashSet<MatchingPair> foundTargets = new HashSet<MatchingPair>();
		
		for (int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			found = false;
			for (int j = 0; j < targetList.size(); j++) {
				target = targetList.get(j);
				
				if(source.sameSource(target) && source.sameTarget(target)){
					foundTargets.add(target);
					if(source.relation.equals(target.relation)){
						found = true;
						break;
					}
					else{
						report += "Wrong, right relation " + target.relation + "\t" + source + "\n";
					}
				}
			}
			if(found == false){
				report += "Wrong\t" + source + "\n";
			}
		}
		
		for (int i = 0; i < targetList.size(); i++) {
			if(!foundTargets.contains(targetList.get(i))){
				report += "Missed\t" + targetList.get(i);
				
				if(sOnt == null && tOnt == null) report += "\n";

				if(sOnt.containsClassLocalName(targetList.get(i).sourceURI)){
					report += "\tContained";
				}
				else report += "\tNot Contained";
				
				if(tOnt.containsClassLocalName(targetList.get(i).targetURI)){
					report += "\tContained\n";
				}
				else report += "\tNot Contained\n";
			}
		}
		report = report.replaceAll("\\|", "\t");		
		
		try {
			FileOutputStream fos = new FileOutputStream("C:/Users/federico/Desktop/results.tsv");
			fos.write(report.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return report;
	}
	
	
	
	public static void main(String[] args) throws Exception {
//		fromSubClassof(new File("LOD/BLOOMS/Music-DBpedia/SubClass.txt"), 
//				LODOntologies.DBPEDIA_URI, LODOntologies.MUSIC_ONTOLOGY_URI);
		LODEvaluator eval = new LODEvaluator();
		
		eval.evaluateAllTests();
		//eval.evaluate("LOD/lastResults/music-dbpedia.txt", LODReferences.MUSIC_DBPEDIA);
		//eval.evaluate("LOD/lastResults/foaf-dbpedia.txt", LODReferences.FOAF_DBPEDIA);
		//eval.evaluate("LOD/lastResults/geonames-dbpedia.txt", LODReferences.GEONAMES_DBPEDIA);
		//eval.evaluate("LOD/lastResults/sioc-foaf.txt", LODReferences.SIOC_FOAF);
		
		//eval.evaluate("LOD/batch/music-dbpedia.txt", LODReferences.MUSIC_DBPEDIA);
		//eval.testDiff("LOD/batch/music-dbpedia.txt", "LOD/lastResults/music-dbpedia.txt", false);
		//eval.testDiff("LOD/batch/music-dbpedia.txt", LODReferences.MUSIC_DBPEDIA, true);
		
		
		//eval.evaluate("LOD/batch/foaf-dbpedia.txt", LODReferences.FOAF_DBPEDIA);
		//eval.evaluate("LOD/lastResults/geonames-dbpedia.txt", LODReferences.GEONAMES_DBPEDIA);
		//eval.evaluate("LOD/batch/sioc-foaf.txt", LODReferences.SIOC_FOAF_FIXED);
		//eval.testDiff("LOD/batch/sioc-foaf.txt", "LOD/lastResults/sioc-foaf.txt", false);
		//eval.testDiff("LOD/batch/sioc-foaf.txt", LODReferences.SIOC_FOAF, true);
		//System.out.println("MUSIC_BBC");
		//eval.testDiff("LOD/batch/music-bbc.txt", LODReferences.MUSIC_BBC, true);
		//eval.testDiff("LOD/batch/music-bbc.txt", "LOD/lastResults/music-bbc.txt", false);
		//eval.evaluate("LOD/batch/music-bbc.txt", LODReferences.MUSIC_BBC);
		
		//eval.testDiff("LOD/batch/music-bbc.txt", LODReferences.MUSIC_BBC, true);
		//eval.testDiff("LOD/batch/swc-dbpedia.txt", "LOD/batchOld/swc-dbpedia.txt", false);
		
		eval.testDiff("LOD/batch/music-bbc.txt", LODReferences.MUSIC_BBC, LODOntology.MUSIC_ONTOLOGY, LODOntology.BBC_PROGRAM, true);
		
		//eval.testDiff("LOD/batch/music-bbc.txt", "LOD/batchOld/music-bbc.txt", false);
		
		
		//System.out.println("SWC_DBPEDIA");
		//eval.evaluate("LOD/batch/swc-dbpedia.txt", LODReferences.SWC_DBPEDIA);
		
		//System.out.println("SWC_AKT");
		//eval.evaluate("LOD/batch/swc-akt.txt", LODReferences.SWC_AKT);
		
	}


	private void testDiff(String file1, String file2, LODOntology source, LODOntology target, boolean reference) throws IOException {
		BufferedReader fileBR = new BufferedReader(new FileReader(file1));
		ArrayList<MatchingPair> file1Pairs = matcher.parseRefFormat4(fileBR);
		fileBR = new BufferedReader(new FileReader(file2));
		ArrayList<MatchingPair> file2Pairs;
		if(reference)
			file2Pairs = matcher.parseRefFormat2(fileBR);
		else file2Pairs = matcher.parseRefFormat4(fileBR);
		
		System.out.println(diff(file1Pairs, file2Pairs, source, target));
	}
}

