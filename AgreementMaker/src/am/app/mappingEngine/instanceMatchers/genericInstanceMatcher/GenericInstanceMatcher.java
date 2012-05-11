package am.app.mappingEngine.instanceMatchers.genericInstanceMatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Report;
import am.app.mappingEngine.instanceMatchers.BaseInstanceMatcher;
import am.app.mappingEngine.instanceMatchers.UsesKB;
import am.app.mappingEngine.instanceMatchers.combination.CombinationFunction;
import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.LabeledDatasource;
import am.app.ontology.instance.Instance;

/**
 *	This matcher contains a list of matchers, which are all run and then combined 
 *	using a customizable combination function.
 *	It supports: 
 *		- multiple passes
 *		- logging a report with the similarities by matcher 
 *		- Passing the KBs to the matcher which need them
 * 
 * @author Federico Caimi
 *
 */
public class GenericInstanceMatcher extends BaseInstanceMatcher implements UsesKB{
	private static final long serialVersionUID = -5745262888574700843L;
	
	private List<AbstractMatcher> matchers = new ArrayList<AbstractMatcher>();
	CombinationFunction combination;

	private boolean generateReport = true;
	
	Logger log = Logger.getLogger(GenericInstanceMatcher.class);
	
	private String corefFolder = "CoreferenceReports";
			
	public GenericInstanceMatcher(){
		instanceMatchingReport = new Report();
		instanceMatchingReport.setMatchers(matchers);
	}
	
	@Override
	public void passStart() {
		if(generateReport){
			System.out.println("init instanceMatchingReport");
			System.out.println(this);
			instanceMatchingReport = new Report();
			instanceMatchingReport.setMatchers(matchers);
		}
		
		if(!firstPassDone){
			log.info("First pass, requiresTwoPasses=" + requiresTwoPasses());
		}
	}
	
	@Override
	public void passEnd() {
		if(generateReport){
			String output = instanceMatchingReport.printTable();
			
			Date date = new Date() ;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
			File folder = new File(corefFolder);
			if(!folder.exists()) folder.mkdir();
			
			try {
				FileOutputStream fos = new FileOutputStream(corefFolder + File.separator + "sims-" + dateFormat.format(date) + ".tab");
				fos.write(output.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (AbstractMatcher matcher : matchers) {
			matcher.passEnd();
		}
		
		super.passEnd();
	}
	
	public void addInstanceMatcher(AbstractMatcher matcher){
		matchers.add(matcher);
	}
	
	@Override
	public double instanceSimilarity(Instance source, Instance target)
			throws Exception {
		//In this case we have to first run just the matchers requiring two passes
		//and we do not care about the similarity
		if(requiresTwoPasses && !firstPassDone){
			for (AbstractMatcher matcher : matchers) {
				if(matcher.requiresTwoPasses())
					matcher.instanceSimilarity(source, target);
			}			
			return 0.0;
		}
				
		List<Double> similarities = new ArrayList<Double>();
		for (AbstractMatcher matcher : matchers) {
			similarities.add(matcher.instanceSimilarity(source, target));
		}		
		
		if(generateReport && instanceMatchingReport != null){
			//System.out.println(source.getUri().startsWith("text"));
			instanceMatchingReport.putSim(source.getUri() + "||" + target.getUri(), similarities);
		}
		
		return combination.combine(similarities);
	}
	
	public void setCombination(CombinationFunction combination) {
		this.combination = combination;
	}
	
	@Override
	public String getName() {
		return "Generic Instance Matcher" + matchers;
	}
	
	@Override
	public String toString() {
		return matchers.toString();
	}
	
	@Override
	public boolean requiresTwoPasses() {
		for (AbstractMatcher matcher : matchers) {
			if(matcher.requiresTwoPasses())
				requiresTwoPasses = true;
		}
		return requiresTwoPasses;
	}

	@Override
	/**
	 * Make sure this method is called after adding all the matchers!
	 */
	public void setSourceKB(LabeledDatasource sourceKB) {
		for (AbstractMatcher matcher : matchers) {
			if(matcher instanceof UsesKB){
				if(sourceKB != null)
					((UsesKB) matcher).setSourceKB(sourceKB);
			}
		}	
	}

	@Override
	/**
	 * Make sure this method is called after adding all the matchers!
	 */
	public void setTargetKB(LabeledDatasource targetKB) {
		for (AbstractMatcher matcher : matchers) {
			if(matcher instanceof UsesKB){
				if(targetKB != null){
					System.out.println("Setting target Knowledge Base " + matcher.getName());
					((UsesKB) matcher).setTargetKB(targetKB);
				}
			}
		}		
	}
}
