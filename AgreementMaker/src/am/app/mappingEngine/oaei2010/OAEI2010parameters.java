package am.app.mappingEngine.oaei2010;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.batchMode.AnatomyTrack;
import am.batchMode.BenchmarkTrack;
import am.batchMode.TrackDispatcher;

public class OAEI2010parameters extends AbstractParameters {
	
	public final static String ANATOMY = TrackDispatcher.ANATOMY;
	public final static String BENCHMARKS1 = TrackDispatcher.BENCHMARK;
	public final static String BENCHMARKS2 = "Benchmark_2";
	public final static String CONFERENCE1 = TrackDispatcher.CONFERENCE;
	public final static String CONFERENCE2 = "Conference_2";
	
	public boolean useWordNet = false;
	public boolean useUMLS = true;
	public String partialReferenceFile = "";
	public String format = "";
	public String trackName = ""; // the name of the track that this OAEI2009 matcher is running on.
	public boolean runningFromCommandline = false;
	
	private boolean usingASM;
	private boolean usingPSM;
	private boolean usingVMM;
	private boolean usingIM1;
	private boolean usingLWC1;
	private boolean usingGFM;
	private boolean usingFCM;
	private boolean usingIM2;
	private boolean usingLWC2;
	
	public OAEI2010parameters(){
		setAll();
	}
	
	/**
	 * OAEI2010parameters: set parameters according to the string
	 * just put to false the parameters you don't want
	 * use usingIM1(); or usingIM2(); or notUsingIM(); to deal with ulas algoritm
	 */
	public OAEI2010parameters(String track) {
		setAll();
		trackName = track;
		if(track.equals(TrackDispatcher.ANATOMY)){
		}
		else if(track.equals(TrackDispatcher.BENCHMARK)) {
			setUsingIM1();	
		}
		else if(track.equals(BENCHMARKS2)) {
			setUsingIM2();

		}
		else if(track.equals(TrackDispatcher.CONFERENCE)){
			usingVMM = false;
			usingFCM = false;
			notUsingIM();
			usingLWC2 = false;
		}
		else if(track.equals(CONFERENCE2)){
			usingVMM = false;
			notUsingIM();
		}
		else{
			
		}
	}

	/**
	 * setAll: sets all value to true and uses IM2
	 * @see usingIM1
	 * @see usingIM2
	 * Note: can use either IM1 or IM2, one method automatically exludes the other
	 * @author michele
	*/
	public void setAll(){
		usingASM = true;
		usingPSM = true;
		usingVMM = true;
		//setUsingIM1();
		
		usingLWC1 = true;
		
		usingGFM = true;
		usingFCM = true;
		setUsingIM2();
		
		usingLWC2 = true;
	}
	
	/**
	 * @return the usingASM
	 */
	public boolean isUsingASM() {
		return usingASM;
	}

	/**
	 * @param usingASM the usingASM to set
	 */
	public void setUsingASM(boolean usingASM) {
		this.usingASM = usingASM;
	}

	/**
	 * @return the usingPSM
	 */
	public boolean isUsingPSM() {
		return usingPSM;
	}

	/**
	 * @param usingPSM the usingPSM to set
	 */
	public void setUsingPSM(boolean usingPSM) {
		this.usingPSM = usingPSM;
	}

	/**
	 * @return the usingVMM
	 */
	public boolean isUsingVMM() {
		return usingVMM;
	}

	/**
	 * @param usingVMM the usingVMM to set
	 */
	public void setUsingVMM(boolean usingVMM) {
		this.usingVMM = usingVMM;
	}

	/**
	 * @return the usingIM1
	 */
	public boolean isUsingIM1() {
		return usingIM1;
	}

	/**
	 * setUsingIM1
	 * Note: can use either IM1 or IM2, one method automatically exludes the other
	 * @author michele
	*/
	private void setUsingIM1(){
		usingIM1 = true;
		usingIM2 = false;
	}

	/**
	 * @return the usingLWC1
	 */
	public boolean isUsingLWC1() {
		return usingLWC1;
	}

	/**
	 * @param usingLWC1 the usingLWC1 to set
	 */
	public void setUsingLWC1(boolean usingLWC1) {
		this.usingLWC1 = usingLWC1;
	}

	/**
	 * @return the usingGFM
	 */
	public boolean isUsingGFM() {
		return usingGFM;
	}

	/**
	 * @param usingGFM the usingGFM to set
	 */
	public void setUsingGFM(boolean usingGFM) {
		this.usingGFM = usingGFM;
	}

	/**
	 * @return the usingFCM
	 */
	public boolean isUsingFCM() {
		return usingFCM;
	}

	/**
	 * @param usingFCM the usingFCM to set
	 */
	public void setUsingFCM(boolean usingFCM) {
		this.usingFCM = usingFCM;
	}

	/**
	 * @return the usingIM2
	 */
	public boolean isUsingIM2() {
		return usingIM2;
	}

	/**
	 * setUsingIM2
	 * Note: can use either IM1 or IM2, one method automatically exludes the other
	 * @author michele
	*/
	private void setUsingIM2(){
		usingIM1 = false;
		usingIM2 = true;
	}

	/**
	 * @return the usingLWC2
	 */
	public boolean isUsingLWC2() {
		return usingLWC2;
	}

	/**
	 * @param usingLWC2 the usingLWC2 to set
	 */
	public void setUsingLWC2(boolean usingLWC2) {
		this.usingLWC2 = usingLWC2;
	}
	
	/**
	 * notUsingIM
	 * @author michele
	*/
	private void notUsingIM(){
		usingIM1 = false;
		usingIM2 = false;
	}
	
}
