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
 * @date July 29, 2010.  @author Cosmin.
 * @description Initial SEALS implementation.          
 * 
 *  
 */

package am.tools.seals;

import java.net.URI;

import javax.jws.WebService;

import eu.sealsproject.omt.ws.matcher.AlignmentWS;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;

/**
 * This class is in charge of publishing
 * @author cosmin
 *
 */
@WebService(endpointInterface="eu.sealsproject.omt.ws.matcher.AlignmentWS")
public class SealsServer implements AlignmentWS {

	private AbstractMatcher matcher;
	
	public SealsServer( AbstractMatcher m ) {
		matcher = m;
	}
	
	
	@Override
	public String align(URI source, URI target) {
		
		
		// 1. Load ontologies.
		matcher.getProgressDisplay().appendToReport("Source URI: " + source + "\n");
		
		
		matcher.getProgressDisplay().appendToReport("Target URI: " + target + "\n");
		
		// 2. Run the matcher.
		
		
		// 3. Parse the alignment set into OAEI format.
		
		// 3. Return the parsed alignment.
		String emptyAlignment = new String();
		emptyAlignment += "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		emptyAlignment += "<rdf:RDF xmlns=\"http://knowledgeweb.semanticweb.org/heterogeneity/alignment\"	 xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"	 xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">";
		
		emptyAlignment += "<Alignment>	<xml>yes</xml>		<level>0</level>		<type>??</type>";
		//emptyAlignment += "<map>	<Cell>			<entity1 rdf:resource=\"http://mouse.owl#MA_0001951\"/>			<entity2 rdf:resource=\"http://human.owl#NCI_C12715\"/>			<measure rdf:datatype=\"xsd:float\">0.9665265679359436</measure>			<relation>=</relation>		</Cell>	</map>";
		emptyAlignment += "</Alignment></rdf:RDF>";
		return emptyAlignment;
	}

}
