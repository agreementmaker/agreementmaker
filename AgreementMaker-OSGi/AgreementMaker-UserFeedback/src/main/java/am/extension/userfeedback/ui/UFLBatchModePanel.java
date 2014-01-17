package am.extension.userfeedback.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import am.app.Core;
import am.extension.userfeedback.preset.ExperimentPreset;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.extension.userfeedback.preset.PresetStorage;
import am.ui.utility.SettingsDialog;
import am.ui.utility.SettingsPanel;
import am.utility.Pair;

public class UFLBatchModePanel extends SettingsPanel implements ActionListener {

	private static final long serialVersionUID = -6904848518527229784L;

	private String[] columnNames = { "#", "Matching Task", "Experiment Setup" };
	
	private JTable tblExperiments = new JTable();
	private JScrollPane scrExperiments;
	
	private JLabel lblRuns = new JLabel("UFL Batch Mode Runs:");
	private JButton btnAddRun = new JButton("add");
	private JButton btnDelRun = new JButton("del");
	private JButton btnLoadRuns = new JButton("load");
	private JButton btnSaveRuns = new JButton("save");
	
	private List<Pair<MatchingTaskPreset,ExperimentPreset>> runs;
	
	private DefaultTableModel model;
	
	private static final String PREF_LOAD_DIR = "PREF_LOAD_DIR";
	
	public UFLBatchModePanel() {
		super();
		
		if( runs == null ) {
			runs = new LinkedList<>();
		}
		
		scrExperiments = new JScrollPane(tblExperiments);
		tblExperiments.setFillsViewportHeight(true);
		
		updateTable();
		
		btnAddRun.addActionListener(this);
		btnDelRun.addActionListener(this);
		btnLoadRuns.addActionListener(this);
		btnSaveRuns.addActionListener(this);
		
		//tblExperiments.setPreferredSize(new Dimension(200,200));
		
		setBorder(new EmptyBorder(10,10,10,10));
		setLayout(new GridBagLayout());
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			add(lblRuns, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(0, 50, 0, 0);
			add(btnAddRun, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 0;
			c.insets = new Insets(0, 5, 0, 0);
			add(btnDelRun, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 3;
			c.gridy = 0;
			c.insets = new Insets(0, 5, 0, 0);
			add(btnLoadRuns, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 4;
			c.gridy = 0;
			c.insets = new Insets(0, 5, 0, 0);
			add(btnSaveRuns, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 5;
			c.insets = new Insets(10, 0, 0, 0);
			add(scrExperiments, c);
		}
		
	}

	private void updateTable() {
		
		Object[][] data = new Object[runs.size()][3];
		
		for( int i = 0; i < runs.size(); i++ ) {
			Pair<MatchingTaskPreset,ExperimentPreset> run = runs.get(i);
			data[i][0] = (Integer) i;
			data[i][1] = run.getLeft();
			data[i][2] = run.getRight();
		}
		
		model = new DefaultTableModel(data, columnNames);
				
		tblExperiments.setModel(model);
		TableColumn col = tblExperiments.getColumnModel().getColumn(0);
		col.setPreferredWidth(20);
		col.setMaxWidth(30);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnAddRun ) {
			UFLExperimentRunnerPanel ufl = new UFLExperimentRunnerPanel();
			SettingsDialog dialog = new SettingsDialog(getDialog(), ufl, "Add Experiment");
			dialog.setVisible(true);
			if( dialog.getStatus() == SettingsDialog.OK ) {
				runs.add(ufl.getRun());
				updateTable();
			}
			return;
		}
		
		if( e.getSource() == btnDelRun ) {
			int[] selectedRows = tblExperiments.getSelectedRows();
			
			List<Pair<MatchingTaskPreset,ExperimentPreset>> newRuns = new LinkedList<>();
			for( int i = 0; i < runs.size(); i++ ) {
				boolean excluded = false;
				for( int j = 0; j < selectedRows.length; j++ ) {
					if( selectedRows[j] == i ) excluded = true;
				}
				if( !excluded ) {
					newRuns.add(runs.get(i));
				}
			}
			
			runs = newRuns;
			updateTable();
			
			return;
		}
		
		
		if( e.getSource() == btnLoadRuns ) {
			Preferences prefs = Preferences.userNodeForPackage(getClass());
			String lastDir = prefs.get(PREF_LOAD_DIR, Core.getInstance().getRoot());
			
			JFileChooser fc = new JFileChooser(lastDir);
			FileFilter fl = new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					if( pathname.isDirectory() ) return true;

					if( pathname.getName().matches("(?i).*\\.xml$") ) {
						return true;
					}
					else {
						return false;
					}
				}

				@Override
				public String getDescription() {
					return "XML files (*.xml)";
				}
				
			};
			fc.setFileFilter(fl);
			int result = fc.showOpenDialog(this);
			if( result == JFileChooser.APPROVE_OPTION ) {
				prefs.put(PREF_LOAD_DIR, fc.getSelectedFile().getAbsolutePath());
				runs = PresetStorage.loadBatchModeRunsFromXML(fc.getSelectedFile().getAbsolutePath());
				updateTable();
			}
		}
		
		if( e.getSource() == btnSaveRuns ) {
			String fileName = JOptionPane.showInputDialog("Please enter a file name, without any file extensions:");
			fileName = fileName + ".xml";
			String outputFile = PresetStorage.saveBatchModeRunsToXML(runs, fileName);
			if( outputFile == null ) {
				JOptionPane.showMessageDialog(this, "Could not save batch mode runs.", "Error: Could not save", JOptionPane.ERROR_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(this, "Saved batch mode runs to:\n" + outputFile, "Saved successfully", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	public List<Pair<MatchingTaskPreset,ExperimentPreset>> getRuns() {
		return runs;
	}
	
	/* This entry point is used to preview the panel layout */
	public static void main(String[] args) {
		final JFrame d = new JFrame("Run UFL Experiment");
		JButton btnSettings = new JButton("Launch Settings ...");
		btnSettings.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				SettingsPanel ufl = new UFLBatchModePanel();
				SettingsDialog dialog = new SettingsDialog(d, ufl, "Run Experiments");
				dialog.setVisible(true);
			}
		});
		d.add(btnSettings);
		d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
	}
	
}
