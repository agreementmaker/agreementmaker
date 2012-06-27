package am.tools.finder;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class FinderFrame extends JFrame {

	private static final long serialVersionUID = 7729229444529413362L;
	
	FinderPanel panel;
		
	public FinderFrame(Finder finder){
		super("Finder");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel = new FinderPanel(finder);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.CENTER);
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
