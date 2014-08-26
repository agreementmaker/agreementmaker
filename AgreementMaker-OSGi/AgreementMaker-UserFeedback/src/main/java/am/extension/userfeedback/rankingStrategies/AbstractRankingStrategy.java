package am.extension.userfeedback.rankingStrategies;

import java.util.List;

import am.app.mappingEngine.Mapping;

public abstract class AbstractRankingStrategy implements StrategyInterface {

	protected double percentage;
	protected int priority;
	protected int count;
	
	protected List<Mapping> rankedList;
	
	@Override
	public double getPercentage() {	return percentage; }
	
	@Override
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public void setCount(int count) {
		this.count = count;
	}
	
	public void incrementCount() {
		int count = getCount();
		setCount(count+1);
	}
	
	@Override
	public int compareTo(StrategyInterface o) {
		return Integer.compare(this.priority, o.getPriority());
	}
	
	@Override
	public List<Mapping> getRankedList() {
		return rankedList;
	}
	
	public int getBestMappingNumber(double threshold)
	{
		int countM=0;
		for (Mapping m : rankedList)
		{
			if (m.getSimilarity()<threshold)
				countM++;
		}
		return countM;
	}
	
	public int getBestMappingNumber(double threshold, boolean revert)
	{
		int countM=0;
		for (Mapping m : rankedList)
		{
			if (revert)
			{
				if (m.getSimilarity()>1-threshold)
					countM++;
			}
			else
			{
				if (m.getSimilarity()<threshold)
					countM++;
			}
		}
		return countM;
	}
	
}
