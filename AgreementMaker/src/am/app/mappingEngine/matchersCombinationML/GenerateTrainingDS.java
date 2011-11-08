package am.app.mappingEngine.matchersCombinationML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GenerateTrainingDS {
	
	public static void generateXML(ArrayList<TrainingLayout> trainFileSet)
	{
		 try {
			 int i=1;
			 String outputfilename="bench/training.xml";
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root element
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("trainingsets");
				doc.appendChild(rootElement);
		        for(TrainingLayout tl : trainFileSet)
		        {
		        
				Element trainingset = doc.createElement("trainingset");
				rootElement.appendChild(trainingset);
				trainingset.setAttribute("id", Integer.toString(i));
		 
				// source ontology
				Element sourceontology = doc.createElement("sourceontology");
				sourceontology.setAttribute("name", tl.getsourceOntology());
				sourceontology.setAttribute("path", tl.getsourceOntologyPath());
				trainingset.appendChild(sourceontology);
		 
				// target ontology
				Element targetontology = doc.createElement("targetontology");
				targetontology.setAttribute("name", tl.gettargetOntology());
				targetontology.setAttribute("path", tl.gettargetOntologyPath());
				trainingset.appendChild(targetontology);
		 
				// reference alignment
				Element refalignment = doc.createElement("refalignment");
				refalignment.setAttribute("name", "reference");
				refalignment.setAttribute("path", tl.getrefAlignmentPath());
				trainingset.appendChild(refalignment);
				 i++;
		        }
					 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				Result result = new StreamResult( new FileOutputStream(outputfilename));
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				System.out.println("File saved! " + outputfilename);
		       
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
			  catch (FileNotFoundException fnf) {
					fnf.printStackTrace();
				  }
	}
	public static void main(String args[])
	
	{
		ArrayList<TrainingLayout> trainingfs=new ArrayList<TrainingLayout>();
		String path="bench/training/";
		File directory=new File(path);
		String[] files=directory.list();
		if(files==null)
		{
			System.out.println("directory does not exist or it's not a directory");
		}
		else
		{
			for(int i=0;i<files.length;i++)
			{
				String filename=files[i];
				System.out.println(filename);
				
				if(!filename.equals("101"))
				{
					TrainingLayout tl=new TrainingLayout();
					tl.setsourceOntology("101");
					tl.settargetOntology(filename);
					tl.setsourceOntologyPath("bench/training/101/onto.RDF");
					tl.settargetOntologyPath(path+filename+"/onto.RDF");
					tl.setrefalignmentPath(path+filename+"/refalign.RDF");
					trainingfs.add(tl);
				}	
			}
		}
		generateXML(trainingfs);
	}
}
