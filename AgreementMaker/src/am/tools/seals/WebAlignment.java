/**    ________________________________________
 * ___/ Copyright Notice / Warranty Disclaimer \________________
 *
 * @copyright { Copyright (c) 2010
 * Advances in Information Systems Laboratory at the
 * University of Illinois at Chicago
 * All Rights Reserved. }
 * 
 * @disclaimer {
 * This work is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. }
 * 
 *     _____________________
 * ___/ Authors / Changelog \___________________________________
 * 
 * 
 * @date July 29, 2010.  @author Cosmin.
 * @description Initial SEALS implementation.          
 * 
 *  
 */

package am.tools.seals;

import java.net.URI;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/**
 * 
 * @author Cosmin Stroe
 * @version 0.1 Jul 29, 2010.
 *
 */
@WebService
@SOAPBinding(style = Style.RPC)
public interface WebAlignment {

	/**
	 * This method runs the alignment process and returns the result as a string via a WebService.
	 * @param source The URI of the source ontology.
	 * @param target The URI of the target ontology.
	 * @return A string of the alignment in OAEI Alignment format (RDF/XML, http://alignapi.gforge.inria.fr/format.html).
	 */
	@WebMethod
	public String align(URI source, URI target);
}
