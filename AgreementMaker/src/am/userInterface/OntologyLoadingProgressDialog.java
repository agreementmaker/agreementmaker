package am.userInterface;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import am.GlobalStaticVariables;
import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Matcher;
import am.app.ontology.ontologyParser.TreeBuilder;

public class OntologyLoadingProgressDialog extends JDialog implements PropertyChangeListener, ActionListener {

    /**
	 * This is the Progress Dialog class.
	 * @author Cosmin Stroe @date Dec 17, 2008  
	 */
	private static final long serialVersionUID = 2113641985534214381L;
	
	private JPanel progressPanel;
	private JPanel textPanel;
	
	private JProgressBar progressBar;
    private TreeBuilder treeBuilder; // the matcher that is associated with this dialog, needed in order for cancel() to work.
    
    private JButton okButton = new JButton("Ok");
    private JButton cancelButton = new JButton("Cancel");
    private JTextArea report;
    private JScrollPane scrollingArea;
    
	/**
	 * Constructor. 
	 * @param m
	 */
	public OntologyLoadingProgressDialog (TreeBuilder t) {
	    super();
	
	    report = new JTextArea(10, 38);
	    
		setTitle("Agreement Maker is Running ...");  // you'd better go catch it!
		report.setText("Loading...\nIf the ontology contains several thousands of concepts,\nthis operation may take some minutes.");
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
	    buttonPanel.add(okButton);
	    buttonPanel.add(cancelButton);
	    
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
		
	    treeBuilder.setProgressDialog(this);
		
		treeBuilder.execute();
		
		pack();
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);   
	    
	}
	
	
	/**
	 * This function is called from the AbstractMatcher everytime setProgress() is called from inside the matcher.
	 * @author Cosmin Stroe @date Dec 17, 2008
	 */
	public void propertyChange(PropertyChangeEvent evt) {		
		
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
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

	public void loadingComplete() {
		//  The algorithm has been completed, update the report text area.
	
		progressBar.setIndeterminate(false);
		progressBar.setValue(100);
		report.setText(treeBuilder.getReport());
		cancelButton.setEnabled(false);
		okButton.setEnabled(true);
		
	}
	

	
}
