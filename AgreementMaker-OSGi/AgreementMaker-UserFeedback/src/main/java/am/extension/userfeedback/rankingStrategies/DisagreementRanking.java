package am.extension.userfeedback.rankingStrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.VarianceMatcherDisagreement;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;

public class DisagreementRanking extends AbstractRankingStrategy {

	private List<SimilarityMatrix> classMatrices=new ArrayList<SimilarityMatrix>();
	private List<SimilarityMatrix> propMatrices=new ArrayList<SimilarityMatrix>();
	private SparseMatrix classPos;
	private SparseMatrix classNeg;
	private SparseMatrix propPos;
	private SparseMatrix propNeg;
	private SimilarityMatrix refMatrixC;
	private SimilarityMatrix refMatrixP;
	
	
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
	public void rank() {
		rankedList = linearCombination(classMatrices, classPos, classNeg, refMatrixC);
		rankedList.addAll(linearCombination(propMatrices, propPos, propNeg,refMatrixP));
		Collections.sort(rankedList, new MappingSimilarityComparator() );
		Collections.reverse(rankedList);
	}
	
	
	//Linear combination of UD and the inverse of AMD
	private List<Mapping> linearCombination(List<SimilarityMatrix>  lMtrx, SparseMatrix mPos, SparseMatrix mNeg, SimilarityMatrix refMatrix)
	{
		Mapping mp=null;
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		VarianceMatcherDisagreement vmd=new VarianceMatcherDisagreement(lMtrx);
		for (int i=0;i<mPos.getRows();i++)
		{
			for (int j=0;j<mPos.getColumns();j++)
			{
				mp=refMatrix.get(i, j);
				
				sim=vmd.getQuality(null, i, j);
				mp.setSimilarity(sim);
				lst.add(mp);
			}
		}
		return lst;
		
	}

}
