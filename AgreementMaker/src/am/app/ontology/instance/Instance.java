package am.app.ontology.instance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;

public class Instance implements Serializable{
	private static final long serialVersionUID = 4568266674951302327L;
	protected String uri;
	protected String type;
	
	protected Hashtable<String,List<String>> properties;
	private String serializedModel;
	protected transient List<Statement> statements;
		
	public Instance(String uri, String type) {
		this.uri = uri;
		this.type = type;
		properties = new Hashtable<String, List<String>>();
		statements = new ArrayList<Statement>();
	}

	public String getUri() {
		return uri;
	}

	public Enumeration<String>  listProperties() {
		return properties.keys();
	}
	
	public List<String> getProperty( String key ) {
		return properties.get(key);
	}
	
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

