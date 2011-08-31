package am.app.mappingEngine.oaei.oaei2011;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.oaei.OAEI_Track;

public class OAEI2011MatcherParameters extends AbstractParameters {

	// values for running different combination
	public boolean usingASM;
	public boolean usingPSM;
	public boolean usingVMM;
	public boolean usingLSM;  // Lexical Synonym Matcher
	public boolean usingLWC1;
	public boolean usingGFM;
	public boolean usingIISM;
	public boolean usingLCM;
	public boolean usingLWC2;

	public boolean lsmUseExtractedTermSyonyms;
	
	public OAEI_Track currentTrack;	
	
	OAEI2011MatcherParameters( OAEI_Track whichTrack ) { 
		super(); 
		initBooleansForOAEI2010(whichTrack);
		currentTrack = whichTrack;
	}
	
	/** *********************************** SUPPORT METHODS *********************************************/
	void initBooleansForOAEI2010( OAEI_Track whichTrack ){
		
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
			usingIISM = false;
			usingLCM = false;
			usingLWC2 = false;
		case AllMatchers:
		default:
			usingASM = true;
			usingPSM = true;
			usingVMM = true;
			usingLWC1 = true;
			usingGFM = true;
			usingIISM = true;
			usingLCM = true;
			usingLWC2 = true;
		
		}
		
	}
	
}
