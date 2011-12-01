package am.app.collaborationEngine;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.utility.WrapLayout;

public class CollaborationServerExperiment extends JFrame implements ActionListener {

	private static final long serialVersionUID = 7271738846789890792L;


	private JButton btnCreateServer, btnCandidateSelection, btnRunUsersExperiment;
	private CollaborationServer cs;

	private int ontoPair;
	
	public CollaborationServerExperiment() {
		super();
		
		JRootPane pane = getRootPane();
		pane.setLayout(new WrapLayout());
		
		btnCreateServer = new JButton("Create Server");
		btnCreateServer.addActionListener(this);
		pane.add(btnCreateServer);
		
		btnCandidateSelection = new JButton("Candidate Selection");
		btnCandidateSelection.addActionListener(this);
		pane.add(btnCandidateSelection);
		
		btnRunUsersExperiment = new JButton("Run Users Experiment");
		btnRunUsersExperiment.addActionListener(this);
		pane.add(btnRunUsersExperiment);
		
		setMinimumSize(new Dimension(200,200));
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( e.getSource() == btnCreateServer ) {
			Logger log = Logger.getLogger(this.getClass());
			log.setLevel(Level.DEBUG);
			
			cs = new CollaborationServerImpl();
			
			log.debug("Loading and matching ontologies ...");
			ontoPair = cs.addOntologyPair("/home/cosmin/Documents/eclipse/AgreementMaker_main_workspace/Ontologies/OAEI/2011/anatomy/mouse.owl", 
					           "/home/cosmin/Documents/eclipse/AgreementMaker_main_workspace/Ontologies/OAEI/2011/anatomy/human.owl");
		}
		
		
		if( e.getSource() == btnCandidateSelection ) {
			
			
			
		}
		
	}
	
	
	public static void main(String[] args) {
		CollaborationServerExperiment exp = new CollaborationServerExperiment();
		exp.setVisible(true);
	}




	
	
	
}
