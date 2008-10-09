/*
 * Created on Oct 1, 2004
 */
package test;

import java.awt.Color;
import java.awt.Component;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import org.mindswap.pellet.jena.OWLReasoner;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.QNameProvider;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;


/**
 * An example to show how to use OWLReasoner class. This example creates 
 * a JTree that displays the class hierachy. This is a simplified version 
 * of the class tree displayed in SWOOP. 
 * 
 * usage: java ClassTree <ontology URI>
 * 
 * @author Evren Sirin
 */
public class ClassTree {
	QNameProvider qnames;
	OWLReasoner reasoner;
	
	// render the classes using qname provider
	TreeCellRenderer treeCellRenderer = new DefaultTreeCellRenderer() {
		public Component getTreeCellRendererComponent(
				JTree tree,
				Object value,
				boolean sel,
				boolean expanded,
				boolean leaf,
				int row,
				boolean hasFocus) {
			
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			
			// eahc node represents a set of classes
			Set set = (Set) node.getUserObject();
			String label = "";
			
			// a set may contain one or more elements
			if (set.size() > 1)
				label += "[";
			Iterator i = set.iterator();
			
			// get the first one and add it to the label
			Resource first = (Resource) i.next();
			label += qnames.shortForm(first.getURI());
			// add the rest (if they exist)
			while (i.hasNext()) {
				Resource c = (Resource) i.next();
				
				label += " = ";	
				label += qnames.shortForm(c.getURI());
			}
			if (set.size() > 1)
				label += "]";
			
			// show unsatisfiable concepts red
			if(!reasoner.isSatisfiable(first)) {
				setForeground(Color.RED);
			}
			
			setText(label);
			setIcon(getDefaultClosedIcon());
			
			return this;
		}
		
	};
	
	public ClassTree(String ontology) throws Exception {
		// create a reasoner
		reasoner = new OWLReasoner();
		
		// create a model for the ontology
		//ModelReader reader = new ModelReader();
		System.out.print("Reading...");
		//Model model = reader.read(ontology);
		OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read("file:/windows/D/MSProj/Ontologies/networkA.n3", null, "N3");
		System.out.println("done");
		
		// load the model to the reasoner
		System.out.print("Loading...");
		reasoner.load(model);
		System.out.println("done");
		
		// compute the classification tree
		System.out.print("Classifying...");
		reasoner.classify();
		System.out.println("done");
		
		// initialize the qname generator
		qnames = new QNameProvider();
	}
	
	public JTree getJTree() {
		// start with owl:Thing (and all its equivalent classes)
		Set set = reasoner.getEquivalentClasses(OWL.Thing);
		// equivalent classes function does not include the class 
		// itself so add it manually
		set.add(OWL.Thing);
		
		// create a tree starting with owl:Thing node as the root
		DefaultMutableTreeNode thing = createTree(set);
		
		// Find all unsatisfiable concepts, i.e classes equivalent
		// to owl:Nothing
		Set eqs = reasoner.getEquivalentClasses(OWL.Nothing);
		Iterator i = eqs.iterator();
		if (i.hasNext()) {			
			// We want to display every unsatisfiale concept as a 
			// different node in the tree
			DefaultMutableTreeNode nothing = createNode(OWL.Nothing);
			// iterate through unsatisfiable concepts and add them to
			// the tree
			while (i.hasNext()) {
				Resource sub = (Resource) i.next();
				
				DefaultMutableTreeNode node = createNode(sub);				
				nothing.add(node);
			}
			// add nothing as a child node to owl:Thing
			thing.add(nothing);
		}
		
		
		// create the tree
		JTree classTree = new JTree(new DefaultTreeModel(thing));
		classTree.setCellRenderer(treeCellRenderer);
		
		// expand everything
		for (int r = 0; r < classTree.getRowCount(); r++)
			classTree.expandRow(r);
		
		return classTree;
	}
	
	/**
	 * Create a root node for the given concepts and add child nodes for
	 * the subclasses. Return null for owl:Nothing 
	 * 
	 * @param concepts
	 * @return
	 */
	DefaultMutableTreeNode createTree(Set concepts) {
		if (concepts.contains(OWL.Nothing))
			return null;
		
		DefaultMutableTreeNode root = createNode(concepts);
		
		if (concepts.isEmpty())
			return root;
		
		// every class in the set is equvalent so we can pick any one we want
		Resource c = (Resource) concepts.iterator().next();
		
		// get only direct subclasses  
		Set subs = reasoner.getSubClasses(c, true);
		// result is a set of sets. equivalent concepts are returned inside one set 
		Iterator i = subs.iterator();
		while (i.hasNext()) {
			Set set = (Set) i.next();
			
			DefaultMutableTreeNode node = createTree(set);
			// if set contains owl:Nothing tree will be null
			if (node != null)
				root.add(node);
		}
		
		return root;
	}
	
	/**
	 * Create a TreeNode for the given class
	 * 
	 * @param entity
	 * @return
	 */
	DefaultMutableTreeNode createNode(Resource entity) {
		return new DefaultMutableTreeNode(Collections.singleton(entity));
	}
	
	/**
	 * Create a TreeNode for the given set of classes
	 * 
	 * @param entity
	 * @return
	 */
	DefaultMutableTreeNode createNode(Set set) {
		return new DefaultMutableTreeNode(set);
	}
	
	public static void main(String[] args) throws Exception {
		ClassTree tree = new ClassTree("file:/windows/D/MSProj/Ontologies/travel.owl");
		//ClassTree tree = new ClassTree("http://www.w3.org/2001/sw/WebOnt/guide-src/wine");
		//ClassTree tree = new ClassTree("http://protege.stanford.edu/plugins/owl/owl-library/not-galen.owl");
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(tree.getJTree()));
		frame.setSize(400, 1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
