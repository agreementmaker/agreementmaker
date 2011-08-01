package am.extension;

import java.util.List;

import org.apache.log4j.Logger;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;

/**
 * 
 * @author cosmin
 *
 */
public class MyTestMatcher extends AbstractMatcher {

	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		Logger log = Logger.getLogger(this.getClass());
		//log.info("Classes with more than one child:");
		
		
		for( Node source : sourceOntology.getClassesList() ) {
	
			List<Node> childList = source.getChildren();
	
			if( childList.size() == 0 ) continue;
			
			log.info("Node localname: " + source.getLocalName());
			
			log.info("This node has " + childList.size() + " children.");
			for( int i = 0; i < childList.size(); i++ ) {
				Node currentChild = childList.get(i);
				log.info(i + ". " + currentChild.getLocalName() );
			}
			
			log.info("");
		}
		
	}
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes) throws Exception {
		
		return null;
	}
	
}
