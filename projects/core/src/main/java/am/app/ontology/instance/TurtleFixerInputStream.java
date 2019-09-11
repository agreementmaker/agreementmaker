package am.app.ontology.instance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This input stream fixes invalid OAEI2013 turtle files. The turtle files are
 * not valid turtle because they contain illegal escape characters in the
 * literal values, which is not standard TURTLE.
 * 
 * TODO: Add support for triple quotes? - Cosmin.
 * 
 * @author cosmin
 * 
 */
public class TurtleFixerInputStream extends InputStream {

	private InputStream is;
	
	/** Keep translated characters */
	private Queue<Integer> streamQueue;
	
	/** Indicates whether we're inside a quoted string (in  between the quotes). */
	private boolean insideQuotes = false;
	
	/**
	 * @param instanceSourceFileURL This must be a URL.
	 * @throws IOException 
	 * @throws FileNotFoundException
	 */
	public TurtleFixerInputStream(String instanceSourceFileURL) throws IOException {
		URL url = new URL(instanceSourceFileURL);
		this.is = url.openStream();
		
		streamQueue = new LinkedList<Integer>();
	}
	
	@Override
	public int read() throws IOException {
		if( !streamQueue.isEmpty() ) {
			return streamQueue.remove();
		}
		
		int currentCharacter = is.read();
		if( currentCharacter == -1 ) return -1; // end of file
		
		if( currentCharacter == (int) '\\' ) {
			int nextCharacter = is.read();
			if( nextCharacter == -1 ) { 
				return -1; // remove \ from end of file
			}
			
			if( nextCharacter != (int)'t'  || 
				nextCharacter != (int)'n'  ||
				nextCharacter != (int)'r'  ||
				nextCharacter != (int)'"'  ||
				nextCharacter != (int)'\\' ||
				nextCharacter != (int)'U'  ||
				nextCharacter != (int)'u' ) {
				streamQueue.offer( (int)'\\' );
				streamQueue.offer( nextCharacter );
				return currentCharacter; // automatically escape single slashes that are not valid string escapes.
			}
		}
		
		if( currentCharacter == (int) '"' ) {
			insideQuotes ^= true; // toggle the indicator
		}
		
		 if( currentCharacter == (int) '\n' ) {
			 if( insideQuotes ) {
				 // we must translate all newlines that are inside quotes.
				 streamQueue.offer( (int) 'n' );
				 return (int) '\\';  // escape the new line
			 }
		 }
		 
		 if( currentCharacter == (int) '\r' ) {
			 if( insideQuotes ) {
				// we must translate all newlines that are inside quotes.
				 streamQueue.offer( (int) 'r' );
				 return (int) '\\';  // escape the new line
			 }
		 }
		
		return currentCharacter;
	}

}
