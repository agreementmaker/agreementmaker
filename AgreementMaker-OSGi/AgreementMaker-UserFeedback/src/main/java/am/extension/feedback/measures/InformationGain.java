package am.extension.feedback.measures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.extension.feedback.CandidateConcept;
import am.extension.feedback.FilteredAlignmentMatrix;

public class InformationGain extends RelevanceMeasure {

	int whichOntology;
	alignType whichType;
	
	
	public void calculateRelevances() {
		
		// classes
		InfoGain_Classes( fbl.getClassesMatrix(), Core.getInstance().getSourceOntology().getClassesList(), Core.getInstance().getTargetOntology().getClassesList() );

		// properties
		InfoGain_Properties( fbl.getPropertiesMatrix(), Core.getInstance().getSourceOntology().getPropertiesList(), Core.getInstance().getTargetOntology().getPropertiesList() );
		
		
	}


	private void InfoGain_Classes(FilteredAlignmentMatrix classesMatrix, List<Node> sourceClasses, List<Node> targetClasses ) {
		
		ArrayList<Frequency> frequencyList = new ArrayList<Frequency>();
		
		double[] minRowSim = new double[classesMatrix.getRows()];
		double[] minColSim = new double[classesMatrix.getColumns()];
		
		for( int row = 0; row < classesMatrix.getRows(); row++ ) {
			
			double minSim_e = classesMatrix.getRowMinValue_notZero( row );
			minRowSim[row] = minSim_e;
			Frequency freq_e = new Frequency();
			freq_e.similarity = minSim_e;
			freq_e.occurs = classesMatrix.getFrequency(minSim_e);
			
			Iterator<Frequency> freqIter = frequencyList.iterator();
			while( freqIter.hasNext() ) {
				Frequency ithfreq = freqIter.next();
				if( ithfreq.equals(freq_e) ) {
					ithfreq.occurs += freq_e.occurs;
					freq_e = null;
					break;
				}
			}
			
			if( freq_e != null ) {
				frequencyList.add(freq_e);
			}
			
		}
		
		
		for( int col = 0; col < classesMatrix.getColumns(); col++ ) {
			
			double minSim_e = classesMatrix.getColMinValue_notZero( col );
			minColSim[col] = minSim_e;
			Frequency freq_e = new Frequency();
			freq_e.similarity = minSim_e;
			freq_e.occurs = classesMatrix.getFrequency(minSim_e);
			
			Iterator<Frequency> freqIter = frequencyList.iterator();
			while( freqIter.hasNext() ) {
				Frequency ithfreq = freqIter.next();
				if( ithfreq.equals(freq_e) ) {
					ithfreq.occurs += freq_e.occurs;
					freq_e = null;
					break;
				}
			}
			
			if( freq_e != null ) {
				frequencyList.add(freq_e);
			}
			
		}
		// the frequency list is complete
		
		
		// compute totalFreq
		int totalFreq = 0;
		{
			Iterator<Frequency> freqIter = frequencyList.iterator();
			while( freqIter.hasNext() ) {
				totalFreq += freqIter.next().occurs;
			}
		}
		
		for( int row = 0; row < sourceClasses.size(); row++ ) {
			double prob =  (double)getFreq(frequencyList, minRowSim[row] ) / (double)totalFreq ;
			if( prob > 0.0d ) {
				Node n = sourceClasses.get(row);
				double relevance = -1.0d * Math.log(prob) / Math.log(2.0d);  // -log2(prob)
				CandidateConcept cc = new CandidateConcept(n, relevance, Ontology.SOURCE, alignType.aligningClasses );
				candidateList.add(cc);
			}
		}
		
		for( int col = 0; col < targetClasses.size(); col++ ) {
			double prob = (double) getFreq( frequencyList, minColSim[col] ) / (double)totalFreq;
			if( prob > 0.0d ) {
				Node n = targetClasses.get(col);
				double relevance = -1.0d * Math.log(prob) / Math.log(2.0d);  // -log2(prob)
				CandidateConcept cc = new CandidateConcept(n, relevance, Ontology.SOURCE, alignType.aligningClasses );
				candidateList.add(cc);
			}
		}
		
	}

	private void InfoGain_Properties(FilteredAlignmentMatrix classesMatrix, List<Node> sourceClasses, List<Node> targetClasses ) {
		
		ArrayList<Frequency> frequencyList = new ArrayList<Frequency>();
		
		double[] minRowSim = new double[classesMatrix.getRows()];
		double[] minColSim = new double[classesMatrix.getColumns()];
		
		for( int row = 0; row < classesMatrix.getRows(); row++ ) {
			
			double minSim_e = classesMatrix.getRowMinValue_notZero( row );
			minRowSim[row] = minSim_e;
			Frequency freq_e = new Frequency();
			freq_e.similarity = minSim_e;
			freq_e.occurs = classesMatrix.getFrequency(minSim_e);
			
			Iterator<Frequency> freqIter = frequencyList.iterator();
			while( freqIter.hasNext() ) {
				Frequency ithfreq = freqIter.next();
				if( ithfreq.equals(freq_e) ) {
					ithfreq.occurs += freq_e.occurs;
					freq_e = null;
					break;
				}
			}
			
			if( freq_e != null ) {
				frequencyList.add(freq_e);
			}
			
		}
		
		
		for( int col = 0; col < classesMatrix.getColumns(); col++ ) {
			
			double minSim_e = classesMatrix.getColMinValue_notZero( col );
			minColSim[col] = minSim_e;
			Frequency freq_e = new Frequency();
			freq_e.similarity = minSim_e;
			freq_e.occurs = classesMatrix.getFrequency(minSim_e);
			
			Iterator<Frequency> freqIter = frequencyList.iterator();
			while( freqIter.hasNext() ) {
				Frequency ithfreq = freqIter.next();
				if( ithfreq.equals(freq_e) ) {
					ithfreq.occurs += freq_e.occurs;
					freq_e = null;
					break;
				}
			}
			
			if( freq_e != null ) {
				frequencyList.add(freq_e);
			}
			
		}
		// the frequency list is complete
		
		
		// compute totalFreq
		int totalFreq = 0;
		{
			Iterator<Frequency> freqIter = frequencyList.iterator();
			while( freqIter.hasNext() ) {
				totalFreq += freqIter.next().occurs;
			}
		}
		
		for( int row = 0; row < sourceClasses.size(); row++ ) {
			double prob =  (double)getFreq(frequencyList, minRowSim[row] ) / (double)totalFreq ;
			if( prob > 0.0d ) {
				Node n = sourceClasses.get(row);
				double relevance = -1.0d * Math.log(prob) / Math.log(2.0d);  // -log2(prob)
				CandidateConcept cc = new CandidateConcept(n, relevance, Ontology.SOURCE, alignType.aligningProperties );
				candidateList.add(cc);
			}
		}
		
		for( int col = 0; col < targetClasses.size(); col++ ) {
			double prob = (double) getFreq( frequencyList, minColSim[col] ) / (double)totalFreq;
			if( prob > 0.0d ) {
				Node n = targetClasses.get(col);
				double relevance = -1.0d * Math.log(prob) / Math.log(2.0d);  // -log2(prob)
				CandidateConcept cc = new CandidateConcept(n, relevance, Ontology.SOURCE, alignType.aligningProperties );
				candidateList.add(cc);
			}
		}
		
	}
	
	
	public int getFreq( ArrayList<Frequency> freqList , double sim ) {
		Iterator<Frequency> frIter = freqList.iterator();
		while( frIter.hasNext() ) {
			Frequency f = frIter.next();
			if( f.similarity == sim ) {
				return f.occurs;
			}
		}
		return 0;
	}
	
	
	
}
