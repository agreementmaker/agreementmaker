package am.extension.batchmode.matchingTask;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.ontology.Ontology;

public abstract class FluentMatchingTaskRunner implements MatchingTaskRunner {

	protected DefaultMatcherParameters   matcherParams;
	protected AbstractMatcher            matcher;
	protected DefaultSelectionParameters selectorParams;
	protected SelectionAlgorithm         selector;
	
	protected String shortcutFile;
	
	protected MatchingTask[] inputMatchers;
	
    @Override
    public void setMatcherParameters(DefaultMatcherParameters params) {
    	this.matcherParams = params;
    }
    
    @Override
    public void setMatcher(AbstractMatcher matcher) {
    	this.matcher = matcher;
    }
    
    @Override
    public void setSelectorParameters(DefaultSelectionParameters params) {
    	this.selectorParams = params;
    }
    
    @Override
    public void setSelector(SelectionAlgorithm selector) {
    	this.selector = selector;
    }
    
    @Override
    public void setShortcutFile(String filePath) {
    	this.shortcutFile = filePath;
    }

    public FluentMatchingTaskRunner withMatcherParameters(DefaultMatcherParameters params) {
    	setMatcherParameters(params);
    	return this;
    }
    
    public FluentMatchingTaskRunner andMatcher(AbstractMatcher matcher) {
    	setMatcher(matcher);
    	return this;
    }
    
    public FluentMatchingTaskRunner andSelectorParameters(DefaultSelectionParameters params) {
    	setSelectorParameters(params);
    	return this;
    }

    public FluentMatchingTaskRunner andSelector(SelectionAlgorithm selector) {
    	setSelector(selector);
    	return this;
    }
    
    public FluentMatchingTaskRunner matchingOntologies(Ontology o1, Ontology o2) {
    	matcherParams.setOntologies(o1, o2);
    	return this;
    }
    
    public FluentMatchingTaskRunner and(AbstractMatcher matcher) {
    	return andMatcher(matcher);
    }
    
    public FluentMatchingTaskRunner with(DefaultMatcherParameters params) {
    	return withMatcherParameters(params);
    }
    
    public FluentMatchingTaskRunner and(DefaultSelectionParameters params) {
    	return andSelectorParameters(params);
    }
    
    public FluentMatchingTaskRunner and(SelectionAlgorithm selector) {
    	return andSelector(selector);
    }
    
    public FluentMatchingTaskRunner matching(Ontology o1, Ontology o2) {
    	return matchingOntologies(o1, o2);
    }
    
    public FluentMatchingTaskRunner matching(MatchingTask... inputMatchers) {
    	this.inputMatchers = inputMatchers;
    	return this;
    }
}
