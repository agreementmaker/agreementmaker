package test;
/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       Ian.Dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            27-Mar-2003
 * Filename           $RCSfile: ClassHierarchy.java,v $
 * Revision           $Revision: 1.6 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2010-12-14 00:09:43 $
 *               by   $Author: cstroe1 $
 *
 * (c) Copyright 2002, 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////



// Imports
///////////////
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


/**
 * <p>
 * Simple demonstration program to show how to list a hierarchy of classes. This
 * is not a complete solution to the problem (sub-classes of restrictions, for example,
 * are not shown).  It is inteded only to be illustrative of the general approach.
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: ClassHierarchy.java,v 1.6 2010-12-14 00:09:43 cstroe1 Exp $
 */
public class ClassHierarchy {
    
    protected OntModel m_model;
    private Map<AnonId,String> m_anonIDs = new HashMap<AnonId,String>();
    private int m_anonCount = 0;
    int prova = 0;

    /** Show the sub-class hierarchy encoded by the given model */
    public void showHierarchy( PrintStream out, OntModel m ) {
        // create an iterator over the root classes that are not anonymous class expressions
    	
        ExtendedIterator<OntClass> i =  m.listHierarchyRootClasses();
    	//Iterator i =  m.listNamedClasses()
         /*.filterDrop( new Filter() {
                                    public boolean accept( Object o ) {
                                        return ((Resource) o).isAnon();
                                    }} );*/

        while (i.hasNext()) {
            showClass( out, i.next(), new ArrayList<OntClass>(), 0 );
        }
    }
    /** Present a class, then recurse down to the sub-classes.
     *  Use occurs check to prevent getting stuck in a loop
     */
    protected void showClass( PrintStream out, OntClass cls, List<OntClass> occurs, int depth ) {
        renderClassDescription( out, cls, depth );
        out.println();

        // recurse to the next level down
        if (cls.canAs( OntClass.class )  &&  !occurs.contains( cls )) {
            for (ExtendedIterator<OntClass> i = cls.listSubClasses( true );  i.hasNext(); ) {
                OntClass sub = i.next();

                // we push this expression on the occurs list before we recurse
                occurs.add( cls );
                showClass( out, sub, occurs, depth + 1 );
                occurs.remove( cls );
            }
        }
    }

    /**
     * <p>Render a description of the given class to the given output stream.</p>
     * @param out A print stream to write to
     * @param c The class to render
     */
    public void renderClassDescription( PrintStream out, OntClass c, int depth ) {
        indent( out, depth );

            if (!c.isAnon()) {
                out.print( "Class " );
                renderURI( out, c.getModel(), c.getLocalName() );
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
            renderURI( out, r.getModel(), r.getURI() );
        }
        else {
            renderAnonymous( out, r, "restriction" );
        }

        out.print( " on property " );
        renderURI( out, r.getModel(), r.getOnProperty().getURI() );
    }

    /** Render a URI */
    protected void renderURI( PrintStream out, PrefixMapping prefixes, String uri ) {
        out.print(uri  );
    }

    /** Render an anonymous class or restriction */
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

    /** Generate the indentation */
    protected void indent( PrintStream out, int depth ) {
        for (int i = 0;  i < depth; i++) {
            out.print( "  " );
        }
    }


}