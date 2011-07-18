/**    ________________________________________
 * ___/ Copyright Notice / Warranty Disclaimer \________________
 *
 * @copyright { Copyright (c) 2010
 * Advances in Information Systems Laboratory at the
 * University of Illinois at Chicago
 * All Rights Reserved. }
 * 
 * @disclaimer {
 * This work is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. }
 * 
 */

package am.userInterface.canvas2.layouts.legacylayout;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.userInterface.canvas2.Canvas2;
import am.userInterface.canvas2.graphical.GraphicalData;
import am.userInterface.canvas2.graphical.MappingData;
import am.userInterface.canvas2.layouts.LegacyLayout;
import am.userInterface.canvas2.nodes.LegacyMapping;
import am.userInterface.canvas2.nodes.LegacyNode;
import am.userInterface.canvas2.popupmenus.CreateMappingMenu;
import am.userInterface.canvas2.popupmenus.DeleteMappingMenu;
import am.userInterface.canvas2.utility.Canvas2Edge;
import am.userInterface.canvas2.utility.Canvas2Vertex;
import am.userInterface.sidebar.provenance.ProvenanceSidebar;
import am.userInterface.sidebar.vertex.VertexDescriptionPane;
import am.utility.DirectedGraphEdge;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


/**
 * This class is meant to keep all the mouse interactions of the LegacyLayout.
 * It has been created in order to break up the LegacyLayout class, which is getting 
 * too big for working with.
 * 
 * The mouse interactions meant to be taken care of by this class are:
 * 
 * mouseClicked( MouseEvent e )
 * mouseMoved( MouseEvent e )
 *  
 * @author Cosmin Stroe
 * @date Monday, December 13th, 2010
 *
 */
public class LegacyLayoutMouseHandler {
	
	LegacyLayout layout;
	
	public LegacyLayoutMouseHandler(LegacyLayout lay) {	layout = lay; }
	
	
	/**
	 ****** mouseClicked(MouseEvent e);
	 * 
	 * Process mouse click events.  This method has multiple functions:
	 * 
	 * 1) On single left click:
	 * 		a) select the current node, or
	 * 		b) if (Control or Shift are held down) add the node to the current selection, or
	 * 		c) if clicking in an empty area 
	 * 			i) if single mapping view is enabled, disable it
	 * 			ii) clear any selected nodes
	 * 		d) if the selection list contains nodes from one ontology and we click a node
	 * 		   from the opposite ontology, bring up the create mapping menu
	 *  
	 * 
	 * 2) On double left click:
	 * 		a) Enable single mapping view.
	 * 
	 * 3) On right click:
	 * 		a) if right clicking over a node, bring up the delete mapping menu.
	 * 
	 * @param e
	 */
	
	public void mouseClicked( MouseEvent e ) {
		
		// BUTTON1 = Left Click Button, BUTTON2 = Middle Click Button, BUTTON3 = Right Click Button

		// The next 3 lines are important.  Do not remove.  They give a nice feel to the interaction between the popup menus and the canvas.
		Canvas2Vertex hoverBefore = layout.getHoveringOver();
		updateHoveringOver(e.getPoint());
		if( hoverBefore != layout.getHoveringOver() ) return; // prevent deselecting nodes on the first click
		
		// commonly used variables
		Canvas2 vizpanel = layout.getVizPanel();
		Canvas2Vertex hoveringOver = layout.getHoveringOver();
		ArrayList<LegacyNode> selectedNodes = layout.getSelectedNodes();
		
		Graphics g = vizpanel.getGraphics();   // used for any redrawing of nodes
		ArrayList<Canvas2Vertex> visibleVertices = layout.getVizPanel().getVisibleVertices();

		Logger log = Logger.getLogger(this.getClass());
		if( Core.DEBUG ) log.setLevel(Level.DEBUG);

		// if we have an active popup menu, cancel it
		if( layout.isPopupMenuActive() ) { 
			layout.cancelPopupMenu(g);
		}

		
		
		// only process mouse clicks if there's not a popup menu active
		switch( e.getButton() ) {

		// because of the way Java (and most any platform) handles the difference between single and double clicks,
		// the single click action must be "complementary" to the double click action, as when you double click a 
		// single click is always fired just before the double click is detected.  
		// There is no way around this.  A single click event will *always* be fired just before a double click.

		// So then:
		//		- LEFT button SINGLE click = select NODE (or deselect if clicking empty space)
		//		- LEFT button DOUBLE click = line up two nodes by their mapping (do nothing if it's empty space)

		// Jan 29, 2010 - Cosmin
		//   Ok now, we are adding menu support:
		//      1. User must single left click to select a node in one ontology graph, in order to select that node.
		//      2. User must single left click a node in the OTHER ontology graph in order to cause a menu to come up.
		//         If the user clicks a node in the same ontology, this new node becomes the selected node.

		//      These actions should work with MULTIPLE selections (using the Control key).

		// Feb 13th, 2010 - Cosmin
		//   Adding rightclick menu for deleting mappings.

		// June 17th, 2010 - Cosmin
		//   Added the SingleMappingView to replace SMO.  Activated by doubleclicking a node.
		
		// Dec 13th, 2010 - Cosmin
		//   Worked out all the bugs in the interface.  It feels and looks really nice now.

		case MouseEvent.BUTTON1:
			if( e.getClickCount() == 2 ) {  // double click with the left mouse button
				if( Core.DEBUG) log.debug("Double click with the LEFT mouse button detected.");
				//do stuff

				if( layout.getHoveringOver() != null && layout.isViewActive(LegacyLayout.VIEW_SINGLE_MAPPING) != true ) {
					layout.enableSingleMappingView();
					vizpanel.repaint();
				}

			} else if( e.getClickCount() == 1 ) {  // single click with left mouse button
				if( layout.isViewActive(LegacyLayout.VIEW_SINGLE_MAPPING) == true ) {
					// if we don't click on anything, cancel the single mapping view
					// restore the previous visibility of the nodes and edges

					if( layout.getHoveringOver() == null ) {
						layout.disableSingleMappingView();
						layout.getVizPanel().repaint();
					} else {
						// we doubleclicked on another node.
						layout.disableSingleMappingView();
						
						// move the viewpane to the new node
						//vizpanel.getScrollPane().scrollRectToVisible( new Rectangle(0, vizpanel.getScrollPane().getSize().height, 1, 1) );
						//new Point(vizpanel.getScrollPane().getViewport().getLocation().x - vizpanel.getScrollPane().getViewport().getWidth()/2, 
							//	hoveringOver.getBounds().y - vizpanel.getScrollPane().getViewport().getHeight()/2 ));  // TODO: Check canvas boundaries when moving view.
						if( hoveringOver.getGraphicalData().ontologyID == layout.getRightOntologyID() ) {
							// move towards the left as much as we can
							vizpanel.getScrollPane().getViewport().setViewPosition(
									new Point( Math.max(hoveringOver.getBounds().x - vizpanel.getScrollPane().getViewport().getWidth() + hoveringOver.getBounds().width + 20, 0), 
									Math.max(hoveringOver.getBounds().y - vizpanel.getScrollPane().getViewport().getHeight()/2 ,0) ));  // TODO: Check canvas boundaries when moving view.
						} else if ( hoveringOver.getGraphicalData().ontologyID == layout.getLeftOntologyID() ) {
							// move towards the right as much as we can
							vizpanel.getScrollPane().getViewport().setViewPosition(
									new Point( Math.max(hoveringOver.getBounds().x - hoveringOver.getBounds().width - 20, 0), 
									Math.max(hoveringOver.getBounds().y - vizpanel.getScrollPane().getViewport().getHeight()/2 ,0) ));  // TODO: Check canvas boundaries when moving view.
						} else {
							vizpanel.getScrollPane().getViewport().setViewPosition(
								new Point( Math.max(hoveringOver.getBounds().x - vizpanel.getScrollPane().getViewport().getWidth()/2, 0), 
								Math.max(hoveringOver.getBounds().y - vizpanel.getScrollPane().getViewport().getHeight()/2 ,0) ));  // TODO: Check canvas boundaries when moving view.
						}
						
						//System.out.print( "Moving viewport to: " + hoveringOver.getBounds().toString() );
						hoveringOver = null;
						vizpanel.repaint();
					}
				} else if( hoveringOver == null ) {
					// we have clicked in an empty area, clear all the selected nodes
					Iterator<LegacyNode> nodeIter = selectedNodes.iterator();
					while( nodeIter.hasNext() ) {
						LegacyNode selectedNode = nodeIter.next();
						selectedNode.setSelected(false); // deselect the node
						if( visibleVertices.contains( (Canvas2Vertex) selectedNode ) ) {
							// redraw only if it's currently visible
							//selectedNode.clearDrawArea(g);
							selectedNode.draw(g);
						}
					}
					selectedNodes.clear();
				} else {
					// user clicked over a node.

					// is it a node in the OTHER ontology?
					if( layout.getSelectedNodesOntology() != Core.ID_NONE && 
						layout.getSelectedNodesOntology() != hoveringOver.getGraphicalData().ontologyID ) {
						// yes it is in the other ontology
						// bring up the Mapping Popup Menu, so the user can make a mapping
						CreateMappingMenu menuCreate = new CreateMappingMenu( layout );
						menuCreate.show( vizpanel, e.getX(), e.getY());
						menuCreate.addPopupMenuListener(layout);
						layout.setPopupMenuActive(true);
					} else {
						// the nodes are in the same ontology
						// we either add to the selection, or clear it and select the node that was just clicked
						if( e.isControlDown() ) {
							// if the user control clicked (CTRL+LEFTCLICK), we have to add this node to the list of selected nodes.
							if( selectedNodes.contains(hoveringOver) ) { // if it already is in the list, remove it
								selectedNodes.remove(hoveringOver);
								hoveringOver.setSelected(false);
							} else { // it's not in the list already, add it
								hoveringOver.setSelected(true);
								selectedNodes.add((LegacyNode) hoveringOver);
							}

							//hoveringOver.clearDrawArea(g);
							hoveringOver.draw(g);
						} else { // control is not pressed, clear any selections that there may be, and select single node

							Iterator<LegacyNode> nodeIter = selectedNodes.iterator();
							while( nodeIter.hasNext() ) {
								LegacyNode selectedNode = nodeIter.next();
								selectedNode.setSelected(false); // deselect the node
								if( visibleVertices.contains( (Canvas2Vertex) selectedNode ) ) {
									// redraw only if it's currently visible
									//selectedNode.clearDrawArea(g);
									selectedNode.draw(g);
								}
							}
							selectedNodes.clear();

							// select single node
							hoveringOver.setSelected(true);
							selectedNodes.add( (LegacyNode)hoveringOver);
							//hoveringOver.clearDrawArea(g);
							hoveringOver.draw(g);
						}

						//insert the propegation data if it exits
						if(Core.getUI().getUISplitPane().getRightComponent() instanceof ProvenanceSidebar){
							if(selectedNodes.size()>0)
							{
								LegacyNode selected=selectedNodes.get(0);
								ArrayList<LegacyMapping> mappingList=selected.getMappings();
								String provenance="";
								for (LegacyMapping l : mappingList ){
									MappingData md=(MappingData)l.getObject();
									provenance+=md.alignment.getProvenance();
									provenance+="\n";
								}
								ProvenanceSidebar psb=(ProvenanceSidebar) Core.getUI().getUISplitPane().getRightComponent();
								psb.setProvenance(provenance);
							}
						}
						
						// Populate the annotation box.
						if( hoveringOver.getGraphicalData().r != null ) {

							if( hoveringOver.getGraphicalData().r.canAs( OntClass.class ) ) {
								// we clicked on a class
								OntClass currentClass = (OntClass) hoveringOver.getGraphicalData().r.as(OntClass.class);
								StmtIterator i = currentClass.listProperties();
								
								String annotationProperties = new String();
								while( i.hasNext() ) {
									Statement s = (Statement) i.next();
									Property p = s.getPredicate();

									if( p.canAs( AnnotationProperty.class ) ) {
										// this is an annotation property
										RDFNode obj = s.getObject();
										if( obj.canAs(Literal.class) ) {
											Literal l = (Literal) obj.as(Literal.class);
											annotationProperties += p.getLocalName() + ": " + l.getString() + "\n";
										} else {
											annotationProperties += p.getLocalName() + ": " + obj.toString() + "\n";
										}
									}
								}

								
								String propString = new String();
								// get a list of the properties.  TODO: Make this easier to work with.
								try {
									// we need the Ontology to map from OntResource to Node
									Ontology o = Core.getInstance().getOntologyByID(hoveringOver.getGraphicalData().ontologyID);
									// map from the OntResource we are hoveringOver to the Node that it represents
									Node classNode = o.getNodefromOntResource(hoveringOver.getGraphicalData().r, alignType.aligningClasses);
									// get a list of the properties that are declared for this class
									ArrayList<Node> properties = classNode.getClassProperties();
									// iterate over the property list and build a string representation.
									Iterator<Node> propIter = properties.iterator();
									while( propIter.hasNext() ) {
										Node currentProperty = propIter.next();
										propString += currentProperty.getLocalName() + "\n";
									}
								} catch (Exception e1) {
									// Could not convert from OntResource to Node, give up.
									//e1.printStackTrace();
								}
								
								// if we have listed some properties, add them to the main string
								if( !propString.equals("") ) {
									annotationProperties += "\n\nProperties:\n\n" + propString;
								}
								
								
								
								if(Core.getUI().getUISplitPane().getRightComponent() instanceof VertexDescriptionPane){
									// update the text box.
									VertexDescriptionPane vdp = (VertexDescriptionPane) Core.getUI().getUISplitPane().getRightComponent();
									if( hoveringOver.getGraphicalData().ontologyID == layout.getLeftOntologyID() ) {
										vdp.setSourceAnnotations(annotationProperties);
									} else {
										vdp.setTargetAnnotations(annotationProperties);
									}
								
								
								
								
									// populate the individuals list
									String individuals = new String();
									ExtendedIterator<?> indiIter = currentClass.listInstances(true);
									int indicount = 1;
									while( indiIter.hasNext() ) {
										Individual indi = (Individual) indiIter.next();
										if( indi.isAnon() ) {
											individuals += indicount + ". Anonymous Individual (" + indi.getId() + ")\n";
											indicount++;
										} else {
											individuals += indicount + ". " + indi.getLocalName() + "\n";
											indicount++;
										}
									}
									
									// try to deal with improperly declared individuals.
									if( individuals.equals("") ) {
										OntModel mod = (OntModel) currentClass.getModel();
									
									
										List<Statement> ls = mod.listStatements(null , mod.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), mod.getResource(currentClass.getLocalName())).toList();
										
										Iterator<Statement> lsiter = ls.iterator();
										int k = 1;
										while( lsiter.hasNext() ) {
											Statement s = lsiter.next();
											OntResource indi = s.getSubject().as(OntResource.class);
											if( indi.isAnon() ) {
												individuals += k + ". Anonymous Individual (" + indi.getId() + ")\n";
												k++;
											} else {
												individuals += k + ". " + indi.getLocalName() + "\n";
												k++;
											}
											
										}
									}
								
								
								
									if( hoveringOver.getGraphicalData().ontologyID == layout.getLeftOntologyID() ) {
										vdp.setSourceIndividuals(individuals);
									} else {
										vdp.setTargetIndividuals(individuals);
									}
								}
								
							} else if( hoveringOver.getGraphicalData().r.canAs( OntProperty.class)) {
								// we clicked on a property
								
								OntProperty currentProperty = (OntProperty) hoveringOver.getGraphicalData().r.as(OntProperty.class);
								StmtIterator i = currentProperty.listProperties();
								String annotationProperties = new String();
								while( i.hasNext() ) {
									Statement s = (Statement) i.next();
									Property p = s.getPredicate();

									if( p.canAs( AnnotationProperty.class) ) {
										RDFNode obj = s.getObject();
										if( obj.canAs( Literal.class)) {
											Literal l = (Literal) obj.as(Literal.class);
											annotationProperties += p.getLocalName() + ": " + l.getString() + "\n";
										} else {
											annotationProperties += p.getLocalName() + ": " + obj.toString() + "\n";
										}
									}
								}
								
								// The domain of a property
								String domString = new String();
								// get a list of the properties.  TODO: Make this easier to work with.
								try {
									// we need the Ontology to map from OntResource to Node
									Ontology o = Core.getInstance().getOntologyByID(hoveringOver.getGraphicalData().ontologyID);
									// map from the OntResource we are hoveringOver to the Node that it represents
									Node propertyNode = o.getNodefromOntResource(hoveringOver.getGraphicalData().r, alignType.aligningProperties);
									// get a list of the properties that are declared for this class
									OntResource domNode = propertyNode.getPropertyDomain();
									// iterate over the property list and build a string representation.
									
									domString += domNode.getLocalName() + "\n";
									
								} catch (Exception e1) {
									// Could not convert from OntResource to Node, give up.
									//e1.printStackTrace();
								}
								
								// if we have listed some properties, add them to the main string
								if( !domString.equals("") ) {
									annotationProperties += "Property Domain: " + domString;
								}
								
								
								// The range of a property
								String rangeString = new String();
								// get a list of the properties.  TODO: Make this easier to work with.
								try {
									// we need the Ontology to map from OntResource to Node
									Ontology o = Core.getInstance().getOntologyByID(hoveringOver.getGraphicalData().ontologyID);
									// map from the OntResource we are hoveringOver to the Node that it represents
									Node propertyNode = o.getNodefromOntResource(hoveringOver.getGraphicalData().r, alignType.aligningProperties);
									// get a list of the properties that are declared for this class
									OntResource domNode = propertyNode.getPropertyRange();
									// iterate over the property list and build a string representation.
									
									rangeString += domNode.getLocalName() + "\n";
									
								} catch (Exception e1) {
									// Could not convert from OntResource to Node, give up.
									//e1.printStackTrace();
								}
								
								// if we have listed some properties, add them to the main string
								if( !domString.equals("") ) {
									annotationProperties += "Property Range: " + rangeString;
								}
								
								if(Core.getUI().getUISplitPane().getRightComponent() instanceof VertexDescriptionPane){
									VertexDescriptionPane vdp = (VertexDescriptionPane) Core.getUI().getUISplitPane().getRightComponent();
									if( hoveringOver.getGraphicalData().ontologyID == layout.getLeftOntologyID() ) {
										vdp.setSourceAnnotations(annotationProperties);
									} else {
										vdp.setTargetAnnotations(annotationProperties);
									}
								}
							}
						}  // end of populate the annotation box.


					}
				}

			}
			break;

		/*************************************************** MIDDLE MOUSE BUTTON ******************************************/
		case MouseEvent.BUTTON2:
			if( e.getClickCount() == 2 ) {
				// double click with the middle mouse button.
				log.debug("Double click with the MIDDLE mouse button detected.");
				//do stuff
			} else if( e.getClickCount() == 1 ) {
				// middle click, print out debugging info
				if( hoveringOver != null ) { // relying on the hover code in MouseMove
					log.debug("\nResource: " + hoveringOver.getObject().r + 
							"\nHashCode: " + hoveringOver.getObject().r.hashCode());
					log.debug("\nPosition" + e.getPoint().toString() );

				}
				//log.debug("Single click with the MIDDLE mouse button detected.");
			}
			break;

		/*************************************************** RIGHT CLICK **************************************************/
		case MouseEvent.BUTTON3:
			if( e.getClickCount() == 2 ) {
				// double click with the right mouse button.
				if( Core.DEBUG ) log.debug("Double click with the RIGHT mouse button detected.");
				//do stuff
			} else if( e.getClickCount() == 1 ) {
				// single right click, bring up delete menu
				if( hoveringOver != null ) {
					DeleteMappingMenu menuDelete = new DeleteMappingMenu( layout, hoveringOver, hoveringOver.getMappings() );
					menuDelete.show( vizpanel, e.getX(), e.getY());
					menuDelete.addPopupMenuListener(layout);
					layout.setPopupMenuActive(true);
				}

				if( Core.DEBUG ) log.debug("Single click with the RIGHT mouse button detected.");
			}
			break;
		}

		g.dispose(); // dispose of this graphics element, we don't need it anymore
	}
	
	
	
	public void mouseMoved(MouseEvent e)    {

		if( layout.isPopupMenuActive() ) return; // don't mess with anything while there's a popup menu active.
		
		// Setup our commonly used variables.
		Canvas2 vizpanel = layout.getVizPanel();
		if( vizpanel == null ) return; // viz panel is not set yet.
		Canvas2Vertex hoveringOver = layout.getHoveringOver();
		Graphics g = vizpanel.getGraphics();
		
		boolean hoveringOverEmptySpace = updateHoveringOver(e.getPoint());
		
		if( hoveringOverEmptySpace && hoveringOver != null) {
			// clear the hover (moving from hovering over a node to empty space)
			hoveringOver.setHover(false);
			//hoveringOver.clearDrawArea(g);
			hoveringOver.draw(g);
			
			hoveringOver = null;
			layout.setHoveringOver(null);
		}
		
		g.dispose();
		
		
	}
	
	/**
	 * Update the hoveringOver variable with the current mouse position.
	 * @param mousePosition
	 * @return true if we are hovering over something, false if we're hovering over nothing (empty space)
	 */
	public boolean updateHoveringOver(Point mousePosition) {
		if( layout.getVizPanel() == null ) return false; // no viz panel set
		Graphics g = layout.getVizPanel().getGraphics();
		
		boolean hoveringOverEmptySpace = true;
		for( Canvas2Vertex vertex : layout.getVizPanel().getVisibleVertices() ) {
			if( vertex instanceof LegacyNode )    // we only care about legacy nodes (for now)
			if( vertex.contains(mousePosition) ) {
				
				// we are hovering over vertex
				hoveringOverEmptySpace = false;
				// first, remove the hover from the last element we were hovering over
				if( layout.getHoveringOver() == vertex ) {
					// we are still hoovering over this element, do nothing
					break;
				} 
				
				if( layout.getHoveringOver() != null ) {
					// we had been hovering over something, but now we're not
					Canvas2Vertex hoveringOver = layout.getHoveringOver();
					layout.getHoveringOver().setHover(false);
					layout.setHoveringOver(null);
					//hoveringOver.clearDrawArea(g);
					if( !layout.isPopupMenuActive() ) hoveringOver.draw(g);
				}
				  
				layout.setHoveringOver(vertex); // update the layout
				vertex.setHover(true);
				//hoveringOver.clearDrawArea(g);
				
				// don't redraw over a popupmenu
				if( layout.isPopupMenuActive() ) { break; }
				
				// redraw all the edges connected to this node. (only if they are visible)
				Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeInIter = vertex.edgesInIter();
				while( edgeInIter.hasNext() ) { 
					Canvas2Edge currentEdge = (Canvas2Edge) edgeInIter.next();
					if( !currentEdge.getObject().visible ) continue;
					Canvas2Vertex originNode = (Canvas2Vertex) currentEdge.getOrigin();
					if( originNode.getObject().visible )
						currentEdge.draw(g); 
				}
				Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeOutIter = vertex.edgesOutIter();
				while( edgeOutIter.hasNext() ) {
					Canvas2Edge currentEdge = (Canvas2Edge) edgeOutIter.next();
					if( !currentEdge.getObject().visible ) continue;
					Canvas2Vertex destinationNode = (Canvas2Vertex) currentEdge.getDestination();
					if( destinationNode.getObject().visible )
						currentEdge.draw(g);
				}
				
				
				vertex.draw(g);
				break;
				
			}
		}
		
		if( hoveringOverEmptySpace && layout.getHoveringOver() != null ) {
			// clear the hover, we are now hovering over empty space
			Canvas2Vertex hoveringOver = layout.getHoveringOver();
			hoveringOver.setHover(false);
			layout.setHoveringOver(null);
		}
		return hoveringOverEmptySpace;
	}
}
