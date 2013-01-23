package am.evaluation.clustering;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.evaluation.clustering.localByThreshold.LocalByThresholdMethod;

public class ClusterFactory {

	public static enum ClusteringType {
		//GLOBAL_FIXED("GLOBAL FIXED", GlobalFixedMethod.class ),    // Global clustering with fixed thresholds.
		LOCAL_BY_THRESHOLD("LOCAL BY THRESHOLD", LocalByThresholdMethod.class );
		
		
		/******************************************************************************/
		
		private String name;
		private Class<? extends ClusteringMethod> methodClass;
		
		ClusteringType(String n, Class<? extends ClusteringMethod> pnl) { name = n; methodClass = pnl; }
		
		public String getName() { return name; }
		public Class<? extends ClusteringMethod> getMethodClass() { return methodClass; }
	}
	
	
	public static ClusteringMethod createClusteringMethod( ClusteringType t, List<AbstractMatcher> matchers ) {
		ClusteringMethod method = null;
		
		try {
			
			// instantiate a new clustering method
			
			Constructor<? extends ClusteringMethod> c = 
					t.getMethodClass().getConstructor( List.class );
			
			method = c.newInstance(matchers);
			
			// prepare the parameters dialog
			ClusterFactoryDialog dialog = new ClusterFactoryDialog(method);
			
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);
			dialog.setVisible(true); // execution will stop until dialog is dismissed
			
			if( dialog.userCanceled() ) return null;
			
			// the user has entered the parameters, update the method with the parameters
			method.setParameters( dialog.getParameters() );
			
			
		} 
		catch(InvocationTargetException e) {
			// The constructor threw an exception.
			e.printStackTrace();
			
			Throwable target = null;
			if( (target = e.getTargetException()) != null )
				target.printStackTrace();
			
			return null;
		} 
		catch(NoSuchMethodException e) {
			// The constructor does not exist.  This should not happen.
			e.printStackTrace();
			return null;
		} 
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return method;
	}
	
	
}
