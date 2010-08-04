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
import java.net.InetAddress;
import java.net.UnknownHostException;

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
import javax.swing.LayoutStyle.ComponentPlacement;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.MatcherFactory;

public class SealsPanel extends JPanel {

	private static final long serialVersionUID = 3284754599688612733L;

	/**
	 * Create the layout of the SEALS interface panel.
	 */
	public SealsPanel() {
		super();
		
		// find out the current ip of the computer
		String currentIP = null;
		String underHostText = null; 
		try {
			InetAddress ip = InetAddress.getLocalHost();
			currentIP = ip.getHostAddress();
		} catch (UnknownHostException e) {
			//e.printStackTrace();
		}
		
		
		// create all the user interface elements for the settings panel.
		
		JLabel lblSealsLogo = new JLabel();
		lblSealsLogo.setIcon(new ImageIcon("images/seals-logo.jpg"));
		
		JLabel lblHost = new JLabel("Host:");
		JTextField txtHost = new JTextField(currentIP);

		JLabel lblUnderHost = new JLabel("Your current ip seems to be " + currentIP);
		
		JLabel lblPort = new JLabel("Port:");
		JTextField txtPort = new JTextField("8081", 6);
		
		JLabel lblMatcher = new JLabel("Matcher:");
		
		String[] matcherList = MatcherFactory.getMatcherComboList();
		JComboBox cmbMatcherList = new JComboBox(matcherList);
		
		JButton btnPublish = new JButton("Publish!");
		
		
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
						.addGap(12)
						.addComponent(lblUnderHost)
						)
				.addGroup( settingsLayout.createSequentialGroup()
						.addComponent(lblMatcher)
						.addComponent(cmbMatcherList)
						.addComponent(btnPublish)
						)
				);
		
		settingsLayout.setVerticalGroup( settingsLayout.createSequentialGroup()
				.addGroup( settingsLayout.createParallelGroup()
						.addComponent(lblHost)
						.addComponent(txtHost)
						.addComponent(lblPort)
						.addComponent(txtPort)
						)
				.addComponent(lblUnderHost)
				.addGap(10)
				.addGroup( settingsLayout.createParallelGroup()
						.addComponent(lblMatcher)
						.addComponent(cmbMatcherList)
						.addComponent(btnPublish)
						)
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
		
	}
	
}
