package am.userInterface.matchingtask;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;

import am.app.ontology.Ontology;

/**
 * A panel to display the matching task overview.
 * 
 * @author Cosmin Stroe
 *
 */
public class MatchingTaskOverviewPanel extends JPanel {

	private static final long serialVersionUID = 4468327991195412746L;

	protected OntologyDetailsPanel sourceOntDetails, targetOntDetails;

	private JLabel lblMatchingAlgorithm = new JLabel("Matching Algorithm:");
	private JLabel lblSelectionAlgorithm = new JLabel("Selection Algorithm:");
	
	private JLabel lblMatchingAlgorithmValue = new JLabel("None.");
	private JLabel lblSelectionAlgorithmValue = new JLabel("None.");
	
	public MatchingTaskOverviewPanel(Ontology sourceOntology, Ontology targetOntology) {
		super();
		
		sourceOntDetails = new OntologyDetailsPanel(sourceOntology);
		targetOntDetails = new OntologyDetailsPanel(targetOntology);
		
		sourceOntDetails.setBorder(new TitledBorder("Source Ontology:"));
		targetOntDetails.setBorder(new TitledBorder("Target Ontology:"));
		
		
		GroupLayout layout = new GroupLayout(this);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		
		layout.setHorizontalGroup( layout.createParallelGroup()
			.addGroup( Alignment.CENTER, layout.createSequentialGroup()
				.addComponent(sourceOntDetails)
				.addComponent(targetOntDetails)
			)
			.addGroup( Alignment.CENTER, layout.createSequentialGroup()
				.addComponent(lblMatchingAlgorithm)
				.addComponent(lblMatchingAlgorithmValue)
			)
			.addGroup( Alignment.CENTER, layout.createSequentialGroup()
				.addComponent(lblSelectionAlgorithm)
				.addComponent(lblSelectionAlgorithmValue)
			)
		);
			
		layout.setVerticalGroup( layout.createSequentialGroup()
			.addGroup( layout.createParallelGroup()
				.addComponent(sourceOntDetails)
				.addComponent(targetOntDetails)
			)
			.addGroup( layout.createParallelGroup()
				.addComponent(lblMatchingAlgorithm)
				.addComponent(lblMatchingAlgorithmValue)
			)
			.addGroup( layout.createParallelGroup()
				.addComponent(lblSelectionAlgorithm)
				.addComponent(lblSelectionAlgorithmValue)
			)
		);
		
		
		setLayout(layout);
	}
}
