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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.jws.WebService;
import javax.swing.SwingWorker.StateValue;

import eu.sealsproject.omt.ws.matcher.AlignmentWS;

import am.GlobalStaticVariables;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.output.AlignmentOutput;
import am.userInterface.MatchingProgressDisplay;

/**
 * This class handles the align requests from the published Endpoint.
 * @author cosmin
 *
 */
@WebService(endpointInterface="eu.sealsproject.omt.ws.matcher.AlignmentWS")
public class SealsServer implements AlignmentWS {

//	private AbstractMatcher matcher;
	
	private MatchersRegistry matcherRegistry;
	private MatchingProgressDisplay progressDisplay;
	
	private double threshold;
	private int sourceRelations;
	private int targetRelations;
	private AbstractParameters parameters;
	
	public SealsServer( MatchersRegistry mR, MatchingProgressDisplay pD, double th, int sourceR, int targetR, AbstractParameters params ) {
		matcherRegistry = mR;
		progressDisplay = pD;
		threshold = th;
		sourceRelations = sourceR;
		targetRelations = targetR;
		parameters = params;
	}
	
	
	@Override
	public String align(URI source, URI target) {

		// 0. Instantiate the matcher.
		
		AbstractMatcher m = MatcherFactory.getMatcherInstance(matcherRegistry, Core.getInstance().getMatcherInstances().size());
		//m.setProgressDisplay(progressDisplay);
		m.setThreshold(threshold);
		m.setMaxSourceAlign(sourceRelations);
		m.setMaxTargetAlign(targetRelations);
		m.setParam(parameters);
		
		
		// 1. Load ontologies.
		
		String sourceOntologyFilename = downloadFile(source);
		String targetOntologyFilename = downloadFile(target);
		
		
		progressDisplay.appendToReport("Loading source ontology. \n\tURI: " + source + "\n\tFile: " + sourceOntologyFilename + "\n");
		
		
		OntoTreeBuilder otb1 = new OntoTreeBuilder(sourceOntologyFilename, GlobalStaticVariables.SOURCENODE, GlobalStaticVariables.LANG_OWL, "RDF/XML", false, false);
		
		progressDisplay.appendToReport("Building source Ontology().\n");
		otb1.build( OntoTreeBuilder.Profile.noReasoner );
		m.setSourceOntology( otb1.getOntology() );
		progressDisplay.appendToReport(otb1.getReport());
		progressDisplay.appendToReport("Sucessfully loaded source ontology.\n");
		
		
		progressDisplay.appendToReport("Loading target ontology. URI: " + target + "\n");
		
		OntoTreeBuilder otb2 = new OntoTreeBuilder( targetOntologyFilename, GlobalStaticVariables.TARGETNODE, GlobalStaticVariables.LANG_OWL, "RDF/XML", true, false);
		
		progressDisplay.appendToReport("Building target Ontology().\n");
		otb2.build( OntoTreeBuilder.Profile.noReasoner );
		m.setTargetOntology( otb2.getOntology() );
		progressDisplay.appendToReport("Sucessfully loaded target ontology.\n");
		
		// 2. Run the matcher.
		try {
			m.match();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			progressDisplay.appendToReport( e.getMessage() + "\n");
			progressDisplay.appendToReport("Exception has been thrown, stopping matcher.\n");
			m.cancel(true);
			return "";
		}
		
		
		// 3. Parse the alignment set into OAEI format.
		AlignmentOutput output = new AlignmentOutput(m.getAlignmentSet());
		String sourceUri = m.getSourceOntology().getURI();
		String targetUri = m.getTargetOntology().getURI();
		String alignment = output.compileString(sourceUri, targetUri, sourceUri, targetUri);
		
		
		// debugging
		progressDisplay.appendToReport(alignment + "\n");
		// 3. Return the parsed alignment.
		return alignment;
	}

	
	
	private String downloadFile( URI file ) {
		// save ontology to a file
		String sourceOntologyFilename = null;
		
		try {
			 URL soURL = file.toURL();
			 URLConnection soURLConnection = soURL.openConnection();
			 
			 String contentType = soURLConnection.getContentType();
			 int contentLength = soURLConnection.getContentLength();
			 
			 
			 InputStream raw = soURLConnection.getInputStream();
			 InputStream in = new BufferedInputStream(raw);
			 
			 byte[] data = new byte[contentLength];
			 int bytesRead = 0;
			 int offset = 0;
			 
			 while (offset < contentLength) {
				 bytesRead = in.read(data, offset, data.length - offset);
			     if (bytesRead == -1)
			        break;
			     offset += bytesRead;
			 }
			 in.close();

			 if (offset != contentLength) {
			      //throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
			 }

			 File sourceOntologyFile = File.createTempFile("agreementmaker", "owl", new File("/home/cosmin/Desktop/temp_seals_downloads"));
			 //sourceOntologyFile.deleteOnExit();
			 sourceOntologyFilename = sourceOntologyFile.getAbsolutePath();
			 FileOutputStream out = new FileOutputStream(sourceOntologyFile);
			 out.write(data);
			 out.flush();
			 out.close();
			 
		} catch (MalformedURLException e1) {
			progressDisplay.appendToReport("Source ontology URI cannot be converted to a URL.\n");
			progressDisplay.appendToReport( e1.getMessage() + "\n" );
			e1.printStackTrace();
		} catch (IOException e) {
			progressDisplay.appendToReport("Cannot open source ontology URL.\n");
			progressDisplay.appendToReport( e.getMessage() + "\n" );
			e.printStackTrace();
		}
		
		return sourceOntologyFilename;
	}
}
