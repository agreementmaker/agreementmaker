package am.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;


public class OnDiskLocationDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 2226622082564201065L;
	
	private JTextField txtLocationSource;
	private JTextField txtLocationTarget;
	private JLabel lblSourceLocation;
	private JButton btnCancel, btnSave, btnBrowseSource, btnBrowseTarget;
	//private Preferences p;
	private JPanel mainPanel;
	private JPanel pnlSource, pnlTarget;
	private JLabel lblTargetLocation ;

	public final static String TDB_LAST_SOURCE_DIRECTORY = "TDB_LAST_SOURCE_DIRECTORY";
	public final static String TDB_LAST_TARGET_DIRECTORY = "TDB_LAST_TARGET_DIRECTORY";
	
	public final static String TDB_LAST_SOURCE_PERSISTENT = "TDB_LAST_SOURCE_PERSISTENT";
	public final static String TDB_LAST_TARGET_PERSISTENT = "TDB_LAST_TARGET_PERSISTENT";

	private JCheckBox chkSourcePersistent, chkTargetPersistent;

	private boolean sourceEnabled, targetEnabled;

	public OnDiskLocationDialog(JDialog openFile, boolean sourceEnabled, boolean targetEnabled)
	{
		super(openFile,true);

		this.sourceEnabled=sourceEnabled;
		this.targetEnabled=targetEnabled;
		setup();
		this.getRootPane().setDefaultButton(btnSave);  // make the default button work
		this.setResizable(false);
		this.setModal(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void setup()
	{
		Preferences p = Preferences.userNodeForPackage(this.getClass());
		
		this.setTitle("On Disk Settings");
		lblSourceLocation = new JLabel("Source Directory:");
		lblTargetLocation = new JLabel("Target Directory:");

		txtLocationSource = new JTextField(p.get(TDB_LAST_SOURCE_DIRECTORY, ""));
		txtLocationSource.setPreferredSize(new Dimension(400, txtLocationSource.getHeight()));

		txtLocationTarget=new JTextField(p.get(TDB_LAST_TARGET_DIRECTORY, ""));
		txtLocationTarget.setPreferredSize(new Dimension(400, txtLocationTarget.getHeight()));

		btnCancel = new JButton("Cancel");
		btnSave = new JButton("Save");
		btnBrowseSource = new JButton("...");
		btnBrowseTarget = new JButton("...");


		btnCancel.addActionListener(this);
		btnSave.addActionListener(this);
		btnBrowseSource.addActionListener(this);
		btnBrowseTarget.addActionListener(this);

		chkSourcePersistent = new JCheckBox("Persistent Source Directory");
		chkTargetPersistent = new JCheckBox("Persistent Target Directory");

		chkSourcePersistent.setSelected(p.getBoolean(TDB_LAST_SOURCE_PERSISTENT, false));
		chkTargetPersistent.setSelected(p.getBoolean(TDB_LAST_TARGET_PERSISTENT, false));


		pnlSource=new JPanel();
		pnlTarget=new JPanel();
		pnlSource.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Source Ontology"));
		pnlTarget.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Target Ontology"));

		//init the panels, add the components to them, create layouts for them
		mainPanel = new JPanel();//this panel has both input and button panels in it

		GroupLayout inputPanelLayoutSource=new GroupLayout(pnlSource);
		pnlSource.setLayout(inputPanelLayoutSource);

		inputPanelLayoutSource.setAutoCreateGaps(true);
		inputPanelLayoutSource.setAutoCreateContainerGaps(true);

		inputPanelLayoutSource.setHorizontalGroup(
				inputPanelLayoutSource.createParallelGroup()
				.addGroup(inputPanelLayoutSource.createSequentialGroup()
						.addComponent(lblSourceLocation)
						.addComponent(txtLocationSource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
						.addComponent(btnBrowseSource)

				)
				.addGroup(inputPanelLayoutSource.createSequentialGroup()
						.addComponent(chkSourcePersistent)
				)
		);

		inputPanelLayoutSource.setVerticalGroup(
				inputPanelLayoutSource.createSequentialGroup()
				.addGroup(inputPanelLayoutSource.createParallelGroup()
						.addComponent(lblSourceLocation)
						.addComponent(txtLocationSource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
						.addComponent(btnBrowseSource)
				)
				.addGroup(inputPanelLayoutSource.createParallelGroup()
						.addComponent(chkSourcePersistent)
				)
		);

		GroupLayout inputPanelLayoutTarget=new GroupLayout(pnlTarget);
		pnlTarget.setLayout(inputPanelLayoutTarget);

		inputPanelLayoutTarget.setAutoCreateGaps(true);
		inputPanelLayoutTarget.setAutoCreateContainerGaps(true);

		inputPanelLayoutTarget.setHorizontalGroup(
				inputPanelLayoutTarget.createParallelGroup()
				.addGroup(inputPanelLayoutTarget.createSequentialGroup()
						.addComponent(lblTargetLocation )
						.addComponent(txtLocationTarget, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
						.addComponent(btnBrowseTarget)
				)
				.addGroup(inputPanelLayoutTarget.createSequentialGroup()
						.addComponent(chkTargetPersistent)
				)
		);

		inputPanelLayoutTarget.setVerticalGroup(
				inputPanelLayoutTarget.createSequentialGroup()
				.addGroup(inputPanelLayoutTarget.createParallelGroup()
						.addComponent(lblTargetLocation )
						.addComponent(txtLocationTarget, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
						.addComponent(btnBrowseTarget)
				)
				.addGroup(inputPanelLayoutTarget.createParallelGroup()
						.addComponent(chkTargetPersistent)
				)
		);

		GroupLayout mainPanelLayout=new GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);

		mainPanelLayout.setAutoCreateGaps(true);
		mainPanelLayout.setAutoCreateContainerGaps(true);

		mainPanelLayout.setHorizontalGroup(
				mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(pnlSource)
				.addComponent(pnlTarget)
				.addGroup( mainPanelLayout.createSequentialGroup()
						.addComponent(btnCancel)
						.addComponent(btnSave)
				)
		);

		mainPanelLayout.setVerticalGroup(
				mainPanelLayout.createSequentialGroup()
				.addComponent(pnlSource)
				.addComponent(pnlTarget)
				.addGroup( mainPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(btnCancel)
						.addComponent(btnSave)
				)
		);

		//check to see if either ontology is being loaded into a db, if not the fields are greyed out
		if(!sourceEnabled){
			pnlSource.setEnabled(false);
			lblSourceLocation.setEnabled(false);
			txtLocationSource.setEnabled(false);
			btnBrowseSource.setEnabled(false);
			chkSourcePersistent.setEnabled(false);
		}
		if(!targetEnabled){
			pnlTarget.setEnabled(false);
			lblTargetLocation.setEnabled(false);
			txtLocationTarget.setEnabled(false);
			btnBrowseTarget.setEnabled(false);
			chkTargetPersistent.setEnabled(false);
		}

		this.add(mainPanel);
	}
	
	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		Action actionListener = new AbstractAction() {
			private static final long serialVersionUID = 1774539460694983567L;
			public void actionPerformed(ActionEvent actionEvent) {
				btnCancel.doClick();
			}
		};
		InputMap inputMap = rootPane
		.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}
	
	private boolean setPreferences()
	{
		Preferences p=Preferences.userNodeForPackage(this.getClass());

		if( txtLocationSource.isEnabled() ) p.put(TDB_LAST_SOURCE_DIRECTORY, txtLocationSource.getText());
		if( txtLocationTarget.isEnabled() ) p.put(TDB_LAST_TARGET_DIRECTORY, txtLocationTarget.getText());

		if( chkSourcePersistent.isEnabled() ) p.putBoolean(TDB_LAST_SOURCE_PERSISTENT, chkSourcePersistent.isSelected());
		if( chkTargetPersistent.isEnabled() ) p.putBoolean(TDB_LAST_TARGET_PERSISTENT, chkTargetPersistent.isSelected());

		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		if(obj==btnCancel) {
			//cancel button code here
			setVisible(false);
		}
		else if(obj==btnSave) {
			//proceed button code here
			setPreferences();
			setVisible(false);
		}
		else if(obj==btnBrowseSource) {
			Preferences p=Preferences.userNodeForPackage(this.getClass());
			
			File selectedFile = new File(p.get(TDB_LAST_SOURCE_DIRECTORY, "."));
			
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setSelectedFile(selectedFile);
			int returnVal = fc.showOpenDialog(this);
			
			if( returnVal == JFileChooser.APPROVE_OPTION ) txtLocationSource.setText(fc.getSelectedFile().getPath());
		}
		else if(obj==btnBrowseTarget){
			Preferences p=Preferences.userNodeForPackage(this.getClass());
			
			File selectedFile = new File(p.get(TDB_LAST_TARGET_DIRECTORY, "."));
			
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setSelectedFile(selectedFile);
			int returnVal = fc.showOpenDialog(this);

			if( returnVal == JFileChooser.APPROVE_OPTION ) txtLocationTarget.setText(fc.getSelectedFile().getPath());
		}
	}

}

