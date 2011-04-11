package am.userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class OnDiskLocationDialog extends JDialog implements ActionListener{

	private JTextField locationSource;
	private JTextField locationTarget;
	private JLabel locationL;
	private JButton cancel, proceed, browseSource, browseTarget;
	private Preferences p;
	private JPanel buttonsPanel, inputPanelSource,inputPanelTarget, mainPanel;
	private JPanel source, target;
	private JLabel locationLT;
	
	private final static String TDB_LAST_SOURCE_DIRECTORY = "TDB_LAST_SOURCE_DIRECTORY";
	private final static String TDB_LAST_TARGET_DIRECTORY = "TDB_LAST_TARGET_DIRECTORY";

	private JCheckBox persistentSourceChk, persistentTargetChk;
	
	private boolean sourceEnabled, targetEnabled;
	
	public OnDiskLocationDialog(JDialog openFile, boolean sourceEnabled, boolean targetEnabled)
	{
		super(openFile,true);
		p=Preferences.userNodeForPackage(this.getClass());
		this.sourceEnabled=sourceEnabled;
		this.targetEnabled=targetEnabled;
		setup();
	}
	private void setup()
	{
		this.setTitle("Database Settings");
		locationL=new JLabel("Directory Location");
		
		locationLT=new JLabel("Directory Location");
		
		locationSource=new JTextField(p.get(TDB_LAST_SOURCE_DIRECTORY, ""));
	
		locationTarget=new JTextField(p.get(TDB_LAST_TARGET_DIRECTORY, ""));
		
		cancel=new JButton("Cancel");
		proceed=new JButton("Proceed");
		browseSource=new JButton("...");
		browseTarget=new JButton("...");
		
		
		cancel.addActionListener(this);
		proceed.addActionListener(this);
		browseSource.addActionListener(this);
		browseTarget.addActionListener(this);
		
		persistentSourceChk=new JCheckBox("Persistent Source Directory");
		persistentTargetChk=new JCheckBox("Persistent Target Directory");
		
		persistentSourceChk.setSelected(p.getBoolean("persistentSource", false));
		persistentTargetChk.setSelected(p.getBoolean("persistentTarget", false));
		
		
		source=new JPanel();
		target=new JPanel();
		source.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Source"));
		target.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Target"));
		
		//init the panels, add the componants to them, create layouts for them
		inputPanelSource=new JPanel();
		inputPanelTarget=new JPanel();
		buttonsPanel=new JPanel();
		mainPanel=new JPanel();//this panel has both input and button panels in it
		
		GroupLayout inputPanelLayoutSource=new GroupLayout(inputPanelSource);
		inputPanelSource.setLayout(inputPanelLayoutSource);
		
		inputPanelLayoutSource.setAutoCreateGaps(true);
		inputPanelLayoutSource.setAutoCreateContainerGaps(true);
		
		inputPanelLayoutSource.setHorizontalGroup(
				inputPanelLayoutSource.createParallelGroup()
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(locationL)
							.addComponent(locationSource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
							.addComponent(browseSource)
							
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(persistentSourceChk)
					)
			);
		
		inputPanelLayoutSource.setVerticalGroup(
				inputPanelLayoutSource.createSequentialGroup()
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(locationL)
							.addComponent(locationSource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
							.addComponent(browseSource)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(persistentSourceChk)
					)
		);
		
		GroupLayout inputPanelLayoutTarget=new GroupLayout(inputPanelTarget);
		inputPanelTarget.setLayout(inputPanelLayoutTarget);
		
		inputPanelLayoutTarget.setAutoCreateGaps(true);
		inputPanelLayoutTarget.setAutoCreateContainerGaps(true);
		
		inputPanelLayoutTarget.setHorizontalGroup(
				inputPanelLayoutTarget.createParallelGroup()
					.addGroup(inputPanelLayoutTarget.createSequentialGroup()
							.addComponent(locationLT)
							.addComponent(locationTarget, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
							.addComponent(browseTarget)
					)
					.addGroup(inputPanelLayoutTarget.createSequentialGroup()
							.addComponent(persistentTargetChk)
					)
			);
		
		inputPanelLayoutTarget.setVerticalGroup(
				inputPanelLayoutTarget.createSequentialGroup()
					.addGroup(inputPanelLayoutTarget.createParallelGroup()
							.addComponent(locationLT)
							.addComponent(locationTarget, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE)
							.addComponent(browseTarget)
					)
					.addGroup(inputPanelLayoutTarget.createParallelGroup()
							.addComponent(persistentTargetChk)
					)
		);
		
		GroupLayout buttonsPanelLayout=new GroupLayout(buttonsPanel);
		buttonsPanel.setLayout(buttonsPanelLayout);
		
		buttonsPanelLayout.setAutoCreateGaps(true);
		buttonsPanelLayout.setAutoCreateContainerGaps(true);
		
		buttonsPanelLayout.setHorizontalGroup(
				buttonsPanelLayout.createSequentialGroup()
					.addComponent(cancel)
					.addComponent(proceed)
			);
		
		buttonsPanelLayout.setVerticalGroup(
				buttonsPanelLayout.createParallelGroup()
					.addComponent(cancel)
					.addComponent(proceed)
		);
		
		source.add(inputPanelSource);
		target.add(inputPanelTarget);
		
		GroupLayout mainPanelLayout=new GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		
		mainPanelLayout.setAutoCreateGaps(true);
		mainPanelLayout.setAutoCreateContainerGaps(true);
		
		mainPanelLayout.setHorizontalGroup(
				mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(source)
					.addComponent(target)
					.addComponent(buttonsPanel)
		);
		
		mainPanelLayout.setVerticalGroup(
				mainPanelLayout.createSequentialGroup()
					.addComponent(source)
					.addComponent(target)
					.addComponent(buttonsPanel)
		);
		
		//check to see if either ontology is being loaded into a db, if not the fields are greyed out
		if(!sourceEnabled){
			locationSource.setEnabled(false);
			browseSource.setEnabled(false);
			persistentSourceChk.setEnabled(false);
		}
		if(!targetEnabled){
			locationTarget.setEnabled(false);
			browseTarget.setEnabled(false);
			persistentTargetChk.setEnabled(false);
		}
		
		this.add(mainPanel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	private boolean setPreferences()
	{
		p=Preferences.userNodeForPackage(this.getClass());
		
		p.put(TDB_LAST_SOURCE_DIRECTORY, locationSource.getText());
		p.put(TDB_LAST_TARGET_DIRECTORY, locationTarget.getText());
		
		p.putBoolean("persistentSource", persistentSourceChk.isSelected());
		p.putBoolean("persistentTarget", persistentTargetChk.isSelected());
		
		return true;//no problems found
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		if(obj==cancel){
			//cancel button code here
			this.dispose();
		}else if(obj==proceed){
			//proceed button code here
			if(setPreferences())
				this.dispose();
		}else if(obj==browseSource){
			
			JFileChooser fc = new JFileChooser();
			
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showOpenDialog(this);

			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				locationSource.setText(fc.getSelectedFile().getPath());
			}
			System.out.println(fc.getSelectedFile().getPath());
		}else if(obj==browseTarget){
			
			JFileChooser fc = new JFileChooser();
			
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			int returnVal = fc.showOpenDialog(this);

			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				locationTarget.setText(fc.getSelectedFile().getPath());
			}
		}
	}

}

