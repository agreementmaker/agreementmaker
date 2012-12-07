package am.extension.partition;

import java.util.ArrayList;
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
import com.hp.hpl.jena.util.FileManager;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

public class OntoProcessingold {

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
		ArrayList<Node> parents2 = node2.ancestorList;
		Node sca = null;
		
		boolean chkFlag = false;
		//System.out.println("Common ancestor computation for "+node1.localName +" and "+node2.localName);
		//System.out.println("Node1 ancestor list size ="+parents1.size());
		//System.out.println("Node2 ancestor list size ="+parents2.size());
		
		for(int i=0; i<parents1.size(); i++)
		{
			for(int j=0; j<parents2.size(); j++)
			{
				if(parents1.get(i).equals(parents2.get(j)))
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
	
	public double calculateCohesivenesswithnBlock(ArrayList<CustomNode> cn)								//cn defines a block of concepts
    {
          double cohesive_val;
          double linkWeight = 0;
          CustomNode first;
          CustomNode second;

          for (int i=0;i<cn.size();i++)																	//For number of blocks created from original ontology
          {	  
        	  for(int j=0;j<cn.size();j++)																//For each element within that block
              {	
        		  if(Math.abs(cn.get(i).n.getDepth() - cn.get(j).n.getDepth()) <= 1)
        		  {
	        		  first = cn.get(i);
	                  second = cn.get(j);
	                  
	                  //System.out.println("Calculating link weight for "+first.localName +" and "+second.localName);
	                  linkWeight += 2*(getCommonAncestor(first,second).getDepth()+1)/(double)(first.n.getDepth()+1+second.n.getDepth()+1);
	                  //System.out.println("Linkweight after iteration #"+(j+1)+" is "+linkWeight );
        		  }

              }
          }
        	  
          cohesive_val = linkWeight/(cn.size()*cn.size());
          return cohesive_val;
    }


	public double calculateCoupling(ArrayList<CustomNode> cns1,ArrayList<CustomNode> cns2)
	{
	      double cohesive_val;
          double linkWeight = 0;
	      CustomNode first;
	      CustomNode second;

	      for (int i=0;i<cns1.size();i++)
	    	  for(int j=0;j<cns2.size();j++)
	          {
	    		  if(Math.abs(cns1.get(i).n.getDepth()-cns2.get(j).n.getDepth()) <= 1)
        		  {
		              first = cns1.get(i);
		              second = cns2.get(j);
		              
		              //System.out.println("Calculating link weight for "+first.localName +" and "+second.localName);
		              
		              //System.out.println("Nr :"+2*(getCommonAncestor(first,second).getDepth()));
		              //System.out.println("Dr :"+(first.n.getDepth())+(second.n.getDepth()));
		              double linkW = 2*(getCommonAncestor(first,second).getDepth()+1)/(double)(first.n.getDepth()+1+second.n.getDepth()+1);
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
	
	public void createBlocks(ArrayList<CustomNode> cns)
	{
		ArrayList<ArrayList<CustomNode>> a = new ArrayList<ArrayList<CustomNode>>(cns.size());
		ArrayList<CustomNode> cn ;
		for(int i=0;i<cns.size();i++)
		{
			cn = new ArrayList<CustomNode>();
			cn.add(cns.get(i));
			a.add(cn);		
		}
		
		mergeBlocks(a);
	}
	
	public void mergeBlocks(ArrayList<ArrayList<CustomNode>> a)
	{
		double coh = 0, temp = 0, coupling = 0;
		int index = 0, indexofcoup = 0;
		int size = a.size();
		int block_size = 1;
		int temp_blocksize = a.size();
		int count = 0;
		
		while(a.size() > .1*size)																//Restricting the no of elements to be added within a block
		{	
			if(temp_blocksize == a.size() && count!=0)
				break;
			System.out.println(a.size());
			temp_blocksize = a.size();
			
			for(int i=0;i<a.size();i++)
			{
				temp = calculateCohesivenesswithnBlock(a.get(i));
				System.out.println("Cohesive value returned from calCohesive fn :"+temp);
				//coh = temp>coh?temp:coh;
				System.out.println("Value of coh :"+coh);
				if(temp>coh)
				{	
					coh = temp;
					index=i;				    
				}	
				else 
					index=index;
				  
			}
			
			System.out.println("Value of index after calculating Cohesiveness of all the blocks="+index);
			
			//for(int i=index;i<a.size();i++)	
				for(int j=0;j<a.size();j++)
				{	
					
					    if(j==index) continue;
						temp = calculateCoupling(a.get(index), a.get(j));
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
			
			System.out.println("Value of indexofcoup after calculating Coupling of all the blocks="+indexofcoup);	
			
			a = merge(a,index,indexofcoup);
			a.remove(indexofcoup);
			
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
		for(int k=0;k<a.get(j).size();k++)
		{	
			//System.out.println(a.get(i));
			System.out.println(a.get(j).get(k));
			a.get(i).add(a.get(j).get(k));
		}
		
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
	
	public static ArrayList<CustomNode> testOntoScalability(String sourceOntFile)
	{
		Ontology onto = OntoTreeBuilder.loadOWLOntology(sourceOntFile);
		ArrayList<CustomNode> listofNodes = new ArrayList<CustomNode>();
		ArrayList<Node> parents = null;
		
		Node classRoot = onto.getClassesRoot();
		
		List<Node> list = classRoot.getChildren();										//Returns the list of nodes having depth 1
		
		for(Node n : list)
		{
			//System.out.println("Level 1 nodes returned :"+n.getLocalName() +"with Depth : "+n.getDepth());
			String localName = n.getLocalName();
			parents = new ArrayList<Node>();
			parents = addParentstoList(n, parents);
			
			CustomNode cn = new CustomNode(n,parents,localName,n.getDepth());
			listofNodes.add(cn);
			
			addChildrenToList(n,listofNodes);
		}
		
		int classCount = 1;
		for(CustomNode customNode : listofNodes)
		{	
			System.out.println("Element#"+classCount+" --> "+customNode.localName+ " with depth="+customNode.depth);
			System.out.println("\t With ancestors");
			
			for(int i=0; i<customNode.ancestorList.size(); i++)
			{	
				System.out.print(customNode.ancestorList.get(i).getLocalName()+",");
			}
			classCount++;
			System.out.println("\n");
		}
		
		
		
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String ontology = "C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Anatomy Track\\mouse_anatomy_2010.owl";
		String ontology1 = "C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\205\\onto.rdf";
		//OntoProcessing.traverseOntology(ontology1);
		
		//For Testing purpose
		ArrayList<CustomNode> testList = testOntoScalability(ontology1);
		
		OntoProcessing testObj = new OntoProcessing();
		testObj.createBlocks(testList);
	}

}
