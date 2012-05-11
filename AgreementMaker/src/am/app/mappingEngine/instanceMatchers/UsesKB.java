package am.app.mappingEngine.instanceMatchers;

import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.LabeledDatasource;

/**
 * This interface is implemented by matchers which use the knowledge base in the matching process.
 * All the matchers have access to the instances, which do not contain a reference to the knowledge base
 * they come from. This is because it helps use keeping values in the instances separate from the
 * knowledge bases. 
 * 
 * 
 * @author Federico Caimi
 *
 */
public interface UsesKB {
	
	public void setSourceKB(LabeledDatasource sourceKB);
	public void setTargetKB(LabeledDatasource targetKB);
}
