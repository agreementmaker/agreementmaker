package am.userInterface.find;

public interface FindInterface {

	public void resetSearch();  // start the search from the beginning
	public boolean hasMoreStraw(); // does the haystack have more straws??
	public String getNextStraw();  // get the next thing for us to search
	public void displayCurrentStraw(); // we found the needle in the haystack (it is the current straw), so you must display it.
	
}
