package am.app.lexicon.ontology;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Property;

import am.app.lexicon.SubconceptSynonymLexiconBuilder;
import am.app.ontology.Ontology;

public class SCSLexiconBuilder 
	   extends OntologyLexiconBuilder 
	   implements SubconceptSynonymLexiconBuilder {

	public SCSLexiconBuilder(Ontology ont, boolean includeLN,
			List<Property> label, List<Property> synonym,
			List<Property> definition) {
		super(ont, includeLN, label, synonym, definition);
	}

	@Override
	public List<String> processSynonymList(List<String> synonymList) {
		// TODO Auto-generated method stub
		return null;
	}

}
