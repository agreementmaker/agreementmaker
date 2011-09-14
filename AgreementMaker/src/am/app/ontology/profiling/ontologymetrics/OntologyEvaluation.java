package am.app.ontology.profiling.ontologymetrics;

import java.util.List;

import am.app.ontology.Node;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class OntologyEvaluation {
	Ontology sourceOntology;
	Ontology targetOntology;
	
	boolean skipNonOntology = false;
	
	private WordNetDatabase WordNet;
	
	boolean debug = false;
	
	enum Type {LOCALNAMES, LABELS, COMMENTS};
	
	public OntologyEvaluation(Ontology sourceOntology, Ontology targetOntology) {
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
		initWordnet();
	}
	
	public OntologyEvaluation(){}
	
	public OntologyMetrics evaluateOntology(Ontology ontology){
		//System.out.println("initialize wordnet......");
		initWordnet();
		System.out.println("Evaluating ontology...");
		
		OntologyMetrics metrics = new OntologyMetrics();
		OntModel model = ontology.getModel();
		
		int classes = 0;
		
		int total;
		int haveInstances = 0;
		int totInst = 0;
		
		int nullLabels = 0;
		int diffLabelLocalName = 0;
		int nullComments = 0;
		
		float nullCommentPerc;
		float nullLabelPerc;
		float diffLabelLocPerc;
		
		
		int dataProperties = 0;
				
		for(OntProperty prop: model.listDatatypeProperties().toList()){
			if(prop.isAnon()) continue;
			if(skipNonOntology && !prop.getURI().startsWith(ontology.getURI())) continue;
			dataProperties++;
		}
		if(debug) System.out.println("dataProperties:"+dataProperties);
		
		int objectProperties = 0;
		
		for(OntProperty prop: model.listObjectProperties().toList()){
			if(prop.isAnon()) continue;
			if(skipNonOntology && !prop.getURI().startsWith(ontology.getURI())) continue;
			
			objectProperties++;
		}
		if(debug) System.out.println("objectProperties:"+objectProperties);
		
		String label;
		String comment;
		
		float avgDepth = 0;
		
		for (OntClass cl: model.listClasses().toList()) {
			if(cl.isAnon()) continue;
			if(skipNonOntology && !cl.getURI().startsWith(ontology.getURI())) continue;
			
			classes++;
			int instSize = cl.listInstances().toList().size();
			if(instSize!=0){
				haveInstances++;
				totInst += instSize;
			}
				
			//if(debug) System.out.println(cl.getLocalName());
			
			label = cl.getLabel(null);
			
			if(label==null) nullLabels++;
			else if(!label.equals(cl.getLocalName())){
				diffLabelLocalName++;
				//if(debug) System.out.println("DIFF:"+label+" "+cl.getLocalName());
			}
			
			comment = cl.getComment(null);
			if(comment==null) nullComments++;
			
			int depth = Utility.getClassDepth(cl,1);
			//if(debug) System.out.println("name:"+cl.getLocalName()+" depth:"+depth);
			avgDepth += depth;
		}
		
		avgDepth /= classes;
		
		if(debug) System.out.println("avgDepth:"+avgDepth);
		
		
		
		for (OntProperty pr: model.listDatatypeProperties().toList()) {
			if(skipNonOntology && !pr.getURI().startsWith(ontology.getURI())) continue;
						
			label = pr.getLabel(null);
			
			if(label==null) nullLabels++;
			else if(!label.equals(pr.getLocalName())){
				diffLabelLocalName++;
			}
			
			comment = pr.getComment(null);
			if(comment==null) nullComments++;
		}
		
		for (OntProperty pr: model.listObjectProperties().toList()) {
			if(skipNonOntology && !pr.getURI().startsWith(ontology.getURI())) continue;
			
			label = pr.getLabel(null);
			
			if(label==null) nullLabels++;
			else if(!label.equals(pr.getLocalName())){
				diffLabelLocalName++;
			}
			
			comment = pr.getComment(null);
			if(comment==null) nullComments++;
		}
		
		total = classes + dataProperties + objectProperties;
		
		if(debug) System.out.println("nullLabels: "+nullLabels);
		if(debug) System.out.println("diffLabelLocalName: "+diffLabelLocalName);
		if(debug) System.out.println("nullComments: "+nullComments);
		
		nullCommentPerc = (float)nullComments/total;
		nullLabelPerc = (float)nullLabels/total;
		diffLabelLocPerc = (float)diffLabelLocalName/total;
		
		if(debug) System.out.println("nullCommentPerc: "+nullCommentPerc);
		if(debug) System.out.println("nullLabelPerc: "+nullLabelPerc);
		if(debug) System.out.println("diffLabelLocPerc: "+diffLabelLocPerc);
		
		if(debug) System.out.println("Classes: "+classes);
		
		int subClassOf = countSubClassOf(ontology);		
		if(debug) System.out.println("SubclassOf: "+subClassOf);
				
		int otherRelations = model.listObjectProperties().toList().size();
		if(debug) System.out.println("Other relations: "+otherRelations);
		float relationshipRichness = (float)otherRelations/(otherRelations+subClassOf);
		if(debug) System.out.println("relationshipRichness: "+relationshipRichness);
		float inheritanceRichness = (float)subClassOf/classes;
		if(debug) System.out.println("Inheritance richness: "+inheritanceRichness);
		
		int attributes = model.listDatatypeProperties().toList().size();
		if(debug) System.out.println("attributes: "+attributes);
		
		float attributeRichness = (float)attributes/classes;
		if(debug) System.out.println("attributeRichness: "+attributeRichness);
		
		if(debug) System.out.println("instances: "+totInst);
		
		float avgPopulation = (float) totInst/classes;
		if(debug) System.out.println("avgPopulation: "+avgPopulation);
		
		if(debug) System.out.println("haveInstances: "+haveInstances);
		
		float classRichness = (float)haveInstances/classes;
		
		if(debug) System.out.println("classRichness: "+classRichness);
		
		double labelsWordnet = wordnetCoverage(ontology, Type.LABELS);
		if(debug) System.out.println("label wordnet coverage:"+labelsWordnet);
		double localWordnet = wordnetCoverage(ontology, Type.LOCALNAMES);
		if(debug) System.out.println("local wordnet coverage:"+localWordnet);
		
		
		metrics.setAttributeRichness(attributeRichness);
		metrics.setInheritanceRichness(inheritanceRichness);
		metrics.setRelationshipRichness(relationshipRichness);
		metrics.setAvgPopulation(avgPopulation);
		metrics.setClassRichness(classRichness);
		metrics.setDiffLabelLocPerc(diffLabelLocPerc);
		metrics.setNullCommentPerc(nullCommentPerc);
		metrics.setNullLabelPerc(nullLabelPerc);
		metrics.setLabelWordnet((float)labelsWordnet);
		metrics.setLocalWordnet((float)localWordnet);
		metrics.setAvgDepth(avgDepth);
		
		System.out.println("DONE");
		return metrics;
	}
	
	private double wordnetCoverage(Ontology ontology, Type type) {
		String string = null;
		
		int count = 0;
		
		int coveredCount = 0;
		
		
		for(Node cl:ontology.getClassesList()){
			if(cl.getResource().getURI()==null) continue;
			if(skipNonOntology && !cl.getResource().getURI().startsWith(ontology.getURI())) continue;
						
			count++;
			if(type.equals(Type.LOCALNAMES))
				string = cl.getLocalName();
			else if(type.equals(Type.LABELS))
				string = cl.getLabel();
			
			if(string == null){
				continue;
			}
			
			string = am.Utility.treatString(string);
			
			String[] tokenized = string.split("\\s");
		
			Synset[] synsets;
			
			boolean covered = false;
			for (int i = 0; i < tokenized.length; i++) {
				synsets = WordNet.getSynsets(tokenized[i],null);
				if(synsets.length>0) covered = true;
			}
			
			if(covered) coveredCount++;
		}
		
		for(Node pr:ontology.getPropertiesList()){
			if(pr.getResource().getURI()==null) continue;
			if(skipNonOntology && !pr.getResource().getURI().startsWith(ontology.getURI())) continue;
			
			
			count++;
			if(type.equals(Type.LOCALNAMES))
				string = pr.getLocalName();
			else if(type.equals(Type.LABELS))
				string = pr.getLabel();
			
			if(string == null){
				continue;
			}
			
			string = am.Utility.treatString(string);
			
			String[] tokenized = string.split("\\s");
		
			Synset[] synsets;
			
			boolean covered = false;
			for (int i = 0; i < tokenized.length; i++) {
				synsets = WordNet.getSynsets(tokenized[i],null);
				if(synsets.length>0) covered = true;
			}
			
			if(covered) coveredCount++;
		}
		
		return (double)coveredCount/count;
	}

	public int countSubClassOf(Ontology ontology){
		int count = 0;
		List<Node> classes = ontology.getClassesList();
		OntClass ontClass;
		
		for (Node cl : ontology.getClassesList()){
			if(cl.getResource().getURI()==null) continue;
			if(skipNonOntology && !cl.getResource().getURI().startsWith(ontology.getURI())) continue;
			
			if(!cl.getResource().canAs(OntClass.class)) continue;
			ontClass = cl.getResource().as(OntClass.class);
			count += ontClass.listSubClasses().toList().size();
		}
		
		return count;
	}
	
	public ComparisonMetrics compareOntologies(OntologyMetrics sourceMetrics, OntologyMetrics targetMetrics){
		ComparisonMetrics comparison = new ComparisonMetrics();
		
		double rr1 = sourceMetrics.getRelationshipRichness();
		double rr2 = targetMetrics.getRelationshipRichness();
		
		double ir1 = sourceMetrics.getInheritanceRichness();
		double ir2 = targetMetrics.getInheritanceRichness();
		
		if(debug) System.out.println("ir1:"+ir1+" ir2:"+ir2+" rr1:"+rr1+" rr2:"+rr2);
		
		double rs = (Math.min(rr1, rr2)/Math.max(rr1, rr2) + Math.min(ir1, ir2)/Math.max(ir1, ir2))/2;
		
		if(debug) System.out.println("rs:"+rs);
		
		comparison.setRelationshipSimilarity(rs);
		
		if(sourceMetrics.getNullLabelPerc()==1 || targetMetrics.getNullLabelPerc()==1)
			comparison.noLabels = true;
		
		if(sourceMetrics.getNullCommentPerc()==1 || targetMetrics.getNullCommentPerc()==1)
			comparison.noComments = true;
		
		if(sourceMetrics.getLocalWordnet()<=0.2 || targetMetrics.getLocalWordnet()<=0.2)
			comparison.noLocal = true;
		
		if(sourceMetrics.getAvgPopulation()==0 || targetMetrics.getAvgPopulation()==0)
			comparison.noInstances = true;
		
		return comparison;
	}
	
	private void initWordnet() {
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";

		System.setProperty("wordnet.database.dir", wordnetdir);
		// Instantiate wordnet.
		try {
			WordNet = WordNetDatabase.getFileInstance();
		} catch( Exception e ) {
			am.Utility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}		
	}

	public void doEvaluation(){
		if(debug) System.out.println("EVALUATING ONTOLOGY 1");
		evaluateOntology(sourceOntology);
		if(debug) System.out.println("EVALUATING ONTOLOGY 2");
		evaluateOntology(targetOntology);
		
		
		
		
	}
}
