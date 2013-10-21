package am.ui.matchingtask;

import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import am.app.ontology.Ontology;
import am.app.ontology.instance.InstanceDataset;

/**
 * This is a simple panel which displays information about an ontology.
 * Currently displays the filename of the ontology and the number of 
 * classes, properties, and instances in the ontology.
 * 
 *
 * @author Cosmin Stroe
 *
 * @see MatchingTaskOverviewPanel
 */
public class OntologyDetailsPanel extends JPanel {

	private static final long serialVersionUID = 4801396336675173738L;

	protected JLabel lblFilename = new JLabel("Filename:");
	protected JLabel lblClasses = new JLabel("Classes:");
	protected JLabel lblProperties = new JLabel("Properties:");
	protected JLabel lblInstances = new JLabel("Instances:");
	
	protected JLabel lblFilenameValue = new JLabel();
	protected JLabel lblClassesValue = new JLabel();
	protected JLabel lblPropertiesValue = new JLabel();
	protected JLabel lblInstancesValue = new JLabel();
	
	public OntologyDetailsPanel(Ontology ont) {
		super();
		
		if( ont == null ) {
			lblFilenameValue.setText("null");
			lblClassesValue.setText("null");
			lblPropertiesValue.setText("null");
			lblInstancesValue.setText("null");
		}
		else {
			String s = ont.getFilename();
			s = "..." + s.substring(Math.max(s.length() - 27, 0), s.length());
			lblFilenameValue.setText( s );
			lblClassesValue.setText( Integer.toString(ont.getClassesList().size()) );
			lblPropertiesValue.setText( Integer.toString(ont.getPropertiesList().size()) );
			
			if( ont.getInstances() != null ) {
				InstanceDataset dataset = ont.getInstances();
				if( dataset.isIterable() ) {
					lblInstancesValue.setText( Long.toString(dataset.size()) );
				} 
				else {
					lblInstancesValue.setText("Not iterable.");
				}
			} 
			else {
				lblInstancesValue.setText("Not defined.");
			}
		}
		
		GroupLayout layout = new GroupLayout(this);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup( layout.createSequentialGroup()
			.addGroup( layout.createParallelGroup()
				.addComponent(lblFilename)
				.addComponent(lblClasses)
				.addComponent(lblProperties)
				.addComponent(lblInstances)	
			)
			.addGroup( layout.createParallelGroup()
				.addComponent(lblFilenameValue)
				.addComponent(lblClassesValue)
				.addComponent(lblPropertiesValue)
				.addComponent(lblInstancesValue)
			)
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup()
			.addGroup( layout.createParallelGroup()
				.addComponent(lblFilename)
				.addComponent(lblFilenameValue)
			)
			.addGroup( layout.createParallelGroup()
				.addComponent(lblClasses)
				.addComponent(lblClassesValue)
			)
			.addGroup( layout.createParallelGroup()
				.addComponent(lblProperties)
				.addComponent(lblPropertiesValue)
			)
			.addGroup( layout.createParallelGroup()
				.addComponent(lblInstances)
				.addComponent(lblInstancesValue)
			)
		);
		
		setLayout(layout);
	}
	
	
	/* Testing entrypoint */ 
	public static void main(String[] args) {
		JPanel p = new OntologyDetailsPanel(null);
		JDialog d = new JDialog();
		d.getContentPane().add(p, BorderLayout.CENTER);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
	}
}
