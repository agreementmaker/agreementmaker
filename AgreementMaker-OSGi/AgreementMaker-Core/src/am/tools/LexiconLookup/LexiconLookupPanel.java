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
 * @date Aug 18, 2010.  @author Cosmin.
 * @description Initial Wordnet Lookup Panel implementation.          
 * 
 *  
 */

package am.tools.LexiconLookup;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.commons.lang.StringEscapeUtils;

import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;

import com.hp.hpl.jena.ontology.OntResource;

public class LexiconLookupPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = -6497486517225004234L;
	
	private JLabel lblTerm;
	private JTextField txtTerm;
	private JButton btnLookup, btnViewAll, btnReset;
	private JScrollPane sclResult;
	
	private JTextPane txtResult;
	
	//private WordNetDatabase WordNet; // the WordNet Interface
	
	Lexicon lexicon;
	
	int currentIndex = 0;
	int pageSize = 50;
	
	public LexiconLookupPanel( Lexicon l ) {
		super();
		
		if( l == null ) throw new NullPointerException("Cannot display a null Lexicon.");
		
		lexicon = l;
		lexicon.setLookupPanel(this);
		
		// create the UI components for the WordNet Lookup Panel.
		
		lblTerm = new JLabel("Term: ");
		txtTerm = new JTextField();
		txtTerm.addKeyListener( this );
		
		btnLookup = new JButton("Lookup");
		btnLookup.addActionListener(this);
		
		int lastIndex = (currentIndex+pageSize) < lexicon.size() ? (currentIndex+pageSize) : lexicon.size();
		btnViewAll = new JButton("View " + currentIndex + " to " + lastIndex + " of " + lexicon.size() );
		btnViewAll.addActionListener(this);
		
		btnReset = new JButton("Reset" );
		btnReset.addActionListener(this);
		
		txtResult = new JTextPane();
		txtResult.setContentType("text/html");
		
		Font font = new Font("Courier New", Font.PLAIN, 12);
		if( font != null ) txtResult.setFont(font);
		
		sclResult = new JScrollPane();
		sclResult.setWheelScrollingEnabled(true);
		sclResult.getVerticalScrollBar().setUnitIncrement(20);
		sclResult.setViewportView(txtResult);
		
		// set a 5 pixel all around border
		setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5) ); 

		// create the placement of the components
		GroupLayout wnpanelLayout = new GroupLayout(this);
		wnpanelLayout.setAutoCreateGaps(true);
		wnpanelLayout.setAutoCreateContainerGaps(true);
		
		
		wnpanelLayout.setHorizontalGroup( wnpanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup( wnpanelLayout.createSequentialGroup()
						.addComponent(lblTerm)
						.addComponent(txtTerm)
						.addComponent(btnLookup)
						.addGap(30)
						.addComponent(btnViewAll)
						.addComponent(btnReset)
						)
				.addComponent(sclResult)
				);
		
		wnpanelLayout.setVerticalGroup( wnpanelLayout.createSequentialGroup()
				.addGroup( wnpanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblTerm)
						.addComponent(txtTerm, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLookup)
						.addComponent(btnViewAll)
						.addComponent(btnReset)
						)
				.addComponent(sclResult)
				);
		
		setLayout(wnpanelLayout);

	}

	/**
	 * Handle the lookup button clicks.
	 */
	@Override
	public void actionPerformed(ActionEvent currentEvent) {
			
		if (currentEvent.getSource() == btnLookup) {
			// The Lookup button was clicked. 
			doLookup();
		} else if ( currentEvent.getSource() == btnViewAll ) {
			viewRange();
		} else if( currentEvent.getSource() == btnReset ) { reset(); }

	}

	private void reset() {
		currentIndex = 0;
		txtResult.setText( "" );
		txtResult.setCaretPosition(0); // scroll to the top
		int lastIndex = (currentIndex+pageSize) < lexicon.size() ? (currentIndex+pageSize) : lexicon.size();
		btnViewAll.setText("View " + currentIndex + " to " + lastIndex + " of " + lexicon.size());
		btnViewAll.setEnabled(true);
	}
	
	private void viewRange() {
		
		//HTMLDocument resultDocument = new HTMLDocument();
		
		String wholeLexicon = new String();
		
		Collection<LexiconSynSet> lexMapCollection = lexicon.getSynSetMap().values();
		Object[] lexMapArray = lexMapCollection.toArray();
		for( int i = currentIndex; i < lexMapArray.length && i - currentIndex <= pageSize; i++ ) {
			LexiconSynSet currentSynSet = (LexiconSynSet) lexMapArray[i];
			wholeLexicon += getSynSetDescription(currentSynSet);
			
			
			boolean first = true;
			for( LexiconSynSet related : currentSynSet.getRelatedSynSets() ) {
				if( first ) { wholeLexicon += "<br><b>Related Synsets</b><br>"; first = false; }
				wholeLexicon += getSynSetDescription(related);
			}

			wholeLexicon += "<br><hr><br>";
		}
	
		// ok, we have created the SyledDocument for the result, update the
		// JTextPane.
		txtResult.setText( wholeLexicon );
		txtResult.setCaretPosition(0); // scroll to the top
		
		currentIndex += pageSize;
		if( currentIndex < lexicon.size() ) {
			int lastIndex = (currentIndex+pageSize) < lexicon.size() ? (currentIndex+pageSize) : lexicon.size();
			btnViewAll.setText("View " + currentIndex + " to " + lastIndex + " of " + lexicon.size());
		} else {
			btnViewAll.setEnabled(false);
		}
		
		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override public void keyPressed(KeyEvent e) { }
	@Override public void keyReleased(KeyEvent e) {
		if( e.getSource() == txtTerm && e.getKeyCode() == KeyEvent.VK_ENTER ) {
			doLookup();
		}
	}
	
	public void doLookup(OntResource r) {
		String newResult = new String();
		
		if( r == null ) {
			newResult += "<h2><font color=\"red\">Resource is null.</font></h2>";
			newResult += "<p>No resource was selected to be viewed.</p>";
		} else {
			// do the lookup
			LexiconSynSet synSet = lexicon.getSynSet(r);
			
			if( synSet == null ) {
				// term was not found.
				newResult += "<h2><font color=\"red\">Resource not found.</font></h2>";
				newResult += "<p>This resource does not have a lexicon entry.</p><p>" + r.getLocalName() + "</p>";
	
			} else {
	
				newResult += "<h2><font color=\"blue\">Main Synset</font></h2>";
				newResult += getSynSetDescription(synSet);
				newResult += "<h2><font color=\"blue\">Related Synsets</font></h2>";
	
				for( LexiconSynSet related : synSet.getRelatedSynSets() ) {
					newResult += getSynSetDescription(related);
				}
				newResult += "<br><hr><br><br>";
			}
		}

		// ok, we have created the SyledDocument for the result, update the
		// JTextPane.
		txtResult.setText( newResult );
		txtResult.setCaretPosition(0); // scroll to the top
		
		txtTerm.select(0, txtTerm.getText().length() );
	}
	
	private void doLookup() {
		// Let's do a lexicon lookup!

		String searchTerm = txtTerm.getText();

		String newResult = new String();
		//HTMLDocument resultDocument = new HTMLDocument();

		newResult += "<h1>Search Term: " + txtTerm.getText() + "</h1><br>";
		
		
		// lookup and print out.
		List<LexiconSynSet> synSet = lexicon.lookup(searchTerm);

		if( synSet.size() == 0 ) {
			// term was not found.
			newResult += "<h2><font color=\"red\">Term not found.</font></h2>";
			newResult += "<p>The search string is treated as a regular expression over the set of all word forms.  Definitions are not searched at this time.</p>";
			
		} else {
			
			for( LexiconSynSet sset : synSet ) {
				newResult += "<h2><font color=\"blue\">Main Synset</font></h2>";
				newResult += getSynSetDescription(sset);
				newResult += "<h2><font color=\"blue\">Related Synsets</font></h2>";
		
				for( LexiconSynSet related : sset.getRelatedSynSets() ) {
					newResult += getSynSetDescription(related);
				}
				newResult += "<br><hr><br><br>";
			}
		}

		// ok, we have created the SyledDocument for the result, update the
		// JTextPane.
		txtResult.setText( newResult );
		txtResult.setCaretPosition(0); // scroll to the top
		
		txtTerm.select(0, txtTerm.getText().length() );
		
	}
	

	private String getSynSetDescription( LexiconSynSet synSet ) {
		
		String newResult = new String();
		
		if( synSet == null ) return newResult;

		newResult += "<b>OntResource: " + "</b>" + StringEscapeUtils.escapeHtml( synSet.getOntologyConcept().getLocalName() ) + ".<br>";
		newResult += "<b>SynSet Type: " + "</b>" + StringEscapeUtils.escapeHtml( synSet.getType().getLexiconName() ) + ".<br>";
		
		//newResult += "<b>Synset " + "</b> (" + synSet.getID() + ") (" + synSet.getType().getLexiconName() + ").<br>";
		newResult += "<b>Set of synonyms: " + "</b>";
		for( String syn : synSet.getSynonyms() ) {
			newResult += StringEscapeUtils.escapeHtml(syn) + ", ";
		}
		newResult += ".<br>";
		
		if( synSet.getGloss() != null ) {
			newResult += "<b>Gloss: " + "</b>"+ StringEscapeUtils.escapeHtml( synSet.getGloss() ) + ".<br>";
		} else {
			newResult += "<b>Gloss: " + "</b>null.<br>";
		}
		
		return newResult;
	}
}
