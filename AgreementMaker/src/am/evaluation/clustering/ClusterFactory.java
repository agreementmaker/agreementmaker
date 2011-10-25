package am.evaluation.clustering;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.evaluation.clustering.localByThreshold.LocalByThresholdMethod;

public class ClusterFactory {

	public static enum ClusteringType {
		//GLOBAL_FIXED("GLOBAL FIXED", GlobalFixedMethod.class ),    // Global clustering with fixed thresholds.
		LOCAL_BY_THRESHOLD("LOCAL BY THRESHOLD", LocalByThresholdMethod.class );
		
		private String name;
		private Class<? extends ClusteringMethod> methodClass;
		
		ClusteringType(String n, Class<? extends ClusteringMethod> pnl) { name = n; methodClass = pnl; }
		public String getName() { return name; }
		public Class<? extends ClusteringMethod> getMethodClass() { return methodClass; }
	}
	
	
	public static ClusteringMethod getMethodInstance( ClusteringType t, List<AbstractMatcher> matchers ) {
		ClusteringMethod method = null;
		
		try {
			
			// instantiate the new method
			
			method = t.getMethodClass().newInstance();
			method.setAvailableMatchers(matchers);  // set the available matchers.
			
			// prepare the parameters dialog
			ClusterFactoryDialog dialog = new ClusterFactoryDialog(method);
			
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);
			dialog.setVisible(true); // execution will stop until dialog is dismissed
			
			if( dialog.userCanceled() ) return null;
			
			// the user has entered the parameters, update the method with the parameters
			method.setParameters( dialog.getParameters() );
			
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return method;
	}
	
	
}
