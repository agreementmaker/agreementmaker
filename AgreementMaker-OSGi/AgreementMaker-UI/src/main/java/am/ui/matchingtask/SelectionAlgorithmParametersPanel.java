package am.ui.matchingtask;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import am.app.Core;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.SelectionAlgorithm;
import am.ui.matchingtask.MatchingTaskCreatorDialog.MatchingTaskCreatorDialogMessages;
import am.utility.messagesending.MessageDispatch;
import am.utility.messagesending.SimpleMessage;

/**
 * Handles settings for the Selection Algorithm
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 */
public class SelectionAlgorithmParametersPanel extends JPanel {

	private static final long serialVersionUID = 6398060031490749143L;
	private MessageDispatch<Object> dispatch;

	private JComboBox<SelectionAlgorithm> box;
	
	public SelectionAlgorithmParametersPanel(MessageDispatch<Object> dispatch) {
		super();
		this.dispatch = dispatch;
		
		box = new JComboBox<SelectionAlgorithm>(
				new DefaultComboBoxModel<SelectionAlgorithm>(new Vector<SelectionAlgorithm>(Core.getInstance().getSelectionAlgorithms())));
		
		this.dispatch.publish(new SimpleMessage<Object>(
				MatchingTaskCreatorDialogMessages.SELECT_SELECTION_ALGORITHM.name(), (Object)box.getSelectedItem()));
		
		this.setLayout(new BorderLayout());
		this.add(box, BorderLayout.NORTH);
	}
	
	public SelectionAlgorithm getSelectionAlgorithm() {
		return (SelectionAlgorithm) box.getSelectedItem();
	}
	
	public DefaultSelectionParameters getSelectionParameters() {
		return new DefaultSelectionParameters();
	}
}
