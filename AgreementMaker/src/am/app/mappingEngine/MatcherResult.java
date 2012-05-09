package am.app.mappingEngine;

import java.awt.Color;
import java.util.List;

import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import am.app.ontology.Ontology;

public interface MatcherResult {
	
	public Alignment<Mapping> getAlignment();
    public Alignment<Mapping> getClassAlignmentSet();
    public Alignment<Mapping> getPropertyAlignmentSet();
    public Alignment<Mapping> getInstanceAlignmentSet();
    public int getID();
    public String getMatcherName();
    public boolean isShown();
    public DefaultMatcherParameters getParameters();
    public List<AbstractMatcher> getInputMatchers();
    public boolean isModifiedByUser();
    public boolean isAlignClass();
    public boolean isAlignProp();
    public int getTotalNumberAlignments();
    public long getExecutionTime();
    public boolean isRefEvaluated();
    public ReferenceEvaluationData getRefEvaluation();
    public boolean isQualEvaluated();
    public QualityEvaluationData getQualEvaluation();
    public Color getColor();
    public SimilarityMatrix getClassesMatrix();
    public SimilarityMatrix getPropertiesMatrix();
    
    public void setShown(boolean b);
    public void setThreshold(double d);
    public void setMaxSourceAlign(int i);
    public void setMaxTargetAlign(int i);
    public void setAlignClass(boolean b);
    public void setAlignProp(boolean b);
    public void setColor(Color c);
    public void setMatcherName(String s);

    public void setPropertyAlignmentSet(Alignment<Mapping> set);
    public void setClassAlignmentSet(Alignment<Mapping> set);
    public void setInstanceAlignmentSet(Alignment<Mapping> set);
    
    public Ontology getSourceOntology();
    public Ontology getTargetOntology();
	public void setQualEvaluation(QualityEvaluationData data);
	public void setRefEvaluation(ReferenceEvaluationData data);
    
}
