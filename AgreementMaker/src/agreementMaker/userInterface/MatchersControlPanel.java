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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;


import agreementMaker.AMException;
import agreementMaker.GlobalStaticVariables;
import agreementMaker.Utility;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.AlignmentSet;
import agreementMaker.application.mappingEngine.MatcherFactory;
import agreementMaker.application.mappingEngine.MatchersRegistry;
import agreementMaker.application.mappingEngine.manualMatcher.UserManualMatcher;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluationData;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluator;
import agreementMaker.application.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import agreementMaker.application.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import agreementMaker.application.mappingEngine.referenceAlignment.ReferenceEvaluator;
import agreementMaker.userInterface.table.MatchersTablePanel;
import agreementMaker.userInterface.table.MyTableModel;

public class MatchersControlPanel extends JPanel implements ActionListener,
		ItemListener {

	private static final long serialVersionUID = -2258009700001283026L;
	
	
	private UI ui;
	//SELECTION MATCHER PANEL, the one in the top
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
	private JButton defaultValButton;
	//TABLE PANEL
	private MatchersTablePanel matchersTablePanel;
	//EDIT MATCHINGS PANEL
	private JButton newMatching;
	private JButton delete;
	private JButton clearMatchings;
	private JButton copyButton;
	private JButton editMatrixButton;
	private JButton refEvaluate;
	private JButton qualityEvaluationButton;
	private JButton saveToFileButton;
	private JButton exportMatchingsButton;
	private JButton importMatchingsButton;

	
	
	
	
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
		defaultValButton = new JButton("Default");
		defaultValButton.addActionListener(this);


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
		matcherSelectionPanel.add(defaultValButton);
		
		
		//TABLE PANEL:center panel
		matchersTablePanel = new MatchersTablePanel(ui);
		
		//JPANEL EDIT MATCHINGS: lower panel
		newMatching = new JButton("New");
		newMatching.addActionListener(this);
		delete = new JButton("Delete");
		delete.addActionListener(this);
		refEvaluate = new JButton("Reference Evaluation");
		refEvaluate.addActionListener(this);
		clearMatchings = new JButton("Clear All");
		clearMatchings.addActionListener(this);
		copyButton = new JButton("Copy");
		copyButton.addActionListener(this);
		editMatrixButton = new JButton("Edit Similarity Matrix");
		editMatrixButton.addActionListener(this);
		saveToFileButton = new JButton("Save");
		saveToFileButton.addActionListener(this);
		exportMatchingsButton = new JButton("Export");
		exportMatchingsButton.addActionListener(this);
		importMatchingsButton = new JButton("Import");
		importMatchingsButton.addActionListener(this);
		qualityEvaluationButton = new JButton("Quality Evaluation");
		qualityEvaluationButton.addActionListener(this);
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new FlowLayout(FlowLayout.LEADING));
		panel3.add(newMatching);
		panel3.add(copyButton);
		panel3.add(delete);
		panel3.add(clearMatchings);
		panel3.add(saveToFileButton);
		panel3.add(refEvaluate);
		panel3.add(qualityEvaluationButton);
		//panel3.add(editMatrixButton);

		//

		//panel3.add(importMatchingsButton);
		//panel3.add(exportMatchingsButton);
		
		// WHOLE CONTROL PANEL
		add(matcherSelectionPanel);
		add(matchersTablePanel);
		add(panel3);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			Object obj = e.getSource();
			if(obj == viewDetails) {
				String matcherName = (String) matcherCombo.getSelectedItem();
				AbstractMatcher a = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //i'm just using a fake instance so the algorithm code is not important i put 0 but maybe anything
				Utility.displayMessagePane(a.getDetails(), "Matcher details");
			}
			else if(obj == matchButton) {
				matchSelected();
			}
			else if(obj == delete) {
				delete();
			}
			else if(obj == refEvaluate) {
				evaluate();
			}
			else if(obj == clearMatchings) {
				clearAll();
			}
			else if(obj == defaultValButton) {
				setDefaultCommonParameters();
			}
			else if (obj == copyButton) {
				copy();
			}
			else if(obj == saveToFileButton) {
				save();
			}
			else if(obj == newMatching) {
				newManual();
			}
			else if(obj == qualityEvaluationButton) {
				qualityEvaluation();
			}
		}
		catch(AMException ex2) {
			Utility.displayMessagePane(ex2.getMessage(), null);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR, null);
		}
	}
	
	private void qualityEvaluation() {
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else {
			AbstractMatcher toBeEvaluated;
			String report="Quality Evaluation Complete\n\n";
			QualityEvaluationData q;
			for(int i = 0; i < rowsIndex.length; i++) {
				
				toBeEvaluated = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
				report+=i+" "+toBeEvaluated.getName().getMatcherName()+"\n\n";
				for(int j = 0; j < QualityEvaluator.QUALITIES.length; j++) {
					q = QualityEvaluator.evaluate(toBeEvaluated, QualityEvaluator.QUALITIES[j]);
					if(!q.isLocal()) {
						report+= QualityEvaluator.QUALITIES[j]+"\n";
						report+= "Global Classes Quality: "+q.getGlobalClassMeasure()+"\n" ;
						report+= "Global Properties Quality: "+q.getGlobalPropMeasure()+"\n" ;
						report+= "\n";
					}
					else {
						q = QualityEvaluator.evaluate(toBeEvaluated, QualityEvaluator.QUALITIES[j]);
						report+= QualityEvaluator.QUALITIES[j]+"\n";
						report+= "Average of local Classes Quality: "+Utility.getAverageOfArray(q.getLocalClassMeasures())+"\n" ;
						report+= "Average of local Properties Quality: "+Utility.getAverageOfArray(q.getLocalPropMeasures())+"\n" ;
						//Add the list of local qualities here
						report+= "\n";
					}
				}
			}
			Utility.displayTextAreaPane(report,"Quality Evaluation Report");
		}
		
	}

	public void newManual() throws Exception {
		int lastIndex = Core.getInstance().getMatcherInstances().size();
		AbstractMatcher manualMatcher = MatcherFactory.getMatcherInstance(MatchersRegistry.UserManual, lastIndex);
		match(manualMatcher , true);
	}
	
	public void delete() throws Exception {
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
				if(i == 0 && MatcherFactory.isTheUserMatcher(toBeDeleted)) {
					//YOU CAN'T DELETE THE FIRST USER MATCHING just clear the matchings previusly created
					Utility.displayMessagePane("The default "+MatchersRegistry.UserManual + " can't be deleted.\nOnly alignments will be cleared.", null);
					try {
						toBeDeleted.match();//reinitialize the user matching as an empty one
						matchersTablePanel.updatedRows(0, 0);
					}
					catch(AMException ex) {Utility.displayErrorPane(ex.getMessage(), null);}
				}
				else {
					matchersTablePanel.removeMatcher(toBeDeleted);
				}
				matchersTablePanel.deletedRows(rowsIndex[0], rowsIndex[rowsIndex.length-1]);
				ui.redisplayCanvas();
			}
		}
	}

	public void save() {
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else 	new SaveFileDialog(); //demand control to the savefile dialog which since is modal will take care of everything
	}
	
	public void copy() throws Exception{
		//TODO TO BE CORRECTED, IT SHOULD BE A REAL COPY OF THE MATCHER, like a clone; shoulnd't invoke copymatcher while matcher.clone();
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else {
			AbstractMatcher toBeCopied;
			for(int i = 0; i < rowsIndex.length; i++) {
				toBeCopied = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
				AbstractMatcher aCopy = toBeCopied.copy(); //it does everything also setting the last index and matching
				matchersTablePanel.addMatcher(aCopy);
			}
			Utility.displayMessagePane(rowsIndex.length+" matchers have been copied.\n",null);
			ui.redisplayCanvas();
		}
	}
	
	public void evaluate() throws Exception{
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else {
			//Run the reference alignment matcher to get the list of alignments in reference file, we are not going to add it in the table list
			ReferenceAlignmentMatcher refMatcher = (ReferenceAlignmentMatcher)MatcherFactory.getMatcherInstance(MatchersRegistry.ReferenceAlignment,0);
			MatcherParametersDialog dialog = new MatcherParametersDialog(refMatcher);
			if(dialog.parametersSet()) {
				refMatcher.setParam(dialog.getParameters());
				refMatcher.setThreshold(refMatcher.getDefaultThreshold());
				refMatcher.setMaxSourceAlign(refMatcher.getDefaultMaxSourceRelations());
				refMatcher.setMaxTargetAlign(refMatcher.getDefaultMaxTargetRelations());
				refMatcher.match();
				AlignmentSet referenceSet = refMatcher.getAlignmentSet(); //class + properties
				AbstractMatcher toBeEvaluated;
				AlignmentSet evaluateSet;
				ReferenceEvaluationData rd;
				String report="Reference Evaluation Complete\n\n";
				for(int i = 0; i < rowsIndex.length; i++) {
					toBeEvaluated = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
					evaluateSet = toBeEvaluated.getAlignmentSet();
					rd = ReferenceEvaluator.compare(evaluateSet, referenceSet);
					toBeEvaluated.setRefEvaluation(rd);
					report+=i+" "+toBeEvaluated.getName().getMatcherName()+"\n\n";
					report +=rd.getReport()+"\n";
					AbstractTableModel model = (AbstractTableModel)matchersTablePanel.getTable().getModel();
					model.fireTableRowsUpdated(toBeEvaluated.getIndex(), toBeEvaluated.getIndex());
				}
				Utility.displayTextAreaPane(report,"Reference Evaluation Report");
			}
			dialog.dispose();
			ui.redisplayCanvas();
		}
		
	}
	
	//WARNING THIS METHOD IS INVOKED BY matchSelected(), but by newManual(), basically this method should be invoked anytime we want to invoke a specific matcher, like if we selected it and clicked match button.
	public void match(AbstractMatcher currentMatcher, boolean defaultParam) throws Exception{
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows(); //indexes in the table correspond to the indexes of the matchers in the matcherInstances list in core class
		int selectedMatchers = rowsIndex.length;
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load a Source and Target ontologies before running any matcher\nClick on File Menu and select Open Ontology functions ", null);
		}
		else if(currentMatcher.getMinInputMatchers() > selectedMatchers ) {
			Utility.displayErrorPane("Select at least "+currentMatcher.getMinInputMatchers()+" matchings from the table to run this matcher.", null);
		}
		else {
			//Set matchers into the abstractmatcher VERY IMPORTANT to set them before invoking the parameter panel, infact the parameter panel may need to work on inputMatchers also.
			for(int i = 0; i<rowsIndex.length && i< currentMatcher.getMaxInputMatchers(); i++) {
				AbstractMatcher input = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
				currentMatcher.addInputMatcher(input);
			}
			boolean everythingOk = true;
			//Asking parameters before setting input matcher list, just because the user can still cancel the operation
			if(currentMatcher.needsParam()) {
				everythingOk = false;
				MatcherParametersDialog dialog = new MatcherParametersDialog(currentMatcher);
				if(dialog.parametersSet()) {
					currentMatcher.setParam(dialog.getParameters());
					everythingOk = true;
				}
				dialog.dispose();
			}
			if(everythingOk) {
				if(defaultParam) {
					currentMatcher.setThreshold(currentMatcher.getDefaultThreshold());
					currentMatcher.setMaxSourceAlign(currentMatcher.getDefaultMaxSourceRelations());
					currentMatcher.setMaxTargetAlign(currentMatcher.getDefaultMaxTargetRelations());
				}
				else {
					currentMatcher.setThreshold(Utility.getDoubleFromPercent((String)thresholdCombo.getSelectedItem()));
					currentMatcher.setMaxSourceAlign(Utility.getIntFromNumRelString((String)sRelationCombo.getSelectedItem()));
					currentMatcher.setMaxTargetAlign(Utility.getIntFromNumRelString((String)tRelationCombo.getSelectedItem()));
				}
				
				
				// The dialog will start the matcher in a background thread, show progress as the matcher is running, and show the report at the end.
				ProgressDialog progress = new ProgressDialog(currentMatcher);  // Program flow will not continue until the dialog is dismissed. (User presses Ok or Cancel)
				
				if(!currentMatcher.isCancelled()) {  // If the algorithm finished successfully, add it to the control panel.
					matchersTablePanel.addMatcher(currentMatcher);
					ui.redisplayCanvas();
				}	

				System.out.println("Matching Process Complete");
			}
		}
	}
	
	public void matchSelected() throws Exception{
		String matcherName = (String) matcherCombo.getSelectedItem();
		//the new matcher will put at the end of the list so at the end of the table so the index will be:
		int lastIndex = Core.getInstance().getMatcherInstances().size();
		AbstractMatcher currentMatcher = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), lastIndex);
		match(currentMatcher , false);
	}

	
	
	public MatchersTablePanel getTablePanel() {
		return matchersTablePanel;
	}
	
	public void userMatching(ArrayList<Alignment> alignments) {
		if(alignments.size()>0) {
			int[] rows = matchersTablePanel.getTable().getSelectedRows();
			if(rows != null) {
				if(rows.length == 0) {
					//lf no rows are selected it adds it to the UserMatching
					Core.getInstance().performUserMatching(0, alignments);
					matchersTablePanel.updatedRows(0,0);
				}
				else {
					for(int i=0; i < rows.length;i++) {
						Core.getInstance().performUserMatching(rows[i], alignments);
						
					}
					matchersTablePanel.updatedRows(rows[0], rows[rows.length-1]);
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
			setDefaultCommonParameters();
			
		}
	}

	
	public void setDefaultCommonParameters() {
		String matcherName = (String) matcherCombo.getSelectedItem();
		AbstractMatcher a = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //i'm just using a fake instance so the algorithm code is not important i put 0 but maybe anythings
		thresholdCombo.setSelectedItem(Utility.getNoFloatPercentFromDouble(a.getDefaultThreshold()));
		sRelationCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxSourceRelations()));
		tRelationCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxTargetRelations()));
	}
	
	public void clearAll() {
		//don't put this code into resetMatchings because resetMatching is used by the system also in other situation when the confirmation is not required by the user.
		boolean ok = Utility.displayConfirmPane("This operation will clear all the matchings prevously calculated.\nDo you want to continue?", null);
		if(ok) {
			resetMatchings();
		}
	}
	
	public void resetMatchings() {
		try {
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
			matchersTablePanel.deletedRows(firstRow, lastRow);
			ui.getCanvas().clearAllSelections();
			ui.redisplayCanvas();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane("Unexepcted System Error.\nTry to reset the system and repeat the operation.\nContact developers if the error persists.", null);
		}
	}
}
