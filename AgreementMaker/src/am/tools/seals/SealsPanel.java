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
import java.beans.PropertyChangeEvent;
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
import javax.xml.ws.Endpoint;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.userInterface.MatcherParametersDialog;
import am.userInterface.MatchingProgressDisplay;
import am.utility.LinuxInetAddress;
/**
 * 
 * @author cosmin
 *
 */
public class SealsPanel extends JPanel implements MatchingProgressDisplay, ActionListener {

	private static final long serialVersionUID = 3284754599688612733L;

	
	private JButton btnPublish, btnDefaultName;
	private JTextField txtHost, txtPort, txtEndpoint;
	private JTextArea txtReport;
	private JComboBox cmbMatcherList, cmbThreshold, cmbSource, cmbTarget;
	private JProgressBar barProgress;
	
	private AbstractMatcher matcherToPublish;
	private Endpoint endpoint;
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
		
		
		
		String[] thresholds = Utility.getPercentStringList();
		String[] numRelations = Utility.getNumRelList();
		
		JLabel lblThreshold = new JLabel("Threshold:");
		cmbThreshold = new JComboBox(thresholds);
		cmbThreshold.setSelectedItem("60%");
		
		JLabel lblCardinality = new JLabel("Cardinality:");
		cmbSource = new JComboBox(numRelations);
		
		JLabel lblTo = new JLabel("to");
		cmbTarget = new JComboBox(numRelations);
		
		
		btnPublish = new JButton("Publish!");
		btnPublish.setToolTipText("Begin publishing.");
		btnPublish.addActionListener(this);
		btnPublish.setActionCommand("publish");
		
		
		JLabel lblEndpoint = new JLabel("Name:");
		txtEndpoint = new JTextField("matcherWS");
		txtEndpoint.setToolTipText("Endpoint name.");
		
		btnDefaultName = new JButton("Default Name");
		btnDefaultName.addActionListener(this);
		btnDefaultName.setToolTipText("Use the default matcher name.");
		
		int buttonWidth = (btnDefaultName.getPreferredSize().width > btnPublish.getPreferredSize().width) ? btnDefaultName.getPreferredSize().width : btnPublish.getPreferredSize().width;  
		
		
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
						.addComponent(lblHost, GroupLayout.PREFERRED_SIZE, lblThreshold.getPreferredSize().width, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtHost)
						.addComponent(lblPort)
						.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, txtPort.getPreferredSize().width, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup( settingsLayout.createSequentialGroup()
						.addComponent(lblMatcher, GroupLayout.PREFERRED_SIZE, lblThreshold.getPreferredSize().width, GroupLayout.PREFERRED_SIZE)
						.addComponent(cmbMatcherList)
						)
				.addGroup( settingsLayout.createSequentialGroup()
						.addComponent(lblThreshold)
						.addComponent(cmbThreshold)
						.addComponent(lblCardinality)
						.addComponent(cmbSource)
						.addComponent(lblTo)
						.addComponent(cmbTarget)
						)
				.addGroup( settingsLayout.createSequentialGroup()
						.addComponent(lblEndpoint, GroupLayout.PREFERRED_SIZE, lblThreshold.getPreferredSize().width, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtEndpoint)
						.addGroup( settingsLayout.createParallelGroup()
								.addComponent(btnPublish, GroupLayout.PREFERRED_SIZE, buttonWidth, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnDefaultName, GroupLayout.PREFERRED_SIZE, buttonWidth, GroupLayout.PREFERRED_SIZE)
								)
						)
				.addComponent(btnPublish, GroupLayout.PREFERRED_SIZE, buttonWidth, GroupLayout.PREFERRED_SIZE)
				);
		
		settingsLayout.setVerticalGroup( settingsLayout.createSequentialGroup()
				.addGroup( settingsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblHost)
						.addComponent(txtHost)
						.addComponent(lblPort)
						.addComponent(txtPort)
						)
				.addGroup( settingsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMatcher)
						.addComponent(cmbMatcherList)
						)
				.addGroup( settingsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblThreshold)
						.addComponent(cmbThreshold)
						.addComponent(lblCardinality)
						.addComponent(cmbSource)
						.addComponent(lblTo)
						.addComponent(cmbTarget)
						)
				.addGroup( settingsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblEndpoint)
						.addComponent(txtEndpoint)
						.addComponent(btnDefaultName)
						)
				.addGap(20)
				.addComponent(btnPublish)
				);
		
		pnlSettings.setLayout(settingsLayout);
		
		
		
		// create all the elements for the matcher progress panel
		
		barProgress = new JProgressBar(0, 100);
		barProgress.setValue(0);
		barProgress.setStringPainted(true);
		barProgress.setEnabled(false);
		barProgress.setVisible(false);
		
		txtReport = new JTextArea(8, 35);
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
		} else if( actionEvent.getSource() == btnPublish ) {
			
			if( btnPublish.getActionCommand().equals("publish") ) {
				// We are going to publish a new matcher.
				
				
				// 1. Get the matcher instance.
				MatchersRegistry mR = MatcherFactory.getMatchersRegistryEntry( cmbMatcherList.getSelectedItem().toString() );
				matcherToPublish = MatcherFactory.getMatcherInstance(mR, Core.getInstance().getMatcherInstances().size());
				
				// 2. Make sure we are using a Layer I matcher.
				if( matcherToPublish.getMinInputMatchers() > 0 ) {
					Utility.displayErrorPane("Matcher must be a \"Layer I\" matcher.  A \"Layer I\" matcher does not require any input matchers.", "Cannot use this matcher.");
					//throw new Exception("Matcher must be a \"Layer I\" matcher.");
					return;
				}
				
				// 3. If the matcher requires parameters, bring up the corresponding parameters panel and have the user set the parameters
				if( matcherToPublish.needsParam() ) {
					// bring up the matcherparametersdialog to have the users set the parameters for the algorithm
					MatcherParametersDialog paramDialog = new MatcherParametersDialog( matcherToPublish );
					matcherToPublish.setParam( paramDialog.getParameters() );
				}

			/*  // Ignore this. It was used to test if the parameters were trully set.
				AbstractParameters param = matcherToPublish.getParam();
				if( param instanceof BaseSimilarityParameters ) {
					BaseSimilarityParameters bsmParam = (BaseSimilarityParameters)param;
					txtReport.append("Use dictionary? " + bsmParam.useDictionary + "\n");
				}
			*/
				
				// 4. We want to see the display of the matcher in the SEALS panel.  Register this panel as a MatcherProgressDisplay.
				//    Also, set the threshold and the cardinality.
				matcherToPublish.setProgressDisplay( this );
				matcherToPublish.setThreshold(Utility.getDoubleFromPercent((String)cmbThreshold.getSelectedItem()));
				matcherToPublish.setMaxSourceAlign(Utility.getIntFromNumRelString((String)cmbSource.getSelectedItem()));
				matcherToPublish.setMaxTargetAlign(Utility.getIntFromNumRelString((String)cmbTarget.getSelectedItem()));
				
				
				
				// 5. Publish this matcher.
				
				SealsServer sealsServer = new SealsServer(mR, this, Utility.getDoubleFromPercent((String)cmbThreshold.getSelectedItem()), 
										Utility.getIntFromNumRelString((String)cmbSource.getSelectedItem()), 
										Utility.getIntFromNumRelString((String)cmbTarget.getSelectedItem()), matcherToPublish.getParam());
				
				endpoint = Endpoint.create(sealsServer);
				String endpointDescription = "http://" + txtHost.getText().trim() + ":" + txtPort.getText().trim() + "/" + txtEndpoint.getText().trim(); 
				endpoint.publish(endpointDescription);
				
				appendToReport("Matcher service name: " + SealsServer.class.getName() + "\n");
				appendToReport("Matcher published to " + endpointDescription + "\n");
				
				
				// 6. Update the Publish button to a Stop button.
				txtHost.setEnabled(false);
				txtPort.setEditable(false);
				cmbMatcherList.setEnabled(false);
				cmbThreshold.setEnabled(false);
				cmbSource.setEnabled(false);
				cmbTarget.setEnabled(false);
				txtEndpoint.setEnabled(false);
				btnDefaultName.setEnabled(false);
				
				btnPublish.setText("Stop!");
				btnPublish.setActionCommand("stop");
				
			} else if( btnPublish.getActionCommand().equals("stop") ) {
				
				// the user wants to stop the publishing of the service.
				
				// 1. Stop the endpoint.
				endpoint.stop();
				
				// 2. Stop the matcher.
				matcherToPublish.cancel(true);
				appendToReport("Publishing stopped. Matcher stopped.\n");
				
				// 3. Dereference anything we don't need anymore.
				matcherToPublish = null;
				endpoint = null;
				
				// 4. Update the Stop button to a Publish button.
				txtHost.setEnabled(true);
				txtPort.setEditable(true);
				cmbMatcherList.setEnabled(true);
				cmbThreshold.setEnabled(true);
				cmbSource.setEnabled(true);
				cmbTarget.setEnabled(true);
				txtEndpoint.setEnabled(true);
				btnDefaultName.setEnabled(true);
				
				btnPublish.setText("Publish!");
				btnPublish.setActionCommand("publish");
			}
			
			
		}
		
	}

	
	/********************************* Matcher Progress Display Methods *************************************/
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			if( barProgress.isEnabled() == false ) { barProgress.setEnabled(true); }
			
            int progress = (Integer) evt.getNewValue();
            barProgress.setValue(progress);
        }		
	}

	@Override
	public void appendToReport(String report) { txtReport.append(report); }

	@Override
	public void matchingStarted() { 
		barProgress.setEnabled(true); 
		barProgress.setValue(0); 
		txtReport.append("Matcher has started to run.\n"); 
	}
	
	@Override
	public void matchingComplete() { 
		barProgress.setEnabled(false); 
		txtReport.append( matcherToPublish.getReport() ); 
	}

	
}
