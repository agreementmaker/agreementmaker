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

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;

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
	
	List<AbstractMatcher> matchers;
	GVM_Clustering gvmcl;
	
	JScrollPane scrResult = new JScrollPane();
	
	Thread clusteringThread;
	
	public GVM_Clustering_Panel(List<AbstractMatcher> matchers) {
		super();
		
		this.matchers = matchers;
		prgClustering.setEnabled(false);
		
		txtNumClusters.setMinimumSize(new Dimension(40,-1));
		
		txtResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
		if( matchers != null ) {
			txtResult.setText("Input is " + matchers.size() + " matchers.\n\n");
			for( AbstractMatcher m : matchers ) {
				txtResult.append(m.getName() + "\n");
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
						
						gvmcl = new GVM_Clustering(matchers, numClusters);
						gvmcl.addPropertyChangeListener(GVM_Clustering_Panel.this);
						gvmcl.cluster();
						gvmcl.removePropertyChangeListener(GVM_Clustering_Panel.this);
						
						prgClustering.setIndeterminate(true);
						
						AbstractMatcher referenceMatcher = Core.getInstance().getMatcherInstances().get(cmbReference.getSelectedIndex());
						SimilarityMatrix classesMatrix = referenceMatcher.getClassesMatrix();
						Alignment<Mapping> classesAlignment = referenceMatcher.getClassAlignmentSet();
						
						List<DblResult<List<double[]>>> clusters = gvmcl.getClusters();
						
						List<ClusterData> clusterData = new ArrayList<ClusterData>();
						
						for( int i = 0; i < clusters.size(); i++ ) {
							DblResult<List<double[]>> currentCluster = clusters.get(i);
							List<double[]> clusterKey = currentCluster.getKey();
							
							ClusterData cd = new ClusterData();
							cd.clusterID = i;
							cd.clusterSize = clusterKey.size();
							cd.correctMappings = 0;
							for( double[] point : clusterKey ) {
								if( classesMatrix.getSimilarity( (int)point[point.length-2], (int)point[point.length-1]) > 0.0d )
									cd.correctMappings++;
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
						txtResult.append("             | Cluster Size |   # correct  | % correct | Recall |\n");
						for( ClusterData cd : clusterData ) {
							txtResult.append(String.format("cluster %4d | %12d | %12d |   %2.2f   | %3.2f |\n", 
									cd.clusterID, cd.clusterSize, cd.correctMappings, (double) cd.correctMappings / (double) cd.clusterSize,
									(double) cd.correctMappings / (double) classesAlignment.size()) );
						}
						
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
