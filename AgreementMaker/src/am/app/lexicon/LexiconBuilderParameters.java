package am.app.lexicon;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Property;

public class LexiconBuilderParameters {

	public boolean sourceUseLocalname;
	public boolean targetUseLocalname;
	
	public List<Property> sourceSynonyms;
	public List<Property> sourceDefinitions;
	
	public List<Property> targetSynonyms;
	public List<Property> targetDefinitions;
	
}
