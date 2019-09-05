package am.ui.glue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import am.extension.batchmode.api.BatchModeFileReader;
import am.extension.batchmode.api.BatchModeRunner;
import am.extension.batchmode.api.BatchModeSpec;
import am.ui.UICore;
import am.ui.UIMenu;
import am.ui.api.AMMenuItem;

public class BatchModeMenuItem extends JMenuItem implements AMMenuItem {

	private static final long serialVersionUID = -7679368085474956610L;
	
	private static final String PREF_LAST_FILE = "BATCHMODE_LAST_FILE";
	
	
	public BatchModeMenuItem() {
		super("Batch Mode");
		
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BatchModeMenuItem.this.runBatchMode();
			}
		});
	}
	
	@Override public String getMenuLocation() { return UIMenu.MENU_MATCHERS + "/"; }
	@Override public JMenuItem getMenuItem() { return this; }
	
	/**
	 * Asks for an input for the BatchModeFile and then runs the batch mode.
	 */
	private void runBatchMode() {
		
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		String previousFile = prefs.get(PREF_LAST_FILE, "");
		
		// ask user to select the xml file
		final JFileChooser fd = new JFileChooser();
		
		if( !previousFile.isEmpty() )
			fd.setSelectedFile(new File(previousFile));
		
		fd.setDialogTitle("Select BatchMode JSON File");
		
		FileFilter xmlFileFilter = new FileFilter() {
			@Override public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".json");
			}

			@Override public String getDescription() {
				return "JSON File (*.json)";
			}
		};
		
		fd.addChoosableFileFilter(xmlFileFilter);
		fd.setFileFilter(xmlFileFilter);
		
		int retVal = fd.showOpenDialog(UICore.getUI().getUIFrame());
		
		if( retVal == JFileChooser.APPROVE_OPTION ) {
			final File selectedFile = fd.getSelectedFile();
			try {
				prefs.put(PREF_LAST_FILE, selectedFile.getCanonicalPath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Runnable sbm = new Runnable() {
				@Override public void run() {
					try {
//                        ServiceReference<BatchModeFileReader> readerServicereference = Activator.getContext().getServiceReference(BatchModeFileReader.class);
//                        BatchModeFileReader reader = Activator.getContext().getService(readerServicereference);

//                        ServiceReference<BatchModeRunner> runnerServiceReference = Activator.getContext().getServiceReference(BatchModeRunner.class);
//                        BatchModeRunner runner = Activator.getContext().getService(runnerServiceReference);

//                        BatchModeSpec spec = reader.read(new FileInputStream(selectedFile));
//                        runner.run(spec);
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(
								UICore.getUI().getUIFrame(), 
								e.getClass() + "\n" + e.getMessage(), 
								"ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
					}

                    JOptionPane.showMessageDialog(UICore.getUI().getUIFrame(),  "Batch Mode runner has completed.", "Completed", JOptionPane.INFORMATION_MESSAGE);
				}
			};
			
			Thread sbmThread = new Thread(sbm);
			sbmThread.setName("SimpleBatchModeRunner " +  sbmThread.getId());
			sbmThread.start();
		}	
	}
}
