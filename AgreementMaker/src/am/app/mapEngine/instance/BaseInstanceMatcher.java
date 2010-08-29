package am.app.mapEngine.instance;

import java.util.ArrayList;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.ontology.Node;

/**
 * This class finds the same instances across the ontologies and 
 * matches the concepts which has the same instances.
 * @author Ulas
 *
 */

public class BaseInstanceMatcher extends AbstractMatcher{
	
	//
	private ArrayList<Instance> matchedSourceInstances;
	private ArrayList<Instance> matchedTargetInstances;
	
	public ArrayList<Instance> getMatchedSourceInstances() {
		return matchedSourceInstances;
	}

	public ArrayList<Instance> getMatchedTargetInstances() {
		return matchedTargetInstances;
	}
	//
	
	//
	private InstanceList instanceListSource;
	private InstanceList instanceListTarget;
	
	public InstanceList getInstanceListSource() {
		return instanceListSource;
	}

	public void setInstanceListSource(InstanceList instanceListSource) {
		this.instanceListSource = instanceListSource;
	}

	public InstanceList getInstanceListTarget() {
		return instanceListTarget;
	}

	public void setInstanceListTarget(InstanceList instanceListTarget) {
		this.instanceListTarget = instanceListTarget;
	}

	
	//Constructor
	public BaseInstanceMatcher() throws Exception {
		// warning, param is not available at the time of the constructor (?)
		super();
		needsParam = false;
		alignProp = false;
		matchedSourceInstances = new ArrayList<Instance>();
		matchedTargetInstances = new ArrayList<Instance>();
		instanceListSource = new InstanceList();
		instanceListTarget = new InstanceList();
	}
	
	//Description of Algorithm
	public String getDescriptionString() {
		return ".............\n"; 
	}
	
	protected void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		instanceListSource = Core.getInstance().getSourceOntology().getInstanceList();
		instanceListTarget = Core.getInstance().getTargetOntology().getInstanceList();
		matchInstances();
	}
	
	
	/* Algorithm functions beyond this point */
	
	/**
	 * Function aligns 2 nodes using ...:
	 */
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) throws Exception {
		String tURI, sURI;
		sURI = Core.getInstance().getSourceOntology().getURI();
		tURI = Core.getInstance().getTargetOntology().getURI();
		
		if(!source.getUri().contains(sURI) || !target.getUri().contains(tURI)){
			return new Alignment(source, target, 0, Alignment.EQUIVALENCE);
		}
		
		for(int x = 0; x < matchedSourceInstances.size(); x++){
			if( source.getLocalName().equalsIgnoreCase( matchedSourceInstances.get(x).getConcept() ) ){
				if(target.getLocalName().equalsIgnoreCase( matchedTargetInstances.get(x).getConcept() )){
					return new Alignment(source, target, 99, Alignment.EQUIVALENCE);
				}
			}
		}
		
		return new Alignment(source, target, 0, Alignment.EQUIVALENCE);
	}
	
	public void matchInstances(){
		boolean match = false;
		for (Instance s : instanceListSource.getInstanceList()){
			for (Instance t : instanceListTarget.getInstanceList()){
								
				InstanceProperty ips = s.getIP().get(0);
				InstanceProperty ipt = t.getIP().get(0);
				if(ips.getSubject() != null && !ips.getSubject().equalsIgnoreCase("nil") && ips.getSubject().equalsIgnoreCase( ipt.getSubject() )){
					matchedSourceInstances.add(s);
					matchedTargetInstances.add(t);
					match = true;
					break;
				}				
			}
			if(match) continue;
			
		}
		/*
		for (Instance s : instanceListSource.getInstanceList()){
			for (Instance t : instanceListTarget.getInstanceList()){
				
				int numIPs = s.getIP().size();
				int numIPt = t.getIP().size();//?
				
				//Check property equality
				for(InstanceProperty ips : s.getIP()){
					if( ips.getObject().equalsIgnoreCase("type") )
						continue;
					for(InstanceProperty ipt : t.getIP()){
							if( ipt.getObject().equalsIgnoreCase("type") )
								continue;
							if(ips.getObject().equalsIgnoreCase( ipt.getObject() )){                
								numIPs--;
								continue;
							}
							else{
								//-7ad47acd:127b310f080:-7ec3
				                // 919407f:127b0fdbab3:-7f7f
				                String regex = "-?[a-f0-9]{7,9}:[a-f0-9]{11}:-[a-f0-9]{4}";
				                String text = ips.getObject();
				                String text2 = ipt.getObject();
				                Pattern pattern = Pattern.compile(regex);
				                Matcher matcher = pattern.matcher(text);
				                Matcher matcher2 = pattern.matcher(text2);
				                if(matcher.find() && matcher2.find()){
				                	System.out.println("    matches PATTERN...");
				                	numIPs--;
				                	continue;
				                }	
							}
					}
				}
				
				//For a property, if the values are the same, then instances belong to the same class.
				//TODO: 0 or smaller, what to do with numIPt?
				if(numIPs <= 0){
					matchedSourceInstances.add(s);
					matchedTargetInstances.add(t);
					break;
				}
			}			
		}
		*/
	}
		
}
