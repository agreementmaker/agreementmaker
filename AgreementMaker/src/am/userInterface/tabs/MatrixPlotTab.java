package am.userInterface.tabs;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

import am.Utility;
import am.app.mappingEngine.StringUtil.PorterStemmer;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class MatrixPlotTab extends JPanel implements ActionListener, KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5538266168231508803L;
	
	private JScrollPane matrixPanel;
	private JPanel actionPanel;
	
	private JButton addView;
	private JButton removeView;
	
	private JLabel lblTerm;
	private JTextField txtTerm;
	
	
	
	private JTextPane txtResult;
	
	public MatrixPlotTab() {
		super();
		initElements();

		lblTerm = new JLabel("Term: ");
		
		txtTerm = new JTextField();
		txtTerm.addKeyListener( this );
		
		txtResult = new JTextPane();
		txtResult.setContentType("text/html");
		
		Font font = new Font("Courier New", Font.PLAIN, 12);
		if( font != null ) txtResult.setFont(font);
		
		// set a 5 pixel all around border
		setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5) ); 

		// create the placement of the components
		GroupLayout wnpanelLayout = new GroupLayout(this);
		wnpanelLayout.setAutoCreateGaps(true);
		wnpanelLayout.setAutoCreateContainerGaps(true);
		
		
		wnpanelLayout.setHorizontalGroup( wnpanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(matrixPanel)
				.addComponent(actionPanel)
				);
		
		wnpanelLayout.setVerticalGroup( wnpanelLayout.createSequentialGroup()
				.addComponent(matrixPanel)
				.addComponent(actionPanel)
				);
		
		setLayout(wnpanelLayout);
		
	}
	
	private void initElements(){
		
		matrixPanel = new JScrollPane();
		matrixPanel.setWheelScrollingEnabled(true);
		matrixPanel.getVerticalScrollBar().setUnitIncrement(20);
		matrixPanel.setViewportView(txtResult);

		// contains all the panels for the matrix
		actionPanel = new JPanel();
		
		addView = new JButton("Add");
		addView.addActionListener(this);
		removeView = new JButton("Remove");
		removeView.addActionListener(this);
		
		setActionPanelview();
	}
	
	private void setActionPanelview(){
		GroupLayout wnpanelLayout = new GroupLayout(actionPanel);
		wnpanelLayout.setAutoCreateGaps(true);
		wnpanelLayout.setAutoCreateContainerGaps(true);
		
		
		wnpanelLayout.setHorizontalGroup( wnpanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(addView)
				.addComponent(removeView)
			);
		
		wnpanelLayout.setVerticalGroup( wnpanelLayout.createSequentialGroup()
				.addComponent(addView)
				.addComponent(removeView)
				);
		
		setLayout(wnpanelLayout);
	}

	/**
	 * Handle the lookup button clicks.
	 */
	@Override
	public void actionPerformed(ActionEvent currentEvent) {
			
		if (currentEvent.getSource() == addView) {
			// The Lookup button was clicked. 
			//doLookup();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override public void keyPressed(KeyEvent e) { }
	@Override public void keyReleased(KeyEvent e) {
		if( e.getSource() == txtTerm && e.getKeyCode() == KeyEvent.VK_ENTER ) {
			//doLookup();
		}
	}
	
}
