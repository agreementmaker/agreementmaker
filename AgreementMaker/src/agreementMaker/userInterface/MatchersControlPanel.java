package agreementMaker.userInterface;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;


import agreementMaker.AMException;
import agreementMaker.Utility;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.AlignmentSet;
import agreementMaker.application.mappingEngine.Evaluator;
import agreementMaker.application.mappingEngine.MatcherFactory;
import agreementMaker.application.mappingEngine.MatchersRegistry;
import agreementMaker.application.mappingEngine.ResultData;
import agreementMaker.application.mappingEngine.manualMatcher.UserManualMatcher;
import agreementMaker.application.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import agreementMaker.userInterface.table.MatchersTablePanel;
import agreementMaker.userInterface.table.MyTableModel;

public class MatchersControlPanel extends JPanel implements ActionListener,
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
	private JButton delete;
	private JButton refEvaluate;
	private UI ui;
	private MatchersTablePanel matchersTablePanel;
	
	public final static int SHOW = 0;
	public final static int HIDE = 1;
	
	
	MatchersControlPanel(UI ui, UIMenu uiMenu, Canvas canvas) {
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
		String[] matcherList = MatcherFactory.getMatcherComboList();
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
		
		delete = new JButton("Delete");
		delete.addActionListener(this);
		refEvaluate = new JButton("Reference Evaluation");
		refEvaluate.addActionListener(this);
		matchersTablePanel = new MatchersTablePanel(ui);
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new FlowLayout(FlowLayout.LEADING));
		//panel3.setAlignmentX(LEFT_ALIGNMENT);
		panel3.add(delete);
		panel3.add(refEvaluate);
		add(matcherSelectionPanel);
		add(matchersTablePanel);
		add(panel3);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			Object obj = e.getSource();
			if(obj == viewDetails) {
				//int nameIndex = matcherCombo.getSelectedIndex();
				String matcherName = (String) matcherCombo.getSelectedItem();
				AbstractMatcher a = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //i'm just using a fake instance so the algorithm code is not important i put 0 but maybe anything
				Utility.displayMessagePane(a.getDetails(), "Matcher details");
			}
			else if(obj == matchButton) {
				match();
			}
			else if(obj == delete) {
				int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
				if(rowsIndex.length == 0) {
					Utility.displayErrorPane("No matchers selected", null);
				}
				else if(Utility.displayConfirmPane(rowsIndex.length+" matchers will be deleted.\n Do you want to continue?", null)) {
					AbstractMatcher toBeDeleted;
					Core core = Core.getInstance();
					LinkedList<AbstractMatcher> deleteList = new LinkedList<AbstractMatcher>();
					for(int i = 0; i < rowsIndex.length; i++) {// I need to build a list because indexes will be modified so i can't access them using the rowsindex structures
						deleteList.add(core.getMatcherInstances().get(rowsIndex[i]));
					}
					for(int i = 0; i< deleteList.size(); i++) {
						toBeDeleted = deleteList.get(i);
						if(MatcherFactory.isTheUserMatcher(toBeDeleted)) {
							//YOU CAN'T DELETE THE USER MATCHING just clear the matchings previusly created
							Utility.displayMessagePane(MatchersRegistry.UserManual + " can't be deleted.\nOnly alignments will be cleared.", null);
							try {
								toBeDeleted.match();//reinitialize the user matching as an empty one
							}
							catch(AMException ex) {Utility.displayErrorPane(ex.getMessage(), null);}
						}
						else {
							matchersTablePanel.removeMatcher(toBeDeleted);
						}
						ui.redisplayCanvas();
					}
				}
			}
			else if(obj == refEvaluate) {
				evaluate();
			}
		}
		catch(Exception ex) {
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR, null);
		}
	}
	
	private void evaluate() throws Exception{
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else {
			//Run the reference alignment matcher to get the list of alignments in reference file, we are not going to add it in the table list
			ReferenceAlignmentMatcher refMatcher = (ReferenceAlignmentMatcher)MatcherFactory.getMatcherInstance(MatchersRegistry.ReferenceAlignment,0);
			AbstractMatcherParametersDialog dialog = new AbstractMatcherParametersDialog(refMatcher);
			if(dialog.parametersSet()) {
				refMatcher.setParam(dialog.getParameters());
				refMatcher.setThreshold(refMatcher.getDefaultThreshold());
				refMatcher.setMaxSourceAlign(refMatcher.getDefaultMaxSourceRelations());
				refMatcher.setMaxTargetAlign(refMatcher.getDefaultMaxTargetRelations());
				refMatcher.match();
				AlignmentSet referenceSet = refMatcher.getAlignmentSet(); //class + properties
				AbstractMatcher toBeEvaluated;
				AlignmentSet evaluateSet;
				ResultData rd;
				for(int i = 0; i < rowsIndex.length; i++) {
					toBeEvaluated = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
					evaluateSet = toBeEvaluated.getAlignmentSet();
					rd = Evaluator.compare(evaluateSet, referenceSet);
					toBeEvaluated.setRefEvaluation(rd);
					AbstractTableModel model = (AbstractTableModel)matchersTablePanel.getTable().getModel();
					model.fireTableRowsUpdated(toBeEvaluated.getIndex(), toBeEvaluated.getIndex());
				}
			}
			dialog.dispose();
			ui.redisplayCanvas();
		}
		
	}

	public void match() throws Exception{
		String matcherName = (String) matcherCombo.getSelectedItem();
		//the new matcher will put at the end of the list so at the end of the table so the index will be:
		int lastIndex = Core.getInstance().getMatcherInstances().size();
		AbstractMatcher currentMatcher = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), lastIndex);
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows(); //indexes in the table correspond to the indexes of the matchers in the matcherInstances list in core class
		int selectedMatchers = rowsIndex.length;
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load a Source and Target ontologies before running any matcher\nClick on File Menu and select Open Ontology functions ", null);
		}
		else if(currentMatcher.getMinInputMatchers() > selectedMatchers ) {
			Utility.displayErrorPane("Select at least "+currentMatcher.getMinInputMatchers()+" matchings from the table to run this matcher.", null);
		}
		/* Warning to alert that user has selected more inputMatcher than necessary, I had to remove this because it happens all the time and it's boring
		else if(currentMatcher.getMaxInputMatchers() < selectedMatchers) {
			int answer = Utility.displayConfirmPane("More matchers are selected as input than required.\n The matcher will consider only the first.\nDo you want to continue? "+currentMatcher.getMaxInputMatchers(), null);
			if(answer == JOptionPane.NO_OPTION) {
				everythingOk = false;
			}
		}
		if(everythingOk) {
		*/
		else {
			boolean everythingOk = true;
			//Asking parameters before setting input matcher list, just because the user can still cancel the operation
			if(currentMatcher.needsParam()) {
				everythingOk = false;
				AbstractMatcherParametersDialog dialog = new AbstractMatcherParametersDialog(currentMatcher);
				if(dialog.parametersSet()) {
					currentMatcher.setParam(dialog.getParameters());
					everythingOk = true;
				}
				dialog.dispose();
			}
			if(everythingOk) {
				currentMatcher.setThreshold(Utility.getDoubleFromPercent((String)thresholdCombo.getSelectedItem()));
				currentMatcher.setMaxSourceAlign(Utility.getIntFromNumRelString((String)sRelationCombo.getSelectedItem()));
				currentMatcher.setMaxTargetAlign(Utility.getIntFromNumRelString((String)tRelationCombo.getSelectedItem()));
				//parameters are set if they were needed, now we need to set the list of input matchers
				for(int i = 0; i<rowsIndex.length && i< currentMatcher.getMaxInputMatchers(); i++) {
					AbstractMatcher input = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
					currentMatcher.addInputMatcher(input);
				}
				try {
					currentMatcher.match();
					matchersTablePanel.addMatcher(currentMatcher);
					ui.redisplayCanvas();
				}
				catch(AMException ex){
					Utility.displayMessagePane(ex.getMessage(), null);
				}
				System.out.println("Matching Process Complete");
			}
		}
	}
	
	public MatchersTablePanel getTablePanel() {
		return matchersTablePanel;
	}
	
	public void userMatching(ArrayList<Alignment> alignments) {
		if(alignments.size()>0) {
			int[] rows = matchersTablePanel.getTable().getSelectedRows();
			if(rows == null || rows.length == 0) {
				//lf no rows are selected it adds it to the UserMatching
				Core.getInstance().performUserMatching(0, alignments);
			}
			else {
				for(int i=0; i < rows.length;i++) {
					Core.getInstance().performUserMatching(rows[i], alignments);
					
				}
				ui.redisplayCanvas();
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
			String matcherName = (String) matcherCombo.getSelectedItem();
			AbstractMatcher a = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //i'm just using a fake instance so the algorithm code is not important i put 0 but maybe anythings
			thresholdCombo.setSelectedItem(Utility.getNoFloatPercentFromDouble(a.getDefaultThreshold()));
			sRelationCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxSourceRelations()));
			tRelationCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxTargetRelations()));
		}
	}

	
	public void resetMatchings() {
		try {
			// TODO Auto-generated method stub
			Core core = Core.getInstance();
			ArrayList<AbstractMatcher> matchers = core.getMatcherInstances();
			Iterator<AbstractMatcher> it = matchers.iterator();
			//Take the UserManualMatcher and run it for the first time to create empty matrix and alignmentSet
			UserManualMatcher userMatcher =(UserManualMatcher) it.next();
			userMatcher.setSourceOntology(core.getSourceOntology());
			userMatcher.setTargetOntology(core.getTargetOntology());
			try {
				userMatcher.match();
			}
			catch(AMException ex){
				Utility.displayMessagePane(ex.getMessage(), null);
			}
			
			//Delete all other matchers if there are any
			// I'm not using the controlPanel removeMatcher because is not needed we just remove them so we don't need to update index and we update table all together
			int firstRow = 1;
			int lastRow = matchers.size() -1;
			while(it.hasNext()) {
				it.next();
				it.remove();
			}
			//update the table
			((AbstractTableModel)matchersTablePanel.getTable().getModel()).fireTableRowsDeleted(firstRow, lastRow);
			ui.getCanvas().clearAllSelections();
			ui.redisplayCanvas();
		}
		catch(Exception ex) {
			Utility.displayErrorPane("Unexepcted System Error.\nTry to reset the system and repeat the operation.\nContact developers if the error persists.", null);
		}
	}
}
