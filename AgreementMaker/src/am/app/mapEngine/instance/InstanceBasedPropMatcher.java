package am.app.mapEngine.instance;

import java.util.ArrayList;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.ontology.Node;

public class InstanceBasedPropMatcher extends AbstractMatcher{

	ArrayList<InstanceProperty> ipss;	//Source instance props
	ArrayList<InstanceProperty> ipst;	//Target instance props
	
	ArrayList<InstanceProperty> mipss;	//Matched Source instance props
	ArrayList<InstanceProperty> mipst;	//Matched Target instance props
	
	public InstanceBasedPropMatcher() {
		super();
		needsParam = false;
		alignProp = true;
		ipss = new ArrayList<InstanceProperty>();
		ipst = new ArrayList<InstanceProperty>();
		mipss = new ArrayList<InstanceProperty>();
		mipst = new ArrayList<InstanceProperty>();
	}
	
	protected void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		ipss = Core.getInstance().getSourceOntology().getInstanceProperties();
		ipst = Core.getInstance().getTargetOntology().getInstanceProperties();
		matchProperties();
	}

	protected Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		
		if(typeOfNodes.equals(alignType.aligningProperties)){
			for(int i = 0; i < mipss.size(); i++){
				if(mipss.get(i).getPredicate().contains("#" + source.getLocalName())){
					if(mipst.get(i).getPredicate().contains("#" + target.getLocalName())){
						return new Alignment(source, target, 99.0, Alignment.EQUIVALENCE);
					}
				}
			}
		}
		return new Alignment(source, target, 0.0, Alignment.EQUIVALENCE);
	}
	
	public void matchProperties(){
		for(int i = 0; i < ipss.size(); i++){
			for(int j = 0; j < ipst.size(); j++){
				String ss = ipss.get(i).getSubject();
				int lis = ss.lastIndexOf("#");
				if(lis != -1) ss = ss.substring(lis, ss.length());
				
				String st = ipst.get(j).getSubject();
				int lit = st.lastIndexOf("#");
				if(lit != -1) st = st.substring(lit, st.length());
				
				
				String os = ipss.get(i).getObject();
				int lios = os.lastIndexOf("#");
				if(lios != -1) os = os.substring(lios, os.length());

				String ot = ipst.get(j).getObject();
				int liot = ot.lastIndexOf("#");
				if(liot != -1) ot = ot.substring(liot, ot.length());

				if(os.equalsIgnoreCase(ot) && ss.equalsIgnoreCase(st)){
					mipss.add(ipss.get(i));
					mipst.add(ipst.get(j));
				}
				
			}
		}
	}
	
	/*
	public void matchProperties2(){
		
		for(int i = 0; i < ipss.size(); i++){
			
			//if(ipss.get(i).propType.equals(PropType.Data) || ipss.get(i).propType.equals(PropType.Object)){
				
				for(int j = 0; j < ipst.size(); j++){
				
					//if(ipst.get(j).propType.equals(PropType.Data) || ipst.get(j).propType.equals(PropType.Object)){
						//If they belong to the same individual
						String ss = ipss.get(i).getSubject();
						int lis = ss.lastIndexOf("#");
						if(lis != -1) ss = ss.substring(lis, ss.length());
						
						String st = ipst.get(j).getSubject();
						int lit = st.lastIndexOf("#");
						if(lit != -1) st = st.substring(lit, st.length());
						
						if(ipss.get(i).propType == PropType.Object && ipst.get(j).propType == PropType.Object){
							String os = ipss.get(i).getObject();
							int lios = os.lastIndexOf("#");
							if(lios != -1) os = os.substring(lios, os.length());
							
							String ot = ipst.get(j).getObject();
							int liot = ot.lastIndexOf("#");
							if(liot != -1) ot = ot.substring(liot, ot.length());
							
							if(os.equalsIgnoreCase(ot) && ss.equalsIgnoreCase(st)){
								mipss.add(ipss.get(i));
								mipst.add(ipst.get(j));
							}
						}
						
						else if(ss.equalsIgnoreCase(st)){
							//Check if they have the same value
							if(ipss.get(i).getObject().equalsIgnoreCase(ipst.get(j).getObject())){
								//Then they are the same prop
								mipss.add(ipss.get(i));
								mipst.add(ipst.get(j));
							}
						}
					//}
				}
			//}
		}
	}
	*/
	
	public String getDescriptionString() {
		String result = "This matcher matches properties of the ontology" +
				" using the instances in the ontology.";
				return result;
	}
}
