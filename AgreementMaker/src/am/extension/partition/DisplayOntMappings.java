/**    ________________________________________
 * ___/ Copyright Notice / Warranty Disclaimer \________________
 *
 * @copyright { Copyright (c) 2010
 * Advances in Information Systems Laboratory at the
 * University of Illinois at Chicago
 * All Rights Reserved. }
 * 
 * @disclaimer {
 * This work is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. }
 * 
 *     _____________________
 * ___/ Authors / Changelog \___________________________________          
 * 
 *  
 */

package am.extension.partition;

import java.io.File;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import joptsimple.OptionSet;
import am.app.Core;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.TDBOntoTreeBuilder;

import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.osgi.AMHost;
import am.userInterface.UI;

/**
 * Main class -
 *
 * This class creates an instance of UI class
 *
 * @author ADVIS Research Laboratory
 * @version 11/27/2004
 */
public class DisplayOntMappings
{
	
	public static Ontology openOntology(String fileName){
		Ontology ontology = null;
		try {
			OntologyDefinition odef = new OntologyDefinition();
			odef.loadOntology = false;
			odef.loadInstances = true;
			odef.instanceSourceType = DatasetType.DATASET;
			odef.instanceSourceFormat = 0;
			odef.instanceSourceFile = fileName;
			
			TDBOntoTreeBuilder treeBuilder = new TDBOntoTreeBuilder(odef);
			
			treeBuilder.buildTreeNoReasoner();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	/**
	 * This is the application entry point.
	 * It instantiates the UI.
	 * 
	 * @param args Command line arguments, used for operating AgreementMaker in automatic mode, without a UI.
	 */
	public static void main(String args[])
	{	
		
		Thread mainUI = new Thread("AMStart") {
				public void run() {
					Core.setUI( new UI() );
					
					
					// OSGi
					AMHost host = new AMHost(new File(Core.getInstance().getRoot()));
					Core.getInstance().setFramework(host);
				}
		};
		
		mainUI.start();
		
	}
	
}

