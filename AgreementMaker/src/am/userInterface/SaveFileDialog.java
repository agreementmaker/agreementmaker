package am.userInterface;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;

import am.Utility;
import am.application.Core;
import am.output.AlignmentOutput;
import am.output.OutputController;
import am.userInterface.UI.WindowEventHandler;



public class SaveFileDialog implements ActionListener{
	
	/**
	 * @param args
	 */
	private JLabel fileNameLabel;
	private JTextField fileName;
	private JButton browse, cancel, save;
	
	private JLabel fileDir;
	private JTextField filePath;
	
	public JDialog frame;
	private JList formatList;
	
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public SaveFileDialog() {
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		frame = new JDialog();
		frame.setTitle("Output File Dialog");
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		fileNameLabel = new JLabel("File name (no extension): ");
		fileName = new JTextField(0);
		fileDir = new JLabel("Output file directory");
		filePath = new JTextField(0);
		
		//the system suggests the last file opened
		if( prefs.getLastDirOutput().exists() ) {
			filePath.setText(prefs.getLastDirOutput().getPath());
		}
		fileName.setText(prefs.getLastNameOutput());
		
		browse = new JButton("Browse...");
		cancel = new JButton("Cancel");
		save = new JButton("Save File");
		browse.addActionListener(this);
		cancel.addActionListener(this);
		save.addActionListener(this);
		
		String[] format_list = {OutputController.XLS, OutputController.TXT, OutputController.DOC, OutputController.RDF};
		
		
		formatList = new JList(format_list);
		formatList.setPrototypeCellValue("01234567890123456789"); // this string sets the width of the list
		formatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		formatList.setVisibleRowCount(3);
		formatList.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "File extension"));
		formatList.setSelectedIndex(0);  // if more then one format will be implemented could be useful to add the lastformatRefOutput to the preferences
		
		
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
					.addGroup(layout.createParallelGroup()
							.addComponent(fileDir) //the label Output File directory
							.addComponent(fileNameLabel) //the label output file name
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(filePath) 			// filepath text
							.addComponent(fileName) 	        //filename text
							.addComponent(formatList) 	
							.addGroup(layout.createSequentialGroup()
									.addComponent(cancel)		
									.addComponent(save)
							)
					)		
					.addComponent(browse)
		);
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(
				layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addComponent(fileDir) //the label Output File directory
							.addComponent(fileNameLabel) //the label output file name
					)
					.addGroup(layout.createSequentialGroup()
							.addComponent(filePath) 			// filepath text
							.addComponent(fileName) 	        //filename text
							.addComponent(formatList) 	
							.addGroup(layout.createParallelGroup()
									.addComponent(cancel)		
									.addComponent(save)
							)
					)		
					.addComponent(browse)
		);
		// end of Layout Code
		frame.addWindowListener(Core.getInstance().getUI().new WindowEventHandler());
		frame.pack(); // automatically set the frame size
		frame.setLocationRelativeTo(null); 	// center the window on the screen
		frame.setModal(true);
		frame.setVisible(true);
		//the order of modal and visible must be exactly this one!
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if(obj == cancel){
			frame.setModal(false);
			frame.dispose();
		}
		else if(obj == browse){
			browse();
		}
		else if(obj == save){
			save();
		}
	}
	
	private void save() {
		try {
			AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
			String outPath = filePath.getText();
			String outName = fileName.getText();
			String outF = formatList.getSelectedValue().toString();
			String fullName = outPath+"/"+outName+"."+outF;

			if(outPath.equals("")){
				JOptionPane.showMessageDialog(frame, "Select the directory for the output file to proceed.");
			}
			else if(outName.equals("")){
				JOptionPane.showMessageDialog(frame, "Insert a name for the output file to proceed");
			}
			else if(outName.indexOf(".")!=-1) {
				JOptionPane.showMessageDialog(frame, "Insert a file name without Extension");
			}
			else{
				//boolean ok = true;
				try {
					//File f = new File(fullName);
					if(outF.equals(OutputController.RDF)){
						prefs.saveLastNameOutput(outName);
						OutputController.printDocumentOAEI(fullName);
						Utility.displayMessagePane("File saved successfully.\nLocation: "+fullName+"\n", null);
						frame.setModal(false);
						frame.dispose();
					}
					else{
						prefs.saveLastNameOutput(outName);
						OutputController.printDocument(fullName);
						Utility.displayMessagePane("File saved successfully.\nLocation: "+fullName+"\n", null);
						frame.setModal(false);
						frame.dispose();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					//ok = false;
					JOptionPane.showMessageDialog(frame, "Error while saving the file\nTry to change filename or location.");
				}
			}
		}
		catch(Exception e) {
			//for developer users, when the tool released there should be a standard message like Unexpected Exception, for us it's useful to keep it full now
			JOptionPane.showMessageDialog(frame, e.getMessage());
			e.printStackTrace();
		}
	}

	private void browse() {
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences 
		JFileChooser fc; 
		
		//If the user has select an output file already the system will use the last ouput file dir to start the chooser, 
		//if he didn't the system will try to use the last dir used to open the ontologies, maybe is the same one of the reference or it's closer 
		//if not even that one exists, the chooser starts normally 
		if(prefs.getLastDirOutput().exists()) {  
			fc = new JFileChooser(prefs.getLastDirOutput()); 
		} 	 
		else if( prefs.getLastDir().exists() ) { 
			fc = new JFileChooser(prefs.getLastDir()); 
		} else { fc = new JFileChooser(); } 

		//This lines are needed to set the filechooser as directory chooser, we are creating a file filter class here which has to implements all needed methods.
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		fc.setFileFilter(new FileFilter() {  
            public boolean accept(File f) {  
                if (f.isDirectory()) { 
                    return true;   
                } 
                return false;  
            }
 
            public String getDescription() { 
                return "";
            }
        });

		int returnVal = fc.showOpenDialog(frame);

		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			File selectedfile = fc.getSelectedFile();	
			// ok, now that we know what file the user selected
			// let's save it for future use (for the chooser)
			prefs.saveLastDirOutput(selectedfile); 
			filePath.setText(selectedfile.getPath());
		}
	}
	
}
