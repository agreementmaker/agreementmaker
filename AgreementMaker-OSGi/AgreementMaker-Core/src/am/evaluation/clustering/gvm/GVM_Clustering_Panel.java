package am.evaluation.clustering.gvm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;

import com.tomgibara.cluster.gvm.dbl.DblResult;

public class GVM_Clustering_Panel extends JPanel implements PropertyChangeListener, ActionListener {

	private static final long serialVersionUID = 7870888087833218397L;

	JLabel lblNumClusters = new JLabel("Number of Clusters:");
	JTextField txtNumClusters = new JTextField("20");
	JLabel lblReference = new JLabel("Reference Alignment:");
	JComboBox cmbReference = new JComboBox();
	JProgressBar prgClustering = new JProgressBar();
	
	JButton btnCluster = new JButton("Cluster");
	
	JTextArea txtResult = new JTextArea();
	
	List<MatcherResult> matchers;
	GVM_Clustering gvmcl;
	
	JScrollPane scrResult = new JScrollPane();
	
	Thread clusteringThread;
	
	public GVM_Clustering_Panel(List<MatcherResult> matchers) {
		super();
		
		this.matchers = matchers;
		prgClustering.setEnabled(false);
		
		txtNumClusters.setMinimumSize(new Dimension(40,-1));
		
		txtResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
		if( matchers != null ) {
			txtResult.setText("Input is " + matchers.size() + " matchers.\n\n");
			for( MatcherResult m : matchers ) {
				txtResult.append(m.getMatchingTask().getShortLabel() + "\n");
			}
		} else {
			txtResult.setText("No input matchers.");
		}
		
		scrResult.setMinimumSize(new Dimension(400,200));
		scrResult.setViewportView(txtResult);
		
		GroupLayout layout = new GroupLayout(this);
		
		// setup the reference alignment combo box
		cmbReference.setMinimumSize(new Dimension(100,-1));
		for( AbstractMatcher m : Core.getInstance().getMatcherInstances() ) {
			cmbReference.addItem(m.getName());
		}
		
		btnCluster.addActionListener(this);
		
		prgClustering.setDoubleBuffered(true);   
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup( layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(lblNumClusters)
					.addComponent(txtNumClusters)
					.addComponent(lblReference)
					.addComponent(cmbReference)
					.addComponent(btnCluster)
				)
				.addComponent(prgClustering)
				.addComponent(scrResult)
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
					.addComponent(lblNumClusters)
					.addComponent(txtNumClusters)
					.addComponent(lblReference)
					.addComponent(cmbReference)
					.addComponent(btnCluster)
				)
				.addComponent(prgClustering)
				.addComponent(scrResult)
		);
		
		setLayout(layout);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if( evt.getPropertyName().equals("progress") ) {
			if ( prgClustering.isEnabled() ) prgClustering.setValue((Integer)evt.getNewValue());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnCluster ) {
			
			if( btnCluster.getText().equals("Stop") ) 
			{
				prgClustering.setEnabled(false);
				txtNumClusters.setEnabled(true);
				cmbReference.setEnabled(true);
				if( clusteringThread != null ) {
					clusteringThread.stop();
					clusteringThread = null;
				}
				btnCluster.setText("Cluster");
			} 
			else {
				btnCluster.setText("Stop");
				prgClustering.setEnabled(true);
				txtNumClusters.setEnabled(false);
				cmbReference.setEnabled(false);
				
				clusteringThread = new Thread(new Runnable() {
					@Override
					public void run() {
						
						int numClusters = 2;
						try {
							numClusters = Integer.parseInt(txtNumClusters.getText());
						} catch( NumberFormatException e ) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(GVM_Clustering_Panel.this, "Could not parse the number of clusters.  Defaulting to 2.");
						}
						long startTime = System.currentTimeMillis();
						
						gvmcl = new GVM_Clustering(matchers, numClusters);
						gvmcl.addPropertyChangeListener(GVM_Clustering_Panel.this);
						gvmcl.cluster();
						gvmcl.removePropertyChangeListener(GVM_Clustering_Panel.this);
						
						prgClustering.setIndeterminate(true);
						
						AbstractMatcher referenceMatcher = Core.getInstance().getMatcherInstances().get(cmbReference.getSelectedIndex());
						List<Node> sourceClasses = referenceMatcher.getSourceOntology().getClassesList();
						List<Node> targetClasses = referenceMatcher.getTargetOntology().getClassesList();
						SimilarityMatrix classesMatrix = referenceMatcher.getClassesMatrix();
						Alignment<Mapping> classesAlignment = referenceMatcher.getClassAlignmentSet();
						
						
						// list of clusters
						
						List<DblResult<List<double[]>>> clusters = gvmcl.getClusters();
						//
						List<ClusterData> clusterData = new ArrayList<ClusterData>();
						//loop thru clusters, for each cluster, there is a cluster KEY [for each key we have a 
						// row and column of mapping. keys is a list, of all of the mappings in the cluster
						// and the mappings are identified by row and column stored in array of doubles. 
						
						// every mapping gets a number inside a NEW matrix 
						for( int i = 0; i < clusters.size(); i++ ) {
							DblResult<List<double[]>> currentCluster = clusters.get(i);
							List<double[]> clusterKey = currentCluster.getKey();
							
							ClusterData cd = new ClusterData();
							cd.clusterID = i;
							cd.clusterSize = clusterKey.size();
							cd.correctMappings = 0;
							
							for( int j = 0; j < clusterKey.size(); j++ ) {
								double[] point = clusterKey.get(j);
								
								Node sourceNode = sourceClasses.get((int)point[point.length-2]);
								Node targetNode = targetClasses.get((int)point[point.length-1]);
								
								if( classesAlignment.contains( sourceNode, targetNode) != null )  {
									cd.correctMappings++;
								}
								/*if( classesMatrix.getSimilarity( (int)point[point.length-2], (int)point[point.length-1]) > 0.0d )
									cd.correctMappings++;*/
							}
							
							clusterData.add(cd);
						}
						
						Collections.sort(clusterData, new Comparator<ClusterData>() {
							@Override
							public int compare(ClusterData o1, ClusterData o2) {
								return o1.correctMappings - o2.correctMappings;
							}
						});
						
						Collections.reverse(clusterData);
						
						txtResult.setText("Clustering results:\n\n");
						txtResult.append("             | Cluster Size |   # correct  | correct | recall |\n");
						
						int selClusterSize = 0, selClusterCorrect = 0;
						
						for( ClusterData cd : clusterData ) {
							double recall = ((double) cd.correctMappings / (double) classesAlignment.size());
							double correct = ((double) cd.correctMappings / (double) cd.clusterSize);
							
							if( correct > 0.5d ) {
								selClusterSize += cd.clusterSize;
								selClusterCorrect += cd.correctMappings;
							}
							
							txtResult.append(String.format("cluster %4d | %12d | %12d |  %1.3f  | %1.3f |\n",
									cd.clusterID, cd.clusterSize, cd.correctMappings, correct, recall));
						}
						
						long endTime = System.currentTimeMillis();
						
						txtResult.append("\n\nSelecting clusters with %correct > 0.5:\n\n");
						txtResult.append(String.format("Correct: %1.3f\n", (double)selClusterCorrect/(double)selClusterSize ));
						txtResult.append(String.format("Recall: %1.3f\n", (double)selClusterCorrect/(double)classesAlignment.size() ));
						
						txtResult.append("\nRun time: " + Utility.getFormattedTime(endTime-startTime) + "\n");
						
						prgClustering.setIndeterminate(false);
						prgClustering.setEnabled(false);
						txtNumClusters.setEnabled(true);
						cmbReference.setEnabled(true);
						btnCluster.setText("Cluster");
						
						
					}
				});
				
				clusteringThread.start();
				
				
			}
			
		}
	}
	
	
	private class ClusterData {
		int clusterID;
		int clusterSize;
		int correctMappings;
	}
	
	public static void main(String[] args) {
		
		JFrame frm = new JFrame();
		
		frm.setLayout(new BorderLayout());
		
		frm.add(new GVM_Clustering_Panel(null), BorderLayout.CENTER);
		
		frm.pack();
		frm.setLocationRelativeTo(null);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);

	}





}
