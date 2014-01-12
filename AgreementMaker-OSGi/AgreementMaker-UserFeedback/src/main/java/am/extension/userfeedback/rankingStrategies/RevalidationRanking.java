package am.extension.userfeedback.rankingStrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private double tetha=3.0;
	private double zeta=1.0;
	
	
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
		List<Mapping> rankList=linearCombination(classMatrices, classPos, classNeg, uflClass);
		rankList.addAll(linearCombination(propMatrices, propPos, propNeg,uflProp));
		Collections.sort(rankList, new MappingSimilarityComparator() );
		Collections.reverse(rankList);
		
		return rankList;
	}
	
	
	//Linear combination of UD and the inverse of AMD
	private List<Mapping> linearCombination(List<SimilarityMatrix>  lMtrx, SparseMatrix mPos, SparseMatrix mNeg, SimilarityMatrix mtrx)
	{
		Mapping mp=null;
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		UserDisagrement ud=new UserDisagrement(mPos, mNeg);
		CrossCountQuality ccq=new CrossCountQuality(mtrx);
		SimilarityScoreHardness ssh=new SimilarityScoreHardness(mtrx);
		MappingQualityMetric rr=new InverseOf(new RevalidationRate(mPos, mNeg));
		VarianceMatcherDisagreement vmd=new VarianceMatcherDisagreement(lMtrx);
		if (toRank==null) return new ArrayList<Mapping>();
		for (int i=0;i<mtrx.getRows();i++)
		{
			for (int j=0;j<mtrx.getColumns();j++)
			{
				mp=mtrx.get(i, j);
				if (toRank.contains(mp))
				{
					sim=alpha*ud.getQuality(null, i, j)+beta*ccq.getQuality(null, i, j)+
							gamma*(1-ssh.getQuality(null, i, j))+tetha*rr.getQuality(null, i, j)+
							zeta*vmd.getQuality(null, i, j);
					mp.setSimilarity(sim);
					lst.add(mp);
				}
				else
					continue;
			}
		}
		return lst;
		
	}

}
