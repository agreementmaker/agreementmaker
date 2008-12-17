package agreementMaker.userInterface;

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

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;

public class ProgressDialog extends JDialog implements PropertyChangeListener, ActionListener {

    /**
	 * This is the Progress Dialog class.
	 * @author Cosmin Stroe @date Dec 17, 2008  
	 */
	private static final long serialVersionUID = 2113641985534214381L;
	
	private JPanel progressPanel;
	
	private JProgressBar progressBar;
    private AbstractMatcher matcher;
    
    private JButton okButton = new JButton("Ok");
    private JButton cancelButton = new JButton("Cancel");
    
	// constructor
	public ProgressDialog (AbstractMatcher m) {
	    super();
	
		setTitle("Agreement Maker is Running ...");
		//setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
	    
	    progressPanel = new JPanel(new BorderLayout());
	    
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
	    progressPanel.add(buttonPanel, BorderLayout.PAGE_END);
	    
	    this.add(progressPanel);
	    
	    matcher = m;
	    
	    matcher.setProgressDialog(this);
	    matcher.addPropertyChangeListener(this);
	    
		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		matcher.execute();
		
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
			matcher.setAborted(true);
			this.dispose();
		}
		
	}

	public void matchingComplete() {
		// TODO
		//set into the text area matcher.getReport() right now is in the Joptionpane
		Utility.displayMessagePane(matcher.getMatchReport(), "Matching Process Report");
		okButton.setEnabled(true);
	}
	

	
}
