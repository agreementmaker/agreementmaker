package am.userInterface;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton; 
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;



public class MatcherParametersDialog extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 7150332604304262664L;

	private JLabel matcherLabel;
	private JComboBox matcherCombo;
	private JLabel settingsLabel;
	private JComboBox settingsCombo;
	private JButton saveButton;
	private JButton deleteButton;
	
	private JLabel thresholdLabel;
	private JComboBox thresholdCombo;
	private JLabel sourceRelLabel;
	private JComboBox sourceRelCombo;
	private JLabel targetRelLabel;
	private JComboBox targetRelCombo;
	private JCheckBox completionBox;
	
	private JButton runButton;
	private JButton cancelButton;
	
	private JPanel topPanel;
	private JPanel generalPanel;
	private JPanel settingsPanel;
	
	private boolean success = false;
	AbstractMatcherParametersPanel parametersPanel;
	
	AbstractMatcher matcher;
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public MatcherParametersDialog(AbstractMatcher a) {
		super();
		
		initComponents();
		
		ComboBoxModel model = matcherCombo.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			String curr = (String)model.getElementAt(i);
			if(curr.equals(a.getName().getMatcherName()))
				matcherCombo.setSelectedIndex(i);
		}
		matcherCombo.setSelectedItem(a.getName());		
		
		matcherCombo.setEnabled(false);
		
		String name = a.getName().getMatcherName();
		setTitle(name+": additional parameters");
		//This is the specific panel defined by the developer to set additional parameters to the specific matcher implemented
		parametersPanel = a.getParametersPanel();
		
		// top panel
		topPanel = createTopPanel();
		
		// general panel
		generalPanel = createGeneralPanel();
		
		// matcher specific settings panel
		if(a.getParametersPanel() != null){  settingsPanel = a.getParametersPanel(); }
		else { settingsPanel = new JPanel(); }
		settingsPanel.setBorder(new TitledBorder("Matcher Specific Settings"));
		
		initLayout();
				
		//frame.addWindowListener(new WindowEventHandler());//THIS SHOULD BE CHANGED THE PROGRAM SHOULD NOT CLOSE
		//pack(); // automatically set the frame size
		//set the width equals to title dimension
		if( getFont() != null && getFontMetrics(getFont()) != null ) {
			FontMetrics fm = getFontMetrics(getFont());
			// +100 to allow for icon and "x-out" button
			int width = fm.stringWidth(getTitle()) + 100;
			width = Math.max(width, getPreferredSize().width);
			setSize(new Dimension(width, getPreferredSize().height));
		}
		pack();  // make it smaller.
		setLocationRelativeTo(null); 	// center the window on the screen
		setModal(true);
		setVisible(true);
	}
	
	

	public MatcherParametersDialog() {
		super();
		
		initComponents();
		
		matcher = MatcherFactory.getMatcherInstance(
				MatcherFactory.getMatchersRegistryEntry((String)matcherCombo.getSelectedItem()), 0);
		
		String name = matcher.getName().getMatcherName();
		setTitle(name+": additional parameters");
		//This is the specific panel defined by the developer to set additional parameters to the specific matcher implemented
		parametersPanel = matcher.getParametersPanel();
		
		// top panel
		topPanel = createTopPanel();
		
		// general panel
		generalPanel = createGeneralPanel();
		
		// matcher specific settings panel
		if(matcher.getParametersPanel() != null){  settingsPanel = matcher.getParametersPanel(); }
		else { settingsPanel = new JPanel(); }
		settingsPanel.setBorder(new TitledBorder("Matcher Specific Settings"));
		
		initLayout();
				
		//frame.addWindowListener(new WindowEventHandler());//THIS SHOULD BE CHANGED THE PROGRAM SHOULD NOT CLOSE
		//pack(); // automatically set the frame size
		//set the width equals to title dimension
		if( getFont() != null && getFontMetrics(getFont()) != null ) {
			FontMetrics fm = getFontMetrics(getFont());
			// +100 to allow for icon and "x-out" button
			int width = fm.stringWidth(getTitle()) + 100;
			width = Math.max(width, getPreferredSize().width);
			setSize(new Dimension(width, getPreferredSize().height));
		}
		pack();  // make it smaller.
		setLocationRelativeTo(null); 	// center the window on the screen
		setModal(true);
		setVisible(true);
	}

	
	
	private void initComponents() {
		matcherLabel = new JLabel("Matcher:");
		String[] matcherList = MatcherFactory.getMatcherComboList();
		matcherCombo = new JComboBox(matcherList);
		
		settingsLabel = new JLabel("Settings:");
		settingsCombo = new JComboBox();
		saveButton = new JButton("Save");
		deleteButton = new JButton("Delete");
		
		thresholdLabel = new JLabel("Threshold:");
		String[] thresholdList = Utility.getPercentStringList();
		thresholdCombo = new JComboBox(thresholdList);
		thresholdCombo.setSelectedItem("60%");
		
		sourceRelLabel = new JLabel("Source relations:");
		Object[] numRelList = Utility.getNumRelList();
		sourceRelCombo = new JComboBox(numRelList);
		sourceRelCombo.setSelectedItem(1);
		
		targetRelLabel = new JLabel("Target relations:");
		targetRelCombo = new JComboBox(numRelList);
		targetRelCombo.setSelectedItem(1);
		
		completionBox = new JCheckBox("Completion mode");
	
		// cancel and run buttons
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
	
	}
	
	
	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		
		GroupLayout topLayout = new GroupLayout(topPanel);
		topLayout.setAutoCreateContainerGaps(true);
		topLayout.setAutoCreateGaps(true);
		
		
		topLayout.setHorizontalGroup( topLayout.createSequentialGroup()
				.addComponent(matcherLabel)
				.addComponent(matcherCombo)
	            .addComponent(settingsLabel)
	            .addComponent(settingsCombo)
	            .addComponent(saveButton)
	            .addComponent(deleteButton)
		);
		
		topLayout.setVerticalGroup( topLayout.createParallelGroup()
				.addComponent(matcherLabel)
				.addComponent(matcherCombo)
	            .addComponent(settingsLabel)
	            .addComponent(settingsCombo)
	            .addComponent(saveButton)
	            .addComponent(deleteButton)
		);

		topPanel.setLayout(topLayout);
		
		return topPanel;
	}
	
	
	private JPanel createGeneralPanel() {
		
		JPanel generalPanel = new JPanel();
		generalPanel.setBorder(new TitledBorder("General Settings"));
		
		//set generalPanel
		GroupLayout generalLayout = new GroupLayout(generalPanel);
		generalLayout.setAutoCreateContainerGaps(true);
		generalLayout.setAutoCreateGaps(true);
		
		generalLayout.setHorizontalGroup( generalLayout.createParallelGroup()
				.addGroup(  generalLayout.createSequentialGroup()
						.addComponent(thresholdLabel)
						.addComponent(thresholdCombo)
			            .addComponent(sourceRelLabel)
			            .addComponent(sourceRelCombo)
			            .addComponent(targetRelLabel)
			            .addComponent(targetRelCombo)
				)
				.addComponent(completionBox)
				
		);
		
		
		generalLayout.setVerticalGroup( generalLayout.createSequentialGroup()
				.addGroup( generalLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(thresholdLabel)
						.addComponent(thresholdCombo)
			            .addComponent(sourceRelLabel)
			            .addComponent(sourceRelCombo)
			            .addComponent(targetRelLabel)
			            .addComponent(targetRelCombo)
				)
				.addComponent(completionBox)
		);
		
		generalPanel.setLayout(generalLayout);
		
		return generalPanel;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	
	private void initLayout() {
		//overall dialog layout
		GroupLayout layout = new GroupLayout(this.getContentPane());
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		
		layout.setHorizontalGroup( layout.createParallelGroup(Alignment.TRAILING)
				.addComponent(topPanel)
				.addComponent(generalPanel)
	            .addComponent(settingsPanel)
	            .addGroup( layout.createSequentialGroup()
	            		.addComponent(cancelButton)
	            		.addComponent(runButton)
	            )
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup()
				.addComponent(topPanel)
				.addComponent(generalPanel)
	            .addComponent(settingsPanel)
	            .addGroup( layout.createParallelGroup()
	            		.addComponent(cancelButton)
	            		.addComponent(runButton)
	            )
		); 

		setLayout(layout);	
	}	

	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if(obj == cancelButton){
			success = false;
			//setModal(false);
			setVisible(false);  // required
		}
		else if(obj == runButton){
			String check = parametersPanel.checkParameters();
			if(check == null || check.equals("")) {
				success = true;
				//setModal(false);
				setVisible(false);  // required
			}
			else Utility.displayErrorPane(check, "Illegal Parameters" );
		}
		
	}
	
	
	public boolean parametersSet() {
		return success;
	}
	
	public AbstractParameters getParameters() {
		return parametersPanel.getParameters();
	}
	
	
	public static void main(String[] args) {
		AbstractMatcher matcher = MatcherFactory.getMatcherInstance(MatchersRegistry.IISM, 1);
		new MatcherParametersDialog(matcher);
		
		//new MatcherParametersDialog();

	}
	
}
