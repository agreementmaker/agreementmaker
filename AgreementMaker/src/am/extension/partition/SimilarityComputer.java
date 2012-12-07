package am.extension.partition;
//import t.*;

import org.apache.log4j.Logger;

import simpack.measure.external.alignapi.Hamming;
import simpack.measure.external.alignapi.Jaro;
import simpack.measure.external.alignapi.JaroWinkler;
import simpack.measure.external.alignapi.NGram;
import simpack.measure.external.alignapi.NeedlemanWunch;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.extension.MyMatcher;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SimilarityComputer extends AbstractMatcher {
	
	public static SimilarityMatrix simMatrix = null;

	private static final long serialVersionUID = -3772449944360959530L;

	
	private static int ontology203 = 0;
	
	private static Logger log = Logger.getLogger(MyMatcher.class);

	
	public SimilarityComputer() {
		super();
		setName("My Matcher"); // change this to something else if you want
	}
    
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes, SimilarityMatrix matrix) throws Exception {

		
		
		/*
		 * int sourceIndex = source.getIndex()
		int targetIndex = target.getIndex();
		
		
		
		Mapping mapping = matrix.get(sourceIndex, targetIndex);*/
		/*double sim;
	    if(ontology203 == 0) sim = matchSimilarity(getLocalName(source), getLocalName(target) );
	    else 
	     sim = matchSimilarity(getLocalName(source), getLocalName(target) );
	    
	    BaseSimilarityMatcher  b = new BaseSimilarityMatcher();
	    */
	    
		/*Uncomment for synonymns lookup - 205*/
		/*Hyponymns h = new Hyponymns();
		String[] s = h.syn(source.getLocalName());
		double similarity,sim;
		
		for (int i = 0; i < s.length; i++)
		{similarity = matchSimilarity1(source.getLocalName(), target.getLocalName() );
		if(similarity>sim){
			sim = similarity;
		
		}*/
		/*Uncomment for synonymns lookup*/
		/*Till Here*/
		//log.info("Matching " + source.getLocalName() + " with " + target.getLocalName());
	    System.out.println(source.getIndex()+" "+target.getIndex()+" "+matrix.getRows()+" "+matrix.getColumns());
      /*  if(source.getIndex()==matrix.getRows()-1 &&target.getIndex()==matrix.getColumns()-1)
        	simMatrix  = matrix;*/
	    Levenshtein l;
		//return new Mapping(source, target, sim);
	    if(source.getLocalName().length()>target.getLocalName().length())
	    	 l = new Levenshtein(target.getLocalName(),source.getLocalName());
	    else
	    	 l = new Levenshtein(source.getLocalName(),target.getLocalName());
	    System.out.println((double)(l.getSimilarity())/(double)(source.getLocalName().length()+target.getLocalName().length()));
		return new Mapping(source, target,1.0 - (double)(l.getSimilarity())/(source.getLocalName().length()+target.getLocalName().length()));
        
	}
	
	/**
	 * Run MyMatcher on a set of ontologies and print out the Precision, Recall,
	 * and F-Measure as compared against the reference alignment.
	 * 
	 * FIXME: Change the path to the ontologies and the reference alignment according 
	 * to where you saved the Benchmarks dataset.
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		Ontology source = OntoTreeBuilder.loadOWLOntology("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\101\\onto.rdf"); // update the path to your own
		Ontology target = OntoTreeBuilder.loadOWLOntology("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\205\\onto.rdf"); // update the path to your own

		SimilarityComputer mm = new SimilarityComputer();

		mm.setSourceOntology(source);
		mm.setTargetOntology(target);

		/*AbstractParameters param = new AbstractParameters();
		param.threshold = .75;
		param.maxSourceAlign = 1;
		param.maxTargetAlign = 1;
        */
		//mm.setParam(param);

		try {
			mm.match();
		} catch (Exception e) {
			log.error("Caught exception when running MyMatcher.", e);
		}
        System.out.println("For 205\n====\n\n");
		// run the reference alignment evaluation and output it to the log4j logger
		//mm.referenceEvaluation("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\205\\refalign.rdf"); // update the path to your own

		

		Ontology source1= OntoTreeBuilder.loadOWLOntology("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\101\\onto.rdf"); // update the path to your own
		Ontology target1= OntoTreeBuilder.loadOWLOntology("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\203\\onto.rdf"); // update the path to your own

		ontology203 = 1;
	/*	param.threshold = .79;
		param.maxSourceAlign = 1;
		param.maxTargetAlign = 1;
*/
		//mm.setParam(param);

		try {
			mm.match();
		} catch (Exception e) {
			log.error("Caught exception when running MyMatcher.", e);
		}
        System.out.println("For 203\n====\n\n");
		// run the reference alignment evaluation and output it to the log4j logger
		//mm.referenceEvaluation("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\203\\refalign.rdf"); // update the path to your own

        ontology203 = 0;
		
		
		
		
		source = OntoTreeBuilder.loadOWLOntology("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\101\\onto.rdf"); // update the path to your own
		target = OntoTreeBuilder.loadOWLOntology("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\223\\onto.rdf"); // update the path to your own

		

		mm.setSourceOntology(source);
		mm.setTargetOntology(target);

		
		/*param.threshold = .8;
		param.maxSourceAlign = 1;
		param.maxTargetAlign = 1;*/

		//mm.setParam(param);

		try {
			mm.match();
		} catch (Exception e) {
			log.error("Caught exception when running MyMatcher.", e);
		}
        System.out.println("For 223\n=====\n\n\n");
		// run the reference alignment evaluation and output it to the log4j logger
		mm.referenceEvaluation("C:\\Users\\KRISHNA DAS\\Desktop\\AgreementMaker\\ontologies\\OAEI2010_OWL_RDF\\Benchmark Track\\223\\refalign.rdf"); // update the path to your own

		
	
	}

	
	public static SimilarityMatrix getSimilarityMatrix(String sourceOntologyPath,String targetOntologyPath)
	{
		Ontology source = OntoTreeBuilder.loadOWLOntology(sourceOntologyPath);
		Ontology target = OntoTreeBuilder.loadOWLOntology(targetOntologyPath);
        System.out.println(source);
        System.out.println(target);
		MyMatcher mm = new MyMatcher();

		mm.setSourceOntology(source);
		mm.setTargetOntology(target);

		

		try {
			mm.match();
		} catch (Exception e) {
			log.error("Caught exception when running MyMatcher.", e);
		}
		
		
		return simMatrix;
		
	}
	
	
	public void match() throws Exception {

		/*// setup the Ontologies
		if( sourceOntology == null ) {
			if( Core.getInstance().getSourceOntology() == null ) {
				// no source ontology defined or loaded
				throw new Exception("No source ontology is loaded!");
			} else {
				// the source Ontology is not defined, but a Source ontology is loaded in the Core. Use that.
				sourceOntology = Core.getInstance().getSourceOntology();
			}
		}

		if( targetOntology == null ) {
			if( Core.getInstance().getTargetOntology() == null ) {
				// no target ontology defined or loaded
				throw new Exception("No target ontology is loaded!");
			} else {
				// the target Ontology is not defined as part of this matcher, but a Target ontology is loaded in the Core.  Use that.
				targetOntology = Core.getInstance().getTargetOntology();
			}
		}
        */
		matchStart();
		//buildSimilarityMatrices(); // align()
		if(performSelection && !this.isCancelled() ){
			select();	
		}
        
	
		
		matchEnd();
		//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
		//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
	}
	
	
	/**
	 * Sample String Similarity.
	 */
	private static double matchSimilarity(String sourceString, String targetString ) {
		
		/*Hamming hammingSim = new Hamming(sourceString, targetString);
		hammingSim.calculate();
		double h =  hammingSim.getSimilarity().doubleValue();*/
		
		/*Levenshtein lSim = new Levenshtein(sourceString, targetString);
		lSim.calculate();
		double l =  lSim.getSimilarity().doubleValue();
        */
		Jaro jaroSim = new Jaro(sourceString, targetString);
		jaroSim.calculate();
		//double j =  hammingSim.getSimilarity().doubleValue();
		
		JaroWinkler jaroWSim = new JaroWinkler(sourceString, targetString);
		jaroWSim.calculate();
	    double jw =  jaroWSim.getSimilarity().doubleValue();
		//return ((h+l+j+jw)/4.0);
		
		NGram nSim = new NGram(sourceString, targetString);
		nSim.calculate();
	    double n =  nSim.getSimilarity().doubleValue();
	    
	    NeedlemanWunch nmSim = new NeedlemanWunch(sourceString, targetString);
		nmSim.calculate();
	    double nm =  nmSim.getSimilarity().doubleValue();
		
	    
	    return ((n+nm)/2.0);
		
		/*
		JaroWinkler jaroWSim = new JaroWinkler(sourceString, targetString);
		jaroWSim.calculate();
		double jw =  jaroWSim.getSimilarity().doubleValue();
		
		
		
		simpack.measure.external.alignapi.JaroWinkler
		simpack.measure.external.alignapi.Levenshtein
		simpack.measure.external.alignapi.NeedlemanWunch
		simpack.measure.external.alignapi.NGram
		simpack.measure.external.alignapi.SMOA
		simpack.measure.external.alignapi.SubStringSimilarity
		*/
	}
	
	
private static double matchSimilarity1(String sourceString, String targetString ) {
		
		Hamming hammingSim = new Hamming(sourceString, targetString);
		hammingSim.calculate();
		double h =  hammingSim.getSimilarity().doubleValue();
		
	    
	    return h;
		
		/*
		JaroWinkler jaroWSim = new JaroWinkler(sourceString, targetString);
		jaroWSim.calculate();
		double jw =  jaroWSim.getSimilarity().doubleValue();
		
		
		
		simpack.measure.external.alignapi.JaroWinkler
		simpack.measure.external.alignapi.Levenshtein
		simpack.measure.external.alignapi.NeedlemanWunch
		simpack.measure.external.alignapi.NGram
		simpack.measure.external.alignapi.SMOA
		simpack.measure.external.alignapi.SubStringSimilarity
		*/
	}
	

	/**
	 * Compute the Precision, Recall and F-Measure for MyMatcher given a
	 * reference alignment.
	 * 
	 * @param pathToReferenceAlignment
	 *            The path to the reference alignment file. To be safe, you
	 *            should provide the absolute file path.
	 * @throws Exception
	 *             If bad things happen.
	 */
	private void referenceEvaluation(String pathToReferenceAlignment) throws Exception {
		//Run the reference alignment matcher to get the list of mappings in the reference alignment file
		ReferenceAlignmentMatcher refMatcher = (ReferenceAlignmentMatcher) MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment,0);
		
		// these parameters are equivalent to the ones in the graphical interface
		ReferenceAlignmentParameters parameters = new ReferenceAlignmentParameters();
		parameters.fileName = pathToReferenceAlignment;
		parameters.format = ReferenceAlignmentMatcher.OAEI;
		parameters.onlyEquivalence = false;
		parameters.skipClasses = false;
		parameters.skipProperties = false;
		
		// When working with sub-superclass relations the cardinality is always ANY to ANY
		if(!parameters.onlyEquivalence){
			parameters.maxSourceAlign = AbstractMatcher.ANY_INT;
			parameters.maxTargetAlign = AbstractMatcher.ANY_INT;
		}
		
		refMatcher.setParam(parameters);

		// set the source and target ontologies
		refMatcher.setSourceOntology( getSourceOntology() );
		refMatcher.setTargetOntology( getTargetOntology() );
		
		// load the reference alignment
		refMatcher.match();


		Alignment<Mapping> referenceSet;
		if( refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned() ) {
			referenceSet = refMatcher.getAlignment(); //class + properties
		} else if( refMatcher.areClassesAligned() ) {
			referenceSet = refMatcher.getClassAlignmentSet();
		} else if( refMatcher.arePropertiesAligned() ) {
			referenceSet = refMatcher.getPropertyAlignmentSet();
		} else {
			// empty set? -- this should not happen
			referenceSet = new Alignment<Mapping>(Ontology.ID_NONE, Ontology.ID_NONE);
		}

		// the alignment which we will evaluate
		Alignment<Mapping> myAlignment;
		
		if( refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned() ) {
			myAlignment = getAlignment();
		} else if( refMatcher.areClassesAligned() ) {
			myAlignment = getClassAlignmentSet();
		} else if( refMatcher.arePropertiesAligned() ) {
			myAlignment = getPropertyAlignmentSet();
		} else {
			myAlignment = new Alignment<Mapping>(Ontology.ID_NONE,Ontology.ID_NONE); // empty
		}

		// use the ReferenceEvaluator to actually compute the metrics
		ReferenceEvaluationData rd = ReferenceEvaluator.compare(myAlignment, referenceSet);
		
		// optional
		setRefEvaluation(rd);
		
		// output the report
		StringBuilder report = new StringBuilder();
		report	.append("Reference Evaluation Complete\n\n")
				.append(getName())
				.append("\n\n")
				.append(rd.getReport())
				.append("\n");
		
		log.info(report);
		
		// use system out if you don't see the log4j output
		//System.out.println(report);
	}

	/**
	 * Use the Jena API to return the label of a node.
	 */
	
	private String getLabel(Node n) {
		
		// get the Jena Resource of this node
		Resource res = n.getResource();
		
		// get the label of this node (returns the whole triple which defines the label)
		Statement labelTriple = res.getProperty(RDFS.label);
		if( labelTriple == null ) {
			return null; // no label defined
		}
		
		// extract the object of the triple (which is the label)
		RDFNode object = labelTriple.getObject();
		Literal label = object.asLiteral();
		
		// get the string value of the literal
		return label.getString();
	}
	
	
}
