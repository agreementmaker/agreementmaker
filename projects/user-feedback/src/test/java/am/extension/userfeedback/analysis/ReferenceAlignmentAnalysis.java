package am.extension.userfeedback.analysis;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.extension.multiUserFeedback.MatchingTasks2014;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.utility.referenceAlignment.AlignmentUtilities;

import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

/**
 * Analyze the experiments for our UFL tests.
 * 
 * @author cosmin
 *
 */
public class ReferenceAlignmentAnalysis extends AnalysisBase {
	
	private static final Logger LOG = LogManager.getLogger(ReferenceAlignmentAnalysis.class);
	
	/** SEPARATOR */
	private static final String S = "\t";

	/**
	 * How many mappings in the reference alignments, and what kind of mappings are they?
	 */
	@Test
	@Ignore("fix org.apache.jena.riot.RiotNotFoundException")
	public void analyzeReferenceAlignments() {
		// setup a basic log4j configuration that logs to the console
		setupLogging();
	    
		// silence all the other loggers
		silenceNoisyLoggers(noisyLoggers);
		RDFDefaultErrorHandler.silent = true;
		
		LOG.info(" Analysis of the mappings in the various reference alignments.");
		LOG.info(" clsEQ  : Number of equivalent class mappings.");
		LOG.info(" clsSC  : Number of subclass/superclass mappings.");
		LOG.info(" clsO   : Number of class mappings other than equivalent, subclass, and superclass.");
		LOG.info(" clsTot : Total number of class mappings.");
		LOG.info(" propEQ : Number of equivalent properties mappings.");
		LOG.info(" propSC : Number of subproperty/superproperty mappings.");
		LOG.info(" propO  : Number of property mappings other than equivalent, subproperty, and superproperty.");
		LOG.info(" propTot: Total number of property mappings.");
		LOG.info(" totEq  : Total number of equivalent mappings.");
		LOG.info("");
		LOG.info(" Experiment\t\t\t" + 
					"clsEQ\tclsSC\tclsO\tclsTot\t|\t" +
					"propEQ\tpropSP\tpropO\tpropTot\t|\ttotEQ");
		
		for(MatchingTaskPreset p : MatchingTasks2014.paperTasks) {
			analyzeReferenceAlignment(p);
		}
	}
	
	private void analyzeReferenceAlignment(MatchingTaskPreset p) {
		Ontology sourceOnt = OntoTreeBuilder.loadOWLOntology(p.getSourceOntology());
		Ontology targetOnt = OntoTreeBuilder.loadOWLOntology(p.getTargetOntology());
		
		Alignment<Mapping> a = AlignmentUtilities.getOAEIAlignment(p.getReference(), sourceOnt, targetOnt);
		
		int classesTotalMappings = 0;
		int classesEquivalentMappings = 0;
		int classesSubClassMappings = 0;
		int classesOtherMappings = 0;
		
		int propertiesTotalMappings = 0;
		int propertiesEquivalentMappings = 0;
		int propertiesSubPropertyMappings = 0;
		int propertiesOtherMappings = 0;
		
		for(Mapping m : a) {
			if(m.getAlignmentType() == alignType.aligningClasses ) {
				classesTotalMappings++;
				switch( m.getRelation() ) {
				case EQUIVALENCE:
					classesEquivalentMappings++;
					break;
				case SUBCLASS:
				case SUPERCLASS:
					classesSubClassMappings++;
					break;
				default:
					classesOtherMappings++;
					break;
				}
			}
			else if(m.getAlignmentType() == alignType.aligningProperties ) {
				propertiesTotalMappings++;
				switch( m.getRelation() ) {
				case EQUIVALENCE:
					propertiesEquivalentMappings++;
					break;
				case SUBCLASS:
				case SUPERCLASS:
					propertiesSubPropertyMappings++;
					break;
				default:
					propertiesOtherMappings++;
					break;
				}
			}
		}
		
		Assert.assertEquals(a.size(), classesTotalMappings + propertiesTotalMappings);
		Assert.assertEquals(classesTotalMappings, classesEquivalentMappings + classesSubClassMappings + classesOtherMappings);
		Assert.assertEquals(propertiesTotalMappings, propertiesEquivalentMappings + propertiesSubPropertyMappings + propertiesOtherMappings);
		
		String initialTab = S;
		if( p.getName().length() <= "ConferenceEkawIasted".length() ) initialTab = S + S;

		LOG.info(p.getName() +
				initialTab + classesEquivalentMappings + S + classesSubClassMappings + S + classesOtherMappings + S + classesTotalMappings + S + "|" +
				S + propertiesEquivalentMappings + S + propertiesSubPropertyMappings + S + propertiesOtherMappings + S + propertiesTotalMappings + S + "|" +
				S + (classesEquivalentMappings + propertiesEquivalentMappings));
	}
	
}
