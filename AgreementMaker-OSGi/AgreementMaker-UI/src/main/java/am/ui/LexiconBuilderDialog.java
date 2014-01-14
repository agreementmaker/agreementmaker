package am.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import am.Utility;
import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.utility.LocalnameComparator;

import com.hp.hpl.jena.rdf.model.Property;
import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.CheckBoxListSelectionModel;


public class LexiconBuilderDialog extends JDialog implements ListSelectionListener, ActionListener {

	private static final long serialVersionUID = -1139168102581149499L;

	// these are just for being used in correctly saving and restoring the preferences
	private CheckBoxListSelectionModel sourceSynonym;
	private CheckBoxListSelectionModel sourceDefinition;
	private CheckBoxListSelectionModel targetSynonym;
	private CheckBoxListSelectionModel targetDefinition;
	
	private List<Property> sourceProperties;	
	private List<Property> targetProperties;

	private JButton btnOk;
	private JButton btnCancel;

	private JCheckBox sourceUseLocalnames, targetUseLocalnames;
	private JCheckBox sourceUseSTLexicon, targetUseSTLexicon;

	private static final String PREF_SOURCE_USE_STLEXICON = "PREF_SOURCE_USESTLEXICON";
	private static final String PREF_TARGET_USE_STLEXICON = "PREF_TARGET_USESTLEXICON";
	
	public LexiconBuilderDialog() throws Exception {
		super(UICore.getUI().getUIFrame(), "Lexicon Builder Settings", true);
		
		setMinimumSize(new Dimension(600,400));
		
		btnOk = new JButton("Build lexicons");
		btnOk.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		
		if( sourceOntology == null || targetOntology == null ) throw new Exception("Ontologies are not loaded.");
		
		// instantiate our panels
		JPanel sourcePanel = createAnnotationConfigurationPanel(sourceOntology);
		JPanel targetPanel = createAnnotationConfigurationPanel(targetOntology);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		if(sourceOntology.getTitle() != null && sourceOntology.getTitle().length() > 15 )
			tabbedPane.addTab( "Source (" + sourceOntology.getTitle().substring(0, 15) + "...)", null, sourcePanel, "The lexicon settings for the source ontology.");
		else
			tabbedPane.addTab( "Source (" + sourceOntology.getTitle() + ")", null, sourcePanel, "The lexicon settings for the source ontology.");
		
		if(targetOntology.getTitle() != null && targetOntology.getTitle().length() > 15 )
			tabbedPane.addTab( "Target (" + targetOntology.getTitle().substring(0, 15) + "...)", null, targetPanel, "The lexicon settings for the target ontology.");
		else
			tabbedPane.addTab( "Target (" + targetOntology.getTitle() + ")", null, targetPanel, "The lexicon settings for the target ontology.");
		
		GroupLayout panelLayout = new GroupLayout(getRootPane());
		panelLayout.setAutoCreateContainerGaps(true);
		panelLayout.setAutoCreateGaps(true);

		panelLayout.setHorizontalGroup( panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING) 
				.addComponent(tabbedPane)
				.addGroup( panelLayout.createSequentialGroup()
						.addComponent(btnCancel)
						.addComponent(btnOk)
				)
		);
		
		panelLayout.setVerticalGroup( panelLayout.createSequentialGroup() 
				.addComponent(tabbedPane)
				.addGroup( panelLayout.createParallelGroup()
						.addComponent(btnCancel)
						.addComponent(btnOk)
				)
		);
		
		getRootPane().setLayout(panelLayout);
		getRootPane().setDefaultButton(btnOk);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		Action actionListener = new AbstractAction() {
			private static final long serialVersionUID = -723158360969494574L;

			public void actionPerformed(ActionEvent actionEvent) {
				btnCancel.doClick();
			}
		};
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_FOCUSED);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}
	
	private JPanel createAnnotationConfigurationPanel(Ontology sourceOntology) {
		JPanel sourceSynonymPanel = createSynonymPanel(sourceOntology);
		JPanel sourceDefinitionPanel = createDefinitionPanel(sourceOntology);
		
		JPanel sourcePanel = new JPanel();
		
		GroupLayout panelLayout = new GroupLayout(sourcePanel);
		panelLayout.setAutoCreateContainerGaps(true);
		panelLayout.setAutoCreateGaps(true);
		
		panelLayout.setHorizontalGroup( panelLayout.createSequentialGroup()
				.addComponent( sourceSynonymPanel )
				.addComponent( sourceDefinitionPanel )
		);
		
		panelLayout.setVerticalGroup( panelLayout.createParallelGroup()
				.addComponent( sourceSynonymPanel )
				.addComponent( sourceDefinitionPanel )
		);
		
		
		sourcePanel.setLayout(panelLayout);
		return sourcePanel;
	}

	private JPanel createDefinitionPanel(Ontology ont) {
		JPanel panel = new JPanel();
		panel.setBorder( new TitledBorder("Definitions:") );
		
			
		CheckBoxList list = createAnnotationPropertyCheckBoxList(ont);
		
		// determine the correct key
		String key = "";
		if( Core.getInstance().getSourceOntology() == ont )  {
			sourceDefinition = list.getCheckBoxListSelectionModel();
			key = "PREF_SOURCE_DEFINITION_";
		}
		else {
			targetDefinition = list.getCheckBoxListSelectionModel();
			key = "PREF_TARGET_DEFINITION_";
		}
		
		// restore previous checked boxes
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		
		for( int i = 0; i < list.getModel().getSize(); i++ ) {
			if( prefs.getBoolean( key + list.getModel().getElementAt(i).toString(), false) == true ) {
				list.getCheckBoxListSelectionModel().addSelectionInterval(i, i);
			}
		}
		
		JScrollPane annotationScrollPane = new JScrollPane(list);
		//JScrollPane allScrollPane = new JScrollPane(allPropertiesList);
		
		GroupLayout panelLayout = new GroupLayout(panel);
		panelLayout.setAutoCreateContainerGaps(true);
		panelLayout.setAutoCreateGaps(true);
		
		JLabel annotationLabel = new JLabel("Annotation properties:");
		
		panelLayout.setHorizontalGroup( panelLayout.createParallelGroup()
				.addComponent( annotationLabel )
				.addComponent(annotationScrollPane)

		);
		
		panelLayout.setVerticalGroup( panelLayout.createSequentialGroup()
				.addComponent(annotationLabel )
				.addComponent(annotationScrollPane)

		);
		
		panel.setLayout(panelLayout);
		
		return panel;
	}

	private JPanel createSynonymPanel(Ontology ont) {
		
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		
		JPanel panel = new JPanel();
		panel.setBorder( new TitledBorder("Synonyms:") );
		
		JCheckBox chkUseLocalnames;
		if( ont == Core.getInstance().getSourceOntology() ) { 
			sourceUseLocalnames = new JCheckBox("Include local names");
			chkUseLocalnames = sourceUseLocalnames;
			chkUseLocalnames.setSelected(prefs.getBoolean("PREF_SOURCE_USELOCALNAMES", false));
		} else {
			targetUseLocalnames = new JCheckBox("Include local names");
			chkUseLocalnames = targetUseLocalnames;
			chkUseLocalnames.setSelected(prefs.getBoolean("PREF_TARGET_USELOCALNAMES", false));
		}
		
		chkUseLocalnames.addActionListener(this);
		
		JCheckBox chkUseSTLexicon;
		if( ont == Core.getInstance().getSourceOntology() ) {
			sourceUseSTLexicon = new JCheckBox("Compute synonym terms.");
			chkUseSTLexicon = sourceUseSTLexicon;
			chkUseSTLexicon.setSelected(prefs.getBoolean(PREF_SOURCE_USE_STLEXICON, false));
		} else {
			targetUseSTLexicon = new JCheckBox("Compute synonym terms.");
			chkUseSTLexicon = targetUseSTLexicon;
			chkUseSTLexicon.setSelected(prefs.getBoolean(PREF_TARGET_USE_STLEXICON, false));
		}
		
		chkUseSTLexicon.addActionListener(this);
		
		CheckBoxList list = createAnnotationPropertyCheckBoxList(ont);

		// determine the correct key
		String key = "";
		if( Core.getInstance().getSourceOntology() == ont )  {
			sourceSynonym = list.getCheckBoxListSelectionModel();
			key = "PREF_SOURCE_SYNONYM_";
		}
		else {
			targetSynonym = list.getCheckBoxListSelectionModel();
			key = "PREF_TARGET_SYNONYM_";
		}
		
		// restore previous checked boxes		
		for( int i = 0; i < list.getModel().getSize(); i++ ) {
			if( prefs.getBoolean( key + list.getModel().getElementAt(i).toString(), false) == true ) {
				list.getCheckBoxListSelectionModel().addSelectionInterval(i, i);
			}
		}
		
		JScrollPane annotationScrollPane = new JScrollPane(list);
		

		GroupLayout panelLayout = new GroupLayout(panel);
		panelLayout.setAutoCreateContainerGaps(true);
		panelLayout.setAutoCreateGaps(true);

		JLabel annotationLabel = new JLabel("Annotation properties:");
		
		panelLayout.setHorizontalGroup( panelLayout.createParallelGroup()				
				.addComponent(chkUseLocalnames)
				.addComponent(chkUseSTLexicon)
				.addComponent( annotationLabel )
				.addComponent(annotationScrollPane)
		);
		
		panelLayout.setVerticalGroup( panelLayout.createSequentialGroup()
				.addComponent(chkUseLocalnames)
				.addComponent(chkUseSTLexicon)
				.addGap(10)
				.addComponent(annotationLabel )
				.addComponent(annotationScrollPane)
		);
		
		
		panel.setLayout(panelLayout);

		return panel;
	}
	
	
	private Map<Ontology,List<Property>> listHashMap = new HashMap<Ontology,List<Property>>(); 
	
	/**
	 * Create the list of annotation properties with checkboxes next to them.
	 */
	private CheckBoxList createAnnotationPropertyCheckBoxList(Ontology ont) {
		//OntModel m = ont.getModel();
		
		List<Property> annotationList = listHashMap.get(ont);
		if( annotationList == null ) {

			annotationList = new ArrayList<Property>();
			annotationList.addAll(ont.getModel().listAnnotationProperties().toList());
			
			Collections.sort(annotationList, new LocalnameComparator());
			
			listHashMap.put(ont, annotationList);
			if( ont == Core.getInstance().getSourceOntology() ) {
				sourceProperties = annotationList;
			} else {
				targetProperties = annotationList;
			}
		}
		
		DefaultListModel propertyList = new DefaultListModel();
		for( Property annotationProperty : annotationList ) propertyList.addElement(annotationProperty.getLocalName());
				
		CheckBoxList list = new CheckBoxList(propertyList);
		list.getCheckBoxListSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.getCheckBoxListSelectionModel().addListSelectionListener(this);
		
		return list;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if( e.getSource() instanceof CheckBoxListSelectionModel ) {
			
			CheckBoxListSelectionModel src = (CheckBoxListSelectionModel) e.getSource();
			
			String key = "";
			if( src == sourceSynonym ) { key = "PREF_SOURCE_SYNONYM_"; }
			if( src == sourceDefinition ) { key = "PREF_SOURCE_DEFINITION_"; }
			if( src == targetSynonym ) { key = "PREF_TARGET_SYNONYM_"; }
			if( src == targetDefinition ) { key = "PREF_TARGET_DEFINITION_"; }
						
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			
			CheckBoxListSelectionModel l = (CheckBoxListSelectionModel) e.getSource();
			
			for( int i = e.getFirstIndex(); i <= e.getLastIndex(); i++ ) {
				String s = (String) l.getModel().getElementAt(i);
				if( l.isSelectedIndex(i) ) { 
					prefs.putBoolean(key + s, true); 
				} 
				else { 
					prefs.remove(key + s); 
				}
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnCancel ) {
			setVisible(false);
			return;
		}
		
		if( e.getSource() == btnOk ) {
			LexiconBuilderParameters params = new LexiconBuilderParameters();
			
			params.sourceOntology = Core.getInstance().getSourceOntology();
			params.targetOntology = Core.getInstance().getTargetOntology();
			
			params.sourceUseLocalname = sourceUseLocalnames.isSelected();
			params.targetUseLocalname = targetUseLocalnames.isSelected();
			
			params.sourceUseSCSLexicon = sourceUseSTLexicon.isSelected();
			params.targetUseSCSLexicon = targetUseSTLexicon.isSelected();
			
			params.sourceSynonyms = new ArrayList<Property>();
			for( int i = 0; i < sourceProperties.size(); i++ ) {
				if( sourceSynonym.isSelectedIndex(i) ) params.sourceSynonyms.add(sourceProperties.get(i));
			}
			
			params.sourceDefinitions = new ArrayList<Property>();
			for( int i = 0; i < sourceProperties.size(); i++ ) {
				if( sourceDefinition.isSelectedIndex(i) ) params.sourceDefinitions.add(sourceProperties.get(i));
			}
			
			params.targetSynonyms = new ArrayList<Property>();
			for( int i = 0; i < targetProperties.size(); i++ ) {
				if( targetSynonym.isSelectedIndex(i) ) params.targetSynonyms.add(targetProperties.get(i));
			}
			
			params.targetDefinitions = new ArrayList<Property>();
			for( int i = 0; i < targetProperties.size(); i++ ) {
				if( targetDefinition.isSelectedIndex(i) ) params.targetDefinitions.add(targetProperties.get(i));
			}
			
			/*Ontology sourceOntology = Core.getInstance().getSourceOntology();
			OntModel sourceOntModel = sourceOntology.getModel();
			params.sourceLabelProperties = new ArrayList<Property>();
			Property sourceRDFSLabel = sourceOntModel.getProperty(Ontology.RDFS + "label"); // TODO: Make this customizable by the user.
			if( sourceRDFSLabel == null ) {
				// source ontology does not have RDFS label defined???
				params.sourceLabelProperties.addAll( params.sourceSynonyms );
			} else {
				// choose rdfs:label as the label property
				params.sourceLabelProperties.add( sourceRDFSLabel );
			}
			
			Ontology targetOntology = Core.getInstance().getTargetOntology();
			OntModel targetOntModel = targetOntology.getModel();
			params.targetLabelProperties = new ArrayList<Property>();
			Property targetRDFSLabel = targetOntModel.getProperty(Ontology.RDFS + "label"); // TODO: Make this customizable by the user.
			if( targetRDFSLabel == null ) {
				// target ontology does not have RDFS label defined???
				params.targetLabelProperties.addAll( params.targetSynonyms );
			} else {
				// choose rdfs:label as the label property
				params.targetLabelProperties.add( targetRDFSLabel );
			}*/
						
			Core.getLexiconStore().setParameters(params);
			
			try {
				Core.getLexiconStore().buildAll();
				setVisible(false);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Utility.displayErrorPane("Unexpected error while building lexicons.\n" + e1.getMessage(), "Runtime Exception");
			}
		}
		
		if( e.getSource() == sourceUseLocalnames ) {
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			if( sourceUseLocalnames.isSelected() ) {
				prefs.putBoolean("PREF_SOURCE_USELOCALNAMES", true);
			} else {
				prefs.remove("PREF_SOURCE_USELOCALNAMES");
			}
		}
		
		if( e.getSource() == targetUseLocalnames ) {
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			if( targetUseLocalnames.isSelected() ) {
				prefs.putBoolean("PREF_TARGET_USELOCALNAMES", true);
			} else {
				prefs.remove("PREF_TARGET_USELOCALNAMES");
			}
		}
		
		if( e.getSource() == sourceUseSTLexicon ) {
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			if( sourceUseSTLexicon.isSelected() ) {
				prefs.putBoolean(PREF_SOURCE_USE_STLEXICON, true);
			} else {
				prefs.remove(PREF_SOURCE_USE_STLEXICON);
			}
		}
		
		if( e.getSource() == targetUseSTLexicon ) {
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			if( targetUseSTLexicon.isSelected() ) {
				prefs.putBoolean(PREF_TARGET_USE_STLEXICON, true);
			} else {
				prefs.remove(PREF_TARGET_USE_STLEXICON);
			}
		}
		
		
	}
	
	
	
}
