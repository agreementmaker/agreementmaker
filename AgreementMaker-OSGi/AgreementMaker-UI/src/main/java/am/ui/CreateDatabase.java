package am.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import am.Utility;

public class CreateDatabase extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = -6783509852631814172L;
	
	private JTextField dbName,host, port, username;
	private JPasswordField password;
	private JLabel notice;
	private JButton cancel, test;
	private JLabel hostL, portL,DBNameL, userL, passL;
	private JPanel inputPanel;
	private JPanel buttonsPanel;
	private JPanel mainPanel; 
	
	public CreateDatabase(JDialog openFile){
		super(openFile,true);
		setup();
	}
	private void setup()
	{
		this.setTitle("Create A New Database");
		hostL=new JLabel("Host Name");
		portL=new JLabel("Port Number");
		DBNameL=new JLabel("New Database Name");
		userL=new JLabel("Admin Username");
		passL=new JLabel("Admin Password");
		
		host=new JTextField();
		port=new JTextField("5432");
		dbName=new JTextField();
		username=new JTextField();
		password=new JPasswordField();	
		notice= new JLabel("Notice: You will need to provide an Admin username and password.");
		
		cancel=new JButton("Cancel");
		test=new JButton("Create Database");
		
		cancel.addActionListener(this);
		test.addActionListener(this);
		
		inputPanel=new JPanel();
		buttonsPanel=new JPanel();
		mainPanel=new JPanel();
		
		GroupLayout inputPanelLayoutSource=new GroupLayout(inputPanel);
		inputPanel.setLayout(inputPanelLayoutSource);
		
		inputPanelLayoutSource.setAutoCreateGaps(true);
		inputPanelLayoutSource.setAutoCreateContainerGaps(true);
		
		inputPanelLayoutSource.setHorizontalGroup(
				inputPanelLayoutSource.createParallelGroup()
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(hostL)
							.addComponent(host)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(portL)
							.addComponent(port)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(DBNameL)
							.addComponent(dbName)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(userL)
							.addComponent(username)
					)
					.addGroup(inputPanelLayoutSource.createSequentialGroup()
							.addComponent(passL)
							.addComponent(password)
					)
			);
		
		inputPanelLayoutSource.setVerticalGroup(
				inputPanelLayoutSource.createSequentialGroup()
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(hostL)
							.addComponent(host)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(portL)
							.addComponent(port)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(DBNameL)
							.addComponent(dbName)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(userL)
							.addComponent(username)
					)
					.addGroup(inputPanelLayoutSource.createParallelGroup()
							.addComponent(passL)
							.addComponent(password)
					)
		);
		
		GroupLayout buttonsPanelLayout=new GroupLayout(buttonsPanel);
		buttonsPanel.setLayout(buttonsPanelLayout);
		
		buttonsPanelLayout.setAutoCreateGaps(true);
		buttonsPanelLayout.setAutoCreateContainerGaps(true);
		
		buttonsPanelLayout.setHorizontalGroup(
				buttonsPanelLayout.createSequentialGroup()
					.addComponent(cancel)
					.addComponent(test)
			);
		
		buttonsPanelLayout.setVerticalGroup(
				buttonsPanelLayout.createParallelGroup()
					.addComponent(cancel)
					.addComponent(test)
		);
		
		GroupLayout mainPanelLayout=new GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		
		mainPanelLayout.setAutoCreateGaps(true);
		mainPanelLayout.setAutoCreateContainerGaps(true);
		
		mainPanelLayout.setHorizontalGroup(
				mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(inputPanel)
					.addComponent(notice)
					.addComponent(buttonsPanel)
		);
		
		mainPanelLayout.setVerticalGroup(
				mainPanelLayout.createSequentialGroup()
					.addComponent(inputPanel)
					.addComponent(notice)
					.addComponent(buttonsPanel)
		);
		
		this.add(mainPanel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		if(obj==cancel){
			this.dispose();
		}
		else if(obj==test){
			if(createDB()){
				Utility.displayConfirmPane("The database "+dbName.getText()+" was created.", "Database Created");
				this.dispose();
			}
			else
				Utility.displayErrorPane("Database could not be created.  Please check the information entered.", "Error Creating Database");
		}
	}
	public boolean createDB(){
		Connection connect = null;
		Statement statement;
		try
		{
			Class.forName("org.postgresql.Driver");
			connect = DriverManager.getConnection("jdbc:postgresql://"+host.getText()+":"+port.getText(),username.getText(), 
					String.valueOf(password.getPassword())); 
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
		
		try {
			statement = connect.createStatement();
			statement.executeUpdate("CREATE DATABASE "+dbName.getText());
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
