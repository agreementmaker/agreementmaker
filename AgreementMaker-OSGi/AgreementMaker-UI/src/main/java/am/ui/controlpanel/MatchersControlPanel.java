package am.ui.controlpanel;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;

import am.AMException;
import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluator;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.ui.ExportDialog;
import am.ui.ImportDialog;
import am.ui.MatcherParametersDialog;
import am.ui.MatcherProgressDialog;
import am.ui.MatchingProgressDisplay;
import am.ui.QualityEvaluationDialog;
import am.ui.UICore;
import am.ui.controlpanel.table.MatchersControlPanelTableModel;
import am.ui.controlpanel.table.MatchersTablePanel;
import am.ui.matchingtask.MatchingTaskCreatorDialog;

public class MatchersControlPanel extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = -2258009700001283026L;


	private JButton btnMatch = new JButton("Match!");;
	private MatchersTablePanel matchersTablePanel;

	private JButton btnNewMatching = new JButton("New");;
	private JButton btnDelete = new JButton("Delete");;
	private JButton btnClearAllMatchings = new JButton("Clear All");
	private JButton btnCopy = new JButton("Copy");
	private JButton btnEditMatrix = new JButton("Edit Similarity Matrix");
	private JButton btnRefEvaluate = new JButton("Reference Evaluation");
	private JButton btnQualityEvaluation = new JButton("Quality Evaluation");
	private JButton btnExportAlignments = new JButton("Export");
	private JButton btnImportAlignments = new JButton("Import");
	private JButton btnThresholdTuning = new JButton("Tuning");;
	
	public MatchersControlPanel() {
		super();
		init();
	}

	void init() {
		GroupLayout layout = new GroupLayout(this);
		
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Matching Tasks Control Panel"));
				
		//TABLE PANEL:center panel
		matchersTablePanel = new MatchersTablePanel();
		matchersTablePanel.getTable().addMouseListener(this);
		
		//JPANEL EDIT MATCHINGS
		btnMatch.addActionListener(this);
		btnNewMatching.addActionListener(this);
		btnDelete.addActionListener(this);
		btnRefEvaluate.addActionListener(this);
		btnClearAllMatchings.addActionListener(this);
		btnCopy.addActionListener(this);
		btnEditMatrix.addActionListener(this);
		btnQualityEvaluation.addActionListener(this);
		btnExportAlignments.addActionListener(this);
		btnImportAlignments.addActionListener(this);
		btnThresholdTuning.addActionListener(this);
		
		JPanel fauxToolBar = new JPanel();  // a toolbar wannabe
		fauxToolBar.setLayout(new FlowLayout(FlowLayout.LEADING));
		fauxToolBar.add(btnMatch);
		fauxToolBar.add(btnNewMatching);
		fauxToolBar.add(btnCopy);
		fauxToolBar.add(btnDelete);
		fauxToolBar.add(btnClearAllMatchings);
		fauxToolBar.add(btnExportAlignments);
		fauxToolBar.add(btnRefEvaluate);
		fauxToolBar.add(btnQualityEvaluation);
		fauxToolBar.add(btnExportAlignments);
		fauxToolBar.add(btnImportAlignments);
		fauxToolBar.add(btnThresholdTuning);
		
		// Layout
		layout.setHorizontalGroup( layout.createParallelGroup() 
				.addComponent(fauxToolBar)
				.addComponent(matchersTablePanel)
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup() 
				.addComponent(fauxToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
				.addComponent(matchersTablePanel)
		);
		
		setLayout(layout);
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			Object obj = e.getSource();

			if(obj == btnMatch) {
				btnMatchClick();
			}
			else if(obj == btnDelete) {
				btnDeleteClick();
			}
			else if(obj == btnRefEvaluate) {
				btnEvaluateClick();
			}
			else if(obj == btnClearAllMatchings) {
				clearAll();
			}
			else if (obj == btnCopy) {
				copy();
			}
			else if(obj == btnExportAlignments) {
				export();
			}
			else if(obj == btnImportAlignments) {
				btnImportClick();
			}
			else if(obj == btnNewMatching) {
				newManual();
			}
			else if(obj == btnQualityEvaluation) {
				qualityEvaluation();
			}
			else if(obj == btnThresholdTuning) {
				tuning();
			}
		}

		//ATTENTION: the exception of the match() method of a matcher is not catched here because it runs in a separated thread
		catch(AMException ex2) {
			Utility.displayMessagePane(ex2.getMessage(), null);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR + "\n" + ex.getMessage() , null);
		}
	}
	
	private void qualityEvaluation() throws Exception {
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load Source and Target ontologies before quality evaluation.\nClick on File Menu and select Open Ontology functions ", null);
			return;
		}
		
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else {
			QualityEvaluationDialog qDialog = new QualityEvaluationDialog();
			if(qDialog.isSuccess()) {
				QualityMetricRegistry quality = (QualityMetricRegistry) qDialog.qualCombo.getSelectedItem();
				AbstractMatcher toBeEvaluated;
				String report= "Quality Evaluation Complete\n\n";
				QualityEvaluationData q;
				for(int i = 0; i < rowsIndex.length; i++) {
					
					toBeEvaluated = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
					report+=(i+1)+" "+toBeEvaluated.getName()+"\n\n";
					q = QualityEvaluator.evaluate(toBeEvaluated, QualityEvaluator.getQM(quality));
					if(!q.isLocal()) {
						report+= quality+"\n";
						report+= "Class Alignments Quality: "+Utility.getOneDecimalPercentFromDouble(q.getGlobalClassMeasure())+"\n" ;
						report+= "Property Alignments Quality: "+Utility.getOneDecimalPercentFromDouble(q.getGlobalPropMeasure())+"\n" ;
						report+= "\n";
						
					}
					else {
						List<Node> classList;
						List<Node> propList; 
						report+= quality+"\n";
						if(q.isSourceOntology()) {
							report+= "This quality is local in respect to source concepts: \n\n";
							classList = Core.getInstance().getSourceOntology().getClassesList();
							propList = Core.getInstance().getSourceOntology().getPropertiesList();
						}
						else {
							report+= "This quality is local in respect to target concepts: \n\n";
							classList = Core.getInstance().getTargetOntology().getClassesList();
							propList = Core.getInstance().getTargetOntology().getPropertiesList();
						}
						report+= "Class Alignments Quality:\n";
						for(int k = 0; k < q.getLocalClassMeasures().length; k++) {
							Node n = classList.get(k);
							report+= (k+1)+" "+n.getLocalName()+": "+Utility.getOneDecimalPercentFromDouble(q.getLocalClassMeasures()[k])+"\n" ;
						}
						report+= "\n";
						report+= "Property Alignments Quality:\n";
						for(int k = 0; k < q.getLocalPropMeasures().length; k++) {
							Node n = propList.get(k);
							report+= (k+1)+" "+n.getLocalName()+": "+Utility.getOneDecimalPercentFromDouble(q.getLocalPropMeasures()[k])+"\n" ;
						}
						report+= "\n";	
					}
					toBeEvaluated.setQualEvaluation(q);
					AbstractTableModel model = (AbstractTableModel)matchersTablePanel.getTable().getModel();
					model.fireTableRowsUpdated(toBeEvaluated.getIndex(), toBeEvaluated.getIndex());
					/*
					else {
						q = QualityEvaluator.evaluate(toBeEvaluated, QualityEvaluator.QUALITIES[j]);
						report+= QualityEvaluator.QUALITIES[j]+"\n";
						report+= "Average of local Classes Quality: "+Utility.getAverageOfArray(q.getLocalClassMeasures())+"\n" ;
						report+= "Average of local Properties Quality: "+Utility.getAverageOfArray(q.getLocalPropMeasures())+"\n" ;
						//Add the list of local qualities here
						report+= "\n";
					}
					*/
				}
				Utility.displayTextAreaPane(report,"Quality Evaluation Report");

			}
			qDialog.dispose();
		}
		
	}

	public void newManual() throws Exception {
		String matcherName = JOptionPane.showInputDialog("Name for the new matcher? (Cancel for default)", MatchersRegistry.UserManual.getMatcherName());
		int lastIndex = Core.getInstance().getMatcherInstances().size();
		final AbstractMatcher manualMatcher = MatcherFactory.getMatcherInstance(UserManualMatcher.class);
		if( manualMatcher.needsParam() ) {
			MatcherParametersDialog d = new MatcherParametersDialog(manualMatcher, false, true);
			if( d.parametersSet() ) manualMatcher.setParam(d.getParameters());
			else return;
		}
		if( matcherName != null ) manualMatcher.setName(matcherName);
		
		final MatchingTask t = new MatchingTask(manualMatcher, manualMatcher.getParam(), 
				new MwbmSelection(), new DefaultSelectionParameters());
		
		// TODO: There must be a better way to do this!
		manualMatcher.addProgressDisplay(new MatchingProgressDisplay() {
			private boolean ignore = false;
			@Override public void setProgressLabel(String label) {}
			@Override public void setIndeterminate(boolean indeterminate) {}
			@Override public void scrollToEndOfReport() {}
			@Override public void propertyChange(PropertyChangeEvent evt) {}
			@Override public void matchingStarted(AbstractMatcher m) {}
			@Override public void matchingComplete() {
				if( ignore ) return;
				if(!manualMatcher.isCancelled()) {  // If the algorithm finished successfully, add it to the control panel.
					Core.getInstance().addMatchingTask(t);
				}
				manualMatcher.removeProgressDisplay(this);
			}
			@Override public void ignoreComplete(boolean ignore) {this.ignore = ignore;}
			@Override public void clearReport() {}
			@Override public void appendToReport(String report) {}
		});
		
		new MatcherProgressDialog(t);
	}
	
	public void btnDeleteClick() throws Exception {
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else if(Utility.displayConfirmPane(rowsIndex.length+" matchers will be deleted.\n Do you want to continue?", null)) {
			Core core = Core.getInstance();
			LinkedList<MatchingTask> deleteList = new LinkedList<MatchingTask>();
			for(int i = 0; i < rowsIndex.length; i++) {// I need to build a list because indexes will be modified so i can't access them using the rowsindex structures
				final int deleteIndex = rowsIndex[i];
				deleteList.add(core.getMatchingTasks().get(deleteIndex));
			}
			for(int i = 0; i< deleteList.size(); i++) {
				final MatchingTask toBeDeleted = deleteList.get(i);
				if(i == 0 && toBeDeleted.matchingAlgorithm.getName().equals("User Manual Matching")) {
					//YOU CAN'T DELETE THE FIRST USER MATCHING just clear the matchings previusly created
					Utility.displayMessagePane("The default "+MatchersRegistry.UserManual + " can't be deleted.\nOnly alignments will be cleared.", null);
					try {
						toBeDeleted.match();//reinitialize the user matching as an empty one
						matchersTablePanel.updatedRows(0, 0);
					}
					catch(Exception ex) {
						Utility.displayErrorPane(ex.getMessage(), null);
					}
				}
				else {
					matchersTablePanel.removeTask(toBeDeleted);
				}
				matchersTablePanel.deletedRows(rowsIndex[0], rowsIndex[rowsIndex.length-1]);
				UICore.getUI().redisplayCanvas();
				
			}
		}
	}

	public void export() {
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load Source and Target ontologies before exporting alignments.\nClick on File Menu and select Open Ontology functions ", null);
			return;
		}
		
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else 	new ExportDialog(UICore.getUI().getUIFrame()); //demand control to the savefile dialog which since is modal will take care of everything
	}
	
	public void btnImportClick() throws Exception {
		
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load Source and Target ontologies before importing alignments.\nClick on File Menu and select Open Ontology functions ", null);
			return;
		}
		
		new ImportDialog();
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
				MatchingTask t = new MatchingTask(aCopy, aCopy.getParam(), 
						new MwbmSelection(), new DefaultSelectionParameters());
				Core.getInstance().addMatchingTask(t);
			}
			Utility.displayMessagePane(rowsIndex.length+" matchers have been copied.\n",null);
			UICore.getUI().redisplayCanvas();
		}
	}
	
	public void btnEvaluateClick() throws Exception{
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load Source and Target ontologies before reference evaluation.\nClick on File Menu and select Open Ontology functions ", null);
			return;
		}
		
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected from the control panel table.", null);
		}
		else {
			//Run the reference alignment matcher to get the list of alignments in reference file, we are not going to add it in the table list
			ReferenceAlignmentMatcher refMatcher = (ReferenceAlignmentMatcher)MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			MatcherParametersDialog dialog = new MatcherParametersDialog(refMatcher, false, false);
			if(dialog.parametersSet()) {
				DefaultMatcherParameters param = dialog.getParameters();
				ReferenceAlignmentParameters params = (ReferenceAlignmentParameters)param;
				refMatcher.setParam(param);
				
				//When working with sub-superclass relations the cardinality is always ANY to ANY
				if(!params.onlyEquivalence){
					params.maxSourceAlign = AbstractMatcher.ANY_INT;
					params.maxTargetAlign = AbstractMatcher.ANY_INT;
				}
				refMatcher.match();
									
				
				// TODO: Move the if-else into ReferenceAlignmentMatcher
				Alignment<Mapping> referenceSet;
				if( refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned() ) {
					referenceSet = refMatcher.getAlignment(); //class + properties
				} else if( refMatcher.areClassesAligned() ) {
					referenceSet = refMatcher.getClassAlignmentSet();
				} else if( refMatcher.arePropertiesAligned() ) {
					referenceSet = refMatcher.getPropertyAlignmentSet();
				} else {
					// empty set?
					referenceSet = new Alignment<Mapping>(Ontology.ID_NONE, Ontology.ID_NONE);
				}
				MatchingTask toBeEvaluated;
				Alignment<Mapping> evaluateSet;
				ReferenceEvaluationData rd;
				String report="Reference Evaluation Complete\n\n";
				for(int i = 0; i < rowsIndex.length; i++) {
					toBeEvaluated = Core.getInstance().getMatchingTasks().get(rowsIndex[i]);
					//evaluateSet = null;
					if( refMatcher.areClassesAligned() && refMatcher.arePropertiesAligned() ) {
						evaluateSet = toBeEvaluated.selectionResult.getAlignment();
					} else if( refMatcher.areClassesAligned() ) {
						evaluateSet = toBeEvaluated.selectionResult.getClassAlignmentSet();
					} else if( refMatcher.arePropertiesAligned() ) {
						evaluateSet = toBeEvaluated.selectionResult.getPropertyAlignmentSet();
					} else {
						evaluateSet = new Alignment<Mapping>(Ontology.ID_NONE,Ontology.ID_NONE); // empty
					}
					
					rd = ReferenceEvaluator.compare(evaluateSet, referenceSet);
					toBeEvaluated.matchingAlgorithm.setRefEvaluation(rd);
					report+=i+" "+toBeEvaluated.matchingAlgorithm.getName()+"\n\n";
					report +=rd.getReport()+"\n";
					AbstractTableModel model = (AbstractTableModel)matchersTablePanel.getTable().getModel();
					model.fireTableRowsUpdated(rowsIndex[i], rowsIndex[i]);
				}
				Utility.displayTextAreaPane(report,"Reference Evaluation Report");
			}
			dialog.dispose();
			UICore.getUI().redisplayCanvas();
		}
		
	}
	
	//WARNING THIS METHOD IS INVOKED BY matchSelected(), and by newManual(), basically this 
	//method should be invoked anytime we want to invoke a specific matcher, 
	// like if we selected it and clicked match button.
	public void btnMatchClick() throws Exception{
		
		// 1. Make sure ontologies are loaded.
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load Source and Target ontologies before running any matcher\nClick on File Menu and select Open Ontology functions ", null);
			return;
		}
		
		// 2. Create the MatcherParametersDialog and get the matcher with settings.
		//MatcherParametersDialog dialog = new MatcherParametersDialog();
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		
		MatchingTaskCreatorDialog dialog = new MatchingTaskCreatorDialog(sourceOntology, targetOntology);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setVisible(true);
		
		final AbstractMatcher currentMatcher;
		final MatchingTask matchingTask = dialog.getMatchingTask();
		
		// Fill inputTasks
		int[] selectedTasks = getTablePanel().getTable().getSelectedRows();
		MatchersControlPanelTableModel model = (MatchersControlPanelTableModel)getTablePanel().getTable().getModel();
		for( int i : selectedTasks ) {
			if( matchingTask.inputMatchingTasks == null ) {
				matchingTask.inputMatchingTasks = new LinkedList<MatchingTask>();
			}
			matchingTask.inputMatchingTasks.add(model.data.get(i));
		}
		
		if(matchingTask == null) return;
			
		currentMatcher = (AbstractMatcher) matchingTask.matchingAlgorithm;
		currentMatcher.setParameters(matchingTask.matcherParameters);
				
		// 3. Bring up MatcherProgressDialog which runs the matcher.
		// The dialog will start the matcher in a background thread, show progress as the matcher is running, and show the report at the end.
		
		// This dialog is not modal.
		new MatcherProgressDialog(matchingTask);
		
	}

	public void tuning() throws Exception{
		if(!Core.getInstance().ontologiesLoaded() ) {
			Utility.displayErrorPane("You have to load Source and Target ontologies before tuning.\nClick on File Menu and select Open Ontology functions ", null);
			return;
		}
		
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length == 0) {
			Utility.displayErrorPane("No matchers selected", null);
		}
		else {
			//Run the reference alignment matcher to get the list of alignments in reference file, we are not going to add it in the table list
			ReferenceAlignmentMatcher refMatcher = (ReferenceAlignmentMatcher)MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			MatcherParametersDialog dialog = new MatcherParametersDialog(refMatcher, false, false);
			if(dialog.parametersSet()) {
				final DefaultMatcherParameters param = dialog.getParameters();
				
				param.threshold = refMatcher.getDefaultThreshold();
				param.maxSourceAlign = refMatcher.getDefaultMaxSourceRelations();
				param.maxTargetAlign = refMatcher.getDefaultMaxTargetRelations();
				
				refMatcher.setParameters(param);
				refMatcher.match();

				Alignment<Mapping> referenceSet = refMatcher.getAlignment(); //class + properties
				AbstractMatcher toBeEvaluated;
				
				String report="Tuning Complete\n\n";
				report +="Measure are displayed in this order:\nThreshold: value - Measures: precision, recall, Fmeasure\n\n";
				
				//You have to use this array instead 
				//double[] thresholds = Utility.STEPFIVE;  // TODO: Make it so the user can select this from the UI.
				for(int i = 0; i < rowsIndex.length; i++) {
					toBeEvaluated = Core.getInstance().getMatcherInstances().get(rowsIndex[i]);
					// FIXME: Migrate to ThresholdAnalysis.java
					//ThresholdAnalysisData data = AlignmentUtilities.thresholdAnalysis(toBeEvaluated, referenceSet);
					//report += data.getReport();
					AbstractTableModel model = (AbstractTableModel)matchersTablePanel.getTable().getModel();
					model.fireTableRowsUpdated(toBeEvaluated.getIndex(), toBeEvaluated.getIndex());
				}
				Utility.displayTextAreaWithDim(report,"Reference Evaluation Report", 35, 60);
			}
			dialog.dispose();
			UICore.getUI().redisplayCanvas();
		}
		
	}
	

/*	public void disagreementEval() throws Exception {
		int[] rowsIndex = matchersTablePanel.getTable().getSelectedRows();
		if(rowsIndex.length < 1) {
			Utility.displayErrorPane("Select two or more matchers", null);
		}
		else if(Utility.displayConfirmPane("AgreementMaker will now perform disagreement evaluation on the selected matchers", null)) {
			Core core = Core.getInstance();
			int sourceDim = core.getSourceOntology().getClassesList().size(),
				targetDim = core.getTargetOntology().getClassesList().size();
			
			SimilarityMatrix localMatrix = new SimilarityMatrix(sourceDim, targetDim, alignType.aligningClasses);
			
			for(int i = 0; i < sourceDim; i++){
				for(int j = 0; j < targetDim; j++){
					localMatrix.set(i, j, new Mapping(core.getSourceOntology().getClassesList().get(i),
														core.getTargetOntology().getClassesList().get(j),
														disagreementComp(i, j, core.getMatcherInstances())));
					System.out.println(disagreementComp(i, j, core.getMatcherInstances()));
				}
			}
			
			// to be managed better overall (we should not need the selected matcher here
			AbstractMatcher selectedMatcher = Core.getInstance().getMatcherInstances().get(0);			
			MatrixPlotPanel mp = new MatrixPlotPanel( selectedMatcher, localMatrix, null);
			
			mp.getPlot().draw(false);
			JPanel plotPanel = new JPanel();
			plotPanel.add(mp);
			Core.getUI().addTab("Disagreement Tab", null , plotPanel , selectedMatcher.getName().getMatcherName());
		}
	}
	
	public double disagreementComp(int row, int column, ArrayList<AbstractMatcher> involvedMatchers){
		int totalMatchers = matchersTablePanel.getTable().getSelectedRows().length;
		double mean = 0.0, var = 0.0;
		for(int i = 0; i < totalMatchers; i++){
			mean += involvedMatchers.get(i).getClassesMatrix().getSimilarity(row, column);
		}
		mean /= totalMatchers;
		for(int i = 0; i < totalMatchers; i++){
			var += (mean - involvedMatchers.get(i).getClassesMatrix().getSimilarity(row, column)) *
					(mean - involvedMatchers.get(i).getClassesMatrix().getSimilarity(row, column));
		}
		return mean;
	}*/
	
		
	
	/////////////////////////////////////////////////PANEL METHODS
	
	

	// TODO: Need to implement methods so we don't have to expose the matchers table panel if we need to get which matchers are selected
	public MatchersTablePanel getTablePanel() {
		return matchersTablePanel;
	}
	
	public void userMatching(ArrayList<Mapping> alignments) {
		try {
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
					UICore.getUI().redisplayCanvas();
					
				}
			}
		} catch(Exception e) {
			Utility.displayErrorPane("There was a problem in creating the mapping.\n\n"+e.getMessage(), "Cannot create mapping");
			e.printStackTrace();
		}
	}
	

	/**
	 * This method takes care of the action perfromed by one of the check boxes
	 *
	 * @param e ActionEvent object
	 */
/*	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getItemSelectable();
		if(obj == matcherCombo) {
			setDefaultCommonParameters();
			
		}
	}*/

	
/*	public void setDefaultCommonParameters() {
		String matcherName = (String) matcherCombo.getSelectedItem();
		AbstractMatcher a = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //i'm just using a fake instance so the algorithm code is not important i put 0 but maybe anythings
		thresholdCombo.setSelectedItem(Utility.getNoDecimalPercentFromDouble(a.getDefaultThreshold()));
		sRelationCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxSourceRelations()));
		tRelationCombo.setSelectedItem(Utility.getStringFromNumRelInt(a.getDefaultMaxTargetRelations()));
	}*/
	
	public boolean clearAll() {
		//don't put this code into resetMatchings because resetMatching is used by the system also in other situation when the confirmation is not required by the user.
		boolean ok = Utility.displayConfirmPane("This operation will remove all matching tasks.\nDo you want to continue?", null);
		if(ok) {
			resetMatchings();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Reset all the matching results currently in the MatchersControlPanel.
	 */
	public void resetMatchings() {
		try {
			//Take the UserManualMatcher and run it for the first time to create empty matrix and alignmentSet
			UserManualMatcher userMatcher=new UserManualMatcher();
			userMatcher.setSourceOntology(Core.getInstance().getSourceOntology());
			userMatcher.setTargetOntology(Core.getInstance().getTargetOntology());
			try {
				userMatcher.match();
			}
			catch(AMException ex){
				Utility.displayMessagePane(ex.getMessage(), null);
			}
			
			Core.getInstance().getMatcherResults().clear();
			Core.getInstance().getMatchingTasks().clear();
			
			MatchingTask t = new MatchingTask(userMatcher, userMatcher.getParam(), 
					new MwbmSelection(), new DefaultSelectionParameters());
			Core.getInstance().addMatchingTask(t);
			
			//update the table
			Core.getInstance().removeAllMatchingTasks();
			matchersTablePanel.dataChanged();
			UICore.getUI().getCanvas().clearAllSelections();
			UICore.getUI().redisplayCanvas();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR+ex.getMessage(), null);
		}
	}
	
/*	public void addMatcher( AbstractMatcher a ) {
		matchersTablePanel.addMatcher(a);
	}*/
	
	//public String getComboboxSelectedItem() { return (String) matcherCombo.getSelectedItem(); }
	
	public MatchersTablePanel getMatchersTablePanel() {
		return matchersTablePanel;
	}

	@Override public void mouseClicked(MouseEvent e) { 
		if( e.getSource() == matchersTablePanel.getTable() 
				&& e.getButton() == MouseEvent.BUTTON3 ) {
			// right click
			MatchersControlPanelPopupMenu popup = new MatchersControlPanelPopupMenu(this);
			popup.show(matchersTablePanel.getTable(), e.getX(), e.getY());
		}
	}

	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
}
