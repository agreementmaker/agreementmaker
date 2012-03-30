package lexicalmatcherjaws.internal;

public class LexiconException extends Exception {

	private static final long serialVersionUID = 4111465962629649687L;

	public static enum ExceptionType {
		WordformNotFound("This wordform does not exist in the Lexicon.");
		
		public String description;
		ExceptionType( String desc) { description = desc; }
	}
	
	public LexiconException( ExceptionType message ) {
		super();
		
	}
	
}
