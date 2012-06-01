package am.userInterface.matchingtask;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import am.app.Core;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.OntologyProfilerPanel;
import am.utility.CenterPanel;

/**
 * Given two ontologies, create a matching task object which will
 * be used to match these ontologies.
 * 
 * @author Cosmin Stroe
 *
 */
public class MatchingTaskCreatorDialog extends JDialog {

	private static final long serialVersionUID = 6754123828376018512L;
	
	private JTabbedPane mainPane = new JTabbedPane();
	
	public MatchingTaskCreatorDialog(Ontology sourceOntology, Ontology targetOntology) {
		super();
		
		mainPane.addTab("Matching Task Overview", 
				new CenterPanel(new MatchingTaskOverviewPanel(sourceOntology, targetOntology)));
		mainPane.addTab("Matching Algorithm", new CenterPanel(new MatchingAlgorithmParametersPanel()));
		mainPane.addTab("Selection Algorithm", new SelectionAlgorithmParametersPanel());
		if( Core.getInstance().getOntologyProfiler() != null ) {
			mainPane.addTab("Annotation Profiling", 
				Core.getInstance().getOntologyProfiler().getProfilerPanel(false));
		}
		
		getContentPane().add(mainPane, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	
	
	
	public static void main(String[] args) {
		JDialog d = new MatchingTaskCreatorDialog(null, null);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setVisible(true);
	}
}

