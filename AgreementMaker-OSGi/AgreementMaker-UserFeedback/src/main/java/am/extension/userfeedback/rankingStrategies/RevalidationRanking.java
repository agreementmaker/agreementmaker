package am.extension.userfeedback.rankingStrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.qualityEvaluation.MappingQualityMetric;
import am.app.mappingEngine.qualityEvaluation.metrics.InverseOf;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossCountQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.RevalidationRate;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.SimilarityScoreHardness;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.UserDisagrement;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.VarianceMatcherDisagreement;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;

public class RevalidationRanking implements StrategyInterface{

	private List<SimilarityMatrix> classMatrices=new ArrayList<SimilarityMatrix>();
	private List<SimilarityMatrix> propMatrices=new ArrayList<SimilarityMatrix>();
	private List<Mapping> toRank=new ArrayList<Mapping>();
	private SparseMatrix classPos;
	private SparseMatrix classNeg;
	private SparseMatrix propPos;
	private SparseMatrix propNeg;
	private SimilarityMatrix uflClass;
	private SimilarityMatrix uflProp;
	private double alpha=1.0;
	private double beta=1.0;
	private double gamma=1.0;

	
	
	public RevalidationRanking(List<SimilarityMatrix> clMatrix, List<SimilarityMatrix> prMatrix, SparseMatrix cp, SparseMatrix cn, SparseMatrix pp,SparseMatrix pn, SimilarityMatrix uClass, SimilarityMatrix uProp, List<Mapping> torank)
	{
		this.classMatrices=clMatrix;
		this.propMatrices=prMatrix;
		this.classPos=cp;
		this.classNeg=cn;
		this.propPos=pp;
		this.propNeg=pn;
		this.uflClass=uClass;
		this.uflProp=uProp;
		this.toRank=torank;
	}
	
	@Override
	public List<Mapping> rank() {
		List<Mapping> rankList=linearCombination(alignType.aligningClasses,classMatrices, classPos, classNeg, uflClass);
		rankList.addAll(linearCombination(alignType.aligningProperties,propMatrices, propPos, propNeg,uflProp));
		Collections.sort(rankList, new MappingSimilarityComparator() );
		Collections.reverse(rankList);
		
		return rankList;
	}
	
	
	//Linear combination of UD and the inverse of AMD
	private List<Mapping> linearCombination(alignType type, List<SimilarityMatrix>  lMtrx, SparseMatrix mPos, SparseMatrix mNeg, SimilarityMatrix mtrx)
	{
		List<Double> ud_norm=new ArrayList<Double>();
		List<Double> ccq_norm=new ArrayList<Double>();
		List<Double> rr_norm=new ArrayList<Double>();
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		UserDisagrement ud=new UserDisagrement(mPos, mNeg);
		CrossCountQuality ccq=new CrossCountQuality(mtrx);
		MappingQualityMetric rr=new InverseOf(new RevalidationRate(mPos, mNeg));
		if (toRank==null) return new ArrayList<Mapping>();
		for (Mapping m : toRank)
		{
			if(m.getAlignmentType() != type) continue;
			
			int i=m.getSourceKey();
			int j=m.getTargetKey();
			ud_norm.add(ud.getQuality(null, i, j));
			ccq_norm.add(ccq.getQuality(null, i, j));
			rr_norm.add(rr.getQuality(null, i, j));
		}
		normalize(ud_norm);
		normalize(ccq_norm);
		normalize(rr_norm);
		int count=0;
		for (Mapping m : toRank)
		{
			if(m.getAlignmentType() != type) continue;
			sim=alpha*ud_norm.get(count)+beta*ccq_norm.get(count)+gamma*rr_norm.get(count);
			m.setSimilarity(sim);
			lst.add(m);
			count++;
		}
		
		
		return lst;
		
	}
	
	private void normalize(List<Double> lst)
	{
		double max=Double.MIN_VALUE;
		double min=Double.MAX_VALUE;
		
		for(Double d :lst)
		{
			if (d<min)
				min=d;
			if (d>max) max=d;	
		}
		for(int i=0;i<lst.size();i++)
		{
			double tmp=lst.get(i);
			tmp=(tmp-min)/(max-min);
			lst.set(i, tmp);
		}
		
	}


}
