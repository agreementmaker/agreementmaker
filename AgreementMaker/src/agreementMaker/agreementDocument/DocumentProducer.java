package agreementMaker.agreementDocument;



import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import agreementMaker.userInterface.vertex.Vertex;



public class DocumentProducer
{
	
	PrintWriter writer; 

	public DocumentProducer(Vertex globalRoot, String fileName){
		try{	

		ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream (fileName));
		oos.writeObject(globalRoot);
		oos.close();

		
		}

		catch(Exception ioe){
				JOptionPane.showMessageDialog(null, "FAILED TO SAVE FILE" + "\n" + ioe.toString());
		}

		
	} // end of constructor


} // end of class
