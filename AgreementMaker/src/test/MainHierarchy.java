/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       ian.dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            22-Aug-2003
 * Filename           $RCSfile: MainHierarchy.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2009-08-07 00:24:27 $
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
 * @version CVS $Id: MainHierarchy.java,v 1.3 2009-08-07 00:24:27 flav Exp $
 */
public class MainHierarchy {
	
	static String a;
	
    public static void main( String[] args ) {
    	String h = a();
    	String g = b();
    	System.out.println(h+" "+g);
    }
    
    public static String a(){
    	a = "ciao";
    	return a;
    }
    
    public static String b(){
    	a = "bau";
    	return a;
    }



}

