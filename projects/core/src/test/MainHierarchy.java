/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       ian.dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            22-Aug-2003
 * Filename           $RCSfile: MainHierarchy.java,v $
 * Revision           $Revision: 1.6 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2011-02-27 00:50:54 $
 *               by   $Author: cstroe1 $
 *
 * (c) Copyright 2002, 2003, 2004, 2005 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

// Package
///////////////
package test;


// Imports
///////////////


/**
 * <p>
 * Execution wrapper for class hierarchy example
 * </p>
 *
 * @author Ian Dickinson, HP Labs
 *         (<a  href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: MainHierarchy.java,v 1.6 2011-02-27 00:50:54 cstroe1 Exp $
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

