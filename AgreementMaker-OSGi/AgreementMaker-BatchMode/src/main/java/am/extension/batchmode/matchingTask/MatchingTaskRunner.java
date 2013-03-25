package am.extension.batchmode.matchingTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import am.utility.RunTimer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class MatchingTaskRunner {

	public static final String DOCUMENTS_DIR = 
			"/home/cosmin/Documents/ADVIS/OAEI 2013/LargeBioMed_dataset_oaei2012/";
	
	public static void main(String argv[]) {

		try {

			File fXmlFile = new File( DOCUMENTS_DIR + File.separator + "matching_tasks.xml");
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());

			NodeList taskList = doc.getDocumentElement().getChildNodes();
			for( int i = 0; i < taskList.getLength(); i++ ) {
				Node currentNode = taskList.item(i);
				if( currentNode.getNodeName().equals("task") ) doTasks(taskList.item(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void doTasks(Node task) {
		
		NodeList subTasks = task.getChildNodes();

		List<Node> ontologyNodes = new LinkedList<Node>();
		
		for( int i = 0; i < subTasks.getLength(); i++ ) {
			Node childNode = subTasks.item(i);
			if( childNode.getNodeName().equals("task") ) {
				doTasks(childNode);
			}
			else if ( childNode.getNodeName().equals("ontology") ) {
				ontologyNodes.add(childNode);
			}
		}

		final Node taskNameNode = task.getAttributes().getNamedItem("name");
		final String taskName = taskNameNode.getTextContent().trim();
		
		if( ontologyNodes.size() == 2 ) {
			// process the ontology pair

			System.out.println("Starting task '" + taskName + "'");
			RunTimer timer = new RunTimer().resetAndStart();
			processPair(taskName, ontologyNodes.get(0), ontologyNodes.get(1));
			timer.stop();
			System.out.println("Task done: " + timer);
			
			gc();
			
		}
		else {
			System.out.println("Do not know how to handle this task: '" + taskName + "'");
		}
	}

	private static void processPair(String taskName, Node sNode, Node tNode) {
		final String sourceOnt = sNode.getTextContent().trim();
		final String targetOnt = tNode.getTextContent().trim();
		String sourceURI = "file:" + DOCUMENTS_DIR + File.separator + sourceOnt;
		String targetURI = "file:" + DOCUMENTS_DIR + File.separator + targetOnt;
		
		OntModel sm = loadOntology(sourceOnt, sourceURI);
		OntModel tm = loadOntology(targetOnt, targetURI);
		
		sm.close();
		tm.close();
	}

	private static OntModel loadOntology(String sourceOnt, String sourceURI) {
		System.out.println("\tLoading ontology: " + sourceOnt);
		
		RunTimer timer = new RunTimer().resetAndStart();
		
		OntModel sourceModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
		sourceModel.read( sourceURI, null, "RDF/XML" );
		
		long sourceModelSize = sourceModel.size();
		
		timer.stop();
		
		System.out.println("\tLoaded " + sourceModelSize + " triples in " + timer);
		
		return sourceModel;
	}
	

	/**
	    * This method guarantees that garbage collection is
	    * done unlike <code>{@link System#gc()}</code>
	    */
	   public static void gc() {
	     Object obj = new Object();
	     WeakReference<Object> ref = new WeakReference<Object>(obj);
	     obj = null;
	     while(ref.get() != null) {
	       System.gc();
	     }
	   }

}
