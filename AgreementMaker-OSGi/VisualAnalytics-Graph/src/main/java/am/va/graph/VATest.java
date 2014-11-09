package am.va.graph;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.va.graph.VAVariables.ontologyType;

/**
 * Testing models, in VASyncData
 * 
 * @author Yiting
 * 
 */
public class VATest {
	private Node node;
	private MatchingTask currentTask;

	public VATest() {

	}

	public VATest(Node node) {
		this.node = node;
	}

	public void setNode(Node n) {
		this.node = n;
	}

	public MatchingTask getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(MatchingTask currentTask) {
		this.currentTask = currentTask;
	}

	public Node getNode() {
		return node;
	}

	public void showNodeProperties() {
		boolean isClass = node.isClass();
		if (isClass) {
			System.out.println("This node is a class node");
		} else {
			System.out.println("This node is a property node");
			try {
				String domain = node.getPropertyDomain().getLocalName();
				String range = node.getPropertyRange().getLocalName();
				System.out.println("domain=" + domain + ", range=" + range);
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}

	/**
	 * Given a domain name, find all properties with this domain
	 * 
	 * @param domain
	 */
	public ArrayList<String> showNodePropertyLists(String domain, ontologyType type) {
		ArrayList<String> cluster = new ArrayList<String>();
		if (currentTask == null) {
			System.out.println("Error! Current task NULL");
			return cluster;
		}
		MatcherResult mr = currentTask.matcherResult;
		Ontology source = mr.getSourceOntology();
		Ontology target = mr.getTargetOntology();
		SimilarityMatrix smProperty = mr.getPropertiesMatrix();
		Mapping map[] = null;
		Node matchingNode = null;
		double sim = 0.0;
		// Ontology target = mr.getTargetOntology();

		// property list of source ontology
		System.out.println(domain);
		List<Node> propertyList;
		if (type == ontologyType.Source) {
			propertyList = source.getPropertiesList();
		} else {
			propertyList = target.getPropertiesList();
		}
		for (Node n : propertyList) {
			try {
				// if (type == ontologyType.Source) {
				map = smProperty.getRowMaxValues(n.getIndex(), 1);
				// } else {
				// map = smProperty.getColMaxValues(n.getIndex(), 1);
				// }
				if (map != null) {
					matchingNode = map[0].getEntity2();
					sim = map[0].getSimilarity();
				} else {
					continue;
				}
				if (n.getPropertyDomain().getLocalName().equals(domain)) {
					cluster.add(n.getLocalName());
					System.out.println("node=" + n.getLocalName() + ", domain=" + domain + ", range="
							+ n.getPropertyRange().getLocalName() + ", match=" + matchingNode.getLocalName() + ", sim="
							+ sim);
				}
			} catch (Exception e) { // null pointer exception will happen
				//System.out.println(e.getMessage());
			}
		}
		return cluster;
	}
}
