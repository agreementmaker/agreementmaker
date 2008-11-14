package agreementMaker.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import agreementMaker.Utility;
import agreementMaker.agreementDocument.DocumentProducer;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.MatcherFactory;
import agreementMaker.userInterface.table.MatchersTable;
import agreementMaker.userInterface.table.MatchersTablePanel;
import agreementMaker.userInterface.table.MyTableModel;

public class ControlPanelTry extends JPanel implements ActionListener,
		ItemListener {

	private static final long serialVersionUID = -2258009700001283026L;
	
	private JLabel matcherLabel;
	private JLabel thresholdLabel;
	private JLabel sRelLabel;
	private JLabel tRelLabel;;
	private JComboBox matcherCombo;
	private JComboBox thresholdCombo;
	private JComboBox sRelationCombo;
	private JComboBox tRelationCombo;
	private JButton matchButton;
	private JButton viewDetails;
	private JButton button2;
	private JButton button3;
	private UI ui;
	private MatchersTablePanel matchersTablePanel;
	
	public final static int SHOW = 0;
	public final static int HIDE = 1;
	
	
	ControlPanelTry(UI ui, UIMenu uiMenu, Canvas canvas) {
		this.ui = ui;
		init();
	}

	void init() {

				
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Matchings Control Panel"));
		//setAlignmentX(LEFT_ALIGNMENT);
		//setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		//JPANEL MATCHER SELECTION (first top of the three panels)
		//label
		matcherLabel = new JLabel("Matcher selection: ");
		String[] matcherList = MatcherFactory.getMatcherNames();
		//matcher combo list
		matcherCombo = new JComboBox(matcherList);
		matcherCombo.addItemListener(this);
		//button to view matcher details
		viewDetails = new JButton("View details");
		viewDetails.addActionListener(this);
		//button to run match
		matchButton = new JButton("Match!");
		matchButton.addActionListener(this);
		//Threshold combo and label
		thresholdLabel = new JLabel("Threshold");
		String[] thresholdList = Utility.getPercentStringList();
		thresholdCombo = new JComboBox(thresholdList);
		thresholdCombo.setSelectedItem("75%");
		//Relations combo
		Object[] numRelList = Utility.getNumRelList();
		sRelLabel = new JLabel("Source relations");
		sRelationCombo = new JComboBox(numRelList);
		sRelationCombo.setSelectedItem(1);
		tRelLabel = new JLabel("Target relations");
		tRelationCombo = new JComboBox(numRelList);
		tRelationCombo.setSelectedItem(MyTableModel.ANY);
		//matcher selection panel
		JPanel matcherSelectionPanel = new JPanel();
		matcherSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		matcherSelectionPanel.add(matcherLabel);
		matcherSelectionPanel.add(matcherCombo);
		matcherSelectionPanel.add(viewDetails);
		matcherSelectionPanel.add(matchButton);
		matcherSelectionPanel.add(thresholdLabel);
		matcherSelectionPanel.add(thresholdCombo);
		matcherSelectionPanel.add(sRelLabel);
		matcherSelectionPanel.add(sRelationCombo);
		matcherSelectionPanel.add(tRelLabel);
		matcherSelectionPanel.add(tRelationCombo);
		
		button2 = new JButton("Button2");
		button3 = new JButton("Button3");

		matchersTablePanel = new MatchersTablePanel();
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new FlowLayout(FlowLayout.LEADING));
		//panel3.setAlignmentX(LEFT_ALIGNMENT);
		panel3.add(button2);
		panel3.add(button3);
		add(matcherSelectionPanel);
		add(matchersTablePanel);
		add(panel3);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == viewDetails) {
			int nameIndex = matcherCombo.getSelectedIndex();
			AbstractMatcher a = MatcherFactory.getMatcherInstance(nameIndex, 0); //i'm just using a fake istance so the algorithm code is not important i put 0 but maybe anything
			Utility.displayMessagePane(a.getDetails(), "Matcher details");
		}
		else if(obj == matchButton) {
			match();
		}
	}
	
	public void match() {
		int nameIndex = matcherCombo.getSelectedIndex();
		//the new matcher will put at the end of the list so at the end of the table so the index will be:
		int lastIndex = Core.getInstance().getMatcherInstances().size();
		AbstractMatcher currentMatcher = MatcherFactory.getMatcherInstance(nameIndex, lastIndex);
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows(); //indexes in the table correspond to the indexes of the matchers in the matcherInstances list in core class
		int selectedMatchers = rowsIndex.length;
		if(currentMatcher.getMinInputMatchers() > selectedMatchers ) {
			Utility.dysplayErrorPane("Select at least "+currentMatcher.getMinInputMatchers()+" matchings from the table to run this matcher.", null);
		}
		else if(currentMatcher.getMaxInputMatchers() < selectedMatchers) {
			Utility.displayConfirmPane("More matchers are selected as input than required.\n The matcher will consider only the first.\nDo you want to continue? "+currentMatcher.getMaxInputMatchers(), null);
		}
		else {
			//Asking parameters before setting input matcher list, just because the user can still cancel the operation
			boolean matcherReady = true; //a matcher which doesn't need parameters is ready
			if(currentMatcher.needsParam()) {
				matcherReady = false;
				AbstractMatcherParametersDialog dialog = new AbstractMatcherParametersDialog(currentMatcher);
				if(dialog.parametersSet()) {
					currentMatcher.setParam(dialog.getParameters());
					matcherReady = true;
				}
				dialog.dispose();
			}
			if(matcherReady) {
				//parameters are set if they were needed, now we need to set the list of input matchers
				for(int i = 0; i<rowsIndex.length; i++) {
					AbstractMatcher input = Core.getInstance().getMatcherInstances().get(i);
					currentMatcher.addInputMatcher(input);
				}
				currentMatcher.match();
				matchersTablePanel.addMatcher(currentMatcher);
				//REDISPLAY EVERYTHING
				System.out.println("yeeei");
			}
		}
		
	}

	/**
	 * This method takes care of the action perfromed by one of the check boxes
	 *
	 * @param e ActionEvent object
	 */
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getItemSelectable();
		if(obj == matcherCombo) {
			//TODO we could add the method getDefaultThreshold and getDefaultNumRelations in the abstractMatcher class
			//so that the system auto select threshold and numrelations depending on algorithm selected
			//but this feature could be boring if you have to run many different algorithms with the same parameters
		}
	}
}
