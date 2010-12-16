package am.userInterface;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;

public class MatchPanel extends JPanel implements ActionListener{
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
	private JPanel bottomPanel;
	
	private AbstractMatcher matcher;
	
	public MatchPanel(AbstractMatcher matcher) {
		super();
		this.matcher = matcher;	
		initComponents();
		addComponents();
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
		
		topPanel = new JPanel();
		
		generalPanel = createGeneralPanel();
		
		settingsPanel = new JPanel();
		settingsPanel.setBorder(new TitledBorder("Matcher Specific Settings"));
		settingsPanel.setBackground(Color.RED);
		
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		
			
		
		bottomPanel = new JPanel();
		
		
		
		
		
	}

	private void addComponents() {
		//set topPanel
		
		
		
		
		
		
		
		
		
		
		
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

	public static void main(String[] args) {
		IterativeInstanceStructuralMatcher matcher = new IterativeInstanceStructuralMatcher();
		MatchPanel panel = new MatchPanel(matcher);
		JDialog dialog = new JDialog();
		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
				
	}
}
