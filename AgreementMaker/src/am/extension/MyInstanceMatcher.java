package am.extension;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.Instance;
import am.output.AlignmentOutput;

public class MyInstanceMatcher extends AbstractMatcher {

	int ambiguous;
	int noResult;
	int singleResult;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8278698313888419789L;

	@Override
	protected void beforeAlignOperations() throws Exception {
		// TODO Auto-generated method stub
		super.beforeAlignOperations();
	}
	
	@Override
	public MatchingPair alignInstanceCandidates(Instance sourceInstance,
			List<Instance> targetCandidates) throws Exception {
		
		//System.out.println("Source instance: " + sourceInstance );
		//System.out.println("Target instance list: " + targetCandidates );
		//System.out.println("");
		
		//progressDisplay.appendToReport(sourceInstance.toString() + "\n");
					
		int size = targetCandidates.size();
		if(size == 0) noResult++;
		else if(size == 1) singleResult++;
		else if(size > 1) ambiguous++;
		
		//System.out.println("SOURCE:" + sourceInstance);
		//System.out.println(targetCandidates);
		
		
		if(size == 1){
			Instance target = targetCandidates.get(0);
			MatchingPair pair = new MatchingPair(sourceInstance.getUri(), target.getUri(), 1.0, MappingRelation.EQUIVALENCE);
			return pair;
		}
		else{
			//returns a list of article URIs
			List<String> articles = sourceInstance.getProperty("article");
			if(articles == null) return null;
			
			Instance article;
			for (int i = 0; i < articles.size(); i++) {
				//Get the first of the list
				String articleURI = articles.get(i);
				article = sourceOntology.getInstances().getInstance(articleURI);
				System.out.println(article);
			}
			
		}
		return null;
	}
	
	@Override
	protected void afterAlignOperations() {
		super.afterAlignOperations();
		System.out.println("Ambiguous: " + ambiguous);
		System.out.println("No Results: " + noResult);
		System.out.println("Single result: " + singleResult);
		System.out.println("Total: " + (ambiguous + noResult + singleResult));
		
		System.out.println("Writing on file...");
		String output = alignmentsToOutput(instanceAlignmentSet);
		FileOutputStream fos;
		
		try {
			fos = new FileOutputStream("alignment.rdf");
			fos.write(output.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");

	}
	
	public String alignmentsToOutput(List<MatchingPair> mappings){
		AlignmentOutput ao = new AlignmentOutput(null);
		ao.stringNS();
        ao.stringStart("yes", "0", "11", "onto1", "onto2", "uri1", "uri2");
        
        for (int i = 0, n = mappings.size(); i < n; i++) {
            MatchingPair mapping = mappings.get(i);
            String e1 = mapping.sourceURI;
            String e2 = mapping.targetURI;
            String measure = Double.toString(1.0);
            ao.stringElement(e1, e2, measure);
        }
        
        ao.stringEnd();
        return ao.getString();
	}
	
}
