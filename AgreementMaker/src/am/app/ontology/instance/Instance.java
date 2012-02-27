package am.app.ontology.instance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Represents an instance in AgreementMaker and InformationMatching.
 * It is identified by a URI, and can have a type.
 * <p>
 * There are two layers of property-value pairs:
 * <ul>
 * <li> A syntactic layer which is composed by strings (keys and values), stored in a Hashtable.
 * <li> A semantic layer composed by a list of statements in an ontology, stored in a List.
 * </ul>
 * <p>
 *  The two layers allow us to model Jena instances (semantic layer) as well as instances 
 *  extracted from XML and JSON (syntactic layer).
 * </p>
 * @author Federico Caimi
 *
 */
public class Instance implements Serializable {
	private static final long serialVersionUID = 4568266674951302327L;

	protected String uri;
	protected String type;
	
	protected Hashtable<String,List<String>> properties; // the syntactic properties of this instance 
	private String serializedModel;
	protected transient List<Statement> statements; // the semantic RDF statements of this instance.
	
	/**
	 * Create an instance.
	 * @param uri The unique URI of this instance.
	 * @param type A URI of a class in the Ontology-backed KB.
	 */
	public Instance(String uri, String type) {
		this.uri = uri;
		this.type = type;
		properties = new Hashtable<String, List<String>>();
		statements = new ArrayList<Statement>();
	}

	/**
	 * Create an instance given a JENA Individual.
	 * @param i
	 */
	public Instance( Individual i ) {
		this.uri = i.getURI();
		this.type = i.getOntClass().getURI();
		
		properties = new Hashtable<String, List<String>>();
		statements = new ArrayList<Statement>();
		
		StmtIterator iter = i.listProperties();
		while( iter.hasNext() ) {
			Statement s = iter.next();
			statements.add(s);
		}
	}
	
	public String getUri() {
		return uri;
	}

	public Enumeration<String>  listProperties() {
		return properties.keys();
	}
	
	/**
	 * Returns the values of a syntactic property.  The may be multiple
	 * values for a single property, so they are returned as a list.
	 * <p>
	 * NOTE: If you would like to get the value of a single-valued property
	 * and avoid using a List in the return, use {@link #getSingleValuedProperty(String)}.
	 * 
	 * @param key The property name.
	 * @return The list of values for this property.
	 * @see #getSingleValuedProperty(String)
	 */
	public List<String> getProperty( String key ) {
		return properties.get(key);
	}
	
	public List<String> getAllPropertyValues(){
		List<String> values = new ArrayList<String>();
		for (List<String> strings: properties.values()) {
			values.addAll(strings);
		}
		return values;
	}
	
	/**
	 * Return the value of a <i>single valued</i> syntactic property.
	 * @param key The string representation of the property name.
	 * @return The value of the property, as a string.
	 * @see #getProperty(String)
	 */
	public String getSingleValuedProperty( String key ) {
		List<String> strings = properties.get(key);
		if(strings == null) return null;
		if(strings.size() != 1) return null;
		return strings.get(0);
	}
	
	/** Passing a null value will remove the key from the properties table. */
	public void setProperty( String key, List<String> value ) {
		if( value == null ) {
			properties.remove(key);
		} else {
			properties.put(key,value);
		}
	}
	
	public void setProperty( String key, String value ) {
		if( value == null ) {
			properties.remove(key);
		} else {
			List<String> values = properties.get(key);
			if(values == null){
				List<String> list = new ArrayList<String>();
				list.add(value);
				properties.put(key,list);
			}
			else {
				if(!values.contains(value))
					values.add(value);
			}
		}
	}

	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return uri + " " + properties;
	}
	
	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}
	
	public void addStatements(List<Statement> statements) {
		if(this.statements != null)
			this.statements.addAll(statements);
		else this.statements = statements;
	}
	
	public List<Statement> getStatements() {
		return statements;
	}
	
	public void setURI(String uri){
		this.uri = uri;
	}	
	
	/* *************************** Serialization methods. *************************** */
	
    private void writeObject(java.io.ObjectOutputStream stream) throws IOException{
        //put the statements into a model
    	Model model = ModelFactory.createDefaultModel();
    	model.add(statements);
    	//serialize the model into xml or rdf
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	model.write(baos, "N-TRIPLE");
    	//get a string of the serialization
    	serializedModel = baos.toString("UTF-8");
    	//default write    	
        stream.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException{
        stream.defaultReadObject();
        
        Model model = ModelFactory.createDefaultModel();
        model.read(new StringReader(serializedModel), "http://base", "N-TRIPLE");
        statements = model.listStatements().toList();
    }
}

