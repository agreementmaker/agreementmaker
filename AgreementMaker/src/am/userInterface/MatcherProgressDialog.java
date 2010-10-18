package am.userInterface;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import am.GlobalStaticVariables;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;

public class MatcherProgressDialog extends JDialog implements MatchingProgressDisplay, ActionListener {

    /**
	 * This is the Progress Dialog class.
	 * @author Cosmin Stroe @date Dec 17, 2008  
	 */
	private static final long serialVersionUID = 2113641985534214381L;
	
	private JPanel progressPanel;
	private JPanel textPanel;
	
	private JProgressBar progressBar;
    private AbstractMatcher matcher; // the matcher that is associated with this dialog, needed in order for cancel() to work.
    
    private JButton okButton = new JButton("Ok");
    private JButton cancelButton = new JButton("Cancel");
    private JTextArea matcherReport;
    private JScrollPane scrollingArea;
    
    private JCheckBox radioAlarm, radioBeep;
    private JLabel finishLabel = new JLabel("On finish:");
    
    /** Application Wide preferences, that are saved to a configuration file, and can be restored at any time. */
	private AppPreferences prefs;
    
	/**
	 * Constructor. 
	 * @param m
	 */
	public MatcherProgressDialog (AbstractMatcher m) {
	    super();
	
	    prefs = Core.getAppPreferences();
	    
	    matcherReport = new JTextArea(8, 35);
	    
		setTitle("Agreement Maker is Running ...");  // you'd better go catch it!
		matcherReport.setText("Running...");
		//setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
	    
	    progressPanel = new JPanel(new BorderLayout());
	    textPanel = new JPanel(new BorderLayout());
	    
	    progressBar = new JProgressBar(0, 100);
	    progressBar.setValue(0);
	    progressBar.setStringPainted(true);
	
	    progressPanel.add(progressBar, BorderLayout.PAGE_START);
	    progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    
	    radioBeep = new JCheckBox("Beep");
	    radioBeep.addActionListener(this);
	    radioAlarm = new JCheckBox("Alarm");
	    radioAlarm.addActionListener(this);
	    
	    if( prefs.getBeepOnFinish() ) radioBeep.setSelected(true);
	    else radioBeep.setSelected(false);
	    
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    okButton.setEnabled(false);
	    okButton.addActionListener(this);
	    cancelButton.addActionListener(this);
	    buttonPanel.add(finishLabel);
	    buttonPanel.add(radioBeep);
	    //buttonPanel.add(radioAlarm);
	    buttonPanel.add(cancelButton);
	    buttonPanel.add(okButton);

	    
	    scrollingArea = new JScrollPane(matcherReport);
	    textPanel.add(scrollingArea);
	    textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
	    
	    progressPanel.add(textPanel);
	    progressPanel.add(buttonPanel, BorderLayout.PAGE_END);
	    
	    this.add(progressPanel);
	    
	    matcher = m;
	    
		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		if( !GlobalStaticVariables.USE_PROGRESS_BAR )
			progressBar.setIndeterminate(true); // we are not updating the progress bar.
		else {
		    matcher.addPropertyChangeListener(this);  // we are receiving updates from the matcher.
		}
		
	    matcher.setProgressDisplay(this);
		
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
			matcher.cancel(true);
			this.dispose();
		} else if( obj == radioBeep ) {
			if( radioBeep.isSelected() && radioAlarm.isSelected() ) radioAlarm.setSelected(false);
			if( radioBeep.isSelected() ) { prefs.saveBeepOnFinish(true); }
			else { prefs.saveBeepOnFinish(false); }
		} else if( obj == radioAlarm ) {
			if( radioBeep.isSelected() && radioAlarm.isSelected() ) radioBeep.setSelected(false);
		}
		
	}

	
	/*********** Matching Progress Display Methods *****************************************/
	
	/**
	 * Add text to the report text box.
	 */
	@Override
	public void appendToReport( String report ) { matcherReport.append( report ); }
	
	@Override
	public void matchingStarted() { progressBar.setEnabled(true); }
	
	@Override
	public void matchingComplete() {
		//  The algorithm has been completed, update the report text area.
		if( !GlobalStaticVariables.USE_PROGRESS_BAR ) {
			progressBar.setIndeterminate(false);
			progressBar.setValue(100);
		}
		
		matcherReport.setText( matcher.getReport() );
		cancelButton.setEnabled(false);
		okButton.setEnabled(true);
		
		if( !matcher.isCancelled() ) {
			if( radioAlarm.isSelected() ) {
				finishLabel.setText("> > > > > > > >");
				radioBeep.setVisible(false);
				radioAlarm.setText("Uncheck to stop alarm.");
				
				startAlarm();
			} else if( radioBeep.isSelected() ) {
				doBeep();
			}
		}
		
		
	}


	private void doBeep() {
		new SoundNotification("images/yeah.wav").start();
	}


	private void startAlarm() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void scrollToEndOfReport() {
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	if( scrollingArea != null && matcherReport != null ) {
	        		// a complete hack to make the JScrollPane move to the bottom of the JTextArea
	        		Document d = matcherReport.getDocument();
	        		matcherReport.setCaretPosition(d.getLength());
	        	}
	        }
		});
	}


	@Override
	public void clearReport() {
		matcherReport.setText("");		
	}	

	
}
