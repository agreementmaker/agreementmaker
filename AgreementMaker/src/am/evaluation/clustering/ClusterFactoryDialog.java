package am.evaluation.clustering;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;


public class ClusterFactoryDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -6675553141084080729L;

	private ClusteringParametersPanel clusterPanel = null;
	
	private JButton btnCancel;
	private JButton btnOK;
	private JCheckBox[] matcherCheckboxes;
	private List<AbstractMatcher> matchers;
	private boolean userCanceled = false;
	
	public ClusterFactoryDialog( ClusteringMethod method ) {
		super();
		
		matchers = method.getAvailableMatchers();
		
		setLayout(new BorderLayout());
		
		// Build the matchers selection panel
		matcherCheckboxes = new JCheckBox[matchers.size()];
		JPanel selectionPanel = new JPanel();
		
		selectionPanel.setLayout( new GridLayout( matchers.size(), 1) );
		
		for( int i = 0; i < matchers.size(); i++ ) {
			AbstractMatcher currentMatcher = matchers.get(i);
			JCheckBox chkMatcher = new JCheckBox(currentMatcher.getRegistryEntry().getMatcherName());
			chkMatcher.setSelected(true);
			matcherCheckboxes[i] = chkMatcher;
			selectionPanel.add(chkMatcher);
		}
		
		add(selectionPanel, BorderLayout.NORTH);		
		
		
		if( method != null ) {
			clusterPanel = method.getParametersPanel();
			if( clusterPanel != null ) add(clusterPanel, BorderLayout.CENTER);
		}
		
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		
		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new FlowLayout( FlowLayout.TRAILING , 10, 5 ) );
		buttonPanel.add(btnCancel);
		buttonPanel.add(btnOK);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( e.getSource() == btnCancel ) {
			userCanceled = true;
			setVisible(false);
			dispose();
		}
		if( e.getSource() == btnOK ) {
			// check parameters and set them up
			String errorMsg = clusterPanel.checkParameters();
			if( errorMsg == null ) {
				setVisible(false);
				dispose();
			}
			else {
				Utility.displayErrorPane(errorMsg, "Invalid parameters.");
			}
		}
		
	}
	
	public ClusteringParameters getParameters() {
		ClusteringParameters p = clusterPanel.getParameters();
		
		for( int i = 0; i < matcherCheckboxes.length; i++ ) {
			if( matcherCheckboxes[i].isSelected() ) {
				p.addMatcher(matchers.get(i));
			}
		}
		
		return p;
	}
	
	public boolean userCanceled() { return userCanceled; } 
	
}
