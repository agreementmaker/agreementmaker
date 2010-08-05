/**    ________________________________________
 * ___/ Copyright Notice / Warranty Disclaimer \________________
 *
 * @copyright { Copyright (c) 2010
 * Advances in Information Systems Laboratory at the
 * University of Illinois at Chicago
 * All Rights Reserved. }
 * 
 * @disclaimer {
 * This work is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. }
 * 
 *     _____________________
 * ___/ Authors / Changelog \___________________________________
 * 
 * 
 * @date July 29, 2010.  @author Cosmin.
 * @description Initial SEALS implementation.          
 * 
 *  
 */

package am.tools.seals;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.LayoutStyle.ComponentPlacement;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.utility.LinuxInetAddress;

public class SealsPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3284754599688612733L;

	
	private JButton btnPublish, btnDefaultName;
	private JTextField txtHost, txtPort, txtEndpoint;
	private JComboBox cmbMatcherList;
	/**
	 * Create the layout of the SEALS interface panel.
	 */
	public SealsPanel() {
		super();
		
		// find out the current ip of the computer
		String currentIP = null;
		String underHostText = null; 
		try {
			InetAddress ip = LinuxInetAddress.getLocalHost4();
			currentIP = ip.getHostAddress();
		} catch (UnknownHostException e) {
			//e.printStackTrace();
		}
		
		
		// create all the user interface elements for the settings panel.
		
		JLabel lblSealsLogo = new JLabel();
		lblSealsLogo.setIcon(new ImageIcon("images/seals-logo.jpg"));
		
		JLabel lblHost = new JLabel("Host:");
		txtHost = new JTextField(currentIP);
		//lblHost.setToolTipText("IP address/hostname of the computer.");
		txtHost.setToolTipText("IP address/hostname of the computer.");
		
		//JLabel lblUnderHost = new JLabel("Your current ip seems to be " + currentIP);
		
		JLabel lblPort = new JLabel("Port:");
		txtPort = new JTextField("8081", 6);
		//lblPort.setToolTipText("Any port that's free.");
		txtPort.setToolTipText("Free port number.");
		
		JLabel lblMatcher = new JLabel("Matcher:");
		//lblMatcher.setToolTipText("Matcher run to handle the align() requests.");
		
		String[] matcherList = MatcherFactory.getMatcherComboList();
		cmbMatcherList = new JComboBox(matcherList);
		cmbMatcherList.setToolTipText("Matcher run to handle the align() requests.");
		
		btnPublish = new JButton("Publish!");
		btnPublish.setToolTipText("Begin publishing.");
		
		
		JLabel lblEndpoint = new JLabel("Name:");
		txtEndpoint = new JTextField("matcherWS");
		txtEndpoint.setToolTipText("Endpoint name.");
		
		btnDefaultName = new JButton("Default");
		btnDefaultName.addActionListener(this);
		btnDefaultName.setToolTipText("Use the default matcher name.");
		
		int buttonWidth = btnDefaultName.getPreferredSize().width > btnPublish.getPreferredSize().width ? btnDefaultName.getPreferredSize().width : btnPublish.getPreferredSize().width;  
		
		
		// now create the settings panel
		
		JPanel pnlSettings = new JPanel();
		pnlSettings.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5,5,5,5)));
	
		
		GroupLayout settingsLayout = new GroupLayout(pnlSettings);
		settingsLayout.setAutoCreateGaps(true);
		settingsLayout.setAutoCreateContainerGaps(true);
		
		settingsLayout.setHorizontalGroup( settingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup( settingsLayout.createSequentialGroup()
						.addComponent(lblHost, GroupLayout.PREFERRED_SIZE, lblMatcher.getPreferredSize().width, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtHost)
						.addComponent(lblPort)
						.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, txtPort.getPreferredSize().width, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup( settingsLayout.createSequentialGroup()
						.addComponent(lblMatcher)
						.addComponent(cmbMatcherList)
						)
				.addGroup( settingsLayout.createSequentialGroup()
						.addComponent(lblEndpoint, GroupLayout.PREFERRED_SIZE, lblMatcher.getPreferredSize().width, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtEndpoint)
						.addGroup( settingsLayout.createParallelGroup()
								.addComponent(btnPublish)
								.addComponent(btnDefaultName, GroupLayout.PREFERRED_SIZE, buttonWidth, GroupLayout.PREFERRED_SIZE)
								)
						)
				.addComponent(btnPublish, GroupLayout.PREFERRED_SIZE, buttonWidth, GroupLayout.PREFERRED_SIZE)
				);
		
		settingsLayout.setVerticalGroup( settingsLayout.createSequentialGroup()
				.addGroup( settingsLayout.createParallelGroup()
						.addComponent(lblHost)
						.addComponent(txtHost)
						.addComponent(lblPort)
						.addComponent(txtPort)
						)
				.addGroup( settingsLayout.createParallelGroup()
						.addComponent(lblMatcher)
						.addComponent(cmbMatcherList)
						)
				.addGroup( settingsLayout.createParallelGroup()
						.addComponent(lblEndpoint)
						.addComponent(txtEndpoint)
						.addComponent(btnDefaultName)
						)
				.addGap(20)
				.addComponent(btnPublish)
				);
		
		pnlSettings.setLayout(settingsLayout);
		
		
		
		// create all the elements for the matcher progress panel
		
		JProgressBar barProgress = new JProgressBar(0, 100);
		barProgress.setValue(0);
		barProgress.setStringPainted(true);
		
		JTextArea txtReport = new JTextArea(8, 35);
		JScrollPane sclReport = new JScrollPane(txtReport);

		
		// create the matcher progress panel
		
		JPanel pnlProgressReport = new JPanel();
		
		pnlProgressReport.setBorder( BorderFactory.createCompoundBorder( 
				BorderFactory.createTitledBorder("Status"),
				BorderFactory.createEmptyBorder(5,5,5,5)));
	
		
		GroupLayout progressLayout = new GroupLayout(pnlProgressReport);
		progressLayout.setAutoCreateGaps(true);
		progressLayout.setAutoCreateContainerGaps(true);
		
		progressLayout.setHorizontalGroup( progressLayout.createParallelGroup()
				.addComponent(barProgress)
				.addComponent(sclReport)
				);
		
		progressLayout.setVerticalGroup( progressLayout.createSequentialGroup()
				.addComponent(barProgress)
				.addComponent(sclReport)
				);
		
		pnlProgressReport.setLayout(progressLayout);

			

		
		// create the layout for the dialog
		
		GroupLayout sealsLayout = new GroupLayout(this);
		
		sealsLayout.setAutoCreateGaps(true);
		sealsLayout.setAutoCreateContainerGaps(true);
		
		sealsLayout.setHorizontalGroup( sealsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup( sealsLayout.createSequentialGroup()
						.addComponent(lblSealsLogo)
						.addComponent(pnlSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						
						)
				.addComponent(pnlProgressReport)
				);
		
		sealsLayout.setVerticalGroup( sealsLayout.createSequentialGroup()
				.addGroup( sealsLayout.createParallelGroup()
						.addComponent(pnlSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSealsLogo)
						)
				.addComponent(pnlProgressReport)
				);
		
		this.setLayout(sealsLayout);
		
		
		txtReport.append("Your current IP address seems to be " + currentIP + ".\n");
		
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		
		if( actionEvent.getSource() == btnDefaultName ) {
			String className = MatcherFactory.getMatchersRegistryEntry( cmbMatcherList.getSelectedItem().toString() ).getMatcherClass();
			
			Pattern searchPattern = Pattern.compile( "\\w+$" );
			Matcher matcher = searchPattern.matcher(className); // get only the class name of the class definition
			if( matcher.find() ) {
				className = matcher.group();  // this is the matcher name
			}
			txtEndpoint.setText( className );	
		}
		
	}
	
}
