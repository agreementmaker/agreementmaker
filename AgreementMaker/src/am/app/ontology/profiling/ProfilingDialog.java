package am.app.ontology.profiling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import am.app.Core;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;

/**
 * The ontology profiling dialog.  Is is meant to be accessed from the
 * Ontology UIMenu.
 * 
 * Note: Modeled after the MatcherParametersDialog.
 * 
 * @author Cosmin Stroe @date January 25, 2011
 *
 */
public class ProfilingDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -7200184968498699650L;

	JButton cancelButton;
	JButton profileButton;
	
	JComboBox profilingAlgorithmsBox;
	
	/* Default constructor. */
	public ProfilingDialog() {
		super(Core.getUI().getUIFrame(), true);
		
		initComponents();
		initLayout();
		
		getRootPane().setDefaultButton(profileButton);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initComponents() {
		profilingAlgorithmsBox = new JComboBox();
		
		// make this iterate through matchers.
		profilingAlgorithmsBox.addItem(ProfilerRegistry.ManualProfiler.getProfilerName());
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		profileButton = new JButton("Profile");
		profileButton.addActionListener(this);
		
	}
	
	
	private void initLayout() {
		
		GroupLayout mainLayout = new GroupLayout(getContentPane());
		mainLayout.setAutoCreateContainerGaps(true);
		mainLayout.setAutoCreateGaps(true);
		
		mainLayout.setHorizontalGroup( mainLayout.createParallelGroup( GroupLayout.Alignment.TRAILING )
				.addComponent(profilingAlgorithmsBox)
				.addGroup( mainLayout.createSequentialGroup()
						.addComponent(cancelButton)
						.addComponent(profileButton)
				)
		);
		
		mainLayout.setVerticalGroup( mainLayout.createSequentialGroup()
				.addComponent(profilingAlgorithmsBox)
				.addGroup( mainLayout.createParallelGroup()
						.addComponent(cancelButton)
						.addComponent(profileButton)
				)
		);
		
		getContentPane().setLayout(mainLayout);
	}
	
	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
	    JRootPane rootPane = new JRootPane();
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
	    Action actionListener = new AbstractAction() {
			private static final long serialVersionUID = 1774539460694983567L;
			public void actionPerformed(ActionEvent actionEvent) {
		        cancelButton.doClick();
		      }
	    };
	    InputMap inputMap = rootPane
	        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    inputMap.put(stroke, "ESCAPE");
	    rootPane.getActionMap().put("ESCAPE", actionListener);

	    return rootPane;
	  }

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == cancelButton ) {
			setVisible(false);
			return;
		}
		
		if( e.getSource() == profileButton ) {
			ManualOntologyProfiler mop = new ManualOntologyProfiler(Core.getInstance().getSourceOntology(), Core.getInstance().getTargetOntology());
			Core.getInstance().setOntologyProfiler(mop);
			setVisible(false);
			return;
		}
	}
	
}
