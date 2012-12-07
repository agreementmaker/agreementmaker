package am.extension.semanticExplanation.mouseWorks;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;

import org.apache.commons.collections15.Transformer;

import am.extension.semanticExplanation.ExplanationNode;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class MostSignificantPathMenuItem<V> extends JMenuItem implements VertexMenuListener<V> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private V vertex;
    private VisualizationViewer<ExplanationNode, String> visComp;
    
    public MostSignificantPathMenuItem() {
    	super("Show Most Significant Path");
    	this.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {

    			visComp.getPickedVertexState().pick((ExplanationNode) vertex,true);
    			final List<ExplanationNode> mspList = ExplanationNode.findMostSignificantPath((ExplanationNode) vertex);
    		
    			final Transformer<ExplanationNode,Paint> mspTransformer = new Transformer<ExplanationNode,Paint>() {
    				public Paint transform(ExplanationNode i) {
    					if(mspList.contains(i)) {
    						return Color.CYAN;
    					} else {
    						return Color.GREEN;
    					}
    				}
    			};
/*    			Transformer<ExplanationNode, String> labelTransformer = new Transformer<ExplanationNode,String>() {
    				
    				@Override
    				public String transform(ExplanationNode node) {
    	
    					return String.valueOf(node.getVal());
    				}
    				
    			};

    			visComp.getRenderContext().setVertexLabelTransformer(labelTransformer);
    			visComp.getRenderer().getVertexLabelRenderer().setPosition(Position.E);*/
    			visComp.getRenderContext().setVertexFillPaintTransformer(mspTransformer);
    			visComp.repaint();
    		}
    	});
    }
	@Override
	public void setVertexAndView(V v, VisualizationViewer visView) {
        this.vertex = v;
        this.visComp = visView;
        this.setText("Most Significant Path");
		
	}

}
