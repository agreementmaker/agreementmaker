package am.app.ontology.profiling.metrics.lexicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.metrics.AbstractOntologyMetric;
import am.utility.numeric.AvgMinMaxNumber;

public class LexiconStringSizeMetric extends AbstractOntologyMetric {

	public LexiconStringSizeMetric(Ontology o) {
		super(o);
	}

	private AvgMinMaxNumber synonymCount;
	private AvgMinMaxNumber synonymLength;
	
	@Override
	public void runMetric() {
		try {
			Lexicon ontLex = Core.getLexiconStore().getLexicon(ontology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);
			
			Collection<LexiconSynSet> synsets = ontLex.getSynSetMap().values();
			int[] synCount = new int[synsets.size()];
			double[] synStringSize = new double[synsets.size()]; 
			
			// for every synset in the lexicon:
			// 1. count the number of synonyms.
			// 2. compute the average string size of the synonyms.
			int i = 0;
			for( LexiconSynSet synset : synsets ) {
				Set<String> extendedSynList = ontLex.extendSynSet(synset);
				synCount[i] = extendedSynList.size(); // the number of synonyms
				
				int[] synonymStringLength = new int[extendedSynList.size()];
				int j = 0;
				for( String extendedSynonym : extendedSynList ) {
					synonymStringLength[j] = extendedSynonym.length();
					j++;
				}
				AvgMinMaxNumber number = new AvgMinMaxNumber(synonymStringLength);
				synStringSize[i] = number.average;  // average size of the synonyms for this synset
				i++;
			}
			
			synonymCount = new AvgMinMaxNumber("Synonym Count: ", synCount);
			synonymLength = new AvgMinMaxNumber("Synonym String Length: ", synStringSize);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<AvgMinMaxNumber> getResult() {
		List<AvgMinMaxNumber> retList = new ArrayList<AvgMinMaxNumber>();
		
		retList.add(synonymCount);
		retList.add(synonymLength);
		
		return retList;
	}
 
	
	
}
