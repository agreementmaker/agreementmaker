package am.visualization.clusterViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.border.LineBorder;

import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.evaluation.clustering.gvm.GVM_Clustering;
import am.evaluation.clustering.gvm.GVM_Clustering_Panel;
import am.parsing.OutputController;

import com.tomgibara.cluster.gvm.dbl.DblResult;

public class ClusterViewer extends JPanel implements ActionListener,
PropertyChangeListener {

	private static final long serialVersionUID = -8262540327917020659L;

	// public List<AbstractMatcher> matchers;
	private JLabel lblClusters = new JLabel("Clusters:");
	private JTextField txtClusters = new JTextField("Cluster Amt");
	private JButton btnClusters = new JButton("Cluster");
	private JButton btnDraw = new JButton("Draw");
	private JProgressBar prgBar = new JProgressBar();
	private JLabel drawLabel = new JLabel();
	private JButton btnLoad = new JButton("Load Matrices");
	private GVM_Clustering gvmcl;
	private GVM_Clustering_Panel gcp;
	private SimilarityMatrix[] matrices;
	private Thread myThread;
	private Thread myThread2;
	private Thread drawThread;
	private int[][] clusterMatrix;
	private boolean cluster;
	private BufferedImage I;
	private int[][] newMatrix;
	private int[][] finalMatrix;
	private double cPanelX;
	private double cPanelY;
	private Scrollable picture;
	public ClusterViewer(SimilarityMatrix[] matrices) {
		super();
		this.matrices = matrices;
		GroupLayout layout = new GroupLayout(this);
		drawLabel.setMinimumSize(new Dimension(300, 200));
		drawLabel.setBorder(new LineBorder(Color.black));
		JScrollPane scr = new JScrollPane(drawLabel);
		txtClusters.setMinimumSize(new Dimension(50, -1));
		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup().addComponent(btnLoad)
						.addComponent(lblClusters)
						.addComponent(txtClusters)
						.addComponent(btnClusters)
						.addComponent(btnDraw).addComponent(prgBar)

						).addComponent(scr));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(Alignment.CENTER, false)
						.addComponent(btnLoad)
						.addComponent(lblClusters)
						.addComponent(txtClusters)
						.addComponent(btnClusters)
						.addComponent(btnDraw).addComponent(prgBar))
						.addComponent(scr));

		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		setLayout(layout);

		btnClusters.addActionListener(this);
		btnLoad.addActionListener(this);
		btnDraw.addActionListener(this);
	}

	public static void main(String[] args) {
		ClusterViewer view = new ClusterViewer(null);
		JFrame frm = new JFrame();

		frm.setLayout(new BorderLayout());

		frm.add(view, BorderLayout.CENTER);

		frm.pack();
		frm.setLocationRelativeTo(null);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == btnClusters) {
			Runnable work;

			if (btnLoad.getText().equals("Clustering...")) {
				myThread2.stop();
				myThread2 = null;

			} else {
				work = new Runnable() {

					@Override
					public void run() {
						btnClusters.setText("Clustering...");
						gvmcl = new GVM_Clustering(matrices,
								Integer.parseInt(txtClusters.getText()));
						gvmcl.addPropertyChangeListener(ClusterViewer.this);
						// prgBar.setIndeterminate(true);
						gvmcl.cluster();
						gvmcl.removePropertyChangeListener(ClusterViewer.this);
						prgBar.setValue(0);
						btnClusters.setText("Cluster!");
						cluster = true;
					}
				};
				myThread2 = new Thread(work);
				myThread2.start();
			}
		}
		if (arg0.getSource() == btnLoad) {
			if (btnLoad.getText().equals("Loading...")) {
				myThread.stop(); // Deprecated, might want to fix...
				matrices = null;
				myThread = null;
				btnLoad.setText("Load Matrices");
				prgBar.setValue(0);
			} else {
				Runnable work = new Runnable() {

					@Override
					public void run() {
						btnLoad.setText("Loading...");
						matrices = new SimilarityMatrix[5];
						prgBar.setValue(0);
						matrices[0] = OutputController
								.readMatrixFromCSV(
										"../Ontologies/OAEI/2011/anatomy/matrices/lsmw.rdf.bz2",
										true);
						prgBar.setValue(prgBar.getValue() + 20);
						matrices[1] = OutputController
								.readMatrixFromCSV(
										"../Ontologies/OAEI/2011/anatomy/matrices/mm.rdf.bz2",
										true);
						prgBar.setValue(prgBar.getValue() + 20);
						matrices[2] = OutputController
								.readMatrixFromCSV(
										"../Ontologies/OAEI/2011/anatomy/matrices/oaei2011.rdf.bz2",
										true);
						prgBar.setValue(prgBar.getValue() + 20);
						matrices[3] = OutputController
								.readMatrixFromCSV(
										"../Ontologies/OAEI/2011/anatomy/matrices/psm.rdf.bz2",
										true);
						prgBar.setValue(prgBar.getValue() + 20);
						matrices[4] = OutputController
								.readMatrixFromCSV(
										"../Ontologies/OAEI/2011/anatomy/matrices/vmm.rdf.bz2",
										true);
						prgBar.setValue(prgBar.getValue() + 20);
						btnLoad.setText("Load Matrices");
					}
				};
				myThread = new Thread(work, "Load");

				myThread.start();
			}
		}

		if (arg0.getSource() == btnDraw) {
			if (btnLoad.getText().equals("Drawing...")) {
				drawThread.stop();
				drawThread = null;
				btnDraw.setText("Draw!");
				prgBar.setValue(0);
			} else {
				Runnable work = new Runnable() {

					@Override
					public void run() {
						btnDraw.setText("Drawing...");
						prgBar.setValue(20);
						// changeSize();
						prgBar.setValue(0);

						if (cluster == true) {
							increment();
							createImage();
						} else {
							JOptionPane.showMessageDialog(btnDraw,
									"YOU DIDN'T CLUSTER! JEEZ!");
						}

					}

				};
				drawThread = new Thread(work, "DrawThread");
				drawThread.start();
			}

		}

	}

	public void changeSize() {
		double panelX;
		double panelY;
		double rows = matrices[0].getRows();
		double cols = matrices[0].getColumns();

		panelY = drawLabel.getSize().height;
		panelX = drawLabel.getSize().width;

		cPanelX = Math.ceil(cols / panelX);
		cPanelY = Math.ceil(rows / panelY);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("progress")) {
			if (prgBar.isEnabled())
				prgBar.setValue(((Integer) evt.getNewValue()).intValue());

		}
	}

	
	public void giveClusterNumber() {
		int bigRows = matrices[0].getRows();
		int bigCols = matrices[0].getColumns();

		clusterMatrix = new int[bigRows][bigCols];
		
		gcp = new GVM_Clustering_Panel(null);

		// returns list of doubles
		List<DblResult<List<double[]>>> clusters = gvmcl.getClusters();
		for (int i = 0; i < clusters.size(); i++) {
			List<double[]> key = clusters.get(i).getKey();
			for (int j = 0; j < key.size(); j++) {
				double[] mapping = key.get(j);

				clusterMatrix[(int) mapping[0]][(int) mapping[1]] = i;
			}
		}
	}

	public void increment() {
		giveClusterNumber();
		int r = matrices[0].getRows();
		int c = matrices[0].getColumns();
		int k = Integer.parseInt(txtClusters.getText());
		List<int[]> kr = new ArrayList<int[]>();
		List<int[]> kc = new ArrayList<int[]>();
		List<int[]> krfinal = new ArrayList<int[]>();
		List<int[]> kcfinal = new ArrayList<int[]>();
		

		// for every row, increment the number of times there is a cluster value
		// inside of
		// the cluster

		int clusterNumber = 0;
		for (int CMr = 0; CMr < clusterMatrix.length; CMr++) {
			
			int[] row = new int[k + 1];
			for (int CMc = 0; CMc < clusterMatrix[0].length; CMc++) {
				clusterNumber = clusterMatrix[CMr][CMc];
				row[clusterNumber]++;
			}
			row[row.length - 1] = CMr;
			
			kr.add(row);
		}
		//-clusterNumber = 0;
		for (int CMc = 0; CMc < clusterMatrix[0].length; CMc++) {
			
			int[] col = new int[k + 1];
			for (int CMr = 0; CMr <clusterMatrix.length; CMr++) {
				clusterNumber = clusterMatrix[CMr][CMc];
				col[clusterNumber]++;
			}
			col[col.length - 1] = CMc;
			//CMc = col[col.length - 1] ;
			kc.add(col);
		}

		// clusters sorted by
		// size******************************************************************************
		final List<DblResult<List<double[]>>> clusters = gvmcl.getClusters();

		PriorityQueue<DblResult<List<double[]>>> q = new PriorityQueue<DblResult<List<double[]>>>(
				clusters.size(), new Comparator<DblResult<List<double[]>>>() {

					@Override
					public int compare(DblResult<List<double[]>> o1,
							DblResult<List<double[]>> o2) {
						// sorted in reverse order, so get max out
						return o2.getKey().size() - o1.getKey().size();
					}
				});

		q.addAll(clusters);

		// ************************************************************************************
		// removes first one and gives it to you. :)
		
		DblResult<List<double[]>> currentCluster = new DblResult<List<double[]>>(clusterNumber);

		while ((currentCluster = q.poll()) != null) {
			
			reorder(clusters, currentCluster, kr, krfinal, c);
			reorder(clusters, currentCluster, kc, kcfinal, c);
		}
		krfinal.addAll(kr);
		kcfinal.addAll(kc);
		newMatrix = new int[r][c];
		finalMatrix = new int[r][c];
		
		for (int i = 0; i<(r); i++){
			newMatrix[i] = clusterMatrix[krfinal.get(i)[krfinal.get(i).length-1]];
		//	newMatrix[i] = clusterMatrix[kr.get(i).length-1];			
		}
		
		for (int i = 0; i<(c); i++){
			for(int j = 0 ; j<r; j++){
				finalMatrix[j][i] = newMatrix[j][kcfinal.get(i)[kcfinal.get(i).length-1]]	;
						}
		}
		

	}
	public void reorder(List<DblResult<List<double[]>>> clusters, DblResult<List<double[]>> currentCluster, List<int[]> k,
						List<int[]>kFinal, int c){
	double	mu = 1/k.size();
		int clusterIndex = 0;

			for (clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
				if (clusters.get(clusterIndex) == currentCluster) {
					break;
				}
			}
			final int finalIndex = clusterIndex;
			// sorting by cluster
			Collections.sort(k, new Comparator<int[]>() {

				@Override
				public int compare(int[] arg0, int[] arg1) {
					return arg1[finalIndex] - arg0[finalIndex];
				}

			});

			for (int i = 0; i < k.size(); i++) {
				int[] currentRow = k.get(i);
				if (currentRow[clusterIndex] / c > mu) {
					kFinal.add(currentRow);
					k.remove(i);
					i--;
				} else {
					break;
				}

			}
			
			
	}
	public void createImage() {
		int bigRows = newMatrix.length;
		int bigCols = newMatrix[0].length;
		int amtClusters = Integer.parseInt(txtClusters.getText());
		I = new BufferedImage(bigRows, bigCols, BufferedImage.TYPE_INT_RGB);
		WritableRaster wr = I.getRaster();
		// int[] color = {r,g,b};
		int[][] clusterColor = new int[amtClusters][3];

		Random rand = new Random();

		for (int k = 0; k < amtClusters; k++) {
			int r = rand.nextInt() % 256;
			int g = rand.nextInt() % 256;
			int b = rand.nextInt() % 256;
			int[] color = { r, g, b };
			clusterColor[k] = color;
		}

		for (int i = 0; i < bigRows; i++) {
			for (int j = 0; j < bigCols; j++) {

				wr.setPixel(i, j, clusterColor[newMatrix[i][j]]);

			}
		}
		drawLabel.setIcon(new ImageIcon(I));
		System.out.println("Done Creating Image...");
	}

	/*
	 * @Override public void paintComponent(Graphics g) {
	 * 
	 * //if( enclosingPanel.popupMenuActive() ) return;
	 * 
	 * super.paintComponent(g); if (I != null){ Graphics2D gPlotArea =
	 * (Graphics2D)g;
	 * 
	 * gPlotArea.setComposite( AlphaComposite.getInstance(
	 * AlphaComposite.SRC_ATOP , 1.0f)); gPlotArea.drawImage(I, 0, 0, this);
	 * }else{ System.out.println("I is equal to null!!!");
	 * 
	 * }
	 * 
	 * }
	 */
}
