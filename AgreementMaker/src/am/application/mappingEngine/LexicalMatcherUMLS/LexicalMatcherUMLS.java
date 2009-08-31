package am.application.mappingEngine.LexicalMatcherUMLS;

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

import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.Alignment;

import am.application.ontology.Node;

public class LexicalMatcherUMLS extends AbstractMatcher{

	private KSSRetrieverV5_0 retriever = null;
    private static String hostName = "//umlsks.nlm.nih.gov/KSSRetriever";
    private static String LAT = "";
    private static String DBYEAR = "2009AA";
    private static int classListSize = 0;
    private static boolean firstExec = true;
    private static ArrayList<String> targetNodeName;
    private static ArrayList synsLists [];
    private static ArrayList<String> cuis ;
    private int curr = 0;
    
	//Constructor
	public LexicalMatcherUMLS() {
		super();
		needsParam = false;		
		alignProp = false;
	}
	
	//Connect to UMLS database 
	//You have to add your IP to the account profile from:
	//http://kscas-lhc.nlm.nih.gov/UMLSKS/servlet/Turbine/template/admin,user,KSS_login.vm
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
	public void getDataFromServer()  throws Exception{
		classListSize = targetOntology.getClassesList().size();
		System.out.println("Number of classes: " + classListSize);
		
		//Get synonyms. Took 45 mins for NCI ontology(3304 concepts).
		System.out.println("Retrieving synonyms for each concept...");
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
				synsLists[curr] = new ArrayList();
				synsLists[curr].add(getSynonyms(cui));
				curr++;
			}
			else{
				cuis.add("fakecui");
				ArrayList<String> a = new ArrayList<String>();
				a.add("fakelist");
				synsLists[curr] = new ArrayList();
				synsLists[curr].add(a);
				curr++;
			}
		}
		long endtime = System.nanoTime()/1000000000;
    	long time = (endtime-startime);
    	firstExec = false;
		System.out.println("Synonyms retrieved... in " + time);
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
			getDataFromServer();
		}
	}
	
	
	//Function aligns 2 nodes using UMLS.
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		
		//Get labels		
		String sourceName= source.getLabel();
		String targetName = target.getLabel();
		
		if(sourceName.equalsIgnoreCase("") || targetName.equalsIgnoreCase(""))
			return new Alignment( source, target, 0.0d, Alignment.EQUIVALENCE);
		
		//if(sourceName.equalsIgnoreCase("Brunner's gland"))
			//System.out.println( sourceName + " : " + targetName);
		
		//Run treatString() on target name to remove underscores
		targetName = treatString(targetName);
		
		if(typeOfNodes.equals(alignType.aligningClasses) )
		{
			try {
				int index = targetNodeName.indexOf(targetName);
				if(index == -1){
					//System.out.println(targetName + " is not in the list");
					return new Alignment( source, target, 0.0d, Alignment.EQUIVALENCE);
				}
				
				String synArr[] = synsLists[index].get(0).toString().replace("[", "").replace("]", "").split(", ");
				int size = synArr.length;
				
				for (int i = 0; i < size; i++){
					if(sourceName.equalsIgnoreCase(synArr[i]))
					{
						//System.out.println(sourceName + " && " + targetName + " MATCHED FROM SYNONYMS LIST...");
						return new Alignment( source, target, 0.99d, Alignment.EQUIVALENCE);
					}
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				System.out.println("index = " + index + " target name = " + targetName);
				e.printStackTrace();
			}
			return new Alignment( source, target, 0.0d, Alignment.EQUIVALENCE);
		}
		else{
			 return new Alignment( source, target, 0.0d, Alignment.EQUIVALENCE);
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
        ArrayList synonyms = new ArrayList();
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
	public String getCUIfromName(String def){
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
}
