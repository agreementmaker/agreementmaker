package am.matcher.oaei2009;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;

public class OAEI2009parameters extends DefaultMatcherParameters {
	
	private static final long serialVersionUID = -8100658976837894464L;
	
	public final static String BENCHMARKS = "benchmarks";
	public final static String ANATOMY = "anatomy";
	public final static String CONFERENCE = "conference";
	public final static String CONFERENCE_EXTENDED = "conference_extended";	
	public final static String ANATOMY_PRA = "Anatomy with PRA";
	public final static String ANATOMY_PRI = "PraIntegration";
	public final static String WordnetNoUMLS = "WordNet and no UMLS";
	public final static String BENCHMARKS_303_PRA = "Benchmarks 303 with PRA";
	
	public boolean useWordNet = false;
	public boolean useUMLS = true;
	public String partialReferenceFile = null;
	public String format = "";
	public String trackName = ""; // the name of the track that this OAEI2009 matcher is running on.
	public boolean runningFromCommandline = false;
	
	public OAEI2009parameters(){}
	
	public OAEI2009parameters(String track) {
		trackName = track;
		if(track.equals(ANATOMY)){
			useWordNet = false;
			useUMLS = true;
			partialReferenceFile = "";
			format = "";
		}
		else if( track.equals( ANATOMY_PRI ) ) {
			// this is Flavio's Anatomy PRI.
			useWordNet = false;
			useUMLS = true;
			if( partialReferenceFile == null ) 
				throw new RuntimeException("You need to set the partial reference file.");
			format = ReferenceAlignmentMatcher.OAEI;
		}
		// this is Angela's Track!
		else if(track.equals(ANATOMY_PRA)){
			useWordNet = false;
			useUMLS = true;
			if( partialReferenceFile == null ) 
				throw new RuntimeException("You need to set the partial reference file.");
			format = ReferenceAlignmentMatcher.OAEI;
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
			if( partialReferenceFile == null ) 
				throw new RuntimeException("You need to set the partial reference file.");
			format = ReferenceAlignmentMatcher.OAEI;
		}
	}

	
	
}
