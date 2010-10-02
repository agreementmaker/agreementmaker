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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

import com.hp.hpl.jena.ontology.OntResource;

import am.Utility;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class LexiconLookupPanel extends JPanel implements ActionListener, KeyListener {

	
	private JLabel lblTerm;
	private JTextField txtTerm;
	private JButton btnLookup, btnViewAll;
	private JScrollPane sclResult;
	
	private JTextPane txtResult;
	
	//private WordNetDatabase WordNet; // the WordNet Interface
	
	Lexicon lexicon;
	
	
	public LexiconLookupPanel( Lexicon l ) {
		super();
		
		if( l == null ) throw new NullPointerException("Cannot display a null Lexicon.");
		
		lexicon = l;
		
		// create the UI components for the WordNet Lookup Panel.
		
		lblTerm = new JLabel("Term: ");
		txtTerm = new JTextField();
		txtTerm.addKeyListener( this );
		
		btnLookup = new JButton("Lookup");
		btnLookup.addActionListener(this);
		
		btnViewAll = new JButton("View All");
		btnViewAll.addActionListener(this);
		
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
						.addComponent(btnViewAll)
						)
				.addComponent(sclResult)
				);
		
		wnpanelLayout.setVerticalGroup( wnpanelLayout.createSequentialGroup()
				.addGroup( wnpanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblTerm)
						.addComponent(txtTerm, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLookup)
						.addComponent(btnViewAll)
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
			viewAll();
		}

	}

	private void viewAll() {
		
		HTMLDocument resultDocument = new HTMLDocument();
		
		String wholeLexicon = new String();
		
		Map<OntResource,LexiconSynSet> lexMap = lexicon.getSynSetMap();
		for( LexiconSynSet currentSynSet : lexMap.values() ) {

			wholeLexicon += getSynSetDescription(currentSynSet);
			
			
			boolean first = true;
			for( LexiconSynSet related : currentSynSet.getRelatedSynSets() ) {
				if( first ) { wholeLexicon += "<br><b>Related Synsets</b><br>"; first = false; }
				wholeLexicon += getSynSetDescription(related);
			}

			wholeLexicon += "<hr>";
		}
		
	
		// ok, we have created the SyledDocument for the result, update the
		// JTextPane.
		txtResult.setText( wholeLexicon );
		txtResult.setCaretPosition(0); // scroll to the top
		
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
	
	
	private void doLookup() {
		// Let's do a wordnet lookup!

		String searchTerm = txtTerm.getText();

		String newResult = new String();
		HTMLDocument resultDocument = new HTMLDocument();

		newResult += "<h1>Search Term: " + txtTerm.getText() + "</h1><br>";
		
		newResult += "<h2><font color=\"blue\">Main Synset</font></h2>";
		
		// lookup and print out.
		LexiconSynSet synSet = lexicon.getSynSet(searchTerm);

		newResult += getSynSetDescription(synSet);
		
		newResult += "<h2><font color=\"blue\">Related Synsets</font></h2>";

		for( LexiconSynSet related : synSet.getRelatedSynSets() ) {
			newResult += getSynSetDescription(related);
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
		
		newResult += "<b>Synset " + "</b> (" + synSet.getID() + ") (" + synSet.getType().getLexiconName() + ").<br>";
		newResult += "<b>Gloss: " + "</b>"+ synSet.getGloss() + ".<br>";
		
		if( synSet.getOntologyConcept() != null ) {
			newResult += "<b>OntResource: " + "</b> " + synSet.getOntologyConcept().getLocalName() + ".<br>";
		}

		for( String syn : synSet.getSynonyms() ) {
			newResult += syn + ", ";
		}
		newResult += ".<br><br>";
		
		return newResult;
	}
}
