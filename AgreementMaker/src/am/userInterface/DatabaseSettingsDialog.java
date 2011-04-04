package am.userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import am.Utility;
import am.app.triplestore.jenatdb.JenaTDBTripleStore;

public class DatabaseSettingsDialog extends JDialog implements ActionListener{
	
	private JTextField dbNameSource,hostSource, portSource, usernameSource;
	private JPasswordField passwordSource;
	private JTextField dbNameTarget,hostTarget, portTarget, usernameTarget;
	private JPasswordField passwordTarget;
	private JLabel hostL, portL,DBNameL, userL, passL;
	private JButton cancel, proceed, test;
	private Preferences p;
	private JPanel buttonsPanel, inputPanelSource,inputPanelTarget, mainPanel;
	private JPanel source, target;
	private JLabel hostLT;
	private JLabel portLT;
	private JLabel DBNameLT;
	private JLabel userLT;
	private JLabel passLT;
	
	private JCheckBox sourceInfo, targetInfo;
	private JLabel sourceInfoLbl, targetInfoLbl;
	
	public DatabaseSettingsDialog(JDialog openFile)
	{
		super(openFile,true);
		p=Preferences.userNodeForPackage(this.getClass());
		setup();
	}
	private void setup()
	{
		this.setTitle("Database Settings");
		hostL=new JLabel("Host Name");
		portL=new JLabel("Port Number");
		DBNameL=new JLabel("Database Name");
		userL=new JLabel("Username");
		passL=new JLabel("Password");
		
		hostLT=new JLabel("Host Name");
		portLT=new JLabel("Port Number");
		DBNameLT=new JLabel("Database Name");
		userLT=new JLabel("Username");
		passLT=new JLabel("Password");
		
		hostSource=new JTextField(p.get("hostSource", ""));
		portSource=new JTextField(String.valueOf(p.getInt("portSource", 5432)));
		dbNameSource=new JTextField(p.get("dbNameSource", ""));
		usernameSource=new JTextField(p.get("usernameSource", ""));
		passwordSource=new JPasswordField(p.get("passwordSource", ""));	
		
		hostTarget=new JTextField(p.get("hostTarget", ""));
		portTarget=new JTextField(String.valueOf(p.getInt("portTarget", 5432)));
		dbNameTarget=new JTextField(p.get("dbNameTarget", ""));
		usernameTarget=new JTextField(p.get("usernameTarget", ""));
		passwordTarget=new JPasswordField(p.get("passwordTarget", ""));	
		
		cancel=new JButton("Cancel");
		proceed=new JButton("Proceed");
		test=new JButton("Test Connection");
		
		sourceInfoLbl=new JLabel("Source login information is the same as target login information");
		targetInfoLbl=new JLabel("Target login information is the same as source login information");
		
		sourceInfo=new JCheckBox();
		targetInfo=new JCheckBox();
		
		sourceInfo.setSelected(p.getBoolean("sourceChk", false));
		targetInfo.setSelected(p.getBoolean("targetChk", false));
		
		if(sourceInfo.isSelected()){
			usernameSource.setEnabled(false);
			passwordSource.setEnabled(false);
			targetInfo.setEnabled(false);
		}
		if(targetInfo.isSelected()){
			usernameTarget.setEnabled(false);
			passwordTarget.setEnabled(false);
			sourceInfo.setEnabled(false);
		}
		
		sourceInfo.addActionListener(this);
		targetInfo.addActionListener(this);
		cancel.addActionListener(this);
		proceed.addActionListener(this);
		test.addActionListener(this);
		
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
							.addComponent(hostL)
							.addComponent(hostSource)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(portL)
							.addComponent(portSource)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(DBNameL)
							.addComponent(dbNameSource)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(userL)
							.addComponent(usernameSource)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(passL)
							.addComponent(passwordSource)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(sourceInfo)
							.addComponent(sourceInfoLbl)
					)
			);
		
		inputPanelLayoutSource.setVerticalGroup(
				inputPanelLayoutSource.createSequentialGroup()
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(hostL)
							.addComponent(hostSource)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(portL)
							.addComponent(portSource)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(DBNameL)
							.addComponent(dbNameSource)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(userL)
							.addComponent(usernameSource)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(passL)
							.addComponent(passwordSource)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(sourceInfo)
							.addComponent(sourceInfoLbl)
					)
		);
		
		GroupLayout inputPanelLayoutTarget=new GroupLayout(inputPanelTarget);
		inputPanelTarget.setLayout(inputPanelLayoutTarget);
		
		inputPanelLayoutTarget.setAutoCreateGaps(true);
		inputPanelLayoutTarget.setAutoCreateContainerGaps(true);
		
		inputPanelLayoutTarget.setHorizontalGroup(
				inputPanelLayoutTarget.createParallelGroup()
					.addGroup(inputPanelLayoutTarget.createSequentialGroup()
							.addComponent(hostLT)
							.addComponent(hostTarget)
					)
					.addGroup(inputPanelLayoutTarget.createSequentialGroup()
							.addComponent(portLT)
							.addComponent(portTarget)
					)
					.addGroup(inputPanelLayoutTarget.createSequentialGroup()
							.addComponent(DBNameLT)
							.addComponent(dbNameTarget)
					)
					.addGroup(inputPanelLayoutTarget.createSequentialGroup()
							.addComponent(userLT)
							.addComponent(usernameTarget)
					)
					.addGroup(inputPanelLayoutTarget.createSequentialGroup()
							.addComponent(passLT)
							.addComponent(passwordTarget)
					)
					.addGroup(inputPanelLayoutTarget.createSequentialGroup()
							.addComponent(targetInfo)
							.addComponent(targetInfoLbl)
					)
			);
		
		inputPanelLayoutTarget.setVerticalGroup(
				inputPanelLayoutTarget.createSequentialGroup()
					.addGroup(inputPanelLayoutTarget.createParallelGroup()
							.addComponent(hostLT)
							.addComponent(hostTarget)
					)
					.addGroup(inputPanelLayoutTarget.createParallelGroup()
							.addComponent(portLT)
							.addComponent(portTarget)
					)
					.addGroup(inputPanelLayoutTarget.createParallelGroup()
							.addComponent(DBNameLT)
							.addComponent(dbNameTarget)
					)
					.addGroup(inputPanelLayoutTarget.createParallelGroup()
							.addComponent(userLT)
							.addComponent(usernameTarget)
					)
					.addGroup(inputPanelLayoutTarget.createParallelGroup()
							.addComponent(passLT)
							.addComponent(passwordTarget)
					)
					.addGroup(inputPanelLayoutTarget.createParallelGroup()
							.addComponent(targetInfo)
							.addComponent(targetInfoLbl)
					)
		);
		
		GroupLayout buttonsPanelLayout=new GroupLayout(buttonsPanel);
		buttonsPanel.setLayout(buttonsPanelLayout);
		
		buttonsPanelLayout.setAutoCreateGaps(true);
		buttonsPanelLayout.setAutoCreateContainerGaps(true);
		
		buttonsPanelLayout.setHorizontalGroup(
				buttonsPanelLayout.createSequentialGroup()
					.addComponent(test)
					.addComponent(cancel)
					.addComponent(proceed)
			);
		
		buttonsPanelLayout.setVerticalGroup(
				buttonsPanelLayout.createParallelGroup()
					.addComponent(test)
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
		
		this.add(mainPanel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	private boolean setPreferences()
	{
		p=Preferences.userNodeForPackage(this.getClass());
		
		p.put("hostSource", hostSource.getText());
		p.put("dbNameSource",dbNameSource.getText());
		
		if(sourceInfo.isSelected()){
			p.put("usernameSource",usernameTarget.getText());
			p.put("passwordSource",String.valueOf(passwordTarget.getPassword()));
		}else{
			p.put("usernameSource",usernameSource.getText());
			p.put("passwordSource",String.valueOf(passwordSource.getPassword()));
		}
		
		p.put("hostTarget", hostTarget.getText());
		p.put("dbNameTarget",dbNameTarget.getText());
		if(targetInfo.isSelected()){
			p.put("usernameTarget",usernameSource.getText());
			p.put("passwordTarget",String.valueOf(passwordSource.getPassword()));
		}else{
			p.put("usernameTarget",usernameTarget.getText());
			p.put("passwordTarget",String.valueOf(passwordTarget.getPassword()));
		}
		
		p.putBoolean("sourceChk", sourceInfo.isSelected());
		p.putBoolean("targetChk", targetInfo.isSelected());
		
		try{
			p.putInt("portSource", Integer.parseInt(portSource.getText()));
		}catch(NumberFormatException e)
		{
			Utility.displayErrorPane("Source port number is invalid.", "ERROR");
			return false;//problem found, do not proceed
		}
		try{
			p.putInt("portTarget", Integer.parseInt(portSource.getText()));
		}catch(NumberFormatException e)
		{
			Utility.displayErrorPane("Target port number is invalid.", "ERROR");
			return false;//problem found, do not proceed
		}
		return true;//no problems found
	}
	private void testConnection()
	{	
		JenaTDBTripleStore testDB=new JenaTDBTripleStore(
			p.get("hostSource",""),p.getInt("portSource", 5432),p.get("dbNameSource", ""),p.get("usernameSource", ""),p.get("passwordSource", ""),"",
			p.get("hostTarget",""),p.getInt("portTarget", 5432),p.get("dbNameTarget", ""),p.get("usernameTarget", ""),p.get("passwordTarget", ""),""
			, false, false);
		if(testDB.openSourceConnection()){
			Utility.displayMessagePane("Connected successfully to the Source database.", "Connection Successful");
			testDB.closeSourceConnection();
		}else
			Utility.displayErrorPane("Could not connect to the Source database.  Please check the settings and try again.","Connection Unsuccessful");
		
		if(testDB.openTargetConnection()){
			Utility.displayMessagePane("Connected successfully to the Target database.", "Connection Successful");
			testDB.closeSourceConnection();
		}else
			Utility.displayErrorPane("Could not connect to the Target database.  Please check the settings and try again.","Connection Unsuccessful");
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
		}else if(obj==test){
			//test button code here
			if(setPreferences())
				testConnection();
		}else if(obj==sourceInfo){
			if(sourceInfo.isSelected()){
				usernameSource.setEnabled(false);
				passwordSource.setEnabled(false);
				targetInfo.setEnabled(false);
			}
			else{
				usernameSource.setEnabled(true);
				passwordSource.setEnabled(true);
				targetInfo.setEnabled(true);
			}
		}
		else if(obj==targetInfo){
			if(targetInfo.isSelected()){
				usernameTarget.setEnabled(false);
				passwordTarget.setEnabled(false);
				sourceInfo.setEnabled(false);
			}
			else{
				usernameTarget.setEnabled(true);
				passwordTarget.setEnabled(true);
				sourceInfo.setEnabled(true);
			}
		}
	}

}
