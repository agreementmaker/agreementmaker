package am.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import am.app.ontology.ontologyParser.TreeBuilder;
import am.app.ontology.ontologyParser.TreeBuilder.ProgressEvent;

public class OntologyLoadingProgressDialog extends JDialog implements PropertyChangeListener, ActionListener {

    /**
	 * This is the Progress Dialog class.
	 * @author Cosmin Stroe @date Dec 17, 2008  
	 */
	private static final long serialVersionUID = 2113641985534214381L;
	
	private JPanel progressPanel;
	private JPanel textPanel;
	
	private JProgressBar progressBar;
    private TreeBuilder<?> treeBuilder; // the matcher that is associated with this dialog, needed in order for cancel() to work.
    
    private JButton okButton = new JButton("Ok");
    private JButton cancelButton = new JButton("Cancel");
    private JTextArea report;
    private JScrollPane scrollingArea;
    
	/**
	 * Constructor. 
	 * @param m
	 */
	public OntologyLoadingProgressDialog (TreeBuilder<?> t) {
	    super(UICore.getUI().getUIFrame(), true);  // to get focus back
	
	    report = new JTextArea(10, 38);
	    
		setTitle("Agreement Maker is Running ...");  // you'd better go catch it!
		report.setText("Loading...\nIf the ontology contains several thousands of concepts,\nthis operation may take some minutes.");
		report.setEditable(false);
		//setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
	    
	    progressPanel = new JPanel(new BorderLayout());
	    textPanel = new JPanel(new BorderLayout());
	    
	    progressBar = new JProgressBar(0, 100);
	    progressBar.setValue(0);
	    progressBar.setStringPainted(true);
	
	    progressPanel.add(progressBar, BorderLayout.PAGE_START);
	    progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    
	    JPanel buttonPanel = new JPanel(new FlowLayout());
	    okButton.setEnabled(false);
	    okButton.addActionListener(this);
	    cancelButton.addActionListener(this);
	    
	    buttonPanel.add(cancelButton);
	    buttonPanel.add(okButton);
	    
	    scrollingArea = new JScrollPane(report);
	    textPanel.add(scrollingArea);
	    textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
	    
	    progressPanel.add(textPanel);
	    progressPanel.add(buttonPanel, BorderLayout.PAGE_END);
	    
	    this.add(progressPanel);
	    
	    treeBuilder = t;
	    
		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		progressBar.setIndeterminate(true); // we are not updating the progress bar.
	    treeBuilder.addPropertyChangeListener(this);  // we are receiving updates from the matcher.
		
	    treeBuilder.addProgressListener(this);
		
		treeBuilder.execute();
		
		getRootPane().setDefaultButton(okButton);
		
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);   
	    
	}
	
	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
	    JRootPane rootPane = new JRootPane();
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
	    Action actionListener = new AbstractAction() {
	    	private static final long serialVersionUID = -8158364198124217519L;
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
	
	/**
	 * Callback for the TreeBuilder to send events.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {		

		ProgressEvent event = ProgressEvent.getEvent(evt.getPropertyName());
		
		if( event == ProgressEvent.ONTOLOGY_LOADED ) {
			// The algorithm has been completed, update the report text area.
			progressBar.setIndeterminate(false);
			progressBar.setValue(100);
			report.append(treeBuilder.getReport());
			cancelButton.setEnabled(false);
			okButton.setEnabled(true);
			return;
		}
		else if( event == ProgressEvent.CLEAR_LOG ) {
			report.setText("");
			return;
		}
		else if( event == ProgressEvent.APPEND_LINE ) {
			report.append((String)evt.getNewValue() + "\n");
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == okButton) {
			this.dispose();
		}
		else if(obj == cancelButton) {
			treeBuilder.cancel(true);
			this.dispose();
		}
		
	}	
}
