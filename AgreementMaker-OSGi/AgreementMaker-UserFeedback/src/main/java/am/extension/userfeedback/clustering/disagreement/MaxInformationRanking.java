package am.extension.userfeedback.clustering.disagreement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.extension.userfeedback.experiments.SUExperiment;
import am.extension.userfeedback.selection.CandidateSelection;

public class MaxInformationRanking extends CandidateSelection<SUExperiment> {
	
	private List<Mapping> rankedClassMappings;
	private List<Mapping> rankedPropertyMappings;
	private List<Mapping> allRanked;
	private SUExperiment experiment;
	//weight for the Uncertain Mappings discovered in the system
	private final double weight_um=0.5;
	//weight for the suspected Missing Mapping in the system
	private final double weight_mm=0.3;
	private boolean flag=true;
	
	@Override public List<Mapping> getRankedMappings(alignType t) 
	{ 
		if( t == alignType.aligningClasses ) 
		{ 
			return rankedClassMappings; 
		}
		if( t == alignType.aligningProperties ) 
		{ 
			return rankedPropertyMappings; 
		}

		return null;
	}
	
	public Mapping getCandidateMapping_1_1() 
	{

		for( int i = 0; i < allRanked.size(); i++ ){
			if( experiment.correctMappings == null && experiment.incorrectMappings == null )
				return allRanked.get(i);
			
			Mapping m = allRanked.get(i);
			if( experiment.correctMappings != null && (experiment.correctMappings.contains(m.getEntity1(),Ontology.SOURCE) != null ||
				experiment.correctMappings.contains(m.getEntity2(),Ontology.TARGET) != null) ) 
			{
				// assume 1-1 mapping, skip already validated mappings.
				continue;
			}
			if( experiment.incorrectMappings != null && experiment.incorrectMappings.contains(m) ) 
				continue; // we've validated this mapping already.
				
			return m;
		}
		
		return null;
	}
	
	public Mapping getCandidateMapping_m_n() 
	{

		for( int i = 0; i < allRanked.size(); i++ ){
			if( experiment.correctMappings == null && experiment.incorrectMappings == null )
				return allRanked.get(i);
			
			Mapping m = allRanked.get(i);
			if( experiment.correctMappings != null && (experiment.correctMappings.contains(m.getEntity1(),Ontology.SOURCE) != null &&
				experiment.correctMappings.contains(m.getEntity2(),Ontology.TARGET) != null) ) 
			{
				// assume 1-1 mapping, skip already validated mappings.
				continue;
			}
			if( experiment.incorrectMappings != null && experiment.incorrectMappings.contains(m) ) 
				continue; // we've validated this mapping already.
				
			return m;
		}
		
		return null;
	}
	

	@Override
	public List<Mapping> getRankedMappings() {
		return allRanked;
	}
	
	@Override
	public void rank(SUExperiment ex) {
		this.experiment = ex;
//		if (ex.getIterationNumber()==0)
//			initializeRankedMatrix();
		// get the matchers from the execution semantics
		List<AbstractMatcher> matchers = ex.initialMatcher.getComponentMatchers();
		

		//if (ex.getIterationNumber()<=1)
		//{
		//rank(matchers);
		ex.disRanked=allRanked;
		//}
		//else
		//	allRanked=ex.allRanked;
		saveRankList(ex.getIterationNumber());
		
//		if (experiment.getAlignCardinalityType()==alignCardinality.c1_1)
//		selectedMapping=getCandidateMapping_1_1();
		selectedMapping=getCandidateMapping_m_n();
		if (flag){
			saveSV();
			flag=false;
		}
		
		done();
	}
	
	private void saveRankList(int iteration)
	{
		File file = new File("/home/frank/Documents/RankList/rankList_"+iteration+".txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fw=null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(Mapping mp : allRanked)
		{
			try {
				bw.write(mp.toString());
				bw.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveSV()
	{
		Object[] sv;
		File file = new File("/home/frank/Documents/SignatureVector/SV.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fw=null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		int count=0;
		double avg=0;
		for(Mapping mp : allRanked)
		{
			try {
				sv=getSignatureVector(mp);
				if(validSsv(sv))
				{
					bw.write(Integer.toString(count)+"\n");
					count++;
					bw.write(mp.toString());
					bw.write("\n");
					for(int i=0;i<sv.length;i++)
						bw.write(sv[i]+" ");
					bw.write("---\n");
					avg+=mp.getSimilarity();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			bw.write(Double.toString(avg));
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	private void initializeRankedMatrix()
//	{
//		SimilarityMatrix smClass=experiment.initialMatcher.getFinalMatcher().getClassesMatrix().clone();
//		SimilarityMatrix smProperty=experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix().clone();
//		for(int i=0;i<smClass.getRows();i++)
//			for(int j=0;j<smClass.getColumns();j++)
//				smClass.setSimilarity(i, j, 0.5);
//		for(int i=0;i<smProperty.getRows();i++)
//			for(int j=0;j<smProperty.getColumns();j++)
//				smProperty.setSimilarity(i, j, 0.5);
//		experiment.setUflClassMatrix(smClass);
//		experiment.setUflPropertyMatrix(smProperty);
//	}
	
	
	
//	private SimilarityMatrix addPropagationWeight(SimilarityMatrix sm, SimilarityMatrix ufl)
//	{
//		Alignment<Mapping> unAlignment=retriveUnconfidentMapping(ufl);
//		int row=sm.getRows();
//		int col=sm.getColumns();
//		double sim=0;
//		for(int i=0;i<row;i++)
//		{
//			for(int j=0;j<col;j++)
//			{
//				if (unAlignment.contains(sm.get(i, j)))
//				{
//					sim=sm.getSimilarity(i, j)+unAlignment.getSimilarity(sm.get(i, j).getEntity1(), sm.get(i, j).getEntity2());
//					sm.setSimilarity(i, j, sim);
//				}
//			}
//		}
//		return sm;
//	}
	
	
	
	
//	private Alignment<Mapping> retriveUnconfidentMapping(SimilarityMatrix sm)
//	{
//		Alignment<Mapping> mpng=new Alignment<Mapping>(0,0);
//		Mapping m=null;
//		int row=sm.getRows();
//		int col=sm.getColumns();
//		double weight=0;
//		CrossCountQuality qm = new CrossCountQuality(sm);
//		for(int i=0;i<row;i++)
//		{
//			for(int j=0;j<col;j++)
//			{
//				weight=0.0;
//				if (sm.getSimilarity(i, j)==0.0)
//				{
////					continue;
//					if (sm.getRowMaxValues(i, 1)[0].getSimilarity()==0.0)
//					{
//						if (sm.getColMaxValues(j, 1)[0].getSimilarity()==0.0)
//						{
//							weight=weight_mm;
//						}
//						else
//						{
//							continue;
//						}
//					}
//					else
//					{
//						continue;
//					}
//				}
//				else
//				{
//					m=sm.get(i, j);
//					m.setSimilarity( qm.getQuality(null, i, j) );
//					mpng.add(m);
//				}
//			}
//		}
//		
//		return mpng;
//	}
	


	
	private double numOfMapping(Mapping[] map)
	{
		double count=0;
		for(Mapping m :map)
		{
			if(m.getSimilarity()!=0.0)
				count++;
		}
		return count;
	}
	
//	private Alignment<Mapping> missedMappingRetriver(SimilarityMatrix sm, TreeSet<Integer> forbidden_row, TreeSet<Integer> forbidden_col)
//	{
//		Alignment<Mapping> missed=new Alignment<Mapping>(0, 0);
//		int row=sm.getRows();
//		int col=sm.getColumns();
//		double row_sim=0;
//		double col_sim=0;
//		for(int i=0;i<row;i++)
//		{
//			row_sim=sm.getRowMaxValues(i, 1)[0].getSimilarity();
//			if (row_sim!=0)
//				continue;
//			if (forbidden_row.contains(i)) 
//				continue;
//			for(int j=0;j<col;j++)
//			{
//				if(forbidden_col.contains(j))
//					continue;
//				
//				col_sim=sm.getColMaxValues(j, 1)[0].getSimilarity();
//				if (col_sim!=0.0)
//					continue;
//				
//				missed.add(sm.get(i, j));
//			}
//		}
//		return missed;
//	}
	
	private List<Mapping> conflitualMapping(SimilarityMatrix sm)
	{
		List<Mapping> lst=new ArrayList<Mapping>();
		Mapping m=null;
		int row=sm.getRows();
		int col=sm.getColumns();
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				m=sm.get(i, j);
				if (m.getSimilarity()!=0)
				{
					if (checkConf(sm,m))
					{
						lst.add(m);
					}
				}
			}
		}
		return lst;
	}
	
	private SimilarityMatrix addConflitualWeight(SimilarityMatrix sm, List<Mapping> mList)
	{
		Mapping m=null;
		for(int i =0;i<sm.getRows();i++)
		{
			for(int j=0;j<sm.getColumns();j++)
			{
				m=sm.get(i, j);
				if (mList.contains(m))
				{
					sm.setSimilarity(i, j, 1.0);
				}
			}
		}
		return sm;
	}
	
	private boolean checkConf(SimilarityMatrix sm, Mapping m)
	{
		Mapping[] row=sm.getRowMaxValues(m.getSourceKey(), sm.getColumns());
		Mapping[] col=sm.getColMaxValues(m.getTargetKey(), sm.getRows());
		if (numOfMapping(row)>1) return true;
		if (numOfMapping(col)>1) return true;
		return false;
	}
	
	private void rankByDistance(SimilarityMatrix sm, SimilarityMatrix ufl)
	{
		for(int i=0;i<sm.getRows();i++)
		{
			for(int j=0;j<sm.getColumns();j++)
			{
				sm.setSimilarity(i, j, getDistance(sm.get(i, j),ufl));
			}
		}
	}
	
	private Object[] getSignatureVector(Mapping mp)
	{
		List<AbstractMatcher> inputMatchers=experiment.initialMatcher.getComponentMatchers();
		int size=inputMatchers.size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		AbstractMatcher a;
		Object[] ssv=new Object[size];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			ssv[i]=a.getAlignment().getSimilarity(sourceNode, targetNode);
			
		}
		return ssv;
	}
	
	private boolean validSsv(Object[] ssv)
	{
		Object obj=0.0;
		for(int i=0;i<ssv.length;i++)
		{
			if (!ssv[i].equals(obj))
				return true;
		}
		return false;
	}
	
	private int getDistance(Mapping m, SimilarityMatrix sm)
	{
		Object[] sv1=getSignatureVector(m);
		Object[] sv2;
		int count=0;
		if (!validSsv(sv1))
			return 0;
		for(int i=0;i<sm.getRows();i++)
		{
			for(int j=0;j<sm.getColumns();j++)
			{
				sv2=getSignatureVector(sm.get(i, j));
				if (validSsv(sv2))
					if (svDistance(sv1,sv2)==0)
						count++;
			}
		}
		return count;
	}
	
	private double svDistance(Object[] sv1, Object[] sv2)
	{
		double distance=0;
		for(int j=0;j<sv1.length;j++)
		{
			distance+=Math.pow((double)sv1[j]-(double)sv2[j],2);
		}
		return Math.sqrt(distance);
	}
	
	
//	public void rank(List<AbstractMatcher> matchers)
//	{
//		
//
//		// setup the variance disagreement calculation
//		VarianceDisagreementParameters disagreementParams = new VarianceDisagreementParameters();
//		disagreementParams.setMatchers(matchers);
//		
//		VarianceDisagreement disagreementMetric = new VarianceDisagreement();
//		disagreementMetric.setParameters(disagreementParams);
//		
//		// run the disagreement calculations
//		SimilarityMatrix classDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningClasses);
//		
//		//add the weight coming from the ranking matrix produced in the UFL propagation phase
//		
//		classDisagreement=addPropagationWeight(classDisagreement, experiment.getUflClassMatrix());
//
//		try {
//			rankedClassMappings = classDisagreement.toList();
//			Collections.sort(rankedClassMappings, new MappingSimilarityComparator() );
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//		classDisagreement = null;  // release the memory used by this
//
//		
//		SimilarityMatrix propertyDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningProperties);
//		
//		//add the weight coming from the ranking matrix produced in the UFL propagation phase
//		
//		propertyDisagreement=addPropagationWeight(propertyDisagreement, experiment.getUflPropertyMatrix());
//
//		try {
//			rankedPropertyMappings = propertyDisagreement.toList();
//			Collections.sort(rankedPropertyMappings, new MappingSimilarityComparator() );
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		propertyDisagreement = null;
//		
//		
//		allRanked = new ArrayList<Mapping>();
//		
//		allRanked.addAll(rankedClassMappings);
//		allRanked.addAll(rankedPropertyMappings);
//		Collections.sort(allRanked, new MappingSimilarityComparator() );
//		Collections.reverse(allRanked);
////		if(useProperty)
////			propertyRank(allRanked, matchers);
//		
//	}

	@Override
	public Mapping getSelectedMapping() {
		// TODO Auto-generated method stub
		return selectedMapping;
	}

}
