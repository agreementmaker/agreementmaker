package am.app.mappingEngine.oaei2009;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.batchMode.AnatomyTrack;
import am.batchMode.BenchmarkTrack;
import am.batchMode.TrackDispatcher;

public class OAEI2009parameters extends AbstractParameters {
	
	public final static String BENCHMARKS = TrackDispatcher.BENCHMARK;
	public final static String ANATOMY = TrackDispatcher.ANATOMY;
	public final static String ANATOMY_PRA = "Anatomy with PRA";
	public final static String ANATOMY_PRI = "PraIntegration";
	public final static String CONFERENCE = TrackDispatcher.CONFERENCE;
	public final static String WordnetNoUMLS = "WordNet and no UMLS";
	public final static String BENCHMARKS_303_PRA = "Benchmarks 303 with PRA";
	
	public boolean useWordNet = false;
	public boolean useUMLS = true;
	public String partialReferenceFile = "";
	public String format = "";
	public String trackName = ""; // the name of the track that this OAEI2009 matcher is running on.
	public boolean runningFromCommandline = false;
	
	public OAEI2009parameters(){}
	
	public OAEI2009parameters(String track) {
		trackName = track;
		if(track.equals(TrackDispatcher.ANATOMY)){
			useWordNet = false;
			useUMLS = true;
			partialReferenceFile = "";
			format = "";
		}
		else if( track.equals( ANATOMY_PRI ) ) {
			// this is Flavio's Anatomy PRI.
			useWordNet = false;
			useUMLS = true;
			partialReferenceFile = AnatomyTrack.PARTIAL_REFERENCE;
			format = ReferenceAlignmentMatcher.REF0;
		
		}
		// this is Angela's Track!
		else if(track.equals(ANATOMY_PRA)){
			useWordNet = false;
			useUMLS = true;
			partialReferenceFile = AnatomyTrack.PARTIAL_REFERENCE;
			format = ReferenceAlignmentMatcher.REF0;
		}
		else if(track.equals(BENCHMARKS)){
			useWordNet = false;
			useUMLS = false;
			partialReferenceFile = "";
			format = "";
		}
		else if(track.equals(CONFERENCE)){
			useWordNet = false;
			useUMLS = false;
			partialReferenceFile = "";
			format = "";
		}
		else if(track.equals(WordnetNoUMLS)){
			useWordNet = true;
			useUMLS = false;
			partialReferenceFile = "";
			format = "";
		}
		else if(track.equals(BENCHMARKS_303_PRA)){
			useWordNet = false;
			useUMLS = false;
			partialReferenceFile = BenchmarkTrack.TRACK_INPUT_DIR+"303/refalign.rdf";
			format = ReferenceAlignmentMatcher.REF0;
		}
	}

	
	
}
