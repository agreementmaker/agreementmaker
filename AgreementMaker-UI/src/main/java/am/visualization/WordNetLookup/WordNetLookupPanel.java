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

package am.visualization.WordNetLookup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;

import am.Utility;
import am.app.mappingEngine.StringUtil.PorterStemmer;
import am.ui.UIUtility;
import am.ui.api.AMTab;
import am.ui.api.impl.AMTabSupportPanel;
import am.visualization.graphviz.GraphViz;
import am.visualization.graphviz.wordnet.NavigableImagePanel;
import am.visualization.graphviz.wordnet.WordnetVisualizer;
import edu.smu.tspell.wordnet.api.NounSynset;
import edu.smu.tspell.wordnet.api.Synset;
import edu.smu.tspell.wordnet.api.SynsetType;
import edu.smu.tspell.wordnet.api.WordNetDatabase;

public class WordNetLookupPanel extends AMTabSupportPanel implements ActionListener, KeyListener {

	
	private JLabel lblTerm;
	private JTextField txtTerm;
	private JButton btnLookup;
	private JButton btnGraphViz;
	private JScrollPane sclResult;
	
	private JTextPane txtResult;
	
	private WordNetDatabase WordNet; // the WordNet Interface
	
	public WordNetLookupPanel() {
		super("WordNet", "Query the WordNet dictionary.");
		
		// create the UI components for the WordNet Lookup Panel.
		
		lblTerm = new JLabel("Term: ");
		txtTerm = new JTextField();
		txtTerm.addKeyListener( this );
		
		btnLookup = new JButton("Lookup");
		btnLookup.addActionListener(this);
		
		btnGraphViz = new JButton("Graph");
		btnGraphViz.addActionListener(this);
		
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
						.addComponent(btnGraphViz)
						)
				.addComponent(sclResult)
				);
		
		wnpanelLayout.setVerticalGroup( wnpanelLayout.createSequentialGroup()
				.addGroup( wnpanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblTerm)
						.addComponent(txtTerm, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLookup)
						.addComponent(btnGraphViz)
						)
				.addComponent(sclResult)
				);
		
		setLayout(wnpanelLayout);
		
		
		// Initialize the WordNet Interface (JAWS)
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		
		
		// Instantiate wordnet.
		try {
			WordNet = WordNetDatabase.getFileInstance();
		} catch( Exception e ) {
			UIUtility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}

	}

	/**
	 * Handle the lookup button clicks.
	 */
	@Override
	public void actionPerformed(ActionEvent currentEvent) {
			
		if (currentEvent.getSource() == btnLookup) {
			// The Lookup button was clicked. 
			doLookup();
		}
		
		if (currentEvent.getSource() == btnGraphViz) {
			String searchTerm = txtTerm.getText();
			
			WordnetVisualizer viz = new WordnetVisualizer();
			Synset[] synsets = viz.getSynsets(searchTerm);
			
			GraphViz gv = new GraphViz();
		    
			String source = viz.synsetsToGraph(synsets);
			byte[] graph = gv.getGraph( source, "gif" );
			
			File out = new File("out.gif");
		    System.out.println("Writing graph to file...");
		    
		    gv.writeGraphToFile( graph , out );
			
			
			
			final String cwd = System.getProperty("user.dir");
						
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {		
					final JFrame frame = new JFrame("Navigable Image Panel");
					NavigableImagePanel panel = new NavigableImagePanel();
					try {
						final BufferedImage image = ImageIO.read(new File(cwd + "/out.gif"));
						panel.setImage(image);								
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "", 
							JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					}
					
					frame.getContentPane().add(panel, BorderLayout.CENTER);
					
					GraphicsEnvironment ge = 
						GraphicsEnvironment.getLocalGraphicsEnvironment();
					Rectangle bounds = ge.getMaximumWindowBounds();
					frame.setSize(new Dimension(bounds.width, bounds.height));
					frame.setVisible(true);				
				}
			});
			
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
	
	
	private void doLookup() {
		// Let's do a wordnet lookup!

		String searchTerm = txtTerm.getText();

		String newResult = new String();
		HTMLDocument resultDocument = new HTMLDocument();

		newResult += "<h1>Search Term: " + txtTerm.getText() + "</h1><br>";

		PorterStemmer ps = new PorterStemmer();
		
		String tokenized_searchTerm[] = searchTerm.split("\\s"); // token array of source LocalName (sLN)
		
		newResult += "<h1>Stemming: " + txtTerm.getText() + "</h1><br>";
		int mn = 1;
		for( String token : tokenized_searchTerm ) {
			newResult += mn + ": " + ps.stem(token) + "<br>";
		}
		newResult += "<hr>";
		
		
		// lookup and print out.
		for (SynsetType t : SynsetType.ALL_TYPES) {
			Synset[] synsets = WordNet.getSynsets(searchTerm, t);
			if( synsets.length > 0 ) {
				if (t == SynsetType.NOUN) {
					newResult += "<h2><font color=\"red\">Noun</font></h2><br>";
				} else if (t == SynsetType.VERB) {
					newResult += "<h2><font color=\"yellow\">Verb</font></h2><br>";
				} else if (t == SynsetType.ADJECTIVE) {
					newResult += "<h2><font color=\"blue\">Adjective</font></h2><br>";
				} else if (t == SynsetType.ADJECTIVE_SATELLITE) {
					newResult += "<h2><font color=\"blue\">Adjective Satelite</font></h2><br>";
				} else if (t == SynsetType.ADVERB) {
					newResult += "<h2><font color=\"green\">Adverb</font></h2><br>";
				}
			}
			
			for (int i = 0; i < synsets.length; i++) {
				String[] words = synsets[i].getWordForms();

				newResult += "<hr><b>Synset " + i + "</b> (" + synsets[i].hashCode() + "): " + synsets[i].getDefinition() + ".<br>";

				for (int j = 0; j < words.length; j++) {
					if (j > 0 && j < words.length) newResult += ", ";
					newResult += words[j];
				}
				newResult += ".<br><br>";

				// nouns have hyponyms, hypernyms
				if (synsets[i].getType() == SynsetType.NOUN) {

					NounSynset nounSynset = (NounSynset) (synsets[i]);

					// hyponyms
					NounSynset[] hyponymSynsets = nounSynset.getHyponyms();
					if( hyponymSynsets.length > 0 ) {
						newResult += "<h2><font color=\"blue\">Hyponyms of Synset " + i + "</font></h2><br>"; 
					}

					for (int k = 0; k < hyponymSynsets.length; k++) {
						newResult += "<b>Hyponym " + k + "</b> (" + hyponymSynsets[k].hashCode() + "): " + hyponymSynsets[k].getDefinition() + ".<br>";
						String[] hyponymWords = hyponymSynsets[k].getWordForms();
						if (hyponymWords.length > 0) newResult += "Word forms: ";
						
						for (int l = 0; l < hyponymWords.length; l++) {
							if (l > 0 && l < words.length) newResult += ", "; 
							newResult += hyponymWords[l]; 
						}
						newResult += ".<br><br>"; 
					}

					// hypernyms
					NounSynset[] hypernymSynsets = nounSynset.getHypernyms();
					
					if( hypernymSynsets.length > 0 ) {
						newResult += "<h2><font color=\"blue\">Hypernyms of Synset "+ i + "</font></h2><br>";
					}

					for (int n = 0; n < hypernymSynsets.length; n++) {
						
						newResult += "<b>Hypernym " + n + "</b> (" + hypernymSynsets[n].hashCode() + "): " + hypernymSynsets[n].getDefinition() + ".<br>";
						
						String[] hypernymWords = hypernymSynsets[n].getWordForms();
						
						if (hypernymWords.length > 0) newResult += "Word forms: ";
						for (int p = 0; p < hypernymWords.length; p++) {
							if (p > 0 && p < hypernymWords.length) newResult += ", ";
							newResult += hypernymWords[p];
						}
						newResult += ".<br><br>"; 
					}
				}
			}
		}

		// ok, we have created the SyledDocument for the result, update the
		// JTextPane.
		txtResult.setText( newResult );
		txtResult.setCaretPosition(0); // scroll to the top
		
		txtTerm.select(0, txtTerm.getText().length() );
		
	}
}
