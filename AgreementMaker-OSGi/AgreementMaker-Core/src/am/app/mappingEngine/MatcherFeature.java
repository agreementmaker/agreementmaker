package am.app.mappingEngine;

/**
 * This enumeration represents features supported by a matcher.
 * 
 * These are meant to be used with AbstractMatcher.supportsFeature(MatcherFeature)
 * in order to decide if certain features are supported by a matcher.
 * 
 * One of the first features is ontology profiling, meant to solve the problem 
 * of automatically matching heterogeneous fields.
 * 
 * @author cosmin
 * @date January 25, 2011
 *
 */
public enum MatcherFeature {
	ONTOLOGY_PROFILING,   		// Ontology Profiling general feature.
	ONTOLOGY_PROFILING_CLASS_ANNOTATION_FIELDS,				// Supports profiling of CLASS ANNOTATION FIELDS (one field).
	ONTOLOGY_PROFILING_PROPERTY_ANNOTATION_FIELDS,			// Supports profiling of PROPERTY ANNOTATION FIELDS (one field).
	ONTOLOGY_PROFILING_CLASS_ANNOTATION_MULTI_FIELDS,		// Supports profiling of CLASS ANNOTATION FIELDS (multiple fields).
	ONTOLOGY_PROFILING_PROPERTY_ANNOTATION_MULTI_FIELDS,	// Supports profiling of PROPERTY ANNOTATION FIELDS (multiple fields).
	
	MAPPING_PROVENANCE,										// Supports storing provenance information for mappings.
	
	THREADED_MODE,											// Supports running multiple invocations of alignTwoNodes(), but WITHOUT overlapping the source/target pairs.  See THREADED_OVERLAP if overlapping does not cause problems.
	THREADED_OVERLAP,										// Supports running multiple invocations of alignTwoNodes() with overlaps between the source/target pairs.  
															// Overlapping means that if alignTwoNodes() is currently matching source1 with target1, simultaneously invoking alignTwoNodes() to match source1 with target2 will overlap with the first invocation (because they're both matching source1 with another node). 
	
	EXPLANATATION_ENABLED,									// Whether semantic explanation will be enabled for the matcher
	; // THE END .. OR IS IT???
}
