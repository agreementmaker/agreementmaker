package agreementMaker.userInterface;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import agreementMaker.GSM;
import agreementMaker.userInterface.vertex.VertexDescriptionPane;


/**
 * @author Nalin
 *
 */
public class OpenOntologyFileDialog implements ActionListener, ListSelectionListener{
	
	/**
	 * @param args
	 */
	
	private JButton browse, cancel, proceed;
	private JTextField filePath;
	private JLabel fileType;
	private JDialog frame;
	private int ontoType;
	private JList syntaxList, langList;	
	private UI ui;
	
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public OpenOntologyFileDialog(int ontoType, UI userInterface) {
		

		//	Setting the Look and Feel of the application to that of Windows
		//try { javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		//catch (Exception e) { System.out.println(e); }
		
		//typeOfFile = fileType;  
		ui = userInterface;
		this.ontoType = ontoType;
		
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		
		frame = new JDialog();
		if(ontoType == GSM.SOURCENODE)
			frame.setTitle("Open Source Ontology File...");
		else if(ontoType == GSM.TARGETNODE)
			frame.setTitle("Open Target Ontology File...");
		

		
		//Container contentPane = frame.getContentPane();
		//frame.setResizable(false);
		
		//TODO: work out a better layout method for this dialog
		//DONE: By Cosmin (Oct 5th, 2008)
		//frame.setSize(new Dimension(600,250));

		
		if(ontoType == GSM.SOURCENODE)
			fileType = new JLabel("Source Ontology");
		else if(ontoType == GSM.TARGETNODE)
			fileType = new JLabel("Target Ontology");
		
		filePath = new JTextField(0);
		
		browse = new JButton("Browse...");
		cancel = new JButton("Cancel");
		proceed = new JButton("Proceed");
		browse.addActionListener(this);
		cancel.addActionListener(this);
		proceed.addActionListener(this);
		
		String[] ts_list = {"RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "N3", "TURTLE"};
		String[] l_list = {"RDFS", "OWL", "XML"};
		
		
		syntaxList = new JList(ts_list);
		syntaxList.setPrototypeCellValue("01234567890123456789"); // this string sets the width of the list
		syntaxList.addListSelectionListener(this);
		syntaxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		syntaxList.setVisibleRowCount(5);
		//syntaxList.setSize(300,100); // this function does not seem to make a difference
		syntaxList.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Syntax Language"));
		syntaxList.setSelectedIndex(prefs.getSyntaxListSelection());  // select the last thing selected
		
		langList = new JList(l_list);
		langList.addListSelectionListener(this);
		langList.setPrototypeCellValue("01234567890123456789"); // this string sets the width of the list
		langList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		langList.setVisibleRowCount(5);
		//langList.setSize(300,100);  // this function does not seem to make a difference
		langList.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Ontology Language"));
		langList.setSelectedIndex(prefs.getLanguageListSelection());  // select the last thing selected

		
		//Make the GroupLayout for this dialog (somewhat complicated, but very flexible)
		// This Group layout lays the items in relation with eachother.  The horizontal
		// and vertical groups decide the relation between UI elements.
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(fileType) 					// fileType label
					.addGroup(layout.createParallelGroup()
							.addComponent(filePath) 			// filepath textbox
							.addGroup(layout.createSequentialGroup()
									.addComponent(langList) 	// the lists are part of their own group
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
											 GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(syntaxList))
							.addGroup(layout.createSequentialGroup()
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						                     GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(cancel)		// the buttons are also part of their own groups
									.addComponent(proceed)
									)
							)
					.addGroup(layout.createParallelGroup()
							.addComponent(browse)
							)
		);
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(
				layout.createParallelGroup()
					.addComponent(fileType)
					.addGroup(layout.createSequentialGroup()
							.addComponent(filePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							          GroupLayout.PREFERRED_SIZE)
							.addGroup(layout.createParallelGroup()
									.addComponent(langList)
									.addComponent(syntaxList))
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
									.addComponent(cancel)
									.addComponent(proceed)
									)
							)
					.addGroup(layout.createSequentialGroup()
							.addComponent(browse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							          GroupLayout.PREFERRED_SIZE))
		);

		// end of Layout Code
		
		frame.addWindowListener(new WindowEventHandler());
		frame.pack(); // automatically set the frame size
		frame.setLocationRelativeTo(null); 	// center the window on the screen
		frame.setModal(true);
		
		frame.setVisible(true);
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		JFileChooser fc;
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		
		if(obj == cancel){
			frame.dispose();
		}else if(obj == browse){
			
			
			
			// if the directory we received from our preferences exists, use that as the 
			// starting directory for the chooser
			if( prefs.getLastDir().exists() ) {
				fc = new JFileChooser(prefs.getLastDir());
			} else { fc = new JFileChooser(); } 
			
			int returnVal = fc.showOpenDialog(frame);

			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				File selectedfile = fc.getSelectedFile();
				
				// ok, now that we know what file the user selected
				// let's save it for future use (for the chooser)
				prefs.saveLastDir(selectedfile); 
								
				ui.setOntoFileName(selectedfile.getPath(), ontoType);
				filePath.setText(ui.getOntoFileName(ontoType));
				

			}
		}else if(obj == proceed){
			if(filePath.equals("")){
				JOptionPane.showMessageDialog(frame, "Load an ontology file to proceed.");
			}else{
				try{
					JPanel jPanel = new JPanel();
					
					if(langList.getSelectedIndex() == 0)//RDFS
						jPanel = new VertexDescriptionPane(GSM.RDFSFILE);//takes care of fields for XML files as well
					else if(langList.getSelectedIndex() == 1)//OWL
						jPanel = new VertexDescriptionPane(GSM.ONTFILE);//takes care of fields for XML files as well
					else if(langList.getSelectedIndex() == 2)//XML
						jPanel = new VertexDescriptionPane(GSM.XMLFILE);//takes care of fields for XML files as well 
					
					ui.getUISplitPane().setRightComponent(jPanel);
					ui.setDescriptionPanel(jPanel);
					ui.buildOntology(ontoType, langList.getSelectedIndex(), syntaxList.getSelectedIndex());
					
					// once we are done, let's save the syntax and language selection that was made by the user
					// and save the file used to the recent file list, and also what syntax and language it is
					prefs.saveOpenDialogListSelection(syntaxList.getSelectedIndex() , langList.getSelectedIndex());
					prefs.saveRecentFile(filePath.getText(), ontoType, syntaxList.getSelectedIndex(), langList.getSelectedIndex());
				
					
				}catch(Exception ex){
					JOptionPane.showConfirmDialog(null,"Can not parse the file '" + ui.getOntoFileName(ontoType) + "'. Please check the policy.","Parser Error",JOptionPane.PLAIN_MESSAGE);
					System.out.println("STACK TRACE:");
					ex.printStackTrace();
					ui.setOntoFileName(null, ontoType);
				}

				frame.dispose();
			}
		}// end of obj == proceed
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() == langList){
			if(langList.getSelectedIndex() == 2)//for XML files selection
				syntaxList.setEnabled(false);
			else
				syntaxList.setEnabled(true);
		}
	}
	
}
