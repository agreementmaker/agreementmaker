package am.app.ontology.profiling.ontologymetrics;

import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

public class Utility {
	public static int getClassDepth(OntClass cl, int i) {
		List<OntClass> parents = cl.listSuperClasses().toList();
		
		for (int j = 0; j < parents.size(); j++) {
			if(parents.get(j).isAnon()) continue;
			return getClassDepth(parents.get(j), i+1);
		}
		return i;
	}
	
	public static int getPropertyDepth(OntProperty pr, int i) {
		List<? extends OntProperty> parents = pr.listSuperProperties().toList();
		
		for (int j = 0; j < parents.size(); j++) {
			if(parents.get(j).isAnon()) continue;
			return getPropertyDepth(parents.get(j), i+1);
		}
		return i;
	}

}
