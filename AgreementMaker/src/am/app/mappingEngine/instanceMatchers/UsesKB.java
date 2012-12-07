package am.app.mappingEngine.instanceMatchers;

import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.LabeledDatasource;

/**
 * This interface is implemented by matchers which use the knowledge base in the
 * matching process. All the matchers have access to instances, but instances do
 * not contain a reference to the knowledge base they come from, so the KB must
 * be specified using this interface.
 * 
 * @author Federico Caimi
 */
public interface UsesKB {
	
	/**
	 * @param sourceKB If value is null, the source KB is cleared.
	 */
	public void setSourceKB(LabeledDatasource sourceKB);
	
	/**
	 * @param targetKB If value is null, the target KB is cleared.
	 */	
	public void setTargetKB(LabeledDatasource targetKB);
}
