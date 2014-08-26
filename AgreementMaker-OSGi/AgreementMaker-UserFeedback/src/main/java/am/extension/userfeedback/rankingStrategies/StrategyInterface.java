package am.extension.userfeedback.rankingStrategies;

import java.util.List;

import am.app.mappingEngine.Mapping;

public interface StrategyInterface extends Comparable<StrategyInterface> {
	
	public void rank();
	
	/**
	 * NOTE: {@link #rank()} must be called first.
	 */
	public List<Mapping> getRankedList(); 
	
	/**
	 * @return The selection percentage for this metric. Must be from 0.0 to
	 *         1.0. If the selection percentage = 0, then it means that the
	 *         selection percentage is unspecified, and will be automatically
	 *         computed using a balanced approach.
	 */
	public double getPercentage();
	
	/** Set the selection percentage for this metric. */
	public void setPercentage(double percentage);
	
	/**
	 * @return The priority of this strategy. The lower the value, the higher
	 *         the priority. Ties in priority will be indeterministically
	 *         broken.
	 */
	public int getPriority();
	
	public void setPriority(int priority);
	
	/**
	 * @return The number of times mappings were selected from this strategy.
	 */
	public int getCount();
	
	/**
	 * @param count
	 *            Set the number of times mappings were selected from this
	 *            strategy.
	 */
	public void setCount(int count);
	
	public void incrementCount();
}
