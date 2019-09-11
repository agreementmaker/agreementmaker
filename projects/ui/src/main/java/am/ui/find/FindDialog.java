package am.ui.find;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.Core;
import am.app.ontology.Ontology;
import am.ui.UICore;

public class FindDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -8744051447800085387L;

	private JButton cancelButton;
	private JButton findButton;
	
	private JComboBox cmbQuery;
	
	private JCheckBox nameCheckbox;
	private JCheckBox labelCheckbox;
	private JCheckBox sensitiveCheckbox;
	
	private Iterator<Ontology> ontIter = null;
	private boolean continueFromLast = false;
	
	private String searchString = null;
	private Pattern searchPattern = null;
	private Matcher matcher = null;
	
	private static final String PREF_ENTRIES_PREFIX = "PREF_ENTRY_";
	
	FindInterface haystack;  // searching for a needle in the haystack
	
	public FindDialog( FindInterface hs ) {
		super(UICore.getUI().getUIFrame());

		haystack = hs;
		
		setTitle("Find...");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// components
		JLabel findLabel = new JLabel("Search words/Regular expression:");
		cmbQuery = new JComboBox();
		cmbQuery.setEditable(true);
		cmbQuery.addActionListener(this);
		
		// populate the find combobox
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		for( int i = 0; i < 10; i++ ) {
			
		}
	
		JLabel searchThroughLabel = new JLabel("Search through:");
		nameCheckbox = new JCheckBox("Name");
		nameCheckbox.setSelected( UICore.getUI().getCanvas().getShowLocalName() );
		labelCheckbox = new JCheckBox("Label");
		labelCheckbox.setSelected( UICore.getUI().getCanvas().getShowLabel() );
		nameCheckbox.setEnabled(false);
		labelCheckbox.setEnabled(false);
		
		JLabel optionsLabel = new JLabel("Options:");
		sensitiveCheckbox = new JCheckBox("Case sensitive");
		
		findButton = new JButton("Find");
		cancelButton = new JButton("Cancel");
		
		findButton.addActionListener(this);
		cancelButton.addActionListener(this);
		findButton.setSize( cancelButton.getPreferredSize() );
		
		// build the layout of the components
		GroupLayout mainLayout = new GroupLayout(getContentPane());
		getContentPane().setLayout(mainLayout);
		mainLayout.setAutoCreateGaps(true);
		mainLayout.setAutoCreateContainerGaps(true);
		
		mainLayout.setHorizontalGroup( mainLayout.createSequentialGroup()
				.addGroup( mainLayout.createParallelGroup()
					.addComponent(findLabel)
					.addComponent(cmbQuery, GroupLayout.PREFERRED_SIZE, cmbQuery.getPreferredSize().width+200, GroupLayout.PREFERRED_SIZE)
					.addGroup( mainLayout.createSequentialGroup()
						.addGroup( mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(searchThroughLabel, GroupLayout.PREFERRED_SIZE, searchThroughLabel.getPreferredSize().width+50, GroupLayout.PREFERRED_SIZE)
							.addComponent(nameCheckbox)
							.addComponent(labelCheckbox)
						)
						.addGroup( mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(optionsLabel)
							.addComponent(sensitiveCheckbox)
						)
				    )
				)
				.addGroup( mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(findButton, GroupLayout.PREFERRED_SIZE, cancelButton.getPreferredSize().width, GroupLayout.PREFERRED_SIZE)
						.addComponent(cancelButton)
				)
		);
		
		mainLayout.setVerticalGroup( mainLayout.createSequentialGroup()
				.addComponent(findLabel)
				.addGroup( mainLayout.createParallelGroup()
						.addComponent(cmbQuery)
						.addComponent(findButton)
				)
				.addGroup( mainLayout.createParallelGroup()
						.addGroup( mainLayout.createParallelGroup()
								.addGroup( mainLayout.createSequentialGroup()
										.addComponent(searchThroughLabel)
										.addComponent(nameCheckbox)
										.addComponent(labelCheckbox)
								)
								.addGroup( mainLayout.createSequentialGroup()
										.addComponent(optionsLabel)
										.addComponent(sensitiveCheckbox)
								)
						)
						.addComponent(cancelButton)
				)
		);
		
		
		getRootPane().setDefaultButton(findButton);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
	}

	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
	    JRootPane rootPane = new JRootPane();
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
	    Action actionListener = new AbstractAction() {
	      public void actionPerformed(ActionEvent actionEvent) {
	        cancelButton.doClick();
	      }
	    };
	    InputMap inputMap = rootPane
	        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    inputMap.put(stroke, "ESCAPE");
	    rootPane.getActionMap().put("ESCAPE", actionListener);

	    return rootPane;
	  }
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		if( e.getSource() == cancelButton ) {
			haystack.resetSearch();
			setVisible(false);
		} else if( e.getSource() == findButton ) {
			if( continueFromLast ) {
				// we continue searching from the last found thing
				if( findButton.getText() == "Reset" ) {
					haystack.resetSearch();
					findButton.setText("Find");
				}
				findNext();
			} else {
				// create a new search
				Object selectedItem = cmbQuery.getSelectedItem();
				if( selectedItem == null ) {
					// nothing to search for
					return;
				}
				searchString = selectedItem.toString();
				if( sensitiveCheckbox.isSelected() ) {
					searchPattern = Pattern.compile( searchString );
				} else {
					searchPattern = Pattern.compile( searchString, Pattern.CASE_INSENSITIVE );
				}
				continueFromLast = true;
				findNext();
			}
		} else if( e.getSource() == cmbQuery ) {
			// the user is editing the search field
			if( Core.DEBUG ) {
				Logger log = Logger.getLogger(this.getClass());
				log.setLevel(Level.DEBUG);
				log.debug(e);
			}
			if( e.getActionCommand() == "comboBoxEdited" ) {
				Object selectedItem = cmbQuery.getSelectedItem(); 
				if( selectedItem != null && searchString != selectedItem.toString() )
					//continueFromLast = false;	
					haystack.resetSearch();
					findButton.setText("Find");
			}
			
		}
	}
	
	private void findNext() {
		// get the next straw
		Logger log = null;
		if( Core.DEBUG ) {
			log = Logger.getLogger(this.getClass());
			log.setLevel(Level.DEBUG);
		}
		String straw = null;
		while( haystack.hasMoreStraw() ) {
			straw = haystack.getNextStraw();
			if( Core.DEBUG ) log.debug("Checking straw:" + straw);
			if( straw == null ) {
				//we are at the end
				findButton.setText("Reset");
				break;
			}
			
			
			// check this straw if it has the needle
			matcher = searchPattern.matcher(straw);

			while( matcher.find() ) {
				if( matcher.start() == matcher.end() ) {
					// zero length match, ignore
					continue;
				}
				// found a non trivial match.
				haystack.displayCurrentStraw();
				
				if( Core.DEBUG ) {
					log.setLevel(Level.DEBUG);
					log.debug("Found needle!");
				}
				findButton.setText("Next");
				return; // stop here until the user presses find again
			}
		}
		
		findButton.setText("Find");
		haystack.resetSearch();
	}
}
