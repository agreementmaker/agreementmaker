package am.matcher.myMatcher;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;


public class MyMatcher  extends AbstractMatcher  {

	private static FileOutput writer;

	public MyMatcher(){
		super();
		
		setName("MyMatcher");
		setCategory(MatcherCategory.UNCATEGORIZED);		
	}
	
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes, SimilarityMatrix matrix) throws Exception 
	{

				StringSimilarity s=new StringSimilarity();
				double sim=0.0d;
				String sourceName=source.getLocalName();
				sourceName = Character.toLowerCase(sourceName.charAt(0)) + sourceName.substring(1); 
				String targetName=target.getLocalName();
				targetName = Character.toLowerCase(targetName.charAt(0)) + targetName.substring(1); 
				double score,max;
				
				if(sourceName.equalsIgnoreCase(targetName))
					sim=1.0d;
				
				else
				{
					int count=0;
					ArrayList<String> sparts=getSparts(sourceName);
					ArrayList<String> tparts=getTparts(targetName);
					//check last word of the two array list
					
					double lscore=s.similarity(sparts.get(sparts.size()-1), tparts.get(tparts.size()-1));
					if(lscore >0.8d && ((sparts.size()+tparts.size())==4||(sparts.size()+tparts.size())==3))
					{
						sim=0.6;
						
					}
					
					// else count the number of matching words
					else
					{
						
						for(String t:tparts)
						{
							for(String sp:sparts)
							{	
									score=s.similarity(sp, t);
									if(score>0.8)
									{
										count++;
										
									}
								
							}
						}
						//find the ratio of matching words
						float sizes=(sparts.size()+tparts.size());
						float y=(2*count)/sizes;
						if(y>0.7)
							sim=y;
					}
				}
				

					
					
					
					
				
					
/*				
*/				/*if(sparts.get(sparts.size()-1).equals((tparts).get(tparts.size()-1)))
				{
					sim=0.7d;
					
					
				}*/
					
					
			
				

					
				
						
					
				 
					
					return new Mapping(source, target, sim);
						
	}
	
	public ArrayList<String> getTparts(String targetName)
	{
		ArrayList<String> tparts=new ArrayList<String>();	
		String[] pt;
		if(targetName.contains("_"))
		{	
			String ts[]=targetName.toLowerCase().split("_");
			for(String x:ts)
			{
				tparts.add(x);
			}
		}
		else
			if(!targetName.equals(targetName.toLowerCase()))
			{
				pt=targetName.split("(?=\\p{Upper})");	
				for(String x:pt)
				{
					tparts.add(x.toLowerCase());
				}
			}
			else
			{
			
			tparts.add(targetName);
			}
		return tparts;
	}
	public ArrayList<String> getSparts(String sourceName)
	{
		ArrayList<String> sparts=new ArrayList<String>();
		String[] ps;
		if(sourceName.contains("_"))
		{
			String st[]=sourceName.toLowerCase().split("_");
			for(String x:st)
			{
				sparts.add(x);
			}
		}
		else
		{
			if(!sourceName.equals(sourceName.toLowerCase()))
			{
				ps=sourceName.split("(?=\\p{Upper})");	
				for(String x:ps)
				{
					sparts.add(x.toLowerCase());
				}
			}
			else
			{
			
			sparts.add(sourceName);
			}
		}
		return sparts;
	}


	private ArrayList<Double> referenceEvaluation(String pathToReferenceAlignment)
			throws Exception {

	
		// Run the reference alignment matcher to get the list of mappings in
		// the reference alignment file
		ReferenceAlignmentMatcher refMatcher = new ReferenceAlignmentMatcher();


		// these parameters are equivalent to the ones in the graphical
		// interface
		ReferenceAlignmentParameters parameters = new ReferenceAlignmentParameters();
		parameters.fileName = pathToReferenceAlignment;
		parameters.format = ReferenceAlignmentMatcher.OAEI;
		parameters.onlyEquivalence = false;
		parameters.skipClasses = false;
		parameters.skipProperties = false;
		refMatcher.setSourceOntology(this.getSourceOntology());
		refMatcher.setTargetOntology(this.getTargetOntology());

		// When working with sub-superclass relations the cardinality is always
		// ANY to ANY
		if (!parameters.onlyEquivalence) {
			parameters.maxSourceAlign = AbstractMatcher.ANY_INT;
			parameters.maxTargetAlign = AbstractMatcher.ANY_INT;
		}

		refMatcher.setParam(parameters);

		// load the reference alignment
		refMatcher.match();
		
		Alignment<Mapping> referenceSet;
		if (refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned()) {
			referenceSet = refMatcher.getAlignment(); // class + properties
		} else if (refMatcher.areClassesAligned()) {
			referenceSet = refMatcher.getClassAlignmentSet();
		} else if (refMatcher.arePropertiesAligned()) {
			referenceSet = refMatcher.getPropertyAlignmentSet();
		} else {
			// empty set? -- this should not happen
			referenceSet = new Alignment<Mapping>(Ontology.ID_NONE,
					Ontology.ID_NONE);
		}

		// the alignment which we will evaluate
		Alignment<Mapping> myAlignment;

		if (refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned()) {
			myAlignment = getAlignment();
		} else if (refMatcher.areClassesAligned()) {
			myAlignment = getClassAlignmentSet();
		} else if (refMatcher.arePropertiesAligned()) {
			myAlignment = getPropertyAlignmentSet();
		} else {
			myAlignment = new Alignment<Mapping>(Ontology.ID_NONE,
					Ontology.ID_NONE); // empty
		}

		// use the ReferenceEvaluator to actually compute the metrics
		ReferenceEvaluationData rd = ReferenceEvaluator.compare(myAlignment,
				referenceSet);
		int i,j;
		for (i=0;i<referenceSet.size();i++)
		{
			Boolean flag=false;
			for (j=0;j<myAlignment.size();j++)
			{
				if (referenceSet.get(i).equals(myAlignment.get(j)))
				{
					writer.write(referenceSet.get(i).toString(), myAlignment.get(j).toString());
					flag=true;
					myAlignment.remove(j);
				}
					
			}
			if (!flag)
				writer.write("Unmatched "+referenceSet.get(i).toString(),"");
				
		}
		
		for (j=0;j<myAlignment.size();j++)
		{
			writer.write("False positives "+myAlignment.get(j).toString(),"");
		}
		
		
		System.out.println();

		// optional
		setRefEvaluation(rd);

		// output the report
		StringBuilder report = new StringBuilder();
		report.append("Reference Evaluation Complete\n\n").append(getName())
				.append("\n\n").append(rd.getReport()).append("\n");
		
		
		double precision=rd.getPrecision();
		double recall=rd.getRecall();
		double fmeasure=rd.getFmeasure();
		
		ArrayList<Double> results=new ArrayList<Double>();
		results.add(precision);
		results.add(recall);
		results.add(fmeasure);
		
		return results;
		
		//log.info(report);
		
		// use system out if you don't see the log4j output
	//	System.out.println(report);

	}

	
	
	public static void main(String[] args) throws Exception {
	
		String ONTOLOGY_BASE_PATH ="conference_dataset/"; // Use your base path
		//String[] confs = {"cmt","conference","confOf","edas","ekaw","iasted","sigkdd"};
		String[] confs = {"conference","ekaw"};
	/*	 static String SOURCE_ONTOLOGY = "cmt";  // Change this for TESTING
		 static String TARGET_ONTOLOGY = "confOf";// Change this for TESTING
	*/
		MyMatcher mm = new MyMatcher();
		
		System.setProperty("wordnet.database.dir","wordnet-3.0/dict");
		double precision=0.0d;
		double recall=0.0d;
		double fmeasure=0.0d;
		int size=21;
		for(int i = 0; i < confs.length-1; i++)
		{
			for(int j = i+1; j < confs.length; j++)
			{
				Ontology source = OntoTreeBuilder.loadOWLOntology(ONTOLOGY_BASE_PATH + "/"+confs[i]+".owl");
				Ontology target = OntoTreeBuilder.loadOWLOntology(ONTOLOGY_BASE_PATH + "/"+confs[j]+".owl");
				
				OntologyDefinition def1=new OntologyDefinition(true, source.getURI(), null, null);
				OntologyDefinition def2=new OntologyDefinition(true, target.getURI(), null, null);
		
				def1.largeOntologyMode=false;
				source.setDefinition(def1);
				def2.largeOntologyMode=false;
				target.setDefinition(def2);
				ManualOntologyProfiler mop=new ManualOntologyProfiler(source, target);
				mm.setSourceOntology(source);
				mm.setTargetOntology(target);
		
				DefaultMatcherParameters param = new DefaultMatcherParameters();
		
		//Set your parameters
				param.threshold = 0.0;
				param.maxSourceAlign = 1;
				param.maxTargetAlign = 1;
			//	mm.setName(TARGET_ONTOLOGY);
				mm.setParameters(param);

				try {
					mm.match();			
			
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			writer=new FileOutput(confs[i]+"-"+confs[j]+".csv");
			writer.writeHeader();
			ArrayList<Double> results=	mm.referenceEvaluation(ONTOLOGY_BASE_PATH + confs[i]+"-"+confs[j]+".rdf");
			precision+=results.get(0);
			recall+=results.get(1);
			fmeasure+=results.get(2);
			writer.close();
			
			
			}
			
		}

		StringBuilder sb= new StringBuilder();
		
		/*precision/=size;
		recall/=size;
		fmeasure/=size;*/
		
		String pPercent = Utility.getOneDecimalPercentFromDouble(precision);
		String rPercent = Utility.getOneDecimalPercentFromDouble(recall);
		String fPercent = Utility.getOneDecimalPercentFromDouble(fmeasure);
		
		
		sb.append("Precision = Correct/Discovered: "+ pPercent+"\n");
		sb.append("Recall = Correct/Reference: "+ rPercent+"\n");
		sb.append("Fmeasure = 2(precision*recall)/(precision+recall): "+ fPercent+"\n");
	
		String report=sb.toString();
		System.out.println("Evaulation results:");
		System.out.println(report);

	}
	
 

	
}
