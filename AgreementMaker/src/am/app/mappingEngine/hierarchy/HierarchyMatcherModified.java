package am.app.mappingEngine.hierarchy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import am.GlobalStaticVariables;
import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.LinkedOpenData.LODOntologies;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

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
public class HierarchyMatcherModified extends AbstractMatcher
{
	private ArrayList<Node> ListOfClassesSource;
	private ArrayList<Node> ListOfClassesTarget;
	private ArrayList<Node> sourceClassList;
	private ArrayList<Node> targetClassList;
	private ArrayList<Node> ListOfPropSource;
	private ArrayList<Node> ListOfPropTarget;
	private ArrayList<Node> sourcePropList;
	private ArrayList<Node> targetPropList;
	private ArrayList<String> sourceSynonym;
	private ArrayList<String> targetSynonym;
	private OntModel sourceModel1;
	private WordNetDatabase WordNet;
	private HashMap<String, List<String>> sourceWordNetMeaning;
	double inputMatcherThreshold;
	
	
	/*VARIABLES TO SET THE THRESHOLD*/
	double EQUALITY_THRESHOLD_VALUE;
	double SUBCLASS_THRESHOLD_VALUE;
	double SUPERCLASS_THRESHOLD_VALUE;
	
	boolean useOtherOntologies = true;
	ArrayList<OntModel> otherOntologies;
	
	public HierarchyMatcherModified()
	{
		super();
		/*minInputMatchers & maxInputMatcher insures that the user gives the input */
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
		param = new AbstractParameters();
		
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
	
	protected void align() throws Exception{
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
		sourcePropList = sourceOntology.getPropertiesList();
		targetPropList = targetOntology.getPropertiesList();
		ListOfClassesSource = sourceOntology.getClassesList();
		ListOfClassesTarget = targetOntology.getClassesList();
		ListOfPropSource = sourceOntology.getPropertiesList();
		ListOfPropTarget = sourceOntology.getPropertiesList();
		
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
		
		String name;
		String[] sourceSplitted;
		Node sNode;
		Node tNode;
		for (int i = 0; i < sourceClassList.size(); i++) {
			sNode = sourceClassList.get(i);
			name = Utilities.separateWords(sNode.getLocalName());
			sourceSplitted = name.split(" ");
			
			if(sourceSplitted.length != 2) continue;
			
			for (int j = 0; j < targetClassList.size(); j++) {
				tNode = targetClassList.get(j);
				if(sourceSplitted[1].equals(tNode.getLocalName())){
					Mapping m = new Mapping(sNode, tNode, 1.0d, MappingRelation.SUBCLASS);
					classesMatrix.set(i, j, m);
				}
			}
		}
		
		String[] targetSplitted;
		for (int i = 0; i < targetClassList.size(); i++) {
			tNode = targetClassList.get(i);
			name = Utilities.separateWords(tNode.getLocalName());
			targetSplitted = name.split(" ");
			
			if(targetSplitted.length != 2) continue;
			
			for (int j = 0; j < sourceClassList.size(); j++) {
				sNode = sourceClassList.get(j);
				if(targetSplitted[1].equals(sNode.getLocalName())){
					Mapping m = new Mapping(sNode, tNode, 1.0d, MappingRelation.SUPERCLASS);
					classesMatrix.set(j, i, m);
				}
			}
		}
		
		
		
		
		/*This is the second level of relationship mappings being done
		 * based on wordnet*/
		/*Get the synonym set of Source*/
		
		
		
		for (int i = 0; i < ListOfClassesSource.size(); i++)
		{
			Node sourceNode = ListOfClassesSource.get(i);
			
			System.out.println(sourceNode.getLocalName());
			
			ArrayList<NounSynset> sourceSynsetList = doLookUp(sourceNode);
			
			ArrayList<NounSynset> dis = disambiguate(sourceNode, sourceSynsetList);
			
			if(dis.size()>0){
				System.out.println("IUPPIS: disambiguated "+ sourceNode.getLocalName());
				System.out.println(dis);
				sourceSynsetList = dis;
			}
						
			ArrayList<ArrayList<NounSynset>> sourceHypernymList = buildHypernymList(sourceNode, sourceSynsetList, 5);
			
			System.out.println(sourceSynsetList);
			System.out.println(dis);
			System.out.println(sourceHypernymList);
			
			for(int j =0 ; j<ListOfClassesTarget.size();j++)
			{
				Node targetNode = ListOfClassesTarget.get(j);
				
				ArrayList<NounSynset> targetSynsetList = doLookUp(targetNode);
				
				ArrayList<NounSynset> targetDis = disambiguate(targetNode, targetSynsetList);
				
				if(targetDis.size()>0){
					System.out.println("IUPPIT: disambiguated "+ targetNode.getLocalName());
					System.out.println(targetDis);
					//targetSynsetList = targetDis;
				}
				
				ArrayList<ArrayList<NounSynset>> targetHypernymList = buildHypernymList(targetNode, null, 5);
				
				
				if( synsetIsContainedBy( sourceSynsetList, targetHypernymList ) ) {
					// source > target
					/*The similarity is set to be less than 0.80d as I do not want to
					 * overwrite alread established relationships*/
					if( classesMatrix.get( sourceNode.getIndex(), targetNode.getIndex()) == null || 
						classesMatrix.getSimilarity( sourceNode.getIndex(), targetNode.getIndex()) < 0.80d ) {
						System.out.println("mapping >: "+sourceNode.getLocalName() + " " + targetNode.getLocalName() + sourceSynsetList);
						classesMatrix.set(sourceNode.getIndex(), targetNode.getIndex(), new Mapping(sourceNode, targetNode, 0.89d, MappingRelation.SUPERCLASS));
					}
				} else if ( synsetIsContainedBy(targetSynsetList, sourceHypernymList) ) {
					//source < target
					if( classesMatrix.get( sourceNode.getIndex(), targetNode.getIndex()) == null || 
							classesMatrix.getSimilarity( sourceNode.getIndex(), targetNode.getIndex()) < 0.80d ) {
							System.out.println("mapping <: "+sourceNode.getLocalName() + " " + targetNode.getLocalName() + targetSynsetList);
							classesMatrix.set(sourceNode.getIndex(), targetNode.getIndex(), new Mapping(sourceNode, targetNode, 0.89d, MappingRelation.SUBCLASS));
						}
				}
				
			}
		}
		
		//computeTransitiveClosure();
					
		
//		for (int i = 0; i < classesMatrix.getRows(); i++) {
//			for (int j = 0; j < classesMatrix.getColumns(); j++) {
//				System.out.println(sourceClassList.get(i).getLocalName() + " " + targetClassList.get(j).getLocalName());
//				if(classesMatrix.get(i,j) == null) continue;
//				
//				if(!classesMatrix.get(i,j).getEntity1().getLocalName().equals(sourceClassList.get(i).getLocalName())
//					|| !classesMatrix.get(i,j).getEntity2().getLocalName().equals(targetClassList.get(j).getLocalName()))
//					System.out.println("WEIRD");
//				
//			}
//		}
		
		if(useOtherOntologies == true){
			initOtherOntologies();
			useOtherOntologies();
		}
		
		filterEqualityMappings();
		
	}
	
	private void initOtherOntologies() {
		Ontology ontology;
		OntoTreeBuilder treeBuilder;
		otherOntologies = new ArrayList<OntModel>();
		
		if(!sourceOntology.getURI().equals(LODOntologies.FOAF_URI) &&
				!targetOntology.getURI().equals(LODOntologies.FOAF_URI)){
			System.out.println("Opening FOAF");
			try {
				treeBuilder = new OntoTreeBuilder(LODOntologies.FOAF, GlobalStaticVariables.SOURCENODE,
						GlobalStaticVariables.LANG_OWL, 
						GlobalStaticVariables.SYNTAX_RDFXML, false, true);
				treeBuilder.build();
				ontology = treeBuilder.getOntology();
				otherOntologies.add(ontology.getModel());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		if(!sourceOntology.getURI().equals(LODOntologies.SW_CONFERENCE_URI) &&
				!targetOntology.getURI().equals(LODOntologies.SW_CONFERENCE_URI)){
			System.out.println("Opening SWC");
			try {
				treeBuilder = new OntoTreeBuilder(LODOntologies.SW_CONFERENCE, GlobalStaticVariables.SOURCENODE,
						GlobalStaticVariables.LANG_OWL, 
						GlobalStaticVariables.SYNTAX_RDFXML, false, true);
				
				treeBuilder.build();
				ontology = treeBuilder.getOntology();
				otherOntologies.add(ontology.getModel());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * This method uses information taken from other LOD ontologies to improve the match.
	 * The idea is that we can use sub and superclasses of standard classes in LOD famous ontologies to
	 * get more information to use in the matching process. 
	 */
	private void useOtherOntologies() {
		OntModel ontology;
		Node source;
		Node target;
		for (int i = 0; i < sourceClassList.size(); i++) {
			source = sourceClassList.get(i);
			
			for (int j = 0; j < otherOntologies.size(); j++) {
				ontology = otherOntologies.get(j);
				
				for (OntClass cl: ontology.listClasses().toList()) {
					
					if(source.getUri().equals(cl.getURI())){
						System.out.println("WOW Let's study class " + cl.getURI());
						
						System.out.println(cl.listSuperClasses().toList());
						
						//Subclasses of the class can help generate superclass mappings
						for(OntClass subclass : cl.listSubClasses().toList()){
							
							
							for(int t = 0; t < targetClassList.size(); t++){
								target = targetClassList.get(t);
								if(target.getLocalName().equals(subclass.getLocalName())){
									//create superclass mapping source -> target
									System.out.println("CREATING MAPPING "+source.getLocalName() + " " + target.getLocalName());
									Mapping m = new Mapping(source, target, 1.0, MappingRelation.SUPERCLASS);
									classesMatrix.set(i, t, m);
									
									//also subclasses of target are subclasses of source:
									for(OntClass child: listAllSubclasses(target.getResource().as(OntClass.class), null)){
										int index = getIndex(targetClassList, child.getURI());
										m = new Mapping(source, targetClassList.get(index), 1.0, MappingRelation.SUPERCLASS);
										classesMatrix.set(i, index, m);
									}
								}
							}
						}
						
						
						
					}
				}
				
			}
			
			
		}
		
	}

	private void filterEqualityMappings() {
		Mapping m;
		for (int i = 0; i < classesMatrix.getRows(); i++) {
			for (int j = 0; j < classesMatrix.getColumns(); j++) {
				if(classesMatrix.get(i, j) != null){
					m = classesMatrix.get(i, j);
					if(m.getRelation() == MappingRelation.EQUIVALENCE && m.getSimilarity()<1.0)
						classesMatrix.set(i, j, null);
				}
			}
		}	
	}

	private ArrayList<NounSynset> disambiguate(Node sourceNode, ArrayList<NounSynset> sourceSynsetList) {
		//System.out.println("DISAMBIGUATE");
		Set<Node> descendantsSet = sourceNode.getDescendants();
		List<Node> descendants = new ArrayList<Node>(descendantsSet);
		
		ArrayList<NounSynset> synsetList;
		ArrayList<ArrayList<NounSynset>> hypernymList;
		ArrayList<NounSynset> contained = new ArrayList<NounSynset>();
		for (int i = 0; i < descendants.size(); i++) {
			hypernymList = buildHypernymList(descendants.get(i), null, 5);
			for (int j = 0; j < hypernymList.size(); j++) {
				for (int k = 0; k < hypernymList.get(j).size(); k++) {
					if(sourceSynsetList.contains(hypernymList.get(j).get(k)) && !contained.contains(hypernymList.get(j).get(k)))
						contained.add(hypernymList.get(j).get(k));
				}
			}			
		}
		return contained;
	}

	private void computeTransitiveClosure() {
		Mapping m;
		for (int i = 0; i < classesMatrix.getRows(); i++) {
			for (int j = 0; j < classesMatrix.getColumns(); j++) {
				 m = classesMatrix.get(i, j);
				 if(m == null) continue;
				 if(m.getRelation() == MappingRelation.SUBCLASS){
					 OntClass source = sourceClassList.get(m.getSourceKey()).getResource().as(OntClass.class);
					 List<OntClass> subclasses = listAllSubclasses(source, null);
					 for(OntClass cl: subclasses){
						 int index = getIndex(sourceClassList, cl.getURI());
						 Mapping mapping = new Mapping(sourceClassList.get(index), m.getEntity2(), m.getSimilarity(), m.getRelation());
						 classesMatrix.set(index, j, mapping);
					 }
				 }
				 else if(m.getRelation() == MappingRelation.SUPERCLASS){
					 OntClass target = targetClassList.get(m.getTargetKey()).getResource().as(OntClass.class);
					 List<OntClass> subclasses = listAllSubclasses(target, null);
					 for(OntClass cl: subclasses){
						 int index = getIndex(targetClassList, cl.getURI());
						 Mapping mapping = new Mapping(m.getEntity1(), targetClassList.get(index), m.getSimilarity(), m.getRelation());
						 classesMatrix.set(i, index, mapping);
					 }
				 }
			}
		}		
	}
	
//	private void extendToChildren(OntClass node, OntClass , MappingRelation relation, double similarity){
//		List<OntClass> subclasses = node.listSubClasses().toList();
//		
//	}
//	
	private List<OntClass> listAllSubclasses(OntClass node, List<OntClass> result){
		if(result == null) result = new ArrayList<OntClass>();
		List<OntClass> subclasses = node.listSubClasses().toList();
		result.addAll(subclasses);
		for(OntClass cl: subclasses)
			listAllSubclasses(cl, result);
		return result;
	}
	

	private boolean synsetIsContainedBy(ArrayList<NounSynset> sourceSynsetList,
			ArrayList<ArrayList<NounSynset>> hypernymList) {
		
		for( ArrayList<NounSynset> currentHypernymList : hypernymList ) {
			for( NounSynset currentHypernym : currentHypernymList ) {
				if( sourceSynsetList.contains(currentHypernym) ){
					System.out.println("HYP:" + currentHypernym);
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	private ArrayList<ArrayList<NounSynset>> buildHypernymList(Node source, ArrayList<NounSynset> nodeLookupList, int limit) {
		ArrayList<ArrayList<NounSynset>> retVal = new ArrayList<ArrayList<NounSynset>>();
			
		if(nodeLookupList == null)
			nodeLookupList = doLookUp(source);
			
		ArrayList<NounSynset> hypernymLookupList = nodeLookupList;
		 
		/*while( true ) {
			hypernymLookupList = doHypernymLookup(hypernymLookupList);
			//if( !hypernymLookupList.isEmpty() ) retVal.add(hypernymLookupList);
			//else break;
			
		}*/
		
		for(int i=0;;i++)
		{
			if(i<limit && !hypernymLookupList.isEmpty())
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
		int limit = 3;
		
		ArrayList<NounSynset> synonymSet = new ArrayList<NounSynset>();
		
		String localName = conceptNode.getLocalName();
		String searchTerm = localName;
		
		Synset[] synsets = WordNet.getSynsets(searchTerm, SynsetType.NOUN);
		for (int i = 0; i < Math.min(synsets.length, limit); i++)
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
	private Node getSuperClass(Node Target,ArrayList<Node> list)
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
	
	public static int getIndex(ArrayList<Node> list, String uri) 

	{
		for (int i = 0; i < list.size(); i++)
		{
			if(list.get(i).getUri().equals(uri))
				return i;
		}
		return -1;
	}
}