package am.app.mappingEngine.LinkedOpenData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sun.org.mozilla.javascript.internal.ast.ForInLoop;

import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.utility.referenceAlignment.AlignmentsComparison;
import am.utility.referenceAlignment.AlignmentUtilities;

public class LODEvaluator {
	//Contains methods for parsing
	ReferenceAlignmentMatcher matcher;
	
	boolean printWrongMappings;
	boolean printRightMappings;
	boolean printMissedMappings;
	
	double threshold = -1;
	
	public LODEvaluator(){
		matcher = new ReferenceAlignmentMatcher();
		//printEverything();
		
	}
	
	void printEverything(){
		printWrongMappings = true;
		printRightMappings = true;
		printMissedMappings = true;
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
			
			String[] split = line.split(" ");
			
			if(split.length!=3){
				System.out.println("File format error");
				return null;
			}
			
			
			if(split[0].startsWith(sourceURI) || split[2].startsWith(targetURI)){
				source = split[0];
				target = split[2];
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
			else if(split[0].startsWith(targetURI) || split[2].startsWith(sourceURI)){
				source = split[2];
				target = split[0];
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
	public void evaluate(String file, String reference, LODOntology sourceOntology, LODOntology targetOntology) throws Exception{
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
	
	public void evaluateAllTestsEq() throws Exception {
			String folder = "LOD/batchEq/ASM/";
			
			evaluate(folder + "foaf-dbpedia.txt", LODReferences.FOAF_DBPEDIA_EQ);
			evaluate(folder + "geonames-dbpedia.txt", LODReferences.GEONAMES_DBPEDIA_EQ);
			evaluate(folder + "music-bbc.txt", LODReferences.MUSIC_BBC_EQ);
			evaluate(folder + "music-dbpedia.txt", LODReferences.MUSIC_DBPEDIA_EQ);
			evaluate(folder + "swc-akt.txt", LODReferences.SWC_AKT_EQ);
			evaluate(folder + "swc-dbpedia.txt", LODReferences.SWC_DBPEDIA_EQ);
			evaluate(folder + "sioc-foaf.txt", LODReferences.SIOC_FOAF_EQ);
			
	}

	public void evaluateAllTestsOld() throws Exception{
		String folder = "LOD/batch/";
		
		evaluate(folder + "foaf-dbpedia.txt", LODReferences.FOAF_DBPEDIA);
		evaluate(folder + "geonames-dbpedia.txt", LODReferences.GEONAMES_DBPEDIA);
		evaluate(folder + "music-bbc.txt", LODReferences.MUSIC_BBC);
		evaluate(folder + "music-dbpedia.txt", LODReferences.MUSIC_DBPEDIA);
		evaluate(folder + "swc-akt.txt", LODReferences.SWC_AKT);
		evaluate(folder + "swc-dbpedia.txt", LODReferences.SWC_DBPEDIA);
		evaluate(folder + "sioc-foaf.txt", LODReferences.SIOC_FOAF);
		
	}
	
	public void evaluateAllTests() throws Exception{
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
		
		boolean printHeader = true;

		String folder = "LOD/batch/";
		
		if(printHeader) System.out.println("FOAF - DBPEDIA");
		evaluate(folder + "foaf-dbpedia.txt", LODReferences.FOAF_DBPEDIA, LODOntology.FOAF, LODOntology.DBPEDIA);
		if(printHeader) System.out.println("GEONAMES - DBPEDIA");
		evaluate(folder + "geonames-dbpedia.txt", LODReferences.GEONAMES_DBPEDIA, LODOntology.GEONAMES, LODOntology.DBPEDIA);
		if(printHeader) System.out.println("MUSIC - BBC");
		evaluate(folder + "music-bbc.txt", LODReferences.MUSIC_BBC, LODOntology.MUSIC_ONTOLOGY, LODOntology.BBC_PROGRAM);
		if(printHeader) System.out.println("MUSIC - DBPEDIA");
		evaluate(folder + "music-dbpedia.txt", LODReferences.MUSIC_DBPEDIA, LODOntology.MUSIC_ONTOLOGY, LODOntology.DBPEDIA);
		if(printHeader) System.out.println("SWC - AKT");
		evaluate(folder + "swc-akt.txt", LODReferences.SWC_AKT, LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL);
		if(printHeader) System.out.println("SWC - DBPEDIA");
		evaluate(folder + "swc-dbpedia.txt", LODReferences.SWC_DBPEDIA, LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA);
		if(printHeader) System.out.println("SIOC - FOAF");
		evaluate(folder + "sioc-foaf.txt", LODReferences.SIOC_FOAF, LODOntology.SIOC, LODOntology.FOAF);
		
	}
	
	public void compare(ArrayList<MatchingPair> toEvaluate, ArrayList<MatchingPair> reference){
		compare(toEvaluate, reference, null, null);
	}
	
	public ReferenceEvaluationData compare(ArrayList<MatchingPair> toEvaluate, ArrayList<MatchingPair> reference, List<String> inSource, List<String> inTarget){
		ReferenceEvaluationData rd = new ReferenceEvaluationData();
		int count = 0;
		MatchingPair p1;
		MatchingPair p2;
		MatchingPair right = null;
		boolean found;
		
		Ontology sOnt = null;
		Ontology tOnt = null;
		
		
		HashSet<MatchingPair> foundTargets = new HashSet<MatchingPair>();
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
						&& p1.relation.equals(p2.relation) && (p1.similarity > threshold)){
					//System.out.println("Right\t" + p1.getTabString().replaceAll("\\|","\t"));
					count++;
					found = true;
					foundTargets.add(p2);
					break;
				}
			}
			if(found == false && printWrongMappings){
				if(right != null){
					//System.out.println("Right:" + right.getTabString());
				}
				System.out.println("Wrong\t" + p1.getTabString().replaceAll("\\|","\t"));
			}
		}	
		if(printMissedMappings){
			for (int i = 0; i < reference.size(); i++) {
				p2 = reference.get(i);
				if(!foundTargets.contains(p2)){
					if(sOnt != null && tOnt!= null){
						String superclasses1 = "Not contained";
						String superclasses2 = "Not contained";
						String comment1 = "Not contained";
						String comment2 = "Not contained";
						Node node = sOnt.containsClassLocalName(p2.sourceURI);
						if(node != null){
							superclasses1 = LODUtils.superclassesString(node);
							comment1 = node.getComment().replaceAll("\n", " ");														
						
						node = tOnt.containsClassLocalName(p2.targetURI);
						if(node != null){
							superclasses2 = LODUtils.superclassesString(node);
							comment2 = node.getComment().replaceAll("\n", " ");
						}
						
						System.out.println("Missed\t" + p2.getTabString() + "\t" + comment1 + "\t" + comment2 + "\t" +
								superclasses1 + "\t" + superclasses2);	
						}
					}
					else{
						System.out.println("Missed\t" + p2.getTabString());
					}
					
				
				
					
				}		
			}
		}
		//System.out.println("right mappings: "+count);
		//System.out.println("prec:"+ (float)count/toEvaluate.size() + " rec: " +  (float)count/reference.size());
		
		double prec;
        if(count == 0.0d) {
        	prec = 0.0d;
        }
        else prec = (double) count / (double) toEvaluate.size();
        
        double rec;
        if(reference.size() == 0.0d) {
        	rec = 0.0d;
        }
        else rec = (double) count / (double) reference.size();
        //System.out.println("Precision: " + prec + ", Recall: " + rec);
        // F-measure
        double fm;
        if(prec + rec == 0.0d) {
        	fm = 0.0d;
        }
        //else  fm = (1 + ALPHA) * (prec * rec) / (ALPHA * prec + rec);
        else fm = 2 * (prec * rec) / (prec + rec);  // from Ontology Matching book
        
        System.out.print((float)count/toEvaluate.size() + "\t" +  (float)count/reference.size() + "\t");
		
        rd.setPrecision(prec);
        rd.setRecall(rec);
        rd.setFmeasure(fm);
		
		return rd;
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
						if(printRightMappings)
							report += "Right\t" + source + "\n";
						found = true;
						break;
					}
					else{
						if(printWrongMappings)
							report += "Wrong, right relation " + target.relation + "\t" + source + "\n";
					}
				}
			}
			if(found == false){
				if(printWrongMappings)
					report += "Wrong\t" + source + "\n";
			}
		}
		
		int count = 0;
		
		String superclasses;
		boolean contained = true;
		for (int i = 0; i < targetList.size(); i++) {
			contained = true;
			if(!foundTargets.contains(targetList.get(i))){
				
				if(printMissedMappings)
					report += "Missed\t" + targetList.get(i);
				
				if(sOnt == null && tOnt == null){
					report += "\n";
					continue;
				}

				Node node = sOnt.containsClassLocalName(targetList.get(i).sourceURI);
				if(node != null){
					superclasses = LODUtils.superclassesString(node);
					report += "\t" + superclasses;
				}
				else {
					report += "\tNot Contained";
					System.out.println(targetList.get(i).sourceURI + " not contained in source");
					contained = false;
				}
				
				node = tOnt.containsClassLocalName(targetList.get(i).targetURI);
				if(node != null){
					superclasses = LODUtils.superclassesString(node);
					report += "\t" + superclasses + "\n";
				}
				else {
					report += "\tNot Contained\n";
					System.out.println(targetList.get(i).targetURI + " not contained in target");
					contained = false;
				}
				
				if(!contained){
					System.out.println("Please remove: " + targetList.get(i));
					count++;
				}
					
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
		
		System.out.println("End");
		
		return report;
	}
	
	
	
	

	public void cleanReference() throws IOException{
		BufferedReader fileBR = new BufferedReader(new FileReader(LODReferences.MUSIC_BBC));
		ArrayList<MatchingPair> refPairs = matcher.parseRefFormat2(fileBR);
		//diff(new ArrayList<MatchingPair>(), refPairs, LODOntology.MUSIC_ONTOLOGY, LODOntology.BBC_PROGRAM);
		
		fileBR = new BufferedReader(new FileReader(LODReferences.SWC_DBPEDIA));
		refPairs = matcher.parseRefFormat2(fileBR);
		//diff(new ArrayList<MatchingPair>(), refPairs, LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA);
		
		fileBR = new BufferedReader(new FileReader(LODReferences.SWC_AKT));
		refPairs = matcher.parseRefFormat2(fileBR);
		//diff(new ArrayList<MatchingPair>(), refPairs, LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL);
		
		fileBR = new BufferedReader(new FileReader(LODReferences.SIOC_FOAF));
		refPairs = matcher.parseRefFormat2(fileBR);
		//diff(new ArrayList<MatchingPair>(), refPairs, LODOntology.SIOC, LODOntology.FOAF);
		
		fileBR = new BufferedReader(new FileReader(LODReferences.MUSIC_DBPEDIA));
		refPairs = matcher.parseRefFormat2(fileBR);
		//diff(new ArrayList<MatchingPair>(), refPairs, LODOntology.MUSIC_ONTOLOGY, LODOntology.DBPEDIA);
		
		fileBR = new BufferedReader(new FileReader(LODReferences.GEONAMES_DBPEDIA));
		refPairs = matcher.parseRefFormat2(fileBR);
		//diff(new ArrayList<MatchingPair>(), refPairs, LODOntology.GEONAMES, LODOntology.DBPEDIA);
		
		fileBR = new BufferedReader(new FileReader(LODReferences.FOAF_DBPEDIA));
		refPairs = matcher.parseRefFormat2(fileBR);
		diff(new ArrayList<MatchingPair>(), refPairs, LODOntology.FOAF, LODOntology.DBPEDIA);	
	}
	
	public String testDiff(String file1, String file2, LODOntology source, LODOntology target, boolean reference) throws IOException {
		BufferedReader fileBR = new BufferedReader(new FileReader(file1));
		ArrayList<MatchingPair> file1Pairs = matcher.parseRefFormat4(fileBR);
		fileBR = new BufferedReader(new FileReader(file2));
		ArrayList<MatchingPair> file2Pairs;
		if(reference)
			file2Pairs = matcher.parseRefFormat2(fileBR);
		else file2Pairs = matcher.parseRefFormat4(fileBR);
		
		return diff(file1Pairs, file2Pairs, source, target);
	}
	
	public void testRefUtilsDiff(String file1, String file2, boolean reference) throws IOException{
		BufferedReader fileBR = new BufferedReader(new FileReader(file1));
		ArrayList<MatchingPair> sourcePairs = matcher.parseRefFormat4(fileBR);
		fileBR = new BufferedReader(new FileReader(file2));
		ArrayList<MatchingPair> targetPairs;
		if(reference)
			targetPairs = matcher.parseRefFormat2(fileBR);
		else targetPairs = matcher.parseRefFormat4(fileBR);
		
		AlignmentsComparison comparison = AlignmentUtilities.diff(sourcePairs, targetPairs);
		
		System.out.println(comparison);
						
	}
	
	public boolean equals(MatchingPair mp1, MatchingPair mp2){
		if(mp1.getTabString().equals(mp2.getTabString()))
			return true;
		return false;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public static void main(String[] args) throws Exception {
//		fromSubClassof(new File("LOD/BLOOMS/Music-DBpedia/SubClass.txt"), 
//				LODOntologies.DBPEDIA_URI, LODOntologies.MUSIC_ONTOLOGY_URI);
		LODEvaluator eval = new LODEvaluator();
		
//		for (int i = 0; i < 10; i++) {
//			double th = 0.1 * (i+1);
//			System.out.println(th);
//			eval.setThreshold(th);
//			eval.evaluateAllTestsOld();
//			System.out.println("\n");
//		}
		
		eval.setThreshold(0.66);
		eval.evaluateAllTestsOld();
		
		Logger.getRootLogger().setLevel(Level.ERROR);
		
		String report = "";
		
		//eval.evaluateAllTests();
		
		
		//eval.evaluateAllTestsEq();
		//eval.testRefUtilsDiff("LOD/batch/music-bbc.txt", LODReferences.MUSIC_BBC, true);

		
//		
//		Ontology sOnt = null;
//		Ontology tOnt = null;
//		
//		LODOntology sourceOntology = LODOntology.MUSIC_ONTOLOGY;
//		LODOntology targetOntology = LODOntology.BBC_PROGRAM;
//		
//		
//		if(sourceOntology != null && targetOntology != null){
//			sOnt = OntoTreeBuilder.loadOntology(new File(sourceOntology.getFilename()).getAbsolutePath(), sourceOntology.getLang(), sourceOntology.getSyntax());			
//			tOnt = OntoTreeBuilder.loadOntology(new File(targetOntology.getFilename()).getAbsolutePath(), targetOntology.getLang(), targetOntology.getSyntax());			
//			
//		
//			for (int i = 0; i < sOnt.getClassesList().size(); i++) {
//				System.out.println(sOnt.getClassesList().get(i).getUri());
//			}
//			
//		}
//		
		
		
		//eval.cleanReference();
		
		//eval.testDiff("LOD/batchNoLimit/music-bbc.txt", "LOD/batch/music-bbc.txt", null, null, false);
//		report += eval.testDiff("LOD/batch/music-bbc.txt", LODReferences.MUSIC_BBC, null, null, true);
//		System.out.println(report);
		
		
		//report = eval.testDiff("LOD/batch/sioc-foaf.txt", "LOD/batchNoLimit/sioc-foaf.txt", null, null, false);
		//report = eval.testDiff("LOD/batch/sioc-foaf.txt", LODReferences.SIOC_FOAF_FIXED, null, null, true);		
		
		//System.out.println(report);
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
		
		//eval.testDiff("LOD/batch/music-bbc.txt", LODReferences.MUSIC_BBC, LODOntology.MUSIC_ONTOLOGY, LODOntology.BBC_PROGRAM, true);
		
		//eval.testDiff("LOD/batch/music-bbc.txt", "LOD/batchOld/music-bbc.txt", false);
		
		
		//System.out.println("SWC_DBPEDIA");
		//eval.evaluate("LOD/batch/swc-dbpedia.txt", LODReferences.SWC_DBPEDIA);
		
		//System.out.println("SWC_AKT");
		//eval.evaluate("LOD/batch/swc-akt.txt", LODReferences.SWC_AKT);
	}
}

