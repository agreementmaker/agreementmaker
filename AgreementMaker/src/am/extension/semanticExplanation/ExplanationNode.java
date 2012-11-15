package am.extension.semanticExplanation;

import java.util.ArrayList;
import java.util.List;

public class ExplanationNode {
	double val;
	List<ExplanationNode> children;
	CombinationCriteria criteria;
	String description;

	
	public ExplanationNode(double val, List<ExplanationNode> children,
			CombinationCriteria criteria, String description) {
		this.val = val;
		this.children = children;
		this.criteria = criteria;
		this.description = description;
	}


	public ExplanationNode() {
		this.val = 0;
		this.children = new ArrayList<ExplanationNode>();
		this.criteria = CombinationCriteria.NOTDEFINED;
		this.description = "";
	}

	public void addChild(ExplanationNode node){
		this.children.add(node);
	}
	

	public double getVal() {
		return val;
	}


	public void setVal(double val) {
		this.val = val;
	}


	public List<ExplanationNode> getChildren() {
		return children;
	}


	public void setChildren(List<ExplanationNode> children) {
		this.children = children;
	}


	public CombinationCriteria getCriteria() {
		return criteria;
	}


	public void setCriteria(CombinationCriteria criteria) {
		this.criteria = criteria;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	
}
