package agreementMaker.agreementDocument;



import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

import agreementMaker.mappingEngine.ContextMapping;
import agreementMaker.mappingEngine.DefnMapping;
import agreementMaker.mappingEngine.UserMapping;
import agreementMaker.userInterface.vertex.Vertex;



public class AgreementDocumentReader
{
	
	PrintWriter writer; 
	Vertex globalRoot;

	public AgreementDocumentReader(String fileName){
		try{	
			ObjectInputStream ois = new ObjectInputStream (new FileInputStream (fileName));
			globalRoot = (Vertex) ois.readObject();
			ois.close();
		}
		catch(Exception ioe){
				JOptionPane.showMessageDialog(null, "FAILED TO SAVE FILE" + "\n" + ioe.toString() );
		}
	} // end of constructor
	public String getUserMappingType(String sourceNode, String targetNode){

		Vertex node = findNode(sourceNode);
		if(node == null) return null;

		UserMapping um = node.getUserMapping();
		Vector vertices = um.getLocalVertices();
		
		for(int i=0; i<vertices.size(); i++)
			if( (((Vertex) vertices.get(i)).getName()).equalsIgnoreCase(targetNode) )
				return um.getMappingType();

		return "NONE";
	}
	public String getContextMappingType(String sourceNode, String targetNode){

		Vertex node = findNode(sourceNode);
		if(node == null) return null;

		ContextMapping cm = node.getContextMapping();
	
		if(cm.getLocalVertex().getName().equalsIgnoreCase(targetNode) )
			return cm.getMappingType();

		return "NONE";
	}
	public float getDefnMappingSimilarites(String sourceNode, String targetNode){

		Vertex node = findNode(sourceNode);
		if(node == null) return -1f;

		DefnMapping dm = node.getDefnMapping();
		Vector vertices = dm.getLocalVertices();
		Vector sims = dm.getSimilarities();

		for(int i=0; i<vertices.size(); i++)
			if( (((Vertex) vertices.get(i)).getName()).equalsIgnoreCase(targetNode) )
				return ((Float)(sims.get(i))).floatValue();

		return 0f;
	}
	private Vertex findNode(String name){

		for (Enumeration e = globalRoot.preorderEnumeration(); e.hasMoreElements(); ){
			// get the node
			Vertex node;
			node = (Vertex) e.nextElement();
		    if(node != null)
				if(node.getName().equalsIgnoreCase(name))
					return node;
		}
		
		return null;
	}
} // end of class
