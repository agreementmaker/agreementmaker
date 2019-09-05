package am.extension.batchmode;

import java.io.File;
import java.io.PrintWriter;

import am.Utility;

public class TrackDispatcher {
	
	public final static String BENCHMARK = "benchmarks";
	public final static String ANATOMY = "anatomy";
	public final static String CONFERENCE = "conference";
	public final static String CONFERENCE_EXTENDED = "conference_extended";
	
	//decides which track has to be launched
	public static void dispatchTrack(String track, String subTrack){
		try{
			if(subTrack.equals("")){
				System.out.println("Running in batchMode on track = "+track);
			}
			else System.out.println("Running in batchMode on track = "+track+" and subTrack = "+subTrack);
			
			if(track.equalsIgnoreCase(BENCHMARK)){
				BenchmarkTrack bt = new BenchmarkTrack(subTrack);
				bt.launch();
			}
			else if(track.equalsIgnoreCase(ANATOMY)){
				AnatomyTrack at = new AnatomyTrack(subTrack);
				at.launch();
			}
			else if(track.equalsIgnoreCase(CONFERENCE)){
				//TODO
				ConferenceTrack ct = new ConferenceTrack(subTrack);
				ct.launch();
			}			
			else if(track.equalsIgnoreCase(CONFERENCE_EXTENDED)){
				//TODO
				ConferenceTrack ct = new ConferenceTrack(subTrack);
				ct.solveConflicts = true;
				ct.launch();
			}
			else{
				System.out.println("The selected track doesn't exist.\nTo run the UI remove the string argument from the running parameters.");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void printExecutionTime(long time, String fileName) throws Exception{
		System.out.println("Total execution time in h:m:s:ms: "+Utility.getFormattedTime(time));
		File timeFile = new File(fileName);
		PrintWriter ptime = new PrintWriter(timeFile);
		ptime.println("Total execution time in ms: "+time);
		ptime.println("Total execution time in h:m:s:ms: "+Utility.getFormattedTime(time));
		ptime.close();
		
	}
}
