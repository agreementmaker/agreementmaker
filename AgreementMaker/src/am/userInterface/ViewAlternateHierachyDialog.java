package am.userInterface;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import am.Utility;
import am.app.Core;
import am.app.ontology.Ontology;
import am.app.ontology.OntologyChangeEvent;
import am.app.ontology.OntologyChangeEvent.EventType;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.userInterface.canvas2.Canvas2;
import am.userInterface.canvas2.layouts.AlternateHierarchyLayout;
import am.userInterface.classic.AgreementMakerClassic;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class ViewAlternateHierachyDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 995776906361225272L;

	private JLabel lblSourceProperty, lblTargetProperty;
	private JComboBox cmbSourceProperties, cmbTargetProperties;
	private JButton btnOk, btnCancel;
	
	public ViewAlternateHierachyDialog() {
		super(Core.getUI().getUIFrame(), true);
		
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		
		cmbSourceProperties = new JComboBox();
		if( sourceOntology != null ) {
			OntModel sourceModel = sourceOntology.getModel();
			ExtendedIterator<OntProperty> propertyIter = sourceModel.listAllOntProperties();
			while( propertyIter.hasNext() ) {
				OntProperty currentProperty = propertyIter.next();
				cmbSourceProperties.addItem(currentProperty);
			}
		}
		
		cmbTargetProperties = new JComboBox();
		if( targetOntology != null ) {
			OntModel targetModel = targetOntology.getModel();
			
			ExtendedIterator<OntProperty> propertyIter = targetModel.listAllOntProperties();
			while( propertyIter.hasNext() ) {
				OntProperty currentProperty = propertyIter.next();
				cmbTargetProperties.addItem(currentProperty);
			}
		}
		
		lblSourceProperty = new JLabel("Source Property:");
		lblTargetProperty = new JLabel("Target Property:");
		
		
		btnOk = new JButton("Ok");
		btnOk.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		
		
		GroupLayout layout = createLayout(this.getContentPane());		
		
		this.setLayout(layout);
		this.pack();
		this.setLocationRelativeTo(Core.getUI().getUIFrame());
	}
	
	
	
	/**
	 * After the dialog UI components have been created, 
	 * create the layout for the dialog.
	 */
	private GroupLayout createLayout(Container dialog) {

		GroupLayout layout = new GroupLayout(dialog);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup( layout.createParallelGroup()
				.addGroup( layout.createSequentialGroup()
						.addComponent(lblSourceProperty)
						.addComponent(cmbSourceProperties)
				)
				.addGroup( layout.createSequentialGroup()
						.addComponent(lblTargetProperty)
						.addComponent(cmbTargetProperties)
				)
				.addGroup( layout.createSequentialGroup()
						.addComponent(btnCancel)
						.addComponent(btnOk)
				)
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup()
				.addGroup( layout.createParallelGroup()
						.addComponent(lblSourceProperty)
						.addComponent(cmbSourceProperties)
				)
				.addGroup( layout.createParallelGroup()
						.addComponent(lblTargetProperty)
						.addComponent(cmbTargetProperties)
				)
				.addGroup( layout.createParallelGroup()
						.addComponent(btnCancel)
						.addComponent(btnOk)
				)
		);
		
		return layout;
		
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnOk ) {
			
			OntProperty sourceProperty = null;
			OntProperty targetProperty = null;
			
			Object sourceSelected = cmbSourceProperties.getSelectedItem();
			if( sourceSelected != null && sourceSelected instanceof OntProperty ) {
				sourceProperty = (OntProperty) sourceSelected;
			}
			
			Object targetSelected = cmbTargetProperties.getSelectedItem();
			if( targetSelected != null && targetSelected instanceof OntProperty ) {
				targetProperty = (OntProperty) targetSelected;
			}
			
			createNewHierarcyTab(sourceProperty, targetProperty);
			
			setVisible(false);
			return;
		}
		
		if( e.getSource() == btnCancel ) {
			setVisible(false);
		}
		
	}
	

	public void createNewHierarcyTab(OntProperty sourceProperty,
			OntProperty targetProperty) {
		
		AlternateHierarchyLayout altLayout = new AlternateHierarchyLayout(sourceProperty, targetProperty);
		
		Canvas2 canvas = new Canvas2(altLayout);
		altLayout.setVizPanel(canvas);
		
		if( Core.getInstance().sourceIsLoaded() ) {
			canvas.buildLayoutGraphs(Core.getInstance().getSourceOntology());
			canvas.ontologyChanged(
					new OntologyChangeEvent(this, EventType.ONTOLOGY_ADDED, Core.getInstance().getSourceOntology().getID()));
		}
		
		if( Core.getInstance().targetIsLoaded() ) {
			canvas.buildLayoutGraphs(Core.getInstance().getTargetOntology());
			canvas.ontologyChanged(
					new OntologyChangeEvent(this, EventType.ONTOLOGY_ADDED, Core.getInstance().getTargetOntology().getID()));
		}
		
		
		AgreementMakerClassic amPanel = new AgreementMakerClassic(canvas);
				
		Core.getUI().addTab("Alternate Hierarchy", null, amPanel, null);
		
	}



	public static void main(String[] args) {
		
		Preferences prefs = Preferences.userNodeForPackage(ViewAlternateHierachyDialog.class);
		
		String lastSelected = prefs.get("LAST_FILE", "");
		
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(lastSelected));
		
		int retVal = fc.showOpenDialog(null);
		
		if( retVal != JFileChooser.APPROVE_OPTION ) return; // user pressed cancel.
		
		prefs.put("LAST_FILE", fc.getSelectedFile().getAbsolutePath());
		
		OntoTreeBuilder builder = new OntoTreeBuilder(fc.getSelectedFile().getAbsolutePath(), Ontology.SOURCE, Ontology.LANG_OWL, Ontology.SYNTAX_RDFXML, true);
		
		try {
			builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Ontology o = builder.getOntology();
		
		OntModel model = o.getModel();
				
		String queryString  = "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
		       queryString += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
		       queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
		       queryString += "SELECT ?subject ?object \n { \n";
		       queryString += "?subject rdf:type owl:Class .";
		       queryString += "?subject rdfs:subClassOf ?restriction .";
		       queryString += "?restriction rdf:type owl:Restriction .";
		       queryString += "?restriction owl:onProperty <http://mouse.owl#UNDEFINED_part_of> .";
		       queryString += "?restriction owl:someValuesFrom ?object .";
		       queryString += "}";
		
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		try {
			ResultSet results = qexec.execSelect() ;
			
			String resultString = ResultSetFormatter.asText(results, query);
			System.out.println(resultString);
			
//			for ( ; results.hasNext() ; )
//			{
//				//QuerySolution soln = results.nextSolution() ;
//				
//			}
		} catch(Exception exc) {
			exc.printStackTrace();
			Utility.displayErrorPane(exc.toString() + "\n\n" + exc.getMessage(), "ERROR");
		} finally {
			qexec.close();
		}
		
		
	}



	
	
	
}

