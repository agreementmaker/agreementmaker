package am.app.ontology.instance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import am.app.mappingEngine.instance.EntityTypeMapper;
import am.app.mappingEngine.instance.EntityTypeMapper.EntityType;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

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
 * 
 * @author Federico Caimi
 *
 */
public class Instance implements Serializable {

	private static final long serialVersionUID = -2774522573905235740L;

	/**
	 * This is a property key, used for the textual representation of the
	 * Instance.
	 */
	public static final String INST_LABEL = "label";
	
	/**
	 * This is a property key, used for the context of the Instance. 
	 */
	public static final String INST_CONTEXT = "paragraph";
	
	/**
	 * This is a property key, used for aliases.
	 */
	public static final String INST_ALIAS = "alias";
	
	/**
	 * The URI of this instance. This is a required field. 
	 * TODO: Consider making this "final".
	 */
	protected String uri;
	
	/**
	 * The type of this instance. These are not Semantic Web URIs but very
	 * generic types that can be used across systems. Use
	 * {@link EntityTypeMapper#getEnumEntityType(String)} to map a known URI
	 * into one of our internal types.
	 * 
	 * @see #typeValue
	 */
	protected EntityType type;
	
	/**
	 * Can be a URI, but it doesn't have to be. We chose this convention to be
	 * more flexible, and not have to be tied to an ontology.
	 */
	protected String typeValue;
	
	// Are these transient because we don't want to serialize the Statement
	// object? If so, the properties don't need to be transient. -- Cosmin.
	protected transient Hashtable<String,Set<String>> properties; // the syntactic properties of this instance 
	protected transient List<Statement> statements; // the semantic RDF statements of this instance.
	
	// this is required for correct serialization.
	private String serializedModel;
	
	// used for serialization.
	protected String[] keys;
	protected LinkedList<Set<String>> values;
	
	
	/**
	 * Create an instance.
	 * @param uri The unique URI of this instance.
	 * @param type A URI of a class in the Ontology-backed KB.
	 */
	public Instance(String uri, EntityType type) {
		this.uri = uri;
		this.type = type;
		properties = new Hashtable<String, Set<String>>();
		statements = new ArrayList<Statement>();
	}

	/**
	 * Create an instance given a JENA Individual.
	 * @param i
	 */
	public Instance( Individual i ) {
		this.uri = i.getURI();
		
		// first check for rdf:type
		OntClass rdfType = null;
		try {
			rdfType = i.getOntClass();
		}
		catch( Exception ex1 ) { }
		
		if( rdfType == null ) {
			// this dataset does not have rdf:type defined for its individuals? try to work around it.
			/**
			 * NIST KB Workaround.  This is because the {@link NistKbToRDF} converter does not properly populate
			 * rdf:type.  Instead it uses "http://www.kb.com#type".
			 */
			
			Model m = i.getModel();
			Property p = m.getProperty("http://www.kb.com#type");
			
			if( p != null ) {
				RDFNode node = i.getPropertyValue(p);
				if( node.isLiteral() ) {
					String type = node.as(Literal.class).getString();
					this.type = EntityTypeMapper.getEnumEntityType(type);
					this.typeValue = type;
				}
			}
		}
		else {
			String uri = rdfType.getURI();
			this.type = EntityTypeMapper.getEnumEntityType(uri);
			this.typeValue = uri; 				
		}
		
		properties = new Hashtable<String, Set<String>>();
		statements = new ArrayList<Statement>();
		
		StmtIterator iter = i.listProperties();
		while( iter.hasNext() ) {
			Statement s = iter.next();
			statements.add(s);
		}
		
		// collect the labels
		RDFNode node = i.getPropertyValue(RDFS.label);
	
		if(node != null) {
			String label = node.asLiteral().toString();
			
			if(label != null && !label.isEmpty()){
				setProperty(Instance.INST_LABEL, label);
			}
		}
		
	}
	
	public String getUri() {
		return uri;
	}

	public Enumeration<String> listProperties() {
		return properties.keys();
	}
	
	/**
	 * Returns the values of a syntactic property.  There may be multiple
	 * values for a single property, so they are returned as a list.
	 * <p>
	 * NOTE: If you would like to get the value of a single-valued property
	 * and avoid using a List in the return, use {@link #getSingleValuedProperty(String)}.
	 * 
	 * @param key The property name.
	 * @return The list of values for this property.
	 * @see #getSingleValuedProperty(String)
	 */
	public Set<String> getProperty( String key ) {
		return properties.get(key);
	}
	
	public Set<String> getAllPropertyValues(){
		Set<String> values = new HashSet<String>();
		for (Set<String> strings: properties.values()) {
			values.addAll(strings);
		}
		return values;
	}
		
	public Set<String> getAllValuesFromStatements(){
		Set<String> values = new HashSet<String>();
		
		for (Statement statement : statements) {
			RDFNode node = statement.getObject();
			if( node.canAs(Literal.class) ) {
				String literal = statement.getObject().asLiteral().getString();
				
				//values.add(literal);
				
				//TODO figure out what to do with the the limit
				int limit = 300;				
				if(literal.length() < limit)					
					values.add(literal);
				else values.add(literal.substring(0, limit - 1));
			}
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
		Set<String> strings = properties.get(key);
		if(strings == null) return null;
		if(strings.size() < 1) return null;
		return strings.iterator().next();
	}
	
	/** Passing a null value will remove the key from the properties table. */
	public void setProperty( String key, Set<String> strings ) {
		if( strings == null ) {
			properties.remove(key);
		} else {
			properties.put(key,strings);
		}
	}
	
	/**
	 * <p>
	 * This method allows multi-valued properties (the same property key can
	 * have multiple values). This means that when calling this method with the
	 * same key, but different values, both values will be stored.
	 * </p>
	 * 
	 * @param key
	 *            The key of the property.
	 * @param value
	 *            A value to associated with the property key. Passing a null
	 *            value will erase the key.
	 * 
	 * @see {@link #setProperty(String, ArrayList)}, {@link #getProperty(String)}, {@link #getSingleValuedProperty(String)}
	 */
	public void setProperty( String key, String value ) {
		if( value == null ) {
			properties.remove(key);
		} else {
			Set<String> values = properties.get(key);
			if(values == null){
				Set<String> list = new HashSet<String>();
				list.add(value);
				properties.put(key,list);
			}
			else {
				if(!values.contains(value))
					values.add(value);
			}
		}
	}

	public EntityType getType() {
		return type;
	}
	
	public String getTypeValue() {
		if( typeValue == null && type != null ) 
			return type.name();
		return typeValue;
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
    	
    	Set<String> keySet = properties.keySet();
    	
    	//System.out.println(keySet);
    	
		/**
		 * This is necessary because {@link #properties} and {@link #statements}
		 * are transient. Why are they transient? If we don't need them to be
		 * transient, then we don't need this extra serialization.
		 */
    	keys = new String[keySet.size()];  
    	values = new LinkedList<Set<String>>(); // can't create arrays of generic types.
    	int i = 0;
    	for (String string : keySet) {
			keys[i] = string;
			values.set(i, properties.get(string));
			i++;
		}    	
    	    	
    	//default write    	
        stream.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException{
        stream.defaultReadObject();
        
        Model model = ModelFactory.createDefaultModel();
        model.read(new StringReader(serializedModel), "http://base", "N-TRIPLE");
        statements = model.listStatements().toList();
        
        properties = new Hashtable<String, Set<String>>();
        
        //System.out.println(Arrays.toString(keys));
        //System.out.println(Arrays.toString(values));
        
        for (int i = 0; i < keys.length; i++) {
			properties.put(keys[i], values.get(i));
		}
        
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj == null) return false;
    	if(!(obj instanceof Instance)) return false;
    	return uri.equals(((Instance)obj).getUri());
    }
    
    @Override
    public int hashCode() {
    	return uri.hashCode();
    }
    
}

