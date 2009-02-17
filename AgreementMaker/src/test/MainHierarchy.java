/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       ian.dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            22-Aug-2003
 * Filename           $RCSfile: MainHierarchy.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2009-02-17 18:36:55 $
 *               by   $Author: flav $
 *
 * (c) Copyright 2002, 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////
package test;


// Imports
///////////////
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;


/**
 * <p>
 * Execution wrapper for class hierarchy example
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: MainHierarchy.java,v 1.2 2009-02-17 18:36:55 flav Exp $
 */
public class MainHierarchy {

    public static void main( String[] args ) {
        OntModel m = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF, null );
    	
        //m.read( "http://www.w3.org/2001/sw/WebOnt/guide-src/wine" );
       m.read("file:/root/Desktop/mgedontology.rdfs");
        //System.out.println("Ontology accepted without errors");
       new ClassHierarchy().showHierarchy( System.out, m );
    }



}

