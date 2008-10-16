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
import javax.swing.filechooser.FileFilter;

import agreementMaker.AMException;
import agreementMaker.GSM;
import agreementMaker.development.ReferenceEvaluation;
import agreementMaker.userInterface.vertex.VertexDescriptionPane;


/**
 * @author Nalin
 *
 */
public class ReferenceOutputFileDialog implements ActionListener{
	
	/**
	 * @param args
	 */
	private JLabel fileType;
	private JTextField fileName;
	private JButton browse, cancel, evaluate, previous;
	
	private JLabel fileDir;
	private JTextField filePath;
	
	public JDialog frame;
	private JList formatList;
	private ReferenceFileDialog prevDialog;
	
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public ReferenceOutputFileDialog(ReferenceFileDialog prev) {
		  
		prevDialog = prev;
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		frame = new JDialog();
		frame.setTitle("Evaluate reference - Output file ");
		fileType = new JLabel("File name (NO EXT)");
		fileName = new JTextField(0);
		fileDir = new JLabel("Output file directory");
		filePath = new JTextField(0);
		
		//the system suggests the last file opened
		if( prefs.getLastDirRefOutput().exists() ) {
			filePath.setText(prefs.getLastDirRefOutput().getPath());
		}
		fileName.setText(prefs.getLastNameRefOutput());
		
		browse = new JButton("Browse...");
		cancel = new JButton("Cancel");
		previous = new JButton("Previous");
		evaluate = new JButton("Evaluate");
		browse.addActionListener(this);
		cancel.addActionListener(this);
		evaluate.addActionListener(this);
		previous.addActionListener(this);
		
		String[] format_list = {ReferenceEvaluation.OUTF1};
		
		
		formatList = new JList(format_list);
		formatList.setPrototypeCellValue("01234567890123456789"); // this string sets the width of the list
		formatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		formatList.setVisibleRowCount(3);
		formatList.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "File format"));
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
							.addComponent(fileType) //the label output file name
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(filePath) 			// filepath text
							.addComponent(fileName) 	        //filename text
							.addComponent(formatList) 	
							.addGroup(layout.createSequentialGroup()
									.addComponent(cancel)		
									.addComponent(previous)
									.addComponent(evaluate)
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
							.addComponent(fileType) //the label output file name
					)
					.addGroup(layout.createSequentialGroup()
							.addComponent(filePath) 			// filepath text
							.addComponent(fileName) 	        //filename text
							.addComponent(formatList) 	
							.addGroup(layout.createParallelGroup()
									.addComponent(cancel)		
									.addComponent(previous)
									.addComponent(evaluate)
							)
					)		
					.addComponent(browse)
		);

		// end of Layout Code
		
		frame.addWindowListener(new WindowEventHandler());//THIS SHOULD BE CHANGED THE PROGRAM SHOULD NOT CLOSE
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
		
		if(obj == cancel){
			prevDialog.frame.dispose();	
			prevDialog = null;
			frame.setModal(false);
			frame.dispose();
		}
		else if(obj == browse){
			browse();
		}
		else if(obj == previous){
			frame.setModal(false);
			frame.setVisible(false);
			prevDialog.frame.setModal(true);
			prevDialog.frame.setVisible(true);
		}
		else if(obj == evaluate){
			evaluate();
		}
	}
	
	private void evaluate() {
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences
		String refN = prevDialog.filePath.getText();
		String refF = prevDialog.formatList.getSelectedValue().toString();
		String outPath = filePath.getText();
		String outName = fileName.getText();
		String outN = outPath+"\\"+outName+".txt";//right now only one extension is admitted
		String outF = formatList.getSelectedValue().toString();
		if(outPath.equals("")){
			JOptionPane.showMessageDialog(frame, "Select the directory for the output file to proceed.");
		}
		else if(outName.equals("")){
			JOptionPane.showMessageDialog(frame, "Inser a name for the output file to proceed");
		}
		else if(outName.indexOf(".")!=-1) {
			JOptionPane.showMessageDialog(frame, "Insert a file name without Extension");
		}
		else{
			prefs.saveLastNameRefOutput(fileName.getText());
			try {
				//The referenceEvaluation class keeps the control methods of this task
				ReferenceEvaluation refEva = new ReferenceEvaluation(prevDialog.ui.getOntologyController(),refN, refF, outN, outF);
				refEva.evaluate();
				JOptionPane.showMessageDialog(frame,refEva.getReport()+"\n\nA more detailed report can be found in the Output file:\n"+outN+"\nYou can open the file with Excel also.");
			}
			catch(AMException ame) {
				JOptionPane.showMessageDialog(frame, ame.getMessage());
				//no need to print in console because it's just a user error;
			}
			catch(Exception e) {
				//for developer users, when the tool released there should be a standard message like Unexpected Exception, for us it's useful to keep it full now
				JOptionPane.showMessageDialog(frame, e.getMessage());
				e.printStackTrace();
			}
			
			prevDialog.frame.dispose();
			prevDialog = null;
			frame.setModal(false);
			frame.dispose();
		}
	}

	private void browse() {
		AppPreferences prefs = new AppPreferences(); // Class interface to Application Preferences 
		JFileChooser fc; 
		
		//If the user has select an output file already the system will use the last ouput file dir to start the chooser, 
		//if he didn't the system will try to use the last dir used to open the ontologies, maybe is the same one of the reference or it's closer 
		//if not even that one exists, the chooser starts normally 
		if(prefs.getLastDirRefOutput().exists()) {  
			fc = new JFileChooser(prefs.getLastDirRefOutput()); 
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
			prefs.saveLastDirRefOutput(selectedfile); 
			filePath.setText(selectedfile.getPath());
		}
	}
	
}
