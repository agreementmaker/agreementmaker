package am.tools.AlignmentViewer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

public class AlignmentViewer extends JPanel {

	
	private Alignment<? extends Mapping> alignment;
	
	private JScrollPane scrollPane;
	private JTable table;
	
	
	public AlignmentViewer(Alignment<? extends Mapping> a) {
		alignment = a;
		
		
		
		table = new JTable();
		scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		
	}
	
	

	
	
}
