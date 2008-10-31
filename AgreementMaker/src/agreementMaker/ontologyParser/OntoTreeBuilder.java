package agreementMaker.ontologyParser;

import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.jena.OWLReasoner;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.QNameProvider;

import agreementMaker.userInterface.vertex.Vertex;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * <p>Title: </p>
 *
 * <p>Description: Ontology to Tree Builder</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ADVIS LAB</p>
 *
 * @author Nalin Makar
 * @version 1.5
 */
public class OntoTreeBuilder extends TreeBuilder{
	
	static final long serialVersionUID = 1;
	//instance variables
	
	private OWLReasoner reasoner;
	private QNameProvider qnames;
	private OntModel ontModel;
	
	public OntoTreeBuilder(String fileName, int syntaxIndex) {
		
		treeCount = 1;
		reasoner = new OWLReasoner();
		
		String fileExt = "RDF/XML";
		if(syntaxIndex == 0){
			fileExt = "RDF/XML";
		}else if(syntaxIndex == 1){
			fileExt = "RDF/XML-ABBREV";
		}else if(syntaxIndex == 2){
			fileExt = "N-TRIPLE";
		}else if(syntaxIndex == 3){
			fileExt = "N3";
		}else if(syntaxIndex == 4){
			fileExt = "N3";
		}
		
		System.out.print("Reading Model...");
		ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		//TODO: Figure out if the 2nd arg in next fn call should be null or someother URI
		ontModel.read( "file:"+fileName, null, fileExt );
		System.out.println("done");
		
		//load the model to the reasoner
		System.out.print("Loading...");
		reasoner.load(ontModel);
		System.out.println("done");
		
		// compute the classification tree
		System.out.print("Classifying...");
		reasoner.classify();
		System.out.println("done");
		
		/*TODO: If we have local copies we can add them using m.getDocumentManager().addAltEntry("xxx")
		 * This will help if there is no internet connection
		 */
		
		qnames = new QNameProvider();
		
		//start with owl:Thing (and all its equivalent classes)
		Set set = reasoner.getEquivalentClasses(OWL.Thing);
		// equivalent classes function does not include the class 
		// itself so add it manually
		set.add(OWL.Thing);
		
		// create a tree starting with owl:Thing node as the root
		treeRoot = createNode(OWL.Thing);
		buildTree(treeRoot, set);
		
		// Find all unsatisfiable concepts, i.e classes equivalent
		// to owl:Nothing
		Set eqs = reasoner.getEquivalentClasses(OWL.Nothing);
		Iterator i = eqs.iterator();
		if (i.hasNext()) {			
			// We want to display every unsatisfiale concept as a 
			// different node in the tree
			Vertex nothing = createNode(OWL.Nothing);
			// iterate through unsatisfiable concepts and add them to
			// the tree
			while (i.hasNext()) {
				Resource sub = (Resource) i.next();
				
				if(sub != null){
					Vertex node = createNode(sub);				
					nothing.add(node);
					treeCount++;
				}
			}
			// add nothing as a child node to owl:Thing
			treeRoot.add(nothing);
		}
	}
	
	/**
	 * Create a root node for the given concepts and add child nodes for
	 * the subclasses. Return null for owl:Nothing 
	 * 
	 * @param concepts
	 * @return
	 */
	void buildTree(Vertex currentNode, Set concepts) {
		if (!concepts.contains(OWL.Nothing) && !concepts.isEmpty()){
			// every class in the set is equvalent so we can pick any one we want
			Resource c = (Resource) concepts.iterator().next();
			
			// get only direct subclasses  
			Set subs = reasoner.getSubClasses(c, true);
			// result is a set of sets. equivalent concepts are returned inside one set 
			Iterator i = subs.iterator();
			while (i.hasNext()) {
				Set set = (Set) i.next();
				
				if (!set.contains(OWL.Nothing) && !set.isEmpty()){
					String label = "";
					if(set.size() > 1)
						label+="[";
					
					Iterator ii = set.iterator();
					
					// get the first one and add it to the label
					Resource first = (Resource) ii.next();
					if(set.size() >1 )//|| first.equals(OWL.Thing))
						label += qnames.shortForm(first.getURI());
					else 
						label += first.getLocalName();
					
					// add the rest (if they exist)
					while (ii.hasNext()) {
						Resource cc = (Resource) ii.next();
						
						label += " = ";	
						label += qnames.shortForm(cc.getURI());
					}
					
					if (set.size() > 1)
						label += "]";
					
					Vertex node = createNode(label, first);
					treeCount++;
					
					currentNode.add(node);
					buildTree(node, set);
				}
			}
			
		}
	}
	
	/**
	 * Create a TreeNode for the given class
	 * 
	 * @param entity
	 * @param label
	 * @return
	 */
	Vertex createNode(String label, Resource entity) {
		return new Vertex(label, entity.getURI(), ontModel);
	}
	
	/**
	 * Create a TreeNode for the given class
	 * 
	 * @param entity
	 * @return
	 */
	Vertex createNode(Resource entity) {
		String label;
		 if(entity.canAs(OntClass.class))
				label=((OntClass)entity).getLocalName();
		else
			label= qnames.shortForm(entity.getURI());
		 
		return new Vertex(label, entity.getURI(), ontModel);
	}
	
}