package am.userInterface.vertex;



import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import am.GlobalStaticVariables;
import am.application.ontology.Node;

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
	
	private JPanel sp1,sp2,sp3,sp4,sp5,lp1,lp2,lp3,lp4,lp5;
	private JScrollPane ss1,ss2,ss3,ss4,ss5,ls1,ls2,ls3,ls4,ls5;
	private JTextArea st1,st2,st3,st4,st5,lt1,lt2,lt3,lt4,lt5;
	private int typeOfFile;

	private String annotations;
	private String disjointClasses;
	private String propertyOrClasses;
	private String restrictions;
	
	public VertexDescriptionPane(int fileType){
		typeOfFile = fileType;
		init();
	}
	
	public void init(){
		
		setLayout(new GridLayout(2,1));
		
		sourcePane = new JTabbedPane();
		targetPane = new JTabbedPane();
		
		add(sourcePane);
		add(targetPane);
		
		st1 = new JTextArea();
		lt1 = new JTextArea();
		sp1 = new JPanel();
		lp1 = new JPanel();
		sp1.setLayout(new GridLayout(1,1));
		lp1.setLayout(new GridLayout(1,1));
		st5 = new JTextArea();
		lt5 = new JTextArea();
		sp5 = new JPanel();
		lp5 = new JPanel();
		sp5.setLayout(new GridLayout(1,1));
		lp5.setLayout(new GridLayout(1,1));
		
		if(typeOfFile == GlobalStaticVariables.XMLFILE){
			//sourcePane
			st1.setEditable(false);
	        st1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st1.setLineWrap(true);
	        ss1 = new JScrollPane(st1);
	        ss1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Description"));
	        sp1.add(ss1);
	
	        st5.setEditable(false);
	        st5.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st5.setLineWrap(true);
	        ss5 = new JScrollPane(st5);
	        ss5.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Mapping Information"));
	        sp5.add(ss5);
	        
	        //localPane
	        lt1.setEditable(false);
	        lt1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt1.setLineWrap(true);
	        ls1 = new JScrollPane(lt1);
	        ls1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Description"));
	        lp1.add(ls1);
	
	        lt5.setEditable(false);
	        lt5.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt5.setLineWrap(true);
	        ls5 = new JScrollPane(lt5);
	        ls5.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Mapping Information"));
	        lp5.add(ls5);
	        
	        sourcePane.addTab("Description", null, sp1, "Description of the node");
	        targetPane.addTab("Description", null, lp1, "Description of the node");
	        sourcePane.addTab("Mapping Information", null, sp1, "Description of the node");
	        targetPane.addTab("Mapping Information", null, lp1, "Description of the node");
		}else if(typeOfFile == GlobalStaticVariables.ONTFILE){
			st2 = new JTextArea();
			st3 = new JTextArea();
			st4 = new JTextArea();
			lt2 = new JTextArea();
			lt3 = new JTextArea();
			lt4 = new JTextArea();
			
			sp2 = new JPanel();
			sp3 = new JPanel();
			sp4 = new JPanel();
			lp2 = new JPanel();
			lp3 = new JPanel();
			lp4 = new JPanel();
			
			sp2.setLayout(new GridLayout(1,1));
			sp3.setLayout(new GridLayout(1,1));
			sp4.setLayout(new GridLayout(1,1));
			lp2.setLayout(new GridLayout(1,1));
			lp3.setLayout(new GridLayout(1,1));
			lp4.setLayout(new GridLayout(1,1));
						
			//sourcePane
			st1.setEditable(false);
	        st1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st1.setLineWrap(true);
	        ss1 = new JScrollPane(st1);
	        ss1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABDESC));
	        sp1.add(ss1);
	
	        st2.setEditable(false);
	        st2.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st2.setLineWrap(true);
	        ss2 = new JScrollPane(st2);
	        ss2.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABANNOTATIONS));
	        sp2.add(ss2);
	
	        st3.setEditable(false);
	        st3.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st3.setLineWrap(true);
	        ss3 = new JScrollPane(st3);
	        ss3.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABPROP));
	        sp3.add(ss3);
	
	        st4.setEditable(false);
	        st4.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st4.setLineWrap(true);
	        ss4 = new JScrollPane(st4);
	        ss4.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABINDIVIDUALS));
	        sp4.add(ss4);
	        
	        //localPane
	        lt1.setEditable(false);
	        lt1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt1.setLineWrap(true);
	        ls1 = new JScrollPane(lt1);
	        ls1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABDESC));
	        lp1.add(ls1);
	
	        lt2.setEditable(false);
	        lt2.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt2.setLineWrap(true);
	        ls2 = new JScrollPane(lt2);
	        ls2.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABANNOTATIONS));
	        lp2.add(ls2);
	
	        lt3.setEditable(false);
	        lt3.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt3.setLineWrap(true);
	        ls3 = new JScrollPane(lt3);
	        ls3.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABPROP));
	        lp3.add(ls3);
	
	        lt4.setEditable(false);
	        lt4.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt4.setLineWrap(true);
	        ls4 = new JScrollPane(lt4);
	        ls4.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), TABINDIVIDUALS));
	        lp4.add(ls4);
			
			sourcePane.addTab(TABDESC, null, sp1, TIPDESC);
			sourcePane.addTab(TABANNOTATIONS, null, sp2,TIPANNOTATIONS);
			sourcePane.addTab(TABPROP, null, sp3, TIPPROP);
			sourcePane.addTab(TABINDIVIDUALS, null, sp4, TIPINDIVIDUALS);
			targetPane.addTab(TABDESC, null, lp1, TIPDESC);
			targetPane.addTab(TABANNOTATIONS, null, lp2, TIPANNOTATIONS);
			targetPane.addTab(TABPROP, null, lp3, TIPPROP);
			targetPane.addTab(TABINDIVIDUALS, null, lp4, TIPINDIVIDUALS);
		}
		
	}
	
	public String getAnnotations(){
		return annotations;
	}
	
	public String getDisjointClasses (){
		return disjointClasses;
	}
	
	public String getProperties(){
		return propertyOrClasses;
	}
	
	public String getRestrictions(){
		return restrictions;
	}
	
	public void fillDescription (Vertex v){
		clearDescription(v);
		Node node = v.getNode();
		if(typeOfFile == GlobalStaticVariables.XMLFILE){			
			if(v.isSourceOrGlobal()){
		    	   st1.setText(node.getLabel());
		    }else {
		    	   lt1.setText(node.getLabel());
		    }
		}
		else if(typeOfFile == GlobalStaticVariables.ONTFILE){		
	       if(v.isSourceOrGlobal()){
	    	   st1.setText(node.getDescriptionsString());
	    	   st2.setText(node.getAnnotationsString());
	    	   st3.setText(node.getPropOrClassString());
	    	   st4.setText(node.getIndividualsString());
	       }else {
	    	   lt1.setText(node.getDescriptionsString());
	    	   lt2.setText(node.getAnnotationsString());
	    	   lt3.setText(node.getPropOrClassString());
	    	   lt4.setText(node.getIndividualsString());
	       }
		}else if(typeOfFile == GlobalStaticVariables.RDFSFILE){
			//TODO: WORK here for RDFS
		}
	}
	
	public void clearDescription (Vertex node){
		if(typeOfFile == GlobalStaticVariables.XMLFILE){
			if(node.isSourceOrGlobal()){
		    	   st1.setText("");
		       }else {
		    	   lt1.setText("");
		       }
		}else if(typeOfFile == GlobalStaticVariables.ONTFILE){
			if(node.isSourceOrGlobal()){
		    	   st1.setText("");
		    	   st2.setText("");
		    	   st3.setText("");
		    	   st4.setText("");
		       }else{
		    	   lt1.setText("");
		    	   lt2.setText("");
		    	   lt3.setText("");
		    	   lt4.setText("");
		       }
		}
	}
}
