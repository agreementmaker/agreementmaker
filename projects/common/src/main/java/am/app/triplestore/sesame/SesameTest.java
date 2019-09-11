package am.app.triplestore.sesame;

public class SesameTest {
	public static void main(String[] args)
	{
		SesameTripleStore s=new SesameTripleStore("/home/joe/ADVISWorkspace/AgreementMaker/AgreementMaker/ontologies/OAEI09_OWL_RDF:XML/benchmarks/102/wine.rdf","");
		s.openConnection();
		s.closeConnection();
		System.out.println("done");
	}

}
