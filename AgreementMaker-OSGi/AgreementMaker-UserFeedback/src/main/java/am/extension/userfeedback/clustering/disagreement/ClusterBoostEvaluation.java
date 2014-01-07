package am.extension.userfeedback.clustering.disagreement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.localByThreshold.LocalByThresholdMethod;
import am.evaluation.clustering.localByThreshold.LocalByThresholdParameters;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.UFLExperiment;

public class ClusterBoostEvaluation extends PropagationEvaluation {

	@Override
	public void evaluate(UFLExperiment exp) {
		
		
		// Step 1.  For every ranked mapping, calculate the clustering threshold change
		//			plot.
		List<AbstractMatcher> availableMatchers = exp.initialMatcher.getComponentMatchers();
		LocalByThresholdMethod clusteringMethod = new LocalByThresholdMethod(availableMatchers);
		
		LocalByThresholdParameters clusteringParameters = new LocalByThresholdParameters();
		clusteringParameters.setMatchers(availableMatchers);
		clusteringMethod.setParameters(clusteringParameters);
		
		NumberFormat filenumberFormat = new DecimalFormat("000000");
		
		
		List<Mapping> rankedMappings = exp.candidateSelection.getRankedMappings();
		for( int i = 0; i < rankedMappings.size(); i++ ) {
			Mapping currentMapping = rankedMappings.get(i);
			PrintStream out = prepareFile(filenumberFormat.format(i));
			
			System.out.println("Mapping: " + currentMapping);
			for( double clusteringThreshold = 0.0; clusteringThreshold <= 1.0; clusteringThreshold += 0.001 ) {
				System.out.println("th: " + clusteringThreshold);
				
				clusteringParameters.clusteringThreshold = clusteringThreshold;
				clusteringMethod.setParameters(clusteringParameters);
				Cluster<Mapping> cluster = clusteringMethod.getCluster(currentMapping);
				
				out.println(clusteringThreshold + ", " + cluster.size() );
				if( cluster.size() > 500 ) break;
			}
			
			out.close();
			
		}
		
		
		
		
		
		done();
		
	}

	private PrintStream prepareFile(String fileName) {
		String prefix = "/home/cosmin/clustering_evaluation/";
		String suffix = ".data";
		
		try {
			File newFile = new File(prefix + fileName + suffix);
			FileOutputStream fs = new FileOutputStream(newFile);
			PrintStream buff = new PrintStream(fs);
			return buff;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
}
