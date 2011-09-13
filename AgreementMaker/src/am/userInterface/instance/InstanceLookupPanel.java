package am.userInterface.instance;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.hp.hpl.jena.graph.query.Expression.Util;

import am.AMException;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.profiling.ontologymetrics.Utility;

public class InstanceLookupPanel extends JPanel {

	private static final long serialVersionUID = -7574874573811232312L;

	
	public InstanceLookupPanel(InstanceDataset sourceDataset, InstanceDataset targetDataset ) {
		super();
		
		JPanel sourcePanel;
		JPanel targetPanel;
		
		if( sourceDataset != null && sourceDataset.isIterable() ) {
			sourcePanel = new IterableLookupPanel(sourceDataset);
		} else {
			sourcePanel = new NonIterableLookupPanel(sourceDataset);
		}
		
		if( targetDataset != null && targetDataset.isIterable() ) {
			targetPanel = new IterableLookupPanel(targetDataset);
		} else {
			targetPanel = new NonIterableLookupPanel(targetDataset);
		}
		
		setLayout( new BorderLayout() );
		
		JSplitPane splitPane = new JSplitPane();
		
		splitPane.setLeftComponent(sourcePanel);
		splitPane.setRightComponent(targetPanel);
		
		add(splitPane, BorderLayout.CENTER);
		
	}
	
	
	
	/* **********************************************************************
	 * *********************** ITERABLE LOOKUP PANEL ************************ 
	 * ********************************************************************** */
	
	private class IterableLookupPanel extends JPanel implements ActionListener {
		
		private JLabel[] labels;
		private JComboBox[] comboboxes;
		private JButton[] buttons;
		private JTextArea[] textareas;
		
		private InstanceDataset dataset;
		
		public IterableLookupPanel( InstanceDataset dataset ) {
			super();
			this.dataset = dataset;
			
			// Initialize the UI components.
			labels = new JLabel[1];
			labels[0] = new JLabel("Instance: ");
			
			comboboxes = new JComboBox[1];
			comboboxes[0] = new JComboBox();
			
			buttons = new JButton[1];
			buttons[0] = new JButton("URI Lookup");
			buttons[0].addActionListener(this);
			
			textareas = new JTextArea[1];
			textareas[0] = new JTextArea();
			
			
			// Create the layout.
			GroupLayout lay = new GroupLayout(this);
			
			lay.setAutoCreateContainerGaps(true);
			lay.setAutoCreateGaps(true);
			
			lay.setHorizontalGroup( lay.createParallelGroup() 
					.addGroup( lay.createSequentialGroup()
							.addComponent(labels[0])
							.addComponent(comboboxes[0])
							.addComponent(buttons[0])
					)
					.addComponent(textareas[0])
			);
			
			lay.setVerticalGroup( lay.createSequentialGroup()
					.addGroup( lay.createParallelGroup(Alignment.CENTER, false)
							.addComponent(labels[0])
							.addComponent(comboboxes[0])
							.addComponent(buttons[0])
					)
					.addComponent(textareas[0])
			);
			
			setLayout(lay);
			
			try {
				List<Instance> instanceList = dataset.getInstances();
				
				// sort by uri
				Collections.sort(instanceList, new Comparator<Instance>() {
					@Override
					public int compare(Instance o1, Instance o2) {
						return o1.getUri().compareTo(o2.getUri());
					}
				});
				
				for( Instance currentInstance : instanceList ) {
					comboboxes[0].addItem( currentInstance.getUri() );
				}
				
				
				doLookup( (String) comboboxes[0].getSelectedItem() );
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		private void doLookup( String uri ) throws AMException {
			
			Instance instance = dataset.getInstance(uri);
			
			JTextArea textarea = textareas[0];
			
			textarea.setText(""); 
			textarea.append("Instance URI: " + uri + "\n\n");
			
			textarea.append("Properties:\n\n");
			
			Enumeration<String> properties = instance.listProperties();
			
			while( properties.hasMoreElements() ) {
				String currentProperty = properties.nextElement();
				
				textarea.append(currentProperty + ": " + instance.getProperty(currentProperty).toString() + "\n");
			}
			
		}


		@Override
		public void actionPerformed(ActionEvent evt) {
			if( evt.getSource() == buttons[0] ) {
				try {
					doLookup( (String) comboboxes[0].getSelectedItem() );
				} catch( AMException e ) {
					am.Utility.displayErrorPane(e.getMessage(), "Lookup Error");
				}
			}
		}
		
	}
	
	
	private class NonIterableLookupPanel extends JPanel implements ActionListener {
		
		private JLabel[] labels;
		//private JComboBox[] comboboxes;
		private JTextField[] textfields;
		private JButton[] buttons;
		private JTextArea[] textareas;
		
		private InstanceDataset dataset;
		
		public NonIterableLookupPanel( InstanceDataset dataset ) {
			super();
			this.dataset = dataset;
			
			// Initialize the UI components.
			labels = new JLabel[1];
			labels[0] = new JLabel("Search Term: ");
			labels[1] = new JLabel("Instance Type: ");
			
			
			textfields = new JTextField[2];
			textfields[0] = new JTextField();
			textfields[1] = new JTextField();
			
			buttons = new JButton[0];
			buttons[0] = new JButton("Candidate Lookup");
			buttons[0].addActionListener(this);
			
			textareas = new JTextArea[1];
			textareas[0] = new JTextArea();
			
			
			// Create the layout.
			GroupLayout lay = new GroupLayout(this);
			
			lay.setAutoCreateContainerGaps(true);
			lay.setAutoCreateGaps(true);
			
			lay.setHorizontalGroup( lay.createParallelGroup() 
					.addGroup( lay.createSequentialGroup()
							.addComponent(labels[0])
							.addComponent(textfields[0])
							.addComponent(labels[1])
							.addComponent(textfields[1])
							.addComponent(buttons[0])
					)
					.addComponent(textareas[0])
			);
			
			lay.setVerticalGroup( lay.createSequentialGroup()
					.addGroup( lay.createParallelGroup(Alignment.CENTER, false)
							.addComponent(labels[0])
							.addComponent(textfields[0])
							.addComponent(labels[1])
							.addComponent(textfields[1])
							.addComponent(buttons[0])
					)
					.addComponent(textareas[0])
			);
			
			setLayout(lay);
			
		}
		
		
		private void doLookup( String queryString, String type ) throws AMException {
			
			List<Instance> instances = dataset.getCandidateInstances(queryString, type);
			
			JTextArea textarea = textareas[0];
			
			textarea.setText("Candidate instances:\n\n");
			
			for( Instance instance : instances ) {
				String uri = instance.getUri();
				textarea.append("Instance URI: " + uri + "\n\n");
				textarea.append("Properties:\n\n");
				
				Enumeration<String> properties = instance.listProperties();
				
				while( properties.hasMoreElements() ) {
					String currentProperty = properties.nextElement();
					
					textarea.append(currentProperty + ": " + instance.getProperty(currentProperty).toString() + "\n");
				}
				
				textarea.append("\n\n");
			}
			
		}


		@Override
		public void actionPerformed(ActionEvent arg0) {	
			if( arg0.getSource() == buttons[0] ) {
				try {
					doLookup( textfields[0].getText() , textfields[1].getText() );
				} catch( AMException e ) {
					am.Utility.displayErrorPane(e.getMessage(), "Lookup Error");
				}
			}
		}
		
	}
	
}
