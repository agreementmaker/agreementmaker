package am.app.mappingEngine.matchersCombinationML;

/**
 * This class is used generate the XML training and test files given

 *currently we have to manually makes changes specific to 
 *test and training files but will be made generic in future 
 *  TODO: generalize approach to both test and training set
 */
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
	
	public static void generateXML(ArrayList<String> trainFileSet,String outputfilename)
	{
		 try {
			 int j=1;
			 //String outputfilename="mlroot/mltraining/training.xml";
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root element
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("datasets");
				doc.appendChild(rootElement);
		        for(int i=0;i<trainFileSet.size();i++)
		        {
		        	Element trainingset = doc.createElement("dataset");
					rootElement.appendChild(trainingset);
					trainingset.setAttribute("id", Integer.toString(j));
				
					String str1=trainFileSet.get(i);
					i++;
					String str2=trainFileSet.get(i);
					i++;
					String str3=trainFileSet.get(i);
					
					if(str1.contains("refalign"))
					{
						// source ontology
						Element sourceontology = doc.createElement("sourceontology");
						sourceontology.setAttribute("name", "source");
						sourceontology.setAttribute("path", str2);
						trainingset.appendChild(sourceontology);
					
						// target ontology
						Element targetontology = doc.createElement("targetontology");
						targetontology.setAttribute("name", "target");
						targetontology.setAttribute("path", str3);
						trainingset.appendChild(targetontology);
						
						// reference alignment
						Element refalignment = doc.createElement("refalignment");
						refalignment.setAttribute("name", "reference");
						refalignment.setAttribute("path", str1);
						trainingset.appendChild(refalignment);
					
					}
		        
					if(str2.contains("refalign"))
					{
						// source ontology
						Element sourceontology = doc.createElement("sourceontology");
						sourceontology.setAttribute("name", "source");
						sourceontology.setAttribute("path", str1);
						trainingset.appendChild(sourceontology);
					
						// target ontology
						Element targetontology = doc.createElement("targetontology");
						targetontology.setAttribute("name", "target");
						targetontology.setAttribute("path", str3);
						trainingset.appendChild(targetontology);
						
						// reference alignment
						Element refalignment = doc.createElement("refalignment");
						refalignment.setAttribute("name", "reference");
						refalignment.setAttribute("path", str2);
						trainingset.appendChild(refalignment);
					
					}
		     

					if(str3.contains("refalign"))
					{
						// source ontology
						Element sourceontology = doc.createElement("sourceontology");
						sourceontology.setAttribute("name", "source");
						sourceontology.setAttribute("path", str1);
						trainingset.appendChild(sourceontology);
					
						// target ontology
						Element targetontology = doc.createElement("targetontology");
						targetontology.setAttribute("name", "target");
						targetontology.setAttribute("path", str2);
						trainingset.appendChild(targetontology);
						
						// reference alignment
						Element refalignment = doc.createElement("refalignment");
						refalignment.setAttribute("name", "reference");
						refalignment.setAttribute("path", str3);
						trainingset.appendChild(refalignment);
					
					}
		     

				
					 j++;
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
	public static void generateXML(String srcOntology,String tarOntology,String refAlign,String outputFileName)
	{
 try {
			 
			 //TODO: merge this with file that generates the xml doc
			 	int i=1;
			 	
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root element
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("datasets");
				doc.appendChild(rootElement);
		        
				Element trainingSet = doc.createElement("dataset");
				rootElement.appendChild(trainingSet);
				trainingSet.setAttribute("id", Integer.toString(i));
		 
				// source ontology
		
				Element sourceOntology = doc.createElement("sourceontology");
				sourceOntology.setAttribute("name", "srcOntology");
				sourceOntology.setAttribute("path", srcOntology);
				trainingSet.appendChild(sourceOntology);
				
				
				
				// target ontology
				Element targetOntology = doc.createElement("targetontology");
				targetOntology.setAttribute("name", "targetntology");
				targetOntology.setAttribute("path", tarOntology);
				trainingSet.appendChild(targetOntology);
		 
				// reference alignment
				Element refAlignment = doc.createElement("refalignment");
				refAlignment.setAttribute("name", "refalignment");
				refAlignment.setAttribute("path", refAlign);
				trainingSet.appendChild(refAlignment);
				
		       
					 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(doc);
				Result result = new StreamResult( new FileOutputStream(outputFileName));
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				//System.out.println("XML File saved! " + outputfilename);
				
		       
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
			  catch (FileNotFoundException fnf) {
					fnf.printStackTrace();
				  }
	}
	public static void getFilesFromFolder(ArrayList<String> files, String folder)
	{
		File file=new File(folder);
		
		if(file.isDirectory())
		{
			File[] filesInDir=file.listFiles();
			if(!file.getName().contains("svn"))
			{
				for(int i=0;i<filesInDir.length;i++)
				{
					getFilesFromFolder(files, filesInDir[i].getAbsolutePath());
				}
			}
		}
		else
		{
			if(!file.getName().equals("entries"))
			{
				files.add(file.getAbsolutePath());	
			}
			
		}
	}
	

	public static void main(String args[])
	
	{
		ArrayList<TrainingLayout> trainingfs=new ArrayList<TrainingLayout>();
		String path="mlroot/mltraining";
		String outputfilename="";
		File directory=new File(path);
		GenerateTrainingDS ds=new GenerateTrainingDS();
		ArrayList<String> files=new ArrayList<String>();
		ds.getFilesFromFolder(files, path);
		ds.generateXML(files,outputfilename);
		 
	}
		/*String[] files=directory.list();
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
		}*/
		//generateXML(trainingfs);
	
}
