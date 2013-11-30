package am.app.ontology.instance;

import java.io.IOException;
import java.io.InputStream;

/**
 * This input stream fixes invalid OAEI2013 turtle files. The turtle files are
 * not valid turtle because they contain random \r characters in the
 * literal values, which is interpreted by Jena as the end of a line, and
 * therefore as invalid (TURTLE is supposed to end a line with a dot and then a
 * newline).
 * 
 * @author cosmin
 * 
 */
public class TurtleFixerInputStream extends InputStream {

	public TurtleFixerInputStream(String instanceSourceFile) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
