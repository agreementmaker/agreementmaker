package am.app.mappingEngine.LexicalMatcherUMLS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

import gov.nih.nlm.kss.models.meta.concept.*;
import gov.nih.nlm.kss.api.KSSRetriever;
import gov.nih.nlm.kss.api.KSSRetrieverV5_0;
import gov.nih.nlm.kss.util.DatabaseException;
import gov.nih.nlm.kss.util.XMLException;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;


public class LexicalMatcherUMLS extends AbstractMatcher{

	private static final long serialVersionUID = -592216894091578508L;
	
	private KSSRetrieverV5_0 retriever = null;
    private static String hostName = "//umlsks.nlm.nih.gov/KSSRetriever";
    private static String LAT = "";
    private static String DBYEAR = "2009AA";
    private static int classListSize = 0;
    private static boolean firstExec = false;
    private static ArrayList<String> targetNodeName;
    private static ArrayList synsLists [];
    private static ArrayList<String> cuis ;
    private int curr = 0;
    
    //For source data
    private static int classListSizeS = 0;
    private static ArrayList synsListsS [];
    private static ArrayList<String> sourceNodeName;
    private static ArrayList<String> cuisS ;
    private int currS = 0;
    
	private static String [][] conceptPartsS;	//0:name, 1:cui, 2:synonyms
	private static String [][] synonymsS;		//synonyms
	private static String [] conceptInfoS;	//(name|cui|synonyms) array
	private static String [][] conceptPartsT;
	private static String [][] synonymsT;
	private static String [] conceptInfoT;
	
	
	//Constructor
	public LexicalMatcherUMLS() {
		super();
		needsParam = false;		
		//alignProp = false;
	}
	
	//Connect to UMLS database 
	//You have to add your IP to the account profile from:
	//http://kscas-lhc.nlm.nih.gov/UMLSKS/servlet/Turbine/template/admin,user,KSS_login.vm
	//https://login.nlm.nih.gov/cas/login?service=http://umlsks.nlm.nih.gov/uPortal/Login  -- updated July 17th 2010, Cosmin.
	//Use ulaskeles as login name and password if you don't have a license.
	public void connectToServer()  throws Exception{
		try {
	    	retriever = (KSSRetrieverV5_0)Naming.lookup(hostName);
		
		} catch (RemoteException rex) {
			System.err.println("RemoteException: " + rex.getMessage());
		} catch (NotBoundException nbex) {
			System.err.println("NotBoundException: " + nbex.getMessage());
		} catch (MalformedURLException mfurl) {
			System.err.println("MalformedURLException: " + mfurl.getMessage());
		}
		
	}
	
	//
	public void getSourceDataFromServer()  throws Exception{
		classListSizeS = sourceOntology.getClassesList().size();
		System.out.println("Number of classes in Source Ontology: " + classListSizeS);
		
		//Get synonyms. Took ?? mins for MA ontology(2700 concepts).
		System.out.println("Retrieving Source List synonyms for each concept...");
		long startime = System.nanoTime()/1000000000;
		ArrayList<Node> sourceClassList = sourceOntology.getClassesList();
		synsListsS = new ArrayList[classListSizeS]; 
		
		sourceNodeName = new ArrayList<String>();
		cuisS = new ArrayList<String>();		

		for(int j = 0; j < sourceClassList.size(); j++) {
			String sourceName = sourceClassList.get(j).getLabel();
			if(sourceName.equals(""))
				continue;
			sourceName = treatString(sourceName);
			sourceNodeName.add(sourceName);
			String cui = null;
			cui = getCUIfromName(sourceName);
			if(cui != null){
				cuisS.add(cui);
				synsListsS[currS] = new ArrayList<ArrayList<String>>();
				synsListsS[currS].add(getSynonyms(cui));
				currS++;
			}
			else{
				cuisS.add("fakecui");
				ArrayList<String> a = new ArrayList<String>();
				a.add("fakelist");
				synsListsS[currS] = new ArrayList<ArrayList<String>>();
				synsListsS[currS].add(a);
				currS++;
			}
		}
		long endtime = System.nanoTime()/1000000000;
    	long time = (endtime-startime);
		System.out.println("Source List Synonyms retrieved... in " + time);
		System.out.println("Writing Source List into file...");
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("sourceConcepts.txt"));
	        for(int k = 0; k < sourceNodeName.size(); k++)
	        {
	        	out.write(sourceNodeName.get(k));
	        	out.write("|");
	        	out.write(cuisS.get(k));
	        	out.write("|");
	        	for(int m = 0; m < synsListsS[k].size(); m++){
	        		out.write(synsListsS[k].get(m).toString().replace("[", "").replace("]", ""));
	        		
	        	}
	        	out.write(":::");
	        }
	        out.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    System.out.println("Writing Source List into file... Finished...");
	}
	
	//
	public void getTargetDataFromServer()  throws Exception{
		classListSize = targetOntology.getClassesList().size();
		System.out.println("Number of classes: " + classListSize);
		
		//Get synonyms. Took 45 mins for NCI ontology(3304 concepts).
		System.out.println("Retrieving Target List synonyms for each concept...");
		long startime = System.nanoTime()/1000000000;
		ArrayList<Node> targetClassList = targetOntology.getClassesList();
		synsLists = new ArrayList[classListSize]; 
		
		targetNodeName = new ArrayList<String>();
		cuis = new ArrayList<String>();		

		for(int j = 0; j < targetClassList.size(); j++) {
			String targetName = targetClassList.get(j).getLabel();
			if(targetName.equals(""))
				continue;
			targetName = treatString(targetName);
			targetNodeName.add(targetName);
			String cui = null;
			cui = getCUIfromName(targetName);
			if(cui != null){
				cuis.add(cui);
				synsLists[curr] = new ArrayList<ArrayList<String>>();
				synsLists[curr].add(getSynonyms(cui));
				curr++;
			}
			else{
				cuis.add("fakecui");
				ArrayList<String> a = new ArrayList<String>();
				a.add("fakelist");
				synsLists[curr] = new ArrayList<ArrayList<String>>();
				synsLists[curr].add(a);
				curr++;
			}
		}
		long endtime = System.nanoTime()/1000000000;
    	long time = (endtime-startime);
		System.out.println("Target List Synonyms retrieved... in " + time);
		System.out.println("Writing Target List into file...");
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("targetConcepts.txt"));
	        for(int k = 0; k < targetNodeName.size(); k++)
	        {
	        	out.write(targetNodeName.get(k));
	        	out.write("|");
	        	out.write(cuis.get(k));
	        	out.write("|");
	        	for(int m = 0; m < synsLists[k].size(); m++){
	        		out.write(synsLists[k].get(m).toString().replace("[", "").replace("]", ""));
	        	}
	        	out.write(":::");
	        }
	        out.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    System.out.println("Writing Target List into file... Finished...");
	}
	
	//
	private void readConceptInfo(String filename)
	{
	    StringBuilder contents = new StringBuilder();
	    File aFile = new File(filename);
	    try {
	      BufferedReader input =  new BufferedReader(new FileReader(aFile));
	      try {
	        String line = null; //not declared within while loop

	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	        }
	        if(filename.equalsIgnoreCase("sourceConcepts.txt")){
	        	conceptPartsS = new String [2738][3];
	        	synonymsS = new String [2738][30];
		        conceptInfoS = contents.toString().split(":::");
		        
		        for(int j = 0; j < conceptInfoS.length; j++){
		        	//System.out.println(conceptInfoS[j]);
		        	String s = conceptInfoS[j];
		        	//STUPID FUNCTIONS DOES NOT WORK. asdiasodhaodbao
		        	int i1 = s.indexOf("|");
		        	int i2 = s.lastIndexOf("|");
		        	int i3 = s.length();
		        	conceptPartsS[j][0] = s.substring(0, i1);
		        	conceptPartsS[j][1] = s.substring(i1+1, i2);
		        	conceptPartsS[j][2] = s.substring(i2+1, i3);
		        	//String splitted[] = s.split("|",3);
		        	//conceptPartsS[j][0] = splitted[0];
		        	//System.out.println(conceptPartsS[j][0] + " -- " + conceptPartsS[j][1] + " -- " + conceptPartsS[j][2]);
		        	synonymsS[j] = conceptPartsS[j][2].split(", ");
		        }
	        }
	        else if(filename.equalsIgnoreCase("targetConcepts.txt")){
	        	conceptPartsT = new String [3304][3];
	        	synonymsT = new String [3304][30];
	        	conceptInfoT = contents.toString().split(":::");
		        for(int j = 0; j < conceptInfoT.length; j++){
		        	//System.out.println(conceptInfoT[j]);
		        	String s = conceptInfoT[j];
		        	//STUPID FUNCTIONS DOES NOT WORK. asdiasodhaodbao
		        	int i1 = s.indexOf("|");
		        	int i2 = s.lastIndexOf("|");
		        	int i3 = s.length();
		        	conceptPartsT[j][0] = s.substring(0, i1);
		        	conceptPartsT[j][1] = s.substring(i1+1, i2);
		        	conceptPartsT[j][2] = s.substring(i2+1, i3);
		        	//String splitted[] = s.split("|",3);
		        	//conceptPartsS[j][0] = splitted[0];
		        	//System.out.println(conceptPartsT[j][0] + " -- " + conceptPartsT[j][1] + " -- " + conceptPartsT[j][2]);
		        	synonymsT[j] = conceptPartsT[j][2].split(", ");
		        }
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	}
	
	
	//Function executed before aligning operation
	//Involves connecting to database, getting versions
	//If running the matcher for the first time on loaded ontologies then it connects and retrieves data.
	//After 1st execution of the matcher it does not need to do it again.
	//WARNING: if you load other ontologies and try to match with this matcher, code needs to be changed.
	//Coming...
	
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		if(firstExec){
			connectToServer();
			getSourceDataFromServer();
			getTargetDataFromServer();
			firstExec = false;
		}
		else{
			readConceptInfo("sourceConcepts.txt");
			readConceptInfo("targetConcepts.txt");
		}
	}
	
	
	//Function aligns 2 nodes using UMLS.
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes) throws Exception {
		
		//Get labels		
		String sourceName= source.getLabel();
		String targetName = target.getLabel();
		
		if(sourceName.equalsIgnoreCase("") || targetName.equalsIgnoreCase(""))
			return new Mapping( source, target, 0.0d, Mapping.EQUIVALENCE);
		
		//if(sourceName.equalsIgnoreCase("Brunner's gland"))
			//System.out.println( sourceName + " : " + targetName);
		
		if(typeOfNodes.equals(alignType.aligningClasses) )
		{
			if(!firstExec){
				//System.out.println("Check1");
				int indT = -1;
				for(int i = 0; i < conceptPartsT.length; i++){
					if(conceptPartsT[i][0] != null && conceptPartsT[i][2] != null && conceptPartsT[i][1] != null && conceptPartsT[i][0].equalsIgnoreCase(targetName)){
						indT = i;
					}
				}
				
				int indS = -1;
				for(int i = 0; i < conceptPartsS.length; i++){
					if(conceptPartsS[i][0] != null && conceptPartsT[i][2] != null && conceptPartsT[i][1] != null && conceptPartsS[i][0].equalsIgnoreCase(sourceName)){
						indS = i;
					}
				}
				
				//System.out.println(sourceName + " :: " + targetName + " : " + indS + " : " + indT);
				if(indS == -1 || indT == -1)
					return new Mapping( source, target, 0.0d, Mapping.EQUIVALENCE);
				
				if(synonymsT[indT].length == 0 || synonymsS[indS].length == 0){
					return new Mapping( source, target, 0.0d, Mapping.EQUIVALENCE);
				}
				
				if(		(synonymsT[indT].length == 1 && synonymsT[indT][0].equals("") ) 
						|| ( synonymsS[indS].length == 1 && synonymsS[indS][0].equals("") )
						){
					return new Mapping( source, target, 0.0d, Mapping.EQUIVALENCE);
				}
					
				for(int i = 0; i < synonymsT[indT].length; i++){
					String s = synonymsT[indT][i];
					for(int j = 0; j < synonymsS[indS].length; j++){
						
						if(s.equalsIgnoreCase(synonymsS[indS][j]) && !s.equalsIgnoreCase("unspecified") 
								&& !s.equalsIgnoreCase("SAI") && !s.equalsIgnoreCase("NOS") ){
							return new Mapping( source, target, 0.99d, Mapping.EQUIVALENCE);
						}
						
						/*
						BaseSimilarityMatcher bsm = new BaseSimilarityMatcher();
						bsm.setThreshold(threshold);
				    	bsm.setMaxSourceAlign(maxSourceAlign);
				    	bsm.setMaxTargetAlign(maxTargetAlign);
				    	BaseSimilarityParameters bsmp = new BaseSimilarityParameters();
				    	bsmp.initForOAEI2009();
				    	bsm.setParam(bsmp);
				    	am.app.ontology.Node sourceN = new Node(1, synonymsS[indS][j], "OWL-classnode");
				    	am.app.ontology.Node targetN = new Node(1, synonymsT[indT][j], "OWL-classnode");
						Alignment alnmt = bsm.alignTwoNodes(sourceN, targetN, alignType.aligningClasses);
						
						if(alnmt.getSimilarity() >= 0.8)
							return alnmt;
						*/
					}
				}
				
				return new Mapping( source, target, 0.0d, Mapping.EQUIVALENCE);
			}
			else
			{
				//Run treatString() on target name to remove underscores
				targetName = treatString(targetName);
				
				try {
					int index = targetNodeName.indexOf(targetName);
					if(index == -1){
						//System.out.println(targetName + " is not in the list");
						return new Mapping( source, target, 0.0d, Mapping.EQUIVALENCE);
					}
					
					String synArr[] = synsLists[index].get(0).toString().replace("[", "").replace("]", "").split(", ");
					int size = synArr.length;
					
					for (int i = 0; i < size; i++){
						if(sourceName.equalsIgnoreCase(synArr[i]))
						{
							//System.out.println(sourceName + " && " + targetName + " MATCHED FROM SYNONYMS LIST...");
							return new Mapping( source, target, 0.99d, Mapping.EQUIVALENCE);
						}
					}
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("index = " + index + " target name = " + targetName);
					e.printStackTrace();
				}
				return new Mapping( source, target, 0.0d, Mapping.EQUIVALENCE);
			}
		}
		else{
			//properties are not matched in this matcher
			if(isCompletionMode() && inputMatchers.size() > 0){ 
				return inputMatchers.get(0).getPropertiesMatrix().get(source.getIndex(), target.getIndex());
			}
			else{
				return new Mapping( source, target, 0.0d, Mapping.EQUIVALENCE);
			}
		}
	}
	
	
	//C33022 code returns C1261075
    public String getCUIfromAUI(String aui, String label) {
        
        try {
            char[] result = retriever.findCUI(DBYEAR, null, aui);
            String xml = new String(result);
            ConceptIdVector cv = new ConceptIdVector();
            cv = ConceptIdVector.getInstance(xml);
            
            //Iterator<ConceptIdVector> it = cv.iterator();
            //int i = 0;
            String [] labelWords = label.split("_");
            
            for(int j = cv.size()-1; j >= 0; j--)
            {
            	ConceptId ci = (ConceptId) cv.elementAt(j);
            	for(int k = 0; k < labelWords.length; k++){
            		if(ci.getCN().contains(labelWords[k])){
            			return ci.getCUI();
            		}
            	}
            }
            System.out.println("no cui");
        } catch (gov.nih.nlm.kss.util.DatabaseException e) {
            System.out.println(e.getMessage());
        } catch (java.rmi.RemoteException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
	
	//Function gets the synonyms of CUI, 
	//C0040300 Returns:
	//Tissue Types, Tissues, Normal Tissue, Textus, Body tissue structure (body structure),...
	public ArrayList<String> getSynonyms(String CUI) {
        ArrayList<String> synonyms = new ArrayList<String>();
        try {
            char[] result = retriever.getBasicConceptProperties(DBYEAR, CUI, null, LAT);
            String xml = new String(result);

            ConceptVector cv = new ConceptVector();
            cv = ConceptVector.getInstance(xml);
            Iterator i = cv.iterator();
            while (i.hasNext()) {
            	gov.nih.nlm.kss.models.meta.concept.Concept c = (gov.nih.nlm.kss.models.meta.concept.Concept) i.next();
                Vector terms = c.getTerms();
                ListIterator j = terms.listIterator();
                while (j.hasNext()) {
                    gov.nih.nlm.kss.models.meta.concept.Term term = (gov.nih.nlm.kss.models.meta.concept.Term) j.next();
                    if (term.getTS().equals("S")) {
                        synonyms.add(term.getTN());
                    }
                }
            }
        } catch (gov.nih.nlm.kss.util.DatabaseException e) {
            System.out.println(e.getMessage());
        } catch (java.rmi.RemoteException e) {
            System.out.println(e.getMessage());
        }
        return synonyms;
    }
	
	
	//e.g. "Acquired Immunodeficiency Syndrome" returns C0001175
	public String getCUIfromName(String def) throws RemoteException, DatabaseException, XMLException{
		ArrayList <String> matches = new ArrayList <String>();
		try {
			char[] result;
			Vector SABS = null;
			result = retriever.findCUI(DBYEAR, def, SABS, LAT, KSSRetriever.NormalizeString);
			ConceptIdVector idVector = new ConceptIdVector(new String(result));
			Iterator i = idVector.iterator();
			while (i.hasNext()) {
				ConceptId nextId = (ConceptId) i.next();
				matches.add(nextId.getCUI());
			}
		
		} catch (DatabaseException e) {
			System.out.println(e.getMessage());
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		} catch (XMLException e) {
			System.out.println(e.getMessage());
		}
		
		if (matches == null || matches.isEmpty()) {
			System.out.println("No results for " + def);
			return null;
		}
		else {
			//System.out.println( matches.get(0) );
			return matches.get(0);
		}
	}
		

	//Description of Algorithm
	public String getDescriptionString() {
		//TODO: Explain more
		return "A lexical matcher using UMLS.\n"; 
	}
	
		
	// This function treats a string
	// -- Removes identifier prefix
	private String treatString(String label) {
		//Remove identifier 
		/*
		if(label.contains("NCI_"))
			label = label.replaceFirst("NCI_", "");
		else if(label.contains("MA_"))
			label = label.replaceFirst("MA_", "");
		*/
		label = label.replace("_", " ");
		if(label.contains(" s "))
			label = label.replace(" s ", "'s ");
		return label;
	}
	
	/*
	//
	private void writeConceptToFile(){
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("outfilename"));
	        out.write("aString");
	        out.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	*/
}
