package am.matcher.FilterMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.MappedNodes;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;




public class FilterMatcher  extends AbstractMatcher  {

	
	public FilterMatcher(){
		super();
		
		setName("FilterMatcher");
		setCategory(MatcherCategory.UNCATEGORIZED);		
	}
	


	@Override
	protected SimilarityMatrix alignUnmappedNodes( List<Node> sourceList, List<Node> targetList, SimilarityMatrix inputMatrix,
			Alignment<Mapping> inputAlignmentSet, alignType typeOfNodes) throws Exception {

		MappedNodes mappedNodes = new MappedNodes(sourceList, targetList, inputAlignmentSet, param.maxSourceAlign, param.maxTargetAlign);
		AbstractMatcher a;
		SimilarityMatrix matrix = null; //= new ArraySimilarityMatrix(sourceOntology, targetOntology, typeOfNodes);
		a = inputMatchers.get(0);
			// is important to be if if if without else
		
		if (typeOfNodes == alignType.aligningClasses
				&& a.areClassesAligned()) {
			matrix=a.getClassesMatrix().clone();
		}else if (typeOfNodes == alignType.aligningProperties
				&& a.arePropertiesAligned()) {
			matrix=a.getPropertiesMatrix().clone();
			
		}
		
	
		
		Node source;
		Node target;
		Mapping alignment; 
		//Mapping inputAlignment;
		for(int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			for(int j = 0; j < targetList.size(); j++) {
				target = targetList.get(j);

				if( !this.isCancelled() ) {
					//if both nodes have not been mapped yet enough times
					//we map them regularly
					if(!mappedNodes.isSourceMapped(source) && !mappedNodes.isTargetMapped(target)){
						alignment = alignTwoNodes(source, target, typeOfNodes, matrix); 
					}
					//else we take the alignment that was computed from the previous matcher
					else{
						alignment = inputMatrix.get(i, j);

						//alignment = new Mapping(inputAlignment.getEntity1(), inputAlignment.getEntity2(), inputAlignment.getSimilarity(), inputAlignment.getRelation());
					}
					matrix.set(i,j,alignment);
					if( isProgressDisplayed() ) stepDone(); // we have completed one step
				}
				else { return matrix; }
			}
			if( isProgressDisplayed() ) updateProgress(); // update the progress dialog, to keep the user informed.
		}
		return matrix;
	}
	
	

	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes, SimilarityMatrix matrix) {
	
		
		int sourceindex = source.getIndex();
		int targetindex = target.getIndex();
				
		AbstractMatcher a;
		double sim;
		
		
			
			a = inputMatchers.get(0);

			// get the sim for this two nodes in the input matcher matrix
			if (typeOfNodes != alignType.aligningClasses
					&& typeOfNodes != alignType.aligningProperties)
				throw new RuntimeException(
						"DEVELOPER ERROR: the alignType of node is not prop or class. ("
								+ a + ")");

			if (typeOfNodes == alignType.aligningClasses
					&& a.areClassesAligned()) {
				if (matrix.get(sourceindex, targetindex) != null) {
	
		sim = matrix.get(sourceindex, targetindex).getSimilarity();
		String srcinpFileName=a.getSourceOntology().getFilename();
		String trginpFileName=a.getTargetOntology().getFilename();		
		Model model = ModelFactory.createDefaultModel();
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		String resource_uri=null;
		String resource_uri1=null;
		String resource_uri2=null;

		
		InputStream in1 = FileManager.get().open(srcinpFileName);
		InputStream in2 = FileManager.get().open(trginpFileName);
		
		 model1.read(in1, null);
		 model2.read(in2, null);
		 int p1=0, p2=0;
		 
		 	resource_uri1 = a.getSourceOntology().getClassesList().get(sourceindex).getUri();		
			Resource srcconcept1 = model1.getResource(resource_uri1);
			resource_uri2 = a.getTargetOntology().getClassesList().get(targetindex).getUri();		
			Resource trgconcept1 = model2.getResource(resource_uri2);
			
		List<Statement>srcsublist=srcconcept1.listProperties(RDFS.subClassOf).toList();
		List<Statement>trgsublist=trgconcept1.listProperties(RDFS.subClassOf).toList();
		

					
					
			if(srcsublist!=null && trgsublist!=null && !srcsublist.isEmpty() && !trgsublist.isEmpty()){
			

				
		//		for(int sind=0;sind<srcsublist.size();sind++){
					
					String srcuriname=srcsublist.get(0).asTriple().getObject().toString();
					if(srcuriname.startsWith("http") && !srcuriname.contains("owl#Thing")){
			//			System.out.println("Resource URI has below subclass"+resource_uri1);
			//			System.out.println("subclass:	"+srcuriname);
					p1=	a.getSourceOntology().getNodeByURI(srcuriname).getIndex();
					}else{
						
						p1=-1;
					}
		//		}
				
			/*	for(int tind=0;tind<trgsublist.size();tind++){*/
					String trguriname=trgsublist.get(0).asTriple().getObject().toString();
					if(trguriname.startsWith("http") && !trguriname.contains("owl#Thing")){
					p2=	a.getTargetOntology().getNodeByURI(trguriname).getIndex();	
					}else{
						p2=-1;
					}
			//		System.out.println("index p2"+p2);
			/*	} */

				if(p1>-1 && p2>-1){
				double parentsim=a.getClassesMatrix().getSimilarity(p1, p2);

				if(parentsim<0.6){
					System.out.println("source concept:"+ resource_uri1);
				//	System.out.println("parent source: "+srcsublist.get(0).asTriple().getObject().toString());
					System.out.println("target concept:"+ resource_uri2);
			//		System.out.println("target_parent"+trgsublist.get(0).asTriple().getObject().toString());
					sim=0;
					}
				}
				
			}		else{
			
		
			InputStream in = FileManager.get().open(srcinpFileName);
			
			 
			if (in == null) {
			     throw new IllegalArgumentException("File: " + srcinpFileName + " not found");
			}

			 // read the RDF/XML file
			 model.read(in, null);	
			 try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			    resource_uri = a.getSourceOntology().getClassesList().get(sourceindex).getUri();		
				Resource srcconcept = model.getResource(resource_uri);			
				List<Statement>srcdisjoint=srcconcept.listProperties(OWL.disjointWith).toList();
			 
				String	dissrcnm=null;
				int dissrcind=0;
				double tmpsim1=0;
							
				ArrayList<Double> srcsz=new ArrayList<Double>();
				
				if(srcdisjoint!=null){

					for(int sind=0;sind<srcdisjoint.size();sind++){					
						dissrcnm=srcdisjoint.get(sind).asTriple().getMatchObject().getURI().toString();
						dissrcind=	a.getSourceOntology().getNodeByURI(dissrcnm).getIndex();		
						Mapping tmpmap=a.getClassesMatrix().get(dissrcind, targetindex);
						if(tmpmap != null){
							tmpsim1=tmpmap.getSimilarity();
							if (tmpsim1>=0.6){
								srcsz.add(tmpsim1);							
							}				
						}
						}
				}
				
				 

				
				String	distrgnm=null;
				int distrgind=0;
				double tmpsim2=0;
				in=FileManager.get().open(trginpFileName);
				model.read(in, null);		 
			    resource_uri = a.getTargetOntology().getClassesList().get(targetindex).getUri();							
				Resource trgconcept = model.getResource(resource_uri);			
				List<Statement>trgdisjoint=trgconcept.listProperties(OWL.disjointWith).toList();
				ArrayList<Double> trgsz=new ArrayList<Double>();
				
				if(trgdisjoint!=null){
					

				
					for(int tind=0;tind<trgdisjoint.size();tind++){	
					distrgnm=trgdisjoint.get(tind).asTriple().getMatchObject().getURI().toString();
					distrgind=	a.getTargetOntology().getNodeByURI(distrgnm).getIndex();
					Mapping tmpmap=a.getClassesMatrix().get(sourceindex, distrgind);
					if(tmpmap != null){
						tmpsim2=tmpmap.getSimilarity();
						if (tmpsim2>=0.6){
							trgsz.add(tmpsim2);
						}				
						
					}
					
				}
						
			}	
				
				int cnt1=0;
				for(int i1=0;i1<srcsz.size();i1++){
					if(srcsz.get(i1)>0.5){
						cnt1++;
					}				
				}
				
				if(cnt1>1){
					sim=0.3;
		//			System.out.println("Incorrect mapping"+a.getTargetOntology().getClassesList().get(targetindex).getLocalName());
				}else{
					
					int cnt2=0;
					for(int i2=0;i2<trgsz.size();i2++){
						if(trgsz.get(i2)>0.5){
							cnt2++;
						}				
					}
					
					if(cnt2>=1){
						sim=0.3;
					}
					
				}
			
			
			
		}//end of parentsim
		
		
			
			
			
			
			
			
						
				} else {
					sim = 0.0;
				}
				
			} else if (typeOfNodes == alignType.aligningProperties
					&& a.arePropertiesAligned()) {
				if (matrix.get(sourceindex, targetindex) != null) {
			//		sim = a.getPropertiesMatrix().get(sourceindex, targetindex).getSimilarity();
					
					String srcdomain, trgdomain, srcrange, trgrange;
					OntResource stringuri;

					stringuri = a.getSourceOntology().getPropertiesList()
							.get(sourceindex).getPropertyDomain();
					srcdomain = getstring(stringuri);

					stringuri = a.getTargetOntology().getPropertiesList()
							.get(targetindex).getPropertyDomain();
					trgdomain = getstring(stringuri);

					stringuri = a.getSourceOntology().getPropertiesList()
							.get(sourceindex).getPropertyRange();
					srcrange = getstring(stringuri);

					stringuri = a.getTargetOntology().getPropertiesList()
							.get(targetindex).getPropertyRange();
					trgrange = getstring(stringuri);

					if (srcdomain.equals(trgdomain)
							&& srcrange.equals(trgrange)) {
						sim = matrix.get(sourceindex, targetindex).getSimilarity();
					} else {
						sim = 0.4;
					}
					
				} else {
					sim = 0.0;
				}
				
			} else
				throw new RuntimeException(
						"DEVELOPER ERROR: The input matchers must perform the mapping selection step. ("
								+ a + ")");


		
		
		return new Mapping(source, target, sim, MappingRelation.EQUIVALENCE);
	}

	private String getstring(OntResource str) {
		String stringuri;
		if (str == null)
			return "";
		else
			stringuri=str.toString();
		// TODO Auto-generated method stub
		String result;

		int loc = 0, j = 0;
		for (int i = 0; i < stringuri.length(); i++) {
			if (stringuri.charAt(i) == '#') {
				loc = i + 1;
				break;
			}
		}

		result = stringuri.substring(loc).toLowerCase();

		return result;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
		
	
	
	 
}