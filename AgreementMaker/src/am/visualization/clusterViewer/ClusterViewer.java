package am.visualization.clusterViewer;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
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
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import am.app.mappingEngine.SimilarityMatrix;
import am.evaluation.clustering.gvm.GVM_Clustering;
import am.evaluation.clustering.gvm.GVM_Clustering_Panel;
import am.output.OutputController;

import com.tomgibara.cluster.gvm.dbl.DblResult;

public class ClusterViewer extends JPanel implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = -8262540327917020659L;

	//public List<AbstractMatcher> matchers;
	private JLabel lblClusters = new JLabel("Clusters:");
	private JTextField txtClusters = new JTextField("20");
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
	
	private double cPanelX;
	private double cPanelY;
	public ClusterViewer(SimilarityMatrix[] matrices) {
		super();
		this.matrices = matrices; 
		GroupLayout layout = new GroupLayout(this);
		drawLabel.setMinimumSize(new Dimension(300, 200));
		drawLabel.setBorder(new LineBorder(Color.black));
		txtClusters.setMinimumSize(new Dimension(50, -1));
		layout.setHorizontalGroup( layout.createParallelGroup()
				.addGroup( layout.createSequentialGroup()
						.addComponent(btnLoad)
						.addComponent(lblClusters)
						.addComponent(txtClusters)
						.addComponent(btnClusters)
						.addComponent(btnDraw)
						.addComponent(prgBar)

						)
						.addComponent(drawLabel)
				);

		layout.setVerticalGroup( layout.createSequentialGroup()
				.addGroup( layout.createParallelGroup(Alignment.CENTER,false)
						.addComponent(btnLoad)
						.addComponent(lblClusters)
						.addComponent(txtClusters)
						.addComponent(btnClusters)
						.addComponent(btnDraw)
						.addComponent(prgBar)
						)
						.addComponent(drawLabel)
				);

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

		frm.add( view, BorderLayout.CENTER);

		frm.pack();
		frm.setLocationRelativeTo(null);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);

		

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == btnClusters){
			Runnable work;

			if (btnLoad.getText().equals("Clustering...")){
				myThread2.stop(); 
				myThread2 = null;

			}else{
				work = new Runnable(){
				
					@Override
					public void run() {
						btnClusters.setText("Clustering...");
						gvmcl = new GVM_Clustering(matrices, Integer.parseInt(txtClusters.getText()));
						gvmcl.addPropertyChangeListener(ClusterViewer.this);
						//prgBar.setIndeterminate(true);
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
		if (arg0.getSource() == btnLoad ){
			if (btnLoad.getText().equals("Loading...")){
				myThread.stop();											// Deprecated, might want to fix...
				matrices = null; 
				myThread = null;
				btnLoad.setText("Load Matrices");
				prgBar.setValue(0);
			}else{
				Runnable work = new Runnable(){

					@Override
					public void run() {
						btnLoad.setText("Loading...");
						matrices = new SimilarityMatrix[5];
						prgBar.setValue(0);
						matrices[0] = OutputController.readMatrixFromCSV("../Ontologies/OAEI/2011/anatomy/matrices/lsmw.rdf.bz2", true);
						prgBar.setValue(prgBar.getValue()+20);
						matrices[1] = OutputController.readMatrixFromCSV("../Ontologies/OAEI/2011/anatomy/matrices/mm.rdf.bz2", true);
						prgBar.setValue(prgBar.getValue()+20);
						matrices[2] = OutputController.readMatrixFromCSV("../Ontologies/OAEI/2011/anatomy/matrices/oaei2011.rdf.bz2", true);
						prgBar.setValue(prgBar.getValue()+20);
						matrices[3] = OutputController.readMatrixFromCSV("../Ontologies/OAEI/2011/anatomy/matrices/psm.rdf.bz2", true);
						prgBar.setValue(prgBar.getValue()+20);
						matrices[4] = OutputController.readMatrixFromCSV("../Ontologies/OAEI/2011/anatomy/matrices/vmm.rdf.bz2", true);
						prgBar.setValue(prgBar.getValue()+20);
						btnLoad.setText("Load Matrices");
					}
				};
				myThread = new Thread(work, "Load");
				
				myThread.start();
			}
		}
		
		if (arg0.getSource() == btnDraw ){
			if (btnLoad.getText().equals("Drawing...")){
				drawThread.stop(); 
				drawThread = null;
				btnDraw.setText("Draw!");
				prgBar.setValue(0);	
			}else{
				Runnable work = new Runnable(){

					@Override
					public void run() {
						btnDraw.setText("Drawing...");
						prgBar.setValue(20);
						//changeSize();
						prgBar.setValue(0);
						
						if (cluster == true){
						giveClusterNumber();
						createImage();
						}else{
							JOptionPane.showMessageDialog(btnDraw, "YOU DIDN'T CLUSTER! JEEZ!");
						}
						
					}
					
				};
				drawThread = new Thread(work, "DrawThread");
				drawThread.start();
			}
			
		}

	}


	public void changeSize (){
		double panelX;
		double panelY;
		double rows = matrices[0].getRows();
		double cols = matrices[0].getColumns();

		panelY = drawLabel.getSize().height;
		panelX = drawLabel.getSize().width; 

		cPanelX = Math.ceil(cols/panelX);
		cPanelY = Math.ceil(rows/panelY);

		 
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if( evt.getPropertyName().equals("progress") ) {
			if ( prgBar.isEnabled() ) prgBar.setValue(((Integer)evt.getNewValue()).intValue());

		}
	}

	public void designateColor(){
		
		Random rand = new Random();
		
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		
		gvmcl.getClusters();
		
	}

	public void giveClusterNumber(){
		int bigRows = matrices[0].getRows();
		int bigCols = matrices[0].getColumns();
		
		clusterMatrix = new int[bigRows][bigCols];;
		gcp = new GVM_Clustering_Panel(null);
		
		// returns list of doubles
		List<DblResult<List<double[]>>> clusters = gvmcl.getClusters();
		for(int i=0; i<clusters.size(); i++){
			List<double[]> key = clusters.get(i).getKey();
			for (int j=0; j<key.size(); j++){
				double[] mapping = key.get(j);
				
				clusterMatrix[(int)mapping[0]][(int)mapping[1]] = i;
			}
		}	
		}
	
	public void createImage(){
		int bigRows = clusterMatrix.length;
		int bigCols = clusterMatrix[0].length;
		int amtClusters = Integer.parseInt(txtClusters.getText());
		I = new BufferedImage(bigRows, bigCols, BufferedImage.TYPE_INT_RGB);
		WritableRaster wr = I.getRaster();
		//int[] color = {r,g,b};
		int[][] clusterColor = new int[amtClusters][3];
		 
		
		
		Random rand = new Random();

		for(int k = 0; k<amtClusters; k++){
			int r = rand.nextInt()%256;
			int g = rand.nextInt()%256;
			int b = rand.nextInt()%256;
			int[] color = {r,g,b};
			clusterColor[k] = color;
			}
		
			for(int i = 0; i<bigRows; i++){
				for(int j = 0; j<bigCols; j++){
					
					wr.setPixel(i, j, clusterColor[clusterMatrix[i][j]]);
					
				}
			}
			
			drawLabel.setIcon(new ImageIcon (I));
			System.out.println("Done Creating Image...");
	}
	
/*	@Override
	public void paintComponent(Graphics g) {
				
		//if( enclosingPanel.popupMenuActive() ) return;
		
		super.paintComponent(g);
		if (I != null){
		Graphics2D gPlotArea = (Graphics2D)g;

		gPlotArea.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP , 1.0f));
		gPlotArea.drawImage(I, 0, 0, this);
		}else{
			System.out.println("I is equal to null!!!");
		
		}
			
	}*/
}
