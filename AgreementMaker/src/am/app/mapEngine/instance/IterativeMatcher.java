package am.app.mapEngine.instance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.TransitiveProperty;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.ontology.Node;

/**
 * This class matches properties of ontologies using instance information.
 * Proceeds looking at properties and iterates as it finds class and property mappings.
 * @author Ulas
 *
 */

public class IterativeMatcher extends AbstractMatcher{
	
	OntModel modelS;
	OntModel modelT;
	
	ArrayList<OntProperty> matchedPropsS;
	ArrayList<OntProperty> matchedPropsT;
	ArrayList<OntClass> matchedClassesS;
	ArrayList<OntClass> matchedClassesT;
	ArrayList<Individual> matchedIndividualsS;
	ArrayList<Individual> matchedIndividualsT;
	
	ArrayList<Individual> individualsS;
	ArrayList<Individual> individualsT;
	ArrayList<Statement> statementsS;
	ArrayList<Statement> statementsT;

	public IterativeMatcher() {
		super();
		needsParam = false;
		alignProp = true;		
	}
	
	protected void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		
		modelS = sourceOntology.getModel();
		modelT = targetOntology.getModel();
		
		matchedPropsS = new ArrayList<OntProperty>();
		matchedPropsT = new ArrayList<OntProperty>();
		matchedClassesS = new ArrayList<OntClass>();
		matchedClassesT = new ArrayList<OntClass>();
		matchedIndividualsS = new ArrayList<Individual>();
		matchedIndividualsT = new ArrayList<Individual>();
		
		
		individualsS = new ArrayList<Individual>();
		individualsT = new ArrayList<Individual>();
		statementsS = new ArrayList<Statement>();
		statementsT = new ArrayList<Statement>();
		
		
		//Matching methods here.
		matchTransitiveProperties();
		//0-1
		for(int i = 0; i < 2; i++){
			//0-1
			matchClassesUsingInstances();
			//9-1
			//Precision = Correct/Discovered: 100.0%
			//Recall = Correct/Reference: 10.1%
			//Fmeasure = 2(precision*recall)/(precision+recall): 18.3%
			
			matchPropertiesUsingInstances();
			//9-8
			//Precision = Correct/Discovered: 100.0%
			//Recall = Correct/Reference: 17.2%
			//Fmeasure = 2(precision*recall)/(precision+recall): 29.3%
			
			matchUnionClasses();
			//13-8
			//Precision = Correct/Discovered: 100.0%
			//Recall = Correct/Reference: 21.2%
			//Fmeasure = 2(precision*recall)/(precision+recall): 35.0%
			
			matchObjectPropertiesUsingDomainAndRange();
			//13-16
			//Precision = Correct/Discovered: 100.0%
			//Recall = Correct/Reference: 29.3%
			//Fmeasure = 2(precision*recall)/(precision+recall): 45.3%
			
			matchObjectPropertiesUsingAnnotations();
			//14-16
			//Precision = Correct/Discovered: 100.0%
			//Recall = Correct/Reference: 30.3%
			//Fmeasure = 2(precision*recall)/(precision+recall): 46.5%
			
			matchDatatypePropertiesUsingAnnotations();
			
			
			
			//matchByDefinedResources();
			
			//matchPropertiesUsingClasses();
			
			//matchClassesUsingProperties();
			
			//matchSuperClasses();
			
			//matchDataProperties();
		}
	}

	
	
	protected Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		
		String tURI, sURI;
		sURI = sourceOntology.getURI();
		tURI = targetOntology.getURI();
		
		
		if(!source.getUri().contains(sURI) || !target.getUri().contains(tURI)){
			return new Alignment(source, target, 0, Alignment.EQUIVALENCE);
		}

		if(!source.getUri().contains(sURI) && !source.getUri().equalsIgnoreCase(target.getUri()) ) {
			//System.out.println("External Node: " + source.getLocalName());
			return new Alignment(source, target, 0, Alignment.EQUIVALENCE);
		}
		else if (!target.getUri().contains(tURI) && !target.getUri().equalsIgnoreCase(source.getUri())){
			//System.out.println("External Node: " + target.getLocalName());
			return new Alignment(source, target, 0, Alignment.EQUIVALENCE);
		}
		
		
		if(typeOfNodes.equals(alignType.aligningProperties)){
			for(int k = 0; k < matchedPropsS.size(); k++){
				if(source.getLocalName().equalsIgnoreCase(matchedPropsS.get(k).getLocalName())){
					if(target.getLocalName().equalsIgnoreCase(matchedPropsT.get(k).getLocalName())){
						return new Alignment(source, target, 99.0, Alignment.EQUIVALENCE);
					}
				}
			}
		}
		else{
			for(int x = 0; x < matchedClassesS.size(); x++){
				if( source.getLocalName().equalsIgnoreCase( matchedClassesS.get(x).getLocalName() ) ){
					if(target.getLocalName().equalsIgnoreCase( matchedClassesT.get(x).getLocalName() )){
						return new Alignment(source, target, 99.0, Alignment.EQUIVALENCE);
					}
				}
			}
		}
		
		return new Alignment(source, target, 0.0, Alignment.EQUIVALENCE);
		
	}
	
	//Matches Instances then
	//Adds to Classes
	public void matchClassesUsingInstances(){
		//Save individuals of Source and Target
		ExtendedIterator<Individual> indS= modelS.listIndividuals();
		while(indS.hasNext()){
			individualsS.add(indS.next());
		}
		
		ExtendedIterator<Individual> indT= modelT.listIndividuals();
		while(indT.hasNext()){
			individualsT.add(indT.next());
		}
		
		//
		for(int i = 0; i < individualsS.size(); i++){
			Individual iS = individualsS.get(i);
			for(int j = 0; j < individualsT.size(); j++){
				Individual iT = individualsT.get(j);
				
				//TODO: isAnon()
				if(iS.isAnon() && iT.isAnon()){
					
				}
				else{
					String s = iS.getLocalName();
					String t = iT.getLocalName();
					if(s != null && t != null && s.equals(t)){
						matchedIndividualsS.add(iS);
						matchedIndividualsT.add(iT);
						OntClass sc = iS.getOntClass();
						OntClass tc = iT.getOntClass();
						if(!matchedClassesS.contains(sc) && !matchedClassesT.contains(tc)){
							if(!matchedClassesS.contains(sc)){
								mapTwoOntClasses(sc, tc);
							}
							
						}
					}
				}
			}
		}
	}
	
	//Adds to Properties
	public void matchTransitiveProperties(){
		ExtendedIterator<TransitiveProperty> tps = modelS.listTransitiveProperties();
		int numOfTransPropsS = 0;
		TransitiveProperty tp1 = null;
		while(tps.hasNext()){
			tp1 = tps.next();
			numOfTransPropsS++;
		}
		ExtendedIterator<TransitiveProperty> tpt = modelT.listTransitiveProperties();
		int numOfTransPropsT = 0;
		TransitiveProperty tp2 = null;
		while(tpt.hasNext()){
			tp2 = tpt.next();
			numOfTransPropsT++;
		}
		if(tp1 != null && tp2 != null && numOfTransPropsS == 1 && numOfTransPropsT == 1){
			matchedPropsS.add(tp1);
			matchedPropsT.add(tp2);
			matchDeclaringClasses(tp1, tp2);
		}
	}
	
	//Adds to Classes
	public void matchUnionClasses(){
		ArrayList<UnionClass> unionClassesS = new ArrayList<UnionClass>();
		ArrayList<UnionClass> unionClassesT = new ArrayList<UnionClass>();
		
		ExtendedIterator<UnionClass> its = modelS.listUnionClasses();
		
		while(its.hasNext()){
			UnionClass uc = its.next();
			unionClassesS.add(uc);
			//System.out.println(uc.getLocalName() + " ." + uc.toString());
		}
		
		ExtendedIterator<UnionClass> itt = modelT.listUnionClasses();
		
		while(itt.hasNext()){
			UnionClass uc = itt.next();
			unionClassesT.add(uc);
			//System.out.println(uc.getLocalName() + " ." + uc.toString());
		}
		
		//For each union class, match.
		for(int i = 0; i < unionClassesS.size(); i++){
			UnionClass uc = unionClassesS.get(i);
			ArrayList<OntClass> list = new ArrayList<OntClass>();
			
			for (ExtendedIterator e = uc.listOperands(); e.hasNext(); ) {
				Resource r0 = (Resource) e.next();
				OntClass unionMember = (OntClass) r0.as( OntClass.class );
				list.add(unionMember);
				//System.out.print(" " + unionMember.getLocalName());
			}
			
			for(int j = 0; j < unionClassesT.size(); j++){
				UnionClass uc2 = unionClassesT.get(j);
				ArrayList<OntClass> list2 = new ArrayList<OntClass>();

				for (ExtendedIterator ei = uc2.listOperands(); ei.hasNext(); ) {
					Resource r1 = (Resource) ei.next();
					OntClass unionMember2 = (OntClass) r1.as( OntClass.class );
					list2.add(unionMember2);
					//System.out.print(" " + unionMember2.getLocalName());
				}
			
				//Match HERE
				//I have the list of classes constitutes the union class
				if(list.size() == 2 && list2.size() == 2){
					
					if(matchedClassesS.contains(list.get(0))){
						if(matchedClassesT.contains(list2.get(0)) && matchedClassesT.get( matchedClassesS.indexOf(list.get(0)) ).equals(list2.get(0))){
							if(!matchedClassesS.contains(list.get(1))){
								mapTwoOntClasses(list.get(1), list2.get(1));
							}
						}
						else if(matchedClassesT.contains(list2.get(1)) && matchedClassesT.get( matchedClassesS.indexOf(list.get(0)) ).equals(list2.get(1))){
							if(!matchedClassesS.contains(list.get(1))){
								mapTwoOntClasses(list.get(1), list2.get(0));
							}
						}
					}
					else if(matchedClassesS.contains(list.get(1))){
						if(matchedClassesT.contains(list2.get(0)) && matchedClassesT.get( matchedClassesS.indexOf(list.get(1)) ).equals(list2.get(0))){
							if(!matchedClassesS.contains(list.get(0))){
								mapTwoOntClasses(list.get(0), list2.get(1));
							}
						}
						if(matchedClassesT.contains(list2.get(1)) && matchedClassesT.get( matchedClassesS.indexOf(list.get(1)) ).equals(list2.get(1))){
							if(!matchedClassesS.contains(list.get(0))){
								mapTwoOntClasses(list.get(0), list2.get(0));
							}
						}
					}
				}
				else if(list.size() == 3 && list2.size() == 3){
					
				}
			
			}
		
		}
		
	}

	//Adds to Classes
	public void matchClassesUsingProperties(){
		ExtendedIterator<OntClass> cls = modelS.listClasses();
		while(cls.hasNext()){
			OntClass c = cls.next();
			if(c.isAnon()) continue;
			//If the class is not already matched
			if(!matchedClassesS.contains(c)){
				ExtendedIterator<OntClass> clt = modelT.listClasses();
				while(clt.hasNext()){
					OntClass b = clt.next();
					if(b.isAnon()) continue;
					if(!matchedClassesT.contains(b)){
						ExtendedIterator<OntProperty> opIterS = c.listDeclaredProperties(true);
						ExtendedIterator<OntProperty> opIterT = b.listDeclaredProperties(true);
						
						boolean matched = false;
						while(opIterS.hasNext()){
							OntProperty op1 = opIterS.next();
							OntProperty op2 = null;
							if(opIterT.hasNext()){
								op2 = opIterT.next();
							}
							else break;
							
							if(op1 != null && op2 != null){
								if(matchedPropsS.contains(op1)){
									if(matchedPropsT.contains(op2) && matchedPropsT.get( matchedPropsS.indexOf(op1) ).equals(op2) ){
										if(!matchedClassesS.contains(c)){
											mapTwoOntClasses(c, b);
											matched = true;
											break;
										}
									}
								}
							}
							
						}
						if(matched) break;
					}
					
				}
			}
			
		}
		
	}
	
	//Adds to Properties
	public void matchPropertiesUsingInstances(){
		String sURI = sourceOntology.getURI();
		String tURI = targetOntology.getURI();
		
		for(int i = 0; i < matchedIndividualsS.size(); i++){
			Individual indS = matchedIndividualsS.get(i);
			Individual indT = matchedIndividualsT.get(i);
			StmtIterator stmtIt1 = indS.listProperties();
			StmtIterator stmtIt2 = indT.listProperties();

			while(stmtIt1.hasNext()){
				Statement stmt1 = stmtIt1.next();
				Property p1 = stmt1.getPredicate();
				RDFNode n1 = stmt1.getObject();
				String objectvalue1 = n1.toString();
				OntProperty op1 = modelS.getOntProperty(sURI + p1.getLocalName());

				if(op1 == null) continue;

				//Property op = stmt1.getPredicate();
				Statement stmt2 = stmtIt2.next();
				Property p2 = stmt2.getPredicate();
				RDFNode n2 = stmt2.getObject();
				String objectvalue2 = n2.toString();
				OntProperty op2 = modelT.getOntProperty(tURI + p2.getLocalName());

				if(op2 == null) continue;

				if(objectvalue1.equals(objectvalue2)){
					if(!matchedPropsS.contains(op1) && !matchedPropsT.contains(op2))
					{
							matchedPropsS.add(op1);
							matchedPropsT.add(op2);
					}
					matchDeclaringClasses(op1, op2);
				}

				//System.out.println(stmt1 + "\n" + stmt2);
			}
			
		}
	}
	
	public void matchObjectPropertiesUsingDomainAndRange(){
		ExtendedIterator<ObjectProperty> itS = modelS.listObjectProperties();
		while(itS.hasNext()){
			ObjectProperty opS = itS.next();
			if(matchedPropsS.contains(opS)) continue;
			
			ExtendedIterator<ObjectProperty> itT = modelT.listObjectProperties();
			while(itT.hasNext()){
				ObjectProperty opT = itT.next();
				if(matchedPropsT.contains(opT)) continue;
				
				if(isDomainsAreSame(opS, opT) && isRangesAreSame(opS, opT)){
					if(opS.getDomain() == null && opT.getDomain() == null && opS.getRange() == null && opT.getRange() == null ){}
					else{
						matchedPropsS.add(opS);
						matchedPropsT.add(opT);
						matchDeclaringClasses(opS, opT);
						break;
					}
				}
			}
		}
	}
	
	//Adds to Properties
	public void matchObjectPropertiesUsingDomainAndRange2(){		
		ExtendedIterator<ObjectProperty> itS = modelS.listObjectProperties();
		while(itS.hasNext()){
			ObjectProperty opS = itS.next();
			if(matchedPropsS.contains(opS)) continue;
			
			ExtendedIterator<ObjectProperty> itT = modelT.listObjectProperties();
			while(itT.hasNext()){
				ObjectProperty opT = itT.next();
				if(matchedPropsT.contains(opT)) continue;
				
				OntResource domS = opS.getDomain();
				OntResource domT = opT.getDomain();
				OntResource rngS = opS.getRange();
				OntResource rngT = opT.getRange();
				
				if(domS == null) break;
				if(domT == null) break;
				
				boolean uniondS = false;
				boolean uniondT = false;
				boolean unionrS = false;
				boolean unionrT = false;
				
				ArrayList<OntClass> domainsS = new ArrayList<OntClass>();
				ArrayList<OntClass> domainsT = new ArrayList<OntClass>();
				ArrayList<OntClass> rangesS = new ArrayList<OntClass>();
				ArrayList<OntClass> rangesT = new ArrayList<OntClass>();
				
				//Put source domain classes in a list
				if(domS != null){
					if (domS.isClass() && domS.asClass().isUnionClass()) {
						for (Iterator i = domS.asClass().asUnionClass().listOperands(); i.hasNext();) {
							OntClass c = (OntClass)i.next();
							domainsS.add(c);
							uniondS = true;
						}
					}
					else if(domS.isClass() && !domS.asClass().isUnionClass()) {
						OntClass c = domS.asClass();
						domainsS.add(c);
					}
				}
				
				//Put target domain classes in a list
				if(domT != null){
					if (domT.isClass() && domT.asClass().isUnionClass()) {
						for (Iterator i = domT.asClass().asUnionClass().listOperands(); i.hasNext();) {
							OntClass c = (OntClass)i.next();
							domainsT.add(c);
							uniondT = true;
						}
					}
					else if(domT.isClass() && !domT.asClass().isUnionClass()) {
						OntClass c = domT.asClass();
						domainsT.add(c);
					}
				}
				
				//Put source range classes in a list
				if(rngS != null){
					if (rngS.isClass() && rngS.asClass().isUnionClass()) {
						for (Iterator i = rngS.asClass().asUnionClass().listOperands(); i.hasNext();) {
							OntClass c = (OntClass)i.next();
							rangesS.add(c);
							unionrS = true;
						}
					}
					else if(rngS.isClass() && !rngS.asClass().isUnionClass()) {
						OntClass c = rngS.asClass();
						rangesS.add(c);
					}
				}
				
				//Put target range classes in a list
				if(rngT != null){
					if (rngT.isClass() && rngT.asClass().isUnionClass()) {
						for (Iterator i = rngT.asClass().asUnionClass().listOperands(); i.hasNext();) {
							OntClass c = (OntClass)i.next();
							rangesT.add(c);
							unionrT = true;
						}
					}
					else if(rngT.isClass() && !rngT.asClass().isUnionClass()) {
						OntClass c = rngT.asClass();
						rangesT.add(c);
					}
				}
				
				
				
				//MATCH HERE BEGIN
				if(domS == null){
					if(domT == null){
						//look at ranges, if range is null
						if(rngS == null && rngT == null && opS.getComment(null).equals(opT.getComment(null))){
							matchedPropsS.add(opS);
							matchedPropsT.add(opT);
							matchDeclaringClasses(opS, opT);
							break;
						}
						//if range is single class
						else if(!unionrS && !unionrT){
							if( matchedClassesS.contains(( rangesS.get(0) ) ) ){
								int index = matchedClassesS.indexOf(rangesS.get(0));
								if(matchedClassesT.get(index).equals( rangesT.get(0) ) ){
									matchedPropsS.add(opS);
									matchedPropsT.add(opT);
									matchDeclaringClasses(opS, opT);
									break;
								}
							}
						}
						//if range is union class
						else{
							OntClass s1 = rangesS.get(0);
							OntClass s2 = rangesS.get(1);
							OntClass t1 = rangesT.get(0);
							OntClass t2 = rangesT.get(1);
							
							if( matchedClassesS.contains(s1) && matchedClassesS.contains(s2) ){
								int index1 = matchedClassesS.indexOf(s1);
								int index2 = matchedClassesS.indexOf(s2);
								if(t1.equals( matchedClassesT.get(index1)) && t2.equals( matchedClassesT.get(index2)) ){
									matchedPropsS.add(opS);
									matchedPropsT.add(opT);
									matchDeclaringClasses(opS, opT);
									break;
								}
							}
						}
					}
				}
				else{
					//if domain is single class
					if(!uniondS && !uniondT){
						if( matchedClassesS.contains(( domainsS.get(0) ) ) ){
							int index = matchedClassesS.indexOf(domainsS.get(0));
							if(matchedClassesT.get(index).equals( domainsT.get(0) ) ){
								//look at ranges, if range is null
								if(rngS == null && rngT == null){
									matchedPropsS.add(opS);
									matchedPropsT.add(opT);
									matchDeclaringClasses(opS, opT);
									break;
								}
								//if range is single class
								else if(!unionrS && !unionrT){
									if( matchedClassesS.contains(( rangesS.get(0) ) ) ){
										int index2 = matchedClassesS.indexOf(rangesS.get(0));
										if(matchedClassesT.get(index2).equals( rangesT.get(0) ) ){
											matchedPropsS.add(opS);
											matchedPropsT.add(opT);
											matchDeclaringClasses(opS, opT);
											break;
										}
									}
								}
								//if range is union class
								else{
									OntClass s1 = rangesS.get(0);
									OntClass s2 = rangesS.get(1);
									OntClass t1 = rangesT.get(0);
									OntClass t2 = rangesT.get(1);
									
									if( matchedClassesS.contains(s1) && matchedClassesS.contains(s2) ){
										int index1 = matchedClassesS.indexOf(s1);
										int index2 = matchedClassesS.indexOf(s2);
										if(t1.equals( matchedClassesT.get(index1)) && t2.equals( matchedClassesT.get(index2)) ){
											matchedPropsS.add(opS);
											matchedPropsT.add(opT);
											matchDeclaringClasses(opS, opT);
											break;
										}
									}
								}
							}
						}
					}
					//domain is union class
					else if(uniondS && uniondT) {
						OntClass s1 = domainsS.get(0);
						OntClass s2 = domainsS.get(1);
						OntClass t1 = domainsT.get(0);
						OntClass t2 = domainsT.get(1);//This gives outofbounds error in 304
						
						if( matchedClassesS.contains(s1) && matchedClassesS.contains(s2) ){
							int index1 = matchedClassesS.indexOf(s1);
							int index2 = matchedClassesS.indexOf(s2);
							if(t1.equals( matchedClassesT.get(index1)) && t2.equals( matchedClassesT.get(index2)) ){
								//look at ranges, if range is null
								if(rngS == null && rngT == null){
									matchedPropsS.add(opS);
									matchedPropsT.add(opT);
									matchDeclaringClasses(opS, opT);
									break;
								}
								//if range is single class
								else if(!unionrS && !unionrT){
									if( matchedClassesS.contains(( rangesS.get(0) ) ) ){
										int index = matchedClassesS.indexOf(rangesS.get(0));
										if(matchedClassesT.get(index).equals( rangesT.get(0) ) ){
											matchedPropsS.add(opS);
											matchedPropsT.add(opT);
											matchDeclaringClasses(opS, opT);
											break;
										}
									}
								}
								//if range is union class
								else{
									OntClass s5 = rangesS.get(0);
									OntClass s6 = rangesS.get(1);
									OntClass t5 = rangesT.get(0);
									OntClass t6 = rangesT.get(1);
									
									if( matchedClassesS.contains(s5) && matchedClassesS.contains(s6) ){
										int index5 = matchedClassesS.indexOf(s5);
										int index6 = matchedClassesS.indexOf(s6);
										if(t5.equals( matchedClassesT.get(index5)) && t6.equals( matchedClassesT.get(index6)) ){
											matchedPropsS.add(opS);
											matchedPropsT.add(opT);
											matchDeclaringClasses(opS, opT);
											break;
										}
									}
								}
							}
						}
					}
				}
				
				//MATCH HERE END
				
			}
		}
	}
	
	//Adds to Properties
	public void matchDataPropertiesUsingDomainAndRange(){
		
	}
	
	//Adds to Properties
	
	public void matchPropertiesUsingClasses(){
		
	}
	
	
	//Changes correct Proceedings mapping with Book mapping? 
	public void matchObjectPropertiesUsingAnnotations(){
		ExtendedIterator<ObjectProperty> itS = modelS.listObjectProperties();
		while(itS.hasNext()){
			ObjectProperty opS = itS.next();
			
			if(!matchedPropsS.contains(opS)){
				ExtendedIterator<ObjectProperty> itT = modelT.listObjectProperties();
				
				while(itT.hasNext()){
					ObjectProperty opT = itT.next();
					if(!matchedPropsT.contains(opT)){
						String cS = opS.getComment(null);
						String cT = opT.getComment(null);
						if(cS != null && cT != null && cS.equals(cT)){
							if( isDomainsAreSame(opS, opT) && isRangesAreSame(opS, opT)){
								matchedPropsS.add(opS);
								matchedPropsT.add(opT);
								matchDeclaringClasses(opS, opT);
							}
						}
					}
				}
			}
		}	
	}
	
	//Adds to Properties
	//TODO: "numberOrVolume" and "dzezd" have the same comment but NOT MATCHED!
	//TODO: Add label checking if needed. Improves recall if it is used as a single matcher.
	public void matchDatatypePropertiesUsingAnnotations(){
		ExtendedIterator<DatatypeProperty> itS = modelS.listDatatypeProperties();
		while(itS.hasNext()){
			DatatypeProperty opS = itS.next();
			
			if(!matchedPropsS.contains(opS)){
				ExtendedIterator<DatatypeProperty> itT = modelT.listDatatypeProperties();
				
				while(itT.hasNext()){
					DatatypeProperty opT = itT.next();
					if(!matchedPropsT.contains(opT)){
						String cS = opS.getComment(null);
						String cT = opT.getComment(null);
						if(cS != null && cT != null && cS.equals(cT)){
							if(hasSameDataRange(opS, opT)){
								matchedPropsS.add(opS);
								matchedPropsT.add(opT);
								matchDeclaringClasses(opS, opT);
								break;
							}
						}
					}
				}
			}
		}		
	}
	
	//Each property is defined by some resources, find and match them.
	public void matchByDefinedResources(){
		ExtendedIterator<ObjectProperty> itS = modelS.listObjectProperties();
		while(itS.hasNext()){
			ObjectProperty opS = itS.next();
			if(!matchedPropsS.contains(opS)){
				
				ExtendedIterator<? extends OntClass> cls = opS.listDeclaringClasses(true);
				List<? extends OntClass> ls = cls.toList();
				if(ls.size() == 1){
					OntClass c = ls.get(0);
					if(!matchedClassesS.contains(c)) continue;
					
					ExtendedIterator<ObjectProperty> itT = modelT.listObjectProperties();
					while(itT.hasNext()){
						ObjectProperty opT = itT.next();
						if(!matchedPropsT.contains(opT)){
							
							ExtendedIterator<? extends OntClass> clt = opT.listDeclaringClasses(true);
							List<? extends OntClass> lt = clt.toList();
							if(lt.size() == 1){
								OntClass ct = lt.get(0);
								
								if(!matchedClassesT.contains(ct)) continue;
									
								if(matchedClassesS.indexOf(c) == matchedClassesT.indexOf(ct)){
									matchedPropsS.add(opS);
									matchedPropsT.add(opT);
									matchDeclaringClasses(opS, opT);
									break;
								}
								
							}
							//TODO: do for more classes.
						}
					}
				}
			}
			
		}
	}
	
	//For single and double declaring classes of two properties, match them
	public void matchDeclaringClasses(OntProperty p1, OntProperty p2){
		ExtendedIterator<? extends OntClass> c1 = p1.listDeclaringClasses(true);
		List<? extends OntClass> ls = c1.toList();
		ExtendedIterator<? extends OntClass> c2 = p2.listDeclaringClasses(true);
		List<? extends OntClass> lt = c2.toList();
		
		if(ls.size() == 1 && lt.size() == 1){
			OntClass cs = ls.get(0);
			OntClass ct = lt.get(0);
			if(cs.isAnon() || ct.isAnon())return;
			if(!matchedClassesS.contains(cs)){
				mapTwoOntClasses(cs, ct);
			}
		}
		if(ls.size() == 2 && lt.size() == 2){
			OntClass cs0 = ls.get(0);
			OntClass cs1 = ls.get(1);
			OntClass ct0 = lt.get(0);
			OntClass ct1 = lt.get(1);
			if(cs0.isAnon() || ct0.isAnon() || cs1.isAnon() || ct1.isAnon() )return;
			if(isOntClassesEqual(cs0, ct0)){
				if(!isSourceMapped(cs1)){
					mapTwoOntClasses(cs1, ct1);
				}
			}
			else if(isOntClassesEqual(cs1, ct1)){
				if(!isSourceMapped(cs0)){
					mapTwoOntClasses(cs0, ct0);
				}
			}
			else if(isOntClassesEqual(cs0, ct1)){
				if(!isSourceMapped(cs1)){
					mapTwoOntClasses(cs1, ct0);
				}
			}
			else{
				if(!isSourceMapped(cs0)){
					mapTwoOntClasses(cs0, ct1);
				}
			}
		}
	}
	
	
	//Adds to Classes
	public void matchSuperClasses(){
		ExtendedIterator<OntClass> ei = modelS.listClasses();
		while(ei.hasNext()){
			OntClass cs = ei.next();
			System.out.println(cs.getLocalName());
			OntClass sup = null;
			ExtendedIterator<OntClass> sp = cs.listSuperClasses(true);
			while(sp.hasNext()){
				OntClass csp = sp.next();
				System.out.println("\t" + csp.getLocalName());
				if(csp.getLocalName() != null){
					sup = csp;
					break;
				}
				
			}
			
			ExtendedIterator<OntClass> et = modelT.listClasses();
			OntClass ct = null;
			OntClass supt = null;
			while(et.hasNext()){
				ct = et.next();
				System.out.println(ct.getLocalName());
				ExtendedIterator<OntClass> st = ct.listSuperClasses(true);
				while(st.hasNext()){
					OntClass cst = st.next();
					System.out.println("\t" + cst.getLocalName());
					if(cst.getLocalName() != null){
						supt = cst;
						break;
					}
				}
				
				//Match here
				try{
				if(matchedClassesS.contains(cs) && matchedClassesT.get( matchedClassesS.indexOf(cs) ).equals(ct) ){
					if(!matchedClassesS.contains(sup)){
						mapTwoOntClasses(sup, supt);
					}
				}
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			
			
			
		}
		
	}
	
	//Adds to Classes
	public void matchSubClasses(){
		
	}
	
	
	//
	public void matchBasedOnRestrictions(){
		
	}
	
	//Check if two ObjectProperty have the same domain class/es
	//TODO: Works OntClass type domain for now.
	public boolean isDomainsAreSame(ObjectProperty ob1, ObjectProperty ob2){
		OntResource domS = ob1.getDomain();
		OntResource domT = ob2.getDomain();
		
		if(domS == null && domT == null) return true;
		else if(domS == null && domT != null) return false;
		else if(domS != null && domT == null) return false;
		else{
			if(domS.isClass() && !domS.asClass().isUnionClass()){
				OntClass cs = domS.asClass();

				if(domT.isClass() && !domT.asClass().isUnionClass()){
					OntClass ct = domT.asClass();

					if(isOntClassesEqual(cs, ct)){
						return true;
					}
				}
			}
			else if (domS.isClass() && domS.asClass().isUnionClass()) {
				ArrayList<OntClass> ds = new ArrayList<OntClass>();
				for (Iterator i = domS.asClass().asUnionClass().listOperands(); i.hasNext();) {
					OntClass c = (OntClass)i.next();
					ds.add(c);
				}
				OntClass cs0 = ds.get(0);
				OntClass cs1 = ds.get(1);

				if (domT.isClass() && domT.asClass().isUnionClass()) {
					ArrayList<OntClass> dt = new ArrayList<OntClass>();
					for (Iterator i = domT.asClass().asUnionClass().listOperands(); i.hasNext();) {
						OntClass c = (OntClass)i.next();
						dt.add(c);
					}
					OntClass ct0 = dt.get(0);
					OntClass ct1 = dt.get(1);

					if(isOntClassesEqual(cs0, ct0)){
						if(isOntClassesEqual(cs1, ct1)){return true;}
					}
					else if(isOntClassesEqual(cs1, ct1)){
						if(isOntClassesEqual(cs0, ct0)){return true;}
					}
					else if(isOntClassesEqual(cs0, ct1)){
						if(isOntClassesEqual(cs1, ct0)){return true;}
					}
					else{
						if(isOntClassesEqual(cs0, ct1)){return true;}
					}
				}

			}
		}
		return false;
	}
	
	//Check if two ObjectProperty have the same range class
	//TODO: Works for OntClass type range for now.
	public boolean isRangesAreSame(ObjectProperty ob1, ObjectProperty ob2){
		//domS mean rangeS.
		OntResource domS = ob1.getRange();
		OntResource domT = ob2.getRange();
		
		if(domS == null && domT == null) return true;
		else if(domS == null && domT != null) return false;
		else if(domS != null && domT == null) return false;
		else{
			if(domS.isClass() && !domS.asClass().isUnionClass()){
				OntClass cs = domS.asClass();

				if(domT.isClass() && !domT.asClass().isUnionClass()){
					OntClass ct = domT.asClass();

					if(isOntClassesEqual(cs, ct)){
						return true;
					}
				}
			}
			else if (domS.isClass() && domS.asClass().isUnionClass()) {
				ArrayList<OntClass> ds = new ArrayList<OntClass>();
				for (Iterator i = domS.asClass().asUnionClass().listOperands(); i.hasNext();) {
					OntClass c = (OntClass)i.next();
					ds.add(c);
				}
				OntClass cs0 = ds.get(0);
				OntClass cs1 = ds.get(1);

				if (domT.isClass() && domT.asClass().isUnionClass()) {
					ArrayList<OntClass> dt = new ArrayList<OntClass>();
					for (Iterator i = domT.asClass().asUnionClass().listOperands(); i.hasNext();) {
						OntClass c = (OntClass)i.next();
						dt.add(c);
					}
					OntClass ct0 = dt.get(0);
					OntClass ct1 = dt.get(1);

					if(isOntClassesEqual(cs0, ct0)){
						if(isOntClassesEqual(cs1, ct1)){return true;}
					}
					else if(isOntClassesEqual(cs1, ct1)){
						if(isOntClassesEqual(cs0, ct0)){return true;}
					}
					else if(isOntClassesEqual(cs0, ct1)){
						if(isOntClassesEqual(cs1, ct0)){return true;}
					}
					else{
						if(isOntClassesEqual(cs0, ct1)){return true;}
					}
				}

			}
		}
		return false;
	}
	
	public boolean isRangePrimitive(OntResource r){
		if(r.getLocalName().equals("http://www.w3.org/2001/XMLSchema#string")){
			return true;
		}
		else if(r.getLocalName().equals("http://www.w3.org/2001/XMLSchema#nonNegativeInteger")){
			return true;
		}
		else if(r.getLocalName().equals("http://www.w3.org/2001/XMLSchema#language")){
			return true;
		}
		else if(r.getLocalName().equals("http://www.w3.org/2001/XMLSchema#gDay")){
			return true;
		}
		else if(r.getLocalName().equals("http://www.w3.org/2001/XMLSchema#gMonth")){
			return true;
		}
		else if(r.getLocalName().equals("http://www.w3.org/2001/XMLSchema#gYear")){
			return true;
		}
		return false;
	}
	
	//Checks for primitive type data ranges
	public boolean hasSameDataRange(DatatypeProperty p1, DatatypeProperty p2){
		//domS mean rangeS.
		OntResource domS = p1.getRange();
		OntResource domT = p2.getRange();
		
		if(domS == null && domT == null) return true;
		else if(domS == null && domT != null) return false;
		else if(domS != null && domT == null) return false;
		else{
			if(isRangePrimitive(domS) && isRangePrimitive(domT)){
				if(domS.getLocalName().equals(domT.getLocalName())){
					return true;
				}
			}
		}
		return false;
	}
	
	
	//Check if a given source OntClass a is mapped to target OntClass b 
	public boolean isOntClassesEqual(OntClass a, OntClass b){
		if(matchedClassesS.contains(a)){
			if(matchedClassesT.contains(b)){
				if(matchedClassesS.indexOf(a) == matchedClassesT.indexOf(b)){
					return true;
				}
			}
		}
		return false;
	}
	
	//Returns true if a given source OntClass is already mapped
	public boolean isSourceMapped(OntClass a){
		if(matchedClassesS.contains(a)){
			return true;
		}
		return false;
	}
	
	public void mapTwoOntClasses(OntClass a, OntClass b){
		matchedClassesS.add(a);
		matchedClassesT.add(b);
	}
	
	public void populateStatements(){
		ExtendedIterator<Statement> sITs = modelS.listStatements();
		while(sITs.hasNext()){
			statementsS.add(sITs.next());
		}
		ExtendedIterator<Statement> sITt = modelT.listStatements();
		while(sITt.hasNext()){
			statementsT.add(sITt.next());
		}
	}
	
	
	public String getDescriptionString() {
		String result = "This matcher matches properties of the ontology" +
				" using the instances in the ontology.";
				return result;
	}
	
	
	
}
