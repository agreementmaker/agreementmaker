package am.utility;

import java.util.Comparator;

import com.hp.hpl.jena.rdf.model.Resource;

public class LocalnameComparator implements Comparator<Resource> {

	@Override
	public int compare(Resource o1, Resource o2) {
		return String.CASE_INSENSITIVE_ORDER.compare(o1.getLocalName(), o2.getLocalName());
	}

}
