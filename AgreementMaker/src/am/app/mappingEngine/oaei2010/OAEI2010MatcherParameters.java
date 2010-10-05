package am.app.mappingEngine.oaei2010;

import am.app.mappingEngine.AbstractParameters;

public class OAEI2010MatcherParameters extends AbstractParameters {

	// values for running different combination
	public boolean usingASM;
	public boolean usingPSM;
	public boolean usingVMM;
	public boolean usingLSM;  // Lexical Synonym Matcher
	public boolean usingLWC1;
	public boolean usingGFM;
	public boolean usingFCM;
	public boolean usingLCM;
	public boolean usingLWC2;

	public Track currentTrack;
	
	public static enum Track {
		Anatomy,
		Benchmarks,
		Conference,
		AllMatchers;
	}
	
	
	OAEI2010MatcherParameters( Track whichTrack ) { 
		super(); 
		initBooleansForOAEI2010(whichTrack);
		currentTrack = whichTrack;
	}
	
	/** *********************************** SUPPORT METHODS *********************************************/
	void initBooleansForOAEI2010( Track whichTrack ){
		
		switch( whichTrack ) {
		case Anatomy:
			usingASM = false;
			usingPSM = true;
			usingVMM = true;
			usingLSM = true;
			usingLWC1 = true;
		case Benchmarks:
		case Conference:
			usingASM = true;
			usingPSM = true;
			usingVMM = false;
			usingLWC1 = true;
			usingGFM = true;
			usingFCM = false;
			usingLCM = false;
			usingLWC2 = false;
		case AllMatchers:
		default:
			usingASM = true;
			usingPSM = true;
			usingVMM = true;
			usingLWC1 = true;
			usingGFM = true;
			usingFCM = true;
			usingLCM = true;
			usingLWC2 = true;
		
		}
		
	}
	
}
