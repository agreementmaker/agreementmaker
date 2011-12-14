package am.visualization.clusterViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import am.app.mappingEngine.AbstractMatcher;
import am.evaluation.clustering.gvm.GVM_Clustering;
import am.evaluation.clustering.gvm.GVM_Clustering_Panel;

public class ClusterViewer extends JPanel implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = -8262540327917020659L;
	
	public List<AbstractMatcher> matchers;
	private JLabel lblClusters = new JLabel("Clusters:");
	private JTextField txtClusters = new JTextField("20");
	private JButton btnClusters = new JButton("Cluster");
	private JButton btnDraw = new JButton("Draw");
	private JProgressBar prgBar = new JProgressBar();
	private JPanel panel = new JPanel();

	private GVM_Clustering gvmcl;
	
	
public ClusterViewer(List<AbstractMatcher> matchers) {
	super();
	this.matchers = matchers; 
	GroupLayout layout = new GroupLayout(this);
	panel.setMinimumSize(new Dimension(300, 200));
	panel.setBorder(new LineBorder(Color.black));
	txtClusters.setMinimumSize(new Dimension(50, -1));
	layout.setHorizontalGroup( layout.createParallelGroup()
			.addGroup( layout.createSequentialGroup()
					.addComponent(lblClusters)
					.addComponent(txtClusters)
					.addComponent(btnClusters)
					.addComponent(btnDraw)
					.addComponent(prgBar)
			)
			.addComponent(panel)
	);
	
	layout.setVerticalGroup( layout.createSequentialGroup()
			.addGroup( layout.createParallelGroup(Alignment.CENTER,false)
					.addComponent(lblClusters)
					.addComponent(txtClusters)
					.addComponent(btnClusters)
					.addComponent(btnDraw)
					.addComponent(prgBar)
			)
			.addComponent(panel)
	);
	
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	setLayout(layout);
	
	btnClusters.addActionListener(this);
	
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
		JOptionPane.showConfirmDialog(null, "awesome!");
		long startTime = System.currentTimeMillis();
		
		gvmcl = new GVM_Clustering(matchers, Integer.parseInt(txtClusters.getText()));
		gvmcl.addPropertyChangeListener(this);
		gvmcl.cluster();
		gvmcl.removePropertyChangeListener(this);
		prgBar.setIndeterminate(true);
	}
	
}

@Override
public void propertyChange(PropertyChangeEvent evt) {
	if( evt.getPropertyName().equals("progress") ) {
		if ( prgBar.isEnabled() ) prgBar.setValue((Integer)evt.getNewValue());
	}
}


}
