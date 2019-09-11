package test;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.ontology.BooleanClassDescription;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;



/**
 * <p>
 * Simple example of describing the basic attributes of a OWL, DAML or RDFS class
 * using the ontology API.  This is not meant as a definitive solution to the problem,
 * but as an illustration of one approach to solving the problem. This example should
 * be adapted as necessary to provide a given application with the means to render
 * a class description in a readable form.
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: DescribeClass.java,v 1.7 2011-02-27 00:50:54 cstroe1 Exp $
 */
public class DescribeClass {

    private Map<AnonId, String> m_anonIDs = new HashMap<AnonId, String>();
    private int m_anonCount = 0;


    /**
     * <p>Describe the given ontology class in texttual form. The description
     * produced has the following form (approximately):
     * <pre>
     * Class foo:Bar
     *    is a sub-class of foo:A, ex:B
     *    is a super-class of ex:C
     * </pre>
     * </p>
     *
     * @param out The print stream to write the description to
     * @param cls The ontology class to describe
     */
    public void describeClass( PrintStream out, OntClass cls ) {
    	renderClassDescription( out, cls );
        out.println();

        // sub-classes
        for (ExtendedIterator<OntClass> i = cls.listSuperClasses( true ); i.hasNext(); ) {
            out.print( "  is a sub-class of " );
            renderClassDescription( out, (OntClass) i.next() );
            out.println();
        }

        // super-classes
        for (ExtendedIterator<OntClass> i = cls.listSubClasses( true ); i.hasNext(); ) {
            out.print( "  is a super-class of " );
            renderClassDescription( out, (OntClass) i.next() );
            out.println();
        }
    }

    /**
     * <p>Render a description of the given class to the given output stream.</p>
     * @param out A print stream to write to
     * @param c The class to render
     */
    public void renderClassDescription( PrintStream out, OntClass c ) {
       if (!c.isAnon()) {
                out.print( "Class " );
                renderURI( out, prefixesFor( c ), c.getURI() );
                out.print( ' ' );
            }
            else {
                renderAnonymous( out, c, "class" );
            }
        }

    /**
     * <p>Handle the case of rendering a restriction.</p>
     * @param out The print stream to write to
     * @param r The restriction to render
     */
    protected void renderRestriction( PrintStream out, Restriction r ) {
        if (!r.isAnon()) {
            out.print( "Restriction " );
            renderURI( out, prefixesFor( r ), r.getURI() );
        }
        else {
            renderAnonymous( out, r, "restriction" );
        }

        out.println();

        renderRestrictionElem( out, "    on property", r.getOnProperty() );
        out.println();

        if (r.isAllValuesFromRestriction()) {
            renderRestrictionElem( out, "    all values from", r.asAllValuesFromRestriction().getAllValuesFrom() );
        }
        if (r.isSomeValuesFromRestriction()) {
            renderRestrictionElem( out, "    some values from", r.asSomeValuesFromRestriction().getSomeValuesFrom() );
        }
        if (r.isHasValueRestriction()) {
            renderRestrictionElem( out, "    has value", r.asHasValueRestriction().getHasValue() );
        }
    }

    protected void renderRestrictionElem( PrintStream out, String desc, RDFNode value ) {
        out.print( desc );
        out.print( " " );
        renderValue( out, value );
    }

    protected void renderValue( PrintStream out, RDFNode value ) {
        if (value.canAs( OntClass.class )) {
            renderClassDescription( out, (OntClass) value.as( OntClass.class ) );
        }
        else if (value instanceof Resource) {
            Resource r = (Resource) value;
            if (r.isAnon()) {
                renderAnonymous( out, r, "resource" );
            }
            else {
                renderURI( out, r.getModel(), r.getURI() );
            }
        }
        else if (value instanceof Literal) {
            out.print( ((Literal) value).getLexicalForm() );
        }
        else {
            out.print( value );
        }
    }

    protected void renderURI( PrintStream out, PrefixMapping prefixes, String uri ) {
        out.print( prefixes.shortForm( uri ) );
    }

    protected PrefixMapping prefixesFor( Resource n ) {
        return n.getModel().getGraph().getPrefixMapping();
    }

    protected void renderAnonymous( PrintStream out, Resource anon, String name ) {
        String anonID = (String) m_anonIDs.get( anon.getId() );
        if (anonID == null) {
            anonID = "a-" + m_anonCount++;
            m_anonIDs.put( anon.getId(), anonID );
        }

        out.print( "Anonymous ");
        out.print( name );
        out.print( " with ID " );
        out.print( anonID );
    }

    protected void renderBooleanClass( PrintStream out, String op, BooleanClassDescription boolClass ) {
        out.print( op );
        out.println( " of {" );

        for (ExtendedIterator<? extends OntClass> i = boolClass.listOperands(); i.hasNext(); ) {
            out.print( "      " );
            renderClassDescription( out, (OntClass) i.next() );
            out.println();
        }
        out.print( "  } " );
    }

}


