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
	ONTOLOGY_PROFILING_PROPERTY_ANNOTATION_MULTI_FIELDS;	// Supports profiling of PROPERTY ANNOTATION FIELDS (multiple fields). 
}
