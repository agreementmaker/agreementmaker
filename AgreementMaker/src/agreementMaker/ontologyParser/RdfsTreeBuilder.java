package agreementMaker.ontologyParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import agreementMaker.userInterface.vertex.Vertex;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

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
public class RdfsTreeBuilder extends TreeBuilder{
	
	static final long serialVersionUID = 1;
	//instance variables
	private OntModel ontModel;
	
	public RdfsTreeBuilder(String fileName, int syntaxIndex) {
		
		treeCount = 1;
		
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
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, null);
		//TODO: Figure out if the 2nd arg in next fn call should be null or someother URI
		ontModel.read( "file:"+fileName, "", fileExt );
		System.out.println("done");
		
		/*TODO: If we have local copies we can add them using m.getDocumentManager().addAltEntry("xxx")
		 * This will help if there is no internet connection
		 */

		treeRoot = new Vertex("rdfs:Resource", RDFS.getURI(), ontModel);//Creates the root of type Vertex for the tree
		
		Iterator i = ontModel.listHierarchyRootClasses();

		OntClass cls;
		while (i.hasNext()) {
			cls = (OntClass)i.next();
			Vertex newVertex = new Vertex(cls.getLocalName(), cls.getURI(), ontModel);
			treeRoot.add(newVertex);
			treeCount++;
			buildTree(newVertex, cls, new ArrayList(), ontModel); 
		}
		
	}
	
	private void buildTree(Vertex currentTreeNode, OntClass cls, List occurs, OntModel m) {
		
		if (cls.canAs( OntClass.class )  &&  !occurs.contains( cls )) {
			for (Iterator i = cls.listSubClasses( true );  i.hasNext(); ) {
				OntClass childNode = (OntClass) i.next();
				
				// we push this expression on the occurs list before we recurse
				occurs.add( cls );
				if (!childNode.isAnon()) {
					
					Vertex newNode = new Vertex(childNode.getLocalName(), childNode.getURI(), m);
					//adds the newly created node to the previous node
					currentTreeNode.add(newNode);
					//Unlike XmlTreeBuilder, Description for the node isnt set here. The call to do that is made in the vertex constructor itself
					//increment the number of nodes created
					treeCount++;
					//call the function itself to examine the next class and add it to the tree
					buildTree(newNode, childNode, occurs, m);
				}
				occurs.remove( cls );
			}
		}
		
	}
}