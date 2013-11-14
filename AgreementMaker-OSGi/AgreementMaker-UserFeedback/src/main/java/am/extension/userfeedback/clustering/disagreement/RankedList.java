package am.extension.userfeedback.clustering.disagreement;

import java.awt.List;
import java.util.ArrayList;


import am.app.mappingEngine.Mapping;

public class RankedList {
	private double rank;
	public ArrayList<Mapping> mList;
	public double getRank() {
		return rank;
	}

	public void setRank(double rank) {
		this.rank = rank;
	}
	
	public RankedList()
	{
		//super();
		rank=0;
		mList=new ArrayList<Mapping>();
	}

}
