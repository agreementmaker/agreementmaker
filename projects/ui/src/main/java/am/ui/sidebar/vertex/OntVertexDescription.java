package am.ui.sidebar.vertex;

//import edu.stanford.smi.protegex.ont.ProtegeOWL;
//import edu.stanford.smi.protegex.ont.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class OntVertexDescription {
	
	/*private static String annotations;
	private static String disjointClasses;
	private static String properties;
	private static String restrictions;*/
	
	public static void main( String[] args ) {
        // read the argument file, or the default
        String source = (args.length == 0) ? "file:C:\\Documents and Settings\\Nalin\\Desktop\\MSProj\\Ontologies\\travel.owl" : args[0];

        // guess if we're using a daml source
        boolean isDAML = source.endsWith( ".daml" );

        if( isDAML )
        	throw new RuntimeException("Jena no longer supports DAML.");
        
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

        m.read( source );

        ExtendedIterator<OntClass> i =  m.listHierarchyRootClasses();
    	/*Iterator i =  m.listNamedClasses()
                      .filterDrop( new Filter() {
                                    public boolean accept( Object o ) {
                                        return ((Resource) o).isAnon();
                                    }} );*/

        while (i.hasNext()) {
            showClass(System.out, i.next(), new ArrayList<OntClass>());
            System.out.println("===============================================");
            System.out.println("===============================================");
        }
    }
	
	private static void showClass(PrintStream out, OntClass cls, List<OntClass> occurs) {
		renderClassDescription( cls );
        out.println();
		// recurse to the next level down
        if (cls.canAs( OntClass.class )  &&  !occurs.contains( cls )) {
            for (ExtendedIterator<OntClass> i = cls.listSubClasses( true );  i.hasNext(); ) {
                OntClass sub = i.next();
                // we push this expression on the occurs list before we recurse
                occurs.add( cls );
                showClass(out, sub, occurs);
                occurs.remove( cls );
            }
        }
		
	}

	private static void renderClassDescription(OntClass cls) {
		OntClass sub;
		if(!cls.isAnon()){
			System.out.println("\n~~~~~~~ " + cls.getLocalName().toUpperCase() + " ~~~~~~~~");
			System.out.println("Super Classes:");//true
			for(ExtendedIterator<OntClass> i = cls.listSuperClasses(); i.hasNext();){
				sub = i.next();
				if(!sub.isAnon())System.out.println("\t"+sub.getLocalName());
			}
			System.out.println("Sub Classes:");//true
			for(ExtendedIterator<OntClass> i = cls.listSubClasses(); i.hasNext();) {
				sub = i.next();
				if(!sub.isAnon())System.out.println("\t"+sub.getLocalName());
			}
			System.out.println("Disjoint With:");
			for(ExtendedIterator<OntClass> i = cls.listDisjointWith(); i.hasNext();) {
				sub = i.next();
				if(!sub.isAnon())System.out.println("\t"+sub.getLocalName());
			}
			System.out.println("Equivalent Classes:");
			for(ExtendedIterator<OntClass> i = cls.listEquivalentClasses(); i.hasNext();) {
				sub = i.next();
				if(!sub.isAnon())System.out.println("\t"+sub.getLocalName());
			}
			System.out.println("Declared Properties:");
			for(ExtendedIterator<OntProperty> i = cls.listDeclaredProperties(); i.hasNext();) {
				System.out.println("\t" + i.next().getLocalName());
			}
			System.out.println("Version Info:");
			for(ExtendedIterator<String> i = cls.listVersionInfo(); i.hasNext();){
				System.out.println("\t"+i.next());
			}
			System.out.println("Labels:");
			for(ExtendedIterator<RDFNode> i = cls.listLabels(null); i.hasNext();){
				System.out.println("\t"+i.next());
			}
			System.out.println("Comments:");
			for(ExtendedIterator<RDFNode> i = cls.listComments(null); i.hasNext();){
				System.out.println("\t"+i.next());
			}
			System.out.println("Same As:");
			for(ExtendedIterator<? extends Resource> i = cls.listSameAs(); i.hasNext();){
				System.out.println("\t"+i.next());
			}
			System.out.println("See Also:");
			for(ExtendedIterator<RDFNode> i = cls.listSeeAlso(); i.hasNext();){
				System.out.println("\t"+i.next());
			}
		}
		
	}

	public static String getVertexDescription(String className, OntModel ontModel){
		
		return "Dude.. fix this !!";
	}

	
	
}