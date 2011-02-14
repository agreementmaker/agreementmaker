package am.userInterface.sidebar.provenance;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class ProvenanceSidebar extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4099989208896574958L;
	JTabbedPane provenancePane;
	//JPanel provenance;
	JTextArea txtProvenance;
	
	private Component oldComponent;

	public ProvenanceSidebar() {
		init();
	}
	public void init(){
		setLayout(new GridLayout(1,1));
		provenancePane=new JTabbedPane();
		add(provenancePane);
		
		txtProvenance=new JTextArea();
		//provenance=new JPanel();
		
		//txtProvenance.setText("hey this works!");
		//add(txtProvenance);
		
		txtProvenance.setEditable(false);
		txtProvenance.setLineWrap(true);
		txtProvenance.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
		
		
		//provenancePane.addTab("Provenace",null,provenance,"Provenance of the selected mapping");
		provenancePane.addTab("Provenace Information",null,txtProvenance,"Provenance of the selected mapping");
	}
	public void setProvenance(String prov)
	{
		txtProvenance.setText(prov);
	}
	public void clearProvenance()
	{
		txtProvenance.setText("");
	}
	public String getProvenance(){return txtProvenance.getText();}
	public Component getOldComponent() {
		return oldComponent;
	}
	public void setOldComponent(Component oldComponent) {
		this.oldComponent = oldComponent;
	}
	
}
