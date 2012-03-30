package hierarchymatcher.internal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;



/*The aim of this matcher is to find the subClassOf and superClassOf relationships between the classes that have been mapped by equivalence
 relationship by the input matchers.*/
public class HierarchyMatcher extends AbstractMatcher
{
	private List<Node> ListOfClassesSource;
	private List<Node> ListOfClassesTarget;
	private List<Node> sourceClassList;
	private List<Node> targetClassList;
	private OntModel sourceModel1;
	private WordNetDatabase WordNet;
	private HashMap<String, List<String>> sourceWordNetMeaning;
	double inputMatcherThreshold;
	
	
	/*VARIABLES TO SET THE THRESHOLD*/
	double EQUALITY_THRESHOLD_VALUE;
	double SUBCLASS_THRESHOLD_VALUE;
	double SUPERCLASS_THRESHOLD_VALUE;
	
	
	public HierarchyMatcher()
	{
		super();
		/*minInputMatchers & maxInputMatcher insures that the user gives the input */
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
		param = new DefaultMatcherParameters();
		
		/* maxSourceAlign and maxTargetAlign set the cardinality of the alignments to many to many that is 
		 one concept in source can be aligned to more than one concept in target*/
		param.maxSourceAlign = ANY_INT;
		param.maxTargetAlign = ANY_INT;
		
		
		// Initialize the WordNet Interface (JAWS)
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		// Instantiate 
		try 
		{
			WordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ) 
		{
			Utility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}
	}
	
	@Override
	protected void align() throws Exception
	{
		/*miscellaneous variables used in  the method*/
		Node source=null;
		Node target=null;
		Node temp = null;
		OntResource resSource = null;
		OntResource resTarget = null;
		double similarityValue;
		ArrayList<String> sourcesyno;
		ArrayList<String> targetsyno;
		ArrayList<String> sourcehyper = null;
		ArrayList<String> targethyper = null;
		
		if( sourceOntology == null || targetOntology == null )
			return;  // cannot align just one ontology 
		
		/*it gets the inputMatches being used for this Algo and initializes the variables
		 * classesMatrix , propertiesMatrix */
		getInputMatcher();
		
		/*maintains a list of nodes in the ontology*/
		sourceClassList = sourceOntology.getClassesList();
		targetClassList = targetOntology.getClassesList();
		ListOfClassesSource = sourceOntology.getClassesList();
		ListOfClassesTarget = targetOntology.getClassesList();
		
		/*used for getting the individuals in the ontology*/
		sourceModel1 = sourceOntology.getModel();
		
				
		/*First Iteration that uses the similarity values received in the input matchers
		 * It calculates all the subClassOf and superClassOf relationship based on the equivalence 
		 * relationship received form the first Matcher*/
		
		for (int i = 0; i < ListOfClassesSource.size(); i++)
		{
			for(int j =0 ; j<ListOfClassesTarget.size();j++)
			{
				similarityValue = classesMatrix.getSimilarity(i, j);
				/*Right now the similarity value is kept one as a pessimist approach
				 * that anything less than one will yield wrong results. It has been verified by
				 * runs of AM*/
				if(similarityValue == 1)
				{
					source = ListOfClassesSource.get(i);
					target = ListOfClassesTarget.get(j);
					resSource = (OntResource) source.getResource();
					resTarget = (OntResource) target.getResource();
					matchManySourceTarget(source,target);
					matchManyTargetSource(source,target);
				}
			}
		}
		
		
		/*This is the second level of relationship mappings being done
		 * based on wordnet*/
		/*Get the synonym set of Source*/
		
		
		
		for (int i = 0; i < ListOfClassesSource.size(); i++)
		{
			Node sourceNode = ListOfClassesSource.get(i);
			for(int j =0 ; j<ListOfClassesTarget.size();j++)
			{
				Node targetNode = ListOfClassesTarget.get(j);
				
				ArrayList<NounSynset> sourceSynsetList = doLookUp(sourceNode);
				ArrayList<NounSynset> targetSynsetList = doLookUp(targetNode);
				
				ArrayList<ArrayList<NounSynset>> sourceHypernymList = buildHypernymList(sourceNode);
				ArrayList<ArrayList<NounSynset>> targetHypernymList = buildHypernymList(targetNode);
				
				if( synsetIsContainedBy( sourceSynsetList, targetHypernymList ) ) {
					// source > target
					/*The similarity is set to be less than 0.80d as I do not want to
					 * overwrite alread established relationships*/
					if( classesMatrix.get( sourceNode.getIndex(), targetNode.getIndex()) == null || 
						classesMatrix.getSimilarity( sourceNode.getIndex(), targetNode.getIndex()) < 0.80d ) {
						classesMatrix.set(sourceNode.getIndex(), targetNode.getIndex(), new Mapping(sourceNode, targetNode, 0.89d, MappingRelation.SUPERCLASS));
					}
				} else if ( synsetIsContainedBy(targetSynsetList, sourceHypernymList) ) {
					//source < target
					if( classesMatrix.get( sourceNode.getIndex(), targetNode.getIndex()) == null || 
							classesMatrix.getSimilarity( sourceNode.getIndex(), targetNode.getIndex()) < 0.80d ) {
							classesMatrix.set(sourceNode.getIndex(), targetNode.getIndex(), new Mapping(sourceNode, targetNode, 0.89d, MappingRelation.SUBCLASS));
						}
				}
				
			}
		}
					
	}
	
	
	private boolean synsetIsContainedBy(ArrayList<NounSynset> sourceSynsetList,
			ArrayList<ArrayList<NounSynset>> hypernymList) {
		
		for( ArrayList<NounSynset> currentHypernymList : hypernymList ) {
			for( NounSynset currentHypernym : currentHypernymList ) {
				if( sourceSynsetList.contains(currentHypernym) ) return true;
			}
		}
		
		return false;
	}
	
	
	private ArrayList<ArrayList<NounSynset>> buildHypernymList(Node source) {

		ArrayList<ArrayList<NounSynset>> retVal = new ArrayList<ArrayList<NounSynset>>();
		
		ArrayList<NounSynset> nodeLookupList = doLookUp(source);
		
		ArrayList<NounSynset> hypernymLookupList = nodeLookupList;
		/*while( true ) {
			hypernymLookupList = doHypernymLookup(hypernymLookupList);
			//if( !hypernymLookupList.isEmpty() ) retVal.add(hypernymLookupList);
			//else break;
			
		}*/
		
		for(int i=0;;i++)
		{
			if(i<5 && !hypernymLookupList.isEmpty())
			{
				hypernymLookupList = doHypernymLookup(hypernymLookupList);
				retVal.add(hypernymLookupList);
			}
			else
			{
				return retVal;
			}
		}
		//return retVal;
	}
	
	/**
	 * This method compiles a list of NounSynsets which are the hypernyms of all the passed in NounSynsets.
	 * @param hypernymLookupList
	 * @return
	 */
	private ArrayList<NounSynset> doHypernymLookup(
			ArrayList<NounSynset> hypernymLookupList) {

		ArrayList<NounSynset> hypernymSet = new ArrayList<NounSynset>();
	
		// lookup
		for (Synset t : hypernymLookupList)
		{
			NounSynset[] hypernyms = ((NounSynset)t).getHypernyms();
			for (int i = 0; i < hypernyms.length; i++)
			{
				hypernymSet.add( hypernyms[i] );
			}
		}
		return hypernymSet;
	}
	
	/**
	 * This method returns the list of corresponding WordNet Synsets for given a Node.
	 * @param conceptNode
	 * @return
	 */
	private ArrayList<NounSynset> doLookUp(Node conceptNode)
	{	
		ArrayList<NounSynset> synonymSet = new ArrayList<NounSynset>();
		
		String localName = conceptNode.getLocalName();
		String searchTerm = localName;
		
		Synset[] synsets = WordNet.getSynsets(searchTerm, SynsetType.NOUN);
		for (int i = 0; i < synsets.length; i++)
		{
			Synset currentSynset = synsets[i];
			synonymSet.add( (NounSynset) currentSynset);
		}
		return synonymSet;
}
	public void listInstancesOfOntology()
	{
		//System.out.println("****************************");
		//System.out.println("Details of Individual");
		//Save individuals of Source and Target
			ArrayList<Individual> sourceIndividuals1 = new ArrayList<Individual>();
			ExtendedIterator<Individual> sourceIndividualIterator = sourceModel1.listIndividuals();
			while(sourceIndividualIterator.hasNext())
			{
				sourceIndividuals1.add(sourceIndividualIterator.next());
			}
			for(int i = 0; i < sourceIndividuals1.size(); i++)
			{
				Individual sourceIndividual = sourceIndividuals1.get(i);
				if(sourceIndividual.isAnon())
				{
						
				}
				else
				{
						String s = sourceIndividual.getLocalName();
						StmtIterator anonPropertySourceItr =  sourceIndividual.listProperties();
						while(anonPropertySourceItr.hasNext())
						{
							Statement stmtFirstAnonIndi = (Statement)anonPropertySourceItr.nextStatement();
							Property pFirst = (Property)stmtFirstAnonIndi.getPredicate();
							RDFNode rfFirst = stmtFirstAnonIndi.getObject();
							Literal lFirst;
							if(rfFirst.isLiteral())
							{
								//System.out.println("****************************");
								//System.out.println("LOCAL NAME IS "+s);
								//System.out.println("URI IS "+sourceIndividual.getURI());
								lFirst = (Literal)rfFirst;
								//System.out.println("1-  " +lFirst.toString());
								//System.out.println("****************************");
								//String lFirstString = lFirst.toString();
							}
						}
						
						
						
				}
			}
			
		
	}
	

	public void getIndividuals( Node currentNode,ArrayList<Node> list )
	{
		System.out.println("I am getIndividulas method I have been called by "+currentNode.getLocalName());
		ArrayList<Individual> individualsList = new ArrayList<Individual>(); 
	
		OntClass currentClass = (OntClass) currentNode.getResource().as(OntClass.class);
	
		ExtendedIterator indiIter = currentClass.listInstances(true);
		
		while( indiIter.hasNext() )
			individualsList.add( (Individual) indiIter.next());
		
		System.out.println("THE LENGTH OS THE INDIVIDUAL LIST IS "+ individualsList.size());
		
		for (int k = 0; k < individualsList.size(); k++) 
		{
			int row1 = getIndex(list,individualsList.get(k).getURI());
			System.out.println("THE VALUE OF MY INDEX IS "+ row1);
			if(row1 != -1)
			{
				Node sourceTemp = (Node)list.get(row1);
				System.out.println("THE NODE I HAVE NOW IS AN INDIVIDUAL "+sourceTemp.getLocalName());
				//System.out.println("Super Class of  "+source.getLocalName()+" IS  "+sourceTemp.getLocalName());
			}
		}
		
		System.out.println("***********************");
	}
	@Override
	public int getDefaultMaxSourceRelations() {
		return AbstractMatcher.ANY_INT;
	}
	
	@Override
	public int getDefaultMaxTargetRelations() {
		return AbstractMatcher.ANY_INT;
	}
	
	private void getInputMatcher()
	{
		if(inputMatchers.size()>0)
		{
			//System.out.println("got the matchers");
			AbstractMatcher input = inputMatchers.get(0);
			classesMatrix = input.getClassesMatrix();
			propertiesMatrix = input.getPropertiesMatrix();
			double inputMatcherThreshold = input.getDefaultThreshold();
		}
	}
	private int getIndex( List<Node> list, String uri) 

	{
		for (int i = 0; i < list.size(); i++)
		{
			if(list.get(i).getUri().equals(uri))
				return i;
		}
		return -1;
	}
	
/*This function maps many Target Concepts to a source concept*/	
	private void matchManyTargetSource(Node source,Node target)
	{
		Node superClassOfTarget;
		Node superClassOfSOurce;
		
		/*GET THE INDEX OF THE INPUT PARAMETERS OF THE METHOD*/
		int indexSource = source.getIndex();
		int indexTarget = target.getIndex();
		
		
		ExtendedIterator targetIterator;
		ExtendedIterator targetSubClassIterator;
		OntClass OntClasstarget;
		OntClass subClass;
		
		/*THIS ARRAY LIST COLLECTS THE FIRST LEVEL OF SUB-CLASSES*/
		ArrayList<OntClass> targetSubClasses = new ArrayList<OntClass>();
		ArrayList<OntClass> targetSubClassesLevel2 = new ArrayList<OntClass>();
		
		superClassOfTarget = getSuperClass(target, targetClassList);
		superClassOfSOurce = getSuperClass(source, sourceClassList);
		
		/*CONVERTING THE TARGET NODE INTO OntClass*/
		OntClasstarget = (OntClass)targetClassList.get(indexTarget).getResource().as(OntClass.class);
		targetIterator = OntClasstarget.listSubClasses();
		while(targetIterator.hasNext())
		{
			targetSubClasses.add((OntClass)targetIterator.next());
			
		}
		
		/*MATCHING THE FIRST LEVEL OF SUB-CLASSES*/
		for (int k = 0; k < targetSubClasses.size(); k++) 
		{
			int row = getIndex(targetClassList,targetSubClasses.get(k).getURI());
			Node targetTemp = (Node)targetClassList.get(row);
			if(row != -1)
			{
				
				classesMatrix.set(indexSource,targetTemp.getIndex(),new Mapping(source,targetTemp,0.85d,MappingRelation.SUPERCLASS));
			}
			/*IF THE SOURCE HAS A SUPER CLASS IT WILL HAVE THE SAME subClassOf RELATIONSHIP WITH THE TARGET CONCEPT*/
			if(superClassOfSOurce != null)
			{
				classesMatrix.set(superClassOfSOurce.getIndex(),targetTemp.getIndex(),new Mapping(superClassOfSOurce,targetTemp,0.85d,MappingRelation.SUPERCLASS));
			}
		}
		

		
		/*IF SOME FIRST LEVEL OF SUBCLASSES OF TARGET HAVE THE SECOND LEVEL C subClassOf B subClassOf A WE ARE AT LEVEL B AND GOING TO LEVEL C*/
		if (targetSubClasses.size() > 0)
		{
				for (int i = 0; i < targetSubClasses.size(); i++)
				{
					subClass = targetSubClasses.get(i);
					targetSubClassIterator = subClass.listSubClasses();
					
						while(targetSubClassIterator.hasNext())
						{
							
							OntClass temp = (OntClass)targetSubClassIterator.next();
							targetSubClassesLevel2.add(temp);
							int index1 = getIndex(targetClassList,temp.getURI());
							Node temp1 = targetClassList.get(index1);
							/*HERE THE SECOND LEVEL OF SUBCLASSES ARE BEING MATCHED*/
							classesMatrix.set(indexSource,temp1.getIndex(),new Mapping(source,temp1,0.85d,MappingRelation.SUPERCLASS));
							if(superClassOfSOurce != null)
							{
								classesMatrix.set(superClassOfSOurce.getIndex(),temp1.getIndex(),new Mapping(superClassOfSOurce,temp1,0.85d,MappingRelation.SUPERCLASS));
							}
						}
					
				}
		}
		/*IF THE THIRD  LEVEL GOES TO THE FOURTH  LEVEL AS IN D subClassOf C subClassOf B subClassOf A so I attempt to match D to A*/
		
		if (targetSubClassesLevel2.size() > 0 )
		{
				for (int i = 0; i < targetSubClassesLevel2.size(); i++)
				{
					subClass = targetSubClassesLevel2.get(i);
					targetSubClassIterator = subClass.listSubClasses();
					while(targetSubClassIterator.hasNext())
					{
						OntClass temp = (OntClass)targetSubClassIterator.next();
						int index1 = getIndex(targetClassList,temp.getURI());
						Node temp1 = targetClassList.get(index1);
						classesMatrix.set(indexSource,temp1.getIndex(),new Mapping(source,temp1,0.85d,MappingRelation.SUPERCLASS));
						if(superClassOfSOurce != null)
						{
							classesMatrix.set(superClassOfSOurce.getIndex(),temp1.getIndex(),new Mapping(superClassOfSOurce,temp1,0.85d,MappingRelation.SUPERCLASS));
						}
					}
					
				}
		}
		
		/*FINALLY MATCHING THE SUPER CLASS TO THE TARGET CONCEPT WITH subClassOf relationship*/
		if(superClassOfTarget != null)
		{
			//System.out.println("I have matcher "+source.getLocalName() +" TO "+superClassOfTarget.getLocalName());
			classesMatrix.set(source.getIndex(),superClassOfTarget.getIndex(),new Mapping(source,superClassOfTarget,0.85d,MappingRelation.SUPERCLASS));
		}
		
	}
	private void matchManySourceTarget(Node source,Node target)
	{
		Node superClassOfSource;
		Node superClassOfTarget;
		
		ArrayList<OntClass> sourceSubClasses = new ArrayList<OntClass>();
		ArrayList<OntClass> sourceSubClassesLevel2 = new ArrayList<OntClass>();
		
		int indexSource = source.getIndex();
		int indexTarget = target.getIndex();
		
		OntClass OntClasssource;
		OntClass subClass;
		OntClass temp;
		ExtendedIterator sourceIterator;
		ExtendedIterator sourceSubClassIterator;
		
		superClassOfSource = getSuperClass(source,sourceClassList);
		superClassOfTarget = getSuperClass(target,targetClassList);
		
		
		OntClasssource = (OntClass)sourceClassList.get(indexSource).getResource().as(OntClass.class);
		sourceIterator = OntClasssource.listSubClasses();
		
		while(sourceIterator.hasNext())
		{
			sourceSubClasses.add((OntClass)sourceIterator.next());
			
		}
		
		/* A node of target ontology has an EQUIVALENCE relation with source concept S
		 * B subClassOf A  here we are matching all B to S by subClassOf relationship */
		for (int k = 0; k < sourceSubClasses.size(); k++) 
		{
			int row = getIndex(sourceClassList,sourceSubClasses.get(k).getURI());
			Node sourceTemp = (Node)sourceClassList.get(row);
			classesMatrix.set(sourceTemp.getIndex(),indexTarget,new Mapping(sourceTemp,target,0.85d,MappingRelation.SUBCLASS));
			if(superClassOfTarget != null)
			{
				
				classesMatrix.set(sourceTemp.getIndex(),superClassOfTarget.getIndex(),new Mapping(sourceTemp,superClassOfTarget,0.85d,MappingRelation.SUBCLASS));
			}
		}
		
		/*IF SOME FIRST LEVEL OF SUBCLASSES HAVE THE SECOND LEVEL C subClassOf B subClassOf A WE ARE AT LEVEL B AND GOING TO LEVEL C to A*/
		if (sourceSubClasses.size() > 0)
		{
			//System.out.println("I will print second level of subclasses if present");
			for (int i = 0; i < sourceSubClasses.size(); i++)
			{
				subClass = sourceSubClasses.get(i);
				sourceSubClassIterator = subClass.listSubClasses();
				while(sourceSubClassIterator.hasNext())
				{
					temp = (OntClass)sourceSubClassIterator.next();
					sourceSubClassesLevel2.add(temp);
					int index1 = getIndex(sourceClassList,temp.getURI());
					Node temp1 = sourceClassList.get(index1);
					classesMatrix.set(temp1.getIndex(),indexTarget,new Mapping(temp1,target,0.85d,MappingRelation.SUBCLASS));
					if (superClassOfTarget != null)
					{
						classesMatrix.set(temp1.getIndex(),superClassOfTarget.getIndex(),new Mapping(temp1,superClassOfTarget,0.85d,MappingRelation.SUBCLASS));
					}
				}
			}
			
		}
		
		/*IF SOME SECOND LEVEL OF SUBCLASSES HAVE THE THIRD  LEVEL D subClassOf C subClassOf B subClassOf A WE ARE AT LEVEL C AND GOING TO LEVEL D and matching D to A*/
		if (sourceSubClassesLevel2.size() > 0)
		{
			
			for (int i = 0; i < sourceSubClassesLevel2.size(); i++)
			{
				subClass = sourceSubClassesLevel2.get(i);
				sourceSubClassIterator = subClass.listSubClasses();
				while(sourceSubClassIterator.hasNext())
				{
					temp = (OntClass)sourceSubClassIterator.next();
					int index1 = getIndex(sourceClassList,temp.getURI());
					Node temp1 = sourceClassList.get(index1);
					classesMatrix.set(temp1.getIndex(),indexTarget,new Mapping(temp1,target,0.85d,MappingRelation.SUBCLASS));
					if (superClassOfTarget != null)
					{
						classesMatrix.set(temp1.getIndex(),superClassOfTarget.getIndex(),new Mapping(temp1,superClassOfTarget,0.85d,MappingRelation.SUBCLASS));
					}
				}
			}
			
		}
		
		
		
		if(superClassOfSource != null)
		{
			//System.out.println("I have matcher "+source.getLocalName() +" TO "+superClassOfSource.getLocalName());
			classesMatrix.set(superClassOfSource.getIndex(),target.getIndex(),new Mapping(superClassOfSource,target,0.85d,MappingRelation.SUPERCLASS));
		}
	}
	private Node getSuperClass(Node Target, List<Node> list)
	{
		int indexTarget = Target.getIndex();
		ArrayList<OntClass> targetSuperClasses = new ArrayList<OntClass>();
		//targetSuperClasses = null;
		OntClass OntClassTarget = (OntClass)list.get(indexTarget).getResource().as(OntClass.class);
		ExtendedIterator targetIterator = OntClassTarget.listSuperClasses();
		while(targetIterator.hasNext())
		{
					targetSuperClasses.add((OntClass)targetIterator.next());
		}
		for (int k = 0; k < targetSuperClasses.size(); k++) 
		{
			int row1 = getIndex(list,targetSuperClasses.get(k).getURI());
			if(row1 != -1)
			{
				Node targetTemp = (Node)list.get(row1);
				//System.out.println("Node being returned is "+targetTemp.getLocalName());
				return targetTemp;
			}
		}
		return null;
	}	
}