package am.extension.semanticExplanation;

/**
 *
 * Class AxiomRelation takes in two input Strings and define a relation between the two. 
 * @author jjosep37@
 *
 */
public class AxiomRelation {
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public RelationType getRelation() {
		return relation;
	}
	public void setRelation(RelationType relation) {
		this.relation = relation;
	}
	
	
	public AxiomRelation(String source, String target, RelationType relation) {
		this.source = source;
		this.target = target;
		this.relation = relation;
	}

	public AxiomRelation() {
		this.source= "";
		this.target = "";
		this.relation = RelationType.NOTDEFINED;
	}


	String source, target;
	RelationType relation;
}
