package agreementMaker.userInterface.vertex;



import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import agreementMaker.GSM;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;


public class VertexDescriptionPane extends JPanel{

	static final long serialVersionUID = 1;
	private JTabbedPane sourcePane, localPane;
	
	private JPanel sp1,sp2,sp3,sp4,sp5,lp1,lp2,lp3,lp4,lp5;
	private JScrollPane ss1,ss2,ss3,ss4,ss5,ls1,ls2,ls3,ls4,ls5;
	private JTextArea st1,st2,st3,st4,st5,lt1,lt2,lt3,lt4,lt5;
	private int typeOfFile;

	private String annotations;
	private String disjointClasses;
	private String properties;
	private String restrictions;
	
	public VertexDescriptionPane(int fileType){
		typeOfFile = fileType;
		init();
	}
	
	public void init(){
		
		setLayout(new GridLayout(2,1));
		
		sourcePane = new JTabbedPane();
		localPane = new JTabbedPane();
		
		add(sourcePane);
		add(localPane);
		
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
		
		if(typeOfFile == GSM.XMLFILE){
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
	        localPane.addTab("Description", null, lp1, "Description of the node");
	        sourcePane.addTab("Mapping Information", null, sp1, "Description of the node");
	        localPane.addTab("Mapping Information", null, lp1, "Description of the node");
		}else if(typeOfFile == GSM.ONTFILE){
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
	        ss1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Annotations"));
	        sp1.add(ss1);
	
	        st2.setEditable(false);
	        st2.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st2.setLineWrap(true);
	        ss2 = new JScrollPane(st2);
	        ss2.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Asserted Conditions"));
	        sp2.add(ss2);
	
	        st3.setEditable(false);
	        st3.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st3.setLineWrap(true);
	        ss3 = new JScrollPane(st3);
	        ss3.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Properties"));
	        sp3.add(ss3);
	
	        st4.setEditable(false);
	        st4.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        st4.setLineWrap(true);
	        ss4 = new JScrollPane(st4);
	        ss4.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Disjoint Classes"));
	        sp4.add(ss4);
	        
	        //localPane
	        lt1.setEditable(false);
	        lt1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt1.setLineWrap(true);
	        ls1 = new JScrollPane(lt1);
	        ls1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Annotations"));
	        lp1.add(ls1);
	
	        lt2.setEditable(false);
	        lt2.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt2.setLineWrap(true);
	        ls2 = new JScrollPane(lt2);
	        ls2.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Asserted Conditions"));
	        lp2.add(ls2);
	
	        lt3.setEditable(false);
	        lt3.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt3.setLineWrap(true);
	        ls3 = new JScrollPane(lt3);
	        ls3.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Properties"));
	        lp3.add(ls3);
	
	        lt4.setEditable(false);
	        lt4.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
	        lt4.setLineWrap(true);
	        ls4 = new JScrollPane(lt4);
	        ls4.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Disjoints"));
	        lp4.add(ls4);
			
			sourcePane.addTab("Annotations", null, sp1, "Annotations for current node");
			sourcePane.addTab("Asserted Conditions", null, sp2, "Asserted Conditions for current node");
			sourcePane.addTab("Properties", null, sp3, "Properties of current node");
			sourcePane.addTab("Disjoint Classes", null, sp4, "Disjoint Classes for current node");
			
			localPane.addTab("Annotations", null, lp1, "Annotations for current node");
			localPane.addTab("Asserted Conditions", null, lp2, "Asserted Conditions for current node");
			localPane.addTab("Properties", null, lp3, "Properties of current node");
			localPane.addTab("Disjoint Classes", null, lp4, "Disjoint Classes for current node");
		}
		
	}
	
	public String getAnnotations(){
		return annotations;
	}
	
	public String getDisjointClasses (){
		return disjointClasses;
	}
	
	public String getProperties(){
		return properties;
	}
	
	public String getRestrictions(){
		return restrictions;
	}
	
	public void fillDescription (Vertex node){
		
		OntModel ontModel;
		int nodeType;
		
		nodeType=node.getNodeType();
		clearDescription(node);
		if(typeOfFile == GSM.XMLFILE){			
			if(nodeType == GSM.SOURCENODE){
		    	   st1.setText(node.getDesc());
		    }else if(nodeType == GSM.TARGETNODE){
		    	   lt1.setText(node.getDesc());
		    }
		}else if(typeOfFile == GSM.ONTFILE){
			ontModel=node.getOntModel();
			OntClass cls = ontModel.getOntClass(node.getUri());
				
		    //work on Annotations            
	        String temp;
	        annotations="";
	        for(Iterator i = cls.listComments(null); i.hasNext();){
	            temp = ((Literal)i.next()).toString();
	            if(temp.compareTo("")!=0) annotations = annotations + "rdfs:comment\t\"" + temp + "\"\n";
	        }
	        
	        for(Iterator i = cls.listIsDefinedBy(); i.hasNext();){
				temp = ((Resource)i.next()).getLocalName();
	            if(temp.compareTo("")!=0) annotations = annotations + "rdfs:isDefinedBy\t\"" + temp + "\"\n";
	        }
	        
	        for(Iterator i = cls.listLabels(null); i.hasNext();){
				temp = ((Literal)i.next()).toString();
				if(temp.compareTo("")!=0) annotations = annotations + "rdfs:label\t\"" + temp + "\"\n";
	        }
	        
	        for(Iterator i = cls.listSeeAlso(); i.hasNext();){
				temp = ((Resource)i.next()).getLocalName();
				if(temp.compareTo("")!=0) annotations = annotations + "rdfs:SeeAlso\t\"" + temp + "\"\n";
	        }
	        
	        if(ontModel.getSpecification() == OntModelSpec.RDFS_MEM_RDFS_INF)
		        for(Iterator i = cls.listVersionInfo(); i.hasNext();){
					temp = (String)i.next();
					if(temp.compareTo("")!=0) annotations = annotations + "rdfs:VersionInfo\t\"" + temp + "\"\n";
		        }
	        
	       /*//Work on Restrictions/* 
	       Collection collRestrictions = cls.getRestrictions(true);
	       Iterator itr1 = collRestrictions.iterator();
	       restrictions = "";
	       while (itr1.hasNext()) 
	          restrictions = restrictions + ((OWLRestriction)itr1.next()).getBrowserText() + " \n";
	       */
	       //work on Properties
	        properties="";
	        for(Iterator i = cls.listDeclaredProperties(); i.hasNext();)//true
				properties = properties + ((OntProperty)i.next()).getLocalName() + " \n";
	       
	       //Work on Disjoint Classes
	        disjointClasses="";
			for(Iterator i = cls.listDisjointWith(); i.hasNext();){
				OntClass sub = (OntClass)i.next();
				disjointClasses = disjointClasses + sub.getLocalName() + " \n";
			} 
	    
	       if(nodeType == GSM.SOURCENODE){
	    	   st1.setText(annotations);
	    	   st2.setText(restrictions);
	    	   st3.setText(properties);
	    	   st4.setText(disjointClasses);
	       }else if(nodeType == GSM.TARGETNODE){
	    	   lt1.setText(annotations);
	    	   lt2.setText(restrictions);
	    	   lt3.setText(properties);
	    	   lt4.setText(disjointClasses);
	       }
		}else if(typeOfFile == GSM.RDFSFILE){
			//TODO: WORK here for RDFS
		}
	}
	
	public void clearDescription (Vertex node){
		int nodeType = node.getNodeType();
		if(typeOfFile == GSM.XMLFILE){
			if(nodeType == GSM.SOURCENODE){
		    	   st1.setText("");
		       }else if(nodeType == GSM.TARGETNODE){
		    	   lt1.setText("");
		       }
		}else if(typeOfFile == GSM.ONTFILE){
			if(nodeType == GSM.SOURCENODE){
		    	   st1.setText("");
		    	   st2.setText("");
		    	   st3.setText("");
		    	   st4.setText("");
		       }else if(nodeType == GSM.TARGETNODE){
		    	   lt1.setText("");
		    	   lt2.setText("");
		    	   lt3.setText("");
		    	   lt4.setText("");
		       }
		}
	}
}
