package am.app.mappingEngine.matchersCombinationML;

/**
 * Data structure for storing the source and target paths when generating the 
 * training and test data
 * @author AS961967
 *
 */
public class TrainingLayout {
	String sourceOntology;
	String targetOntology;
	String sourceOntologypath;
	String targetOntologypath;
	String refAlignmentpath;
	
	public String getsourceOntology()
	{
		return sourceOntology;
	}
	
	public String gettargetOntology()
	{
		return targetOntology;
	}
	
	public String getrefAlignmentPath()
	{
		return refAlignmentpath;
	}
	
	
	public String getsourceOntologyPath()
	{
		return sourceOntologypath;
	}
	
	
	public String gettargetOntologyPath()
	{
		return targetOntologypath;
	}
	
	public void setsourceOntology(String srcOnto)
	{
		sourceOntology=srcOnto;
	}
	
	public void settargetOntology(String targetOnto)
	{
		targetOntology=targetOnto;
	}
	
	public void setrefalignmentPath(String refalign)
	{
		refAlignmentpath=refalign;
	}
	
	public void setsourceOntologyPath(String srcpath)
	{
		sourceOntologypath=srcpath;
	}
	
	public void settargetOntologyPath(String tarpath)
	{
		targetOntologypath=tarpath;
	}
}
