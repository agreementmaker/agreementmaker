package am.tools.finder;

import javax.swing.JFrame;

public class FinderFrame extends JFrame {
	FinderPanel panel;
		
	public FinderFrame(Finder finder){
		super("Finder");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel = new FinderPanel(finder);
		add(panel);
		setSize(800, 600);
		setVisible(true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FinderFrame(new DBPediaFinder());
	}

}
