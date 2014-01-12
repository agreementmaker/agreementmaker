package am.extension.userfeedback.rankingStrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.UserDisagrement;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.VarianceMatcherDisagreement;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;

public class DisagreementRanking implements StrategyInterface{

	private List<SimilarityMatrix> classMatrices=new ArrayList<SimilarityMatrix>();
	private List<SimilarityMatrix> propMatrices=new ArrayList<SimilarityMatrix>();
	private SparseMatrix classPos;
	private SparseMatrix classNeg;
	private SparseMatrix propPos;
	private SparseMatrix propNeg;
	private SimilarityMatrix refMatrixC;
	private SimilarityMatrix refMatrixP;
	private double alpha=1.0;
	private double beta=1.0;
	
	
	public DisagreementRanking(List<SimilarityMatrix> clMatrix, List<SimilarityMatrix> prMatrix, SparseMatrix cp, SparseMatrix cn, SparseMatrix pp,SparseMatrix pn, SimilarityMatrix referenceMC, SimilarityMatrix referenceMP)
	{
		this.classMatrices=clMatrix;
		this.propMatrices=prMatrix;
		this.classPos=cp;
		this.classNeg=cn;
		this.propPos=pp;
		this.propNeg=pn;
		this.refMatrixC=referenceMC;
		this.refMatrixP=referenceMP;
	}
	
	@Override
	public List<Mapping> rank() {
		List<Mapping> rankList=linearCombination(classMatrices, classPos, classNeg, refMatrixC);
		rankList.addAll(linearCombination(propMatrices, propPos, propNeg,refMatrixP));
		Collections.sort(rankList, new MappingSimilarityComparator() );
		//Collections.reverse(rankList);
		
		return rankList;
	}
	
	
	//Linear combination of UD and the inverse of AMD
	private List<Mapping> linearCombination(List<SimilarityMatrix>  lMtrx, SparseMatrix mPos, SparseMatrix mNeg, SimilarityMatrix refMatrix)
	{
		Mapping mp=null;
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		UserDisagrement ud=new UserDisagrement(mPos, mNeg);
		VarianceMatcherDisagreement vmd=new VarianceMatcherDisagreement(lMtrx);
		for (int i=0;i<mPos.getRows();i++)
		{
			for (int j=0;j<mPos.getColumns();j++)
			{
				mp=refMatrix.get(i, j);
				
				sim=alpha*ud.getQuality(null, i, j)+beta*vmd.getQuality(null, i, j);
				mp.setSimilarity(sim);
				lst.add(mp);
			}
		}
		return lst;
		
	}

}
