package eu.sealsproject.omt.ws.matcher;

import java.net.URI;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface AlignmentWS {

	@WebMethod
	public String align(URI source, URI target);
}
