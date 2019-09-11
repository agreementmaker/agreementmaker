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

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.app.osgi.MatcherNotFoundException;
import am.parsing.AlignmentOutput;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * This class handles the align requests from the published Endpoint.
 * @author cosmin
 *
 */
public class SealsServer {

//	private AbstractMatcher matcher;
	
	private AbstractMatcher matcher;
	private MatchingProgressListener progressDisplay;
	
	private DefaultMatcherParameters parameters;
	
	private int BUFFERSIZE = 4096;
	
	public SealsServer( AbstractMatcher matcher, MatchingProgressListener pD, DefaultMatcherParameters params ) {
		this.matcher = matcher;
		this.progressDisplay = pD;
		this.parameters = params;
	}
	
	
	//@Override
	public String align(URI source, URI target) {

		// 0. Instantiate the matcher.
		
		AbstractMatcher m;
		try {
			m = MatcherFactory.getMatcherInstance(matcher.getClass());
		} catch (MatcherNotFoundException e1) {
			e1.printStackTrace();
			return e1.getMessage();
		}
		//m.setProgressDisplay(progressDisplay);
		m.setParam(parameters);
		
		
		// 1. Load ontologies.
		
		String sourceOntologyFilename = downloadFile(source);
		String targetOntologyFilename = downloadFile(target);
		
		if( sourceOntologyFilename == null || targetOntologyFilename == null ) {
			if( sourceOntologyFilename == null ) {
				progressDisplay.appendToReport("FATAL ERROR: The source ontology could not be saved to a file.\nURI: " + source + "\n");
			}
			if( targetOntologyFilename == null ) {
				progressDisplay.appendToReport("FATAL ERROR: The target ontology could not be saved to a file.\nURI: " + target + "\n");
			}
			return "";
		}
		
		progressDisplay.appendToReport("Loading source ontology. \n\tURI: " + source + "\n\tFile: " + sourceOntologyFilename + "\n");
		
		
		OntologyDefinition sourceOntDef = new OntologyDefinition(true, sourceOntologyFilename, OntologyLanguage.OWL, OntologySyntax.RDFXML);
		OntoTreeBuilder otb1 = new OntoTreeBuilder(sourceOntDef);
		
		progressDisplay.appendToReport("Building source Ontology().\n");
		otb1.build( OntoTreeBuilder.Profile.noReasoner );
		m.setSourceOntology( otb1.getOntology() );
		
		progressDisplay.appendToReport(otb1.getReport()+"\n");
		progressDisplay.appendToReport("Sucessfully loaded source ontology.\n");
		
		
		progressDisplay.appendToReport("Loading target ontology. URI: " + target + "\n\tFile: " + targetOntologyFilename + "\n");
		
		OntologyDefinition targetOntDef = new OntologyDefinition(true, targetOntologyFilename, OntologyLanguage.OWL, OntologySyntax.RDFXML);		
		OntoTreeBuilder otb2 = new OntoTreeBuilder(targetOntDef);
		
		progressDisplay.appendToReport("Building target Ontology().\n");
		otb2.build( OntoTreeBuilder.Profile.noReasoner );
		m.setTargetOntology( otb2.getOntology() );
		
		progressDisplay.appendToReport(otb2.getReport()+"\n");
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

		
		Alignment<Mapping> finalAlignments = m.getAlignment();
		
		progressDisplay.appendToReport( "Matching done. Found " + finalAlignments.size() + " mappings.");
		
		// 3. Parse the alignment set into OAEI format.
		AlignmentOutput output = new AlignmentOutput(finalAlignments);
		
		
		String sourceUri = source.toString();
		String targetUri = target.toString();
		String alignment = output.compileString(sourceUri, targetUri, sourceUri, targetUri);
		
		
		// debugging
		progressDisplay.appendToReport("\n\nFinal alignment: \n" + alignment + "\n");
		// 3. Return the parsed alignment.
		return alignment;
	}

	
	
	private String downloadFile( URI file ) {
		// save ontology to a file
		String ontologyFilename = null;
		
		try {
			 URL oURL = file.toURL();
			 URLConnection oURLConnection = oURL.openConnection();
			 
			 //String contentType = soURLConnection.getContentType();
			 //int contentLength = soURLConnection.getContentLength();
			 
			 
			 InputStream raw = oURLConnection.getInputStream();
			 InputStream in = new BufferedInputStream(raw);

			 File ontologyFile = File.createTempFile("agreementmaker", ".owl", null);
			 FileOutputStream out = new FileOutputStream(ontologyFile);
			 
			 
			 byte[] buffer = new byte[BUFFERSIZE];
			 int totalBytesRead = 0;
			 int currentBytesRead = 0;
			 
			 while ( (currentBytesRead = in.read(buffer, 0, BUFFERSIZE)) != -1) {
				 // read the file from the URI, by reading BUFFERSIZE bytes at a time.
				 totalBytesRead += currentBytesRead;
				 		 
				 out.write( Arrays.copyOf(buffer, currentBytesRead) ); // only write the number of bytes we actually read
				 
			 }
			 in.close();
			 out.flush();
			 out.close();

			 progressDisplay.appendToReport("Downloaded " + file + " (" + totalBytesRead + " bytes).\n");
			 
			 ontologyFile.deleteOnExit();
			 ontologyFilename = ontologyFile.getAbsolutePath();
			 
			 
		} catch (MalformedURLException e1) {
			progressDisplay.appendToReport("Ontology URI cannot be converted to a URL.\n");
			progressDisplay.appendToReport( e1.getMessage() + "\n" );
			e1.printStackTrace();
		} catch (IOException e) {
			progressDisplay.appendToReport("Cannot open ontology URL.\n");
			progressDisplay.appendToReport( e.getMessage() + "\n" );
			e.printStackTrace();
		}
		
		return ontologyFilename;
	}
}
