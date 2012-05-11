package iterativeinstancestructuralmatcher.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import am.AMException;
import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.userInterface.MatcherParametersDialog;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.CardinalityRestriction;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.MaxCardinalityRestriction;
import com.hp.hpl.jena.ontology.MinCardinalityRestriction;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class IterativeInstanceStructuralMatcher extends AbstractMatcher {
	private static final long serialVersionUID = 3612931342445940115L;

	double PROPERTY_THRESHOLD = 0.8;
	double CLASS_THRESHOLD = 0.8;
	
	boolean individuals = true;
	boolean matchUnionClasses = true;
	boolean printMappingTable = false;
	
	static boolean verbose = false; 
	
	private List<Node> sourceClassList;
	private List<Node> targetClassList;
	private List<Node> sourcePropList;
	private List<Node> targetPropList;
	
	private transient HashMap<Node, List<Restriction>> sourceRestrictions;
	private transient HashMap<Node, List<Restriction>> targetRestrictions;
	private transient HashMap<Restriction, Node> restrictions;
	private transient HashMap<OntProperty, List<String>> sourcePropValues;
	private transient HashMap<OntProperty, List<String>> targetPropValues;
	
	private PropertySimilarity[][] propSimilarities;
	private ClassSimilarity[][] classSimilarities;
	
	//Provenance Strings
	private final String RECURSIVE_INDIVIDUALS = "Recursive Individuals";
	private final String SUBPROPERTIES = "Subproperties";
	private final String SUBCLASSES = "Subclasses";
	private final String COMBINATION = "Combination";
	private final String PROPERTY_VALUES = "Property Values";
	private final String PROPERTY_USAGE = "Property Usage";
	private final String UNION_CLASSES = "Union Classes";
	private final String SUBCLASSOF = "SubclassOf";
	private final String RANGE_DOMAIN = "Range Domain";
	private final String SYNTACTIC = "Syntactic";

	
	IterativeInstanceStructuralParameters parameters;
	
	public IterativeInstanceStructuralMatcher(){
		super();
		minInputMatchers = 0;
		maxInputMatchers = 1;
		
		needsParam = true;
		setName("Iterative Instance and Structural Matcher");
		//progressDisplay = new MatchingProgressDisplay();		
	}
	
	@Override
	public void matchEnd() {
		// TODO Auto-generated method stub
		super.matchEnd();
		
		if(printMappingTable)
			evaluate();
	}
	
//	@SuppressWarnings("unchecked")
	@Override
	protected void align() throws Exception {
		if (sourceOntology == null || targetOntology == null)
			return; // cannot align just one ontology
		
		if(param!=null)
			parameters = (IterativeInstanceStructuralParameters)param;
		
		sourceClassList = sourceOntology.getClassesList();
		targetClassList = targetOntology.getClassesList();
		sourcePropList = sourceOntology.getPropertiesList();
		targetPropList = targetOntology.getPropertiesList();
		
		//Initialize maps for information about restrictions
		initHashMaps();
		
		receiveInputMatrices();		
		
		initSimilarityMatrices();
		
		if(individuals){
			sourcePropValues = initPropValues(sourcePropList,sourceOntology);
			targetPropValues = initPropValues(targetPropList,targetOntology);
		}
		
		printPropValues();
		
		if(individuals){
			//Match properties by similar values
			if(parameters.usePropertyUsage)
			matchPropertyValues();
			
			Node source;
			Node target;
			for (int i = 0; i < sourceClassList.size(); i++) {
				source = sourceClassList.get(i);
				for (int j = 0; j < targetClassList.size(); j++) {
					target = targetClassList.get(j);
					if(matchIndividuals(source,target)){
						Mapping m = new Mapping(source, target, 1.0);
						m.setProvenance(RECURSIVE_INDIVIDUALS);
						classesMatrix.set(i, j, m);
					}
						
				}
			}
		}
			
		//Iterative part
		for (int i = 0;  ; i++) {
			double totAlign = getNumberOfClassAlignments() + getNumberOfPropAlignments();
			//Match by superclasses and restriction on properties
			if(parameters.useSuperclasses)
				matchSuperclasses();
			//Match properties by range and domain	
			if(parameters.useRangeDomain)
				matchRangeAndDomain();
			//Match properties by their presence in restrictions
			if(parameters.useRangeDomain)
				matchPropertyUsage();
			//match sons of aligned classes
			matchSubClasses();
			//match sons of aligned properties
			matchSubProperties();
			
			findNewAlignments();

			double totAlign2 = getNumberOfClassAlignments() + getNumberOfPropAlignments();
			if(totAlign2==totAlign){
				if( Core.DEBUG_FCM ) System.out.println("CONVERGED IN "+(i+1)+" ITERATIONS");
				break;
			}
				
		}
		
		if( matchUnionClasses ) matchUnionClasses();
		
		filterNonOntologyAlignments();
		
		//printAllSimilarities();
		
		//evaluate();
		
	}
	
	private void findNewAlignments() {
		double sim;
		for (int i = 0; i < classSimilarities.length; i++) {
			for (int j = 0; j < classSimilarities[0].length; j++) {
				sim = classSimilarities[i][j].getSimilarity();
				if(sim > classesMatrix.getSimilarity(i, j)){
					Mapping m = new Mapping( sourceClassList.get(i), targetClassList.get(j), sim );
					m.setProvenance(COMBINATION);
					classesMatrix.set(i, j, m);
				}
			}
		}
		for (int i = 0; i < propSimilarities.length; i++) {
			for (int j = 0; j < propSimilarities[0].length; j++) {
				//System.out.print(sourcePropList.get(i)+" "+targetPropList.get(j));
				sim = propSimilarities[i][j].getSimilarity();
				if(sim > propertiesMatrix.getSimilarity(i, j)){
					Mapping m = new Mapping( sourcePropList.get(i), targetPropList.get(j), sim );
					m.setProvenance(COMBINATION);
					propertiesMatrix.set(i, j, m);
				}
					
			}
		}
	}



	private void initSimilarityMatrices() {
		classSimilarities = new ClassSimilarity[sourceClassList.size()][targetClassList.size()];
		propSimilarities = new PropertySimilarity[sourcePropList.size()][targetPropList.size()];
		
		for (int i = 0; i < sourceClassList.size(); i++) {
			for (int j = 0; j < targetClassList.size(); j++) {
				classSimilarities[i][j] = new ClassSimilarity();
				classSimilarities[i][j].setSyntactic(classesMatrix.getSimilarity(i, j));
			}
		}
		
		for (int i = 0; i < sourcePropList.size(); i++) {
			for (int j = 0; j < targetPropList.size(); j++) {
				propSimilarities[i][j] = new PropertySimilarity();
				propSimilarities[i][j].setSyntactic(propertiesMatrix.getSimilarity(i, j));
			}
		}
		
	}

	private void receiveInputMatrices() {
		if(inputMatchers.size()>0){
			AbstractMatcher input = inputMatchers.get(0);
			//classesMatrix = input.getClassesMatrix();
			try {
				classesMatrix = new ArraySimilarityMatrix(input.getClassesMatrix());
			} catch( AMException e ) {
				e.printStackTrace();
			}
			//propertiesMatrix = input.getPropertiesMatrix();
			try {
				propertiesMatrix = new ArraySimilarityMatrix(input.getPropertiesMatrix());
			} catch( AMException e ) {
				e.printStackTrace();
			}
			//System.out.println();
		}
		else{
			classesMatrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, alignType.aligningClasses);
			propertiesMatrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, alignType.aligningProperties);
		}
		
	}

	private void filterNonOntologyAlignments() {
		for (int i = 0; i < sourceClassList.size(); i++) {
			if(!sourceClassList.get(i).getUri().startsWith(sourceOntology.getURI())){
				for (int j = 0; j < targetClassList.size(); j++) {
					classesMatrix.set(i, j, null);
				}
			}
		}
		for (int j = 0; j < targetClassList.size(); j++) {
			if(!targetClassList.get(j).getUri().startsWith(targetOntology.getURI())){
				for (int i = 0; i < sourceClassList.size(); i++) {
					classesMatrix.set(i, j, null);
				}
			}
		}
		for (int i = 0; i < sourcePropList.size(); i++) {
			if(!sourcePropList.get(i).getUri().startsWith(sourceOntology.getURI())){
				for (int j = 0; j < targetPropList.size(); j++) {
					propertiesMatrix.set(i, j, null);
				}
			}
		}
		for (int j = 0; j < targetPropList.size(); j++) {
			if(!targetPropList.get(j).getUri().startsWith(targetOntology.getURI())){
				for (int i = 0; i < sourcePropList.size(); i++) {
					propertiesMatrix.set(i, j, null);
				}
			}
		}
	}

	private void evaluate() {
		
		ReferenceAlignmentMatcher refMatcher = (ReferenceAlignmentMatcher)MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment,0);
		MatcherParametersDialog dialog = new MatcherParametersDialog(refMatcher,false,false);
		if(dialog.parametersSet()) {
			refMatcher.setParam(dialog.getParameters());
			refMatcher.setThreshold(refMatcher.getDefaultThreshold());
			refMatcher.setMaxSourceAlign(refMatcher.getDefaultMaxSourceRelations());
			refMatcher.setMaxTargetAlign(refMatcher.getDefaultMaxTargetRelations());
			try {
				refMatcher.match();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String report="Reference Evaluation Complete\n\n";
			
			ReferenceEvaluationData rd = ReferenceEvaluator.compare(getAlignment(), refMatcher.getAlignment());
			setRefEvaluation(rd);
			
			System.out.println("CORRECT MAPPINGS");
			System.out.println(allSimilarities(rd.getCorrectAlignments()));
			System.out.println("WRONG MAPPINGS");
			System.out.println(allSimilarities(rd.getErrorAlignments()));			
			System.out.println("MISSED MAPPINGS");
			System.out.println(allSimilarities(rd.getLostAlignments()));			
						
			report+= getRegistryEntry().getMatcherName()+"\n\n";
			report +=rd.getReport()+"\n";
			Utility.displayTextAreaPane(report,"Reference Evaluation Report");
		}
		dialog.dispose();
		Core.getUI().redisplayCanvas();
	}

	private void matchPropertyValues() {
		if( Core.DEBUG_FCM ) System.out.println("MATCH PROPERTY VALUES");
		OntProperty sProp;
		OntProperty tProp;
		for (int i = 0; i < sourcePropList.size() ; i++) {
			sProp = (OntProperty)sourcePropList.get(i).getResource().as(OntProperty.class);
			List<String> sList = sourcePropValues.get(sProp);
			for (int j = 0; j < targetPropList.size(); j++) {
				
				tProp = (OntProperty)targetPropList.get(j).getResource().as(OntProperty.class);	
				
				if(!sProp.getURI().startsWith(sourceOntology.getURI())||
						!tProp.getURI().startsWith(targetOntology.getURI()))
					continue;
				
				List<String> tList = targetPropValues.get(tProp);
				
				if(sList.size()==0 || tList.size()==0) continue;
				
				if( Core.DEBUG_FCM ) System.out.println(sProp.getLocalName()+" "+tProp.getLocalName()+" litsize: "+sList.size()+" "+tList.size());
				
				double sim = 0;
								
				String l1;
				String l2;
				for (int k = 0; k < sList.size(); k++) {
					l1 = sList.get(k);
					for (int t = 0; t < tList.size(); t++) {
						l2 = tList.get(t);
						if(l1.equals(l2)){
							sim++; 
							
						}
							
					}
				}
				sim = sim / Math.max(sList.size(),tList.size()); 				
				
				propSimilarities[i][j].setValues(sim);
				
				if(sim >= parameters.getPropertyValuesThreshold()){
					Mapping m = new Mapping(sourcePropList.get(i), targetPropList.get(j), sim);
					m.setProvenance(PROPERTY_VALUES);
					if(parameters.boostPropertyValues) m.setSimilarity(1.0d);
					propertiesMatrix.set(i, j, m);
					if( Core.DEBUG_FCM ) System.out.println("ALIGNMENT:"+sProp.getLocalName()+" "+tProp.getLocalName()+" BY PROP VALUES");
				}
			}
		}		
	}

	@SuppressWarnings("unchecked")
	private HashMap<OntProperty, List<String>> initPropValues(List<Node> propList,Ontology ontology) {
		HashMap<OntProperty, List<String>> propValues = new HashMap<OntProperty, List<String>>();
		List<Statement> stmts;
		List<String> literals;
		for (int i = 0; i < propList.size(); i++) {
			OntProperty sProp = (OntProperty)propList.get(i).getResource().as(OntProperty.class);
			//System.out.println("Prop: "+sProp);
			literals = new ArrayList<String>();
			stmts = ontology.getModel().listStatements(null, sProp, (RDFNode)null).toList();
			if( stmts.isEmpty() ) { stmts = ontology.getModel().listStatements(null,   ontology.getModel().getProperty(sProp.getLocalName()) ,(RDFNode)null).toList(); }
			for (int j = 0; j < stmts.size(); j++) {
				Statement s = stmts.get(j);
				//System.out.println(s);	
				RDFNode obj = s.getObject();
				if(obj.isLiteral()){
					Literal l = (Literal)obj;
					if(!literals.contains(l.getString()))
						literals.add(l.getString());
				}
			}
			propValues.put(sProp, literals);
		}
		return propValues;
	}

	private void matchSubProperties() {
		if(verbose)
		if( Core.DEBUG_FCM ) System.out.println("MATCH SUBPROPERTIES");
		ArrayList<OntProperty> sSub;
		ArrayList<OntProperty> tSub;
		for (int i = 0; i < sourcePropList.size(); i++) {
			sSub = new ArrayList<OntProperty>();
			OntProperty pr1 = (OntProperty)sourcePropList.get(i).getResource().as(OntProperty.class);
			ExtendedIterator it1 = pr1.listSubProperties();
			while(it1.hasNext()){
				sSub.add((OntProperty)it1.next());
			}
			for (int j = 0; j < targetPropList.size(); j++){
				tSub = new ArrayList<OntProperty>();
				OntProperty pr2 = (OntProperty)targetPropList.get(j).getResource().as(OntProperty.class);
				ExtendedIterator it2 = pr2.listSubProperties();
				while(it2.hasNext()){
					tSub.add((OntProperty)it2.next());
				}
				
				if(alignedProp(pr1.getURI(),pr2.getURI())>=PROPERTY_THRESHOLD &&
						sSub.size()==tSub.size() && sSub.size()>0){
					
					if(verbose){
						if( Core.DEBUG_FCM ) System.out.println("size: "+sSub.size());
						if( Core.DEBUG_FCM ) System.out.println("prop1: "+pr1.getLocalName());
						if( Core.DEBUG_FCM ) System.out.println(sSub);
						if( Core.DEBUG_FCM ) System.out.println("prop2: "+pr2.getLocalName());
						if( Core.DEBUG_FCM ) System.out.println(tSub);
					}
						
					for (int k = 0; k < sSub.size(); k++) {
						for (int t = 0; t < tSub.size(); t++) {
							if(alignedProp(sSub.get(k).getURI(),tSub.get(t).getURI())>=PROPERTY_THRESHOLD){
								sSub.remove(k);
								tSub.remove(t);
								k--;
								t--;
								break;
							}
						}
					}
					if(verbose){
						System.out.println("Still to align: "+sSub.size());
					}
					
					if(sSub.size()==0) continue;
					
					if(sSub.size()==1){
						int row = getIndex(sourcePropList,sSub.get(0).getURI());
						int col = getIndex(targetPropList,tSub.get(0).getURI()); 
						
						Mapping m = new Mapping( sourcePropList.get(row), targetPropList.get(col), 1.0d);
						m.setProvenance(SUBPROPERTIES);
						propertiesMatrix.set( row, col, m);

						if( Core.DEBUG_FCM ) System.out.println("ALIGNMENT:"+sSub.get(0).getLocalName()+" "+
								tSub.get(0).getLocalName()+" BY SUBPROPERTIES");
						continue;
					}
					
					double[][] sims = new double[sSub.size()][sSub.size()];
					
					for (int k = 0; k < sSub.size(); k++) {
						for (int t = 0; t < tSub.size(); t++) {
							sims[k][t] = rangeAndDomainSimilarity(sSub.get(k), tSub.get(t));	
						}
					}
					List<AlignIndexes> aligns = Utils.optimalAlignments(sims);
					
					for (int k = 0; k < aligns.size(); k++) {
						if( Core.DEBUG_FCM ) System.out.println(aligns.get(k).getX()+" "+aligns.get(k).getY());
						int row = getIndex(sourcePropList,sSub.get(aligns.get(k).getX()).getURI());
						int col = getIndex(targetPropList,tSub.get(aligns.get(k).getY()).getURI()); 
						
						Mapping m = new Mapping( sourcePropList.get(row), targetPropList.get(col), 1.0d);
						m.setProvenance(SUBPROPERTIES);
						propertiesMatrix.set( row, col, m);
						
						if( Core.DEBUG_FCM ) System.out.println("ALIGNMENT:"+sSub.get(aligns.get(k).getX()).getLocalName()+" "
								+tSub.get(aligns.get(k).getY()).getLocalName()+ " BY SUBPROPERTIES");
					}
					if(verbose){
						Utils.printMatrix(sims);
					}
				}
			}
		}
		return;
	}

	private double rangeAndDomainSimilarity(OntProperty sProp,
			OntProperty tProp) {
		if((sProp.getDomain()==null && tProp.getDomain()!=null) ||
				(sProp.getDomain()!=null && tProp.getDomain()==null)) 
			return 0.0;
		
		if((sProp.getRange()==null && tProp.getRange()!=null) ||
				(sProp.getRange()!=null && tProp.getRange()==null))
				return 0.0;
		
		if(sProp.isDatatypeProperty() && !tProp.isDatatypeProperty() ||
				!sProp.isDatatypeProperty() && tProp.isDatatypeProperty())
		return 0.0;
		
		if(verbose){
			System.out.println(sProp.getLocalName()+","+tProp.getLocalName());
		}
		
		double rangeSim = 0;
		double domainSim = 0;
		boolean unions = false;
				
		if(sProp.getDomain()==null && tProp.getDomain()==null){
			domainSim = 0.8;
		}
		else{
			try {
				domainSim = domainSimilarity(sProp.getDomain(),tProp.getDomain());
				if(sProp.getDomain().asClass().isUnionClass() &&
						tProp.getDomain().asClass().isUnionClass())
					unions = true;
			} catch( Exception e ) {
				e.printStackTrace();
				domainSim = 0.8;
			}
			
		}
			
		if(sProp.getRange()!=null && tProp.getRange()!=null)
			rangeSim = compareResources(sProp.getRange(), tProp.getRange()); 
		
		if(sProp.getRange()==null && tProp.getRange()==null)
			rangeSim = 0.8;
		
		if(!unions && tProp.getRange()!=null && tProp.getRange().getURI() != null &&
				Utils.primitiveType(tProp.getRange().getURI()))
			rangeSim *= 0.75;
		
		/*
		else if(sProp.isObjectProperty() && tProp.isObjectProperty()){
			domainSim = alignedClass(sProp.getRange().getURI(), tProp.getRange().getURI());
		}*/
		
		if(verbose){
			System.out.println("rangesim: "+rangeSim+" domsim: "+domainSim);
		}
		double sim = (rangeSim+domainSim)/2;
		return sim;
	}

	private void matchSubClasses() {
		if(verbose){
			System.out.println("MATCH SUBCLASSES");
		}
		ArrayList<OntClass> sSub;
		ArrayList<OntClass> tSub;
		for (int i = 0; i < sourceClassList.size(); i++) {
			sSub = new ArrayList<OntClass>();
			OntClass cl1 = (OntClass)sourceClassList.get(i).getResource().as(OntClass.class);
			ExtendedIterator it1 = cl1.listSubClasses();
			while(it1.hasNext()){
				sSub.add((OntClass)it1.next());
			}
			for (int j = 0; j < targetClassList.size(); j++){
				tSub = new ArrayList<OntClass>();
				OntClass cl2 = (OntClass)targetClassList.get(j).getResource().as(OntClass.class);
				ExtendedIterator it2 = cl2.listSubClasses();
				while(it2.hasNext()){
					tSub.add((OntClass)it2.next());
				}
				
				if(alignedClass(cl1.getURI(),cl2.getURI())>=CLASS_THRESHOLD &&
						sSub.size()==tSub.size() && sSub.size()>0){
					
					if(verbose){
						System.out.println("size: "+sSub.size());
						System.out.println("class1: "+cl1.getLocalName());
						System.out.println(sSub);
						System.out.println("class2: "+cl2.getLocalName());
						System.out.println(tSub);
					}
					
					for (int k = 0; k < sSub.size(); k++) {
						for (int t = 0; t < tSub.size(); t++) {
							if(alignedClass(sSub.get(k).getURI(),tSub.get(t).getURI())>=CLASS_THRESHOLD){
								sSub.remove(k);
								tSub.remove(t);
								k--;
								t--;
								break;
							}
						}
					}
					if(verbose){
						System.out.println("Still to align: "+sSub.size());
					}
					
					if(sSub.size()==1){
						int row = getIndex(sourceClassList,sSub.get(0).getURI());
						int col = getIndex(targetClassList,tSub.get(0).getURI());
						Mapping m = new Mapping( sourceClassList.get(row), targetClassList.get(col), 1.0d);
						m.setProvenance(SUBCLASSES);
						classesMatrix.set(row, col, m);
						if( Core.DEBUG_FCM ) System.out.println("ALIGNMENT:"+sSub.get(0)+" "+tSub.get(0)+" BY SUBCLASSES");
						continue;
					}
					
					
					double[][] sims = new double[sSub.size()][sSub.size()];
					
					for (int k = 0; k < sSub.size(); k++) {
						for (int t = 0; t < tSub.size(); t++) {
							if (verbose) System.out.println(sSub.get(k).getLocalName()+" "+tSub.get(t).getLocalName());
							sims[k][t] = superclassesComparison(sSub.get(k), tSub.get(t));	
						}
					}
					
					if(verbose){
						System.out.println("class1: "+cl1.getLocalName());
						System.out.println("class2: "+cl2.getLocalName());
						Utils.printMatrix(sims);
					}
				}
			}
		}
	}

	private void initHashMaps() {
		sourceRestrictions = new HashMap<Node, List<Restriction>>();
		targetRestrictions = new HashMap<Node, List<Restriction>>();
		restrictions = new HashMap<Restriction, Node>();
		for (int i = 0; i < sourcePropList.size(); i++) {
			sourceRestrictions.put(sourcePropList.get(i), 
					getRestrictionsOnProperty(sourceClassList, sourcePropList.get(i)));
		}
		for (int i = 0; i < targetPropList.size(); i++) {
			targetRestrictions.put(targetPropList.get(i), 
					getRestrictionsOnProperty(targetClassList, targetPropList.get(i)));
		}
	}

	private void matchPropertyUsage() {
		if(verbose){
			System.out.println("MATCH PROPERTY USAGE");
		}
		Node sProp;
		Node tProp;
		List<Restriction> l1;
		List<Restriction> l2;
		ArrayList<Double> similarities = new ArrayList<Double>();
		
		for (int i = 0; i < sourcePropList.size(); i++) {
			sProp = sourcePropList.get(i);
			l1 = sourceRestrictions.get(sProp);
			//System.out.println("prop: "+sProp.getLocalName()+" size: "+l1.size());
			similarities = new ArrayList<Double>();
			
			for (int j = 0; j < targetPropList.size(); j++) {
				tProp = targetPropList.get(j);
				
				OntProperty sp = (OntProperty) sProp.getResource().as(OntProperty.class);
				OntProperty tp = (OntProperty) tProp.getResource().as(OntProperty.class);
				
				if((sp.isDatatypeProperty() && !tp.isDatatypeProperty())||
						(!sp.isObjectProperty() && tp.isObjectProperty())){
					similarities.add(0.0);
					continue;
				}
								
				l2 = targetRestrictions.get(tProp);
				
				if(l1.size()!=l2.size() || l1.size()==0){
					similarities.add(0.0);
					continue;
				}
				
				if(verbose){
					System.out.println(sProp.getLocalName()+" "+tProp.getLocalName());
				}
				
				double[][] sims = new double[l1.size()][l1.size()]; 
				
				for(int t=0; t<l1.size(); t++){
					Restriction r1 = l1.get(t);
					for(int k=0; k<l2.size(); k++){
						Restriction r2 = l2.get(k);
						sims[t][k] = restrictionUsageSimilarity(r1,r2);
					}
				}
				
				//Obtain suboptimal solution
				double usSim = Utils.optimalAlignment(sims);
				
				similarities.add(usSim);
				
				//Utils.printMatrix(sims);
				if(verbose){
					System.out.println("subSim: "+usSim);
				}
			}
			
			//System.out.println(similarities);
			
			int index = Utils.getOnlyMax(similarities);
			
			if(verbose){
				System.out.println("onlyMax: "+index);
			}
			if(index!=-1 && verbose) System.out.println(similarities.get(index));
			
						
			if(index!=-1 && similarities.get(index)>parameters.getPropertyUsageThreshold()){
				Mapping m = new Mapping( sProp, targetPropList.get(index), similarities.get(index));
				m.setProvenance(PROPERTY_USAGE);
				if(parameters.boostPropertyUsage) m.setSimilarity(1.0d);
				propertiesMatrix.set(i, index, m);
				if( Core.DEBUG_FCM ) System.out.println("ALIGNMENT:"+sProp.getLocalName()+" "+targetPropList.get(index).getLocalName()+" BY PROPERTY USAGE");
			}
		}	
	}

	private double restrictionUsageSimilarity(Restriction r1, Restriction r2) {
		try {
			double restrSim = restrictionSimilarity(r1, r2, false);
			double resSim = compareResources(restrictions.get(r1).getResource(), restrictions.get(r1).getResource());
			return (2*restrSim+resSim)/3;
		} catch( Exception e ) {
			e.printStackTrace();
			return 0d;
		}
	}

	private List<Restriction> getRestrictionsOnProperty( List<Node> classList,
			Node sProp) {
		ArrayList<Restriction> restr = new ArrayList<Restriction>();
		for(Node cl: classList){
			OntClass ontClass = (OntClass)cl.getResource().as(OntClass.class);
			try {
				for(Object o: ontClass.listSuperClasses().toList()){
					OntClass supClass  = (OntClass) o;
					if(supClass.isRestriction()){
						Restriction r = supClass.asRestriction();
						restrictions.put(r, cl);
						if(r.getOnProperty().equals((OntProperty)sProp.getResource().as(OntProperty.class)))
							restr.add(r);
					}
				}
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		return restr;
	}
	

	/* Find UnionClass types and match member classes
	 * @author Ulas
	 */
	private void matchUnionClasses(){
		if(verbose) System.out.println("MATCH UNION");
		ArrayList<UnionClass> unionClassesS = new ArrayList<UnionClass>();
		ArrayList<UnionClass> unionClassesT = new ArrayList<UnionClass>();
		
		ExtendedIterator<UnionClass> its = getSourceOntology().getModel().listUnionClasses();
		ExtendedIterator<UnionClass> itt = getTargetOntology().getModel().listUnionClasses();
		
		while(its.hasNext()){
			UnionClass uc = its.next();
			unionClassesS.add(uc);
		}
		while(itt.hasNext()){
			UnionClass uc = itt.next();
			unionClassesT.add(uc);
		}
		//System.out.println();
		for(int k = 0; k < unionClassesS.size(); k++){
			for(int m = 0; m < unionClassesT.size(); m++){
				try {
					matchUnionClassMember(unionClassesS.get(k), unionClassesT.get(m));
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/* Matches member classes of two union classes
	 * @author Ulas
	 * @param UnionClass, UnionClass
	 */
	private void matchUnionClassMember(UnionClass a, UnionClass b){
		
		ArrayList<OntClass> aList = new ArrayList<OntClass>();
		for (ExtendedIterator<? extends OntClass> e = a.listOperands(); e.hasNext(); ) {
			Resource r0 = (Resource) e.next();
			OntClass unionMember = (OntClass) r0.as( OntClass.class );
			aList.add(unionMember);
			//System.out.print(" " + unionMember.getLocalName());
		}
		ArrayList<OntClass> bList = new ArrayList<OntClass>();
		for (ExtendedIterator<? extends OntClass> e = b.listOperands(); e.hasNext(); ) {
			Resource r0 = (Resource) e.next();
			OntClass unionMember = (OntClass) r0.as( OntClass.class );
			bList.add(unionMember);
			//System.out.print(" " + unionMember.getLocalName());
		}
		
		if(aList.size() > 2 || bList.size() > 2){ return;}
		
		boolean matchedS0 = false;
		boolean matchedT0 = false;
		boolean matchedS1 = false;
		boolean matchedT1 = false;
		
		for (int i = 0; i < sourceOntology.getClassesList().size(); i++) {
			for (int j = 0; j < targetOntology.getClassesList().size(); j++) {
				Mapping aln = null;
				try{
					aln = classesMatrix.get(i, j);
					if(aln==null) continue;
					Node currentNode = aln.getEntity1();
					OntClass currentClassS = (OntClass) currentNode.getResource().as(OntClass.class);
					if(currentClassS.equals(aList.get(0))){
						matchedS0 = true;
						
						//System.out.println("current: "+currentClassS);
						Node n = classesMatrix.get(i, j).getEntity2();
						double sims = classesMatrix.get(i,j).getSimilarity();
						
						OntClass cT = (OntClass) n.getResource().as(OntClass.class);
						
						if(verbose){
							System.out.println(sourceClassList.get(i)+" "+targetClassList.get(j));
							System.out.println("sims:"+sims);
							System.out.println("cT:"+cT);
							System.out.println("aList:"+aList);
							System.out.println("bList:"+bList);
							System.out.println("eq:"+cT.equals(bList.get(0))+" sims:"+sims);
							
						}
						if(cT.equals(bList.get(0)) && sims > 0.8){
							//Align 1 and 1 Here
							//int index1 = findSourceIndex(aList.get(0));
							int i1 = getIndex(sourceClassList,aList.get(1).getURI());
							int i2 = getIndex(targetClassList,bList.get(1).getURI());
							if(i1==-1 || i2==-1) continue;
							double sim1 = classesMatrix.getRowMaxValues(i1, 1)[0].getSimilarity();
							double sim2 = classesMatrix.getColMaxValues(i2, 1)[0].getSimilarity();
							
							if(verbose){
								System.out.println(aList.get(0).getLocalName()+" "+bList.get(1).getLocalName());							
								System.out.println(sourceClassList.get(i)+" "+targetClassList.get(j));
								System.out.println("sim1:"+sim1+" sim2:"+sim2);
							}
							
							if(sim1 < 0.6d && sim2 < 0.6d){
								if(Core.DEBUG_FCM) System.out.println("ALIGNMENT:"+aList.get(1)+" "+bList.get(1)+" BY ULAS");
//								classesMatrix.set(findSourceIndex(aList.get(1)), findTargetIndex(bList.get(1)), 
//										new Mapping(findSourceNode(aList.get(1)), findTargetNode(bList.get(1)), 1.0d));
								int c1 = findSourceIndex(aList.get(1));
								int c2 = findTargetIndex(bList.get(1));
								if(i1==-1 || i2==-1) continue;
								Mapping m = new Mapping(sourceClassList.get(c1), targetClassList.get(c2), 1.0d);
								m.setProvenance(UNION_CLASSES);
								classesMatrix.set(c1, c2, m);
								if(verbose) System.out.println("ALIGNMENT:"+aList.get(1)+" "+bList.get(1)+" BY ULAS1");
							}
						}
						else{
							if(cT.equals(bList.get(1)) && sims > 0.8){
								//Align 1 and 0 here
								int i1 = getIndex(sourceClassList,aList.get(1).getURI());
								int i2 = getIndex(targetClassList,bList.get(0).getURI());
								if(i1==-1 || i2==-1) continue;
								double sim1 = classesMatrix.getRowMaxValues(i1, 1)[0].getSimilarity();
								double sim2 = classesMatrix.getColMaxValues(i2, 1)[0].getSimilarity();
								if(sim1 < 0.6 && sim2 < 0.6d){
									int c1 = findSourceIndex(aList.get(1));
									int c2 = findTargetIndex(bList.get(0));
									Mapping m = new Mapping(sourceClassList.get(c1), targetClassList.get(c2), 1.0d);
									m.setProvenance(UNION_CLASSES);
									classesMatrix.set(c1, c2, m);
									if(verbose) System.out.println("ALIGNMENT:"+aList.get(1)+" "+bList.get(0)+" BY ULAS2");
								} 
								
							}
						}
					}
					else if(currentClassS.equals(aList.get(1))){
						matchedS1 = true;
						
						Node n = classesMatrix.get(i, j).getEntity2();
						double sims = classesMatrix.get(i,j).getSimilarity();
						OntClass cT = (OntClass) n.getResource().as(OntClass.class);
						if(cT.equals(bList.get(0)) && sims > 0.8){
							//Align 0 and 1 Here
							double sim1 = classesMatrix.getRowMaxValues(findSourceIndex(aList.get(0)), 1)[0].getSimilarity();
							double sim2 = classesMatrix.getColMaxValues(findTargetIndex(bList.get(1)), 1)[0].getSimilarity();
							if(sim1 < 0.6d && sim2 < 0.6d){
								int c1 = findSourceIndex(aList.get(0));
								int c2 = findTargetIndex(bList.get(1));
								Mapping m = new Mapping(sourceClassList.get(c1), targetClassList.get(c2), 1.0d);
								m.setProvenance(UNION_CLASSES);
								classesMatrix.set(c1, c2, m);
								System.out.println("ALIGNMENT:"+aList.get(0)+" "+bList.get(1)+" BY ULAS3");
								//System.out.println("A");
//								classesMatrix.set(findSourceIndex(aList.get(0)), findTargetIndex(bList.get(1)), 
//										new Mapping(findSourceNode(aList.get(0)), findTargetNode(bList.get(1)), 1.0d));
//								System.out.println();
							}
						}
						else{
							if(cT.equals(bList.get(1)) && sims > 0.8){
								//Align 0 and 0 here
								double sim1 = classesMatrix.getRowMaxValues(findSourceIndex(aList.get(0)), 1)[0].getSimilarity();
								double sim2 = classesMatrix.getColMaxValues(findTargetIndex(bList.get(0)), 1)[0].getSimilarity();
								if(sim1 < 0.6d && sim2 < 0.6d){
									int c1 = findSourceIndex(aList.get(0));
									int c2 = findTargetIndex(bList.get(0));
									Mapping m = new Mapping(sourceClassList.get(c1), targetClassList.get(c2), 1.0d);
									m.setProvenance(UNION_CLASSES);
									classesMatrix.set(c1, c2, m);
									System.out.println("ALIGNMENT:"+aList.get(0)+" "+bList.get(0)+" BY ULAS2");
//									System.out.println("B");
//									classesMatrix.set(findSourceIndex(aList.get(0)), findTargetIndex(bList.get(0)), 
//											new Mapping(findSourceNode(aList.get(0)), findTargetNode(bList.get(0)), 1.0d));
//									System.out.println();
								}
							}
						}
					}
				}
				catch(Exception e){ 
					//e.printStackTrace();
				}
			
			}
		}
	}
	
	/* Finds index of a source class in the matrix
	 * @author Ulas
	 * @param OntClass
	 * @return int index
	 */
	private int findSourceIndex(OntClass c){
//		Mapping aln = null;
//		for(int i = 0; i < sourceOntology.getClassesList().size(); i++) {
//			try{
//				aln = classesMatrix.get(i, 0);
//				Node currentNode = aln.getEntity1();
//				OntClass currentClassS = (OntClass) currentNode.getResource().as(OntClass.class);
//				if(c.equals(currentClassS)){
//					return i;
//				}
//			}
//			catch(Exception e){
//			}
//		}
//		return -1;
		
		return getIndex(sourceClassList, c.getURI());
	}
	
	/* Find Node type of a source class in the matrix
	 * @author Ulas
	 * @param OntClass
	 * @return Node represents the OntClass
	 */
	private Node findSourceNode(OntClass c){
		Mapping aln = null;
		for(int i = 0; i < sourceOntology.getClassesList().size(); i++) {
			try{
				aln = classesMatrix.get(i, 0);
				Node currentNode = aln.getEntity1();
				OntClass currentClassS = (OntClass) currentNode.getResource().as(OntClass.class);
				if(c.equals(currentClassS)){
					return currentNode;
				}
			}
			catch(Exception e){
			}
		}
		return null;
	}
	
	/* Finds index of a target class in the matrix
	 * @author Ulas
	 * @param OntClass
	 * @return int index
	 */
	private int findTargetIndex(OntClass c){
//		Mapping aln = null;
//		for(int i = 0; i < targetOntology.getClassesList().size(); i++) {
//			try{
//				aln = classesMatrix.get(0, i);
//				Node currentNode = aln.getEntity2();
//				OntClass currentClassS = (OntClass) currentNode.getResource().as(OntClass.class);
//				if(c.equals(currentClassS)){
//					return i;
//				}
//			}
//			catch(Exception e){
//			}
//		}
//		return -1;
		
		return getIndex(targetClassList, c.getURI());
	}
	
	/* Find Node type of a target class in the matrix
	 * @author Ulas
	 * @param OntClass
	 * @return Node represents the OntClass
	 */
	private Node findTargetNode(OntClass c){
		Mapping aln = null;
		for(int i = 0; i < targetOntology.getClassesList().size(); i++) {
			try{
				aln = classesMatrix.get(0, i);
				Node currentNode = aln.getEntity2();
				OntClass currentClassT = (OntClass) currentNode.getResource().as(OntClass.class);
				if(c.equals(currentClassT)){
					return currentNode;
				}
			}
			catch(Exception e){
			}
		}
		return null;
	}
	
	private void matchSuperclasses() {
		ArrayList<Double> similarities = new ArrayList<Double>();
		//Match classes based on Superclasses and types
		for (int i = 0; i<sourceOntology.getClassesList().size(); i++) {
			Node source = sourceOntology.getClassesList().get(i);
			//You can print something once per class
			similarities = new ArrayList<Double>();
			
			double sim;
			for (int j = 0; j<targetOntology.getClassesList().size(); j++) {
				Node target = targetOntology.getClassesList().get(j);
				
				sim = superclassesComparison(source,target);
				classSimilarities[i][j].setSuperclasses(sim);
				similarities.add(sim);				
			}
			
			if(verbose)	System.out.println(similarities);
			
			int index = Utils.getOnlyMax(similarities);
			
			if(verbose){
				System.out.println("onlyMax: "+index);
				if(index!=-1) System.out.println(similarities.get(index));
			}
			
			if(index!=-1 && similarities.get(index)>=parameters.getSuperclassThreshold()){
				Mapping m = new Mapping(source, targetClassList.get(index), similarities.get(index));
				m.setProvenance(SUBCLASSOF);
				if(parameters.boostSubclassOf) m.setSimilarity(1.0d);
				classesMatrix.set(i, index, m);
				if( Core.DEBUG_FCM ) System.out.println("ALIGNMENT:"+source.getLocalName()+" "
						+targetClassList.get(index).getLocalName()+" BY SUBCLASSOF");
			}
			verbose = false;
		}			
	}
	
	private double superclassesComparison(OntClass sClass, OntClass tClass) {
		
		double subSim = 0.0d;
		
		try {
		
			List<OntClass> l1 = sClass.listSuperClasses().toList();
			List<OntClass> l2 = tClass.listSuperClasses().toList();
		
			if(l1.size()!=l2.size() || l1.size()==0) return 0.0d;
		
			double[][] sims = new double[l1.size()][l1.size()]; 
				
			for(int i=0; i<l1.size(); i++){
				OntClass c1 = (OntClass) l1.get(i);
				for(int j=0; j<l2.size(); j++){
					OntClass c2 = (OntClass) l2.get(j);
					sims[i][j] = superClassSimilarity(c1,c2);
				}
			}
			
			//Obtain best matching solution
			subSim = Utils.optimalAlignment(sims);
			
			if(verbose){
				Utils.printMatrix(sims);
				System.out.println("subSim: "+subSim);
			}
		} catch( Exception e ) {
			e.printStackTrace();
			return 0.0d;
		}
		return subSim;
	}

	private double superclassesComparison(Node source, Node target) {
		if(verbose)
		System.out.println("SuperClassesComp: "+source.getLocalName()+","+target.getLocalName());
		
		OntClass sClass = null;
		OntClass tClass = null;
		
		if(!source.getResource().canAs(OntClass.class) || !target.getResource().canAs(OntClass.class))		
			return 0.0;
			
		sClass = (OntClass) source.getResource();
		tClass = (OntClass) target.getResource();
		
		return superclassesComparison(sClass, tClass);
	}


	private double superClassSimilarity(OntClass c1, OntClass c2) {
		//System.out.println(c1+" "+c2);
		if(c1.isRestriction() && c2.isRestriction()){
			//System.out.println("RESTR");
			try {
				return restrictionSimilarity((Restriction)c1.as(Restriction.class),
						(Restriction)c2.as(Restriction.class),true);
			} catch( Exception e ) {
				e.printStackTrace();
				return 0d;
			}
		}
		if(c1.getURI()!=null && c2.getURI()!=null){
			//System.out.println("ALIGN");
			return alignedClass(c1.getURI(),c2.getURI());
		}
		return 0.0;
	}
	
	private double restrictionSimilarity(Restriction r1, Restriction r2,boolean classes) throws Exception {
		double sim = 0;
		double onProp = 0;
		
		
		try {
			if(classes)
			onProp = alignedProp(r1.getOnProperty().getURI(), r2.getOnProperty().getURI());
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		if(r1.isMaxCardinalityRestriction() && r2.isMaxCardinalityRestriction()){
			MaxCardinalityRestriction m1 = r1.asMaxCardinalityRestriction();
			MaxCardinalityRestriction m2 = r1.asMaxCardinalityRestriction();
			if(m1.getMaxCardinality()==m2.getMaxCardinality())
				sim++;
		}
		else if(r1.isMinCardinalityRestriction() && r2.isMinCardinalityRestriction()){
			MinCardinalityRestriction m1 = r1.asMinCardinalityRestriction();
			MinCardinalityRestriction m2 = r1.asMinCardinalityRestriction();
			if(m1.getMinCardinality()==m2.getMinCardinality())
				sim++;
		}
		else if(r1.isCardinalityRestriction() && r2.isCardinalityRestriction()){
			try {
				CardinalityRestriction c1 = r1.asCardinalityRestriction();
				CardinalityRestriction c2 = r2.asCardinalityRestriction();
				if(c1.getCardinality()==c2.getCardinality())
					sim++;
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		else if(r1.isAllValuesFromRestriction() && r2.isAllValuesFromRestriction()){
			AllValuesFromRestriction a1 = r1.asAllValuesFromRestriction();
			AllValuesFromRestriction a2 = r2.asAllValuesFromRestriction();
			double resSim = compareResources(a1.getAllValuesFrom(),a2.getAllValuesFrom());
			if(resSim==1 && Utils.primitiveType(a1.getAllValuesFrom().getURI()))
				resSim = 0.75;
				sim += resSim;
		}
		if(classes)
			return (sim*3+onProp)/4;
		else return sim;
	}

	private void matchRangeAndDomain() {
		for (int i = 0; i < sourcePropList.size(); i++) {
			for (int j = 0; j < targetPropList.size(); j++){
				
				double sim = rangeAndDomainSimilarity(sourcePropList.get(i),targetPropList.get(j));
				propSimilarities[i][j].setRangeAndDomain(sim);
				if(sim>=parameters.getRangeDomainThreshold()){
					if( Core.DEBUG_FCM ) System.out.println("ALIGNMENT:"+sourcePropList.get(i).getLocalName()+" "
							+targetPropList.get(j).getLocalName()+" BY RANGE/DOMAIN");
					Mapping m = new Mapping(sourcePropList.get(i),targetPropList.get(j), sim);
					m.setProvenance(RANGE_DOMAIN);
					if(parameters.boostRangeDomain) m.setSimilarity(1.0d);
					propertiesMatrix.set(i,j, m);				
				}			
			}
		}		
		return;
	}
	
	private double rangeAndDomainSimilarity(Node source, Node target) {
		if(!source.getResource().canAs(OntProperty.class) ||
				!target.getResource().canAs(OntProperty.class))
			return 0.0;
		
		OntProperty sProp = (OntProperty) source.getResource().as(OntProperty.class);
		OntProperty tProp = (OntProperty) target.getResource().as(OntProperty.class);
		
		return rangeAndDomainSimilarity(sProp, tProp);
	}
	
	private double domainSimilarity(OntResource sDom, OntResource tDom) {
		if(sDom.canAs(OntClass.class) && tDom.canAs(OntClass.class)){
			OntClass c1 = sDom.asClass();
			OntClass c2 = tDom.asClass();
			
			if(!c1.isUnionClass() || !c2.isUnionClass()){
				if(sDom.getURI()!=null && tDom.getURI()!=null){
					if(sDom.getURI().equals(tDom.getURI()))
						return 1;
					else return alignedClass(sDom.getURI(), tDom.getURI());
				}
			}
			else{
				//BOTH UNION CLASSES
				if(verbose){
					System.out.println("Both union!!");
					System.out.println(c1.getLocalName()+", "+c2.getLocalName());
				}
				UnionClass u1 = c1.asUnionClass();
				UnionClass u2 = c2.asUnionClass();
				List<? extends OntClass> l1 = u1.listOperands().toList();
				List<? extends OntClass> l2 = u2.listOperands().toList();
				
				if(l1.size() != l2.size() || l1.size()==0)
					return 0.0;
				
				
				double[][] sims = new double[l1.size()][l1.size()]; 
				
				for(int i=0; i<l1.size(); i++){
					OntResource r1 =  (OntResource)l1.get(i);
					for(int j=0; j<l2.size(); j++){
						OntResource r2 = (OntResource) l2.get(j);
						sims[i][j] = compareResources(r1, r2);
					}
				}
				if(verbose){
					System.out.println("UNION COMP:");
					Utils.printMatrix(sims);
				}
				
				//Obtain suboptimal solution
				double unionSim = Utils.optimalAlignment(sims);
				
				if(verbose){
					System.out.println("unionSim: "+unionSim);
				}
				
				if(unionSim>0) unionSim += 0.3;
				
				return unionSim;
			}
		}
		return 0;
	}
	
	public static double individualsComparison(List<Individual> sList, List<Individual> tList){
		//Look at individuals
		if(sList.size()==0 || tList.size()==0) return 0;
		
		Individual sInd;
		Individual tInd;
		
		int count = 0;
		
		for (int i = 0; i < sList.size(); i++) {
			for (int j = 0; j < tList.size(); j++) {
				sInd = sList.get(i);
				tInd = tList.get(j);
				if(!sInd.isAnon() && !tInd.isAnon()){
					if(sInd.getLocalName().equals(tInd.getLocalName())){
						count++;
					}
				}				
			}
		}
		return 2*count/(sList.size()+tList.size());
	}
	
	public double alignedClass(String sURI,String tURI){
		int s = -1;
		int t = -1;
		for (int i = 0; i < sourceClassList.size(); i++) {
			if(sourceClassList.get(i).getUri().equals(sURI))
				s = i;
		}
		if(s==-1) return 0.0;
		for (int i = 0; i < targetClassList.size(); i++) {
			if(targetClassList.get(i).getUri().equals(tURI))
				t = i;
		}
		if(t==-1) return 0.0;
		return classesMatrix.getSimilarity(s, t);
	}
	
	public double alignedProp(String sURI,String tURI){
		int s = -1;
		int t = -1;
		for (int i = 0; i < sourcePropList.size(); i++) {
			if(sourcePropList.get(i).getUri().equals(sURI))
				s = i;
		}
		if(s==-1) return 0.0;
		for (int i = 0; i < targetPropList.size(); i++) {
			if(targetPropList.get(i).getUri().equals(tURI))
				t = i;
		}
		if(t==-1) return 0.0;
		return propertiesMatrix.getSimilarity(s, t);
	}
		
	private int getIndex( List<Node> list, String uri) {
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getUri().equals(uri))
				return i;
		}
		return -1;
	}

	private double compareResources(Resource r1, Resource r2){
		String uri1 = r1.getURI();
		String uri2 = r2.getURI();
		if(uri1==null || uri2==null) return 0.0;
		if(uri1.equals(uri2))
			return 1.0;
		double simClass = alignedClass(uri1,uri2);
		double simProp = alignedProp(uri1,uri2);
		if(simClass > simProp)
		return simClass;
		else return simProp;
	}
	
	private double getNumberOfClassAlignments() {
		double[][] matrix = classesMatrix.getCopiedSimilarityMatrix();
		double sum = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				sum += matrix[i][j];
			}
		}
		return sum;
	}
	
	private double getNumberOfPropAlignments() {
		double[][] matrix = propertiesMatrix.getCopiedSimilarityMatrix();
		double sum = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				sum += matrix[i][j];
			}
		}
		return sum;
	}
	
	private void printPropValues() {
		Iterator<OntProperty> it = sourcePropValues.keySet().iterator();
		while(it.hasNext()){
			OntProperty prop = it.next();
			//System.out.println(prop);
			//System.out.println(sourcePropValues.get(prop));
		}
		//System.out.println("TARGET");
		//System.out.println(targetPropValues);
		Iterator<OntProperty> it2 = targetPropValues.keySet().iterator();
		while(it2.hasNext()){
			OntProperty prop = it2.next();
			//System.out.println(prop);
			//System.out.println(targetPropValues.get(prop));
		}	
	}
	
	
	
	/**
	 * Input must be a Node representing a class. (i.e. Node.isClass() == true)	
	 * @param currentNode Node object representing a class.
	 * @return List of OntResource object representing the individuals.
	 */
	public ArrayList<Individual> getIndividuals( Node currentNode ) {
		
		ArrayList<Individual> individualsList = new ArrayList<Individual>(); 
		
		OntClass currentClass = (OntClass) currentNode.getResource().as(OntClass.class);
		
		ExtendedIterator indiIter = currentClass.listInstances(true);
		while( indiIter.hasNext() ) {
			Individual ci = (Individual) indiIter.next();
			
			//if( ci.isAnon() ) System.out.println("\n************************\nProperties of individual:" + ci.getId() );
			//else System.out.println("\n************************\nProperties of individual:" + ci.getLocalName() );
			
			StmtIterator indiPropertiesIter = ci.listProperties();
			while( indiPropertiesIter.hasNext() ) {
				Statement currentProperty = indiPropertiesIter.nextStatement();
				//System.out.println(currentProperty);
			}
			
			individualsList.add( ci );
		}
		
		// try to deal with improperly declared individuals. (from the 202 scrambled ontology) 
		if( individualsList.isEmpty() ) {
			
			
			OntModel mod = (OntModel) currentClass.getModel();

			List<Statement> ls = mod.listStatements(null , mod.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), mod.getResource(currentClass.getLocalName())).toList();
			
			Iterator<Statement> lsiter = ls.iterator();
			int k = 1;
			while( lsiter.hasNext() ) {
				Statement s = lsiter.next();
				Resource r = s.getSubject();
				if( r.canAs(Individual.class) ) {
					Individual indi = r.as(Individual.class);
					Individual ci = indi;
					
					//if( ci.isAnon() ) System.out.println("\n************************\nProperties of individual:" + ci.getId() );
					//else System.out.println("\n************************\nProperties of individual:" + ci.getLocalName() );
					
					StmtIterator indiPropertiesIter = ci.listProperties();
					while( indiPropertiesIter.hasNext() ) {
						Statement currentProperty = indiPropertiesIter.nextStatement();
						RDFNode currentnode = currentProperty.getObject();
						if( currentnode.canAs(Literal.class) )  {
							Literal currentLiteral = (Literal) currentnode.as(Literal.class); 
							currentLiteral.getString();
						}
						//System.out.println(currentProperty);
					}
					
					individualsList.add(indi);
				}
			}
		}	
		return individualsList;
	}
	
	private boolean matchIndividuals(Node source, Node target) {
		boolean classMatched = false;
		boolean individualMatched = false;
		
		ArrayList<Individual> sourceIndi = getIndividuals(source);
		ArrayList<Individual> targetIndi = getIndividuals(target);

		for (Individual iSource: sourceIndi){
			for (Individual iTarget: targetIndi) {
				if (!iSource.isAnon() && !iTarget.isAnon()) { //if neither is anonymous
					if (iSource.getLocalName().equals(iTarget.getLocalName())) {
						classMatched = true;
						individualMatched = recursiveMatchIndividuals(iSource, iTarget);
					}
				}
				else if (iSource.isAnon() && iTarget.isAnon()) { //both anonnymous
					individualMatched = recursiveMatchIndividuals(iSource, iTarget); //prop?
				}
				if (individualMatched) classMatched = true;
			}
		}
		return classMatched;
	}

	private boolean recursiveMatchIndividuals(Individual iSource, Individual iTarget) {
		boolean IndividualsMatched = false;
		boolean propertyMatched = false;

		List<Statement> sourceProperties = iSource.listProperties().toList();
		List<Statement> targetProperties = iTarget.listProperties().toList();
		//	for (Statement s: sourceProperties) System.out.println(s);

		for(int i=0;i<sourceProperties.size();i++){
			for(int j=0;j<targetProperties.size();j++){
				Statement sourceProperty = sourceProperties.get(i);
				Statement targetProperty = targetProperties.get(j);
				propertyMatched = false;
				if (sourceProperty.getObject().isAnon() && targetProperty.getObject().isAnon()) {
					//						RDFNode subject = sourceProperty.getSubject();
					//						RDFNode object = sourceProperty.getObject();
					//						RDFNode prop = sourceProperty.getPredicate();

					propertyMatched = recursiveMatchIndividuals((Individual)(sourceProperty.getObject().as(Individual.class)), 
							(Individual)(targetProperty.getObject().as(Individual.class)));
				}
				
				else {
					Triple sourcePropTriple = (sourceProperty).asTriple();
					Triple targetPropTriple = (targetProperty).asTriple();
					//System.out.println("s:"+sourcePropTriple);
					//System.out.println("t:"+targetPropTriple);
					if(sourcePropTriple.getObject().equals(targetPropTriple.getObject())){
						//System.out.println("EQUALS");
						//System.out.println(sourcePropTriple.getPredicate().getURI());
						String uri1 = sourcePropTriple.getPredicate().getURI();
						String uri2 = targetPropTriple.getPredicate().getURI();
						if(uri2.length()<20)
							uri2 = targetOntology.getURI() + uri2;
						//System.out.println(uri2);
						Node source = get(sourcePropList, uri1);
						//System.out.println(source);
						Node target = get(targetPropList, uri2);
						//System.out.println(target);
						if (source != null && target != null) {
							Mapping m = new Mapping(source, target, 1.0d);
							m.setProvenance(RECURSIVE_INDIVIDUALS);
							propertiesMatrix.set(sourcePropList.indexOf(source),  targetPropList.indexOf(target), m);
							//propertiesMatrix.setSimilarity(sourcePropList.indexOf(source), targetPropList.indexOf(target), 1.0);
						}
						propertyMatched = true;
					}
				}
				if (propertyMatched) IndividualsMatched = true;
			}	
		}
		return IndividualsMatched;
	}

	private Node get( List<Node> nodeList, String uri) {
		int ind = getIndex(nodeList, uri);
		if(ind!=-1)
			return nodeList.get(ind);
		return null;
	}
	
	public ClassSimilarity getClassSimilarity(Mapping mapping){
		if(mapping.getAlignmentType().equals(alignType.aligningProperties)) return null;
		return classSimilarities[mapping.getSourceKey()][mapping.getTargetKey()];
	}
	
	public PropertySimilarity getPropertySimilarity(Mapping mapping){
		if(mapping.getAlignmentType().equals(alignType.aligningClasses)) return null;
		return propSimilarities[mapping.getSourceKey()][mapping.getTargetKey()];
	}
	
	public void setUseIndividuals(boolean useIndividuals){
		individuals = useIndividuals;
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel(){
		if(parametersPanel == null){
			parametersPanel = new IterativeInstanceStructuralParametersPanel();
		}
		return parametersPanel;
	}
	
	public String allSimilarities(Alignment<Mapping> mappings){
		
		
		String ret = "Source\tTarget\tSimilarity\tSyntactic\tRestrictions\tSuperclasses\tSubclasses\tProvenance\n";
			
		for(Mapping mapping: mappings){
			if(mapping.getAlignmentType() == alignType.aligningClasses)
			ret += classMappingTuple(mapping) + "\n";
		}
			
		ret += "Source\tTarget\tSimilarity\tSyntactic\tRangeDomain\tValues\tSubproperties\tProvenance\n";
			
		for(Mapping mapping: mappings){
			if(mapping.getAlignmentType() == alignType.aligningProperties)
			ret += propertyMappingTuple(mapping) + "\n";
		}
				
		return ret;
	}	

	public String allSimilarities(alignType type, boolean onlyMappings){
		if(classesAlignmentSet==null || propertiesAlignmentSet==null)
			return null;
		
		
		if(type == alignType.aligningClasses){
			String ret = "Source\tTarget\tSimilarity\tSyntactic\tRestrictions\tSuperclasses\tSubclasses\tProvenance\n";
			
			for(Mapping mapping: classesAlignmentSet){
				ret += classMappingTuple(mapping) + "\n";
			}
			
			return ret;
		}
		
		else if(type == alignType.aligningProperties){
			String ret = "Source\tTarget\tSimilarity\tSyntactic\tRangeDomain\tValues\tSubproperties\tProvenance\n";
			
			PropertySimilarity sim = null;
			
			for(Mapping mapping: propertiesAlignmentSet){
				ret += propertyMappingTuple(mapping) + "\n";
			}
			
			return ret;
		}
		
		return null;
	}
	
	public String classMappingTuple(Mapping mapping){
		String ret = mapping.getEntity1().getLocalName() + "\t" + mapping.getEntity2().getLocalName() +
		"\t" + mapping.getSimilarity();
		ClassSimilarity sim = getClassSimilarity(mapping);
		ret += "\t" + sim.getSyntactic() + "\t" + sim.getRestrictions() + "\t" + sim.getSuperclasses()
			+ "\t" + sim.getSubclasses();
		if(mapping.getProvenance()!=null) ret += "\t" + mapping.getProvenance();
		else ret += "\t" + SYNTACTIC;
		return ret;
	}
	
	public String propertyMappingTuple(Mapping mapping){
		String ret = mapping.getEntity1().getLocalName() + "\t" + mapping.getEntity2().getLocalName() +
		"\t" + mapping.getSimilarity();
		PropertySimilarity sim = getPropertySimilarity(mapping);
		ret += "\t" + sim.getSyntactic() + "\t" + sim.getRangeAndDomain() + "\t" + sim.getValues() + 
			"\t" + sim.getSubProperties();
		if(mapping.getProvenance()!=null) ret += "\t" + mapping.getProvenance();
		else ret += "\t" + SYNTACTIC;
		return ret;
	}
	
	private void printAllSimilarities() {
		for (int i = 0; i < classSimilarities.length; i++) {
			for (int j = 0; j < classSimilarities[0].length; j++) {
				System.out.println(sourceClassList.get(i)+" "+targetClassList.get(j)
						+classesMatrix.getSimilarity(i, j));
				System.out.println(classSimilarities[i][j]);
			}
		}
		for (int i = 0; i < propSimilarities.length; i++) {
			for (int j = 0; j < propSimilarities[0].length; j++) {
				System.out.println(sourcePropList.get(i)+" "+targetPropList.get(j)
						+ " " + propertiesMatrix.getSimilarity(i, j));
				System.out.println(propSimilarities[i][j]);
			}
		}
	}
}