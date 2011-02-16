package am.userInterface.sidebar.provenance;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	JScrollPane scrollPane;
	
	private Component oldComponent;

	public ProvenanceSidebar() {
		init();
	}
	public void init(){
		setLayout(new GridLayout(1,1));
		provenancePane=new JTabbedPane();
		add(provenancePane);
		
		
		txtProvenance=new JTextArea();
		//txtProvenance.
		
		txtProvenance.setEditable(false);
		//txtProvenance.setLineWrap(true);
		txtProvenance.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
		
		scrollPane=new JScrollPane(txtProvenance);
		//scrollPane.setViewportView(txtProvenance);
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//scrollPane.setEnabled(true);
		scrollPane.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Provenance"));
		//provenancePane.setLayout((LayoutManager) scrollPane);
		
		provenancePane.add(scrollPane);
		
		//provenancePane.addTab("Provenace",null,provenance,"Provenance of the selected mapping");
		provenancePane.addTab("Provenace Information",null,scrollPane,"Provenance of the selected mapping");
	}
	public void setProvenance(String prov)
	{
		txtProvenance.setText(prov);
		//txtProvenance.setPreferredSize(new Dimension(1000,1000));
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
