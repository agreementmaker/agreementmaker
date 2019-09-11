package test;


public class testFlow {

	
	/**
	 * 
	 * the problem is that the matchercontrolpanel is in the PAGE_END PANEL of a borderlayout
	 * so buttons canno't be rearranged in souther line because there is no more souther space
	 * the UI shouldn't be orgranized with a BORDER LAYOUT
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestUI ui = new TestUI();
		ui.init();

	}

}
