package am.extension.multiUserFeedback.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import am.extension.collaborationClient.api.CollaborationTask;
import am.ui.UICore;

public class TaskSelectionDialog extends JDialog {

	private static final long serialVersionUID = -7052618568055986717L;

	private JComboBox<CollaborationTask> cmbTasks;
	private JLabel lblSelectTask = new JLabel("Select a matching task:");
	private JButton btnSelect = new JButton("Select Task");
	
	public TaskSelectionDialog(List<CollaborationTask> tasks) {
		super(UICore.getUI().getUIFrame());
		
		cmbTasks = new JComboBox<CollaborationTask>(tasks.toArray(new CollaborationTask[0]));
		
		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TaskSelectionDialog.this.setVisible(false);
			}
		});
		
		setLayout(new FlowLayout());
		add(lblSelectTask);
		add(cmbTasks);
		add(btnSelect);
		
		pack();
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
	}
	
	public CollaborationTask getTask() {
		return (CollaborationTask) cmbTasks.getSelectedItem();
	}
}
