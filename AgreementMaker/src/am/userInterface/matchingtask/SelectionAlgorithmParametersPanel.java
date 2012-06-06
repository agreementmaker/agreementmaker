package am.userInterface.matchingtask;

import javax.swing.JPanel;

import am.utility.messagesending.MessageDispatch;

public class SelectionAlgorithmParametersPanel extends JPanel {

	private static final long serialVersionUID = 6398060031490749143L;
	private MessageDispatch<Object> dispatch;

	public SelectionAlgorithmParametersPanel(MessageDispatch<Object> dispatch) {
		super();
		this.dispatch = dispatch;
	}
	
}
