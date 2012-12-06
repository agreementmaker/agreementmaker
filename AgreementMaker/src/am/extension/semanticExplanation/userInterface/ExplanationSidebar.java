package am.extension.semanticExplanation.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import org.apache.commons.collections15.Transformer;

import am.extension.semanticExplanation.CombinationCriteria;
import am.extension.semanticExplanation.ExplanationNode;
import am.extension.semanticExplanation.SubTreeLayout;
import am.extension.semanticExplanation.mouseWorks.MyMouseMenus;
import am.extension.semanticExplanation.mouseWorks.PopupVertexEdgeMenuMousePlugin;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.MouseListenerTranslator;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class ExplanationSidebar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4516195051938561775L;

    private static  JLabel nodeDescriptionValue;
    private static JLabel criteriaLabel;
    private static JLabel valueLabel;
    private static JPanel panel = new JPanel(new BorderLayout());
    private static JPanel labelPanel = new JPanel(new GridLayout(5,1));
	Border blackline;
	Layout<ExplanationNode, String> layout;
	VisualizationViewer<ExplanationNode, String> vv;
	JTabbedPane explanationPane;
	JScrollPane scrollPane;
	
	private Component oldComponent;
	public static DelegateTree<ExplanationNode,String> tree;
	
	   private static class VertexPaintTransformer implements Transformer<ExplanationNode,Paint> {

	        private final PickedInfo<ExplanationNode> pi;

	        VertexPaintTransformer ( PickedInfo<ExplanationNode> pi ) { 
	            super();
	            if (pi == null)
	                throw new IllegalArgumentException("PickedInfo instance must be non-null");
	            this.pi = pi;
	        }

	        @Override
	        public Paint transform(ExplanationNode i) {
	            Color p = null;
	            //Edit here to set the colours as reqired by your solution
	                p = Color.GREEN;
	            //Remove if a selected colour is not required
	            if ( pi.isPicked(i)){
	                p = Color.yellow;
	            }
	            return p;
	        }
	    }

	   
	public ExplanationSidebar() {
		init();
	}

	public void init() {
		setLayout(new GridLayout(1,1));

		explanationPane=new JTabbedPane();
		add(explanationPane);

		blackline = BorderFactory.createLineBorder(Color.BLACK);
		labelPanel.add(new JPanel());
		labelPanel.add(new JPanel());
		//tree field must be set from the Matcher method!!!
		if(tree != null) {
			layout = new SubTreeLayout(tree);
			layout.setSize(new Dimension(200,350));
			vv = new VisualizationViewer<ExplanationNode, String>(layout);
			vv.setPreferredSize(new Dimension(300,400)); //Sets the viewing area size
			Transformer<ExplanationNode, String> labelTransformer = new Transformer<ExplanationNode,String>() {
	
				@Override
				public String transform(ExplanationNode node) {
					return String.valueOf(node.getVal());
				}
				
			};
			final Transformer<ExplanationNode,Paint> vertexPaint = new Transformer<ExplanationNode,Paint>() {
				public Paint transform(ExplanationNode i) {
				return Color.GREEN;
				}
			};
			vv.getRenderContext().setVertexLabelTransformer(labelTransformer);
			vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
			vv.getRenderer().getVertexLabelRenderer().setPosition(Position.E);
			Transformer<ExplanationNode, String> toolTipTransformer = new Transformer<ExplanationNode, String>() {
	
				@Override
				public String transform(ExplanationNode node) {
					String nodeDesc = node.getDescription()+": "+node.getVal();
					if(!node.getCriteria().toString().equals(CombinationCriteria.NOTDEFINED.toString())) {
						nodeDesc+="\nChlidren joined by: "+node.getCriteria();
					}
					return nodeDesc;
				}
				
			};
			vv.setVertexToolTipTransformer(toolTipTransformer );
			
			final DefaultModalGraphMouse<ExplanationNode, String> graphMouse = new DefaultModalGraphMouse<ExplanationNode, String>();
		
			graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
			
			GraphMouseListener<ExplanationNode> mygel = new GraphMouseListener<ExplanationNode>() {
	
				private ExplanationNode previousNode;
	
				@Override
				public void graphClicked(final ExplanationNode node, MouseEvent me) {
					vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
	
					if(me.getButton() == MouseEvent.BUTTON1) {
						if(previousNode != null) {
							vv.getPickedVertexState().pick(previousNode, false);
						}
						vv.getPickedVertexState().pick(node, true);
						 vv.getRenderContext().setVertexFillPaintTransformer(new VertexPaintTransformer(vv.getPickedVertexState()));
	
						System.out.println("left click");
						System.out.println("Clicked " + node.getDescription());	
						if(nodeDescriptionValue != null) {
							labelPanel.remove(nodeDescriptionValue);
						}
						nodeDescriptionValue = new JLabel("Description: "+node.getDescription());
						nodeDescriptionValue.setOpaque(true);
						labelPanel.add(nodeDescriptionValue);
						if(criteriaLabel != null) {
							labelPanel.remove(criteriaLabel);
						}
						if(!node.getCriteria().toString().equals(CombinationCriteria.NOTDEFINED.toString())) {
							criteriaLabel = new JLabel("Method: "+node.getCriteria().toString());
							criteriaLabel.setOpaque(true);
							labelPanel.add(criteriaLabel);
						} else {
							criteriaLabel = new JLabel("Reached End node!");
							criteriaLabel.setOpaque(true);
							labelPanel.add(criteriaLabel);							
						}
						if(valueLabel != null) {
							labelPanel.remove(valueLabel);
						}
						valueLabel = new JLabel("Value: "+node.getVal());
						valueLabel.setOpaque(true);
						labelPanel.add(valueLabel);
						labelPanel.setBorder(blackline);
						panel.add(labelPanel,BorderLayout.NORTH);
	/*					frame.pack();
						frame.setVisible(true);*/
					} else if(me.getButton() == MouseEvent.BUTTON3) {
						System.out.println("right click");
						System.out.println("Clicked " + node.getDescription());		
						
						PopupVertexEdgeMenuMousePlugin<ExplanationNode, String> myPlugin = new PopupVertexEdgeMenuMousePlugin<ExplanationNode,String>();
						JPopupMenu vertexMenu = new MyMouseMenus.VertexMenu();
						myPlugin.setVertexPopup(vertexMenu);
						graphMouse.add(myPlugin);
					}
					previousNode = node;
				}
	
				@Override
				public void graphPressed(ExplanationNode arg0, MouseEvent arg1) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void graphReleased(ExplanationNode arg0, MouseEvent arg1) {
					// TODO Auto-generated method stub
					
				}
	
			};
			
	
			vv.setGraphMouse(graphMouse);
			vv.addKeyListener(graphMouse.getModeKeyListener());
			vv.addMouseListener(new MouseListenerTranslator<ExplanationNode, String>(mygel, vv));
			panel.add(vv,BorderLayout.CENTER);
			scrollPane=new JScrollPane(panel);
			scrollPane.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Explanation"));
			explanationPane.add(scrollPane);
			explanationPane.addTab("Semantic Explanation Information",null,scrollPane,"Explanation of the selected mapping");
		}
	}
	
	public Component getOldComponent() {
		return oldComponent;
	}
	public void setOldComponent(Component oldComponent) {
		this.oldComponent = oldComponent;
	}
	
}
