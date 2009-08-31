package am.application.mappingEngine.oaei2009;

import am.application.mappingEngine.AbstractParameters;
import am.application.mappingEngine.StringUtil.NormalizerParameter;
import am.batchMode.AnatomyTrack;
import am.batchMode.TrackDispatcher;

public class OAEI2009parameters extends AbstractParameters {
	
	public final static String BENCHMARKS = TrackDispatcher.BENCHMARK;
	public final static String ANATOMY = TrackDispatcher.ANATOMY;
	public final static String ANATOMY_PRA = "Anatomy with PRA";
	public final static String CONFERENCE = TrackDispatcher.CONFERENCE;
	public final static String WordnetNoUMLS = "WordNet and no UMLS";
	
	public boolean useWordNet = false;
	public boolean useUMLS = false;
	public String partialReferenceFile = "";
	
	public OAEI2009parameters(){}
	
	public OAEI2009parameters(String track) {
		if(track.equals(TrackDispatcher.ANATOMY)){
			useWordNet = false;
			useUMLS = true;
			partialReferenceFile = "";
		}
		else if(track.equals(ANATOMY_PRA)){
			useWordNet = false;
			useUMLS = true;
			partialReferenceFile = AnatomyTrack.PARTIAL_REFERENCE;
		}
		else if(track.equals(BENCHMARKS)){
			useWordNet = false;
			useUMLS = false;
			partialReferenceFile = "";
		}
		else if(track.equals(CONFERENCE)){
			useWordNet = true;
			useUMLS = false;
			partialReferenceFile = "";
		}
		else if(track.equals(WordnetNoUMLS)){
			useWordNet = true;
			useUMLS = false;
			partialReferenceFile = "";
		}
	}

	
	
}
