package am.extension.multiUserFeedback;

import am.extension.userfeedback.preset.MatchingTaskPreset;



/**
 * The matching tasks that we used for our UFL paper in 2014.
 * 
 * @author cosmin
 *
 */
public class MatchingTasks2014 {

	/* ************************** BENCHMARKS TRACK (30x) ********************** */
	
	public static final MatchingTaskPreset benchmarks301 =
			new MatchingTaskPreset(
					"Benchmarks301", 
					"OAEI/2013/benchmarks/101/onto.rdf",
					"OAEI/2013/benchmarks/301/onto.rdf",
					"OAEI/2013/benchmarks/301/refalign.rdf");
	
	public static final MatchingTaskPreset benchmarks302 =
			new MatchingTaskPreset(
					"Benchmarks302", 
					"OAEI/2013/benchmarks/101/onto.rdf",
					"OAEI/2013/benchmarks/302/onto.rdf",
					"OAEI/2013/benchmarks/302/refalign.rdf");
	
	public static final MatchingTaskPreset benchmarks303 =
			new MatchingTaskPreset(
					"Benchmarks303", 
					"OAEI/2013/benchmarks/101/onto.rdf",
					"OAEI/2013/benchmarks/303/onto.rdf",
					"OAEI/2013/benchmarks/303/refalign.rdf");
	
	public static final MatchingTaskPreset benchmarks304 =
			new MatchingTaskPreset(
					"Benchmarks304", 
					"OAEI/2013/benchmarks/101/onto.rdf",
					"OAEI/2013/benchmarks/304/onto.rdf",
					"OAEI/2013/benchmarks/304/refalign.rdf");
	
	/* ************************** CONFERENCE TRACK **************************** */
	
	public static final MatchingTaskPreset conferenceEdasIasted =
			new MatchingTaskPreset(
					"ConferenceEdasIasted",
					"OAEI/2013/conference/ontologies/edas.owl",
					"OAEI/2013/conference/ontologies/iasted.owl",
					"OAEI/2013/conference/referenceAlignments/edas-iasted.rdf");
	
	public static final MatchingTaskPreset conferenceEkawIasted =
			new MatchingTaskPreset(
					"ConferenceEkawIasted",
					"OAEI/2013/conference/ontologies/ekaw.owl",
					"OAEI/2013/conference/ontologies/iasted.owl",
					"OAEI/2013/conference/referenceAlignments/ekaw-iasted.rdf");
	
	public static final MatchingTaskPreset conferenceCmtEkaw =
			new MatchingTaskPreset(
					"ConferenceCmtEkaw",
					"OAEI/2013/conference/ontologies/cmt.owl",
					"OAEI/2013/conference/ontologies/ekaw.owl",
					"OAEI/2013/conference/referenceAlignments/cmt-ekaw.rdf");
	
	public static final MatchingTaskPreset conferenceConfOfEkaw =
			new MatchingTaskPreset(
					"ConferenceConfOfEkaw",
					"OAEI/2013/conference/ontologies/confOf.owl",
					"OAEI/2013/conference/ontologies/ekaw.owl",
					"OAEI/2013/conference/referenceAlignments/confOf-ekaw.rdf");
	
	
	
	public static final MatchingTaskPreset[] paperTasks = {
		benchmarks301, benchmarks302, benchmarks303, benchmarks304,
		conferenceEdasIasted, conferenceEkawIasted, conferenceCmtEkaw, conferenceConfOfEkaw
	};
}
