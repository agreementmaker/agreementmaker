package am.extension.collaborationEngine;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.utility.WrapLayout;

public class CollaborationServerExperiment extends JFrame implements ActionListener {

	private static final long serialVersionUID = 7271738846789890792L;

	private static final Logger sLog = Logger.getLogger(CollaborationServerExperiment.class);

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
			
			Logger log = Logger.getLogger(this.getClass());
			log.setLevel(Level.DEBUG);
			
			ReferenceAlignmentMatcher ram = new ReferenceAlignmentMatcher();
			
			CollaborationOntologyPair cop = cs.getPair(ontoPair);
			
			ram.setSourceOntology(cop.sourceOntology);
			ram.setTargetOntology(cop.targetOntology);
			
			ReferenceAlignmentParameters rap = new ReferenceAlignmentParameters();
			rap.fileName = "/home/cosmin/Documents/eclipse/AgreementMaker_main_workspace/Ontologies/OAEI/2011/anatomy/reference_2011.rdf";
			rap.format = ReferenceAlignmentMatcher.OAEI;
			
			
			ram.setParam(rap);
			try {
				
				log.debug("Loading reference alignment ...");
				
				ram.match();
				
				log.debug("Saving top 1,000,000 disagreed upon mappings ...");
				
				Queue<Mapping> queue = cs.getRankingQueue(ontoPair);
				
				int sum = 0;
				SimilarityMatrix ref = ram.getClassesMatrix();
				BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("/home/cosmin/Desktop/clustering/top1000000.csv"))); 
				for( int i = 0; queue.peek() != null; i++ ) {
					Mapping m = queue.poll();
					if( ref.getSimilarity(m.getSourceKey(), m.getTargetKey()) > 0d ) {
						sum++;
					}
					
					bwr.write(Integer.toString(i) + "," + Integer.toString(sum) + "\n");
				}
				
				bwr.close();
				
			} catch (Exception ex) {
				sLog.error("", ex);
			}
			
		}
		
		if( e.getSource() == btnRunUsersExperiment ) {
			
		}
		
	}
	
	
	public static void main(String[] args) {
		CollaborationServerExperiment exp = new CollaborationServerExperiment();
		exp.setVisible(true);
	}

	
}
