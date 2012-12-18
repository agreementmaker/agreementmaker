package am.extension.partition;

import java.util.ArrayList;
import am.app.ontology.Ontology;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.semanticweb.HermiT.model.Concept;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntTools;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;

import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.extension.partition.CustomNode;

public class OntoProcessing {
	
	public static double percentage = 0.5;
	
	public static final double threshold = 0.8;
	public static final double proximityThreshold = 0.1;
	public double globalHighestCohesion = 0.0;
	public int globalHighestCohesionIndex = 0;
    int iterationOfBlockCreation = 0;
	int[][] ancestors = null;
	double[][] coupling = null;
	

	/**
	 * @param args
	 */
	public int calculateWeightedLinks(Node c1, Node c2)
	{
		int linkWeight;
		
		
		
		return 0;
	}
	
	public static void traverseOntology(String sourceOntFile)
	{	
		//Model m = ModelFactory.createDefaultModel();
		//m.read(sourceOntFile);
		
		Ontology onto = OntoTreeBuilder.loadOWLOntology(sourceOntFile);
		//int tree_count = onto.getTreeCount();
		//System.out.println("Count of tree = "+tree_count);
		
		List<Node> sourceClassList = onto.getClassesList();
		System.out.println("Size of source class list = "+sourceClassList.size());
		
		int indexCount = 1;
		Iterator<Node> iter = sourceClassList.iterator();
		Node firstClassElem = null;
		ArrayList<CustomNode> listofNodes = new ArrayList<CustomNode>();
		ArrayList<Node> parents = null;
		while(iter.hasNext())																	//For all classes under OWL Classes Hierarchy
		{	
			firstClassElem = iter.next();
			String localName = firstClassElem.getLocalName();
			//System.out.println("Parent node # "+indexCount+ ":"+localName+ " with their depth="+firstClassElem.getDepth());
			
			parents = new ArrayList<Node>();
			parents = addParentstoList(firstClassElem, parents);
				
			CustomNode cn = new CustomNode(firstClassElem,parents,localName,firstClassElem.getDepth());
			
			listofNodes.add(cn);
			indexCount++;
		}
		
		
		Collections.sort(listofNodes, new CustomNodeComparator());										//sorts listofNodes in ascending order
		Iterator<CustomNode> iter2 = listofNodes.iterator();
		System.out.println("<------------------- Sorted List of CustomNodes --------------------->");
		while(iter2.hasNext())
		{
			CustomNode custNode = iter2.next();
			
			System.out.println("LocalName: "+custNode.localName+ " Depth: "+custNode.depth);
			System.out.println("Ancestors for "+custNode.localName+ " in their order of reverse traversal");
			for(Node ancNode : custNode.ancestorList)
			{
				System.out.println("----->"+ancNode.getLocalName());
			}
			System.out.println(" ");
		}
		
		int fixed = 0;
		int iter1 = 0;
		
		for(fixed=0; fixed<listofNodes.size(); fixed++)
		{	
			for(iter1 = fixed+1; (iter1<listofNodes.size()) && (listofNodes.get(iter1).depth - listofNodes.get(fixed).depth<=1); iter1++)
			{
				
				Node commonAnc = getCommonAncestor(listofNodes.get(iter1),listofNodes.get(fixed));
				
				if(commonAnc!=null)
				{	
				System.out.println("Common ancestor for "+listofNodes.get(iter1).localName + " and "+listofNodes.get(fixed).localName + "is "+commonAnc.getLocalName());
				
				//float linkWeight = 2*(commonAnc.getDepth())/(listofNodes.get(iter1).depth)+(listofNodes.get(fixed).depth);
				
				}
				//Adding Node to the hashmap of outer Node  with its link weight
				//listofNodes.get(fixed).insertsimilarConcepts(listofNodes.get(iter1), linkWeight);
				System.out.println(" ");
			}
		}
		
		
	}
	
	public static ArrayList<Node> addParentstoList(Node startNode, ArrayList<Node> parentList)
	{	
		parentList.add(startNode);
		if(startNode.getIndex() != -1)
		{	
			List<Node> ls = startNode.getParents();
			
			for(int i=0; i<ls.size(); i++)	
				addParentstoList(ls.get(i), parentList);
		}
		return parentList;
	}
	
	
	public static Node getCommonAncestor(CustomNode node1, CustomNode node2)
	{
		ArrayList<Node> parents1 = node1.ancestorList;
		
		Node sca = null;
		
		boolean chkFlag = false;
		//System.out.println("Common ancestor computation for "+node1.localName +" and "+node2.localName);
		//System.out.println("Node1 ancestor list size ="+parents1.size());
		//System.out.println("Node2 ancestor list size ="+parents2.size());
		
		for(int i=0; i<node1.ancestors.length; i++)
		{
			for(int j=0; j<node2.ancestors.length; j++)
			{
				if(node1.ancestors[i]==node2.ancestors[j])
				{
					sca = ((List<Node>)parents1).get(i);
					chkFlag = true;
					break;
				}	
			}
			if(chkFlag == true)
				break;
		}
		
		return sca;
	}
	
	/*public float calculateCohesivenesswithnBlock(ArrayList<CustomNode> cns)
	{	
		float cohesive_val;
		
		if(cns.size() == 1)				            //Initial calculation of cohesiveness for each concept as a block 
		{	
			float link_weight = 1;
			cohesive_val = 1;
		}
		else
		{	
			List<Number> linkW = null;
			for(CustomNode i : cns)					//For each concept belonging to the input block
			{	
				Set<CustomNode> customNKeys = i.similarNodeWeights.keySet();
				
				for(CustomNode similarNodes : customNKeys)						//Getting all similarNodes for the concept node
				{	
					float simVal = i.similarNodeWeights.get(similarNodes);
					linkW.add(simVal);
				}
			}
			
			System.out.println("Size of linkWeight list for all the concepts within a block: "+linkW.size());
			cohesive_val = calculateSum(linkW);
		}
		
		return cohesive_val;
	}*/
	
	public static int getCommonAncestor1(CustomNode node1, CustomNode node2)
	{
		ArrayList<Node> parents1 = node1.ancestorList;
		
		Node sca = null;
		
		boolean chkFlag = false;
		//System.out.println("Common ancestor computation for "+node1.localName +" and "+node2.localName);
		//System.out.println("Node1 ancestor list size ="+parents1.size());
		//System.out.println("Node2 ancestor list size ="+parents2.size());
		
		for(int i=0; i<node1.ancestors.length; i++)
		{
			for(int j=0; j<node2.ancestors.length; j++)
			{
				if(node1.ancestors[i]==node2.ancestors[j])
				{
					sca = ((List<Node>)parents1).get(i);
					chkFlag = true;
					break;
				}	
			}
			if(chkFlag == true)
				break;
		}
		
		return sca.getIndex();
	}

	public double calculateCohesivenesswithnBlock(ArrayList<CustomNode> cn)								//cn defines a block of concepts
    {
		
		  if(cn.size()==1) return 100000;
          double cohesive_val;
          double linkWeight = 0;
          CustomNode first;
          CustomNode second;
          Node ancestor = null;
          int firstDepth,secondDepth,aDepth;;
          for (int i=0;i<cn.size();i++)																	//For number of blocks created from original ontology
          {	  
        	  for(int j=0;j<cn.size();j++)																//For each element within that block
              {	
        		  
        		  first = cn.get(i);
                  second = cn.get(j);
                  firstDepth = first.n.getDepth();
                  secondDepth = second.n.getDepth();
        		  if(Math.abs(firstDepth - secondDepth) <= 1)
        		  {
	        		  
	                  
	                  if(first.equals(null)||second.equals(null)) continue;
	                 
	                	 
	                     
	                	 aDepth = getCommonAncestor(first,second).getDepth();
	                  
	                  //System.out.println("Calculating link weight for "+first.localName +" and "+second.localName);
	                  linkWeight += (2*aDepth+1)/(double)(firstDepth+1+secondDepth+1);
	                  //System.out.println("Linkweight after iteration #"+(j+1)+" is "+linkWeight );
        		  }

              }
          }
        	  
          cohesive_val = linkWeight/(cn.size()*cn.size());
          return cohesive_val;
    }


	public static double calculateCoupling(ArrayList<CustomNode> cns1,ArrayList<CustomNode> cns2)
	{
		if(cns1.size()==1 && cns2.size()==1) return 100000;
	      double cohesive_val;
          double linkWeight = 0;
	      CustomNode first;
	      CustomNode second;
          
          int firstDepth,secondDepth,aDepth;;
	      for (int i=0;i<cns1.size();i++)
	    	  for(int j=0;j<cns2.size();j++)
	          {
	    		  first = cns1.get(i);
	              second = cns2.get(j);
	    		  if(Math.abs(first.n.getDepth()-second.n.getDepth()) <= 1)
        		  {
		              
		              firstDepth = first.n.getDepth();
		              secondDepth = second.n.getDepth();
		              Node ancestor = null;
		              if(first.equals(null)||second.equals(null)) continue;
	                  
	                         aDepth = getCommonAncestor(first,second).getDepth();
		                  
	                  
	                  
		              //System.out.println("Nr :"+2*(getCommonAncestor(first,second).getDepth()));
		              //System.out.println("Dr :"+(first.n.getDepth())+(second.n.getDepth()));
		              double linkW = 2*(aDepth)/(double)(firstDepth+1+secondDepth+1)/(double)(firstDepth+1+secondDepth+1);
		              //linkWeight += (2*(getCommonAncestor(first,second).getDepth()))/((first.n.getDepth())+(second.n.getDepth()));
		              linkWeight+=linkW;
        		  }
	    		  
	          }
	                
	      cohesive_val = linkWeight/(cns1.size()*cns2.size());
	      return cohesive_val;
	}
	
	/**
	 * Calculates sum of link weights in a list of weights for one or multiple nodes 
	 * @return
	 */
	public static float calculateSum(List<Number> linkWeights)				
	{
		float total=0;
		for(Number i : linkWeights)
		{
			total += i.floatValue();
		}
		
		return total;
	}
	
	public  ArrayList<ArrayList<CustomNode>> createBlocks(ArrayList<CustomNode> cns)
	{
		ArrayList<ArrayList<CustomNode>> a = new ArrayList<ArrayList<CustomNode>>(cns.size());
		ArrayList<CustomNode> cn ;
		ArrayList<CustomNode> temp = new ArrayList<CustomNode>();
		
		double tempCoh = 0.0;
        globalHighestCohesion = 0.0;
		globalHighestCohesionIndex=0;
		iterationOfBlockCreation = 0;
		for(int i=0;i<cns.size();i++)
		{
			cn = new ArrayList<CustomNode>();
			temp.add(cns.get(i));
			 tempCoh = calculateCohesivenesswithnBlock(temp);
			 temp.remove(0);
			 cns.get(i).cohesion  = tempCoh;
			 cns.get(i).indexOfBlock = i;
			 
			 if(tempCoh>=globalHighestCohesion)
				{	
					globalHighestCohesion = tempCoh;
					globalHighestCohesionIndex=i;				    
				}	
				else 
					;//Do nothing
			cn.add(cns.get(i));
			a.add(cn);		
		}
		
		mergeBlocks(a);
		return a;
	}
	
	public void mergeBlocks(ArrayList<ArrayList<CustomNode>> a)
	{
		double coh = 0, tempCoh = 0, coupling = 0, temp;
		int index = 0, indexofcoup = 0;
		int size = a.size();
		int block_size = 1;
		int temp_blocksize = a.size();
		int count = 0;
		
		//couplingTracker = constructCouplingTracker(a);
		
		while(a.size() > percentage*size)																//Restricting the no of elements to be added within a block
		{	
			
			if(temp_blocksize == a.size() && count!=0)
				break;
			System.out.println(a.size());
			temp_blocksize = a.size();
			 
			iterationOfBlockCreation++;
			index = globalHighestCohesionIndex;
			
			System.out.println(coh+"Value of index after calculating Cohesiveness of all the blocks="+index+" "+a.size());
			int run = 0;
			
			if(index==a.size()-1 &&a.size()>1)
				run = a.size()-2;
			else 
				run = a.size()-1;
		    coupling = 0;
			//for(int i=index;i<a.size();i++)	
				for(int j=0;j<=run;j++)
				{	
					    System.out.println(j);
					    if(j==index) continue;
					    
						temp = calculateCoupling(a.get(index), a.get(j));
                        if(j==0) coupling=temp;
						System.out.println("Temp coupling value returned from calCoupling fn :"+temp);
						//coupling = temp>coupling?temp:coupling;
						System.out.println("Value of coupling :"+coupling);
						if(temp>coupling)
						{	
							coupling = temp;
							//indexofcoup=index;
							indexofcoup=j;
						}	
						else
						{
							//Do nothing
							//System.out.println("Inside else that means no change in indexofcoup");
						}
					
					System.out.println("");
				}
				
				
				
				
				
			if(indexofcoup == index || indexofcoup == a.size()) indexofcoup = indexofcoup - 1;
			System.out.println(a.size()+"Value of indexofcoup after calculating Coupling of all the blocks="+indexofcoup);	
			
			a = merge(a,index,indexofcoup);
			a.remove(indexofcoup);
		//	couplingTracker = updateCouplingTracker(a, couplingTracker, index, indexofcoup);
			//Reusing tempCoh
			
			tempCoh = 0;
			for(int l=0;l<a.size();l++)
				if(a.get(l).get(0).cohesion > tempCoh)
				        globalHighestCohesionIndex = l;		
			
			for(int x=0; x<a.size(); x++)
			{	
				System.out.println("Block #"+(x+1));
				for(int k=0; k<a.get(x).size(); k++)
					System.out.print(a.get(x).get(k).localName+",");
				
				System.out.println(" ");
			}
			
			//block_size = a.get(index).size();
			
			count++;
		}
		
	}
	
	public ArrayList<ArrayList<CustomNode>> merge(ArrayList<ArrayList<CustomNode>> a,int i, int j)
	{
		System.out.println(i+"a"+j);
		double tempCoh = 0.0;
		for(int k=0;k<a.get(j).size();k++)
		{	
			//System.out.println(a.get(i));
			System.out.println(a.get(j).get(k));
			a.get(i).add(a.get(j).get(k));
		}
		tempCoh =  calculateCohesivenesswithnBlock(a.get(i));
		a.get(i).get(0).cohesion = tempCoh;
		/*if(i<j)
		for(int l=i+1;l<a.size();l++)
		{
			a.get(l).get(0).indexOfBlock = l-1;
		}
		
		else
			for(int l=j+1;l<a.size();l++)
			{
				a.get(l).get(0).indexOfBlock = l-1;
			}
			
		*/
			    

		return a;
	}
	
		
	public static void scanOntology(String onto)
	{	
		OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        FileManager.get().readModel( m, "src/main/resources/sbirs.owl" );
         
        //String NS = "http://www.semanticweb.org/ontologies/2012/0/SBIRS.owl#";

        OntClass shape = m.getOntClass( onto + "Organization" );

        for (Iterator<OntClass> subs = shape.listSubClasses();
             subs.hasNext(); ) {
            OntClass sub = subs.next();

            System.out.println( "sbirs:Shape has sub-class " + sub.getURI() +
                                " with declared properties:" );

            for (Iterator<OntProperty> props = sub.listDeclaredProperties();
                 props.hasNext(); ) {
                OntProperty declared = props.next();
                System.out.println( " - " + declared );
            }
        }
    }
	
	public static ArrayList<CustomNode> testOntoScalability(Ontology onto)
	{
		
		ArrayList<CustomNode> listofNodes = new ArrayList<CustomNode>();
		ArrayList<Node> parents = null;
		
		Node classRoot = onto.getClassesRoot();
		
		List<Node> list = classRoot.getChildren();										//Returns the list of nodes having depth 1
		int indexCount=0;
		for(Node n : list)
		{
			//System.out.println("Level 1 nodes returned :"+n.getLocalName() +"with Depth : "+n.getDepth());
			String localName = n.getLocalName();
			parents = new ArrayList<Node>();
			parents = addParentstoList(n, parents);
			
			CustomNode cn = new CustomNode(n,parents,localName,n.getDepth());
			cn.indexOfNode  = indexCount++;
			listofNodes.add(cn);
			
			addChildrenToList(n,listofNodes);
		}
		
		int classCount = 1;
		for(CustomNode customNode : listofNodes)
		{	
			System.out.println("Element#"+classCount+" --> "+customNode.localName+ " with depth="+customNode.depth+"with Index "+customNode.n.getIndex());
			System.out.println("\t With ancestors");
			
			for(int i=0; i<customNode.ancestorList.size(); i++)
			{	
				System.out.print(customNode.ancestorList.get(i).getLocalName()+",");
			}
			classCount++;
			System.out.println("\n");
		}
		
		Collections.sort(listofNodes, new CustomNodeComparator());
		
		/*List<Node> sourceClassList = onto.getClassesList();
		Node firstClassElem = null;
		ArrayList<CustomNode> listofNodes = new ArrayList<CustomNode>();
		ArrayList<Node> parents = null;
		Iterator<Node> iter = sourceClassList.iterator();
		
		while(iter.hasNext())																	//For all classes under OWL Classes Hierarchy
		{	
			firstClassElem = iter.next();
			String localName = firstClassElem.getLocalName();
			//System.out.println("Parent node # "+indexCount+ ":"+localName+ " with their depth="+firstClassElem.getDepth());
			
			parents = new ArrayList<Node>();
			parents = addParentstoList(firstClassElem, parents);
				
			CustomNode cn = new CustomNode(firstClassElem,parents,localName,firstClassElem.getDepth());
			listofNodes.add(cn);
		}
		
		Collections.sort(listofNodes, new CustomNodeComparator());								//Sorting concepts in ascending order of their depth
		*/
		return listofNodes;
	}
	
	
	public static ArrayList<CustomNode> addChildrenToList(Node n, ArrayList<CustomNode> list)
	{	
		List<Node> childList;
		ArrayList<Node> parents = null;
		int lastNIndex=0;
		
		if(n.getChildCount()!=0)									//Checking whether the node has any further children
		{
			childList = n.getChildren();
			Iterator<Node> iter = childList.iterator();
			
			for(int i=0; i<childList.size(); i++)
			{	
				Node childN = childList.get(i);
				String localName = childN.getLocalName();
				parents = new ArrayList<Node>();
				parents = addParentstoList(childN, parents);
				
				CustomNode cn = new CustomNode(childN,parents,localName,childN.getDepth());
				list.add(cn);
				
				System.out.println("Node added currently to the list: "+cn.localName);
				
				if(childN.getChildCount()!=0)
				{
					lastNIndex = i;
					addChildrenToList(childN, list);	
				}
			}
			
		}
		else
		{
			/*parents = new ArrayList<Node>();
			parents = addParentstoList(n, parents);
			
			CustomNode cn = new CustomNode(n,parents,n.getLocalName(),n.getDepth());
			list.add(cn);*/
		}
		
		/*System.out.println("Current nodes in the Required List");
		for(int j=0; j<list.size(); j++)
		{
			System.out.println("Node :"+list.get(j).localName+" at depth "+list.get(j).depth);
		}
		System.out.println(" ");*/
		return list;
	}
	public Model createModelfrmBlock(ArrayList<CustomNode> block)
	{	
		Model m = null;
		//ModelFactory m_factory;
		
		com.hp.hpl.jena.graph.Node s,p,o;
		Triple t;
		for(int i=0; i<block.size(); i++)
		{	
			String uri = block.get(i).n.getUri();
			s = com.hp.hpl.jena.graph.Node.createURI(uri);
			p = com.hp.hpl.jena.graph.Node.createURI("");
			o = com.hp.hpl.jena.graph.Node.createURI("");
			
			t = Triple.create(s, p, o);
			m.add(m.asStatement(t));
			
		}
		
		NodeIterator n_iter = m.listObjects();
		//System.out.println("Size of Node Iterator="+n_iter.);
		while(n_iter.hasNext())
		{	
			System.out.println("URI of Object:"+n_iter.next().asNode().getURI());
			System.out.println("Object in the created Model:"+n_iter.next().asNode().getLocalName());
		}
		return m;
	}
	
	public int caluculateAnchors(ArrayList<CustomNode> sourceBlock,ArrayList<CustomNode> targetBlock,SimilarityMatrix m)
	{
		int anchors = 0;
		for(int i=0;i<sourceBlock.size();i++)
			for(int j=0;j<targetBlock.size();j++)
				  if(m.get(sourceBlock.get(i).n.getIndex(),targetBlock.get(j).n.getIndex()).getSimilarity()>=threshold)
					  anchors++;
					  
		return anchors;
		
		
	}
	
	public AncestorTracker[][] constructAncestorMatrix(ArrayList<CustomNode> conceptList)
    {        
            AncestorTracker[][] ancestorMatrix = new AncestorTracker[conceptList.size()][conceptList.size()];
            
            for(int i=0; i<conceptList.size(); i++)
                    for(int j=0; j<conceptList.size(); j++)
                    {
                            ancestorMatrix[i][j] = new AncestorTracker(conceptList.get(i), conceptList.get(j));
                            
                    }
            
            return ancestorMatrix;
    }
	
	public CouplingTracker[][] constructCouplingTracker(ArrayList<ArrayList<CustomNode>> blockList)
    {        
            CouplingTracker[][] couplingMatrix = new CouplingTracker[blockList.size()][blockList.size()];
            
            for(int i=0; i<blockList.size(); i++)
                    for(int j=0; j<blockList.size(); j++)
                    {
                            couplingMatrix[i][j] = new CouplingTracker(blockList.get(i), blockList.get(j));
                            
                    }
            
            return couplingMatrix;
    }
	
	CouplingTracker[][] updateCouplingTracker(ArrayList<ArrayList<CustomNode>> blockList,CouplingTracker[][] oldTracker,int index, int indexOfCoup)
    {        
            CouplingTracker[][] tempCouplingMatrix = new CouplingTracker[blockList.size()-1][blockList.size()-1];
            if(index<indexOfCoup)
            for(int i=0; i<blockList.size()-1; i++)
                    for(int j=0; j<blockList.size()-1; j++)
                    {
                    	   if(i<indexOfCoup&&j<indexOfCoup)
                                tempCouplingMatrix[i][j] = oldTracker[i][j];
                    	   
                    	  if(i<indexOfCoup&&j>indexOfCoup)
                               tempCouplingMatrix[i][j-1] = oldTracker[i][j];
                    	  
                    	  if(i>indexOfCoup&&j>indexOfCoup)
                              tempCouplingMatrix[i-1][j-1] = oldTracker[i][j];
                    	  
                    	  if(i>indexOfCoup&&j<indexOfCoup)
                              tempCouplingMatrix[i-1][j] = oldTracker[i][j];
                             
                    }
            
            else
            	
            	for(int i=0; i<blockList.size()-1; i++)
                    for(int j=0; j<blockList.size()-1; j++)
                    {
                    	   if(i<index&&j<index)
                                tempCouplingMatrix[i][j] = oldTracker[i][j];
                    	   
                    	  if(i<index&&j>index)
                               tempCouplingMatrix[i][j-1] = oldTracker[i][j];
                    	  
                    	  if(i>index&&j>index)
                              tempCouplingMatrix[i-1][j-1] = oldTracker[i][j];
                    	  
                    	  if(i>index&&j<index)
                              tempCouplingMatrix[i-1][j] = oldTracker[i][j];
                             
                    }
            
            if(index<indexOfCoup)
            {
                for(int j=0; j<blockList.size()-1; j++)
                {
                	
                	tempCouplingMatrix[index][j] = new CouplingTracker(blockList.get(index), blockList.get(j));
                }
            
            for(int i=0; i<blockList.size()-1; i++)
                
                {
                	
            	tempCouplingMatrix[i][index] = new CouplingTracker(blockList.get(i), blockList.get(index));
                }
            	
            }
            else
            {
                for(int j=0; j<blockList.size()-1; j++)
                {
                	
                	tempCouplingMatrix[indexOfCoup][j] = new CouplingTracker(blockList.get(indexOfCoup), blockList.get(j));
                }
            
            for(int i=0; i<blockList.size()-1; i++)
                
                {
                	
            	tempCouplingMatrix[i][indexOfCoup] = new CouplingTracker(blockList.get(i), blockList.get(indexOfCoup));
                }
            	
            }
            
            
            
            if(index<indexOfCoup)
            {
                for(int j=0; j<blockList.size()-1; j++)
                {
                	
                	tempCouplingMatrix[indexOfCoup][j] = new CouplingTracker(blockList.get(indexOfCoup), blockList.get(j));
                }
            
            for(int i=0; i<blockList.size()-1; i++)
                
                {
                	
            	tempCouplingMatrix[i][indexOfCoup] = new CouplingTracker(blockList.get(i), blockList.get(indexOfCoup));
                }
            	
            }
            else
            {
                for(int j=0; j<blockList.size()-1; j++)
                {
                	
                	tempCouplingMatrix[index][j] = new CouplingTracker(blockList.get(index), blockList.get(j));
                }
            
            for(int i=0; i<blockList.size()-1; i++)
                
                {
                	
            	tempCouplingMatrix[i][index] = new CouplingTracker(blockList.get(i), blockList.get(index));
                }
            	
            }
            return tempCouplingMatrix;
    }
	
	
	
	
	
	@SuppressWarnings({ "null", "unused" })
	public static void main(String[] args) throws Exception {
		long seconds = System.nanoTime();
		@SuppressWarnings("unused")
		String ontology = "C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Anatomy Track\\mouse_anatomy_2010.owl";
		String ontology1 = "C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Anatomy Track\\nci_anatomy_2010.owl";
		
	Ontology source = OntoTreeBuilder.loadOWLOntology(ontology); // update the path to your own
	Ontology target = OntoTreeBuilder.loadOWLOntology(ontology1); // update the path to your own

		
	//	DisplayOntMappings.openOntology("C:\\Academic\\Ist Semester\\Data and Web Semantics\\BigOntolgies\\oaei2012_FMA_whole_ontology.owl");
	//	DisplayOntMappings.openOntology("C:\\Academic\\Ist Semester\\Data and Web Semantics\\BigOntolgies\\oaei2012_NCI_whole_ontology.owl");
		
		//OntoProcessing.traverseOntology(ontology1);
		
		//For Testing purpose
		ArrayList<CustomNode> testList = testOntoScalability(source);
		
		OntoProcessing testObj = new OntoProcessing();
		ArrayList<ArrayList<CustomNode>> sourceBlocks = testObj.createBlocks(testList);
		
		
        ArrayList<CustomNode> testList2 = testOntoScalability(target);
		
        int size1 = testList.size();
        int size2 = testList2.size();
        CustomNode c1 = null;
        CustomNode c2 = null;
        Node n;
        for(int i=0;i<size1;i++)
			for(int j=0;j<size2;j++)
			{
				c1 = testList.get(i);
				c2 = testList.get(j);
				if(Math.abs(c1.depth-c2.depth)<=1)
					n = getCommonAncestor(c1,c2);
				
			}
				
		OntoProcessing testObj2 = new OntoProcessing();
		ArrayList<ArrayList<CustomNode>> targetBlocks = testObj.createBlocks(testList2);
		SimilarityComputer s = new SimilarityComputer();
		s.setSourceOntology(source);
     	s.setTargetOntology(target);
		s.buildSimilarityMatrices();
		SimilarityMatrix m = s.getClassesMatrix();
		SimilarityMatrix finalMatrix = m;
		for(int i=0;i<m.getRows();i++)
			for(int j=0;j<m.getColumns();j++)
				finalMatrix.get(i,j).setSimilarity(0.0);
		OntoProcessing processingObj = new  OntoProcessing();
		double proximity;
		ArrayList<ArrayList<ArrayList<CustomNode>>> finalList = new  ArrayList<ArrayList<ArrayList<CustomNode>>>();
		ArrayList<ArrayList<CustomNode>> finalTargetList = new  ArrayList<ArrayList<CustomNode>>();
		for(int i=0;i<sourceBlocks.size();i++)
			for(int j=0;j<targetBlocks.size();j++)
			{	
	            proximity = caluculateProiximity(sourceBlocks.get(i),targetBlocks.get(j),sourceBlocks,targetBlocks,m);
	            System.out.println(processingObj.caluculateAnchors(sourceBlocks.get(i),targetBlocks.get(j),m)+" Proximity "+proximity);
	            if(proximity>proximityThreshold)
	            {
	            	SimilarityMatrix m1 = alignBlocks(sourceBlocks.get(i),targetBlocks.get(j));
	            	mergeSimilarity(finalMatrix,m1);
	            }
	            	
			}
		for(int k=0;k<finalMatrix.getRows();k++)
		{    
			
		    double max=0;
		    double index=0;
		    double sim = 0;
			for(int l=0;l<finalMatrix.getColumns();l++)
			{
			
				{  sim = finalMatrix.get(k, l).getSimilarity();
				if(sim>max)
				{max = sim; index = l;
				}
			}
					 
					     
		    }
				
				
		
		Alignment<Mapping> al = s.scanMatrix(finalMatrix);
		s.matchStart();
		s.setClassesAlignmentSet(al);
		s.matchEnd();
	
		seconds = System.nanoTime() - seconds;
		
		System.out.println(seconds);
		
		
				
			
		
		}		
	}



	public static void mergeSimilarity(SimilarityMatrix finalMatrix,
			SimilarityMatrix m1) {
		Mapping map =null;
		int sourceIndex;
		int targetIndex;
		for(int i=0;i<m1.getRows();i++)
			for(int j=0;j<m1.getColumns();j++)
				{
				 map = finalMatrix.get(i, j);
				 sourceIndex = map.getEntity1().getIndex();
				 targetIndex = map.getEntity2().getIndex();
				 
				 finalMatrix.get(sourceIndex, targetIndex).setSimilarity(map.getSimilarity());
				 
			
				}
		
	}

	@SuppressWarnings("deprecation")
	public static SimilarityMatrix alignBlocks(ArrayList<CustomNode> arrayList,
			ArrayList<CustomNode> arrayList2) throws Exception {
		OntoProcessing o = new OntoProcessing();
		Ontology o1 = o.createOntology(arrayList);
		Ontology o2 = o.createOntology(arrayList2);
		AdvancedSimilarityParameters ap = new AdvancedSimilarityParameters();
		ap.useLabels  = true;
		AdvancedSimilarityMatcher a = new AdvancedSimilarityMatcher();
		a.setParam(ap);
		a.setSourceOntology(o1);
		a.setTargetOntology(o2);
		
		a.buildSimilarityMatrices();
		
		
		
		return a.getClassesMatrix();
		
		
	}

	public static double caluculateProiximity(ArrayList<CustomNode> arrayList,
			ArrayList<CustomNode> arrayList2,
			ArrayList<ArrayList<CustomNode>> sourceBlocks,
			ArrayList<ArrayList<CustomNode>> targetBlocks, SimilarityMatrix m) {
		    OntoProcessing processingObj = new  OntoProcessing();
		    int sharedAnchors = processingObj.caluculateAnchors(arrayList,arrayList2,m);
		    int anchorsWithtarget=0,anchorsWithSource=0;
		    
		    for(int j=0;j<targetBlocks.size();j++)
		    	anchorsWithtarget += processingObj.caluculateAnchors(arrayList,targetBlocks.get(j),m);
		    for(int i=0;i<targetBlocks.size();i++)
		         anchorsWithSource += processingObj.caluculateAnchors(sourceBlocks.get(i),arrayList2,m);
		    
		    return (double)(2*sharedAnchors)/(double)(anchorsWithtarget+anchorsWithSource);
	}
	
	public  Ontology createOntology(ArrayList<CustomNode> a)
	{
		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		
		
		for(int i=0;i<a.size();i++)
		{
			OntClass c = model.createClass(a.get(i).n.getUri());
			c.setLabel(a.get(i).n.getLocalName(),"EN");
			
		}
		Ontology o = new Ontology();
		o.setModel(model);
		return o;
		
	}
	
}		    
		    
	
