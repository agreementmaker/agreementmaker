package am.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import am.app.Core;
import am.app.mappingEngine.MatchingTask;


/**
 * This class implements functions to allow developers a quick and easy
 * dialog for selecting matchers.
 * 
 * @author cosmin
 *
 */

public class MatchersChooser {

	/**
	 * This function presents a dialog to the user with a checkbox for each
	 * active matcher in the system.  The user then selects the checkboxes
	 * and a list of the matchers corresponding to the checkboxes is returned.
	 * @return Null if the dialog is canceled.  Empty list if nothing is selected.
	 */
	public static List<MatchingTask> getManyMatchers(String message) {
		
		MatchersChooserCheckboxPanel checkBoxPanel = new MatchersChooserCheckboxPanel();
		MatchersChooserDialog dialog = new MatchersChooserDialog(checkBoxPanel);
		dialog.setMessage(message);
		dialog.setVisible(true);
		
		return dialog.getMatchers();
	}
	
	/**
	 * This function presents a radio button dialog of all the matchers
	 * in the system so the user can choose a single one.
	 * @return Null if the dialog is canceled, or if nothing is selected.
	 */
	public static MatchingTask getOneMatcher(String message) {
		MatchersChooserRadioPanel radioPanel = new MatchersChooserRadioPanel();
		MatchersChooserDialog dialog = new MatchersChooserDialog(radioPanel);
		dialog.setMessage(message);
		dialog.setVisible(true);
		
		List<MatchingTask> l =  dialog.getMatchers();
		if( l == null || l.isEmpty() ) return null;
		return l.get(0);
	}
	
	/**
	 * Inner class that represents the panel that holds the checkboxes.
	 * @author cosmin
	 *
	 */
	public static class MatchersChooserRadioPanel extends JPanel {

		private static final long serialVersionUID = -4199567169803000678L;
		
		List<MatchingTask> matchers;
		private JRadioButton[] matcherRadios;
		
		
		public MatchersChooserRadioPanel() {
			super();
			
			
			matchers = Core.getInstance().getMatchingTasks();
			
			setLayout(new BorderLayout());
			
			// Build the matchers selection panel
			matcherRadios = new JRadioButton[matchers.size()];
			JPanel selectionPanel = new JPanel();
			
			selectionPanel.setLayout( new GridLayout( matchers.size(), 1) );
			
			ButtonGroup grpMatchers = new ButtonGroup(); 
			
			for( int i = 0; i < matchers.size(); i++ ) {
				MatchingTask currentMatcher = matchers.get(i);
				JRadioButton radMatcher = new JRadioButton(currentMatcher.getShortLabel());
				radMatcher.setSelected(true);
				matcherRadios[i] = radMatcher;
				grpMatchers.add(radMatcher);
				selectionPanel.add(radMatcher);
			}
			
			add(selectionPanel, BorderLayout.CENTER);		
						
		}
		
		/**
		 * Return a list of matchers corresponding to the selected checkboxes.
		 * @return Null if no matcher was selected.
		 */
		public List<MatchingTask> getMatchers() {
			if( matcherRadios == null ) { return null; }
			
			List<MatchingTask> matcherSelected = new ArrayList<>();
			for( int i = 0; i < matcherRadios.length; i++ ) {
				if( matcherRadios[i] != null && matcherRadios[i].isSelected() ) {
					matcherSelected.add(matchers.get(i));
					break;
				}
			}
			
			return matcherSelected;
		}
	}
	
	
	
	/**
	 * Inner class that represents the panel that holds the checkboxes.
	 * This panel allows the user to select more than one matcher.
	 * @author cosmin
	 *
	 */
	public static class MatchersChooserCheckboxPanel extends JPanel {

		private static final long serialVersionUID = -4199567169803000678L;
		
		List<MatchingTask> matchers;
		private JCheckBox[] matcherCheckboxes;
		
		
		public MatchersChooserCheckboxPanel() {
			super();
			
			
			matchers = Core.getInstance().getMatchingTasks();
			
			setLayout(new BorderLayout());
			
			// Build the matchers selection panel
			matcherCheckboxes = new JCheckBox[matchers.size()];
			JPanel selectionPanel = new JPanel();
			
			selectionPanel.setLayout( new GridLayout( matchers.size(), 1) );
			
			for( int i = 0; i < matchers.size(); i++ ) {
				MatchingTask currentMatcher = matchers.get(i);
				JCheckBox chkMatcher = new JCheckBox(currentMatcher.getShortLabel());
				chkMatcher.setSelected(true);
				matcherCheckboxes[i] = chkMatcher;
				selectionPanel.add(chkMatcher);
			}
			
			add(selectionPanel, BorderLayout.CENTER);		
						
		}
		
		/**
		 * Return a list of matchers corresponding to the selected checkboxes.
		 * @return
		 */
		public List<MatchingTask> getMatchers() {
			if( matcherCheckboxes == null ) { return null; }
			
			List<MatchingTask> matchersSelected = new ArrayList<>();
			for( int i = 0; i < matcherCheckboxes.length; i++ ) {
				if( matcherCheckboxes[i] != null && matcherCheckboxes[i].isSelected() ) matchersSelected.add( matchers.get(i) );
			}
			
			return matchersSelected;
		}
	}

	/**
	 * A dialog that holds the checkboxes for choosing matchers 
	 * and has ok/cancel buttons.
	 * 
	 * @author cosmin
	 */
	public static class MatchersChooserDialog extends JDialog implements ActionListener {
		
		private static final long serialVersionUID = 1049597688835219951L;
		
		private boolean canceled = false;
		private JPanel checkboxPanel;

		public MatchersChooserDialog() {
			super(UICore.getUI().getUIFrame(), true);
			init(new MatchersChooserCheckboxPanel());
		}
		
		public MatchersChooserDialog(JPanel chooserPanel) {
			super(UICore.getUI().getUIFrame(), true);
			init(chooserPanel);
		}

		private void init(JPanel chooserPanel) {
			checkboxPanel = chooserPanel;
			
			getContentPane().setLayout(new BorderLayout());
			
			getContentPane().add(checkboxPanel, BorderLayout.CENTER);
			
			final JButton btnCancel = new JButton("Cancel");
			btnCancel.setActionCommand("cancel");
			btnCancel.addActionListener(this);
			
			JButton btnOK = new JButton("OK");
			btnOK.setActionCommand("ok");
			btnOK.addActionListener(this);
			
			JPanel buttonPanel = new JPanel();
			
			buttonPanel.setLayout(new FlowLayout( FlowLayout.TRAILING , 10, 5 ) );
			buttonPanel.add(btnCancel);
			buttonPanel.add(btnOK);
			
			add(buttonPanel, BorderLayout.SOUTH);
			getRootPane().setDefaultButton(btnOK);

			// make escape key work
			JRootPane rootPane = getRootPane();
		    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		    Action actionListener = new AbstractAction() {
		      public void actionPerformed(ActionEvent actionEvent) {
		        btnCancel.doClick();
		      }
		    };
		    InputMap inputMap = rootPane
		        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		    inputMap.put(stroke, "ESCAPE");
		    rootPane.getActionMap().put("ESCAPE", actionListener);
			
		    
			pack();
			setLocationRelativeTo(getOwner());
		}
				
		/**
		 * This function can only be called once.  You may not change the message once it's set.
		 * @param message
		 */
		public void setMessage(String message) {
			JLabel lblMessage = new JLabel(message);
			getContentPane().add(lblMessage, BorderLayout.NORTH);
			pack();
			setLocationRelativeTo(getOwner());
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if( e.getActionCommand() == "cancel" ) { canceled = true; }
			setVisible(false);
		}
		
		public List<MatchingTask> getMatchers() {
			if( canceled ) return null;
			else {
				if( checkboxPanel instanceof MatchersChooserCheckboxPanel ) return ((MatchersChooserCheckboxPanel)checkboxPanel).getMatchers();
				if( checkboxPanel instanceof MatchersChooserRadioPanel ) return ((MatchersChooserRadioPanel)checkboxPanel).getMatchers();
			}
			return null;
		}
	}
	
	
}