package am.batchMode;

public class TrackDispatcher {
	
	public final static String BENCHMARK = "benchmarks";
	public final static String ANATOMY = "anatomy";
	public final static String CONFERENCE = "conference";
	
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
				//TODO
			}
			else if(track.equalsIgnoreCase(CONFERENCE)){
				//TODO
			}
			else{
				System.out.println("The selected track doesn't exist.\nTo run the UI remove the string argument from the running parameters.");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		

		
	}
}
