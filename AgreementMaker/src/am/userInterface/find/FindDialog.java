package am.userInterface.find;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.Core;
import am.app.ontology.Ontology;

public class FindDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -8744051447800085387L;

	private JButton cancelButton;
	private JButton findButton;
	
	private JComboBox findBox;
	
	private JCheckBox nameCheckbox;
	private JCheckBox labelCheckbox;
	private JCheckBox sensitiveCheckbox;
	
	private Iterator<Ontology> ontIter = null;
	private boolean continueFromLast = false;
	
	private String searchString = null;
	private Pattern searchPattern = null;
	private Matcher matcher = null;
	
	FindInterface haystack;  // searching for a needle in the haystack
	
	public FindDialog( FindInterface hs ) {
		super();

		haystack = hs;
		
		setTitle("Find...");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// components
		JLabel findLabel = new JLabel("Search words/Regular expression:");
		findBox = new JComboBox();
		findBox.setEditable(true);
		findBox.addActionListener(this);
	
		JLabel searchThroughLabel = new JLabel("Search through:");
		nameCheckbox = new JCheckBox("Name");
		nameCheckbox.setSelected( Core.getUI().getCanvas().getShowLocalName() );
		labelCheckbox = new JCheckBox("Label");
		labelCheckbox.setSelected( Core.getUI().getCanvas().getShowLabel() );
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
					.addComponent(findBox, GroupLayout.PREFERRED_SIZE, findBox.getPreferredSize().width+200, GroupLayout.PREFERRED_SIZE)
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
						.addComponent(findBox)
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
		
		
		
		
/*  unfinished spring layout		
		
		
		// create a spring layout for the find dialog
		Container contentPane = getContentPane();
		SpringLayout mainLayout = new SpringLayout();
		contentPane.setLayout(mainLayout);
		
		JPanel input = new JPanel();
		input.setLayout( new BoxLayout(input, BoxLayout.Y_AXIS));
		
		JLabel labelRegularExpression = new JLabel("Search words/Regular Expression:");
		labelRegularExpression.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelRegularExpression.setHorizontalTextPosition(JLabel.LEFT);
		labelRegularExpression.setVerticalTextPosition(JLabel.BOTTOM);
		
		JComboBox cmbFind = new JComboBox(); // TODO: Add previous search entries
		cmbFind.setEditable(true);
		cmbFind.setAlignmentX(LEFT_ALIGNMENT);
		labelRegularExpression.setLabelFor(cmbFind);
		
		// TODO: cmbFind.addActionListener(this);
		

		
		JButton findButton = new JButton("Find Next");
		findButton.addActionListener(this);
		
		
		input.add( labelRegularExpression );
		input.add( cmbFind );
		
		
		
		
		// compile all the sections together
		contentPane.add(input);
		//sections.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.red), sections.getBorder()));

		mainLayout.putConstraint(SpringLayout.WEST, input, 5, SpringLayout.WEST, contentPane);
		
		contentPane.add(findButton);
		
		mainLayout.putConstraint(SpringLayout.NORTH, findButton, 5, SpringLayout.SOUTH, input);
		
		mainLayout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, input);
		mainLayout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, findButton);
		
*/
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
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
				}
				
				findNext();
			} else {
				// create a new search
				Object selectedItem = findBox.getSelectedItem();
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
		} else if( e.getSource() == findBox ) {
			// the user is editing the search field
			if( Core.DEBUG ) {
				Logger log = Logger.getLogger(this.getClass());
				log.setLevel(Level.DEBUG);
				log.debug(e);
			}
			continueFromLast = false;
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
	}
}
