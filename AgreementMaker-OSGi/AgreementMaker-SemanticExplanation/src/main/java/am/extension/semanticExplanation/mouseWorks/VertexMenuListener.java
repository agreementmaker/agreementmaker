/*
 * VertexMenuListener.java
 *
 * Created on March 21, 2007, 1:50 PM; Updated May 29, 2007
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package am.extension.semanticExplanation.mouseWorks;

import am.extension.semanticExplanation.ExplanationNode;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * Used to indicate that this class wishes to be told of a selected vertex
 * along with its visualization component context. Note that the VisualizationViewer
 * has full access to the graph and layout.
 * @author Dr. Greg M. Bernstein
 */
@SuppressWarnings("hiding")
public interface VertexMenuListener<ExplanationNode> {
    void setVertexAndView(ExplanationNode v, VisualizationViewer<ExplanationNode, String> visView);    
}