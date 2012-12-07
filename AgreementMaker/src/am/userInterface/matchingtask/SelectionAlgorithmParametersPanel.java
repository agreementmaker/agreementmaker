package am.userInterface.matchingtask;

import javax.swing.JPanel;

import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.utility.messagesending.MessageDispatch;

/**
 * Handles settings for the Selection Algorithm
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 */
public class SelectionAlgorithmParametersPanel extends JPanel {

	private static final long serialVersionUID = 6398060031490749143L;
	private MessageDispatch<Object> dispatch;

	public SelectionAlgorithmParametersPanel(MessageDispatch<Object> dispatch) {
		super();
		this.dispatch = dispatch;
	}
	
	public SelectionAlgorithm getSelectionAlgorithm() {
		return new MwbmSelection();
	}
	
	public DefaultSelectionParameters getSelectionParameters() {
		return new DefaultSelectionParameters();
	}
}
