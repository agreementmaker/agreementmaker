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

public class LeastSignificantPathMenuItem<V> extends JMenuItem implements VertexMenuListener<V> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private V vertex;
    private VisualizationViewer<ExplanationNode, String> visComp;
    
    public LeastSignificantPathMenuItem() {
    	super("Show Most Significant Path");
    	this.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {

    			visComp.getPickedVertexState().pick((ExplanationNode) vertex,true);
    			final List<ExplanationNode> lspList ;
    			ExplanationNode node = (ExplanationNode) vertex;
    			
    			if(node.isUniversalUse()){
    				lspList = ExplanationNode.findMinSPGeneral(node);
    			}
    			else{
        			lspList = ExplanationNode.findLeastSignificantPath(node);
    			}
    		
    			final Transformer<ExplanationNode,Paint> mspTransformer = new Transformer<ExplanationNode,Paint>() {
    				public Paint transform(ExplanationNode i) {
    					if(lspList.contains(i)) {
    						return Color.PINK;
    					} else {
    						return Color.GREEN;
    					}
    				}
    			};
    			
    			visComp.getRenderContext().setVertexFillPaintTransformer(mspTransformer);
    			visComp.repaint();
    		}
    	});
    }
	@Override
	public void setVertexAndView(V v, VisualizationViewer visView) {
        this.vertex = v;
        this.visComp = visView;
        this.setText("Least Significant Path");
		
	}

}
