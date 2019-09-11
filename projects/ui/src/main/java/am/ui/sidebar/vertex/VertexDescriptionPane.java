package am.ui.sidebar.vertex;



import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import am.app.Core;
import am.app.ontology.Node;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;

public class VertexDescriptionPane extends JPanel{
	
	final static String TABDESC = "Descriptions";
	final static String TABANNOTATIONS = "Annotations";
	final static String TABPROP = "Class/Property";
	final static String TABINDIVIDUALS = "Individuals";
	
	final static String TIPDESC = "Basic information of the current node";
	final static String TIPANNOTATIONS = "Longer descriptions for the node";
	final static String TIPPROP = "List of properties which involve the selected node";
	final static String TIPINDIVIDUALS = "List of instances of the selected class node";
	
	final static String EMPTY = "The list is empty for the selected node";
	
	
	static final long serialVersionUID = 1;
	private JTabbedPane sourcePane, targetPane;
	
	private JPanel pnlSourceDescription,pnlSourceAnnotations,pnlSourceProperties,pnlSourceIndividuals,sp5,pnlTargetDescription,pnlTargetAnnotations,pnlTargetProperties,pnlTargetIndividuals,lp5;
	private JScrollPane sclSourceDescription,sclSourceAnnotations,ss3,ss4,sclMappingInformation,ls1,ls2,ls3,ls4,ls5;
	private JTextArea txtSourceDescription,txtSourceAnnotations,txtSourceProperties,txtSourceIndividuals,txtMappingInformation,lt1,txtTargetAnnotations,lt3,txtTargetIndividuals,lt5;
	private OntologyLanguage typeOfFile;

	private String sourceAnnotations;
	private String sourceIndividuals;
	//private String disjointClasses;
	//private String propertyOrClasses;
	//private String restrictions;
	
	private String targetAnnotations;
	private String targetIndividuals;
	//private String targetDisjointClasses;
	//private String targetPropertyOrClasses;
	//private String targetRestrictions;
	
	public VertexDescriptionPane(OntologyLanguage language){
		typeOfFile = language;
		init();
	}
	
	public void init(){
		
		setLayout(new GridLayout(2,1));
		
		sourcePane = new JTabbedPane();
		targetPane = new JTabbedPane();
		
		add(sourcePane);
		add(targetPane);
		
		txtSourceDescription = new JTextArea();
		lt1 = new JTextArea();
		pnlSourceDescription = new JPanel();
		pnlTargetDescription = new JPanel();
		pnlSourceDescription.setLayout(new GridLayout(1,1));
		pnlTargetDescription.setLayout(new GridLayout(1,1));
		txtMappingInformation = new JTextArea();
		lt5 = new JTextArea();
		sp5 = new JPanel();
		lp5 = new JPanel();
		sp5.setLayout(new GridLayout(1,1));
		lp5.setLayout(new GridLayout(1,1));
		
		if(typeOfFile == OntologyLanguage.XML){
			//sourcePane
			txtSourceDescription.setEditable(false);
	        txtSourceDescription.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        txtSourceDescription.setLineWrap(true);
	        sclSourceDescription = new JScrollPane(txtSourceDescription);
	        sclSourceDescription.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Description"));
	        pnlSourceDescription.add(sclSourceDescription);
	
	        txtMappingInformation.setEditable(false);
	        txtMappingInformation.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        txtMappingInformation.setLineWrap(true);
	        sclMappingInformation = new JScrollPane(txtMappingInformation);
	        sclMappingInformation.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Mapping Information"));
	        sp5.add(sclMappingInformation);
	        
	        //localPane
	        lt1.setEditable(false);
	        lt1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt1.setLineWrap(true);
	        ls1 = new JScrollPane(lt1);
	        ls1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Description"));
	        pnlTargetDescription.add(ls1);
	
	        lt5.setEditable(false);
	        lt5.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt5.setLineWrap(true);
	        ls5 = new JScrollPane(lt5);
	        ls5.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Mapping Information"));
	        lp5.add(ls5);
	        
	        sourcePane.addTab("Description", null, pnlSourceDescription, "Description of the source node");
	        targetPane.addTab("Description", null, pnlTargetDescription, "Description of the target node");
	        sourcePane.addTab("Mapping Information", null, pnlSourceDescription, "Description of the source node");
	        targetPane.addTab("Mapping Information", null, pnlTargetDescription, "Description of the target node");
		}else if(typeOfFile == OntologyLanguage.OWL){
			txtSourceAnnotations = new JTextArea();
			txtSourceProperties = new JTextArea();
			txtSourceIndividuals = new JTextArea();
			txtTargetAnnotations = new JTextArea();
			lt3 = new JTextArea();
			txtTargetIndividuals = new JTextArea();
			
			pnlSourceAnnotations = new JPanel();
			pnlSourceProperties = new JPanel();
			pnlSourceIndividuals = new JPanel();
			pnlTargetAnnotations = new JPanel();
			pnlTargetProperties = new JPanel();
			pnlTargetIndividuals = new JPanel();
			
			pnlSourceAnnotations.setLayout(new GridLayout(1,1));
			pnlSourceProperties.setLayout(new GridLayout(1,1));
			pnlSourceIndividuals.setLayout(new GridLayout(1,1));
			pnlTargetAnnotations.setLayout(new GridLayout(1,1));
			pnlTargetProperties.setLayout(new GridLayout(1,1));
			pnlTargetIndividuals.setLayout(new GridLayout(1,1));
						
			//sourcePane
			txtSourceDescription.setEditable(false);
	        txtSourceDescription.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        txtSourceDescription.setLineWrap(true);
	        sclSourceDescription = new JScrollPane(txtSourceDescription);
	        sclSourceDescription.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABDESC));
	        pnlSourceDescription.add(sclSourceDescription);
	
	        txtSourceAnnotations.setEditable(false);
	        txtSourceAnnotations.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        txtSourceAnnotations.setLineWrap(true);
	        sclSourceAnnotations = new JScrollPane(txtSourceAnnotations);
	        sclSourceAnnotations.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABANNOTATIONS));
	        pnlSourceAnnotations.add(sclSourceAnnotations);
	
	        txtSourceProperties.setEditable(false);
	        txtSourceProperties.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        txtSourceProperties.setLineWrap(true);
	        ss3 = new JScrollPane(txtSourceProperties);
	        ss3.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABPROP));
	        pnlSourceProperties.add(ss3);
	
	        txtSourceIndividuals.setEditable(false);
	        txtSourceIndividuals.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        txtSourceIndividuals.setLineWrap(true);
	        ss4 = new JScrollPane(txtSourceIndividuals);
	        ss4.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABINDIVIDUALS));
	        pnlSourceIndividuals.add(ss4);
	        
	        //localPane
	        lt1.setEditable(false);
	        lt1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt1.setLineWrap(true);
	        ls1 = new JScrollPane(lt1);
	        ls1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABDESC));
	        pnlTargetDescription.add(ls1);
	
	        txtTargetAnnotations.setEditable(false);
	        txtTargetAnnotations.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        txtTargetAnnotations.setLineWrap(true);
	        ls2 = new JScrollPane(txtTargetAnnotations);
	        ls2.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABANNOTATIONS));
	        pnlTargetAnnotations.add(ls2);
	
	        lt3.setEditable(false);
	        lt3.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt3.setLineWrap(true);
	        ls3 = new JScrollPane(lt3);
	        ls3.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABPROP));
	        pnlTargetProperties.add(ls3);
	
	        txtTargetIndividuals.setEditable(false);
	        txtTargetIndividuals.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        txtTargetIndividuals.setLineWrap(true);
	        ls4 = new JScrollPane(txtTargetIndividuals);
	        ls4.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABINDIVIDUALS));
	        pnlTargetIndividuals.add(ls4);
			
			//sourcePane.addTab(TABDESC, null, pnlSourceDescription, TIPDESC);
			sourcePane.addTab(TABANNOTATIONS, null, pnlSourceAnnotations, TIPANNOTATIONS);
			//sourcePane.addTab(TABPROP, null, pnlSourceProperties, TIPPROP);
			sourcePane.addTab(TABINDIVIDUALS, null, pnlSourceIndividuals, TIPINDIVIDUALS);
			//targetPane.addTab(TABDESC, null, pnlTargetDescription, TIPDESC);
			targetPane.addTab(TABANNOTATIONS, null, pnlTargetAnnotations, TIPANNOTATIONS);
			//targetPane.addTab(TABPROP, null, pnlTargetProperties, TIPPROP);
			targetPane.addTab(TABINDIVIDUALS, null, pnlTargetIndividuals, TIPINDIVIDUALS);
		}
		
	}
	
	public void setSourceAnnotations( String annotations ) {
		if( txtSourceAnnotations != null ) txtSourceAnnotations.setText(annotations);
		this.sourceAnnotations = annotations;
	}
	
	public void setTargetAnnotations( String annotations ) {
		if( txtTargetAnnotations != null ) txtTargetAnnotations.setText(annotations);
		this.targetAnnotations = annotations;
	}
	
	public void setSourceIndividuals( String individuals ) {
		if( txtSourceIndividuals != null ) txtSourceIndividuals.setText(individuals);
		this.sourceIndividuals = individuals;
	}
	
	public void setTargetIndividuals( String individuals ) {
		if( txtTargetIndividuals != null ) txtTargetIndividuals.setText(individuals);
		this.targetIndividuals = individuals;
	}
	
	public String getSourceAnnotations() { return sourceAnnotations; }
	public String getTargetAnnotations() { return targetAnnotations; }
	public String getSourceIndividuals() { return sourceIndividuals; }
	public String getTargetIndividuals() { return targetIndividuals; }
	
	//public String getDisjointClasses (){
	//	return disjointClasses;
	//}
	
	//public String getProperties(){
	//	return propertyOrClasses;
	//}
	
	//public String getRestrictions(){
	//	return restrictions;
	//}
	
	@Deprecated
	public void fillDescription (Node node){
		clearDescription(node);
			if(typeOfFile == OntologyLanguage.XML){			
				if(node.getOntologyID() == Core.getInstance().getSourceOntology().getID()){
			    	   txtSourceDescription.setText(node.getLabel());
			    }else {
			    	   lt1.setText(node.getLabel());
			    }
			}
			else if(typeOfFile == OntologyLanguage.OWL){
		       if(node.getOntologyID() == Core.getInstance().getSourceOntology().getID()){
		    	   txtSourceDescription.setText(node.getDescriptionsString());
		    	   txtSourceAnnotations.setText(node.getAnnotationsString());
		    	   txtSourceProperties.setText(node.getPropOrClassString());
		    	   txtSourceIndividuals.setText(node.getIndividualsString());
		       }else {
		    	   lt1.setText(node.getDescriptionsString());
		    	   txtTargetAnnotations.setText(node.getAnnotationsString());
		    	   lt3.setText(node.getPropOrClassString());
		    	   txtTargetIndividuals.setText(node.getIndividualsString());
		       }
			}else if(typeOfFile == OntologyLanguage.RDFS){
				//TODO: WORK here for RDFS
				throw new RuntimeException("VertexDescriptionPane for RDFS not implemented.");
			}
	}
	
	public void clearDescription (Node node){
		if(typeOfFile == OntologyLanguage.XML){
			if(node.getOntologyID() == Core.getInstance().getSourceOntology().getID()){
		    	   txtSourceDescription.setText("");
		       }else {
		    	   lt1.setText("");
		       }
		}else if(typeOfFile == OntologyLanguage.OWL){
			if(node.getOntologyID() == Core.getInstance().getSourceOntology().getID()){
		    	   txtSourceDescription.setText("");
		    	   txtSourceAnnotations.setText("");
		    	   txtSourceProperties.setText("");
		    	   txtSourceIndividuals.setText("");
		       }else{
		    	   lt1.setText("");
		    	   txtTargetAnnotations.setText("");
		    	   lt3.setText("");
		    	   txtTargetIndividuals.setText("");
		       }
		}
	}
}
